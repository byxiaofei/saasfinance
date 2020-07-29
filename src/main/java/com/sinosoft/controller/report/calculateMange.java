package com.sinosoft.controller.report;


import com.sinosoft.common.InvokeResult;
import com.sinosoft.common.SysLog;
import com.sinosoft.domain.Report.ReportStyleInfo;
import com.sinosoft.dto.report.ReportDataDTO;
import com.sinosoft.service.report.ReportService;
import com.sinosoft.util.AjaxJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/calculateManage")
public class calculateMange {
	@Resource
	private ReportService reportService;

	private Logger logger = LoggerFactory.getLogger(calculateMange.class);

	@RequestMapping("/")
	public String page(){
		return "report/calculateManage";
	}

	@RequestMapping("/getZDYRepotHead")
	@ResponseBody
	public AjaxJson getRepotHead(ReportDataDTO dto) {
		AjaxJson j = new AjaxJson();
		List<ReportStyleInfo> level=reportService.findZDYReportHeadLevel(dto);

		StringBuilder jsonBuilder = new StringBuilder();
		jsonBuilder.append("{\"title\":[");
		for(int i=0;i<level.size();i++){
			dto.setLevel(level.get(i).getId().getHeadLevel());
			List<ReportStyleInfo> listEntity=reportService.findZDYReportHead(dto);
			//这是循环获取列名称
            //if(level.size()!=1){jsonBuilder.append("[");}
			if(level.size()!=0){jsonBuilder.append("[");}
			for (int n = 0; n < listEntity.size(); n++){
				jsonBuilder.append("{");
				jsonBuilder.append("\"field");
				jsonBuilder.append("\":\"");
				jsonBuilder.append(listEntity.get(n).getD1());//设置field
				jsonBuilder.append("\",");
				jsonBuilder.append("\"title");
				jsonBuilder.append("\":\"");
				jsonBuilder.append(listEntity.get(n).getD2());//设置title
				jsonBuilder.append("\",");
				jsonBuilder.append("\"width");
				jsonBuilder.append("\":\"");
				jsonBuilder.append(listEntity.get(n).getD3());//设置宽度
				jsonBuilder.append("\",");
				if(listEntity.get(n).getD7()!=null&&listEntity.get(n).getD7()!=""&&!listEntity.get(n).getD7().equals("0")){

					jsonBuilder.append("\"colspan");
					jsonBuilder.append("\":");
					jsonBuilder.append(Integer.valueOf(listEntity.get(n).getD7()));//设置跨列数
					jsonBuilder.append(",");
				}
				if(listEntity.get(n).getD6()!=null&&listEntity.get(n).getD6()!=""&&!listEntity.get(n).getD6().equals("0")){
					jsonBuilder.append("\"rowspan");
					jsonBuilder.append("\":");
					jsonBuilder.append(Integer.valueOf(listEntity.get(n).getD6()));//设置跨行数
					jsonBuilder.append(",");
				}

				/*if(!listEntity.get(n).getD1().equals("###")){
					jsonBuilder.append("\"formatter");
					jsonBuilder.append("\":\"");
					jsonBuilder.append("formatterResult");//设置formatter方法
					jsonBuilder.append("\",");
				}*/
				jsonBuilder.append("\"halign");
				jsonBuilder.append("\":\"");
				jsonBuilder.append(listEntity.get(n).getD4());//设置数据位置
				jsonBuilder.append("\",");
				jsonBuilder.append("\"align");
				jsonBuilder.append("\":\"");
				jsonBuilder.append(listEntity.get(n).getD5());//设置数据位置
				jsonBuilder.append("\"");
				jsonBuilder.append("},");
			}
			jsonBuilder.deleteCharAt(jsonBuilder.length() - 1);
			if(level.size()!=1){jsonBuilder.append("],");}
			if(level.size()==1){jsonBuilder.append("]");}
		}
		String mess="";
		if(level.size()!=1){
			mess=jsonBuilder.toString().substring(0,jsonBuilder.length() - 1);
			mess+="]}";
		}else{
			mess=jsonBuilder.toString();
			mess+="]}";
		}
		System.out.println("表头拼接信息："+mess);
		j.setSuccess(true);
		j.setObj(mess);
		j.setMsg("success");
		return j;
	}

	@RequestMapping("/ZDYlist")
	@ResponseBody
	public List<?> qryByConditions(ReportDataDTO dto){
		return reportService.qryZDYReportData(dto);
	}

	@RequestMapping("/saveZDYCal")
	@ResponseBody
	@SysLog(value = "报表自定义公式配置")
	public InvokeResult saveZDYCal(ReportDataDTO dto){
		try {
			return reportService.saveZDYCal(dto);
		} catch (Exception e) {
			logger.error("报表自定义公式配置保存异常", e);
			return InvokeResult.failure("报表自定义公式配置保存异常");
		}
	}

	@RequestMapping("/checkFormula")
	@ResponseBody
	@SysLog(value = "报表自定义公式配置")
	public InvokeResult checkFormula(ReportDataDTO dto){
		try {
			return reportService.checkFormula(dto);
		} catch (Exception e) {
			logger.error("报表自定义公式配置校验异常", e);
			return InvokeResult.failure("报表自定义公式配置校验异常");
		}
	}

	@RequestMapping("/createNewZDY")
	@ResponseBody
	@SysLog(value = "生成新自定义报表版本")
	public InvokeResult createNewZDY(ReportDataDTO dto){
		return reportService.createNewZDY(dto);
	}

}
