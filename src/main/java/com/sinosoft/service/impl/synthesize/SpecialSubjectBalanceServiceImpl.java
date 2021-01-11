package com.sinosoft.service.impl.synthesize;

import com.sinosoft.common.CurrentUser;
import com.sinosoft.common.SysPringLog;
import com.sinosoft.dto.VoucherDTO;
import com.sinosoft.repository.VoucherRepository;
import com.sinosoft.service.synthesize.DetailAccountService;
import com.sinosoft.service.synthesize.SpecialSubjectBalanceService;
import com.sinosoft.util.ExcelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;

@Service
public class SpecialSubjectBalanceServiceImpl implements SpecialSubjectBalanceService {
    private Logger logger = LoggerFactory.getLogger(SpecialSubjectBalanceServiceImpl.class);
    @Resource
    private VoucherRepository voucherRepository;
    @Value("${MODELPath}")
    private String MODELPath ;
    @Resource
    private DetailAccountService detailAccountService;

    /*
        导出操作，为避免重复查询，临时存储校验查询结果，此结果即为导出数据，否则不可用
        key = 用户ID + 下划线 + 当前机构 + 下划线 + 当前核算单位 + 下划线 + 当前登录账套类型 + 下划线 + 当前登录账套编码
     */
    private Map<String, Object> exportDataMap;

    /**
     * 专项科目余额表查询
     * @param dto
     * @param cumulativeAmount 显示累计发生额（0-否 1-是）
     * @return
     */
    @Override
    @SysPringLog(value = "专项科目余额查询")  //打印日志信息
    public List<?> querySpecialSubjectBalanceList(VoucherDTO dto, String cumulativeAmount){
        //  所有的时分秒
        long time = System.currentTimeMillis();

        //  机构编号
        List<String> centerCode = detailAccountService.getSubBranch();
        //  将机构编号设置给branchCode
        List<String> branchCode = centerCode;
        //  账套类型
        String accBookType = CurrentUser.getCurrentLoginAccountType();
        //  账套代码
        String accBookCode = CurrentUser.getCurrentLoginAccount();
        //  创建集合用来存放信息
        List result = new ArrayList();

        boolean chargeFlag = false;//包含未记账凭证，默认不包含
        //科目代码
        String subjectCode = dto.getSubjectCode();
        //专项名称是否全部显示
        String specialNameP=dto.getSpecialNameP();
        //期间范围
        String startYearMonth = dto.getYearMonth();
        String endYearMonth = dto.getYearMonthDate();

        //专项代码
        String specialCode = "";
        List<String> specialCondition = new ArrayList<>();
        if (dto.getSpecialName()!=null&&!"".equals(dto.getSpecialName())) {
            specialCode = dto.getSpecialCode();
            if (specialCode!=null && !"".equals(specialCode)) {
                specialCondition = Arrays.asList(specialCode.split(","));//获取专项筛选条件
            }
        }
        //是否包含未记账凭证：0-否；1-是
        String voucherGene = dto.getVoucherGene();
        if("1".equals(voucherGene)) chargeFlag = true;
        //核算规则：1-科目汇总；2-专项汇总
        String accounRules = dto.getAccounRules();

        if("1".equals(accounRules)){//科目汇总
            if(!"".equals(subjectCode)){//科目不为空
                String[] subjectCodeArr = subjectCode.split(",");
                for(int i = 0; i < subjectCodeArr.length; i++){//按页面科目顺序排序
                    String itemCode = subjectCodeArr[i];
                    getSubjectSpecialBalance(itemCode, centerCode, branchCode, accBookType, accBookCode, specialCondition, startYearMonth, endYearMonth, cumulativeAmount, chargeFlag, result,specialNameP);
                }
            }else{//科目为空
                getSubjectSpecialBalance(null, centerCode, branchCode, accBookType, accBookCode, specialCondition, startYearMonth, endYearMonth, cumulativeAmount, chargeFlag, result,specialNameP);
            }
        }else if(("2".equals(accounRules))){//专项汇总
            if(!"".equals(specialCode)&&specialCode!=null){//专项不为空
                String[] specialCodeArr = specialCode.split(",");
                for(int i = 0; i < specialCodeArr.length; i++) {//按页面专项顺序排序
                    getSpecialSubjectBalance(specialCodeArr[i], centerCode, branchCode, accBookType, accBookCode, subjectCode, startYearMonth, endYearMonth, cumulativeAmount, chargeFlag, result,specialNameP);
                }
            }else{//专项为空
                getSpecialSubjectBalance(null, centerCode, branchCode, accBookType, accBookCode, subjectCode, startYearMonth, endYearMonth, cumulativeAmount, chargeFlag, result,specialNameP);
            }
        }
        addSummary(result, accounRules, cumulativeAmount);//添加合计

        //如果没勾选显示累计发生额，则当期初，发生额，及期末全为0时，不显示此条，只要其中一项不为0，就显示。
        //如果勾选了显示累计发生额，则当期初，发生额，累计发生额，及期末全为0时，不显示此条，只要其中一项不为0，就显示
        result = ignoreSomeData(cumulativeAmount, dto.getAccounRules(), result);

        //处理查询条件之余额限制
        result = balanceLimit(cumulativeAmount, dto, result);

        System.out.println("专项科目余额表查询用时："+(System.currentTimeMillis()-time)+" ms");
        return result;
    }

    @Override
    public String isHasData(VoucherDTO voucherDTO, String cumulativeAmount) {
        List<?> list = querySpecialSubjectBalanceList(voucherDTO,cumulativeAmount);
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
    public void exportData(HttpServletRequest request, HttpServletResponse response, VoucherDTO voucherDTO,String accounRules, String cumulativeAmount, String yearMonth, String yearMonthDate) {
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
            result = querySpecialSubjectBalanceList(voucherDTO,cumulativeAmount);
        }

        ExcelUtil excelUtil = new ExcelUtil();
        excelUtil.exportSpecialSubjectBalance(request,response,result,accounRules,cumulativeAmount,yearMonth,yearMonthDate,MODELPath);
    }

    //添加合计项
    private void addSummary(List result, String accounRules, String cumulativeAmount) {
        if(result != null && !result.isEmpty()){
            String sumFlag = "";
            String sumColumn = "";
            if("1".equals(accounRules)){//1-科目汇总
                sumFlag = "科目合计";
                sumColumn = "subjectCode";
            }else if("2".equals(accounRules)){//2-专项汇总
                sumFlag = "专项合计";
                sumColumn = "detailCode";
            }else{
                sumFlag = "科目合计";
                sumColumn = "subjectCode";
            }
            //合计项
            BigDecimal sumBalanceQc = new BigDecimal(0.00);
            BigDecimal sumBalanceQm = new BigDecimal(0.00);
            BigDecimal sumDebitBq = new BigDecimal(0.00);
            BigDecimal sumCreditBq = new BigDecimal(0.00);
            BigDecimal sumDebitBn = new BigDecimal(0.00);
            BigDecimal sumCreditBn = new BigDecimal(0.00);
            for(int i = 0; i < result.size(); i++){
                Map<String, String> map = (Map<String, String>) result.get(i);
                if(sumFlag.equals(map.get(sumColumn))){
                    if("借".equals(map.get("directionQc")) || "平".equals(map.get("directionQc"))){
                        sumBalanceQc = sumBalanceQc.add(new BigDecimal(map.get("balanceQc")));
                    }else{
                        sumBalanceQc = sumBalanceQc.subtract(new BigDecimal(map.get("balanceQc")));
                    }
                    if("借".equals(map.get("directionQm")) || "平".equals(map.get("directionQm"))){
                        sumBalanceQm = sumBalanceQm.add(new BigDecimal(map.get("balanceQm")));
                    }else{
                        sumBalanceQm = sumBalanceQm.subtract(new BigDecimal(map.get("balanceQm")));
                    }
                    sumDebitBq = sumDebitBq.add(new BigDecimal(map.get("debitBq")));
                    sumCreditBq = sumCreditBq.add(new BigDecimal(map.get("creditBq")));
                    if("1".equals(cumulativeAmount)) {//是否显示本年累计，0-否，1-是
                        sumDebitBn = sumDebitBn.add(new BigDecimal(map.get("debitBn")));
                        sumCreditBn = sumCreditBn.add(new BigDecimal(map.get("creditBn")));
                    }
                }
            }
            Map<String, String> sumMap = new HashMap<>();
            sumMap.put(sumColumn, "合计");
            sumMap.put("debitBq", sumDebitBq.toString());
            sumMap.put("creditBq", sumCreditBq.toString());
            if("1".equals(cumulativeAmount)){//是否显示本年累计，0-否，1-是
                sumMap.put("debitBn", sumDebitBn.toString());
                sumMap.put("creditBn", sumCreditBn.toString());
            }
            result.add(sumMap);
        }
    }

