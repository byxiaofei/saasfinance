package com.sinosoft.repository.account;

import com.sinosoft.domain.SubjectInfo;
import com.sinosoft.domain.account.AccRemarkManage;
import com.sinosoft.domain.account.RemarkId;
import com.sinosoft.dto.account.AccRemarkManageDTO;
import com.sinosoft.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface AccRemarkManageRespository extends BaseRepository<AccRemarkManage,Integer> {

    @Query(value = "select * from accremarkmanage a where a.reamrk_code = ?1 and a.remark_name = ?2 and a.acc_book_code = ?3 and a.center_code = ?4" ,nativeQuery = true)
    List<AccRemarkManage> findAllByCodeAndName(String RemarkCode, String RemarkName, String accBookCode, String centerCode);

    /**
     * 通过账套类型、账套代码、摘要代码检查摘要是否已存在
     * @param AccBookType
     * @param AccBookCode
     * @param RemarkCode
     * @return
     */
    @Query(value = "select * from accremarkmanage a where a.acc_book_type = ?1 and a.acc_book_code = ?2 and a.remark_code = ?3 and a.center_code = ?4",nativeQuery = true)
    List<AccRemarkManage> checkExiste(String AccBookType, String AccBookCode,String RemarkCode, String centercode);

    /**
     * 修改摘要时，只能通过id查询，不能通过摘要编码、账套、核算单位联合查询
     * @param id
     * @return
     */
    @Query(value = "select * from accremarkmanage a where a.id = ?1",nativeQuery = true)
    AccRemarkManage qryAccRemarkManageById (long id);

    /**
     * RemarkCode下拉框
     * @return
     */
    @Query(value="select a.remark_code as value,a.remark_code as text from accremarkmanage a where a.center_code = ?1 and a.acc_book_code = ?2 and a.acc_book_type = ?3 ", nativeQuery = true)
    List<Map<String, Object>> findRemarkCode(String center,String accBookCode, String accBookType);

    /**
     * 根据科目编码查询科目名称
     * @param subjectCode
     * @return
     */
    @Query(value = "select s.subject_name as name ,s.super_subject as superSubject from subjectinfo s where CONCAT(s.all_subject,s.subject_code,\"/\") = ?1 and s.account = ?2",nativeQuery = true)
    List <Map<String,Object>> findSubjectName(String subjectCode, String account);

    @Query(value = "select s.subject_name as name ,s.super_subject as superSubject from subjectinfo s where s.id = ?1",nativeQuery = true)
    List <Map<String,Object>> findSubjectNameById(int id);

    /**
     * 查询一级科目编码与名称
     * @return
     */
    @Query(value = "SELECT s.id AS id,s.subject_code as code,s.subject_name as text FROM subjectinfo s WHERE s.account = ?1 AND (s.super_subject LIKE \"\" OR s.super_subject IS NULL) ORDER BY s.id", nativeQuery = true)
    List<Map<String, Object>> findSuperSubject(String account);

    /**
     * 查询非一级科目编码及名称
     * @param superMenu
     * @return
     */
    @Query(value = "select s.id as id ,s.special_code as text from specialinfo s where super_special=?1 order by s.id", nativeQuery = true)
    List<Map<String, Object>> findChildrenBySuperSpecial(Integer superMenu);
}
