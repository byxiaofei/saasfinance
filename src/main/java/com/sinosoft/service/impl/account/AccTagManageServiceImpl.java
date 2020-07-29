package com.sinosoft.service.impl.account;

import com.sinosoft.common.CurrentTime;
import com.sinosoft.common.CurrentUser;
import com.sinosoft.domain.AccTagManage;
import com.sinosoft.domain.UserInfo;
import com.sinosoft.dto.account.AccTagManageDTO;
import com.sinosoft.repository.account.AccTagManageRespository;
import com.sinosoft.service.account.AccTagManageService;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.*;

/**
 * @Auther: hanchuanxing
 * @Date: 2019/2/19 17:48
 * @Description:标注管理业务处理实现类
 */

@Service
public class AccTagManageServiceImpl implements AccTagManageService {

    @Resource
    private AccTagManageRespository accTagManageRespository;

    @Override
    public Page<?> qryAccTagManage(int page, int rows, AccTagManageDTO accTagManageDTO) {

        StringBuffer sb = new StringBuffer();
        sb.append("select new com.sinosoft.dto.account.AccTagManageDTO(t.id,t.centerCode,t.accBookType, t.accBookCode, t.tagCode, t.tagName, t.endFlag, t.upperTag) from AccTagManage t where 1 = 1");

        Map<String,Object> params = new HashMap<>();

        sb.append(" and t.centerCode =: centerCode");
        params.put("centerCode", CurrentUser.getCurrentLoginManageBranch());
        sb.append(" and t.accBookType =: accBookType");
        params.put("accBookType", CurrentUser.getCurrentLoginAccountType());
        sb.append(" and t.accBookCode =: accBookCode");
        params.put("accBookCode", CurrentUser.getCurrentLoginAccount());

        if(StringUtils.isNotEmpty(accTagManageDTO.getTagCode())){


            String [] ar= accTagManageDTO.getTagCode().split(",");
            for(int i=0;i<ar.length;i++){
                if(i==0){
                    if(ar.length==1){
                        sb.append(" and  t.tagCode =:tagCode"+ar[i]);
                        params.put("tagCode"+ar[i],ar[i]  );
                        break;
                    }
                    sb.append(" and ( t.tagCode =:tagCode"+ar[i]);
                    params.put("tagCode"+ar[i],ar[i]  );

                }
                if(i>0){
                    if(i==ar.length-1){
                        sb.append(" or t.tagCode =:tagCode"+ar[i]);
                        params.put("tagCode"+ar[i], ar[i]);
                        sb.append(" )");

                    }else {
                        sb.append(" or t.tagCode =:tagCode" + ar[i]);
                        params.put("tagCode" + ar[i], ar[i]);
                    }
                }
            }
        }

        if(StringUtils.isNotEmpty(accTagManageDTO.getEndFlag())){
            sb.append(" and t.endFlag =: endFlag");
            params.put("endFlag", accTagManageDTO.getEndFlag());
        }

        sb.append(" order by t.tagCode asc");

        Page<?> res = accTagManageRespository.queryByPage(page, rows, sb.toString(), params);
        List<?> list = res.getContent();
        if (list!=null&&list.size()>0) {
            for (int i=0;i<list.size();i++) {
                AccTagManageDTO atm = (AccTagManageDTO)list.get(i);
                String result = checkTagExistLowerOrUse(atm.getId());
                if (result!=null&&!"".equals(result)) {
                    atm.setExistLowerOrUse("Y");
                }
            }
        }

        return res;
    }

