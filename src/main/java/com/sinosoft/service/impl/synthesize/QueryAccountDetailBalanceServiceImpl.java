package com.sinosoft.service.impl.synthesize;

import com.sinosoft.common.Constant;
import com.sinosoft.common.CurrentUser;
import com.sinosoft.common.RedisCache;
import com.sinosoft.common.RedisConstant;
import com.sinosoft.dto.VoucherDTO;
import com.sinosoft.repository.AccArticleBalanceRepository;
import com.sinosoft.repository.AccDetailBalanceRepository;
import com.sinosoft.repository.VoucherRepository;
import com.sinosoft.service.synthesize.DetailAccountService;
import com.sinosoft.service.synthesize.QueryAccountDetailBalanceService;
import com.sinosoft.util.ExcelUtil;
import com.sinosoft.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;

@Service
public class QueryAccountDetailBalanceServiceImpl implements QueryAccountDetailBalanceService {
    @Autowired
    private AccDetailBalanceRepository accDetailBalanceRepository;
    @Resource
    private AccArticleBalanceRepository accArticleBalanceRepository;
    @Resource
    private DetailAccountService detailAccountService;
    @Value("${MODELPath}")
    private String MODELPath ;
    @Resource
    private VoucherRepository voucherRepository;

    /*
        导出操作，为避免重复查询，临时存储校验查询结果，此结果即为导出数据，否则不可用
        key = 用户ID + 下划线 + 当前机构 + 下划线 + 当前核算单位 + 下划线 + 当前登录账套类型 + 下划线 + 当前登录账套编码
     */
    private Map<String, Object> exportDataMap;

    /**
     * 查询科目余额
     * cumulativeAmount:是否显示累计发生额0-否1-是
     */
    @Override
    public List<?> queryAccountDetailBalance(VoucherDTO dto, String cumulativeAmount) {
        long start = System.currentTimeMillis();

        //账套代码
        List centerCode = detailAccountService.getSubBranch();
        List branchCode = centerCode;
        String accBookType = CurrentUser.getCurrentLoginAccountType();
        String accBookCode = CurrentUser.getCurrentLoginAccount();

        //期间范围
        String startYearMonth = dto.getYearMonth();
        String endYearMonth = startYearMonth;
        //是否结转
        Boolean settleState = getSettleState(centerCode, accBookType, accBookCode, endYearMonth);

        //科目范围
        String itemStart = dto.getItemCode1();
        String itemEnd = dto.getItemCode2();
        //科目层级范围
        String itemLevelStart = dto.getLevel();
        String itemLevelEnd = dto.getLevelEnd();
        //是否包含未记账凭证:0-否;1-是
        String voucherGene = dto.getVoucherGene();

        //1.科目余额查询，将结果放在map集合中
        Map<String, Map<String, String>> itemBalanceMap = getItemBalance2(centerCode, branchCode, accBookType, accBookCode, startYearMonth,itemStart, itemEnd,settleState);
        //2.未结转时 对凭证处理
        if(!settleState){
            dealNoChargeVoucher2(voucherGene, centerCode, branchCode, accBookType, accBookCode, startYearMonth, endYearMonth, itemStart, itemEnd, itemBalanceMap);
        }
        //3.科目(层级)汇总
        Map<String, Map<String, String>> itemSumaryMap = itemSummary2(accBookCode, itemStart, itemEnd, itemBalanceMap);
        //4.数据封装，返给前端
        List resultList = getDataResult2(accBookCode, itemStart, itemEnd, itemLevelStart, itemLevelEnd, itemSumaryMap, cumulativeAmount);
        System.out.println("科目余额查询用时："+(System.currentTimeMillis()-start)+"ms");
        return resultList;
    }

    /**
     * 判断会计期间是否结转
     * @param centerCode  核算机构
     * @param accBookType 账套类型
     * @param accBookCode 账套编码
     * @param yearMonth   年月
     * @return 已结转 返回true
     */
    private Boolean getSettleState(List centerCode, String accBookType, String accBookCode, String yearMonth) {
        StringBuffer sql = new StringBuffer("select acc_month_stat as yearMonthState from accmonthtrace where 1 = 1 ");
        sql.append(" and center_code in (?1)");
        sql.append(" and acc_book_type = ?2");
        sql.append(" and acc_book_code = ?3");
        sql.append(" and year_month_date = ?4");

        Map<Integer, Object> params = new HashMap<>();
        params.put(1, centerCode);
        params.put(2, accBookType);
        params.put(3, accBookCode);
        params.put(4, yearMonth);

        List list = voucherRepository.queryBySqlSC(sql.toString(), params);
        if(list != null && !list.isEmpty()){
            for(Object temp : list){
                Map<String, String> map = (Map<String, String>) temp;
                String yearMonthState = map.get("yearMonthState");
                //1-当前,2-未结转,3-已结转,4-未决算,5-已决算
                if("1".equals(yearMonthState) || "2".equals(yearMonthState)) return false;
            }
            return true;
        }else{
            throw new RuntimeException("无效的会计期间：" + yearMonth + "。");
        }
    }

