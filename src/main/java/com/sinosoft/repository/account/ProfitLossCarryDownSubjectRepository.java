package com.sinosoft.repository.account;

import com.sinosoft.domain.account.ProfitLossCarryDownSubject;
import com.sinosoft.domain.account.ProfitLossCarryDownSubjectId;
import com.sinosoft.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ProfitLossCarryDownSubjectRepository extends  BaseRepository<ProfitLossCarryDownSubject, ProfitLossCarryDownSubjectId> {

    /**
     * 查询所有权益类末级科目（本年利润科目）
     * @return
     */
    @Query(value="SELECT CONCAT_WS('',s.all_subject,s.subject_code,'/') AS value,CONCAT_WS('',s.all_subject,s.subject_code,' ',s.subject_name) AS text from subjectinfo s where s.subject_type = '3' and s.end_flag = '0' and s.account = ?1 order by value", nativeQuery = true)
    //@Query(value="SELECT CONCAT_WS('',s.all_subject,s.subject_code,'/') AS value,s.subject_name AS text from subjectinfo s where s.subject_type = '3' and s.end_flag = '0' and s.account = ?1 order by value", nativeQuery = true)
    List<Map<String, Object>> findRightsInterestsCode(String account);
}