    //获取科目专项余额数据
    @SysPringLog(value = "根据科目汇总")
    private void getSubjectSpecialBalance(String itemCode, List centerCode, List branchCode, String accBookType, String accBookCode, List<String> specialCondition, String startYearMonth, String endYearMonth, String cumulativeAmount, boolean chargeFlag, List result,String specialNameP) {
        //获取需要展示的科目专项及发生额
        List itemList = getSubjectSpecialList(centerCode, branchCode, accBookType, accBookCode, itemCode, specialCondition, startYearMonth, endYearMonth, chargeFlag,specialNameP);
        if(itemList == null || itemList.isEmpty()) return;
        //获取期初专项余额信息
        Map<String, Map<String, String>> qcMap = getPeriodSubjectSpecialData(centerCode, branchCode, accBookType, accBookCode, itemCode, specialCondition, startYearMonth);
        //获取期末余额和本年累计
        Map<String, Map<String, String>> qmMap = getPeriodSubjectSpecialData(centerCode, branchCode, accBookType, accBookCode, itemCode, specialCondition, endYearMonth);

        //1.计入期末余额
        if(chargeFlag){
            //未结转期间已记账/未记、账凭证处理
            Map<String, Map<String, String>> noChargeItemBq = getNoChargeItemBq(centerCode, branchCode, accBookType, accBookCode, itemCode, specialCondition, endYearMonth);
            for(String s : noChargeItemBq.keySet()){
                BigDecimal noChargeDebitBq = new BigDecimal(noChargeItemBq.get(s).get("debitBq"));//本期-未记账-借
                BigDecimal noChargeCreditBq = new BigDecimal(noChargeItemBq.get(s).get("creditBq"));//本期-未记账-贷
                Map<String, String> qmDataMap = qmMap.get(s);
                if(qmDataMap == null){
                    qmDataMap = new HashMap<>();
                    qmDataMap.put("balanceQm", noChargeDebitBq.subtract(noChargeCreditBq).toString());
                    qmDataMap.put("debitBn", "0.00");
                    qmDataMap.put("creditBn", "0.00");
                    qmMap.put(s, qmDataMap);
                }else{
                    BigDecimal curBalanceQm = new BigDecimal(qmDataMap.get("balanceQm"));//期末-不含未记账
                    qmDataMap.put("balanceQm", curBalanceQm.add(noChargeDebitBq).subtract(noChargeCreditBq).toString());
                    qmMap.put(s, qmDataMap);
                }
            }
            //计入本年累计发生
            Map<String, Map<String, String>> noChargeItemBn = getNoChargeItemBn( centerCode, branchCode, accBookType, accBookCode, itemCode, specialCondition, startYearMonth, endYearMonth);
            for(String s : noChargeItemBn.keySet()){
                BigDecimal noChargeDebitBn = new BigDecimal(noChargeItemBn.get(s).get("debitBn"));//本年-未记账-借
                BigDecimal noChargeCreditBn = new BigDecimal(noChargeItemBn.get(s).get("creditBn"));//本年-未记账-贷
                Map<String, String> qmDataMap = qmMap.get(s);
                BigDecimal curDebitBn = qmDataMap.get("debitBn") == null ? new BigDecimal(0.00) : new BigDecimal(qmDataMap.get("debitBn"));//本年借方-不含未记账
                BigDecimal curCreditBn = qmDataMap.get("creditBn") == null ? new BigDecimal(0.00) : new BigDecimal(qmDataMap.get("creditBn"));//本年贷方-不含未记账
                qmDataMap.put("debitBn", curDebitBn.add(noChargeDebitBn).toString());
                qmDataMap.put("creditBn", curCreditBn.add(noChargeCreditBn).toString());
                qmMap.put(s, qmDataMap);
            }
        }

        //封装返回集合
        //科目合计
        String sumCode = "subjectCode";
        String sumName = "科目合计";
        String curSumCode = "";
        BigDecimal sumBalanceQc = new BigDecimal(0.00);
        BigDecimal sumBalanceQm = new BigDecimal(0.00);
        BigDecimal sumDebitBq = new BigDecimal(0.00);
        BigDecimal sumCreditBq = new BigDecimal(0.00);
        BigDecimal sumDebitBn = new BigDecimal(0.00);
        BigDecimal sumCreditBn = new BigDecimal(0.00);
        for(int k = 0; k < itemList.size(); k++){
            Map<String, String> itemMap = (Map<String, String>) itemList.get(k);
            String itemSpecial = itemMap.get("itemSpecial");
            Map<String, String> qcDataMap = qcMap.get(itemSpecial);
            Map<String, String> qmDataMap = qmMap.get(itemSpecial);
            //展示字段
            String _subjectCode = itemMap.get("itemCode");//科目代码
            itemMap.put("subjectCode", _subjectCode);
            String _subjectName = itemMap.get("itemName");//科目名称
            String _detailCode = itemMap.get("specialCode");//专项代码
            itemMap.put("detailCode", _detailCode);
            String _detailName = itemMap.get("specialName");//专项名称
            BigDecimal _balanceQc = qcDataMap == null ? new BigDecimal(0.00) : new BigDecimal(qcDataMap.get("balanceQc"));//期初余额
//            BigDecimal _balanceQc = qcDataMap == null ? new BigDecimal(0.00) : new BigDecimal(qcDataMap.get("balanceQm"));//期初余额
            BigDecimal _balanceQm = qmDataMap == null ? new BigDecimal(0.00) : new BigDecimal(qmDataMap.get("balanceQm"));//期末余额
            BigDecimal _debitBq = new BigDecimal(itemMap.get("debitBq"));//本期借
            BigDecimal _creditBq = new BigDecimal(itemMap.get("creditBq"));//本期贷
            BigDecimal _debitBn = qmDataMap == null ? new BigDecimal(0.00) : new BigDecimal(qmDataMap.get("debitBn"));//本年累计借
            BigDecimal _creditBn = qmDataMap == null ? new BigDecimal(0.00) : new BigDecimal(qmDataMap.get("creditBn"));//本年累计贷
            Map<String, String> map = new HashMap<>();
            map.put("centerCode", CurrentUser.getCurrentLoginManageBranch());
            map.put("subjectCode", _subjectCode);
            map.put("subjectName", _subjectName);
            map.put("detailCode", _detailCode);
            map.put("detailName", _detailName);
            if(_balanceQc.compareTo(BigDecimal.ZERO) > 0){
                map.put("directionQc", "借");
                map.put("balanceQc", _balanceQc.toString());
            }else if(_balanceQc.compareTo(BigDecimal.ZERO) == 0){
                map.put("directionQc", "平");
                map.put("balanceQc", _balanceQc.toString());
            }else if(_balanceQc.compareTo(BigDecimal.ZERO) < 0){
                map.put("directionQc", "贷");
                map.put("balanceQc", _balanceQc.abs().toString());
            }
            if(_balanceQm.compareTo(BigDecimal.ZERO) > 0){
                map.put("directionQm", "借");
                map.put("balanceQm", _balanceQm.toString());
            }else if(_balanceQm.compareTo(BigDecimal.ZERO) == 0){
                map.put("directionQm", "平");
                map.put("balanceQm", _balanceQm.toString());
            }else if(_balanceQm.compareTo(BigDecimal.ZERO) < 0){
                map.put("directionQm", "贷");
                map.put("balanceQm", _balanceQm.abs().toString());
            }
            map.put("debitBq", _debitBq.toString());
            map.put("creditBq", _creditBq.toString());
            if("1".equals(cumulativeAmount)){//是否显示本年累计，0-否，1-是
                map.put("debitBn", _debitBn.toString());
                map.put("creditBn", _creditBn.toString());
            }
            if(k == 0){
                curSumCode = itemMap.get(sumCode);
            }else{
                if(!curSumCode.equals(itemMap.get(sumCode))){//添加科目合计
                    Map<String, String> sumMap = new HashMap<>();
                    sumMap.put(sumCode, sumName);
                    if(sumBalanceQc.compareTo(BigDecimal.ZERO) > 0){
                        sumMap.put("directionQc", "借");
                        sumMap.put("balanceQc", sumBalanceQc.toString());
                    }else if(sumBalanceQc.compareTo(BigDecimal.ZERO) == 0){
                        sumMap.put("directionQc", "平");
                        sumMap.put("balanceQc", sumBalanceQc.toString());
                    }else if(sumBalanceQc.compareTo(BigDecimal.ZERO) < 0){
                        sumMap.put("directionQc", "贷");
                        sumMap.put("balanceQc", sumBalanceQc.abs().toString());
                    }
                    if(sumBalanceQm.compareTo(BigDecimal.ZERO) > 0){
                        sumMap.put("directionQm", "借");
                        sumMap.put("balanceQm", sumBalanceQm.toString());
                    }else if(sumBalanceQm.compareTo(BigDecimal.ZERO) == 0){
                        sumMap.put("directionQm", "平");
                        sumMap.put("balanceQm", sumBalanceQm.toString());
                    }else if(sumBalanceQm.compareTo(BigDecimal.ZERO) < 0){
                        sumMap.put("directionQm", "贷");
                        sumMap.put("balanceQm", sumBalanceQm.abs().toString());
                    }
                    sumMap.put("debitBq", sumDebitBq.toString());
                    sumMap.put("creditBq", sumCreditBq.toString());
                    if("1".equals(cumulativeAmount)){//是否显示本年累计，0-否，1-是
                        sumMap.put("debitBn", sumDebitBn.toString());
                        sumMap.put("creditBn", sumCreditBn.toString());
                    }
                    result.add(sumMap);
                    //初始化
                    curSumCode = itemMap.get(sumCode);;
                    sumBalanceQc = new BigDecimal(0.00);
                    sumBalanceQm = new BigDecimal(0.00);
                    sumDebitBq = new BigDecimal(0.00);
                    sumCreditBq = new BigDecimal(0.00);
                    sumDebitBn = new BigDecimal(0.00);
                    sumCreditBn = new BigDecimal(0.00);
                }
            }
            sumBalanceQc = sumBalanceQc.add(_balanceQc);
            sumBalanceQm = sumBalanceQm.add(_balanceQm);
            sumDebitBq = sumDebitBq.add(_debitBq);
            sumCreditBq = sumCreditBq.add(_creditBq);
            sumDebitBn = sumDebitBn.add(_debitBn);
            sumCreditBn = sumCreditBn.add(_creditBn);
            result.add(map);
            //循环最后添加科目合计
            if(k == itemList.size() - 1){
                Map<String, String> sumMap = new HashMap<>();
                sumMap.put(sumCode, sumName);
                if(sumBalanceQc.compareTo(BigDecimal.ZERO) > 0){
                    sumMap.put("directionQc", "借");
                    sumMap.put("balanceQc", sumBalanceQc.toString());
                }else if(sumBalanceQc.compareTo(BigDecimal.ZERO) == 0){
                    sumMap.put("directionQc", "平");
                    sumMap.put("balanceQc", sumBalanceQc.toString());
                }else if(sumBalanceQc.compareTo(BigDecimal.ZERO) < 0){
                    sumMap.put("directionQc", "贷");
                    sumMap.put("balanceQc", sumBalanceQc.abs().toString());
                }
                if(sumBalanceQm.compareTo(BigDecimal.ZERO) > 0){
                    sumMap.put("directionQm", "借");
                    sumMap.put("balanceQm", sumBalanceQm.toString());
                }else if(sumBalanceQm.compareTo(BigDecimal.ZERO) == 0){
                    sumMap.put("directionQm", "平");
                    sumMap.put("balanceQm", sumBalanceQm.toString());
                }else if(sumBalanceQm.compareTo(BigDecimal.ZERO) < 0){
                    sumMap.put("directionQm", "贷");
                    sumMap.put("balanceQm", sumBalanceQm.abs().toString());
                }
                sumMap.put("debitBq", sumDebitBq.toString());
                sumMap.put("creditBq", sumCreditBq.toString());
                if("1".equals(cumulativeAmount)){//是否显示本年累计，0-否，1-是
                    sumMap.put("debitBn", sumDebitBn.toString());
                    sumMap.put("creditBn", sumCreditBn.toString());
                }
                result.add(sumMap);
            }
        }
    }
    //获取未结转期间（已记账、未记账）--本年发生额
    private Map<String, Map<String, String>> getNoChargeItemBn( List centerCode, List branchCode, String accBookType, String accBookCode, String itemCode, List<String> specialCondition, String startYearMonth, String endYearMonth) {
        StringBuffer sql = new StringBuffer("select t2.direction_idx as itemCode,t2.specialCode,cast(sum(t2.debit_dest) as char) as debitBn,cast(sum(t2.credit_dest) as char) as creditBn from (" +
                "select * from accmainvoucher am where 1=1 and am.center_code in (?1) and am.branch_code in (?2) and am.acc_book_type = ?3 and am.acc_book_code = ?4 and am.year_month_date >= ?5 and am.year_month_date <= ?6 ) t1 left join (" +
                "select a.voucher_no,a.direction_idx,substring_index(substring_index(a.direction_other,',',b.id + 1),',' ,- 1) as specialCode,a.debit_dest,a.credit_dest from (" +
                "select * from accsubvoucher where 1=1 and center_code in (?1) and branch_code in (?2) and acc_book_type = ?3 and acc_book_code = ?4 and year_month_date >= ?5 and year_month_date <= ?6 ) a " +
                "join splitstringsort b ON b.id < (length(a.direction_other) - length(REPLACE (a.direction_other, ',', '')) + 1) " +
                ") t2 on t1.voucher_no = t2.voucher_no where ");
        sql.append(" t1.voucher_flag in ('1','2') ");
        Map<Integer, Object> params = new HashMap<>();
        params.put(1, centerCode);
        params.put(2, branchCode);
        params.put(3, accBookType);
        params.put(4, accBookCode);
        params.put(5, startYearMonth);
        params.put(6, endYearMonth);
        int paramsNo = 7;

        if(itemCode != null && !"".equals(itemCode)) {
            sql.append(" and t2.direction_idx like ?" + paramsNo);
            params.put(paramsNo, itemCode+"%");
            paramsNo++;
        }
        if(specialCondition != null && specialCondition.size()>0) {
            sql.append(" and t2.specialCode in ( ?"+ paramsNo + " )");
            params.put(paramsNo, specialCondition);
            paramsNo++;
        }

        sql.append(" and t1.year_month_date like ?" + paramsNo);
        params.put(paramsNo, endYearMonth.substring(0, 4)+"%");
        paramsNo++;

        sql.append(" group by t2.direction_idx, t2.specialCode ");
        sql.append(" order by t2.direction_idx, t2.specialCode ");

        List list = voucherRepository.queryBySqlSC(sql.toString(), params);
        Map<String, Map<String, String>> resultMap = new HashMap<>();
        if(list != null && !list.isEmpty()){
            for(int i = 0; i < list.size(); i++){
                Map<String, String> map = (Map<String, String>) list.get(i);
                resultMap.put(map.get("itemCode") + "_" + map.get("specialCode"), map);
            }
        }
        return resultMap;
    }