    @Override
    public String isHasData(VoucherDTO dto, String cumulativeAmount) {
        List<?> list = queryAccountDetailBalance(dto,cumulativeAmount);
        if (list != null && list.size() != 0){
            String centerCode = CurrentUser.getCurrentLoginManageBranch();
            String branchCode = CurrentUser.getCurrentLoginManageBranch();
            String accBookType = CurrentUser.getCurrentLoginAccountType();
            String accBookCode = CurrentUser.getCurrentLoginAccount();
            String key = CurrentUser.getCurrentUser().getId()+"_"+centerCode+"_"+branchCode+"_"+accBookType+"_"+accBookCode;
            if (exportDataMap==null) {
                exportDataMap = new HashMap<String, Object>();
            }
            exportDataMap.put(key, list);
            return "EXIST";
        }else {
            return "NOTEXIST";
        }
    }

    @Override
    public void exportData(HttpServletRequest request, HttpServletResponse response, VoucherDTO dto, String cumulativeAmount, String Date1, String Date2) {
        List<?> result;
        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String branchCode = CurrentUser.getCurrentLoginManageBranch();
        String accBookType = CurrentUser.getCurrentLoginAccountType();
        String accBookCode = CurrentUser.getCurrentLoginAccount();
        String key = CurrentUser.getCurrentUser().getId()+"_"+centerCode+"_"+branchCode+"_"+accBookType+"_"+accBookCode;
        if (exportDataMap!=null && exportDataMap.get(key)!=null) {
            result = (List) exportDataMap.get(key);
            exportDataMap.put(key, null); // 使用之后便清除，减少内存占用
        } else {
            result = queryAccountDetailBalance(dto,cumulativeAmount);
        }
        ExcelUtil excelUtil = new ExcelUtil();
        excelUtil.exportAccountDetail(request,response,result, Date1,Date2,cumulativeAmount,MODELPath);
    }

    /**
     * 末级科目余额查询
     */
    public Map<String, Map<String, String>> getItemBalance2(List centerCode, List branchCode, String accBookType, String accBookCode, String startYearMonth, String itemStart, String itemEnd,boolean settleState){
        Map<String, Map<String, String>> resultMap = new HashMap();
        String _yearMonth = startYearMonth;
        if(!settleState){ _yearMonth = getClosestSettledYearMonth(centerCode, accBookType, accBookCode, _yearMonth);}
        StringBuffer sql = new StringBuffer("SELECT\n" +
                "  t.center_code   AS centerCode,\n" +
                "  t.direction_idx AS itemCode,\n" +
                "  CAST(t.balance_begin_dest AS CHAR) AS balanceQc,\n" +
                "  CAST(t.debit_dest AS CHAR) AS debitBq,\n" +
                "  CAST(t.credit_dest AS CHAR) AS creditBq,\n" +
                "  CAST(t.balance_dest AS CHAR) AS balanceQm,\n" +
                "  CAST(debit_dest_year AS CHAR) AS debitBn,\n" +
                "  CAST(credit_dest_year AS CHAR) AS creditBn\n" +
                "      FROM accdetailbalancehis t\n" +
                "      WHERE 1 = 1\n" +
                "          AND center_code IN(?1)\n" +
                "          AND branch_code IN(?2)\n" +
                "          AND acc_book_type = ?3\n" +
                "          AND acc_book_code = ?4\n" +
                "          AND year_month_date = ?5\n" +
                "          AND ((direction_idx >= ?6\n" +
                "                AND direction_idx <= ?7)\n" +
                "                OR direction_idx LIKE ?7 ) \n" +
                "ORDER BY t.direction_idx");

        StringBuffer sql2 = new StringBuffer("SELECT\n" +
                "  t.center_code   AS centerCode,\n" +
                "  t.direction_idx AS itemCode,\n" +
                "  CAST(t.balance_dest AS CHAR) AS balanceQc,\n" +
                "  CAST(0.00 AS CHAR) AS debitBq,\n" +
                "  CAST(0.00 AS CHAR) AS creditBq,\n" +
                "  CAST(t.balance_dest AS CHAR) AS balanceQm,\n" +
                "  CAST(debit_dest_year AS CHAR) AS debitBn,\n" +
                "  CAST(credit_dest_year AS CHAR) AS creditBn\n" +
                "      FROM accdetailbalancehis t\n" +
                "      WHERE 1 = 1\n" +
                "          AND center_code IN(?1)\n" +
                "          AND branch_code IN(?2)\n" +
                "          AND acc_book_type = ?3\n" +
                "          AND acc_book_code = ?4\n" +
                "          AND year_month_date = ?5\n" +
                "          AND ((direction_idx >= ?6\n" +
                "                AND direction_idx <= ?7)\n" +
                "                OR direction_idx LIKE ?7 ) \n" +
                "ORDER BY t.direction_idx");


        Map<Integer, Object> params = new HashMap<>();
        params.put(1, centerCode);
        params.put(2, branchCode);
        params.put(3, accBookType);
        params.put(4, accBookCode);
        params.put(5, _yearMonth);
        params.put(6, itemStart);
        params.put(7, itemEnd);
        params.put(7, itemEnd+"%");

        List list = accDetailBalanceRepository.queryBySqlSC(settleState ? sql.toString() : sql2.toString(), params);
        if(list != null && !list.isEmpty()){
            for(int i = 0; i < list.size(); i++){
                Map<String, String> map = (Map<String, String>) list.get(i);
                resultMap.put(map.get("itemCode"), map);
            }
        }
        return resultMap;
    }

