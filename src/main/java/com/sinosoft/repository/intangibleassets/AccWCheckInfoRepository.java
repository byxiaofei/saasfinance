package com.sinosoft.repository.intangibleassets;

import com.sinosoft.domain.intangibleassets.AccWCheckInfo;
import com.sinosoft.domain.intangibleassets.AccWCheckInfoId;
import com.sinosoft.repository.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author jiangyc
 * @Description
 * @create 2019-04-09 19:37
 */
@Repository
public interface AccWCheckInfoRepository extends BaseRepository<AccWCheckInfo, AccWCheckInfoId>{

    /**
     * 取消固定资产对账
     * @param yearMonthDate
     * @param accBookType
     * @param accBookCode
     */
    @Modifying
    @Query(value="update accwcheckinfo set is_check = '' , check_by = '' , check_time = '' where year_month_date >= ?1 and acc_book_type = ?2 and acc_book_code = ?3 and center_code = ?4", nativeQuery = true)
    void updateIsCheck(String yearMonthDate, String accBookType, String accBookCode,String centerCode);

    /**
     * 会计期间获取
     * @return
     */
    @Query(value="select year_month_date AS value,year_month_date AS text from AccwCheckInfo where acc_book_code = ?1  and center_code = ?2 order by year_month_date desc", nativeQuery = true)
    List<Map<String, Object>> getYearMonthDate(String accBookCode,String centerCode);

    /**
     * 起始层级获取
     * @return
     */
    @Query(value="select DISTINCT level as value, level as text from IntangibleAccAssetCodeType where  acc_book_code=?1 order by level asc", nativeQuery = true)
    List<Map<String, Object>> getStartLevel(String accBookCode);

    /**
     * 终止层级获取
     * @return
     */
    @Query(value="select DISTINCT level as value, level as text from IntangibleAccAssetCodeType  where acc_book_code=?1 order by level desc", nativeQuery = true)
    List<Map<String, Object>> geteEndLevel( String accBookCode);

    //折旧状态的会计期间不能回退
    @Query(value = "select * from accwcheckinfo where center_code= ?1 and  acc_book_code= ?2 and year_month_date= ?3 ",nativeQuery = true)
    List<AccWCheckInfo> queryAccWCheckInfoByChooseMessage(String centerCode,String accBookCode,String yearMonthDate);




}
