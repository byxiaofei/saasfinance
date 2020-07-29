package com.sinosoft.repository;

import com.sinosoft.common.CurrentUser;
import com.sinosoft.domain.CodeSelect;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CodeSelectRepository extends BaseRepository<CodeSelect, Integer> {
    /**
     * 根据codeType(code_type)单一条件查询，并且codeCcode(code_code) asc
     * @param codeType
     * @return
     */
    @Query(value="select c.code_code as value,c.code_name as text from codemanage c where c.code_type = ?1 order by c.code_code ASC", nativeQuery = true)
    List<Map<String, Object>> findByComTypeAsc(String codeType);

    /**
     *
     * 功能描述:    根据给的
     *
     */
    @Query(value="select c.code_code as value,c.code_name as text from codemanage c where c.code_type = ?1 and  c.code_code = ?2 order by c.code_code ASC", nativeQuery = true)
    List<Map<String, Object>> findByComTypeAsc(String codeType,String codeCode);
    /**
     * 根据codeType(code_type)单一条件查询，并且codeCcode(code_code) asc
     * @param param
     * @return
     */
    @Query(value="select c.code_code as value,c.code_name as text from codemanage c where c.code_type ='SDBBreportType' and c.temp like %?1% and c.code_code in ('3#1','3#2','3#3','4#1','4#2','4#3') order by c.code_code ASC", nativeQuery = true)
    List<Map<String, Object>> findzdyReport(String param);

    @Query(value="SELECT r.version as value,r.version as text  FROM reportcompute r where r.report_code='4#1' GROUP BY r.version order by r.version", nativeQuery = true)
    List<Map<String, Object>> findVersion();
    /**
     * 根据codeType(code_type)单一条件查询，并且codeCcode(code_code) desc
     * @param codeType
     * @return
     */
    @Query(value="select c.code_code as value,c.code_name as text from codemanage c where c.code_type = ?1 order by c.code_code DESC", nativeQuery = true)
    List<Map<String, Object>> findByComTypeDesc(String codeType);
    /**
     * 初始化上级机构下拉框
     * @return
     */
    @Query(value="select b.id as value,b.com_name as text from branchinfo b order by b.level,b.com_code", nativeQuery = true)
    List<Map<String, Object>> initSuperCom();
    /**
     * 加载账套下拉框信息（账套信息id、accountName）
     * @return
     */
    @Query(value="select a.id as value,a.accountName as text from AccountInfo a where a.useFlag = '1' order by a.id")
    List<Map<String, Object>> referToAccount();
    /**
     * 根据机构Id加载账套下拉框信息（账套信息id、accountName）
     * @return
     */
    @Query(value = "select a.id as value,a.accountName as text from AccountInfo a left join BranchAccount b on b.accountInfo.id = a.id where b.branchInfo.id = ?1")
    List<Map<String, Object>> findAccountByComComId(Integer id);

    /**
     * 根据 codeType 获取字典表整类数据信息
     * @param codeType
     * @return
     */
    @Query(value="select c.code_type as codeType,c.code_code as codeCode,c.code_name as codeName,c.temp as temp,c.order_by as orderBy from codemanage c where c.code_type = ?1", nativeQuery = true)
    List<Map<String, Object>> findCodeSelectInfoByCodeType(String codeType);

    /**
     * 根据 codeType、orderBy获取字典表整条数据信息
     * @param codeType
     * @param orderBy
     * @return
     */
    @Query(value="select c.code_type as codeType,c.code_code as codeCode,c.code_name as codeName,c.temp as temp,c.order_by as orderBy from codemanage c where c.code_type = ?1 and c.order_by = ?2", nativeQuery = true)
    List<Map<String, Object>> findCodeSelectInfoByCodeTypeAndOrderBy(String codeType, String orderBy);

    /**
     * 根据codeType、codeCode获取字典表整条数据信息
     * @param codeType
     * @param codeCode
     * @return
     */
    @Query(value="select c.code_type as codeType,c.code_code as codeCode,c.code_name as codeName,c.temp as temp,c.order_by as orderBy from codemanage c where c.code_type = ?1 and c.code_code = ?2", nativeQuery = true)
    List<Map<String, Object>> findCodeSelectInfo(String codeType, String codeCode);

    /**
     * 根据codeType、codeCode获取字典表整条数据信息
     * @param codeType
     * @param codeCode
     * @return
     */
    @Query(value="select * from codemanage c where c.code_type = ?1 and c.code_code = ?2", nativeQuery = true)
    CodeSelect findCodeSelect(String codeType, String codeCode);

    /**
     * 根据codeType、orderBy获取字典表上一条的整条数据信息
     * @param codeType
     * @param orderBy
     * @return
     */
    @Query(value="select * from codemanage c where c.code_type = ?1 and (c.order_by is not null or c.order_by !='') and c.order_by < ?2 order by c.order_by asc limit 1", nativeQuery = true)
    List<CodeSelect> lastCodeSelectByOrder(String codeType, String orderBy);

    /**
     * 根据codeType、orderBy获取字典表下一条的整条数据信息
     * @param codeType
     * @param orderBy
     * @return
     */
    @Query(value="select * from codemanage c where c.code_type = ?1 and (c.order_by is not null or c.order_by !='') and c.order_by > ?2 order by c.order_by asc limit 1", nativeQuery = true)
    List<CodeSelect> nextCodeSelectByOrder(String codeType, String orderBy);

    /**
     * 查询未结转的会计期间
     * @return
     */
    @Query(value="SELECT a.year_month_date as value,a.year_month_date as text from AccMonthTrace a  where a.acc_month_stat not in('3','5') and a.acc_book_code=?1 and a.center_code = ?2 order by value desc", nativeQuery = true)
    List<Map<String, Object>> findYearMonth(String account, String centerCode);
    /**
     * 查询全部会计期间
     * @return
     */
    @Query(value="SELECT a.year_month_date as value,a.year_month_date as text from AccMonthTrace a  where a.acc_book_code=?1 and a.center_code = ?2 order by value desc", nativeQuery = true)
    List<Map<String, Object>> findYearMonthAll(String account, String centerCode);
    /**
     * 查询全部会计期间（不包含决算）
     * @return
     */
    @Query(value="SELECT a.year_month_date as value,a.year_month_date as text from AccMonthTrace a  where a.acc_book_code=?1 and a.center_code = ?2 and right(a.year_month_date, 2) != 'JS' order by value desc", nativeQuery = true)
    List<Map<String, Object>> findYearMonthAllAndNotJS(String account, String centerCode);
    /**
     * 查询会计月度表中存在的所有年份
     * @return
     */
    @Query(value="select distinct left(a.year_month_date,4) as value,left(a.year_month_date,4) as text from AccMonthTrace a where a.acc_book_code=?1 and a.center_code = ?2 order by value", nativeQuery = true)
    List<Map<String, Object>> findYearsByAccMonthTrace(String account, String centerCode);
    /**
     * 查询当前年份全部会计期间
     * @return
     */
    @Query(value="SELECT a.year_month_date as value,a.year_month_date as text from AccMonthTrace a  where a.acc_book_code=?1 and a.year_month_date like ?2% and a.center_code = ?3 order by value", nativeQuery = true)
    List<Map<String, Object>> findYearMonthByCurrentYear(String account, String currentYear, String centerCode);
    /**
     * 查询所有已结转的会计期间（不包含决算）
     * @return
     */
    @Query(value="SELECT a.year_month_date as value,a.year_month_date as text from AccMonthTrace a  where a.acc_book_code=?1 and a.center_code = ?2 and a.acc_month_stat ='3'  order by value desc", nativeQuery = true)
    List<Map<String, Object>> findJZYearMonth(String account, String centerCode);
    /**
     * 查询未结转的会计期间
     * @return
     */
    @Query(value="SELECT a.year_month_date as value,a.year_month_date as text,'未结转' as 'group' from AccMonthTrace a  where a.acc_month_stat not in('3','5') and a.acc_book_code=?1 and a.center_code = ?2 order by value desc", nativeQuery = true)
    List<Map<String, Object>> findYearMonth1(String account, String centerCode);
    /**
     * 查询已结转的会计期间
     * @return
     */
    @Query(value="SELECT a.year_month_date as value,a.year_month_date as text,'已结转' as 'group' from AccMonthTrace a  where a.acc_month_stat in('3','5') and a.acc_book_code=?1 and a.center_code = ?2 order by value desc", nativeQuery = true)
    List<Map<String, Object>> findYearMonth2(String account, String centerCode);

    /**
     * 根据指定账套查询四大报表下拉
     * @param account
     * @return
     */
    @Query(value="SELECT c.code_code AS value,c.code_name AS text FROM codemanage c WHERE c.code_type LIKE 'SDBBreportType' AND c.temp LIKE %?1% ORDER BY value ASC", nativeQuery = true)
    List<Map<String, Object>> findSDBBreportTypeByAccount(String account);

    /**
     * 根据指定账套和类型查询四大报表基本表
     * @param account
     * @return
     */
    @Query(value="SELECT c.code_code AS value,c.code_name AS text FROM codemanage c WHERE c.code_type LIKE 'SDBBreportType' AND c.temp LIKE %?1% AND c.order_by =?2 ORDER BY value ASC", nativeQuery = true)
    List<Map<String, Object>> findSDBBreportTypeByAccountAndType1(String account, String type);

    /**
     * 查询四大报表合并表
     * @return
     */
    @Query(value="SELECT c.code_code AS value,c.order_by AS text,c.code_name as codeName FROM codemanage c WHERE c.code_type LIKE 'SDBBreportType' AND c.temp LIKE %?1%  and c.order_by in (2,3) ORDER BY value ASC", nativeQuery = true)
    List<Map<String, Object>> findSDBBreport(String account);
    /**
     * 查询报表结果表数据
     * @return
     */
    @Query(value="SELECT  DISTINCT  r.acc_book_code AS value, r.report_code AS text FROM reportdata r WHERE r.`report_code` IN ('3#1','4#1','5#1','6#1') AND r.year_month_date = ?1  AND r.version = ?2 AND r.unit = ?3 and r.center_code=?4 and r.acc_book_code=?5 ORDER BY value ASC", nativeQuery = true)
    List<Map<String, Object>> findDataFromReportData(String yearMonthDate,String version ,String uint,String centerCode, String accBookCode);
    /**
     * 查询固定资产信息变动年份
     * @return
     */
    @Query( value = "SELECT LEFT(a.change_code,4) AS value,LEFT(a.change_code,4) AS text FROM accAssetInfoChange a  WHERE a.acc_book_code=?1 and a.center_code = ?2 GROUP BY VALUE ORDER BY VALUE DESC ",nativeQuery = true)
    List<Map<String, Object>> findChangeCodeByAccount(String account,String centerCode);
    /**
     * 查询无形资产信息变动年份
     * @return
     */
    @Query( value = "SELECT LEFT(i.change_code,4) AS value,LEFT(i.change_code,4) AS text FROM intangibleAccAssetInfoChange i  WHERE i.acc_book_code=?1 and i.center_code = ?2   GROUP BY VALUE ORDER BY VALUE DESC ",nativeQuery = true)
    List<Map<String, Object>> findChangeIntangibleCodeByAccount(String account,String centerCode);

    @Query(value = "select code_name from codemanage where code_code = ?1 and code_type='clearCode' ",nativeQuery = true)
    List<?> findCodeNameByCodeCode(String codeCode);
}
