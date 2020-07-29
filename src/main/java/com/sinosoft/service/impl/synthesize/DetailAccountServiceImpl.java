package com.sinosoft.service.impl.synthesize;

import com.sinosoft.common.CurrentTime;
import com.sinosoft.common.CurrentUser;
import com.sinosoft.domain.SubjectInfo;
import com.sinosoft.domain.account.AccMonthTrace;
import com.sinosoft.domain.account.AccMonthTraceId;
import com.sinosoft.dto.VoucherDTO;
import com.sinosoft.repository.BranchInfoRepository;
import com.sinosoft.repository.VoucherRepository;
import com.sinosoft.repository.account.AccMonthTraceRespository;
import com.sinosoft.service.VoucherService;
import com.sinosoft.service.synthesize.DetailAccountService;
import com.sinosoft.util.CommonUtil;
import com.sinosoft.util.ExcelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.*;

@Service
public class DetailAccountServiceImpl implements DetailAccountService {
    private Logger logger = LoggerFactory.getLogger(DetailAccountServiceImpl.class);
    @Resource
    private VoucherRepository voucherRepository;
    @Resource
    private AccMonthTraceRespository accMonthTraceRespository;
    @Resource
    private VoucherService voucherService;
    @Value("${MODELPath}")
    private String MODELPath ;
    @Resource
    BranchInfoRepository branchInfoRepository;

    /*
        导出操作，为避免重复查询，临时存储校验查询结果，此结果即为导出数据，否则不可用
        key = 用户ID + 下划线 + 当前机构 + 下划线 + 当前核算单位 + 下划线 + 当前登录账套类型 + 下划线 + 当前登录账套编码
     */
    private Map<String, Object> exportDataMap;

    /**
     * 明细账查询
     * @param dto
     * @param synthDetailAccount 综合明细账（0-否 1-是）
     * @return
     */
    @Override
    public List<?> queryDetailAccount(VoucherDTO dto, String synthDetailAccount,List  centerCode){
        long time = System.currentTimeMillis();

        List<?> list = new ArrayList<>();
        Map<String, Object> specialNameMap = new HashMap<>();
        if ((dto.getSpecialNameP()!=null&&"0".equals(dto.getSpecialNameP())) && (dto.getSpecialCode()!=null&&!"".equals(dto.getSpecialCode())) && (dto.getSpecialName()!=null&&!"".equals(dto.getSpecialName()))) {
            String[] codes = dto.getSpecialCode().split(",");
            String[] names = dto.getSpecialName().split(",");
            for (int i=0;i<codes.length;i++) {
                specialNameMap.put(codes[i], names[i]);
            }
        }

        StringBuffer itemCode2Sql = new StringBuffer("SELECT CONCAT_WS('',s.all_subject,s.subject_code) AS subjectCodeAll FROM subjectinfo s WHERE 1=1");

        int paramsNo = 1;
        Map<Integer, Object> params = new HashMap<>();

        itemCode2Sql.append(" AND s.account = ?" + paramsNo);
        params.put(paramsNo, CurrentUser.getCurrentLoginAccount());
        paramsNo++;
        itemCode2Sql.append(" AND CONCAT_WS('',s.all_subject,s.subject_code) LIKE ?" + paramsNo + " ORDER BY subjectCodeAll DESC LIMIT 1");
        params.put(paramsNo, dto.getItemCode2()+"%");
        paramsNo++;

        List<?> itemCode2List = voucherRepository.queryBySqlSC(itemCode2Sql.toString(), params);
        if (itemCode2List!=null&&itemCode2List.size()>0) {
            dto.setItemCode2((String) ((Map) itemCode2List.get(0)).get("subjectCodeAll"));
        }

        if (dto.getSubjectCode()!=null&&!"".equals(dto.getSubjectCode())) {
            //按对方科目查询处理，同时考虑排序规则（按凭证/科目）
            list = queryDetailAccountByOppositeSubject(dto, specialNameMap,centerCode);
            System.out.println("按对方科目查询处理");
        } else if (synthDetailAccount!=null&&"1".equals(synthDetailAccount)) {
            //按综合明细账查询排序处理（不考虑排序规则）
            list = querySynthDetailAccount(dto, specialNameMap,centerCode);
            System.out.println("按综合明细账查询排序处理");
        } else {
            //按凭证排序或者科目排序查询处理
            list = queryDetailAccountByVoucherNoOrSubject(dto, specialNameMap,centerCode);
            System.out.println("按凭证或科目排序查询处理查询排序处理");
        }
        System.out.println("明细账查询用时："+(System.currentTimeMillis()-time)+" ms");
        return list;
    }


    /**
     * 明细账查询
     * @param page
     * @param rows
     * @param dto
     * @param synthDetailAccount 综合明细账（0-否 1-是）
     * @return
     */
    @Override
    public Page<?> queryDetailAccountByPage(int page, int rows, VoucherDTO dto, String synthDetailAccount){
        List<String> subBranch = getSubBranch();
        List<?> list = new ArrayList<>();
        list = queryDetailAccount(dto, synthDetailAccount,subBranch);
        List<Object> pageList = new ArrayList<>();
        //开始截取需要的数据
        //开始位置，下标从0开始（每页开始的位置就是之前页的条数之和）
        int index = (page-1)*rows;
        //数据总条数
        int listSize = list.size();
        if (listSize<=rows) {
            //数据总条数不超过每页显示的行数
            pageList = (List<Object>) list;
        } else {//数据总条数超过每页显示的行数
            int indexEnd = index+rows;//按每页足数显示时，结束位置下标
            if (indexEnd+1>listSize) {
                //当前页的取数范围超过总数据范围
                for (int i=0;i<rows;i++) {
                    if (index>=listSize) {
                        break;
                    } else {
                        pageList.add(list.get(index));
                    }
                    index++;
                }
            } else {//当前页的取数范围未超过总数据范围
                for (int i=0;i<rows;i++) {
                    pageList.add(list.get(index));
                    index++;
                }
            }
        }

        Page<?> res = new PageImpl<>(pageList, new PageRequest(page-1, rows, null), listSize);
        return res;
    }

    /**
     * 获取汇总机构的全部子机构，不是汇总机构的返回本身
     * @return
     */
    public List<String> getSubBranch() {
        List<String> subBranch = new ArrayList<>();
        String centerCode1 = CurrentUser.getCurrentLoginManageBranch();
        //判断是否为汇总机构
        List<String>  summaryBranch = new ArrayList();
        summaryBranch = branchInfoRepository.findByLevel("1");
        if(summaryBranch.contains(centerCode1)){
            subBranch = branchInfoRepository.findBySuperCom(centerCode1);
        }else{
            subBranch.add(centerCode1);
        }
        return subBranch;
    }

