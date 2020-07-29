package com.sinosoft.repository.intangibleassets;

import com.sinosoft.domain.fixedassets.AccDepre;
import com.sinosoft.domain.fixedassets.AccDepreId;
import com.sinosoft.domain.intangibleassets.IntangibleAccDepre;
import com.sinosoft.domain.intangibleassets.IntangibleAccDepreId;
import com.sinosoft.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author zst
 * @Description
 * @create 2019-03-29 15:13
 * 固定资产查询
 */
@Repository
public interface IntangibleAccAssetSelectRepository extends BaseRepository<IntangibleAccDepre, IntangibleAccDepreId> {

    @Query(value="    select * from IntangibleAccDepre where center_code=?1 and branch_code=?2 and acc_book_type=?3 and acc_book_code=?4 and code_type=?5 and asset_type=?6 and asset_code=?7", nativeQuery = true)
    List<Map<String ,Object>> getIntangibleAccDepre(String centerCode, String branchCode, String accBookType, String accBookCode, String codeType, String assetType, String assetCode);
    @Query(value="select asset_origin_value as assetOriginValue,asset_net_value as assetNetValue,impairment,end_depre_amount as endDepreAmount,end_depre_money as endDepreMoney,init_depre_money as initDepreMoney from IntangibleAccAssetInfo where center_code=?1 and branch_code=?2 and acc_book_type=?3 and acc_book_code=?4 and code_type=?5  and card_code=?6", nativeQuery = true)
    List<Map<String ,Object>> getAccAssetValue(String centerCode, String branchCode, String accBookType, String accBookCode, String codeType, String cardCode);
    @Query(value="select all_depre_money as allDepreMoney,all_depre_quantity as allDepreQuantity  from IntangibleAccDepre where center_code=?1 and branch_code=?2 and acc_book_type=?3 and acc_book_code=?4 and code_type=?5 and asset_type=?6 and asset_code=?7 and year_month_data=?8", nativeQuery = true)
    Map<String ,BigDecimal> getCount(String centerCode, String branchCode, String accBookType, String accBookCode, String codeType, String assetType, String assetCode, String yearMonthData);

}
