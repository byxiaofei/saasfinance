package com.sinosoft.service.impl.account;

import com.sinosoft.common.CurrentUser;
import com.sinosoft.dto.account.AccTagManageDTO;
import com.sinosoft.repository.account.QueryTagJournalRespository;
import com.sinosoft.service.account.QueryTagJournalService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

@Service
public class QueryTagJournalServiceImpl implements QueryTagJournalService {

    @Resource
    QueryTagJournalRespository queryTagJournalRespository ;

    @Override
    public List<?> qryVoucherTag(String yearMonthDateBegin, String yearMonthDateEnd, String DateStart, String DateStop, String tagCode) {
        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String branchCode = CurrentUser.getCurrentLoginManageBranch();
        String accBookType = CurrentUser.getCurrentLoginAccountType();
        String accBookCode = CurrentUser.getCurrentLoginAccount();

        StringBuffer sql = new StringBuffer("SELECT DISTINCT am.voucher_date AS 'voucherDate', am.voucher_no AS 'voucherNo',am.year_month_date AS 'yearMonthDate', ac.suffix_no AS 'suffixNo', ac.flag AS 'tagCode'," +
                " atg.tag_name AS 'tagName', ac.remark AS 'remark', CAST( IFNULL(ac.debit_dest, 0.00) AS CHAR ) AS 'debitDest', CAST( IFNULL(ac.credit_dest, 0.00) AS CHAR )" +
                " AS 'creditDest' FROM accsubvoucher ac  LEFT JOIN accmainvoucher am ON am.center_code = ac.center_code AND am.branch_code = ac.branch_code AND am.acc_book_type = " +
                "ac.acc_book_type AND am.acc_book_code = ac.acc_book_code AND am.voucher_no = ac.voucher_no LEFT JOIN acctagmanage atg ON atg.center_code = ac.center_code and ac.flag = atg.tag_code WHERE 1 = 1 AND" +
                " am.voucher_no is NOT NULL  AND ac.flag is NOT NULL");

        int paramsNo = 1;
        Map<Integer, Object> params = new HashMap<>();

        if (StringUtils.isNotEmpty(centerCode)) {
            sql.append(" AND ac.center_code = ?" + paramsNo);
            params.put(paramsNo, centerCode);
            paramsNo++;
        }
        if (StringUtils.isNotEmpty(branchCode)) {
            sql.append(" AND ac.branch_code = ?" + paramsNo);
            params.put(paramsNo, branchCode);
            paramsNo++;
        }
        if (StringUtils.isNotEmpty(accBookType)) {
            sql.append(" and ac.acc_book_type = ?" + paramsNo);
            params.put(paramsNo, accBookType);
            paramsNo++;
        }
        if (StringUtils.isNotEmpty(accBookCode)) {
            sql.append(" AND ac.acc_book_code = ?" + paramsNo);
            params.put(paramsNo, accBookCode);
            paramsNo++;
        }
        //开始会计期间
        if (StringUtils.isNotEmpty(yearMonthDateBegin)) {
            sql.append(" AND am.year_month_date >= ?" + paramsNo);
            params.put(paramsNo, yearMonthDateBegin);
            paramsNo++;
        }
        //结束会计期间
        if (StringUtils.isNotEmpty(yearMonthDateEnd)) {
            sql.append(" AND am.year_month_date <= ?" + paramsNo);
            params.put(paramsNo, yearMonthDateEnd);
            paramsNo++;
        }
        //开始时间
        if (StringUtils.isNotEmpty(DateStart)) {
            sql.append(" AND am.voucher_date >= ?" + paramsNo);
            params.put(paramsNo, DateStart);
            paramsNo++;
        }
        //结束时间
        if (StringUtils.isNotEmpty(DateStop)) {
            sql.append(" AND am.voucher_date <= ?" + paramsNo);
            params.put(paramsNo, DateStop);
            paramsNo++;
        }
        //标注代码
        if (StringUtils.isNotEmpty(tagCode)) {
            String []str = tagCode.split(",");
            sql.append(" AND atg.tag_code IN (" + "?" + paramsNo + ")");
            params.put(paramsNo, Arrays.asList(str));
            paramsNo++;
        }
        //连表查询（历史表）

        String sql1 = sql.toString();
        sql1 =sql1.replaceAll("accsubvoucher","accsubvoucherhis");
        sql1 =sql1.replaceAll("ac\\.","ach\\.");
        sql1 =sql1.replaceAll("ac ","ach ");
        sql1 =sql1.replaceAll("accmainvoucher","accmainvoucherhis");
        sql1 =sql1.replaceAll("am\\.","amh\\.");
        sql1 =sql1.replaceAll("am ","amh ");

        sql.append(" union all ");
        sql.append(sql1);
        sql.append(" ORDER BY tagCode,voucherNo");
        List<?> result = queryTagJournalRespository.queryBySqlSC(sql.toString(), params);

        /**
         * 处理结果集、添加小计与合计
         */
        List<Map<String,String>> list = new ArrayList<>();
        if(result.size() > 0){
            BigDecimal totalDebitSum = new BigDecimal("0.00");
            BigDecimal totalCreditSum = new BigDecimal("0.00");
            BigDecimal littleCreditSum = new BigDecimal("0.00");
            BigDecimal littleDebitSum = new BigDecimal("0.00");
            String lc = "";
            String ld = "";
            String tCode = (String) ((Map)result.get(0)).get("tagCode");
            for(Object obj : result){
                //先判断是否需要添加小计行
               String s = (String) ((Map)obj).get("tagCode");
                if(!tCode.equals(s)){
                    Map map = new HashMap();
                    tCode = (String)((Map) obj).get("tagCode");
                    lc = littleCreditSum.toString();
                    ld = littleDebitSum.toString();
                    map.put("remark","小计");
                    map.put("debitDest",ld);
                    map.put("creditDest",lc);
                    list.add(map);
                    littleCreditSum = new BigDecimal("0.00");
                    littleDebitSum = new BigDecimal("0.00");
                }
                //借款总计
                totalDebitSum = totalDebitSum.add(new BigDecimal((String) ((Map)obj).get("debitDest")));
                //贷款总计
                totalCreditSum = totalCreditSum.add(new BigDecimal((String) ((Map)obj).get("creditDest")));
                //借款小计
                littleDebitSum = littleDebitSum.add(new BigDecimal((String) ((Map)obj).get("debitDest")));
                //贷款小计
                littleCreditSum = littleCreditSum.add(new BigDecimal((String) ((Map)obj).get("creditDest")));

                list.add((Map)obj);
            }
            lc  = littleCreditSum.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
            ld = littleDebitSum.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
            Map map1 = new HashMap();
            map1.put("remark","小计");
            map1.put("debitDest",ld);
            map1.put("creditDest",lc);
            list.add(map1);

            String tC = totalCreditSum.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
            String tD = totalDebitSum.setScale(2, BigDecimal.ROUND_HALF_UP).toString();

            Map map = new HashMap();
            map.put("remark","总计");
            map.put("debitDest",tD);
            map.put("creditDest",tC);
            list.add(map);
        }

        return list;
    }