    public List<?> queryDetailAccountByOppositeSubject(VoucherDTO dto, Map<String, Object> specialNameMap,List centerCode){

        List branchCode = centerCode;
        String accBookType = CurrentUser.getCurrentLoginAccountType();
        String accBookCode = CurrentUser.getCurrentLoginAccount();
        List<Object> result = new ArrayList<>();

        StringBuffer sql = new StringBuffer("SELECT am.voucher_date AS voucherDate,a.year_month_date AS yearMonthDate,a.voucher_no AS voucherNo,a.suffix_no AS suffixNo,a.direction_idx AS directionIdx,a.direction_idx_name AS directionIdxName,a.direction_other AS directionOther,a.remark AS remarkName,a.debit_dest AS debitDest,a.credit_dest AS creditDest,(a.debit_dest-a.credit_dest) AS balance,am.center_code AS centerCode FROM accsubvoucher a LEFT JOIN accmainvoucher am ON am.center_code=a.center_code AND am.branch_code=a.branch_code AND am.acc_book_type=a.acc_book_type AND am.acc_book_code=a.acc_book_code AND am.year_month_date=a.year_month_date AND am.voucher_no=a.voucher_no WHERE 1=1");

        int paramsNo = 1;
        Map<Integer, Object> params = new HashMap<>();

        sql.append(" AND a.center_code in (?" + paramsNo+")");
        params.put(paramsNo, centerCode);
        paramsNo++;
        sql.append(" AND a.branch_code in (?" + paramsNo+")");
        params.put(paramsNo, branchCode);
        paramsNo++;
        sql.append(" AND a.acc_book_type = ?" + paramsNo);
        params.put(paramsNo, accBookType);
        paramsNo++;
        sql.append(" AND a.acc_book_code = ?" + paramsNo);
        params.put(paramsNo, accBookCode);
        paramsNo++;

        if (dto.getYearMonth()!=null&&!"".equals(dto.getYearMonth())) {
            sql.append(" AND a.year_month_date >= ?" + paramsNo);
            params.put(paramsNo, dto.getYearMonth());
            paramsNo++;
        }
        if (dto.getYearMonthDate()!=null&&!"".equals(dto.getYearMonthDate())) {
            sql.append(" AND a.year_month_date <= ?" + paramsNo);
            params.put(paramsNo, dto.getYearMonthDate());
            paramsNo++;
        }
        sql.append(" AND a.direction_idx IN (SELECT CONCAT_WS('',s.all_subject,s.subject_code,'/') AS subjectCode FROM subjectinfo s WHERE 1=1 AND s.account = a.acc_book_code");
        if (dto.getItemCode1()!=null&&!"".equals(dto.getItemCode1())) {
            sql.append(" AND CONCAT_WS('',s.all_subject,s.subject_code) >= ?" + paramsNo);
            params.put(paramsNo, dto.getItemCode1());
            paramsNo++;
        }
        /*sql.append(" AND CONCAT_WS('',s.all_subject,s.subject_code) <= (SELECT CONCAT_WS('',ss.all_subject,ss.subject_code) AS subjectCodeAll FROM subjectinfo ss WHERE 1=1 AND ss.account = s.account");
        if (dto.getItemCode2()!=null&&!"".equals(dto.getItemCode2())) {
            sql.append(" AND CONCAT_WS('',ss.all_subject,ss.subject_code) LIKE '"+dto.getItemCode2()+"%'");
        }
        sql.append(" ORDER BY subjectCodeAll DESC LIMIT 1)");*/
        if (dto.getItemCode2()!=null&&!"".equals(dto.getItemCode2())) {
            sql.append(" AND CONCAT_WS('',s.all_subject,s.subject_code) <= ?" + paramsNo);
            params.put(paramsNo, dto.getItemCode2());
            paramsNo++;
        }
        if (dto.getLevel()!=null&&!"".equals(dto.getLevel())) {
            sql.append(" AND s.level >= ?" + paramsNo);
            params.put(paramsNo, dto.getLevel());
            paramsNo++;
        }
        if (dto.getLevelEnd()!=null&&!"".equals(dto.getLevelEnd())) {
            sql.append(" AND s.level <= ?" + paramsNo);
            params.put(paramsNo, dto.getLevelEnd());
            paramsNo++;
        }
        sql.append(" ORDER BY subjectCode)");
        if (dto.getMoneyStart()!=null&&!"".equals(dto.getMoneyStart())) {
            sql.append(" AND (CASE a.debit_dest WHEN 0.00 THEN a.credit_dest ELSE a.debit_dest END)>= ?" + paramsNo);
            params.put(paramsNo, dto.getMoneyStart());
            paramsNo++;
        }
        if (dto.getMoneyEnd()!=null&&!"".equals(dto.getMoneyEnd())) {
            sql.append(" AND (CASE a.debit_dest WHEN 0.00 THEN a.credit_dest ELSE a.debit_dest END)<= ?" + paramsNo);
            params.put(paramsNo, dto.getMoneyEnd());
            paramsNo++;
        }
        if (dto.getVoucherNoStart()!=null&&!"".equals(dto.getVoucherNoStart())) {
            sql.append(" AND a.voucher_no >= ?" + paramsNo);
            params.put(paramsNo, dto.getVoucherNoStart());
            paramsNo++;
        }
        if (dto.getVoucherNoEnd()!=null&&!"".equals(dto.getVoucherNoEnd())) {
            sql.append(" AND a.voucher_no <= ?" + paramsNo);
            params.put(paramsNo, dto.getVoucherNoEnd());
            paramsNo++;
        }
        if (dto.getVoucherDateStart()!=null&&!"".equals(dto.getVoucherDateStart())) {
            sql.append(" AND am.voucher_date >= ?" + paramsNo);
            params.put(paramsNo, dto.getVoucherDateStart());
            paramsNo++;
        }
        if (dto.getVoucherDateEnd()!=null&&!"".equals(dto.getVoucherDateEnd())) {
            sql.append(" AND am.voucher_date <= ?" + paramsNo);
            params.put(paramsNo, dto.getVoucherDateEnd());
            paramsNo++;
        }
        if (dto.getSubjectCode()!=null&&!"".equals(dto.getSubjectCode())) {
            sql.append(" AND a.direction_idx LIKE ?" + paramsNo);
            params.put(paramsNo, dto.getSubjectCode()+"%");
            paramsNo++;
        }
        if (dto.getRemarkName()!=null&&!"".equals(dto.getRemarkName())) {
            sql.append(" AND a.remark LIKE ?" + paramsNo);
            params.put(paramsNo, "%"+dto.getRemarkName()+"%");
            paramsNo++;
        }
        if (dto.getSpecialName()!=null&&!"".equals(dto.getSpecialName())) {
            //拼接明细对象SQL（OR）
            jointQuertSqlBySpecialCodes(sql, dto, "a", params, paramsNo);
            if (paramsNo != params.size()+1) { paramsNo = params.size()+1; }
        }
        if (dto.getVoucherGene()!=null&&!"".equals(dto.getVoucherGene())) {
            if ("0".equals(dto.getVoucherGene())) {
                sql.append(" AND am.voucher_flag = '3'");
            } else {
                sql.append(" AND am.voucher_flag in ('1','2','3')");
            }
        }

        //联查历史表
        String sqlStr = sql.toString();
        sqlStr = sqlStr.replaceAll("accsubvoucher","accsubvoucherhis");
        sqlStr = sqlStr.replaceAll("a\\.","ah\\.");
        sqlStr = sqlStr.replaceAll(" a "," ah ");
        sqlStr = sqlStr.replaceAll("accmainvoucher","accmainvoucherhis");
        sqlStr = sqlStr.replaceAll("am\\.","amh\\.");
        sqlStr = sqlStr.replaceAll(" am "," amh ");

        sql.append(" UNION ALL ");
        sql.append(sqlStr);

        if (dto.getOrderingRule()!=null&&"1".equals(dto.getOrderingRule())) {
            //按凭证排序
            sql.append(" ORDER BY voucherNo,directionIdx,suffixNo");
        } else {
            //按科目排序
            sql.append(" ORDER BY directionIdx,yearMonthDate,voucherNo,suffixNo");
        }

        List<?> list =  voucherRepository.queryBySqlSC(sql.toString(), params);
        if (list!=null&&list.size()>0) {
            BigDecimal balanceBeginDest = new BigDecimal("0.00");
            String direction = "";
            BigDecimal debitDestSum = new BigDecimal("0.00");
            BigDecimal creditDestSum = new BigDecimal("0.00");

            //先查询出对方科目的余额情况（上月的期末、科目余额方向）
            String lastYearMonth = getLastYearMonth(dto.getYearMonth());//上一个会计期间
            //如此会计期间已结转侧需要替换表名
            String tableName = "accdetailbalance";
            if (whetherCarryForward(centerCode, accBookType, accBookCode, lastYearMonth)) {
                tableName = "accdetailbalancehis";
            }
            StringBuffer balanceSql = new StringBuffer("SELECT s.subject_name AS subjectName,s.direction AS direction,(");
            balanceSql.append(" SELECT IFNULL(SUM(a.balance_dest),0.00) FROM "+tableName+" a WHERE 1=1");

            paramsNo = 1;
            params = new HashMap<>();

            balanceSql.append(" AND a.center_code in (?" + paramsNo+")");
            params.put(paramsNo, centerCode);
            paramsNo++;
            balanceSql.append(" AND a.branch_code in (?" + paramsNo+")");
            params.put(paramsNo, branchCode);
            paramsNo++;
            balanceSql.append(" AND a.acc_book_type = ?" + paramsNo);
            params.put(paramsNo, accBookType);
            paramsNo++;
            balanceSql.append(" AND a.acc_book_code = s.account");

            balanceSql.append(" AND a.year_month_date = ?" + paramsNo);
            params.put(paramsNo, lastYearMonth);
            paramsNo++;

            balanceSql.append(jointDirectionIdxSqlBySubjectCode(dto.getSubjectCode(), "a", params, paramsNo));
            if (paramsNo != params.size()+1) { paramsNo = params.size()+1; }

            balanceSql.append(" )");
            balanceSql.append(" AS balanceBeginDest FROM subjectinfo s WHERE 1=1");

            balanceSql.append(" AND s.account = ?" + paramsNo);
            params.put(paramsNo, accBookCode);
            paramsNo++;

            String string = dto.getSubjectCode();
            if ("/".equals(string.substring(string.length()-1))) {
                string = string.substring(0,string.length()-1);
            }
            balanceSql.append(" AND CONCAT_WS('',s.all_subject,s.subject_code) = ?" + paramsNo);
            params.put(paramsNo, string);
            paramsNo++;

            List<?> balanceSqlList = voucherRepository.queryBySqlSC(balanceSql.toString(), params);
            //初始化余额数据
            if (balanceSqlList!=null&&balanceSqlList.size()>0) {
                Map m = (Map) balanceSqlList.get(0);
                balanceBeginDest = (BigDecimal) m.get("balanceBeginDest");
                direction = (String) m.get("direction");
            }

            Map<String, Object> subjectNameMap = new HashMap<>();
            for (Object obj : list) {
                Map<String, Object> dataMap = (Map) obj;
                debitDestSum = debitDestSum.add((BigDecimal) dataMap.get("debitDest"));
                creditDestSum = creditDestSum.add((BigDecimal) dataMap.get("creditDest"));
                balanceBeginDest = balanceBeginDest.add((BigDecimal) dataMap.get("balance"));

                //setRowData(Map<String, Object> rowMap, Map<String, Object> dataMap, VoucherDTO dto, Map<String, Object> specialNameMap, String direction, BigDecimal balance)
                Map<String, Object> rowMap = new HashMap<>();
                setRowData(rowMap, dataMap, dto, specialNameMap, direction, balanceBeginDest);
                result.add(rowMap);
            }

            Map<String, Object> amount = new HashMap<>();
            amount.put("remarkName", "合计");
            amount.put("debitDest", debitDestSum.toString());
            amount.put("creditDest", creditDestSum.toString());
            result.add(amount);
        }
        return result;
    }

