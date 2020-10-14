package com.sinosoft.service.impl.account;

import com.sinosoft.common.CurrentTime;
import com.sinosoft.common.CurrentUser;
import com.sinosoft.common.CusSpecification;
import com.sinosoft.domain.account.*;
import com.sinosoft.domain.fixedassets.AccGCheckInfo;
import com.sinosoft.domain.intangibleassets.AccWCheckInfo;
import com.sinosoft.repository.account.*;
import com.sinosoft.repository.fixedassets.AccGCheckInfoRepository;
import com.sinosoft.repository.intangibleassets.AccWCheckInfoRepository;
import com.sinosoft.service.account.AccCheckInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AccCheckInfoServiceImpl implements AccCheckInfoService {

    @Resource
    private AccCheckInfoRespository accCheckInfoRespository;
    @Resource
    private AccCheckResultRespository accCheckResultRespository;
    @Resource
    private AccMainVoucherRespository accMainVoucherRespository;
    @Resource
    private AccVoucherNoRespository accVoucherNoRespository;
    @Resource
    private AccGCheckInfoRepository accGCheckInfoRepository;
    @Resource
    private AccWCheckInfoRepository accWCheckInfoRepository;
    @Override
    public List<?> qryAccCheckInfo(String year){
        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String branchCode = CurrentUser.getCurrentLoginManageBranch();
        List<AccCheckInfo> list = accCheckInfoRespository.findAll(new CusSpecification<AccCheckInfo>().and(
                CusSpecification.Cnd.eq("id.centerCode", centerCode),
                CusSpecification.Cnd.eq("id.accBookType", CurrentUser.getCurrentLoginAccountType()),
                CusSpecification.Cnd.eq("id.accBookCode", CurrentUser.getCurrentLoginAccount()),
                CusSpecification.Cnd.rlike("id.yearMonthDate", year)).asc("id.yearMonthDate"));
        return list;
    }

    @Override
    @Transactional
    public String computeAccCheckInfo(String yearMonthDate, String isCarry, String generalLedgerAndDetail, String generalLedgerAndAssist, String assistAndDetail){
        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String branchCode = CurrentUser.getCurrentLoginManageBranch();
        /*
            允许对账的情况：1.上个期间未对账或对账失败，则不允许对账
                          2.当前会计期间内所有凭证均已记账
                          3.固定、无形均已对账平衡
         */
        String msg = allowCheck(yearMonthDate);
        if (msg!=null && !"".equals(msg)) {
            if(!"noData".equals(msg)){
                return msg;
            }
        }
        /*
            itemCode：科目全代码 eg:1002/01/02/01/
            itemName：科目全名称 eg:银行存款/活期存款/农行/基本账户/
            debitDestSumS:总账的借方发生额       creditDestSumS:总账的贷方发生额    balanceSumS:总账的科目余额
            debitDestDB:明细账的借方金额合计     creditDestDB:明细账的贷方金额合计   balanceSumDB:明细账的余额合计
            debitDestAB:辅助账的借方金额合计     creditDestAB:辅助账的贷方金额合计   balanceSumAB:辅助账的余额合计
            debitDestSumR:借方金额对账结果      creditDestSumR:贷方金额对账结果     balanceSumR:余额对账结果

            balanceSumS (总账的借方发生额-总账的贷方发生额)
            balanceSumDB (明细账的期末余额-明细账的期初余额)
            balanceSumAB (辅助账的期末余额合计-辅助账的期初余额合计)

            debitDestSumR (总账-明细账 / 总账-辅助账 / 明细账-辅助账)
            creditDestSumR (总账-明细账 / 总账-辅助账 / 明细账-辅助账)
            balanceSumR (总账-明细账 / 总账-辅助账 / 明细账-辅助账)

            若对账结果为0，则表示对账平衡，否则不平衡
         */
        String accBookType = CurrentUser.getCurrentLoginAccountType();
        String accBookCode = CurrentUser.getCurrentLoginAccount();
        //先查询对账结果表中有无数据，若有则删除
        List<AccCheckResult> list1 = accCheckResultRespository.findAll(new CusSpecification<AccCheckResult>().and(
                CusSpecification.Cnd.eq("centerCode", centerCode),
                CusSpecification.Cnd.eq("accBookType", accBookType),
                CusSpecification.Cnd.eq("accBookCode", accBookCode),
                CusSpecification.Cnd.eq("yearMonthDate", yearMonthDate)));
        if (list1!=null && list1.size()>0) {
            for (AccCheckResult result : list1) {
                accCheckResultRespository.delete(result);
            }
            accCheckResultRespository.flush();
        }

        boolean flag = true;//用于表示对账是否平衡，true-平衡 false-不平
        StringBuffer sb = new StringBuffer();
        if (generalLedgerAndDetail!=null && "Y".equals(generalLedgerAndDetail)) {
            //总账与明细账对账
            sb.append("SELECT a.direction_idx AS itemCode,a.direction_idx_name AS itemName,SUM(a.debit_dest) AS debitDestSumS,SUM(a.credit_dest) AS creditDestSumS,(SUM(a.debit_dest)-SUM(a.credit_dest)) AS balanceSumS,adb.debit_dest AS debitDestDB,adb.credit_dest AS creditDestDB,(adb.balance_dest-adb.balance_begin_dest) AS balanceSumDB,(SUM(a.debit_dest)-adb.debit_dest) AS debitDestSumR,(SUM(a.credit_dest)-adb.credit_dest) AS creditDestSumR,((SUM(a.debit_dest)-SUM(a.credit_dest))-(adb.balance_dest-adb.balance_begin_dest)) AS balanceSumR FROM accsubvoucher a LEFT JOIN accdetailbalance adb ON adb.center_code = a.center_code AND adb.acc_book_type = a.acc_book_type AND adb.acc_book_code = a.acc_book_code AND adb.year_month_date = a.year_month_date AND adb.direction_idx = a.direction_idx WHERE 1=1");

            int paramsNo = 1;
            Map<Integer, Object> params = new HashMap<>();

            sb.append(" AND a.year_month_date = ?" + paramsNo);
            params.put(paramsNo, yearMonthDate);
            paramsNo++;
            sb.append(" AND a.center_code = ?" + paramsNo);
            params.put(paramsNo, centerCode);
            paramsNo++;
            sb.append(" AND a.acc_book_type = ?" + paramsNo);
            params.put(paramsNo, accBookType);
            paramsNo++;
            sb.append(" AND a.acc_book_code = ?" + paramsNo);
            params.put(paramsNo, accBookCode);
            paramsNo++;

            sb.append(" GROUP BY a.direction_idx ORDER BY a.direction_idx");

            if (isCarry!=null&&!"".equals(isCarry)&&"Y".equals(isCarry)) {
                //查历史表
                String sql = sb.toString();
                sql =sql.replaceAll("accsubvoucher","accsubvoucherhis");
                sql =sql.replaceAll("ah\\.","ah\\.");
                sql =sql.replaceAll(" ah "," ah ");
                sql =sql.replaceAll("accdetailbalance","accdetailbalancehis");
                sql =sql.replaceAll("adb\\.","adbh\\.");
                sql =sql.replaceAll(" adb "," adbh ");
                sb.setLength(0);
                sb.append(sql);
            }
            List<?> list = accCheckInfoRespository.queryBySqlSC(sb.toString(), params);
            if (list!=null && list.size()>0) {
                boolean b = saveAccCheckResult(list, accBookType, accBookCode, yearMonthDate, "0");
                if (!b)
                    flag = b;
            } else {
               // return "noData";
                if("noData".equals(msg)){
                    boolean b = saveAccCheckResult(list, accBookType, accBookCode, yearMonthDate, "0");
                }
            }
        }
        if (generalLedgerAndAssist!=null && "Y".equals(generalLedgerAndAssist)) {
            //总账与辅助账对账
            sb.setLength(0);

            int paramsNo = 1;
            Map<Integer, Object> params = new HashMap<>();

            sb.append("SELECT a.direction_idx AS itemCode,a.direction_idx_name AS itemName,SUM(a.debit_dest) AS debitDestSumS,SUM(a.credit_dest) AS creditDestSumS,(SUM(a.debit_dest)-SUM(a.credit_dest)) AS balanceSumS,temp.debitDestAB,temp.creditDestAB,temp.balanceSumAB,(SUM(a.debit_dest)-temp.debitDestAB) AS debitDestSumR,(SUM(a.credit_dest)-temp.creditDestAB) AS creditDestSumR,((SUM(a.debit_dest)-SUM(a.credit_dest))-temp.balanceSumAB) AS balanceSumR FROM accsubvoucher a");
            sb.append(" LEFT JOIN (SELECT aab.direction_idx AS itemCode,aab.direction_idx_name AS itemName,SUM(aab.debit_dest) AS debitDestAB,SUM(aab.credit_dest) AS creditDestAB,(SUM(aab.balance_dest)-SUM(aab.balance_begin_dest)) AS balanceSumAB FROM accarticlebalance aab WHERE 1=1");

            sb.append(" AND aab.year_month_date = ?" + paramsNo);
            params.put(paramsNo, yearMonthDate);
            paramsNo++;
            sb.append(" AND aab.center_code = ?" + paramsNo);
            params.put(paramsNo, centerCode);
            paramsNo++;
            sb.append(" AND aab.acc_book_type = ?" + paramsNo);
            params.put(paramsNo, accBookType);
            paramsNo++;
            sb.append(" AND aab.acc_book_code = ?" + paramsNo);
            params.put(paramsNo, accBookCode);
            paramsNo++;

            sb.append(" GROUP BY aab.direction_idx ORDER BY aab.direction_idx) temp");
            sb.append(" ON temp.itemCode = a.direction_idx WHERE 1=1");

            sb.append(" AND a.year_month_date = ?" + paramsNo);
            params.put(paramsNo, yearMonthDate);
            paramsNo++;
            sb.append(" AND a.center_code = ?" + paramsNo);
            params.put(paramsNo, centerCode);
            paramsNo++;
            sb.append(" AND a.acc_book_type = ?" + paramsNo);
            params.put(paramsNo, accBookType);
            paramsNo++;
            sb.append(" AND a.acc_book_code = ?" + paramsNo);
            params.put(paramsNo, accBookCode);
            paramsNo++;

            sb.append(" AND a.direction_other != ?" + paramsNo);
            params.put(paramsNo, "");
            paramsNo++;
            sb.append(" AND a.direction_other is not null ");



            sb.append(" GROUP BY a.direction_idx ORDER BY a.direction_idx");

            if (isCarry!=null&&!"".equals(isCarry)&&"Y".equals(isCarry)) {
                //查历史表
                String sql = sb.toString();
                sql =sql.replaceAll("accsubvoucher","accsubvoucherhis");
                sql =sql.replaceAll("ah\\.","ah\\.");
                sql =sql.replaceAll(" ah "," ah ");
                sql =sql.replaceAll("accarticlebalance","accarticlebalancehis");
                sql =sql.replaceAll("aab\\.","aabh\\.");
                sql =sql.replaceAll(" aab "," aabh ");
                sb.setLength(0);
                sb.append(sql);
            }

            List<?> list = accCheckInfoRespository.queryBySqlSC(sb.toString(), params);
            if (list!=null && list.size()>0) {
                boolean b = saveAccCheckResult(list, accBookType, accBookCode, yearMonthDate, "1");
                if (!b)
                    flag = b;
            } else {
              //  return "noData";
                if("noData".equals(msg)){
                    boolean b = saveAccCheckResult(list, accBookType, accBookCode, yearMonthDate, "1");
                }
            }
        }
        if (assistAndDetail!=null && "Y".equals(assistAndDetail)) {
            //明细账DB与辅助账对账AB
            sb.setLength(0);

            int paramsNo = 1;
            Map<Integer, Object> params = new HashMap<>();

            sb.append("SELECT adb.direction_idx AS itemCode,adb.direction_idx_name AS itemName,adb.debit_dest AS debitDestDB,adb.credit_dest AS creditDestDB,(adb.balance_dest-adb.balance_begin_dest) AS balanceSumDB,temp.debitDestAB,temp.creditDestAB,temp.balanceSumAB,(adb.debit_dest-temp.debitDestAB) AS debitDestSumR,(adb.credit_dest-temp.creditDestAB) AS creditDestSumR,((adb.balance_dest-adb.balance_begin_dest)-temp.balanceSumAB) AS balanceSumR FROM accsubvoucher a");
            sb.append(" LEFT JOIN accdetailbalance adb ON adb.center_code = a.center_code AND adb.acc_book_type = a.acc_book_type AND adb.acc_book_code = a.acc_book_code AND adb.year_month_date = a.year_month_date AND adb.direction_idx = a.direction_idx ");
            sb.append(" LEFT JOIN (SELECT aab.direction_idx AS itemCode,aab.direction_idx_name AS itemName,SUM(aab.debit_dest) AS debitDestAB,SUM(aab.credit_dest) AS creditDestAB,(SUM(aab.balance_dest)-SUM(aab.balance_begin_dest)) AS balanceSumAB FROM accarticlebalance aab WHERE 1=1");

            sb.append(" AND aab.year_month_date = ?" + paramsNo);
            params.put(paramsNo, yearMonthDate);
            paramsNo++;
            sb.append(" AND aab.center_code = ?" + paramsNo);
            params.put(paramsNo, centerCode);
            paramsNo++;
            sb.append(" AND aab.acc_book_type = ?" + paramsNo);
            params.put(paramsNo, accBookType);
            paramsNo++;
            sb.append(" AND aab.acc_book_code = ?" + paramsNo);
            params.put(paramsNo, accBookCode);
            paramsNo++;

            sb.append(" GROUP BY aab.direction_idx ORDER BY aab.direction_idx) temp");
            sb.append(" ON temp.itemCode = adb.direction_idx  WHERE 1=1");

            sb.append(" AND a.year_month_date = ?" + paramsNo);
            params.put(paramsNo, yearMonthDate);
            paramsNo++;
            sb.append(" AND a.center_code = ?" + paramsNo);
            params.put(paramsNo, centerCode);
            paramsNo++;
            sb.append(" AND a.acc_book_type = ?" + paramsNo);
            params.put(paramsNo, accBookType);
            paramsNo++;
            sb.append(" AND a.acc_book_code = ?" + paramsNo);
            params.put(paramsNo, accBookCode);
            paramsNo++;

            sb.append(" AND a.direction_other != ?" + paramsNo);
            params.put(paramsNo, "");
            paramsNo++;
            sb.append(" AND a.direction_other is not null ");

            sb.append(" GROUP BY a.direction_idx ORDER BY a.direction_idx");

            if (isCarry!=null&&!"".equals(isCarry)&&"Y".equals(isCarry)) {
                //查历史表
                String sql = sb.toString();
                sql =sql.replaceAll("accsubvoucher","accsubvoucherhis");
                sql =sql.replaceAll("ah\\.","ah\\.");
                sql =sql.replaceAll(" ah "," ah ");
                sql =sql.replaceAll("accdetailbalance","accdetailbalancehis");
                sql =sql.replaceAll("adb\\.","adbh\\.");
                sql =sql.replaceAll(" adb "," adbh ");
                sql =sql.replaceAll("accarticlebalance","accarticlebalancehis");
                sql =sql.replaceAll("aab\\.","aabh\\.");
                sql =sql.replaceAll(" aab "," aabh ");
                sb.setLength(0);
                sb.append(sql);
            }

            List<?> list = accCheckInfoRespository.queryBySqlSC(sb.toString(), params);
            if (list!=null && list.size()>0) {
                boolean b = saveAccCheckResult(list, accBookType, accBookCode, yearMonthDate, "2");
                if (!b)
                    flag = b;
            } else {
              //  return "noData";
                boolean b = saveAccCheckResult(list, accBookType, accBookCode, yearMonthDate, "2");

            }
        }

        //更新对账信息表数据
        AccMonthTraceId id = new AccMonthTraceId();
        id.setCenterCode(centerCode);
        id.setAccBookType(accBookType);
        id.setAccBookCode(accBookCode);
        id.setYearMonthDate(yearMonthDate);
        AccCheckInfo accCheckInfo = accCheckInfoRespository.findById(id).get();
        accCheckInfo.setCreateBy(CurrentUser.getCurrentUser().getId()+"");
        accCheckInfo.setCreateTime(CurrentTime.getCurrentTime());
        if (flag) {
            accCheckInfo.setIsCheck("Y");
        } else {
            accCheckInfo.setIsCheck("N");
        }
        accCheckInfoRespository.save(accCheckInfo);
        if("noData".equals(msg)){
            return "notmessage";
        }
        return "";
    }

    private String allowCheck(String yearMonthDate){
        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String branchCode = CurrentUser.getCurrentLoginManageBranch();
        String accBookType = CurrentUser.getCurrentLoginAccountType();
        String accBookCode = CurrentUser.getCurrentLoginAccount();

        /*
           允许对账的情况：1.上个期间未对账或对账失败，则不允许对账
                         2.当前会计期间内所有凭证均已记账
                         3.固定、无形均已对账平衡
        */

        String str = yearMonthDate.substring(4);

        String lastYMD = yearMonthDate;
        if ("JS".equals(str)) {
            lastYMD = yearMonthDate.substring(0, 4) + "12";
        } else if ("01".equals(str)) {
            lastYMD = (Integer.valueOf(yearMonthDate.substring(0, 4))-1) + "14";
        } else {
            lastYMD = (Integer.valueOf(yearMonthDate)-1) + "";
        }
        AccMonthTraceId id = new AccMonthTraceId(centerCode, lastYMD, accBookType, accBookCode);
        AccCheckInfo lastInfo = accCheckInfoRespository.findById(id).get();
        if (!(lastInfo!=null && "Y".equals(lastInfo.getIsCheck()))) {
            return "lastNotCheck";
        }

        if (!"JS".equals(str) && !"13".equals(str) && !"14".equals(str)) {
            //资产模块只有常规月度
            //判断固定资产是否对账
            StringBuffer sql1=new StringBuffer("select * from AccGCheckInfo where 1=1");

            int paramsNo = 1;
            Map<Integer, Object> params = new HashMap<>();

            sql1.append(" and center_code = ?" + paramsNo);
            params.put(paramsNo, centerCode);
            paramsNo++;
            sql1.append(" and year_month_date = ?" + paramsNo);
            params.put(paramsNo, yearMonthDate);
            paramsNo++;
            sql1.append(" and acc_book_code = ?" + paramsNo);
            params.put(paramsNo, accBookCode);
            paramsNo++;
            sql1.append(" and acc_book_type = ?" + paramsNo);
            params.put(paramsNo, accBookType);
            paramsNo++;

            List<AccGCheckInfo> accgcheckinfo= (List<AccGCheckInfo>)accGCheckInfoRepository.queryBySql(sql1.toString(), params, AccGCheckInfo.class);
            if(accgcheckinfo!=null&&accgcheckinfo.size()>0){
                AccGCheckInfo ag = accgcheckinfo.get(0);

            /*if(ag.getIsCheck()==null || "".equals(ag.getIsCheck())){
                //未对账
                return "GDNotCheck";
            }
            if("N".equals(ag.getIsCheck())){
                //固定对账失败
                return "GDfailcheck";
            }*/

                if (ag.getFlag()!=null && !"".equals(ag.getFlag())) {
                    String flag = ag.getFlag();
                    if ("0".equals(flag)) {
                        //未计提折旧
                        return "GDNotDepre";
                    } else if ("1".equals(flag)) {
                        //未生成计提折旧凭证
                        return "GDNotVoucher";
                    } else if ("2".equals(flag)) {
                        //未生成计提折旧凭证
                        return "GDNotVoucher";
                    } else if ("3".equals(flag)) {
                        //已生成计提折旧凭证
                    }
                }
            }

            //判断无形资产是否对账
            StringBuffer sql2=new StringBuffer("select * from accwcheckinfo where 1=1");

            paramsNo = 1;
            params = new HashMap<>();

            sql2.append(" and center_code = ?" + paramsNo);
            params.put(paramsNo, centerCode);
            paramsNo++;
            sql2.append(" and year_month_date = ?" + paramsNo);
            params.put(paramsNo, yearMonthDate);
            paramsNo++;
            sql2.append(" and acc_book_code = ?" + paramsNo);
            params.put(paramsNo, accBookCode);
            paramsNo++;
            sql2.append(" and acc_book_type = ?" + paramsNo);
            params.put(paramsNo, accBookType);
            paramsNo++;

            List<AccWCheckInfo> accwcheckinfo= (List<AccWCheckInfo>)accGCheckInfoRepository.queryBySql(sql2.toString(), params, AccWCheckInfo.class);
            if(accwcheckinfo!=null&&accwcheckinfo.size()>0){
                AccWCheckInfo aw = accwcheckinfo.get(0);

            /*if(aw.getIsCheck()==null || "".equals(aw.getIsCheck())){
                //未对账
                return "WXNotCheck";
            }
            if("N".equals(aw.getIsCheck())){
                //无形对账失败
                return "WXfailcheck";
            }*/

                if (aw.getFlag()!=null && !"".equals(aw.getFlag())) {
                    String flag = aw.getFlag();
                    if ("0".equals(flag)) {
                        //未计提摊销
                        return "WXNotDepre";
                    } else if ("1".equals(flag)) {
                        //未生成计提摊销凭证
                        return "WXNotVoucher";
                    } else if ("2".equals(flag)) {
                        //未生成计提摊销凭证
                        return "WXNotVoucher";
                    } else if ("3".equals(flag)) {
                        //已生成计提折旧凭证
                    }
                }
            }
        }

        List<AccVoucherNo> noList =  accVoucherNoRespository.findAll(new CusSpecification<>().and(
                CusSpecification.Cnd.eq("id.centerCode", centerCode),
                CusSpecification.Cnd.eq("id.accBookType", accBookType),
                CusSpecification.Cnd.eq("id.accBookCode", accBookCode),
                CusSpecification.Cnd.eq("id.yearMonthDate", yearMonthDate),
                CusSpecification.Cnd.eq("voucherNo", "1")));
        if (noList!=null && noList.size()>0) {//voucherNo 不为1，则表示当前会计期间内存在凭证
            String no = noList.get(0).getVoucherNo();
            if ("1".equals(no))
                return "noData";
        }

        List<AccMainVoucher> list = accMainVoucherRespository.findAll(new CusSpecification<>().and(
                CusSpecification.Cnd.eq("id.centerCode", centerCode),
                CusSpecification.Cnd.eq("id.branchCode", branchCode),
                CusSpecification.Cnd.eq("id.accBookType", accBookType),
                CusSpecification.Cnd.eq("id.accBookCode", accBookCode),
                CusSpecification.Cnd.eq("id.yearMonthDate", yearMonthDate),
                CusSpecification.Cnd.ne("voucherFlag", "3")));
        if (list!=null && list.size()>0) {
            return "existNotGene";
        }

        return "";
    }

    private boolean saveAccCheckResult(List<?> list, String accBookType, String accBookCode, String yearMonthDate, String checkType){
        /*
            itemCode：科目全代码 eg:1002/01/02/01/
            itemName：科目全名称 eg:银行存款/活期存款/农行/基本账户/
            debitDestSumS:总账的借方发生额       creditDestSumS:总账的贷方发生额    balanceSumS:总账的科目余额
            debitDestDB:明细账的借方金额合计     creditDestDB:明细账的贷方金额合计   balanceSumDB:明细账的余额合计
            debitDestAB:辅助账的借方金额合计     creditDestAB:辅助账的贷方金额合计   balanceSumAB:辅助账的余额合计
            debitDestSumR:借方金额对账结果      creditDestSumR:贷方金额对账结果     balanceSumR:余额对账结果

            balanceSumS (总账的借方发生额-总账的贷方发生额)
            balanceSumDB (明细账的期末余额-明细账的期初余额)
            balanceSumAB (辅助账的期末余额合计-辅助账的期初余额合计)

            debitDestSumR (总账-明细账 / 总账-辅助账 / 明细账-辅助账)
            creditDestSumR (总账-明细账 / 总账-辅助账 / 明细账-辅助账)
            balanceSumR (总账-明细账 / 总账-辅助账 / 明细账-辅助账)

            若对账结果为0，则表示对账平衡，否则不平衡
         */
        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String branchCode = CurrentUser.getCurrentLoginManageBranch();
        boolean flag = true;//用于表示对账是否平衡，true-平衡 false-不平
        String[][] str = {
                {"debitDestSumS","debitDestDB","creditDestSumS","creditDestDB","balanceSumS","balanceSumDB"},
                {"debitDestSumS","debitDestAB","creditDestSumS","creditDestAB","balanceSumS","balanceSumAB"},
                {"debitDestDB","debitDestAB","creditDestDB","creditDestAB","balanceSumDB","balanceSumAB"},
                {"总账借方发生额:","，明细账借方金额合计:","总账贷方发生额:","，明细账贷方金额合计:","总账科目余额:","，明细账余额合计:"},
                {"总账借方发生额:","，辅助账借方金额合计:","总账贷方发生额:","，辅助账贷方金额合计:","总账科目余额:","，辅助账余额合计:"},
                {"明细账借方金额合计:","，辅助账借方金额合计:","明细账贷方金额合计:","，辅助账贷方金额合计:","明细账余额合计:","，辅助账余额合计:"}};
        int type = 0;
        if (!(checkType!=null && ("0".equals(checkType)||"1".equals(checkType)||"2".equals(checkType)))) {
            throw new RuntimeException("参数异常：checkType="+checkType);
        } else {
            type = Integer.parseInt(checkType);
        }
        for (Object o : list) {
            AccCheckResult result = null;
            Map map = (Map) o;
            if (map.get("debitDestSumR")!=null && ((BigDecimal)map.get("debitDestSumR")).compareTo(new BigDecimal("0.00"))!=0) {
                result = new AccCheckResult();
                //详细
                String detail = str[type+3][0]+((BigDecimal)map.get(str[type][0])).toString()+str[type+3][1]+((BigDecimal)map.get(str[type][1])).toString();
                result.setDetail(detail);
                flag = false;
            }
            if (map.get("creditDestSumR")!=null && ((BigDecimal)map.get("creditDestSumR")).compareTo(new BigDecimal("0.00"))!=0) {
                String detail = str[type+3][2]+((BigDecimal)map.get(str[type][2])).toString()+str[type+3][3]+((BigDecimal)map.get(str[type][3])).toString();
                if (result!=null) {
                    //详细
                    result.setDetail(result.getDetail()+"<br/>"+detail);
                } else {
                    result = new AccCheckResult();
                    result.setDetail(detail);
                }
                flag = false;
            }


            if (map.get("balanceSumR")!=null && ((BigDecimal)map.get("balanceSumR")).compareTo(new BigDecimal("0.00"))!=0) {
                String detail = str[type+3][4]+((BigDecimal)map.get(str[type][4])).toString()+str[type+3][5]+((BigDecimal)map.get(str[type][5])).toString();
                if (result!=null) {
                    //详细
                    result.setDetail(result.getDetail()+"<br/>"+detail);
                } else {
                    result = new AccCheckResult();
                    result.setDetail(detail);
                }
                flag = false;
            }
            if (result!=null) {
                result.setCenterCode(centerCode);//核算单位
                result.setAccBookType(accBookType);//账套类型
                result.setAccBookCode(accBookCode);//账套编码
                result.setYearMonthDate(yearMonthDate);//会计期间
                result.setIsBalance("1");//平衡情况 0-平衡 1-不平
                //科目及名称
                String[] itemNames = ((String) map.get("itemName")).split("/");
                result.setItemCode((String) map.get("itemCode") + (itemNames.length>0?"("+itemNames[itemNames.length-1]+")":""));
                result.setCheckType(checkType);//对账类型 0-总账与明细账 1-总账与辅助账 2-辅助账与明细账
                accCheckResultRespository.save(result);
            }
        }
        if (flag) {
            AccCheckResult result = new AccCheckResult();
            result.setCenterCode(centerCode);//核算单位
            result.setAccBookType(accBookType);//账套类型
            result.setAccBookCode(accBookCode);//账套编码
            result.setYearMonthDate(yearMonthDate);//会计期间
            result.setIsBalance("0");//平衡情况 0-平衡 1-不平
            result.setCheckType(checkType);//对账类型 0-总账与明细账 1-总账与辅助账 2-辅助账与明细账
            accCheckResultRespository.save(result);
        }
        accCheckResultRespository.flush();
        return flag;
    }

    @Override
    public List<?> qryComputeAccCheckInfo(String yearMonthDate, String generalLedgerAndDetail, String generalLedgerAndAssist, String assistAndDetail){
        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String branchCode = CurrentUser.getCurrentLoginManageBranch();
        String accBookType = CurrentUser.getCurrentLoginAccountType();
        String accBookCode = CurrentUser.getCurrentLoginAccount();
        //查询总账与明细账对账结果
        List<?> list1 = accCheckResultRespository.findAll(new CusSpecification<AccCheckResult>().and(
                CusSpecification.Cnd.eq("centerCode", centerCode),
                CusSpecification.Cnd.eq("accBookType", accBookType),
                CusSpecification.Cnd.eq("accBookCode", accBookCode),
                CusSpecification.Cnd.eq("yearMonthDate", yearMonthDate),
                CusSpecification.Cnd.eq("checkType", "0")).asc("itemCode"));
        //查询总账与辅助账对账结果
        List<?> list2 = accCheckResultRespository.findAll(new CusSpecification<AccCheckResult>().and(
                CusSpecification.Cnd.eq("centerCode", centerCode),
                CusSpecification.Cnd.eq("accBookType", accBookType),
                CusSpecification.Cnd.eq("accBookCode", accBookCode),
                CusSpecification.Cnd.eq("yearMonthDate", yearMonthDate),
                CusSpecification.Cnd.eq("checkType", "1")).asc("itemCode"));
        //查询辅助账与明细账对账结果
        List<?> list3 = accCheckResultRespository.findAll(new CusSpecification<AccCheckResult>().and(
                CusSpecification.Cnd.eq("centerCode", centerCode),
                CusSpecification.Cnd.eq("accBookType", accBookType),
                CusSpecification.Cnd.eq("accBookCode", accBookCode),
                CusSpecification.Cnd.eq("yearMonthDate", yearMonthDate),
                CusSpecification.Cnd.eq("checkType", "2")).asc("itemCode"));

        List<Object> list = new ArrayList();
        list.add(list1);
        list.add(list2);
        list.add(list3);

        return list;
    }
}
