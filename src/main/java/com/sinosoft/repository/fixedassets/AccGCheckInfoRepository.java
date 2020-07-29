package com.sinosoft.repository.fixedassets;

import com.sinosoft.domain.account.AccDetailBalance;
import com.sinosoft.domain.fixedassets.AccAssetInfo;
import com.sinosoft.domain.fixedassets.AccGCheckInfo;
import com.sinosoft.domain.fixedassets.AccGCheckInfoId;
import com.sinosoft.dto.fixedassets.AccGCheckInfoDTO;
import com.sinosoft.repository.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author jiangyc
 * @Description
 * @create 2019-04-09 18:38
 */
@Repository
public interface AccGCheckInfoRepository extends BaseRepository<AccGCheckInfo, AccGCheckInfoId> {

    /**
     * 取消固定资产对账
     * @param yearMonthDate
     * @param accBookType
     * @param accBookCode
     */
    @Modifying
    @Query(value="update accgcheckinfo set is_check = '' , check_by = '' , check_time = '' where year_month_date >= ?1 and acc_book_type = ?2 and acc_book_code = ?3 and center_code = ?4", nativeQuery = true)
    void updateIsCheck(String yearMonthDate, String accBookType, String accBookCode,String centerCode);

    /**
     * 会计期间获取
     * @return
     */
    @Query(value="select year_month_date AS value,year_month_date AS text from AccGCheckInfo where acc_book_code = ?1 and center_code = ?2 order by year_month_date desc", nativeQuery = true)
    List<Map<String, Object>> getYearMonthDate(String accBookCode,String centerCode);

    /**
     * 起始层级获取
     * @return
     */
    @Query(value="select DISTINCT level as value, level as text from AccAssetCodeType where acc_book_code=?1 order by level asc", nativeQuery = true)
    List<Map<String, Object>> getStartLevel(String accBookCode);

    /**
     * 终止层级获取
     * @return
     */
    @Query(value="select DISTINCT level as value, level as text from AccAssetCodeType where acc_book_code=?1  order by level desc", nativeQuery = true)
    List<Map<String, Object>> geteEndLevel(String accBookCode);


    /**
     *
     * 功能描述: 先查找固定会计期间表，查找该会计期间是否折旧
     *
     */
    @Query(value = "select * from accgcheckinfo where center_code = ?1 and acc_book_code = ?2 and year_month_date= ?3 ",nativeQuery = true)
    List<AccGCheckInfo> findAccgcheckinfoByCenterCodeAndAccBookCodeAndYearMonthDate(String centerCode,String accBookCode,String yearMonthDate);



    @Query(value = "select year_month_date from AccGCheckInfo t1 where t1.center_code= ?1 and  t1.acc_book_code= ?2 and t1.year_month_date = ?3 ",nativeQuery = true)
    List<?> findYearMonthDateByCenterCodeAndAccBookCodeAndYearMonthDate(String centerCode, String accBookCode , String yearMonthDate);

    //启用时间对应的会计期间是否已计提
    @Query(value = "select * from accgcheckinfo a where 1=1 and a.flag != '0' and a.center_code = ?1 and  a.acc_book_code = ?2 and a.year_month_date= ?3 ",nativeQuery = true)
    List<AccGCheckInfo> findAccgcheckinfoByCenterCodeAndAccBookCodeAndYearMonthDateAndFlag(String centerCode,String accBookCode,String yearMonthDate);

    //折旧状态的会计期间不能回退
    @Query(value = "select * from AccGCheckInfo where center_code= ?1 and acc_book_code= ?2 and year_month_date= ?3",nativeQuery = true)
    List<AccGCheckInfo> queryAccGCheckInfoByCenterCodeAndAccBookCodeAndYearMonthDate(String centerCode,String accBookCode,String yearMonthDate);

    //
    @Query(value = "select * from accgcheckinfo where center_code= ?1 and year_month_date = ?2 and acc_book_type = ?3  and acc_book_code = ?4 ",nativeQuery = true)
    List<AccGCheckInfo> queryAccGCheckInfoByCenterCodeAndYearMonthDateAndAccBookTypeAndAccBookCode(String centerCode,String yearMonthDate,String accBookType,String accBookCode);
}
