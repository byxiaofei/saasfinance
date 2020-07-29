package com.sinosoft.repository.fixedassets;

import com.sinosoft.domain.fixedassets.AccAssetInfo;
import com.sinosoft.domain.fixedassets.AccAssetInfoId;
import com.sinosoft.repository.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author jiangyc
 * @Description
 * @create 2019-03-29 15:13
 */
@Repository
public interface AccAssetInfoRepository extends BaseRepository<AccAssetInfo, AccAssetInfoId> {

    /**
     * 加载固定资产类别编码中维护的类别
     * @return
     */
    @Query(value="select s.asset_type as value, s.asset_complex_name as text from AccAssetCodeType s where acc_book_code=?1", nativeQuery = true)
    List<Map<String, Object>> qryAssetType(String accBookCode);

    /**
     * 加载专项中维护的BM类专项
     * @return
     */
    @Query(value="select s.id as value, s.special_namep as text from specialinfo s where s.special_code  like \"BM%\" and s.account = ?1 and s.endflag = '0' order by s.id asc", nativeQuery = true)
    List<Map<String, Object>> qryuUnitCode(String accBookCode);

    /**
     *
     * 功能描述:    加载专项中维护的BM类专项（只查询出使用状态下的值）
     *
     */
    @Query(value = "select s.id as value, s.special_namep as text from specialinfo s where s.special_code  like \"BM%\" and s.account = ?1   and s.useflag = '1' and s.endflag = '0' order by s.id asc", nativeQuery = true)
    List<Map<String,Object>> qryUnitCodeByUseFlagOne(String accBookCode);

    /**
     * 加载专项中维护的BM类专项
     * @return
     */
    @Query(value="select a.dep_years from accassetcodetype a where a.asset_complex_name = ?1 ", nativeQuery = true)
    List<?> getDepYears(String superCode1);

    /**
     * 查询accassetcodetype表中最大卡片编码
     * @return
     */
    @Query(value="select MAX(card_code) from AccAssetInfo where acc_book_code=?1 and center_code = ?2", nativeQuery = true)
    String qryMaxCardCode(String accBookCode,String centerCode);
    /**
     *删除固定资产卡片
     * @return
     */
    @Modifying
    @Query(value="delete from accAssetInfo where center_code = ?1 and branch_code = ?2 and acc_book_type = ?3 " +
            "and acc_book_code = ?4 and code_type = ?5 and card_code = ?6", nativeQuery = true)
    void delete(String centerCode, String branchCode, String accBookType, String accBookCode, String codeType, String cardCode);

    /**
     * 凭证号清空
     * @param voucherNo
     */
    @Modifying
    @Query(value="update accAssetInfo set voucher_no = null where voucher_no= ?1 and acc_book_type = ?2 and acc_book_code = ?3 and center_code = ?4 ", nativeQuery = true)
    void clearVoucherNo(String voucherNo,String accBookType, String accBookCode,String centerCode);
    /**
     * 获取预计残值率
     * @return
     */
    @Query(value="select net_surplus_rate from AccAssetCodeType  where acc_book_type = ?1 " +
            "and acc_book_code = ?2 and asset_type = ?3 and code_type=?4", nativeQuery = true)
    String getNetSurplusRate(String accBookType,String accBookCode,String assetType,String codeType);

    @Query(value="    select acc_book_code as accBookCode,code_type as codeType,asset_type as assetType,sum(asset_origin_value) AS assetOriginValue,sum(end_depre_money) AS endDepreMoney,sum(sum) AS sum,sum(input_tax) AS inputTax from AccAssetInfo a where a.voucher_no like CONCAT(?1,'%') and clear_flag != '1'  and acc_book_code = ?2  GROUP BY asset_type", nativeQuery = true)
    List<AccAssetInfo> getAssetinfo(String voucherno, String accBookCode);



