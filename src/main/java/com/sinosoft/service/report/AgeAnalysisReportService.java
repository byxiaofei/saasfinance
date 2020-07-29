package com.sinosoft.service.report;

import com.sinosoft.domain.Report.AgeAnalysisData;
import com.sinosoft.util.AjaxJson;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface AgeAnalysisReportService {
	/**
	 * 查询表头信息
	 * @param version 版本号
	 * @return
	 */
	AjaxJson getReportHead(Integer version);

	/**
	 * 查询账龄数据
	 * @param ageAnalysisType 账龄类型
	 * @param computeDate 计算日期
	 * @param unit 单位
	 * @param version 版本号
	 * @return
	 */
	List<?> qryReportData(String ageAnalysisType, String computeDate, String unit, Integer version);

	/**
	 * 账龄计算
	 * @param ageAnalysisType 账龄类型
	 * @param computeDate 计算日期
	 * @param version 版本号
	 * @return
	 */
	String ageAnalysisCompute(String ageAnalysisType, String computeDate, Integer version);

	/**
	 * 检查是否已存在数据
	 * @param ageAnalysisType 账龄类型
	 * @param computeDate 计算日期
	 * @param version 版本号
	 * @return
	 */
	List<AgeAnalysisData> checkReport(String ageAnalysisType, String computeDate, Integer version);

	/**
	 * 账龄数据下载
	 * @param request
	 * @param response
	 * @param ageAnalysisType
	 * @param computeDate
	 * @param unit
	 * @param version
	 */
	void dynamicExportDownload(HttpServletRequest request, HttpServletResponse response, String ageAnalysisType, String computeDate, String unit, Integer version);
}
