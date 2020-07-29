package com.sinosoft.repository.intangibleassets;

import com.sinosoft.domain.intangibleassets.IntangibleAccDepre;
import com.sinosoft.domain.intangibleassets.IntangibleAccDepreId;
import com.sinosoft.repository.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author jiangyc
 * @Description
 * @create 2019-04-10 10:54
 */
@Repository
public interface IntangibleAccDepreRepository extends BaseRepository<IntangibleAccDepre, IntangibleAccDepreId> {

    /**
     * 根据折旧年月删除折旧记录表中数据
     * @param yearMonthData
     */
    @Modifying
    @Query(value = "delete from IntangibleAccDepre where year_month_data = ?1 and center_code=?2 and acc_book_code=?3 ", nativeQuery = true)
    void delByYearMonthData(String yearMonthData,String centerCode,String accBookCode);

    /**
     * 凭证号清空
     * @param voucherNo
     */
    @Modifying
    @Query(value = "update IntangibleAccDepre set voucher_no = '' where voucher_no= ?1 and acc_book_type = ?2 and acc_book_code = ?3 and center_code = ?4", nativeQuery = true)
    void clearVoucherNo(String voucherNo, String accBookType, String accBookCode,String centerCode);


    @Query(value = "select * from IntangibleAccDepre where 1=1 and center_code = ?1  and acc_book_type = ?2 and acc_book_code = ?3 and code_type = ?4 and asset_code = ?5 ", nativeQuery = true)
    List<IntangibleAccDepre> queryIntangibleAccDepreByAssetCode(String centerCode, String accBookType, String accBookCode, String codeType, String assetCode);

}
