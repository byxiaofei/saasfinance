package com.sinosoft.repository.account;

import com.sinosoft.domain.AccTagManage;
import com.sinosoft.domain.account.AccRemarkManage;
import com.sinosoft.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface QueryTagJournalRespository extends BaseRepository<AccTagManage,Integer> {

    @Query(value = "SELECT a.tag_code as value,a.tag_name as text FROM acctagmanage a WHERE (a.upper_tag = '' OR a.upper_tag IS NULL) AND a.center_code = ?1  AND a.acc_book_type = ?2 AND  a.acc_book_code = ?3 ORDER BY a.tag_code", nativeQuery = true)
    List<Map<String, Object>> querySuperTagManage(String centerCode, String accBookType, String accBookCode);

    @Query(value = "SELECT a.tag_code as value,a.tag_name as text FROM acctagmanage a WHERE a.center_code = ?1  AND a.acc_book_type = ?2 AND  a.acc_book_code = ?3 AND a.upper_tag = ?4 ORDER BY a.tag_code", nativeQuery = true)
    List<Map<String, Object>> queryTagManageByUpperTag(String centerCode, String accBookType, String accBookCode, String upperTag);

    @Query(value = "SELECT a.tag_code as value,a.tag_name as text FROM acctagmanage a WHERE a.center_code = ?1  AND a.acc_book_type = ?2 AND  a.acc_book_code = ?3 AND a.tag_name LIKE %?4% ORDER BY a.tag_code", nativeQuery = true)
    List<Map<String, Object>> queryTagManageByTagName(String centerCode, String accBookType, String accBookCode, String upperTag);
}