    /**
     * 获取最接近的已结转期间
     *
     * @param centerCode  核算机构
     * @param accBookType 账套类型
     * @param accBookCode 账套编码
     * @param yearMonth   年月
     * @return 获取该期间后最接近的，已结转期间或者已决算期间
     */
    private String getClosestSettledYearMonth(List centerCode, String accBookType, String accBookCode, String yearMonth) {
        int  count = 999999;
        for (Object obj : centerCode){
            StringBuffer sql = new StringBuffer("select year_month_date as yearMonth from accmonthtrace where 1 = 1 ");
            sql.append(" and center_code = ?1");
            sql.append(" and acc_book_type = ?2");
            sql.append(" and acc_book_code = ?3");
            sql.append(" and year_month_date < ?4");
            sql.append(" and acc_month_stat in ('3', '5') ");
            sql.append(" order by year_month_date desc");

            Map<Integer, Object> params = new HashMap<>();
            params.put(1, (String)obj);
            params.put(2, accBookType);
            params.put(3, accBookCode);
            params.put(4, yearMonth);

            List list = voucherRepository.queryBySqlSC(sql.toString(), params);
            if(list != null && !list.isEmpty()) {

                Map<String, String> map = (Map<String, String>) list.get(0);

                if (count > Integer.parseInt(map.get("yearMonth"))) {
                    count = Integer.parseInt(map.get("yearMonth"));
                }
            }

        }

        return  count<Integer.parseInt(yearMonth) ? count+"" : yearMonth;
    }

    /**
     * 凭证处理，结果放在itemBalanceMap中
     */
    public void dealNoChargeVoucher2(String voucherGene, List centerCode, List branchCode, String accBookType, String accBookCode, String startYearMonth, String endYearMonth, String itemStart, String itemEnd, Map<String, Map<String, String>> itemBalanceMap){
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT\n" +
                "                t1.center_code     AS centerCode,\n" +
                "                t2.direction_idx   AS itemCode,\n" +
                "                CAST(SUM(t2.debit_dest) AS CHAR)      AS debitAmount,\n" +
                "                CAST(SUM(t2.credit_dest) AS CHAR)     AS creditAmmount\n" +
                "                FROM accmainvoucher t1\n" +
                "                LEFT JOIN accsubvoucher t2\n" +
                "                ON t1.center_code = t2.center_code\n" +
                "                AND t1.branch_code = t2.branch_code\n" +
                "                AND t1.acc_book_type = t2.acc_book_type\n" +
                "                AND t1.acc_book_code = t2.acc_book_code\n" +
                "                AND t1.year_month_date = t2.year_month_date\n" +
                "                AND t1.voucher_no = t2.voucher_no\n" +
                "                WHERE 1 = 1");
        if (voucherGene!=null&&"1".equals(voucherGene)) {
            sql.append(" and t1.voucher_flag in ('1', '2','3')");
        }else{
            sql.append(" and t1.voucher_flag = '3' ");
        }

        sql.append(" AND t1.center_code IN(?1)\n" +
                "          AND t1.branch_code IN(?2)\n" +
                "          AND t1.acc_book_type = ?3\n" +
                "          AND t1.acc_book_code = ?4\n" +
                "          AND t1.year_month_date >= ?5\n" +
                "          AND t1.year_month_date <= ?6\n" +
                "          AND ( (t2.direction_idx >= ?7 \n" +
                "             AND t2.direction_idx <= ?8)\n" +
                "          OR t2.direction_idx LIKE CONCAT(?8, '%'))" +
                "GROUP BY t1.center_code,t2.direction_idx\n" +
                "ORDER BY t2.direction_idx");

        Map<Integer, Object> params = new HashMap<>();
        params.put(1, centerCode);
        params.put(2, branchCode);
        params.put(3, accBookType);
        params.put(4, accBookCode);
        params.put(5, startYearMonth);
        params.put(6, endYearMonth);
        params.put(7, itemStart);
        params.put(8, itemEnd);

