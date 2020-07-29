package com.sinosoft.repository.account;

import com.sinosoft.domain.account.AccMonthTrace;
import com.sinosoft.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface AccMonthRespository extends BaseRepository<AccMonthTrace,Integer> {
    /**
     * 根据指定参数查询未结转会计期间
     * @param accBookCode
     * @param yearMonthDate
     */
    @Query(value = "SELECT a.id.yearMonthDate,a.accMonthStat,a.id.accBookCode FROM AccMonthTrace a WHERE a.id.accBookCode = ?1 AND a.id.yearMonthDate = ?2 AND a.id.centerCode = ?3 AND a.accMonthStat IN ('1','2','4')")
    List<Map<String, Object>> queryAccMonthTraceNo(String accBookCode, String yearMonthDate, String centerCode);
    /**
     * 根据指定参数查询已结转会计期间
     * @param accBookCode
     * @param yearMonthDate
     */
    @Query(value = "SELECT a.id.yearMonthDate,a.accMonthStat,a.id.accBookCode FROM AccMonthTrace a WHERE a.id.accBookCode = ?1 AND a.id.yearMonthDate = ?2 AND a.id.centerCode = ?3 AND a.accMonthStat IN ('3','5')")
    List<Map<String, Object>> queryAccMonthTrace(String accBookCode, String yearMonthDate, String centerCode);
}