    @Override
    public List<?> queryTagJournal(String value) {
        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String accBookCode = CurrentUser.getCurrentLoginAccount();
        String accBookType = CurrentUser.getCurrentLoginAccountType();
        List resultList=new ArrayList();
        System.out.println("value:"+value);
        if (value!=null&&!"".equals(value)) {
            resultList = queryTagJournalByValue(value);
        }else {
            //获取所有父菜单为空也就是最外层的父菜单
            List<?> list = queryTagJournalRespository.querySuperTagManage(centerCode, accBookType, accBookCode);
            //遍历查询父菜单的子菜单
            if (list != null && list.size() > 0 && !list.isEmpty()) {
                for (Object obj : list) {
                    Map map = new HashMap();
                    map.putAll((Map) obj); //将list中的每个元素value、text以map的形式放入map集合中
                    List list2 = qryChildrenTag((String) map.get("value"));
                    if (list2 != null && list2.size() > 0 && !list2.isEmpty()) {
                        map.put("children", list2);//将上一个map以父节点的形式来放入他的子节点
                        map.put("state","closed");
                    }
                    resultList.add(map);
                }
            }
        }
        return resultList;
    }

    private List<AccTagManageDTO> qryChildrenTag(String value){
        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String accBookCode = CurrentUser.getCurrentLoginAccount();
        String accBookType = CurrentUser.getCurrentLoginAccountType();
        List list1=new ArrayList();

        List<?> list= queryTagJournalRespository.queryTagManageByUpperTag(centerCode, accBookType, accBookCode, value);

        if(list!=null&&list.size()>0&&!list.isEmpty()){
            for (Object obj : list) {
                Map map= new HashMap();
                map.putAll((Map) obj);
                List list2=qryChildrenTag((String) map.get("value"));
                if(list2!=null&&list2.size()>0&&!list2.isEmpty()){
                    map.put("value",map.get("value"));
                    map.put("text",map.get("text"));
                    map.put("children",list2);
                    map.put("state","closed");
                }
                map.put("value",map.get("value"));
                map.put("text",map.get("text"));
                list1.add(map);
            }
        }
        return list1;
    }

