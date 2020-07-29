package com.sinosoft.service.report.impl;

import com.sinosoft.common.Constant;
import com.sinosoft.common.CurrentTime;
import com.sinosoft.common.CurrentUser;
import com.sinosoft.common.CusSpecification;
import com.sinosoft.domain.AgeSegmentDefine;
import com.sinosoft.domain.CodeSelect;
import com.sinosoft.domain.Report.AgeAnalysisData;
import com.sinosoft.domain.Report.AgeAnalysisDataId;
import com.sinosoft.domain.Report.AgeAnalysisStyleInfo;
import com.sinosoft.domain.SpecialInfo;
import com.sinosoft.repository.AgeSegmentDefineRepository;
import com.sinosoft.repository.CodeSelectRepository;
import com.sinosoft.repository.SpecialInfoRepository;
import com.sinosoft.repository.report.AgeAnalysisDataRepository;
import com.sinosoft.repository.report.AgeAnalysisStyleInfoRepository;
import com.sinosoft.service.report.AgeAnalysisReportService;
import com.sinosoft.util.AjaxJson;
import com.sinosoft.util.ExcelUtil;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class AgeAnalysisReportServiceImp implements AgeAnalysisReportService {
	@Resource
	private AgeAnalysisDataRepository ageAnalysisDataRepository;
	@Resource
	private AgeAnalysisStyleInfoRepository ageAnalysisStyleInfoRepository;
	@Resource
	private AgeSegmentDefineRepository ageSegmentDefineRepository;
	@Resource
	private CodeSelectRepository codeSelectRepository;
	@Resource
	private SpecialInfoRepository specialInfoRepository;

	@Override
	public AjaxJson getReportHead(Integer version) {
		if (version==null || version==0) {
			version = 1;
		}

		AjaxJson ajaxJson = new AjaxJson();

		List<Integer> levelList = ageAnalysisStyleInfoRepository.queryHeadLevel(version);

		if (levelList!=null && levelList.size()>0) {
			StringBuilder jsonBuilder = new StringBuilder();
			jsonBuilder.append("{\"title\":[");

			for(int i=0;i<levelList.size();i++){
				Integer headLevel = levelList.get(i);
				List<AgeAnalysisStyleInfo> listEntity=ageAnalysisStyleInfoRepository.queryHead(version, headLevel);

				//这是循环获取列名称
				if(levelList.size()!=0){jsonBuilder.append("[");}

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

					if(listEntity.get(n).getD6()!=null&&listEntity.get(n).getD6()!=""&&!listEntity.get(n).getD6().equals("0")){
						jsonBuilder.append("\"rowspan");
						jsonBuilder.append("\":");
						jsonBuilder.append(Integer.valueOf(listEntity.get(n).getD6()));//设置跨行数
						jsonBuilder.append(",");
					}

					if(listEntity.get(n).getD7()!=null&&listEntity.get(n).getD7()!=""&&!listEntity.get(n).getD7().equals("0")){
						jsonBuilder.append("\"colspan");
						jsonBuilder.append("\":");
						jsonBuilder.append(Integer.valueOf(listEntity.get(n).getD7()));//设置跨列数
						jsonBuilder.append(",");
					}

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
				if(levelList.size()!=1){jsonBuilder.append("],");}
				if(levelList.size()==1){jsonBuilder.append("]");}
			}

			String mess="";
			if(levelList.size()!=1){
				mess=jsonBuilder.toString().substring(0,jsonBuilder.length() - 1);
				mess+="]}";
			}else{
				mess=jsonBuilder.toString();
				mess+="]}";
			}

			System.out.println("表头拼接信息："+mess);
			ajaxJson.setSuccess(true);
			ajaxJson.setObj(mess);
			ajaxJson.setMsg("success");
			return ajaxJson;
		} else {
			return null;
		}
	}
	@Override
	public List<?> qryReportData(String ageAnalysisType, String computeDate, String unit, Integer version) {
		String centerCode = CurrentUser.getCurrentLoginManageBranch();
		String accBookCode = CurrentUser.getCurrentLoginAccount();
		int paramsNo = 1;
		Map<Integer,Object> params = new HashMap<>();
		int index = 0;
		if (unit!=null&&"2".equals(unit)) {
			index = 1;
		}

		List<AgeAnalysisStyleInfo> head = ageAnalysisStyleInfoRepository.queryHeadField(version);

		StringBuffer sql=new StringBuffer();
		sql.append("select ");

		for(int i=0;i<head.size();i++) {
			int m = i + 1;
			String d8 = head.get(i).getD8();
			String d9 = head.get(i).getD9();
			if (d8!=null&&!"".equals(d8)) {
				String[] d8s = d8.split(",");
				if(m<head.size()){
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
		sql.append(" from ageanalysisdata r where 1=1 ");
		sql.append(" and r.center_code =?"+paramsNo);
		params.put(paramsNo,centerCode);
		paramsNo++;
		sql.append(" and r.acc_book_code =?"+paramsNo);
		params.put(paramsNo,accBookCode);
		paramsNo++;
		sql.append(" and r.age_analysis_type=?"+paramsNo);
		params.put(paramsNo,ageAnalysisType);
		paramsNo++;
		sql.append(" and r.version=?"+paramsNo);
		params.put(paramsNo,version);
		paramsNo++;
		sql.append(" and r.compute_date=?"+paramsNo);
		params.put(paramsNo,computeDate);
		paramsNo++;
		sql.append(" and r.unit=?"+paramsNo);
		params.put(paramsNo,unit);
		paramsNo++;
		sql.append(" order by r.d1,r.d5,r.d3");

		List<?> list = ageAnalysisDataRepository.queryBySqlSC(sql.toString(),params);

		return  list;
	}

	@Override
	@Transactional
	public String ageAnalysisCompute(String ageAnalysisType, String computeDate, Integer version) {
		/*
			1.查询当前账龄类型下参与计算的科目有哪些并按科目代码排序
			2.检查是否已存在数据，存在则需要先删除，再生成新的数据
			3.查询账龄段配置信息
			4.循环科目代码处理，查询按照账龄段科目下往来对象数据
			5.按照特定规则封装数据
			6.保存数据
		 */
		String centerCode = CurrentUser.getCurrentLoginManageBranch();
		String centerCodeName = CurrentUser.getCurrentManageBranchName();
		String branchCode = CurrentUser.getCurrentLoginManageBranch();
		String accBookCode = CurrentUser.getCurrentLoginAccount();
		String accBookType = CurrentUser.getCurrentLoginAccountType();

		List<Map<String, Object>> subjectCodeAndEndFlagList = ageAnalysisDataRepository.getSubjectCodeAndEndFlagByAgeAnalysisType(accBookCode, ageAnalysisType);
		if (subjectCodeAndEndFlagList!=null && subjectCodeAndEndFlagList.size()>0) {

			List<String> subjectCodeList = new ArrayList<String>();
			List<String> subjectCodeEndList = new ArrayList<String>();

			for (Map<String, Object> map : subjectCodeAndEndFlagList) {
				String subjectCode = map.get("subjectCode").toString();
				String endFlag = map.get("endFlag").toString();
				subjectCodeList.add(subjectCode);
				if (endFlag!=null && "0".equals(endFlag)) {
					//末级科目
					subjectCodeEndList.add(subjectCode);
				}
			}

			// 处理科目名称,key:科目编码，value:科目全名称
			Map<String, String> subjectNameMap = new HashMap<String, String>();
			setSubjectNameMap(subjectNameMap, subjectCodeList, accBookCode);

			Map<String, String> specialNameMap = new HashMap<String, String>();

			// 检查是否已存在数据，存在则需要先删除
			List<AgeAnalysisData> ageAnalysisDataList = checkReport(ageAnalysisType, computeDate, version);
			if (ageAnalysisDataList!=null && ageAnalysisDataList.size()>0) {
				for (AgeAnalysisData data : ageAnalysisDataList) {
					ageAnalysisDataRepository.delete(data);
				}
				ageAnalysisDataRepository.flush();
			}

			// 查询账龄段配置信息
			List<AgeSegmentDefine> ageSegmentDefineList = ageSegmentDefineRepository.findAll(new CusSpecification<>().asc("orderBy"));
			if (ageSegmentDefineList!=null && ageSegmentDefineList.size()>0) {
				List<Map<String, String>> ageTempList = new ArrayList<Map<String, String>>();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				for (int i=0;i<ageSegmentDefineList.size();i++) {
					AgeSegmentDefine age = ageSegmentDefineList.get(i);
					String beginDay = age.getBeginDay();
					String endDay = age.getEndDay();

					try {
						if (beginDay!=null && !"".equals(beginDay)) {
						    int num = Integer.valueOf(beginDay);
                            if (num>0) { num--; }
							beginDay = sdf.format(DateUtils.addDays(sdf.parse(computeDate), num*-1));
						}
						if (endDay!=null && !"".equals(endDay)) {
                            int num = Integer.valueOf(endDay);
                            if (num>0) { num--; }
							endDay = sdf.format(DateUtils.addDays(sdf.parse(computeDate), num*-1));
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}

					Map<String, String> map = new HashMap<String, String>();
					map.put("segmentName", age.getSegmentName());
					map.put("beginDay", beginDay);
					map.put("endDay", endDay);
					ageTempList.add(i, map);
				}

				StringBuffer startSql = new StringBuffer();
				int paramsNo = 1;
				Map<Integer,Object> params = new HashMap<>();
				int oldParamsNo;
				Map<Integer,Object> oldParams = new HashMap<>();
				startSql.append("select a.direction_other as directionOther,sum(a.debit_dest) as debitDestSum,sum(a.credit_dest) as creditDestSum from accmainvoucher am left join accsubvoucher a on a.center_code = am.center_code and a.branch_code = am.branch_code and a.acc_book_type = am.acc_book_type and a.acc_book_code = am.acc_book_code and a.year_month_date = am.year_month_date and a.voucher_no = am.voucher_no where 1=1");
				startSql.append(" and am.center_code = ?"+paramsNo);
				params.put(paramsNo,centerCode);
				paramsNo++;
				startSql.append(" and am.branch_code = ?"+paramsNo);
				params.put(paramsNo,branchCode);
				paramsNo++;
				startSql.append(" and am.acc_book_type = ?"+paramsNo);
				params.put(paramsNo,accBookType);
				paramsNo++;
				startSql.append(" and am.acc_book_code = ?"+paramsNo);
				params.put(paramsNo,accBookCode);
				paramsNo++;
				startSql.append(" and am.voucher_flag in('3')");

				String startSql2 = startSql.toString()
						.replaceAll("accmainvoucher", "accmainvoucherhis")
						.replaceAll("accsubvoucher", "accsubvoucherhis");


				/*
					key：科目编码_专项编码
					value：key：ageSegment_n_debit/credit（其中：n表示账龄段号）
						   value：对应的合计金额
				*/
				Map<String, Map<String, BigDecimal>> subjectSpecialAgeSegmentMap = new HashMap<>();
				List<String> subjectSpecialList = new ArrayList<String>();
				// 第一次存储的内容放在这里
				oldParams.putAll(params);
				oldParamsNo = paramsNo;
				for (String subjectCode : subjectCodeList) {

					// 上述是相同的，没有变，需要从old中拿出来之前的信息全部替换掉在放入params中进行覆盖(从此处继续)。
					params = new HashMap<>();
					params.putAll(oldParams);
					paramsNo = oldParamsNo;

					StringBuffer centreSql = new StringBuffer();
					String[] codes = subjectCode.split("/");
					for (int i=0;i<codes.length;i++) {
						if (i<9) {
							centreSql.append(" and a.f0" + (i+1) + " = ?" + paramsNo );
							params.put(paramsNo,codes[i]);
							paramsNo++;
						} else {
							centreSql.append(" and a.f" + (i+1) + " = ?" + paramsNo );
							params.put(paramsNo,codes[i]);
							paramsNo++;
						}
					}

					// 为了下面能使用
					int oldParamsNo1 ;
					Map<Integer,Object> oldParams1 = new HashMap<>();
					oldParams1.putAll(params);
					oldParamsNo1 = paramsNo;
					for (int i=0;i<ageTempList.size();i++) {

						params = new HashMap<>();
						params.putAll(oldParams1);
						paramsNo = oldParamsNo1;
						Map<String, String> map = ageTempList.get(i);
						String beginDay = map.get("beginDay");
						String endDay = map.get("endDay");

						StringBuffer endSql = new StringBuffer();
						if (beginDay!=null && !"".equals(beginDay)) {
							endSql.append(" and am.voucher_date <= ?" + paramsNo );
							params.put(paramsNo,beginDay);
							paramsNo++;
						}
						if (endDay!=null && !"".equals(endDay)) {
							endSql.append(" and am.voucher_date >= ?" + paramsNo );
							params.put(paramsNo,endDay);
							paramsNo++;
						}
						endSql.append(" group by a.direction_other");

						StringBuffer sql = new StringBuffer();
						sql.append(startSql).append(centreSql).append(endSql);
						sql.append(" union all ");
						sql.append(startSql2).append(centreSql).append(endSql);
						sql.append(" order by directionOther");

						// 在某科目下且相应账龄段下各专项的借方和贷方合计数
						List<Map<String, Object>> tempList = (List<Map<String, Object>>) ageAnalysisDataRepository.queryBySqlSC(sql.toString(),params);

						if (tempList!=null && tempList.size()>0) {
							for (Map map1 : tempList) {
								String directionOther = (String) map1.get("directionOther");
								BigDecimal debitDestSum = (BigDecimal) map1.get("debitDestSum");
								BigDecimal creditDestSum = (BigDecimal) map1.get("creditDestSum");

								String keyD = "ageSegment_" + (i+1) + "_debit";
								String keyC = "ageSegment_" + (i+1) + "_credit";

								// 非末级科目按现行规则应无专项的，所以这里如果存在多条，则做合并处理
								if (!subjectCodeEndList.contains(subjectCode)) {
									directionOther = "";
								}
								if (directionOther!=null && !"".equals(directionOther)) {
									String[] directionOthers = directionOther.split(",");
									for (String s : directionOthers) {
										String key = subjectCode + "_" + s;
										setsSubjectSpecialAgeSegmentMap(subjectSpecialAgeSegmentMap, key, keyD, keyC, debitDestSum, creditDestSum, subjectSpecialList);
									}
								} else {
									String key = subjectCode + "_null";
									setsSubjectSpecialAgeSegmentMap(subjectSpecialAgeSegmentMap, key, keyD, keyC, debitDestSum, creditDestSum, subjectSpecialList);
								}
							}
						}
					}
				}

				if (subjectSpecialAgeSegmentMap!=null && subjectSpecialAgeSegmentMap.size()>0) {

					CodeSelect codeSelect = codeSelectRepository.findCodeSelect("ageAnalysisType", ageAnalysisType);
					String direction = codeSelect.getTemp();
					String currentTime = CurrentTime.getCurrentTime();
					String userId = String.valueOf(CurrentUser.getCurrentUser().getId());

					Collections.sort(subjectSpecialList);

					int number = 0;
					for (String key : subjectSpecialList) {
						if (subjectSpecialAgeSegmentMap.containsKey(key)) {

							number++;
							/*
								key：科目编码_专项编码
								value：key：ageSegment_n_debit/credit（其中：n表示账龄段号）
									   value：对应的合计金额
							*/
							Map<String, BigDecimal> map = subjectSpecialAgeSegmentMap.get(key);

							AgeAnalysisDataId id = new AgeAnalysisDataId(centerCode,branchCode,accBookType,accBookCode,ageAnalysisType,version,computeDate,"1", number);
							AgeAnalysisDataId id2 = new AgeAnalysisDataId(centerCode,branchCode,accBookType,accBookCode,ageAnalysisType,version,computeDate,"2", number);
							AgeAnalysisData data = new AgeAnalysisData(id);
							AgeAnalysisData data2 = new AgeAnalysisData(id2);

							// 按照特定规则封装数据
							setAgeAnalysisData(data, data2, key, map, ageTempList, subjectNameMap, direction, centerCodeName, specialNameMap, accBookCode);

							data.setCreateBy(userId);
							data2.setCreateBy(userId);
							data.setCreateTime(currentTime);
							data2.setCreateTime(currentTime);

							// 保存数据
							ageAnalysisDataRepository.save(data);
							ageAnalysisDataRepository.save(data2);
						}
					}
				} else {
					return "本期无相应的账龄科目数据，无需计算！";
				}

			} else {
				return "未获取到账龄段配置信息！";
			}
		} else {
			return "未发现参与计算账龄的科目！";
		}
		return null;
	}

	private void setsSubjectSpecialAgeSegmentMap(Map<String, Map<String, BigDecimal>> map, String key, String keyD, String keyC, BigDecimal debitDestSum, BigDecimal creditDestSum, List<String> subjectSpecialList) {
		/*
			key：科目编码_专项编码
			value：key：ageSegment_n_debit/credit（其中：n表示账龄段号）
				   value：对应的合计金额
		*/
		if (map.containsKey(key)) {
			Map<String, BigDecimal> tempMap = map.get(key);
			if (tempMap.containsKey(keyD)) {
				tempMap.put(keyD, tempMap.get(keyD).add(debitDestSum));
				tempMap.put(keyC, tempMap.get(keyC).add(creditDestSum));
			} else {
				tempMap.put(keyD, debitDestSum);
				tempMap.put(keyC, creditDestSum);
			}
			// 余额
			tempMap.put("balance", tempMap.get("balance").add(debitDestSum).subtract(creditDestSum));
		} else {
			subjectSpecialList.add(key);
			Map<String, BigDecimal> tempMap = new HashMap<String, BigDecimal>();
			tempMap.put(keyD, debitDestSum);
			tempMap.put(keyC, creditDestSum);
			// 余额
			tempMap.put("balance", debitDestSum.subtract(creditDestSum));
			map.put(key, tempMap);
		}
	}

	private void setSubjectNameMap(Map<String, String> subjectNameMap, List<String> subjectCodeList, String accBookCode) {
		Set<String> codeSet = new HashSet<>();
		String temp = "";
		int seatsNo = 1;
		Map<Integer,Object> seats = new HashMap<>();
		StringBuffer sql = new StringBuffer();
		sql.append("select concat(s.all_subject,s.subject_code) as subjectCode,s.subject_name as subjectName from subjectinfo s where 1=1 and s.account = ?"+seatsNo );
		seats.put(seatsNo,accBookCode);
		seatsNo++;
		sql.append(" and (");
		for (String str : subjectCodeList) {
			temp = str.contains("/") ? str.substring(0, str.indexOf("/")) : str;
			if (!"".equals(temp) && !codeSet.contains(temp)) {
				codeSet.add(temp);
				if (codeSet.size()>1) {
//					sql.append(" or concat(s.all_subject,s.subject_code) like '"+temp+"%'");
					sql.append(" or concat(s.all_subject,s.subject_code) like ?"+seatsNo );
					seats.put(seatsNo,temp+"%");
					seatsNo++;
				} else {
//					sql.append(" concat(s.all_subject,s.subject_code) like '"+temp+"%'");
					sql.append(" concat(s.all_subject,s.subject_code) like ?"+seatsNo );
					seats.put(seatsNo,temp+"%");
					seatsNo++;
				}
			}
		}
		sql.append(" )");
		sql.append(" order by concat(s.all_subject,s.subject_code) asc");
		List<Map<String, String>> list = (List<Map<String, String>>) ageAnalysisDataRepository.queryBySqlSC(sql.toString(),seats);
		if (list!=null && list.size()>0) {
			for (Map<String, String> map : list) {
				String code = map.get("subjectCode");
				String name = map.get("subjectName");
				if (code.contains("/")) {
					String superCode = code.substring(0, code.lastIndexOf("/"));
					if (subjectNameMap.containsKey(superCode)) {
						subjectNameMap.put(code, subjectNameMap.get(superCode) + "/" + name);
					} else {
						// 不存在，则说明此科目没有父级，设置错误，则默认为本级名称
						subjectNameMap.put(code, name);
					}
				} else {
					subjectNameMap.put(code, name);
				}
			}
		}
	}

	private void setAgeAnalysisData(AgeAnalysisData data, AgeAnalysisData data2, String key, Map<String, BigDecimal> map, List<Map<String, String>> ageTempList, Map<String, String> subjectNameMap, String direction, String centerCodeName, Map<String, String> specialNameMap, String accBookCode) {
		int DBDecimals = 6;// 单位万元是保留位数
		String[] keys = key.split("_");
		String subjectCode = keys[0];
		String specialCode = keys[1];

		// 1.公司编码 2.公司名称 3.客商编码 4.客商名称
		// 5.科目编码 6.科目名称 7.余额 8.方向
		// 9-17.账龄段
		setAgeAnalysisDataD(data, data.getId().getCenterCode(), 1);
		setAgeAnalysisDataD(data2, data2.getId().getCenterCode(), 1);
		setAgeAnalysisDataD(data, centerCodeName, 2);
		setAgeAnalysisDataD(data2, centerCodeName, 2);
		if (!"null".equals(specialCode)) {
			setAgeAnalysisDataD(data, specialCode, 3);
			setAgeAnalysisDataD(data2, specialCode, 3);
			String specialName = null;
			if (!specialNameMap.containsKey(specialCode)) {
				SpecialInfo specialInfo = specialInfoRepository.findSpecialInfoBySpecialCode(accBookCode, specialCode);
				specialName = specialInfo.getSpecialNameP();
				specialNameMap.put(specialCode, specialName);
			} else {
				specialName = specialNameMap.get(specialCode);
			}
			setAgeAnalysisDataD(data, specialName, 4);
			setAgeAnalysisDataD(data2, specialName, 4);
		} else {
			setAgeAnalysisDataD(data, null, 3);
			setAgeAnalysisDataD(data2, null, 3);
			setAgeAnalysisDataD(data, null, 4);
			setAgeAnalysisDataD(data2, null, 4);
		}
		setAgeAnalysisDataD(data, subjectCode, 5);
		setAgeAnalysisDataD(data2, subjectCode, 5);
		setAgeAnalysisDataD(data, subjectNameMap.get(subjectCode), 6);
		setAgeAnalysisDataD(data2, subjectNameMap.get(subjectCode), 6);

		// 当各账龄段处理完毕，还需设置余额数据 map 中余额 balance 是通过加借减贷得来的
		BigDecimal balance = map.get("balance");
		if ("2".equals(direction)) {// 贷
			balance = balance.negate();// 取反
		}
		setAgeAnalysisDataD(data, balance.toString(), 7);
		setAgeAnalysisDataD(data2, balance.divide(new BigDecimal("10000.00"), DBDecimals, BigDecimal.ROUND_HALF_UP).toString(), 7);

		if ("1".equals(direction)) {
			setAgeAnalysisDataD(data, "借", 8);
			setAgeAnalysisDataD(data2, "借", 8);
		} else if ("2".equals(direction)) {
			setAgeAnalysisDataD(data, "贷", 8);
			setAgeAnalysisDataD(data2, "贷", 8);
		}

		// 对方合计值
		BigDecimal oppositeSum = BigDecimal.ZERO;
		BigDecimal oppositeSum2 = BigDecimal.ZERO;

		// 正向处理
		for (int i=0;i<ageTempList.size();i++) {
			String keyD = "ageSegment_" + (i+1) + "_debit";
			String keyC = "ageSegment_" + (i+1) + "_credit";
			int ageSegmentNoByD = 8+i+1;
			if (map.containsKey(keyD)) {
				if ("1".equals(direction)) {
					oppositeSum = oppositeSum.add(map.get(keyC));
					oppositeSum2 = oppositeSum2.add(map.get(keyC).divide(new BigDecimal("10000.00"), DBDecimals, BigDecimal.ROUND_HALF_UP));

					setAgeAnalysisDataD(data, map.get(keyD).toString(), ageSegmentNoByD);
					setAgeAnalysisDataD(data2, map.get(keyD).divide(new BigDecimal("10000.00"), DBDecimals, BigDecimal.ROUND_HALF_UP).toString(), ageSegmentNoByD);
				} else if ("2".equals(direction)) {
					oppositeSum = oppositeSum.add(map.get(keyD));
					oppositeSum2 = oppositeSum2.add(map.get(keyD).divide(new BigDecimal("10000.00"), DBDecimals, BigDecimal.ROUND_HALF_UP));

					setAgeAnalysisDataD(data, map.get(keyC).toString(), ageSegmentNoByD);
					setAgeAnalysisDataD(data2, map.get(keyC).divide(new BigDecimal("10000.00"), DBDecimals, BigDecimal.ROUND_HALF_UP).toString(), ageSegmentNoByD);
				}
			} else {
				setAgeAnalysisDataD(data, null, ageSegmentNoByD);
				setAgeAnalysisDataD(data2, null, ageSegmentNoByD);
			}
		}

		// 反向扣减
		// 先将对方合计值取反，为负数，这样就保证了合算时都是本方值加上对方值
		oppositeSum = oppositeSum.negate();
		oppositeSum2 = oppositeSum2.negate();

		for (int i=ageTempList.size();i>0;i--) {
			int ageSegmentNoByD = 8+i;
			String str = getAgeAnalysisDataD(data, ageSegmentNoByD);
			String str2 = getAgeAnalysisDataD(data2, ageSegmentNoByD);
			if (str!=null && !"".equals(str)) {
				BigDecimal money = new BigDecimal(str);
				BigDecimal money2 = new BigDecimal(str2);

				oppositeSum = money.add(oppositeSum);
				oppositeSum2 = money2.add(oppositeSum2);

				if (oppositeSum.compareTo(BigDecimal.ZERO) > 0) {
					setAgeAnalysisDataD(data, oppositeSum.toString(), ageSegmentNoByD);
					setAgeAnalysisDataD(data2, oppositeSum2.toString(), ageSegmentNoByD);
					break;
				} else if (oppositeSum.compareTo(BigDecimal.ZERO) < 0) {
					if (i != 1) {
						setAgeAnalysisDataD(data, null, ageSegmentNoByD);
						setAgeAnalysisDataD(data2, null, ageSegmentNoByD);
					} else {
						// 若账龄段区间金额全被核销为0，剩余的对方发生额则负数放在第一账龄段区间。
						setAgeAnalysisDataD(data, oppositeSum.toString(), ageSegmentNoByD);
						setAgeAnalysisDataD(data2, oppositeSum2.toString(), ageSegmentNoByD);
					}
				} else {
					// 等于0是，是显示，还是不显示？
					// 当前显示
					setAgeAnalysisDataD(data, oppositeSum.toString(), ageSegmentNoByD);
					setAgeAnalysisDataD(data2, oppositeSum2.toString(), ageSegmentNoByD);
					break;
				}
			} else {
				if (i != 1) {
					setAgeAnalysisDataD(data, null, ageSegmentNoByD);
					setAgeAnalysisDataD(data2, null, ageSegmentNoByD);
				} else {
					if (oppositeSum.compareTo(BigDecimal.ZERO) < 0) {
						// 若账龄段区间金额全被核销为0，剩余的对方发生额则负数放在第一账龄段区间。
						setAgeAnalysisDataD(data, oppositeSum.toString(), ageSegmentNoByD);
						setAgeAnalysisDataD(data2, oppositeSum2.toString(), ageSegmentNoByD);
					} else {
						setAgeAnalysisDataD(data, null, ageSegmentNoByD);
						setAgeAnalysisDataD(data2, null, ageSegmentNoByD);
					}
				}
			}
		}
	}

	private void setAgeAnalysisDataD(AgeAnalysisData entity, String data, int d){
		if (d>=1 && d<=30) {
			switch (d) {
				case 1:
					entity.setD1(data);
					break;
				case 2:
					entity.setD2(data);
					break;
				case 3:
					entity.setD3(data);
					break;
				case 4:
					entity.setD4(data);
					break;
				case 5:
					entity.setD5(data);
					break;
				case 6:
					entity.setD6(data);
					break;
				case 7:
					entity.setD7(data);
					break;
				case 8:
					entity.setD8(data);
					break;
				case 9:
					entity.setD9(data);
					break;
				case 10:
					entity.setD10(data);
					break;
				case 11:
					entity.setD11(data);
					break;
				case 12:
					entity.setD12(data);
					break;
				case 13:
					entity.setD13(data);
					break;
				case 14:
					entity.setD14(data);
					break;
				case 15:
					entity.setD15(data);
					break;
				case 16:
					entity.setD16(data);
					break;
				case 17:
					entity.setD17(data);
					break;
				case 18:
					entity.setD18(data);
					break;
				case 19:
					entity.setD19(data);
					break;
				case 20:
					entity.setD20(data);
					break;
				case 21:
					entity.setD21(data);
					break;
				case 22:
					entity.setD22(data);
					break;
				case 23:
					entity.setD23(data);
					break;
				case 24:
					entity.setD24(data);
					break;
				case 25:
					entity.setD25(data);
					break;
				case 26:
					entity.setD26(data);
					break;
				case 27:
					entity.setD27(data);
					break;
				case 28:
					entity.setD28(data);
					break;
				case 29:
					entity.setD29(data);
					break;
				default :
					entity.setD30(data);
			}
		} else {
			System.out.println("无效的位置，d="+d);
		}
	}

	private String getAgeAnalysisDataD(AgeAnalysisData entity, int d){
		if (d>=1 && d<=30) {
			switch (d) {
				case 1:
					return entity.getD1();
				case 2:
					return entity.getD2();
				case 3:
					return entity.getD3();
				case 4:
					return entity.getD4();
				case 5:
					return entity.getD5();
				case 6:
					return entity.getD6();
				case 7:
					return entity.getD7();
				case 8:
					return entity.getD8();
				case 9:
					return entity.getD9();
				case 10:
					return entity.getD10();
				case 11:
					return entity.getD11();
				case 12:
					return entity.getD12();
				case 13:
					return entity.getD13();
				case 14:
					return entity.getD14();
				case 15:
					return entity.getD15();
				case 16:
					return entity.getD16();
				case 17:
					return entity.getD17();
				case 18:
					return entity.getD18();
				case 19:
					return entity.getD19();
				case 20:
					return entity.getD20();
				case 21:
					return entity.getD21();
				case 22:
					return entity.getD22();
				case 23:
					return entity.getD23();
				case 24:
					return entity.getD24();
				case 25:
					return entity.getD25();
				case 26:
					return entity.getD26();
				case 27:
					return entity.getD27();
				case 28:
					return entity.getD28();
				case 29:
					return entity.getD29();
				default :
					return entity.getD30();
			}
		} else {
			System.out.println("无效的位置，d="+d);
			return "";
		}
	}

	@Override
	public List<AgeAnalysisData> checkReport(String ageAnalysisType, String computeDate, Integer version) {
		String centerCode = CurrentUser.getCurrentLoginManageBranch();
		String accBookCode = CurrentUser.getCurrentLoginAccount();
		if (version==null || version==0) {
			version = 1;
		}
		List<AgeAnalysisData> ageAnalysisDataList = null;

		ageAnalysisDataList = ageAnalysisDataRepository.findAll(new CusSpecification<>().and(
				CusSpecification.Cnd.like("id.centerCode", centerCode),
				CusSpecification.Cnd.like("id.accBookCode", accBookCode),
				CusSpecification.Cnd.eq("id.ageAnalysisType", ageAnalysisType),
				CusSpecification.Cnd.eq("id.computeDate", computeDate),
				CusSpecification.Cnd.eq("id.version", version)));

		return ageAnalysisDataList;
	}

	@Override
	public void dynamicExportDownload(HttpServletRequest request, HttpServletResponse response, String ageAnalysisType, String computeDate, String unit, Integer version) {
		List<?> result = qryReportData(ageAnalysisType, computeDate, unit, version);

		CodeSelect codeSelect = codeSelectRepository.findCodeSelect("ageAnalysisType", ageAnalysisType);
		String ageAnalysisTypeName = codeSelect.getCodeName();
		String centerCodeName = CurrentUser.getCurrentManageBranchName();
		String accBookCodeName = CurrentUser.getCurrentAccountName();

		ExcelUtil excelUtil = new ExcelUtil();
		excelUtil.exportAgeAnalysis(request, response, result, Constant.MODELPATH, computeDate, version, ageAnalysisTypeName,centerCodeName, accBookCodeName, unit);
	}
}
