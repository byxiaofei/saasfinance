package com.sinosoft.service.impl;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.sinosoft.common.CurrentTime;
import com.sinosoft.common.CurrentUser;
import com.sinosoft.common.CusSpecification;
import com.sinosoft.domain.SpecialInfo;
import com.sinosoft.domain.SubjectInfo;
import com.sinosoft.dto.MenuInfoDTO;
import com.sinosoft.dto.SubjectInfoDTO;
import com.sinosoft.repository.SpecialInfoRepository;
import com.sinosoft.repository.SubjectRepository;
import com.sinosoft.service.SubjectService;

import com.sinosoft.util.ExcelUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Service
public class SubjectServiceImp implements SubjectService {
	@Resource
	private SubjectRepository subjectRepository;
	@Resource
	private SpecialInfoRepository specialInfoRepository;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@PersistenceContext
	private EntityManager em; //注入EntityManager
	private Logger logger = LoggerFactory.getLogger(SubjectServiceImp.class);


	@Override
	public Page<SubjectInfo> qryProjectInfo(int page, int rows, SubjectInfoDTO subjectInfoDTO) {
		CusSpecification<SubjectInfo> cs = new CusSpecification<SubjectInfo>().and(
				CusSpecification.Cnd.ge("subjectCode", subjectInfoDTO.getSubjectCode()),
				CusSpecification.Cnd.le("subjectCode", subjectInfoDTO.getSubjectCodeEnd()),
				CusSpecification.Cnd.ge("level", subjectInfoDTO.getLevel()),
				CusSpecification.Cnd.le("level", subjectInfoDTO.getLevelEnd()),
				CusSpecification.Cnd.like("subjectName", subjectInfoDTO.getSubjectName()),
				CusSpecification.Cnd.eq("subjectType", subjectInfoDTO.getSubjectType()),
				CusSpecification.Cnd.eq("endFlag", subjectInfoDTO.getEndFlag()),
				CusSpecification.Cnd.eq("useflag", subjectInfoDTO.getUseflag()),
				CusSpecification.Cnd.eq("direction", subjectInfoDTO.getDirection())).asc("subjectCode");
		return subjectRepository.findAll(cs, new PageRequest((page - 1), rows));
	}

	@Override
	public List<?> qryALLSubject(SubjectInfoDTO subjectInfoDTO) {
		List<?> list = subjectRepository.findALLSubject(CurrentUser.getCurrentLoginAccount());
		return list;
	}
	@Override
	public List<?> getspecialList() {
		List<?> list = subjectRepository.getspecialList(CurrentUser.getCurrentLoginAccount());
		return list;
	}

	/*@Transactional
	public String saveSubjectInfo(SubjectInfoDTO subjectInfoDTO) {
		if(subjectRepository.findByCondition(subjectInfoDTO.getSubjectCode(),"222").size() > 0){
			return SUBJECT_ISEXISTE;
		}else{
			int level=1;
			SubjectInfo ss =new SubjectInfo();
			ss.setSubjectCode(subjectInfoDTO.getSubjectCode());
			ss.setSubjectName(subjectInfoDTO.getSubjectName());
			ss.setDirection(subjectInfoDTO.getDirection());
			ss.setEndFlag(subjectInfoDTO.getEndFlag());
			ss.setSubjectType(subjectInfoDTO.getSubjectType());
			ss.setAllSubject(subjectInfoDTO.getAllSubject());
			if(!subjectInfoDTO.getAllSubject().equals("")&&subjectInfoDTO.getAllSubject()!=null){
				String[] codes=subjectInfoDTO.getAllSubject().split("/");
				String id=subjectRepository.findByCondition(codes[codes.length-1],"222").get(0).getId()+"";
				level=Integer.parseInt(subjectRepository.findByCondition(codes[codes.length-1],"222").get(0).getLevel());
				ss.setSuperSubject(id);
			}
			ss.setSpecialId(subjectInfoDTO.getSpecialId());
			ss.setTemp(subjectInfoDTO.getTemp());
			ss.setUseflag(subjectInfoDTO.getUseflag());
			if(level==1){
				ss.setLevel(level+"");
			}else{
				ss.setLevel(level+1+"");
			}

			ss.setCreateOper(CurrentUser.getCurrentUser().getId() + "");
			ss.setCreateTime(CurrentTime.getCurrentTime());
			ss.setAccount("222");
			subjectRepository.save(ss);
			return "success";
		}
	}*/

