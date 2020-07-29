package com.sinosoft.service.impl.synthesize;

import com.sinosoft.common.CurrentUser;
import com.sinosoft.dto.VoucherDTO;
import com.sinosoft.repository.BranchInfoRepository;
import com.sinosoft.repository.VoucherRepository;
import com.sinosoft.service.VoucherService;
import com.sinosoft.service.synthesize.DetailAccountService;
import com.sinosoft.service.synthesize.SpecialSubjectDetailAccountService;
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
public class SpecialSubjectDetailAccountServiceImpl implements SpecialSubjectDetailAccountService {
    private Logger logger = LoggerFactory.getLogger(SpecialSubjectDetailAccountServiceImpl.class);
    @Resource
    private VoucherRepository voucherRepository;
    @Resource
    private DetailAccountService detailAccountService;
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
     * 专项科目明细账查询
     * @param dto
     * @return
     */
    @Override
    public List<?> querySpecialSubjectDetailAccountList(VoucherDTO dto){
        long time = System.currentTimeMillis();
        List<String> centerCode = getSubBranch();
        List<String> branchCode = centerCode;
        String accBookType = CurrentUser.getCurrentLoginAccountType();
        String accBookCode = CurrentUser.getCurrentLoginAccount();

        Map<String, String> specialNameMap = new HashMap<>();
        if ((dto.getSpecialNameP()!=null&&"0".equals(dto.getSpecialNameP())) && (dto.getSpecialCode()!=null&&!"".equals(dto.getSpecialCode())) && (dto.getSpecialName()!=null&&!"".equals(dto.getSpecialName()))) {
            String[] codes = dto.getSpecialCode().split(",");
            String[] names = dto.getSpecialName().split(",");
            for (int i=0;i<codes.length;i++) {
                specialNameMap.put(codes[i], names[i]);
            }
        }

        List<Object> result = new ArrayList<Object>();
        //录入了科目代码，并且需要按录入的顺序排序
        if (dto.getSubjectCode()!=null&&!"".equals(dto.getSubjectCode())) {
            List<String> subjectCodeSortIn = new ArrayList<>();//科目排序顺序
            List<String> specialCodeSortIn = new ArrayList<>();//专项排序顺序
            Map<String, String> subjectDirectionMap = new HashMap<>();//科目余额方向
            //1.先查询出科目范围内可存在明细的科目和余额方向
            StringBuffer needSubjectCodeSql = new StringBuffer("SELECT CONCAT_WS('',s.all_subject,s.subject_code,'/') AS subjectCode,s.direction FROM subjectinfo s WHERE 1=1");
            needSubjectCodeSql.append(" AND s.account = ?1 AND s.end_flag ='0' AND s.special_id IS NOT NULL AND s.special_id!=''");
            needSubjectCodeSql.append(" AND ( CONCAT_WS('',s.all_subject,s.subject_code) LIKE ?2 ) ORDER BY subjectCode");

            Map<Integer, Object> params = new HashMap<>();
            params.put(1, accBookCode);

            String[] subjectCodes = dto.getSubjectCode().split(",");
            for (int i=0;i<subjectCodes.length;i++) {
                String str = subjectCodes[i];
                if ("/".equals(str.substring(str.length()-1))) {
                    params.put(2, str.substring(0,str.length()-1)+"%");
                } else {
                    params.put(2, str+"%");
                }
                List<?> needSubjectCodeSqlList = voucherRepository.queryBySqlSC(needSubjectCodeSql.toString(), params);
                if (needSubjectCodeSqlList!=null&&needSubjectCodeSqlList.size()>0) {
                    for (Object obj : needSubjectCodeSqlList) {
                        Map m = (Map) obj;
                        String code = (String) m.get("subjectCode");
                        subjectCodeSortIn.add(code);
                        subjectDirectionMap.put(code, (String) m.get("direction"));
                    }
                }
            }

            //如果录入了专项，同时把专项排序顺序处理下
            if (dto.getSpecialName()!=null&&!"".equals(dto.getSpecialName()) && dto.getSpecialCode()!=null&&!"".equals(dto.getSpecialCode())) {
                String[] strings = dto.getSpecialCode().split(",");
                for (String str : strings) {
                    specialCodeSortIn.add(str);
                }
            }

            //2.查询凭证信息
            StringBuffer sql = new StringBuffer("SELECT am.voucher_date AS voucherDate,a.year_month_date AS yearMonthDate,a.voucher_no AS voucherNo,a.suffix_no AS suffixNo,a.direction_idx AS directionIdx,a.direction_idx_name AS directionIdxName, SUBSTRING_INDEX(SUBSTRING_INDEX(a.direction_other,',',sp.id+1),',',-1) AS directionOther,a.remark AS remarkName,a.debit_dest AS debitDest,a.credit_dest AS creditDest,(a.debit_dest-a.credit_dest) AS balance,a.center_code AS centerCode FROM accsubvoucher a");
            sql.append(" LEFT JOIN accmainvoucher am ON am.center_code = a.center_code AND am.branch_code = a.branch_code AND am.acc_book_type = a.acc_book_type AND am.acc_book_code = a.acc_book_code AND am.year_month_date = a.year_month_date AND am.voucher_no = a.voucher_no");
            sql.append(" JOIN splitstringsort sp ON sp.id<(LENGTH(a.direction_other)-LENGTH(REPLACE(a.direction_other,',',''))+1) WHERE 1=1");

            int sqlParamsNo = 1;
            Map<Integer, Object> sqlParams = new HashMap<>();

            sql.append(" AND a.center_code in (?" + sqlParamsNo +")");
            sqlParams.put(sqlParamsNo, centerCode);
            sqlParamsNo++;
            sql.append(" AND a.branch_code in (?" + sqlParamsNo +")");
            sqlParams.put(sqlParamsNo, branchCode);
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
            if (dto.getVoucherGene()!=null&&!"".equals(dto.getVoucherGene())) {
                if ("0".equals(dto.getVoucherGene())) {
                    sql.append(" AND am.voucher_flag = '3'");
                } else {
                    sql.append(" AND am.voucher_flag in ('1','2','3')");
                }
            }
            //专项段不为空的
            sql.append(" AND (a.direction_other IS NOT NULL OR a.direction_other !='')");

            if (dto.getRemarkName()!=null&&!"".equals(dto.getRemarkName())) {
                sql.append(" AND a.remark LIKE ?" + sqlParamsNo);
                sqlParams.put(sqlParamsNo, "%"+dto.getRemarkName()+"%");
                sqlParamsNo++;
            }
            //金额方向
            if (dto.getSourceDirection()!=null&&!"".equals(dto.getSourceDirection())) {
                if ("1".equals(dto.getSourceDirection())) {//借方金额
                    if (dto.getMoneyStart()!=null&&!"".equals(dto.getMoneyStart())) {
                        sql.append(" AND a.debit_dest >= ?" + sqlParamsNo);
                        sqlParams.put(sqlParamsNo, dto.getMoneyStart());
                        sqlParamsNo++;
                    }
                    if (dto.getMoneyEnd()!=null&&!"".equals(dto.getMoneyEnd())) {
                        sql.append(" AND a.debit_dest <= ?" + sqlParamsNo);
                        sqlParams.put(sqlParamsNo, dto.getMoneyEnd());
                        sqlParamsNo++;
                    }
                } else {//贷方金额
                    if (dto.getMoneyStart()!=null&&!"".equals(dto.getMoneyStart())) {
                        sql.append(" AND a.credit_dest >= ?" + sqlParamsNo);
                        sqlParams.put(sqlParamsNo, dto.getMoneyStart());
                        sqlParamsNo++;
                    }
                    if (dto.getMoneyEnd()!=null&&!"".equals(dto.getMoneyEnd())) {
                        sql.append(" AND a.credit_dest <= ?" + sqlParamsNo);
                        sqlParams.put(sqlParamsNo, dto.getMoneyEnd());
                        sqlParamsNo++;
                    }
                }
            } else {
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
            }
            if (dto.getSpecialName()!=null&&!"".equals(dto.getSpecialName())) {
                //拼接明细对象SQL（OR）
                detailAccountService.jointQuertSqlBySpecialCodes(sql, dto, "a", sqlParams, sqlParamsNo);
                if (sqlParamsNo != sqlParams.size()+1) { sqlParamsNo = sqlParams.size()+1; }
            }

            sql.append(" AND a.direction_idx IN( ?" + sqlParamsNo + " )");
            sqlParams.put(sqlParamsNo, subjectCodeSortIn);
            sqlParamsNo++;

            ///联查历史表数据
            String sqlStr = sql.toString();
            sqlStr = sqlStr.replaceAll("accsubvoucher","accsubvoucherhis");
            sqlStr = sqlStr.replaceAll("a\\.","ah\\.");
            sqlStr = sqlStr.replaceAll(" a "," ah ");
            sqlStr = sqlStr.replaceAll("accmainvoucher","accmainvoucherhis");
            sqlStr = sqlStr.replaceAll("am\\.","amh\\.");
            sqlStr = sqlStr.replaceAll(" am "," amh ");

            sql.append(" UNION ALL ");
            sql.append(sqlStr);

            if (dto.getAccounRules()!=null&&!"".equals(dto.getAccounRules())) {
                if ("1".equals(dto.getAccounRules())) {//按科目汇总
                    if (specialCodeSortIn.size()>0) {
                        sql.append(" ORDER BY FIELD(directionIdx, ?" + sqlParamsNo + " )");
                        sqlParams.put(sqlParamsNo, subjectCodeSortIn);
                        sqlParamsNo++;
                        sql.append(" ,FIELD(directionOther, ?" + sqlParamsNo + " ),yearMonthDate,voucherNo");
                        sqlParams.put(sqlParamsNo, specialCodeSortIn);
                        sqlParamsNo++;
                    } else {
                        sql.append(" ORDER BY FIELD(directionIdx, ?" + sqlParamsNo + " ),directionOther,yearMonthDate,voucherNo");
                        sqlParams.put(sqlParamsNo, subjectCodeSortIn);
                        sqlParamsNo++;
                    }
                } else {//按专项汇总
                    if (specialCodeSortIn.size()>0) {
                        sql.append(" ORDER BY FIELD(directionOther, ?" + sqlParamsNo + " )");
                        sqlParams.put(sqlParamsNo, specialCodeSortIn);
                        sqlParamsNo++;
                        sql.append(" ,FIELD(directionIdx, ?" + sqlParamsNo + " ),yearMonthDate,voucherNo");
                        sqlParams.put(sqlParamsNo, subjectCodeSortIn);
                        sqlParamsNo++;
                    } else {
                        sql.append(" ORDER BY directionOther,FIELD(directionIdx, ?" + sqlParamsNo + " ),yearMonthDate,voucherNo");
                        sqlParams.put(sqlParamsNo, subjectCodeSortIn);
                        sqlParamsNo++;
                    }
                }
            }

            List<?> list = voucherRepository.queryBySqlSC(sql.toString(), sqlParams);
            if (list!=null&&list.size()>0) {
                //查询区间内有数据
                //先处理出所有可能的专项科目以及对应的借贷本年累计、余额数据
                //所有可能需要展示的的专项科目(科目可以有哪些专项，通过一级专项去匹配有哪些末级专项)
                StringBuffer needSpecialSubjectSql = new StringBuffer("SELECT DISTINCT temp1.subjectCode,s2.special_code AS specialCode FROM (SELECT CONCAT_WS('',su.all_subject,su.subject_code,'/') AS subjectCode,(SELECT s1.special_code FROM specialinfo s1 WHERE s1.id = (SUBSTRING_INDEX(SUBSTRING_INDEX(su.special_id,',',sp.id+1),',',-1))) AS specialCode,su.end_flag AS endFlag,su.useflag AS useFlag,su.account AS account FROM subjectinfo su JOIN splitstringsort sp ON sp.id<(LENGTH(su.special_id)-LENGTH(REPLACE(su.special_id,',',''))+1) WHERE");

                int needSpecialSubjectSqlParamsNo = 1;
                Map<Integer, Object> needSpecialSubjectSqlParams = new HashMap<>();

                needSpecialSubjectSql.append(" su.account = ?" + needSpecialSubjectSqlParamsNo);
                needSpecialSubjectSqlParams.put(needSpecialSubjectSqlParamsNo, accBookCode);
                needSpecialSubjectSqlParamsNo++;

                needSpecialSubjectSql.append(" AND su.end_flag = '0' AND su.useflag = '1' AND su.special_id IS NOT NULL AND su.special_id !=''");

                needSpecialSubjectSql.append(" AND CONCAT_WS('',su.all_subject,su.subject_code,'/') IN( ?" + needSpecialSubjectSqlParamsNo + " )) temp1");
                needSpecialSubjectSqlParams.put(needSpecialSubjectSqlParamsNo, subjectCodeSortIn);
                needSpecialSubjectSqlParamsNo++;

                needSpecialSubjectSql.append(" LEFT JOIN specialinfo s2 ON s2.account = temp1.account AND s2.endflag = temp1.endFlag AND s2.useflag = temp1.useFlag AND LEFT(s2.special_code, LENGTH(temp1.specialCode)) = temp1.specialCode WHERE 1=1");

                if (specialCodeSortIn.size()>0) {
//                    needSpecialSubjectSql.append(" s2.special_code IN("+specialCodeSortIn.toString()+")");
                    needSpecialSubjectSql.append(" AND s2.special_code IN( ?" + needSpecialSubjectSqlParamsNo + " )");
                    needSpecialSubjectSqlParams.put(needSpecialSubjectSqlParamsNo, specialCodeSortIn);
                    needSpecialSubjectSqlParamsNo++;
                }

                //查询余额数据（以需要展示的科目为基础一次全查出，如果没有则按0处理）
                String lastYearMonth = detailAccountService.getLastYearMonth(dto.getYearMonth());//上一个会计期间
                String tableName = "accarticlebalance";//专项余额表
                if (detailAccountService.whetherCarryForward(centerCode, CurrentUser.getCurrentLoginAccountType(), accBookCode, lastYearMonth)) {
                    tableName = "accarticlebalancehis";//专项余额历史表
                }
                StringBuffer balanceSql = new StringBuffer("SELECT a.direction_idx AS directionIdx,SUBSTRING_INDEX(SUBSTRING_INDEX(a.direction_other,',',sp.id+1),',',-1) AS directionOther,a.direction_idx_name AS directionIdxName,SUM(IFNULL(a.debit_dest_year,0.00)) AS debitDestYear,SUM(IFNULL(a.credit_dest_year,0.00)) AS creditDestYear,SUM(IFNULL(a.balance_dest,0.00)) AS balanceBeginDest,a.center_code AS centerCode");
                balanceSql.append(" FROM "+tableName+" a");
                balanceSql.append(" JOIN splitstringsort sp ON sp.id<(LENGTH(a.direction_other)-LENGTH(REPLACE(a.direction_other,',',''))+1) WHERE 1=1");

                int balanceSqlParamsNo = 1;
                Map<Integer, Object> balanceSqlParams = new HashMap<>();

                balanceSql.append(" AND a.center_code in (?" + balanceSqlParamsNo +")");
                balanceSqlParams.put(balanceSqlParamsNo, centerCode);
                balanceSqlParamsNo++;
                balanceSql.append(" AND a.branch_code in (?" + balanceSqlParamsNo +")");
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
                balanceSql.append(" AND a.direction_idx IN( ?" + balanceSqlParamsNo + " )");
                balanceSqlParams.put(balanceSqlParamsNo, subjectCodeSortIn);
                balanceSqlParamsNo++;

                balanceSql.append(" GROUP BY directionIdx,directionOther");

                if (specialCodeSortIn.size()>0) {
                    balanceSql.append(" HAVING directionOther IN( ?" + balanceSqlParamsNo + " )");
                    balanceSqlParams.put(balanceSqlParamsNo, specialCodeSortIn);
                    balanceSqlParamsNo++;
                }
                //排序规则
                if (dto.getAccounRules()!=null&&!"".equals(dto.getAccounRules())) {
                    if ("1".equals(dto.getAccounRules())) {//按科目汇总
                        if (specialCodeSortIn.size()>0) {
                            needSpecialSubjectSql.append(" ORDER BY FIELD(subjectCode, ?" + needSpecialSubjectSqlParamsNo + " )");
                            needSpecialSubjectSqlParams.put(needSpecialSubjectSqlParamsNo, subjectCodeSortIn);
                            needSpecialSubjectSqlParamsNo++;
                            needSpecialSubjectSql.append(" ,FIELD(specialCode, ?" + needSpecialSubjectSqlParamsNo + " )");
                            needSpecialSubjectSqlParams.put(needSpecialSubjectSqlParamsNo, specialCodeSortIn);
                            needSpecialSubjectSqlParamsNo++;

                            balanceSql.append(" ORDER BY FIELD(directionIdx, ?" + balanceSqlParamsNo + " )");
                            balanceSqlParams.put(balanceSqlParamsNo, subjectCodeSortIn);
                            balanceSqlParamsNo++;
                            balanceSql.append(" ,FIELD(directionOther, ?" + balanceSqlParamsNo + " )");
                            balanceSqlParams.put(balanceSqlParamsNo, specialCodeSortIn);
                            balanceSqlParamsNo++;
                        } else {
                            needSpecialSubjectSql.append(" ORDER BY FIELD(subjectCode, ?" + needSpecialSubjectSqlParamsNo + " ),specialCode");
                            needSpecialSubjectSqlParams.put(needSpecialSubjectSqlParamsNo, subjectCodeSortIn);
                            needSpecialSubjectSqlParamsNo++;

                            balanceSql.append(" ORDER BY FIELD(directionIdx, ?" + balanceSqlParamsNo + " ),directionOther");
                            balanceSqlParams.put(balanceSqlParamsNo, subjectCodeSortIn);
                            balanceSqlParamsNo++;
                        }
                    } else {//按专项汇总
                        if (specialCodeSortIn.size()>0) {
                            needSpecialSubjectSql.append(" ORDER BY FIELD(specialCode, ?" + needSpecialSubjectSqlParamsNo + " )");
                            needSpecialSubjectSqlParams.put(needSpecialSubjectSqlParamsNo, specialCodeSortIn);
                            needSpecialSubjectSqlParamsNo++;
                            needSpecialSubjectSql.append(" ,FIELD(subjectCode, ?" + needSpecialSubjectSqlParamsNo + " )");
                            needSpecialSubjectSqlParams.put(needSpecialSubjectSqlParamsNo, subjectCodeSortIn);
                            needSpecialSubjectSqlParamsNo++;

                            balanceSql.append(" ORDER BY FIELD(directionOther, ?" + balanceSqlParamsNo + " )");
                            balanceSqlParams.put(balanceSqlParamsNo, specialCodeSortIn);
                            balanceSqlParamsNo++;
                            balanceSql.append(" ,FIELD(directionIdx, ?" + balanceSqlParamsNo + " )");
                            balanceSqlParams.put(balanceSqlParamsNo, subjectCodeSortIn);
                            balanceSqlParamsNo++;
                        } else {
                            needSpecialSubjectSql.append(" ORDER BY specialCode,FIELD(subjectCode, ?" + needSpecialSubjectSqlParamsNo + " )");
                            needSpecialSubjectSqlParams.put(needSpecialSubjectSqlParamsNo, subjectCodeSortIn);
                            needSpecialSubjectSqlParamsNo++;

                            balanceSql.append(" ORDER BY directionOther,FIELD(directionIdx, ?" + balanceSqlParamsNo + " )");
                            balanceSqlParams.put(balanceSqlParamsNo, subjectCodeSortIn);
                            balanceSqlParamsNo++;
                        }
                    }
                }

                List<?> needSpecialSubjectSqlList = voucherRepository.queryBySqlSC(needSpecialSubjectSql.toString(), needSpecialSubjectSqlParams);
                List<?> balanceSqlList = voucherRepository.queryBySqlSC(balanceSql.toString(), balanceSqlParams);

                //packageData(List<Object> result, List<?> balanceSqlList, List<?> needSpecialSubjectSqlList, List<?> list, VoucherDTO dto, Map<String, String> subjectDirectionMap, Map<String, String> specialNameMap)
                packageData(result, balanceSqlList, needSpecialSubjectSqlList, list, dto, subjectDirectionMap, specialNameMap);
            }
        } else {//没有录入科目代码
            List<String> specialCodeSortIn = new ArrayList<>();
            //录入了专项，处理专项排序顺序
            if (dto.getSpecialName()!=null&&!"".equals(dto.getSpecialName()) && dto.getSpecialCode()!=null&&!"".equals(dto.getSpecialCode())) {
                String[] strings = dto.getSpecialCode().split(",");
                for (String str : strings) {
                    specialCodeSortIn.add(str);
                }
            }

            //查询凭证信息
            StringBuffer sql = new StringBuffer("SELECT am.voucher_date AS voucherDate,a.year_month_date AS yearMonthDate,a.voucher_no AS voucherNo,a.suffix_no AS suffixNo,a.direction_idx AS directionIdx,a.direction_idx_name AS directionIdxName, SUBSTRING_INDEX(SUBSTRING_INDEX(a.direction_other,',',sp.id+1),',',-1) AS directionOther,a.remark AS remarkName,a.debit_dest AS debitDest,a.credit_dest AS creditDest,(a.debit_dest-a.credit_dest) AS balance,a.center_code AS centerCode FROM accsubvoucher a");
            sql.append(" LEFT JOIN accmainvoucher am ON am.center_code = a.center_code AND am.branch_code = a.branch_code AND am.acc_book_type = a.acc_book_type AND am.acc_book_code = a.acc_book_code AND am.year_month_date = a.year_month_date AND am.voucher_no = a.voucher_no");
            sql.append(" JOIN splitstringsort sp ON sp.id<(LENGTH(a.direction_other)-LENGTH(REPLACE(a.direction_other,',',''))+1) WHERE 1=1");

            int sqlParamsNo = 1;
            Map<Integer, Object> sqlParams = new HashMap<>();

            sql.append(" AND a.center_code in (?" + sqlParamsNo +")");
            sqlParams.put(sqlParamsNo, centerCode);
            sqlParamsNo++;
            sql.append(" AND a.branch_code in (?" + sqlParamsNo +")");
            sqlParams.put(sqlParamsNo, branchCode);
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
            if (dto.getVoucherGene()!=null&&!"".equals(dto.getVoucherGene())) {
                if ("0".equals(dto.getVoucherGene())) {
                    sql.append(" AND am.voucher_flag = '3'");
                } else {
                    sql.append(" AND am.voucher_flag in ('1','2','3')");
                }
            }

            //专项段不为空的
            sql.append(" AND (a.direction_other IS NOT NULL OR a.direction_other !='')");

            if (dto.getRemarkName()!=null&&!"".equals(dto.getRemarkName())) {
                sql.append(" AND a.remark LIKE ?" + sqlParamsNo);
                sqlParams.put(sqlParamsNo, "%"+dto.getRemarkName()+"%");
                sqlParamsNo++;
            }
            //金额方向
            if (dto.getSourceDirection()!=null&&!"".equals(dto.getSourceDirection())) {
                if ("1".equals(dto.getSourceDirection())) {//借方金额
                    if (dto.getMoneyStart()!=null&&!"".equals(dto.getMoneyStart())) {
                        sql.append(" AND a.debit_dest >= ?" + sqlParamsNo);
                        sqlParams.put(sqlParamsNo, dto.getMoneyStart());
                        sqlParamsNo++;
                    }
                    if (dto.getMoneyEnd()!=null&&!"".equals(dto.getMoneyEnd())) {
                        sql.append(" AND a.debit_dest <= ?" + sqlParamsNo);
                        sqlParams.put(sqlParamsNo, dto.getMoneyEnd());
                        sqlParamsNo++;
                    }
                } else {//贷方金额
                    if (dto.getMoneyStart()!=null&&!"".equals(dto.getMoneyStart())) {
                        sql.append(" AND a.credit_dest >= ?" + sqlParamsNo);
                        sqlParams.put(sqlParamsNo, dto.getMoneyStart());
                        sqlParamsNo++;
                    }
                    if (dto.getMoneyEnd()!=null&&!"".equals(dto.getMoneyEnd())) {
                        sql.append(" AND a.credit_dest <= ?" + sqlParamsNo);
                        sqlParams.put(sqlParamsNo, dto.getMoneyEnd());
                        sqlParamsNo++;
                    }
                }
            } else {
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
            }
            if (dto.getSpecialName()!=null&&!"".equals(dto.getSpecialName())) {
                //拼接明细对象SQL（OR）
                detailAccountService.jointQuertSqlBySpecialCodes(sql, dto, "a", sqlParams, sqlParamsNo);
                if (sqlParamsNo != sqlParams.size()+1) { sqlParamsNo = sqlParams.size()+1; }
            }

            ///联查历史表数据
            String sqlStr = sql.toString();
            sqlStr = sqlStr.replaceAll("accsubvoucher","accsubvoucherhis");
            sqlStr = sqlStr.replaceAll("a\\.","ah\\.");
            sqlStr = sqlStr.replaceAll(" a "," ah ");
            sqlStr = sqlStr.replaceAll("accmainvoucher","accmainvoucherhis");
            sqlStr = sqlStr.replaceAll("am\\.","amh\\.");
            sqlStr = sqlStr.replaceAll(" am "," amh ");

            sql.append(" UNION ALL ");
            sql.append(sqlStr);

            if (dto.getAccounRules()!=null&&!"".equals(dto.getAccounRules())) {
                if ("1".equals(dto.getAccounRules())) {//按科目汇总
                    sql.append(" ORDER BY directionIdx,FIELD(directionOther, ?" + sqlParamsNo + " ),yearMonthDate,voucherNo");
                    sqlParams.put(sqlParamsNo, specialCodeSortIn);
                    sqlParamsNo++;
                } else {//按专项汇总
                    sql.append(" ORDER BY FIELD(directionOther, ?" + sqlParamsNo + " ),directionIdx,yearMonthDate,voucherNo");
                    sqlParams.put(sqlParamsNo, specialCodeSortIn);
                    sqlParamsNo++;
                }
            }

            List<?> list = voucherRepository.queryBySqlSC(sql.toString(), sqlParams);

            if (list!=null&&list.size()>0) {
                //查询区间内有数据
                //先处理出所有可能的专项科目以及对应的借贷本年累计、余额数据
                //所有可能需要展示的的专项科目(专项可以有哪些科目，通过专项找到一级专项，用一级专项去匹配有哪些末级科目)
                StringBuffer needSpecialSubjectSql = new StringBuffer("SELECT DISTINCT sp.special_code AS specialCode,CONCAT_WS('',temp1.subjectCode,'/') AS subjectCode FROM specialinfo sp LEFT JOIN specialinfo sp1 ON sp1.account = sp.account AND sp1.endflag = '1' AND sp1.useflag = sp.useflag AND (sp1.super_special IS NULL OR sp1.super_special = '') AND LEFT(sp1.special_code,2) = LEFT(sp.special_code,2) JOIN (SELECT CONCAT_WS('',su.all_subject,su.subject_code) AS subjectCode, SUBSTRING_INDEX(SUBSTRING_INDEX(su.special_id,',',sp2.id+1),',',-1) AS specialId FROM subjectinfo su LEFT JOIN splitstringsort sp2 ON sp2.id<(LENGTH(su.special_id)-LENGTH(REPLACE(su.special_id,',',''))+1)");

                int needSpecialSubjectSqlParamsNo = 1;
                Map<Integer, Object> needSpecialSubjectSqlParams = new HashMap<>();

                needSpecialSubjectSql.append(" WHERE su.account = ?" + needSpecialSubjectSqlParamsNo);
                needSpecialSubjectSqlParams.put(needSpecialSubjectSqlParamsNo, accBookCode);
                needSpecialSubjectSqlParamsNo++;

                needSpecialSubjectSql.append(" AND su.end_flag = '0' AND su.useflag = '1' AND su.special_id IS NOT NULL AND su.special_id !='') temp1 ON temp1.specialId = sp1.id");

                needSpecialSubjectSql.append(" WHERE sp.account = ?" + needSpecialSubjectSqlParamsNo);
                needSpecialSubjectSqlParams.put(needSpecialSubjectSqlParamsNo, accBookCode);
                needSpecialSubjectSqlParamsNo++;

                needSpecialSubjectSql.append(" AND sp.endflag ='0' AND sp.useflag = '1'");

                needSpecialSubjectSql.append(" AND sp.special_code IN( ?" + needSpecialSubjectSqlParamsNo + " )");
                needSpecialSubjectSqlParams.put(needSpecialSubjectSqlParamsNo, specialCodeSortIn);
                needSpecialSubjectSqlParamsNo++;


                //查询余额数据（以需要展示的专项为基础一次全查出，包含科目段，如果没有则按0处理）
                String lastYearMonth = detailAccountService.getLastYearMonth(dto.getYearMonth());//上一个会计期间
                String tableName = "accarticlebalance";//专项余额表
                if (detailAccountService.whetherCarryForward(centerCode, CurrentUser.getCurrentLoginAccountType(), accBookCode, lastYearMonth)) {
                    tableName = "accarticlebalancehis";//专项余额历史表
                }
                StringBuffer balanceSql = new StringBuffer("SELECT SUBSTRING_INDEX(SUBSTRING_INDEX(a.direction_other,',',sp.id+1),',',-1) AS directionOther,a.direction_idx AS directionIdx,a.direction_idx_name AS directionIdxName,SUM(IFNULL(a.debit_dest_year,0.00)) AS debitDestYear,SUM(IFNULL(a.credit_dest_year,0.00)) AS creditDestYear,SUM(IFNULL(a.balance_dest,0.00)) AS balanceBeginDest,a.center_code AS centerCode");
                balanceSql.append(" FROM "+tableName+" a");
                balanceSql.append(" JOIN splitstringsort sp ON sp.id<(LENGTH(a.direction_other)- LENGTH(REPLACE(a.direction_other,',',''))+1) WHERE 1=1");

                int balanceSqlParamsNo = 1;
                Map<Integer, Object> balanceSqlParams = new HashMap<>();

                balanceSql.append(" AND a.center_code in (?" + balanceSqlParamsNo +")");
                balanceSqlParams.put(balanceSqlParamsNo, centerCode);
                balanceSqlParamsNo++;
                balanceSql.append(" AND a.branch_code in (?" + balanceSqlParamsNo +")");
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

                if (dto.getSpecialName()!=null&&!"".equals(dto.getSpecialName())) {
                    //拼接明细对象SQL（OR）
                    detailAccountService.jointQuertSqlBySpecialCodes(balanceSql, dto, "a", balanceSqlParams, balanceSqlParamsNo);
                    if (balanceSqlParamsNo != balanceSqlParams.size()+1) { balanceSqlParamsNo = balanceSqlParams.size()+1; }
                }
                balanceSql.append(" GROUP BY directionOther,directionIdx");
                if (dto.getAccounRules()!=null&&!"".equals(dto.getAccounRules())) {
                    if ("1".equals(dto.getAccounRules())) {//按科目汇总
                        needSpecialSubjectSql.append(" ORDER BY subjectCode,FIELD(specialCode, ?" + needSpecialSubjectSqlParamsNo + " )");
                        needSpecialSubjectSqlParams.put(needSpecialSubjectSqlParamsNo, specialCodeSortIn);
                        needSpecialSubjectSqlParamsNo++;

                        balanceSql.append(" ORDER BY directionIdx,FIELD(directionOther, ?" + balanceSqlParamsNo + " )");
                        balanceSqlParams.put(balanceSqlParamsNo, specialCodeSortIn);
                        balanceSqlParamsNo++;
                    } else {//按专项汇总
                        needSpecialSubjectSql.append(" ORDER BY FIELD(specialCode, ?" + needSpecialSubjectSqlParamsNo + " ),subjectCode");
                        needSpecialSubjectSqlParams.put(needSpecialSubjectSqlParamsNo, specialCodeSortIn);
                        needSpecialSubjectSqlParamsNo++;

                        balanceSql.append(" ORDER BY directionIdx,FIELD(directionOther, ?" + balanceSqlParamsNo + " )");
                        balanceSqlParams.put(balanceSqlParamsNo, specialCodeSortIn);
                        balanceSqlParamsNo++;
                    }
                }

                List<?> needSpecialSubjectSqlList = voucherRepository.queryBySqlSC(needSpecialSubjectSql.toString(), needSpecialSubjectSqlParams);
                List<?> balanceSqlList = voucherRepository.queryBySqlSC(balanceSql.toString(), balanceSqlParams);

                Map<String, String> subjectDirectionMap = new HashMap<>();//科目余额方向
                if (needSpecialSubjectSqlList!=null&&needSpecialSubjectSqlList.size()>0) {
                    List<String> codeList = new ArrayList<>();
                    for (Object obj : needSpecialSubjectSqlList) {
                        Map map = (Map) obj;
                        String subjectCode = (String) map.get("subjectCode");
                        if (!codeList.contains(subjectCode))
                            codeList.add(subjectCode);
                    }
                    if (codeList.size()>0) {
                        StringBuffer subjectDirectionSql = new StringBuffer("SELECT CONCAT_WS('',s.all_subject,s.subject_code,'/') AS subjectCode,s.direction AS direction FROM subjectinfo s WHERE 1=1");
                        subjectDirectionSql.append(" AND s.account = ?1 AND s.end_flag = '0'");
                        subjectDirectionSql.append(" AND CONCAT_WS('',s.all_subject,s.subject_code,'/') IN( ?2 )");

                        Map<Integer, Object> subjectDirectionSqlparams = new HashMap<>();
                        subjectDirectionSqlparams.put(1, accBookCode);
                        subjectDirectionSqlparams.put(2, codeList);

                        List<?> list1 = voucherRepository.queryBySqlSC(subjectDirectionSql.toString(), subjectDirectionSqlparams);
                        if (list1!=null&&list1.size()>0) {
                            for (Object o : list1) {
                                Map map = (Map) o;
                                subjectDirectionMap.put((String) map.get("subjectCode"), (String) map.get("direction"));
                            }
                        }
                    }
                }

                //packageData(List<Object> result, List<?> balanceSqlList, List<?> needSpecialSubjectSqlList, List<?> list, VoucherDTO dto, Map<String, String> subjectDirectionMap, Map<String, String> specialNameMap)
                packageData(result, balanceSqlList, needSpecialSubjectSqlList, list, dto, subjectDirectionMap, specialNameMap);
            }

        }

        System.out.println("专项科目明细账查询用时："+(System.currentTimeMillis()-time)+" ms");
        return result;
    }