    public List<?> querySynthDetailAccount(VoucherDTO dto, Map<String, Object> specialNameMap,List centerCode){

        List branchCode = centerCode;
        String accBookType = CurrentUser.getCurrentLoginAccountType();
        String accBookCode = CurrentUser.getCurrentLoginAccount();
        List<Object> result = new ArrayList<>();
        /*
            1.通过科目起止代码和起止层级获得科目范围（范围内的均需要显示，无论是否、是否发生），获取科目全代码、科目余额方向、科目末级、科目名称(全级/本身)
            2.再循环科目数据，通过拆分科目代码，
                2.1 获取科目期初余额、借贷本年累计
                2.2 按照一定的规则获取凭证数据
                    2.2.1 非末级科目，只显示期初余额、本月合计、本年累计项（期初余额同样需要逐月显示）
                    2.2.2 末级科目，若有凭证发生还需显示明细信息
            3.将所得数据，处理到最终返回结果中
         */
        //1.通过科目起止代码和起止层级获得科目范围,获取科目全代码、科目余额方向、科目名称
        //needSubjectList : Map 中 subjectCode 为科目代码, direction 科目余额方向, endFlag 为科目末级标志, subjectName 为科目名称（末级名称/全级名称）
        List<Map<String, String>> needSubjectList = new ArrayList<>();
        setNeedSubjectList(needSubjectList, dto, accBookCode);

        if (needSubjectList.size()>0) {
            //2.
            String lastYearMonth = getLastYearMonth(dto.getYearMonth());//上一个会计期间

            //如此会计期间已结转侧需要替换表名
            String tableName = "accdetailbalance";
            if (whetherCarryForward(centerCode,CurrentUser.getCurrentLoginAccountType(),accBookCode,lastYearMonth)) {
                tableName = "accdetailbalancehis";
            }

            //2.1 获取科目期初余额、借贷本年累计SQL 前半部分
            StringBuffer balanceSql = new StringBuffer("SELECT IFNULL(SUM(a.debit_dest_year),0.00) AS debitDestYear, IFNULL(SUM(a.credit_dest_year),0.00) AS creditDestYear, IFNULL(SUM(a.balance_dest),0.00) AS balanceBeginDest FROM "+tableName+" a WHERE 1=1");

            int balanceSqlParamsNo = 1;
            Map<Integer, Object> balanceSqlParams = new HashMap<>();

            balanceSql.append(" AND a.center_code  in (?" + balanceSqlParamsNo+")" );
            balanceSqlParams.put(balanceSqlParamsNo, centerCode);
            balanceSqlParamsNo++;
            balanceSql.append(" AND a.branch_code in (?" + balanceSqlParamsNo+")" );
            balanceSqlParams.put(balanceSqlParamsNo, branchCode);
            balanceSqlParamsNo++;
            balanceSql.append(" AND a.acc_book_type = ?" + balanceSqlParamsNo);
            balanceSqlParams.put(balanceSqlParamsNo, accBookType);
            balanceSqlParamsNo++;
            balanceSql.append(" AND a.acc_book_code = ?" + balanceSqlParamsNo);
            balanceSqlParams.put(balanceSqlParamsNo, accBookCode);
            balanceSqlParamsNo++;
            balanceSql.append(" AND a.year_month_date = ?" + balanceSqlParamsNo);
            balanceSqlParams.put(balanceSqlParamsNo, lastYearMonth);
            balanceSqlParamsNo++;

            //2.2.再根据科目代码和查询条件查询出凭证信息SQL1(针对非末级科目)和SQL2(针对末级科目)
            StringBuffer sql1 = new StringBuffer("SELECT a.year_month_date AS yearMonthDate,SUM(a.debit_dest) AS debitDest,SUM(a.credit_dest) AS creditDest,SUM((a.debit_dest-a.credit_dest)) AS balance FROM accsubvoucher a LEFT JOIN accmainvoucher am ON am.center_code=a.center_code AND am.branch_code=a.branch_code AND am.acc_book_type=a.acc_book_type AND am.acc_book_code=a.acc_book_code AND am.year_month_date=a.year_month_date AND am.voucher_no=a.voucher_no WHERE 1=1");
            StringBuffer sql2 = new StringBuffer("SELECT am.voucher_date AS voucherDate,a.year_month_date AS yearMonthDate,a.voucher_no AS voucherNo,a.suffix_no AS suffixNo,a.direction_idx AS directionIdx,a.direction_idx_name AS directionIdxName,a.direction_other AS directionOther,a.remark AS remarkName,a.debit_dest AS debitDest,a.credit_dest AS creditDest,(a.debit_dest-a.credit_dest) AS balance,am.center_code AS centerCode FROM accsubvoucher a LEFT JOIN accmainvoucher am ON am.center_code=a.center_code AND am.branch_code=a.branch_code AND am.acc_book_type=a.acc_book_type AND am.acc_book_code=a.acc_book_code AND am.year_month_date=a.year_month_date AND am.voucher_no=a.voucher_no WHERE 1=1");

            int sql1ParamsNo = 1;
            Map<Integer, Object> sql1Params = new HashMap<>();
            int sql2ParamsNo = 1;
            Map<Integer, Object> sql2Params = new HashMap<>();

            sql1.append(" AND a.center_code in (?" + sql1ParamsNo+")");
            sql2.append(" AND a.center_code in (?" + sql1ParamsNo+")");
            sql1Params.put(sql1ParamsNo, centerCode);
            sql1ParamsNo++;
            sql2Params.put(sql2ParamsNo, centerCode);
            sql2ParamsNo++;
            sql1.append(" AND a.branch_code in (?" + sql1ParamsNo+")");
            sql2.append(" AND a.branch_code in (?" + sql1ParamsNo+")");
            sql1Params.put(sql1ParamsNo, branchCode);
            sql1ParamsNo++;
            sql2Params.put(sql2ParamsNo, branchCode);
            sql2ParamsNo++;
            sql1.append(" AND a.acc_book_type = ?" + sql1ParamsNo);
            sql2.append(" AND a.acc_book_type = ?" + sql2ParamsNo);
            sql1Params.put(sql1ParamsNo, accBookType);
            sql1ParamsNo++;
            sql2Params.put(sql2ParamsNo, accBookType);
            sql2ParamsNo++;
            sql1.append(" AND a.acc_book_code = ?" + sql1ParamsNo);
            sql2.append(" AND a.acc_book_code = ?" + sql2ParamsNo);
            sql1Params.put(sql1ParamsNo, accBookCode);
            sql1ParamsNo++;
            sql2Params.put(sql2ParamsNo, accBookCode);
            sql2ParamsNo++;

            if (dto.getYearMonth()!=null&&!"".equals(dto.getYearMonth())) {
                sql1.append(" AND a.year_month_date >= ?" + sql1ParamsNo);
                sql2.append(" AND a.year_month_date >= ?" + sql2ParamsNo);
                sql1Params.put(sql1ParamsNo, dto.getYearMonth());
                sql1ParamsNo++;
                sql2Params.put(sql2ParamsNo, dto.getYearMonth());
                sql2ParamsNo++;
            }
            if (dto.getYearMonthDate()!=null&&!"".equals(dto.getYearMonthDate())) {
                sql1.append(" AND a.year_month_date <= ?" + sql1ParamsNo);
                sql2.append(" AND a.year_month_date <= ?" + sql2ParamsNo);
                sql1Params.put(sql1ParamsNo, dto.getYearMonthDate());
                sql1ParamsNo++;
                sql2Params.put(sql2ParamsNo, dto.getYearMonthDate());
                sql2ParamsNo++;
            }
            if (dto.getMoneyStart()!=null&&!"".equals(dto.getMoneyStart())) {
                sql1.append(" AND (CASE a.debit_dest WHEN 0.00 THEN a.credit_dest ELSE a.debit_dest END)>= ?" + sql1ParamsNo);
                sql2.append(" AND (CASE a.debit_dest WHEN 0.00 THEN a.credit_dest ELSE a.debit_dest END)>= ?" + sql2ParamsNo);
                sql1Params.put(sql1ParamsNo, dto.getMoneyStart());
                sql1ParamsNo++;
                sql2Params.put(sql2ParamsNo, dto.getMoneyStart());
                sql2ParamsNo++;
            }
            if (dto.getMoneyEnd()!=null&&!"".equals(dto.getMoneyEnd())) {
                sql1.append(" AND (CASE a.debit_dest WHEN 0.00 THEN a.credit_dest ELSE a.debit_dest END)<= ?" + sql1ParamsNo);
                sql2.append(" AND (CASE a.debit_dest WHEN 0.00 THEN a.credit_dest ELSE a.debit_dest END)<= ?" + sql2ParamsNo);
                sql1Params.put(sql1ParamsNo, dto.getMoneyEnd());
                sql1ParamsNo++;
                sql2Params.put(sql2ParamsNo, dto.getMoneyEnd());
                sql2ParamsNo++;
            }
            if (dto.getVoucherNoStart()!=null&&!"".equals(dto.getVoucherNoStart())) {
                sql1.append(" AND a.voucher_no >= ?" + sql1ParamsNo);
                sql2.append(" AND a.voucher_no >= ?" + sql2ParamsNo);
                sql1Params.put(sql1ParamsNo, dto.getVoucherNoStart());
                sql1ParamsNo++;
                sql2Params.put(sql2ParamsNo, dto.getVoucherNoStart());
                sql2ParamsNo++;
            }
            if (dto.getVoucherNoEnd()!=null&&!"".equals(dto.getVoucherNoEnd())) {
                sql1.append(" AND a.voucher_no <= ?" + sql1ParamsNo);
                sql2.append(" AND a.voucher_no <= ?" + sql2ParamsNo);
                sql1Params.put(sql1ParamsNo, dto.getVoucherNoEnd());
                sql1ParamsNo++;
                sql2Params.put(sql2ParamsNo, dto.getVoucherNoEnd());
                sql2ParamsNo++;
            }
            if (dto.getVoucherDateStart()!=null&&!"".equals(dto.getVoucherDateStart())) {
                sql1.append(" AND am.voucher_date >= ?" + sql1ParamsNo);
                sql2.append(" AND am.voucher_date >= ?" + sql2ParamsNo);
                sql1Params.put(sql1ParamsNo, dto.getVoucherDateStart());
                sql1ParamsNo++;
                sql2Params.put(sql2ParamsNo, dto.getVoucherDateStart());
                sql2ParamsNo++;
            }
            if (dto.getVoucherDateEnd()!=null&&!"".equals(dto.getVoucherDateEnd())) {
                sql1.append(" AND am.voucher_date <= ?" + sql1ParamsNo);
                sql2.append(" AND am.voucher_date <= ?" + sql2ParamsNo);
                sql1Params.put(sql1ParamsNo, dto.getVoucherDateEnd());
                sql1ParamsNo++;
                sql2Params.put(sql2ParamsNo, dto.getVoucherDateEnd());
                sql2ParamsNo++;
            }
            if (dto.getSubjectCode()!=null&&!"".equals(dto.getSubjectCode())) {
                sql1.append(" AND a.direction_idx LIKE ?" + sql1ParamsNo);
                sql2.append(" AND a.direction_idx LIKE ?" + sql2ParamsNo);
                sql1Params.put(sql1ParamsNo, dto.getSubjectCode()+"%");
                sql1ParamsNo++;
                sql2Params.put(sql2ParamsNo, dto.getSubjectCode()+"%");
                sql2ParamsNo++;
            }
            if (dto.getRemarkName()!=null&&!"".equals(dto.getRemarkName())) {
                sql1.append(" AND a.remark LIKE ?" + sql1ParamsNo);
                sql2.append(" AND a.remark LIKE ?" + sql2ParamsNo);
                sql1Params.put(sql1ParamsNo, "%"+dto.getRemarkName()+"%");
                sql1ParamsNo++;
                sql2Params.put(sql2ParamsNo, "%"+dto.getRemarkName()+"%");
                sql2ParamsNo++;
            }
            if (dto.getSpecialName()!=null&&!"".equals(dto.getSpecialName())) {
                //拼接明细对象SQL（OR）
                jointQuertSqlBySpecialCodes(sql1, dto,"a", sql1Params, sql1ParamsNo);
                if (sql1ParamsNo != sql1Params.size()+1) { sql1ParamsNo = sql1Params.size()+1; }

                jointQuertSqlBySpecialCodes(sql2, dto,"a", sql2Params, sql2ParamsNo);
                if (sql2ParamsNo != sql2Params.size()+1) { sql2ParamsNo = sql2Params.size()+1; }
            }
            if (dto.getVoucherGene()!=null&&!"".equals(dto.getVoucherGene())) {
                if ("0".equals(dto.getVoucherGene())) {
                    sql1.append(" AND am.voucher_flag = '3'");
                    sql2.append(" AND am.voucher_flag = '3'");
                } else {
                    sql1.append(" AND am.voucher_flag in ('1','2','3')");
                    sql2.append(" AND am.voucher_flag in ('1','2','3')");
                }
            }

            //准备会计期间数组（用于逐月循环）
            List<String> monthList = getMonthList(dto.getYearMonth(), dto.getYearMonthDate());

            //循环前各参数数据
            int oldBalanceSqlParamsNo = balanceSqlParamsNo;
            Map<Integer, Object> oldBalanceSqlParams = new HashMap<>();
            oldBalanceSqlParams.putAll(balanceSqlParams);
            int oldSql1ParamsNo = sql1ParamsNo;
            Map<Integer, Object> oldSql1Params = new HashMap<>();
            oldSql1Params.putAll(sql1Params);
            int oldSql2ParamsNo = sql2ParamsNo;
            Map<Integer, Object> oldSql2Params = new HashMap<>();
            oldSql2Params.putAll(sql2Params);

            //循环需要展示的科目，并从开始会计期间至终止会计期间逐月显示
            for (Map<String, String> map : needSubjectList) {
                balanceSqlParamsNo = oldBalanceSqlParamsNo;
                balanceSqlParams = new HashMap<>();
                balanceSqlParams.putAll(oldBalanceSqlParams);
                sql1ParamsNo = oldSql1ParamsNo;
                sql1Params = new HashMap<>();
                sql1Params.putAll(oldSql1Params);
                sql2ParamsNo = oldSql2ParamsNo;
                sql2Params = new HashMap<>();
                sql2Params.putAll(oldSql2Params);

                // map 中 subjectCode 为科目代码, direction 科目余额方向, endFlag 为科目末级标志, subjectName 为科目名称（末级名称/全级名称）
                //科目段部分SQL拼接
                String balanceSqlDirectionIdxSql = jointDirectionIdxSqlBySubjectCode(map.get("subjectCode"), "a", balanceSqlParams, balanceSqlParamsNo);
                if (balanceSqlParamsNo != balanceSqlParams.size()+1) { balanceSqlParamsNo = balanceSqlParams.size()+1; }

                String sql1DirectionIdxSql = jointDirectionIdxSqlBySubjectCode(map.get("subjectCode"), "a", sql1Params, sql1ParamsNo);
                if (sql1ParamsNo != sql1Params.size()+1) { sql1ParamsNo = sql1Params.size()+1; }

                String sql2DirectionIdxSql = jointDirectionIdxSqlBySubjectCode(map.get("subjectCode"), "a", sql2Params, sql2ParamsNo);
                if (sql2ParamsNo != sql2Params.size()+1) { sql2ParamsNo = sql2Params.size()+1; }

                //完整的余额查询SQL
                String balanceSqlAll = balanceSql.toString() + balanceSqlDirectionIdxSql;//科目段拼接SQL
                //是否是末级科目标志：true-末级，false-非末级
                boolean endFlag = ("0".equals(map.get("endFlag")))?true:false;
                //完整的凭证查询SQL拼接
                StringBuffer sqlAll = new StringBuffer();
                Map<Integer, Object> sqlAllParams = new HashMap<>();
                if (!endFlag) {//非末级科目
                    sqlAll = new StringBuffer(sql1.toString() + sql1DirectionIdxSql);
                    sqlAll.append(" GROUP BY yearMonthDate");
                    sqlAllParams = sql1Params;
                } else {//末级科目
                    sqlAll = new StringBuffer(sql2.toString() + sql2DirectionIdxSql);
                    sqlAllParams = sql2Params;
                }
                //凭证查询SQL联查历史表
                String sqlStr = sqlAll.toString();
                sqlStr = sqlStr.replaceAll("accsubvoucher","accsubvoucherhis");
                sqlStr = sqlStr.replaceAll("a\\.","ah\\.");
                sqlStr = sqlStr.replaceAll(" a "," ah ");
                sqlStr = sqlStr.replaceAll("accmainvoucher","accmainvoucherhis");
                sqlStr = sqlStr.replaceAll("am\\.","amh\\.");
                sqlStr = sqlStr.replaceAll(" am "," amh ");
                sqlAll.append(" UNION ALL ");
                sqlAll.append(sqlStr);
                if (endFlag) {//末级科目
                    //按科目排序
                    sqlAll.append(" ORDER BY directionIdx,yearMonthDate,voucherNo,suffixNo");
                } else {//非末级科目
                    sqlAll.append(" ORDER BY yearMonthDate");
                }

                List<?> balanceSqlList = voucherRepository.queryBySqlSC(balanceSqlAll, balanceSqlParams);//科目余额数据
                List<?> sqlList = voucherRepository.queryBySqlSC(sqlAll.toString(), sqlAllParams);//凭证数据（非末级科目为合计数，末级科目为凭证明细）

                //将 sqlList 数据处理成 Map<String, List<Map<String, Object>>>数据，key 为会计期间，List 为对应期间的数据
                Map<String, List<Map<String, Object>>> dataMap = new HashMap<>();
                if (sqlList!=null&&sqlList.size()>0) {
                    String ym = "";
                    List<Map<String, Object>> list = new ArrayList<>();
                    for (Object obj : sqlList) {
                        Map map1 = (Map) obj;
                        String yearMonthDate = (String) map1.get("yearMonthDate");
                        if (!"".equals(ym) && !ym.equals(yearMonthDate)) {
                            dataMap.put(ym, list);
                            list = new ArrayList<>();
                        }
                        list.add(map1);
                        ym = yearMonthDate;
                    }
                    if (list.size()>0) {
                        dataMap.put(ym, list);
                    }
                }

                BigDecimal debitDestYear = new BigDecimal("0.00");//本位币本年借方金额
                BigDecimal creditDestYear = new BigDecimal("0.00");//本位币本年贷方金额
                BigDecimal balanceBeginDest = new BigDecimal("0.00");//本位币期初余额
                BigDecimal debitDestMonth = new BigDecimal("0.00");//本位币本月借方金额
                BigDecimal creditDestMonth = new BigDecimal("0.00");//本位币本月贷方金额

                //初始化期初余额和借贷本年累计数据
                Map<String, Object> balanceMap = (Map<String, Object>) balanceSqlList.get(0);
                if (!"01".equals(dto.getYearMonth().substring(4))) {
                    debitDestYear = (BigDecimal) balanceMap.get("debitDestYear");
                    creditDestYear = (BigDecimal) balanceMap.get("creditDestYear");
                }
                balanceBeginDest = (BigDecimal) balanceMap.get("balanceBeginDest");

                //逐月处理数据
                String oldYearMonth = "";
                boolean flag1 = true;//用于表示是否追加期初
                boolean flag2 = true;//用于表示是否追加本月合计、本年累计
                for (String yearMonth : monthList) {
                    if (!"".equals(oldYearMonth) && !oldYearMonth.equals(yearMonth)) {
                        //同时需要考虑JS月要合并到当年12月
                        if ("12".equals(yearMonth.substring(4))) { flag2 = false; } else { flag2 = true; }
                        if ("JS".equals(yearMonth.substring(4))) { flag1 = false; } else { flag1 = true; }
                        if (!("JS".equals(yearMonth.substring(4)) && "12".equals(oldYearMonth.substring(4)) && oldYearMonth.substring(0,4).equals(yearMonth.substring(0,4)))) {
                            debitDestMonth = new BigDecimal("0.00");
                            creditDestMonth = new BigDecimal("0.00");
                        }
                        //判断是否跨年，若是，则借贷本年累计归零
                        if (!oldYearMonth.substring(0,4).equals(yearMonth.substring(0,4))) {
                            debitDestYear = new BigDecimal("0.00");
                            creditDestYear = new BigDecimal("0.00");
                        }
                    }
                    if (dataMap.containsKey(yearMonth)) {//查询结果( dataMap )中存在当前会计期间数据
                        List<Map<String, Object>> list = dataMap.get(yearMonth);//一个会计期间内的数据
                        if (endFlag) {//末级科目
                            if (list!=null&&list.size()>0) {
                                //先追加期初
                                if (flag1) {
                                    setRowDataByEndFlagAndBalanceBegin(result, yearMonth, map, balanceBeginDest);
                                }

                                Map<String, Object> rowMap = new HashMap();
                                for (Map<String, Object> listMap : list) {
                                    //每次循环即使一个明细科目
                                    //计算借贷本月合计、借贷本年累计、余额
                                    BigDecimal debitDest = (BigDecimal) listMap.get("debitDest");
                                    BigDecimal creditDest = (BigDecimal) listMap.get("creditDest");
                                    debitDestMonth = debitDestMonth.add(debitDest);
                                    creditDestMonth = creditDestMonth.add(creditDest);
                                    debitDestYear = debitDestYear.add(debitDest);
                                    creditDestYear = creditDestYear.add(creditDest);
                                    balanceBeginDest = balanceBeginDest.add((BigDecimal) listMap.get("balance"));

                                    rowMap = new HashMap<>();
                                    // 方法setRowData(Map<String, Object> rowMap, Map<String, Object> dataMap, VoucherDTO dto, Map<String, Object> specialNameMap, String direction, BigDecimal balance)
                                    setRowData(rowMap, listMap, dto, specialNameMap, map.get("direction"), balanceBeginDest);
                                    result.add(rowMap);
                                }
                                if (flag2) {
                                    //明细之后追加借贷本月合计、借贷本年累计
                                    setRowDataByEndFlagAndMonthAndYear(result, yearMonth, map, debitDestMonth, creditDestMonth, debitDestYear, creditDestYear, balanceBeginDest);
                                }
                            }
                        } else {//非末级科目
                            Map<String, Object> listMap = list.get(0);
                            //先追加期初
                            if (flag1) {
                                setRowDataByEndFlagAndBalanceBegin(result, yearMonth, map, balanceBeginDest);
                            }

                            //计算借贷本年累计和余额
                            BigDecimal debitDest = (BigDecimal) listMap.get("debitDest");
                            BigDecimal creditDest = (BigDecimal) listMap.get("creditDest");
                            debitDestMonth = debitDestMonth.add(debitDest);
                            creditDestMonth = creditDestMonth.add(creditDest);
                            debitDestYear = debitDestYear.add(debitDest);
                            creditDestYear = creditDestYear.add(creditDest);
                            balanceBeginDest = balanceBeginDest.add((BigDecimal) listMap.get("balance"));

                            if (flag2) {
                                //追加借贷本月合计、借贷本年累计
                                setRowDataByEndFlagAndMonthAndYear(result, yearMonth, map, debitDestMonth, creditDestMonth, debitDestYear, creditDestYear, balanceBeginDest);
                            }
                        }
                    } else {//查询结果( dataMap )中无当前会计期间数据
                        if (endFlag) {//末级科目
                            setRowDataByEndFlag(result, flag1, flag2, yearMonth, map, debitDestYear, creditDestYear, balanceBeginDest);
                        } else {//非末级科目
                            setRowDataByEndFlag(result, flag1, flag2, yearMonth, map, debitDestYear, creditDestYear, balanceBeginDest);
                        }
                    }
                    oldYearMonth = yearMonth;
                }
            }
        }
        return result;
    }

