package com.sinosoft.service.impl;

import com.sinosoft.common.CurrentTime;
import com.sinosoft.common.CurrentUser;
import com.sinosoft.common.CusSpecification;
import com.sinosoft.common.InvokeResult;
import com.sinosoft.domain.AccTagManage;
import com.sinosoft.domain.SpecialInfo;
import com.sinosoft.domain.SubjectInfo;
import com.sinosoft.domain.account.*;
import com.sinosoft.dto.MenuInfoDTO;
import com.sinosoft.dto.VoucherDTO;
import com.sinosoft.repository.*;
import com.sinosoft.repository.account.*;
import com.sinosoft.service.VoucherService;
import com.sinosoft.service.account.VoucherManageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@SpringBootApplication
public class VoucherServiceImp implements VoucherService {
	private Logger logger = LoggerFactory.getLogger(VoucherServiceImp.class);
	@Resource
	private VoucherRepository voucherRepository;
	@Resource
	private VoucherSubRepository voucherSubRepository;
	@Resource
	private SubjectRepository subjectRepository;
	@Resource
	private SpecialInfoRepository specialInfoRepository;
	@Resource
	private AccMainVoucherRespository accMainVoucherRespository;
	@Resource
	private AccSubVoucherRespository accSubVoucherRespository;
	@Resource
	private CodeSelectRepository codeSelectRepository;
	@Resource
    private UserInfoRepository userInfoRepository;
	@Resource
	private AccRemarkManageRespository accRemarkManageRespository;
	@Resource
    private AccTagManageRespository accTagManageRespository;
	@Resource
	private VoucherManageService voucherManageService;
	@Resource
	private AccVoucherNoRespository accVoucherNoRespository;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@PersistenceContext
	private EntityManager em; //注入EntityManager
	@Value("${voucher.currency}")
	private String currency;
	@Value("${voucher.exchangeRate}")
	private String exchangeRate;

	@Override
	public  List<?> qryTagList(String value){
		String centerCode = CurrentUser.getCurrentLoginManageBranch();
		String branchCode = CurrentUser.getCurrentLoginManageBranch();
		String accBookType = CurrentUser.getCurrentLoginAccountType();
		String accBookCode = CurrentUser.getCurrentLoginAccount();

		Set<String> neededCodes = new HashSet<String>();
		if (value!=null&&!"".equals(value)) {
			String[] strings = value.split(",");
			for (String s : strings) {
				List<AccTagManage> tag = accTagManageRespository.findByCenterCodeAndAccBookTypeAndAccBookCodeAndLikeTagName(centerCode, accBookType, accBookCode, s);
				if (tag!=null&&tag.size()>0) {
					for (AccTagManage atm : tag) {
						neededCodes.add(atm.getTagCode());
					}
				}
			}
		}

		List resultList = new ArrayList();
		List<?> superTagList = voucherRepository.findSuperTag(centerCode, accBookType, accBookCode);
		if (superTagList!=null&&superTagList.size()>0) {
			for (Object o : superTagList) {
				Map map = new HashMap();
				map.putAll((Map)o);
				if ("0".equals(map.get("endFlag"))) {
					//为末级
					if (value!=null&&!"".equals(value)) {
						if (neededCodes.contains((String)map.get("id"))){
							resultList.add(map);
						}
					} else {
						resultList.add(map);
					}
				} else {
					List list = qryChildrenTagList((String)map.get("id"), value, neededCodes);
					if (list!=null&&list.size()>0) {
						map.put("children",list);
						resultList.add(map);
					}
				}
			}
		}
		return resultList;
	}

	private List<?> qryChildrenTagList(String upperTag, String value, Set<String> neededCodes){
		String centerCode = CurrentUser.getCurrentLoginManageBranch();
		String branchCode = CurrentUser.getCurrentLoginManageBranch();
		String accBookType = CurrentUser.getCurrentLoginAccountType();
		String accBookCode = CurrentUser.getCurrentLoginAccount();

		List list1=new ArrayList();
		List<?> childrenTagList = voucherRepository.findTagByUpperTag(centerCode, accBookType, accBookCode, upperTag);
		if (childrenTagList!=null&&childrenTagList.size()>0) {
			for (Object o : childrenTagList) {
				Map map = new HashMap();
				map.putAll((Map)o);
				if ("0".equals(map.get("endFlag"))) {
					//为末级
					if (value!=null&&!"".equals(value)) {
						if (neededCodes.contains((String)map.get("id"))){
							list1.add(map);
						}
					} else {
						list1.add(map);
					}
				} else {
					List list = qryChildrenTagList((String)map.get("id"), value, neededCodes);
					if (list!=null&&list.size()>0) {
						map.put("children",list);
						list1.add(map);
					}
				}
			}
		}
		return list1;
	}

	@Override
	public List<?> qryRemarkList(String value) {
		String centerCode = CurrentUser.getCurrentLoginManageBranch();
		String branchCode = CurrentUser.getCurrentLoginManageBranch();
		String accBookType = CurrentUser.getCurrentLoginAccountType();
		String accBookCode = CurrentUser.getCurrentLoginAccount();

		List resultList=new ArrayList();
		List<AccRemarkManage> remarkList = accRemarkManageRespository.findAll(new CusSpecification<>().and(
				CusSpecification.Cnd.eq("CenterCode", centerCode),
				CusSpecification.Cnd.eq("AccBookType", accBookType),
				CusSpecification.Cnd.eq("AccBookCode", accBookCode),
				CusSpecification.Cnd.like("RemarkName", value)).asc("RemarkName"));
		if (remarkList!=null&&remarkList.size()>0) {
			for (AccRemarkManage accRemarkManage : remarkList) {
				Map map = new HashMap();
				map.put("id",accRemarkManage.getId());
				map.put("text",accRemarkManage.getRemarkName());
				map.put("subjectCode",accRemarkManage.getItemCode());
				map.put("subjectName",accRemarkManage.getItemName());
				resultList.add(map);
			}
		}
		return  resultList;
	}

	//递归
	@Override
	public List<?> qrySubjectCodeForCheck(String value) {
		List resultListAll=new ArrayList();
		if (value!=null&&!"".equals(value)) {
			resultListAll = qrySubjectCodeForCheckByValue(value, "N");
		} else {
			long start = System.currentTimeMillis();
			String accBookCode = CurrentUser.getCurrentLoginAccount();
			// 找到的是四大类
			String subjectTypeSql = "select c.code_code as id,c.code_name as text from codemanage c where c.code_type = 'subjectType' order by id";
			List<?> subjectTypeList = voucherRepository.queryBySqlSC(subjectTypeSql);
			if (subjectTypeList!=null&&subjectTypeList.size()>0) {
				//查询存在下级的科目id
				StringBuffer superSql = new StringBuffer("select distinct s.super_subject as superSubject from subjectinfo s where 1=1 and s.super_subject !='' and s.super_subject is not null");
				superSql.append(" and s.account = ?1");

				Map<Integer, Object> params = new HashMap<>();
				params.put(1, accBookCode);

				List<?> superList = voucherRepository.queryBySqlSC(superSql.toString(), params);
				Set<String> superIdSet = new HashSet<>();
				if (superList!=null&&superList.size()>0) {
					for (Object obj: superList) {
						//	把对应的父类id存到set集合中
						superIdSet.add(((Map)obj).get("superSubject").toString());
					}
				}

				for (Object o : subjectTypeList) {
					List resultList=new ArrayList();
					Map subjectTypeMap = new HashMap();
					subjectTypeMap.putAll((Map) o);
					//  通过四大类类型(subject_type字段) 找出对应4位编码
					StringBuffer sql=new StringBuffer(" select cast(m.id as char) as id,m.subject_name as text ,m.subject_code as mid,m.end_flag as endFlag from subjectinfo m " +
							"where (m.super_subject is null or m.super_subject= '') and m.subject_type = ?1 and m.account = ?2 order by concat(m.all_subject,m.subject_code)" );

					params = new HashMap<>();
					params.put(1, subjectTypeMap.get("id"));
					params.put(2, accBookCode);

					List<?> list =voucherRepository.queryBySqlSC(sql.toString(), params);
					for (Object obj : list) {
						//Map map=(Map) obj;
						Map map = new HashMap();
						map.putAll((Map) obj);
						List<?> list2 = null;
						//	set集合中的superCode中对应的父级id与map中的id对应
						if (superIdSet.contains(map.get("id").toString())) {
							list2 = qryChildrenForCheck((String)map.get("id"),value,superIdSet);
						}
						map.put("id",map.get("mid"));
						map.put("text",map.get("text"));

						if(list2!=null&&list2.size() != 0){
							map.put("children",list2);
							map.put("state","closed");
							resultList.add(map);
						} else if ("0".equals(map.get("endFlag"))){
							//无子级，但为末级
							resultList.add(map);
						} else {
							//不需要
						}
					}

					if (resultList!=null&&resultList.size()>0) {
						subjectTypeMap.put("children",resultList);
						subjectTypeMap.put("state","closed");
						resultListAll.add(subjectTypeMap);
					}
				}
			}
			System.out.println("执行用时:" + (System.currentTimeMillis()-start) + " ms");
		}
		return resultListAll;
	}

	private List<MenuInfoDTO> qryChildrenForCheck(String id,String value,Set<String> superIdSet){
		List list1=new ArrayList();
		StringBuffer sql=new StringBuffer(" select cast(m.id as char) as id,m.subject_name as text,m.subject_code as mid,m.end_flag as endFlag from subjectinfo m " +
				"where m.super_subject = ?1");

		Map<Integer, Object> params = new HashMap<>();
		params.put(1, id);

		if(value!=null&&!"".equals(value)){
			sql.append(" and m.subject_name like ?2");
			params.put(2, value);
		}

		sql.append(" order by concat(m.all_subject,m.subject_code)");

		List<?> list =voucherRepository.queryBySqlSC(sql.toString(), params);
		if(list!=null&&list.size()>0&&!list.isEmpty()){
			for (Object obj : list) {
				Map map = new HashMap();
				map.putAll((Map) obj);

				List<?> list2 = null;
				if (superIdSet.contains(map.get("id").toString())) {
					list2 = qryChildrenForCheck((String)map.get("id"),value,superIdSet);
				}
				if(list2!=null&&list2.size()>0&&!list2.isEmpty()){
					map.put("id",map.get("mid"));
					map.put("text",map.get("text"));
					map.put("children",list2);
					map.put("state","closed");
					//map.put("checked", map.get("mid")!=null&&!map.get("mid").equals("")?true:false);
					list1.add(map);
				} else if ("0".equals(map.get("endFlag"))){
					//无子级，但为末级
					map.put("id",map.get("mid"));
					map.put("text",map.get("text"));
					//map.put("checked", map.get("mid")!=null&&!map.get("mid").equals("")?true:false);
					list1.add(map);
				} else {
					//不需要
				}
			}
		}
		return list1;
	}

	private List<?> qryChildrenForCheck(String id, Set<String> superIdSet){
		List list1=new ArrayList();
		StringBuffer sql=new StringBuffer(" select cast(m.id as char) as id,m.subject_name as text ,m.subject_code as mid,m.end_flag as endFlag from subjectinfo m " +
				"where m.super_subject = ?1 order by concat(m.all_subject,m.subject_code)" );

		Map<Integer, Object> params = new HashMap<>();
		params.put(1, id);

		List<?> list =voucherRepository.queryBySqlSC(sql.toString(), params);
		if(list!=null&&list.size()>0&&!list.isEmpty()){
			for (Object obj : list) {
				Map map = new HashMap();
				map.putAll((Map) obj);

				List<?> list2 = null;
				String currentId = (String) map.get("id");
				if (superIdSet.contains(currentId)) {
					list2 = qryChildrenForCheck(currentId, superIdSet);
				}

				map.put("id",map.get("mid"));
				map.put("text",map.get("text"));
				if(list2!=null&&list2.size()>0&&!list2.isEmpty()){
					map.put("children",list2);
					list1.add(map);
				}else if ("0".equals(map.get("endFlag"))){
					//无子级，但为末级
					list1.add(map);
				} else {
					//不需要
				}
			}
		}
		return list1;
	}

	/**
	 *
	 * @param value 科目名称
	 * @param onlyLastStage 是否仅查询末级：Y:是，N:否 (若此参数为空，则默认为 Y )
	 * @return
	 */
	@Override
	public List<?> qrySubjectCodeForCheck(String value, String onlyLastStage) {
		if (value!=null && !"".equals(value)) {
			return qrySubjectCodeForCheckByValue(value, onlyLastStage);
		} else {
			return qrySubjectCodeForCheck(value);
		}
	}

	/**
	 * 根据科目名称模糊查询科目树信息（编码、名称）（本方法支持多查询，多条之间用英文逗号隔开）
	 * @param value 科目名称
	 * @param onlyLastStage 是否仅查询末级：Y:是，N:否
	 * @return
	 */
	@Override
	public List<?> qrySubjectCodeForCheckByValue(String value, String onlyLastStage){
		long start = System.currentTimeMillis();
		String accBookCode = CurrentUser.getCurrentLoginAccount();
		List resultListAll=new ArrayList();
		StringBuffer neededSql;
		Set<String> neededIds = new HashSet<String>();

		int paramsNo = 1;
		Map<Integer, Object> params = new HashMap<>();

		if (onlyLastStage!=null && "N".equals(onlyLastStage)) {
			//部分末级、非末级（全查）
			neededSql = new StringBuffer("select s.id as id from subjectinfo s where s.account = ?" + paramsNo);
		} else {//仅查询末级
			neededSql = new StringBuffer("select s.id as id from subjectinfo s where s.end_flag = '0' and s.account = ?" + paramsNo);
		}

		params.put(paramsNo, accBookCode);
		paramsNo++;

        neededSql.append(" and(");
		if (value.contains(",")) {
		    String[] codes = value.split(",");
		    for (int i=0;i<codes.length;i++) {
                if (i==0) {
                    neededSql.append(" s.subject_name like ?" + paramsNo);
					params.put(paramsNo, "%"+codes[i]+"%");
					paramsNo++;
					neededSql.append(" or replace(concat(s.all_subject,s.subject_code),'/','') like ?" + paramsNo);
					params.put(paramsNo, codes[i].replaceAll("/","")+"%");
					paramsNo++;
                } else {
                    neededSql.append(" or s.subject_name like ?" + paramsNo);
					params.put(paramsNo, "%"+codes[i]+"%");
					paramsNo++;
                    neededSql.append(" or replace(concat(s.all_subject,s.subject_code),'/','') like ?" + paramsNo);
					params.put(paramsNo, codes[i].replaceAll("/","")+"%");
					paramsNo++;
                }
            }
        } else {
            neededSql.append(" s.subject_name like ?" + paramsNo);
			params.put(paramsNo, "%"+value+"%");
			paramsNo++;
            neededSql.append(" or replace(concat(s.all_subject,s.subject_code),'/','') like ?" + paramsNo);
			params.put(paramsNo, value.replaceAll("/","")+"%");
			paramsNo++;
        }

		neededSql.append(")");

		List<?> neededList =voucherRepository.queryBySqlSC(neededSql.toString(), params);
		if (neededList!=null&&neededList.size()>0) {
			for(int j=0;j<neededList.size();j++){
				Map map = (Map<String, Object>) neededList.get(j);
				neededIds.add(map.get("id").toString());
			}
		}

		String subjectTypeSql = "select c.code_code as id,c.code_name as text from codemanage c where c.code_type = 'subjectType' order by id";
		List<?> subjectTypeList = voucherRepository.queryBySqlSC(subjectTypeSql);
		if (subjectTypeList!=null&&subjectTypeList.size()>0) {
			//查询存在下级的科目id
			StringBuffer superSql = new StringBuffer("select distinct s.super_subject as superSubject from subjectinfo s where 1=1 and s.super_subject !='' and s.super_subject is not null");
			superSql.append(" and s.account = ?1");

			params = new HashMap<>();
			params.put(1, accBookCode);

			List<?> superList = voucherRepository.queryBySqlSC(superSql.toString(), params);
			Set<String> superIdSet = new HashSet<>();
			if (superList!=null&&superList.size()>0) {
				for (Object obj: superList) {
					superIdSet.add(((Map)obj).get("superSubject").toString());
				}
			}

			for (Object o : subjectTypeList) {
				List resultList=new ArrayList();
				Map subjectTypeMap = new HashMap();
				subjectTypeMap.putAll((Map) o);

				//查询最外层
				StringBuffer sql=new StringBuffer(" select cast(m.id as char) as id,m.subject_name as text,m.subject_code as mid,m.end_flag as endFlag from subjectinfo m where (m.super_subject is null or m.super_subject= '')" );
				sql.append(" and m.subject_type = ?1");
				sql.append(" and m.account = ?2 order by concat(m.all_subject,m.subject_code)");

				params = new HashMap<>();
				params.put(1, subjectTypeMap.get("id"));
				params.put(2, accBookCode);

				List<?> list =voucherRepository.queryBySqlSC(sql.toString(), params);
				for (Object obj : list) {
					Map map = new HashMap();
					map.putAll((Map) obj);
					List<?> list2 = null;
					String currentId = map.get("id").toString();
					if (superIdSet.contains(currentId)) {
						if (neededIds.contains(currentId)) {
							//存在下级（表示是需要的非末级），那么其所有下级均是需要的
							list2 = qryChildrenForCheck(currentId, superIdSet);
						} else {
							list2 = qryChildrenForCheckByValue(currentId, neededIds, superIdSet);
						}
					}
					map.put("id",map.get("mid"));
					map.put("text",map.get("text"));
					if (list2!=null && list2.size()>0) {
						map.put("children",list2);
						resultList.add(map);
					} else if ("0".equals(map.get("endFlag")) && neededIds.contains(currentId)){
						//无子级，但为末级
						resultList.add(map);
					} else {
						//不需要
					}
				}
				if (resultList!=null&&resultList.size()>0) {
					subjectTypeMap.put("children",resultList);
					resultListAll.add(subjectTypeMap);
				}
			}
		}
		System.out.println("执行用时:" + (System.currentTimeMillis()-start) + " ms");
		return resultListAll;
	}

	@Override
	public String qrySubjectNameAllBySubjectCode(String subjectCode){
		//首先判断科目全代码的规范性，如果最后一个字符为“/”，则去掉
		if ("/".equals(subjectCode.substring(subjectCode.length()-1))) {
			subjectCode = subjectCode.substring(0,subjectCode.length()-1);
		}
		List<?> list = voucherRepository.getSubjectNameBySubjectCode(CurrentUser.getCurrentLoginAccount(), "0", "1", subjectCode);
		StringBuffer nameAll = new StringBuffer();
		if (list!=null&&list.size()>0) {
			Map map = (Map) list.get(0);
			nameAll.append((String) map.get("subjectName"));
			long id = 0;
			if (map.get("superSubject")!=null&&!"".equals((String) map.get("superSubject"))) {
				id = Long.valueOf((String) map.get("superSubject"));
			}
			while (id!=0) {
				SubjectInfo subjectInfo = subjectRepository.findById(id).get();
				nameAll.insert(0, subjectInfo.getSubjectName()+"/");
				if (subjectInfo.getSuperSubject()!=null&&!"".equals(subjectInfo.getSuperSubject())) {
					id = Long.valueOf(subjectInfo.getSuperSubject());
				} else {
					id = 0;
				}
			}
		}
		return (nameAll.toString().length()>0)?nameAll.toString()+"/":"";
	}

