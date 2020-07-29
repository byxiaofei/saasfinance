package com.sinosoft.repository;

import com.sinosoft.domain.SubjectInfo;
import com.sinosoft.domain.account.AccMainVoucher;
import com.sinosoft.domain.account.AccMainVoucherId;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface VoucherRepository extends  BaseRepository<AccMainVoucher, AccMainVoucherId> {

    /**
     * 查询标注树信息（根父级）
     * @param centerCode
     * @param accBookType
     * @param accBookCode
     * @return
     */
    @Query(value = "select a.tag_code as id,a.tag_name as text,a.end_flag as endFlag from acctagmanage a where a.center_code = ?1 and a.acc_book_type = ?2 and a.acc_book_code = ?3 and (a.upper_tag is null or a.upper_tag = '') and a.flag = '1'", nativeQuery = true)
    List<Map<String,Object>> findSuperTag(String centerCode, String accBookType, String accBookCode);

    /**
     * 根据父级查询标注树信息
     * @param centerCode
     * @param accBookType
     * @param accBookCode
     * @param upperTag
     * @return
     */
    @Query(value = "select a.tag_code as id,a.tag_name as text,a.end_flag as endFlag from acctagmanage a where a.center_code = ?1 and a.acc_book_type = ?2 and a.acc_book_code = ?3 and a.upper_tag = ?4 and a.flag = '1'", nativeQuery = true)
    List<Map<String,Object>> findTagByUpperTag(String centerCode, String accBookType, String accBookCode, String upperTag);

    /**
     * 根据标注编码查询tagName、endFlag、flag
     * @param centerCode
     * @param accBookType
     * @param accBookCode
     * @param tagCode
     * @return
     */
    @Query(value = "select a.tag_name as tagName,a.end_flag as endFlag,a.flag as flag from acctagmanage a where a.center_code = ?1 and a.acc_book_type = ?2 and a.acc_book_code = ?3 and a.tag_code = ?4", nativeQuery = true)
    List<Map<String,Object>> findTagByTagCode(String centerCode, String accBookType, String accBookCode, String tagCode);
    /**
     * 凭证最大号自加1
     * @param centerCode 核算单位
     * @param accBookType 账套类型
     * @param accBookCode 账套编码
     * @param yearMonthDate 年月（会计期间）
     * @param maxFlag 标志
     */
    @Modifying
    @Query(value = "UPDATE accvoucherno a SET a.voucher_no = a.voucher_no+1 WHERE a.center_code =?1 AND a.acc_book_type = ?2 AND a.acc_book_code = ?3 AND a.year_month_date = ?4 AND a.max_flag = ?5", nativeQuery = true)
    void voucherMaxNoAutoIncrementOne(String centerCode, String accBookType, String accBookCode, String yearMonthDate , String maxFlag);
    /**
     * 凭证最大号自减1
     * @param centerCode 核算单位
     * @param accBookType 账套类型
     * @param accBookCode 账套编码
     * @param yearMonthDate 年月（会计期间）
     * @param maxFlag 标志
     */
    @Modifying
    @Query(value = "UPDATE accvoucherno a SET a.voucher_no = a.voucher_no-1 WHERE a.center_code =?1 AND a.acc_book_type = ?2 AND a.acc_book_code = ?3 AND a.year_month_date = ?4 AND a.max_flag = ?5", nativeQuery = true)
    void voucherMaxNoDecrementOne(String centerCode, String accBookType, String accBookCode, String yearMonthDate , String maxFlag);

    /**
     * 获取凭证最大号
     * @param accBookCode
     * @param yearMonthDate
     * @return
     */
    @Query(value = "SELECT a.voucher_no AS voucherNo FROM accvoucherno a WHERE a.acc_book_code = ?1 AND a.year_month_date = ?2 AND a.center_code = ?3", nativeQuery = true)
    List<Map<String,String>> getVoucherNoMax(String accBookCode, String yearMonthDate, String centerCode);
    /**
     * 根据账套编码和会计期间
     * 来判断当前会计期间的状态是否为1或2
     * @param accBookCode
     * @param yearMonthDate
     * @return
     */
    @Query(value = "SELECT COUNT(a.year_month_date) from AccMonthTrace a where a.acc_month_stat in ('1','2') and a.acc_book_code=?1 and a.year_month_date=?2 and a.center_code = ?3", nativeQuery = true)
    int yearMonthDateStatus(String accBookCode, String yearMonthDate, String centerCode);

    @Query(value = "SELECT a.year_month_date AS text, a.year_month_date AS value FROM accmonthtrace a where a.center_code = ?1 and a.acc_book_type = ?2 and a.acc_book_code = ?3 order by value desc", nativeQuery = true)
    List<Map<String,String>> getBeginDateList(String centerCode,String accBookType ,String accBookCode);

    @Query(value = "SELECT * FROM AccSubVoucher a where a.center_code= ?1 and a.branch_code = ?2 and a.acc_book_type = ?3 and a.acc_book_code = ?4 and a.year_month_date = ?5 and a.voucher_no = ?6 order by a.suffix_no", nativeQuery = true)
    List<Map<String,String>> getSource(String centerCode, String branch, String accBookType, String accBookCode, String yearMonthDate, String voucherNo);

    /**
     * 校验当月凭证号在当前凭证号后的所有凭证是否均为“未复核”状态，如存在已复核的，则可查询出凭证号
     * @param accBookCode 当前账套编码
     * @param yearMonthDate 会计期间
     * @param voucherNo 当前凭证号
     * @return
     */
    @Query(value = "SELECT a.id.voucherNo AS voucherNo FROM AccMainVoucher a WHERE a.id.accBookCode = ?1 AND a.id.yearMonthDate = ?2 AND a.id.voucherNo > ?3 AND a.voucherFlag <> '1' AND a.id.centerCode = ?4")
    List<Map<String,Object>> checkAfterTheVoucherExistApprove(String accBookCode, String yearMonthDate, String voucherNo, String centerCode);

    /**
     * 根据全科目代码查询科目信息（父级科目id、科目名称）
     * @param account 账套编码
     * @param endFlag 末级标志 0:末级，1:非末级
     * @param useFlag 使用状态 1:使用，0:非使用
     * @param useFlag 余额方向 1-借方余额、2-贷方余额
     * @param subjectCode 科目全代码
     * @return
     */
    @Query(value = "SELECT s.superSubject AS superSubject,s.subjectName AS subjectName FROM SubjectInfo s WHERE s.account = ?1 AND s.endFlag = ?2 AND s.useflag = ?3 AND CONCAT_WS('',s.allSubject,s.subjectCode) = ?4")
    List<Map<String,Object>> getSubjectNameBySubjectCode(String account, String endFlag, String useFlag, String subjectCode);

    @Query(nativeQuery = true, value = "select cast(m.id as char) as id,m.subject_name as text ,m.subject_code as mid from subjectinfo m where (m.super_subject is null or m.super_subject= '') and m.end_flag='1' and m.account = :account and m.subject_type = :subjecttype and m.subject_code != :subjectcode and m.direction= :direction")
    List<Map<String, Object>> findSuperCode(@Param("account") String account, @Param("subjecttype") Integer subjecttype, @Param("subjectcode") String subjectcode ,@Param("direction") String direction);
    @Query(nativeQuery = true, value = "select cast(m.id as char) as id,m.subject_name as text ,m.subject_code as mid from subjectinfo m where m.super_subject = :code and m.end_flag='1' and m.account = :account and m.subject_code != :subjectcode and m.direction= :direction")
    List<Map<String, Object>> findSuperCode1(@Param("code") String code, @Param("account") String account,@Param("subjectcode") String subjectcode,@Param("direction") String direction);

    /**
     * 通过专项编码查询专项简称
     * @param specialCode
     * @param accBookCode
     * @return
     */
    @Query(value = "SELECT s.special_name FROM specialinfo s WHERE s.account = ?1 AND s.special_code = ?2 ",nativeQuery = true)
    String getSpecialName(String accBookCode,String specialCode);

    /**
     * 根据科目全代码查询科目信息
     * @param accBookCode
     * @param subjectCodeAll
     * @return
     */
    @Query(value = "SELECT * FROM subjectinfo s WHERE s.account = ?1 AND CONCAT(s.all_subject,s.subject_code) = ?2",nativeQuery = true)
    List<Map<String, Object>> findSubjectBySubjectCodeAll(String accBookCode,String subjectCodeAll);

    /**
     * 查询指定会计期间内已复核的凭证主表信息
     * @param accBookCode
     * @param yearMonthDate
     * @return
     */
    @Query(value = "select * from accmainvoucher a where 1=1 and a.acc_book_code = ?1 and a.year_month_date = ?2 and a.voucher_flag in ('2','3') and a.center_code = ?3",nativeQuery = true)
    List<Map<String, Object>> findMainVoucherAlreadyApprove(String accBookCode, String yearMonthDate, String centerCode);

    /**
     * 查询指定会计期间内的凭证主表信息
     * @param accBookCode
     * @param yearMonthDate
     * @return
     */
    @Query(value = "select * from accmainvoucher a where 1=1 and a.acc_book_code = ?1 and a.year_month_date = ?2 and a.center_code = ?3",nativeQuery = true)
    List<Map<String, Object>> findMainVoucher(String accBookCode, String yearMonthDate, String centerCode);

    /**
     * 根据以集装箱编码获取专项段存储位置
     * @param segmentCol
     * @return
     */
    @Query(value = "select a.segment_flag as 'specialCode' from AccSegmentDefine a where a.segment_col = ?1",nativeQuery = true)
    String findSpecialSegmentSXX(String segmentCol);
}
