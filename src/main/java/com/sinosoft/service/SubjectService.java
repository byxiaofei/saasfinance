package com.sinosoft.service;


import com.sinosoft.domain.SubjectInfo;
import com.sinosoft.domain.UserInfo;
import com.sinosoft.dto.SubjectInfoDTO;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface SubjectService {

	public static final String SUBJECT_ISEXISTE = "SUBJECT_ISEXIST";
	//public static final String MOJICANNOT_CHANGE="MOJICANNOT_CHANGE";
	public static final String CANNOT_CHANGE="CANNOT_CHANGE";
	public static final String ACCSUB_ISEXISTE="ACCSUB_ISEXISTE";

	/**
	 * 分页查询
	 * @param page 起始页
	 * @param rows 记录数
	 * @param subjectInfoDTO
	 * @return
	 */
	public Page<SubjectInfo> qryProjectInfo(int page, int rows, SubjectInfoDTO subjectInfoDTO);

	/**
	 * 列表查询数据
	 * @param subjectInfoDTO
	 * @return
	 */
	List<?> qryALLSubject(SubjectInfoDTO subjectInfoDTO);
	List<?> getALLSubject(SubjectInfoDTO subjectInfoDTO);

	/**
	 * 新增保存科目
	 * @param subjectInfoDTO
	 * @return
	 */
	/*public String saveSubjectInfo(SubjectInfoDTO subjectInfoDTO);*/

	/**
	 * 新增保存一级科目
	 * @param subjectInfoDTO
	 * @return
	 */
	public String saveSubjectInfo(SubjectInfoDTO subjectInfoDTO);

	/**
	 * 修改科目信息
	 * @param subjectInfo
	 * @return
	 */
	public String updateSubjectInfo(SubjectInfo subjectInfo);

	/**
	 * 删除科目信息
	 * @param ids
	 * @return
	 */
	public String deleteSubjectInfo(String ids);

	/**
	 * 查询父级科目编码
	 * @return
	 */
	List<?> qrySubjectCodeForCheck(String code);

	List<?> getspecialList();

	/**
	 * 查询下级编码是否存在
	 * @return
	 */
	int findNextFlag(int id);

	/**
	 * 将符合条件的数据导出至Excel中
	 * @param request
	 * @param response
	 * @param name 导出文件名称
	 * @param queryConditions 封装导出数据限制条件
	 * @param cols 导出列
	 */
	public void exportByCondition(HttpServletRequest request, HttpServletResponse response, String name,
								  String queryConditions, String cols);


	public List<Map<String,String>> getAccount();

	/**
	 * 根据纯数字编码确定科目全编码(带“/”的)
	 * @param numberCode
	 * @param slashFlag 如果为 true ，则返回最后一位字符带“/”的，否则不带
	 * @return
	 */
	public String getSubjectCodeByNumCode(String numberCode, Boolean slashFlag);
}
