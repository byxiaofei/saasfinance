package com.sinosoft.repository.intangibleassets;

import com.sinosoft.domain.intangibleassets.IntangibleAccAssetInfo;
import com.sinosoft.domain.intangibleassets.IntangibleAccAssetInfoId;
import com.sinosoft.repository.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


/**
 * 无形资产类别编码
 */
@Repository
public interface IntangibleAccAssetInfoRepository extends BaseRepository<IntangibleAccAssetInfo,IntangibleAccAssetInfoId> {

    /**
     * 加载固定资产类别编码中维护的类别
     * @return
     */
    @Query(value="select s.asset_type as value, s.asset_complex_name as text from IntangibleAccAssetCodeType s where  s.acc_book_code=?1", nativeQuery = true)
    List<Map<String, Object>> qryAssetType(String accBookCode);

    /**
     * 查询IntangibleAccAssetInfo表中最大卡片编码
     * @return
     */
    @Query(value="select MAX(card_code) from IntangibleAccAssetInfo where center_code=?1 and acc_book_code=?2", nativeQuery = true)
    String qryMaxCardCode(String centerCode, String accBookCode);

    @Modifying
    @Query(value="delete from IntangibleAccAssetInfo where center_code = ?1 and branch_code = ?2 and acc_book_type = ?3 " +
            "and acc_book_code = ?4 and code_type = ?5 and card_code = ?6", nativeQuery = true)
    void delete(String centerCode, String branchCode, String accBookType, String accBookCode, String codeType, String cardCode);

    /**
     * 凭证号清空
     * @param voucherNo
     */
    @Modifying
    @Query(value="update IntangibleAccAssetInfo set voucher_no = null where voucher_no= ?1 and acc_book_type = ?2 and acc_book_code = ?3 and center_code = ?4", nativeQuery = true)
    void clearVoucherNo(String voucherNo,String accBookType, String accBookCode,String centerCode);

    @Query(value = "select * from intangibleAccAssetInfo where 1=1 and center_code = ?1 and branch_code = ?2 and acc_book_type = ?3 and acc_book_code = ?4 and code_type = ?5 and card_code = ?6 ", nativeQuery = true)
    List<IntangibleAccAssetInfo> queryIntangibleAccAssetInfoByCardCode(String centerCode, String branchCode, String accBookType, String accBookCode, String codeType, String cardCode);


    @Query(value = "select * from intangibleaccassetinfo where acc_book_code= ?1 and card_code= ?2 and center_code = ?3 ",nativeQuery = true)
    List<IntangibleAccAssetInfo> queryIntangibleAccAssetInfoByAccBookCodeAndCardCodeAndCenterCode(String accBookCode,String cardCode,String centerCode);
}