    @Transactional
    public String save(AccTagManageDTO accTagManageDTO) {
        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String branchCode = CurrentUser.getCurrentLoginManageBranch();
        String accBookType = CurrentUser.getCurrentLoginAccountType();
        String accBookCode = CurrentUser.getCurrentLoginAccount();

        UserInfo user = CurrentUser.getCurrentUser();

        List<?> result = accTagManageRespository.findByCenterCodeAndAccBookCodeAndAccBookTypeAndTagCode(centerCode, accBookCode, accBookType, accTagManageDTO.getTagCode());

        if(result.size() > 0){
            return "Tag_ISEXIST";
        }
        AccTagManage accTagManage = new AccTagManage();
        accTagManage.setAccBookType(accBookType);
        accTagManage.setAccBookCode(accBookCode);
        accTagManage.setTagCode(accTagManageDTO.getTagCode());
        accTagManage.setCenterCode(centerCode);
        accTagManage.setTagName(accTagManageDTO.getTagName());
        accTagManage.setEndFlag(accTagManageDTO.getEndFlag());
        accTagManage.setFlag("1");
        if(StringUtils.isNotEmpty(accTagManageDTO.getUpperTag())){
            accTagManage.setUpperTag(accTagManageDTO.getUpperTag());
        }
        accTagManage.setCreateBy(CurrentUser.getCurrentUser().getId() + "");
        accTagManage.setCreateTime(CurrentTime.getCurrentTime());

        accTagManageRespository.save(accTagManage);

        return "SUCCESS";
    }

    @Transactional
    public String delTag(long id) {
        //删除前校验，是否存在下级，是否使用过
        String result = checkTagExistLowerOrUse(id);
        if (result!=null&&!"".equals(result)) {
            return result;
        }
        accTagManageRespository.deleteById(id);
        return "";
    }

    @Override
    public String update(AccTagManageDTO accTagManageDTO) {

        AccTagManage tagManage = accTagManageRespository.findById(accTagManageDTO.getId()).get();

        //编辑校验，是否存在下级，是否使用过
        String re = checkTagExistLowerOrUse(tagManage.getId());
        if (re!=null&&!"".equals(re)) {
            return re;
        }

        List<?> result = accTagManageRespository.findByCenterCodeAndAccBookCodeAndAccBookTypeAndTagCode(CurrentUser.getCurrentLoginManageBranch(), tagManage.getAccBookCode(), tagManage.getAccBookType(), accTagManageDTO.getTagCode());
        if(!accTagManageDTO.getTagCode().equals(accTagManageDTO.getNtagCode()) && result.size() >0){
            return "Tag_ISEXIST";
        }

        tagManage.setTagCode(accTagManageDTO.getTagCode());
        tagManage.setTagName(accTagManageDTO.getTagName());
        tagManage.setEndFlag(accTagManageDTO.getEndFlag());
        tagManage.setLastModifyBy(CurrentUser.getCurrentUser().getId() + "");
        tagManage.setLastModifyTime(CurrentTime.getCurrentTime());
        accTagManageRespository.save(tagManage);

        return "SUCCESS";
    }

    /**
     * 存在下级的标注不允许删除、已经使用过的标注不允许删除
     * @param id
     * @return
     */
    private String checkTagExistLowerOrUse(long id){
        AccTagManage tagManage = accTagManageRespository.findById(id).get();
        if (tagManage!=null) {
            List<?> result = accTagManageRespository.findByCenterCodeAndAccBookCodeAndAccBookTypeAndUpperTag(CurrentUser.getCurrentLoginManageBranch(), tagManage.getAccBookCode(), tagManage.getAccBookType(), tagManage.getTagCode());
            if (result!=null&&result.size()>0) {
                return "existLower";
            }
            //只有末级标注才有可能被使用
            if ("0".equals(tagManage.getEndFlag())) {
                StringBuffer sql = new StringBuffer("select a.acc_book_code as accBookCode,a.voucher_no as voucherNo,a.suffix_no as suffixNo from accsubvoucher a where a.flag = '"+tagManage.getTagCode()+"'");
                sql.append(" and a.center_code = '"+CurrentUser.getCurrentLoginManageBranch()+"'");
                String sql1 = sql.toString();
                sql1 =sql1.replaceAll("accsubvoucher","accsubvoucherhis");
                sql1 =sql1.replaceAll("a\\.","ah\\.");
                sql1 =sql1.replaceAll("a ","ah ");
                sql.append(" union all ");
                sql.append(sql1);
                result = accTagManageRespository.queryBySqlSC(sql.toString());
                if (result!=null&&result.size()>0) {
                    return "use";
                } else {
                    sql.setLength(0);
                    sql.append("select a.acc_book_code as accBookCode,a.voucher_no as voucherNo,a.suffix_no as suffixNo from accsubvoucher a where a.center_code='"+CurrentUser.getCurrentLoginManageBranch()+"' and  a.acc_book_code='"+CurrentUser.getCurrentLoginAccount()+"' and a.flag like '%"+tagManage.getTagCode()+",%' or a.flag like '%,"+tagManage.getTagCode()+"'");
                    sql1 = sql.toString();
                    sql1 =sql1.replaceAll("accsubvoucher","accsubvoucherhis");
                    sql1 =sql1.replaceAll("a\\.","ah\\.");
                    sql1 =sql1.replaceAll("a ","ah ");
                    sql.append(" union all ");
                    sql.append(sql1);
                    result = accTagManageRespository.queryBySqlSC(sql.toString());
                    if (result!=null&&result.size()>0) {
                        return "use";
                    }
                }
            }
        }
        return "";
    }