    /**
     * 获取汇总机构的全部子机构，不是汇总机构的返回本身
     * @return
     */
    private List<String> getSubBranch() {
        List<String> subBranch = new ArrayList<>();
        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        List<String>  summaryBranch = new ArrayList();
        summaryBranch = branchInfoRepository.findByLevel("1");
        if(summaryBranch.contains(centerCode)){
            subBranch = branchInfoRepository.findBySuperCom(centerCode);
        }else{
            subBranch.add(centerCode);
        }
        return subBranch;
    }

    /**
     * 科目代码范围内是否存在专项科目校验
     * @param subjectCode
     * @return
     */
    public String checkSpecialSubject(String subjectCode){
        if (subjectCode!=null&&!"".equals(subjectCode)) {
            StringBuffer sb = new StringBuffer("SELECT CONCAT_WS('',s.all_subject,s.subject_code,'/') AS subjectCode,s.direction FROM subjectinfo s WHERE 1=1");

            int paramsNo = 1;
            Map<Integer, Object> params = new HashMap<>();

            sb.append(" AND s.account = ?"+ paramsNo + " AND s.end_flag ='0' AND s.special_id IS NOT NULL AND s.special_id!=''");
            params.put(paramsNo, CurrentUser.getCurrentLoginAccount());
            paramsNo++;

            sb.append("AND (");
            String[] subjectCodes = subjectCode.split(",");
            for (int i=0;i<subjectCodes.length;i++) {
                String str = subjectCodes[i];
                if ("/".equals(str.substring(str.length()-1))) {
                    str = str.substring(0,str.length()-1);
                }
                if (i!=0) {
                    sb.append(" OR CONCAT_WS('',s.all_subject,s.subject_code) LIKE ?" + paramsNo);
                    params.put(paramsNo, str+"%");
                    paramsNo++;
                } else {
                    sb.append(" CONCAT_WS('',s.all_subject,s.subject_code) LIKE ?" + paramsNo);
                    params.put(paramsNo, str+"%");
                    paramsNo++;
                }
            }
            sb.append(") ORDER BY subjectCode");

            List<?> list = voucherRepository.queryBySqlSC(sb.toString(), params);
            if (list!=null&&list.size()>0) {
                return "true";
            } else {
                return "false";
            }
        } else {
            return "";
        }
    }

