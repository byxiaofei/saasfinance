package com.sinosoft.service.report;

import com.sinosoft.common.InvokeResult;
import com.sinosoft.dto.report.ReportDataDTO;

import java.math.BigDecimal;
import java.util.Map;

public interface ReportComputeService {
	/**
	 * 当前会计期间是否允许生成报表校验
	 * @param dto
	 * @param type
	 * @return
	 */
	boolean checkCompute(ReportDataDTO dto, String type);
	/**
	 * 报表计算，返回计算成功与否的字符串：SUCCESS-计算成功，FAILURE-计算失败，NOTEXIST-无计算公式配置信息
	 * @param dto 参数包含账套编码、会计期间、报表编码（两个字段）、报表版本、单位，其中账套编码不设置则为当前登录账套编码，版本号默认为1
	 * @return
	 */
	String compute(ReportDataDTO dto);

	/**
	 * 四大报表生成
	 * @param dto
	 * @return
	 */
	String fourComputeFund(ReportDataDTO dto);

	/**
	 * 四大报表合并表生成前校验
	 * @param dto
	 * @return
	 */
	String  checkFourReportCompute(ReportDataDTO dto);

	/**
	 * 四大报表合并表生成
	 * @param dto
	 * @return
	 */
	String fourMergeComputeFund(ReportDataDTO dto);

	String computeAll(ReportDataDTO dto);
	//财产保险公司保险保障基金余额明细表 表格最下方 注明 获取期末余额
	Map<String,BigDecimal> getAccountData(String accBookCode,String yearMonthData);
	/**
	 * 报表自定义公式初步校验
	 * @param reportCode 报表编码
	 * @param formula 计算公式
	 * @param formulaK 计算参数
	 * @param formulaV 参数取数规则
	 * @return
	 */
	String checkFormula(String reportCode, String formula, String formulaK, String formulaV);
}