    public List<?> queryDetailAccountByVoucherNoOrSubject(VoucherDTO dto, Map<String, Object> specialNameMap,List centerCode){
        List<Object> result = new ArrayList<>();//最终返回结果
        List branchCode = centerCode;
        String accBookType = CurrentUser.getCurrentLoginAccountType();
        String accBookCode = CurrentUser.getCurrentLoginAccount();
        /*
            1.通过科目起止代码和起止层级获得科目范围,获取科目全代码、科目余额方向、科目名称(全级/本身)
            2.再循环科目数据，通过拆分科目代码，获取科目期初余额、借贷本年累计，同时按照一定的规则获取凭证数据
            3.将所得数据，处理到最终返回结果中
        */

        //1.通过科目起止代码和起止层级获得科目范围,获取科目全代码、科目余额方向、科目名称
        //needSubjectList : Map 中 subjectCode 为科目代码, direction 科目余额方向, subjectName为科目名称（末级名称/全级名称）
        List<Map<String, String>> needSubjectList = new ArrayList<>();
        setNeedSubjectList(needSubjectList, dto, accBookCode);

        if (needSubjectList.size()>0) {
            //2.再循环科目数据，通过拆分科目代码，获取科目期初余额、借贷本年累计，同时按照一定的规则获取凭证数据

            String lastYearMonth = getLastYearMonth(dto.getYearMonth());//上一个会计期间

            //如此会计期间已结转侧需要替换表名
            String tableName = "accdetailbalance";
            if (whetherCarryForward(centerCode,CurrentUser.getCurrentLoginAccountType(),accBookCode,lastYearMonth)) {
                tableName = "accdetailbalancehis";
            }

            //2.1 获取科目期初余额、借贷本年累计SQL 前半部分
            StringBuffer balanceSql = new StringBuffer("SELECT IFNULL(SUM(a.debit_dest_year),0.00) AS debitDestYear, IFNULL(SUM(a.credit_dest_year),0.00) AS creditDestYear, IFNULL(SUM(a.balance_dest),0.00) AS balanceBeginDest FROM "+tableName+" a WHERE 1=1");

            int balanceSqlParamsNo = 1;
            Map<Integer, Object> balanceSqlParams = new HashMap<>();
           // And a.center_code in ("","","","","");
            //String[] str = ["123","456","789"];

            balanceSql.append(" AND a.center_code in (?" + balanceSqlParamsNo +")");
            balanceSqlParams.put(balanceSqlParamsNo, centerCode);
            balanceSqlParamsNo++;
            balanceSql.append(" AND a.branch_code in (?" + balanceSqlParamsNo +")");
            balanceSqlParams.put(balanceSqlParamsNo, centerCode);
            balanceSqlParamsNo++;
            balanceSql.append(" AND a.acc_book_type = ?" + balanceSqlParamsNo);
            balanceSqlParams.put(balanceSqlParamsNo, accBookType);
            balanceSqlParamsNo++;
            balanceSql.append(" AND a.acc_book_code = ?" + balanceSqlParamsNo);
            balanceSqlParams.put(balanceSqlParamsNo, accBookCode);
            balanceSqlParamsNo++;
            balanceSql.append(" AND a.year_month_date = ?" + balanceSqlParamsNo);
            balanceSqlParams.put(balanceSqlParamsNo, lastYearMonth);
            balanceSqlParamsNo++;

            //2.2.再根据科目代码和查询条件查询出凭证信息SQL
            StringBuffer sql = new StringBuffer("SELECT am.voucher_date AS voucherDate,a.year_month_date AS yearMonthDate,a.voucher_no AS voucherNo,a.suffix_no AS suffixNo,a.direction_idx AS directionIdx,a.direction_idx_name AS directionIdxName,a.direction_other AS directionOther,a.remark AS remarkName,a.debit_dest AS debitDest,a.credit_dest AS creditDest,(a.debit_dest-a.credit_dest) AS balance,am.center_code AS centerCode FROM accsubvoucher a LEFT JOIN accmainvoucher am ON am.center_code=a.center_code AND am.branch_code=a.branch_code AND am.acc_book_type=a.acc_book_type AND am.acc_book_code=a.acc_book_code AND am.year_month_date=a.year_month_date AND am.voucher_no=a.voucher_no WHERE 1=1");

            int sqlParamsNo = 1;
            Map<Integer, Object> sqlParams = new HashMap<>();

            sql.append(" AND a.center_code in (?" + sqlParamsNo +")");
            sqlParams.put(sqlParamsNo, centerCode);
            sqlParamsNo++;
            sql.append(" AND a.branch_code in (?" + sqlParamsNo +")");
            sqlParams.put(sqlParamsNo, centerCode);
            sqlParamsNo++;
            sql.append(" AND a.acc_book_type = ?" + sqlParamsNo);
            sqlParams.put(sqlParamsNo, accBookType);
            sqlParamsNo++;
            sql.append(" AND a.acc_book_code = ?" + sqlParamsNo);
            sqlParams.put(sqlParamsNo, accBookCode);
            sqlParamsNo++;

            if (dto.getYearMonth()!=null&&!"".equals(dto.getYearMonth())) {
                sql.append(" AND a.year_month_date >= ?" + sqlParamsNo);
                sqlParams.put(sqlParamsNo, dto.getYearMonth());
                sqlParamsNo++;
            }
            if (dto.getYearMonthDate()!=null&&!"".equals(dto.getYearMonthDate())) {
                sql.append(" AND a.year_month_date <= ?" + sqlParamsNo);
                sqlParams.put(sqlParamsNo, dto.getYearMonthDate());
                sqlParamsNo++;
            }
            if (dto.getMoneyStart()!=null&&!"".equals(dto.getMoneyStart())) {
                sql.append(" AND (CASE a.debit_dest WHEN 0.00 THEN a.credit_dest ELSE a.debit_dest END)>= ?" + sqlParamsNo);
                sqlParams.put(sqlParamsNo, dto.getMoneyStart());
                sqlParamsNo++;
            }
            if (dto.getMoneyEnd()!=null&&!"".equals(dto.getMoneyEnd())) {
                sql.append(" AND (CASE a.debit_dest WHEN 0.00 THEN a.credit_dest ELSE a.debit_dest END)<= ?" + sqlParamsNo);
                sqlParams.put(sqlParamsNo, dto.getMoneyEnd());
                sqlParamsNo++;
            }
            if (dto.getVoucherNoStart()!=null&&!"".equals(dto.getVoucherNoStart())) {
                sql.append(" AND a.voucher_no >= ?" + sqlParamsNo);
                sqlParams.put(sqlParamsNo, dto.getVoucherNoStart());
                sqlParamsNo++;
            }
            if (dto.getVoucherNoEnd()!=null&&!"".equals(dto.getVoucherNoEnd())) {
                sql.append(" AND a.voucher_no <= ?" + sqlParamsNo);
                sqlParams.put(sqlParamsNo, dto.getVoucherNoEnd());
                sqlParamsNo++;
            }
            if (dto.getVoucherDateStart()!=null&&!"".equals(dto.getVoucherDateStart())) {
                sql.append(" AND am.voucher_date >= ?" + sqlParamsNo);
                sqlParams.put(sqlParamsNo, dto.getVoucherDateStart());
                sqlParamsNo++;
            }
            if (dto.getVoucherDateEnd()!=null&&!"".equals(dto.getVoucherDateEnd())) {
                sql.append(" AND am.voucher_date <= ?" + sqlParamsNo);
                sqlParams.put(sqlParamsNo, dto.getVoucherDateEnd());
                sqlParamsNo++;
            }
            if (dto.getSubjectCode()!=null&&!"".equals(dto.getSubjectCode())) {
                sql.append(" AND a.direction_idx LIKE ?" + sqlParamsNo);
                sqlParams.put(sqlParamsNo, dto.getSubjectCode()+"%");
                sqlParamsNo++;
            }
            if (dto.getRemarkName()!=null&&!"".equals(dto.getRemarkName())) {
                sql.append(" AND a.remark LIKE ?" + sqlParamsNo);
                sqlParams.put(sqlParamsNo, "%"+dto.getRemarkName()+"%");
                sqlParamsNo++;
            }
            if (dto.getSpecialName()!=null&&!"".equals(dto.getSpecialName())) {
                //拼接明细对象SQL（OR）
                jointQuertSqlBySpecialCodes(sql, dto,"a", sqlParams, sqlParamsNo);
                if (sqlParamsNo != sqlParams.size()+1) { sqlParamsNo = sqlParams.size()+1; }
            }
            if (dto.getVoucherGene()!=null&&!"".equals(dto.getVoucherGene())) {
                if ("0".equals(dto.getVoucherGene())) {
                    sql.append(" AND am.voucher_flag = '3'");
                } else {
                    sql.append(" AND am.voucher_flag in ('1','2','3')");
                }
            }

            //循环前各参数数据
            int oldBalanceSqlParamsNo = balanceSqlParamsNo;
            Map<Integer, Object> oldBalanceSqlParams = new HashMap<>();
            oldBalanceSqlParams.putAll(balanceSqlParams);
            int oldSqlParamsNo = sqlParamsNo;
            Map<Integer, Object> oldSqlParams = new HashMap<>();
            oldSqlParams.putAll(sqlParams);

            for (Map<String, String> map : needSubjectList) {

                balanceSqlParamsNo = oldBalanceSqlParamsNo;
                balanceSqlParams = new HashMap<>();
                balanceSqlParams.putAll(oldBalanceSqlParams);
                sqlParamsNo = oldSqlParamsNo;
                sqlParams = new HashMap<>();
                sqlParams.putAll(oldSqlParams);

                //科目段部分SQL拼接
                String balanceSqlDirectionIdxSql = jointDirectionIdxSqlBySubjectCode(map.get("subjectCode"), "a", balanceSqlParams, balanceSqlParamsNo);
                if (balanceSqlParamsNo != balanceSqlParams.size()+1) { balanceSqlParamsNo = balanceSqlParams.size()+1; }
                String sqlDirectionIdxSql = jointDirectionIdxSqlBySubjectCode(map.get("subjectCode"), "a", sqlParams, sqlParamsNo);
                if (sqlParamsNo != sqlParams.size()+1) { sqlParamsNo = sqlParams.size()+1; }

                //完整的余额查询SQL
                String balanceSqlAll = balanceSql.toString() + balanceSqlDirectionIdxSql;//科目段拼接SQL
                //完整的凭证查询SQL拼接
                StringBuffer sqlAll = new StringBuffer(sql.toString() + sqlDirectionIdxSql);
                //凭证查询SQL联查历史表
                String sqlStr = sqlAll.toString();
                sqlStr = sqlStr.replaceAll("accsubvoucher","accsubvoucherhis");
                sqlStr = sqlStr.replaceAll("a\\.","ah\\.");
                sqlStr = sqlStr.replaceAll(" a "," ah ");
                sqlStr = sqlStr.replaceAll("accmainvoucher","accmainvoucherhis");
                sqlStr = sqlStr.replaceAll("am\\.","amh\\.");
                sqlStr = sqlStr.replaceAll(" am "," amh ");
                sqlAll.append(" UNION ALL ");
                sqlAll.append(sqlStr);
                if (dto.getOrderingRule()!=null&&"1".equals(dto.getOrderingRule())) {
                    //按科目排序
                    sqlAll.append(" ORDER BY directionIdx,yearMonthDate,voucherNo,suffixNo");
                } else {
                    //按凭证排序
                    sqlAll.append(" ORDER BY yearMonthDate,voucherNo,directionIdx,suffixNo");
                }


                BigDecimal debitDestYear = new BigDecimal("0.00");//本位币本年借方金额
                BigDecimal creditDestYear = new BigDecimal("0.00");//本位币本年贷方金额
                BigDecimal balanceBeginDest = new BigDecimal("0.00");//本位币期初余额
                BigDecimal debitDestMonth = new BigDecimal("0.00");//本位币本月借方金额
                BigDecimal creditDestMonth = new BigDecimal("0.00");//本位币本月贷方金额

                List<?> balanceSqlList = voucherRepository.queryBySqlSC(balanceSqlAll, balanceSqlParams);//余额数据
                List<?> sqlList = voucherRepository.queryBySqlSC(sqlAll.toString(), sqlParams);//凭证数据

                Map<String, Object> balanceMap = (Map<String, Object>) balanceSqlList.get(0);
                balanceBeginDest = (BigDecimal) balanceMap.get("balanceBeginDest");

                /*
                    按凭证顺序显示或按科目顺序显示时，在查询范围内
                       1.如果一个科目期初为0，也无发生，则不显示此科目信息；
                       2.如果期初为0，有发生，则显示期初及发生；
                       3.如果期初不为0，且有发生，则显示期初及发生；
                       4.如果期初不为0，无发生，则显示期初，不显示发生。
                            其余2、3、4点在后续代码中会有处理，此处不考虑
                 */
                /*
                    按凭证顺序显示或按科目顺序显示时，查询结果中，如果一个科目当月有发生额，则当月末需显示【本月合计】及【本年累计】值
                 */
                if (!(sqlList!=null&&sqlList.size()>0) && balanceBeginDest.compareTo(new BigDecimal("0.00"))==0) {
                    //如果一个科目期初为0，也无发生，则不显示此科目信息
                    continue;
                }

                if (!"01".equals(dto.getYearMonth().substring(4))) {
                    debitDestYear = (BigDecimal) balanceMap.get("debitDestYear");
                    creditDestYear = (BigDecimal) balanceMap.get("creditDestYear");
                }

                Map<String, Object> rowMap = new HashMap<>();
                rowMap.put("centerCode", CurrentUser.getCurrentLoginManageBranch());
                rowMap.put("directionIdx", map.get("subjectCode"));
                rowMap.put("directionIdxName", map.get("subjectName"));
                rowMap.put("remarkName", "期初余额");
                rowMap.put("balanceFX", getFXString(map.get("direction"), balanceBeginDest));
                rowMap.put("balanceDest", balanceBeginDest.abs().toString());
                result.add(rowMap);
                if (sqlList!=null&&sqlList.size()>0) {
                    String oldYearMonthDate = "";
                    for (Object obj : sqlList) {
                        Map<String, Object> dataMap = (Map<String, Object>) obj;
                        String yearMonthDate = (String) dataMap.get("yearMonthDate");
                        if (!"".equals(oldYearMonthDate) && !oldYearMonthDate.equals(yearMonthDate)) {
                            //此时非第一条数据，同时与上一条数据不在同一会计期间，需要追加本月合计、本年累计项
                            //同时需要考虑JS月要合并到当年12月
                            if (!("JS".equals(yearMonthDate.substring(4)) && "12".equals(oldYearMonthDate.substring(4)) && oldYearMonthDate.substring(0,4).equals(yearMonthDate.substring(0,4)))) {
                                setRowDataByMonthYear(result, map.get("direction"), debitDestMonth, creditDestMonth, debitDestYear, creditDestYear, balanceBeginDest);
                                //再归零本月合计
                                debitDestMonth = new BigDecimal("0.00");
                                creditDestMonth = new BigDecimal("0.00");
                            }
                            //判断是否跨年，若跨年，则归零本年累计
                            if ("01".equals(yearMonthDate.substring(4))) {
                                //再非第一条数据，同时与上一条数据不在同一会计期间的基础之上，再次出现一月即为跨年
                                debitDestYear = new BigDecimal("0.00");
                                creditDestYear = new BigDecimal("0.00");
                            }
                        }

                        //借贷本月合计、借贷本年累计、余额计算
                        debitDestMonth = debitDestMonth.add((BigDecimal) dataMap.get("debitDest"));
                        creditDestMonth = creditDestMonth.add((BigDecimal) dataMap.get("creditDest"));
                        debitDestYear = debitDestYear.add((BigDecimal) dataMap.get("debitDest"));
                        creditDestYear = creditDestYear.add((BigDecimal) dataMap.get("creditDest"));
                        balanceBeginDest = balanceBeginDest.add((BigDecimal) dataMap.get("balance"));

                        rowMap = new HashMap<>();
                        setRowData(rowMap, dataMap, dto, specialNameMap, map.get("direction"), balanceBeginDest);
                        result.add(rowMap);

                        //本次循环结束，赋值 oldYearMonthDate
                        oldYearMonthDate = yearMonthDate;
                    }
                    //循环结束之后追加本月合计、本年累计项
                    setRowDataByMonthYear(result, map.get("direction"), debitDestMonth, creditDestMonth, debitDestYear, creditDestYear, balanceBeginDest);
                }
            }
        }
        return result;
    }