        List list = accDetailBalanceRepository.queryBySqlSC(sql.toString(), params);
        if(list != null && !list.isEmpty()){
            for(int i = 0; i < list.size(); i++){
                Map<String, String> map = (Map<String, String>) list.get(i);
                String itemCode = map.get("itemCode");
                BigDecimal debitNoCharge = new BigDecimal(map.get("debitAmount"));//借方合计
                BigDecimal creditNoCharge = new BigDecimal(map.get("creditAmmount"));//贷方合计
                if(!itemBalanceMap.containsKey(itemCode)){//当前余额集合itemBalanceMap不存在该科目
                    BigDecimal balanceQc = new BigDecimal(0.00);
                    BigDecimal debitBq = debitNoCharge;
                    BigDecimal creditBq = creditNoCharge;
                    BigDecimal balanceQm = debitNoCharge.subtract(creditNoCharge);
                    BigDecimal debitBn = debitNoCharge;
                    BigDecimal creditBn = creditNoCharge;
                    Map<String, String> dataMap = new HashMap<>();
                    dataMap.put("centerCode", map.get("centerCode"));
                    dataMap.put("balanceQc", balanceQc.toString());
                    dataMap.put("debitBq", debitBq.toString());
                    dataMap.put("creditBq", creditBq.toString());
                    dataMap.put("balanceQm", balanceQm.toString());
                    dataMap.put("debitBn", debitBn.toString());
                    dataMap.put("creditBn", creditBn.toString());
                    itemBalanceMap.put(itemCode, dataMap);
                }else{//当前余额集合itemBalanceMap存在该科目
                    Map<String, String> dataMap = itemBalanceMap.get(itemCode);
                    //加入凭证数据
                    BigDecimal debitBq = new BigDecimal(dataMap.get("debitBq")).add(debitNoCharge);
                    BigDecimal creditBq = new BigDecimal(dataMap.get("creditBq")).add(creditNoCharge);
                    BigDecimal balanceQm = new BigDecimal(dataMap.get("balanceQm")).add(debitNoCharge.subtract(creditNoCharge));
                    BigDecimal debitBn = new BigDecimal(dataMap.get("debitBn")).add(debitBq);
                    BigDecimal creditBn = new BigDecimal(dataMap.get("creditBn")).add(creditBq);
                    dataMap.put("debitBq", debitBq.toString());
                    dataMap.put("creditBq", creditBq.toString());
                    dataMap.put("balanceQm", balanceQm.toString());
                    dataMap.put("debitBn", debitBn.toString());
                    dataMap.put("creditBn", creditBn.toString());
                    itemBalanceMap.put(itemCode, dataMap);
                }
            }
        }
    }

    /**
     * 科目汇总
     */
    public Map<String, Map<String, String>> itemSummary2(String accBookCode, String itemStart, String itemEnd, Map<String, Map<String, String>> itemBalanceMap){
        Map<String, Map<String, String>> resultMap = new HashMap();
        //获取科目汇总规则
        Map<String, List> relationMap = getSummaryRelation(accBookCode, itemStart, itemEnd);

        for(int j = 3; j > 0; j--){
            //从末级科目到一级汇总
            List itemList = getAllLevelSubjectList(accBookCode, itemStart, itemEnd, j);
            if(itemList != null && !itemList.isEmpty()){
                for(int k = 0; k < itemList.size(); k++){
                    Map<String, String> itemMap = (Map<String, String>) itemList.get(k);
                    String itemCode = itemMap.get("itemCode");//科目代码
                    String endFlag = itemMap.get("endFlag");
                    if("0".equals(endFlag)){//末级科目
                        if(itemBalanceMap.get(itemCode) != null){
                            resultMap.put(itemCode, itemBalanceMap.get(itemCode));
                        }else{
                            Map<String, String> sumMap = new HashMap<>();
                            sumMap.put("balanceQc", "0.00");
                            sumMap.put("debitBq", "0.00");
                            sumMap.put("creditBq", "0.00");
                            sumMap.put("balanceQm", "0.00");
                            sumMap.put("debitBn", "0.00");
                            sumMap.put("creditBn", "0.00");
                            resultMap.put(itemCode, sumMap);
                        }
                    }else{//非末级科目
                        Set<String> childSet = new HashSet<>();//子集
                        List childList = relationMap.get(itemCode);
                        if (childList!=null&&childList.size()>0) {

                            for(int c = 0; c < childList.size(); c++){
                                childSet.add(((Map<String, String>) childList.get(c)).get("itemCode"));
                            }

                            BigDecimal balanceQc = new BigDecimal(0.00);//期初
                            BigDecimal debitBq = new BigDecimal(0.00);//本期借
                            BigDecimal creditBq = new BigDecimal(0.00);//本期贷
                            BigDecimal balanceQm = new BigDecimal(0.00);//期末
                            BigDecimal debitBn = new BigDecimal(0.00);//本年累计借
                            BigDecimal creditBn = new BigDecimal(0.00);//本年累计贷
                            String centerCode = null;
                            for(String childItemCode : childSet){
                                Map<String, String> childMap = resultMap.get(childItemCode);
                                if(childMap != null){
                                    balanceQc = balanceQc.add(new BigDecimal(childMap.get("balanceQc")));
                                    debitBq = debitBq.add(new BigDecimal(childMap.get("debitBq")));
                                    creditBq = creditBq.add(new BigDecimal(childMap.get("creditBq")));
                                    balanceQm = balanceQm.add(new BigDecimal(childMap.get("balanceQm")));
                                    debitBn = debitBn.add(new BigDecimal(childMap.get("debitBn")));
                                    creditBn = creditBn.add(new BigDecimal(childMap.get("creditBn")));
                                    if(childMap.get("centerCode") != null) {centerCode = childMap.get("centerCode");}
                                }
                            }
                            Map<String, String> sumMap = new HashMap<>();
                            sumMap.put("balanceQc", balanceQc.toString());
                            sumMap.put("debitBq", debitBq.toString());
                            sumMap.put("creditBq", creditBq.toString());
                            sumMap.put("balanceQm", balanceQm.toString());
                            sumMap.put("debitBn", debitBn.toString());
                            sumMap.put("creditBn", creditBn.toString());
                            sumMap.put("centerCode", centerCode);
                            resultMap.put(itemCode, sumMap);
                        }
                    }
                }
            }
        }
        return resultMap;
    }

    /**
     * 从末级科目到一级汇总
     * @param accBookCode 账套编码
     * @param itemStart 起始科目
     * @param itemEnd 终止科目
     * @param j  循环变量
     * @return
     */
    private List getAllLevelSubjectList(String accBookCode, String itemStart, String itemEnd, int j) {
        String redisKey = RedisConstant.ACCOUNT_BALANCE_INQUIRY_ALL_LEVEL_SUBJECT_INFO_KEY_PREFIX.concat(accBookCode+"_"+itemStart+"_"+itemEnd+"_"+j) ;
        if( RedisUtil.exists(redisKey) ){return  (List) RedisUtil.get(redisKey);}
        StringBuffer sql = new StringBuffer("select concat(all_subject, subject_code, '/') as itemCode, end_flag as endFlag from subjectinfo where useflag = '1' ");
        sql.append(" and account = ?1");
        sql.append(" and ((concat(all_subject, subject_code) >= ?2");
        sql.append(" and concat(all_subject, subject_code) <= ?3 )");
        sql.append(" or concat(all_subject, subject_code) like ?4 )");
        sql.append(" and level = ?5");
        sql.append(" order by itemCode");

        Map<Integer, Object> params = new HashMap<>();
        params.put(1, accBookCode);
        params.put(2, itemStart);
        params.put(3, itemEnd);
        params.put(4, itemEnd+"%");
        params.put(5, j);
        List list = accDetailBalanceRepository.queryBySqlSC(sql.toString(), params);
        if( !RedisUtil.exists(redisKey ) ){RedisUtil.set(redisKey, list, Constant.TIME_OUT);}
        return list;
    }

    /**
     *获取科目汇总关系：<科目，子科目集合>
     */
    public Map<String, List> getSummaryRelation(String accBookCode, String itemStart, String itemEnd){
        String redisKey = RedisConstant.ACCOUNT_BALANCE_INQUIRY_SUMMARY_RELATIONSHIP_SUBJECT_INFO_KEY_PREFIX.concat(accBookCode+"_"+itemStart+"_"+itemEnd) ;
        if( RedisUtil.exists(redisKey) ){return  (Map) RedisUtil.get(redisKey);}
        StringBuffer sql = new StringBuffer("select cast(id as char) as id, concat(all_subject, subject_code, '/') as itemCode from subjectinfo where end_flag = '1' and useflag = '1' ");
        sql.append(" and account = ?1");
        sql.append(" and ((concat(all_subject, subject_code) >= ?2");
        sql.append(" and concat(all_subject, subject_code) <= ?3 )");
        sql.append(" or concat(all_subject, subject_code) like ?4 )");
        sql.append(" order by itemCode");

        Map<Integer, Object> params = new HashMap<>();
        params.put(1, accBookCode);
        params.put(2, itemStart);
        params.put(3, itemEnd);
        params.put(4, itemEnd+"%");

        List list = accDetailBalanceRepository.queryBySqlSC(sql.toString(), params);
        Map<String, List> itemMap = new HashMap();
        if(list != null && !list.isEmpty()){
            for(int i = 0; i < list.size(); i++){
                Map map = (Map)list.get(i);
                String id = (String) map.get("id");
                String itemCode = (String) map.get("itemCode");
                StringBuffer qryChild = new StringBuffer("select concat(all_subject, subject_code, '/') as itemCode from subjectinfo where useflag = '1' ");
                qryChild.append(" and account = ?1");
                qryChild.append(" and super_subject = ?2 order by itemCode");

                params = new HashMap<>();
                params.put(1, accBookCode);
                params.put(2, id);

                List childList = accDetailBalanceRepository.queryBySqlSC(qryChild.toString(), params);
                if(childList != null && !childList.isEmpty()){
                    itemMap.put(itemCode, childList);//保存科目级下级科目
                }else{
                    //throw new RuntimeException("非末级科目：" + itemCode + "未找到可用子科目。");
                }
            }
        }
        if( !RedisUtil.exists(redisKey ) ){RedisUtil.set(redisKey, itemMap, Constant.TIME_OUT);}
        return itemMap;
    }

    /**
     * 封装返回结果
     */
    public List getDataResult2(String accountBookCode, String itemStart, String itemEnd, String itemLevelStart, String itemLevelEnd, Map<String, Map<String, String>> itemSumaryMap, String cumulativeAmount){
        List resultList = new ArrayList();
        //查询符合条件的科目
        List itemList = getItemList(accountBookCode, itemStart, itemEnd, itemLevelStart, itemLevelEnd);
        if(itemList != null && !itemList.isEmpty()){
            //科目类别：1-资产，2-负债，3-权益，4-损益
            String subjectType = null;
            //科目类别合计
            BigDecimal sumBalanceQc = new BigDecimal(0.00);
            BigDecimal sumBalanceQm = new BigDecimal(0.00);
            BigDecimal sumDebitBq = new BigDecimal(0.00);
            BigDecimal sumCreditBq = new BigDecimal(0.00);
            BigDecimal sumDebitBn = new BigDecimal(0.00);
            BigDecimal sumCreditBn = new BigDecimal(0.00);
            a:for(int i = 0; i < itemList.size(); i++){
                Map<String, String> map = (Map<String, String>) itemList.get(i);
                String itemCode = map.get("itemCode");//科目代码
                int level = Integer.parseInt(map.get("level"));//科目层级
                //设置核算单位
                if(itemSumaryMap.get(itemCode) != null){
                    map.put("centerCode", itemSumaryMap.get(itemCode).get("centerCode"));
                }
                //设置科目名称
                String space = "";
                int index = level;
                while(index > 1){
                    space += "&nbsp;&nbsp;&nbsp;&nbsp;";
                    index--;
                }
                map.put("itemName", space + map.get("subjectName"));
                //1.设置期初余额&期末余额
                BigDecimal balanceQc = new BigDecimal("0.00");
                BigDecimal balanceQm = new BigDecimal("0.00");
                if(itemSumaryMap.get(itemCode) != null){
                    balanceQc = new BigDecimal(itemSumaryMap.get(itemCode).get("balanceQc"));
                }
                if(itemSumaryMap.get(itemCode) != null
                        && new BigDecimal(itemSumaryMap.get(itemCode).get("balanceQm")).compareTo(BigDecimal.ZERO) != 0){
                    balanceQm = new BigDecimal(itemSumaryMap.get(itemCode).get("balanceQm"));
                }
                if(BigDecimal.ZERO.compareTo(balanceQc) <= 0){//期初余额大于等于0
                    map.put("debitDest_Qc", balanceQc.toString());
                    map.put("creditDest_Qc", "0.00");
                }else{
                    map.put("debitDest_Qc", "0.00");
                    map.put("creditDest_Qc", balanceQc.abs().toString());
                }
                if(BigDecimal.ZERO.compareTo(balanceQm) <= 0){//期末余额大于等于0
                    map.put("debitDest_Qm", balanceQm.toString());
                    map.put("creditDest_Qm", "0.00");
                }else{
                    map.put("debitDest_Qm", "0.00");
                    map.put("creditDest_Qm", balanceQm.abs().toString());
                }
                //2.设置本期发生
                BigDecimal debitBq = new BigDecimal(0.00);
                BigDecimal creditBq = new BigDecimal(0.00);

                if (itemSumaryMap.get(itemCode) != null) {
                    debitBq = debitBq.add(new BigDecimal(itemSumaryMap.get(itemCode).get("debitBq")));
                }
                if (itemSumaryMap.get(itemCode) != null) {
                    creditBq = creditBq.add(new BigDecimal(itemSumaryMap.get(itemCode).get("creditBq")));
                }
                map.put("debitDest_Bq", debitBq.toString());
                map.put("creditDest_Bq", creditBq.toString());
                //3.设置本年累计发生
                map.put("debitDest_Bn", "0.00");
                map.put("creditDest_Bn", "0.00");
                if(itemSumaryMap.get(itemCode) != null
                        && (new BigDecimal(itemSumaryMap.get(itemCode).get("debitBn")).compareTo(BigDecimal.ZERO) > 0
                        || new BigDecimal(itemSumaryMap.get(itemCode).get("creditBn")).compareTo(BigDecimal.ZERO) > 0)){
                    map.put("debitDest_Bn", itemSumaryMap.get(itemCode).get("debitBn"));
                    map.put("creditDest_Bn", itemSumaryMap.get(itemCode).get("creditBn"));
                }

                if(i == 0){
                    subjectType = map.get("subjectType");//科目类型
                }else{
                    if(!subjectType.equals(map.get("subjectType"))){
                        //添加科目小计
                        //1.合计名称
                        Map<String, String> summaryMap = new HashMap();
                        if("1".equals(subjectType)){
                            summaryMap.put("itemCode", "资产小计");
                        }else if("2".equals(subjectType)){
                            summaryMap.put("itemCode", "负债小计");
                        }else if("3".equals(subjectType)){
                            summaryMap.put("itemCode", "权益小计");
                        }else if("4".equals(subjectType)){
                            summaryMap.put("itemCode", "损益小计");
                        }else if("0".equals(subjectType)){
                            summaryMap.put("itemCode", "非会计科目类型");
                        }
                        //2.期初合计
                        if(sumBalanceQc.compareTo(BigDecimal.ZERO) >= 0){
                            summaryMap.put("debitDest_Qc", sumBalanceQc.toString());
                            summaryMap.put("creditDest_Qc", "0.00");
                        }else{
                            summaryMap.put("debitDest_Qc", "0.00");
                            summaryMap.put("creditDest_Qc", sumBalanceQc.abs().toString());
                        }
                        //3.本期发生
                        summaryMap.put("debitDest_Bq", sumDebitBq.toString());
                        summaryMap.put("creditDest_Bq", sumCreditBq.toString());
                        //4.本年累计发生
                        summaryMap.put("debitDest_Bn", sumDebitBn.toString());
                        summaryMap.put("creditDest_Bn", sumCreditBn.toString());
                        //5.期末合计
                        if(sumBalanceQm.compareTo(BigDecimal.ZERO) >= 0){
                            summaryMap.put("debitDest_Qm", sumBalanceQm.toString());
                            summaryMap.put("creditDest_Qm", "0.00");
                        }else{
                            summaryMap.put("debitDest_Qm", "0.00");
                            summaryMap.put("creditDest_Qm", sumBalanceQm.abs().toString());
                        }
                        //添加科目小计
                        if("0".equals(cumulativeAmount)) {//不显示本年累计发生额
                            if(BigDecimal.ZERO.compareTo(sumBalanceQc) != 0 || BigDecimal.ZERO.compareTo(sumBalanceQm) != 0
                                    || BigDecimal.ZERO.compareTo(sumDebitBq) != 0 || BigDecimal.ZERO.compareTo(sumCreditBq) != 0)
                                resultList.add(summaryMap);
                        }else if("1".equals(cumulativeAmount)){//显示本年累计发生额
                            if(BigDecimal.ZERO.compareTo(sumBalanceQc) != 0 || BigDecimal.ZERO.compareTo(sumBalanceQm) != 0
                                    || BigDecimal.ZERO.compareTo(sumDebitBq) != 0 || BigDecimal.ZERO.compareTo(sumCreditBq) != 0
                                    || BigDecimal.ZERO.compareTo(sumDebitBn) != 0 || BigDecimal.ZERO.compareTo(sumCreditBn) != 0)
                                resultList.add(summaryMap);
                        }else{
                            resultList.add(summaryMap);
                        }

                        //初始化
                        subjectType = map.get("subjectType");
                        sumBalanceQc = new BigDecimal(0.00);
                        sumBalanceQm = new BigDecimal(0.00);
                        sumDebitBq = new BigDecimal(0.00);
                        sumCreditBq = new BigDecimal(0.00);
                        sumDebitBn = new BigDecimal(0.00);
                        sumCreditBn = new BigDecimal(0.00);
                    }
                }
                if(level == 1){//一级科目
                    sumBalanceQc = sumBalanceQc.add(balanceQc);
                    sumBalanceQm = sumBalanceQm.add(balanceQm);
                    sumDebitBq = sumDebitBq.add(debitBq);
                    sumCreditBq = sumCreditBq.add(creditBq);
                    sumDebitBn = sumDebitBn.add(new BigDecimal(map.get("debitDest_Bn")));
                    sumCreditBn = sumCreditBn.add(new BigDecimal(map.get("creditDest_Bn")));
                }
                //添加科目余额
                if("0".equals(cumulativeAmount)){//不显示本年累计发生额
                    if(BigDecimal.ZERO.compareTo(balanceQc) != 0 || BigDecimal.ZERO.compareTo(balanceQm) != 0
                            || BigDecimal.ZERO.compareTo(debitBq) != 0 || BigDecimal.ZERO.compareTo(creditBq) != 0)
                        resultList.add(map);
                }else if("1".equals(cumulativeAmount)){//显示本年累计发生额
                    if(BigDecimal.ZERO.compareTo(balanceQc) != 0 || BigDecimal.ZERO.compareTo(balanceQm) != 0
                            || BigDecimal.ZERO.compareTo(debitBq) != 0 || BigDecimal.ZERO.compareTo(creditBq) != 0
                            || BigDecimal.ZERO.compareTo(new BigDecimal(map.get("debitDest_Bn"))) != 0 || BigDecimal.ZERO.compareTo(new BigDecimal(map.get("creditDest_Bn"))) != 0)
                        resultList.add(map);
                }else{
                    resultList.add(map);
                }
                //最后一行
                if(i == itemList.size() - 1){
                    //添加科目小计
                    //1.合计名称
                    Map<String, String> summaryMap = new HashMap();
                    if("1".equals(subjectType)){
                        summaryMap.put("itemCode", "资产小计");
                    }else if("2".equals(subjectType)){
                        summaryMap.put("itemCode", "负债小计");
                    }else if("3".equals(subjectType)){
                        summaryMap.put("itemCode", "权益小计");
                    }else if("4".equals(subjectType)){
                        summaryMap.put("itemCode", "损益小计");
                    }else if("0".equals(subjectType)){
                        summaryMap.put("itemCode", "非会计科目类型");
                    }
                    //2.期初合计
                    if(sumBalanceQc.compareTo(BigDecimal.ZERO) >= 0){
                        summaryMap.put("debitDest_Qc", sumBalanceQc.toString());
                        summaryMap.put("creditDest_Qc", "0.00");
                    }else{
                        summaryMap.put("debitDest_Qc", "0.00");
                        summaryMap.put("creditDest_Qc", sumBalanceQc.abs().toString());
                    }
                    //3.本期发生
                    summaryMap.put("debitDest_Bq", sumDebitBq.toString());
                    summaryMap.put("creditDest_Bq", sumCreditBq.toString());
                    //4.本年累计发生
                    summaryMap.put("debitDest_Bn", sumDebitBn.toString());
                    summaryMap.put("creditDest_Bn", sumCreditBn.toString());
                    //5.期末合计
                    if(sumBalanceQm.compareTo(BigDecimal.ZERO) >= 0){
                        summaryMap.put("debitDest_Qm", sumBalanceQm.toString());
                        summaryMap.put("creditDest_Qm", "0.00");
                    }else{
                        summaryMap.put("debitDest_Qm", "0.00");
                        summaryMap.put("creditDest_Qm", sumBalanceQm.abs().toString());
                    }
                    //添加科目小计
                    if("0".equals(cumulativeAmount)) {//不显示本年累计发生额
                        if(BigDecimal.ZERO.compareTo(sumBalanceQc) != 0 || BigDecimal.ZERO.compareTo(sumBalanceQm) != 0
                                || BigDecimal.ZERO.compareTo(sumDebitBq) != 0 || BigDecimal.ZERO.compareTo(sumCreditBq) != 0)
                            resultList.add(summaryMap);
                    }else if("1".equals(cumulativeAmount)){//显示本年累计发生额
                        if(BigDecimal.ZERO.compareTo(sumBalanceQc) != 0 || BigDecimal.ZERO.compareTo(sumBalanceQm) != 0
                                || BigDecimal.ZERO.compareTo(sumDebitBq) != 0 || BigDecimal.ZERO.compareTo(sumCreditBq) != 0
                                || BigDecimal.ZERO.compareTo(sumDebitBn) != 0 || BigDecimal.ZERO.compareTo(sumCreditBn) != 0)
                            resultList.add(summaryMap);
                    }else{
                        resultList.add(summaryMap);
                    }
                }
            }
        }
        return resultList;
    }

    public List getItemList(String accBookCode, String itemStart, String itemEnd, String itemLevelStart, String itemLevelEnd){
        StringBuffer sql = new StringBuffer("select subject_code as subjectCode, subject_name as subjectName,subject_type as subjectType, concat(all_subject, subject_code, '/') as itemCode, direction, level from subjectinfo where useflag = '1' ");
        sql.append(" and account = ?1");
        sql.append(" and ((concat(all_subject, subject_code) >= ?2");
        sql.append(" and concat(all_subject, subject_code) <= ?3 )");
        sql.append(" or concat(all_subject, subject_code) like ?4 )");
        sql.append(" and level >= ?5");
        sql.append(" and level <= ?6");
        sql.append(" order by itemCode");

        Map<Integer, Object> params = new HashMap<>();
        params.put(1, accBookCode);
        params.put(2, itemStart);
        params.put(3, itemEnd);
        params.put(4, itemEnd+"%");
        params.put(5, itemLevelStart);
        params.put(6, itemLevelEnd);

        return accDetailBalanceRepository.queryBySqlSC(sql.toString(), params);
    }
}
