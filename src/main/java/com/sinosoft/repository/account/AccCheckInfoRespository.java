package com.sinosoft.repository.account;

import com.sinosoft.domain.account.AccCheckInfo;
import com.sinosoft.domain.account.AccMonthTraceId;
import com.sinosoft.repository.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AccCheckInfoRespository extends BaseRepository<AccCheckInfo, AccMonthTraceId>{

    /**
     * 对账信息表状态修改
     * @param yearMonthDate
     */
    @Modifying
    @Query(value = "update AccCheckInfo set is_carry = '' , is_check = '' where acc_book_type = ?2 and acc_book_code = ?3 and year_month_date >= ?1 and center_code = ?4", nativeQuery = true)
    void updateFlag(String yearMonthDate, String accBookType, String accBookCode, String centerCode);
}
