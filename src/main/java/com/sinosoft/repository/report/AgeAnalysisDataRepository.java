package com.sinosoft.repository.report;

import com.sinosoft.domain.Report.AgeAnalysisData;
import com.sinosoft.domain.Report.AgeAnalysisDataId;
import com.sinosoft.domain.Report.ReportData;
import com.sinosoft.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface AgeAnalysisDataRepository extends  BaseRepository<AgeAnalysisData, AgeAnalysisDataId> {

    @Query(value = "select concat(s.all_subject,s.subject_code) as subjectCode,s.end_flag as endFlag from subjectinfo s where s.account = ?1 and s.temp = ?2 order by concat(s.all_subject,s.subject_code) asc", nativeQuery = true)
    List<Map<String, Object>> getSubjectCodeAndEndFlagByAgeAnalysisType(String accBookCode, String ageAnalysisType);

    @Query(value = "select s.subject_name as subjectName from subjectinfo s where s.account = ?1 and concat(s.all_subject,s.subject_code) = ?2", nativeQuery = true)
    String getSubjectNameBySubjectCode(String accBookCode, String subjectCode);
}
