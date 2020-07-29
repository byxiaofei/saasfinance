package com.sinosoft.repository.report;

import com.sinosoft.domain.Report.ReportCompute;
import com.sinosoft.domain.Report.ReportComputeId;
import com.sinosoft.domain.Report.ReportStyleInfoId;
import com.sinosoft.repository.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ReportComputeRepository extends  BaseRepository<ReportCompute, ReportComputeId> {

    @Query(value="select * from reportcompute r where r.version=?1 ", nativeQuery = true)
    List<ReportCompute> queryCalAll(String name);

    @Query(value="select r.version from reportcompute r  GROUP BY r.version order by r.version desc limit 1 ", nativeQuery = true)
    Integer queryLastVersion();

    @Modifying
    @Query(value="INSERT INTO reportcomputetemp SELECT * from reportcompute where version =?1", nativeQuery = true)
    void copyCaltoTemp(String version);
    @Modifying
    @Query(value="INSERT INTO reportcompute SELECT * from reportcomputetemp ", nativeQuery = true)
    void copyCal();
    @Modifying
    @Query(value="DELETE FROM reportcomputetemp ", nativeQuery = true)
    void deleteCalTemp();
    @Modifying
    @Query(value="update reportcomputetemp r set r.version=?1,r.create_time=?2,r.create_by=?3", nativeQuery = true)
    void updateCal(int version,String data,String userid);
}