	@Override
	public String qrySpecialNamePBySpecialCode(String codeS, String specialNameP){
		StringBuffer nameAll = new StringBuffer();
		if (codeS!=null&&!"".equals(codeS)) {
			String[] codeS1 = codeS.split(",");
			for (int i=0;i<codeS1.length;i++) {//每一行专项数据
				if (codeS1[i]!=null&&!"".equals(codeS1[i])&&!"无关联专项".equals(codeS1[i])) {
					String[] codeS2 = codeS1[i].split(";");
					for (int j=0;j<codeS2.length;j++) {//每行数据的每一条数据
						VoucherDTO d = getSpecialDateBySpecialCode(codeS2[j]);
						if (specialNameP!=null&&"1".equals(specialNameP)) {//全称
							if (j!=codeS2.length-1) {
								nameAll.append(d.getSpecialNameP()+";");
							} else {
								nameAll.append(d.getSpecialNameP());
							}
						} else {//简称
							if (j!=codeS2.length-1) {
								nameAll.append(d.getSpecialName()+";");
							} else {
								nameAll.append(d.getSpecialName());
							}
						}
					}
				} else {
					nameAll.append("无关联专项");//此处即代表本次循环即将结束，因此无需加,分隔符
				}
				nameAll.append(",");
			}
		}
		return (nameAll.length()!=0)?nameAll.toString().substring(0,nameAll.toString().length()-1):"";
	}

	@Override
	public InvokeResult checkSubjectByCode(String subjectCode){
		//首先判断科目全代码的规范性，如果最后一个字符为“/”，则去掉
		if ("/".equals(subjectCode.substring(subjectCode.length()-1))) {
			subjectCode = subjectCode.substring(0,subjectCode.length()-1);
		}
		List<?> list = voucherRepository.findSubjectBySubjectCodeAll(CurrentUser.getCurrentLoginAccount(), subjectCode);
		if (list!=null&&list.size()>0) {
			return InvokeResult.success();
		} else {
			return InvokeResult.failure("你输入的科目不存在，请检查！");
		}
	}

	private List<?> qryChildrenForCheckByValue(String id, Set<String> set, Set<String> superIdSet){
		List list1=new ArrayList();
		StringBuffer sql=new StringBuffer("select cast(m.id as char) as id,m.subject_name as text ,m.subject_code as mid,m.end_flag as endFlag from subjectinfo m");
		sql.append(" where m.super_subject = ?1 order by concat(m.all_subject,m.subject_code)");

		Map<Integer, Object> params = new HashMap<>();
		params.put(1, id);

		List<?> list =voucherRepository.queryBySqlSC(sql.toString(), params);
		if(list!=null&&list.size()>0&&!list.isEmpty()){
			for (Object obj : list) {
				Map map = new HashMap();
				map.putAll((Map) obj);
				List<?> list2 = null;
				String currentId = map.get("id").toString();
				if (superIdSet.contains(currentId)) {
					if (set.contains(currentId)) {
						//存在下级（表示是需要的非末级），那么其所有下级均是需要的
						list2 = qryChildrenForCheck(currentId, superIdSet);
					} else {
						list2 = qryChildrenForCheckByValue(currentId, set, superIdSet);
					}
				}
				map.put("id",map.get("mid"));
				map.put("text",map.get("text"));
				if(list2!=null&&list2.size()>0&&!list2.isEmpty()){
					map.put("children",list2);
					list1.add(map);
				} else if ("0".equals(map.get("endFlag")) && set.contains(currentId)){
					//无子级，但为末级
					list1.add(map);
				} else {
					//不需要
				}
			}
		}
		return list1;
	}

	@Override
	public List<?> qrySpecialTreeBySuperSpecial(String originalValue, String inputValue) {
		long time = System.currentTimeMillis();
		String accBookCode = CurrentUser.getCurrentLoginAccount();
		if (inputValue!=null&&!"".equals(inputValue) && originalValue.equals(inputValue)) {
			inputValue = "";
		} else if (inputValue!=null&&!"".equals(inputValue) && inputValue.startsWith(originalValue)){
			inputValue = inputValue.substring(originalValue.length());
		}
		//先查询出需要的
		Set<String> needIds = new HashSet<>();
		if (inputValue!=null&&!"".equals(inputValue)) {
			StringBuffer needSql = new StringBuffer("select s.id as id from specialinfo s where 1=1");
			needSql.append(" and s.account = ?1");
			needSql.append(" and s.special_code like ?2");
			needSql.append(" and (s.special_name like ?3 or s.special_namep like ?3 or s.special_code like ?4 )");

			Map<Integer, Object> params = new HashMap<>();
			params.put(1, accBookCode);
			params.put(2, originalValue+"%");
			params.put(3, "%"+inputValue+"%");
			params.put(4, originalValue+inputValue+"%");

			List<?> needList = voucherRepository.queryBySqlSC(needSql.toString(), params);
			if (needList!=null&&needList.size()>0) {
				for (Object o:needList) {
					needIds.add(((Map)o).get("id").toString());
				}
			}
		}
		StringBuffer sql = new StringBuffer("select s.id,s.special_code as mid,s.special_namep as text,s.endflag as endFlag,(select count(ss.id) from specialinfo ss where ss.account = s.account and ss.useflag = s.useflag and ss.super_special = s.id) as childNum from specialinfo s where (s.super_special is null or s.super_special = '')");
		sql.append(" and s.account = ?1");
		sql.append(" and s.special_code = ?2 order by s.special_code");

		Map<Integer, Object> params = new HashMap<>();
		params.put(1, accBookCode);
		params.put(2, originalValue);

		List<?> list =voucherRepository.queryBySqlSC(sql.toString(), params);
		List resultList=new ArrayList();
		for (Object obj : list) {
			Map map = new HashMap();
			map.putAll((Map) obj);
			List<?> list2 = null;
			String currentId = map.get("id").toString();
			if (Integer.valueOf(map.get("childNum").toString())!=0) {
				if (needIds!=null && needIds.size()>0 && needIds.contains(currentId)) {
					//needIds 有数据表示模糊查询
					//存在下级（表示是需要的非末级），那么其所有下级均是需要的
					list2 = qrySpecialTreeBySuperSpecialChildren((Integer) map.get("id"),"",needIds);
				} else {
					list2 = qrySpecialTreeBySuperSpecialChildren((Integer) map.get("id"),inputValue,needIds);
				}
			}

			map.put("id",map.get("mid"));
			map.put("text",map.get("text"));
			if(list2!=null&&list2.size()!=0){
				map.put("children",list2);
				if (!(inputValue!=null&&!"".equals(inputValue))) {
					map.put("state","closed");
				}
				resultList.add(map);
			} else if ("0".equals(map.get("endFlag"))){
				//无子级，但为末级
				if (inputValue!=null&&!"".equals(inputValue)) {
					if (needIds.contains(currentId)) {
						resultList.add(map);
					}
				} else {
					resultList.add(map);
				}
			} else {
				//不需要
			}
		}
		System.out.println("根据一级专项查询专项树用时："+(System.currentTimeMillis()-time)+"ms");
		return resultList;
	}

	@Override
	public List<?> qrySpecialTreeUseFlagBySuperSpecial(String originalValue, String inputValue) {
		long time = System.currentTimeMillis();
		String accBookCode = CurrentUser.getCurrentLoginAccount();
		if (inputValue!=null&&!"".equals(inputValue) && originalValue.equals(inputValue)) {
			inputValue = "";
		} else if (inputValue!=null&&!"".equals(inputValue) && inputValue.startsWith(originalValue)){
			inputValue = inputValue.substring(originalValue.length());
		}
		//先查询出需要的
		Set<String> needIds = new HashSet<>();
		if (inputValue!=null&&!"".equals(inputValue)) {
			StringBuffer needSql = new StringBuffer("select s.id as id from specialinfo s where 1=1");
			needSql.append(" and s.account = ?1");
			needSql.append(" and s.special_code like ?2");
			needSql.append(" and (s.special_name like ?3 or s.special_namep like ?3 or s.special_code like ?4 )");

			Map<Integer, Object> params = new HashMap<>();
			params.put(1, accBookCode);
			params.put(2, originalValue+"%");
			params.put(3, "%"+inputValue+"%");
			params.put(4, originalValue+inputValue+"%");

			List<?> needList =voucherRepository.queryBySqlSC(needSql.toString(), params);
			if (needList!=null&&needList.size()>0) {
				for (Object o:needList) {
					needIds.add(((Map)o).get("id").toString());
				}
			}
		}
		StringBuffer sql = new StringBuffer("select s.id,s.special_code as mid,s.special_namep as text,s.endflag as endFlag,(select count(ss.id) from specialinfo ss where ss.account = s.account and ss.useflag = s.useflag and ss.super_special = s.id) as childNum from specialinfo s where (s.super_special is null or s.super_special = '')");
		sql.append(" and s.account = ?1");
		sql.append(" and s.special_code = ?2 order by s.special_code");

		Map<Integer, Object> params = new HashMap<>();
		params.put(1, accBookCode);
		params.put(2, originalValue);

		List<?> list =voucherRepository.queryBySqlSC(sql.toString(), params);
		List resultList=new ArrayList();
		for (Object obj : list) {
			Map map = new HashMap();
			map.putAll((Map) obj);
			List<?> list2 = null;
			String currentId = map.get("id").toString();
			if (Integer.valueOf(map.get("childNum").toString())!=0) {
				if (needIds!=null && needIds.size()>0 && needIds.contains(currentId)) {
					//needIds 有数据表示模糊查询
					//存在下级（表示是需要的非末级），那么其所有下级均是需要的
					list2 = qrySpecialTreeChooseBySuperSpecialChildren((Integer) map.get("id"),"",needIds);
				} else {
					list2 = qrySpecialTreeChooseBySuperSpecialChildren((Integer) map.get("id"),inputValue,needIds);
				}
			}

			map.put("id",map.get("mid"));
			map.put("text",map.get("text"));
			if(list2!=null&&list2.size()!=0){
				map.put("children",list2);
				if (!(inputValue!=null&&!"".equals(inputValue))) {
					map.put("state","closed");
				}
				resultList.add(map);
			} else if ("0".equals(map.get("endFlag"))){
				//无子级，但为末级
				if (inputValue!=null&&!"".equals(inputValue)) {
					if (needIds.contains(currentId)) {
						resultList.add(map);
					}
				} else {
					resultList.add(map);
				}
			} else {
				//不需要
			}
		}
		System.out.println("根据一级专项查询专项树用时："+(System.currentTimeMillis()-time)+"ms");
		return resultList;
	}



	private List<MenuInfoDTO> qrySpecialTreeChooseBySuperSpecialChildren(Integer id,String inputValue,Set<String> needIds){
		String accBookCode = CurrentUser.getCurrentLoginAccount();
		List list1=new ArrayList();
		if (inputValue!=null&&!"".equals(inputValue)) {
			if (needIds!=null && needIds.size()>0) {
				StringBuffer sql = new StringBuffer("select s.id,s.special_code as mid,s.special_namep as text,s.endflag as endFlag,(select count(ss.id) from specialinfo ss where ss.account = s.account and ss.useflag = s.useflag and ss.super_special = s.id) as childNum from specialinfo s where s.account = ?1");
				sql.append(" and s.super_special = ?2");
				sql.append(" and s.useflag = '1' ");	// 新增条件，去掉弃用的进行展示
				sql.append(" order by s.special_code");

				Map<Integer, Object> params = new HashMap<>();
				params.put(1, accBookCode);
				params.put(2, id);

				List<?> list =voucherRepository.queryBySqlSC(sql.toString(), params);
				if(list!=null&&list.size()>0&&!list.isEmpty()){
					for (Object obj : list) {
						Map map = new HashMap();
						map.putAll((Map) obj);
						List<?> list2 = null;
						String currentId = map.get("id").toString();
						if (Integer.valueOf(map.get("childNum").toString())!=0) {
							if (needIds.contains(currentId)){
								//存在下级（表示是需要的非末级），那么其所有下级均是需要的
								list2 = qrySpecialTreeBySuperSpecialChildren((Integer)map.get("id"),"",needIds);
							} else {
								list2 = qrySpecialTreeBySuperSpecialChildren((Integer)map.get("id"),inputValue,needIds);
							}
						}
						map.put("id",map.get("mid"));
						map.put("text",map.get("text"));
						if(list2!=null&&list2.size()>0&&!list2.isEmpty()){
							map.put("id",map.get("mid"));
							map.put("text",map.get("text"));
							map.put("children",list2);
							list1.add(map);
						} else if ("0".equals(map.get("endFlag")) && needIds.contains(currentId)) {
							//无子级，但为末级
							map.put("id",map.get("mid"));
							map.put("text",map.get("text"));
							list1.add(map);
						} else {
							//不需要
						}
					}
				}
			}
		} else {
			StringBuffer sql = new StringBuffer("select s.id,s.special_code as mid,s.special_namep as text,s.endflag as endFlag,(select count(ss.id) from specialinfo ss where ss.account = s.account and ss.useflag = s.useflag and ss.super_special = s.id) as childNum from specialinfo s where s.account = ?1");
			sql.append(" and s.super_special = ?2");
			sql.append(" and s.useflag = '1' ");	// 新增条件，去掉弃用的进行展示
			sql.append(" order by s.special_code");

			Map<Integer, Object> params = new HashMap<>();
			params.put(1, accBookCode);
			params.put(2, id);

			List<?> list =voucherRepository.queryBySqlSC(sql.toString(), params);
			if(list!=null&&list.size()>0&&!list.isEmpty()){
				for (Object obj : list) {
					Map map = new HashMap();
					map.putAll((Map) obj);
					List<?> list2 = null;
					if (Integer.valueOf(map.get("childNum").toString())!=0) {
						list2 = qrySpecialTreeBySuperSpecialChildren((Integer)map.get("id"),inputValue,needIds);
					}
					map.put("id",map.get("mid"));
					map.put("text",map.get("text"));
					if(list2!=null&&list2.size()>0&&!list2.isEmpty()){
						map.put("children",list2);
						//inputValue 无值，needIds有数据时使模糊查询的一部分调用
						if (needIds==null ||needIds.size()==0) {
							map.put("state","closed");
						}
						list1.add(map);
					} else if ("0".equals(map.get("endFlag"))){
						//无子级，但为末级
						list1.add(map);
					} else {
						//不需要
					}
				}
			}
		}
		return list1;
	}

	private List<MenuInfoDTO> qrySpecialTreeBySuperSpecialChildren(Integer id,String inputValue,Set<String> needIds){
		String accBookCode = CurrentUser.getCurrentLoginAccount();
		List list1=new ArrayList();
		if (inputValue!=null&&!"".equals(inputValue)) {
			if (needIds!=null && needIds.size()>0) {
				StringBuffer sql = new StringBuffer("select s.id,s.special_code as mid,s.special_namep as text,s.endflag as endFlag,(select count(ss.id) from specialinfo ss where ss.account = s.account and ss.useflag = s.useflag and ss.super_special = s.id) as childNum from specialinfo s where s.account = ?1");
				sql.append(" and s.super_special = ?2");
				sql.append(" order by s.special_code");

				Map<Integer, Object> params = new HashMap<>();
				params.put(1, accBookCode);
				params.put(2, id);

				List<?> list =voucherRepository.queryBySqlSC(sql.toString(), params);
				if(list!=null&&list.size()>0&&!list.isEmpty()){
					for (Object obj : list) {
						Map map = new HashMap();
						map.putAll((Map) obj);
						List<?> list2 = null;
						String currentId = map.get("id").toString();
						if (Integer.valueOf(map.get("childNum").toString())!=0) {
							if (needIds.contains(currentId)){
								//存在下级（表示是需要的非末级），那么其所有下级均是需要的
								list2 = qrySpecialTreeBySuperSpecialChildren((Integer)map.get("id"),"",needIds);
							} else {
								list2 = qrySpecialTreeBySuperSpecialChildren((Integer)map.get("id"),inputValue,needIds);
							}
						}
						map.put("id",map.get("mid"));
						map.put("text",map.get("text"));
						if(list2!=null&&list2.size()>0&&!list2.isEmpty()){
							map.put("id",map.get("mid"));
							map.put("text",map.get("text"));
							map.put("children",list2);
							list1.add(map);
						} else if ("0".equals(map.get("endFlag")) && needIds.contains(currentId)) {
							//无子级，但为末级
							map.put("id",map.get("mid"));
							map.put("text",map.get("text"));
							list1.add(map);
						} else {
							//不需要
						}
					}
				}
			}
		} else {
			StringBuffer sql = new StringBuffer("select s.id,s.special_code as mid,s.special_namep as text,s.endflag as endFlag,(select count(ss.id) from specialinfo ss where ss.account = s.account and ss.useflag = s.useflag and ss.super_special = s.id) as childNum from specialinfo s where s.account = ?1");
			sql.append(" and s.super_special = ?2");
			sql.append(" order by s.special_code");

			Map<Integer, Object> params = new HashMap<>();
			params.put(1, accBookCode);
			params.put(2, id);

			List<?> list =voucherRepository.queryBySqlSC(sql.toString(), params);
			if(list!=null&&list.size()>0&&!list.isEmpty()){
				for (Object obj : list) {
					Map map = new HashMap();
					map.putAll((Map) obj);
					List<?> list2 = null;
					if (Integer.valueOf(map.get("childNum").toString())!=0) {
						list2 = qrySpecialTreeBySuperSpecialChildren((Integer)map.get("id"),inputValue,needIds);
					}
					map.put("id",map.get("mid"));
					map.put("text",map.get("text"));
					if(list2!=null&&list2.size()>0&&!list2.isEmpty()){
						map.put("children",list2);
						//inputValue 无值，needIds有数据时使模糊查询的一部分调用
						if (needIds==null ||needIds.size()==0) {
							map.put("state","closed");
						}
						list1.add(map);
					} else if ("0".equals(map.get("endFlag"))){
						//无子级，但为末级
						list1.add(map);
					} else {
						//不需要
					}
				}
			}
		}
		return list1;
	}

	/**
	 * 根据科目信息获取匹配的专项信息
	 * 存在问题：
	 * 1.筛选条件需要添加账套的筛选
	 * @param subjectCode
	 * @return
	 */
	public List<?> getSpecialCode(String subjectCode){
		List<?> resultList = null;
		StringBuffer sql = new StringBuffer();
		StringBuffer sql1 = new StringBuffer();
		String sql2 = "SELECT SUBSTRING_INDEX(SUBSTRING_INDEX((stringSql),',',s.id+1),',',-1) AS specialIds FROM splitstringsort s WHERE s.id < LENGTH((stringSql))- LENGTH(REPLACE((stringSql),',',''))+1";
		sql1.append("SELECT s.special_id AS specialIds FROM subjectinfo s WHERE ");
		/*sql.append("select s.special_id as id,");
		sql.append("(select p.special_code from specialinfo p where p.id=s.special_id) as specialCode,");
		sql.append("(select p.special_name from specialinfo p where p.id=s.special_id) as specialName ");
		sql.append("from subjectinfo s where s.account='"+CurrentUser.getCurrentLoginAccount()+"' and concat_ws(\"\",s.all_subject,s.subject_code)= '"+subjectCode.substring(0,subjectCode.length()-1)+"' ");*/
		sql1.append(" s.account = ?1 AND concat_ws(\"\",s.all_subject,s.subject_code)= ?2 ");
		sql2 = sql2.replaceAll("stringSql", sql1.toString());
		sql.append("SELECT s.id AS id,s.special_code AS specialCode,s.special_name AS specialName FROM specialinfo s");
		sql.append(" WHERE s.id IN (" + sql2 + ")");
		sql.append(" ORDER BY s.id");

		Map<Integer, Object> params = new HashMap<>();
		params.put(1, CurrentUser.getCurrentLoginAccount());
		params.put(2, subjectCode.substring(0,subjectCode.length()-1));

		List<?> list =voucherRepository.queryBySqlSC(sql.toString(), params);
		/*VoucherDTO dto = new VoucherDTO();
		Map map = new HashMap();
		if(list!=null&&list.size()!=0){
			map.putAll((Map) list.get(0));
			dto.setId((String)map.get("id"));
			dto.setSpecialCode((String)map.get("specialCode"));
			dto.setSpecialName((String)map.get("specialName"));
		}
		return dto;*/
		return list;
	}