	@Transactional
	public String saveSubjectInfo(SubjectInfoDTO subjectInfoDTO) {
		//当前账套
		String account=CurrentUser.getCurrentLoginAccount();
		List<?> list = null;
		if (subjectInfoDTO.getAllSubject()!=null&&!"".equals(subjectInfoDTO.getAllSubject())) {
			list = subjectRepository.findByCondition(subjectInfoDTO.getAllSubject()+"/"+subjectInfoDTO.getSubjectCode(),account);
		} else {
			list = subjectRepository.findByCondition(subjectInfoDTO.getSubjectCode(),account);
		}
		if(list!=null&&list.size()>0){
			return SUBJECT_ISEXISTE;
		}else{
			int level=1;
			SubjectInfo ss =new SubjectInfo();
			ss.setSubjectCode(subjectInfoDTO.getSubjectCode());
			ss.setSubjectName(subjectInfoDTO.getSubjectName());
			ss.setDirection(subjectInfoDTO.getDirection());
			ss.setEndFlag(subjectInfoDTO.getEndFlag());
			ss.setSubjectType(subjectInfoDTO.getSubjectType());
			if(subjectInfoDTO.getAllSubject()==null||subjectInfoDTO.getAllSubject().equals("")){
				ss.setAllSubject("");
			}else{
				if(subjectInfoDTO.getAllSubject().charAt(subjectInfoDTO.getAllSubject().length()-1)=='/' ){
					ss.setAllSubject(subjectInfoDTO.getAllSubject());
				}else{
					ss.setAllSubject(subjectInfoDTO.getAllSubject()+"/");
				}
			}
			if(subjectInfoDTO.getAllSubject()!=null&&!subjectInfoDTO.getAllSubject().equals("")){
				SubjectInfo subjectInfo = subjectRepository.findByCondition(ss.getAllSubject().substring(0,ss.getAllSubject().length()-1),account).get(0);
				level=Integer.parseInt(subjectInfo.getLevel());
				level=level+1;
				ss.setSuperSubject(subjectInfo.getId()+"");
			}else{
				ss.setSuperSubject("");
			}

			String specialId = "";
			if (subjectInfoDTO.getSpecialId()!=null&&!"".equals(subjectInfoDTO.getSpecialId())) {
				List<Integer> tempList = new ArrayList<>();
				String[] str = subjectInfoDTO.getSpecialId().trim().split(",");
				for (String s : str) {
					if (s!=null && !"".equals(s)) {
						tempList.add(Integer.valueOf(s.trim()));
					}
				}
				if (tempList.size()>0) {
					Integer[] temp = new Integer[tempList.size()];
					tempList.toArray(temp);
					Arrays.sort(temp);
					for (int i=0;i<temp.length;i++) {
						if (i!=temp.length-1) {
							specialId += temp[i]+",";
						} else {
							specialId += temp[i];
						}
					}
				}
			}

			ss.setSpecialId(specialId);
			ss.setTemp(subjectInfoDTO.getTemp());
			ss.setUseflag(subjectInfoDTO.getUseflag());

			ss.setLevel(level+"");
			ss.setCreateOper(CurrentUser.getCurrentUser().getId() + "");
			ss.setCreateTime(CurrentTime.getCurrentTime());
			ss.setAccount(account);
			subjectRepository.save(ss);
			return "success";
		}
	}

