package com.sinosoft.service.synthesize;

import com.sinosoft.dto.VoucherDTO;
import org.springframework.data.domain.Page;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface DetailAccountService {
    /**
     * 明细账查询
     * @param dto
     * @param synthDetailAccount 综合明细账（0-否 1-是）
     * @return
     */
    List<?> queryDetailAccount(VoucherDTO dto, String synthDetailAccount,List centerCode);

    /**
     * 明细账查询
     * @param page
     * @param rows
     * @param dto
     * @param synthDetailAccount 综合明细账（0-否 1-是）
     * @return
     */
    Page<?> queryDetailAccountByPage(int page, int rows, VoucherDTO dto, String synthDetailAccount);

    /**
     * 拼接明细对象SQL（OR）
     * @param sql
     * @param dto
     * @param tableAlias
     * @param params
     * @param paramsNo
     */
    void jointQuertSqlBySpecialCodes(StringBuffer sql, VoucherDTO dto, String tableAlias, Map<Integer, Object> params, int paramsNo);

    /**
     * 拼接明细对象SQL（AND）
     * @param sql
     * @param specialCode
     * @param tableAlias
     * @param params
     * @param paramsNo
     */
    void jointQuertSqlBySpecialCode(StringBuffer sql, String specialCode, String tableAlias, Map<Integer, Object> params, int paramsNo);

    /**
     * 科目段部分SQL拼接
     * @param subjectCode
     * @param tableAlias
     * @param params
     * @param paramsNo
     * @return
     */
    String jointDirectionIdxSqlBySubjectCode(String subjectCode, String tableAlias, Map<Integer, Object> params, int paramsNo);

    /**
     * 根据科目余额方向和科目余额获取余额方向表示字符
     * @param flag
     * @param bigDecimal
     * @return
     */
    String getFXString(String flag, BigDecimal bigDecimal);
    /**
     * 获取上一个会计期间，如果参数为空，则返回上一年的JS月会计期间
     * @param yearMonth
     * @return
     */
    String getLastYearMonth(String yearMonth);
    /**
     * 判断会计期间是否结转
     * @param centerCode
     * @param accBookType
     * @param accBookCode
     * @param yearMonth
     * @return true-结转/决算
     */
    boolean whetherCarryForward(List centerCode, String accBookType, String accBookCode, String yearMonth);

    boolean whetherCarryForward(String  centerCode, String accBookType, String accBookCode, String yearMonth);

    String isHasData(VoucherDTO dto, String synthDetailAccount);;

    void exportData(HttpServletRequest request, HttpServletResponse response, VoucherDTO dto, String synthDetailAccount,String Date1,String Date2);

    /**
     * 获取汇总机构的全部子机构，不是汇总机构的返回本身
     * @return
     */
    public List<String> getSubBranch() ;
}
