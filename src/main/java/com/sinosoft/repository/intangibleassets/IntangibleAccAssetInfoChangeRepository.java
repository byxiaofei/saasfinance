package com.sinosoft.repository.intangibleassets;

import com.sinosoft.domain.fixedassets.AccAssetInfoChange;
import com.sinosoft.domain.fixedassets.AccAssetInfoChangeId;
import com.sinosoft.domain.intangibleassets.IntangibleAccAssetInfoChange;
import com.sinosoft.domain.intangibleassets.IntangibleAccAssetInfoChangeId;
import com.sinosoft.domain.intangibleassets.IntangibleAccAssetInfoId;
import com.sinosoft.repository.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zhangst
 * @Description
 * @create
 *    变动表
 * 无形资产卡片变动管理
 */
@Repository
public interface IntangibleAccAssetInfoChangeRepository extends BaseRepository<IntangibleAccAssetInfoChange,IntangibleAccAssetInfoChangeId> {
    //获取无形固定资产摊销方法
    @Query(value="select dep_type from IntangibleAccAssetCodeType where  acc_book_type=?1 and acc_book_code=?2 and code_type=?3 and asset_type=?4", nativeQuery = true)
    String getAccAssetInfo( String accBookType, String accBookCode, String codeType, String assetType);
    @Modifying
    @Query(value=" update IntangibleAccAssetInfo set use_flag=?1 where center_code=?2 and branch_code=?3 and acc_book_type=?4 and acc_book_code=?5  and code_type=?6 and card_code=?7", nativeQuery = true)
    Integer updateUseFlag(String useFlag,String centerCode, String branchCode, String accBookType, String accBookCode, String codeType,String cardCode);
   /* @Modifying
    @Query(value=" update IntangibleAccAssetInfo set clear_flag=?1 where center_code=?2 and branch_code=?3 and acc_book_type=?4 and acc_book_code=?5  and code_type=?6 and card_code=?7", nativeQuery = true)
    Integer clearCard(String clearFlag,String centerCode, String branchCode, String accBookType, String accBookCode, String codeType,String cardCode);
*/
   @Modifying
   @Query(value="delete from IntangibleAccAssetInfoChange where center_code=?1 and branch_code=?2 and acc_book_type=?3 and acc_book_code=?4 and  change_code=?5", nativeQuery = true)
   Integer deleteAccAssetInfoChange(String centerCode, String branchCode, String accBookType, String accBookCode, String changeCode);

    //获取卡片数目
   @Query(value = "select * from intangibleaccassetinfochange  where center_code= ?1 and acc_book_code= ?2 and  change_code in (?3) group by card_code",nativeQuery = true)
    List<IntangibleAccAssetInfoChange> queryIntangibleAccAssetInfoChangeCardCount(String centerCode,String accBookCode,String[] changeCode);

    //获取变动类型数目
    @Query(value = "select * from intangibleaccassetinfochange where center_code= ?1 and acc_book_code= ?2 and card_code= ?3 and  change_code in (?4)  group by change_type",nativeQuery = true)
   List<IntangibleAccAssetInfoChange> queryIntangibleAccAssetInfoChangeMoveTypeCount(String centerCode,String accBookCode,String cardCode,String[] changeCode);

    //循环变动类型 从大到小
    @Query(value = "select * from intangibleaccassetinfochange where center_code= ?1 and acc_book_code= ?2 and card_code= ?3 and  change_code in (?4) and change_type=?5 ORDER BY change_code desc",nativeQuery = true)
    List<IntangibleAccAssetInfoChange> queryIntangibleAccAssetInfoChangeMoveTypeCountBigToSmall(String centerCode,String accBookCode,String cardCode,String[] changeCode,String changeType);

    //1.查询最大变动单
    @Query(value = "select * from intangibleaccassetinfochange where center_code= ?1 and acc_book_code= ?2 and card_code= ?3 and  change_type= ?4  ORDER BY change_code desc ",nativeQuery = true)
    List<IntangibleAccAssetInfoChange> queryIntangibleAccAssetInfoChangeMaxMoveTypeInfo(String centerCode,String accBookCode,String cardCode,String changeType);

    //2.判断选中的数据是否连续
    @Query(value = "select * from intangibleaccassetinfochange where center_code= ?1 and acc_book_code= ?2 and card_code= ?3  and change_type= ?4 and change_code >= ?5 and change_code<= ?6 ORDER BY change_code desc",nativeQuery = true)
    List<IntangibleAccAssetInfoChange> queryIntangibleAccAssetInfoChangeContinuous(String centerCode,String accBookCode,String cardCode,String changeType,String minChangeCode,String maxChangeCode);
    //查询选中中数据是否连续
    @Query(value = "select * from intangibleaccassetinfochange where center_code= ?1 and acc_book_code= ?2 and card_code= ?3  and change_type= ?4 and change_code >= ?5 and change_code<= ?6 AND change_code in(?7) ORDER BY change_code desc",nativeQuery = true)
    List<IntangibleAccAssetInfoChange> queryIntangibleAccAssetInfoChangeContinuousANDChangeCode(String centerCode,String accBookCode,String cardCode,String changeType,String minChangeCode,String maxChangeCode,String[] changeCodeArr);

}