	@Override
	public VoucherDTO getSpecialDateBySpecialCode(String specialCode){
		StringBuffer sql = new StringBuffer("SELECT s.id AS id,s.special_code AS specialCode,s.special_name AS specialName,s.special_namep AS specialNameP,s.super_special AS superSpecial,s.endflag AS endFlag FROM specialinfo s WHERE");
		sql.append(" s.account = ?1");
		// 加入“BINARY” 将严格区分大小写
		sql.append(" AND BINARY s.special_code = ?2");

		Map<Integer, Object> params = new HashMap<>();
		params.put(1, CurrentUser.getCurrentLoginAccount());
		params.put(2, specialCode);

		List<?> list =voucherRepository.queryBySqlSC(sql.toString(), params);
		VoucherDTO dto = new VoucherDTO();
		Map map = new HashMap();
		if(list!=null&&list.size()!=0){
			map.putAll((Map) list.get(0));
			dto.setId(String.valueOf((Integer)map.get("id")));
			dto.setSpecialCode((String)map.get("specialCode"));
			dto.setSpecialName((String)map.get("specialName"));
			dto.setSpecialNameP((String)map.get("specialNameP"));//全称
			dto.setId((String)map.get("superSpecial"));//专项上级ID
			dto.setEndFlag((String)map.get("endFlag"));// 0-末级，1-非末级
		}
		return dto;
	}

	/**
	 * 根据筛选条件进行凭证信息查询
	 * 存在问题：
	 * 1.筛选条件需要添加账套的筛选
	 * 2.筛选条件需要添加基层单位
	 * 3.筛选条件需要添加核算单位
	 * 账套类型不确定是否需要添加
	 * @param dto
	 * @return
	 */
	public List<?>  getCopyVoucher(VoucherDTO dto){
		String centerCode = CurrentUser.getCurrentLoginManageBranch();
		String branchCode = CurrentUser.getCurrentLoginManageBranch();
		String accBookType = CurrentUser.getCurrentLoginAccountType();
		String accBookCode = CurrentUser.getCurrentLoginAccount();

		//需要判断 可能需要去历史表中去查询
		StringBuffer sql=new StringBuffer();
		sql.append("select a.voucher_no as voucherNo,a.year_month_date as yearMonth,");
		sql.append("(select c.code_name from codemanage c where c.code_code=a.voucher_type and c.code_type='voucherType') as voucherType,");
		sql.append("(select c.code_name from codemanage c where c.code_code=a.voucher_flag and c.code_type='voucherFlag')as voucherFlag, ");
		sql.append("a.voucher_date as voucherDate, a.aux_number as auxNumber, u.user_name AS createBy, ");
		sql.append("(select SUM(s.debit_dest) from accsubvoucher s where s.center_code = a.center_code and s.branch_code = a.branch_code and s.acc_book_type = a.acc_book_type and s.acc_book_code = a.acc_book_code and s.year_month_date=a.year_month_date and s.voucher_no=a.voucher_no) as source,  ");
		sql.append("(select s.remark from accsubvoucher s where s.center_code = a.center_code and s.branch_code = a.branch_code and s.acc_book_type = a.acc_book_type and s.acc_book_code = a.acc_book_code and s.year_month_date=a.year_month_date and s.voucher_no=a.voucher_no and s.remark is not null and s.remark != '' order by s.suffix_no limit 1) as remark  ");
		sql.append("from accmainvoucher a left join userinfo u on u.id = a.create_by where 1=1");

		int paramsNo = 1;
		Map<Integer, Object> params = new HashMap<>();

		sql.append(" and a.center_code = ?" + paramsNo);
		params.put(paramsNo, centerCode);
		paramsNo++;
		sql.append(" and a.branch_code = ?" + paramsNo);
		params.put(paramsNo, branchCode);
		paramsNo++;
		sql.append(" and a.acc_book_type = ?" + paramsNo);
		params.put(paramsNo, accBookType);
		paramsNo++;
		sql.append(" and a.acc_book_code = ?" + paramsNo);
		params.put(paramsNo, accBookCode);
		paramsNo++;

		if(dto.getYearMonth()!=null&&!dto.getYearMonth().equals("")){
			sql.append(" and a.year_month_date >=?" + paramsNo);
			params.put(paramsNo, dto.getYearMonth());
			paramsNo++;
		}
		if(dto.getYearMonthDate()!=null&&!"".equals(dto.getYearMonthDate())){
			sql.append(" and a.year_month_date <= ?" + paramsNo);
			params.put(paramsNo, dto.getYearMonthDate());
			paramsNo++;
		}
		if(dto.getVoucherNo()!=null&&!dto.getVoucherNo().equals("")){
			String voucherNo = "";
			if (dto.getVoucherNo().length()!=(10+centerCode.length())) {
				voucherNo = centerCode+dto.getYearMonth().substring(2,6)+String.format("%06d", Integer.parseInt(dto.getVoucherNo()));
			} else {
				voucherNo = dto.getVoucherNo();
			}
			sql.append(" and a.voucher_no = ?" + paramsNo);
			params.put(paramsNo, voucherNo);
			paramsNo++;
		}
		if (dto.getCreateBy()!=null&&!"".equals(dto.getCreateBy())) {
			sql.append(" and u.user_name like ?" + paramsNo);
			params.put(paramsNo, "%"+dto.getCreateBy()+"%");
			paramsNo++;
		}
		if (dto.getVoucherDateStart()!=null&&!"".equals(dto.getVoucherDateStart())) {
			sql.append(" and a.voucher_date >= ?" + paramsNo);
			params.put(paramsNo, dto.getVoucherDateStart());
			paramsNo++;
		}
		if (dto.getVoucherDateEnd()!=null&&!"".equals(dto.getVoucherDateEnd())) {
			sql.append(" and a.voucher_date <= ?" + paramsNo);
			params.put(paramsNo, dto.getVoucherDateEnd());
			paramsNo++;
		}
		if (dto.getSubjectCode()!=null&&!"".equals(dto.getSubjectCode())) {
			sql.append(" and a.voucher_no = (SELECT distinct s.voucher_no FROM accsubvoucher s where s.center_code=a.center_code and  s.acc_book_code=a.acc_book_code and s.year_month_date=a.year_month_date and s.voucher_no=a.voucher_no and s.direction_idx like ?" + paramsNo + " )");
			params.put(paramsNo, dto.getSubjectCode()+"%");
			paramsNo++;
		}
		if (dto.getRemarkName()!=null&&!"".equals(dto.getRemarkName())) {
			sql.append(" and a.voucher_no = (SELECT distinct s.voucher_no FROM accsubvoucher s where s.center_code=a.center_code and s.acc_book_code=a.acc_book_code and s.year_month_date=a.year_month_date and s.voucher_no=a.voucher_no and s.remark like ?" + paramsNo + " )");
			params.put(paramsNo, "%"+dto.getRemarkName()+"%");
			paramsNo++;
		}

		//联查历史表
		String hisSql = sql.toString();
		hisSql = hisSql.replaceAll("accsubvoucher","accsubvoucherhis");
		hisSql = hisSql.replaceAll("s\\.","sh\\.");
		hisSql = hisSql.replaceAll(" s "," sh ");
		hisSql = hisSql.replaceAll("accmainvoucher","accmainvoucherhis");
		hisSql = hisSql.replaceAll("a\\.","ah\\.");
		hisSql = hisSql.replaceAll(" a "," ah ");

		sql.append(" union all ");
		sql.append(hisSql);

		sql.append(" ORDER BY yearMonth,voucherNo ");
		return voucherRepository.queryBySqlSC(sql.toString(), params);
	}

	/**
	 * 根据选择凭证查询凭证详细信息并加载到录入页面
	 * 存在问题：
	 * 1.筛选条件需要添加账套的筛选
	 * 2.筛选条件需要添加基层单位
	 * 3.筛选条件需要添加核算单位
	 * 账套类型不确定是否需要添加
	 * @param dto
	 * @return
	 */
	@Override
	public VoucherDTO getCopyData(VoucherDTO dto) {
		String centerCode = CurrentUser.getCurrentLoginManageBranch();
		String branchCode = CurrentUser.getCurrentLoginManageBranch();
		String accBookType = CurrentUser.getCurrentLoginAccountType();
		String accBookCode = CurrentUser.getCurrentLoginAccount();

		StringBuffer sql=new StringBuffer("select * from accsubvoucher a where a.center_code = ?1 and a.branch_code = ?2 and a.acc_book_type = ?3 and a.acc_book_code = ?4 and a.year_month_date = ?5 and a.voucher_no = ?6 order by suffix_no ");

		Map<Integer, Object> params = new HashMap<>();
		params.put(1, centerCode);
		params.put(2, branchCode);
		params.put(3, accBookType);
		params.put(4, accBookCode);
		params.put(5, dto.getYearMonth());
		params.put(6, dto.getVoucherNo());

		List<AccSubVoucher> list = (List<AccSubVoucher>)voucherSubRepository.queryBySql(sql.toString(), params, AccSubVoucher.class);
		boolean hisFlag = false;//用于标记后续是否从历史表查询
		if (!(list!=null&&list.size()>0)) {
			hisFlag = true;
			sql.setLength(0);
			sql.append("select * from accsubvoucherhis a where a.center_code = ?1 and a.branch_code = ?2 and a.acc_book_type = ?3 and a.acc_book_code = ?4 and a.year_month_date = ?5 and a.voucher_no = ?6 order by suffix_no ");

			params = new HashMap<>();
			params.put(1, centerCode);
			params.put(2, branchCode);
			params.put(3, accBookType);
			params.put(4, accBookCode);
			params.put(5, dto.getYearMonth());
			params.put(6, dto.getVoucherNo());

			list = (List<AccSubVoucher>)voucherSubRepository.queryBySql(sql.toString(), params, AccSubVoucher.class);
		}
		List<VoucherDTO> list1 = new ArrayList<>();
		List<VoucherDTO> list2 = new ArrayList<>();
		for(int i=0;i<list.size();i++){
			AccSubVoucher accSubVoucher=list.get(i);
			VoucherDTO ddd= new VoucherDTO();
			//标注
			if (accSubVoucher.getFlag()!=null&&!"".equals(accSubVoucher.getFlag())) {
                String flag[] = accSubVoucher.getFlag().split(",");
                String flagName = "";
                for (String s : flag) {
                    List<?> result = accTagManageRespository.findByCenterCodeAndAccBookCodeAndAccBookTypeAndTagCode(CurrentUser.getCurrentLoginManageBranch(), CurrentUser.getCurrentLoginAccount(), CurrentUser.getCurrentLoginAccountType(), s);
                    if (result!=null&&result.size()>0) {
                        AccTagManage tagManage = (AccTagManage)result.get(0);
                        flagName += tagManage.getTagName() + ",";
                    }
                }
                ddd.setTagCode(flagName.substring(0,flagName.length()-1));
                ddd.setTagCodeS(accSubVoucher.getFlag());
            }

			ddd.setSubjectCode(accSubVoucher.getDirectionIdx());
			ddd.setSubjectName(accSubVoucher.getDirectionIdxName());
			//如果为1则为凭证复制 如果为2则为凭证对冲 如果为3则为凭证查看或编辑
			if(dto.getCopyType().equals("1") || "3".equals(dto.getCopyType())){
				ddd.setRemarkName(accSubVoucher.getRemark());
				if (accSubVoucher.getDebitDest()!=null&&!"".equals(accSubVoucher.getDebitDest())) {
                    ddd.setDebit(accSubVoucher.getDebitDest().toString());
                }
                if (accSubVoucher.getCreditDest()!=null&&!"".equals(accSubVoucher.getCreditDest())) {
                    ddd.setCredit(accSubVoucher.getCreditDest().toString());
                }
			}else if(dto.getCopyType().equals("2")){
				ddd.setRemarkName("对冲"+accSubVoucher.getId().getVoucherNo()+"号凭证");
				if(accSubVoucher.getDebitDest()!=null&&accSubVoucher.getDebitDest().compareTo(BigDecimal.ZERO)!=0){
					ddd.setDebit(accSubVoucher.getDebitDest().negate().toString());
				}else{
                    if (accSubVoucher.getDebitDest()!=null&&!"".equals(accSubVoucher.getDebitDest())) {
                        ddd.setCredit(accSubVoucher.getDebitDest().toString());
                    }
				}
				if(accSubVoucher.getCreditDest()!=null&&accSubVoucher.getCreditDest().compareTo(BigDecimal.ZERO)!=0){
					ddd.setCredit(accSubVoucher.getCreditDest().negate().toString());
				}else{
					if (accSubVoucher.getCreditDest()!=null&&!"".equals(accSubVoucher.getCreditDest())) {
                        ddd.setCredit(accSubVoucher.getCreditDest().toString());
                    }
				}
			}
			list1.add(ddd);//将科目信息塞进list1
			VoucherDTO hhh= new VoucherDTO();
			//科目方向段
			hhh.setSubjectCodeS(accSubVoucher.getDirectionIdx());
			hhh.setSubjectNameS(accSubVoucher.getDirectionIdxName());
			hhh.setCheckNo(accSubVoucher.getCheckNo()); // 支票号
			hhh.setInvoiceNo(accSubVoucher.getInvoiceNo()); // 发票号
			//专项方向段
			if (accSubVoucher.getDirectionOther()!=null&&!"".equals(accSubVoucher.getDirectionOther())) {
				String[] str = accSubVoucher.getDirectionOther().split(",");
				String string = "";
				String specialSuperCodeS = "";
				for (String s: str) {
					VoucherDTO d = getSpecialDateBySpecialCode(s);
					if (dto.getSpecialNameP()!=null&&"1".equals(dto.getSpecialNameP())) {
						//显示专项全称
						string = string + s + ";" + d.getSpecialNameP() + ",";
					} else {
						//显示专项简称
						string = string + s + ";" + d.getSpecialName() + ",";
					}
                    String specialSuperCodeSql = "SELECT a.segment_col AS 'segmentCol' FROM accsegmentdefine a WHERE LEFT(a.segment_col,2) = LEFT( ?1 ,2)";

					params = new HashMap<>();
					params.put(1, s);

					List<?> specialSuperCodeList = voucherRepository.queryBySqlSC(specialSuperCodeSql, params);
                    if(specialSuperCodeList!=null&&specialSuperCodeList.size()!=0){
                        Map<String,Object> map = (Map<String,Object>) specialSuperCodeList.get(0);
                        specialSuperCodeS = specialSuperCodeS + map.get("segmentCol") + ",";
                    }
				}
				accSubVoucher.setDirectionOther(string.substring(0,string.length()-1));
				if (!"".equals(specialSuperCodeS)) {
				    hhh.setSpecialSuperCodeS(specialSuperCodeS.substring(0,specialSuperCodeS.length()-1));
                }
			} else {
				accSubVoucher.setDirectionOther("无关联专项");
			}
			hhh.setSpecialCodeS(accSubVoucher.getDirectionOther());
			//设置特殊信息
			if (accSubVoucher.getD01()!=null&&!"".equals(accSubVoucher.getD01())) {
				hhh.setUnitPrice(accSubVoucher.getD01());
			} else {
				hhh.setUnitPrice("");
			}
			if (accSubVoucher.getD02()!=null&&!"".equals(accSubVoucher.getD02())) {
				hhh.setUnitNum(accSubVoucher.getD02());
			} else {
				hhh.setUnitNum("");
			}
			if (accSubVoucher.getD03()!=null&&!"".equals(accSubVoucher.getD03())) {
				hhh.setSettlementType(accSubVoucher.getD03());
				List<?> settlementName = codeSelectRepository.findCodeSelectInfo("settlementType",accSubVoucher.getD03());
				if (settlementName!=null&&settlementName.size()>0) {
					hhh.setSettlementTypeName(((Map<String,Object>)settlementName.get(0)).get("codeName").toString());
				}
			} else {
				hhh.setSettlementType("");
				hhh.setSettlementTypeName("");
			}
			if (accSubVoucher.getD04()!=null&&!"".equals(accSubVoucher.getD04())) {
				hhh.setSettlementNo(accSubVoucher.getD04());
			} else {
				hhh.setSettlementNo("");
			}
			if (accSubVoucher.getD05()!=null&&!"".equals(accSubVoucher.getD05())) {
				hhh.setSettlementDate(accSubVoucher.getD05());
			} else {
				hhh.setSettlementDate("");
			}
			list2.add(hhh);//将专项信息塞进list2
		}
		if ("3".equals(dto.getCopyType())) {
			//查看或编辑时还需要凭证主表信息
			List<AccMainVoucher> mainList = new ArrayList<AccMainVoucher>();
			StringBuffer mainSql = new StringBuffer();
			if (!hisFlag) {
				mainSql.append("SELECT * FROM accmainvoucher a WHERE a.center_code= ?1 AND a.branch_code = ?2 AND a.acc_book_type = ?3 AND a.acc_book_code = ?4 AND a.year_month_date = ?5 AND a.voucher_no = ?6 ");
			} else {
				mainSql.append("SELECT * FROM accmainvoucherhis a WHERE a.center_code= ?1 AND a.branch_code = ?2 AND a.acc_book_type = ?3 AND a.acc_book_code = ?4 AND a.year_month_date = ?5 AND a.voucher_no = ?6 ");
			}

			params = new HashMap<>();
			params.put(1, centerCode);
			params.put(2, branchCode);
			params.put(3, accBookType);
			params.put(4, accBookCode);
			params.put(5, dto.getYearMonth());
			params.put(6, dto.getVoucherNo());

			mainList = (List<AccMainVoucher>) accMainVoucherRespository.queryBySql(mainSql.toString(), params, AccMainVoucher.class);
			if (mainList!=null&&mainList.size()>0) {
				AccMainVoucher accMainVoucher = mainList.get(0);
				//制单日期
				dto.setVoucherDate(accMainVoucher.getVoucherDate());
				//附件张数
				if (accMainVoucher.getAuxNumber()!=null && accMainVoucher.getAuxNumber()!=0) {
					dto.setAuxNumber(String.valueOf(accMainVoucher.getAuxNumber()));
				} else {
					dto.setAuxNumber("");
				}
				//制单人
				dto.setCreateBy(userInfoRepository.findById(Long.parseLong(accMainVoucher.getCreateBy())).get().getUserName());
				//凭证状态
				if (accMainVoucher.getVoucherFlag()!=null&&!"".equals(accMainVoucher.getVoucherFlag())) {
					List<?> voucherFlagName = codeSelectRepository.findCodeSelectInfo("voucherFlag",accMainVoucher.getVoucherFlag());
					if (voucherFlagName!=null&&voucherFlagName.size()>0) {
						dto.setVoucherFlag(((Map<String,Object>)voucherFlagName.get(0)).get("codeName").toString());
					}
				}
				//凭证录入方式
				if (accMainVoucher.getGenerateWay()!=null&&!"".equals(accMainVoucher.getGenerateWay())) {
					dto.setGenerateWay(accMainVoucher.getGenerateWay());
				}
				//凭证类型
				if (accMainVoucher.getVoucherType()!=null&&!"".equals(accMainVoucher.getVoucherType())) {
					dto.setVoucherType(accMainVoucher.getVoucherType());
				}
				//复核人
				if (accMainVoucher.getApproveBy()!=null&&!"".equals(accMainVoucher.getApproveBy())) {
					dto.setApproveBy(userInfoRepository.findById(Long.parseLong(accMainVoucher.getApproveBy())).get().getUserName());
				} else {
					dto.setApproveBy("");
				}
				//记账人
				if (accMainVoucher.getGeneBy()!=null&&!"".equals(accMainVoucher.getGeneBy())) {
					dto.setGeneBy(userInfoRepository.findById(Long.parseLong(accMainVoucher.getGeneBy())).get().getUserName());
				} else {
					dto.setGeneBy("");
				}
			}
		}
		dto.setData2(list1);
		dto.setData3(list2);
		return dto;
	}

