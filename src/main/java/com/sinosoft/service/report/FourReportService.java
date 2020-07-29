package com.sinosoft.service.report;


import com.sinosoft.domain.Report.ReportStyleInfo;
import com.sinosoft.dto.report.ReportDataDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface FourReportService {

	List<ReportStyleInfo> findReportHead(ReportDataDTO dto);
	List<ReportStyleInfo> findReportHeadLevel(ReportDataDTO dto);

	List<?> qryReportData(ReportDataDTO dto);
	/**
	 * 将符合条件的数据导出至Excel中
	 * @param request
	 * @param response
	 * @param queryConditions 封装导出数据限制条件
	 */
	public void exportByCondition(HttpServletRequest request, HttpServletResponse response,
                                  String queryConditions);
	public String hasDatas(HttpServletRequest request, HttpServletResponse response, ReportDataDTO dto,String iSMerge);
	void download(HttpServletRequest request, HttpServletResponse response, List<ReportDataDTO> dtos,String JJreportName,String iSMerge);
}