    /**
     * 根据专项名称模糊查询专项树，不限专项类别(一级专项)
     * 参数可为空，也支持多查询，即多专项名称(用英文逗号隔开)
     * @param value 可为空，支持多查询(用英文逗号隔开)
     * @return
     */
    @Override
    public List<?> querySpecialTree(String value){
        if (value!=null&&!"".equals(value)) {
            return querySpecialTreeByValue(value);
        } else {
            return querySpecialTree();
        }
    }

    private List<?> querySpecialTree(){
        long time = System.currentTimeMillis();

        List<Object> result = new ArrayList<Object>();
        String accBookCode = CurrentUser.getCurrentLoginAccount();
        //查询专项分类，即一级专项
        String superSql = "SELECT s.id AS id,s.special_code AS code,s.special_name AS name,s.special_namep AS nameP,s.endflag AS endFlag,(SELECT COUNT(ss.id) FROM specialinfo ss WHERE ss.account = s.account AND ss.useflag = s.useflag AND ss.super_special = s.id) AS childNum FROM specialinfo s WHERE s.account = ?1 AND s.useflag='1' AND (s.super_special = '' or s.super_special is null) ORDER By code";

        Map<Integer, Object> params = new HashMap<>();
        params.put(1, accBookCode);

        List<?> superList = voucherRepository.queryBySqlSC(superSql, params);
        if (superList!=null&&superList.size()>0) {
            for (Object obj : superList) {
                Map map = (Map) obj;
                //循环遍历查询子级专项
                List<?> list = null;
                if (Integer.valueOf(map.get("childNum").toString())!=0) {
                    list = queryChildrenSpecial((Integer) map.get("id"), accBookCode);
                }
                map.put("text", map.get("nameP"));
                if (list!=null&&list.size()>0) {
                    map.put("children", list);
                    map.put("state", "closed");
                }
                //如果无子级专项，且非末级专项，无需展示，既不需要追加（0-末级专项，1-非末级专项）
                if ((list!=null&&list.size()>0) || "0".equals((String) map.get("endFlag"))) {
                    result.add(map);
                }
            }
        }
        System.out.println("专项树查询用时(querySpecialTree)："+(System.currentTimeMillis()-time)+"ms");
        return result;
    }

