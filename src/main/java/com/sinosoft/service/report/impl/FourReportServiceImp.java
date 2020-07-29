package com.sinosoft.service.report.impl;


import com.sinosoft.common.CurrentUser;
import com.sinosoft.domain.Report.ReportData;
import com.sinosoft.domain.Report.ReportStyleInfo;
import com.sinosoft.dto.report.ReportDataDTO;
import com.sinosoft.repository.report.ReportDataRepository;
import com.sinosoft.repository.report.ReportRepository;
import com.sinosoft.service.report.ReportComputeService;
import com.sinosoft.service.report.FourReportService;
import com.sinosoft.util.ExcelUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FourReportServiceImp implements FourReportService {
	@Resource
	private ReportRepository reportRepository;
	@Resource
	private ReportDataRepository reportDataRepository;
	@Resource
	private ReportComputeService reportComputeService;
	@Value("${reportAccountA}")
	private String reportAccountA;
	@Value("${reportAccountB}")
	private String reportAccountB;
	@Value("${reportAccountC}")
	private String reportAccountC;
	@Value("${MODELPath}")
	private String MODELPath ;

	@Override
    public List<ReportStyleInfo> findReportHead(ReportDataDTO dto) {
        String accountCode = CurrentUser.getCurrentLoginAccountType();
        List<ReportStyleInfo> fourList=reportRepository.queryHead(dto.getJJreportName(),dto.getLevel());//CurrentUser.getCurrentLoginAccount()
		return fourList;
	}

	@Override
	public List<ReportStyleInfo> findReportHeadLevel(ReportDataDTO dto) {
		System.out.println(CurrentUser.getCurrentLoginAccount());
		List<ReportStyleInfo> fourList=reportRepository.queryHeadLevel(dto.getJJreportName());
		return fourList;
	}

	@Override
	public List<?> qryReportData(ReportDataDTO dto) {
		List<ReportStyleInfo> head=reportRepository.queryHeadField(dto.getJJreportName());
		StringBuffer sql=new StringBuffer();
		int paramsNo = 1;
		Map<Integer,Object> params = new HashMap<>();
		sql.append("select ");
		for(int i=0;i<head.size();i++) {
			int m = i + 1;
			if(m<head.size()){
				sql.append(" r.d" + m + " as " + head.get(i).getD1() + ",");
			}else{
				sql.append(" r.d" + m + " as " + head.get(i).getD1() );
			}
		}
		sql.append(" from reportdata r where 1=1 ");
		sql.append(" and r.report_code=?"+paramsNo);
		params.put(paramsNo,dto.getJJreportName());
		paramsNo++;
		sql.append(" and r.acc_book_code = ?"+paramsNo );
		params.put(paramsNo,"000002");
		paramsNo++;
		sql.append(" and r.unit=?"+paramsNo );
		params.put(paramsNo,dto.getUnit());
		paramsNo++;
		sql.append(" and r.year_month_date=?"+paramsNo );
		params.put(paramsNo,dto.getYearMonthDate());
		paramsNo++;
		sql.append(" ORDER BY r.number ");
		List<?> fourList =reportRepository.queryBySqlSC(sql.toString(),params);

		return  fourList;
	}

    @Override
    public void exportByCondition(HttpServletRequest request, HttpServletResponse response, String queryConditions) {

    }

    //判断数据是否已生成，未生成则不能导出
	@Override
    public String hasDatas(HttpServletRequest request, HttpServletResponse response, ReportDataDTO dto, String iSMerge){
		String accBookCode = CurrentUser.getCurrentLoginAccount();//当前账套编码
		List<String> reportCode = new ArrayList<>();
        if(iSMerge.equals("Y")){
			 reportCode =reportDataRepository.getReportCode(accBookCode);
		}else{
			reportCode =reportDataRepository.getReportCode(accBookCode,"1");
		}

		for(int i=0;i<reportCode.size();i++){
			List<ReportData> datas=reportDataRepository.queryReportData(reportCode.get(i),dto.getUnit(),"1",accBookCode,dto.getYearMonthDate(),CurrentUser.getCurrentLoginManageBranch());
			if (datas == null || datas.size() == 0){
				String tableNName =reportDataRepository.queryTableName(reportCode.get(i));
				return "当前账套下"+tableNName+"中没有数据";
			}
		}
		return "EXIST";

	}
	@Override
	public void download(HttpServletRequest request, HttpServletResponse response, List<ReportDataDTO> dtos,String JJreportName,String isMerge) {
		List<ReportDataDTO> list=new ArrayList<ReportDataDTO>();
		//遍历所有报表信息，挨个查询每个报表数据，塞入dto中
		for(int i=0;i<dtos.size();i++){
			ReportDataDTO dto=dtos.get(i);
			List<ReportData> datas=reportDataRepository.queryReportData(dto.getReportCode(),dto.getUnit(),"1",dto.getAccBookCode(),dto.getYearMonthDate(),CurrentUser.getCurrentLoginManageBranch());
			dto.setDataList(datas);
			list.add(i,dto);
		}
		String []account = {reportAccountA,reportAccountB,reportAccountC};
		ExcelUtil excelUtil = new ExcelUtil();
		excelUtil.exportFourReport(request,response,list,account,MODELPath,JJreportName,isMerge);
	}
}
