package com.sinosoft.service.impl.synthesize;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sinosoft.common.CurrentTime;
import com.sinosoft.common.CurrentUser;
import com.sinosoft.domain.SubjectInfo;
import com.sinosoft.domain.account.AccArticleBalanceHis;
import com.sinosoft.domain.account.AccDetailBalanceHis;
import com.sinosoft.domain.account.AccMonthTrace;
import com.sinosoft.domain.account.AccMonthTraceId;
import com.sinosoft.dto.VoucherDTO;
import com.sinosoft.repository.BranchInfoRepository;
import com.sinosoft.repository.VoucherRepository;
import com.sinosoft.repository.account.AccArticleBalanceHisRespository;
import com.sinosoft.repository.account.AccDetailBalanceHisRespository;
import com.sinosoft.repository.account.AccMonthTraceRespository;
import com.sinosoft.service.synthesize.QueryAccsumService;
import com.sinosoft.util.ExcelUtil;
import com.sinosoft.util.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;

@Service
public class QueryAccsumServiceImpl implements QueryAccsumService {
    private Logger logger = LoggerFactory.getLogger(QueryAccsumServiceImpl.class);
    @Resource
    private VoucherRepository voucherRepository;
    @Resource
    private AccMonthTraceRespository accMonthTraceRespository;
    @Resource
    private AccDetailBalanceHisRespository accDetailBalanceHisRespository;
    @Resource
    private AccArticleBalanceHisRespository accArticleBalanceHisRespository;
    @Resource
    private BranchInfoRepository branchInfoRepository;

    /*
        导出操作，为避免重复查询，临时存储校验查询结果，此结果即为导出数据，否则不可用
        key = 用户ID + 下划线 + 当前机构 + 下划线 + 当前核算单位 + 下划线 + 当前登录账套类型 + 下划线 + 当前登录账套编码
     */
    private Map<String, Object> exportDataMap;