    @Override
    public List<?> qryTagCode(String value){
        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String branchCode = CurrentUser.getCurrentLoginManageBranch();
        String accBookCode = CurrentUser.getCurrentLoginAccount();
        String accBookType = CurrentUser.getCurrentLoginAccountType();
        List resultList=new ArrayList();
        if (value!=null&&!"".equals(value)) {
            resultList = queryTagJournalByValue(value);
        }else {

            //获取所有父菜单为空也就是最外层的父菜单
            StringBuffer sql = new StringBuffer(" SELECT a.tag_code as value,a.tag_name as text FROM acctagmanage a " +
                    "WHERE a.upper_tag LIKE \"\" OR a.upper_tag IS NULL and a.center_code ='" + centerCode +
                    "' and a.acc_book_code='" + accBookCode + "' and a.acc_book_type='" + accBookType + "' ORDER BY a.tag_code");
            List<?> list = accTagManageRespository.queryBySqlSC(sql.toString());
            //遍历查询父菜单的子菜单
            if (list != null && list.size() > 0 && !list.isEmpty()) {
                for (Object obj : list) {
                    Map map = new HashMap();
                    ((Map)obj).put("state","closed");
                    map.putAll((Map) obj); //将list中的每个元素value、text以map的形式放入map集合中
                    List list2 = qryChildrenTag((String) map.get("value"));
                    if (list2 != null && list2.size() > 0 && !list2.isEmpty()) {
                        map.put("children", list2);//将上一个map以父节点的形式来放入他的子节点
                    }
                    resultList.add(map);
                }
            }
        }
        return resultList;
    }

    private List<AccTagManageDTO> qryChildrenTag(String value){
        String accBookCode = CurrentUser.getCurrentLoginAccount();
        String accBookType = CurrentUser.getCurrentLoginAccountType();
        List list1=new ArrayList();
        StringBuffer sql = new StringBuffer("select a.tag_code as value, a.tag_name as text from acctagmanage a" +
                " where a.upper_tag ='"+value+"' and a.center_code = '"+CurrentUser.getCurrentLoginManageBranch()+"' and a.acc_book_code='"+accBookCode+"' and a.acc_book_type='"+accBookType+"'");
        List<?> list= accTagManageRespository.queryBySqlSC(sql.toString());
        if(list!=null&&list.size()>0&&!list.isEmpty()){
            for (Object obj : list) {
                Map map= new HashMap();
                map.putAll((Map) obj);
                List list2=qryChildrenTag((String) map.get("value"));
                if(list2!=null&&list2.size()>0&&!list2.isEmpty()){
                    map.put("value",map.get("value"));
                    map.put("text",map.get("text"));
                    map.put("children",list2);
                }
                map.put("value",map.get("value"));
                map.put("text",map.get("text"));
                list1.add(map);
            }
        }
        return list1;
    }