    /**
     * 根据专项ID查询子级专项（递归查询）
     * @param id
     * @param accBookCode
     * @return
     */
    private List<?> queryChildrenSpecial(Integer id, String accBookCode){
        List<Object> result = new ArrayList<Object>();
        String sql = "SELECT s.id AS id,s.special_code AS code,s.special_name AS name,s.special_namep AS nameP,s.endflag AS endFlag,(SELECT COUNT(ss.id) FROM specialinfo ss WHERE ss.account = s.account AND ss.useflag = s.useflag AND ss.super_special = s.id) AS childNum FROM specialinfo s WHERE s.account = ?1 AND s.useflag='1' AND s.super_special = ?2 ORDER By code";

        Map<Integer, Object> params = new HashMap<>();
        params.put(1, accBookCode);
        params.put(2, id);

        List<?> sqlList = voucherRepository.queryBySqlSC(sql, params);
        if (sqlList!=null&&sqlList.size()>0) {
            for (Object obj : sqlList) {
                Map map = (Map) obj;
                //循环遍历查询子级专项
                List<?> list = null;
                if (Integer.valueOf(map.get("childNum").toString())!=0) {
                    list = queryChildrenSpecial((Integer) map.get("id"), accBookCode);
                }
                map.put("text", map.get("nameP"));
                if (list!=null&&list.size()>0) {
                    map.put("children", list);
                    map.put("state", "closed");
                }
                //如果无子级专项，且非末级专项，无需展示，既不需要追加（0-末级专项，1-非末级专项）
                if ((list!=null&&list.size()>0) || "0".equals((String) map.get("endFlag"))) {
                    result.add(map);
                }
            }
        }
        return result;
    }