	@Transactional
	public String updateSubjectInfo(SubjectInfo subjectInfo) {
		String centerCode = CurrentUser.getCurrentLoginManageBranch();
		int level = 1;
		SubjectInfo ss = subjectRepository.findById(subjectInfo.getId()).get();
		//查询下级是否存在
		int count=subjectRepository.findNextFlag(subjectInfo.getId());
		if (count>0) {
			return "SUBJECT_NEXTEXIST";
		}
		//判断编辑科目是否已使用凭证
		String allSubjectCode="";
		if(ss.getEndFlag()!=null&&"0".equals(ss.getEndFlag())){
			if(ss.getAllSubject()==null||ss.getAllSubject().equals("")){
				allSubjectCode=ss.getSubjectCode()+"/";
			}else{
				if(ss.getAllSubject().charAt(ss.getAllSubject().length()-1)=='/' ){
					allSubjectCode=ss.getAllSubject()+ss.getSubjectCode()+"/";
				}else{
					allSubjectCode=ss.getAllSubject()+"/"+ss.getSubjectCode()+"/";
				}
			}
			int isNum=subjectRepository.findUSeAccsub(ss.getAccount(),allSubjectCode, centerCode);
			isNum +=subjectRepository.findUSeAccsubHis(ss.getAccount(),allSubjectCode, centerCode);
			if(isNum>0){
				//凭证存在，不能编辑
				return ACCSUB_ISEXISTE;
			}
		}
		//判断编辑科目代码是否存在
		if(!subjectInfo.getSubjectCode().equals(ss.getSubjectCode())){
			if(subjectRepository.findByCondition(subjectInfo.getAllSubject()+subjectInfo.getSubjectCode(),ss.getAccount()).size() > 0){
				return SUBJECT_ISEXISTE;
			}
		}
		if ("0".equals(ss.getEndFlag())) {//原为末级科目，原非末级的不会出现在摘要表中
			if(!subjectInfo.getSubjectCode().equals(ss.getSubjectCode()) || !subjectInfo.getSubjectName().equals(ss.getSubjectName())){
				if(count<1){
					String oldCode = ss.getAllSubject()+ss.getSubjectCode()+"/";
					String newCode = subjectInfo.getAllSubject()+subjectInfo.getSubjectCode()+"/";
					//查看摘要设置是否关联
					if(subjectRepository.selectRemark(oldCode,ss.getAccount(), CurrentUser.getCurrentLoginManageBranch())>0){
						if ("0".equals(subjectInfo.getEndFlag())){//现仍为末级
							if (!subjectInfo.getSubjectName().equals(ss.getSubjectName())){
								String newName = subjectInfo.getSubjectName()+"/";
								long supId = 0;
								if (ss.getSuperSubject()!=null&&!"".equals(ss.getSuperSubject())) {
									supId = Long.parseLong(ss.getSuperSubject());
								}
								while (supId!=0) {
									SubjectInfo s = subjectRepository.findById(supId).get();
									if (s!=null) {
										if (s.getSuperSubject()!=null&&!"".equals(s.getSuperSubject())) {
											supId = Long.parseLong(s.getSuperSubject());
										} else {
											supId = 0;
										}
										newName = s.getSubjectName()+"/"+newName;
									} else {
										supId = 0;
									}
 								}
								int s=subjectRepository.updateAccrem(newCode, newName, oldCode, ss.getAccount(), centerCode);
							} else {
								int s=subjectRepository.updateAccrem(newCode, oldCode, ss.getAccount(), centerCode);
							}
						} else if ("1".equals(subjectInfo.getEndFlag())) {//现为非末级
							subjectRepository.delAccrem(oldCode, ss.getAccount(), centerCode);
						}
					}
					subjectRepository.flush();
				}else{
					//不能更改
					return CANNOT_CHANGE;
				}
			}
		}

		ss.setSubjectCode(subjectInfo.getSubjectCode());
		ss.setSubjectName(subjectInfo.getSubjectName());
		ss.setDirection(subjectInfo.getDirection());
		//	System.out.println(subjectRepository.findEndFlag(subjectInfo.getId()));

		ss.setEndFlag(subjectInfo.getEndFlag());
		ss.setSubjectType(subjectInfo.getSubjectType());
		ss.setAllSubject(subjectInfo.getAllSubject());
		/*if (subjectInfo.getAllSubject() != null && !subjectInfo.getAllSubject().equals("")) {
			String[] codes = subjectInfo.getAllSubject().split("/");
			String id = subjectRepository.findByCondition(codes[codes.length - 1], subjectInfo.getAccount()).get(0).getId() + "";
			level = Integer.parseInt(subjectRepository.findByCondition(codes[codes.length - 1], subjectInfo.getAccount()).get(0).getLevel());
			level=level+1;
			ss.setSuperSubject(id);
		}*/

		/*ss.setLevel(level + "");*/

		String specialId = "";
		if (subjectInfo.getSpecialId()!=null&&!"".equals(subjectInfo.getSpecialId())) {
			List<Integer> tempList = new ArrayList<>();
			String[] str = subjectInfo.getSpecialId().trim().split(",");
			for (String s : str) {
				if (s!=null && !"".equals(s)) {
					tempList.add(Integer.valueOf(s.trim()));
				}
			}
			if (tempList.size()>0) {
				Integer[] temp = new Integer[tempList.size()];
				tempList.toArray(temp);
				Arrays.sort(temp);
				for (int i=0;i<temp.length;i++) {
					if (i!=temp.length-1) {
						specialId += temp[i]+",";
					} else {
						specialId += temp[i];
					}
				}
			}
		}
		ss.setSpecialId(specialId);
		ss.setTemp(subjectInfo.getTemp());
		ss.setUseflag(subjectInfo.getUseflag());
		ss.setUpdateOper(CurrentUser.getCurrentUser().getId() + "");
		ss.setUpdateTime(CurrentTime.getCurrentTime());
		subjectRepository.save(ss);
		return "success";
	}
	@Transactional
	public String deleteSubjectInfo(String ids) {
		String[] id = ids.split(",");
		for (int i = 0; i < id.length; i++) {
			String allSubjectCode="";
			SubjectInfo subjectInfo = subjectRepository.findById(Long.valueOf(id[i])).get();
			//查询下级是否存在
			int count=subjectRepository.findNextFlag(subjectInfo.getId());
			if (count>0) {
				return "SUBJECT_NEXTEXIST";
			}
			if(subjectInfo.getAllSubject()==null||subjectInfo.getAllSubject().equals("")){
				allSubjectCode=subjectInfo.getSubjectCode()+"/";
			}else{
				if(subjectInfo.getAllSubject().charAt(subjectInfo.getAllSubject().length()-1)=='/' ){
					allSubjectCode=subjectInfo.getAllSubject()+subjectInfo.getSubjectCode()+"/";
				}else{
					allSubjectCode=subjectInfo.getAllSubject()+"/"+subjectInfo.getSubjectCode()+"/";
				}
			}
			int isNum=subjectRepository.findUSeAccsub(subjectInfo.getAccount(),allSubjectCode, CurrentUser.getCurrentLoginManageBranch());
			isNum += subjectRepository.findUSeAccsubHis(subjectInfo.getAccount(),allSubjectCode, CurrentUser.getCurrentLoginManageBranch());
			if(isNum>0){
				//凭证存在，不能编辑
				return ACCSUB_ISEXISTE;
			}
			subjectRepository.delete(subjectInfo);
		}
		return "success";
	}