    /**
     * 根据会计期间、凭证号进行凭证信息的查询（上一张、下一张）
     * @param dto
     * @param beforeOrAfter 参数：before-上一张，after-下一张
     * @param type 参数：look-查看，edit-编辑
     * @return
     */
	@Override
	public VoucherDTO beforeOrAfterVoucher(VoucherDTO dto, String beforeOrAfter, String type) {
		String centerCode = CurrentUser.getCurrentLoginManageBranch();
		String branchCode = CurrentUser.getCurrentLoginManageBranch();
		String accBookType = CurrentUser.getCurrentLoginAccountType();
		String accBookCode = CurrentUser.getCurrentLoginAccount();

		if (dto.getVoucherNo()!=null&&!"".equals(dto.getVoucherNo())) {
			int voucherNo = Integer.parseInt(dto.getVoucherNo().substring(centerCode.length()+4));
			if (beforeOrAfter!=null&&"before".equals(beforeOrAfter)) {
				if (voucherNo==1) {
					VoucherDTO voucherDTO = new VoucherDTO();
					voucherDTO.setTemp("已经是第一张凭证了！");
					return voucherDTO;
				}
			} else if (beforeOrAfter!=null&&"after".equals(beforeOrAfter)){
				voucherNo += 1;
				AccVoucherNoId accVoucherNoId = new AccVoucherNoId();
				accVoucherNoId.setYearMonthDate(dto.getYearMonth());
				accVoucherNoId.setAccBookCode(accBookCode);
				accVoucherNoId.setAccBookType(accBookType);
				accVoucherNoId.setCenterCode(centerCode);
				AccVoucherNo accVoucherNo = accVoucherNoRespository.findById(accVoucherNoId).get();
				if (voucherNo>=Integer.parseInt(accVoucherNo.getVoucherNo())) {
					VoucherDTO voucherDTO = new VoucherDTO();
					voucherDTO.setTemp("已经是最后一张凭证了！");
					return voucherDTO;
				}
			} else {
				VoucherDTO voucherDTO = new VoucherDTO();
				voucherDTO.setTemp("参数错误！");
				return voucherDTO;
			}

            boolean hisFlag = false;//用于标记后续是否从历史表查询
            StringBuffer mainSql = new StringBuffer();
            mainSql.append("SELECT * FROM accmainvoucher a WHERE 1=1");

			int paramsNo = 1;
			Map<Integer, Object> params = new HashMap<>();

			mainSql.append(" AND a.center_code = ?" + paramsNo);
			params.put(paramsNo, centerCode);
			paramsNo++;
			mainSql.append(" AND a.branch_code = ?" + paramsNo);
			params.put(paramsNo, branchCode);
			paramsNo++;
			mainSql.append(" AND a.acc_book_type = ?" + paramsNo);
			params.put(paramsNo, accBookType);
			paramsNo++;
			mainSql.append(" AND a.acc_book_code = ?" + paramsNo);
			params.put(paramsNo, accBookCode);
			paramsNo++;
			mainSql.append(" AND a.year_month_date = ?" + paramsNo);
			params.put(paramsNo, dto.getYearMonth());
			paramsNo++;

			if (type!=null&&"edit".equals(type)) {
				//只允许编辑未复核的凭证，目前上一张下一张控件只开放手工记账凭证编辑
				mainSql.append(" AND a.voucher_flag = '1' AND a.generate_way = '2' AND a.voucher_type = '2'");
			}
			if (beforeOrAfter!=null&&"before".equals(beforeOrAfter)) {
				mainSql.append(" AND a.voucher_no < ?" + paramsNo);
				mainSql.append(" ORDER BY a.voucher_no DESC");
				params.put(paramsNo, dto.getVoucherNo());
				paramsNo++;
			} else {
				mainSql.append(" AND a.voucher_no > ?" + paramsNo);
				mainSql.append(" ORDER BY a.voucher_no ASC");
				params.put(paramsNo, dto.getVoucherNo());
				paramsNo++;
			}

            List<AccMainVoucher> mainList = (List<AccMainVoucher>) accMainVoucherRespository.queryBySql(mainSql.toString(), params, AccMainVoucher.class);
            AccMainVoucher accMainVoucher = null;
            if (!(mainList!=null&&mainList.size()>0)) {
            	if (type!=null&&"edit".equals(type)) {
					VoucherDTO voucherDTO = new VoucherDTO();
					if (beforeOrAfter!=null&&"before".equals(beforeOrAfter)) {
						voucherDTO.setTemp("已经是第一张可编辑的凭证了！");
					} else {
						voucherDTO.setTemp("已经是最后一张可编辑的凭证了！");
					}
					return voucherDTO;
				} else {
					hisFlag = true;
					String mainSql2 = mainSql.toString();
					mainSql2 = mainSql2.replaceAll("accmainvoucher", "accmainvoucherhis");
					mainList = (List<AccMainVoucher>) accMainVoucherRespository.queryBySql(mainSql2, params, AccMainVoucher.class);
					if (!(mainList!=null&&mainList.size()>0)) {
						VoucherDTO voucherDTO = new VoucherDTO();
						if (beforeOrAfter!=null&&"before".equals(beforeOrAfter)) {
							voucherDTO.setTemp("已经是第一张凭证了！");
						} else {
							voucherDTO.setTemp("已经是最后一张凭证了！");
						}
						return voucherDTO;
					} else {
						accMainVoucher = mainList.get(0);
					}
				}
            } else {
                accMainVoucher = mainList.get(0);
            }

            StringBuffer sql=new StringBuffer();
            if (!hisFlag) {
                sql.append("select * from accsubvoucher a where 1=1");
            } else {
                sql.append("select * from accsubvoucherhis a where 1=1");
            }
            sql.append(" and a.center_code = ?1 and a.branch_code = ?2 and a.acc_book_type = ?3 and a.acc_book_code = ?4 and a.year_month_date = ?5 and a.voucher_no = ?6 order by suffix_no");

			params = new HashMap<>();
			params.put(1, centerCode);
			params.put(2, branchCode);
			params.put(3, accBookType);
			params.put(4, accBookCode);
			params.put(5, dto.getYearMonth());
			params.put(6, accMainVoucher.getId().getVoucherNo());

            List<AccSubVoucher> list = (List<AccSubVoucher>)voucherSubRepository.queryBySql(sql.toString(), params, AccSubVoucher.class);

			List<VoucherDTO> list1 = new ArrayList<>();
			List<VoucherDTO> list2 = new ArrayList<>();
			for(int i=0;i<list.size();i++){
				AccSubVoucher accSubVoucher=list.get(i);
				VoucherDTO ddd= new VoucherDTO();
				//标注
				if (accSubVoucher.getFlag()!=null&&!"".equals(accSubVoucher.getFlag())) {
					String flag[] = accSubVoucher.getFlag().split(",");
					String flagName = "";
					for (String s : flag) {
						List<?> result = accTagManageRespository.findByCenterCodeAndAccBookCodeAndAccBookTypeAndTagCode(centerCode, CurrentUser.getCurrentLoginAccount(), CurrentUser.getCurrentLoginAccountType(), s);
						if (result!=null&&result.size()>0) {
							AccTagManage tagManage = (AccTagManage)result.get(0);
							flagName += tagManage.getTagName() + ",";
						}
					}
					ddd.setTagCode(flagName.substring(0,flagName.length()-1));
					ddd.setTagCodeS(accSubVoucher.getFlag());
				}

				ddd.setSubjectCode(accSubVoucher.getDirectionIdx());
				ddd.setSubjectName(accSubVoucher.getDirectionIdxName());

				ddd.setRemarkName(accSubVoucher.getRemark());
				if (accSubVoucher.getDebitDest()!=null&&!"".equals(accSubVoucher.getDebitDest())) {
					ddd.setDebit(accSubVoucher.getDebitDest().toString());
				}
				if (accSubVoucher.getCreditDest()!=null&&!"".equals(accSubVoucher.getCreditDest())) {
					ddd.setCredit(accSubVoucher.getCreditDest().toString());
				}

				list1.add(ddd);//将科目信息塞进list1
				VoucherDTO hhh= new VoucherDTO();
				//科目方向段
				hhh.setSubjectCodeS(accSubVoucher.getDirectionIdx());
				hhh.setSubjectNameS(accSubVoucher.getDirectionIdxName());
				//专项方向段
				if (accSubVoucher.getDirectionOther()!=null&&!"".equals(accSubVoucher.getDirectionOther())) {
					String[] str = accSubVoucher.getDirectionOther().split(",");
					String string = "";
					String specialSuperCodeS = "";
					for (String s: str) {
						VoucherDTO d = getSpecialDateBySpecialCode(s);
						if (dto.getSpecialNameP()!=null&&"1".equals(dto.getSpecialNameP())) {
							//显示专项全称
							string = string + s + ";" + d.getSpecialNameP() + ",";
						} else {
							//显示专项简称
							string = string + s + ";" + d.getSpecialName() + ",";
						}
						String specialSuperCodeSql = "SELECT a.segment_col AS 'segmentCol' FROM accsegmentdefine a WHERE LEFT(a.segment_col,2) = LEFT( ?1 ,2)";

						params = new HashMap<>();
						params.put(1, s);

						List<?> specialSuperCodeList = voucherRepository.queryBySqlSC(specialSuperCodeSql, params);
						if(specialSuperCodeList!=null&&specialSuperCodeList.size()!=0){
							Map<String,Object> map = (Map<String,Object>) specialSuperCodeList.get(0);
							specialSuperCodeS = specialSuperCodeS + map.get("segmentCol") + ",";
						}
					}
					accSubVoucher.setDirectionOther(string.substring(0,string.length()-1));
					if (!"".equals(specialSuperCodeS)) {
						hhh.setSpecialSuperCodeS(specialSuperCodeS.substring(0,specialSuperCodeS.length()-1));
					}
				} else {
					accSubVoucher.setDirectionOther("无关联专项");
				}
				hhh.setSpecialCodeS(accSubVoucher.getDirectionOther());
				//设置特殊信息
				if (accSubVoucher.getD01()!=null&&!"".equals(accSubVoucher.getD01())) {
					hhh.setUnitPrice(accSubVoucher.getD01());
				} else {
					hhh.setUnitPrice("");
				}
				if (accSubVoucher.getD02()!=null&&!"".equals(accSubVoucher.getD02())) {
					hhh.setUnitNum(accSubVoucher.getD02());
				} else {
					hhh.setUnitNum("");
				}
				if (accSubVoucher.getD03()!=null&&!"".equals(accSubVoucher.getD03())) {
					hhh.setSettlementType(accSubVoucher.getD03());
					List<?> settlementName = codeSelectRepository.findCodeSelectInfo("settlementType",accSubVoucher.getD03());
					if (settlementName!=null&&settlementName.size()>0) {
						hhh.setSettlementTypeName(((Map<String,Object>)settlementName.get(0)).get("codeName").toString());
					}
				} else {
					hhh.setSettlementType("");
					hhh.setSettlementTypeName("");
				}
				if (accSubVoucher.getD04()!=null&&!"".equals(accSubVoucher.getD04())) {
					hhh.setSettlementNo(accSubVoucher.getD04());
				} else {
					hhh.setSettlementNo("");
				}
				if (accSubVoucher.getD05()!=null&&!"".equals(accSubVoucher.getD05())) {
					hhh.setSettlementDate(accSubVoucher.getD05());
				} else {
					hhh.setSettlementDate("");
				}
				if (accSubVoucher.getCheckNo()!=null&&!"".equals(accSubVoucher.getCheckNo())) {
					hhh.setCheckNo(accSubVoucher.getCheckNo());//支票号
				} else {
					hhh.setSettlementDate("");
				}
				if (accSubVoucher.getInvoiceNo()!=null&&!"".equals(accSubVoucher.getInvoiceNo())) {
					hhh.setInvoiceNo(accSubVoucher.getInvoiceNo());//发票号
				} else {
					hhh.setSettlementDate("");
				}
				list2.add(hhh);//将专项信息塞进list2
			}

            //查看或编辑时还需要凭证主表信息
			//重新设置 dto 的凭证号
			dto.setVoucherNo(accMainVoucher.getId().getVoucherNo());
            //制单日期
            dto.setVoucherDate(accMainVoucher.getVoucherDate());
            //附件张数
            if (accMainVoucher.getAuxNumber()!=null && accMainVoucher.getAuxNumber()!=0) {
                dto.setAuxNumber(String.valueOf(accMainVoucher.getAuxNumber()));
            } else {
                dto.setAuxNumber("");
            }
            //制单人
            dto.setCreateBy(userInfoRepository.findById(Long.parseLong(accMainVoucher.getCreateBy())).get().getUserName());
            //凭证状态
            if (accMainVoucher.getVoucherFlag()!=null&&!"".equals(accMainVoucher.getVoucherFlag())) {
                List<?> voucherFlagName = codeSelectRepository.findCodeSelectInfo("voucherFlag",accMainVoucher.getVoucherFlag());
                if (voucherFlagName!=null&&voucherFlagName.size()>0) {
                    dto.setVoucherFlag(((Map<String,Object>)voucherFlagName.get(0)).get("codeName").toString());
                }
            }
            //凭证录入方式
            if (accMainVoucher.getGenerateWay()!=null&&!"".equals(accMainVoucher.getGenerateWay())) {
                dto.setGenerateWay(accMainVoucher.getGenerateWay());
            }
            //凭证类型
            if (accMainVoucher.getVoucherType()!=null&&!"".equals(accMainVoucher.getVoucherType())) {
                dto.setVoucherType(accMainVoucher.getVoucherType());
            }
            //复核人
            if (accMainVoucher.getApproveBy()!=null&&!"".equals(accMainVoucher.getApproveBy())) {
                dto.setApproveBy(userInfoRepository.findById(Long.parseLong(accMainVoucher.getApproveBy())).get().getUserName());
            } else {
                dto.setApproveBy("");
            }
            //记账人
            if (accMainVoucher.getGeneBy()!=null&&!"".equals(accMainVoucher.getGeneBy())) {
                dto.setGeneBy(userInfoRepository.findById(Long.parseLong(accMainVoucher.getGeneBy())).get().getUserName());
            } else {
                dto.setGeneBy("");
            }

			dto.setData2(list1);
			dto.setData3(list2);
			return dto;
		} else {
			return null;
		}
	}