    //获取未结转期间（已记账、未记账）/已记账（未结转期间）--本期发生额
    private Map<String, Map<String, String>> getNoChargeItemBq(List centerCode, List branchCode, String accBookType, String accBookCode, String itemCode, List<String> specialCondition, String yearMonth) {
        StringBuffer sql = new StringBuffer("select t2.direction_idx as itemCode,t2.specialCode,cast(sum(t2.debit_dest) as char) as debitBq,cast(sum(t2.credit_dest) as char) as creditBq from (" +
                "select * from accmainvoucher am where 1=1 and am.center_code in (?1) and am.branch_code in (?2) and am.acc_book_type = ?3 and  am.acc_book_code = ?4 and am.year_month_date = ?5 ) t1 left join (" +
                "select a.voucher_no,a.direction_idx,substring_index(substring_index(a.direction_other,',',b.id+ 1),',' ,- 1) as specialCode,a.debit_dest,a.credit_dest from (" +
                "select * from accsubvoucher where 1=1 and center_code in (?1) and branch_code in (?2) and acc_book_type = ?3 and acc_book_code = ?4 and year_month_date = ?5  ) a " +
                "join splitstringsort b ON b.id < (length(a.direction_other) - length(REPLACE (a.direction_other, ',', '')) + 1) " +
                ") t2 on t1.voucher_no = t2.voucher_no where ");

        sql.append(" t1.voucher_flag in ('1','2') ");


        Map<Integer, Object> params = new HashMap<>();
        params.put(1, centerCode);
        params.put(2, branchCode);
        params.put(3, accBookType);
        params.put(4, accBookCode);
        params.put(5, yearMonth);
        int paramsNo = 6;

        if(itemCode != null && !"".equals(itemCode)) {
            sql.append(" and t2.direction_idx like ?" + paramsNo);
            params.put(paramsNo, itemCode+"%");
            paramsNo++;
        }
        if(specialCondition != null && specialCondition.size()>0) {
            sql.append(" and t2.specialCode in ( ?"+ paramsNo + " )");
            params.put(paramsNo, specialCondition);
            paramsNo++;
        }

        sql.append(" group by t2.direction_idx, t2.specialCode ");
        sql.append(" order by t2.direction_idx, t2.specialCode ");
        List list = voucherRepository.queryBySqlSC(sql.toString(), params);
        Map<String, Map<String, String>> resultMap = new HashMap<>();
        if(list != null && !list.isEmpty()){
            for(int i = 0; i < list.size(); i++){
                Map<String, String> map = (Map<String, String>) list.get(i);
                resultMap.put(map.get("itemCode") + "_" + map.get("specialCode"), map);
            }
        }
        return resultMap;
    }

