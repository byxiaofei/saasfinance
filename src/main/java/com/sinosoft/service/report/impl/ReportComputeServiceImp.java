package com.sinosoft.service.report.impl;

import com.sinosoft.common.CurrentTime;
import com.sinosoft.common.CurrentUser;
import com.sinosoft.common.CusSpecification;
import com.sinosoft.domain.Report.ReportCompute;
import com.sinosoft.domain.Report.ReportData;
import com.sinosoft.domain.Report.ReportDataId;
import com.sinosoft.dto.report.ReportDataDTO;
import com.sinosoft.repository.BranchInfoRepository;
import com.sinosoft.repository.CodeSelectRepository;
import com.sinosoft.repository.account.AccMonthRespository;
import com.sinosoft.repository.report.ReportComputeRepository;
import com.sinosoft.repository.report.ReportDataRepository;
import com.sinosoft.service.report.ReportComputeService;
import com.sinosoft.service.synthesize.DetailAccountService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ReportComputeServiceImp implements ReportComputeService {
	@Resource
	private ReportComputeRepository reportComputeRepository;
	@Resource
	private CodeSelectRepository codeSelectRepository;
	@Resource
	private ReportDataRepository reportDataRepository;
	@Resource
	private DetailAccountService detailAccountService;
	@Resource
    private AccMonthRespository accMonthRespository;
	@Resource
	private BranchInfoRepository branchInfoRepository;

	@Value("${reportAccountA}")
	private String reportAccountA;
	@Value("${reportAccountB}")
	private String reportAccountB;
	@Value("${reportAccountC}")
	private String reportAccountC;
	@Value("${reportAccountD}")
	private String reportAccountD;
	@Value("${reportAccountE}")
	private String reportAccountE;

	/**
	 * 校验指定账套下当前会计期间凭证是否全部记账
	 * @param dto
	 */
	@Override
	public boolean checkCompute(ReportDataDTO dto, String type){
		String accBookCode = CurrentUser.getCurrentLoginAccount();

		return checkVouncherAccountingByYearMonthDate(dto.getYearMonthDate(), accBookCode);
	}

	/**
	 * 校验指定账套下当前会计期间凭证是否全部记账
	 * @param yearMonthDate
	 * @param accBookCode 可多账套（eg：000002,000003）
	 * @return true-已全部记账，false-存在未记账凭证
	 */
	private boolean checkVouncherAccountingByYearMonthDate(String yearMonthDate, String accBookCode){
		String aBC = CurrentUser.getCurrentLoginAccount();
		int paramsNo =1;
		Map<Integer,Object> params = new HashMap<>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT a.voucher_no AS voucherNo FROM accmainvoucher a,accmonthtrace am WHERE 1=1");
		sql.append(" AND a.center_code=?"+paramsNo );
		params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
		paramsNo++;
		sql.append("  and  am.acc_book_code = a.acc_book_code AND am.year_month_date = a.year_month_date AND am.acc_month_stat NOT IN ('3','5') AND a.voucher_flag != '3'");
		sql.append(" AND a.year_month_date = ?"+paramsNo);
		params.put(paramsNo,yearMonthDate);
		paramsNo++;
		sql.append(" AND a.acc_book_code in ("+"?"+paramsNo+")");
		params.put(paramsNo, Arrays.asList(aBC));
		List<?> list = reportComputeRepository.queryBySqlSC(sql.toString(),params);
		if (list!=null&&list.size()>0) {
			return false;
		}
		return true;
	}

	/**
	 * 报表计算，返回计算成功与否的字符串：SUCCESS-计算成功，FAILURE-计算失败，NOTEXIST-无计算公式配置信息
	 * @param dto 参数包含账套编码、会计期间、报表编码（两个字段）、报表版本、单位，其中账套编码不设置则为当前登录账套编码，版本号默认为1
	 * @return
	 */
	@Override
	@Transactional
	public String compute(ReportDataDTO dto){
	    String accBookCode = CurrentUser.getCurrentLoginAccount();
	    if (dto.getNeedAccBookCode()!=null&&"Y".equals(dto.getNeedAccBookCode())&&dto.getAccBookCode()!=null&&!"".equals(dto.getAccBookCode())) {
            accBookCode = dto.getAccBookCode();
        }
		/*
			1.先根据参数查询出报表计算逻辑表对应的定义数据（依次按优先级、行号number排序）
			2.再根据查询结果进行计算（按$1、$2、$3）
			3.将计算结果处理到报表数据表中
		 */
		//1
		List<ReportCompute> reportComputeList = getReportComputeList(dto);

		if (reportComputeList!=null&&reportComputeList.size()>0) {
			//先查询对应的报表数据，如果已存在，则删除
			List<ReportData> oldReportDataList = null;
			if (dto.getNeedAccBookCode()!=null&&"Y".equals(dto.getNeedAccBookCode())) {
				oldReportDataList = reportDataRepository.findAll(new CusSpecification<>().and(
						CusSpecification.Cnd.eq("id.yearMonthDate", dto.getYearMonthDate()),
						CusSpecification.Cnd.eq("id.centerCode", CurrentUser.getCurrentLoginManageBranch()),
						CusSpecification.Cnd.like("id.accBookCode", accBookCode),
						CusSpecification.Cnd.eq("id.reportCode", dto.getReportCode()),
						CusSpecification.Cnd.eq("id.version", dto.getVersion())));
			} else {
				oldReportDataList = reportDataRepository.findAll(new CusSpecification<>().and(
						CusSpecification.Cnd.eq("id.centerCode", CurrentUser.getCurrentLoginManageBranch()),
						CusSpecification.Cnd.eq("id.yearMonthDate", dto.getYearMonthDate()),
						CusSpecification.Cnd.eq("id.reportCode", dto.getReportCode()),
						CusSpecification.Cnd.eq("id.version", dto.getVersion())));
			}
			if (oldReportDataList!=null&&oldReportDataList.size()>0) {
				for (ReportData reportData : oldReportDataList) {
					reportDataRepository.delete(reportData);
				}
				reportDataRepository.flush();
			}

			//优先级
			List<Integer> leverList = new ArrayList<>();
			//优先级-行列-完整公式
			Map<Integer, Map<String, List<String>>> leverComputeAndKAndVMap = new HashMap<>();
			for (ReportCompute reportCompute : reportComputeList) {
			    //单位元、万元同时处理
				ReportDataId reportDataId = new ReportDataId();
//				reportDataId.setCenterCode(reportCompute.getId().getCenterCode());
				reportDataId.setCenterCode(CurrentUser.getCurrentLoginManageBranch());
				reportDataId.setBranchCode(reportCompute.getId().getBranchCode());
				reportDataId.setAccBookType(reportCompute.getId().getAccBookType());
				reportDataId.setAccBookCode(accBookCode);
				reportDataId.setReportCode(reportCompute.getId().getReportCode());
				reportDataId.setVersion(reportCompute.getId().getVersion());
				reportDataId.setNumber(reportCompute.getId().getNumber());
				reportDataId.setYearMonthDate(dto.getYearMonthDate());
				reportDataId.setUnit("1");//单位：元
				ReportData reportData = new ReportData();
				reportData.setId(reportDataId);
				reportData.setReportName(reportCompute.getReportName());

                ReportDataId reportDataId2 = new ReportDataId();
//                reportDataId2.setCenterCode(reportCompute.getId().getCenterCode());
				reportDataId2.setCenterCode(CurrentUser.getCurrentLoginManageBranch());
                reportDataId2.setBranchCode(reportCompute.getId().getBranchCode());
                reportDataId2.setAccBookType(reportCompute.getId().getAccBookType());
                reportDataId2.setAccBookCode(accBookCode);
                reportDataId2.setReportCode(reportCompute.getId().getReportCode());
                reportDataId2.setVersion(reportCompute.getId().getVersion());
                reportDataId2.setNumber(reportCompute.getId().getNumber());
                reportDataId2.setYearMonthDate(dto.getYearMonthDate());
                reportDataId2.setUnit("2");//单位：万元
                ReportData reportData2 = new ReportData();
                reportData2.setId(reportDataId2);
                reportData2.setReportName(reportCompute.getReportName());

				//2 开始计算处理
                System.out.println("正在处理报表"+reportDataId.getReportCode()+"："+reportDataId.getNumber()+"行");
				for (int i=1;i<=30;i++) {
					/*
						先获取参数值（V列），
							若无数据则表示该行该列无需计算；
							若有数据则表示需要计算（$1、$2、SQL），
							再判断最后一个附加参数（行内计算优先级），若为1则可直接计算，否则需等待基础数据计算之后才可计算
					 */
					String formulaVAll = getDColV(i, reportCompute);
					if (formulaVAll!=null&&!"".equals(formulaVAll)) {
						//获取附加参数（行内计算优先级）
						Integer lever = Integer.valueOf(formulaVAll.substring(formulaVAll.lastIndexOf(";")+1));
						//计算参数取数规则
						String formulaV = formulaVAll.substring(0, formulaVAll.lastIndexOf(";"));
						if (lever==1) {
							//可直接计算
							//$1、$2、$3、$5和SQL计算
							computeByCol(i, getDCol(i, reportCompute), getDColK(i, reportCompute), formulaV, reportData, reportData2);
						} else {
							//需等待基础数据计算之后才可计算，将其添加到 leverComputeMap 中
							String rowCol = reportDataId.getNumber()+"#"+i;//行#列
							if (leverComputeAndKAndVMap.containsKey(lever)) {
								//已保存过的优先级
								Map<String, List<String>> leverMap = leverComputeAndKAndVMap.get(lever);
								if (leverMap.containsKey(rowCol)) {//已保存过的行列
									//暂不会出现
								} else {
									List<String> dAndKAndVList = new ArrayList<>();
									dAndKAndVList.add(0, getDCol(i, reportCompute));//计算规则
									dAndKAndVList.add(1, getDColK(i, reportCompute));//计算参数
									dAndKAndVList.add(2, formulaV);//计算参数取数规则
									leverMap.put(rowCol, dAndKAndVList);
								}
							} else {
								Map<String, List<String>> leverMap = new HashMap<>();
								List<String> dAndKAndVList = new ArrayList<>();
								dAndKAndVList.add(0, getDCol(i, reportCompute));//计算规则
								dAndKAndVList.add(1, getDColK(i, reportCompute));//计算参数
								dAndKAndVList.add(2, formulaV);//计算参数取数规则
								leverMap.put(rowCol, dAndKAndVList);
								leverComputeAndKAndVMap.put(lever, leverMap);
								leverList.add(lever);
							}
						}
					} else {
						//设置非计算d系列字段
						setDCol(i, reportData, getDCol(i, reportCompute));
                        setDCol(i, reportData2, getDCol(i, reportCompute));
					}
				}
				// 3.将计算结果处理到报表数据表中
				reportData.setCreateBy(CurrentUser.getCurrentUser().getId()+"");
				reportData.setCreateTime(CurrentTime.getCurrentTime());
                reportData2.setCreateBy(CurrentUser.getCurrentUser().getId()+"");
                reportData2.setCreateTime(CurrentTime.getCurrentTime());
				reportDataRepository.save(reportData);
                reportDataRepository.save(reportData2);
				reportDataRepository.flush();
			}
			//可计算的循环结束后，再计算不可直接计算的行内优先级的数据
			if (leverComputeAndKAndVMap.size()>0) {
				computeByCol(dto.getYearMonthDate(), leverList, leverComputeAndKAndVMap, reportComputeList.get(0));
			}
			return "SUCCESS";
		} else {
			return "NOTEXIST";
		}
	}

    /**
	 *四大报表生成
	 * @param dto
	 * @return
	 */
	@Override
	@Transactional
	public String fourComputeFund(ReportDataDTO dto) {
		ReportDataDTO reportDataDTO = new ReportDataDTO();
		reportDataDTO.setYearMonthDate(dto.getYearMonthDate());
		//四大报表有账套区分 所以设为Y
		reportDataDTO.setNeedAccBookCode("Y");
		reportDataDTO.setVersion("1");
		//从codemanage表中查询四大报表基本表（order_by为1的）
		List<Map<String, Object>> list = codeSelectRepository.findSDBBreportTypeByAccountAndType1(CurrentUser.getCurrentLoginAccount(),"1");
		if (list!=null&&list.size()>0) {
			//遍历获取报表编码
			for (Map<String, Object> map : list) {
				String reportCode = (String) map.get("value");
				//调用计算方法计算根据报表编码
				reportDataDTO.setReportCode(reportCode);
				String msg = compute(reportDataDTO);
				//不考虑单位，同时生成两个单位的（元、万元）
				if (msg!=null&&!"".equals(msg)) {
					if ("SUCCESS".equals(msg)){
					} else if ("NOTEXIST".equals(msg)) {
						System.out.println("无报表（"+reportCode+"）计算公式配置信息");
					} else {
						return  msg;
					}
				}
			}
		};
		return "SUCCESS";

	}

    /**
     * 四大报表生成前校验
     */
    @Override
    public String checkFourReportCompute(ReportDataDTO dto) {
		String centerCode = CurrentUser.getCurrentLoginManageBranch();
		String msg="";
    	//获取汇总机构
		List<String>  summaryBranch = new ArrayList();
		summaryBranch = branchInfoRepository.findByLevel("1");
		if(!summaryBranch.contains(centerCode)){
			msg="FAILURE2";
			System.out.println("所属机构不是汇总机构，不允许生成合并表");
			return msg;
		}
		//获取该机构下的所有子机构存到list中
		List<String> subBranch = new ArrayList();
		subBranch = branchInfoRepository.findBySuperCom(centerCode,"0");
		//subBranch.add(centerCode);
       //遍历list，查询那些子机构没有生成基础报表
       for(String branch : subBranch){
		   List<Map<String, Object>> reportDataList = codeSelectRepository.findDataFromReportData(dto.getYearMonthDate(), "1", "1",branch,CurrentUser.getCurrentLoginAccount());
		   int count1 = 0;
		   int count2 = 0;
		   int count3 = 0;
		   int count4 = 0;
		   if(reportDataList != null && reportDataList.size() > 0) {
			   for (Map<String, Object> map : reportDataList) {
				   String reportCode = (String) map.get("text");
				   if ("3#1".equals(reportCode)) {
					   count1++;
				   }
				   if ("4#1".equals(reportCode)) {
					   count2++;
				   }
				   if ("5#1".equals(reportCode)) {
					   count3++;
				   }
				   if ("6#1".equals(reportCode)) {
					   count4++;
				   }
			   }
			   if (!(count1 >= 1 && count2 >= 1 && count3 >= 1 && count4 >= 1)){
				   msg="机构"+branch +"四大报表所有基本表未全部生成，不允许生成合并表";
				   System.out.println("机构"+branch +"四大报表所有基本表未全部生成，不允许生成合并表");
				   return msg;
			   }
		   }else{
			   msg="机构"+branch +"四大报表所有基本表未全部生成，不允许生成合并表";
			   System.out.println("机构"+branch +"四大报表所有基本表未全部生成，不允许生成合并表");
			   return msg;
		   }

	   }
	   msg="SUCCESS";
       System.out.println("四大报表所有基本表均已生成，允许生成合并表");
       return msg;
    }

    /**
	 * 四大报表合并表的生成
	 * @param dto
	 * @return
	 */
	@Override
	@Transactional
	public String fourMergeComputeFund(ReportDataDTO dto) {
		ReportDataDTO reportDataDTO = new ReportDataDTO();
		reportDataDTO.setYearMonthDate(dto.getYearMonthDate());
		reportDataDTO.setNeedAccBookCode("Y");
		reportDataDTO.setVersion("1");

		String centerCode = CurrentUser.getCurrentLoginManageBranch();//机构编码
		String accBookCode = CurrentUser.getCurrentLoginAccount();//账套编码
		//获取该合并机构下的所有子机构 （虚拟机构除外）
		List<String> subBranch = branchInfoRepository.findBySuperCom(centerCode,"0");

		//根据当前账套从codeManage表中查询四大报表合并表的编码
		List<Map<String, Object>> SDBBList = codeSelectRepository.findSDBBreport(accBookCode);
		if (SDBBList != null && SDBBList.size() > 0) {
			for (int i = 0; i < SDBBList.size(); i++) {
				String code = (String) SDBBList.get(i).get("value");
				String code1 = code.substring(0,2)+"1";
				String codeName = (String) SDBBList.get(i).get("codeName");

				//如果以前存在合并数据，先删除历史数据
				List<ReportData> mergeReportDataList = new ArrayList<ReportData>();
				mergeReportDataList = reportDataRepository.findAll(new CusSpecification<>().and(
						CusSpecification.Cnd.eq("id.yearMonthDate", dto.getYearMonthDate()),
						CusSpecification.Cnd.eq("id.centerCode", centerCode),
						CusSpecification.Cnd.like("id.accBookCode", accBookCode),
						CusSpecification.Cnd.eq("id.reportCode", code),
						CusSpecification.Cnd.eq("id.version", dto.getVersion()),
						CusSpecification.Cnd.eq("id.unit", "1") ));
				if(mergeReportDataList!=null && mergeReportDataList.size()>0){
					for (ReportData reportData : mergeReportDataList) {
						reportDataRepository.delete(reportData);
					}
					reportDataRepository.flush();
				}

				//累加各机构的值
				//汇总存储基础表数据
				List<ReportData> newReportDataList1 = new ArrayList<ReportData>();
				List<ReportData> newReportDataList2 = new ArrayList<ReportData>();
				int count = 1;
				for(String branch : subBranch){

					List<ReportData> oldReportDataList = new ArrayList<ReportData>();
					oldReportDataList = reportDataRepository.findAll(new CusSpecification<>().and(
							CusSpecification.Cnd.eq("id.yearMonthDate", dto.getYearMonthDate()),
							CusSpecification.Cnd.eq("id.centerCode", branch),
							CusSpecification.Cnd.like("id.accBookCode", accBookCode),
							CusSpecification.Cnd.eq("id.reportCode", code1),
							CusSpecification.Cnd.eq("id.version", dto.getVersion()),
							CusSpecification.Cnd.eq("id.unit", "1") ));//TODO 默认为元
					if(code.equals("3#2")){
						if(count==1){
							for(ReportData reportData : oldReportDataList){
								ReportData reportData1 = new ReportData();
								ReportData reportData2 = new ReportData();

								ReportDataId reportDataId = new ReportDataId();
								reportDataId.setCenterCode(centerCode);
								reportDataId.setBranchCode(centerCode);
								reportDataId.setAccBookType(reportData.getId().getAccBookType());
								reportDataId.setAccBookCode(reportData.getId().getAccBookCode());
								reportDataId.setReportCode(code);
								reportDataId.setVersion(reportData.getId().getVersion());
								reportDataId.setYearMonthDate(reportData.getId().getYearMonthDate());
								reportDataId.setUnit("1");
								reportDataId.setNumber(reportData.getId().getNumber());

								reportData1.setId(reportDataId);
								reportData1.setReportName(codeName);
								reportData1.setD1(reportData.getD1());
								reportData1.setD2(reportData.getD2());
								reportData1.setD3(reportData.getD3());
								reportData1.setD4(reportData.getD4());
								reportData1.setD5(reportData.getD5());
								reportData1.setD6(reportData.getD6());
								reportData1.setCreateBy(CurrentUser.getCurrentUser().getId()+"");
								reportData1.setCreateTime(CurrentTime.getCurrentTime());

								newReportDataList1.add(reportData1);
								//TODO 保存单位 万
								ReportDataId reportDataId2 = new ReportDataId();
								reportDataId2.setCenterCode(centerCode);
								reportDataId2.setBranchCode(centerCode);
								reportDataId2.setAccBookType(reportData.getId().getAccBookType());
								reportDataId2.setAccBookCode(reportData.getId().getAccBookCode());
								reportDataId2.setReportCode(code);
								reportDataId2.setVersion(reportData.getId().getVersion());
								reportDataId2.setYearMonthDate(reportData.getId().getYearMonthDate());
								reportDataId2.setUnit("2");
								reportDataId2.setNumber(reportData.getId().getNumber());

								reportData2.setId(reportDataId2);
								reportData2.setReportName(codeName);
								reportData2.setD1(reportData.getD1());
								reportData2.setD2(toUnit(reportData.getD2()));//转数字 相加后在转成 字符串
								reportData2.setD3(toUnit(reportData.getD3()));
								reportData2.setD4(reportData.getD4());
								reportData2.setD5(toUnit(reportData.getD5()));
								reportData2.setD6(toUnit(reportData.getD6()));
								reportData2.setCreateBy(CurrentUser.getCurrentUser().getId()+"");
								reportData2.setCreateTime(CurrentTime.getCurrentTime());
								newReportDataList2.add(reportData2);

							}
						}else{
							for(int index =0;index<oldReportDataList.size();index++){
								ReportData reportData1 = new ReportData();
								ReportData reportData2 = new ReportData();

								reportData1 = oldReportDataList.get(index);
								ReportData mergeReportData = new ReportData();
								ReportData mergeReportData2 = new ReportData();
								mergeReportData = newReportDataList1.get(index);
								mergeReportData2 = newReportDataList2.get(index);

								mergeReportData.setD2(numAdd(mergeReportData.getD2(),reportData1.getD2()));//转数字 相加后在转成 字符串
								mergeReportData.setD3(numAdd(mergeReportData.getD3(),reportData1.getD3()));
								mergeReportData.setD5(numAdd(mergeReportData.getD5(),reportData1.getD5()));
								mergeReportData.setD6(numAdd(mergeReportData.getD6(),reportData1.getD6()));
								//单位 万
								mergeReportData2.setD2(toUnit(mergeReportData.getD2()));//转数字 相加后在转成 字符串
								mergeReportData2.setD3(toUnit(mergeReportData.getD3()));
								mergeReportData2.setD5(toUnit(mergeReportData.getD5()));
								mergeReportData2.setD6(toUnit(mergeReportData.getD6()));

							}
						}
					}else if(code.equals("4#2")){
						if(count==1){
							for(ReportData reportData : oldReportDataList){
								ReportData reportData1 = new ReportData();
								ReportData reportData2 = new ReportData();

								ReportDataId reportDataId = new ReportDataId();
								reportDataId.setCenterCode(centerCode);
								reportDataId.setBranchCode(centerCode);
								reportDataId.setAccBookType(reportData.getId().getAccBookType());
								reportDataId.setAccBookCode(reportData.getId().getAccBookCode());
								reportDataId.setReportCode(code);
								reportDataId.setVersion(reportData.getId().getVersion());
								reportDataId.setYearMonthDate(reportData.getId().getYearMonthDate());
								reportDataId.setUnit("1");
								reportDataId.setNumber(reportData.getId().getNumber());

								reportData1.setId(reportDataId);
								reportData1.setReportName(codeName);
								reportData1.setD1(reportData.getD1());
								reportData1.setD2(reportData.getD2());
								reportData1.setD3(reportData.getD3());
								reportData1.setCreateBy(CurrentUser.getCurrentUser().getId()+"");
								reportData1.setCreateTime(CurrentTime.getCurrentTime());

								newReportDataList1.add(reportData1);
								//TODO 保存单位 万
								ReportDataId reportDataId2 = new ReportDataId();
								reportDataId2.setCenterCode(centerCode);
								reportDataId2.setBranchCode(centerCode);
								reportDataId2.setAccBookType(reportData.getId().getAccBookType());
								reportDataId2.setAccBookCode(reportData.getId().getAccBookCode());
								reportDataId2.setReportCode(code);
								reportDataId2.setVersion(reportData.getId().getVersion());
								reportDataId2.setYearMonthDate(reportData.getId().getYearMonthDate());
								reportDataId2.setUnit("2");
								reportDataId2.setNumber(reportData.getId().getNumber());

								reportData2.setId(reportDataId2);
								reportData2.setReportName(codeName);
								reportData2.setD1(reportData.getD1());
								reportData2.setD2(toUnit(reportData.getD2()));//转数字 相加后在转成 字符串
								reportData2.setD3(toUnit(reportData.getD3()));
								reportData2.setCreateBy(CurrentUser.getCurrentUser().getId()+"");
								reportData2.setCreateTime(CurrentTime.getCurrentTime());
								newReportDataList2.add(reportData2);

							}
						}else{
							for(int index =0;index<oldReportDataList.size();index++){
								ReportData reportData1 = new ReportData();
								ReportData reportData2 = new ReportData();

								reportData1 = oldReportDataList.get(index);
								ReportData mergeReportData = new ReportData();
								ReportData mergeReportData2 = new ReportData();
								mergeReportData = newReportDataList1.get(index);
								mergeReportData2 = newReportDataList2.get(index);
								mergeReportData.setD2(numAdd(mergeReportData.getD2(),reportData1.getD2()));//转数字 相加后在转成 字符串
								mergeReportData.setD3(numAdd(mergeReportData.getD3(),reportData1.getD3()));

								//单位 万
								mergeReportData2.setD2(toUnit(mergeReportData.getD2()));//转数字 相加后在转成 字符串
								mergeReportData2.setD3(toUnit(mergeReportData.getD3()));
							}
						}
					}else if(code.equals("5#2")){
						if(count==1){
							for(ReportData reportData : oldReportDataList){
								ReportData reportData1 = new ReportData();
								ReportData reportData2 = new ReportData();

								ReportDataId reportDataId = new ReportDataId();
								reportDataId.setCenterCode(centerCode);
								reportDataId.setBranchCode(centerCode);
								reportDataId.setAccBookType(reportData.getId().getAccBookType());
								reportDataId.setAccBookCode(reportData.getId().getAccBookCode());
								reportDataId.setReportCode(code);
								reportDataId.setVersion(reportData.getId().getVersion());
								reportDataId.setYearMonthDate(reportData.getId().getYearMonthDate());
								reportDataId.setUnit("1");
								reportDataId.setNumber(reportData.getId().getNumber());

								reportData1.setId(reportDataId);
								reportData1.setReportName(codeName);
								reportData1.setD1(reportData.getD1());
								reportData1.setD2(reportData.getD2());
								reportData1.setD3(reportData.getD3());
								reportData1.setD4(reportData.getD4());
								reportData1.setCreateBy(CurrentUser.getCurrentUser().getId()+"");
								reportData1.setCreateTime(CurrentTime.getCurrentTime());

								newReportDataList1.add(reportData1);
								//TODO 保存单位 万
								ReportDataId reportDataId2 = new ReportDataId();
								reportDataId2.setCenterCode(centerCode);
								reportDataId2.setBranchCode(centerCode);
								reportDataId2.setAccBookType(reportData.getId().getAccBookType());
								reportDataId2.setAccBookCode(reportData.getId().getAccBookCode());
								reportDataId2.setReportCode(code);
								reportDataId2.setVersion(reportData.getId().getVersion());
								reportDataId2.setYearMonthDate(reportData.getId().getYearMonthDate());
								reportDataId2.setUnit("2");
								reportDataId2.setNumber(reportData.getId().getNumber());

								reportData2.setId(reportDataId2);
								reportData2.setReportName(codeName);
								reportData2.setD1(reportData.getD1());
								reportData2.setD2(reportData.getD2());
								reportData2.setD3(toUnit(reportData.getD3()));//转数字 相加后在转成 字符串
								reportData2.setD4(toUnit(reportData.getD4()));
								reportData2.setCreateBy(CurrentUser.getCurrentUser().getId()+"");
								reportData2.setCreateTime(CurrentTime.getCurrentTime());
								newReportDataList2.add(reportData2);

							}
						}else{
							for(int index =0;index<oldReportDataList.size();index++){
								ReportData reportData1 = new ReportData();
								ReportData reportData2 = new ReportData();

								reportData1 = oldReportDataList.get(index);
								ReportData mergeReportData = new ReportData();
								ReportData mergeReportData2 = new ReportData();
								mergeReportData = newReportDataList1.get(index);
								mergeReportData2 = newReportDataList2.get(index);
								mergeReportData.setD3(numAdd(mergeReportData.getD3(),reportData1.getD3()));
								mergeReportData.setD4(numAdd(mergeReportData.getD4(),reportData1.getD4()));

								//单位 万
								mergeReportData2.setD3(toUnit(mergeReportData.getD3()));
								mergeReportData2.setD4(toUnit(mergeReportData.getD4()));
							}
						}
					}else if(code.equals("6#2")){
						if(count==1){
							for(ReportData reportData : oldReportDataList){
								ReportData reportData1 = new ReportData();
								ReportData reportData2 = new ReportData();

								ReportDataId reportDataId = new ReportDataId();
								reportDataId.setCenterCode(centerCode);
								reportDataId.setBranchCode(centerCode);
								reportDataId.setAccBookType(reportData.getId().getAccBookType());
								reportDataId.setAccBookCode(reportData.getId().getAccBookCode());
								reportDataId.setReportCode(code);
								reportDataId.setVersion(reportData.getId().getVersion());
								reportDataId.setYearMonthDate(reportData.getId().getYearMonthDate());
								reportDataId.setUnit("1");
								reportDataId.setNumber(reportData.getId().getNumber());

								reportData1.setId(reportDataId);
								reportData1.setReportName(codeName);
								reportData1.setD1(reportData.getD1());
								reportData1.setD2(reportData.getD2());//d2-d14 有数据
								reportData1.setD3(reportData.getD3());
								reportData1.setD4(reportData.getD4());
								reportData1.setD5(reportData.getD5());
								reportData1.setD6(reportData.getD6());
								reportData1.setD7(reportData.getD7());
								reportData1.setD8(reportData.getD8());
								reportData1.setD9(reportData.getD9());
								reportData1.setD10(reportData.getD10());
								reportData1.setD11(reportData.getD11());
								reportData1.setD12(reportData.getD12());
								reportData1.setD13(reportData.getD13());
								reportData1.setD14(reportData.getD14());


								reportData1.setCreateBy(CurrentUser.getCurrentUser().getId()+"");
								reportData1.setCreateTime(CurrentTime.getCurrentTime());

								newReportDataList1.add(reportData1);
								//TODO 保存单位 万
								ReportDataId reportDataId2 = new ReportDataId();
								reportDataId2.setCenterCode(centerCode);
								reportDataId2.setBranchCode(centerCode);
								reportDataId2.setAccBookType(reportData.getId().getAccBookType());
								reportDataId2.setAccBookCode(reportData.getId().getAccBookCode());
								reportDataId2.setReportCode(code);
								reportDataId2.setVersion(reportData.getId().getVersion());
								reportDataId2.setYearMonthDate(reportData.getId().getYearMonthDate());
								reportDataId2.setUnit("2");
								reportDataId2.setNumber(reportData.getId().getNumber());

								reportData2.setId(reportDataId2);
								reportData2.setReportName(codeName);
								reportData2.setD1(reportData.getD1());
								reportData2.setD2(toUnit(reportData.getD2()));//转数字 相加后在转成 字符串
								reportData2.setD3(toUnit(reportData.getD3()));
								reportData2.setD4(toUnit(reportData.getD4()));
								reportData2.setD5(toUnit(reportData.getD5()));
								reportData2.setD6(toUnit(reportData.getD6()));
								reportData2.setD7(toUnit(reportData.getD7()));
								reportData2.setD8(toUnit(reportData.getD8()));
								reportData2.setD9(toUnit(reportData.getD9()));
								reportData2.setD10(toUnit(reportData.getD10()));
								reportData2.setD11(toUnit(reportData.getD11()));
								reportData2.setD12(toUnit(reportData.getD12()));
								reportData2.setD13(toUnit(reportData.getD13()));
								reportData2.setD14(toUnit(reportData.getD14()));

								reportData2.setCreateBy(CurrentUser.getCurrentUser().getId()+"");
								reportData2.setCreateTime(CurrentTime.getCurrentTime());
								newReportDataList2.add(reportData2);

							}
						}else{
							for(int index =0;index<oldReportDataList.size();index++){
								ReportData reportData1 = new ReportData();
								ReportData reportData2 = new ReportData();

								reportData1 = oldReportDataList.get(index);
								ReportData mergeReportData = new ReportData();
								ReportData mergeReportData2 = new ReportData();
								mergeReportData = newReportDataList1.get(index);
								mergeReportData2 = newReportDataList2.get(index);
								mergeReportData.setD2(numAdd(mergeReportData.getD2(),reportData1.getD2()));
								mergeReportData.setD3(numAdd(mergeReportData.getD3(),reportData1.getD3()));
								mergeReportData.setD4(numAdd(mergeReportData.getD4(),reportData1.getD4()));
								mergeReportData.setD5(numAdd(mergeReportData.getD5(),reportData1.getD5()));
								mergeReportData.setD6(numAdd(mergeReportData.getD6(),reportData1.getD6()));
								mergeReportData.setD7(numAdd(mergeReportData.getD7(),reportData1.getD7()));
								mergeReportData.setD8(numAdd(mergeReportData.getD8(),reportData1.getD8()));
								mergeReportData.setD9(numAdd(mergeReportData.getD9(),reportData1.getD9()));
								mergeReportData.setD10(numAdd(mergeReportData.getD10(),reportData1.getD10()));
								mergeReportData.setD11(numAdd(mergeReportData.getD11(),reportData1.getD11()));
								mergeReportData.setD12(numAdd(mergeReportData.getD12(),reportData1.getD12()));
								mergeReportData.setD13(numAdd(mergeReportData.getD13(),reportData1.getD13()));
								mergeReportData.setD14(numAdd(mergeReportData.getD14(),reportData1.getD14()));

								//单位 万
								mergeReportData2.setD2(toUnit(mergeReportData.getD2()));
								mergeReportData2.setD3(toUnit(mergeReportData.getD3()));
								mergeReportData2.setD4(toUnit(mergeReportData.getD4()));
								mergeReportData2.setD5(toUnit(mergeReportData.getD5()));
								mergeReportData2.setD6(toUnit(mergeReportData.getD6()));
								mergeReportData2.setD7(toUnit(mergeReportData.getD7()));
								mergeReportData2.setD8(toUnit(mergeReportData.getD8()));
								mergeReportData2.setD9(toUnit(mergeReportData.getD9()));
								mergeReportData2.setD10(toUnit(mergeReportData.getD10()));
								mergeReportData2.setD11(toUnit(mergeReportData.getD11()));
								mergeReportData2.setD12(toUnit(mergeReportData.getD12()));
								mergeReportData2.setD13(toUnit(mergeReportData.getD13()));
								mergeReportData2.setD14(toUnit(mergeReportData.getD14()));
							}
						}
					}else if(code.equals("7#2")){
					if(count==1){
						for(ReportData reportData : oldReportDataList){
							ReportData reportData1 = new ReportData();
							ReportData reportData2 = new ReportData();

							ReportDataId reportDataId = new ReportDataId();
							reportDataId.setCenterCode(centerCode);
							reportDataId.setBranchCode(centerCode);
							reportDataId.setAccBookType(reportData.getId().getAccBookType());
							reportDataId.setAccBookCode(reportData.getId().getAccBookCode());
							reportDataId.setReportCode(code);
							reportDataId.setVersion(reportData.getId().getVersion());
							reportDataId.setYearMonthDate(reportData.getId().getYearMonthDate());
							reportDataId.setUnit("1");
							reportDataId.setNumber(reportData.getId().getNumber());

							reportData1.setId(reportDataId);
							reportData1.setReportName(codeName);
							reportData1.setD1(reportData.getD1());
							reportData1.setD2(reportData.getD2());
							reportData1.setD3(reportData.getD3());
							reportData1.setD4(reportData.getD4());
							reportData1.setCreateBy(CurrentUser.getCurrentUser().getId()+"");
							reportData1.setCreateTime(CurrentTime.getCurrentTime());

							newReportDataList1.add(reportData1);
							//TODO 保存单位 万
							ReportDataId reportDataId2 = new ReportDataId();
							reportDataId2.setCenterCode(centerCode);
							reportDataId2.setBranchCode(centerCode);
							reportDataId2.setAccBookType(reportData.getId().getAccBookType());
							reportDataId2.setAccBookCode(reportData.getId().getAccBookCode());
							reportDataId2.setReportCode(code);
							reportDataId2.setVersion(reportData.getId().getVersion());
							reportDataId2.setYearMonthDate(reportData.getId().getYearMonthDate());
							reportDataId2.setUnit("2");
							reportDataId2.setNumber(reportData.getId().getNumber());

							reportData2.setId(reportDataId2);
							reportData2.setReportName(codeName);
							reportData2.setD1(reportData.getD1());
							reportData2.setD2(reportData.getD2());//转数字 相加后在转成 字符串
							reportData2.setD3(toUnit(reportData.getD3()));
							reportData2.setD4(toUnit(reportData.getD4()));
							reportData2.setCreateBy(CurrentUser.getCurrentUser().getId()+"");
							reportData2.setCreateTime(CurrentTime.getCurrentTime());
							newReportDataList2.add(reportData2);

						}
					}else{
						for(int index =0;index<oldReportDataList.size();index++){
							ReportData reportData1 = new ReportData();
							ReportData reportData2 = new ReportData();

							reportData1 = oldReportDataList.get(index);
							ReportData mergeReportData = new ReportData();
							ReportData mergeReportData2 = new ReportData();
							mergeReportData = newReportDataList1.get(index);
							mergeReportData2 = newReportDataList2.get(index);
							mergeReportData.setD3(numAdd(mergeReportData.getD3(),reportData1.getD3()));//转数字 相加后在转成 字符串
							mergeReportData.setD4(numAdd(mergeReportData.getD4(),reportData1.getD4()));

							//单位 万
							mergeReportData2.setD4(toUnit(mergeReportData.getD4()));//转数字 相加后在转成 字符串
							mergeReportData2.setD3(toUnit(mergeReportData.getD3()));
						}
					}
				}
					count++;
				}
				//保存
				if(newReportDataList1!=null && newReportDataList1.size()>0){
					reportDataRepository.saveAll(newReportDataList1);
					reportDataRepository.saveAll(newReportDataList2);
					reportDataRepository.flush();
				}
			}
		}
		return "SUCCESS";
    }

	/**
	 * 对单位做转换
	 * @param d2
	 * @return
	 */
	private String toUnit(String d2) {
		String str ;
		if (d2!=null && !d2 .equals("")){
			str= new BigDecimal(d2).divide(new BigDecimal("10000.00"), Integer.parseInt("6"), BigDecimal.ROUND_HALF_UP).toString();
		}else{
			str = "";
		}
		return str;
	}

	/**
	 * //转数字 相加后在转成 字符串
	 * @param d2
	 * @param d21
	 * @return
	 */
	private String numAdd(String d2, String d21) {
		String str ="";
		if (d2!=null && !d2 .equals("")){
			BigDecimal decimal= new BigDecimal(d2);
			BigDecimal decima2 = new BigDecimal(d21);
			decimal = decimal.add(decima2);
			str =decimal.toString();
		}
		return str;
	}


	@Override
	@Transactional
	public String computeAll(ReportDataDTO dto){
		return null;
	}

	private void computeByCol(int col, String formula, String formulaK, String formulaV, ReportData reportData, ReportData reportData2){
		System.out.println("正在计算报表："+reportData.getId().getReportCode()+"-"+reportData.getId().getNumber()+"行-"+col+"列");
		System.out.println("D:"+formula+"，K:"+formulaK+"，V:"+formulaV);

		String yearMonthDate = reportData.getId().getYearMonthDate();
		String reportCode = reportData.getId().getReportCode();
		String version = reportData.getId().getVersion();

		//拆分配置计算参数规则
		String[] formulaVs = formulaV.split(";");//计算参数取数规则
		BigDecimal[] paramValues = new BigDecimal[formulaVs.length];//计算参数值
		BigDecimal[] paramValues2 = new BigDecimal[formulaVs.length];//计算参数值

        //数据精度暂默认为2位，万元6位
        String DBDecimals = "2";
        String DBDecimals2 = "6";
		String DBDecimals3 = "4";
        boolean devFlag = false;
		if (formula.contains("/")) {
			System.out.println("包含除法");
			devFlag = true;
		}

		for (int i=0;i<formulaVs.length;i++) {
			String[] formulas = formulaVs[i].trim().split("_");
			if ("$1".equals(formulas[0].trim()) || "$3".equals(formulas[0].trim())) {//普通计算和特殊计算
				//$1_账套编码_数据类型_科目代码_取数期间_科目余额方向_发生方向
				//$3_账套编码_数据类型_科目代码_取数期间_科目余额方向_发生方向
				StringBuffer sql = new StringBuffer();

				List<?> list = jointFormulaSql(sql, formulas, yearMonthDate);

//				List<?> list = reportComputeRepository.queryBySqlSC(sql.toString());
				if (list!=null&&list.size()>0) {
					BigDecimal needData = (BigDecimal) ((Map) list.get(0)).get("needData");
					if ("$1".equals(formulas[0].trim())) {
						if ("1".equals(formulas[5].trim())) {
							paramValues[i] = needData;
                            paramValues2[i] = needData.divide(new BigDecimal("10000.00"), Integer.parseInt(DBDecimals2), BigDecimal.ROUND_HALF_UP);
						} else if ("2".equals(formulas[5].trim())){//取反
							paramValues[i] = needData.negate();
                            paramValues2[i] = needData.negate().divide(new BigDecimal("10000.00"), Integer.parseInt(DBDecimals2), BigDecimal.ROUND_HALF_UP);
						} else {
							throw new RuntimeException("报表："+reportData.getId().getReportCode()+"第"+reportData.getId().getNumber()+"行"+col+"列,配置错误！");
						}
					} else if ("$3".equals(formulas[0].trim())) {
						if ("1".equals(formulas[5].trim())) {//如果是1，为正数则相加，否则舍去
							if (needData.compareTo(new BigDecimal("0.00"))>=0) {
								paramValues[i] = needData;
                                paramValues2[i] = needData.divide(new BigDecimal("10000.00"), Integer.parseInt(DBDecimals2), BigDecimal.ROUND_HALF_UP);
							} else {
								paramValues[i] = new BigDecimal("0.00");
                                paramValues2[i] = new BigDecimal("0.00");
							}
						} else if ("2".equals(formulas[5].trim())){//如果是2，为负数则相加，否则舍去
							if (needData.compareTo(new BigDecimal("0.00"))<=0) {
								paramValues[i] = needData.abs();
                                paramValues2[i] = needData.abs().divide(new BigDecimal("10000.00"), Integer.parseInt(DBDecimals2), BigDecimal.ROUND_HALF_UP);
							} else {
								paramValues[i] = new BigDecimal("0.00");
                                paramValues2[i] = new BigDecimal("0.00");
							}
						} else {
							throw new RuntimeException("报表："+reportData.getId().getReportCode()+"第"+reportData.getId().getNumber()+"行"+col+"列,配置错误！");
						}
					}
				} else {
					throw new RuntimeException("报表："+reportData.getId().getReportCode()+"第"+reportData.getId().getNumber()+"行"+col+"列,配置错误！");
				}
			} else if ("$2".equals(formulas[0].trim())) {
				//本表计算
				//$2_账套编码_本表位置(行,对应字段number)_本表位置(列,对应字段d系列)

				List<?> list = jointQryThisTableSql(formulas[3].trim(),formulas[1].trim(),reportCode,version,yearMonthDate,"1",formulas[2].trim());
				if (list!=null&&list.size()>0) {
					//本表取出数据时字符串类型的
					String needData = (String) ((Map) list.get(0)).get("needData");
					if (needData!=null&&!"".equals(needData)) {
						paramValues[i] = new BigDecimal(needData);
                        paramValues2[i] = new BigDecimal(needData).divide(new BigDecimal("10000.00"), Integer.parseInt(DBDecimals2), BigDecimal.ROUND_HALF_UP);
					} else {
                        paramValues[i] = new BigDecimal("0.00");
                        paramValues2[i] = new BigDecimal("0.00");
                    }
				} else {
					throw new RuntimeException("报表："+reportData.getId().getReportCode()+"第"+reportData.getId().getNumber()+"行"+col+"列,配置错误！");
				}
			} else if ("$5".equals(formulas[0].trim())) {
				//$5 表示是同一表格不同统计月度的取数（eg：取上月某个单元格位置的数）
				//$5_表位置(行,对应字段number)_表位置(列,对应字段d系列)_取数期间
				int paramsNo = 1;
				Map<Integer,Object> params = new HashMap<>();
				StringBuffer sql = new StringBuffer();
//				sql.append("SELECT r."+formulas[2].trim()+" AS needData FROM reportdata r WHERE 1=1");
				sql.append("SELECT r.?"+paramsNo+" AS needData FROM reportdata r WHERE 1=1");
				params.put(paramsNo,formulas[2].trim());
				paramsNo++;
//				sql.append(" AND r.center_code = '"+CurrentUser.getCurrentLoginManageBranch()+"'");
				sql.append(" AND r.center_code = ?"+paramsNo );
				params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
				paramsNo++;
//				sql.append(" AND r.acc_book_code = '"+reportData.getId().getAccBookCode()+"'");
				sql.append(" AND r.acc_book_code = ?"+paramsNo );
				params.put(paramsNo,reportData.getId().getAccBookCode());
				paramsNo++;
//				sql.append(" AND r.report_code = '"+reportData.getId().getReportCode()+"'");
				sql.append(" AND r.report_code = ?"+paramsNo );
				params.put(paramsNo,reportData.getId().getReportCode());
				paramsNo++;
//				sql.append(" AND r.version = '"+reportData.getId().getVersion()+"'");
				sql.append(" AND r.version = ?"+paramsNo );
				params.put(paramsNo,reportData.getId().getVersion());
				paramsNo++;
				if ("LM".equals(formulas[3].trim())) {
					String newYMD = reportData.getId().getYearMonthDate();
					if ("01".equals(newYMD.substring(4))) {
						newYMD = Integer.valueOf(newYMD.substring(0,4))-1+"12";
					} else {
						newYMD = Integer.valueOf(newYMD)-1+"";
					}
//					sql.append(" AND r.year_month_date = '"+newYMD+"'");
					sql.append(" AND r.year_month_date = ?"+paramsNo );
					params.put(paramsNo,newYMD);
					paramsNo++;
				} else {
					sql.append(" AND r.year_month_date = ?"+paramsNo);
					params.put(paramsNo,"");
					paramsNo++;
				}
				sql.append(" AND r.unit = ?"+paramsNo );
				params.put(paramsNo,reportData.getId().getUnit());
				paramsNo++;
				sql.append(" AND r.number = ?"+paramsNo);
				params.put(paramsNo,formulas[1].trim());
				paramsNo++;

                List<?> list = reportComputeRepository.queryBySqlSC(sql.toString(),params);
                if (list!=null&&list.size()>0) {
                    //报表取出的数据时字符串类型的
                    String needData = (String) ((Map) list.get(0)).get("needData");
                    if (needData!=null&&!"".equals(needData)) {
                        paramValues[i] = new BigDecimal(needData);
                        paramValues2[i] = new BigDecimal(needData).divide(new BigDecimal("10000.00"), Integer.parseInt(DBDecimals2), BigDecimal.ROUND_HALF_UP);
                    } else {
                        paramValues[i] = new BigDecimal("0.00");
                        paramValues2[i] = new BigDecimal("0.00");
                    }
                } else {
                    throw new RuntimeException("报表："+reportData.getId().getReportCode()+"第"+reportData.getId().getNumber()+"行"+col+"列,配置错误！");
                }
            } else if ("SQL".equals(formulas[0].trim())) {
				String sql = formulaVs[i].trim();
				sql = sql.substring(sql.indexOf("_")+1);

                sql = jointFormulaSqlBySQL(sql, yearMonthDate);

				List<?> list = reportComputeRepository.queryBySqlSC(sql);
				if (list!=null&&list.size()>0) {
					BigDecimal needData = (BigDecimal) ((Map) list.get(0)).get("needData");
					paramValues[i] = needData;
                    paramValues2[i] = needData.divide(new BigDecimal("10000.00"), Integer.parseInt(DBDecimals2), BigDecimal.ROUND_HALF_UP);
				} else {
					throw new RuntimeException("报表："+reportData.getId().getReportCode()+"第"+reportData.getId().getNumber()+"行"+col+"列,配置错误！");
				}
			} else if ("$4".equals(formulas[0].trim())) {
				try {
					paramValues[i] = new BigDecimal(formulas[1]);
                    paramValues2[i] = new BigDecimal(formulas[1]).divide(new BigDecimal("10000.00"), Integer.parseInt(DBDecimals2), BigDecimal.ROUND_HALF_UP);
				} catch (Exception e) {
					throw new RuntimeException("报表："+reportData.getId().getReportCode()+"第"+reportData.getId().getNumber()+"行"+col+"列,配置错误！");
				}
			}
		}

		//计算规则、计算参数、计算参数值已准备完毕，可以开始计算了
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("js");
        ScriptEngine engine2 = manager.getEngineByName("js");
		String[] formulaKs = formulaK.split(",");//计算参数
		for (int i=0;i<formulaKs.length;i++){
			System.out.println(formulaKs[i].trim()+":"+paramValues[i].toString());
			engine.put(formulaKs[i].trim(), paramValues[i]);
            engine2.put(formulaKs[i].trim(), paramValues2[i]);
		}
		System.out.println("计算公式："+formula);
		Object result = null;
        Object result2 = null;
		try{
			result = engine.eval(formula);
            if (devFlag) {
				result2 = engine2.eval(formula);
			}
		}catch(ScriptException e){
			//报表编码+行+列
			throw new RuntimeException("报表："+reportData.getId().getReportCode()+"第"+reportData.getId().getNumber()+"行"+col+"列,参数的计算规则配置错误！");
		}
		/*String value = String.valueOf(result);
		if("Jama.Matrix".equals(result.getClass().getName())){
			value=String.valueOf(((Matrix)result).get(0,0));
		}*/

		BigDecimal resultData = new BigDecimal("0.00");
        BigDecimal resultData2 = new BigDecimal("0.00");
		System.out.println("报表"+reportData.getId().getReportCode()+","+reportData.getId().getNumber()+"行"+col + "列,计算结果为："+result);
		if (result!=null&&!"NaN".equals(result.toString())&&!"-Infinity".equals(result.toString())&&!"Infinity".equals(result.toString())){
			try {
				resultData = new BigDecimal(String.valueOf(result));
				if (devFlag) {
					resultData2 = new BigDecimal(String.valueOf(result2));
				}
			} catch (Exception e) {
				throw new RuntimeException("报表"+reportData.getId().getReportCode()+","+reportData.getId().getNumber()+"行"+col + "列,计算结果("+result+")数据转换错误！");
			}
		}

		if (devFlag && formulaKs.length==2) {
			setDCol(col, reportData, resultData.setScale(Integer.parseInt(DBDecimals3), BigDecimal.ROUND_HALF_UP));
		} else {
			setDCol(col, reportData, resultData.setScale(Integer.parseInt(DBDecimals), BigDecimal.ROUND_HALF_UP));
		}
        if (devFlag) {
			setDCol(col, reportData2, resultData2.setScale(Integer.parseInt(DBDecimals2), BigDecimal.ROUND_HALF_UP));
		} else {
			resultData2 = resultData.divide(new BigDecimal("10000.00"), Integer.parseInt(DBDecimals2), BigDecimal.ROUND_HALF_UP);
			setDCol(col, reportData2, resultData2);
		}
	}

	private void computeByCol(String yearMonthDate, List<Integer> leverList, Map<Integer, Map<String, List<String>>> leverComputeAndKAndVMap, ReportCompute reportCompute){
		Collections.sort(leverList);//排序
		String accBookCode = CurrentUser.getCurrentLoginAccount();
		for (Integer lever : leverList) {
			//同一优先级的
			Map<String, List<String>> leverMap = leverComputeAndKAndVMap.get(lever);
			//通过Map.entrySet遍历key和value
			for (Map.Entry<String, List<String>> map : leverMap.entrySet()) {
				String[] rowCol = map.getKey().split("#");
				//List集合的第一项为计算规则，第二项为计算参数，第三项为计算参数取数规则
				List<String> formulaList = map.getValue();

				//获取对应报表对应行的数据
				ReportDataId reportDataId = new ReportDataId();
//				reportDataId.setCenterCode(reportCompute.getId().getCenterCode());
				reportDataId.setCenterCode(CurrentUser.getCurrentLoginManageBranch());
				reportDataId.setBranchCode(reportCompute.getId().getBranchCode());
				reportDataId.setAccBookType(reportCompute.getId().getAccBookType());
				reportDataId.setAccBookCode(accBookCode);
				reportDataId.setReportCode(reportCompute.getId().getReportCode());
				reportDataId.setVersion(reportCompute.getId().getVersion());
				reportDataId.setNumber(Integer.valueOf(rowCol[0]));
				reportDataId.setYearMonthDate(yearMonthDate);
				reportDataId.setUnit("1");

                ReportDataId reportDataId2 = new ReportDataId();
//                reportDataId2.setCenterCode(reportCompute.getId().getCenterCode());
				reportDataId2.setCenterCode(CurrentUser.getCurrentLoginManageBranch());
                reportDataId2.setBranchCode(reportCompute.getId().getBranchCode());
                reportDataId2.setAccBookType(reportCompute.getId().getAccBookType());
                reportDataId2.setAccBookCode(accBookCode);
                reportDataId2.setReportCode(reportCompute.getId().getReportCode());
                reportDataId2.setVersion(reportCompute.getId().getVersion());
                reportDataId2.setNumber(Integer.valueOf(rowCol[0]));
                reportDataId2.setYearMonthDate(yearMonthDate);
                reportDataId2.setUnit("2");
				ReportData reportData =  null;
                ReportData reportData2 =  null;
				try {
					reportData = reportDataRepository.findById(reportDataId).get();
                    reportData2 = reportDataRepository.findById(reportDataId2).get();
				} catch (Exception e) {
					throw new RuntimeException("账套"+reportDataId.getAccBookCode()+"下未找到报表"+reportDataId.getReportCode()+"第"+reportDataId.getNumber()+"行数据");
				}
				//对未计算列进行计算
				//computeByCol(int col, String formula, String formulaK, String formulaV, ReportData reportData);
				computeByCol(Integer.valueOf(rowCol[1]), formulaList.get(0), formulaList.get(1), formulaList.get(2), reportData, reportData2);

				reportDataRepository.save(reportData);
                reportDataRepository.save(reportData2);
				reportDataRepository.flush();
			}
		}
	}

	private String getDCol(int i, ReportCompute reportCompute){
		switch (i) {
			case 1:
				return reportCompute.getD1();
			case 2:
				return reportCompute.getD2();
			case 3:
				return reportCompute.getD3();
			case 4:
				return reportCompute.getD4();
			case 5:
				return reportCompute.getD5();
			case 6:
				return reportCompute.getD6();
			case 7:
				return reportCompute.getD7();
			case 8:
				return reportCompute.getD8();
			case 9:
				return reportCompute.getD9();
			case 10:
				return reportCompute.getD10();
			case 11:
				return reportCompute.getD11();
			case 12:
				return reportCompute.getD12();
			case 13:
				return reportCompute.getD13();
			case 14:
				return reportCompute.getD14();
			case 15:
				return reportCompute.getD15();
			case 16:
				return reportCompute.getD16();
			case 17:
				return reportCompute.getD17();
			case 18:
				return reportCompute.getD18();
			case 19:
				return reportCompute.getD19();
			case 20:
				return reportCompute.getD20();
			case 21:
				return reportCompute.getD21();
			case 22:
				return reportCompute.getD22();
			case 23:
				return reportCompute.getD23();
			case 24:
				return reportCompute.getD24();
			case 25:
				return reportCompute.getD25();
			case 26:
				return reportCompute.getD26();
			case 27:
				return reportCompute.getD27();
			case 28:
				return reportCompute.getD28();
			case 29:
				return reportCompute.getD29();
			default :
				return reportCompute.getD30();
		}
	}

	private String getDColK(int i, ReportCompute reportCompute){
		switch (i) {
			case 1:
				return reportCompute.getD1K();
			case 2:
				return reportCompute.getD2K();
			case 3:
				return reportCompute.getD3K();
			case 4:
				return reportCompute.getD4K();
			case 5:
				return reportCompute.getD5K();
			case 6:
				return reportCompute.getD6K();
			case 7:
				return reportCompute.getD7K();
			case 8:
				return reportCompute.getD8K();
			case 9:
				return reportCompute.getD9K();
			case 10:
				return reportCompute.getD10K();
			case 11:
				return reportCompute.getD11K();
			case 12:
				return reportCompute.getD12K();
			case 13:
				return reportCompute.getD13K();
			case 14:
				return reportCompute.getD14K();
			case 15:
				return reportCompute.getD15K();
			case 16:
				return reportCompute.getD16K();
			case 17:
				return reportCompute.getD17K();
			case 18:
				return reportCompute.getD18K();
			case 19:
				return reportCompute.getD19K();
			case 20:
				return reportCompute.getD20K();
			case 21:
				return reportCompute.getD21K();
			case 22:
				return reportCompute.getD22K();
			case 23:
				return reportCompute.getD23K();
			case 24:
				return reportCompute.getD24K();
			case 25:
				return reportCompute.getD25K();
			case 26:
				return reportCompute.getD26K();
			case 27:
				return reportCompute.getD27K();
			case 28:
				return reportCompute.getD28K();
			case 29:
				return reportCompute.getD29K();
			default :
				return reportCompute.getD30K();
		}
	}

	private String getDColV(int i, ReportCompute reportCompute){
		switch (i) {
			case 1:
				return reportCompute.getD1V();
			case 2:
				return reportCompute.getD2V();
			case 3:
				return reportCompute.getD3V();
			case 4:
				return reportCompute.getD4V();
			case 5:
				return reportCompute.getD5V();
			case 6:
				return reportCompute.getD6V();
			case 7:
				return reportCompute.getD7V();
			case 8:
				return reportCompute.getD8V();
			case 9:
				return reportCompute.getD9V();
			case 10:
				return reportCompute.getD10V();
			case 11:
				return reportCompute.getD11V();
			case 12:
				return reportCompute.getD12V();
			case 13:
				return reportCompute.getD13V();
			case 14:
				return reportCompute.getD14V();
			case 15:
				return reportCompute.getD15V();
			case 16:
				return reportCompute.getD16V();
			case 17:
				return reportCompute.getD17V();
			case 18:
				return reportCompute.getD18V();
			case 19:
				return reportCompute.getD19V();
			case 20:
				return reportCompute.getD20V();
			case 21:
				return reportCompute.getD21V();
			case 22:
				return reportCompute.getD22V();
			case 23:
				return reportCompute.getD23V();
			case 24:
				return reportCompute.getD24V();
			case 25:
				return reportCompute.getD25V();
			case 26:
				return reportCompute.getD26V();
			case 27:
				return reportCompute.getD27V();
			case 28:
				return reportCompute.getD28V();
			case 29:
				return reportCompute.getD29V();
			default :
				return reportCompute.getD30V();
		}
	}

	private void setDCol(int i, ReportData reportData, BigDecimal resultData){
		switch (i) {
			case 1:
				reportData.setD1(resultData.toString());
				break;
			case 2:
				reportData.setD2(resultData.toString());
				break;
			case 3:
				reportData.setD3(resultData.toString());
				break;
			case 4:
				reportData.setD4(resultData.toString());
				break;
			case 5:
				reportData.setD5(resultData.toString());
				break;
			case 6:
				reportData.setD6(resultData.toString());
				break;
			case 7:
				reportData.setD7(resultData.toString());
				break;
			case 8:
				reportData.setD8(resultData.toString());
				break;
			case 9:
				reportData.setD9(resultData.toString());
				break;
			case 10:
				reportData.setD10(resultData.toString());
				break;
			case 11:
				reportData.setD11(resultData.toString());
				break;
			case 12:
				reportData.setD12(resultData.toString());
				break;
			case 13:
				reportData.setD13(resultData.toString());
				break;
			case 14:
				reportData.setD14(resultData.toString());
				break;
			case 15:
				reportData.setD15(resultData.toString());
				break;
			case 16:
				reportData.setD16(resultData.toString());
				break;
			case 17:
				reportData.setD17(resultData.toString());
				break;
			case 18:
				reportData.setD18(resultData.toString());
				break;
			case 19:
				reportData.setD19(resultData.toString());
				break;
			case 20:
				reportData.setD20(resultData.toString());
				break;
			case 21:
				reportData.setD21(resultData.toString());
				break;
			case 22:
				reportData.setD22(resultData.toString());
				break;
			case 23:
				reportData.setD23(resultData.toString());
				break;
			case 24:
				reportData.setD24(resultData.toString());
				break;
			case 25:
				reportData.setD25(resultData.toString());
				break;
			case 26:
				reportData.setD26(resultData.toString());
				break;
			case 27:
				reportData.setD27(resultData.toString());
				break;
			case 28:
				reportData.setD28(resultData.toString());
				break;
			case 29:
				reportData.setD29(resultData.toString());
				break;
			default :
				reportData.setD30(resultData.toString());
		}
	}

	private void setDCol(int i, ReportData reportData, String arg){
		switch (i) {
			case 1:
				reportData.setD1(arg);
				break;
			case 2:
				reportData.setD2(arg);
				break;
			case 3:
				reportData.setD3(arg);
				break;
			case 4:
				reportData.setD4(arg);
				break;
			case 5:
				reportData.setD5(arg);
				break;
			case 6:
				reportData.setD6(arg);
				break;
			case 7:
				reportData.setD7(arg);
				break;
			case 8:
				reportData.setD8(arg);
				break;
			case 9:
				reportData.setD9(arg);
				break;
			case 10:
				reportData.setD10(arg);
				break;
			case 11:
				reportData.setD11(arg);
				break;
			case 12:
				reportData.setD12(arg);
				break;
			case 13:
				reportData.setD13(arg);
				break;
			case 14:
				reportData.setD14(arg);
				break;
			case 15:
				reportData.setD15(arg);
				break;
			case 16:
				reportData.setD16(arg);
				break;
			case 17:
				reportData.setD17(arg);
				break;
			case 18:
				reportData.setD18(arg);
				break;
			case 19:
				reportData.setD19(arg);
				break;
			case 20:
				reportData.setD20(arg);
				break;
			case 21:
				reportData.setD21(arg);
				break;
			case 22:
				reportData.setD22(arg);
				break;
			case 23:
				reportData.setD23(arg);
				break;
			case 24:
				reportData.setD24(arg);
				break;
			case 25:
				reportData.setD25(arg);
				break;
			case 26:
				reportData.setD26(arg);
				break;
			case 27:
				reportData.setD27(arg);
				break;
			case 28:
				reportData.setD28(arg);
				break;
			case 29:
				reportData.setD29(arg);
				break;
			default :
				reportData.setD30(arg);
		}
	}

	private List<?> jointQryThisTableSql(String col, String accBookCode, String reportCode, String version, String yearMonthDate, String unit, String number){
		StringBuffer sql = new StringBuffer();
		int paramsNo = 1;
		Map<Integer,Object> params = new HashMap<>();
		sql.append("SELECT r."+col+" AS needData FROM reportdata r WHERE 1=1");

		sql.append(" AND r.center_code = ?"+paramsNo );
		params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
		paramsNo++;
		/*sql.append(" AND r.branch_code = ?"+paramsNo );
		params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
		paramsNo++;*/

		sql.append(" AND r.acc_book_code = ?"+paramsNo );
		params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
		paramsNo++;
		String newReportCode = reportCode;
		if (reportCode!=null&&!"".equals(reportCode)) {
			int num = accBookCode.split(",").length;
			String bAndc = reportAccountB+","+reportAccountC;
			if ("3#2".equals(reportCode)||"3#3".equals(reportCode)) {
				if (num==1) {
					newReportCode = "3#1";
				} else if (num==2 && bAndc.equals(reportCode)) {
					newReportCode = "3#2";
				}
			} else if ("4#2".equals(reportCode)||"4#3".equals(reportCode)) {
				if (num==1) {
					newReportCode = "4#1";
				} else if (num==2 && bAndc.equals(reportCode)) {
					newReportCode = "4#2";
				}
			} else if ("5#2".equals(reportCode)||"5#3".equals(reportCode)) {
				if (num==1) {
					newReportCode = "5#1";
				} else if (num==2 && bAndc.equals(reportCode)) {
					newReportCode = "5#2";
				}
			} else if ("6#2".equals(reportCode)||"6#3".equals(reportCode)) {
				if (num==1) {
					newReportCode = "6#1";
				} else if (num==2 && bAndc.equals(reportCode)) {
					newReportCode = "6#2";
				}
			}
		}
		sql.append(" AND r.report_code = ?"+paramsNo );
		params.put(paramsNo,newReportCode);
		paramsNo++;
		sql.append(" AND r.version = ?"+paramsNo );
		params.put(paramsNo,version);
		paramsNo++;
		sql.append(" AND r.year_month_date = ?"+paramsNo );
		params.put(paramsNo,yearMonthDate);
		paramsNo++;
		sql.append(" AND r.unit = ?"+paramsNo );
		params.put(paramsNo,unit);
		paramsNo++;
		sql.append(" AND r.number = ?"+paramsNo );
		params.put(paramsNo,number);
		paramsNo++;

		return reportComputeRepository.queryBySqlSC(sql.toString(),params);
	}

	private List<ReportCompute> getReportComputeList(ReportDataDTO dto){
//		String accBookCode = CurrentUser.getCurrentLoginAccount();
		String accBookCode = "100100001";
		String version = "1";
		if (dto.getAccBookCode()!=null&&!"".equals(dto.getAccBookCode())) {
			accBookCode = dto.getAccBookCode();
		}
		if (dto.getVersion()!=null&&!"".equals(dto.getVersion())) {
			version = dto.getVersion();
		}
		if (!(dto.getReportCode()!=null&&!"".equals(dto.getReportCode()))) {
			dto.setReportCode(dto.getJJreportType()+"#"+dto.getJJreportName());
		}

		List<ReportCompute> reportComputeList = new ArrayList<>();
		if (dto.getNeedAccBookCode()!=null&&"Y".equals(dto.getNeedAccBookCode())) {
			reportComputeList = reportComputeRepository.findAll(new CusSpecification<>().and(
					CusSpecification.Cnd.like("id.accBookCode", accBookCode),
					CusSpecification.Cnd.eq("id.reportCode", dto.getReportCode()),
					CusSpecification.Cnd.eq("id.version", version)).asc("computeLever").asc("id.number"));
		} else {
			reportComputeList = reportComputeRepository.findAll(new CusSpecification<>().and(
					CusSpecification.Cnd.eq("id.reportCode", dto.getReportCode()),
					CusSpecification.Cnd.eq("id.version", version)).asc("computeLever").asc("id.number"));
		}
		return  reportComputeList;
	}
	@Override
	public Map<String, BigDecimal> getAccountData(String accBookCode,String yearMonthDate) {
		//判断000002是否已结转
		String centerCode = CurrentUser.getCurrentLoginManageBranch();
		String branchCode = CurrentUser.getCurrentLoginManageBranch();
		Boolean bolean=detailAccountService.whetherCarryForward(centerCode,"04",accBookCode,yearMonthDate);
		Map<String,BigDecimal> map=new HashMap<>();
		if(bolean){
			//已结转 访问当月明细账余额表历史表
			//1133
			BigDecimal balanceDest1133= reportDataRepository.getAccdetailhis(accBookCode,yearMonthDate,"1133", centerCode);
			if(balanceDest1133==null){balanceDest1133=new BigDecimal("0.00");}
			map.put("balanceDest1133",balanceDest1133);
			BigDecimal balanceDest2207= reportDataRepository.getAccdetailhis(accBookCode,yearMonthDate,"2207", centerCode);
			if(balanceDest2207==null){balanceDest2207=new BigDecimal("0.00");}
			balanceDest2207=balanceDest2207.multiply(new BigDecimal(-1));
			map.put("balanceDest2207",balanceDest2207);
			BigDecimal balanceDest4002= reportDataRepository.getAccdetailhis(accBookCode,yearMonthDate,"4002", centerCode);
			if(balanceDest4002==null){balanceDest4002=new BigDecimal("0.00");}
			balanceDest4002=balanceDest4002.multiply(new BigDecimal(-1));
			map.put("balanceDest4002",balanceDest4002);

		}else{
			//未结转 当月明细账余额表
			BigDecimal balanceDest1133= reportDataRepository.getAccdetail(accBookCode,yearMonthDate,"1133", centerCode);
			if(balanceDest1133==null){balanceDest1133=new BigDecimal("0.00");}
			map.put("balanceDest1133",balanceDest1133);
			BigDecimal balanceDest2207= reportDataRepository.getAccdetail(accBookCode,yearMonthDate,"2207", centerCode);
			if(balanceDest2207==null){balanceDest2207=new BigDecimal("0.00");}
			balanceDest2207=balanceDest2207.multiply(new BigDecimal(-1));
			map.put("balanceDest2207",balanceDest2207);
			BigDecimal balanceDest4002= reportDataRepository.getAccdetail(accBookCode,yearMonthDate,"4002", centerCode);
			if(balanceDest4002==null){balanceDest4002=new BigDecimal("0.00");}
			balanceDest4002=balanceDest4002.multiply(new BigDecimal(-1));
			map.put("balanceDest4002",balanceDest4002);
		}
		return map;
	}

	//判断科目是否存在
    private boolean isExistSubject(String subjectCode) {
	    boolean b = false;
	    int paramsNo = 1;
	    Map<Integer,Object> params = new HashMap<>();
        StringBuffer sql = new StringBuffer("select * from subjectinfo where useflag = '1' and account in ('000002', '000003') ");
        sql.append(" and CONCAT(all_subject,subject_code,'/') = ?" +  paramsNo );
        params.put(paramsNo,subjectCode);
        paramsNo++;
        List list = reportComputeRepository.queryBySqlSC(sql.toString(),params);
        if(list != null && !list.isEmpty()) b = true;
	    return b;
    }

	//获取上个月会计期间
	public String getLastYearMonth(String yearMonth){
		String last_yearMonth = "";
		int year = Integer.valueOf(yearMonth.substring(0, 4));
		int month = Integer.valueOf(yearMonth.substring(4).replaceAll("0", ""));
		if(month - 1 == 0){
			/*last_yearMonth = (year - 1) + "JS";*/
			last_yearMonth = (year - 1) + "14";
		}else{
			last_yearMonth = year + ((month - 1) > 9 ? ("" + (month - 1)) : ("0" + (month - 1)));
		}
		return last_yearMonth;
	}

	//获取去年同期会计期间
	public String getLastYearYearMonth(String yearMonth){
		String year = yearMonth.substring(0, 4);
		String month = yearMonth.substring(4);
		return (Integer.valueOf(year) - 1) + month;
	}

    /**
     * 报表自定义公式初步校验
     * @param reportCode 报表编码
     * @param formula 计算公式
     * @param formulaK 计算参数
     * @param formulaV 参数取数规则
     * @return
     */
    @Override
	public String checkFormula(String reportCode, String formula, String formulaK, String formulaV){
        String yearMonthDate = "201911";
        String version = "1";

        //拆分配置计算参数规则
        String[] formulaVs = formulaV.split(";");//计算参数取数规则
        BigDecimal[] paramValues = new BigDecimal[formulaVs.length];//计算参数值

        for (int i=0;i<formulaVs.length;i++) {
            String[] formulas = formulaVs[i].trim().split("_");
            if ("$1".equals(formulas[0].trim()) || "$3".equals(formulas[0].trim())) {//普通计算和特殊计算
                //$1_账套编码_数据类型_科目代码_取数期间_科目余额方向_发生方向
                //$3_账套编码_数据类型_科目代码_取数期间_科目余额方向_发生方向
                StringBuffer sql = new StringBuffer();

				List<?> list = jointFormulaSql(sql, formulas, yearMonthDate);

//				List<?> list = reportComputeRepository.queryBySqlSC(sql.toString());
                if (list!=null&&list.size()>0) {
                    BigDecimal needData = (BigDecimal) ((Map) list.get(0)).get("needData");
                    if ("$1".equals(formulas[0].trim())) {
                        if ("1".equals(formulas[5].trim())) {
                            paramValues[i] = needData;
                        } else if ("2".equals(formulas[5].trim())){//取反
                            paramValues[i] = needData.negate();
                        } else {
							return "第"+(i+1)+"个因子配置错误！";
                        }
                    } else if ("$3".equals(formulas[0].trim())) {
                        if ("1".equals(formulas[5].trim())) {//如果是1，为正数则相加，否则舍去
                            if (needData.compareTo(new BigDecimal("0.00"))>=0) {
                                paramValues[i] = needData;
                            } else {
                                paramValues[i] = new BigDecimal("0.00");
                            }
                        } else if ("2".equals(formulas[5].trim())){//如果是2，为负数则相加，否则舍去
                            if (needData.compareTo(new BigDecimal("0.00"))<=0) {
                                paramValues[i] = needData;
                            } else {
                                paramValues[i] = new BigDecimal("0.00");
                            }
                        } else {
							return "第"+(i+1)+"个因子配置错误！";
                        }
                    }
                } else {
					return "第"+(i+1)+"个因子配置错误！";
                }
            } else if ("$2".equals(formulas[0].trim())) {
                //本表计算
                //$2_账套编码_本表位置(行,对应字段number)_本表位置(列,对应字段d系列)
                List<?> list = jointQryThisTableSql(formulas[3].trim(),formulas[1].trim(),reportCode,version,yearMonthDate,"1",formulas[2].trim());
                if (list!=null&&list.size()>0) {
                    //本表取出数据时字符串类型的
                    String needData = (String) ((Map) list.get(0)).get("needData");
                    if (needData!=null&&!"".equals(needData)) {
                        paramValues[i] = new BigDecimal(needData);
                    } else {
                        paramValues[i] = new BigDecimal("0.00");
                    }
                } else {
					return "第"+(i+1)+"个因子配置错误！";
                }
            } else if ("$5".equals(formulas[0].trim())) {
                //$5 表示是同一表格不同统计月度的取数（eg：取上月某个单元格位置的数）
                //$5_表位置(行,对应字段number)_表位置(列,对应字段d系列)_取数期间
				int paramsNo = 1;
				Map<Integer,Object> params = new HashMap<>();
                StringBuffer sql = new StringBuffer();
                sql.append("SELECT r."+formulas[2].trim()+" AS needData FROM reportdata r WHERE 1=1");
//                sql.append(" AND r.acc_book_code = '"+CurrentUser.getCurrentLoginAccount()+"'");
				sql.append(" AND r.acc_book_code = ?"+paramsNo );
				params.put(paramsNo,CurrentUser.getCurrentLoginAccount());
				paramsNo++;
//                sql.append(" AND r.report_code = '"+reportCode+"'");
				sql.append(" AND r.report_code = ?"+paramsNo);
                params.put(paramsNo,reportCode);
                paramsNo++;
//                sql.append(" AND r.version = '"+version+"'");
				sql.append(" AND r.version = ?"+paramsNo);
				params.put(paramsNo,version);
				paramsNo++;
                if ("LM".equals(formulas[0].trim())) {
                    String newYMD = yearMonthDate;
                    if ("01".equals(newYMD.substring(4))) {
                        newYMD = Integer.valueOf(newYMD.substring(0,4))-1+"12";
                    } else {
                        newYMD = Integer.valueOf(newYMD)-1+"";
                    }
//                    sql.append(" AND r.year_month_date = '"+newYMD+"'");
					sql.append(" AND r.year_month_date = ?"+paramsNo );
					params.put(paramsNo,newYMD);
					paramsNo++;
                } else {
                    sql.append(" AND r.year_month_date = ?"+paramsNo );
					params.put(paramsNo,"");
					paramsNo++;
                }
                sql.append(" AND r.unit = '1'");
//                sql.append(" AND r.number = "+formulas[1].trim());
				sql.append(" AND r.number = ?"+paramsNo );
				params.put(paramsNo,formulas[1].trim());
				paramsNo++;
                List<?> list = reportComputeRepository.queryBySqlSC(sql.toString(),params);
                if (list!=null&&list.size()>0) {
                    //报表取出的数据时字符串类型的
                    String needData = (String) ((Map) list.get(0)).get("needData");
                    if (needData!=null&&!"".equals(needData)) {
                        paramValues[i] = new BigDecimal(needData);
                    } else {
                        paramValues[i] = new BigDecimal("0.00");
                    }
                } else {
                    return "第"+(i+1)+"个因子配置错误！";
                }
            } else if ("SQL".equals(formulas[0].trim())) {
                String sql = formulaVs[i].trim();
                sql = sql.substring(sql.indexOf("_")+1);

                sql = jointFormulaSqlBySQL(sql, yearMonthDate);

                List<?> list = reportComputeRepository.queryBySqlSC(sql);
                if (list!=null&&list.size()>0) {
                    BigDecimal needData = (BigDecimal) ((Map) list.get(0)).get("needData");
                    paramValues[i] = needData;
                } else {
					return "第"+(i+1)+"个因子配置错误！";
                }
            } else if ("$4".equals(formulas[0].trim())) {
                try {
                    paramValues[i] = new BigDecimal(formulas[1]);
                } catch (Exception e) {
					return "第"+(i+1)+"个因子配置错误！";
                }
            }
        }

        //计算规则、计算参数、计算参数值已准备完毕，可以开始计算了
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");
        String[] formulaKs = formulaK.split(",");//计算参数
        for (int i=0;i<formulaKs.length;i++){
            System.out.println(formulaKs[i].trim()+":"+paramValues[i].toString());
            engine.put(formulaKs[i].trim(), paramValues[i]);
        }
        System.out.println("计算公式："+formula);
        Object result = null;
        try{
            result = engine.eval(formula);
        }catch(ScriptException e){
            return "参数的计算规则配置错误！";
        }

        if (formula.contains("/")) {
            System.out.println("包含除法");
        }
        BigDecimal resultData = new BigDecimal("0.00");
        if (result!=null&&!"NaN".equals(result.toString())&&!"-Infinity".equals(result.toString())&&!"Infinity".equals(result.toString())){
            try {
				resultData = new BigDecimal(String.valueOf(result));
			} catch (Exception e) {
				return "公式校验计算结果("+result+")数据转换异常";
			}
        }
	    return "";
    }

    private List<?> jointFormulaSql(StringBuffer sql, String[] formulas, String yearMonthDate){
		String centerCode = CurrentUser.getCurrentLoginManageBranch();
		String account = CurrentUser.getCurrentLoginAccount();
//		int seatsNo = 1;
//		Map<Integer,Object> seats = new HashMap<>();
		int paramsNo = 1;
		Map<Integer, Object> params = new HashMap<>();
        String tableName = "accdetailbalancehis";
        if ("2".equals(formulas[2].trim())) {//专项
            tableName = "accarticlebalancehis";
        }

        if ("QC".equals(formulas[4].trim()) || "LTQC".equals(formulas[4].trim())) {//取期初余额值
            sql.append("SELECT IFNULL(SUM(a.balance_begin_dest),0.00) AS needData FROM "+tableName+" a WHERE 1=1");
        } else if ("QM".equals(formulas[4].trim()) || "LQM".equals(formulas[4].trim()) || "NCQM".equals(formulas[4].trim()) || "LTQ".equals(formulas[4].trim())) {
            //取期末余额值
            sql.append("SELECT IFNULL(SUM(a.balance_dest),0.00) AS needData FROM "+tableName+" a WHERE 1=1");
        } else if ("M".equals(formulas[4].trim())|| "LM".equals(formulas[4].trim()) || "LTQM".equals(formulas[4].trim())) {
            //取本月累计值（借/贷）
            if ("1".equals(formulas[6].trim())){
                sql.append("SELECT IFNULL(SUM(a.debit_dest),0.00) AS needData FROM "+tableName+" a WHERE 1=1");
            } else if ("2".equals(formulas[6].trim())){
                sql.append("SELECT IFNULL(SUM(a.credit_dest),0.00) AS needData FROM "+tableName+" a WHERE 1=1");
            }
        } else if ("Q".equals(formulas[4].trim()) || "LTQQ".equals(formulas[4].trim())) {
            //取本季累计值（借/贷）
            if ("1".equals(formulas[6].trim())){
                sql.append("SELECT IFNULL(SUM(a.debit_dest_quarter),0.00) AS needData FROM "+tableName+" a WHERE 1=1");
            } else if ("2".equals(formulas[6].trim())){
                sql.append("SELECT IFNULL(SUM(a.credit_dest_quarter),0.00) AS needData FROM "+tableName+" a WHERE 1=1");
            }
        } else if ("Y".equals(formulas[4].trim()) || "LTQY".equals(formulas[4].trim()) || "LQMY".equals(formulas[4].trim())) {
            //取本年累计值（借/贷）
            if ("1".equals(formulas[6].trim())){
                sql.append("SELECT IFNULL(SUM(a.debit_dest_year),0.00) AS needData FROM "+tableName+" a WHERE 1=1");
            } else if ("2".equals(formulas[6].trim())){
                sql.append("SELECT IFNULL(SUM(a.credit_dest_year),0.00) AS needData FROM "+tableName+" a WHERE 1=1");
            }
        }


		sql.append(" AND a.acc_book_code = ?"+paramsNo );
		params.put(paramsNo,account);
		paramsNo++;


        //拼接会计期间
        String newYearMonthDate = yearMonthDate;
        if ("LQM".equals(formulas[4].trim()) || "LQMY".equals(formulas[4].trim())) {
            //上年期末值
            /*newYearMonthDate = (Integer.parseInt(yearMonthDate.substring(0,4))-1) + "JS";*/
			newYearMonthDate = (Integer.parseInt(yearMonthDate.substring(0,4))-1) + "14";
        } else if ("NCQM".equals(formulas[4].trim())) {
			//年初期末值
			newYearMonthDate = yearMonthDate.substring(0,4) + "01";
		} else if ("LTQ".equals(formulas[4].trim()) || "LTQC".equals(formulas[4].trim()) || "LTQM".equals(formulas[4].trim()) || "LTQQ".equals(formulas[4].trim()) || "LTQY".equals(formulas[4].trim())) {
            //上年同期值
            newYearMonthDate = (Integer.parseInt(yearMonthDate.substring(0,4))-1)+yearMonthDate.substring(4);
        } else if ("LM".equals(formulas[4].trim())) {
            //上月累计值
            if ("01".equals(yearMonthDate.substring(4))) {
                /*newYearMonthDate = (Integer.parseInt(yearMonthDate.substring(0,4))-1)+ "JS";*/
				newYearMonthDate = (Integer.parseInt(yearMonthDate.substring(0,4))-1)+ "14";
            } else if ("JS".equals(yearMonthDate.substring(4))) {
                newYearMonthDate = yearMonthDate.substring(0,4) + "12";
            } else {
                newYearMonthDate = yearMonthDate.substring(0,4)+(Integer.parseInt(yearMonthDate.substring(4))-1);
            }
        } else {
        }
//        sql.append(" AND a.year_month_date = '"+newYearMonthDate+"'");
		sql.append(" AND a.year_month_date = ?"+paramsNo );
        params.put(paramsNo,newYearMonthDate);
		paramsNo++;
//		sql.append(" AND a.center_code = '"+CurrentUser.getCurrentLoginManageBranch()+"'");
		sql.append(" AND a.center_code = ?"+paramsNo );
		params.put(paramsNo,CurrentUser.getCurrentLoginManageBranch());
		paramsNo++;
		//拼接科目段
        if ("2".equals(formulas[2].trim())) {//专项

//			int paramsNo = 1;
//			Map<Integer, Object> params = new HashMap<>();

            detailAccountService.jointQuertSqlBySpecialCode(sql,formulas[3].trim(), "a", params, paramsNo);

			if (paramsNo != params.size()+1) {
				paramsNo = params.size()+1;
			}

        } else {

//			int paramsNo = 1;
//			Map<Integer, Object> params = new HashMap<>();

            sql.append(detailAccountService.jointDirectionIdxSqlBySubjectCode(formulas[3].trim(), "a", params, paramsNo));

			if (paramsNo != params.size()+1) {
				paramsNo = params.size()+1;
			}

        }

        //判断会计期间是否结转，如果未结转需要替换表名
        if (!"".equals(account) && !"".equals(newYearMonthDate)) {
            List<?> list = accMonthRespository.queryAccMonthTraceNo(account, newYearMonthDate, centerCode);
            if (list!=null&&list.size()>0) {
                String str = sql.toString();
                if ("2".equals(formulas[2].trim())) {//专项
                    str = str.replaceAll("accarticlebalancehis", "accarticlebalance");
                } else {
                    str = str.replaceAll("accdetailbalancehis", "accdetailbalance");
                }
                sql.setLength(0);
                sql.append(str);
            }
        }

        return reportComputeRepository.queryBySqlSC(sql.toString(),params);
    }

    private String jointFormulaSqlBySQL(String sql, String yearMonthDate){
        //替换参数
		String account = CurrentUser.getCurrentLoginAccount();
        String newYearMonthDate = yearMonthDate;
        if (sql.contains("%yearMonthDate%")) {
            sql = sql.replaceAll("%yearMonthDate%", yearMonthDate);
        }
        //上年同期
        if (sql.contains("%LTQ%")) {
            newYearMonthDate = Integer.parseInt(yearMonthDate.substring(0,4))-1+yearMonthDate.substring(4);
            sql = sql.replaceAll("%LTQ%", newYearMonthDate);
        }


        //判断会计期间是否结转，如果未结转需要替换表名
        if (!"".equals(account) && !"".equals(newYearMonthDate)) {
            List<?> list = accMonthRespository.queryAccMonthTraceNo(account, newYearMonthDate, CurrentUser.getCurrentLoginManageBranch());
            if (list!=null&&list.size()>0) {
                sql = sql.replaceAll("accdetailbalancehis", "accdetailbalance");
                sql = sql.replaceAll("accarticlebalancehis", "accarticlebalance");
            }
        }

        return sql;
    }
}
