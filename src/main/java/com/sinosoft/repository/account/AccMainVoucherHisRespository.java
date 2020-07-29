package com.sinosoft.repository.account;

import com.sinosoft.domain.account.AccMainVoucher;
import com.sinosoft.domain.account.AccMainVoucherHis;
import com.sinosoft.repository.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface AccMainVoucherHisRespository extends BaseRepository<AccMainVoucherHis,Integer> {
    /**
     * 通过年月，删除主表信息
     */
    @Modifying
    @Query(value = "delete from accmainvoucherhis where acc_book_type = ?2 and acc_book_code = ?3 and year_month_date >= ?1 and center_code = ?4 and branch_code = ?5", nativeQuery = true)
    void deleteMainByYmd(String yearMonthDate, String accBookType, String accBookCode, String centerCode, String branchCode);
    /**
     * 将数据从原表复制到历史表
     * @param ymd
     */
    @Modifying
    @Query(value = "insert into accmainvoucherhis select * from accmainvoucher where year_month_date = ?1 and acc_book_type = ?2 and acc_book_code = ?3 and center_code = ?4 and branch_code = ?5", nativeQuery = true)
    void copyToHis(String ymd, String accBookType, String accBookCode, String centerCode, String branchCode);

    /**
     * 将数据从历史表复制到原表
     */
    @Modifying
    @Query(value = "insert into accmainvoucher select * from accmainvoucherhis where acc_book_type = ?2 and acc_book_code = ?3 and year_month_date >= ?1 and center_code = ?4 and branch_code = ?5", nativeQuery = true)
    void copyToMain(String yearMonthDate, String accBookType, String accBookCode, String centerCode, String branchCode);

}
