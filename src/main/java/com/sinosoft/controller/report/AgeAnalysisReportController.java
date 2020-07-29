package com.sinosoft.controller.report;

import com.sinosoft.common.InvokeResult;
import com.sinosoft.common.SysLog;
import com.sinosoft.domain.Report.AgeAnalysisData;
import com.sinosoft.service.report.AgeAnalysisReportService;
import com.sinosoft.util.AjaxJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/ageAnalysisReport")
public class AgeAnalysisReportController {

	@Resource
	private AgeAnalysisReportService ageAnalysisReportService;

	private Logger logger = LoggerFactory.getLogger(AgeAnalysisReportController.class);

	@RequestMapping("/")
	public String page(){
		return "report/ageAnalysisReport";
	}

	/**
	 * 查询表头信息
	 * @param version 版本号
	 * @return
	 */
	@RequestMapping("/getRepotHead")
	@ResponseBody
	public AjaxJson getRepotHead(Integer version){
		return ageAnalysisReportService.getReportHead(version);
	}

	/**
	 * 查询账龄数据
	 * @param ageAnalysisType 账龄类型
	 * @param computeDate 计算日期
	 * @param unit 单位
	 * @return
	 */
	@RequestMapping("/list")
	@ResponseBody
	public List<?> qryReportData(String ageAnalysisType, String computeDate, String unit){
		Integer version = 1;

		return ageAnalysisReportService.qryReportData(ageAnalysisType, computeDate, unit, version);
	}

	/**
	 * 账龄计算
	 * @param ageAnalysisType 账龄类型
	 * @param computeDate 计算日期
	 * @return
	 */
	@RequestMapping("/compute")
	@ResponseBody
	@SysLog(value = "账龄计算生成报表数据")
	public InvokeResult ageAnalysisCompute(String ageAnalysisType, String computeDate){
		Integer version = 1;

		try {
			String result = ageAnalysisReportService.ageAnalysisCompute(ageAnalysisType, computeDate, version);
			if (result!=null && !"".equals(result)) {
				return InvokeResult.failure(result);
			}
			return InvokeResult.success();
		} catch (Exception e) {
			logger.error("账龄计算异常", e);
			return InvokeResult.failure("账龄计算异常！");
		}
	}

	/**
	 * 检查是否已存在数据
	 * @param ageAnalysisType 账龄类型
	 * @param computeDate 计算日期
	 * @return
	 */
	@RequestMapping("/checkReport")
	@ResponseBody
	public InvokeResult checkReport(String ageAnalysisType, String computeDate){
		Integer version = 1;

		try {
			List<AgeAnalysisData> ageAnalysisDataList = ageAnalysisReportService.checkReport(ageAnalysisType, computeDate, version);
			if (ageAnalysisDataList!=null&&ageAnalysisDataList.size()>0) {
				return  InvokeResult.success();
			} else {
				return  InvokeResult.failure("无符合当前条件的数据，请先进行账龄计算！");
			}
		} catch (Exception e) {
			logger.error("账龄数据校验异常", e);
			return InvokeResult.failure("账龄数据校验异常！");
		}
	}

	@RequestMapping("/download")
	@ResponseBody
	public void exportDownload(HttpServletRequest request, HttpServletResponse response, String ageAnalysisType, String computeDate, String unit){
		Integer version = 1;
		try {
			ageAnalysisReportService.dynamicExportDownload(request, response, ageAnalysisType, computeDate, unit, version);
		} catch (Exception e) {
			logger.error("账龄数据下载异常", e);
			throw new RuntimeException("账龄数据下载失败！请联系管理员");
		}
	}
}