    private List<?> queryChildrenSpecial2(Integer id, String accBookCode){
        List<Object> result = new ArrayList<Object>();
        String sql = "SELECT s.id AS id,s.special_code AS code,s.special_name AS name,s.special_namep AS nameP,s.endflag AS endFlag,(SELECT COUNT(ss.id) FROM specialinfo ss WHERE ss.account = s.account AND ss.useflag = s.useflag AND ss.super_special = s.id) AS childNum FROM specialinfo s WHERE s.account = ?1 AND s.useflag='1' AND s.super_special = ?2 ORDER By code";

        Map<Integer, Object> params = new HashMap<>();
        params.put(1, accBookCode);
        params.put(2, id);

        List<?> sqlList = voucherRepository.queryBySqlSC(sql, params);
        if (sqlList!=null&&sqlList.size()>0) {
            for (Object obj : sqlList) {
                Map map = (Map) obj;
                //循环遍历查询子级专项
                List<?> list = null;
                if (Integer.valueOf(map.get("childNum").toString())!=0) {
                    list = queryChildrenSpecial((Integer) map.get("id"), accBookCode);
                }
                map.put("text", map.get("nameP"));
                if (list!=null&&list.size()>0) {
                    map.put("children", list);
                }
                //如果无子级专项，且非末级专项，无需展示，既不需要追加（0-末级专项，1-非末级专项）
                if ((list!=null&&list.size()>0) || "0".equals((String) map.get("endFlag"))) {
                    result.add(map);
                }
            }
        }
        return result;
    }

