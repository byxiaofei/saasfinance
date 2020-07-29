package com.sinosoft.repository.intangibleassets;

import com.sinosoft.domain.fixedassets.AccAssetInfo;
import com.sinosoft.domain.fixedassets.AccAssetInfoId;
import com.sinosoft.domain.intangibleassets.IntangibleAccAssetInfo;
import com.sinosoft.domain.intangibleassets.IntangibleAccAssetInfoId;
import com.sinosoft.dto.intangibleassets.IntangibleAccAssetInfoDTO;
import com.sinosoft.repository.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author zhangst
 * @Description
 * @create
 */
@Repository
public interface IntangibleChangeManageRepository extends BaseRepository<IntangibleAccAssetInfo, IntangibleAccAssetInfoId> {
    /**
     * 无形资产卡片变动管理
     * @return
     */

    @Modifying
  /*  @Query(value="update accassetinfo set unit_code=?1 where center_code=?2 and branch_code=?3 and acc_book_type=?4 and acc_book_code=?5 and code_type=?6 and card_code =?7", nativeQuery = true)
    int departChange(String unitCode, String centerCode, String branchCode, String accBookType, String accBookCode, String codeType, String cardCode);
   */ @Query(value=" select o.special_name from specialinfo o where o.id=?1", nativeQuery = true)
    String getSpecialName(String unitCode);

    @Query(value="select * from IntangibleAccAssetInfo where center_code=?1 and branch_code=?2 and acc_book_type=?3 and acc_book_code=?4 and code_type=?5 and card_code=?6", nativeQuery = true)
    List<IntangibleAccAssetInfo> getAccAssetInfo(String centerCode, String branchCode, String accBookType, String accBookCode, String codeType, String cardCode);
    @Query(value=" select asset_type from IntangibleAccAssetInfo  where center_code=?1 and branch_code=?2 and acc_book_type=?3 and acc_book_code=?4 and code_type=?5 and card_code =?6", nativeQuery = true)
    String getAssetType(String centerCode, String branchCode, String accBookType, String accBookCode, String codeType, String cardCode);

   /* @Modifying
    @Query(value="update accassetinfo set organization=?1 where center_code=?2 and branch_code=?3 and acc_book_type=?4 and acc_book_code=?5 and code_type=?6 and card_code =?7", nativeQuery = true)
    int AddressChange(String organization, String centerCode, String branchCode, String accBookType, String accBookCode, String codeType, String cardCode);
 */  //页面固定变动固定编码类型名称下拉框  末级下编码类别
    @Query(value=" select asset_type as value ,asset_complex_name as text from IntangibleAccAssetCodeType  where acc_book_type=?1 and acc_book_code=?2 and code_type=?3 and end_flag='0'", nativeQuery = true)
    List<Map<String, Object>> AssetType(String accBookType, String accBookCode, String codeType);
  @Query(value=" select dep_years as depYears,dep_type as depType from IntangibleAccAssetCodeType  where acc_book_type=?1 and acc_book_code=?2 and code_type=?3 and asset_type=?4", nativeQuery = true)
  String DepreYear( String accBookType, String accBookCode, String codeType,String assetType);

    @Modifying
    @Query(value="update IntangibleAccAssetInfo set use_flag=?1 where center_code=?2 and branch_code=?3 and acc_book_type=?4 and acc_book_code=?5 and code_type=?6 and card_code =?7", nativeQuery = true)
    int useChange(String useFlag, String centerCode, String branchCode, String accBookType, String accBookCode, String codeType, String cardCode);
    @Query(value=" select max(change_code) from IntangibleAccAssetInfoChange where acc_book_type = ?1 and acc_book_code = ?2 and center_code = ?3", nativeQuery = true)
    String getChangeCode(String accBookType, String accBookCode,String centerCode);
    @Modifying
    @Query(value="update IntangibleAccAssetInfo set clear_year_month=?1,clear_code=?2,clearfee=?3,clear_reason=?4,clear_date=curdate(),clear_operator_branch=?5,clear_operator_code=?6,clear_flag=?7 where center_code=?8 and branch_code=?9 and acc_book_type=?10 and acc_book_code=?11 and code_type=?12 and card_code =?13", nativeQuery = true)
    int clearChange(String clearYearMonth, String clear_code,  BigDecimal clearfee, String clearReason, String clear_operator_branch, String clear_operator_code, String clearFlag, String centerCode, String branchCode, String accBookType, String accBookCode, String codeType, String cardCode);