	@Override
	public List<?> qrySubjectCodeForCheck(String code) {
		List resultListAll=new ArrayList();
		List<Map<String, Object>> subjectTypeList = subjectRepository.findSubjectType();
		for (Object subjectTypeObject : subjectTypeList) {
			Map subjectTypeMap = new HashMap();
			subjectTypeMap.putAll((Map) subjectTypeObject);
			Integer subjectType = Integer.valueOf((String)subjectTypeMap.get("id"));
			List resultList=new ArrayList();
			List<Map<String, Object>> list = subjectRepository.findSuperCode(CurrentUser.getCurrentLoginAccount(),subjectType,code);
			for (Object obj : list) {
				//Map map=(Map) obj;
				Map map = new HashMap();
				map.putAll((Map) obj);
				List list2=qryChildrenForCheck((String)map.get("id"),code);
				map.put("id",map.get("mid"));
				map.put("text",map.get("text"));
				if(list2!=null){
					map.put("children",list2);
				}
				resultList.add(map);
			}
			if (resultList!=null&&resultList.size()>0) {
				subjectTypeMap.put("children",resultList);
				resultListAll.add(subjectTypeMap);
			}
		}
		return resultListAll;
	}

	@Override
	public int findNextFlag(int id){
		int count=subjectRepository.findNextFlag(id);
		return count;
	}