    private List<?> querySpecialTreeByValue(String value){
        long time = System.currentTimeMillis();
        List<Object> result = new ArrayList<Object>();
        //先查询出所需要的
        String accBookCode = CurrentUser.getCurrentLoginAccount();

        int paramsNo = 1;
        Map<Integer, Object> params = new HashMap<>();

        StringBuffer needSql = new StringBuffer("SELECT s.id AS id FROM specialinfo s WHERE s.account = ?"+ paramsNo + " AND s.useflag='1'");
        params.put(paramsNo, accBookCode);
        paramsNo++;

        if (value!=null&&!"".equals(value)) {
            String[] names = value.split(",");
            needSql.append(" AND (");
            for (int i=0;i<names.length;i++) {
                if (names[i]!=null&&!"".equals(names[i])) {
                    if (i==0) {
                        needSql.append("s.special_name LIKE ?"+ paramsNo);
                        needSql.append(" OR s.special_namep LIKE ?"+ paramsNo);
                        params.put(paramsNo, "%"+names[i]+"%");
                        paramsNo++;
                        needSql.append(" OR s.special_code LIKE ?"+ paramsNo);
                        params.put(paramsNo, names[i]+"%");
                        paramsNo++;
                    } else {
                        needSql.append(" OR s.special_name LIKE ?"+ paramsNo);
                        needSql.append(" OR s.special_namep LIKE ?"+ paramsNo);
                        params.put(paramsNo, "%"+names[i]+"%");
                        paramsNo++;
                        needSql.append(" OR s.special_code LIKE ?"+ paramsNo);
                        params.put(paramsNo, names[i]+"%");
                        paramsNo++;
                    }
                }
            }
            needSql.append(")");
        }
        List<Map<String,Integer>> needList = (List<Map<String,Integer>>) voucherRepository.queryBySqlSC(needSql.toString(), params);
        if (needList!=null&&needList.size()>0) {
            Set<Integer> set = new HashSet<>();//用于存储需要的专项ID
            for (Map<String,Integer> m : needList) {
                set.add(m.get("id"));
            }

            //查询专项分类，即一级专项
            String superSql = "SELECT s.id AS id,s.special_code AS code,s.special_name AS name,s.special_namep AS nameP,s.endflag AS endFlag,(SELECT COUNT(ss.id) FROM specialinfo ss WHERE ss.account = s.account AND ss.useflag = s.useflag AND ss.super_special = s.id) AS childNum FROM specialinfo s WHERE s.account = ?1 AND s.useflag='1' AND (s.super_special = '' or s.super_special is null) ORDER By code";

            params = new HashMap<>();
            params.put(paramsNo, accBookCode);

            List<?> superList = voucherRepository.queryBySqlSC(superSql, params);
            if (superList!=null&&superList.size()>0) {
                for (Object obj : superList) {
                    Map map = (Map) obj;
                    //循环遍历查询子级专项
                    List<?> list = null;
                    if (Integer.valueOf(map.get("childNum").toString())!=0) {
                        //有子级专项，如果此非末级专项也是需要的，那么专项的所有子级均需展示
                        if (set.contains((Integer) map.get("id"))) {
                            list = queryChildrenSpecial2((Integer) map.get("id"), accBookCode);
                        } else {
                            list = queryChildrenSpecialByValue((Integer) map.get("id"), accBookCode, set);
                        }
                    }
                    if (list!=null&&list.size()>0) {
                        map.put("children", list);
                    }
                    //如果无子级专项，且非末级专项，无需展示，既不需要追加（0-末级专项，1-非末级专项），如果无子级的末级专项并且是不需要的也无需展示
                    if ((list!=null&&list.size()>0) || ("0".equals((String) map.get("endFlag")) && set.contains((Integer) map.get("id")))) {
                        map.put("text", map.get("nameP"));
                        result.add(map);
                    }
                }
            }
        }
        System.out.println("专项树查询用时(querySpecialTreeByValue)："+(System.currentTimeMillis()-time)+"ms");
        return result;
    }