    //获取凭证年月
    @Query(value=" select a.year_month_date as yearMonthDate,a.voucher_no as voucherNo from accvoucherno a where a.center_code=?1 and acc_book_type=?2 and acc_book_code=?3  and a.year_month_date=(select MAX(b.year_month_date) from accvoucherno b where a.center_code=b.center_code and a.acc_book_type=b.acc_book_type and a.acc_book_code=b.acc_book_code )", nativeQuery = true)
    List<Map<String,String>> getYearMonthDate(String centerCode, String accBookType, String accBookCode);

  @Query(value=" select * from accvoucherno a where a.center_code=?1 and acc_book_type=?2 and acc_book_code=?3  and a.year_month_date=?4", nativeQuery = true)
  List<Map<String,String>> getYearMonthDate1(String centerCode, String accBookType, String accBookCode,String yearMonthDate);

  //获取凭证分录号
    @Query(value="select max(suffix_no) from accsubvoucher where center_code=?1 and branch_code=?2 and acc_book_type=?3 and acc_book_code=?4 and voucher_no=?5", nativeQuery = true)
    Integer getSuffixNo(String centerCode, String branchCode, String accBookType, String accBookCode, String voucherNo);
    //类别表判断末级标志
    @Query(value=" select subject_name from subjectinfo where CONCAT(all_subject,subject_code,'/')=?1 and account=?2", nativeQuery = true)
    String getSubjectName(String allSubject, String account);
    //类别表判断末级标志
    @Query(value="select id from AccRemarkManage where center_code=?1 and acc_book_type=?2 and acc_book_code=?3 and item_code=?4", nativeQuery = true)
    Integer  getReamrk(String centerCode, String accBookType, String accBookCode, String allSubject);
    //查找应交增值税所对应的科目代码
    @Query(value="select subject_code as subjectCode,all_subject as allSubject from subjectinfo where account=?1 and subject_name=?2", nativeQuery = true)
    List<Map<String,String>>  getsubjectCode(String accBookCode, String subjectName);
    //根据科目代码查找科目专项
    @Query(value="select s.special_name from specialinfo s ,subjectinfo f where f.account=?1 and CONCAT(f.all_subject,f.subject_code)=?2 and f.special_id=s.id", nativeQuery = true)
    String   getSpecialCode(String accBookCode, String allsubject);
    //查找专项在哪个位置 s01 s02
    @Query(value="select segment_flag from accsegmentdefine where left(segment_col,2)=?1", nativeQuery = true)
    String  getSegmentFlag(String segmentCol);
    //添加凭证号
    @Modifying
    @Query(value="update IntangibleAccAssetInfo set voucher_no=?1 where center_code=?2 and branch_code=?3 and acc_book_type=?4 and acc_book_code=?5 and code_type=?6 and card_code =?7", nativeQuery = true)
    int updateVoucherNo(String useFlag, String centerCode, String branchCode, String accBookType, String accBookCode, String codeType, String cardCode);
    @Query(value="  select c.asset_simple_name from  IntangibleAccAssetCodeType c  where c.acc_book_type=?1 and c.acc_book_code=?2  and c.code_type=?3 and c.asset_type=?4", nativeQuery = true)
    String getAssetName(String accBookType, String accBookCode, String codeType,String assetType);


}
