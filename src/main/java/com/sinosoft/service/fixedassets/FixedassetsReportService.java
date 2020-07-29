package com.sinosoft.service.fixedassets;

import com.sinosoft.common.InvokeResult;

import java.util.List;

/**
 * @author jiangyc
 * @Description
 * @create 2019-04-19 18:24
 */
public interface FixedassetsReportService {

    List<?> qryReportInfo(String yearMonthDate, String startLevel, String endLevel);

    List<?> getYearMonthDate();

    List<?> getStartLevel();

    List<?> geteEndLevel();
}
