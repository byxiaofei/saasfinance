package com.sinosoft.service.report.impl;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.sinosoft.common.CurrentUser;
import com.sinosoft.common.CusSpecification;
import com.sinosoft.common.InvokeResult;
import com.sinosoft.controller.report.calculateMange;
import com.sinosoft.domain.Report.ReportCompute;
import com.sinosoft.domain.Report.ReportComputeId;
import com.sinosoft.domain.Report.ReportData;
import com.sinosoft.domain.Report.ReportStyleInfo;
import com.sinosoft.dto.report.ReportDataDTO;
import com.sinosoft.repository.report.ReportComputeRepository;
import com.sinosoft.repository.report.ReportDataRepository;
import com.sinosoft.repository.report.ReportRepository;
import com.sinosoft.service.report.ReportComputeService;
import com.sinosoft.service.report.ReportService;
import com.sinosoft.util.ExcelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ReportServiceImp implements ReportService {
	@Resource
	private ReportRepository reportRepository;
	@Resource
	private ReportDataRepository reportDataRepository;
	@Resource
	private ReportComputeRepository reportComputeRepository;
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
		if (!(dto.getAccBookCode()!=null&&!"".equals(dto.getAccBookCode()))) {
			dto.setAccBookCode(CurrentUser.getCurrentLoginAccount());
		}
		if (!(dto.getVersion()!=null&&!"".equals(dto.getVersion()))) {
			dto.setVersion("1");
		}
		if (!(dto.getReportCode()!=null&&!"".equals(dto.getReportCode()))) {
			dto.setReportCode(dto.getJJreportType()+"#"+dto.getJJreportName());
		}
		List<ReportStyleInfo> list=reportRepository.queryHead(dto.getReportCode(),dto.getLevel());//CurrentUser.getCurrentLoginAccount()
		return list;
	}

	@Override
	public List<ReportStyleInfo> findReportHeadLevel(ReportDataDTO dto) {
		if (!(dto.getAccBookCode()!=null&&!"".equals(dto.getAccBookCode()))) {
			dto.setAccBookCode(CurrentUser.getCurrentLoginAccount());
		}
		if (!(dto.getVersion()!=null&&!"".equals(dto.getVersion()))) {
			dto.setVersion("1");
		}
		if (!(dto.getReportCode()!=null&&!"".equals(dto.getReportCode()))) {
			dto.setReportCode(dto.getJJreportType()+"#"+dto.getJJreportName());
		}
		List<ReportStyleInfo> list=reportRepository.queryHeadLevel(dto.getReportCode());
		return list;
	}

	@Override
	public List<?> qryReportData(ReportDataDTO dto) {
		if (!(dto.getAccBookCode()!=null&&!"".equals(dto.getAccBookCode()))) {
			dto.setAccBookCode(CurrentUser.getCurrentLoginAccount());
			/*String reportCodes=dto.getReportCode();
			if("3#1".equals(reportCodes)||"4#1".equals(reportCodes)||"5#1".equals(reportCodes)||"6#1".equals(reportCodes)){
				dto.setAccBookCode(CurrentUser.getCurrentLoginAccount());
			}
			if("3#2".equals(reportCodes)||"4#2".equals(reportCodes)||"6#2".equals(reportCodes)){
				dto.setAccBookCode(reportAccountB+","+reportAccountC);
			}
			if("3#3".equals(reportCodes)||"4#3".equals(reportCodes)||"6#3".equals(reportCodes)){
				dto.setAccBookCode(reportAccountA+","+reportAccountB+","+reportAccountC);
			}*/

		}
		if (!(dto.getVersion()!=null&&!"".equals(dto.getVersion()))) {
			dto.setVersion("1");
		}
		if (!(dto.getReportCode()!=null&&!"".equals(dto.getReportCode()))) {
			dto.setReportCode(dto.getJJreportType()+"#"+dto.getJJreportName());
		}
		List<ReportStyleInfo> head=reportRepository.queryHeadField2(dto.getReportCode());
		StringBuffer sql=new StringBuffer();
		int paramsNo = 1;
		Map<Integer,Object> params = new HashMap<>();
		sql.append("select ");
		int index = 0;
		if (dto.getUnit()!=null&&"2".equals(dto.getUnit())) {
			index = 1;
		}
		for(int i=0;i<head.size();i++) {
			int m = i + 1;
			String d8 = head.get(i).getD8();
			String d9 = head.get(i).getD9();
			if (d8!=null&&!"".equals(d8)) {
				String[] d8s = d8.split(",");
				if(m<head.size()){
					/*if(m==1){
						sql.append(" r.d" + m + " as " + head.get(i).getD1() + ",");
					}else{
						sql.append(" format(r.d" + m + "," + d8s[index] + ") as " + head.get(i).getD1() + ",");
					}*/
					if (d9!=null&&"Y".equals(d9)) {//百分号列数据
						sql.append(" if(r.d" + m + " is not null and r.d" + m + " !='', format(r.d" + m + "*100," + d8s[index] + "), r.d" + m + ") as " + head.get(i).getD1() + ",");
					} else {
						sql.append(" if(r.d" + m + " is not null and r.d" + m + " !='', format(r.d" + m + "," + d8s[index] + "), r.d" + m + ") as " + head.get(i).getD1() + ",");
					}
				}else{
					if (d9!=null&&"Y".equals(d9)) {//百分号列数据
						sql.append(" if(r.d" + m + " is not null and r.d" + m + " !='', format(r.d" + m + "*100," + d8s[index] + "), r.d" + m + ") as " + head.get(i).getD1());
					} else {
						sql.append(" if(r.d" + m + " is not null and r.d" + m + " !='', format(r.d" + m + "," + d8s[index] + "), r.d" + m + ") as " + head.get(i).getD1());
					}
				}
			} else {
				if(m<head.size()){
					sql.append(" r.d" + m + " as " + head.get(i).getD1() + ",");
				}else{
					sql.append(" r.d" + m + " as " + head.get(i).getD1() );
				}
			}
		}
		sql.append(" from reportdata r where 1=1 ");
//		sql.append(" and r.report_code='"+dto.getReportCode()+"' and r.acc_book_code ='"+dto.getAccBookCode()+"'");
		sql.append(" and r.report_code=?"+paramsNo );
		params.put(paramsNo,dto.getReportCode());
		paramsNo++;
		sql.append(" and r.acc_book_code =?"+paramsNo );
		params.put(paramsNo,dto.getAccBookCode());
		paramsNo++;
//		sql.append(" and r.unit='"+dto.getUnit()+"' and r.year_month_date='"+dto.getYearMonthDate()+"' ");
		sql.append(" and r.unit=?"+paramsNo );
		params.put(paramsNo,dto.getUnit());
		paramsNo++;
		sql.append(" and r.year_month_date= ?"+paramsNo );
		params.put(paramsNo,dto.getYearMonthDate());
		paramsNo++;
//		sql.append(" and r.center_code = '"+CurrentUser.getCurrentLoginManageBranch()+"'");
		sql.append(" and r.center_code = ?"+paramsNo);
		params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
		paramsNo++;
		sql.append(" ORDER BY r.number ");
		List<?> list =reportRepository.queryBySqlSC(sql.toString(),params);

		return  list;
	}

	@Override
    public List<ReportStyleInfo> findZDYReportHead(ReportDataDTO dto) {
        List<ReportStyleInfo> list=reportRepository.queryZDYHead(dto.getReportCode(),dto.getLevel());//CurrentUser.getCurrentLoginAccount()
        return list;
    }

    @Override
    public List<ReportStyleInfo> findZDYReportHeadLevel(ReportDataDTO dto) {
        List<ReportStyleInfo> list=reportRepository.queryZDYHeadLevel(dto.getReportCode());
        return list;
    }

    @Override
    public List<?> qryZDYReportData(ReportDataDTO dto) {
        List<ReportStyleInfo> head=reportRepository.queryHeadField2(dto.getReportCode());
        int paramsNo = 1;
        Map<Integer,Object> params = new HashMap<>();
        StringBuffer sql=new StringBuffer();
        sql.append("select ");
        for(int i=0;i<head.size();i++) {
            int m = i + 1;
   			sql.append(" r.d" + m + " as " + head.get(i).getD1() + ",");
        }
		if(dto.getReportCode().equals("3#1")||dto.getReportCode().equals("3#2")||dto.getReportCode().equals("3#3")){
			sql.append(" r.d3v as name3v,r.d3k as name3k,r.d4k as name4k,r.d4v as name4v,r.d7k as name7k, r.d7v as name7v,r.d8k as name8k,r.d8v as name8v, ");
		}else if(dto.getReportCode().equals("4#1")||dto.getReportCode().equals("4#2")||dto.getReportCode().equals("4#3")) {
			sql.append(" r.d3v as name3v,r.d3k as name3k,r.d4k as name4k,r.d4v as name4v, ");
		}else{
			System.out.println("报告类型配置有误！");
		}
		sql.append(" r.acc_book_type as accBookType,r.acc_book_code as accBookCode,r.report_code as reportCode,r.version as version,r.number as number ");
		sql.append(" from reportcompute r where 1=1 ");
//		sql.append(" and r.report_code='"+dto.getReportCode()+"' ");
		sql.append(" and r.report_code=?"+paramsNo);
		params.put(paramsNo,dto.getReportCode());
		paramsNo++;
		sql.append(" and r.version=?"+paramsNo);
		params.put(paramsNo,dto.getVersion());
		paramsNo++;
		sql.append(" ORDER BY r.number ");
		List<?> list =reportRepository.queryBySqlSC(sql.toString(),params);

        if(dto.getReportCode().equals("3#1")||dto.getReportCode().equals("3#2")||dto.getReportCode().equals("3#3")){
			for (int i=0;i<list.size();i++) {
				Map map=(Map)list.get(i);
				if(map.get("name3v")!=null&&!"".equals(map.get("name3v"))&&!"null".equals(map.get("name3v"))){
					String name=map.get("name3").toString();
					String namev=map.get("name3v").toString();
					map.put("name3k",map.get("name3"));
					map.put("name3",changeCalculate(name,namev));
					map.put("name3v",changeVCalculate(namev));
				}
				if(map.get("name4v")!=null&&!"".equals(map.get("name4v"))&&!"null".equals(map.get("name4v"))){
					String name=map.get("name4").toString();
					String namev=map.get("name4v").toString();
					map.put("name4k",map.get("name4"));
					map.put("name4",changeCalculate(name,namev));
					map.put("name4v",changeVCalculate(namev));
				}
				if(map.get("name7v")!=null&&!"".equals(map.get("name7v"))&&!"null".equals(map.get("name7v"))){
					String name=map.get("name7").toString();
					String namev=map.get("name7v").toString();
					map.put("name7k",map.get("name7"));
					map.put("name7",changeCalculate(name,namev));
					map.put("name7v",changeVCalculate(namev));
				}
				if(map.get("name8v")!=null&&!"".equals(map.get("name8v"))&&!"null".equals(map.get("name8v"))){
					String name=map.get("name8").toString();
					String namev=map.get("name8v").toString();
					map.put("name8k",map.get("name8"));
					map.put("name8",changeCalculate(name,namev));
					map.put("name8v",changeVCalculate(namev));
				}
			}
		}else if(dto.getReportCode().equals("4#1")||dto.getReportCode().equals("4#2")||dto.getReportCode().equals("4#3")){
			for (int i=0;i<list.size();i++) {
				Map map=(Map)list.get(i);
				if(map.get("name3v")!=null&&!"".equals(map.get("name3v"))&&!"null".equals(map.get("name3v"))){
					String name=map.get("name3").toString();
					String namev=map.get("name3v").toString();
					map.put("name3k",map.get("name3"));
					map.put("name3",changeCalculate(name,namev));
					map.put("name3v",changeVCalculate(namev));
				}
				if(map.get("name4v")!=null&&!"".equals(map.get("name4v"))&&!"null".equals(map.get("name4v"))){
					String name=map.get("name4").toString();
					String namev=map.get("name4v").toString();
					map.put("name4k",map.get("name4"));
					map.put("name4",changeCalculate(name,namev));
					map.put("name4v",changeVCalculate(namev));
				}
			}
		}else{
			System.out.println("报告类型配置有误！");
			return null;
		}
        return  list;
    }

    @Override
	@Transactional
	public InvokeResult saveZDYCal(ReportDataDTO dto) {
		String centerCode = CurrentUser.getCurrentLoginManageBranch();
		String branchCode = CurrentUser.getCurrentLoginManageBranch();
		System.out.println("###");
		ReportComputeId id =new ReportComputeId();
		id.setAccBookCode(dto.getAccBookCode());
		id.setReportCode(dto.getReportCode());
		id.setNumber(Integer.parseInt(dto.getNumber()));
		id.setVersion(dto.getVersion());
		id.setAccBookType(dto.getAccBookType());
		id.setBranchCode(branchCode);
		id.setCenterCode(centerCode);
		ReportCompute reportCompute =reportComputeRepository.findById(id).get();

		reportCompute.setD1(dto.getName1());
		reportCompute.setD2(dto.getName2());
		reportCompute.setD3(dto.getName3k());
		reportCompute.setD3K(DKvalue(dto.getName3v()));
		reportCompute.setD3V(dto.getName3v()==""?dto.getName3v():dto.getName3v()+";1");

		reportCompute.setD4(dto.getName4k());
		reportCompute.setD4K(DKvalue(dto.getName4v()));
		reportCompute.setD4V(dto.getName4v()==""?dto.getName4v():dto.getName4v()+";1");

		if(dto.getReportCode().equals("3#1")||dto.getReportCode().equals("3#2")||dto.getReportCode().equals("3#3")){
			reportCompute.setD5(dto.getName5());
			reportCompute.setD6(dto.getName6());

			reportCompute.setD7(dto.getName7k());
			reportCompute.setD7K(DKvalue(dto.getName7v()));
			reportCompute.setD7V(dto.getName7v()==""?dto.getName7v():dto.getName7v()+";1");

			reportCompute.setD8(dto.getName8k());
			reportCompute.setD8K(DKvalue(dto.getName8v()));
			reportCompute.setD8V(dto.getName8v()==""?dto.getName8v():dto.getName8v()+";1");
		}
		reportComputeRepository.save(reportCompute);
		return InvokeResult.success();
	}

	@Override
	public InvokeResult checkFormula(ReportDataDTO dto){

		if (dto.getName3v()!=null&&!"".equals(dto.getName3v())) {
			String msg = reportComputeService.checkFormula(dto.getReportCode(), dto.getName3k(), DKvalue(dto.getName3v()), dto.getName3v());
			if (msg!=null&&!"".equals(msg)) {
				return InvokeResult.failure("行次"+dto.getName2()+":"+msg);
			}
		}

		if (dto.getName4v()!=null&&!"".equals(dto.getName4v())) {
			String msg = reportComputeService.checkFormula(dto.getReportCode(), dto.getName4k(), DKvalue(dto.getName4v()), dto.getName4v());
			if (msg!=null&&!"".equals(msg)) {
				return InvokeResult.failure("行次"+dto.getName2()+":"+msg);
			}
		}

		if(dto.getReportCode().equals("3#1")||dto.getReportCode().equals("3#2")||dto.getReportCode().equals("3#3")){

			if (dto.getName7v()!=null&&!"".equals(dto.getName7v())) {
				String msg = reportComputeService.checkFormula(dto.getReportCode(), dto.getName7k(), DKvalue(dto.getName7v()), dto.getName7v());
				if (msg!=null&&!"".equals(msg)) {
					return InvokeResult.failure("行次"+dto.getName6()+":"+msg);
				}
			}

			if (dto.getName8v()!=null&&!"".equals(dto.getName8v())) {
				String msg = reportComputeService.checkFormula(dto.getReportCode(), dto.getName8k(), DKvalue(dto.getName8v()), dto.getName8v());
				if (msg!=null&&!"".equals(msg)) {
					return InvokeResult.failure("行次"+dto.getName6()+":"+msg);
				}
			}
		}
		return InvokeResult.success();
	}

	public String DKvalue(String name){
		String[] value=name.split(";");
		switch (value.length){
			case 1:
				return "a";
			case 2:
				return "a,b";
			case 3:
				return "a,b,c";
			case 4:
				return "a,b,c,d";
			case 5:
				return "a,b,c,d,e";
			case 6:
				return "a,b,c,d,e,g";
			case 7:
				return "a,b,c,d,e,f,g";
			case 8:
				return "a,b,c,d,e,f,g,h";
			case 9:
				return "a,b,c,d,e,f,g,h,i";
			case 10:
				return "a,b,c,d,e,f,g,h,i,j";
			case 11:
				return "a,b,c,d,e,f,g,h,i,j,k";
			case 12:
				return "a,b,c,d,e,f,g,h,i,j,k,l";
			case 13:
				return "a,b,c,d,e,f,g,h,i,j,k,l,m";
			case 14:
				return "a,b,c,d,e,f,g,h,i,j,k,l,m,n";

		}
		return null;
	}
	public String changeVCalculate(String name){
		String result="";
		result=name.substring(0,name.length()-2);
		return result;
	}
	public String changeCalculate(String name,String namev){
		//$1_A_1_1503_QC_1_3;$1_A_1_1504_QC_2_3;1
		//$2_A_1_d2;$2_A_2_d2;$2_A_3_d2;$2_A_4_d2;$2_A_5_d2;1
		StringBuffer messge=new StringBuffer();
		messge.append("计算公式："+name+" 因子分别为：");
		String[] cal=namev.split(";");
		//length-1 去除计算层级标识
		for(int i=0;i<cal.length-1;i++){
			String[] msg=cal[i].split("_");
			if(msg[0].contains("$1")){
				String name2 =reportRepository.calculate("calculate2",msg[1]);
				String name3 =reportRepository.calculate("calculate3",msg[2]);
				String name5 =reportRepository.calculate("calculate5",msg[4]);
				String name6 =reportRepository.calculate("calculate6",msg[5]);
				String name7 =reportRepository.calculate("calculate7",msg[6]);
				if(name3.contains("科目")){
					messge.append(name2+"下"+msg[3]+"科目（"+name6+"）的"+name5+name7+";");
				}else{
					messge.append(name2+"下"+msg[3]+"专项的"+name5+name7+";");
				}
			}else if(msg[0].contains("$2")){
				String name2 =reportRepository.calculate("calculate2",msg[1]);
				messge.append(name2+"表内第"+msg[2]+"行第"+msg[3]+"列;");
			}else if(msg[0].contains("$3")){
				String name2 =reportRepository.calculate("calculate2",msg[1]);
				String name3 =reportRepository.calculate("calculate3",msg[2]);
				String name5 =reportRepository.calculate("calculate5",msg[4]);
				String name6 =reportRepository.calculate("calculate6",msg[5]);
				String name7 =reportRepository.calculate("calculate7",msg[6]);
				if(name3.contains("科目")){
					messge.append(name2+"下"+msg[3]+"科目（"+name6+"）的"+name5+name7+"（如果为正参与计算，如果为负则不参与）;");
				}else{
					messge.append(name2+"下"+msg[3]+"专项的"+name5+name7+";");
				}
			}else if(msg[0].contains("$4")){
				messge.append("固定值"+msg[1]+";");
			}else{
				System.out.println("公式配置有误！");
			}
		}
		return  messge.toString();
	}

	@Override
	@Transactional
	public InvokeResult createNewZDY(ReportDataDTO dto) {
		List<ReportCompute> list =reportComputeRepository.queryCalAll(dto.getVersion());
		//复制计算规则表数据到备份表中
		reportComputeRepository.copyCaltoTemp(dto.getVersion());
		//获取当前最大版本号
		int version=reportComputeRepository.queryLastVersion()+1;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = df.format(new Date());
		//修改备份表中的数据
		reportComputeRepository.updateCal(version,date,CurrentUser.getCurrentUser().getId()+"");
		//将备份表中的数据复制回计算规则表
		reportComputeRepository.copyCal();
		//删除备份表数据
		reportComputeRepository.deleteCalTemp();
		return InvokeResult.success();
	}
	/**
	 * 检查是否存在复核当前条件的报表数据
	 * @param dto 参数包含会计期间、报表编码、单位、版本号、账套编码，其中版本号默认为1，账套默认为当前登录账套（如果需要账套的限定，则应设置needAccBookCode字段值为Y）
	 * @return
	 */
	@Override
	public InvokeResult checkReportData(ReportDataDTO dto) {
		String reportCode = "";
		String version = "1";
		String accBookCode = CurrentUser.getCurrentLoginAccount();
		if (dto.getReportCode()!=null&&!"".equals(dto.getReportCode())) {
			reportCode = dto.getReportCode();
		} else {
			reportCode = dto.getJJreportType()+"#"+dto.getJJreportName();
		}
		if (dto.getVersion()!=null&&!"".equals(dto.getVersion())) {
			version = dto.getVersion();
		}
		if (dto.getNeedAccBookCode()!=null&&"Y".equals(dto.getNeedAccBookCode())&&dto.getAccBookCode()!=null&&!"".equals(dto.getAccBookCode())) {
			accBookCode = dto.getAccBookCode();
		}
		List<ReportData> reportDataList = null;
		if (dto.getNeedAccBookCode()!=null&&"Y".equals(dto.getNeedAccBookCode())) {
			reportDataList = reportDataRepository.findAll(new CusSpecification<>().and(
					CusSpecification.Cnd.eq("id.centerCode", CurrentUser.getCurrentLoginManageBranch()),
					CusSpecification.Cnd.eq("id.yearMonthDate", dto.getYearMonthDate()),
					CusSpecification.Cnd.like("id.accBookCode", accBookCode),
					CusSpecification.Cnd.eq("id.reportCode", reportCode),
					CusSpecification.Cnd.eq("id.version", version),
					CusSpecification.Cnd.eq("id.unit", dto.getUnit())));
		} else {
			reportDataList = reportDataRepository.findAll(new CusSpecification<>().and(
					CusSpecification.Cnd.eq("id.centerCode", CurrentUser.getCurrentLoginManageBranch()),
					CusSpecification.Cnd.eq("id.yearMonthDate", dto.getYearMonthDate()),
					CusSpecification.Cnd.eq("id.reportCode", reportCode),
					CusSpecification.Cnd.eq("id.version", version),
					CusSpecification.Cnd.eq("id.unit", dto.getUnit())));
		}
		if (reportDataList!=null&&reportDataList.size()>0) {
			return  InvokeResult.success();
		} else {
			return  InvokeResult.failure("无符合当前条件的数据，请先生成报表数据！");
		}
	}
}