    @Query(value = "select * from accassetinfo a where 1=1 and center_code = ?1 and branch_code = ?2  and acc_book_type = ?3 and acc_book_code = ?4 and code_type = '21' and card_code = ?5",nativeQuery = true)
    List<AccAssetInfo> queryAccAssetInfoByChooseMessage(String centerCode,String branchCode , String accBookType ,String accBookCode, String cardCode);



    @Query(value = "SELECT ac.asset_code AS 'value', ac.asset_code AS 'text' FROM accassetinfo ac  WHERE ac.center_code = ?1  AND ac.branch_code = ?2 AND ac.acc_book_type = ?3  and ac.acc_book_code = ?4 AND ac.asset_type = ?5 ORDER BY VALUE",nativeQuery = true)
    List<?> queryAccAssetInfoByByChooseMessageOrderByValue(String centerCode ,String branchCode,String accBookType ,String accBookCode,String assetType);

    //查询存在下级的科目id(限定1002科目，all_subject like '1002%')
    @Query(value = "select distinct s.super_subject as superSubject from subjectinfo s where 1=1 and s.super_subject != '' and s.super_subject is not null and s.all_subject like '1002%' and s.account = ?1",nativeQuery = true)
    List<?> queryAccAssetInfoBy(String account);

    //  通过四大类类型(subject_type字段) 找出对应4位编码(编订 ：m.subject_code = '1002')
    @Query(value = "select cast(m.id as char) as id,m.subject_name as text ,m.subject_code as mid,m.end_flag as endFlag from subjectinfo m  where (m.super_subject is null or m.super_subject= '') and m.subject_type = ?1 and m.account = ?2 and m.subject_code = '1002' order by concat(m.all_subject,m.subject_code) ",nativeQuery = true)
    List<?> queryAccAssetInfoBySubjectTypeAndAccount(String subjectType,String account);

    //复制操作下 获取资产编号
    @Query(value = "select MAX(asset_code) from accassetinfo where center_code=?1 and  asset_type = ?2 and acc_book_code = ?3 and acc_book_type = ?4 ",nativeQuery = true)
    List<?> queryAccAssetInfoMAXByChooseMessage(String centerCode,String assetType ,String accBookCode ,String accBookType);


    @Query(value = "select * from accassetinfo where center_code= ?1 and  acc_book_code= ?2 and card_code= ?3 ",nativeQuery = true)
    List<AccAssetInfo> queryAccAssetInfoByCenterCodeAndAccBookCodeAndCardCode(String centerCode,String accBookCode,String cardCode);

    //获取卡片来源
    @Query(value = "select code_name from codemanage where code_type='sourceFlag' and code_code= ?1 ",nativeQuery = true)
    List<?> queryCardInfoByCodeCode(String codeCode);

    //先从固定资产基本信息表(AccAssetInfo)中获取所有基本信息
    @Query(value = "select * from AccAssetInfo where center_code= ?1 and depre_flag = 'Y' and end_depre_amount < dep_years  and left(REPLACE(use_start_date,'-',''),6) <= ?2 and ( clear_flag = '0' OR ( clear_flag = '1' AND clear_year_month > ?3  )) and acc_book_type = ?4 and acc_book_code = ?5 ",nativeQuery = true)
    List<?> queryAccAssetInfoByCenterCodeAndDepreFlagAndEndDepreAmountAndAccBookTypeAndAccBookCode(String centerCode,String yearMonthDate,String clearYearMonth,String accBookType,String accBookCode);

    @Query(value = "select * from accassetinfo where center_code= ?1 and  acc_book_code= ?2 and code_type= ?3 and asset_code=?4",nativeQuery = true)
    List<AccAssetInfo> queryAccAssetInfoByCenterCodeAndAccBookCodeAndCodeTypeAndAssetCode(String centerCode,String accBookCode,String codeType,String assetCode);

    @Query(value = "select * from accassetinfo where center_code=?1 and  acc_book_code=?2 and asset_code=?3",nativeQuery = true)
    List<AccAssetInfo> queryAccAssetInfoByCenterCodeAndAcBookCodeAndAssetCode(String centerCode,String accBookCode,String assetCode);
}