    //获取专项科目余额信息
    @SysPringLog(value = "根据专项汇总")
    private void getSpecialSubjectBalance(String specialCode, List centerCode, List branchCode, String accBookType, String accBookCode, String subjectCondition, String startYearMonth, String endYearMonth, String cumulativeAmount, boolean chargeFlag, List result,String specialNameP) {
        //获取需要展示的专项科目及发生额
        List specialItemList = getSpecialSubjectList(centerCode, branchCode, accBookType, accBookCode, specialCode, subjectCondition, startYearMonth, endYearMonth, chargeFlag,specialNameP);
        if(specialItemList == null || specialItemList.isEmpty()) return;
        //获取期初专项余额信息
        Map<String, Map<String, String>> qcMap = getPeriodSpecialSubjectData(centerCode, branchCode, accBookType, accBookCode, specialCode, subjectCondition, startYearMonth);
        //获取期末专项余额信息
        Map<String, Map<String, String>> qmMap = getPeriodSpecialSubjectData(centerCode, branchCode, accBookType, accBookCode, specialCode, subjectCondition, endYearMonth);

        //  根据专项汇总
        if(chargeFlag) {
            //未记账凭证处理
            //1.计入期末余额
            Map<String, Map<String, String>> noChargeSpecialBq = getNoChargeSpecialBq(chargeFlag, centerCode, branchCode, accBookType, accBookCode, specialCode, subjectCondition, startYearMonth, endYearMonth);
            for (String s : noChargeSpecialBq.keySet()) {
                BigDecimal noChargeDebitBq = new BigDecimal(noChargeSpecialBq.get(s).get("debitBq"));//本期-未记账-借
                BigDecimal noChargeCreditBq = new BigDecimal(noChargeSpecialBq.get(s).get("creditBq"));//本期-未记账-贷
                Map<String, String> qmDataMap = qmMap.get(s);
                if (qmDataMap == null) {
                    qmDataMap = new HashMap<>();
                    qmDataMap.put("balanceQm", noChargeDebitBq.subtract(noChargeCreditBq).toString());
                    qmDataMap.put("debitBn", "0.00");
                    qmDataMap.put("creditBn", "0.00");
                    qmMap.put(s, qmDataMap);
                } else {
                    BigDecimal curBalanceQm = new BigDecimal(qmDataMap.get("balanceQm"));//期末-不含未记账
                    qmDataMap.put("balanceQm", curBalanceQm.add(noChargeDebitBq).subtract(noChargeCreditBq).toString());
                    qmMap.put(s, qmDataMap);
                }
            }
            //计入本年累计发生
            Map<String, Map<String, String>> noChargSpecialmBn = getNoChargeSpecialBn(chargeFlag, centerCode, branchCode, accBookType, accBookCode, specialCode, subjectCondition, startYearMonth, endYearMonth);
            for (String s : noChargSpecialmBn.keySet()) {
                BigDecimal noChargeDebitBn = new BigDecimal(noChargSpecialmBn.get(s).get("debitBn"));//本年-未记账-借
                BigDecimal noChargeCreditBn = new BigDecimal(noChargSpecialmBn.get(s).get("creditBn"));//本年-未记账-贷
                Map<String, String> qmDataMap = qmMap.get(s);
                BigDecimal curDebitBn = qmDataMap.get("debitBn") == null ? new BigDecimal(0.00) : new BigDecimal(qmDataMap.get("debitBn"));//本年借方-不含未记账
                BigDecimal curCreditBn = qmDataMap.get("creditBn") == null ? new BigDecimal(0.00) : new BigDecimal(qmDataMap.get("creditBn"));//本年贷方-不含未记账
                qmDataMap.put("debitBn", curDebitBn.add(noChargeDebitBn).toString());
                qmDataMap.put("creditBn", curCreditBn.add(noChargeCreditBn).toString());
                qmMap.put(s, qmDataMap);
            }
        }

        //封装返回集合
        //专项合计
        String sumCode = "detailCode";
        String sumName = "专项合计";
        String curSumCode = "";
        BigDecimal sumBalanceQc = new BigDecimal(0.00);
        BigDecimal sumBalanceQm = new BigDecimal(0.00);
        BigDecimal sumDebitBq = new BigDecimal(0.00);
        BigDecimal sumCreditBq = new BigDecimal(0.00);
        BigDecimal sumDebitBn = new BigDecimal(0.00);
        BigDecimal sumCreditBn = new BigDecimal(0.00);
        for(int k = 0; k < specialItemList.size(); k++){
            Map<String, String> itemMap = (Map<String, String>) specialItemList.get(k);
            String itemSpecial = itemMap.get("itemSpecial");
            Map<String, String> qcDataMap = qcMap.get(itemSpecial);
            Map<String, String> qmDataMap = qmMap.get(itemSpecial);
            //展示字段
            String _subjectCode = itemMap.get("itemCode");//科目代码
            itemMap.put("subjectCode", _subjectCode);
            String _subjectName = itemMap.get("itemName");//科目名称
            String _detailCode = itemMap.get("specialCode");//专项代码
            itemMap.put("detailCode", _detailCode);
            String _detailName = itemMap.get("specialName");//专项名称
            BigDecimal _balanceQc = qcDataMap == null ? new BigDecimal(0.00) : new BigDecimal(qcDataMap.get("balanceQc"));//期初余额
//            BigDecimal _balanceQc = qcDataMap == null ? new BigDecimal(0.00) : new BigDecimal(qcDataMap.get("balanceQm"));//期初余额
            BigDecimal _balanceQm = qmDataMap == null ? new BigDecimal(0.00) : new BigDecimal(qmDataMap.get("balanceQm"));//期末余额
            BigDecimal _debitBq = new BigDecimal(itemMap.get("debitBq"));//本期借
            BigDecimal _creditBq = new BigDecimal(itemMap.get("creditBq"));//本期贷
            BigDecimal _debitBn = qmDataMap == null ? new BigDecimal(0.00) : new BigDecimal(qmDataMap.get("debitBn"));//本年累计借
            BigDecimal _creditBn = qmDataMap == null ? new BigDecimal(0.00) : new BigDecimal(qmDataMap.get("creditBn"));//本年累计贷
            Map<String, String> map = new HashMap<>();
            map.put("centerCode", CurrentUser.getCurrentLoginManageBranch());
            map.put("subjectCode", _subjectCode);
            map.put("subjectName", _subjectName);
            map.put("detailCode", _detailCode);
            map.put("detailName", _detailName);
            if(_balanceQc.compareTo(BigDecimal.ZERO) > 0){
                map.put("directionQc", "借");
                map.put("balanceQc", _balanceQc.toString());
            }else if(_balanceQc.compareTo(BigDecimal.ZERO) == 0){
                map.put("directionQc", "平");
                map.put("balanceQc", _balanceQc.toString());
            }else if(_balanceQc.compareTo(BigDecimal.ZERO) < 0){
                map.put("directionQc", "贷");
                map.put("balanceQc", _balanceQc.abs().toString());
            }
            if(_balanceQm.compareTo(BigDecimal.ZERO) > 0){
                map.put("directionQm", "借");
                map.put("balanceQm", _balanceQm.toString());
            }else if(_balanceQm.compareTo(BigDecimal.ZERO) == 0){
                map.put("directionQm", "平");
                map.put("balanceQm", _balanceQm.toString());
            }else if(_balanceQm.compareTo(BigDecimal.ZERO) < 0){
                map.put("directionQm", "贷");
                map.put("balanceQm", _balanceQm.abs().toString());
            }
            map.put("debitBq", _debitBq.toString());
            map.put("creditBq", _creditBq.toString());
            if("1".equals(cumulativeAmount)){//是否显示本年累计，0-否，1-是
                map.put("debitBn", _debitBn.toString());
                map.put("creditBn", _creditBn.toString());
            }
            if(k == 0){
                curSumCode = itemMap.get(sumCode);
            }else{
                if(!curSumCode.equals(itemMap.get(sumCode))){//添加科目合计
                    Map<String, String> sumMap = new HashMap<>();
                    sumMap.put(sumCode, sumName);
                    if(sumBalanceQc.compareTo(BigDecimal.ZERO) > 0){
                        sumMap.put("directionQc", "借");
                        sumMap.put("balanceQc", sumBalanceQc.toString());
                    }else if(sumBalanceQc.compareTo(BigDecimal.ZERO) == 0){
                        sumMap.put("directionQc", "平");
                        sumMap.put("balanceQc", sumBalanceQc.toString());
                    }else if(sumBalanceQc.compareTo(BigDecimal.ZERO) < 0){
                        sumMap.put("directionQc", "贷");
                        sumMap.put("balanceQc", sumBalanceQc.abs().toString());
                    }
                    if(sumBalanceQm.compareTo(BigDecimal.ZERO) > 0){
                        sumMap.put("directionQm", "借");
                        sumMap.put("balanceQm", sumBalanceQm.toString());
                    }else if(sumBalanceQm.compareTo(BigDecimal.ZERO) == 0){
                        sumMap.put("directionQm", "平");
                        sumMap.put("balanceQm", sumBalanceQm.toString());
                    }else if(sumBalanceQm.compareTo(BigDecimal.ZERO) < 0){
                        sumMap.put("directionQm", "贷");
                        sumMap.put("balanceQm", sumBalanceQm.abs().toString());
                    }
                    sumMap.put("debitBq", sumDebitBq.toString());
                    sumMap.put("creditBq", sumCreditBq.toString());
                    if("1".equals(cumulativeAmount)){//是否显示本年累计，0-否，1-是
                        sumMap.put("debitBn", sumDebitBn.toString());
                        sumMap.put("creditBn", sumCreditBn.toString());
                    }
                    result.add(sumMap);
                    //初始化
                    curSumCode = itemMap.get(sumCode);;
                    sumBalanceQc = new BigDecimal(0.00);
                    sumBalanceQm = new BigDecimal(0.00);
                    sumDebitBq = new BigDecimal(0.00);
                    sumCreditBq = new BigDecimal(0.00);
                    sumDebitBn = new BigDecimal(0.00);
                    sumCreditBn = new BigDecimal(0.00);
                }
            }
            sumBalanceQc = sumBalanceQc.add(_balanceQc);
            sumBalanceQm = sumBalanceQm.add(_balanceQm);
            sumDebitBq = sumDebitBq.add(_debitBq);
            sumCreditBq = sumCreditBq.add(_creditBq);
            sumDebitBn = sumDebitBn.add(_debitBn);
            sumCreditBn = sumCreditBn.add(_creditBn);
            result.add(map);
            //循环最后添加科目合计
            if(k == specialItemList.size() - 1){
                Map<String, String> sumMap = new HashMap<>();
                sumMap.put(sumCode, sumName);
                if(sumBalanceQc.compareTo(BigDecimal.ZERO) > 0){
                    sumMap.put("directionQc", "借");
                    sumMap.put("balanceQc", sumBalanceQc.toString());
                }else if(sumBalanceQc.compareTo(BigDecimal.ZERO) == 0){
                    sumMap.put("directionQc", "平");
                    sumMap.put("balanceQc", sumBalanceQc.toString());
                }else if(sumBalanceQc.compareTo(BigDecimal.ZERO) < 0){
                    sumMap.put("directionQc", "贷");
                    sumMap.put("balanceQc", sumBalanceQc.abs().toString());
                }
                if(sumBalanceQm.compareTo(BigDecimal.ZERO) > 0){
                    sumMap.put("directionQm", "借");
                    sumMap.put("balanceQm", sumBalanceQm.toString());
                }else if(sumBalanceQm.compareTo(BigDecimal.ZERO) == 0){
                    sumMap.put("directionQm", "平");
                    sumMap.put("balanceQm", sumBalanceQm.toString());
                }else if(sumBalanceQm.compareTo(BigDecimal.ZERO) < 0){
                    sumMap.put("directionQm", "贷");
                    sumMap.put("balanceQm", sumBalanceQm.abs().toString());
                }
                sumMap.put("debitBq", sumDebitBq.toString());
                sumMap.put("creditBq", sumCreditBq.toString());
                if("1".equals(cumulativeAmount)){//是否显示本年累计，0-否，1-是
                    sumMap.put("debitBn", sumDebitBn.toString());
                    sumMap.put("creditBn", sumCreditBn.toString());
                }
                result.add(sumMap);
            }
        }
    }

    //获取未结转期间（已记账、未记账）/已记账（未结转期间）--本年发生
    private Map<String, Map<String, String>> getNoChargeSpecialBn(boolean flag, List centerCode, List branchCode, String accBookType, String accBookCode, String specialCode, String subjectCondition, String startYearMonth, String endYearMonth) {
        StringBuffer sql = new StringBuffer("select t2.direction_idx as itemCode,t2.specialCode,cast(sum(t2.debit_dest) as char) as debitBn,cast(sum(t2.credit_dest) as char) as creditBn from (" +
                "select * from accmainvoucher am where 1=1 and am.center_code in (?1) and am.branch_code in (?2) and am.acc_book_type = ?3 and  am.acc_book_code = ?4 and am.year_month_date >= ?5 and am.year_month_date <= ?6 ) t1 left join (" +
                "select a.voucher_no,a.direction_idx,substring_index(substring_index(a.direction_other,',',b.id+ 1),',' ,- 1) as specialCode,a.debit_dest,a.credit_dest from (" +
                "select * from accsubvoucher where 1=1 and center_code in (?1) and branch_code in (?2) and acc_book_type = ?3 and acc_book_code = ?4 and year_month_date >= ?5 and year_month_date <= ?6 ) a " +
                "join splitstringsort b ON b.id < (length(a.direction_other) - length(REPLACE (a.direction_other, ',', '')) + 1) " +
                ") t2 on t1.voucher_no = t2.voucher_no where ");
        if (flag) {
            sql.append(" t1.voucher_flag in ('1', '2', '3') ");
        } else {
            sql.append(" t1.voucher_flag in ('3') ");
        }

        Map<Integer, Object> params = new HashMap<>();
        params.put(1, centerCode);
        params.put(2, branchCode);
        params.put(3, accBookType);
        params.put(4, accBookCode);
        params.put(5, startYearMonth);
        params.put(6, endYearMonth);
        int paramsNo = 7;

        if(subjectCondition != null && !"".equals(subjectCondition)){
            String[] subjectCodeArr = subjectCondition.split(",");
            if(subjectCodeArr.length == 1){
                sql.append(" and t2.direction_idx like ?" + paramsNo);
                params.put(paramsNo, subjectCodeArr[0]+"%");
                paramsNo++;
            }else{
                for(int i = 0; i < subjectCodeArr.length; i++){
                    if(i == 0){
                        sql.append(" and (t2.direction_idx like ?" + paramsNo);
                        params.put(paramsNo, subjectCodeArr[i]+"%");
                        paramsNo++;
                    }else if(i == subjectCodeArr.length - 1){
                        sql.append(" or t2.direction_idx like ?" + paramsNo + " )");
                        params.put(paramsNo, subjectCodeArr[i]+"%");
                        paramsNo++;
                    }else{
                        sql.append(" or t2.direction_idx like ?" + paramsNo);
                        params.put(paramsNo, subjectCodeArr[i]+"%");
                        paramsNo++;
                    }
                }
            }
        }

        if(specialCode != null && !"".equals(specialCode)) {
            sql.append(" and t2.specialCode = ?" + paramsNo);
            params.put(paramsNo, specialCode);
            paramsNo++;
        }

        sql.append(" and t1.year_month_date like ?" + paramsNo);
        params.put(paramsNo, endYearMonth.substring(0, 4)+"%");
        paramsNo++;

        sql.append(" group by t2.direction_idx, t2.specialCode ");
        sql.append(" order by t2.direction_idx, t2.specialCode ");

        List list = voucherRepository.queryBySqlSC(sql.toString(), params);
        Map<String, Map<String, String>> resultMap = new HashMap<>();
        if(list != null && !list.isEmpty()){
            for(int i = 0; i < list.size(); i++){
                Map<String, String> map = (Map<String, String>) list.get(i);
                resultMap.put(map.get("itemCode") + "_" + map.get("specialCode"), map);
            }
        }
        return resultMap;
    }

