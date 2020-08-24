package com.sinosoft.service.impl.account;

import com.sinosoft.common.Constant;
import com.sinosoft.common.CurrentUser;
import com.sinosoft.dto.VoucherDTO;
import com.sinosoft.repository.SubjectRepository;
import com.sinosoft.repository.VoucherRepository;
import com.sinosoft.service.VoucherService;
import com.sinosoft.service.account.QueryDetailAccountService;
import com.sinosoft.util.ExcelUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QueryDetailAccountServiceImpl implements QueryDetailAccountService {
    private Logger logger = LoggerFactory.getLogger(QueryDetailAccountServiceImpl.class);
    @Resource
    private VoucherRepository voucherRepository;
    @Resource
    private SubjectRepository subjectRepository;
    @Resource
    private VoucherService voucherService;

    /**
     * 存在问题：
     * 1.参数问题（包括会计期间、基层单位、核算单位等）
     * 2.会计期间的转换问题：查询表还是字符串转化（是否月份连续）
     * 3.是否专项名称全级显示
     * @return
     */
    @Override
    public List<?> queryDetaiList(String yearMonth, String beginDate, String endDate, String directionIdx, String specialNameP){
        //当前会计期间
        //开始时间
        //截止日期
        //科目方向段
        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String branchCode = CurrentUser.getCurrentLoginManageBranch();
        String accBookType = CurrentUser.getCurrentLoginAccountType();//session获取
        String accBookCode = CurrentUser.getCurrentLoginAccount();//session获取

        StringBuffer initialBalanceSql = new StringBuffer("SELECT DISTINCT cast(IFNULL(a.balance_dest,0.00) as char) AS 'initialBalance',cast(IFNULL(a.debit_dest_year,0.00) as char) AS 'debitYearTotal',cast(IFNULL(a.credit_dest_year,0.00) as char) AS 'creditYearTotal' FROM accdetailbalance a WHERE 1=1");
        StringBuffer sql = new StringBuffer("SELECT DISTINCT am.voucher_date AS 'voucherDate',am.voucher_no AS 'voucherNo',am.year_month_date AS 'yearMonthDate',ac.suffix_no AS 'suffixNo',ac.remark AS 'remark',ac.direction_other AS directionOther,ac.d01 AS 'unitPrice',ac.d02 AS 'amount',CAST(IFNULL(ac.debit_dest,0.00) AS char) AS 'debitDest',CAST(IFNULL(ac.credit_dest,0.00) AS char) AS 'creditDest',CAST((IFNULL(ac.debit_dest,0.00)-IFNULL(ac.credit_dest,0.00)) AS char) AS 'balanceDest' FROM accsubvoucher ac LEFT JOIN accmainvoucher am ON am.center_code = ac.center_code AND am.branch_code = ac.branch_code AND am.acc_book_type = ac.acc_book_type AND am.acc_book_code = ac.acc_book_code AND am.year_month_date = ac.year_month_date AND am.voucher_no = ac.voucher_no WHERE 1=1");

        int paramsNo = 1;
        Map<Integer, Object> params = new HashMap<>();
        int paramsNo2 = 1;
        Map<Integer, Object> params2 = new HashMap<>();

        if (StringUtils.isNotEmpty(directionIdx)) {
            if ("/".equals(directionIdx.substring(directionIdx.length()-1))) {
                directionIdx = directionIdx.substring(0,directionIdx.length()-1);
            }
            String[] codes = directionIdx.split("/");
            for (int i=0;i<codes.length;i++) {
                if (i<9) {
                    initialBalanceSql.append(" AND a.f0"+(i+1)+" = ?" + paramsNo);
                    sql.append(" AND ac.f0"+(i+1)+" = ?" + paramsNo2);

                    params.put(paramsNo, codes[i]);
                    paramsNo++;
                    params2.put(paramsNo2, codes[i]);
                    paramsNo2++;
                } else {
                    initialBalanceSql.append(" AND a.f"+(i+1)+" = ?" + paramsNo);
                    sql.append(" AND ac.f"+(i+1)+" = ?" + paramsNo2);

                    params.put(paramsNo, codes[i]);
                    paramsNo++;
                    params2.put(paramsNo2, codes[i]);
                    paramsNo2++;
                }
            }
        }
        if (StringUtils.isNotEmpty(centerCode)) {
            initialBalanceSql.append(" AND a.center_code = ?" + paramsNo);
            sql.append(" AND ac.center_code = ?" + paramsNo2);

            params.put(paramsNo, centerCode);
            paramsNo++;
            params2.put(paramsNo2, centerCode);
            paramsNo2++;
        }
        if (StringUtils.isNotEmpty(branchCode)) {
            initialBalanceSql.append(" AND a.branch_code = ?" + paramsNo);
            sql.append(" AND ac.branch_code = ?" + paramsNo2);

            params.put(paramsNo, branchCode);
            paramsNo++;
            params2.put(paramsNo2, branchCode);
            paramsNo2++;
        }
        if (StringUtils.isNotEmpty(accBookType)) {
            initialBalanceSql.append(" AND a.acc_book_type = ?" + paramsNo);
            sql.append(" AND ac.acc_book_type = ?" + paramsNo2);

            params.put(paramsNo, accBookType);
            paramsNo++;
            params2.put(paramsNo2, accBookType);
            paramsNo2++;
        }
        if (StringUtils.isNotEmpty(accBookCode)) {
            initialBalanceSql.append(" AND a.acc_book_code = ?" + paramsNo);
            sql.append(" AND ac.acc_book_code = ?" + paramsNo2);

            params.put(paramsNo, accBookCode);
            paramsNo++;
            params2.put(paramsNo2, accBookCode);
            paramsNo2++;
        }
        if (StringUtils.isNotEmpty(beginDate)) {
            //判断当beginDate为决算月时，initialBalanceSql是当月凭证子表与凭证主表联查；sql是当月明细账余额表
            //beginDate在决算月之前时，
            initialBalanceSql.append(" AND a.year_month_date = ?" + paramsNo);
            params.put(paramsNo, getLastYearMonthByBeginDate(beginDate));
            paramsNo++;

            sql.append(" AND am.year_month_date >= ?" + paramsNo2);
            params2.put(paramsNo2, beginDate);
            paramsNo2++;
            sql.append(" AND am.voucher_date >= ?" + paramsNo2);
            params2.put(paramsNo2, getFirstDayDateByBeginDate(beginDate));
            paramsNo2++;
        }
        if (StringUtils.isNotEmpty(endDate)) {
            sql.append(" AND am.voucher_date <= ?" + paramsNo2);
            params2.put(paramsNo2, endDate);
            paramsNo2++;
        }

        //连表查询（历史表）
        String sql1 = initialBalanceSql.toString();
        String sql2 = sql.toString();
        sql1 = sql1.replaceAll("accdetailbalance","accdetailbalancehis");
        sql1 = sql1.replaceAll("a\\.","ah\\.");
        sql1 = sql1.replaceAll(" a "," ah ");
        sql2 =sql2.replaceAll("accsubvoucher","accsubvoucherhis");
        sql2 =sql2.replaceAll("ac\\.","ach\\.");
        sql2 =sql2.replaceAll(" ac "," ach ");
        sql2 =sql2.replaceAll("accmainvoucher","accmainvoucherhis");
        sql2 =sql2.replaceAll("am\\.","amh\\.");
        sql2 =sql2.replaceAll(" am "," amh ");

        initialBalanceSql.append(" union all ");
        sql.append(" union all ");
        initialBalanceSql.append(sql1);
        sql.append(sql2);
        sql.append(" ORDER BY voucherDate,voucherNo");

        List<?> listByInitialBalanceSql = voucherRepository.queryBySqlSC(initialBalanceSql.toString(), params);
        List<?> listBySql = voucherRepository.queryBySqlSC(sql.toString(), params2);

        //期初余额
        BigDecimal initialBalance = new BigDecimal("0.00");
        //余额
        BigDecimal balance = new BigDecimal("0.00");
        //本月合计
        BigDecimal debitMonthTotal = new BigDecimal("0.00");
        BigDecimal creditMonthTotal = new BigDecimal("0.00");
        //本年累计
        BigDecimal debitYearTotal = new BigDecimal("0.00");
        BigDecimal creditYearTotal = new BigDecimal("0.00");
        //方向
        String balanceFX = getDirectionNameBySubjectCodeAndAccount(directionIdx, accBookCode);

        if (listByInitialBalanceSql!=null&&listByInitialBalanceSql.size()>0) {
            Map<String,Object> map = (Map<String,Object>) listByInitialBalanceSql.get(0);
            initialBalance = new BigDecimal((String) map.get("initialBalance"));
            balance = initialBalance;
            if (!"01".equals(beginDate.substring(4))) {
                //开始时间对应的会计期间如果是一月，则无需初始化本年累计数据
                debitYearTotal = new BigDecimal((String) map.get("debitYearTotal"));
                creditYearTotal = new BigDecimal((String) map.get("creditYearTotal"));
            }
        }

        List<Map<String,Object>> list = new ArrayList<>();
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("voucherDate","");
        map.put("voucherNo","");
        map.put("remark","期初余额");
        map.put("unitPrice","");
        map.put("amount","");
        map.put("debitDest","");
        map.put("creditDest","");
        setMapBalanceAndFX(map, initialBalance, balanceFX);
        map.put("flag","");
        list.add(map);
        if (listBySql!=null&&listBySql.size()>0) {

            Map<String, String> specialNameMap = new HashMap<>();

            boolean flag = true;
            if (listBySql.size()==1){
                String voucherDate = (String) ((Map<String,Object>) listBySql.get(0)).get("voucherDate");
                if (!(voucherDate!=null&&!"".equals(voucherDate)&&!"null".equals(voucherDate))) {
                    flag = false;
                }
            }
            if (flag) {
                for (int i =0;i<listBySql.size();i++) {
                    String yearMonthDate = "";//会计期间
                    Map<String,Object> listBySqlMap = (Map<String,Object>) listBySql.get(i);
                    if ("".equals(yearMonthDate)) {
                        yearMonthDate = (String) listBySqlMap.get("yearMonthDate");
                        map = new HashMap<String,Object>();
                        balance = balance.add(new BigDecimal((String) listBySqlMap.get("balanceDest")));
                        setMapData(map, listBySqlMap, balance, balanceFX, specialNameMap, specialNameP);
                        list.add(map);
                        //本月合计
                        BigDecimal debitDest = new BigDecimal((String) listBySqlMap.get("debitDest"));
                        BigDecimal creditDest = new BigDecimal((String) listBySqlMap.get("creditDest"));
                        debitMonthTotal = debitMonthTotal.add(debitDest);
                        creditMonthTotal = creditMonthTotal.add(creditDest);
                        //本年累计
                        debitYearTotal = debitYearTotal.add(debitDest);
                        creditYearTotal = creditYearTotal.add(creditDest);
                    } else {
                        if (!yearMonthDate.equals((String)listBySqlMap.get("yearMonthDate"))) {
                            //本月合计
                            map = new HashMap<String,Object>();
                            setMapData(map, "M", debitMonthTotal, creditMonthTotal, balance, balanceFX);
                            list.add(map);
                            //本年累计
                            map = new HashMap<String,Object>();
                            setMapData(map, "Y", debitYearTotal, creditYearTotal, balance, balanceFX);
                            list.add(map);
                            if (!(yearMonthDate.substring(0,4)).equals(((String)listBySqlMap.get("yearMonthDate")).substring(0,4))) {
                                //如果会计期间的年份不同，则本年累计归零
                                debitYearTotal = new BigDecimal("0.00");
                                creditYearTotal = new BigDecimal("0.00");
                            }
                            yearMonthDate = (String) listBySqlMap.get("yearMonthDate");
                            //本月合计归零
                            debitMonthTotal = new BigDecimal("0.00");
                            creditMonthTotal = new BigDecimal("0.00");

                            map = new HashMap<String,Object>();
                            balance = balance.add(new BigDecimal((String) listBySqlMap.get("balanceDest")));
                            setMapData(map, listBySqlMap, balance, balanceFX, specialNameMap, specialNameP);
                            list.add(map);
                            BigDecimal debitDest = new BigDecimal((String) listBySqlMap.get("debitDest"));
                            BigDecimal creditDest = new BigDecimal((String) listBySqlMap.get("creditDest"));
                            //本月合计
                            debitMonthTotal = debitMonthTotal.add(debitDest);
                            creditMonthTotal = creditMonthTotal.add(creditDest);
                            //本年累计
                            debitYearTotal = debitYearTotal.add(debitDest);
                            creditYearTotal = creditYearTotal.add(creditDest);
                        } else {
                            map = new HashMap<String,Object>();
                            balance = balance.add(new BigDecimal((String) listBySqlMap.get("balanceDest")));
                            setMapData(map, listBySqlMap, balance, balanceFX, specialNameMap, specialNameP);
                            list.add(map);
                            BigDecimal debitDest = new BigDecimal((String) listBySqlMap.get("debitDest"));
                            BigDecimal creditDest = new BigDecimal((String) listBySqlMap.get("creditDest"));
                            //本月合计
                            debitMonthTotal = debitMonthTotal.add(debitDest);
                            creditMonthTotal = creditMonthTotal.add(creditDest);
                            //本年累计
                            debitYearTotal = debitYearTotal.add(debitDest);
                            creditYearTotal = creditYearTotal.add(creditDest);
                        }
                    }

                    if (i==listBySql.size()-1) {
                        //本月合计
                        map = new HashMap<String,Object>();
                        setMapData(map, "M", debitMonthTotal, creditMonthTotal, balance, balanceFX);
                        list.add(map);
                        map = new HashMap<String,Object>();
                        setMapData(map, "Y", debitYearTotal, creditYearTotal, balance, balanceFX);
                        list.add(map);
                    }
                }
            }
        }

        return list;
    }

    /**
     * 存在问题：
     * 1.参数问题（包括会计期间、基层单位、核算单位等）
     * 2.会计期间的转换问题：查询表还是字符串转化（是否月份连续）
     * 3.是否专项名称全级显示
     * @return
     */
    @Override
    public List<?> queryAssistList(String yearMonth, String beginDate, String endDate, String directionIdx, String directionOther, String specialSuperCodeS, String specialNameP){
        //当前会计期间
        //开始时间
        //截止日期
        //科目方向段
        //专项代码
        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String branchCode = CurrentUser.getCurrentLoginManageBranch();
        String accBookType = CurrentUser.getCurrentLoginAccountType();//session获取
        String accBookCode = CurrentUser.getCurrentLoginAccount();//session获取

        StringBuffer initialBalanceSql = new StringBuffer("SELECT * FROM accarticlebalance a WHERE 1=1");
        StringBuffer sql = new StringBuffer("SELECT DISTINCT am.voucher_date AS 'voucherDate',am.voucher_no AS 'voucherNo',am.year_month_date AS 'yearMonthDate',ac.suffix_no AS 'suffixNo',ac.remark AS 'remark',ac.direction_other AS directionOther,ac.d01 AS 'unitPrice',ac.d02 AS 'amount',CAST(IFNULL(ac.debit_dest,0.00) AS char) AS 'debitDest',CAST(IFNULL(ac.credit_dest,0.00) AS char) AS 'creditDest',CAST((IFNULL(ac.debit_dest,0.00)-IFNULL(ac.credit_dest,0.00)) AS char) AS 'balanceDest' FROM accsubvoucher ac LEFT JOIN accmainvoucher am ON am.center_code = ac.center_code AND am.branch_code = ac.branch_code AND am.acc_book_type = ac.acc_book_type AND am.acc_book_code = ac.acc_book_code AND am.year_month_date = ac.year_month_date AND am.voucher_no = ac.voucher_no WHERE 1=1");

        int paramsNo = 1;
        Map<Integer, Object> params = new HashMap<>();
        int paramsNo2 = 1;
        Map<Integer, Object> params2 = new HashMap<>();

        if (StringUtils.isNotEmpty(directionIdx)) {
            if ("/".equals(directionIdx.substring(directionIdx.length()-1))) {
                directionIdx = directionIdx.substring(0,directionIdx.length()-1);
            }
            String[] codes = directionIdx.split("/");
            for (int i=0;i<codes.length;i++) {
                if (i<9) {
                    initialBalanceSql.append(" AND a.f0"+(i+1)+" = ?" + paramsNo);
                    sql.append(" AND ac.f0"+(i+1)+" = ?" + paramsNo2);

                    params.put(paramsNo, codes[i]);
                    paramsNo++;
                    params2.put(paramsNo2, codes[i]);
                    paramsNo2++;
                } else {
                    initialBalanceSql.append(" AND a.f"+(i+1)+" = ?" + paramsNo);
                    sql.append(" AND ac.f"+(i+1)+" = ?" + paramsNo2);

                    params.put(paramsNo, codes[i]);
                    paramsNo++;
                    params2.put(paramsNo2, codes[i]);
                    paramsNo2++;
                }
            }
        }
        if (StringUtils.isNotEmpty(directionOther)) {
            /*initialBalanceSql.append(" AND a.direction_other = '" + directionOther + "'");
            sql.append(" AND ac.direction_other = '" + directionOther + "'");*/

            String[] str = specialSuperCodeS.split(",");
            String[] directionOtherS = directionOther.split(",");
            for (int i=0;i<str.length;i++) {
                String data = voucherRepository.findSpecialSegmentSXX(str[i]);//专项存储位置
                if (data!=null && !"".equals(data)) {
                    boolean directionOtherSFlag = true;
                    if(data.equals("s01")){
                        initialBalanceSql.append(" AND a.s01 = ?" + paramsNo);
                        sql.append(" AND ac.s01 = ?" + paramsNo2);
                    }else if(data.equals("s02")){
                        initialBalanceSql.append(" AND a.s02 = ?" + paramsNo);
                        sql.append(" AND ac.s02 = ?" + paramsNo2);
                    }else if(data.equals("s03")){
                        initialBalanceSql.append(" AND a.s03 = ?" + paramsNo);
                        sql.append(" AND ac.s03 = ?" + paramsNo2);
                    }else if(data.equals("s04")){
                        initialBalanceSql.append(" AND a.s04 = ?" + paramsNo);
                        sql.append(" AND ac.s04 = ?" + paramsNo2);
                    }else if(data.equals("s05")){
                        initialBalanceSql.append(" AND a.s05 = ?" + paramsNo);
                        sql.append(" AND ac.s05 = ?" + paramsNo2);
                    }else if(data.equals("s06")){
                        initialBalanceSql.append(" AND a.s06 = ?" + paramsNo);
                        sql.append(" AND ac.s06 = ?" + paramsNo2);
                    }else if(data.equals("s07")){
                        initialBalanceSql.append(" AND a.s07 = ?" + paramsNo);
                        sql.append(" AND ac.s07 = ?" + paramsNo2);
                    }else if(data.equals("s08")){
                        initialBalanceSql.append(" AND a.s08 = ?" + paramsNo);
                        sql.append(" AND ac.s08 = ?" + paramsNo2);
                    }else if(data.equals("s09")){
                        initialBalanceSql.append(" AND a.s09 = ?" + paramsNo);
                        sql.append(" AND ac.s09 = ?" + paramsNo2);
                    }else if(data.equals("s10")){
                        initialBalanceSql.append(" AND a.s10 = ?" + paramsNo);
                        sql.append(" AND ac.s10 = ?" + paramsNo2);
                    }else if(data.equals("s11")){
                        initialBalanceSql.append(" AND a.s11 = ?" + paramsNo);
                        sql.append(" AND ac.s11 = ?" + paramsNo2);
                    }else if(data.equals("s12")){
                        initialBalanceSql.append(" AND a.s12 = ?" + paramsNo);
                        sql.append(" AND ac.s12 = ?" + paramsNo2);
                    }else if(data.equals("s13")){
                        initialBalanceSql.append(" AND a.s13 = ?" + paramsNo);
                        sql.append(" AND ac.s13 = ?" + paramsNo2);
                    }else if(data.equals("s14")){
                        initialBalanceSql.append(" AND a.s14 = ?" + paramsNo);
                        sql.append(" AND ac.s14 = ?" + paramsNo2);
                    }else if(data.equals("s15")){
                        initialBalanceSql.append(" AND a.s15 = ?" + paramsNo);
                        sql.append(" AND ac.s15 = ?" + paramsNo2);
                    }else if(data.equals("s16")){
                        initialBalanceSql.append(" AND a.s16 = ?" + paramsNo);
                        sql.append(" AND ac.s16 = ?" + paramsNo2);
                    }else if(data.equals("s17")){
                        initialBalanceSql.append(" AND a.s17 = ?" + paramsNo);
                        sql.append(" AND ac.s17 = ?" + paramsNo2);
                    }else if(data.equals("s18")){
                        initialBalanceSql.append(" AND a.s18 = ?" + paramsNo);
                        sql.append(" AND ac.s18 = ?" + paramsNo2);
                    }else if(data.equals("s19")){
                        initialBalanceSql.append(" AND a.s19 = ?" + paramsNo);
                        sql.append(" AND ac.s19 = ?" + paramsNo2);
                    }else if(data.equals("s20")){
                        initialBalanceSql.append(" AND a.s20 = ?" + paramsNo);
                        sql.append(" AND ac.s20 = ?" + paramsNo2);
                    }else{
                        directionOtherSFlag = false;
                        logger.debug(str[i]+"未配置在段定义表中！");
                    }

                    if (directionOtherSFlag) {
                        params.put(paramsNo, directionOtherS[i]);
                        paramsNo++;
                        params2.put(paramsNo2, directionOtherS[i]);
                        paramsNo2++;
                    }
                }
            }
        }
        if (StringUtils.isNotEmpty(centerCode)) {
            initialBalanceSql.append(" AND a.center_code = ?" + paramsNo);
            sql.append(" AND ac.center_code = ?" + paramsNo2);

            params.put(paramsNo, centerCode);
            paramsNo++;
            params2.put(paramsNo2, centerCode);
            paramsNo2++;
        }
        if (StringUtils.isNotEmpty(branchCode)) {
            initialBalanceSql.append(" AND a.branch_code = ?" + paramsNo);
            sql.append(" AND ac.branch_code = ?" + paramsNo2);

            params.put(paramsNo, branchCode);
            paramsNo++;
            params2.put(paramsNo2, branchCode);
            paramsNo2++;
        }
        if (StringUtils.isNotEmpty(accBookType)) {
            initialBalanceSql.append(" AND a.acc_book_type = ?" + paramsNo);
            sql.append(" and ac.acc_book_type = ?" + paramsNo2);

            params.put(paramsNo, accBookType);
            paramsNo++;
            params2.put(paramsNo2, accBookType);
            paramsNo2++;
        }
        if (StringUtils.isNotEmpty(accBookCode)) {
            initialBalanceSql.append(" AND a.acc_book_code = ?" + paramsNo);
            sql.append(" AND ac.acc_book_code = ?" + paramsNo2);

            params.put(paramsNo, accBookCode);
            paramsNo++;
            params2.put(paramsNo2, accBookCode);
            paramsNo2++;
        }
        if (StringUtils.isNotEmpty(beginDate)) {
            initialBalanceSql.append(" AND a.year_month_date = ?" + paramsNo);
            params.put(paramsNo, getLastYearMonthByBeginDate(beginDate));
            paramsNo++;

            sql.append(" AND am.year_month_date >= ?" + paramsNo2);
            params2.put(paramsNo2, beginDate);
            paramsNo2++;
            sql.append(" AND am.voucher_date >= ?" + paramsNo2);
            params2.put(paramsNo2, getFirstDayDateByBeginDate(beginDate));
            paramsNo2++;
        }
        if (StringUtils.isNotEmpty(endDate)) {
            sql.append(" AND am.voucher_date <= ?" + paramsNo2);
            params2.put(paramsNo2, endDate);
            paramsNo2++;
        }

        //连表查询（历史表）
        String sql1 = initialBalanceSql.toString();
        String sql2 = sql.toString();
        sql1 = sql1.replaceAll("accarticlebalance","accarticlebalancehis");
        sql1 = sql1.replaceAll("a\\.","ah\\.");
        sql1 = sql1.replaceAll(" a "," ah ");
        sql2 =sql2.replaceAll("accsubvoucher","accsubvoucherhis");
        sql2 =sql2.replaceAll("ac\\.","ach\\.");
        sql2 =sql2.replaceAll(" ac "," ach ");
        sql2 =sql2.replaceAll("accmainvoucher","accmainvoucherhis");
        sql2 =sql2.replaceAll("am\\.","amh\\.");
        sql2 =sql2.replaceAll(" am "," amh ");

        initialBalanceSql.append(" union all ");
        sql.append(" union all ");
        initialBalanceSql.append(sql1);
        sql.append(sql2);
        sql.append(" ORDER BY voucherDate,voucherNo");

        String tempSql = "select cast(IFNULL(a.balance_dest,0.00) as char) AS 'initialBalance',cast(IFNULL(a.debit_dest_year,0.00) as char) AS 'debitYearTotal',cast(IFNULL(a.credit_dest_year,0.00) as char) AS 'creditYearTotal'";
        List<?> listByInitialBalanceSql = voucherRepository.queryBySqlSC(tempSql + "from (" + initialBalanceSql.toString() + ") a", params);
        List<?> listBySql = voucherRepository.queryBySqlSC(sql.toString(), params2);

        //期初余额
        BigDecimal initialBalance = new BigDecimal("0.00");
        //余额
        BigDecimal balance = new BigDecimal("0.00");
        //本月合计
        BigDecimal debitMonthTotal = new BigDecimal("0.00");
        BigDecimal creditMonthTotal = new BigDecimal("0.00");
        //本年累计
        BigDecimal debitYearTotal = new BigDecimal("0.00");
        BigDecimal creditYearTotal = new BigDecimal("0.00");
        //方向
        String balanceFX = getDirectionNameBySubjectCodeAndAccount(directionIdx, accBookCode);


        if (listByInitialBalanceSql!=null&&listByInitialBalanceSql.size()>0) {
            Map<String,Object> map = (Map<String,Object>) listByInitialBalanceSql.get(0);
            initialBalance = new BigDecimal((String) map.get("initialBalance"));
            balance = initialBalance;
            if (!"01".equals(beginDate.substring(4))) {
                //开始时间对应的会计期间如果是一月，则无需初始化本年累计数据
                debitYearTotal = new BigDecimal((String) map.get("debitYearTotal"));
                creditYearTotal = new BigDecimal((String) map.get("creditYearTotal"));
            }
        }

        List<Map<String,Object>> list = new ArrayList<>();
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("voucherDate","");
        map.put("voucherNo","");
        map.put("remark","期初余额");
        map.put("unitPrice","");
        map.put("amount","");
        map.put("debitDest","");
        map.put("creditDest","");
        setMapBalanceAndFX(map, initialBalance, balanceFX);
        map.put("flag","");
        list.add(map);
        if (listBySql!=null&&listBySql.size()>0) {

            Map<String, String> specialNameMap = new HashMap<>();

            boolean flag = true;
            if (listBySql.size()==1){
                String voucherDate = (String) ((Map<String,Object>) listBySql.get(0)).get("voucherDate");
                if (!(voucherDate!=null&&!"".equals(voucherDate)&&!"null".equals(voucherDate))) {
                    flag = false;
                }
            }
            if (flag) {
                for (int i =0;i<listBySql.size();i++) {
                    String yearMonthDate = "";//会计期间
                    Map<String,Object> listBySqlMap = (Map<String,Object>) listBySql.get(i);
                    if ("".equals(yearMonthDate)) {
                        yearMonthDate = (String) listBySqlMap.get("yearMonthDate");
                        map = new HashMap<String,Object>();
                        balance = balance.add(new BigDecimal((String) listBySqlMap.get("balanceDest")));
                        setMapData(map, listBySqlMap, balance, balanceFX, specialNameMap, specialNameP);
                        list.add(map);
                        BigDecimal debitDest = new BigDecimal((String) listBySqlMap.get("debitDest"));
                        BigDecimal creditDest = new BigDecimal((String) listBySqlMap.get("creditDest"));
                        //本月合计
                        debitMonthTotal = debitMonthTotal.add(debitDest);
                        creditMonthTotal = creditMonthTotal.add(creditDest);
                        //本年累计
                        debitYearTotal = debitYearTotal.add(debitDest);
                        creditYearTotal = creditYearTotal.add(creditDest);
                    } else {
                        if (!yearMonthDate.equals((String)listBySqlMap.get("yearMonthDate"))) {
                            //本月合计
                            map = new HashMap<String,Object>();
                            setMapData(map, "M", debitMonthTotal, creditMonthTotal, balance, balanceFX);
                            list.add(map);
                            //本年累计
                            map = new HashMap<String,Object>();
                            setMapData(map, "Y", debitYearTotal, creditYearTotal, balance, balanceFX);
                            list.add(map);
                            if (!(yearMonthDate.substring(0,4)).equals(((String)listBySqlMap.get("yearMonthDate")).substring(0,4))) {
                                //如果会计期间的年份不同，则本年累计归零
                                debitYearTotal = new BigDecimal("0.00");
                                creditYearTotal = new BigDecimal("0.00");
                            }
                            yearMonthDate = (String) listBySqlMap.get("yearMonthDate");
                            //本月合计归零
                            debitMonthTotal = new BigDecimal("0.00");
                            creditMonthTotal = new BigDecimal("0.00");

                            map = new HashMap<String,Object>();
                            balance = balance.add(new BigDecimal((String) listBySqlMap.get("balanceDest")));
                            setMapData(map, listBySqlMap, balance, balanceFX, specialNameMap, specialNameP);
                            list.add(map);
                            BigDecimal debitDest = new BigDecimal((String) listBySqlMap.get("debitDest"));
                            BigDecimal creditDest = new BigDecimal((String) listBySqlMap.get("creditDest"));
                            //本月合计
                            debitMonthTotal = debitMonthTotal.add(debitDest);
                            creditMonthTotal = creditMonthTotal.add(creditDest);
                            //本年累计
                            debitYearTotal = debitYearTotal.add(debitDest);
                            creditYearTotal = creditYearTotal.add(creditDest);
                        } else {
                            map = new HashMap<String,Object>();
                            balance = balance.add(new BigDecimal((String) listBySqlMap.get("balanceDest")));
                            setMapData(map, listBySqlMap, balance, balanceFX, specialNameMap, specialNameP);
                            list.add(map);
                            BigDecimal debitDest = new BigDecimal((String) listBySqlMap.get("debitDest"));
                            BigDecimal creditDest = new BigDecimal((String) listBySqlMap.get("creditDest"));
                            //本月合计
                            debitMonthTotal = debitMonthTotal.add(debitDest);
                            creditMonthTotal = creditMonthTotal.add(creditDest);
                            //本年累计
                            debitYearTotal = debitYearTotal.add(debitDest);
                            creditYearTotal = creditYearTotal.add(creditDest);
                        }
                    }

                    if (i==listBySql.size()-1) {
                        //本月合计
                        map = new HashMap<String,Object>();
                        setMapData(map, "M", debitMonthTotal, creditMonthTotal, balance, balanceFX);
                        list.add(map);
                        map = new HashMap<String,Object>();
                        setMapData(map, "Y", debitYearTotal, creditYearTotal, balance, balanceFX);
                        list.add(map);
                    }
                }
            }
        }
        return list;
    }

    /**
     *
     * @param map
     * @param arg1 M:表示本月合计，Y:表示本年累计
     * @param d1 借方本月合计/本年累计金额
     * @param d2 贷方本月合计/本年累计金额
     * @param d3 余额
     * @param arg2 方向
     * @return
     */
    private Map<String,Object> setMapData(Map<String,Object> map, String arg1, BigDecimal d1, BigDecimal d2, BigDecimal d3, String arg2){
        map.put("voucherDate","");
        map.put("voucherNo","");
        if ("M".equals(arg1)) {
            map.put("remark","本月合计");
        } else if ("Y".equals(arg1)){
            map.put("remark","本年累计");
        } else {
            map.put("remark","");
        }
        map.put("unitPrice","");
        map.put("amount","");
        if (d1.compareTo(new BigDecimal("0.00"))!=0) {
            map.put("debitDest",d1);
        } else {
            map.put("debitDest","");
        }
        if (d2.compareTo(new BigDecimal("0.00"))!=0) {
            map.put("creditDest",d2);
        } else {
            map.put("creditDest","");
        }
        setMapBalanceAndFX(map, d3, arg2);
        map.put("flag","");
        return map;
    }

    /**
     *
     * @param map
     * @param map1
     * @param d 余额
     * @param arg 方向
     * @return
     */
    private Map<String, Object> setMapData(Map<String, Object> map, Map<String,Object> map1, BigDecimal d ,String arg, Map<String,String> specialNameMap, String specialNameP){
        map.put("voucherDate",map1.get("voucherDate"));
        map.put("yearMonthDate",map1.get("yearMonthDate"));
        map.put("voucherNo",map1.get("voucherNo"));
        map.put("suffixNo",map1.get("suffixNo"));
        map.put("remark",map1.get("remark"));
        String directionOther = (String) map1.get("directionOther");
        if (directionOther!=null&&!"".equals(directionOther)) {
            String[] specialCodes = directionOther.split(",");
            for (String s:specialCodes) {
                if (specialNameMap.containsKey(s)) {
                    map.put("remark",map.get("remark")+"_"+specialNameMap.get(s));
                } else {
                    VoucherDTO voucherDTO = voucherService.getSpecialDateBySpecialCode(s);
                    if (voucherDTO!=null&&voucherDTO.getSpecialCode()!=null&&!"".equals(voucherDTO.getSpecialCode())) {
                        if (specialNameP!=null&&"1".equals(specialNameP)) {//全称
                            map.put("remark",map.get("remark")+"_"+voucherDTO.getSpecialNameP());
                            specialNameMap.put(s, voucherDTO.getSpecialNameP());
                        } else {
                            map.put("remark",map.get("remark")+"_"+voucherDTO.getSpecialName());
                            specialNameMap.put(s, voucherDTO.getSpecialName());
                        }
                    }
                }
            }
        }
        map.put("unitPrice",map1.get("unitPrice"));
        map.put("amount",map1.get("amount"));
        BigDecimal debitDest = new BigDecimal((String) map1.get("debitDest"));
        if (debitDest.compareTo(new BigDecimal("0.00"))!=0) {
            map.put("debitDest",debitDest);
        } else {
            map.put("debitDest","");
        }
        BigDecimal creditDest = new BigDecimal((String) map1.get("creditDest"));
        if (creditDest.compareTo(new BigDecimal("0.00"))!=0) {
            map.put("creditDest",creditDest);
        } else {
            map.put("creditDest","");
        }
        setMapBalanceAndFX(map, d, arg);
        map.put("flag","");
        return map;
    }

    /**
     *
     * @param map
     * @param bigDecimal 余额
     * @param arg 方向：借，贷
     * @return
     */
    private Map<String, Object> setMapBalanceAndFX(Map<String, Object> map, BigDecimal bigDecimal ,String arg) {
        if (bigDecimal.compareTo(new BigDecimal("0.00"))!=0) {
            if ("借".equals(arg)&&(bigDecimal.compareTo(new BigDecimal("0.00"))==-1)) {
                //余额方向为借，但余额为负
                map.put("balanceFX","贷");
            } else if ("贷".equals(arg)&&(bigDecimal.compareTo(new BigDecimal("0.00"))==1)) {
                //余额方向为贷，但余额为正
                map.put("balanceFX","借");
            } else if (!"".equals(arg)){
                //余额方向为借，余额为正；或者余额方向为贷，余额为负
                map.put("balanceFX",arg);
            } else {
                map.put("balanceFX","");
            }
            //余额始终显示为正数
            map.put("balanceDest",bigDecimal.abs());
        } else {
            map.put("balanceDest","0.00");
            map.put("balanceFX","平");
        }
        return map;
    }

    /**
     * 根据科目代码和账套编码获取科目余额方向
     * @param subjectCode
     * @param accountCode
     * @return 借：借方余额，贷：贷方余额
     */
    private String getDirectionNameBySubjectCodeAndAccount(String subjectCode, String accountCode){
        List<?> list = subjectRepository.findDirectionBySubjectCodeAndAccount(subjectCode, accountCode);
        String str = "";
        if (list!=null&&list.size()>0) {
            Map<String,String> map = (Map<String,String>)(list.get(0));
            str = map.get("direction");
        }
        return (str!=null&&!"".equals(str)?str.substring(0,1):"");
    }

    /**
     * 根据开始时间的会计期间获取上一个会计期间（201902 —> 201901）
     * @param beginDate
     * @return
     */
    private String getLastYearMonthByBeginDate(String beginDate){
        String strYAndstrM = "";
        if (StringUtils.isNotEmpty(beginDate)) {
            //根据开始时间，转化查询上个会计期间的余额当做期初余额
            int strY = Integer.valueOf(beginDate.substring(0,4));
            if(beginDate.substring(4).equals("JS")){
                //String strm = beginDate.substring(4);
                strYAndstrM = String.valueOf(strY) + "12";
            }else{
                int strM = Integer.valueOf(beginDate.substring(4));
                if (strM!=1) {
                    strM -= 1;
                    if (strM>9) {
                        strYAndstrM = String.valueOf(strY) + String.valueOf(strM);//201910
                    } else {
                        strYAndstrM = String.valueOf(strY) + "0" + String.valueOf(strM);//201904
                    }
                } else{
                    /*strYAndstrM = String.valueOf(strY-1) + "JS";//2018JS*/
                    strYAndstrM = String.valueOf(strY-1) + "14";//201812
                }
            }

        }
        return strYAndstrM;
    }
    private String getFirstDayDateByBeginDate(String beginDate){
        if (StringUtils.isNotEmpty(beginDate) && !beginDate.endsWith("JS") && !beginDate.endsWith("13") && !beginDate.endsWith("14")) {
            //根据开始时间，转化为当前年月的第一天日期
            return beginDate.substring(0,4) + "-" + beginDate.substring(4) + "-01";
        }else {//当前为决算期间（2017JS ）
            return beginDate.substring(0,4) + "-" + "12" +"-01";
        }
    }

    /**
     * 查询联查明细账与联查辅助明细账的开始时间（将会计期间转为开始时间，例201802->2018.02,2018）
     * @return
     */
    @Override
    public List<?> getBeginDateList(){
        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String branchCode = CurrentUser.getCurrentLoginManageBranch();
        List result = new ArrayList();
        String accBookType = CurrentUser.getCurrentLoginAccountType();//session获取
        String accBookCode = CurrentUser.getCurrentLoginAccount();//session获取
        List<?> list = voucherRepository.getBeginDateList(centerCode, accBookType , accBookCode);
        if(list.size() > 0 && list != null){
            for(Object obj : list){
                Map map = new HashMap();
                map.putAll((Map)obj);

                StringBuilder sb = new StringBuilder((String)map.get("text"));//StringBuilder
                String s = (String)map.get("text");//String
                if(!s.endsWith("JS")){
                    sb.insert(4,".");//StringBuilder
                    map.put("text",sb);
                }
                result.add(map);
            }
        }
        return result;
    }

    @Override
    public void exportData(HttpServletRequest request, HttpServletResponse response, String yearMonth, String beginDate, String endDate, String directionIdx, String specialNameP,String detaiItemName,String dateText) {
        List<?> result = queryDetaiList( yearMonth,  beginDate,  endDate,  directionIdx,  specialNameP);
        ExcelUtil excelUtil = new ExcelUtil();
        excelUtil.exportDatadetailaccount(request,response,result,beginDate,endDate,detaiItemName,directionIdx, dateText, Constant.MODELPATH);
    }
    @Override
    public void exportDatafz(HttpServletRequest request, HttpServletResponse response,String yearMonth, String beginDate, String endDate, String directionIdx,
                             String specialNameP,String specialSuperCodeS,String directionOther,String itemName,String dateText,String otherName,String directionOtherName,String directionOthers){
        List<?> result = queryAssistList( yearMonth,  beginDate,  endDate,  directionIdx,  directionOther,  specialSuperCodeS,  specialNameP);
        ExcelUtil excelUtil = new ExcelUtil();
        excelUtil.exportDatadetailaccountfz(request,response,result, itemName, dateText, otherName, directionOtherName, directionOthers,directionIdx, Constant.MODELPATH);

    };

}