    /**
     * 科目总账查询
     * @param dto
     * @return
     */
    @Override
    public List<?> queryAccsum(VoucherDTO dto){
        //resultData：List<Object>为科目分页集合，其内部第一项为科目代码，第二项为对应一级科目名称，第三项为该科目名称，第四项为该科目数据
        List<List<Object>> resultData = null;
        List<List<Object>> oldResultData= null;
        String centerCode1 = CurrentUser.getCurrentLoginManageBranch();
        String branchCode = CurrentUser.getCurrentLoginManageBranch();
        String accBookType = CurrentUser.getCurrentLoginAccountType();
        String accBookCode = CurrentUser.getCurrentLoginAccount();

        /*
            1.先根据查询条件查询可能需要展示哪些科目和科目对应的余额方向（1-借 2-贷），包括非末级科目（此时可能包含期间内没有录过凭证的科目，但也需要展示）
            2.再查询出科目范围内的（无层级限制）所有末级科目的余额、借贷本年累计
            3.再根据查询条件查询出凭证信息（会计期间、科目全代码、借方金额、贷方金额）
            4.再根据结果汇总结果处理形成最终的返回数据（余额计算、正负值、借贷方向、本月合计、本年累计、科目名称、摘要等的处理）
         */
        // 1.
        StringBuffer itemCode2Sql = new StringBuffer("SELECT CONCAT_WS('',s.all_subject,s.subject_code) AS subjectCodeAll FROM subjectinfo s WHERE 1=1");
        itemCode2Sql.append(" AND s.account = ?1");
        itemCode2Sql.append(" AND CONCAT_WS('',s.all_subject,s.subject_code) LIKE ?2 ORDER BY subjectCodeAll DESC LIMIT 1");

        Map<Integer, Object> params = new HashMap<>();
        params.put(1, accBookCode);
        params.put(2, dto.getItemCode2()+"%");

        List<?> itemCode2List = voucherRepository.queryBySqlSC(itemCode2Sql.toString(), params);
        if (itemCode2List!=null&&itemCode2List.size()>0) {
            dto.setItemCode2((String) ((Map) itemCode2List.get(0)).get("subjectCodeAll"));
        }

        StringBuffer sb = new StringBuffer("SELECT CONCAT_WS('',s.all_subject,s.subject_code) AS subjectCode,s.direction AS direction,s.subject_name AS subjectName,(SELECT s1.subject_name FROM subjectinfo s1 WHERE (s1.super_subject IS NULL OR s1.super_subject = '') AND s1.account = s.account AND s1.subject_code = SUBSTRING_INDEX(CONCAT_WS('',s.all_subject,s.subject_code), '/', 1)) AS superSubjectName FROM subjectinfo s WHERE 1=1");

        int paramsNo = 1;
        params = new HashMap<>();

        sb.append(" AND s.account = ?" + paramsNo);
        params.put(paramsNo, accBookCode);
        paramsNo++;

        if (dto.getItemCode1()!=null&&!"".equals(dto.getItemCode1())) {
            sb.append(" AND CONCAT_WS('',s.all_subject,s.subject_code) >= ?" + paramsNo);
            params.put(paramsNo, dto.getItemCode1());
            paramsNo++;
        }
        /*sb.append(" AND CONCAT_WS('',s.all_subject,s.subject_code) <= (SELECT CONCAT_WS('',ss.all_subject,ss.subject_code) AS subjectCodeAll FROM subjectinfo ss WHERE 1=1");
        sb.append(" AND ss.account = s.account");
        if (dto.getItemCode2()!=null&&!"".equals(dto.getItemCode2())) {
            sb.append(" AND CONCAT_WS('',ss.all_subject,ss.subject_code) LIKE '"+dto.getItemCode2()+"%'");
        }
        sb.append(" ORDER BY subjectCodeAll DESC LIMIT 1)");*/
        if (dto.getItemCode2()!=null&&!"".equals(dto.getItemCode2())) {
            sb.append(" AND CONCAT_WS('',s.all_subject,s.subject_code) <= ?" + paramsNo);
            params.put(paramsNo, dto.getItemCode2());
            paramsNo++;
        }
        if (dto.getLevel()!=null&&!"".equals(dto.getLevel())) {
            sb.append(" AND s.level >= ?" + paramsNo);
            params.put(paramsNo, dto.getLevel());
            paramsNo++;
        }
        if (dto.getLevelEnd()!=null&&!"".equals(dto.getLevelEnd())) {
            sb.append(" AND s.level <= ?" + paramsNo);
            params.put(paramsNo, dto.getLevelEnd());
            paramsNo++;
        }

        sb.append(" ORDER BY subjectCode");

        List<?> sbList = voucherRepository.queryBySqlSC(sb.toString(), params);
        if (sbList!=null&&sbList.size()>0) {
            List<List<String>> list = new ArrayList();
            for (Object o : sbList) {
                Map m = (Map) o;
                List<String> list1 = new ArrayList();
                list1.add(0, (String) m.get("subjectCode"));//科目全代码
                list1.add(1, (String) m.get("direction"));//余额方向
                list1.add(2, (String) m.get("superSubjectName"));//一级科目名称
                list1.add(3, (String) m.get("subjectName"));//科目名称
                list1.add(4, centerCode1);//核算单位
                list.add(list1);
            }
            List<String> subBranch = new ArrayList<>();
            //判断是否为汇总机构
            List<String>  summaryBranch = new ArrayList();
            summaryBranch = branchInfoRepository.findByLevel("1");
            if(summaryBranch.contains(centerCode1)){
                subBranch = branchInfoRepository.findBySuperCom(centerCode1);
            }else{
                subBranch.add(centerCode1);
            }
            //循环各个子机构(不是汇总机构只循环一次)
            int count =0;
            for(String centerCode : subBranch){
                branchCode = centerCode;
                count++;
                resultData = new ArrayList<List<Object>>();
                //2.
                //查询科目的年初余额（期初余额）,查询上个会计期间
                StringBuffer balanceSql = new StringBuffer("SELECT REVERSE(SUBSTRING(REVERSE(sc.subjectCode),2)) AS subjectCode, IFNULL(a.debit_dest_year,0.00) AS debitDestYear, IFNULL(a.credit_dest_year,0.00) AS creditDestYear, IFNULL(a.balance_dest,0.00) AS balanceBeginDest FROM (SELECT CONCAT_WS('',s.all_subject,s.subject_code,'/') AS subjectCode FROM subjectinfo s WHERE 1=1");
                balanceSql.append(" AND s.account = ?1");
                balanceSql.append(" AND CONCAT_WS('',s.all_subject,s.subject_code) >= ?2");
                balanceSql.append(" AND CONCAT_WS('',s.all_subject,s.subject_code) <= ?3");
                balanceSql.append(" AND s.end_flag = '0'");
                balanceSql.append(" ORDER BY subjectCode) sc");
                balanceSql.append(" LEFT JOIN accdetailbalance a ON a.direction_idx = sc.subjectCode");
                balanceSql.append(" AND a.center_code = ?4");
                balanceSql.append(" AND a.branch_code = ?5");
                balanceSql.append(" AND a.acc_book_type = ?6");
                balanceSql.append(" AND a.acc_book_code = ?7");

                String lastYearMonth = "";
                if (dto.getYearMonth()!=null&&!"".equals(dto.getYearMonth())) {
                    String cy = dto.getYearMonth().substring(0,4);//当前年
                    if ((cy+"01").equals(dto.getYearMonth())) {
                        //当前年一月会计期间
                        /*lastYearMonth = Integer.valueOf(cy)-1+"JS";*/
                        lastYearMonth = Integer.valueOf(cy)-1+"14";
                    } else if ((cy+"JS").equals(dto.getYearMonth())) {
                        //当前年决算月会计期间
                        lastYearMonth = cy+"12";
                    } else {
                        lastYearMonth = Integer.valueOf(dto.getYearMonth())-1+"";
                    }
                } else {
                    //默认当前年一月会计期间，则查询上年决算月
                    /*lastYearMonth = Integer.valueOf(cy)-1 + "JS";*/
                    lastYearMonth = Integer.valueOf(CurrentTime.getCurrentYear())-1 + "14";
                }
                balanceSql.append(" AND a.year_month_date = ?8");
                String balanceStr = balanceSql.toString();
                //如此会计期间已结转侧需要替换表名
                AccMonthTraceId mid = new AccMonthTraceId();
                mid.setCenterCode(centerCode);
                mid.setAccBookType(CurrentUser.getCurrentLoginAccountType());
                mid.setAccBookCode(accBookCode);
                mid.setYearMonthDate(lastYearMonth);
                try {
                    AccMonthTrace accMonthTrace = accMonthTraceRespository.findById(mid).get();
                    if ("3".equals(accMonthTrace.getAccMonthStat()) || "5".equals(accMonthTrace.getAccMonthStat())) {
                        balanceStr = balanceStr.replaceAll("accdetailbalance","accdetailbalancehis");
                    }
                } catch (Exception e) {
                    balanceStr = balanceStr.replaceAll("accdetailbalance","accdetailbalancehis");
                    System.out.println("查询并获取会计期间("+lastYearMonth+")数据异常，当前会计期间可能不存在！默认按已结转处理");
                }

            params = new HashMap<>();
            params.put(1, accBookCode);
            params.put(2, dto.getItemCode1());
            params.put(3, dto.getItemCode2());
            params.put(4, centerCode);
            params.put(5, branchCode);
            params.put(6, accBookType);
            params.put(7, accBookCode);
            params.put(8, lastYearMonth);

            List<?> balanceSqlList = voucherRepository.queryBySqlSC(balanceStr, params);
            Map<String,List<BigDecimal>> balanceMap = new HashMap();
            List<String> directionIdxList = new ArrayList<>();
            if (balanceSqlList!=null&&balanceSqlList.size()>0) {
                for (Object o : balanceSqlList) {
                    Map m = (Map) o;
                    List<BigDecimal> list1 = new ArrayList();
                    list1.add(0, (BigDecimal) m.get("debitDestYear"));//本位币本年借方金额
                    list1.add(1, (BigDecimal) m.get("creditDestYear"));//本位币本年贷方金额
                    list1.add(2, (BigDecimal) m.get("balanceBeginDest"));//本位币期初余额
                    String subjectCode = (String) m.get("subjectCode");
                    balanceMap.put(subjectCode, list1);
                    directionIdxList.add(subjectCode);
                }
            }

            // 3.
            //如果查询结果中存在JS的则将会计期间其修改为对应年度12月（已通过SQL直接处理）
            StringBuffer sql = new StringBuffer("SELECT IF(RIGHT(a.year_month_date,2)='JS', CONCAT(LEFT(a.year_month_date,4),'12'),a.year_month_date) AS yearMonthDate, REVERSE(SUBSTRING(REVERSE(a.direction_idx),2)) AS directionIdx,SUM(a.debit_dest) AS debitDest,SUM(a.credit_dest) AS creditDest,(SUM(a.debit_dest)-SUM(a.credit_dest)) AS balance FROM accsubvoucher a LEFT JOIN accmainvoucher am ON am.center_code = a.center_code AND am.branch_code = a.branch_code AND am.acc_book_type = a.acc_book_type AND am.acc_book_code = a.acc_book_code AND am.year_month_date = a.year_month_date AND am.voucher_no = a.voucher_no WHERE 1=1");

            paramsNo = 1;
            params = new HashMap<>();

            sql.append(" AND a.center_code = ?" + paramsNo);
            params.put(paramsNo, centerCode);
            paramsNo++;
            sql.append(" AND a.branch_code = ?" + paramsNo);
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
            sql.append(" AND a.direction_idx IN (SELECT CONCAT_WS('',s.all_subject,s.subject_code,'/') AS subjectCode FROM subjectinfo s WHERE 1=1");
            sql.append(" AND s.account = a.acc_book_code");
            if (dto.getItemCode1()!=null&&!"".equals(dto.getItemCode1())) {
                sql.append(" AND CONCAT_WS('',s.all_subject,s.subject_code) >= ?" + paramsNo);
                params.put(paramsNo, dto.getItemCode1());
                paramsNo++;
            }
            if (dto.getItemCode2()!=null&&!"".equals(dto.getItemCode2())) {
                sql.append(" AND CONCAT_WS('',s.all_subject,s.subject_code) <= ?" + paramsNo);
                params.put(paramsNo, dto.getItemCode2());
                paramsNo++;
            }

            sql.append(" AND s.end_flag = '0' ORDER BY subjectCode)");

            if (dto.getVoucherGene()!=null&&!"".equals(dto.getVoucherGene())) {
                if ("0".equals(dto.getVoucherGene())) {
                    sql.append(" AND am.voucher_flag = '3'");
                } else {
                    sql.append(" AND am.voucher_flag in ('1','2','3')");
                }
            }
            sql.append(" GROUP BY directionIdx,yearMonthDate");

            //联查历史表数据
            String str = sql.toString();
            str = str.replaceAll("accsubvoucher","accsubvoucherhis");
            str = str.replaceAll("a\\.","ah\\.");
            str = str.replaceAll(" a "," ah ");
            str = str.replaceAll("accmainvoucher","accmainvoucherhis");
            str = str.replaceAll("am\\.","amh\\.");
            str = str.replaceAll(" am "," amh ");

            sql.append(" UNION ALL ");
            sql.append(str);

            sql.append(" ORDER BY directionIdx,yearMonthDate");

            List<Object> sqlList = (List<Object>) voucherRepository.queryBySqlSC(sql.toString(), params);
            if (sqlList!=null&&sqlList.size()>0) {
                for (Object obj : sqlList) {
                    Map m = (Map) obj;
                    String subjectCode = (String) m.get("directionIdx");
                    if (directionIdxList.contains(subjectCode)) {
                        directionIdxList.remove(subjectCode);
                    }
                }
                if (directionIdxList.size()>0) {
                    //期间内没有录入凭证的科目也需要展示
                    for (String s : directionIdxList) {
                        Map sMap = new HashMap();
                        sMap.put("yearMonthDate", dto.getYearMonth());
                        sMap.put("directionIdx", s);
                        sMap.put("debitDest", new BigDecimal("0.00"));
                        sMap.put("creditDest", new BigDecimal("0.00"));
                        sMap.put("balance", new BigDecimal("0.00"));
                        sqlList.add(sMap);
                    }
                }
            }
            if (sqlList!=null&&sqlList.size()>0) {
                /*
                    containsKey(Object key) :如果此映射包含对于指定键的映射关系，则返回 true
                    get(Object key)         :返回指定键所映射的值；如果对于该键来说，此映射不包含任何映射关系，则返回 null
                */
                /*
                    resultMap：第一层为科目集合
                               第二层为会计期间集合（包含年初/期初）
                               第三层为年初余额(期初余额)、余额、本月合计借方金额、本月合计贷方金额
                 */
                Map<String, Map<String, Map<String, Object>>> resultMap = new HashMap();
                //会计期间集合（包含年初/期初）
                Map<String, Map<String, Object>> map1 = new HashMap<>();
                //年初余额(期初余额)、余额、本月合计借方金额、本月合计贷方金额、本年累计借方金额、本年累计贷方金额集合
                Map<String, Object> map2 = new HashMap<>();
                String oldDirectionIdx = "";
                BigDecimal debitDestYear = new BigDecimal("0.00");//本位币本年借方金额
                BigDecimal creditDestYear = new BigDecimal("0.00");//本位币本年贷方金额
                BigDecimal balanceBeginDest = new BigDecimal("0.00");//本位币期初余额
                boolean flag = true;//用于表示是否追加年初余额（期初余额）数据：true-表示需要
                for (Object object : sqlList) {
                    //在科目全代码相同的情况下，每循环一次，既是一个会计期间
                    //每循环一次新 new 一个 map3（当科目全代码不同时多一次期初的）
                    //每循环一个科目 新 new 一个 map2
                    Map m = (Map) object;
                    String yearMonthDate = (String) m.get("yearMonthDate");//会计期间
                    String directionIdx = (String) m.get("directionIdx");//科目全代码
                    BigDecimal debitDest = (BigDecimal) m.get("debitDest");//本位币借方金额本月合计
                    BigDecimal creditDest = (BigDecimal) m.get("creditDest");//本位币贷方金额本月合计
                    BigDecimal balance = (BigDecimal) m.get("balance");//本位币本月合计余额（始终是debitDest-creditDest）
                    if (!directionIdx.equals(oldDirectionIdx)) {
                        //不同，即表示与上一个科目不同或者此循环第一条数据
                        //如果 map2 有数据则 put 到 resultMap 中
                        if (map1.size()>0) {
                            resultMap.put(oldDirectionIdx, map1);
                            map1 = new HashMap<>();
                        }
                        oldDirectionIdx = directionIdx;
                        //取该科目的年初余额（期初余额）,查询上个会计期间
                        if (balanceMap.containsKey(directionIdx)) {
                            List<BigDecimal> directionIdxBalance = balanceMap.get(directionIdx);
                            if (!"01".equals(dto.getYearMonth().substring(4))) {
                                debitDestYear = directionIdxBalance.get(0);
                                creditDestYear = directionIdxBalance.get(1);
                            }
                            balanceBeginDest = directionIdxBalance.get(2);
                        }
                        flag = true;
                        map2 = new HashMap<>();
                        map2.put("debitDestYear", debitDestYear);
                        map2.put("creditDestYear", creditDestYear);
                        map2.put("balanceBeginDest", balanceBeginDest);

                        map1.put("begin", map2);
                    } else {
                        flag = false;
                    }
                    map2 = new HashMap<>();
                    map2.put("debitDest", debitDest);
                    map2.put("creditDest", creditDest);
                    map2.put("balance", balance);

                    map1.put(yearMonthDate, map2);

                    //有凭证分录的末级科目处理后，再做向上汇总处理，直到汇总到一级科目
                    if (directionIdx.contains("/")) {//如果不包含“/”则无需汇总处理（因为本就是一级科目）
                        String newSubjectCode = directionIdx;
                        while (newSubjectCode.contains("/")) {
                            newSubjectCode = newSubjectCode.substring(0,newSubjectCode.lastIndexOf("/"));
                            /*
                                3.1、先去 resultMap 中寻找有没有该科目的集合 map1
                                    如果没有则创建一个新的 map3 ，否则获取该集合
                                3.2、判断 map3 有没有期初(begin)，没有则新创建一个新的 map4 并追加
                                        否则，再根据 flag 判断，若果为 true 则取得 map2 并汇总计算处理，否则不处理
                                            本位币本年借方金额 debitDestYearcreditDestYear
                                            本位币本年贷方金额 creditDestYear
                                            本位币期初余额 balanceBeginDest
                                     判断 map3 有没有当前循环会计期间，没有则新创建一个新的 map4 并追加，否则取得 map2 并汇总计算处理
                                        本位币借方金额本月合计 debitDest
                                        本位币贷方金额本月合计 creditDest
                                        本位币本月合计余额（始终是debitDest-creditDest）balance
                             */
                            //会计期间集合（包含年初/期初）
                            Map<String, Map<String, Object>> map3 = new HashMap<>();
                            Map<String, Object> map4 = new HashMap<>();

                            if (resultMap.containsKey(newSubjectCode)) {
                                map3 = resultMap.get(newSubjectCode);
                            } else {
                                map3 = new HashMap<>();
                            }
                            if (map3.containsKey("begin")) {
                                if (flag) {
                                    map4 = map3.get("begin");
                                    map4.put("debitDestYear", debitDestYear.add((BigDecimal) map4.get("debitDestYear")));
                                    map4.put("creditDestYear", creditDestYear.add((BigDecimal) map4.get("creditDestYear")));
                                    map4.put("balanceBeginDest", balanceBeginDest.add((BigDecimal) map4.get("balanceBeginDest")));

                                    map3.put("begin", map4);
                                }
                            } else {
                                map4 = new HashMap<>();
                                map4.put("debitDestYear", debitDestYear);
                                map4.put("creditDestYear", creditDestYear);
                                map4.put("balanceBeginDest", balanceBeginDest);

                                map3.put("begin", map4);
                            }
                            if (map3.containsKey(yearMonthDate)) {
                                map4 = new HashMap<>();
                                map4 = map3.get(yearMonthDate);
                                map4.put("debitDest", debitDest.add((BigDecimal) map4.get("debitDest")));
                                map4.put("creditDest", creditDest.add((BigDecimal) map4.get("creditDest")));
                                map4.put("balance", balance.add((BigDecimal) map4.get("balance")));

                                map3.put(yearMonthDate, map4);
                            } else {
                                map4 = new HashMap<>();
                                map4.put("debitDest", debitDest);
                                map4.put("creditDest", creditDest);
                                map4.put("balance", balance);

                                map3.put(yearMonthDate, map4);
                            }

                            if (map3.size()>0) {
                                resultMap.put(newSubjectCode, map3);
                                map3 = new HashMap<>();
                                map4 = new HashMap<>();
                            }
                        }
                    }
                }
                if (map1.size()>0) {
                    resultMap.put(oldDirectionIdx, map1);
                }

                    // 4.
                    if (resultMap.size()>0) {
                        setResultData(resultData, resultMap, list, dto.getYearMonth(), dto.getYearMonthDate());
                    }
                }
                //最后在结尾累加各机构数值,并修改 方向“平”，“借”，“贷”
                if(count ==1){
                    try {
                        oldResultData= CommonUtil.deepCopy(resultData);
                    }catch (Exception e){
                    }
                }

                if(resultData.size()>0 && count>1){

                    for(int index =0;index<resultData.size();index++) {
                        ArrayList<HashMap<String,String>>  list1 = (ArrayList<HashMap<String,String>>)oldResultData.get(index).get(4);
                        ArrayList<HashMap<String,String>>  list2 = (ArrayList<HashMap<String,String>>)resultData.get(index).get(4);
                        for(int num =0;num<list2.size();num++) {
                            HashMap<String,String>  map1 = (HashMap<String,String>)list1.get(num);
                            HashMap<String,String>  map2 = (HashMap<String,String>)list2.get(num);
                            map1.put("balanceDest", CommonUtil.numAdd(map1.get("balanceDest"),map2.get("balanceDest")));
                            map1.put("debitDest", CommonUtil.numAdd(map1.get("debitDest"),map2.get("debitDest")));
                            map1.put("creditDest", CommonUtil.numAdd(map1.get("creditDest"),map2.get("creditDest")));

                            //修改 方向
                            if (list.size()>0) {
                                List<String> list3 = list.get(index);
                                map1.put("balanceFX", setFX(list3.get(1), new BigDecimal(map1.get("balanceDest"))));
                            }

                        }
                    }
                }
            }
        }
        return oldResultData;
    }