    private void setRowData(Map<String, Object> rowMap, Map<String, Object> dataMap, VoucherDTO dto, Map<String, Object> specialNameMap, String direction, BigDecimal balance){
        rowMap.put("centerCode", dataMap.get("centerCode"));
        rowMap.put("voucherDate", dataMap.get("voucherDate"));
        rowMap.put("yearMonthDate", dataMap.get("yearMonthDate"));
        rowMap.put("voucherNo", dataMap.get("voucherNo"));
        rowMap.put("suffixNo", dataMap.get("suffixNo"));
        rowMap.put("directionIdx", dataMap.get("directionIdx"));
        if (dto.getSubjectNameP()!=null&&"0".equals(dto.getSubjectNameP())) {//非全级显示
            String subjectName = (String) dataMap.get("directionIdxName");
            if ("/".equals(subjectName.substring(subjectName.length()-1))) {
                String[] names = (subjectName.substring(0,subjectName.length()-1)).split("/");
                subjectName = names[names.length-1];
            } else {
                String[] names = subjectName.split("/");
                subjectName = names[names.length-1];
            }
            rowMap.put("directionIdxName", subjectName);
        } else {//全级显示
            rowMap.put("directionIdxName", dataMap.get("directionIdxName"));
        }
        rowMap.put("remarkName", dataMap.get("remarkName"));
        setRemarkNameBySpecial(rowMap, specialNameMap, dto.getSpecialNameP(), (String) dataMap.get("directionOther"));
        rowMap.put("debitDest", dataMap.get("debitDest").toString());
        rowMap.put("creditDest", dataMap.get("creditDest").toString());
        rowMap.put("balanceFX", getFXString(direction, balance));
        rowMap.put("balanceDest", balance.abs().toString());
    }

