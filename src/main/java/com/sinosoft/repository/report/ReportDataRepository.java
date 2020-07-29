package com.sinosoft.repository.report;

import com.sinosoft.domain.Report.ReportData;
import com.sinosoft.domain.Report.ReportDataId;
import com.sinosoft.repository.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Repository
public interface ReportDataRepository extends  BaseRepository<ReportData, ReportDataId> {

    @Query(value = "SELECT * FROM reportdata r where r.report_code=?1 and r.unit=?2 and r.version=?3 and r.acc_book_code like %?4% and r.year_month_date=?5 and r.center_code = ?6 ORDER BY r.number  ", nativeQuery = true)
    List<ReportData> queryReportData(String reportCode, String unit, String version, String accBookType, String yearMonthDate,String centerCode);
   @Query(value = " SELECT c.code_name AS 'name' FROM codemanage c WHERE c.code_type = 'SDBBreportType' AND c.code_code = ?1 ",nativeQuery = true)
    String queryTableName(String reportCode);
    @Query(value = "select a1.account as account,a1.bank as bank,a1.subjectcodeall as subjectcodeall ,a1.money as money,a1.subjectcodemon as subjectcodemon,a1.direction as direction,a2.account1 \n" +
            "            as account1,a2.bank as bank1,a2.subjectcodeall as subjectcodeall1,a2.money as money1,a2.subjectcodemon as subjectcodemon1,a2.direction1 as direction1  from \n" +
            "(select s2.account as account,s1.subject_name as bank,CONCAT(s1.all_subject,s1.subject_code) as subjectcodeall,s2.subject_name as money,CONCAT(s2.all_subject,s2.subject_code) as subjectcodemon,s1.direction as direction from subjectinfo s1,subjectinfo s2 where left(s1.all_subject,4)='1002'  and s1.level=3    and CONCAT(s2.all_subject,s2.subject_code,'/')=s1.all_subject  and s2.account=?1   and s1.account=s2.account ORDER BY s1.subject_code,s2.subject_code\n" +
            "                        ) as a1 ,\n" +
            "(select s2.account as account1, s1.subject_name as bank,CONCAT(s1.all_subject,s1.subject_code) as subjectcodeall,s2.subject_name as money,CONCAT(s2.all_subject,s2.subject_code) as subjectcodemon ,s1.direction as direction1 from subjectinfo s1,subjectinfo s2 where left(s1.all_subject,4)='1002'  and s1.level=3    and CONCAT(s2.all_subject,s2.subject_code,'/')=s1.all_subject  and s2.account=?2   and s1.account=s2.account ORDER BY s1.subject_code,s2.subject_code\n" +
            "                       ) a2 where(a1.bank=a2.bank and a1.money=a2.money)", nativeQuery = true)
    List<Map<String,String>> getsub(String accBookCode1,String accBookCode2);

    @Query(value = " select code_name from codemanage where code_type='JJreportType1' and code_code='5'\n",nativeQuery = true)
    String getCodeName();
    //得到当前明细账余额表 期末余额
    @Query(value = "select ifnull(sum(balance_dest),0.00) from AccDetailBalance where acc_book_code=?1 and year_month_date=?2 and f01=?3 and center_code = ?4", nativeQuery = true)
    BigDecimal getAccdetail(String accBookcode, String yearMonthData, String f01, String centerCode);
    //得到当前明细账余额表历史表 期末余额
    @Query(value = "select ifnull(sum(balance_dest),0.00) from accdetailbalancehis where acc_book_code=?1 and year_month_date=?2 and f01=?3 and center_code = ?4", nativeQuery = true)
    BigDecimal getAccdetailhis(String accBookcode, String yearMonthData, String f01, String centerCode);

    @Query(value = " SELECT c.code_code AS 'reportCode' FROM codemanage c WHERE c.code_type = 'SDBBreportType' AND c.temp LIKE %?1% ORDER BY c.code_code ASC ",nativeQuery = true)
    List<String> getReportCode(String accBookCode);

 @Query(value = " SELECT c.code_code AS 'reportCode' FROM codemanage c WHERE c.code_type = 'SDBBreportType' AND c.temp LIKE %?1% and c.order_by =?2 ORDER BY c.code_code ASC ",nativeQuery = true)
 List<String> getReportCode(String accBookCode,String orderBy);



}