    public List<?> queryTagJournalByValue(String value){
        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String branchCode = CurrentUser.getCurrentLoginManageBranch();
        String accBookCode = CurrentUser.getCurrentLoginAccount();
        String accBookType = CurrentUser.getCurrentLoginAccountType();
        Set<String> neededIds = new HashSet<String>();
        //查询出经过like筛选的标注Set
        StringBuffer neededSql = new StringBuffer("select a.tag_code as value from acctagmanage a where 1=1");

        int paramsNo = 1;
        Map<Integer, Object> params = new HashMap<>();

        neededSql.append(" AND a.center_code = ?" + paramsNo);
        params.put(paramsNo, centerCode);
        paramsNo++;
        neededSql.append(" AND a.acc_book_type = ?" + paramsNo);
        params.put(paramsNo, accBookType);
        paramsNo++;
        neededSql.append(" AND a.acc_book_code = ?" + paramsNo);
        params.put(paramsNo, accBookCode);
        paramsNo++;
        neededSql.append(" AND a.tag_code = ?" + paramsNo);
        params.put(paramsNo, "%"+value+"%");
        paramsNo++;

        List<?> neededList =accTagManageRespository.queryBySqlSC(neededSql.toString(), params);
        if (neededList!=null&&neededList.size()>0) {
            for(int j=0;j<neededList.size();j++){
                List resultList=new ArrayList();
                Map map = (Map<String, Object>) neededList.get(j);
                neededIds.add(map.get("value").toString());
            }
        }

        List resultList=new ArrayList();
        //查询最外层
        StringBuffer sql = new StringBuffer(" SELECT a.tag_code as value,a.tag_name as text FROM acctagmanage a " +
                "WHERE (a.upper_tag LIKE \"\" OR a.upper_tag IS NULL )and a.center_code ='" + centerCode +
                "' and a.acc_book_code='" + accBookCode + "' and a.acc_book_type='" + accBookType + "' ORDER BY a.tag_code");
        List<?> list =accTagManageRespository.queryBySqlSC(sql.toString());

        for (Object obj : list) {
            Map map = new HashMap();
            map.putAll((Map) obj);
            //判断有无下一级标注
            //下级标注集合
            List childList = qryChildrenSubjectByValue((String)map.get("value"), neededIds);
            if(childList.size()<=0 || childList == null){
                if(neededIds.contains(map.get("value").toString())){
                    //如果该一级标注在needed中，那么查询该标注下的所有子级标注
                    map.put("value",map.get("value"));
                    map.put("text",map.get("text"));
                    resultList.add(map);
                }
            }else{
                if(neededIds.contains(map.get("value").toString())){
                    List childAll = qryChildrenTag(map.get("value").toString());
                    map.put("children",childAll);//将上一个map以父节点的形式来放入他的子节点
                    resultList.add(map);
                }else{

                    map.put("children",childList);
                    resultList.add(map);
                }
            }

        }

        return resultList ;
    }
    private List<AccTagManageDTO> qryChildrenSubjectByValue(String tagCode,Set<String> set){
        List list1=new ArrayList();
        String accBookCode = CurrentUser.getCurrentLoginAccount();
        String accBookType = CurrentUser.getCurrentLoginAccountType();
        //通过上一级标注编码查询下一级标注
        StringBuffer sql = new StringBuffer("select a.tag_code as value, a.tag_name as text from acctagmanage a" +
                " where a.center_code='"+CurrentUser.getCurrentLoginManageBranch()+"' and a.upper_tag ='"+tagCode+"' and a.acc_book_code='"+accBookCode+"' and a.acc_book_type='"+accBookType+"'");

        List<?> list =accTagManageRespository.queryBySqlSC(sql.toString());
        if(list!=null&&list.size()>0&&!list.isEmpty()){
            for (Object obj : list) {
                Map map = new HashMap();
                map.putAll((Map) obj);
                List list2=qryChildrenSubjectByValue((String)map.get("value"), set);
                if(list2!=null&&list2.size()>0&&!list2.isEmpty()){
                    map.put("children",list2);
                }
                String currentId = map.get("value").toString();
                map.put("value",map.get("value"));
                map.put("text",map.get("text"));

                if (list2!=null&&list2.size()>0){
                    list1.add(map);
                }
                if (!(list2!=null&&list2.size()>0) && set.contains(currentId)) {
                    //   map.put("children",list2);
                    List list4 = qryChildrenTag(map.get("value").toString());
                    //  list1.add(list4);
                    map.put("children",list4);
                    list1.add(map);
                }
            }
        }
        return list1 ;
    }
}