    @Override
    public String isHasData(VoucherDTO dto) {
        List<?> list = queryAccsum(dto);
        if (list != null && list.size()>0){
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

    private void setResultData(List list, Map<String, Map<String, Map<String, Object>>> resultDataMap, List<List<String>> subjectDataList, String start, String end){
        /*
            resultDataMap 为查询结果数据（半处理）
            subjectDataList 为含非末级的科目数据（科目全代码、科目余额方向、一级科目名称、科目名称）
         */
        if (resultDataMap.size()>0) {
            if (subjectDataList.size()>0) {
                for (Object obj : subjectDataList) {
                    List<String> list1 = (List<String>)obj;//科目数据 0-科目代码 1-余额方向 2- 科目名称
                    if (resultDataMap.containsKey(list1.get(0))) {//subjectDataList 中科目同时存在于 resultDataMap 中
                        List<Map<String, Object>> subjectList = new ArrayList<>();

                        BigDecimal debitDestYear = new BigDecimal("0.00");
                        BigDecimal creditDestYear = new BigDecimal("0.00");
                        BigDecimal balanceBeginDest = new BigDecimal("0.00");

                        Map<String, Map<String, Object>> map = resultDataMap.get(list1.get(0));

                        Map<String, Object> map1 = map.get("begin");
                        debitDestYear = debitDestYear.add((BigDecimal) map1.get("debitDestYear"));
                        creditDestYear = creditDestYear.add((BigDecimal) map1.get("creditDestYear"));
                        balanceBeginDest = balanceBeginDest.add((BigDecimal) map1.get("balanceBeginDest"));

                        //年初/期初
                        Map<String, Object> map2 = new HashMap<>();
                        map2.put("yearMonthDate", "");
                        map2.put("remarkName", ("01".equals(start.substring(4)))?"年初余额":"期初余额");
                        map2.put("debitDest", "");
                        map2.put("creditDest", "");
                        map2.put("balanceFX", setFX(list1.get(1), balanceBeginDest));
                        map2.put("balanceDest", (balanceBeginDest.abs()).toString());
                        subjectList.add(map2);

                        //从开始到结束的会计期间行处理（本月合计、本年累计）
                        List<String> monthList = Arrays.asList("01","02","03","04","05","06","07","08","09","10","11","12","13","14");
                        int startIndex = monthList.indexOf(start.substring(4));
                        int endIndex = monthList.indexOf(end.substring(4));
                        String prefix = start.substring(0,4);
                        for (int i=startIndex;i<=endIndex;i++) {
                            String yearMonthDate = prefix + monthList.get(i);
                            if (map.containsKey(yearMonthDate)) {
                                Map<String, Object> dataMap = map.get(yearMonthDate);

                                balanceBeginDest = balanceBeginDest.add((BigDecimal) dataMap.get("balance"));
                                debitDestYear = debitDestYear.add((BigDecimal) dataMap.get("debitDest"));
                                creditDestYear = creditDestYear.add((BigDecimal) dataMap.get("creditDest"));

                                setAddRowMap(subjectList, dataMap, yearMonthDate, list1.get(1), balanceBeginDest, debitDestYear, creditDestYear);
                            } else {
                                Map<String, Object> dataMap = new HashMap<>();
                                dataMap.put("debitDest", new BigDecimal("0.00"));
                                dataMap.put("creditDest", new BigDecimal("0.00"));
                                setAddRowMap(subjectList, dataMap, yearMonthDate, list1.get(1), balanceBeginDest, debitDestYear, creditDestYear);
                            }
                        }
                        if (subjectList.size()>0) {
                            List<Object> pageList = new ArrayList<>();
                            pageList.add(0, list1.get(0));//科目代码
                            pageList.add(1, list1.get(2));//一级科目名称
                            pageList.add(2, list1.get(3));//科目名称
                            pageList.add(3, list1.get(4));//核算单位
                            pageList.add(4, subjectList);//科目数据
                            list.add(pageList);
                        }
                    } else {
                        //subjectDataList 中科目在 resultDataMap 中不存在
                    }
                }
            }
        }
    }

    private void setAddRowMap(List<Map<String, Object>> subjectList, Map<String, Object> dataMap, String yearMonthDate, String direction, BigDecimal balance, BigDecimal debitDestYear, BigDecimal creditDestYear){
        Map<String, Object> rowMap = new HashMap<>();
        rowMap.put("yearMonthDate", yearMonthDate);
        rowMap.put("remarkName", "本月合计");
        rowMap.put("debitDest", dataMap.get("debitDest").toString());
        rowMap.put("creditDest", dataMap.get("creditDest").toString());
        rowMap.put("balanceFX", setFX(direction, balance));
        rowMap.put("balanceDest", (balance.abs()).toString());
        subjectList.add(rowMap);

        rowMap = new HashMap<>();
        rowMap.put("yearMonthDate", yearMonthDate);
        rowMap.put("remarkName", "本年累计");
        rowMap.put("debitDest", debitDestYear.toString());
        rowMap.put("creditDest", creditDestYear.toString());
        rowMap.put("balanceFX", setFX(direction, balance));
        rowMap.put("balanceDest", (balance.abs()).toString());
        subjectList.add(rowMap);
    }

    private String setFX(String flag, BigDecimal bigDecimal){
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

    private void export(HttpServletRequest request, HttpServletResponse response){

    }

    @Override
    public void download(HttpServletRequest request, HttpServletResponse response, String name, String queryConditions) {
        VoucherDTO dto = new VoucherDTO();
        System.out.println("queryConditions:"+queryConditions);
        //将queryConditions中的条件转到VoucherDto中
        try {
            dto = new ObjectMapper().readValue(queryConditions,VoucherDTO.class);

        } catch (IOException e) {
            e.printStackTrace();
        }

        //查询结果集
        List<List<Object>> result;
        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String branchCode = CurrentUser.getCurrentLoginManageBranch();
        String accBookType = CurrentUser.getCurrentLoginAccountType();
        String accBookCode = CurrentUser.getCurrentLoginAccount();
        String key = CurrentUser.getCurrentUser().getId()+"_"+centerCode+"_"+branchCode+"_"+accBookType+"_"+accBookCode;
        if (exportDataMap!=null && exportDataMap.get(key)!=null) {
            result = (List) exportDataMap.get(key);
            exportDataMap.put(key, null); // 使用之后便清除，减少内存占用
        } else {
            result = (List<List<Object>>)queryAccsum(dto);
        }

        ExcelUtil excelUtil = new ExcelUtil();
        excelUtil.exportAccSum(request,response,name,result);
    }
}
