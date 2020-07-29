package com.sinosoft.repository.report;

import com.sinosoft.domain.Report.AgeAnalysisStyleInfo;
import com.sinosoft.domain.Report.AgeAnalysisStyleInfoId;
import com.sinosoft.domain.Report.ReportStyleInfo;
import com.sinosoft.domain.Report.ReportStyleInfoId;
import com.sinosoft.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgeAnalysisStyleInfoRepository extends  BaseRepository<AgeAnalysisStyleInfo, AgeAnalysisStyleInfoId> {

    @Query(value = "select * from ageanalysisstyleinfo a where a.version = ?1 and a.head_level = ?2 order by a.number", nativeQuery = true)
    List<AgeAnalysisStyleInfo> queryHead(Integer version, Integer headLevel);

    @Query(value = "select distinct a.head_level from ageanalysisstyleinfo a where a.version = ?1 order by a.head_level", nativeQuery = true)
    List<Integer> queryHeadLevel(Integer version);

    @Query(value = "select * from ageanalysisstyleinfo a where a.version = ?1 and a.d1 !='###' order by cast(substring(a.d1,5) as signed)", nativeQuery = true)
    List<AgeAnalysisStyleInfo> queryHeadField(Integer version);
}