    private List<?> queryChildrenSpecialByValue(Integer id, String accBookCode, Set<Integer> set){
        List<Object> result = new ArrayList<Object>();
        String sql = "SELECT s.id AS id,s.special_code AS code,s.special_name AS name,s.special_namep AS nameP,s.endflag AS endFlag,(SELECT COUNT(ss.id) FROM specialinfo ss WHERE ss.account = s.account AND ss.useflag = s.useflag AND ss.super_special = s.id) AS childNum FROM specialinfo s WHERE s.account = ?1 AND s.useflag='1' AND s.super_special = ?2 ORDER By code";

        Map<Integer, Object> params = new HashMap<>();
        params.put(1, accBookCode);
        params.put(1, id);

        List<?> sqlList = voucherRepository.queryBySqlSC(sql, params);
        if (sqlList!=null&&sqlList.size()>0) {
            for (Object obj : sqlList) {
                Map map = (Map) obj;
                //循环遍历查询子级专项
                List<?> list = null;
                if (Integer.valueOf(map.get("childNum").toString())!=0) {
                    //有子级专项，如果此非末级专项也是需要的，那么专项的所有子级均需展示
                    if (set.contains((Integer) map.get("id"))) {
                        list = queryChildrenSpecial((Integer) map.get("id"), accBookCode);
                    } else {
                        list = queryChildrenSpecialByValue((Integer) map.get("id"), accBookCode, set);
                    }
                }
                if (list!=null&&list.size()>0) {
                    map.put("children", list);
                }
                //如果无子级专项，且非末级专项，无需展示，既不需要追加（0-末级专项，1-非末级专项），如果无子级的末级专项并且是不需要的也无需展示
                if ((list!=null&&list.size()>0) || ("0".equals((String) map.get("endFlag")) && set.contains((Integer) map.get("id")))) {
                    map.put("text", map.get("nameP"));
                    result.add(map);
                }
            }
        }
        return result;
    }

    /**
     * 根据科目名称模糊查询科目树，不限科目类别，如果匹配的是非末级，则非末级下的所有子级全部展示
     * @param value 可为空
     * @return
     */
    @Override
    public List<?> querySubjectTree(String value){
        if (value!=null&&!"".equals(value)) {
            return querySubjectTreeByValue(value);
        } else {
            return querySubjectTree();
        }
    }

    @Override
    public String isHasData(VoucherDTO voucherDTO) {
        List<?> list = querySpecialSubjectDetailAccountList(voucherDTO);
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
    public void exportData(HttpServletRequest request, HttpServletResponse response, VoucherDTO voucherDTO,String accounRules,String Date1,String Date2) {
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
            result = querySpecialSubjectDetailAccountList(voucherDTO);
        }

        ExcelUtil excelUtil = new ExcelUtil();
        excelUtil.exportSpecialSubjectDetail(request,response,result,accounRules,Date1,Date2,MODELPath);
    }

    private List<?> querySubjectTree(){
        long time = System.currentTimeMillis();

        List<Object> result = new ArrayList<Object>();

        System.out.println("科目树查询用时(querySubjectTree)："+(System.currentTimeMillis()-time)+"ms");
        return result;
    }

    private List<?> querySubjectTreeByValue(String value){
        long time = System.currentTimeMillis();

        List<Object> result = new ArrayList<Object>();

        System.out.println("科目树查询用时(querySubjectTreeByValue)："+(System.currentTimeMillis()-time)+"ms");
        return result;
    }

    private void setRowBegin(List<Object> result, Map dataMap, String direction, Map<String, String> specialNameMap, VoucherDTO dto, BigDecimal balanceBeginDest){
        Map<String, Object> rowMap = new HashMap<>();
        rowMap.put("centerCode", dataMap.get("centerCode"));
        rowMap.put("subjectCode", dataMap.get("directionIdx"));
        //设置科目名称
        if (dto.getSubjectNameP()!=null&&"0".equals(dto.getSubjectNameP())) {//非全级显示
            rowMap.put("subjectName", getSubjectName((String) dataMap.get("directionIdxName")));
        } else {//全级显示
            rowMap.put("subjectName", dataMap.get("directionIdxName"));
        }
        rowMap.put("specialCode", dataMap.get("directionOther"));
        //设置专项名称
        setSpecialName(rowMap, specialNameMap, dto.getSpecialNameP(), (String) dataMap.get("directionOther"));
        rowMap.put("remarkName", "期初余额");
        rowMap.put("balanceFX", detailAccountService.getFXString(direction, balanceBeginDest));
        rowMap.put("balanceDest", balanceBeginDest.abs().toString());
        result.add(rowMap);
    }

    private void setRowDataByEndFlagAndMonthAndYear(List<Object> result, String direction, BigDecimal debitDestMonth, BigDecimal creditDestMonth, BigDecimal debitDestYear, BigDecimal creditDestYear, BigDecimal balanceBeginDest){
        Map<String, Object> rowMap = new HashMap();
        rowMap = new HashMap();
        rowMap.put("remarkName", "本月合计");
        rowMap.put("debitDest", debitDestMonth.toString());
        rowMap.put("creditDest", creditDestMonth.toString());
        rowMap.put("balanceFX", detailAccountService.getFXString(direction, balanceBeginDest));
        rowMap.put("balanceDest", balanceBeginDest.abs().toString());
        result.add(rowMap);
        rowMap = new HashMap();
        rowMap.put("remarkName", "本年累计");
        rowMap.put("debitDest", debitDestYear.toString());
        rowMap.put("creditDest", creditDestYear.toString());
        rowMap.put("balanceFX", detailAccountService.getFXString(direction, balanceBeginDest));
        rowMap.put("balanceDest", balanceBeginDest.abs().toString());
        result.add(rowMap);
    }

    private void setRowData(Map<String, Object> rowMap, Map<String, Object> dataMap, VoucherDTO dto, Map<String, String> specialNameMap, String direction, BigDecimal balance){
        rowMap.put("centerCode", dataMap.get("centerCode"));
        rowMap.put("voucherDate", dataMap.get("voucherDate"));
        rowMap.put("yearMonthDate", dataMap.get("yearMonthDate"));
        rowMap.put("voucherNo", dataMap.get("voucherNo"));
        rowMap.put("suffixNo", dataMap.get("suffixNo"));
        rowMap.put("subjectCode", dataMap.get("directionIdx"));
        //设置科目名称
        if (dto.getSubjectNameP()!=null&&"0".equals(dto.getSubjectNameP())) {//非全级显示
            rowMap.put("subjectName", getSubjectName((String) dataMap.get("directionIdxName")));
        } else {//全级显示
            rowMap.put("subjectName", dataMap.get("directionIdxName"));
        }
        rowMap.put("specialCode", dataMap.get("directionOther"));
        //设置专项名称
        setSpecialName(rowMap, specialNameMap, dto.getSpecialNameP(), (String) dataMap.get("directionOther"));
        rowMap.put("remarkName", dataMap.get("remarkName"));
        setRemarkNameBySpecial(rowMap, specialNameMap, dto.getSpecialNameP(), (String) dataMap.get("directionOther"));
        rowMap.put("debitDest", dataMap.get("debitDest").toString());
        rowMap.put("creditDest", dataMap.get("creditDest").toString());
        rowMap.put("balanceFX", detailAccountService.getFXString(direction, balance));
        rowMap.put("balanceDest", balance.abs().toString());
    }

    private String getSubjectName(String directionIdxName){
        String subjectName = directionIdxName;
        if ("/".equals(subjectName.substring(subjectName.length()-1))) {
            String[] names = (subjectName.substring(0,subjectName.length()-1)).split("/");
            subjectName = names[names.length-1];
        } else {
            String[] names = subjectName.split("/");
            subjectName = names[names.length-1];
        }
        return subjectName;
    }

    private void setSpecialName(Map<String, Object> map, Map<String, String> specialNameMap, String specialNameP, String specialCode){
        if (specialCode!=null&&!"".equals(specialCode)) {
            if (specialNameMap.containsKey(specialCode)) {
                map.put("specialName", specialNameMap.get(specialCode));
            } else {
                VoucherDTO voucherDTO = voucherService.getSpecialDateBySpecialCode(specialCode);
                if (voucherDTO!=null&&voucherDTO.getSpecialName()!=null&&!"".equals(voucherDTO.getSpecialName())) {
                    if (specialNameP!=null&&"1".equals(specialNameP)) {
                        specialNameMap.put(specialCode, voucherDTO.getSpecialNameP());
                        map.put("specialName", voucherDTO.getSpecialNameP());
                    } else {
                        specialNameMap.put(specialCode, voucherDTO.getSpecialName());
                        map.put("specialName", voucherDTO.getSpecialName());
                    }
                }
            }
        }
    }