    private void setRowDataByMonthYear(List result, String direction, BigDecimal debitDestMonth, BigDecimal creditDestMonth, BigDecimal debitDestYear, BigDecimal creditDestYear, BigDecimal balanceBeginDest){
        Map<String, Object> map = new HashMap();
        map.put("remarkName", "本月合计");
        map.put("debitDest", debitDestMonth.toString());
        map.put("creditDest", creditDestMonth.toString());
        String fx = getFXString(direction, balanceBeginDest);
        map.put("balanceFX", fx);
        map.put("balanceDest", balanceBeginDest.abs().toString());
        result.add(map);
        map = new HashMap();
        map.put("remarkName", "本年累计");
        map.put("debitDest", debitDestYear.toString());
        map.put("creditDest", creditDestYear.toString());
        map.put("balanceFX", fx);
        map.put("balanceDest", balanceBeginDest.abs().toString());
        result.add(map);
    }

    public void jointQuertSqlBySpecialCodes(StringBuffer sql, VoucherDTO dto, String tableAlias, Map<Integer, Object> params, int paramsNo){
        if (dto.getSpecialCode()!=null&&!"".equals(dto.getSpecialCode())) {
            String[] codes = dto.getSpecialCode().split(",");

            Map<String, List<String>> paramsListMap = new HashMap<String, List<String>>();
            Map<String, String> segmentColFlagMap = new HashMap<String, String>();
            List<String> segmentFlagList = new ArrayList<String>();

            for (String code : codes) {
                boolean codeFlag = true;
                for (String key : segmentColFlagMap.keySet()) {
                    if (code.startsWith(key)) {
                        List<String> tempList = paramsListMap.get(segmentColFlagMap.get(key));
                        tempList.add(code);
                        codeFlag = false;
                        break;
                    }
                }

                if (codeFlag) {
                    String specialSql = "SELECT a.segment_flag AS 'segmentFlag',a.segment_col AS 'segmentCol' FROM accsegmentdefine a WHERE LEFT(a.segment_col,2) = LEFT( ?1 ,2)";
                    Map<Integer, Object> params2 = new HashMap<>();
                    params2.put(1, code);

                    List<?> list = voucherRepository.queryBySqlSC(specialSql, params2);

                    if (list!=null&&list.size()>0) {
                        String segmentFlag = (String)((Map)list.get(0)).get("segmentFlag");//专项存储位置
                        String segmentCol = (String)((Map)list.get(0)).get("segmentCol");//一级专项代码
                        segmentColFlagMap.put(segmentCol.substring(0, 2), segmentFlag);
                        segmentFlagList.add(segmentFlag);

                        List<String> tempList = new ArrayList<>();
                        tempList.add(code);
                        paramsListMap.put(segmentFlag, tempList);
                    } else {
                        logger.debug(code+"未配置在段定义表中！");
                    }
                }
            }

            StringBuffer sql1 = new StringBuffer();

            if (segmentFlagList.size()>0) {
                Collections.sort(segmentFlagList);//排序

                for (String s : segmentFlagList) {
                    List<String> tempList = paramsListMap.get(s);
                    if (tempList.size()>0) {
                        if (sql1.toString().length()==0) {
                            sql1.append(tableAlias+"."+s+" IN( ?" + paramsNo + " )");
                            params.put(paramsNo, tempList);
                            paramsNo++;
                        } else {
                            sql1.append(" OR "+tableAlias+"."+s+" IN( ?" + paramsNo + " )");
                            params.put(paramsNo, tempList);
                            paramsNo++;
                        }
                    }
                }
            }

            if (sql1.toString().length()>0) {
                sql.append(" AND (" + sql1.toString() + ")");
            }
        }
    }

