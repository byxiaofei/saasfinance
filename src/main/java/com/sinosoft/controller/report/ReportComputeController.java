package com.sinosoft.controller.report;

import com.sinosoft.common.InvokeResult;
import com.sinosoft.common.SysLog;
import com.sinosoft.dto.report.ReportDataDTO;
import com.sinosoft.service.report.ReportComputeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;


@Controller
@RequestMapping("/reportcompute")
public class ReportComputeController {
	@Resource
	private ReportComputeService reportComputeService;

	private Logger logger = LoggerFactory.getLogger(ReportComputeController.class);

	/**
	 * 报表计算，返回计算成功与否的字符串：SUCCESS-计算成功，FAILURE-计算失败，NOTEXIST-无计算公式配置信息
	 * @param dto 参数包含账套编码、会计期间、报表编码（两个字段）、报表版本、单位，其中账套编码不设置则为当前登录账套编码，版本号默认为1
	 * @return
	 */
	@RequestMapping("/compute")
	@ResponseBody
	@SysLog(value = "报表生成（单表）")
	public InvokeResult compute(ReportDataDTO dto) {
		try {
			String result = reportComputeService.compute(dto);
			if (result!=null&&!"".equals(result)) {
				if ("FAILURE".equals(result)) {
					return InvokeResult.failure("计算失败！");
				} else if ("NOTEXIST".equals(result)) {
					return InvokeResult.failure("无计算公式配置信息！");
				}
			}
			return InvokeResult.success();
		} catch (Exception e) {
			logger.error("报表生成异常", e);
			return InvokeResult.failure("报表生成异常");
		}
	}

	/**
	 * 四大报表生成
	 * @param dto
	 * @return
	 */
	@RequestMapping("/fourcomputefund")
	@ResponseBody
	@SysLog(value = "四大报表生成")
	public InvokeResult fourComputeFund(ReportDataDTO dto) {
		try {
			String result = reportComputeService.fourComputeFund(dto);
			if (result!=null&&"FAILURE".equals(result)) {
				return InvokeResult.failure("计算失败！");
			}
			return InvokeResult.success();
		} catch (Exception e) {
			logger.error("四大报表生成异常", e);
			return InvokeResult.failure("四大报表生成异常");
		}
	}
	/**
	 * 四大报表合并表生成
	 * @param dto
	 * @return
	 */
	@RequestMapping("/fourMergeComputeFund")
	@ResponseBody
	@SysLog(value = "四大报表合并表生成")
	public InvokeResult fourMergeComputeFund(ReportDataDTO dto) {
		try {
			String result = reportComputeService.fourMergeComputeFund(dto);
			if (result!=null&&"FAILURE".equals(result)) {
				return InvokeResult.failure("计算失败!请先生成所有账套下四大报表基本表");
			}
			return InvokeResult.success();
		} catch (Exception e) {
			logger.error("四大报表生成异常", e);
			return InvokeResult.failure("四大报表生成异常");
		}
	}

	@RequestMapping("/computeAll")
	@ResponseBody
	@SysLog(value = "报表生成")
	public InvokeResult computeAll(ReportDataDTO dto) {
		try {
			String result = reportComputeService.computeAll(dto);
			if (result!=null&&"FAILURE".equals(result)) {
				return InvokeResult.failure("计算失败！");
			}
			return InvokeResult.success();
		} catch (Exception e) {
			logger.error("报表生成异常", e);
			return InvokeResult.failure("报表生成异常");
		}
	}

	@RequestMapping("/check")
	@ResponseBody
	public InvokeResult checkCompute(ReportDataDTO dto, String type) {
		try {
			boolean result = reportComputeService.checkCompute(dto, type);
			if (!result) {
				return InvokeResult.failure("当前会计期间凭证数据未全部记账，不可生成");
			}
			return InvokeResult.success();
		} catch (Exception e) {
			logger.error("报表生成校验异常", e);
			return InvokeResult.failure("报表生成校验异常");
		}
	}

	/**
	 * 校验是否允许四大报表合并表生成
	 * @param dto
	 * @return
	 */
	@RequestMapping("/checkFour")
	@ResponseBody
	public InvokeResult checkFourReportCompute(ReportDataDTO dto) {
		try {
			String result = reportComputeService.checkFourReportCompute(dto);
			if (result!=null&&"FAILURE".equals(result)) {
				return InvokeResult.failure("四大报表所有基本表未全部生成，不允许生成合并表");
			}
			if (result!=null&&"FAILURE2".equals(result)) {
				return InvokeResult.failure("所属机构不是汇总机构，不允许生成合并表");
			}
			if (result!=null&&"SUCCESS".equals(result)) {
				return InvokeResult.success();
			}if(result!=null) { //直接返回具体提示： 机构代码+未生成基本报表
				return InvokeResult.failure(result);
			}
			return InvokeResult.success();
		} catch (Exception e) {
			logger.error("报表生成校验异常", e);
			return InvokeResult.failure("报表生成校验异常");
		}
	}


}
