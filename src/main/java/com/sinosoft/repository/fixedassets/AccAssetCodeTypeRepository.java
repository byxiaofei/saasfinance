package com.sinosoft.repository.fixedassets;

import com.sinosoft.common.CurrentUser;
import com.sinosoft.domain.fixedassets.AccAssetCodeType;
import com.sinosoft.domain.fixedassets.AccAssetCodeTypeId;
import com.sinosoft.repository.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author jiangyc
 * @Description
 * @create 2019-03-26 17:16
 */
@Repository
public interface AccAssetCodeTypeRepository extends BaseRepository<AccAssetCodeType, AccAssetCodeTypeId> {

    /**
     * 通过Id查询数据是否存在
     * @param accBookType
     * @param accBookCode
     * @param codeType
     * @param assetType
     * @return
     */
    @Query(value = "select * from accassetcodetype where  acc_book_type = ?1 and acc_book_code = ?2 and code_type = ?3 and asset_type = ?4", nativeQuery = true)
    List<Map<String, String>> qryById(String accBookType, String accBookCode, String codeType, String assetType);

    /**
     * 通过ID删除accassetcodetype中信息
     * @param accBookType
     * @param accBookCode
     * @param codeType
     * @param assetType
     */
    @Modifying
    @Query(value = "delete from accassetcodetype where acc_book_type = ?1 and acc_book_code = ?2 and code_type = ?3 and asset_type = ?4", nativeQuery = true)
    void deleteCategoryCodingData(String accBookType, String accBookCode, String codeType, String assetType);
    //通过类别全称查类别编码
   /* @Query(value=" select * from accassetcodetype  where center_code=?1 and branch_code=?2 and acc_book_type=?3 and acc_book_code=?4 and code_type=?5 and asset_complex_name=?6", nativeQuery = true)
    List<AccAssetCodeType> getAssetTypeByName(String centerCode, String branchCode, String accBookType, String accBookCode, String codeType, String assetComplex_name);
   */
    @Query(value=" select * from AccAssetCodeType  where  acc_book_type=?1 and acc_book_code=?2 and code_type=?3 and asset_type =?4", nativeQuery = true)
    List<AccAssetCodeType> getAssetType(String accBookType,String accBookCode,String codeType,String assetType);
    //查看类别是否有下级
    @Query(value = "select count(asset_type) from accassetcodetype where  acc_book_type=?1 and acc_book_code=?2 and code_type=?3 and super_code=?4", nativeQuery = true)
    Integer lowerlevel( String accBookType, String accBookCode, String codeType, String assetType);

    @Query(value="select * from accassetcodetype where acc_book_code=?1 and code_type=?2 and asset_type=?3", nativeQuery = true)
    List<AccAssetCodeType> getaccassetcode(String accBookCode,String codeType,String assetType);

    /**
     *
     * 功能描述:    判断是新增还是修改
     *
     */
    @Query(value = "select * from accassetcodetype where acc_book_type = ?1 and acc_book_code = ?2 and code_type = '21' and asset_type = ?3",nativeQuery = true)
    List<AccAssetCodeType> checkInsertOrUpdateOprration(String accBookType,String accBookCode,String assetType);

    /**
     *
     * 功能描述:    判断当前类别编码是否存在
     *
     */
    @Query(value = "select * from AccAssetCodeType where asset_type = ?1 and acc_book_type = ?2 and acc_book_code = ?3",nativeQuery = true)
    List<?> queryCategoryNoByAssetTypeAndAccBookTypeAndAccBookCode(String assetType,String accBookType,String accBookCode);

    /**
     *
     * 功能描述:    判断该类别是否已被卡片使用
     *
     */
    @Query(value = "select * from AccAssetInfo where center_code=?1 and asset_type = ?2 and acc_book_type = ?3 and acc_book_code = ?4",nativeQuery = true)
    List<?> queryAccAssetInfoByCenterCodeAndAssetTypeAndAccBookTypeAndAccBookCode(String centerCode,String assetType,String accBookType,String accBookCode);


    @Query(value = "select * from accassetcodetype a where a.asset_type = ?1 and acc_book_type = ?2 and acc_book_code = ?3  ",nativeQuery = true)
    List<AccAssetCodeType> queryAccAssetCodeTypeByChooseMessage(String assetType,String accBookType, String accBookCode );


//    @Query(value = "select asset_type as id, asset_simple_name as text, asset_type as mid,end_flag as endFlag from accassetcodetype where level=1 and acc_book_type = ?1 and acc_book_code = ?2 ",nativeQuery = true)
//    List<?> queryAccAssetCodeTypeByAccBookTypeAndAccBookCode(String accBookType,String accBookCode);


    @Query(value = "select asset_type as id, asset_simple_name as text, asset_type as mid,end_flag as endFlag from accassetcodetype where super_code = ?1 and acc_book_type = ?2 and acc_book_code = ?3 ",nativeQuery = true)
    List<?> queryAccAssetCodeTypeBySuperCodeAndAccBookTypeAndAccBookCode(String id,String accBookType,String accBookCode);

    //通过管理类别编码去获取科目代码、科目段、专项段
    @Query(value = "select * from accassetcodetype where 1=1 and acc_book_type = ?1 and acc_book_code =  ?2  and code_type = '21' and asset_type = ?3",nativeQuery = true)
    List<AccAssetCodeType> queryAccAssetCodeTypeByAccBookTypeAndAccBookCodeAndAssetType(String accBookType,String accBookCode,String assetType);

    //通过管理类别编码去获取科目代码、科目段、专项段
    @Query(value = "select * from accassetcodetype where 1=1 and acc_book_type = ?1 and acc_book_code =  ?2  and code_type = ?3 and asset_type = ?4",nativeQuery = true)
    List<AccAssetCodeType> queryAccAssetCodeTypeByAccBookTypeAndAccBookCodeAndAssetTypeChooseCodeType(String accBookType,String accBookCode,String codeType,String assetType);
}