	private List<MenuInfoDTO> qryChildrenForCheck(String id,String code){
		List list1=new ArrayList();
		List<?> list = subjectRepository.findSuperCode(id,CurrentUser.getCurrentLoginAccount(),code);
		if(list!=null&&list.size()>0&&!list.isEmpty()){
			for (Object obj : list) {
				Map map = new HashMap();
				map.putAll((Map) obj);
				List list2=qryChildrenForCheck((String)map.get("id"),code);
				if(list2!=null&&list2.size()>0&&!list2.isEmpty()){
					map.put("id",map.get("mid"));
					map.put("text",map.get("text"));
					map.put("children",list2);
					map.put("state","closed");
					//map.put("checked", map.get("mid")!=null&&!map.get("mid").equals("")?true:false);
				}else{
					map.put("id",map.get("mid"));
					map.put("text",map.get("text"));
					//map.put("checked", map.get("mid")!=null&&!map.get("mid").equals("")?true:false);
				}
				list1.add(map);
			}
		}
		return list1;
	}


	public List<?> getALLSubject(SubjectInfoDTO subjectInfoDTO) {
		long start = System.currentTimeMillis();
		StringBuffer sql=new StringBuffer("select s.id as id, s.subject_code as subjectCode,s.subject_name as subjectName,s.subject_type as subjectType, " +
				"s.all_subject as allSubject,concat_ws('',s.all_subject,s.subject_code) as subjectCodeAll,s.special_id as specialId,s.super_subject as superSubject,s.direction as direction," +
				"s.end_flag as endFlag,s.level as level,s.useflag as useflag,s.temp as temp,s.create_oper as createOper," +
				"s.create_time as createTime,s.update_oper as updateOper,s.update_time as updateTime," +
				"s.account as account," +
				"(select c.code_name from codemanage c where c.code_code=s.subject_type and c.code_type='subjectType') as subjectTypeName," +
				"(select c.code_name from codemanage c where c.code_code=s.direction and c.code_type='balanceDirection') as directionName," +
				"(select c.code_name from codemanage c where c.code_code=s.end_flag and c.code_type='endflag') as endFlagName," +
				"(select c.code_name from codemanage c where c.code_code=s.useflag and c.code_type='useflag') as useflagName," +
				"(select u.user_name from userinfo u where u.id=s.create_oper) as createOperName," +
				"(select u.user_name from userinfo u where u.id=s.update_oper) as updateOperName " +
				"from subjectinfo s where 1=1 " );

		int paramsNo = 1;
		Map<Integer, Object> params = new HashMap<>();

		sql.append(" and s.account = ?" + paramsNo);
		params.put(paramsNo, CurrentUser.getCurrentLoginAccount());
		paramsNo++;

		if(subjectInfoDTO.getSubjectCode()!=null&&!"".equals(subjectInfoDTO.getSubjectCode())){
			sql.append(" and concat_ws('',s.all_subject,s.subject_code) >= ?" + paramsNo);
			params.put(paramsNo, subjectInfoDTO.getSubjectCode());
			paramsNo++;
		}
		if(subjectInfoDTO.getSubjectCodeEnd()!=null&&!"".equals(subjectInfoDTO.getSubjectCodeEnd())){
			sql.append(" and(");
			sql.append(" concat_ws('',s.all_subject,s.subject_code) <= ?" + paramsNo);
			params.put(paramsNo, subjectInfoDTO.getSubjectCodeEnd());
			paramsNo++;
			sql.append(" or concat_ws('',s.all_subject,s.subject_code) like ?" + paramsNo);
			sql.append(")");
			params.put(paramsNo, subjectInfoDTO.getSubjectCodeEnd()+"%");
			paramsNo++;
		}
		if(subjectInfoDTO.getLevel()!=null&&!"".equals(subjectInfoDTO.getLevel())){
			sql.append(" and s.level >= ?" + paramsNo);
			params.put(paramsNo, subjectInfoDTO.getLevel());
			paramsNo++;
		}
		if(subjectInfoDTO.getLevelEnd()!=null&&!"".equals(subjectInfoDTO.getLevelEnd())){
			sql.append(" and s.level <= ?" + paramsNo);
			params.put(paramsNo, subjectInfoDTO.getLevelEnd());
			paramsNo++;
		}
		if(subjectInfoDTO.getSubjectName()!=null&&!"".equals(subjectInfoDTO.getSubjectName())){
			sql.append(" and s.subject_name like ?" + paramsNo);
			params.put(paramsNo, "%"+subjectInfoDTO.getSubjectName()+"%");
			paramsNo++;
		}
		if(subjectInfoDTO.getSubjectType()!=null&&!"".equals(subjectInfoDTO.getSubjectType())){
			sql.append(" and s.subject_type = ?" + paramsNo);
			params.put(paramsNo, subjectInfoDTO.getSubjectType());
			paramsNo++;
		}
		if(subjectInfoDTO.getEndFlag()!=null&&!"".equals(subjectInfoDTO.getEndFlag())){
			sql.append(" and s.end_flag = ?" + paramsNo);
			params.put(paramsNo, subjectInfoDTO.getEndFlag());
			paramsNo++;
		}
		if(subjectInfoDTO.getUseflag()!=null&&!"".equals(subjectInfoDTO.getUseflag())){
			sql.append(" and s.useflag = ?" + paramsNo);
			params.put(paramsNo, subjectInfoDTO.getUseflag());
			paramsNo++;
		}
		if(subjectInfoDTO.getDirection()!=null&&!"".equals(subjectInfoDTO.getDirection())){
			sql.append(" and s.direction = ?" + paramsNo);
			params.put(paramsNo, subjectInfoDTO.getDirection());
			paramsNo++;
		}
		if(subjectInfoDTO.getSpecialId()!=null&&!"".equals(subjectInfoDTO.getSpecialId())){
			sql.append(" and (");
			sql.append(" s.special_id = ?" + paramsNo);
			params.put(paramsNo, subjectInfoDTO.getSpecialId());
			paramsNo++;
			sql.append(" or s.special_id like ?" + paramsNo);
			params.put(paramsNo, subjectInfoDTO.getSpecialId()+",%");
			paramsNo++;
			sql.append(" or s.special_id like ?" + paramsNo);
			params.put(paramsNo, "%,"+subjectInfoDTO.getSpecialId());
			paramsNo++;
			sql.append(" or s.special_id like ?" + paramsNo);
			params.put(paramsNo, "%,"+subjectInfoDTO.getSpecialId()+",%");
			paramsNo++;
			sql.append(" )");
		}

		sql.append(" ORDER BY concat_ws('',s.all_subject,s.subject_code)");

		List<?> list =subjectRepository.queryBySqlSC(sql.toString(), params);
		System.out.println("科目体系查询耗时："+(System.currentTimeMillis()-start)+"ms");

		//设置专项名称（存在多个专项）
		if (list!=null&&list.size()>0) {
			//先查选出一级专项信息，再处理成需要的数据
			Map<String,String> specialSuperMap = new HashMap<>();
			List<?> specialSuperList = getspecialList();
			for (Object obj: specialSuperList) {
				Map map = (Map) obj;
				specialSuperMap.put(map.get("value").toString(), map.get("text").toString());
			}
			StringBuffer superSql = new StringBuffer("select distinct s.super_subject as superSubject from subjectinfo s where 1=1 and s.super_subject !='' and s.super_subject is not null");
			superSql.append(" and s.account = ?1");

			params = new HashMap<>();
			params.put(1, CurrentUser.getCurrentLoginAccount());

			List<?> superList =subjectRepository.queryBySqlSC(superSql.toString(), params);
			List<Integer> superSet = new ArrayList<>();
			for (Object obj:superList) {
				superSet.add(Integer.valueOf(((Map)obj).get("superSubject").toString()));
			}
            for (Object obj : list) {
            	Map map = (Map) obj;
            	String specialId = ((String) map.get("specialId"));
            	if (specialId!=null&&!"".equals(specialId)) {
            		String[] ids = specialId.trim().split(",");
            		for (int i=0;i<ids.length;i++) {
            			if (i!=0) {
            				map.put("specialIdName", map.get("specialIdName")+","+specialSuperMap.get(ids[i].trim()));
            			} else {
            				map.put("specialIdName", specialSuperMap.get(ids[i].trim()));
            			}
            		}
            	}
            	/*String endFlag = (String) map.get("endFlag");
            	if (endFlag!=null&&"0".equals(endFlag)) {
            		map.put("NextJi", 0);
            	} else {
            		int count = subjectRepository.findNextFlag(Integer.parseInt(String.valueOf(map.get("id"))));
            		map.put("NextJi", count);
            	}*/
				Integer id = (Integer) map.get("id");
				if (superSet.contains(id)) {
					map.put("NextJi", 1);
				} else {
					map.put("NextJi", 0);
				}
            }
        }
		System.out.println("科目体系查询总耗时："+(System.currentTimeMillis()-start)+"ms");
		return  list;
	}