    public void jointQuertSqlBySpecialCode(StringBuffer sql, String specialCode, String tableAlias, Map<Integer, Object> params, int paramsNo){
        if (specialCode!=null&&!"".equals(specialCode)) {
            String specialSql = "SELECT a.segment_flag AS 'segmentFlag' FROM accsegmentdefine a WHERE LEFT(a.segment_col,2) = LEFT( ?1 ,2)";

            Map<Integer, Object> params2 = new HashMap<>();
            params2.put(1, specialCode);

            List<?> list = voucherRepository.queryBySqlSC(specialSql, params2);
            if (list!=null&&list.size()>0) {
                String segmentFlag = (String)((Map)list.get(0)).get("segmentFlag");//专项存储位置
                sql.append(" AND "+tableAlias+"."+segmentFlag+" LIKE ?" + paramsNo);
                params.put(paramsNo, specialCode+"%");
                paramsNo++;
            }
        }
    }

    private void  setRemarkNameBySpecial(Map map, Map specialNameMap, String specialNameP, String directionOther){
        if (directionOther!=null&&!"".equals(directionOther)) {
            String[] directionOthers = directionOther.split(",");
            for (String specialCode : directionOthers) {
                if (specialNameMap.containsKey(specialCode)) {
                    map.put("remarkName" ,map.get("remarkName")+"_"+specialNameMap.get(specialCode));
                } else {
                    VoucherDTO voucherDTO = voucherService.getSpecialDateBySpecialCode(specialCode);
                    if (voucherDTO!=null&&voucherDTO.getSpecialName()!=null&&!"".equals(voucherDTO.getSpecialName())) {
                        if (specialNameP!=null&&"1".equals(specialNameP)) {
                            specialNameMap.put(specialCode, voucherDTO.getSpecialNameP());
                            map.put("remarkName", (String) map.get("remarkName")+"_"+voucherDTO.getSpecialNameP());
                        } else {
                            specialNameMap.put(specialCode, voucherDTO.getSpecialName());
                            map.put("remarkName", (String) map.get("remarkName")+"_"+voucherDTO.getSpecialName());
                        }
                    }
                }
            }
        }
    }

    /**
     * 获取上一个会计期间，如果参数为空，则返回上一年的决算月会计期间
     * @param yearMonth
     * @return
     */
    public String getLastYearMonth(String yearMonth){
        String cy = CurrentTime.getCurrentYear();//当前年
        String lastYearMonth = "";
        if (yearMonth!=null&&!"".equals(yearMonth)) {
            if (("01").equals(yearMonth.substring(4))) {
                //一月会计期间
                /*lastYearMonth = Integer.valueOf(yearMonth.substring(0,4))-1+"JS";*/
                lastYearMonth = Integer.valueOf(yearMonth.substring(0,4))-1+"14";
            } else if (("JS").equals(yearMonth.substring(4))) {
                //决算月会计期间
                lastYearMonth = yearMonth.substring(0,4)+"12";
            } else {
                lastYearMonth = Integer.valueOf(yearMonth)-1+"";
            }
        } else {
            //默认当前年一月会计期间，则查询上年决算月
            /*lastYearMonth = Integer.valueOf(cy)-1 + "JS";*/
            lastYearMonth = Integer.valueOf(cy)-1 + "14";
        }
        return lastYearMonth;
    }

    /**
     * 判断会计期间是否结转
     * @param centerCode
     * @param accBookType
     * @param accBookCode
     * @param yearMonth
     * @return true-结转/决算
     */
    public boolean whetherCarryForward(String centerCode, String accBookType, String accBookCode, String yearMonth){
        AccMonthTraceId mid = new AccMonthTraceId();
        mid.setCenterCode(centerCode);
        mid.setAccBookType(accBookType);
        mid.setAccBookCode(accBookCode);
        mid.setYearMonthDate(yearMonth);
        try {
            AccMonthTrace accMonthTrace = accMonthTraceRespository.findById(mid).get();
            if ("3".equals(accMonthTrace.getAccMonthStat()) || "5".equals(accMonthTrace.getAccMonthStat())) {
                return true;
            }
        } catch (Exception e) {
            System.out.println("查询并获取会计期间("+yearMonth+")数据异常，当前会计期间可能不存在！默认按已结转处理");
            return true;
        }
        return false;
    }
    /**
     * 判断会计期间是否结转
     * @param centerCode []
     * @param accBookType
     * @param accBookCode
     * @param yearMonth
     * @return true-结转/决算
     */
    public boolean whetherCarryForward(List centerCode, String accBookType, String accBookCode, String yearMonth){
        AccMonthTraceId mid = new AccMonthTraceId();
        mid.setAccBookType(accBookType);
        mid.setAccBookCode(accBookCode);
        mid.setYearMonthDate(yearMonth);
        for(int count=0;count<centerCode.size();count++){
            mid.setCenterCode(centerCode.get(count).toString());
            try {
                AccMonthTrace accMonthTrace = accMonthTraceRespository.findById(mid).get();
                if ("1".equals(accMonthTrace.getAccMonthStat()) || "2".equals(accMonthTrace.getAccMonthStat())) {
                    return false;
                }
            } catch (Exception e) {
                System.out.println("查询并获取会计期间("+yearMonth+")数据异常，当前会计期间可能不存在！默认按已结转处理");
                return true;
            }
        }
        return true;
    }

    @Override
    public String isHasData(VoucherDTO dto, String synthDetailAccount) {
        List centerCode1 =getSubBranch();
        List<?> list = queryDetailAccount(dto,synthDetailAccount,centerCode1);
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
    public void exportData(HttpServletRequest request, HttpServletResponse response, VoucherDTO dto, String synthDetailAccount,String Date1,String Date2) {
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
            List<String> subBranch = new ArrayList<>();
            List<String>  summaryBranch = new ArrayList();
            summaryBranch = branchInfoRepository.findByLevel("1");
            if(summaryBranch.contains(centerCode)){
                subBranch = branchInfoRepository.findBySuperCom(centerCode);
            }else{
                subBranch.add(centerCode);
            }
            result = queryDetailAccount(dto,synthDetailAccount,subBranch);
        }

        ExcelUtil excelUtil = new ExcelUtil();
        excelUtil.exportDetialAccount(request,response,result, Date1,Date2,MODELPath);
    }

    public String jointDirectionIdxSqlBySubjectCode(String subjectCode,String tableAlias, Map<Integer, Object> params, int paramsNo){
        StringBuffer directionIdxSql = new StringBuffer();
        if (subjectCode!=null&&!"".equals(subjectCode)) {
            if ("/".equals(subjectCode.substring(subjectCode.length()-1))) {
                subjectCode = subjectCode.substring(0,subjectCode.length()-1);
            }
            String[] subjectCodes = subjectCode.split("/");
            for (int i=0;i<subjectCodes.length;i++) {
                String s = (i<9)?"0"+(i+1):i+1+"";
                directionIdxSql.append(" AND "+tableAlias+".f"+s+" like ?" + paramsNo );
                params.put(paramsNo, subjectCodes[i]+"%");
                paramsNo++;
            }
        }
        return directionIdxSql.toString();
    }

    public String getFXString(String flag, BigDecimal bigDecimal){
        if (bigDecimal.compareTo(new BigDecimal("0.00"))!=0) {//不等于0
            if ("1".equals(flag)) {//原为借方余额
                if (bigDecimal.compareTo(new BigDecimal("0.00"))==-1) {//小于0
                    return "贷";
                } else {
                    return "借";
                }
            } else if ("2".equals(flag)){//原为贷方余额
                if (bigDecimal.compareTo(new BigDecimal("0.00"))==-1) {//小于0
                    return "贷";
                } else {
                    return "借";
                }
            } else {
                return "";
            }
        } else {
            return "平";
        }
    }

