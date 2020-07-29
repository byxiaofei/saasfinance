package com.sinosoft.controller.report;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sinosoft.common.DataGrid;
import com.sinosoft.common.InvokeResult;
import com.sinosoft.domain.Report.ReportStyleInfo;
import com.sinosoft.domain.UserInfo;
import com.sinosoft.dto.report.ReportDataDTO;
import com.sinosoft.service.UserInfoService;
import com.sinosoft.service.report.ReportService;
import com.sinosoft.util.AjaxJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/report")
public class ReportController {
	@Resource
	private ReportService reportService;
	@Value("${reportAccountB}")
	private String reportAccountB;

	private Logger logger = LoggerFactory.getLogger(ReportController.class);

	@RequestMapping("/getSDBBRepotHead")
	@ResponseBody
	public AjaxJson getSDBBRepotHead(ReportDataDTO dto){
		dto.setNeedAccBookCode("Y");
		return getRepotHead(dto);
	}
	private AjaxJson getRepotHead(ReportDataDTO dto) {
		AjaxJson j = new AjaxJson();
		List<ReportStyleInfo> level=reportService.findReportHeadLevel(dto);

		StringBuilder jsonBuilder = new StringBuilder();
		jsonBuilder.append("{\"title\":[");
		for(int i=0;i<level.size();i++){
			dto.setLevel(level.get(i).getId().getHeadLevel());
			List<ReportStyleInfo> listEntity=reportService.findReportHead(dto);
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

	@RequestMapping("/SDBBlist")
	@ResponseBody
	public List<?> qrySDBBByConditions(ReportDataDTO dto){
		dto.setNeedAccBookCode("Y");
		return reportService.qryReportData(dto);
	}

	@RequestMapping("/checkSDBB")
	@ResponseBody
	public InvokeResult checkSDBBReportData(ReportDataDTO dto){
		dto.setNeedAccBookCode("Y");
		List<ReportDataDTO> list  = new ArrayList();
		list.add(dto);
		System.out.println(dto.toString());
		return reportService.checkReportData(dto);
	}
}
