package com.sinosoft.repository.account;

import com.sinosoft.domain.account.AccArticleBalanceHis;
import com.sinosoft.domain.account.AccArticleBalanceId;
import com.sinosoft.repository.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author jiangyc
 * @Description
 * @create 2019-03-13 10:28
 */
@Repository
public interface AccArticleBalanceHisRespository extends BaseRepository<AccArticleBalanceHis, AccArticleBalanceId> {

    /**
     * 批量反结转 批量删除方法
     * @param yearMonthDate
     */
    @Modifying
    @Query(value = "delete from accArticleBalanceHis where acc_book_type = ?2 and acc_book_code = ?3 and year_month_date >= ?1 and center_code = ?4 and branch_code = ?5", nativeQuery = true)
    void deleteDate(String yearMonthDate, String accBookType, String accBookCode, String centerCode, String branchCode);
}