	@Override
	@Transactional
	public InvokeResult saveVoucher(List<VoucherDTO> list2, List<VoucherDTO> list3,VoucherDTO dto) {
		String centerCode = CurrentUser.getCurrentLoginManageBranch();
		String branchCode = CurrentUser.getCurrentLoginManageBranch();
		String accBookType = CurrentUser.getCurrentLoginAccountType();
		String accBookCode = CurrentUser.getCurrentLoginAccount();
		synchronized (this) {
			if (dto.getOldVoucherNo()!=null&&!"".equals(dto.getOldVoucherNo())) {
				//占用已有凭证号
				//如果长度为9，则为完整凭证号，否则为有效号（去四位前缀）
				if (dto.getOldVoucherNo().length()!=(10+centerCode.length())) {
					try {
						//拼出完整的凭证号
						dto.setOldVoucherNo(centerCode+dto.getYearMonth().substring(2,6)+String.format("%06d", Integer.parseInt(dto.getOldVoucherNo())));
					} catch (Exception e) {
						logger.error("占用已有凭证号拼接异常", e);
						return InvokeResult.failure("请输入有效的凭证号！");
					}
				}

				//校验通过，允许占用
				//当占用已有凭证操作时必须先调用此方法再保存凭证信息
				voucherManageService.voucherAnewSortBecauseOccupyOrDel(centerCode, centerCode, accBookType, accBookCode, dto.getYearMonth(), dto.getOldVoucherNo(),"occupy");
				//再将待保存凭证的凭证号改为占用的凭证号
				dto.setVoucherNo(dto.getOldVoucherNo());
			} else {
				//非占用凭证号的保存凭证，还需校验凭证号是否正确且合理
				//获取凭证最大号
				AccVoucherNoId avn = new AccVoucherNoId();
				avn.setCenterCode(centerCode);
				avn.setAccBookType(accBookType);
				avn.setAccBookCode(accBookCode);
				avn.setYearMonthDate(dto.getYearMonth());
				AccVoucherNo accVoucherNo = accVoucherNoRespository.findById(avn).get();
				String checkNo = centerCode+dto.getYearMonth().substring(2,6)+String.format("%06d", Integer.parseInt(accVoucherNo.getVoucherNo()));
				if (!checkNo.equals(dto.getVoucherNo())) {
					dto.setVoucherNo(checkNo);
				}
			}
			//System.out.println("凭证录入开始：");
			AccMainVoucherId amid=new AccMainVoucherId();
			AccMainVoucher am =new AccMainVoucher();
			amid.setCenterCode(centerCode);
			amid.setBranchCode(branchCode);
			amid.setAccBookType(CurrentUser.getCurrentLoginAccountType());
			amid.setAccBookCode(CurrentUser.getCurrentLoginAccount());//账套编码
			amid.setYearMonthDate(dto.getYearMonth());
			amid.setVoucherNo(dto.getVoucherNo());
			am.setId(amid);
			//判断凭证类别
			if(dto.getVoucherType() == null || dto.getVoucherType().equals("")){
				am.setVoucherType("2");//凭证类型为记账凭证
			}else {
				am.setVoucherType(dto.getVoucherType());//凭证类型设置为参数类型
			}
			//判断凭证录入方式
			if(dto.getGenerateWay() == null || dto.getGenerateWay().equals("")){
				am.setGenerateWay("2");//凭证记账方式为手工
			}else {
				am.setGenerateWay(dto.getGenerateWay());//凭证记账方式为自动
			}
			//判断数据来源
			if (dto.getDataSource() == null || "".equals(dto.getDataSource())){
				am.setDataSource("1");
			}else{
				am.setDataSource(dto.getDataSource());
			}

			am.setVoucherDate(dto.getVoucherDate());
			if (dto.getAuxNumber()!=null&&!"".equals(dto.getAuxNumber())) {
				am.setAuxNumber(Integer.valueOf(dto.getAuxNumber()));
			}
			am.setCreateBranchCode("");//制单单位
			am.setCreateBy(String.valueOf(CurrentUser.getCurrentUser().getId()));
			am.setVoucherFlag("1"); //未复核
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = df.format(new Date());
			am.setCreateTime(date);
			voucherRepository.save(am);
			voucherRepository.flush();
			//System.out.println("凭证主表录入完成");
			for(int i=0;i<list2.size();i++){
				VoucherDTO vd1=list2.get(i);
				VoucherDTO vd2=list3.get(i);
				AccSubVoucherId asid=new AccSubVoucherId();
				AccSubVoucher as =new AccSubVoucher();
				asid.setCenterCode(centerCode);
				asid.setBranchCode(branchCode);
				asid.setAccBookType(CurrentUser.getCurrentLoginAccountType());
				asid.setAccBookCode(CurrentUser.getCurrentLoginAccount());//账套编码
				asid.setYearMonthDate(dto.getYearMonth());
				asid.setVoucherNo(dto.getVoucherNo());
				asid.setSuffixNo(i+1+"");
				as.setId(asid);
				String[] itemCode=vd1.getSubjectCode().split("/");
				if(itemCode.length>=1){as.setItemCode(itemCode[0]);as.setF01(itemCode[0]); }
				if(itemCode.length>=2){as.setF02(itemCode[1]);}
				if(itemCode.length>=3){as.setF03(itemCode[2]);}
				if(itemCode.length>=4){as.setF04(itemCode[3]);}
				if(itemCode.length>=5){as.setF05(itemCode[4]);}
				if(itemCode.length>=6){as.setF06(itemCode[5]);}
				if(itemCode.length>=7){as.setF07(itemCode[6]);}
				if(itemCode.length>=8){as.setF08(itemCode[7]);}
				if(itemCode.length>=9){as.setF09(itemCode[8]);}
				if(itemCode.length>=10){as.setF10(itemCode[9]);}
				if(itemCode.length>=11){as.setF11(itemCode[10]);}
				if(itemCode.length>=12){as.setF12(itemCode[11]);}
				if(itemCode.length>=13){as.setF13(itemCode[12]);}
				if(itemCode.length>=14){as.setF14(itemCode[13]);}
				if(itemCode.length>=15){as.setF15(itemCode[14]);}
				as.setDirectionIdx(vd1.getSubjectCode().endsWith("/")?vd1.getSubjectCode():vd1.getSubjectCode()+"/");
				if (vd1.getSubjectName()!=null&&!"".equals(vd1.getSubjectName())) {
					as.setDirectionIdxName(vd1.getSubjectName());
				} else {
					as.setDirectionIdxName(qrySubjectNameAllBySubjectCode(vd1.getSubjectCode()));
				}
				//需要再处理，里面可能保存除专项信息以外的特殊信息，需要单独做处理
				//除专项之外的特殊信息设置：水费的单价(5位小数)数量(2位小数)，电费的单价(2位小数)和数量(2位小数)，银行存款类的结算类型、结算单号、结算日期
				if (vd2.getUnitPrice()!=null&&!"".equals(vd2.getUnitPrice())) {
					as.setD01(vd2.getUnitPrice());//单价
				}
				if (vd2.getUnitNum()!=null&&!"".equals(vd2.getUnitNum())) {
					as.setD02(vd2.getUnitNum());//数量
				}
				if (vd2.getSettlementType()!=null&&!"".equals(vd2.getSettlementType())) {
					as.setD03(vd2.getSettlementType());//结算类型
				}
				if (vd2.getSettlementNo()!=null&&!"".equals(vd2.getSettlementNo())) {
					as.setD04(vd2.getSettlementNo());//结算单号
				}
				if (vd2.getSettlementDate()!=null&&!"".equals(vd2.getSettlementDate())) {
					as.setD05(vd2.getSettlementDate());//结算日期
				}
				if (vd2.getCheckNo()!=null&&!"".equals(vd2.getCheckNo())) {
					as.setCheckNo(vd2.getCheckNo());//支票号
				}
				if (vd2.getInvoiceNo()!=null&&!"".equals(vd2.getInvoiceNo())) {
					as.setInvoiceNo(vd2.getInvoiceNo());//发票号
				}
				//专项存储：根据 段定义表- AccSegmentDefine进行分字段存放
				if (vd2.getSpecialCodeS()!=null&&!"".equals(vd2.getSpecialCodeS())) {
					String[] specialCode=vd2.getSpecialCodeS().split(",");
					String[] specialSuperCode = vd2.getSpecialSuperCodeS().split(",");
					for(int m=0;m<specialCode.length;m++){
						StringBuffer sql=new StringBuffer();
						sql.append(" SELECT a.segment_flag as specialCode from AccSegmentDefine a where a.segment_col = ?1");

						Map<Integer, Object> params = new HashMap<>();
						params.put(1, specialSuperCode[m]);

						List<?> result =voucherRepository.queryBySqlSC(sql.toString(), params);
						Map map = new HashMap();
						String data=null;
						if(result!=null&&result.size()!=0){
							map.putAll((Map) result.get(0));
							data=(String)map.get("specialCode");//专项存储位置
							if(data.equals("s01")){as.setS01(specialCode[m]);
							}else if(data.equals("s02")){as.setS02(specialCode[m]);
							}else if(data.equals("s03")){as.setS03(specialCode[m]);
							}else if(data.equals("s04")){as.setS04(specialCode[m]);
							}else if(data.equals("s05")){as.setS05(specialCode[m]);
							}else if(data.equals("s06")){as.setS06(specialCode[m]);
							}else if(data.equals("s07")){as.setS07(specialCode[m]);
							}else if(data.equals("s08")){as.setS08(specialCode[m]);
							}else if(data.equals("s09")){as.setS09(specialCode[m]);
							}else if(data.equals("s10")){as.setS10(specialCode[m]);
							}else if(data.equals("s11")){as.setS11(specialCode[m]);
							}else if(data.equals("s12")){as.setS12(specialCode[m]);
							}else if(data.equals("s13")){as.setS13(specialCode[m]);
							}else if(data.equals("s14")){as.setS14(specialCode[m]);
							}else if(data.equals("s15")){as.setS15(specialCode[m]);
							}else if(data.equals("s16")){as.setS16(specialCode[m]);
							}else if(data.equals("s17")){as.setS17(specialCode[m]);
							}else if(data.equals("s18")){as.setS18(specialCode[m]);
							}else if(data.equals("s19")){as.setS19(specialCode[m]);
							}else if(data.equals("s20")){as.setS20(specialCode[m]);
							}else{
								logger.debug(specialCode[m]+"未配置在段定义表中！");
							}
						}
					}
				}
				if (vd2.getSpecialCodeS()!=null&&!"".equals(vd2.getSpecialCodeS())) {
					as.setDirectionOther(vd2.getSpecialCodeS());//专项编码（可多个,可为空）
				}
				as.setRemark(vd1.getRemarkName());
				if ((vd1.getTagCode()!=null&&!"".equals(vd1.getTagCode()))&&(vd1.getTagCodeS()!=null&&!"".equals(vd1.getTagCodeS()))) {
					as.setFlag(vd1.getTagCodeS());
				}
				as.setCurrency(currency);//人民币
				as.setExchangeRate(new BigDecimal(exchangeRate));//当前汇率
				if(vd1.getDebit()!=null&&!vd1.getDebit().equals("")){
					as.setDebitSource(new BigDecimal(vd1.getDebit()));
					as.setDebitDest(new BigDecimal(vd1.getDebit()));
				} else {
					as.setDebitSource(new BigDecimal("0.00"));
					as.setDebitDest(new BigDecimal("0.00"));
				}
				if(vd1.getCredit()!=null&&!vd1.getCredit().equals("")){
					as.setCreditSource(new BigDecimal(vd1.getCredit()));
					as.setCreditDest(new BigDecimal(vd1.getCredit()));
				} else {
					as.setCreditSource(new BigDecimal("0.00"));
					as.setCreditDest(new BigDecimal("0.00"));
				}
				as.setCreateBy(CurrentUser.getCurrentUser().getId()+"");
				as.setCreateTime(date);
				voucherSubRepository.save(as);
				voucherSubRepository.flush();
			}
			//System.out.println("凭证子表录入完成");
			//将凭证号最大号数据+1
//			voucherRepository.voucherMaxNoAutoIncrementOne(centerCode ,CurrentUser.getCurrentLoginAccountType(),CurrentUser.getCurrentLoginAccount(),dto.getYearMonth(),"1");
            AccVoucherNoId id = new AccVoucherNoId();
            id.setCenterCode(centerCode);
            id.setAccBookCode(CurrentUser.getCurrentLoginAccount());
            id.setAccBookType(CurrentUser.getCurrentLoginAccountType());
            id.setYearMonthDate(dto.getYearMonth());
            AccVoucherNo accVoucherNo = accVoucherNoRespository.findById(id).get();
            accVoucherNo.setVoucherNo(String.valueOf(Integer.valueOf(accVoucherNo.getVoucherNo()) + 1));
            accVoucherNoRespository.saveAndFlush(accVoucherNo);
            accVoucherNoRespository.flush();

            return InvokeResult.success(am.getId().getVoucherNo());
		}
	}

    @Override
    @Transactional
    public InvokeResult updateVoucher(List<VoucherDTO> list2, List<VoucherDTO> list3,VoucherDTO dto) {
		synchronized (this) {
			String centerCode = CurrentUser.getCurrentLoginManageBranch();
			String branchCode = CurrentUser.getCurrentLoginManageBranch();
			String accBookType = CurrentUser.getCurrentLoginAccountType();
			String accBookCode = CurrentUser.getCurrentLoginAccount();

			if (dto.getOldVoucherNo()!=null&&!"".equals(dto.getOldVoucherNo())) {
				//占用已有凭证号
				//如果长度为9，则为完整凭证号，否则为有效号（去四位前缀）
				if (dto.getOldVoucherNo().length()!=(10+centerCode.length())) {
					try {
						//拼出完整的凭证号
						dto.setOldVoucherNo(centerCode+dto.getYearMonth().substring(2,6)+String.format("%06d", Integer.parseInt(dto.getOldVoucherNo())));
					} catch (Exception e) {
						logger.error("占用已有凭证号拼接异常", e);
						return InvokeResult.failure("请输入有效的凭证号！");
					}
				}
				//校验通过，允许占用
			}

			//先获得当前编辑凭证的最新信息（后台+前台）
			List<AccMainVoucher> accMainVoucherList = new ArrayList<AccMainVoucher>();
			List<AccSubVoucher> accSubVoucherList = new ArrayList<AccSubVoucher>();

			AccMainVoucherId amid=new AccMainVoucherId();
			amid.setCenterCode(centerCode);
			amid.setBranchCode(branchCode);
			amid.setAccBookType(CurrentUser.getCurrentLoginAccountType());
			amid.setAccBookCode(CurrentUser.getCurrentLoginAccount());//账套编码
			amid.setYearMonthDate(dto.getYearMonth());
			amid.setVoucherNo(dto.getVoucherNo());
			AccMainVoucher am = voucherRepository.findById(amid).get();
			if (am!=null) {
				am.setVoucherDate(dto.getVoucherDate());
				if (dto.getAuxNumber()!=null&&!"".equals(dto.getAuxNumber())) {
					am.setAuxNumber(Integer.valueOf(dto.getAuxNumber()));
				}
				//am.setModifyReason("");
				am.setLastModifyBy(String.valueOf(CurrentUser.getCurrentUser().getId()));
				am.setLastModifyTime(CurrentTime.getCurrentTime());
				/*voucherRepository.save(am);*/
				//将其添加到List集合，待后续使用，如果占用已有凭证号，需要处理
				accMainVoucherList.add(am);

				//凭证子表
				for(int i=0;i<list2.size();i++){
					VoucherDTO vd1=list2.get(i);
					VoucherDTO vd2=list3.get(i);
					AccSubVoucherId asid=new AccSubVoucherId();
					asid.setCenterCode(centerCode);
					asid.setBranchCode(branchCode);
					asid.setAccBookType(accBookType);
					asid.setAccBookCode(accBookCode);//账套编码
					asid.setYearMonthDate(dto.getYearMonth());
					asid.setVoucherNo(dto.getVoucherNo());
					asid.setSuffixNo(i+1+"");
					AccSubVoucher as = new AccSubVoucher();
					as.setId(asid);
					String[] itemCode=vd1.getSubjectCode().split("/");
					if(itemCode.length>=1){as.setItemCode(itemCode[0]);as.setF01(itemCode[0]); }
					if(itemCode.length>=2){as.setF02(itemCode[1]);}
					if(itemCode.length>=3){as.setF03(itemCode[2]);}
					if(itemCode.length>=4){as.setF04(itemCode[3]);}
					if(itemCode.length>=5){as.setF05(itemCode[4]);}
					if(itemCode.length>=6){as.setF06(itemCode[5]);}
					if(itemCode.length>=7){as.setF07(itemCode[6]);}
					if(itemCode.length>=8){as.setF08(itemCode[7]);}
					if(itemCode.length>=9){as.setF09(itemCode[8]);}
					if(itemCode.length>=10){as.setF10(itemCode[9]);}
					if(itemCode.length>=11){as.setF11(itemCode[10]);}
					if(itemCode.length>=12){as.setF12(itemCode[11]);}
					if(itemCode.length>=13){as.setF13(itemCode[12]);}
					if(itemCode.length>=14){as.setF14(itemCode[13]);}
					if(itemCode.length>=15){as.setF15(itemCode[14]);}
					as.setDirectionIdx(vd1.getSubjectCode().endsWith("/")?vd1.getSubjectCode():vd1.getSubjectCode()+"/");
					if (vd1.getSubjectName()!=null&&!"".equals(vd1.getSubjectName())) {
						as.setDirectionIdxName(vd1.getSubjectName());
					} else {
						as.setDirectionIdxName(qrySubjectNameAllBySubjectCode(vd1.getSubjectCode()));
					}
					//需要再处理，里面可能保存除专项信息以外的特殊信息，需要单独做处理
					//除专项之外的特殊信息设置：水费的单价(5位小数)数量(2位小数)，电费的单价(2位小数)和数量(2位小数)，银行存款类的结算类型、结算单号、结算日期
					if (vd2.getUnitPrice()!=null&&!"".equals(vd2.getUnitPrice())) {
						as.setD01(vd2.getUnitPrice());//单价
					}
					if (vd2.getUnitNum()!=null&&!"".equals(vd2.getUnitNum())) {
						as.setD02(vd2.getUnitNum());//数量
					}
					if (vd2.getSettlementType()!=null&&!"".equals(vd2.getSettlementType())) {
						as.setD03(vd2.getSettlementType());//结算类型
					}
					if (vd2.getSettlementNo()!=null&&!"".equals(vd2.getSettlementNo())) {
						as.setD04(vd2.getSettlementNo());//结算单号
					}
					if (vd2.getSettlementDate()!=null&&!"".equals(vd2.getSettlementDate())) {
						as.setD05(vd2.getSettlementDate());//结算日期
					}
					if (vd2.getCheckNo()!=null&&!"".equals(vd2.getCheckNo())) {
						as.setCheckNo(vd2.getCheckNo());//支票号
					}
					if (vd2.getInvoiceNo()!=null&&!"".equals(vd2.getInvoiceNo())) {
						as.setInvoiceNo(vd2.getInvoiceNo());//发票号
					}
					//专项存储：根据 段定义表- AccSegmentDefine进行分字段存放
					if (vd2.getSpecialCodeS()!=null&&!"".equals(vd2.getSpecialCodeS())) {
						String[] specialCode=vd2.getSpecialCodeS().split(",");
						String[] specialSuperCode = vd2.getSpecialSuperCodeS().split(",");
						for(int m=0;m<specialCode.length;m++){
							StringBuffer sql=new StringBuffer();
							sql.append(" SELECT a.segment_flag as specialCode from AccSegmentDefine a where a.segment_col = ?1");

							Map<Integer, Object> params = new HashMap<>();
							params.put(1, specialSuperCode[m]);

							List<?> result =voucherRepository.queryBySqlSC(sql.toString(), params);
							Map map = new HashMap();
							String data=null;
							if(result!=null&&result.size()!=0){
								map.putAll((Map) result.get(0));
								data=(String)map.get("specialCode");//专项存储位置
								if(data.equals("s01")){as.setS01(specialCode[m]);
								}else if(data.equals("s02")){as.setS02(specialCode[m]);
								}else if(data.equals("s03")){as.setS03(specialCode[m]);
								}else if(data.equals("s04")){as.setS04(specialCode[m]);
								}else if(data.equals("s05")){as.setS05(specialCode[m]);
								}else if(data.equals("s06")){as.setS06(specialCode[m]);
								}else if(data.equals("s07")){as.setS07(specialCode[m]);
								}else if(data.equals("s08")){as.setS08(specialCode[m]);
								}else if(data.equals("s09")){as.setS09(specialCode[m]);
								}else if(data.equals("s10")){as.setS10(specialCode[m]);
								}else if(data.equals("s11")){as.setS11(specialCode[m]);
								}else if(data.equals("s12")){as.setS12(specialCode[m]);
								}else if(data.equals("s13")){as.setS13(specialCode[m]);
								}else if(data.equals("s14")){as.setS14(specialCode[m]);
								}else if(data.equals("s15")){as.setS15(specialCode[m]);
								}else if(data.equals("s16")){as.setS16(specialCode[m]);
								}else if(data.equals("s17")){as.setS17(specialCode[m]);
								}else if(data.equals("s18")){as.setS18(specialCode[m]);
								}else if(data.equals("s19")){as.setS19(specialCode[m]);
								}else if(data.equals("s20")){as.setS20(specialCode[m]);
								}else{
									logger.debug(specialCode[m]+"未配置在段定义表中！");
								}
							}
						}
					}
					if (vd2.getSpecialCodeS()!=null&&!"".equals(vd2.getSpecialCodeS())) {
						as.setDirectionOther(vd2.getSpecialCodeS());//专项编码（可多个,可为空）
					}
					as.setRemark(vd1.getRemarkName());
					if ((vd1.getTagCode()!=null&&!"".equals(vd1.getTagCode()))&&(vd1.getTagCodeS()!=null&&!"".equals(vd1.getTagCodeS()))) {
						as.setFlag(vd1.getTagCodeS());
					}
					as.setCurrency(currency);//人民币
					as.setExchangeRate(new BigDecimal(exchangeRate));//当前汇率
					if(vd1.getDebit()!=null&&!"".equals(vd1.getDebit())){
						as.setDebitSource(new BigDecimal(vd1.getDebit()));
						as.setDebitDest(new BigDecimal(vd1.getDebit()));
					} else {
						as.setDebitSource(new BigDecimal("0.00"));
						as.setDebitDest(new BigDecimal("0.00"));
					}
					if(vd1.getCredit()!=null&&!"".equals(vd1.getCredit())){
						as.setCreditSource(new BigDecimal(vd1.getCredit()));
						as.setCreditDest(new BigDecimal(vd1.getCredit()));
					} else {
						as.setCreditSource(new BigDecimal("0.00"));
						as.setCreditDest(new BigDecimal("0.00"));
					}
					as.setCreateBy(am.getCreateBy());
					as.setCreateTime(am.getCreateTime());
					//as.setModifyReason("");
					as.setLastModifyBy(am.getLastModifyBy());
					as.setLastModifyTime(am.getLastModifyTime());
					/*voucherSubRepository.save(as);*/
					//将其添加到List集合，待后续使用，如果占用已有凭证号，需要处理
					accSubVoucherList.add(i,as);
				}

				//在拿到当前编辑凭证号全部数据之后，删除当前编辑凭证数据
				voucherRepository.delete(am);
				//获取凭证子表数据
				List<AccSubVoucher> subVoucherList = accSubVoucherRespository.findAll(new CusSpecification<>().and(
						CusSpecification.Cnd.eq("id.centerCode", amid.getCenterCode()),
						CusSpecification.Cnd.eq("id.branchCode", amid.getBranchCode()),
						CusSpecification.Cnd.eq("id.accBookType", amid.getAccBookType()),
						CusSpecification.Cnd.eq("id.accBookCode", amid.getAccBookCode()),
						CusSpecification.Cnd.eq("id.yearMonthDate", amid.getYearMonthDate()),
						CusSpecification.Cnd.eq("id.voucherNo", dto.getVoucherNo())));
				for (AccSubVoucher a:subVoucherList) {
					accSubVoucherRespository.delete(a);
				}
				voucherRepository.flush();

				//判断是否占用已有凭证号
				if (dto.getOldVoucherNo()!=null&&!"".equals(dto.getOldVoucherNo())) {
					//占用已有凭证号
					String main = "select * from accmainvoucher a where 1=1";
					String sub = "select * from accsubvoucher a where 1=1";
					StringBuffer sb = new StringBuffer();

					int paramsNo = 1;
					Map<Integer, Object> params = new HashMap<>();

					sb.append(" and a.center_code = ?" + paramsNo);
					params.put(paramsNo, centerCode);
					paramsNo++;
					sb.append(" and a.branch_code = ?" + paramsNo);
					params.put(paramsNo, branchCode);
					paramsNo++;
					sb.append(" and a.acc_book_type = ?" + paramsNo);
					params.put(paramsNo, accBookType);
					paramsNo++;
					sb.append(" and a.acc_book_code = ?" + paramsNo);
					params.put(paramsNo, accBookCode);
					paramsNo++;
					sb.append(" and a.year_month_date = ?" + paramsNo);
					params.put(paramsNo, dto.getYearMonth());
					paramsNo++;

					Integer editVoucherNo = Integer.valueOf(am.getId().getVoucherNo().substring(centerCode.length()+4));
					Integer occupyVoucherNo = Integer.valueOf(dto.getOldVoucherNo().substring(centerCode.length()+4));
					//比较两凭证号的大小，校验时已排除相等的情况
					if (editVoucherNo>occupyVoucherNo) {
						//1.因占用凭证号比编辑凭证号小，故从occupyVoucherNo(包含)到editVoucherNo(不包含)的凭证号均加1，编辑凭证号之后的不动
						sb.append(" and a.voucher_no >= ?" + paramsNo);
						params.put(paramsNo, dto.getOldVoucherNo());
						paramsNo++;
						sb.append(" and a.voucher_no < ?" + paramsNo);
						params.put(paramsNo, dto.getVoucherNo());
						paramsNo++;
					} else {
						//1.因占用凭证号比编辑凭证号大，故从editVoucherNo(不包含)到occupyVoucherNo(包含)的凭证号均减1，占用凭证号之后的不动
						sb.append(" and a.voucher_no > ?" + paramsNo);
						params.put(paramsNo, dto.getVoucherNo());
						paramsNo++;
						sb.append(" and a.voucher_no <= ?" + paramsNo);
						params.put(paramsNo, dto.getOldVoucherNo());
						paramsNo++;
					}
					sb.append(" order by a.voucher_no asc");
					List<AccMainVoucher> mainList = (List<AccMainVoucher>) accMainVoucherRespository.queryBySql(main+sb.toString(), params, AccMainVoucher.class);
					sb.append(",suffix_no asc");
					List<AccSubVoucher> subList = (List<AccSubVoucher>) accMainVoucherRespository.queryBySql(sub+sb.toString(), params, AccSubVoucher.class);

					//删除主表、子表信息
					for (AccMainVoucher mainVoucher: mainList) { voucherRepository.delete(mainVoucher); }
					for (AccSubVoucher subVoucher: subList) { voucherSubRepository.delete(subVoucher); }
					voucherRepository.flush();

					//凭证号前缀
					String prefix = dto.getYearMonth().substring(2,6);

					//用新的凭证编号替换后再写入数据库
					int newOperationVoucherNo = (editVoucherNo>occupyVoucherNo)?occupyVoucherNo:(editVoucherNo-1);
					List<Map<String,String>> voucherlists=new ArrayList<>();
					for (AccMainVoucher mainVoucher: mainList) {
						newOperationVoucherNo++;
						//新凭证号
						String newVoucherNo = CurrentUser.getCurrentLoginManageBranch()+prefix+String.format("%06d", newOperationVoucherNo);
						AccMainVoucherId id = mainVoucher.getId();
						String currentVoucherNo = id.getVoucherNo();
						id.setVoucherNo(newVoucherNo);
						mainVoucher.setId(id);
						voucherRepository.save(mainVoucher);
						voucherRepository.flush();

						//预处理固定无形关联凭证数据
						Map<String,String> vouchermaps=new HashMap<>();
						vouchermaps.put("newVoucherNo", id.getVoucherNo());
						vouchermaps.put("oldVoucherNo", currentVoucherNo);
						vouchermaps.put("vouchertype", mainVoucher.getVoucherType());
						voucherlists.add(vouchermaps);
					}

					//处理固定无形关联凭证数据
					voucherManageService.fixedasetAndIntangVoucher(centerCode, branchCode, CurrentUser.getCurrentLoginAccountType(), CurrentUser.getCurrentLoginAccount(),voucherlists);
					voucherRepository.flush();

					newOperationVoucherNo = (editVoucherNo>occupyVoucherNo)?occupyVoucherNo:(editVoucherNo-1);
					//用于标记子表数据是否属于同一张凭证下的分录信息
					String oldVoucherNo = "";
					for (AccSubVoucher subVoucher: subList) {
						AccSubVoucherId id = subVoucher.getId();
						if ("".equals(oldVoucherNo) || (!"".equals(oldVoucherNo) && !oldVoucherNo.equals(id.getVoucherNo()))) {
							//oldVoucherNo为空串时，说明是第一条数据
							//或者 oldVoucherNo非空串并且与当前凭证号不一致时，说明不是同一张凭证分录数据
							//此时才会自增1
							newOperationVoucherNo++;
						}
						oldVoucherNo = id.getVoucherNo();
						//新凭证号
						String newVoucherNo = CurrentUser.getCurrentLoginManageBranch()+prefix+String.format("%06d", newOperationVoucherNo);
						id.setVoucherNo(newVoucherNo);
						subVoucher.setId(id);
						voucherSubRepository.save(subVoucher);
						voucherSubRepository.flush();
					}

					//2.再将编辑凭证号改为占用凭证号并保存数据
					for (AccMainVoucher amL : accMainVoucherList) {
						AccMainVoucherId id = amL.getId();
						id.setVoucherNo(dto.getOldVoucherNo());
						amL.setId(id);
						voucherRepository.save(amL);
						voucherRepository.flush();
					}
					for (AccSubVoucher asL : accSubVoucherList) {
						AccSubVoucherId id = asL.getId();
						id.setVoucherNo(dto.getOldVoucherNo());
						asL.setId(id);
						voucherSubRepository.save(asL);
						voucherSubRepository.flush();
					}
				} else {
					//非占用已有凭证号，则直接遍历保存
					for (AccMainVoucher amL : accMainVoucherList) {
						voucherRepository.save(amL);
						voucherRepository.flush();
					}
					for (AccSubVoucher asL : accSubVoucherList) {
						voucherSubRepository.save(asL);
						voucherSubRepository.flush();
					}
				}
			} else {
				InvokeResult.failure(dto.getVoucherNo()+"凭证不存在");
			}
			return InvokeResult.success();
		}
    }