    //获取未结转期间（已记账、未记账）/已记账（未结转期间）--本期发生额
    private Map<String, Map<String, String>> getNoChargeSpecialBq(boolean flag, List centerCode, List branchCode, String accBookType, String accBookCode, String specialCode, String subjectCondition, String startYearMonth, String endYearMonth) {
        StringBuffer sql = new StringBuffer("select t2.direction_idx as itemCode,t2.specialCode,cast(sum(t2.debit_dest) as char) as debitBq,cast(sum(t2.credit_dest) as char) as creditBq from (" +
                "select * from accmainvoucher am where 1=1 and am.center_code in (?1) and am.branch_code in (?2)  and am.acc_book_type = ?3  and  am.acc_book_code = ?4 and am.year_month_date >= ?5 and am.year_month_date <= ?6 ) t1 left join (" +
                "select a.voucher_no,a.direction_idx,substring_index(substring_index(a.direction_other,',',b.id+ 1),',' ,- 1) as specialCode,a.debit_dest,a.credit_dest from (" +
                "select * from accsubvoucher where 1=1 and center_code in (?1) and branch_code in (?2)  and acc_book_type = ?3  and acc_book_code = ?4 and year_month_date >= ?5 and year_month_date <= ?6 ) a " +
                "join splitstringsort b ON b.id < (length(a.direction_other) - length(REPLACE (a.direction_other, ',', '')) + 1) " +
                ") t2 on t1.voucher_no = t2.voucher_no where ");
        if (flag) {
            sql.append(" t1.voucher_flag in ('1', '2', '3') ");
        } else {
            sql.append(" t1.voucher_flag in ('3') ");
        }

        Map<Integer, Object> params = new HashMap<>();
        params.put(1, centerCode);
        params.put(2, branchCode);
        params.put(3, accBookType);
        params.put(4, accBookCode);
        params.put(5, startYearMonth);
        params.put(6, endYearMonth);
        int paramsNo = 7;

        if(subjectCondition != null && !"".equals(subjectCondition)){
            String[] subjectCodeArr = subjectCondition.split(",");
            if(subjectCodeArr.length == 1){
                sql.append(" and t2.direction_idx like ?" + paramsNo);
                params.put(paramsNo, subjectCodeArr[0]+"%");
                paramsNo++;
            }else{
                for(int i = 0; i < subjectCodeArr.length; i++){
                    if(i == 0){
                        sql.append(" and (t2.direction_idx like ?" + paramsNo);
                        params.put(paramsNo, subjectCodeArr[i]+"%");
                        paramsNo++;
                    }else if(i == subjectCodeArr.length - 1){
                        sql.append(" or t2.direction_idx like ?" + paramsNo + " ) ");
                        params.put(paramsNo, subjectCodeArr[i]+"%");
                        paramsNo++;
                    }else{
                        sql.append(" or t2.direction_idx like ?" + paramsNo);
                        params.put(paramsNo, subjectCodeArr[i]+"%");
                        paramsNo++;
                    }
                }
            }
        }
        if(specialCode != null && !"".equals(specialCode)) {
            sql.append(" and t2.specialCode = ?" + paramsNo);
            params.put(paramsNo, specialCode);
            paramsNo++;
        }
        sql.append(" group by t2.direction_idx, t2.specialCode ");
        sql.append(" order by t2.direction_idx, t2.specialCode ");

        List list = voucherRepository.queryBySqlSC(sql.toString(), params);
        Map<String, Map<String, String>> resultMap = new HashMap<>();
        if(list != null && !list.isEmpty()){
            for(int i = 0; i < list.size(); i++){
                Map<String, String> map = (Map<String, String>) list.get(i);
                resultMap.put(map.get("itemCode") + "_" + map.get("specialCode"), map);
            }
        }
        return resultMap;
    }

    //获取会计期间的专项余额信息
    @SysPringLog(value = "根据科目汇总期初和期末") //添加日志
    private Map<String, Map<String, String>> getPeriodSubjectSpecialData(List centerCode, List branchCode, String accBookType, String accBookCode, String itemCode, List<String> specialCondition, String yearMonth) {
        String _yearMonth = yearMonth;
        /*boolean settleFlag = getSettleState(centerCode, accBookType, accBookCode, _yearMonth);//判断期间是否结转
        if(!settleFlag) _yearMonth = getClosestSettledYearMonth(centerCode, accBookType, accBookCode, _yearMonth);//获取最接近的结转期间*/
        Map<String, Map<String, String>> resultMap = new HashMap<>();
        StringBuffer sql = new StringBuffer("select accountBookCode,yearMonth,itemCode,specialCode,CAST(SUM(balanceQc) AS CHAR) AS balanceQc,CAST(SUM(debitBq) AS CHAR) as debitBq,CAST(SUM(creditBq) AS CHAR)  as creditBq,CAST(SUM(balanceQm) AS CHAR) as balanceQm,CAST(SUM(debitBn) AS CHAR) as debitBn,CAST(SUM(creditBn) AS CHAR) as creditBn from ( ");
        sql.append("select a.acc_book_code as accountBookCode,a.year_month_date as yearMonth,a.direction_idx as itemCode,substring_index(substring_index(a.direction_other,',',b.id+ 1),',' ,- 1) as specialCode,cast(a.balance_begin_dest as char) as balanceQc,cast(a.debit_dest as char) as debitBq,cast(a.credit_dest as char) as creditBq,cast(a.balance_dest as char) as balanceQm,cast(debit_dest_year as char) as debitBn,cast(credit_dest_year as char) as creditBn from (" +
                "select * from accarticlebalance ac where 1=1 and ac.center_code in (?1) and ac.branch_code  in (?2) and ac.acc_book_type = ?3 and ac.acc_book_code = ?4 and ac.year_month_date = ?5 ) a " +
                "join splitstringsort b ON b.id< (length(a.direction_other) - length(REPLACE (a.direction_other, ',', '')) + 1) " +
                " union all " +
                "select a.acc_book_code as accountBookCode,a.year_month_date as yearMonth,a.direction_idx as itemCode,substring_index(substring_index(a.direction_other,',',b.id+ 1),',' ,- 1) as specialCode,cast(a.balance_begin_dest as char) as balanceQc,cast(a.debit_dest as char) as debitBq,cast(a.credit_dest as char) as creditBq,cast(a.balance_dest as char) as balanceQm,cast(debit_dest_year as char) as debitBn,cast(credit_dest_year as char) as creditBn from (" +
                "select * from accarticlebalancehis ach where 1=1 and ach.center_code in (?1) and ach.branch_code in (?2)and ach.acc_book_type = ?3 and ach.acc_book_code = ?4 and ach.year_month_date = ?5 ) a " +
                "join splitstringsort b ON b.id< (length(a.direction_other) - length(REPLACE (a.direction_other, ',', '')) + 1) ");
        sql.append(") t where 1 = 1 ");

        Map<Integer, Object> params = new HashMap<>();
        params.put(1, centerCode);
        params.put(2, branchCode);
        params.put(3, accBookType);
        params.put(4, accBookCode);
        params.put(5, _yearMonth);
        int paramsNo = 6;

        if(itemCode != null) {
            sql.append(" and t.itemCode like ?" + paramsNo);
            params.put(paramsNo, itemCode+"%");
            paramsNo++;
        }
        if(specialCondition != null && specialCondition.size()>0) {
            sql.append(" and t.specialCode in ( ?" + paramsNo + " )");
            params.put(paramsNo, specialCondition);
            paramsNo++;
        }

        sql.append(" GROUP BY t.itemCode,t.specialCode order by t.itemCode, t.specialCode");

        List list = voucherRepository.queryBySqlSC(sql.toString(), params);
        if(list != null && !list.isEmpty()){
            //判断最接近结转期间与参数传入的期间比较，判断是否跨年
            boolean crossYear = !_yearMonth.substring(0,4).equals(yearMonth.substring(0,4));//false-不跨年，true-跨年
            for(int i = 0; i < list.size(); i++){
                Map<String, String> map = (Map<String, String>) list.get(i);
                if(crossYear){
                    map.put("debitBn", "0.00");
                    map.put("creditBn", "0.00");
                }
                resultMap.put(map.get("itemCode") + "_" + map.get("specialCode"), map);
            }
        }
        return resultMap;
    }

    //获取会计期间的专项余额信息
    @SysPringLog(value = "根据专项汇总期初和期末")
    private Map<String, Map<String, String>> getPeriodSpecialSubjectData(List centerCode, List branchCode, String accBookType, String accBookCode, String specialCode, String subjectCondition, String yearMonth) {
        String _yearMonth = yearMonth;
        /*boolean settleFlag = getSettleState(centerCode, accBookType, accBookCode, _yearMonth);//判断期间是否结转
        if(!settleFlag) _yearMonth = getClosestSettledYearMonth(centerCode, accBookType, accBookCode, _yearMonth);//获取最接近的结转期间*/
        Map<String, Map<String, String>> resultMap = new HashMap<>();
        StringBuffer sql = new StringBuffer("select accountBookCode,yearMonth,itemCode,specialCode,CAST(SUM(balanceQc) AS CHAR) AS balanceQc,CAST(SUM(debitBq) AS CHAR) AS debitBq,CAST(SUM(creditBq) AS CHAR)  AS creditBq,CAST(SUM(balanceQm) AS CHAR) AS balanceQm,CAST(SUM(debitBn) AS CHAR) AS debitBn,CAST(SUM(creditBn) AS CHAR) AS creditBn from ( ");
        sql.append("select a.acc_book_code as accountBookCode,a.year_month_date as yearMonth,a.direction_idx as itemCode,substring_index(substring_index(a.direction_other,',',b.id+ 1),',' ,- 1) as specialCode,cast(a.balance_begin_dest as char) as balanceQc,cast(a.debit_dest as char) as debitBq,cast(a.credit_dest as char) as creditBq,cast(a.balance_dest as char) as balanceQm,cast(debit_dest_year as char) as debitBn,cast(credit_dest_year as char) as creditBn from (" +
                "select * from accarticlebalance ac where 1=1 and ac.center_code in (?1) and ac.branch_code in (?2) and ac.acc_book_type = ?3 and ac.acc_book_code = ?4 and ac.year_month_date = ?5 ) a " +
                "join splitstringsort b ON b.id< (length(a.direction_other) - length(REPLACE (a.direction_other, ',', '')) + 1) " +
                " union all " +
                "select a.acc_book_code as accountBookCode,a.year_month_date as yearMonth,a.direction_idx as itemCode,substring_index(substring_index(a.direction_other,',',b.id+ 1),',' ,- 1) as specialCode,cast(a.balance_begin_dest as char) as balanceQc,cast(a.debit_dest as char) as debitBq,cast(a.credit_dest as char) as creditBq,cast(a.balance_dest as char) as balanceQm,cast(debit_dest_year as char) as debitBn,cast(credit_dest_year as char) as creditBn from (" +
                "select * from accarticlebalancehis ach where 1=1 and ach.center_code in (?1) and ach.branch_code in (?2) and ach.acc_book_type = ?3 and ach.acc_book_code = ?4 and ach.year_month_date = ?5 ) a " +
                "join splitstringsort b ON b.id< (length(a.direction_other) - length(REPLACE (a.direction_other, ',', '')) + 1) ");
        sql.append(") t where 1 = 1 ");

        Map<Integer, Object> params = new HashMap<>();
        params.put(1, centerCode);
        params.put(2, branchCode);
        params.put(3, accBookType);
        params.put(4, accBookCode);
        params.put(5, _yearMonth);
        int paramsNo = 6;

        if(subjectCondition != null && !"".equals(subjectCondition)){
            String[] subjectCodeArr = subjectCondition.split(",");
            if(subjectCodeArr.length == 1){
                sql.append(" and t.itemCode like ?" + paramsNo);
                params.put(paramsNo, subjectCodeArr[0]+"%");
                paramsNo++;
            }else{
                for(int i = 0; i < subjectCodeArr.length; i++){
                    if(i == 0){
                        sql.append(" and (t.itemCode like ?" + paramsNo);
                        params.put(paramsNo, subjectCodeArr[i]+"%");
                        paramsNo++;
                    }else if(i == subjectCodeArr.length - 1){
                        sql.append(" or t.itemCode like ?" + paramsNo + " ) ");
                        params.put(paramsNo, subjectCodeArr[i]+"%");
                        paramsNo++;
                    }else{
                        sql.append(" or t.itemCode like ?" + paramsNo);
                        params.put(paramsNo, subjectCodeArr[i]+"%");
                        paramsNo++;
                    }
                }
            }
        }
        if(specialCode != null && !"".equals(specialCode)) {
            sql.append(" and t.specialCode = ?" + paramsNo);
            params.put(paramsNo, specialCode);
            paramsNo++;
        }

        sql.append(" GROUP BY t.itemCode,t.specialCode order by t.specialCode, t.itemCode");

        List list = voucherRepository.queryBySqlSC(sql.toString(), params);
        if(list != null && !list.isEmpty()){
            //判断最接近结转期间与参数传入的期间比较，判断是否跨年
            boolean crossYear = !_yearMonth.substring(0,4).equals(yearMonth.substring(0,4));//false-不跨年，true-跨年
            for(int i = 0; i < list.size(); i++){
                Map<String, String> map = (Map<String, String>) list.get(i);
                if(crossYear){
                    map.put("debitBn", "0.00");
                    map.put("creditBn", "0.00");
                }
                resultMap.put(map.get("itemCode") + "_" + map.get("specialCode"), map);
            }
        }
        return resultMap;
    }

