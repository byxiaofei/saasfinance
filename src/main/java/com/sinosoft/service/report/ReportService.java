package com.sinosoft.service.report;


import com.sinosoft.common.InvokeResult;
import com.sinosoft.domain.Report.ReportStyleInfo;
import com.sinosoft.dto.report.ReportDataDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;

public interface ReportService {
	List<ReportStyleInfo> findReportHead(ReportDataDTO dto);
	List<ReportStyleInfo> findReportHeadLevel(ReportDataDTO dto);

	List<?> qryReportData(ReportDataDTO dto);

	/**
	 * 检查是否存在复核当前条件的报表数据
	 * @param dto 参数包含会计期间、报表编码、单位、版本号、账套编码，其中版本号默认为1，账套默认为当前登录账套（如果需要账套的限定，则应设置needAccBookCode字段值为Y）
	 * @return
	 */
	InvokeResult checkReportData(ReportDataDTO dto);
	List<ReportStyleInfo> findZDYReportHead(ReportDataDTO dto);
	List<ReportStyleInfo> findZDYReportHeadLevel(ReportDataDTO dto);

	List<?> qryZDYReportData(ReportDataDTO dto);

    InvokeResult saveZDYCal(ReportDataDTO dto);

	InvokeResult createNewZDY(ReportDataDTO dto);

	InvokeResult checkFormula(ReportDataDTO dto);
}
