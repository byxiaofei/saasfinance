package com.sinosoft.repository.account;

import com.sinosoft.domain.account.AccSubVoucher;
import com.sinosoft.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccSubVoucherRespository extends BaseRepository<AccSubVoucher,Integer> {

    @Query(value = "select * from accsubvoucher a where a.center_code= ?1 and a.branch_code = ?2 and a.acc_book_type = ?3 and a.acc_book_code = ?4 and a.year_month_date = ?5 and a.voucher_no = ?6 order by a.suffix_no", nativeQuery = true)
    List<AccSubVoucher> getAccSubVoucherByYearMonthDateAndVoucherNo(String centerCode, String branch, String accBookType, String accBookCode, String yearMonthDate, String voucherNo);

    @Query(value = "select * from accsubvoucher a where a.center_code = ?1 and a.branch_code = ?2 and a.acc_book_type = ?3 and a.acc_book_code = ?4 and a.year_month_date = ?5 and a.voucher_no > ?6 order by a.voucher_no asc,a.suffix_no asc", nativeQuery = true)
    List<AccSubVoucher> qryAfterVoucherByYearMonthDateAndVoucherNo(String centerCode, String branchCode, String accBookType, String accBookCode, String yearMonthDate, String currentVoucherNo);

    @Query(value = "select * from accsubvoucher a where a.center_code = ?1 and a.branch_code = ?2 and a.acc_book_type = ?3 and a.acc_book_code = ?4 and a.year_month_date = ?5 and a.voucher_no >= ?6 order by a.voucher_no asc,a.suffix_no asc", nativeQuery = true)
    List<AccSubVoucher> qrySelfAndAfterVoucherByYearMonthDateAndVoucherNo(String centerCode, String branchCode, String accBookType, String accBookCode, String yearMonthDate, String currentVoucherNo);

    //先根据主键去 凭证子表中查询，如果信息存在则凭证号+1，若不存在，凭证号为1
    @Query(value = "select * from accsubvoucher where center_code = ?1 and  branch_code = ?2 and acc_book_type = ?3 and acc_book_code = ?4 and year_month_date = ?5 and  voucher_no = ?6 and suffix_no= (select MAX(suffix_no) from accsubvoucher where voucher_no = ?7 )  ",nativeQuery = true)
    List<AccSubVoucher> queryAccSubVoucherByAccId(String centerCode , String branchCode ,String accBookType ,String accBookCode ,String yearMonthDate , String voucherNo , String vourcherNo1);

    //判断科目专项是否相同，合并金额
    @Query(value = "select * from accsubvoucher where acc_book_code= ?1 and voucher_no= ?2 and direction_idx= ?3 and direction_other= ?4 " ,nativeQuery = true)
    List<AccSubVoucher> queryAccSubVoucherByAccBookCodeAndVoucherNoAndDirectionIdxAndDirectionOther(String accBookCode,String voucherNo , String directionIdx,String directionOther);

    //判断科目专项是否相同，合并金额
    @Query(value = "select * from accsubvoucher where  acc_book_code= ?1 and voucher_no= ?2 and direction_idx= ?3 and direction_other= ?4 and center_code = ?5" ,nativeQuery = true)
    List<AccSubVoucher> queryAccSubVoucherByAccBookCodeAndVoucherNoAndDirectionIdxAndDirectionOtherAndCenterCode(String accBookCode,String voucherNo , String directionIdx,String directionOther,String centerCode);
}
