package com.sinosoft.repository;

import com.sinosoft.domain.SubjectInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Repository
public interface SubjectRepository extends  BaseRepository<SubjectInfo, Long> {

    /*@Query(nativeQuery = true, value = "select u.* from subjectinfo u where u.subject_code = :code  and u.account = :account")
    List<SubjectInfo> findByCondition(@Param("code") String code,@Param("account") String account);*/

    @Query(nativeQuery = true, value = "select u.* from subjectinfo u where concat_ws('',u.all_subject,u.subject_code) = ?1  and u.account = ?2")
    List<SubjectInfo> findByCondition( String code, String account);

    @Query(nativeQuery = true, value = "select c.code_code as id,c.code_name as text from codemanage c where c.code_type = 'subjectType' order by id")
    List<Map<String, Object>> findSubjectType();

    @Query(nativeQuery = true, value = "select cast(m.id as char) as id,m.subject_name as text ,m.subject_code as mid from subjectinfo m where (m.super_subject is null or m.super_subject= '') and m.end_flag='1' and m.account = :account and m.subject_type = :subjecttype and m.subject_code != :subjectcode")
    List<Map<String, Object>> findSuperCode(@Param("account") String account,@Param("subjecttype") Integer subjecttype,@Param("subjectcode") String subjectcode);

    @Query(nativeQuery = true, value = "select cast(m.id as char) as id,m.subject_name as text ,m.subject_code as mid from subjectinfo m where m.super_subject = :code and m.end_flag='1' and m.account = :account and m.subject_code != :subjectcode")
    List<Map<String, Object>> findSuperCode(@Param("code") String code, @Param("account") String account,@Param("subjectcode") String subjectcode);

    @Query(nativeQuery = true,value="select count(s.id) from subjectinfo s where super_subject=?1")
    int findNextFlag(long id);

    @Query(nativeQuery = true,value=" select count(a.id) from  accremarkmanage a where a.item_code=?1 and a.acc_book_code=?2 and a.center_code = ?3")
    int selectRemark(String itemCode,String accBookCode, String centerCode);

    @Modifying
    @Query(nativeQuery = true,value=" update accremarkmanage  set item_code=?1 , item_name=?2 where item_code=?3 and acc_book_code=?4 and center_code = ?5")
    int updateAccrem(String newItemCode,String newItemName,String oldItemCode,String accBookCode, String centerCode);
    @Modifying
    @Query(nativeQuery = true,value=" update accremarkmanage  set item_code=?1 where item_code=?2 and acc_book_code=?3 and center_code = ?4")
    int updateAccrem(String newItemCode,String oldItemCode,String accBookCode, String centerCode);
    @Modifying
    @Query(nativeQuery = true,value=" delete from  accremarkmanage where item_code=?1 and acc_book_code=?2 and center_code = ?3")
    void delAccrem(String oldItemCode,String accBookCode, String centerCode);

    //科目 判断是否存在凭证
    @Query(nativeQuery = true,value="select count(center_code) from accsubvoucher  where acc_book_code=?1 and direction_idx=?2 and center_code = ?3")
    int findUSeAccsub(String accBookCode,String directionIdx, String centerCode);
    @Query(nativeQuery = true,value="select count(center_code) from accsubvoucherhis  where acc_book_code=?1 and direction_idx=?2 and center_code = ?3")
    int findUSeAccsubHis(String accBookCode,String directionIdx, String centerCode);

    /*@Query(nativeQuery = true, value = "select s.id as id, s.subject_code as subjectCode,s.subject_name as subjectName,s.subject_type as subjectType, " +
            "s.all_subject as allSubject,s.special_id as specialId,s.super_subject as superSubject,s.direction as direction," +
            "s.end_flag as endFlag,s.level as level,s.useflag as useflag,s.temp as temp,s.create_oper as createOper," +
            "s.create_time as createTime,s.update_oper as updateOper,s.update_time as updateTime," +
            "(select c.code_name from codemanage c where c.code_code=s.subject_type and c.code_type='subjectType') as subjectTypeName," +
            "(select c.code_name from codemanage c where c.code_code=s.direction and c.code_type='balanceDirection') as directionName," +
            "(select c.code_name from codemanage c where c.code_code=s.end_flag and c.code_type='endflag') as endFlagName," +
            "(select c.code_name from codemanage c where c.code_code=s.useflag and c.code_type='useflag') as useflagName," +
            "(select u.user_name from userinfo u where u.id=s.create_oper) as createOperName," +
            "(select u.user_name from userinfo u where u.id=s.update_oper) as updateOperName " +
            "from subjectinfo s where 1=1 " +
            "ORDER BY concat_ws(\"\",s.all_subject,s.subject_code)  ")
    List<Map<String, String>> findALLSubject();*/