    //获取需要展示的科目及专项集合
    private List getSubjectSpecialList(List centerCode, List branchCode, String accBookType, String accBookCode, String itemCode, List<String> specialCondition, String startYearMonth, String endYearMonth, boolean chargeFlag,String specialNameP) {
        StringBuffer itemSql = new StringBuffer("select concat(t.itemCode,'_',t.specialCode) as itemSpecial,t.itemCode,t1.itemName,t.specialCode,t2.specialName,cast(sum(t.debitBq) as char) as debitBq,cast(sum(t.creditBq) as char) as creditBq from (");
        itemSql.append("select a.acc_book_code as accountBookCode,a.year_month_date as yearMonth,a.direction_idx as itemCode,substring_index(substring_index(a.direction_other,',',b.id+ 1),',' ,- 1) as specialCode,debit_dest as debitBq,credit_dest as creditBq from (" +
                "select ac.acc_book_code,ac.year_month_date,ac.direction_idx,ac.direction_other,ac.debit_dest,ac.credit_dest from accarticlebalance ac where 1=1 and ac.center_code  in (?1) and ac.branch_code in (?2) and ac.acc_book_type = ?3 and ac.acc_book_code = ?4 and ac.year_month_date >= ?5 and ac.year_month_date <= ?6 ) a " +
                "join splitstringsort b ON b.id< (length(a.direction_other) - length(REPLACE (a.direction_other, ',', '')) + 1) " +
                " union all " +
                "select a.acc_book_code as accountBookCode,a.year_month_date as yearMonth,a.direction_idx as itemCode,substring_index(substring_index(a.direction_other,',',b.id+ 1),',' ,- 1) as specialCode,debit_dest as debitBq,credit_dest as creditBq from (" +
                "select ach.acc_book_code,ach.year_month_date,ach.direction_idx,ach.direction_other,ach.debit_dest,ach.credit_dest from accarticlebalancehis ach where 1=1 and ach.center_code in (?1) and ach.branch_code in (?2) and ach.acc_book_type = ?3 and ach.acc_book_code = ?4 and ach.year_month_date >= ?5 and ach.year_month_date <= ?6 ) a " +
                "join splitstringsort b ON b.id< (length(a.direction_other) - length(REPLACE (a.direction_other, ',', '')) + 1) ");
        if(chargeFlag) itemSql.append(" union all " +
                "select t1.acc_book_code as accountBookCode,t1.year_month_date as yearMonth,t2.direction_idx as itemCode,t2.specialCode,t2.debit_dest as debitBq,t2.credit_dest as creditBq from (" +
                "select * from accmainvoucher am where 1=1 AND am.center_code in (?1) AND am.branch_code in (?2) AND am.acc_book_type = ?3 AND am.acc_book_code = ?4 AND am.year_month_date >= ?5 AND am.year_month_date <= ?6 ) t1 left join (" +
                "select a.voucher_no,a.direction_idx,substring_index(substring_index(a.direction_other,',',b.id+ 1),',' ,- 1) as specialCode,a.debit_dest,a.credit_dest from (" +
                "select * from accsubvoucher ac where 1=1 AND ac.center_code in (?1) AND ac.branch_code in (?2) AND ac.acc_book_type = ?3 AND ac.acc_book_code = ?4 AND ac.year_month_date >= ?5 AND ac.year_month_date <= ?6 ) a " +
                "join splitstringsort b ON b.id< (length(a.direction_other) - length(REPLACE (a.direction_other, ',', '')) + 1) " +
                ") t2 on t1.voucher_no = t2.voucher_no where t1.voucher_flag in ('1', '2') ");
        itemSql.append(" ) t ");
        itemSql.append(" left join (select s.account,concat(s.all_subject, s.subject_code, '/') as itemCode,s.subject_name as itemName from subjectinfo s where s.account = ?4 and s.end_flag = '0' and s.useflag) t1 on t1.itemCode = t.itemCode and t1.account = t.accountBookCode ");
        if(specialNameP.toString().equals("1")){
            itemSql.append(" left join (select sp.account,sp.special_code as specialCode,sp.special_namep as specialName from specialinfo sp where sp.account = ?4 AND sp.endflag = '0' and sp.useflag) t2 on t2.specialCode = t.specialCode and t2.account = t.accountBookCode ");
        }else {
            itemSql.append(" left join (select sp.account,sp.special_code as specialCode,sp.special_name as specialName from specialinfo sp where sp.account = ?4 AND sp.endflag = '0' and sp.useflag) t2 on t2.specialCode = t.specialCode and t2.account = t.accountBookCode ");
        }
        itemSql.append(" where 1 = 1 ");

        Map<Integer, Object> params = new HashMap<>();
        params.put(1, centerCode);
        params.put(2, branchCode);
        params.put(3, accBookType);
        params.put(4, accBookCode);
        params.put(5, startYearMonth);
        params.put(6, endYearMonth);
        int paramsNo = 7;

        if(itemCode != null && !"".equals(itemCode)) {
            itemSql.append(" and t.itemCode like ?" + paramsNo);
            params.put(paramsNo, itemCode+"%");
            paramsNo++;
        }
        if(specialCondition != null && specialCondition.size()>0) {
            itemSql.append(" and t.specialCode in ( ?" + paramsNo + " )");
            params.put(paramsNo, specialCondition);
            paramsNo++;
        }

        itemSql.append(" group by t.itemCode, t1.itemName, t.specialCode, t2.specialName ");
        itemSql.append(" order by t.itemCode, t.specialCode");

        return voucherRepository.queryBySqlSC(itemSql.toString(), params);
    }