	@Override
	public void exportByCondition(HttpServletRequest request, HttpServletResponse response, String name,
								  String queryConditions,  String cols){
        ExcelUtil excelUtil = new ExcelUtil();

		try {
			SubjectInfoDTO subjectInfoDTO = new ObjectMapper().readValue(queryConditions, SubjectInfoDTO.class);
			// 3. 根据条件查询导出数据集
			List<?> dataList = getALLSubject(subjectInfoDTO);
			// 4. 导出
			excelUtil.exportu(request, response, name, cols, dataList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<SubjectInfoDTO> ImportDataFromExcel(SubjectInfoDTO subjectInfoDTO,InputStream is, String excelFiileName){
		List<SubjectInfoDTO> list = new ArrayList<>();
		try{
			//创建工作簿(输入流，文件名)
			Workbook workbook = this.createWorkbook(is,excelFiileName);
			//创建工作表sheet，第一页（工作簿，工作簿序号）
			Sheet sheet = this.getSheet(workbook,2);//第三页是财产险，第四页是人身险
			//获取sheet中数据的行数
			int rows = sheet.getPhysicalNumberOfRows();
			//获取表头单元格个数
			int cells = sheet.getRow(0).getPhysicalNumberOfCells();
			//利用反射，给JavaBean的属性进行赋值（获取实体类的属性列）
			for (int i = 1;i<rows;i++){
				SubjectInfoDTO dto = new SubjectInfoDTO();
				//获取DTO类的属性列表
				Field[] fields = dto.getClass().getDeclaredFields();

				Row row = sheet.getRow(i);//获取行数据
				int index = 0;//单元格序号
				while(index < cells){
					Cell cell = row.getCell(index);//获取行中单元格的内容
					if (null == cell){
						cell = row.createCell(index);//单元格为空的话就创建一个新的
					}
					cell.setCellType(Cell.CELL_TYPE_STRING);//设置单元格类型为String
					String value = null == cell.getStringCellValue()?"":cell.getStringCellValue();//以String类型获取单元格内容
					Field field = fields[index];//获取DTO类属性
					String filedName = field.getName();//实体类属性名称
					String methodName = "set"+filedName.substring(0,1).toUpperCase()+filedName.substring(1);//获取对应属性的set方法
					Method setMethod = dto.getClass().getMethod(methodName,new Class[]{String.class});
					setMethod.invoke(dto,new Object[]{value});//给实体类对象赋值
					index++;
				}
				if (isHasValues(dto)){//判断对象属性是否有值
					list.add(dto);

				}

			}
		}catch (IOException e){
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		return list;
	}
	//生成workbook(文件后缀名为.xls)
	public Workbook createWorkbook(InputStream is,String excelFileName)throws IOException{
		return new HSSFWorkbook(is);
	}
	//根据sheet索引号获取对应的sheet
	private Sheet getSheet(Workbook workbook, int i) {
		return workbook.getSheetAt(0);
	}

	//判断一个对象所有属性是否有值，如果一个属性有值（非空），则返回true
	public boolean isHasValues(Object object){
		Field[] fields = object.getClass().getDeclaredFields();
		boolean flag = false ;
		for (int i= 0;i<fields.length;i++){
			String fieldName = fields[i].getName();
			String methodName = "get"+fieldName.substring(0,1).toUpperCase()+fieldName.substring(1);
			Method getMethod;
			try{
				getMethod = object.getClass().getMethod(methodName);
				Object obj = getMethod.invoke(object);
				if(null != obj && !("".equals(obj))){
					flag = true;
					break;
				}
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		return flag ;
	}

	//判断一个科目代码的父级ID是否存在于
	public String HasSuperSubjectId(List<SubjectInfo> copylist,String subjectCode)throws Exception {
		int flag = 0;
		for (SubjectInfo s : copylist) {
			String superCode = (s.getAllSubject()==null?"":s.getAllSubject()) + s.getSubjectCode() + "/";
			String[] code = subjectCode.split("/");
			String str = superCode+ code[code.length - 1]+"/";
			if (subjectCode.equals(str)) {
				return String.valueOf(s.getId());
			} else {
				flag++;
			}
			if (flag == copylist.size()) {
				System.out.println("subjectCode:" + subjectCode);
				throw new Exception("父级id找不到");
			}

		}

		return "";
	}
	//判断科目所带专项的ID是否存在
	public String HasSprcialId(String specialCode,String account)throws Exception{
		String id = "";
		int []specialid = null;
		//没带专项时
		if (specialCode == null || specialCode.equals("")){
			return "";
		}else {
			//带多个专项时
			if (specialCode.contains(",")){
				String []special = specialCode.split(",");
				for (int i=0;i< special.length;i++){
					if (String.valueOf(specialInfoRepository.getSpecialId(specialCode,account)) != null)
						specialid[i] = specialInfoRepository.getSpecialId(specialCode,account);
					else {
						System.out.println("specialCode:"+specialCode);
						throw new Exception("专项id找不到");
					}
				}
				Arrays.sort(specialid);
				for (int a:specialid){
					id += String.valueOf(a)+",";
				}
				return id.substring(0,id.length()-1);
			}else {
				if ( String.valueOf(specialInfoRepository.getSpecialId(specialCode,account))!= null){
					return String.valueOf(specialInfoRepository.getSpecialId(specialCode,account));
				}else {
					System.out.println("specialCode:"+specialCode);
					throw new Exception("专项id找不到");
				}
			}
		}

	}

	//查询账套
	@Override
	public List<Map<String,String>> getAccount(){
		return subjectRepository.getAccount();
	}

	/**
	 * 根据纯数字编码确定科目全编码(带“/”的)
	 * @param numberCode
	 * @param slashFlag 如果为 true ，则返回最后一位字符带“/”的，否则不带
	 * @return
	 */
	@Override
	public String getSubjectCodeByNumCode(String numberCode, Boolean slashFlag){
		if (numberCode!=null && !"".equals(numberCode)) {
			String accountCode = CurrentUser.getCurrentLoginAccount();
			//先按精确匹配，如果未找到则再按逐位递减模糊匹配，最终找不到就不再处理
			String result = subjectRepository.findSubjectCodeByNumCode(accountCode, numberCode);
			if (!(result!=null && !"".equals(result))) {
				String newNumberCode = numberCode;

				do {
					result = subjectRepository.findSubjectCodeByLikeNumCode(accountCode, newNumberCode);
					//逐位递减模糊匹配
					newNumberCode = newNumberCode.substring(0, newNumberCode.length()-1);
				} while (!(result!=null && !"".equals(result)) && newNumberCode.length()>=4);

				if (result!=null && !"".equals(result)) {
					String str = "";
					String[] strs = result.split("/");
					int len = strs.length;
					for (int i=0;i<len;i++) {
						if (i != len-1) {
							str += numberCode.substring(0, strs[i].length()) + "/";
							numberCode = numberCode.substring(strs[i].length());
						} else {
							str += numberCode;
						}
					}

					result = str;
				}
			}

			if (result!=null && !"".equals(result)) {
				if (slashFlag) {
					return result + "/";
				} else {
					return result;
				}
			}
		}
		return "";
	}
}



