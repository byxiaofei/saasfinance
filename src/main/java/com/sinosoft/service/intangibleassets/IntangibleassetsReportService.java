package com.sinosoft.service.intangibleassets;

import java.util.List;

/**
 * @author jiangyc
 * @Description
 * @create 2019-04-22 19:08
 */
public interface IntangibleassetsReportService {

    List<?> qryReportInfo(String yearMonthDate, String startLevel, String endLevel);

    List<?> getYearMonthDate();

    List<?> getStartLevel();

    List<?> geteEndLevel();
}