    //获取需要展示的专项及科目集合
    private List getSpecialSubjectList(List centerCode, List branchCode, String accBookType, String accBookCode, String specialCode, String subjectCondition, String startYearMonth, String endYearMonth, boolean chargeFlag,String specialNameP) {
        StringBuffer specialItemSql = new StringBuffer("select concat(t.itemCode,'_',t.specialCode) as itemSpecial,t.itemCode,t1.itemName,t.specialCode,t2.specialName,cast(sum(t.debitBq) as char) as debitBq,cast(sum(t.creditBq) as char) as creditBq from (" +
                "select a.acc_book_code as accountBookCode,a.year_month_date as yearMonth,a.direction_idx as itemCode,substring_index(substring_index(a.direction_other,',',b.id+ 1),',' ,- 1) as specialCode,debit_dest as debitBq,credit_dest as creditBq from (" +
                "select * from accarticlebalance ac where 1 = 1 AND ac.center_code in (?1) AND ac.branch_code  in (?2) AND ac.acc_book_type = ?3 AND ac.acc_book_code = ?4 AND ac.year_month_date >= ?5 AND ac.year_month_date <= ?6 ) a " +
                "join splitstringsort b ON b.id < (length(a.direction_other) - length(REPLACE (a.direction_other, ',', '')) + 1) " +
                " union all " +
                "select a.acc_book_code as accountBookCode,a.year_month_date as yearMonth,a.direction_idx as itemCode,substring_index(substring_index(a.direction_other,',',b.id+ 1),',' ,- 1) as specialCode,debit_dest as debitBq,credit_dest as creditBq from (" +
                "select * from accarticlebalancehis ach where 1 = 1 AND ach.center_code  in (?1) AND ach.branch_code in (?2) AND ach.acc_book_type = ?3 AND ach.acc_book_code = ?4 AND ach.year_month_date >= ?5 AND ach.year_month_date <= ?6 ) a " +
                "join splitstringsort b ON b.id< (length(a.direction_other) - length(REPLACE (a.direction_other, ',', '')) + 1) ");
        if(chargeFlag) specialItemSql.append(" union all " +
                "select t1.acc_book_code as accountBookCode,t1.year_month_date as yearMonth,t2.direction_idx as itemCode,t2.specialCode,t2.debit_dest as debitBq,t2.credit_dest as creditBq from (" +
                "select * from accmainvoucher am where 1=1 and am.center_code  in (?1) and am.branch_code  in (?2) and am.acc_book_type = ?3 and am.acc_book_code = ?4 and am.year_month_date >= ?5 and am.year_month_date <= ?6 ) t1 left join (" +
                "select a.voucher_no,a.direction_idx,substring_index(substring_index(a.direction_other,',',b.id + 1),',' ,- 1) as specialCode,a.debit_dest,a.credit_dest from (" +
                "select * from accsubvoucher where 1=1 and center_code  in (?1) and branch_code  in (?2) and acc_book_type = ?3 and acc_book_code = ?4 and year_month_date >= ?5 and year_month_date <= ?6 ) a " +
                "join splitstringsort b ON b.id < (length(a.direction_other) - length(REPLACE (a.direction_other, ',', '')) + 1) " +
                ") t2 on t1.voucher_no = t2.voucher_no where t1.voucher_flag in ('1', '2') ");
        specialItemSql.append(" ) t ");
        specialItemSql.append(" left join (select s.account,concat(s.all_subject, s.subject_code, '/') as itemCode,s.subject_name as itemName from subjectinfo s where s.account = ?4 and s.end_flag = '0' and s.useflag = '1') t1 on t1.itemCode = t.itemCode and t1.account = t.accountBookCode ");
        if(specialNameP.toString().equals("1")){
            specialItemSql.append(" left join (select sp.account,sp.special_code as specialCode,sp.special_namep as specialName from specialinfo sp where sp.account = ?4 and sp.endflag = '0' and sp.useflag = '1') t2 on t2.specialCode = t.specialCode and t2.account = t.accountBookCode ");
        }else {
            specialItemSql.append(" left join (select sp.account,sp.special_code as specialCode,sp.special_name as specialName from specialinfo sp where sp.account = ?4 and sp.endflag = '0' and sp.useflag = '1') t2 on t2.specialCode = t.specialCode and t2.account = t.accountBookCode ");
        }
        specialItemSql.append(" where 1 = 1 ");

        Map<Integer, Object> params = new HashMap<>();
        params.put(1, centerCode);
        params.put(2, branchCode);
        params.put(3, accBookType);
        params.put(4, accBookCode);
        params.put(5, startYearMonth);
        params.put(6, endYearMonth);
        int paramsNo = 7;

        if(subjectCondition != null && !"".equals(subjectCondition)){
            String[] subjectCodeArr = subjectCondition.split(",");
            if(subjectCodeArr.length == 1){
                specialItemSql.append(" and t.itemCode like ?" + paramsNo);
                params.put(paramsNo, subjectCodeArr[0]+"%");
                paramsNo++;
            }else{
                for(int i = 0; i < subjectCodeArr.length; i++){
                    if(i == 0){
                        specialItemSql.append(" and (t.itemCode like ?" + paramsNo);
                        params.put(paramsNo, subjectCodeArr[i]+"%");
                        paramsNo++;
                    }else if(i == subjectCodeArr.length - 1){
                        specialItemSql.append(" or t.itemCode like ?" + paramsNo + " ) ");
                        params.put(paramsNo, subjectCodeArr[i]+"%");
                        paramsNo++;
                    }else{
                        specialItemSql.append(" or t.itemCode like ?" + paramsNo);
                        params.put(paramsNo, subjectCodeArr[i]+"%");
                        paramsNo++;
                    }
                }
            }
        }
        if(specialCode != null && !"".equals(specialCode)) {
            specialItemSql.append(" and t.specialCode = ?" + paramsNo);
            params.put(paramsNo, specialCode);
            paramsNo++;
        }

        specialItemSql.append(" group by t.itemCode, t1.itemName, t.specialCode, t2.specialName ");
        specialItemSql.append(" order by t.specialCode, t.itemCode ");

        return voucherRepository.queryBySqlSC(specialItemSql.toString(), params);
    }

    //判断会计期间是否结转
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

    //获取最接近的已结转期间
    //由于现在centerCode参数变成了list ,所以该方法需要返回最小的已结转期间 TODO
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