    @Override
    @Transactional
    public InvokeResult updateVoucher2(List<VoucherDTO> list2,VoucherDTO dto) {
        synchronized (this) {
			String centerCode = CurrentUser.getCurrentLoginManageBranch();
			String branchCode = CurrentUser.getCurrentLoginManageBranch();
            //先获得当前编辑凭证的最新信息（后台+前台）
            AccMainVoucherId amid=new AccMainVoucherId();
            amid.setCenterCode(centerCode);
            amid.setBranchCode(branchCode);
            amid.setAccBookType(CurrentUser.getCurrentLoginAccountType());
            amid.setAccBookCode(CurrentUser.getCurrentLoginAccount());//账套编码
            amid.setYearMonthDate(dto.getYearMonth());
            amid.setVoucherNo(dto.getVoucherNo());
            AccMainVoucher am = voucherRepository.findById(amid).get();
            if (am!=null) {
                /*am.setVoucherDate(dto.getVoucherDate());
                am.setAuxNumber(dto.getAuxNumber());*/

				//获取凭证子表数据
				List<AccSubVoucher> subVoucherList = accSubVoucherRespository.findAll(new CusSpecification<>().and(
						CusSpecification.Cnd.eq("id.centerCode", amid.getCenterCode()),
						CusSpecification.Cnd.eq("id.branchCode", amid.getBranchCode()),
						CusSpecification.Cnd.eq("id.accBookType", amid.getAccBookType()),
						CusSpecification.Cnd.eq("id.accBookCode", amid.getAccBookCode()),
						CusSpecification.Cnd.eq("id.yearMonthDate", amid.getYearMonthDate()),
						CusSpecification.Cnd.eq("id.voucherNo", dto.getVoucherNo())));
				for (int i=0;i<subVoucherList.size();i++) {
					AccSubVoucher accSubVoucher = subVoucherList.get(i);
					String oldRemarkName = accSubVoucher.getRemark();
					String newRemarkName = list2.get(i).getRemarkName();
					if (newRemarkName!=null&&!"".equals(newRemarkName) && oldRemarkName!=null&&!"".equals(oldRemarkName) && !newRemarkName.equals(oldRemarkName)) {
						if (newRemarkName.length()<=oldRemarkName.length() || !(newRemarkName.startsWith(oldRemarkName))) {
							throw new RuntimeException("摘要修改错误，原因：只允许在原有的摘要之后追加信息！");
						}
						accSubVoucher.setRemark(newRemarkName);

						am.setLastModifyBy(String.valueOf(CurrentUser.getCurrentUser().getId()));
						am.setLastModifyTime(CurrentTime.getCurrentTime());
						accSubVoucher.setLastModifyBy(am.getLastModifyBy());
						accSubVoucher.setLastModifyTime(am.getLastModifyTime());
					}
					accSubVoucherRespository.save(accSubVoucher);
					accSubVoucherRespository.flush();
				}
				accMainVoucherRespository.save(am);
            } else {
                InvokeResult.failure(dto.getVoucherNo()+"凭证不存在");
            }
            return InvokeResult.success();
        }
    }

	@Override
	public InvokeResult deficitRemind(List<VoucherDTO> list2, List<VoucherDTO> list3,VoucherDTO dto) {
		String centerCode = CurrentUser.getCurrentLoginManageBranch();
		String branchCode = CurrentUser.getCurrentLoginManageBranch();
		String accBookType = CurrentUser.getCurrentLoginAccountType();
		String accBookCode = CurrentUser.getCurrentLoginAccount();
		//如果占用已有凭证号，则需要校验
		if (dto.getOldVoucherNo()!=null&&!"".equals(dto.getOldVoucherNo())) {
			//占用已有凭证号
			//如果长度为9，则为完整凭证号，否则为有效号（去四位前缀）
			if (dto.getOldVoucherNo().length()!=(10+centerCode.length())) {
				try {
					//拼出完整的凭证号
					dto.setOldVoucherNo(centerCode+dto.getYearMonth().substring(2,6)+String.format("%06d", Integer.parseInt(dto.getOldVoucherNo())));
				} catch (Exception e) {
					logger.error("占用已有凭证号拼接异常", e);
					return InvokeResult.failure("请输入有效的凭证号！");
				}
			}
			/*
				1.但还需校验该录入号的有效性，先查询，不存在则无效；
				2.再校验是否允许占用（即将要占用凭证号及其之后的凭证是否均处于未复核状态）
			 */
			if (!dto.getOldVoucherNo().equals(dto.getVoucherNo())) {
				List<AccMainVoucher> mainList = accMainVoucherRespository.findAll(new CusSpecification<>().and(
						CusSpecification.Cnd.eq("id.centerCode", centerCode),
						CusSpecification.Cnd.eq("id.branchCode", branchCode),
						CusSpecification.Cnd.eq("id.accBookType", accBookType),
						CusSpecification.Cnd.eq("id.accBookCode", accBookCode),
						CusSpecification.Cnd.eq("id.yearMonthDate", dto.getYearMonth()),
						CusSpecification.Cnd.eq("id.voucherNo", dto.getOldVoucherNo())));
				if (mainList!=null&&mainList.size()>0) {
					AccMainVoucher accMainVoucher = mainList.get(0);
					if (!"1".equals(accMainVoucher.getVoucherFlag())) {
						return InvokeResult.failure("将要被占用的凭证已复核，不允许占用！");
					} else {
						//checkAfterTheVoucherExistApprove查询范围不包含当前凭证号
						List<?> list = voucherRepository.checkAfterTheVoucherExistApprove(accBookCode,dto.getYearMonth(),dto.getOldVoucherNo(), centerCode);
						if (list!=null&&list.size()>0) {
							return InvokeResult.failure("在凭证号"+dto.getOldVoucherNo()+"之后存在已复核的凭证，不允许占用！");
						}
					}
				} else {
					return InvokeResult.failure("请输入有效的凭证号！");
				}
			} else {
				return InvokeResult.failure("占用已有凭证号与当前凭证号相同，请输入有效的凭证号");
			}
			//校验通过，允许占用
		}

		//凭证附件张数的合法性
		if (dto.getAuxNumber()!=null&&!"".equals(dto.getAuxNumber())) {
			try {
				int auxNumber = Integer.parseInt(dto.getAuxNumber());
			} catch (Exception e) {
				return InvokeResult.failure("请输入合法的附件张数！");
			}
        }

        //判断分录中科目是否符合要求
        for(int i=0;i<list2.size();i++){
            VoucherDTO subject=list2.get(i);
            String subjectCodeAll = subject.getSubjectCode().substring(0,subject.getSubjectCode().length()-1);
			String result = checkSubjectCodePassMusterBySubjectCodeAll(subjectCodeAll);
			if (result!=null && !"".equals(result)) {
				if ("notExist".equals(result)) {
					return InvokeResult.failure(subject.getSubjectCode()+"不存在，请重新输入！");
				}
				if ("notEnd".equals(result)) {
					return InvokeResult.failure(subject.getSubjectCode()+"不是末级科目，请重新输入！");
				}
				if ("notUse".equals(result)) {
					return InvokeResult.failure(subject.getSubjectCode()+"已停用，请重新输入！");
				}
			}
            if((subject.getDebit()==null||"".equals(subject.getDebit())||"0".equals(subject.getDebit()))&&(subject.getCredit()==null||"".equals(subject.getCredit())||"0".equals(subject.getCredit()))){
                return InvokeResult.failure("单条分录的借贷方不能同时为0，请重新输入！");
            }
            if (subject.getTagCode()!=null&&!"".equals(subject.getTagCode())) {
            	boolean flag = false;
            	String[] sC = subject.getTagCodeS().split(",");
				String[] sN = subject.getTagCode().split(",");
            	if (sC.length!=sN.length) {
					flag = true;
				} else {
            		for (int k=0;k<sC.length;k++) {
						List<?> tagList = voucherRepository.findTagByTagCode(centerCode, CurrentUser.getCurrentLoginAccountType(), CurrentUser.getCurrentLoginAccount(), sC[k]);
						if (tagList!=null&&tagList.size()>0) {
							Map map = (Map) tagList.get(0);
							if (!((String)map.get("tagName")).equals(sN[k])) {//编码、名称不一致
								flag = true;
							} else {
								if (!"0".equals((String)map.get("endFlag"))) {//非末级
									return InvokeResult.failure((String)map.get("tagName")+"非末级，请重新双击选择！");
								}
								if (!"1".equals((String)map.get("flag"))) {//非使用
									return InvokeResult.failure((String)map.get("tagName")+"非使用，请重新双击选择！");
								}
							}
						}
					}
				}
				if (flag) {
					return InvokeResult.failure("第"+(i+1)+"行标注名称与编码不一致，请重新双击选择！");
				}
			}
			//判断科目是否挂专项，若有则校验专项信息，若无也处理专项信息
			StringBuffer sql=new StringBuffer("select * from subjectinfo s  where s.account = ?1 and concat_ws('',s.all_subject,s.subject_code) = ?2 and s.special_id is not null and s.special_id != ''");

			Map<Integer, Object> params = new HashMap<>();
			params.put(1, accBookCode);
			params.put(2, subjectCodeAll);

            List<SubjectInfo> listBySql = (List<SubjectInfo>)subjectRepository.queryBySql(sql.toString(), params, SubjectInfo.class);
            // 这里说明当前科目代码存在专项，
			if (listBySql!=null&&listBySql.size()>0) {
				if (list3.get(i).getSpecialCodeS()!=null&&!"".equals(list3.get(i).getSpecialCodeS())) {
					String[] str = (list3.get(i).getSpecialCodeS()).split(",");// 页面传入的数组
					String[] str1 = list3.get(i).getSpecialSuperCodeS().split(",");// 由数据源获得的（Sprcial_id）页面传入的数组
					if(str.length != str1.length){
						return  InvokeResult.failure("专项的所有信息都不能为空，请重新输入！");
					}
					for (String s : str) {
						int j = 0;
						//专项校验
						result = checkSpecialCodePassMusterBySpecialCode(s,accBookCode);
						if (result!=null && !"".equals(result)) {
							if ("notExist".equals(result)) {
								return InvokeResult.failure("专项："+s+" 不存在，请重新输入！");
							}
							if ("notEnd".equals(result)) {
								return InvokeResult.failure(s+"不是末级专项，请重新输入！");
							}
							if ("notUse".equals(result)) {
								return InvokeResult.failure(s+"专项已停用，请重新输入！");
							}
							if ("none".equals(result)){
								return InvokeResult.failure(str1[j]+"的专项信息不能为空！");
							}
						}
						j++;// 为了防止"123,345,,744";
					}
				} else {
					// 当前科目存在专项信息，但传输过来的信息为空，或为null，走此处。
					return InvokeResult.failure(subject.getSubjectCode()+"科目请输入专项信息！");
				}
			}
			// 说明当前科目代码不存在专项信息。
			else {
				// 当前科目代码本不应该存在专项信息，如果存在则报错。
				if (list3.get(i).getSpecialCodeS()!=null&&!"".equals(list3.get(i).getSpecialCodeS())) {
					return InvokeResult.failure(subject.getSubjectCode()+"科目请确认专项信息是否正确！");
				}
			}
        }

		Map<String, Integer> tempDeficitMap = new HashMap<String, Integer>();
		List<Map<String, Object>> tempDeficitList = new ArrayList<Map<String, Object>>();
		for(int i=0;i<list2.size();i++){
			VoucherDTO subject = list2.get(i);
			VoucherDTO special = list3.get(i);

			StringBuffer sql=new StringBuffer("select * from subjectinfo s  where s.account = ?1 and concat_ws(\"\",s.all_subject,s.subject_code) = ?2 ");
			Map<Integer, Object> params = new HashMap<>();
			params.put(1, accBookCode);
			params.put(2, subject.getSubjectCode().substring(0,subject.getSubjectCode().length()-1));
			//获取科目信息
			List<SubjectInfo> list = (List<SubjectInfo>)subjectRepository.queryBySql(sql.toString(), params, SubjectInfo.class);

			//判断损益类科目不需要进行赤字校验
			if(list.size()>0){
				if(list.get(0).getSubjectType().equals("4")){
					continue;
				}
			}

			String[] subjectCode0 = list.get(0).getAllSubject().concat(list.get(0).getSubjectCode()).split("/");
			String key = subject.getSubjectCode();
			boolean onlySubject = true;
			if (!(special.getSpecialCodeS()!=null && !"".equals(special.getSpecialCodeS())) || subjectCode0[0].equals("1002")) {
				//以1002开头的科目提示科目赤字，不用提示专项赤字
			} else {
				//如果存在专项，则需要进行专项赤字校验
				key += "#" + special.getSpecialCodeS();
				onlySubject = false;
			}
			if (!tempDeficitMap.containsKey(key)) {
				Map map = new HashMap();
				map.put("onlySubject", onlySubject);
				map.put("subjectCode", subject.getSubjectCode());
				map.put("subjectName", subject.getSubjectName());
				map.put("direction", list.get(0).getDirection());

				if (!onlySubject) {
					map.put("specialCode", special.getSpecialCodeS());
				}
				BigDecimal debit = BigDecimal.ZERO;
				BigDecimal credit = BigDecimal.ZERO;
				if (subject.getDebit()!=null && !"".equals(subject.getDebit()) && Double.parseDouble(subject.getDebit())!=0) {
					debit = new BigDecimal(subject.getDebit());
				} else {
					credit = new BigDecimal(subject.getCredit());
				}
				map.put("debit", debit);
				map.put("credit", credit);

				tempDeficitMap.put(key, tempDeficitList.size());
				tempDeficitList.add(map);
			} else {
				Map map = tempDeficitList.get(tempDeficitMap.get(key));
				BigDecimal debit = BigDecimal.ZERO;
				BigDecimal credit = BigDecimal.ZERO;
				if (subject.getDebit()!=null && !"".equals(subject.getDebit()) && Double.parseDouble(subject.getDebit())!=0) {
					debit = new BigDecimal(subject.getDebit());
				} else {
					credit = new BigDecimal(subject.getCredit());
				}
				map.put("debit", ((BigDecimal) map.get("debit")).add(debit));
				map.put("credit", ((BigDecimal) map.get("credit")).add(credit));
			}
		}

		//开始赤字校验
		List deficitList = new ArrayList();
		if (tempDeficitList.size()>0) {
			for (Map<String, Object> m : tempDeficitList) {
				boolean onlySubject = (boolean) m.get("onlySubject");
				String subjectCode = (String) m.get("subjectCode");
				String subjectName = (String) m.get("subjectName");
				String direction = (String) m.get("direction");

				BigDecimal debit = (BigDecimal) m.get("debit");
				BigDecimal credit = (BigDecimal) m.get("credit");

				//当前录入凭证的当前科目或专项科目借贷合计为 0 时，不再继续校验，即终止后续校验操作
				BigDecimal debitAndCredit = debit.subtract(credit);
				if (debitAndCredit.compareTo(BigDecimal.ZERO) == 0) {
					continue;
				}

				String specialCode = "";
				if (!onlySubject) {
					specialCode = (String) m.get("specialCode");
				}

				//获取科目或专项科目期末值
				StringBuffer dest = new StringBuffer();
				Map<Integer, Object> params = new HashMap<>();
				if (onlySubject) {
					dest.append("SELECT CAST(a.balance_dest AS CHAR) as dest FROM  AccDetailBalance a where a.center_code = ?1 and a.branch_code = ?2 and a.acc_book_type = ?3 and a.acc_book_code = ?4 and a.direction_idx = ?5");
					params.put(1, centerCode);
					params.put(2, branchCode);
					params.put(3, accBookType);
					params.put(4, accBookCode);
					params.put(5, subjectCode);
				} else {
					dest.append("SELECT CAST(a.balance_dest AS CHAR) as dest FROM  AccArticleBalance a where a.center_code = ?1 and a.branch_code = ?2 and a.acc_book_type = ?3 and a.acc_book_code = ?4 and a.direction_idx = ?5 and a.direction_other = ?6");
					params.put(1, centerCode);
					params.put(2, branchCode);
					params.put(3, accBookType);
					params.put(4, accBookCode);
					params.put(5, subjectCode);
					params.put(6, specialCode);
				}
				List<?> result = voucherRepository.queryBySqlSC(dest.toString(), params);
				BigDecimal sum = BigDecimal.ZERO;
				if(result!=null && result.size()!=0){
					Map map = new HashMap();
					map.putAll((Map) result.get(0));
					sum = new BigDecimal((String) map.get("dest"));//期末值
				}

				//判断该会计期间以上包括该会计期间 是否存在未记账凭证
				StringBuffer sqls2 = new StringBuffer("select IFNULL(SUM(debit_dest-credit_dest),0) from accsubvoucher where center_code = ?1 and branch_code = ?2 and acc_book_type = ?3 and acc_book_code = ?4 and year_month_date <= ?5 and direction_idx = ?6 and voucher_no in(select voucher_no from accmainvoucher where center_code = ?1 and branch_code = ?2 and acc_book_type = ?3 and acc_book_code = ?4 and year_month_date <= ?5 and voucher_flag < '3')");

				params = new HashMap<>();
				params.put(1, centerCode);
				params.put(2, branchCode);
				params.put(3, accBookType);
				params.put(4, accBookCode);
				params.put(5, dto.getYearMonth());
				params.put(6, subjectCode);

				if (!onlySubject) {
					sqls2.append(" and direction_other = ?7");
					params.put(7, specialCode);
				}

				List<?> accsubVoucherList1 = voucherRepository.queryBySql(sqls2.toString(), params);
				if (accsubVoucherList1!=null && accsubVoucherList1.size()>0) {
					//加上已存在凭证中未记账的金额
					sum = sum.add(new BigDecimal(String.valueOf(accsubVoucherList1.get(0))));
				}

				//加上本次凭证同科目或专项科目下合计金额，得到最终余额
				sum = sum.add(debitAndCredit);

				boolean flag = false; //是否需要赤字提醒
				String directionName = "";
				if ("1".equals(direction)) {
					//结果小于零需要做赤字提醒
					if (sum.compareTo(BigDecimal.ZERO) < 0) {
						flag = true;
						sum = sum.negate();
						directionName = "贷";
					}
				} else if ("2".equals(direction)) {
					//结果大于零需要做赤字提醒
					if (sum.compareTo(BigDecimal.ZERO) > 0) {
						flag = true;
						directionName = "借";
					}
				} else {
					logger.error("科目余额方向(direction)错误：direction=" + direction);
				}

				if (flag) {
					Map map = new HashMap();
					//为真表示进行的是明细账数据赤字校验 为假则为辅助明细账数据赤字校验
					//onlySubject ture 查科目 onlySubject 查专项
					if (onlySubject) {
						//return InvokeResult.failure("往来科目："+subject.getSubjectName()+"\n赤字金额：贷 "+sum+"元");
						map.put("subjectCode", subjectCode);
						map.put("subjectName", subjectName);
						map.put("money", sum);
						map.put("direction", directionName);
						map.put("specialCode", "");
						map.put("specialName", "");
					} else {
						//获取专项名称
						StringBuffer sqls1=new StringBuffer("select * from specialinfo where special_code in ?1 and account = ?2 order by field(special_code, ?3 )");

						params = new HashMap<>();
						List<String> tempList = Arrays.asList(specialCode.split(","));
						params.put(1, tempList);
						params.put(2, accBookCode);
						params.put(3, tempList);

						List<SpecialInfo> specialInfoList1=(List<SpecialInfo>)voucherRepository.queryBySql(sqls1.toString(), params, SpecialInfo.class);
						//return InvokeResult.failure("往来科目："+subject.getSubjectName()+"\n赤字金额：贷 "+sum+"元，客户："+specialInfoList1.get(0).getSpecialNameP()+" ");
						map.put("subjectCode", subjectCode);
						map.put("subjectName", subjectName);
						map.put("money", sum);
						map.put("direction", directionName);
						String tempSpecialName = "";
						if (specialInfoList1!=null && specialInfoList1.size()>0) {
							for (SpecialInfo s : specialInfoList1) {
								if (s.getSpecialNameP()!=null && !"".equals(s.getSpecialNameP())) {
									if (!"".equals(tempSpecialName)) {
										tempSpecialName += "<br/>" + s.getSpecialCode() + " " + s.getSpecialNameP();
									} else {
										tempSpecialName = s.getSpecialCode() + " " + s.getSpecialNameP();
									}
								}
							}
						}
						map.put("specialCode", specialCode);
						map.put("specialName", tempSpecialName);
					}

					deficitList.add(map);
				}
			}
		}

		if(deficitList!=null && deficitList.size()>0){
			//有数据 failure 赤字提醒
			return InvokeResult.failure("往来科目", deficitList);
		}
		return  InvokeResult.success();
	}


