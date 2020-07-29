package com.sinosoft.repository.report;

import com.sinosoft.domain.Report.ReportStyleInfo;
import com.sinosoft.domain.Report.ReportStyleInfoId;
import com.sinosoft.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends  BaseRepository<ReportStyleInfo, ReportStyleInfoId> {

    /**
     * 根据报表编号，账套编码，表头层级查询表头信息
     * @param str1
     * @param str2
     * @return
     */
    @Query(value = "select * from reportstyleinfo r where r.report_code=?1 and r.head_level=?2 ORDER BY r.number ", nativeQuery = true)
    List<ReportStyleInfo> queryHead(String str1,String str2);
    /**
     * 根据账套编码，版本号,报表编码查询表头信息
     * @param str1
     * @param str2
     * @return
     */
    @Query(value = "select * from reportstyleinfo r where r.report_code = ?1 and  r.head_level=?2 ORDER BY r.number ", nativeQuery = true)
    List<ReportStyleInfo> queryZDYHead(String str1,String str2);

    /**
     * 根据报表编号，查询表头层级有几层
     * @param str1
     * @return
     */
    @Query(value = "select * from reportstyleinfo r where r.report_code=?1 GROUP BY r.head_level ORDER BY r.head_level ", nativeQuery = true)
    List<ReportStyleInfo> queryHeadLevel(String str1);
    /**
     * 根据报表编号，账套编码查询表头层级有几层
     * @param str1
     * @return
     */
    @Query(value = "select * from reportstyleinfo r where r.report_code=?1  GROUP BY r.head_level ORDER BY r.head_level ", nativeQuery = true)
    List<ReportStyleInfo> queryZDYHeadLevel(String str1);

    /**
     * 根据报表编号，账套编码查询所有filed（d1）不为“...”的表头信息
     * 表头存为'...'的表示是个占位展示的没有对应的Field设置
     * @param str1
     * @return
     */
    @Query(value = "select * from reportstyleinfo r where r.report_code=?1 and  r.d1 !='###' ORDER BY cast(substring(r.d1,5) as signed) ", nativeQuery = true)
    List<ReportStyleInfo> queryHeadField(String str1);
    /**
     * 根据报表编号，账套编码查询所有filed（d1）不为“...”的表头信息
     * 表头存为'...'的表示是个占位展示的没有对应的Field设置
     * @param str1
     * @return
     */
    @Query(value = "select * from reportstyleinfo r where r.report_code=?1 and r.d1 !='###' ORDER BY cast(substring(r.d1,5) as signed) ", nativeQuery = true)
    List<ReportStyleInfo> queryHeadField2(String str1);


    @Query(value = "select c.code_name from codemanagecal c where c.code_type=?1 and c.code_code=?2 ", nativeQuery = true)
    String calculate(String type,String code);
}
