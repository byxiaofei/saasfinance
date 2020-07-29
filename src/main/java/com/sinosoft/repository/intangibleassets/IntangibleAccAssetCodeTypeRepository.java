package com.sinosoft.repository.intangibleassets;

import com.sinosoft.domain.fixedassets.AccAssetCodeType;
import com.sinosoft.domain.fixedassets.AccAssetCodeTypeId;
import com.sinosoft.domain.intangibleassets.IntangibleAccAssetCodeType;
import com.sinosoft.domain.intangibleassets.IntangibleAccAssetCodeTypeId;
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
public interface IntangibleAccAssetCodeTypeRepository extends BaseRepository<IntangibleAccAssetCodeType, IntangibleAccAssetCodeTypeId> {

    /**
     * 通过Id查询数据是否存在
     * @param centerCode
     * @param branchCode
     * @param accBookType
     * @param accBookCode
     * @param codeType
     * @param assetType
     * @return
     */
    @Query(value = "select * from intangibleaccassetcodetype where center_code = ?1 and branch_code = ?2 and acc_book_type = ?3 and acc_book_code = ?4 and code_type = ?5 and asset_type = ?6", nativeQuery = true)
    List<Map<String, String>> qryById(String centerCode, String branchCode, String accBookType, String accBookCode, String codeType, String assetType);

    /**
     * 通过ID删除accassetcodetype中信息
     * @param accBookType
     * @param accBookCode
     * @param codeType
     * @param assetType
     */
    @Modifying
    @Query(value = "delete from intangibleaccassetcodetype where acc_book_type = ?1 and acc_book_code = ?2 and code_type = ?3 and asset_type = ?4", nativeQuery = true)
    void deleteIntangiBleassetsData( String accBookType, String accBookCode, String codeType, String assetType);
    @Query(value=" select * from intangibleaccassetcodetype  where acc_book_type=?1 and acc_book_code=?2 and code_type=?3 and asset_type =?4", nativeQuery = true)
    List<IntangibleAccAssetCodeType> getAssetType(String accBookType,String accBookCode,String codeType,String assetType);


    //查看类别是否有下级
    @Query(value = "select count(asset_type) from intangibleaccassetcodetype where  acc_book_type=?1 and acc_book_code=?2 and code_type=?3 and super_code=?4", nativeQuery = true)
    Integer lowerlevel(String accBookType, String accBookCode, String codeType, String assetType);


    //通过管理类别编码去获取科目代码、科目段、专项段
    @Query(value = "select * from IntangibleAccAssetCodeType where acc_book_type = ?1 and acc_book_code = ?2  and code_type = '31' and asset_type = ?3 ",nativeQuery = true)
    List<IntangibleAccAssetCodeType> queryIntangibleAccAssetCodeTypeByAccBookTypeAndAccBookCodeAndCodeTypeAndAssetType(String accBookType,String accBookCode,String ssetType);

    //判断是新增操作还是修改操作
    @Query(value = "select * from intangibleaccassetcodetype where acc_book_type = ?1 and acc_book_code = ?2 and code_type = '31' and asset_type = ?3 ",nativeQuery = true)
    List<IntangibleAccAssetCodeType>  queryIntangibleAccAssetCodeTypeByAccBookTypeAndAccBookCodeAndCodeTypeAndassetType(String accBookType,String accBookCode,String assetType);

    //判断当前类别编码是否存在
    @Query(value = "select * from IntangibleAccAssetCodeType where acc_book_code= ?1 and asset_type = ?2 ",nativeQuery = true)
    List<?> queryIntangibleAccAssetCodeTypeByAccBookCodeAndAssetType(String accBookCode,String assetType);




    //通过无形资产编码类别名称查询 使用年限
    @Query(value = "select * from IntangibleAccAssetCodeType where  asset_type=?1 and  acc_book_type=?2 and acc_book_code=?3 ", nativeQuery = true)
    List<IntangibleAccAssetCodeType> queryByAssetType(String assetType,String accBookType, String accBookCode);

}
