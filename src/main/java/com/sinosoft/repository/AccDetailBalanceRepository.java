package com.sinosoft.repository;

import com.sinosoft.domain.account.AccDetailBalance;
import com.sinosoft.domain.account.AccDetailBalanceId;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccDetailBalanceRepository extends  BaseRepository<AccDetailBalance, AccDetailBalanceId> {

    /**
     * 根据年月将数据从专项余额表中 复制 到历史专项余额表中
     * @param yearMonthDate
     */
    @Modifying
    @Query(value = "insert into accdetailbalancehis select * from accdetailbalance where year_month_date = ?1 and acc_book_type = ?2 and acc_book_code = ?3 and center_code = ?4 and branch_code = ?5", nativeQuery = true)
    void updateHisData(String yearMonthDate, String accBookType, String accBookCode, String centerCode, String branchCode);

    /**
     * 根据年月将数据从历史专项余额表中 复制 到专项余额表中
     * @param yearMonthDate
     */
    @Modifying
    @Query(value = "insert into accdetailbalance select * from accdetailbalancehis where year_month_date = ?1 and acc_book_type = ?2 and acc_book_code = ?3 and center_code = ?4 and branch_code = ?5", nativeQuery = true)
    void updateLastData(String yearMonthDate, String accBookType, String accBookCode, String centerCode, String branchCode);

    /**
     * 将余额表年月改为下个会计期间
     * @param yearMonthDate
     */
    @Modifying
    @Query(value = "update accdetailbalance set year_month_date=?1 where year_month_date = ?2 and acc_book_type = ?3 and acc_book_code = ?4 and center_code = ?5 and branch_code = ?6", nativeQuery = true)
    void updateYearMonth(String newYearMonthDate,String yearMonthDate, String accBookType, String accBookCode, String centerCode, String branchCode);

    /**
     * 清空专项余额表数据
     */
    @Modifying
    @Query(value = "delete from accdetailbalance where acc_book_type = ?1 and acc_book_code = ?2 and center_code = ?3 and branch_code = ?4", nativeQuery = true)
    void clear(String accBookType, String accBookCode, String centerCode, String branchCode);

    @Query(value = "select * from accdetailbalance a where a.center_code = ?1 and a.branch_code = ?2 and a.acc_book_type = ?3 and a.acc_book_code = ?4 and a.year_month_date = ?5", nativeQuery = true)
    List<AccDetailBalance> qryAccDetailBalanceByYearMonthDate(String centerCode, String branchCode, String accBookType, String accBookCode, String yearMonthDate);

    @Query(value = "select * from accdetailbalance a where a.center_code = ?1 and a.branch_code = ?2 and a.acc_book_type = ?3 and a.acc_book_code = ?4 and a.year_month_date = ?5 and item_code = ?6 and a.direction_idx = ?7", nativeQuery = true)
    List<AccDetailBalance> qryAccDetailBalanceByYearMonthDateAndDirectionIdx(String centerCode, String branchCode, String accBookType, String accBookCode, String yearMonthDate, String itemCode, String directionIdx);

    @Query(value = "select * from accdetailbalance a where a.center_code = ?1 and a.branch_code = ?2 and a.acc_book_type = ?3 and a.acc_book_code = ?4 and a.year_month_date = ?5 ", nativeQuery = true)
    List<AccDetailBalance> qryAccDetailBalanceByYearMonthDateAndDirectionIdx(String centerCode, String branchCode, String accBookType, String accBookCode, String yearMonthDate);

}
