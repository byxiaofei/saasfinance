package com.sinosoft.service;


import com.sinosoft.common.InvokeResult;
import com.sinosoft.domain.SubjectInfo;
import com.sinosoft.dto.SubjectInfoDTO;
import com.sinosoft.dto.VoucherDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface VoucherService {

	public static final String SUBJECT_ISEXISTE = "SUBJECT_ISEXIST";

	/**
	 * 查询标注树信息(同时排除树末级为非末级的标注)，参数（可为空，多个时用“,”隔开）
	 * @return
	 */
	List<?> qryTagList(String value);
	/**
	 * 根据摘要名称查询摘要信息，摘要名称可为空（此时查询所有）
	 * @param value
	 * @return
	 */
	List<?> qryRemarkList(String value);
	/**
	 * 查询父级科目编码
	 * @return
	 */
	List<?> qrySubjectCodeForCheck(String value);

	/**
	 * 查询父级科目编码
	 * @param value 科目名称
	 * @param onlyLastStage (若此参数为空，则默认为 Y )
	 * @return
	 */
	List<?> qrySubjectCodeForCheck(String value, String onlyLastStage);

	/**
	 * 根据科目名称模糊查询科目树信息（编码、名称）
	 * @param value 科目名称
	 * @param onlyLastStage 是否仅查询末级：Y:是，N:否 (若此参数为空，则默认为 Y )
	 * @return
	 */
	public List<?> qrySubjectCodeForCheckByValue(String value, String onlyLastStage);

	/**
	 * 根据科目全代码查询科目全名称
	 * @param subjectCode 科目代码
	 * @return
	 */
	String qrySubjectNameAllBySubjectCode(String subjectCode);
	/**
	 * 根据科目全代码校验科目数据
	 * @param subjectCode 科目全代码
	 * @return
	 */
	InvokeResult checkSubjectByCode(String subjectCode);

	/**
	 * 根据专项代码查询专项简称/全称(本法适用于凭证录入或编辑或查看时)
	 * @param codeS 专项代码，如果存在多个专项代码则用“,”分隔，如果存在二级则用“;”分隔 eg: 1.BM05,BM06  ，2.BM05;BM06,ZC010101;ZC010102
	 * @param specialNameP 专项是否全称显示(0:否，1:是；可为空)
	 * @return 返回字符串结果规则与专项代码参数相同
	 */
	String qrySpecialNamePBySpecialCode(String codeS, String specialNameP);

	/**
	 * 根据一级专项查询专项树信息
	 * @param originalValue 一级专项编码
	 * @param inputValue 专项名称
	 * @return
	 */
	List<?> qrySpecialTreeBySuperSpecial(String originalValue, String inputValue);

	/**
	 *
	 * 功能描述:	根据一级专项查询专项数信息(只显示正在使用的)
	 *
	 */
	List<?> qrySpecialTreeUseFlagBySuperSpecial(String originalValue, String inputValue);

	List<?> getSpecialCode(String subjectCode);

	/**
	 * 根据专项代码查新部分专项信息(id、专项代码、专项简称、专项全称、上级专项ID)
	 * @param specialCode
	 * @return
	 */
	VoucherDTO getSpecialDateBySpecialCode(String specialCode);

	InvokeResult saveVoucher(List<VoucherDTO> list2, List<VoucherDTO> list3, VoucherDTO dto);

	InvokeResult updateVoucher(List<VoucherDTO> list2, List<VoucherDTO> list3,VoucherDTO dto);

	/**
	 * 编辑凭证（自动生成凭证的编辑，只允许部分编辑）
	 * @param list2
	 * @param dto
	 * @return
	 */
	InvokeResult updateVoucher2(List<VoucherDTO> list2, VoucherDTO dto);

	List<?> getCopyVoucher(VoucherDTO dto);

	VoucherDTO getCopyData(VoucherDTO dto);

    /**
     * 根据会计期间、凭证号进行凭证信息的查询（上一张、下一张）
     * @param dto
     * @param beforeOrAfter 参数：before-上一张，after-下一张
     * @param type 参数：look-查看，edit-编辑
     * @return
     */
    VoucherDTO beforeOrAfterVoucher(VoucherDTO dto, String beforeOrAfter, String type);

	InvokeResult deficitRemind(List<VoucherDTO> list2, List<VoucherDTO> list3, VoucherDTO dto);

//	String deficitRemindInterface(List<VoucherDTO> list2,List<VoucherDTO> list3,VoucherDTO dto,String centerCode,String accBookCode,String branchCode,String accBookType);

	VoucherDTO setVoucher(String yearMonth,String centerCode,String branchCode,String accBookCode,String accBookType);

	/**
	 *
	 * 功能描述:		接口对接->校验当前账套信息下是否有凭证月,如果同时并生成凭证号
	 *
	 */
	VoucherDTO setVoucher1(String yearMonth,String centerCode,String branchCode,String accBookCode,String accBookType,String createBy);
	/**
	 * 根据科目全代码检查科目是否允许使用
	 * @param subjectCodeAll 科目全代码
	 * @return 返回值String类型: notExist：不存在，notEnd：非末级，notUse：非使用状态，空字符串或null代表可以使用
	 */
	String checkSubjectCodePassMusterBySubjectCodeAll(String subjectCodeAll);
	/**
	 * 根据专项代码检查专项是否允许使用
	 * @param specialCode 专项代码
	 * @return 返回值String类型: notExist：不存在，notEnd：非末级，notUse：非使用状态，空字符串或null代表可以使用
	 */
	String checkSpecialCodePassMusterBySpecialCode(String specialCode,String accBookCode);

	/**
	 *
	 * 功能描述:	 接口对接
	 *
	 */
	String saveVoucher1(List<VoucherDTO> list2, List<VoucherDTO> list3, VoucherDTO dto);

	/**
	 * 	功能描述：4s接口对接 保存凭证
	 * @param list2
	 * @param list3
	 * @param dto
	 * @return
	 */
	String saveVoucherForFourS(List<VoucherDTO> list2 , List<VoucherDTO> list3 , VoucherDTO dto );
}