    @Query(nativeQuery = true, value = "select s.id as id, s.subject_code as subjectCode,s.subject_name as subjectName,s.subject_type as subjectType, " +
            "s.all_subject as allSubject,s.special_id as specialId,s.super_subject as superSubject,s.direction as direction," +
            "s.end_flag as endFlag,s.level as level,s.useflag as useflag,s.temp as temp,s.create_oper as createOper," +
            "s.create_time as createTime,s.update_oper as updateOper,s.update_time as updateTime," +
            "(select c.code_name from codemanage c where c.code_code=s.subject_type and c.code_type='subjectType') as subjectTypeName," +
            "(select c.code_name from codemanage c where c.code_code=s.direction and c.code_type='balanceDirection') as directionName," +
            "(select c.code_name from codemanage c where c.code_code=s.end_flag and c.code_type='endflag') as endFlagName," +
            "(select c.code_name from codemanage c where c.code_code=s.useflag and c.code_type='useflag') as useflagName," +
            "(select u.user_name from userinfo u where u.id=s.create_oper) as createOperName," +
            "(select u.user_name from userinfo u where u.id=s.update_oper) as updateOperName " +
            "from subjectinfo s where 1=1 " +
            "and s.account = ?1" +
            "ORDER BY concat_ws(\"\",s.all_subject,s.subject_code)  ")
    List<Map<String, String>> findALLSubject(String account);

    /**
     * 利用自定义sql进行分页查询
     * @param pageable
     * @return
     */
    @Query(nativeQuery = true, countQuery = "SELECT count(*) FROM subjectinfo ",value = "select s.id as id, s.subject_code as subjectCode,s.subject_name as subjectName,s.subject_type as subjectType, " +
            "s.all_subject as allSubject,s.special_id as specialId,s.super_subject as superSubject,s.direction as direction," +
            "s.end_flag as endFlag,s.level as level,s.useflag as useflag,s.temp as temp,s.create_oper as createOper," +
            "s.create_time as createTime,s.update_oper as updateOper,s.update_time as updateTime," +
            "(select c.code_name from codemanage c where c.code_code=s.subject_type and c.code_type='subjectType') as subjectTypeName," +
            "(select c.code_name from codemanage c where c.code_code=s.direction and c.code_type='balanceDirection') as directionName," +
            "(select c.code_name from codemanage c where c.code_code=s.end_flag and c.code_type='endflag') as endFlagName," +
            "(select c.code_name from codemanage c where c.code_code=s.useflag and c.code_type='useflag') as useflagName," +
            "(select u.user_name from userinfo u where u.id=s.create_oper) as createOperName," +
            "(select u.user_name from userinfo u where u.id=s.update_oper) as updateOperName " +
            "from subjectinfo s where 1=1 " +
            "ORDER BY concat_ws(\"\",s.all_subject,s.subject_code)  ")
    Page<Map<String, String>> findALLSubject111(Pageable pageable);

    @Query(nativeQuery = true, value = "select s.id as value,s.special_name as text from specialinfo s where (s.super_special is null or s.super_special ='') and s.account = ?1 order by s.id")
    List<Map<String, String>> getspecialList(String account);

    /**
     * 根据科目代码、账套编码获取科目余额方向名称
     * @param subjectCode
     * @param accountCode
     * @return
     */
    @Query(value = "select c.codeName as direction from SubjectInfo s left join CodeSelect c on c.id.codeType = 'balanceDirection' and c.id.codeCode = s.direction where CONCAT_WS('',s.allSubject,s.subjectCode) = ?1 and s.account = ?2")
    List<Map<String, String>> findDirectionBySubjectCodeAndAccount(String subjectCode, String accountCode);
    /**
     * 通过账套查询科目信息
     * @param account
     * @return
     */
    @Query(value = " SELECT * FROM subjectinfo WHERE account = ?1 ",nativeQuery = true)
    List<SubjectInfo> findSubjectByAccount(String account);

    @Query(value = " SELECT account_code AS value,account_name AS text FROM accountinfo ",nativeQuery = true)
    List<Map<String,String>> getAccount();
    /**
     * 根据纯数字编码确定科目全编码
     * @param accountCode
     * @param numberCode
     * @return
     */
    @Query(value = "select concat(s.all_subject,s.subject_code) subjectCode from subjectinfo s where 1=1 and s.account = ?1 and replace(concat(s.all_subject,s.subject_code),'/','') = ?2", nativeQuery = true)
    String findSubjectCodeByNumCode(String accountCode, String numberCode);
    /**
     * 根据纯数字编码模糊确定科目全编码
     * @param accountCode
     * @param numberCode
     * @return
     */
    @Query(value = "select concat(s.all_subject,s.subject_code) subjectCode from subjectinfo s where 1=1 and s.account = ?1 and replace(concat(s.all_subject,s.subject_code),'/','') like ?2% order by subjectCode limit 1", nativeQuery = true)
    String findSubjectCodeByLikeNumCode(String accountCode, String numberCode);


    /**
     *
     * 功能描述:    根据科目代码和账户编码查询科目信息
     *
     */
    @Query(value = "SELECT * FROM subjectinfo where account = ?1  and CONCAT(all_subject,subject_code,'/') = ?2 ",nativeQuery = true)
    List<SubjectInfo> querySubjectInfoByAccountAndAllsubject(String account,String itemcodesArr);
}
