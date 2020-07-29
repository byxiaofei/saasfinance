package com.sinosoft.service.impl.account;

import com.sinosoft.common.CurrentTime;
import com.sinosoft.common.CurrentUser;
import com.sinosoft.common.CusSpecification;
import com.sinosoft.domain.account.AccRemarkManage;
import com.sinosoft.dto.MenuInfoDTO;
import com.sinosoft.dto.account.AccRemarkManageDTO;
import com.sinosoft.repository.account.AccRemarkManageRespository;
import com.sinosoft.service.account.AccRemarkManageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class AccRemarkManageServiceImpl implements AccRemarkManageService {
    @Resource
    private AccRemarkManageRespository accRemarkManageRespository ;

    @Override
    public Page<AccRemarkManageDTO> qryAccRemarkManage(int page, int rows, AccRemarkManageDTO accRemarkManageDTO) {
        Page<AccRemarkManage> result ;
        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String branchCode = CurrentUser.getCurrentLoginManageBranch();

        CusSpecification<AccRemarkManage> cus = new CusSpecification<>() ;
        result = accRemarkManageRespository.findAll(new CusSpecification<AccRemarkManage>().and(
                CusSpecification.Cnd.eq("RemarkCode",accRemarkManageDTO.getRemarkCode()),
                CusSpecification.Cnd.like("RemarkName",accRemarkManageDTO.getRemarkName()),
                CusSpecification.Cnd.eq("CenterCode",centerCode),
                CusSpecification.Cnd.eq("AccBookType",CurrentUser.getCurrentLoginAccountType()),
                CusSpecification.Cnd.eq("AccBookCode",CurrentUser.getCurrentLoginAccount())
        ).asc("RemarkCode").asc("RemarkName"),new PageRequest((page - 1),rows)) ;


        List<AccRemarkManage> listInfo = result.getContent() ;
        List<AccRemarkManageDTO> listDto = new ArrayList<>() ;
        if(listInfo != null && listInfo.size() > 0){
            for(AccRemarkManage accRemarkManage1 : listInfo){
                AccRemarkManageDTO dto = AccRemarkManageDTO.toDTO(accRemarkManage1) ;
                listDto.add(dto) ;
            }
        }
        Page<AccRemarkManageDTO> pageDto = new PageImpl<>(listDto,result.getPageable(),result.getTotalElements());
        return pageDto;
    }

    @Override
    public List<?> qryByCodeAndName(String RemarkCode, String RemarkName) {
        List<AccRemarkManage> acclist = new ArrayList<>() ;
        acclist = accRemarkManageRespository.findAllByCodeAndName(RemarkCode,RemarkName,CurrentUser.getCurrentLoginAccount(), CurrentUser.getCurrentLoginManageBranch()) ;
        //以实体类形式输出到页面？
        List<AccRemarkManageDTO> listDto = new ArrayList<>() ;
        if(acclist != null && acclist.size() > 0){
            for(AccRemarkManage accRemarkManage : acclist){
                AccRemarkManageDTO dto = AccRemarkManageDTO.toDTO(accRemarkManage) ;
                //表中有科目名称该字段
                // dto.setSubjectName(abstractInfoRespository.qryNameByCode(dto.getSubjectCode()));
                listDto.add(dto) ;
            }
        }
        return acclist;
    }

    @Override
    public List<?> qryCodeList(String type) {
        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String branchCode = CurrentUser.getCurrentLoginManageBranch();
        List<?> codelist = new ArrayList<>() ;
        String accBookCode = CurrentUser.getCurrentLoginAccount();
        String accBookType = CurrentUser.getCurrentLoginAccountType();
        if(type.equals("RemarkCode")){
            codelist = accRemarkManageRespository.findRemarkCode(centerCode,accBookCode,accBookType);
        }
        return codelist;
    }

    @Override
    public String saveAccRemarkManage(AccRemarkManageDTO accRemarkManageDTO) {
        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String branchCode = CurrentUser.getCurrentLoginManageBranch();

            //核算单位（可为空）
            accRemarkManageDTO.setCenterCode(centerCode);
            accRemarkManageDTO.setAccBookType(CurrentUser.getCurrentLoginAccountType()); //账套类型
            accRemarkManageDTO.setAccBookCode(CurrentUser.getCurrentLoginAccount());//账套编码
           // accRemarkManageDTO.setFlag("Y");//状态
            //根据科目编码获取对应的科目名称
            String name = getSubjectName(accRemarkManageDTO.getItemCode());
            //System.out.println("科目名称："+name);
            if(name == null || name.equals("")){
                return "error";
            }
            accRemarkManageDTO.setItemName(name);
            accRemarkManageDTO.setCreateBy(CurrentUser.getCurrentUser().getId() + "");
            accRemarkManageDTO.setCreateTime(CurrentTime.getCurrentTime());
            List<?> list = accRemarkManageRespository.checkExiste(CurrentUser.getCurrentLoginAccountType(),CurrentUser.getCurrentLoginAccount(),accRemarkManageDTO.getRemarkCode(), centerCode);
            if(list != null && list.size()> 0)
                return ACCREMARKMANAGE_ISEXISTE ;
            AccRemarkManage accRemarkManage = AccRemarkManageDTO.toEntity(accRemarkManageDTO) ;
            accRemarkManageRespository.save(accRemarkManage) ;
            return "success" ;
    }

    @Override
    public String editAccRemarkManage( long id,AccRemarkManageDTO accRemarkManageDTO) {
        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String branchCode = CurrentUser.getCurrentLoginManageBranch();

        //System.out.println("accRemarkManageDTO-->Impl："+accRemarkManageDTO);
            AccRemarkManage info = accRemarkManageRespository.qryAccRemarkManageById(id);
            //先判断是否修改过摘要编码
            if(!info.getRemarkCode().equals(accRemarkManageDTO.getRemarkCode())){
                //查重
                List<?> list = accRemarkManageRespository.checkExiste(CurrentUser.getCurrentLoginAccountType(),CurrentUser.getCurrentLoginAccount(),accRemarkManageDTO.getRemarkCode(), centerCode);
                if(list != null && list.size()> 0)
                    return ACCREMARKMANAGE_ISEXISTE ;
            }
            //页面上修改过的属性
            info.setRemarkCode(accRemarkManageDTO.getRemarkCode());//摘要编码
            info.setRemarkName(accRemarkManageDTO.getRemarkName());//摘要名称
            info.setItemCode(accRemarkManageDTO.getItemCode());//科目编码
            String name = getSubjectName(accRemarkManageDTO.getItemCode());
            info.setItemName(name);//科目名称
            info.setFlag(accRemarkManageDTO.getFlag());//使用状态
            info.setModifyReason(accRemarkManageDTO.getModifyReason());//修改原因
            info.setTemp(accRemarkManageDTO.getTemp());
            //后台自动获取的属性
            info.setCenterCode(centerCode);//核算单位
            info.setAccBookType(CurrentUser.getCurrentLoginAccountType());  //账套类型
            info.setAccBookCode(CurrentUser.getCurrentLoginAccount()); //账套编码
            info.setLastModifyBy(CurrentUser.getCurrentUser().getId() + "");//最后一次修改人
            info.setLastModifyTime(CurrentTime.getCurrentTime()); //最后一次修改时间

            accRemarkManageRespository.save(info) ;
            return "success" ;
    }

    @Override
    public String deleteAccRemarkManage(String AccBookType,String AccBookCode,String RemarkCode) {

            List<AccRemarkManage> list = new ArrayList<>();
             list = accRemarkManageRespository.checkExiste(AccBookType,AccBookCode,RemarkCode, CurrentUser.getCurrentLoginManageBranch());
            if(list != null && list.size()> 0){
                for(AccRemarkManage info : list){
                    accRemarkManageRespository.delete(info);
                }
                return "success" ;
            }else {
                return "error";
            }
    }
    @Override
    public List<?> qrySubjectCodeForCheck(String value) {
        long start = System.currentTimeMillis();
        List resultListAll=new ArrayList();
        String subjectTypeSql = "select c.code_code as id,c.code_name as text from codemanage c where c.code_type = 'subjectType' order by id";
        List<?> subjectTypeList = accRemarkManageRespository.queryBySqlSC(subjectTypeSql);
        if (subjectTypeList!=null&&subjectTypeList.size()>0) {
            //查询存在下级的科目id
            StringBuffer superSql = new StringBuffer("select distinct s.super_subject as superSubject from subjectinfo s where 1=1 and s.super_subject !='' and s.super_subject is not null");

            int paramsNo = 1;
            Map<Integer, Object> params = new HashMap<>();

            superSql.append(" and s.account = ?" + paramsNo);
            params.put(paramsNo, CurrentUser.getCurrentLoginAccount());
            paramsNo++;

            List<?> superList = accRemarkManageRespository.queryBySqlSC(superSql.toString(), params);
            Set<String> superIdSet = new HashSet<>();
            if (superList!=null&&superList.size()>0) {
                for (Object obj: superList) {
                    superIdSet.add(((Map)obj).get("superSubject").toString());
                }
            }

            for (Object o : subjectTypeList) {
                List resultList=new ArrayList();
                Map subjectTypeMap = new HashMap();
                subjectTypeMap.putAll((Map) o);

                //获取所有父菜单为空也就是最外层的父菜单
                StringBuffer sql=new StringBuffer("SELECT s.id AS id,CONCAT(s.all_subject,s.subject_code,\"/\") as sid,s.subject_name as text,s.end_flag as endFlag " +
                        "FROM subjectinfo s WHERE 1=1");

                paramsNo = 1;
                params = new HashMap<>();

                sql.append(" AND s.account = ?" + paramsNo);
                params.put(paramsNo, CurrentUser.getCurrentLoginAccount());
                paramsNo++;

                sql.append(" AND (s.super_subject LIKE \"\" OR s.super_subject IS NULL)");

                sql.append(" AND s.subject_type = ?" + paramsNo);
                params.put(paramsNo, subjectTypeMap.get("id"));
                paramsNo++;

                sql.append(" ORDER BY s.id");

                List<?> list =accRemarkManageRespository.queryBySqlSC(sql.toString(), params);
                //遍历查询父菜单的子菜单
                if(list!=null&&list.size()>0&&!list.isEmpty()){
                    for (Object obj : list) {
                        Map map= new HashMap();
                        map.putAll((Map) obj); //将list中的每个元素以map的形式放入map集合中
                        List list2 = new ArrayList();
                        if (superIdSet.contains(map.get("id").toString())) {
                            list2=qryChildrenSubject((Integer) map.get("id"),superIdSet);
                        }
                        if(list2!=null&&list2.size()>0&&!list2.isEmpty()){
                            map.put("children",list2);//将上一个map以父节点的形式来放入他的子节点
                            map.put("state","closed");
                        }
                        if ((list2!=null&&list2.size()>0&&!list2.isEmpty())) {
                            resultList.add(map);//存在子级
                        } else if (!(list2!=null&&list2.size()>0&&!list2.isEmpty()) && "0".equals(map.get("endFlag"))){
                            //无子级，但为末级
                            resultList.add(map);
                        } else {
                            //不需要
                        }
                    }
                }
                if (resultList!=null&&resultList.size()>0) {
                    subjectTypeMap.put("children",resultList);
                    subjectTypeMap.put("state","closed");
                    resultListAll.add(subjectTypeMap);
                }
            }
        }
        System.out.println("摘要处科目树查询用时:" + (System.currentTimeMillis()-start) + " ms");
        return resultListAll;
    }
    private List<MenuInfoDTO> qryChildrenSubject(Integer id, Set<String> superIdSet){
        List list1=new ArrayList();
        StringBuffer sql = new StringBuffer("select s.id as id, CONCAT(s.all_subject,s.subject_code,\"/\") as sid,s.subject_name as text,s.end_flag as endFlag from subjectinfo s where 1=1");

        int paramsNo = 1;
        Map<Integer, Object> params = new HashMap<>();
        sql.append(" and s.super_subject = ?" + paramsNo);
        params.put(paramsNo, id);
        paramsNo++;

        List<?> list= accRemarkManageRespository.queryBySqlSC(sql.toString(), params);
        if(list!=null&&list.size()>0&&!list.isEmpty()){
            for (Object obj : list) {
                Map map= new HashMap();
                map.putAll((Map) obj);
                List list2 = new ArrayList();
                if (superIdSet.contains(map.get("id").toString())) {
                    list2=qryChildrenSubject((Integer) map.get("id"),superIdSet);
                }
                if(list2!=null&&list2.size()>0&&!list2.isEmpty()){
                    map.put("children",list2);
                    map.put("state","closed");
                }
                map.put("id",map.get("id"));
                map.put("sid",map.get("sid"));
                map.put("text",map.get("text"));

                if (list2!=null&&list2.size()>0&&!list2.isEmpty()) {
                    list1.add(map);//存在子级
                } else if (!(list2!=null&&list2.size()>0&&!list2.isEmpty()) && "0".equals(map.get("endFlag"))){
                    //无子级，但为末级
                    list1.add(map);
                } else {
                    //不需要
                }
            }
        }
        return list1;
    }
    public String getSubjectName(String subjectCode){
      List<?> list = new ArrayList<>();
        list = accRemarkManageRespository.findSubjectName(subjectCode,CurrentUser.getCurrentLoginAccount());
        String subjectName = "";
        if(list.size()>0){
            Map map = new HashMap();
            map = (Map)list.get(0) ;
            subjectName =map.get("name")+"/"+subjectName;
           /*do{
                list = accRemarkManageRespository.findSubjectNameById(Integer.parseInt(map.get("superSubject").toString()));
                map = (Map)list.get(0) ;
               subjectName =map.get("name")+"/"+subjectName;
            } while(!((map.get("superSubject").toString()).isEmpty()));*/
            while(!((map.get("superSubject").toString()).isEmpty())){
                list = accRemarkManageRespository.findSubjectNameById(Integer.parseInt(map.get("superSubject").toString()));
                map = (Map)list.get(0) ;
                subjectName =map.get("name")+"/"+subjectName;
            }
        }
        else {
            return "";
        }

        return subjectName ;
    }
}
