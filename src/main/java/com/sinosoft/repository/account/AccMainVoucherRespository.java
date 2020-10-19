package com.sinosoft.repository.account;

import com.sinosoft.domain.account.AccDetailBalanceHis;
import com.sinosoft.domain.account.AccMainVoucher;
import com.sinosoft.domain.account.AccMainVoucherId;
import com.sinosoft.repository.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Repository
public interface AccMainVoucherRespository extends BaseRepository<AccMainVoucher,Integer> {

    /**
     * 主表之后会计期间状态修改
     * @param yearMonthDate
     * @param createBy
     * @param createTime
     */
    @Modifying
    @Query(value = "update accmainvoucher set voucher_flag = '2' , create_by = ?2 , create_time = ?3 where voucher_flag >= 3 and acc_book_type = ?4 and acc_book_code = ?5 and year_month_date >= ?1 and center_code = ?6 and branch_code = ?7", nativeQuery = true)
    void updateFlag(String yearMonthDate, String createBy, String createTime, String accBookType, String accBookCode, String centerCode, String branchCode);

    /**
     * 当前会计期间状态修改
     * @param yearMonthDate
     * @param createBy
     * @param createTime
     */
    @Modifying
    @Query(value = "update accmainvoucher set voucher_flag = '3' , create_by = ?2 , create_time = ?3 where acc_book_type = ?4 and acc_book_code = ?5 and year_month_date = ?1 and center_code = ?6 and branch_code = ?7", nativeQuery = true)
    void updateDQFlag(String yearMonthDate, String createBy, String createTime, String accBookType, String accBookCode, String centerCode, String branchCode);

    /**
     * 通过年月，删除主表信息
     * @param ymd
     */
    @Modifying
    @Query(value = "delete from accmainvoucher where year_month_date = ?1 and acc_book_type = ?2 and acc_book_code = ?3 and center_code = ?4 and branch_code = ?5", nativeQuery = true)
    void deleteMainByYmd(String ymd, String accBookType, String accBookCode, String centerCode, String branchCode);

    /**
     * 通过年月、凭证号，删除主表信息
     * @param ymd
     */
    @Modifying
    @Query(value = "delete from accmainvoucher where year_month_date = ?1 and acc_book_type = ?2 and acc_book_code = ?3 and voucher_no = ?4 and center_code = ?5 and branch_code = ?6", nativeQuery = true)
    void deleteMainByYmdAndVoucherNo(String ymd, String accBookType, String accBookCode, String voucherNo, String centerCode, String branchCode);

    /**
     * 通过年月，删除子表信息
     * @param ymd
     */
    @Modifying
    @Query(value = "delete from accsubvoucher where year_month_date = ?1 and acc_book_type = ?2 and acc_book_code = ?3 and center_code = ?4 and branch_code = ?5", nativeQuery = true)
    void deleteSubByYmd(String ymd, String accBookType, String accBookCode, String centerCode, String branchCode);

    /**
     * 查询凭证主表数据（决算凭证）
     * @param centerCode
     * @param branchCode
     * @param accBookType
     * @param accBookCode
     * @param yearMonthDate
     * @return
     */
    @Query(value = "SELECT * FROM accmainvoucher a WHERE a.center_code = ?1 and a.branch_code = ?2 and a.acc_book_type = ?3 AND a.acc_book_code = ?4 AND a.year_month_date = ?5 AND a.generate_way = '1' AND a.voucher_type = '1'",nativeQuery = true)
    AccMainVoucher qryFinanAccMainVoucher(String centerCode, String branchCode, String accBookType, String accBookCode, String yearMonthDate);

    @Query(value = "select * from accmainvoucher a where a.center_code = ?1 and a.branch_code = ?2 and a.acc_book_type = ?3 and a.acc_book_code = ?4 and a.year_month_date = ?5 and a.voucher_flag != '3'", nativeQuery = true)
    List<AccMainVoucher> qryNoAccountVoucherByYearMonthDate(String centerCode, String branchCode, String accBookType, String accBookCode, String yearMonthDate);

    @Query(value = "select * from accmainvoucher a where a.center_code = ?1 and a.branch_code = ?2 and a.acc_book_type = ?3 and a.acc_book_code = ?4 and a.year_month_date = ?5 and a.voucher_no > ?6 order by a.voucher_no asc", nativeQuery = true)
    List<AccMainVoucher> qryAfterVoucherByYearMonthDateAndVoucherNo(String centerCode, String branchCode, String accBookType, String accBookCode, String yearMonthDate, String currentVoucherNo);

    @Query(value = "select * from accmainvoucher a where a.center_code = ?1 and a.branch_code = ?2 and a.acc_book_type = ?3 and a.acc_book_code = ?4 and a.year_month_date = ?5 and a.voucher_no >= ?6 order by a.voucher_no asc", nativeQuery = true)
    List<AccMainVoucher> qrySelfAndAfterVoucherByYearMonthDateAndVoucherNo(String centerCode, String branchCode, String accBookType, String accBookCode, String yearMonthDate, String currentVoucherNo);

    @Query(value = "select * from accmainvoucher a where 1=1 and a.center_code = ?1 and a.branch_code = ?2 and a.acc_book_type = ?3 and a.acc_book_code = ?4 and a.year_month_date = ?5 and a.generate_way = '1' and a.voucher_type = '1'",nativeQuery = true)
    List<AccMainVoucher> queryAccMainVoucherByChooseMessage(String centerCode, String branchCode, String accBookType, String accBookCode, String yearMonthDate);

    /**
     *  根据月份查询凭证主表的信息。
     * @param centerCode
     * @param branchCode
     * @param accBookType
     * @param accBookCode
     * @param yearMonthDate
     * @return
     */
    @Query(value = "select * from accmainvoucher a where 1=1 and a.center_code = ?1 and a.branch_code = ?2 and a.acc_book_type = ?3 and a.acc_book_code = ?4 and a.year_month_date = ?5 ",nativeQuery = true)
    List<AccMainVoucher> queryAccMainVoucherByBaseChoose(String centerCode, String branchCode, String accBookType, String accBookCode, String yearMonthDate);
}