    private void  setRemarkNameBySpecial(Map<String, Object> map, Map<String, String> specialNameMap, String specialNameP, String directionOther){
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

    private String getKeyByAccounRules(String accounRules, String directionIdx, String directionOther){
        String key = "";
        if (accounRules!=null&&!"".equals(accounRules)) {
            if ("1".equals(accounRules)) {//按科目汇总
                key = directionIdx + "#" + directionOther;
            } else {//按专项汇总
                key = directionOther + "#" + directionIdx;
            }
        } else {
            key = directionIdx + "#" + directionOther;
        }
        return key;
    }

    private void packageData(List<Object> result, List<?> balanceSqlList, List<?> needSpecialSubjectSqlList, List<?> list, VoucherDTO dto, Map<String, String> subjectDirectionMap, Map<String, String> specialNameMap){
        //将余额数据封装（科目全代码#专项代码/专项代码#科目全代码，方便取数）
        Map<String, Map<String, Object>> balanceMap = new HashMap();
        if (balanceSqlList!=null&&balanceSqlList.size()>0) {
            for (Object obj : balanceSqlList) {
                Map map = (Map) obj;
                String key = getKeyByAccounRules(dto.getAccounRules(), (String) map.get("directionIdx"), (String) map.get("directionOther"));

                Map<String, Object> m = new HashMap<>();
                m.put("directionIdxName", (String) map.get("directionIdxName"));
                m.put("debitDestYear", map.get("debitDestYear"));
                m.put("creditDestYear", map.get("creditDestYear"));
                m.put("balanceBeginDest", map.get("balanceBeginDest"));
                m.put("centerCode" ,map.get("centerCode"));
                balanceMap.put(key, m);
            }
        }

        //封装凭证数据（科目全代码#专项代码/专项代码#科目全代码，方便取数）
        Map<String, List<Object>> voucherDataMap = new HashMap<>();
        for (Object obj : list) {
            Map map = (Map) obj;
            String directionIdx = (String) map.get("directionIdx");//科目全代码
            String directionOther = (String) map.get("directionOther");//专项代码
            String key = getKeyByAccounRules(dto.getAccounRules(), directionIdx, directionOther);;

            if (voucherDataMap.containsKey(key)) {
                List<Object> rowData = voucherDataMap.get(key);
                rowData.add(map);
                voucherDataMap.put(key, rowData);
            } else {
                List<Object> rowData = new ArrayList<>();
                rowData.add(map);
                voucherDataMap.put(key, rowData);
            }
        }

        //封装返回数据
        if (needSpecialSubjectSqlList!=null&&needSpecialSubjectSqlList.size()>0) {
            for (Object obj : needSpecialSubjectSqlList) {
                Map map = (Map) obj;
                String directionIdx = (String) map.get("subjectCode");//科目全代码
                String directionOther = (String) map.get("specialCode");//专项代码
                String key = getKeyByAccounRules(dto.getAccounRules(), directionIdx, directionOther);

                BigDecimal debitDestYear = new BigDecimal("0.00");//本位币本年借方金额
                BigDecimal creditDestYear = new BigDecimal("0.00");//本位币本年贷方金额
                BigDecimal balanceBeginDest = new BigDecimal("0.00");//本位币期初余额
                BigDecimal debitDestMonth = new BigDecimal("0.00");//本位币本月借方金额
                BigDecimal creditDestMonth = new BigDecimal("0.00");//本位币本月贷方金额
                //有无余额数据，若有还需判断是否为0，同时赋值本年累计值（如果期初为0则不显示期初。期初不为0，就需显示期初）
                if (balanceMap.containsKey(key)) {
                    Map<String, Object> map1 = balanceMap.get(key);
                    balanceBeginDest = (BigDecimal) map1.get("balanceBeginDest");//本位币期初余额
                    if (!"01".equals(dto.getYearMonth().substring(4))) {
                        debitDestYear = (BigDecimal) map1.get("debitDestYear");//本位币本年借方金额
                        creditDestYear = (BigDecimal) map1.get("creditDestYear");//本位币本年贷方金额
                    }
                    if (balanceBeginDest.compareTo(new BigDecimal("0.00"))!=0) {
                        Map<String, String> map2 = new HashMap<>();
                        map2.put("directionIdx", directionIdx);
                        map2.put("directionIdxName", (String) map1.get("directionIdxName"));
                        map2.put("directionOther", directionOther);
                        map2.put("centerCode", (String) map1.get("centerCode"));
                        setRowBegin(result, map2, subjectDirectionMap.get(directionIdx), specialNameMap, dto, balanceBeginDest);
                    }
                }
                //有无凭证数据，只要当月有凭证发生，就需显示本月合计，本年累计
                if (voucherDataMap.containsKey(key)) {
                    List<Object> list1 = voucherDataMap.get(key);
                    String oldYearMonthDate = "";
                    boolean flag = true;//用于表示是否追加本月合计、本年累计
                    for (Object object : list1) {
                        Map dataMap = (Map) object;
                        String yearMonthDate = (String) dataMap.get("yearMonthDate");//会计期间

                        //同时需要考虑JS月要合并到当年12月
                        if ("JS".equals(yearMonthDate.substring(4)) && "12".equals(oldYearMonthDate.substring(4))) { flag = false; } else { flag = true; }

                        if (!"".equals(oldYearMonthDate) && !oldYearMonthDate.equals(yearMonthDate)) {
                            //跨会计期间,追加本月合计、本年累计
                            if (flag) {
                                setRowDataByEndFlagAndMonthAndYear(result, subjectDirectionMap.get(directionIdx), debitDestMonth, creditDestMonth, debitDestYear, creditDestYear, balanceBeginDest);
                            }
                            if (!("JS".equals(yearMonthDate.substring(4)) && "12".equals(oldYearMonthDate.substring(4)) && oldYearMonthDate.substring(0,4).equals(yearMonthDate.substring(0,4)))) {
                                //跨会计期间，借贷本月归零(12月暂不归零)
                                debitDestMonth = new BigDecimal("0.00");
                                creditDestMonth = new BigDecimal("0.00");
                            }
                            //判断是否跨年，若是，则借贷本年累计归零
                            if (!oldYearMonthDate.substring(0,4).equals(yearMonthDate.substring(0,4))) {
                                debitDestYear = new BigDecimal("0.00");//本位币本年借方金额
                                creditDestYear = new BigDecimal("0.00");//本位币本年贷方金额
                            }
                        }

                        //计算借贷本月合计、借贷本年累计、余额
                        BigDecimal debitDest = (BigDecimal) dataMap.get("debitDest");
                        BigDecimal creditDest = (BigDecimal) dataMap.get("creditDest");
                        debitDestMonth = debitDestMonth.add(debitDest);
                        creditDestMonth = creditDestMonth.add(creditDest);
                        debitDestYear = debitDestYear.add(debitDest);
                        creditDestYear = creditDestYear.add(creditDest);
                        balanceBeginDest = balanceBeginDest.add((BigDecimal) dataMap.get("balance"));

                        Map<String, Object> rowMap = new HashMap<>();
                        setRowData(rowMap, dataMap, dto, specialNameMap, subjectDirectionMap.get(directionIdx), balanceBeginDest);
                        result.add(rowMap);

                        oldYearMonthDate = yearMonthDate;
                    }
                    //循环结束，需要追加一次本月合计、本年累计
                    setRowDataByEndFlagAndMonthAndYear(result, subjectDirectionMap.get(directionIdx), debitDestMonth, creditDestMonth, debitDestYear, creditDestYear, balanceBeginDest);
                }
            }
        }
    }
}