    private List<AccTagManageDTO> qryChildrenTag2(String value){
        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String accBookCode = CurrentUser.getCurrentLoginAccount();
        String accBookType = CurrentUser.getCurrentLoginAccountType();
        List list1=new ArrayList();

        List<?> list= queryTagJournalRespository.queryTagManageByUpperTag(centerCode, accBookType, accBookCode, value);

        if(list!=null&&list.size()>0&&!list.isEmpty()){
            for (Object obj : list) {
                Map map= new HashMap();
                map.putAll((Map) obj);
                List list2=qryChildrenTag2((String) map.get("value"));
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
        String accBookCode = CurrentUser.getCurrentLoginAccount();
        String accBookType = CurrentUser.getCurrentLoginAccountType();
        Set<String> neededIds = new HashSet<String>();

        //查询出经过like筛选的标注Set
        List<?> neededList =queryTagJournalRespository.queryTagManageByTagName(centerCode, accBookType, accBookCode, value);
        if (neededList!=null&&neededList.size()>0) {
            for(int j=0;j<neededList.size();j++){
                Map map = (Map<String, Object>) neededList.get(j);
                neededIds.add(map.get("value").toString());
            }
        }

        List resultList=new ArrayList();
        //查询最外层
        List<?> list =queryTagJournalRespository.querySuperTagManage(centerCode, accBookType, accBookCode);

        for (Object obj : list) {
            Map map = new HashMap();
            map.putAll((Map) obj);
            //判断有无下一级标注
            //下级标注集合
            List childList = qryChildrenSubjectByValue((String)map.get("value"), neededIds);
            if(childList == null || childList.size()<=0){
                if(neededIds.contains(map.get("value").toString())){
                    //如果该一级标注在needed中，那么查询该标注下的所有子级标注
                    map.put("value",map.get("value"));
                    map.put("text",map.get("text"));
                    resultList.add(map);
                }
            }else{
                if(neededIds.contains(map.get("value").toString())){
                    List childAll = qryChildrenTag2(map.get("value").toString());
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
        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String accBookCode = CurrentUser.getCurrentLoginAccount();
        String accBookType = CurrentUser.getCurrentLoginAccountType();
        //通过上一级标注编码查询下一级标注
        List<?> list =queryTagJournalRespository.queryTagManageByUpperTag(centerCode, accBookType, accBookCode, tagCode);
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
                    List list4 = qryChildrenTag2(map.get("value").toString());
                    map.put("children",list4);
                    list1.add(map);
                }
            }
        }
        return list1 ;
    }
}
