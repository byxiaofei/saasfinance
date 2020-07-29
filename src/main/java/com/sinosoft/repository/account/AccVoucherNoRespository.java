package com.sinosoft.repository.account;

import com.sinosoft.domain.account.AccVoucherNo;
import com.sinosoft.domain.account.AccVoucherNoId;
import com.sinosoft.repository.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author jiangyc
 * @Description
 * @create 2019-03-13 16:03
 */
@Repository
public interface AccVoucherNoRespository extends BaseRepository<AccVoucherNo, AccVoucherNoId> {

    /**
     * 生成凭证时最大凭证号+1
     * @param yearMonthDate
     * @param accBookType
     * @param accBookCode
     */
    @Modifying
    @Query(value = "update accvoucherno set voucher_no = voucher_no+1 where year_month_date = ?1 and acc_book_type = ?2 and acc_book_code = ?3 and center_code = ?4", nativeQuery = true)
    void updateAddVoucherNo(String yearMonthDate, String accBookType, String accBookCode, String centerCode);

    /**
     * 凭证回退时最大凭证号-1
     * @param yearMonthDate
     * @param accBookType
     * @param accBookCode
     */
    @Modifying
    @Query(value = "update accvoucherno set voucher_no = voucher_no-1 where year_month_date = ?1 and acc_book_type = ?2 and acc_book_code = ?3 and center_code = ?4", nativeQuery = true)
    void updateSubVoucherNo(String yearMonthDate, String accBookType, String accBookCode, String centerCode);


    //从accvoucherno表中获取最大凭证号
    @Query(value = "select * from accvoucherno where center_code= ?1 and  year_month_date = ?2  and acc_book_type = ?3 and acc_book_code = ?4 ",nativeQuery = true)
    List<AccVoucherNo> queryAccVoucherNoByChooseMessage(String centerCode,String yearMonthDate,String accBookType,String accBookCode);
}