        return  count<Integer.parseInt(yearMonth) ? count+"" : yearMonth;//1-当前,2-未结转,3-已结转,4-未决算,5-已决算
    }

    private List ignoreSomeData(String cumulativeAmount, String accounRules, List result){
        int count1 = 0;//合计数
        int count2 = 0;//忽略数
        List resultNew = new ArrayList();
        if ("0".equals(cumulativeAmount)) {
            for (int i=0;i<result.size();i++) {
                if (i!=result.size()-1) {
                    Map<String, String> map = (Map) result.get(i);
                    String subjectCodeOrDetailCode = "1".equals(accounRules)?map.get("subjectCode"):map.get("detailCode");
                    if (subjectCodeOrDetailCode!=null && ("科目合计".equals(subjectCodeOrDetailCode) || "专项合计".equals(subjectCodeOrDetailCode))) {
                        if (!(count1==count2 && count1!=0)) {
                            resultNew.add(result.get(i));
                        }
                        count1 = 0;//合计数
                        count2 = 0;//忽略数
                        continue;
                    }
                    BigDecimal balanceQc = new BigDecimal(map.get("balanceQc"));
                    BigDecimal debitBq = new BigDecimal(map.get("debitBq"));
                    BigDecimal creditBq = new BigDecimal(map.get("creditBq"));
                    BigDecimal balanceQm = new BigDecimal(map.get("balanceQm"));
                    BigDecimal zero = new BigDecimal("0");
                    if (balanceQc.compareTo(zero)==0 && debitBq.compareTo(zero)==0 && creditBq.compareTo(zero)==0 && balanceQm.compareTo(zero)==0) {
                        count2++;
                    } else {
                        resultNew.add(result.get(i));
                    }
                    count1++;
                } else {
                    resultNew.add(result.get(i));
                }
            }
        } else if ("1".equals(cumulativeAmount)) {
            for (int i=0;i<result.size();i++) {
                if (i!=result.size()-1) {
                    Map<String, String> map = (Map) result.get(i);
                    String subjectCodeOrDetailCode = "1".equals(accounRules)?map.get("subjectCode"):map.get("detailCode");
                    if (subjectCodeOrDetailCode!=null && ("科目合计".equals(subjectCodeOrDetailCode) || "专项合计".equals(subjectCodeOrDetailCode))) {
                        if (!(count1==count2 && count1!=0)) {
                            resultNew.add(result.get(i));
                        }
                        count1 = 0;//合计数
                        count2 = 0;//忽略数
                        continue;
                    }
                    BigDecimal balanceQc = new BigDecimal(map.get("balanceQc"));
                    BigDecimal debitBq = new BigDecimal(map.get("debitBq"));
                    BigDecimal creditBq = new BigDecimal(map.get("creditBq"));
                    BigDecimal balanceQm = new BigDecimal(map.get("balanceQm"));
                    BigDecimal debitBn = new BigDecimal(map.get("debitBn"));
                    BigDecimal creditBn = new BigDecimal(map.get("creditBn"));
                    BigDecimal zero = new BigDecimal("0");
                    if (balanceQc.compareTo(zero)==0 && debitBq.compareTo(zero)==0 && creditBq.compareTo(zero)==0 && balanceQm.compareTo(zero)==0 && debitBn.compareTo(zero)==0 && creditBn.compareTo(zero)==0) {
                        count2++;
                    } else {
                        resultNew.add(result.get(i));
                    }
                    count1++;
                } else {
                    resultNew.add(result.get(i));
                }
            }
        }
        return  resultNew;
    }

    private List balanceLimit(String cumulativeAmount, VoucherDTO dto, List result){
        String moneyStart = dto.getMoneyStart();
        String moneyEnd = dto.getMoneyEnd();
        String sourceDirection = dto.getSourceDirection();//1-借方余额，2-贷方余额
        BigDecimal debitBqSum = new BigDecimal("0.00");
        BigDecimal creditBqSum = new BigDecimal("0.00");
        BigDecimal debitBnSum = new BigDecimal("0.00");
        BigDecimal creditBnSum = new BigDecimal("0.00");
        BigDecimal balanceQcSum = new BigDecimal("0.00");
        BigDecimal balanceQmSum = new BigDecimal("0.00");
        BigDecimal debitBqSum2 = new BigDecimal("0.00");
        BigDecimal creditBqSum2 = new BigDecimal("0.00");
        BigDecimal debitBnSum2 = new BigDecimal("0.00");
        BigDecimal creditBnSum2 = new BigDecimal("0.00");
        List resultNew = new ArrayList();

        if (moneyStart!=null&&!"".equals(moneyStart) && moneyEnd!=null&&!"".equals(moneyEnd)) {
            BigDecimal start = new BigDecimal(moneyStart);
            BigDecimal end = new BigDecimal(moneyEnd);
            int count1 = 0;//保留数
            int count2 = 0;//总计保留数
            for (int i=0;i<result.size();i++) {
                Map<String, String> map = (Map) result.get(i);
                if (i!=result.size()-1) {
                    String subjectCodeOrDetailCode = "1".equals(dto.getAccounRules())?map.get("subjectCode"):map.get("detailCode");
                    if (subjectCodeOrDetailCode!=null && ("科目合计".equals(subjectCodeOrDetailCode) || "专项合计".equals(subjectCodeOrDetailCode))) {
                        //当前为科目或专项合计项
                        if (count1!=0) {
                            map.put("debitBq", debitBqSum.toString());
                            map.put("creditBq", creditBqSum.toString());
                            map.put("balanceQc", balanceQcSum.abs().toString());
                            map.put("balanceQm", balanceQmSum.abs().toString());
                            map.put("directionQc", balanceQcSum.compareTo(BigDecimal.ZERO)>0?"借":balanceQcSum.compareTo(BigDecimal.ZERO)<0?"贷":"平");
                            map.put("directionQm", balanceQmSum.compareTo(BigDecimal.ZERO)>0?"借":balanceQmSum.compareTo(BigDecimal.ZERO)<0?"贷":"平");
                            // cumulativeAmount 显示累计发生额（0-否 1-是）
                            if ("1".equals(cumulativeAmount)) {
                                map.put("debitBn", debitBnSum.toString());
                                map.put("creditBn", creditBnSum.toString());
                            }
                            resultNew.add(map);
                        }

                        debitBqSum2 = debitBqSum2.add(debitBqSum);
                        creditBqSum2 = creditBqSum2.add(creditBqSum);
                        debitBnSum2 = debitBnSum2.add(debitBnSum);
                        creditBnSum2 = creditBnSum2.add(creditBnSum);

                        debitBqSum = new BigDecimal("0.00");
                        creditBqSum = new BigDecimal("0.00");
                        debitBnSum = new BigDecimal("0.00");
                        creditBnSum = new BigDecimal("0.00");
                        balanceQcSum = new BigDecimal("0.00");
                        balanceQmSum = new BigDecimal("0.00");

                        count1 = 0;
                        continue;
                    }

                    BigDecimal balanceQm = new BigDecimal(map.get("balanceQm"));
                    String directionQm = map.get("directionQm");
                    // sourceDirection 1-借方余额，2-贷方余额
                    boolean flag = true;
                    if ("1".equals(sourceDirection)) {
                        flag = "借".equals(directionQm) || "平".equals(directionQm);
                    } else if ("2".equals(sourceDirection)) {
                        flag = "贷".equals(directionQm) || "平".equals(directionQm);
                    }
                    if (flag && balanceQm.compareTo(start)>=0 && balanceQm.compareTo(end)<=0) {
                        resultNew.add(result.get(i));
                        count1++;
                        count2++;

                        debitBqSum = debitBqSum.add(new BigDecimal(map.get("debitBq")));
                        creditBqSum = creditBqSum.add(new BigDecimal(map.get("creditBq")));
                        debitBnSum = debitBnSum.add(new BigDecimal(map.get("debitBn")!=null?map.get("debitBn"):"0"));
                        creditBnSum = creditBnSum.add(new BigDecimal(map.get("creditBn")!=null?map.get("creditBn"):"0"));
                        if ("借".equals(map.get("directionQc"))) {
                            balanceQcSum = balanceQcSum.add(new BigDecimal(map.get("balanceQc")));
                        } else if ("贷".equals(map.get("directionQc"))){
                            balanceQcSum = balanceQcSum.subtract(new BigDecimal(map.get("balanceQc")));
                        }
                        if ("借".equals(directionQm)) {
                            balanceQmSum = balanceQmSum.add(new BigDecimal(map.get("balanceQm")));
                        } else if ("贷".equals(directionQm)){
                            balanceQmSum = balanceQmSum.subtract(new BigDecimal(map.get("balanceQm")));
                        }
                    }
                } else {
                    //当前为最后一条合计项
                    if (count2!=0) {
                        map.put("debitBq", debitBqSum2.toString());
                        map.put("creditBq", creditBqSum2.toString());
                        // cumulativeAmount 显示累计发生额（0-否 1-是）
                        if ("1".equals(cumulativeAmount)) {
                            map.put("debitBn", debitBnSum2.toString());
                            map.put("creditBn", creditBnSum2.toString());
                        }
                        resultNew.add(map);
                    }
                }
            }
        } else if (moneyStart!=null&&!"".equals(moneyStart)) {
            BigDecimal start = new BigDecimal(moneyStart);
            int count1 = 0;//保留数
            int count2 = 0;//总计保留数
            for (int i=0;i<result.size();i++) {
                Map<String, String> map = (Map) result.get(i);
                if (i!=result.size()-1) {
                    String subjectCodeOrDetailCode = "1".equals(dto.getAccounRules())?map.get("subjectCode"):map.get("detailCode");
                    if (subjectCodeOrDetailCode!=null && ("科目合计".equals(subjectCodeOrDetailCode) || "专项合计".equals(subjectCodeOrDetailCode))) {
                        //当前为科目或专项合计项
                        if (count1!=0) {
                            map.put("debitBq", debitBqSum.toString());
                            map.put("creditBq", creditBqSum.toString());
                            map.put("balanceQc", balanceQcSum.abs().toString());
                            map.put("balanceQm", balanceQmSum.abs().toString());
                            map.put("directionQc", balanceQcSum.compareTo(BigDecimal.ZERO)>0?"借":balanceQcSum.compareTo(BigDecimal.ZERO)<0?"贷":"平");
                            map.put("directionQm", balanceQmSum.compareTo(BigDecimal.ZERO)>0?"借":balanceQmSum.compareTo(BigDecimal.ZERO)<0?"贷":"平");
                            // cumulativeAmount 显示累计发生额（0-否 1-是）
                            if ("1".equals(cumulativeAmount)) {
                                map.put("debitBn", debitBnSum.toString());
                                map.put("creditBn", creditBnSum.toString());
                            }
                            resultNew.add(map);
                        }

                        debitBqSum2 = debitBqSum2.add(debitBqSum);
                        creditBqSum2 = creditBqSum2.add(creditBqSum);
                        debitBnSum2 = debitBnSum2.add(debitBnSum);
                        creditBnSum2 = creditBnSum2.add(creditBnSum);

                        debitBqSum = new BigDecimal("0.00");
                        creditBqSum = new BigDecimal("0.00");
                        debitBnSum = new BigDecimal("0.00");
                        creditBnSum = new BigDecimal("0.00");
                        balanceQcSum = new BigDecimal("0.00");
                        balanceQmSum = new BigDecimal("0.00");

                        count1 = 0;
                        continue;
                    }

                    BigDecimal balanceQm = new BigDecimal(map.get("balanceQm"));
                    String directionQm = map.get("directionQm");
                    // sourceDirection 1-借方余额，2-贷方余额
                    boolean flag = true;
                    if ("1".equals(sourceDirection)) {
                        flag = "借".equals(directionQm) || "平".equals(directionQm);
                    } else if ("2".equals(sourceDirection)) {
                        flag = "贷".equals(directionQm) || "平".equals(directionQm);
                    }
                    if (flag && balanceQm.compareTo(start)>=0) {
                        resultNew.add(result.get(i));
                        count1++;
                        count2++;

                        debitBqSum = debitBqSum.add(new BigDecimal(map.get("debitBq")));
                        creditBqSum = creditBqSum.add(new BigDecimal(map.get("creditBq")));
                        debitBnSum = debitBnSum.add(new BigDecimal(map.get("debitBn")!=null?map.get("debitBn"):"0"));
                        creditBnSum = creditBnSum.add(new BigDecimal(map.get("creditBn")!=null?map.get("creditBn"):"0"));
                        if ("借".equals(map.get("directionQc"))) {
                            balanceQcSum = balanceQcSum.add(new BigDecimal(map.get("balanceQc")));
                        } else if ("贷".equals(map.get("directionQc"))){
                            balanceQcSum = balanceQcSum.subtract(new BigDecimal(map.get("balanceQc")));
                        }
                        if ("借".equals(directionQm)) {
                            balanceQmSum = balanceQmSum.add(new BigDecimal(map.get("balanceQm")));
                        } else if ("贷".equals(directionQm)){
                            balanceQmSum = balanceQmSum.subtract(new BigDecimal(map.get("balanceQm")));
                        }
                    }
                } else {
                    //当前为最后一条合计项
                    if (count2!=0) {
                        map.put("debitBq", debitBqSum2.toString());
                        map.put("creditBq", creditBqSum2.toString());
                        // cumulativeAmount 显示累计发生额（0-否 1-是）
                        if ("1".equals(cumulativeAmount)) {
                            map.put("debitBn", debitBnSum2.toString());
                            map.put("creditBn", creditBnSum2.toString());
                        }
                        resultNew.add(map);
                    }
                }
            }
        } else if (moneyEnd!=null&&!"".equals(moneyEnd)) {
            BigDecimal end = new BigDecimal(moneyEnd);
            int count1 = 0;//保留数
            int count2 = 0;//总计保留数
            for (int i=0;i<result.size();i++) {
                Map<String, String> map = (Map) result.get(i);
                if (i!=result.size()-1) {
                    String subjectCodeOrDetailCode = "1".equals(dto.getAccounRules())?map.get("subjectCode"):map.get("detailCode");
                    if (subjectCodeOrDetailCode!=null && ("科目合计".equals(subjectCodeOrDetailCode) || "专项合计".equals(subjectCodeOrDetailCode))) {
                        //当前为科目或专项合计项
                        if (count1!=0) {
                            map.put("debitBq", debitBqSum.toString());
                            map.put("creditBq", creditBqSum.toString());
                            map.put("balanceQc", balanceQcSum.abs().toString());
                            map.put("balanceQm", balanceQmSum.abs().toString());
                            map.put("directionQc", balanceQcSum.compareTo(BigDecimal.ZERO)>0?"借":balanceQcSum.compareTo(BigDecimal.ZERO)<0?"贷":"平");
                            map.put("directionQm", balanceQmSum.compareTo(BigDecimal.ZERO)>0?"借":balanceQmSum.compareTo(BigDecimal.ZERO)<0?"贷":"平");
                            // cumulativeAmount 显示累计发生额（0-否 1-是）
                            if ("1".equals(cumulativeAmount)) {
                                map.put("debitBn", debitBnSum.toString());
                                map.put("creditBn", creditBnSum.toString());
                            }
                            resultNew.add(map);
                        }

                        debitBqSum2 = debitBqSum2.add(debitBqSum);
                        creditBqSum2 = creditBqSum2.add(creditBqSum);
                        debitBnSum2 = debitBnSum2.add(debitBnSum);
                        creditBnSum2 = creditBnSum2.add(creditBnSum);

                        debitBqSum = new BigDecimal("0.00");
                        creditBqSum = new BigDecimal("0.00");
                        debitBnSum = new BigDecimal("0.00");
                        creditBnSum = new BigDecimal("0.00");
                        balanceQcSum = new BigDecimal("0.00");
                        balanceQmSum = new BigDecimal("0.00");

                        count1 = 0;
                        continue;
                    }

                    BigDecimal balanceQm = new BigDecimal(map.get("balanceQm"));
                    String directionQm = map.get("directionQm");
                    // sourceDirection 1-借方余额，2-贷方余额
                    boolean flag = true;
                    if ("1".equals(sourceDirection)) {
                        flag = "借".equals(directionQm) || "平".equals(directionQm);
                    } else if ("2".equals(sourceDirection)) {
                        flag = "贷".equals(directionQm) || "平".equals(directionQm);
                    }
                    if (flag && balanceQm.compareTo(end)<=0) {
                        resultNew.add(result.get(i));
                        count1++;
                        count2++;

                        debitBqSum = debitBqSum.add(new BigDecimal(map.get("debitBq")));
                        creditBqSum = creditBqSum.add(new BigDecimal(map.get("creditBq")));
                        debitBnSum = debitBnSum.add(new BigDecimal(map.get("debitBn")!=null?map.get("debitBn"):"0"));
                        creditBnSum = creditBnSum.add(new BigDecimal(map.get("creditBn")!=null?map.get("creditBn"):"0"));
                        if ("借".equals(map.get("directionQc"))) {
                            balanceQcSum = balanceQcSum.add(new BigDecimal(map.get("balanceQc")));
                        } else if ("贷".equals(map.get("directionQc"))){
                            balanceQcSum = balanceQcSum.subtract(new BigDecimal(map.get("balanceQc")));
                        }
                        if ("借".equals(directionQm)) {
                            balanceQmSum = balanceQmSum.add(new BigDecimal(map.get("balanceQm")));
                        } else if ("贷".equals(directionQm)){
                            balanceQmSum = balanceQmSum.subtract(new BigDecimal(map.get("balanceQm")));
                        }
                    }
                } else {
                    //当前为最后一条合计项

                }
            }
        } else {
            resultNew = result;
        }
        return resultNew;
    }
}