	@Override
	public VoucherDTO setVoucher(String yearMonth,String centerCode,String branchCode,String accBookCode,String accBookType) {
//		String centerCode = CurrentUser.getCurrentLoginManageBranch();
//		String branchCode = CurrentUser.getCurrentLoginManageBranch();
//		String accBookType = CurrentUser.getCurrentLoginAccountType();
//		String accBookCode = CurrentUser.getCurrentLoginAccount();

		VoucherDTO dto=new VoucherDTO();
		StringBuffer sql=new StringBuffer();
		sql.append("SELECT a.year_month_date as yearMonthDate,");
		sql.append("(select c.voucher_no from accvoucherno c where c.center_code = a.center_code and c.acc_book_type = a.acc_book_type and c.acc_book_code = a.acc_book_code and c.year_month_date = a.year_month_date) as voucherNo,");
		sql.append("(select m.voucher_date from accmainvoucher m where m.center_code = a.center_code and m.branch_code = ?2 and m.acc_book_type = a.acc_book_type and m.acc_book_code = a.acc_book_code and m.year_month_date = a.year_month_date ORDER BY m.voucher_no desc LIMIT 1 ) as voucherDate ");
		sql.append(" from AccMonthTrace a where a.center_code = ?1 and a.acc_book_type = ?3 and a.acc_book_code = ?4 and a.acc_month_stat !=3 ");

		Map<Integer, Object> params = new HashMap<>();
		params.put(1, centerCode);
		params.put(2, branchCode);
		params.put(3, accBookType);
		params.put(4, accBookCode);

		if (yearMonth!=null&&!"".equals(yearMonth)) {
			sql.append(" and a.year_month_date = ?5 ORDER BY a.year_month_date desc ");
			params.put(5, yearMonth);
		} else {
			sql.append(" ORDER BY a.year_month_date desc ");
		}
		List<?> list =voucherRepository.queryBySqlSC(sql.toString(), params);
		if(list!=null&&list.size()!=0){
			Map map = new HashMap();
			map.putAll((Map) list.get(0));
			dto.setYearMonth((String)map.get("yearMonthDate"));
			dto.setVoucherNo(centerCode + dto.getYearMonth().substring(2,6)+String.format("%06d", Integer.valueOf((String)map.get("voucherNo"))));
			if(!"".equals((String)map.get("voucherDate"))&&(String)map.get("voucherDate")!=null){
				dto.setVoucherDate((String)map.get("voucherDate"));
			}else{
				String str = dto.getYearMonth().substring(4,6);
				if ("JS".equals(str) || "13".equals(str) || "14".equals(str)) {
                    dto.setVoucherDate(dto.getYearMonth().substring(0,4)+"-12-31");
                } else {
                    dto.setVoucherDate(dto.getYearMonth().substring(0,4)+"-"+dto.getYearMonth().substring(4,6)+"-01");
                }
			}
			dto.setCreateBy(CurrentUser.getCurrentUser().getUserName());
		}
		return dto;
	}

	@Override
	public VoucherDTO setVoucher1(String yearMonth, String centerCode, String branchCode, String accBookCode, String accBookType,String createBy) {
		VoucherDTO dto=new VoucherDTO();
		StringBuffer sql=new StringBuffer();
		sql.append("SELECT a.year_month_date as yearMonthDate,");
		sql.append("(select c.voucher_no from accvoucherno c where c.center_code = a.center_code and c.acc_book_type = a.acc_book_type and c.acc_book_code = a.acc_book_code and c.year_month_date = a.year_month_date) as voucherNo,");
		sql.append("(select m.voucher_date from accmainvoucher m where m.center_code = a.center_code and m.branch_code = ?2 and m.acc_book_type = a.acc_book_type and m.acc_book_code = a.acc_book_code and m.year_month_date = a.year_month_date ORDER BY m.voucher_no desc LIMIT 1 ) as voucherDate ");
		sql.append(" from AccMonthTrace a where a.center_code = ?1 and a.acc_book_type = ?3 and a.acc_book_code = ?4 and a.acc_month_stat !=3 ");

		Map<Integer, Object> params = new HashMap<>();
		params.put(1, centerCode);
		params.put(2, branchCode);
		params.put(3, accBookType);
		params.put(4, accBookCode);

		if (yearMonth!=null&&!"".equals(yearMonth)) {
			sql.append(" and a.year_month_date = ?5 ORDER BY a.year_month_date desc ");
			params.put(5, yearMonth);
		} else {
			sql.append(" ORDER BY a.year_month_date desc ");
		}
		List<?> list =voucherRepository.queryBySqlSC(sql.toString(), params);
		if(list!=null&&list.size()!=0){
			Map map = new HashMap();
			map.putAll((Map) list.get(0));
			dto.setYearMonth((String)map.get("yearMonthDate"));
			dto.setVoucherNo(centerCode + dto.getYearMonth().substring(2,6)+String.format("%06d", Integer.valueOf((String)map.get("voucherNo"))));
			if(!"".equals((String)map.get("voucherDate"))&&(String)map.get("voucherDate")!=null){
				dto.setVoucherDate((String)map.get("voucherDate"));
			}else{
				if (!"JS".equals(dto.getYearMonth().substring(4,6))) {
					dto.setVoucherDate(dto.getYearMonth().substring(0,4)+"-"+dto.getYearMonth().substring(4,6)+"-01");
				}
			}
			dto.setCreateBy(createBy);
		}
		return dto;
	}

	/**
	 * 根据科目全代码检查科目是否允许使用
	 * @param subjectCodeAll 科目全代码
	 * @return 返回值String类型: notExist：不存在，notEnd：非末级，notUse：非使用状态，空字符串或null代表可以使用
	 */
	@Override
	public String checkSubjectCodePassMusterBySubjectCodeAll(String subjectCodeAll){
		if ("/".equals(subjectCodeAll.substring(subjectCodeAll.length()-1))) {
			//最后一个字符是“/”，去掉
			subjectCodeAll = subjectCodeAll.substring(0,subjectCodeAll.length()-1);
		}
		StringBuffer sql=new StringBuffer("select * from subjectinfo s  where s.account = ?1 and concat_ws(\"\",s.all_subject,s.subject_code) = ?2 ");

		Map<Integer, Object> params = new HashMap<>();
		params.put(1, CurrentUser.getCurrentLoginAccount());
		params.put(2, subjectCodeAll);

		List<SubjectInfo> list = (List<SubjectInfo>)subjectRepository.queryBySql(sql.toString(), params, SubjectInfo.class);
		if(list==null||list.size()==0){
			return "notExist";
		}else{
			if(list.get(0).getEndFlag().equals("1")){//0表示末级，1表示非末级
				return "notEnd";
			}
			if(list.get(0).getUseflag().equals("0")){//0表示停用，1表示使用
				return "notUse";
			}
			return "";
		}
	}

	/**
	 * 根据专项代码检查专项是否允许使用
	 * @param specialCode 专项代码
	 * @return 返回值String类型: notExist：不存在，notEnd：非末级，notUse：非使用状态，none:specialCode不能为空，空字符串或null代表可以使用
	 */
	@Override
	public String checkSpecialCodePassMusterBySpecialCode(String specialCode,String accBookCode){
		if("".equals(specialCode) || specialCode == null){
			return "none";
		}
		StringBuffer specialSql=new StringBuffer("select * from specialinfo s where s.account = ?1 and s.special_code = ?2 ");

		Map<Integer, Object> params = new HashMap<>();
		params.put(1, accBookCode);
		params.put(2, specialCode);

		List<SpecialInfo> specialList = (List<SpecialInfo>) specialInfoRepository.queryBySql(specialSql.toString(), params, SpecialInfo.class);
		if (specialList!=null&&specialList.size()>0) {
			if(specialList.get(0).getEndflag().equals("1")){//0表示末级，1表示非末级
				return "notEnd";
			}
			if(specialList.get(0).getUseflag().equals("0")){//0表示停用，1表示使用
				return "notUse";
			}
			return "";
		} else {
			return "notExist";
		}
	}

