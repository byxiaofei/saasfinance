package com.sinosoft.controller.report;


import com.sinosoft.common.CurrentUser;
import com.sinosoft.common.InvokeResult;
import com.sinosoft.dto.report.ReportDataDTO;
import com.sinosoft.repository.BranchInfoRepository;
import com.sinosoft.repository.report.ReportDataRepository;
import com.sinosoft.service.report.FourReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/fourReport")
public class FourReportController {
	@Resource
	private ReportDataRepository reportDataRepository ;
	@Resource
    private FourReportService fourReportService ;
	@Resource
	private BranchInfoRepository branchInfoRepository;

	@Value("${reportAccountA}")
	private String reportAccountA;
	@Value("${reportAccountB}")
	private String reportAccountB;
	@Value("${reportAccountC}")
	private String reportAccountC;
	private Logger logger = LoggerFactory.getLogger(FourReportController.class);

	@RequestMapping("/")
	public String page(){
		return "report/fourReportHead";
	}

	@RequestMapping("/isexist")
	@ResponseBody
	public InvokeResult compute(HttpServletRequest request, HttpServletResponse response, ReportDataDTO dto) {
		try {
			//获取汇总机构
			String centerCode = CurrentUser.getCurrentLoginManageBranch();
			List<String>  summaryBranch = new ArrayList();
			summaryBranch = branchInfoRepository.findByLevel("1");
			String iSMerge = "N";
			if(summaryBranch.contains(centerCode)){
				iSMerge = "Y";
			}
			String msg = fourReportService.hasDatas(request,response,dto,iSMerge);
			if (msg!=null&&!"".equals(msg)) {
				if ("EXIST".equals(msg)) {
					return InvokeResult.success();
				} else if (!"EXIST".equals(msg)) {
					return InvokeResult.failure(msg);
				}
			}
			return InvokeResult.success();
		} catch (Exception e) {
			return InvokeResult.failure("报表导出异常");
		}
	}

	/**
	 * 根据页面选择的会计月度和单位进行基金报表的导出
	 * 可以动态设置表信息进行导出
	 * @param request
	 * @param response
	 * @param dto
	 */
	@RequestMapping(path="/download")
	@ResponseBody
	public void download(HttpServletRequest request, HttpServletResponse response, ReportDataDTO dto){
		try{
			//	System.out.println(dto.getYearMonthDate());
			String yearMonthDate = dto.getYearMonthDate();//会计期间
			String accBookCode = CurrentUser.getCurrentLoginAccount();//当前账套编码
			String centerCode = CurrentUser.getCurrentLoginManageBranch();
			String unit = dto.getUnit();//单位
			String JJreportName = dto.getJJreportName();

			//获取汇总机构
			List<String>  summaryBranch = new ArrayList();
			summaryBranch = branchInfoRepository.findByLevel("1");
			if(!summaryBranch.contains(centerCode)){
				List<ReportDataDTO> dtos=new ArrayList<ReportDataDTO>();

				String []name1= {"资产负债表","利润表","现金流量表","所有者权益变动表","国际化报表"};

				int []row1={6,5,6,5,4};
				int []cell1={6,6,5,15,4};

				List<String> reportCode =reportDataRepository.getReportCode(accBookCode,"1");

				for(int i=0;i<name1.length;i++){
					ReportDataDTO d = new ReportDataDTO();
					d.setYearMonthDate(yearMonthDate);
					d.setUnit(unit);
					d.setAccBookCode(accBookCode);
					d.setReportCode(reportCode.get(i));//设置报表编号=（报表类型#报表名称）
					d.setSheetName(name1[i]);
					d.setRowNum(row1[i]);//设置开始塞数行数
					d.setCellNum(cell1[i]);//设置一共塞多少列
					dtos.add(d);
				}
				fourReportService.download(request, response,dtos,JJreportName,"N");
			}else{
				List<ReportDataDTO> dtos=new ArrayList<ReportDataDTO>();
				String []name1= {"资产负债表","资产负债表(合并报表)","利润表","利润表(合并报表)","现金流量表","现金流流量表(合并报表)","所有者权益变动表","所有者权益变动表(合并报表)","国际化报表","国际化报表(合并报表)"};

				int []row1={6,6,5,5,6,6,5,5,4,4};
				int []cell1={6,6,6,6,5,5,15,15,4,4};
				List<String> reportCode =reportDataRepository.getReportCode(accBookCode);

				for(int i=0;i<name1.length;i++){
					ReportDataDTO d = new ReportDataDTO();
					d.setYearMonthDate(yearMonthDate);
					d.setUnit(unit);
					d.setAccBookCode(accBookCode);
					d.setReportCode(reportCode.get(i));//设置报表编号=（报表类型#报表名称）
					d.setSheetName(name1[i]);
					d.setRowNum(row1[i]);//设置开始塞数行数
					d.setCellNum(cell1[i]);//设置一共塞多少列
					dtos.add(d);
				}
				fourReportService.download(request, response,dtos,JJreportName,"Y");
			}


		}catch(Exception e){
			e.printStackTrace();
		}
	}


	@RequestMapping(path = "/downloadSingle")
	@ResponseBody
	public void downloadSingle(HttpServletRequest request, HttpServletResponse response, ReportDataDTO dto){

	}
}
