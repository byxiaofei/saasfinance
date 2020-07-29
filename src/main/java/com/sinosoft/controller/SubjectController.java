package com.sinosoft.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.sinosoft.common.DataGrid;
import com.sinosoft.common.InvokeResult;
import com.sinosoft.common.SysLog;
import com.sinosoft.domain.SubjectInfo;
import com.sinosoft.dto.SubjectInfoDTO;
import com.sinosoft.service.SubjectService;
import com.sinosoft.service.VoucherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/subject")
public class SubjectController {
	@Resource
	private SubjectService subjectService;
	@Resource
	private VoucherService voucherService;

	private Logger logger = LoggerFactory.getLogger(SubjectController.class);

	@RequestMapping("/")
	public String page(){
		return "system/subject";
	}


	@RequestMapping(path="/list")
	@ResponseBody
	public DataGrid qryProjectInfo(@RequestParam int page, @RequestParam int rows, SubjectInfoDTO subjectInfoDTO){
		Page<SubjectInfo> res = subjectService.qryProjectInfo(page,rows, subjectInfoDTO);
		return new DataGrid(res);
	}

	@RequestMapping(path="/listALL")
	@ResponseBody
	public List<?> qryALLSubject(SubjectInfoDTO subjectInfoDTO){
		//return subjectService.qryALLSubject(subjectInfoDTO);
		return subjectService.getALLSubject(subjectInfoDTO);
	}

	/**
	 * 查询加载科目为末级时关联专项信息
	 * @return
	 */
	@RequestMapping(path="/specialList")
	@ResponseBody
	public List<?> qryspecialList(){
		return subjectService.getspecialList();
	}

	/**
	 * 查询加载科目为末级时关联专项信息
	 * @return
	 */
	@RequestMapping(path="/specialListUnlimited")
	@ResponseBody
	public List<?> qryspecialListAndAddUnlimited(){
		List<Object> resultAll=new ArrayList<Object>();
		resultAll.addAll(subjectService.getspecialList());
		Map map=new HashMap();
		map.put("text", "-不限-");
		map.put("value", null);
		resultAll.add(0,map);
		return resultAll;
	}

	/*@SysLog(value = "新增科目信息")  //这里添加了AOP的自定义注解
	@PostMapping("/add")
	@ResponseBody
	public InvokeResult addSubjectInfo(SubjectInfoDTO subjectInfoDTO){
		try{
			String msg = subjectService.saveSubjectInfo(subjectInfoDTO);
			if(msg.equals("SUBJECT_ISEXISTE")){
				return InvokeResult.failure("用户编码已存在");
			}
			return InvokeResult.success();
		}catch(Exception e){
			logger.error("新增科目异常", e);
			return InvokeResult.failure("操作失败！请联系系统管理员。");
		}
	}*/
	@SysLog(value = "新增科目信息")  //这里添加了AOP的自定义注解
	@PostMapping("/add")
	@ResponseBody
	public InvokeResult addSubjectInfo(SubjectInfoDTO subjectInfoDTO){
		try{
			//	System.out.print("controller---"+""+subjectInfoDTO.getSubjectCode()+" "+subjectInfoDTO.getSubjectName()+" "+subjectInfoDTO.getAllSubject());
			String msg = subjectService.saveSubjectInfo(subjectInfoDTO);
			//	System.out.println(msg);
			if(msg.equals("SUBJECT_ISEXIST")){
				return InvokeResult.failure("科目编码已存在");
			}
			return InvokeResult.success();
		}catch(Exception e){
			logger.error("新增科目异常", e);
			return InvokeResult.failure("操作失败！请联系系统管理员。");
		}
	}
	@SysLog(value = "编辑科目信息")  //这里添加了AOP的自定义注解
	@PostMapping("/edit")
	@ResponseBody
	public InvokeResult editSubjectInfo(SubjectInfo subjectInfo){
		try{
			String msg = subjectService.updateSubjectInfo(subjectInfo);
			if(msg.equals("SUBJECT_ISEXIST")){
				return InvokeResult.failure("科目编码已存在");
			}
			if(msg.equals("SUBJECT_NEXTEXIST")){
				return InvokeResult.failure("科目存在下级，不能编辑");
			}
			if(msg.equals("CANNOT_CHANGE")){
				return InvokeResult.failure("科目代码或名称不能更改");
			}
			if(msg.equals("ACCSUB_ISEXISTE")){
				return InvokeResult.failure("此科目已使用凭证，不能编辑");
			}
			return InvokeResult.success();
		}catch(Exception e){
			logger.error("编辑科目异常", e);
			return InvokeResult.failure("操作失败！请联系系统管理员。");
		}
	}

	@SysLog(value = "删除科目信息")  //这里添加了AOP的自定义注解
	@PostMapping("/delete")
	@ResponseBody
	public InvokeResult deleteSubjectInfo(String ids){
		try{
			String msg = subjectService.deleteSubjectInfo(ids);
			if(msg.equals("SUBJECT_NEXTEXIST")){
				return InvokeResult.failure("科目存在下级，不能删除");
			}
			if(msg.equals("ACCSUB_ISEXISTE")){
				return InvokeResult.failure("此科目已使用凭证，不能删除");
			}
			return InvokeResult.success();
		}catch(Exception e){
			logger.error("删除科目异常", e);
			return InvokeResult.failure("操作失败！请联系系统管理员。");
		}
	}

	@PostMapping("/next")
	@ResponseBody
	public int nextSubjectInfo(String id){
		int count=0;
		try{
			count= subjectService.findNextFlag(Integer.parseInt(id));
			//System.out.println(count);
			return count;
		}catch(Exception e){
			logger.error("查看科目是否有下级时异常", e);
		}
		return count;
	}
	@RequestMapping(path="/super")
	@ResponseBody
	public List<?> getCheckData(String code, String onlyLastStage){
		/*return subjectService.qrySubjectCodeForCheck(code);*/
		return voucherService.qrySubjectCodeForCheck(code, onlyLastStage);
	}

	/**
	 * 导出科目管理列表
	 */
	@RequestMapping(path="/subjectdownload")
	public void export(HttpServletRequest request, HttpServletResponse response, String name, String queryConditions, String cols){
		subjectService.exportByCondition(request, response, name, queryConditions, cols);
	}

	@RequestMapping(path = "/getaccount")
	@ResponseBody
	public List<Map<String,String>> getAccount(){
		List<Map<String,String>> list = subjectService.getAccount();
		return list;
	}

	/**
	 * 根据纯数字编码确定科目全编码(带“/”的)
	 * @param numberCode
	 * @param slashFlag 如果为 true ，则返回最后一位字符带“/”的，否则不带
	 * @return
	 */
	@RequestMapping(path = "/getSubjectCodeByNumCode")
	@ResponseBody
	public String getSubjectCodeByNumCode(String numberCode, Boolean slashFlag){
		return subjectService.getSubjectCodeByNumCode(numberCode, slashFlag);
	}
}