    private void setNeedSubjectList(List<Map<String, String>> needSubjectList, VoucherDTO dto, String accBookCode){
        StringBuffer needSubjectSql = new StringBuffer();

        if (dto.getSubjectNameP()!=null&&"1".equals(dto.getSubjectNameP())) {
            //科目名称全级显示
            needSubjectSql.append("SELECT CONCAT_WS('',s.all_subject,s.subject_code) AS subjectCode,s.direction AS direction,s.end_flag AS endFlag,s.subject_name AS subjectName,s.level AS level FROM subjectinfo s WHERE 1=1");

            int paramsNo = 1;
            Map<Integer, Object> params = new HashMap<>();

            needSubjectSql.append(" AND s.account = ?" + paramsNo);
            params.put(paramsNo, accBookCode);
            paramsNo++;

            if (dto.getItemCode1()!=null&&!"".equals(dto.getItemCode1())) {
                needSubjectSql.append(" AND CONCAT_WS('',s.all_subject,s.subject_code) >= ?" + paramsNo);
                params.put(paramsNo, dto.getItemCode1());
                paramsNo++;
            }
            /*needSubjectSql.append(" AND CONCAT_WS('',s.all_subject,s.subject_code) <= (SELECT CONCAT_WS('',ss.all_subject,ss.subject_code) AS subjectCodeAll FROM subjectinfo ss WHERE 1=1 AND ss.account = s.account");
            if (dto.getItemCode2()!=null&&!"".equals(dto.getItemCode2())) {
                needSubjectSql.append(" AND CONCAT_WS('',ss.all_subject,ss.subject_code) LIKE '"+dto.getItemCode2()+"%'");
            }
            needSubjectSql.append(" ORDER BY subjectCodeAll DESC LIMIT 1) ORDER BY subjectCode");*/
            if (dto.getItemCode2()!=null&&!"".equals(dto.getItemCode2())) {
                needSubjectSql.append(" AND CONCAT_WS('',s.all_subject,s.subject_code) <= ?" + paramsNo);
                params.put(paramsNo, dto.getItemCode2());
                paramsNo++;
            }

            needSubjectSql.append(" ORDER BY subjectCode");

            List<?> needSubjectSqlList = voucherRepository.queryBySqlSC(needSubjectSql.toString(), params);
            if (needSubjectSqlList!=null&&needSubjectSqlList.size()>0) {
                Map<String, String> mapTemp = new HashMap<>();
                String tempSql = "select * from subjectinfo s where 1=1 and s.account = ?1 and concat(s.all_subject,s.subject_code) = ?2 ";

                params = new HashMap<>();
                params.put(1, accBookCode);

                for (int i=0;i<needSubjectSqlList.size();i++) {
                    Map m = (Map) needSubjectSqlList.get(i);
                    String subjectCode = (String) m.get("subjectCode");
                    StringBuffer subjectName = new StringBuffer((String) m.get("subjectName"));
                    if (subjectCode.contains("/")) {
                        //查询结果是按科目代码排序的，当前处理的科目的上级科目的全称已处理过

                        if (mapTemp.containsKey(subjectCode.substring(0, subjectCode.lastIndexOf("/")))) {
                            subjectName.insert(0, mapTemp.get(subjectCode.substring(0, subjectCode.lastIndexOf("/")))+"/");
                        } else {
                            String newSubjectCode = subjectCode;
                            while (newSubjectCode.contains("/")) {
                                newSubjectCode = newSubjectCode.substring(0, newSubjectCode.lastIndexOf("/"));

                                params.put(2, newSubjectCode);

                                List<SubjectInfo> list = (List<SubjectInfo>) voucherRepository.queryBySql(tempSql, params, SubjectInfo.class);
                                subjectName.insert(0, list.get(0).getSubjectName()+"/");
                            }
                        }
                    }
                    Map<String, String> map = new HashMap<>();
                    map.put("subjectCode", subjectCode);
                    map.put("direction", (String) m.get("direction"));
                    map.put("endFlag", (String) m.get("endFlag"));
                    map.put("subjectName", subjectName.toString());
                    needSubjectList.add(i, map);
                    mapTemp.put(subjectCode, subjectName.toString());
                }
                for (int i=needSubjectSqlList.size()-1;i>=0;i--) {
                    Map m = (Map) needSubjectSqlList.get(i);
                    int statr = Integer.parseInt(dto.getLevel());
                    int end = Integer.parseInt(dto.getLevelEnd());
                    int level =  Integer.parseInt((String) m.get("level"));
                    if (level<statr || level>end) {
                        needSubjectList.remove(i);//要倒着删除
                    }
                }
            }
        } else {
            needSubjectSql.append("SELECT CONCAT_WS('',s.all_subject,s.subject_code) AS subjectCode,s.direction AS direction,s.end_flag AS endFlag,s.subject_name AS subjectName FROM subjectinfo s WHERE 1=1");

            int paramsNo = 1;
            Map<Integer, Object> params = new HashMap<>();

            needSubjectSql.append(" AND s.account = ?" + paramsNo);
            params.put(paramsNo, accBookCode);
            paramsNo++;

            if (dto.getItemCode1()!=null&&!"".equals(dto.getItemCode1())) {
                needSubjectSql.append(" AND CONCAT_WS('',s.all_subject,s.subject_code) >= ?" + paramsNo);
                params.put(paramsNo, dto.getItemCode1());
                paramsNo++;
            }
            /*needSubjectSql.append(" AND CONCAT_WS('',s.all_subject,s.subject_code) <= (SELECT CONCAT_WS('',ss.all_subject,ss.subject_code) AS subjectCodeAll FROM subjectinfo ss WHERE 1=1 AND ss.account = s.account");
            if (dto.getItemCode2()!=null&&!"".equals(dto.getItemCode2())) {
                needSubjectSql.append(" AND CONCAT_WS('',ss.all_subject,ss.subject_code) LIKE '"+dto.getItemCode2()+"%'");
            }
            needSubjectSql.append(" ORDER BY subjectCodeAll DESC LIMIT 1)");*/
            if (dto.getItemCode2()!=null&&!"".equals(dto.getItemCode2())) {
                needSubjectSql.append(" AND CONCAT_WS('',s.all_subject,s.subject_code) <= ?" + paramsNo);
                params.put(paramsNo, dto.getItemCode2());
                paramsNo++;
            }
            if (dto.getLevel()!=null&&!"".equals(dto.getLevel())) {
                needSubjectSql.append(" AND s.level >= ?" + paramsNo);
                params.put(paramsNo, dto.getLevel());
                paramsNo++;
            }
            if (dto.getLevelEnd()!=null&&!"".equals(dto.getLevelEnd())) {
                needSubjectSql.append(" AND s.level <= ?" + paramsNo);
                params.put(paramsNo, dto.getLevelEnd());
                paramsNo++;
            }

            needSubjectSql.append(" ORDER BY subjectCode");

            List<?> needSubjectSqlList = voucherRepository.queryBySqlSC(needSubjectSql.toString(), params);
            if (needSubjectSqlList!=null&&needSubjectSqlList.size()>0) {
                for (int i=0;i<needSubjectSqlList.size();i++) {
                    Map m = (Map) needSubjectSqlList.get(i);
                    Map<String, String> map = new HashMap<>();
                    map.put("subjectCode", (String) m.get("subjectCode"));
                    map.put("direction", (String) m.get("direction"));
                    map.put("endFlag", (String) m.get("endFlag"));
                    map.put("subjectName", (String) m.get("subjectName"));
                    needSubjectList.add(i, map);
                }
            }
        }
    }

    private List<String> getMonthList(String startYearMonth, String endYearMonth){
        List<String> monthList = new ArrayList<>();
        List<String> month = Arrays.asList("01","02","03","04","05","06","07","08","09","10","11","12","13","14");
        //起始月位置
        int startIndex = month.indexOf(startYearMonth.substring(4));
        //终止月位置
        int endIndex = month.indexOf(endYearMonth.substring(4));
        //判断是否跨年
        if (startYearMonth.substring(0,4).equals(endYearMonth.substring(0,4))) {//不跨年
            for (int i=startIndex;i<=endIndex;i++) {
                monthList.add(startYearMonth.substring(0,4) + month.get(i));
            }
        } else {//跨年
            int startYear = Integer.parseInt(startYearMonth.substring(0,4));
            int endYear = Integer.parseInt(endYearMonth.substring(0,4));
            for (int i=startYear;i<=endYear;i++) {
                String prefix = String.valueOf(i);//完整会计期间前缀
                if (i==startYear) {//起始年，从起始月到决算月
                    for (int j=startIndex;j<month.size();j++) {
                        monthList.add(prefix + month.get(j));
                    }
                } else if (i==endYear) {//终止年，从01月到终止月
                    for (int j=0;j<=endIndex;j++) {
                        monthList.add(prefix + month.get(j));
                    }
                } else {//中间年，会计月全部
                    for (int j=0;j<month.size();j++) {
                        monthList.add(prefix + month.get(j));
                    }
                }
            }
        }
        return monthList;
    }

    private void setRowDataByEndFlag(List<Object> result, boolean flag1, boolean flag2, String yearMonth, Map<String, String> map, BigDecimal debitDestYear, BigDecimal creditDestYear, BigDecimal balanceBeginDest){
        Map<String, Object> rowMap = new HashMap();
        if (flag1) {
            rowMap.put("centerCode", CurrentUser.getCurrentLoginManageBranch());
            rowMap.put("voucherDate", yearMonth);
            rowMap.put("directionIdx", map.get("subjectCode"));
            rowMap.put("directionIdxName", map.get("subjectName"));
            rowMap.put("remarkName", "期初余额");
            rowMap.put("balanceFX", getFXString(map.get("direction"), balanceBeginDest));
            rowMap.put("balanceDest", balanceBeginDest.abs().toString());
            result.add(rowMap);
        }

        if (flag2) {
            rowMap = new HashMap();
            rowMap.put("voucherDate", yearMonth);
            rowMap.put("directionIdx", map.get("subjectCode"));
            rowMap.put("directionIdxName", map.get("subjectName"));
            rowMap.put("remarkName", "本月合计");
            rowMap.put("debitDest", "0.00");
            rowMap.put("creditDest", "0.00");
            rowMap.put("balanceFX", getFXString(map.get("direction"), balanceBeginDest));
            rowMap.put("balanceDest", balanceBeginDest.abs().toString());
            result.add(rowMap);

            rowMap = new HashMap();
            rowMap.put("voucherDate", yearMonth);
            rowMap.put("directionIdx", map.get("subjectCode"));
            rowMap.put("directionIdxName", map.get("subjectName"));
            rowMap.put("remarkName", "本年累计");
            rowMap.put("debitDest", debitDestYear.toString());
            rowMap.put("creditDest", creditDestYear.toString());
            rowMap.put("balanceFX", getFXString(map.get("direction"), balanceBeginDest));
            rowMap.put("balanceDest", balanceBeginDest.abs().toString());
            result.add(rowMap);
        }
    }

    private void setRowDataByEndFlagAndBalanceBegin(List<Object> result, String yearMonth, Map<String, String> map, BigDecimal balanceBeginDest){
        Map<String, Object> rowMap = new HashMap();
        rowMap.put("centerCode", CurrentUser.getCurrentLoginManageBranch());
        rowMap.put("voucherDate", yearMonth);
        rowMap.put("directionIdx", map.get("subjectCode"));
        rowMap.put("directionIdxName", map.get("subjectName"));
        rowMap.put("remarkName", "期初余额");
        rowMap.put("balanceFX", getFXString(map.get("direction"), balanceBeginDest));
        rowMap.put("balanceDest", balanceBeginDest.abs().toString());
        result.add(rowMap);
    }

    private void setRowDataByEndFlagAndMonthAndYear(List<Object> result, String yearMonth, Map<String, String> map, BigDecimal debitDestMonth, BigDecimal creditDestMonth, BigDecimal debitDestYear, BigDecimal creditDestYear, BigDecimal balanceBeginDest){
        Map<String, Object> rowMap = new HashMap();
        rowMap = new HashMap();
        rowMap.put("voucherDate", yearMonth);
        rowMap.put("directionIdx", map.get("subjectCode"));
        rowMap.put("directionIdxName", map.get("subjectName"));
        rowMap.put("remarkName", "本月合计");
        rowMap.put("debitDest", debitDestMonth.toString());
        rowMap.put("creditDest", creditDestMonth.toString());
        rowMap.put("balanceFX", getFXString(map.get("direction"), balanceBeginDest));
        rowMap.put("balanceDest", balanceBeginDest.abs().toString());
        result.add(rowMap);
        rowMap = new HashMap();
        rowMap.put("voucherDate", yearMonth);
        rowMap.put("directionIdx", map.get("subjectCode"));
        rowMap.put("directionIdxName", map.get("subjectName"));
        rowMap.put("remarkName", "本年累计");
        rowMap.put("debitDest", debitDestYear.toString());
        rowMap.put("creditDest", creditDestYear.toString());
        rowMap.put("balanceFX", getFXString(map.get("direction"), balanceBeginDest));
        rowMap.put("balanceDest", balanceBeginDest.abs().toString());
        result.add(rowMap);
    }
}
