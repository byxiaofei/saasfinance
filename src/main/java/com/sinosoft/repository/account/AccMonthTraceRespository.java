package com.sinosoft.repository.account;

import com.sinosoft.domain.account.AccMonthTrace;
import com.sinosoft.domain.account.AccMonthTraceId;
import com.sinosoft.repository.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author jiangyc
 * @Description
 * @create 2019-03-12 11:17
 */
@Repository
public interface AccMonthTraceRespository extends BaseRepository<AccMonthTrace, AccMonthTraceId>{

    /**
     * 会计月度追加
     * @param AccBookCode
     * @param AccBookType
     * @param centerCode
     * @param nextYMD
     * @param flag
     */
    @Modifying
    @Query(value = "insert into AccMonthTrace (acc_book_code, acc_book_type, center_code, year_month_date, acc_month_stat, create_by, create_time, temp) values(?1,?2,?3,?4,?5,?6,?7,?8)", nativeQuery = true)
    void addYearMonthDate(String AccBookCode, String AccBookType, String centerCode, String nextYMD, String flag, String createBy, String createTime, String temp);

    /**
     * 当前会计月度之后状态修改
     * @param yearMonthDate
     * @param createBy
     * @param createTime
     */
    @Modifying
    @Query(value = "update AccMonthTrace set acc_month_stat = '2' , create_by = ?2 , create_time = ?3 where acc_month_stat >= '3' and acc_book_type = ?4 and acc_book_code = ?5 and year_month_date >= ?1 and center_code = ?6", nativeQuery = true)
    void updateFlag(String yearMonthDate, String createBy, String createTime, String accBookType, String accBookCode, String centerCode);

    /**
     * 当前会计月度状态修改
     * @param yearMonthDate
     * @param createBy
     * @param createTime
     */
    @Modifying
    @Query(value = "update AccMonthTrace set acc_month_stat = '2' , create_by = ?2 , create_time = ?3 where acc_book_type = ?4 and acc_book_code = ?5 and year_month_date = ?1 and center_code = ?6", nativeQuery = true)
    void updateDQFlag(String yearMonthDate, String createBy, String createTime, String accBookType, String accBookCode, String centerCode);

    /**
     * 当前会计月度状态修改
     * @param yearMonthDate
     * @param createBy
     * @param createTime
     * @param accBookType
     * @param accBookCode
     */
    @Modifying
    @Query(value = "update AccMonthTrace set acc_month_stat = '2' , create_by = ?2 , create_time = ?3 , temp = '' where acc_book_type = ?4 and acc_book_code = ?5 and year_month_date = ?1 and center_code = ?6", nativeQuery = true)
    void updateDQFlag2(String yearMonthDate, String createBy, String createTime, String accBookType, String accBookCode, String centerCode);

    /**
     *  把当前会计月度的 temp 修改为 2 变为不可修改的状态。
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "update AccMonthTrace set temp = ?1 where  year_month_date = ?2 and center_code = ?3", nativeQuery = true)
    void updateFlag2AboutTemp(String temp,String yearMonthDate,String centerCode );

    /**
     *  把当前会计月度的 temp 修改为 1 变为不可修改的状态。
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "update AccMonthTrace set temp = ?1 where  year_month_date = ?2 and center_code = ?3", nativeQuery = true)
    void updateFlag1AboutTemp(String temp,String yearMonthDate,String centerCode );

    // 寻找最大的会计期间月份
    @Query(value = "select * from accmonthtrace a where a.center_code = ?1 and a.acc_book_type = ?2 and a.acc_book_code = ?3 order by a.year_month_date desc limit 1", nativeQuery = true)
    AccMonthTrace findNewestAccMonthTrace(String centerCode, String accBookType, String accBookCode);
    // 寻找最小的会计期间月份
    @Query(value = "select * from accmonthtrace a where a.center_code = ?1 and a.acc_book_type = ?2 and a.acc_book_code = ?3 order by a.year_month_date asc limit 1", nativeQuery = true)
    AccMonthTrace findNewestAccMonthTraceASC(String centerCode, String accBookType, String accBookCode);

    @Query(value = "select * from accmonthtrace a where a.center_code = ?1 and a.acc_book_type = ?2 and a.acc_book_code = ?3 and a.year_month_date = ?4", nativeQuery = true)
    AccMonthTrace findAccMonthTraceByYearMonthDate(String centerCode, String accBookType, String accBookCode, String yearMonthDate);

    @Query(value = "select * from accmonthtrace a where a.center_code = ?1 and a.year_month_date = ?2", nativeQuery = true)
    AccMonthTrace findAccMonthTraceByYearMonthDate1(String centerCode, String yearMonthDate);

    @Query(value = "select * from accmonthtrace a where a.center_code = ?1 and a.acc_book_type = ?2 and a.acc_book_code = ?3 and acc_month_stat in (?4)", nativeQuery = true)
    List<AccMonthTrace> findAccMonthTraceByAccMonthStat(String centerCode, String accBookType, String accBookCode, String[] accMonthStat);

    @Query(value = "select * from accmonthtrace a where a.center_code = ?1 and a.acc_book_type = ?2 and a.acc_book_code = ?3 and a.year_month_date = ?4 and acc_month_stat in (?5)", nativeQuery = true)
    List<AccMonthTrace> findAccMonthTraceByYearMonthDateAndAccMonthStat(String centerCode, String accBookType, String accBookCode, String yearMonthDate, String[] accMonthStat);

    //判断当前会计期间是否结转，如为结转则不允许进行凭证生成操作
    @Query(value = "select * from accmonthtrace where center_code= ?1 and  year_month_date = ?2 and acc_month_stat > 2 and acc_book_type =?3 and acc_book_code = ?4 " ,nativeQuery = true)
    List<?> queryAccMonthTraceByChooseMessage(String centerCode , String yearMonthDate, String accBookType ,String accBookCode);

    @Query(value = "select * from accmonthtrace where center_code= ?1   and acc_book_code = ?2  and  year_month_date = ?3 and acc_month_stat > 2  " ,nativeQuery = true)
    List<?> queryAccMonthTraceByChooseMessage1(String centerCode  ,String accBookCode, String yearMonthDate);
}