	@Override
	public String saveVoucher1(List<VoucherDTO> list2, List<VoucherDTO> list3, VoucherDTO dto) {
			synchronized (this) {
			String centerCode = dto.getCenterCode();
			String accBookCode = dto.getAccBookCode();
			String accBookType = dto.getAccBookType();
			String branchCode = dto.getBranchCode();
			if (dto.getOldVoucherNo()!=null&&!"".equals(dto.getOldVoucherNo())) {
				//占用已有凭证号
				//如果长度为9，则为完整凭证号，否则为有效号（去四位前缀）
				if (dto.getOldVoucherNo().length()!=(10+centerCode.length())) {
					try {
						//拼出完整的凭证号
						dto.setOldVoucherNo(centerCode+dto.getYearMonth().substring(2,6)+String.format("%06d", Integer.parseInt(dto.getOldVoucherNo())));
					} catch (Exception e) {
						logger.error("占用已有凭证号拼接异常", e);
						return "请输入有效的凭证号！";
					}
				}

				//校验通过，允许占用
				//当占用已有凭证操作时必须先调用此方法再保存凭证信息
				voucherManageService.voucherAnewSortBecauseOccupyOrDel(centerCode, centerCode, accBookType, accBookCode, dto.getYearMonth(), dto.getOldVoucherNo(),"occupy");
				//再将待保存凭证的凭证号改为占用的凭证号
				dto.setVoucherNo(dto.getOldVoucherNo());
			} else {
				//非占用凭证号的保存凭证，还需校验凭证号是否正确且合理
				//获取凭证最大号
				AccVoucherNoId avn = new AccVoucherNoId();
				avn.setCenterCode(centerCode);
				avn.setAccBookType(accBookType);
				avn.setAccBookCode(accBookCode);
				avn.setYearMonthDate(dto.getYearMonth());
				AccVoucherNo accVoucherNo = accVoucherNoRespository.findById(avn).get();
				// 这个是现取的凭证号
				String checkNo = centerCode+dto.getYearMonth().substring(2,6)+String.format("%06d", Integer.parseInt(accVoucherNo.getVoucherNo()));
//				if (!checkNo.equals(dto.getVoucherNo())) {
//					dto.setVoucherNo(checkNo);
//				}
				// 把判断去掉，直接现查现插入
				dto.setVoucherNo(checkNo);
			}
			//System.out.println("凭证录入开始：");
			AccMainVoucherId amid=new AccMainVoucherId();
			AccMainVoucher am =new AccMainVoucher();
			amid.setCenterCode(centerCode);
			amid.setBranchCode(branchCode);
			amid.setAccBookType(accBookType);
			amid.setAccBookCode(accBookCode);//账套编码
			amid.setYearMonthDate(dto.getYearMonth());
			amid.setVoucherNo(dto.getVoucherNo());
			am.setId(amid);
			//判断凭证类别
			if(dto.getVoucherType() == null || dto.getVoucherType().equals("")){
				am.setVoucherType("2");//凭证类型为记账凭证
			}else {
				am.setVoucherType(dto.getVoucherType());//凭证类型设置为参数类型
			}
			//判断凭证录入方式
			if(dto.getGenerateWay() == null || dto.getGenerateWay().equals("")){
				am.setGenerateWay("2");//凭证记账方式为手工
			}else {
				am.setGenerateWay(dto.getGenerateWay());//凭证记账方式为自动
			}
			//判断数据来源
			if (dto.getDataSource() == null || "".equals(dto.getDataSource())){
				am.setDataSource("1");
			}else{
				am.setDataSource(dto.getDataSource());
			}

			am.setVoucherDate(dto.getVoucherDate());
			if (dto.getAuxNumber()!=null&&!"".equals(dto.getAuxNumber())) {
				am.setAuxNumber(Integer.valueOf(dto.getAuxNumber()));
			}
			am.setCreateBranchCode("");//制单单位
			am.setCreateBy("1");
			am.setVoucherFlag("1"); //未复核
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = df.format(new Date());
			am.setCreateTime(date);
			voucherRepository.save(am);
			voucherRepository.flush();
			//System.out.println("凭证主表录入完成");
			for(int i=0;i<list2.size();i++){
				VoucherDTO vd1=list2.get(i);
				VoucherDTO vd2=list3.get(i);
				AccSubVoucherId asid=new AccSubVoucherId();
				AccSubVoucher as =new AccSubVoucher();
				asid.setCenterCode(centerCode);
				asid.setBranchCode(branchCode);
				asid.setAccBookType(accBookType);
				asid.setAccBookCode(accBookCode);//账套编码
				asid.setYearMonthDate(dto.getYearMonth());
				asid.setVoucherNo(dto.getVoucherNo());
				asid.setSuffixNo(i+1+"");
				as.setId(asid);
				String[] itemCode=vd1.getSubjectCode().split("/");
				if(itemCode.length>=1){as.setItemCode(itemCode[0]);as.setF01(itemCode[0]); }
				if(itemCode.length>=2){as.setF02(itemCode[1]);}
				if(itemCode.length>=3){as.setF03(itemCode[2]);}
				if(itemCode.length>=4){as.setF04(itemCode[3]);}
				if(itemCode.length>=5){as.setF05(itemCode[4]);}
				if(itemCode.length>=6){as.setF06(itemCode[5]);}
				if(itemCode.length>=7){as.setF07(itemCode[6]);}
				if(itemCode.length>=8){as.setF08(itemCode[7]);}
				if(itemCode.length>=9){as.setF09(itemCode[8]);}
				if(itemCode.length>=10){as.setF10(itemCode[9]);}
				if(itemCode.length>=11){as.setF11(itemCode[10]);}
				if(itemCode.length>=12){as.setF12(itemCode[11]);}
				if(itemCode.length>=13){as.setF13(itemCode[12]);}
				if(itemCode.length>=14){as.setF14(itemCode[13]);}
				if(itemCode.length>=15){as.setF15(itemCode[14]);}
				as.setDirectionIdx(vd1.getSubjectCode().endsWith("/")?vd1.getSubjectCode():vd1.getSubjectCode()+"/");
				if (vd1.getSubjectName()!=null&&!"".equals(vd1.getSubjectName())) {
					as.setDirectionIdxName(vd1.getSubjectName());
				} else {
					as.setDirectionIdxName(qrySubjectNameAllBySubjectCode(vd1.getSubjectCode()));
				}
				//需要再处理，里面可能保存除专项信息以外的特殊信息，需要单独做处理
				//除专项之外的特殊信息设置：水费的单价(5位小数)数量(2位小数)，电费的单价(2位小数)和数量(2位小数)，银行存款类的结算类型、结算单号、结算日期
				if (vd2.getUnitPrice()!=null&&!"".equals(vd2.getUnitPrice())) {
					as.setD01(vd2.getUnitPrice());//单价
				}
				if (vd2.getUnitNum()!=null&&!"".equals(vd2.getUnitNum())) {
					as.setD02(vd2.getUnitNum());//数量
				}
				if (vd2.getSettlementType()!=null&&!"".equals(vd2.getSettlementType())) {
					as.setD03(vd2.getSettlementType());//结算类型
				}
				if (vd2.getSettlementNo()!=null&&!"".equals(vd2.getSettlementNo())) {
					as.setD04(vd2.getSettlementNo());//结算单号
				}
				if (vd2.getSettlementDate()!=null&&!"".equals(vd2.getSettlementDate())) {
					as.setD05(vd2.getSettlementDate());//结算日期
				}
				if (vd2.getCheckNo()!=null&&!"".equals(vd2.getCheckNo())) {
					as.setCheckNo(vd2.getCheckNo());//支票号
				}
				if (vd2.getInvoiceNo()!=null&&!"".equals(vd2.getInvoiceNo())) {
					as.setInvoiceNo(vd2.getInvoiceNo());//发票号
				}
				//专项存储：根据 段定义表- AccSegmentDefine进行分字段存放
				if (vd2.getSpecialCodeS()!=null&&!"".equals(vd2.getSpecialCodeS())) {
					String[] specialCode=vd2.getSpecialCodeS().split(",");
					String[] specialSuperCode = vd2.getSpecialSuperCodeS().split(",");
					for(int m=0;m<specialCode.length;m++){
						StringBuffer sql=new StringBuffer();
						sql.append(" SELECT a.segment_flag as specialCode from AccSegmentDefine a where a.segment_col = ?1");

						Map<Integer, Object> params = new HashMap<>();
						params.put(1, specialSuperCode[m]);

						List<?> result =voucherRepository.queryBySqlSC(sql.toString(), params);
						Map map = new HashMap();
						String data=null;
						if(result!=null&&result.size()!=0){
							map.putAll((Map) result.get(0));
							data=(String)map.get("specialCode");//专项存储位置
							if(data.equals("s01")){as.setS01(specialCode[m]);
							}else if(data.equals("s02")){as.setS02(specialCode[m]);
							}else if(data.equals("s03")){as.setS03(specialCode[m]);
							}else if(data.equals("s04")){as.setS04(specialCode[m]);
							}else if(data.equals("s05")){as.setS05(specialCode[m]);
							}else if(data.equals("s06")){as.setS06(specialCode[m]);
							}else if(data.equals("s07")){as.setS07(specialCode[m]);
							}else if(data.equals("s08")){as.setS08(specialCode[m]);
							}else if(data.equals("s09")){as.setS09(specialCode[m]);
							}else if(data.equals("s10")){as.setS10(specialCode[m]);
							}else if(data.equals("s11")){as.setS11(specialCode[m]);
							}else if(data.equals("s12")){as.setS12(specialCode[m]);
							}else if(data.equals("s13")){as.setS13(specialCode[m]);
							}else if(data.equals("s14")){as.setS14(specialCode[m]);
							}else if(data.equals("s15")){as.setS15(specialCode[m]);
							}else if(data.equals("s16")){as.setS16(specialCode[m]);
							}else if(data.equals("s17")){as.setS17(specialCode[m]);
							}else if(data.equals("s18")){as.setS18(specialCode[m]);
							}else if(data.equals("s19")){as.setS19(specialCode[m]);
							}else if(data.equals("s20")){as.setS20(specialCode[m]);
							}else{
								logger.debug(specialCode[m]+"未配置在段定义表中！");
							}
						}
					}
				}
				if (vd2.getSpecialCodeS()!=null&&!"".equals(vd2.getSpecialCodeS())) {
					as.setDirectionOther(vd2.getSpecialCodeS());//专项编码（可多个,可为空）
				}
				as.setRemark(vd1.getRemarkName());
				if ((vd1.getTagCode()!=null&&!"".equals(vd1.getTagCode()))&&(vd1.getTagCodeS()!=null&&!"".equals(vd1.getTagCodeS()))) {
					as.setFlag(vd1.getTagCodeS());
				}
				as.setCurrency(currency);//人民币
				as.setExchangeRate(new BigDecimal(exchangeRate));//当前汇率
				if(vd1.getDebit()!=null&&!vd1.getDebit().equals("")){
					as.setDebitSource(new BigDecimal(vd1.getDebit()));
					as.setDebitDest(new BigDecimal(vd1.getDebit()));
				} else {
					as.setDebitSource(new BigDecimal("0.00"));
					as.setDebitDest(new BigDecimal("0.00"));
				}
				if(vd1.getCredit()!=null&&!vd1.getCredit().equals("")){
					as.setCreditSource(new BigDecimal(vd1.getCredit()));
					as.setCreditDest(new BigDecimal(vd1.getCredit()));
				} else {
					as.setCreditSource(new BigDecimal("0.00"));
					as.setCreditDest(new BigDecimal("0.00"));
				}
				as.setCreateBy("1");
				as.setCreateTime(date);
				voucherSubRepository.save(as);
				voucherSubRepository.flush();
			}
			//System.out.println("凭证子表录入完成");
			//将凭证号最大号数据+1
//			voucherRepository.voucherMaxNoAutoIncrementOne(centerCode ,accBookType,accBookCode,dto.getYearMonth(),"1");
//			voucherRepository.flush();
                // 由原来上述原生语句方式修改为Jpa 中形式方式进行刷入到数据库中。
            AccVoucherNoId id = new AccVoucherNoId();
            id.setCenterCode(centerCode);
            id.setAccBookType(accBookType);
            id.setAccBookCode(accBookCode);
            id.setYearMonthDate(dto.getYearMonth());
            AccVoucherNo voucherNo = accVoucherNoRespository.findById(id).get();
            voucherNo.setVoucherNo(String.valueOf(Integer.valueOf(voucherNo.getVoucherNo()) + 1));
            accVoucherNoRespository.saveAndFlush(voucherNo);
            accVoucherNoRespository.flush();

			return am.getId().getVoucherNo();
		}
	}

	@Override
	public String saveVoucherForFourS(List<VoucherDTO> list2, List<VoucherDTO> list3, VoucherDTO dto) {
		synchronized (this) {
			String centerCode = dto.getCenterCode();
			String accBookCode = dto.getAccBookCode();
			String accBookType = dto.getAccBookType();
			String branchCode = dto.getBranchCode();
			if (dto.getOldVoucherNo()!=null&&!"".equals(dto.getOldVoucherNo())) {
				//占用已有凭证号
				//如果长度为9，则为完整凭证号，否则为有效号（去四位前缀）
				if (dto.getOldVoucherNo().length()!=(10+centerCode.length())) {
					try {
						//拼出完整的凭证号
						dto.setOldVoucherNo(centerCode+dto.getYearMonth().substring(2,6)+String.format("%06d", Integer.parseInt(dto.getOldVoucherNo())));
					} catch (Exception e) {
						logger.error("占用已有凭证号拼接异常", e);
						return "请输入有效的凭证号！";
					}
				}

				//校验通过，允许占用
				//当占用已有凭证操作时必须先调用此方法再保存凭证信息
				voucherManageService.voucherAnewSortBecauseOccupyOrDel(centerCode, centerCode, accBookType, accBookCode, dto.getYearMonth(), dto.getOldVoucherNo(),"occupy");
				//再将待保存凭证的凭证号改为占用的凭证号
				dto.setVoucherNo(dto.getOldVoucherNo());
			} else {
				//非占用凭证号的保存凭证，还需校验凭证号是否正确且合理
				//获取凭证最大号
				AccVoucherNoId avn = new AccVoucherNoId();
				avn.setCenterCode(centerCode);
				avn.setAccBookType(accBookType);
				avn.setAccBookCode(accBookCode);
				avn.setYearMonthDate(dto.getYearMonth());
				AccVoucherNo accVoucherNo = accVoucherNoRespository.findById(avn).get();
				// 这个是现取的凭证号
				String checkNo = centerCode+dto.getYearMonth().substring(2,6)+String.format("%06d", Integer.parseInt(accVoucherNo.getVoucherNo()));
//				if (!checkNo.equals(dto.getVoucherNo())) {
//					dto.setVoucherNo(checkNo);
//				}
				// 把判断去掉，直接现查现插入
				dto.setVoucherNo(checkNo);
			}
			//System.out.println("凭证录入开始：");
			AccMainVoucherId amid=new AccMainVoucherId();
			AccMainVoucher am =new AccMainVoucher();
			amid.setCenterCode(centerCode);
			amid.setBranchCode(branchCode);
			amid.setAccBookType(accBookType);
			amid.setAccBookCode(accBookCode);//账套编码
			amid.setYearMonthDate(dto.getYearMonth());
			amid.setVoucherNo(dto.getVoucherNo());
			am.setId(amid);
			//判断凭证类别
			if(dto.getVoucherType() == null || dto.getVoucherType().equals("")){
				am.setVoucherType("2");//凭证类型为记账凭证
			}else {
				am.setVoucherType(dto.getVoucherType());//凭证类型设置为参数类型
			}
			//判断凭证录入方式
			if(dto.getGenerateWay() == null || dto.getGenerateWay().equals("")){
				am.setGenerateWay("2");//凭证记账方式为手工
			}else {
				am.setGenerateWay(dto.getGenerateWay());//凭证记账方式为自动
			}
			//判断数据来源
			if (dto.getDataSource() == null || "".equals(dto.getDataSource())){
				am.setDataSource("1");
			}else{
				am.setDataSource(dto.getDataSource());
			}

			am.setVoucherDate(dto.getVoucherDate());
			if (dto.getAuxNumber()!=null&&!"".equals(dto.getAuxNumber())) {
				am.setAuxNumber(Integer.valueOf(dto.getAuxNumber()));
			}
			am.setCreateBranchCode("");//制单单位
			am.setCreateBy("1");
			am.setVoucherFlag("1"); //未复核
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = df.format(new Date());
			am.setCreateTime(date);
			voucherRepository.save(am);
			voucherRepository.flush();
			//System.out.println("凭证主表录入完成");
			for(int i=0;i<list2.size();i++){
				VoucherDTO vd1=list2.get(i);
				VoucherDTO vd2=list3.get(i);
				AccSubVoucherId asid=new AccSubVoucherId();
				AccSubVoucher as =new AccSubVoucher();
				asid.setCenterCode(centerCode);
				asid.setBranchCode(branchCode);
				asid.setAccBookType(accBookType);
				asid.setAccBookCode(accBookCode);//账套编码
				asid.setYearMonthDate(dto.getYearMonth());
				asid.setVoucherNo(dto.getVoucherNo());
				asid.setSuffixNo(i+1+"");
				as.setId(asid);
				String[] itemCode=vd1.getSubjectCode().split("/");
				if(itemCode.length>=1){as.setItemCode(itemCode[0]);as.setF01(itemCode[0]); }
				if(itemCode.length>=2){as.setF02(itemCode[1]);}
				if(itemCode.length>=3){as.setF03(itemCode[2]);}
				if(itemCode.length>=4){as.setF04(itemCode[3]);}
				if(itemCode.length>=5){as.setF05(itemCode[4]);}
				if(itemCode.length>=6){as.setF06(itemCode[5]);}
				if(itemCode.length>=7){as.setF07(itemCode[6]);}
				if(itemCode.length>=8){as.setF08(itemCode[7]);}
				if(itemCode.length>=9){as.setF09(itemCode[8]);}
				if(itemCode.length>=10){as.setF10(itemCode[9]);}
				if(itemCode.length>=11){as.setF11(itemCode[10]);}
				if(itemCode.length>=12){as.setF12(itemCode[11]);}
				if(itemCode.length>=13){as.setF13(itemCode[12]);}
				if(itemCode.length>=14){as.setF14(itemCode[13]);}
				if(itemCode.length>=15){as.setF15(itemCode[14]);}
				as.setDirectionIdx(vd1.getSubjectCode().endsWith("/")?vd1.getSubjectCode():vd1.getSubjectCode()+"/");
				if (vd1.getSubjectName()!=null&&!"".equals(vd1.getSubjectName())) {
					as.setDirectionIdxName(vd1.getSubjectName());
				} else {
					as.setDirectionIdxName(qrySubjectNameAllBySubjectCode(vd1.getSubjectCode()));
				}
				//需要再处理，里面可能保存除专项信息以外的特殊信息，需要单独做处理
				//除专项之外的特殊信息设置：水费的单价(5位小数)数量(2位小数)，电费的单价(2位小数)和数量(2位小数)，银行存款类的结算类型、结算单号、结算日期
				if (vd2.getUnitPrice()!=null&&!"".equals(vd2.getUnitPrice())) {
					as.setD01(vd2.getUnitPrice());//单价
				}
				if (vd2.getUnitNum()!=null&&!"".equals(vd2.getUnitNum())) {
					as.setD02(vd2.getUnitNum());//数量
				}
				if (vd2.getSettlementType()!=null&&!"".equals(vd2.getSettlementType())) {
					as.setD03(vd2.getSettlementType());//结算类型
				}
				if (vd2.getSettlementNo()!=null&&!"".equals(vd2.getSettlementNo())) {
					as.setD04(vd2.getSettlementNo());//结算单号
				}
				if (vd2.getSettlementDate()!=null&&!"".equals(vd2.getSettlementDate())) {
					as.setD05(vd2.getSettlementDate());//结算日期
				}
				if (vd2.getCheckNo()!=null&&!"".equals(vd2.getCheckNo())) {
					as.setCheckNo(vd2.getCheckNo());//支票号
				}
				if (vd2.getInvoiceNo()!=null&&!"".equals(vd2.getInvoiceNo())) {
					as.setInvoiceNo(vd2.getInvoiceNo());//发票号
				}
				//专项存储：根据 段定义表- AccSegmentDefine进行分字段存放
				if (vd2.getSpecialCodeS()!=null&&!"".equals(vd2.getSpecialCodeS())) {
					String[] specialCode=vd2.getSpecialCodeS().split(",");
					String[] specialSuperCode = vd2.getSpecialSuperCodeS().split(",");
					for(int m=0;m<specialCode.length;m++){
						StringBuffer sql=new StringBuffer();
						sql.append(" SELECT a.segment_flag as specialCode from AccSegmentDefine a where a.segment_col = ?1");

						Map<Integer, Object> params = new HashMap<>();
						params.put(1, specialSuperCode[m]);

						List<?> result =voucherRepository.queryBySqlSC(sql.toString(), params);
						Map map = new HashMap();
						String data=null;
						if(result!=null&&result.size()!=0){
							map.putAll((Map) result.get(0));
							data=(String)map.get("specialCode");//专项存储位置
							if(data.equals("s01")){as.setS01(specialCode[m]);
							}else if(data.equals("s02")){as.setS02(specialCode[m]);
							}else if(data.equals("s03")){as.setS03(specialCode[m]);
							}else if(data.equals("s04")){as.setS04(specialCode[m]);
							}else if(data.equals("s05")){as.setS05(specialCode[m]);
							}else if(data.equals("s06")){as.setS06(specialCode[m]);
							}else if(data.equals("s07")){as.setS07(specialCode[m]);
							}else if(data.equals("s08")){as.setS08(specialCode[m]);
							}else if(data.equals("s09")){as.setS09(specialCode[m]);
							}else if(data.equals("s10")){as.setS10(specialCode[m]);
							}else if(data.equals("s11")){as.setS11(specialCode[m]);
							}else if(data.equals("s12")){as.setS12(specialCode[m]);
							}else if(data.equals("s13")){as.setS13(specialCode[m]);
							}else if(data.equals("s14")){as.setS14(specialCode[m]);
							}else if(data.equals("s15")){as.setS15(specialCode[m]);
							}else if(data.equals("s16")){as.setS16(specialCode[m]);
							}else if(data.equals("s17")){as.setS17(specialCode[m]);
							}else if(data.equals("s18")){as.setS18(specialCode[m]);
							}else if(data.equals("s19")){as.setS19(specialCode[m]);
							}else if(data.equals("s20")){as.setS20(specialCode[m]);
							}else{
								logger.debug(specialCode[m]+"未配置在段定义表中！");
							}
						}
					}
				}
				if (vd2.getSpecialCodeS()!=null&&!"".equals(vd2.getSpecialCodeS())) {
					as.setDirectionOther(vd2.getSpecialCodeS());//专项编码（可多个,可为空）
				}
				as.setRemark(vd1.getRemarkName());
				if ((vd1.getTagCode()!=null&&!"".equals(vd1.getTagCode()))&&(vd1.getTagCodeS()!=null&&!"".equals(vd1.getTagCodeS()))) {
					as.setFlag(vd1.getTagCodeS());
				}
				as.setCurrency(currency);//人民币
				as.setExchangeRate(new BigDecimal(exchangeRate));//当前汇率
				if(vd1.getDebit()!=null&&!vd1.getDebit().equals("")){
					as.setDebitSource(new BigDecimal(vd1.getDebit()));
					as.setDebitDest(new BigDecimal(vd1.getDebit()));
				} else {
					as.setDebitSource(new BigDecimal("0.00"));
					as.setDebitDest(new BigDecimal("0.00"));
				}
				if(vd1.getCredit()!=null&&!vd1.getCredit().equals("")){
					as.setCreditSource(new BigDecimal(vd1.getCredit()));
					as.setCreditDest(new BigDecimal(vd1.getCredit()));
				} else {
					as.setCreditSource(new BigDecimal("0.00"));
					as.setCreditDest(new BigDecimal("0.00"));
				}
				as.setCreateBy("1");
				as.setCreateTime(date);
				voucherSubRepository.save(as);
				voucherSubRepository.flush();
			}
			//System.out.println("凭证子表录入完成");
			//将凭证号最大号数据+1
//			voucherRepository.voucherMaxNoAutoIncrementOne(centerCode ,accBookType,accBookCode,dto.getYearMonth(),"1");
//			voucherRepository.flush();
			// 由原来上述原生语句方式修改为Jpa 中形式方式进行刷入到数据库中。
			AccVoucherNoId id = new AccVoucherNoId();
			id.setCenterCode(centerCode);
			id.setAccBookType(accBookType);
			id.setAccBookCode(accBookCode);
			id.setYearMonthDate(dto.getYearMonth());
			AccVoucherNo voucherNo = accVoucherNoRespository.findById(id).get();
			voucherNo.setVoucherNo(String.valueOf(Integer.valueOf(voucherNo.getVoucherNo()) + 1));
			accVoucherNoRespository.saveAndFlush(voucherNo);
			accVoucherNoRespository.flush();


			System.out.println("当前凭证号为："+am.getId().getVoucherNo());
			return "success";
		}
	}
}
