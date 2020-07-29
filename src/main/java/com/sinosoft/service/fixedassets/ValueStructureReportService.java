package com.sinosoft.service.fixedassets;

import java.util.List;

/**
 * @author jiangyc
 * @Description
 * @create 2019-04-22 20:41
 */
public interface ValueStructureReportService {

    List<?> qryReportInfo(String yearMonthDate, String startLevel, String endLevel);

    List<?> getYearMonthDate();

    List<?> getStartLevel();

    List<?> geteEndLevel();
}
