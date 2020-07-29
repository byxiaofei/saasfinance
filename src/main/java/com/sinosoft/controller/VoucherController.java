package com.sinosoft.controller;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sinosoft.common.CurrentUser;
import com.sinosoft.common.InvokeResult;
import com.sinosoft.common.SysLog;
import com.sinosoft.dto.VoucherDTO;
import com.sinosoft.service.VoucherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/voucher")
public class VoucherController {
	@Resource
	private VoucherService voucherService;
	private Logger logger = LoggerFactory.getLogger(VoucherController.class);

	@RequestMapping("/")
	public ModelAndView page(){
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("account/voucher");
		return modelAndView;
	}

	/**
	 * 跳转到凭证查看页面
	 * @param voucherNo 凭证号
	 * @param yearMonth 会计期间
	 * @param type
	 * @param specialNameP 专项是否全称显示(0:否，1:是；可为空)
	 * @param request
	 * @return
	 */
	@RequestMapping("/look")
	public ModelAndView pageLook(@RequestParam String voucherNo, String yearMonth, @RequestParam String type, String specialNameP, String suffixNo, HttpServletRequest request){
		/*request.getSession().setAttribute("sessionLookVoucherNo", voucherNo);
		request.getSession().setAttribute("sessionLookYearMonth", yearMonth);
		request.getSession().setAttribute("sessionLookSpecialNameP", specialNameP);
		request.getSession().setAttribute("sessionLookType",type);*/

		Map<String, Object> map = new HashMap<>();
		map.put("lookVoucherNo", voucherNo);
		if (yearMonth!=null && !"".equals(yearMonth)) {
			map.put("lookYearMonth", yearMonth);
		} else {
			String centerCode = CurrentUser.getCurrentLoginManageBranch();
			map.put("lookYearMonth", "20" + voucherNo.substring(centerCode.length(), centerCode.length()+4));
		}
		map.put("lookSpecialNameP", specialNameP);
		map.put("lookType", type);
		map.put("lookSuffixNo", suffixNo);
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("account/lookvoucher");
		modelAndView.addObject("map",map);
		return modelAndView;
	}

	/**
	 * 跳转到凭证编辑页面
	 * @param voucherNo 凭证号
	 * @param yearMonth 会计期间
	 * @param specialNameP 专项是否全称显示(0:否，1:是；可为空)
	 * @param request
	 * @return
	 */
	@RequestMapping("/edit")
	public ModelAndView pageEdit(@RequestParam String voucherNo, @RequestParam String yearMonth, String specialNameP, HttpServletRequest request){
		/*request.getSession().setAttribute("sessionEditVoucherNo", voucherNo);
		request.getSession().setAttribute("sessionEditYearMonth", yearMonth);
		request.getSession().setAttribute("sessionEditSpecialNameP", specialNameP);*/

		Map<String, Object> map = new HashMap<>();
		map.put("editVoucherNo", voucherNo);
		map.put("editYearMonth", yearMonth);
		map.put("editSpecialNameP", specialNameP);
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("account/editvoucher");
		modelAndView.addObject("map",map);
		return modelAndView;
	}

	/**
	 * 跳转到凭证编辑页面（自动生成凭证）
	 * @param voucherNo 凭证号
	 * @param yearMonth 会计期间
	 * @param specialNameP 专项是否全称显示(0:否，1:是；可为空)
	 * @param request
	 * @return
	 */
	@RequestMapping("/autoedit")
	public ModelAndView pageEdit2(@RequestParam String voucherNo, @RequestParam String yearMonth, String specialNameP, HttpServletRequest request){
		Map<String, Object> map = new HashMap<>();
		map.put("editVoucherNo", voucherNo);
		map.put("editYearMonth", yearMonth);
		map.put("editSpecialNameP", specialNameP);
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("account/autoeditvoucher");
		modelAndView.addObject("map",map);
		return modelAndView;
	}

	/**
	 * 查询标注信息(同时排除树末级为非末级的标注)，参数（可为空，多个时用“,”隔开）
	 * @param value
	 * @return
	 */
	@RequestMapping(path="/tagList")
	@ResponseBody
	public List<?> getTagList(String value){
		return voucherService.qryTagList(value);
	}

	/**
	 * 根据摘要名称查询摘要信息，摘要名称可为空（此时查询所有）
	 * @param value 摘要名称可为空
	 * @return
	 */
	@RequestMapping(path="/remarkList")
	@ResponseBody
	public List<?> getremarkList(String value){
		return voucherService.qryRemarkList(value);
	}

	@RequestMapping(path="/subjectList")
	@ResponseBody
	public List<?> getCheckData(String value){
		return voucherService.qrySubjectCodeForCheck(value);
	}
	/**
	 * 根据科目名称模糊查询科目树信息（编码、名称）
	 * @param value 科目名称
	 * @param onlyLastStage 是否仅查询末级：Y:是，N:否 (若此参数为空，则默认为 Y )
	 * @return
	 */
	@RequestMapping(path="/subjectListByName")
	@ResponseBody
	public List<?> qrySubjectCodeForCheckByValue(@RequestParam String value, String onlyLastStage){ return voucherService.qrySubjectCodeForCheckByValue(value, onlyLastStage); }

	/**
	 * 根据科目全代码查询科目全名称
	 * @param subjectCode 科目代码
	 * @return
	 */
	@RequestMapping(path="/subjectNameAllBySubjectCode")
	@ResponseBody
	public String qrySubjectNameAllBySubjectCode(@RequestParam String subjectCode){ return voucherService.qrySubjectNameAllBySubjectCode(subjectCode); }

	/**
	 * 根据科目全代码校验科目数据
	 * @param subjectCode 科目全代码
	 * @return
	 */
	@RequestMapping(path="/checkSubjectByCode")
	@ResponseBody
	public InvokeResult checkSubjectByCode(@RequestParam String subjectCode){ return voucherService.checkSubjectByCode(subjectCode); }

	/**
	 * 根据专项代码查询专项简称/全称
	 * @param codeS 专项代码，如果存在多个专项代码则用“,”分隔，如果存在二级则用“;”分隔 eg: 1.BM05,BM06  ，2.BM05;BM06,ZC010101;ZC010102
	 * @param specialNameP 专项是否全称显示(0:否，1:是；可为空)
	 * @return 返回字符串结果规则与专项代码参数相同
	 */
	@RequestMapping(path="/specialNamePBySpecialCode")
	@ResponseBody
	public String qrySpecialNamePBySpecialCode(@RequestParam String codeS, String specialNameP){ return voucherService.qrySpecialNamePBySpecialCode(codeS, specialNameP); }

	/**
	 * 根据一级专项查询专项树信息
	 * @param originalValue 一级专项编码
	 * @param inputValue 专项名称
	 * @return
	 */
	@RequestMapping(path="/specialTree")
	@ResponseBody
	public List<?> qrySpecialTreeBySuperSpecial(String originalValue, String inputValue){
		return voucherService.qrySpecialTreeBySuperSpecial(originalValue, inputValue);
	}

	/**
	 * @Description:	根据一级专项查询专项树信息，只展示使用状态的
	 * @Author: luodejun
	 * @Date: 2020/4/14 14:06
	 * @Param:  [originalValue, inputValue]
	 * @Return: java.util.List<?>
	 * @Exception:
	 */
	@RequestMapping(path = "/specialTreeUseFlogOne")
	@ResponseBody
	public List<?> qrySpecialTreeUseFlagBySuperSpecial(String originalValue,String inputValue){
		return voucherService.qrySpecialTreeUseFlagBySuperSpecial(originalValue,inputValue);

	}

	/**
	 * 设置凭证录入页面初始化信息
	 * 初始化凭证号，会计日期，凭证日期，录入人，参数可为空
	 * @param yearMonth
	 * @return
	 */
	@RequestMapping(path="/getDate")
	@ResponseBody
	public VoucherDTO getDate(String yearMonth){
		String centerCode = CurrentUser.getCurrentLoginManageBranch();
		String branchCode = CurrentUser.getCurrentLoginManageBranch();
		String accBookType = CurrentUser.getCurrentLoginAccountType();
		String accBookCode = CurrentUser.getCurrentLoginAccount();
		return voucherService.setVoucher(yearMonth,centerCode,branchCode,accBookCode,accBookType);
	}

	/**
	 * 根据选择科目编码进行该科目关联专项查询
	 * @param subjectCode
	 * @return
	 */
	@RequestMapping(path="/getSpecialDate")
	@ResponseBody
	public List<?> getSpecialCode(String subjectCode){ return voucherService.getSpecialCode(subjectCode); }

	/**
	 * 根据专项代码查新部分专项信息(id、专项代码、专项简称、专项全称)
	 * @param specialCode
	 * @return
	 */
	@RequestMapping(path="/getSpecialDateBySpecialCode")
	@ResponseBody
	public VoucherDTO getSpecialDateBySpecialCode(String specialCode){
		return voucherService.getSpecialDateBySpecialCode(specialCode);
	}

	/**
	 * 根据前台设置的筛选条件进行凭证信息的查询
	 * @param dto
	 * @return
	 */
	@RequestMapping(path="/getCopyVoucher")
	@ResponseBody
	public  List<?>  getCopyVoucher(VoucherDTO dto){
		return voucherService.getCopyVoucher(dto);
	}
	/**
	 * 根据会计期间、凭证号进行凭证信息的查询
	 * @param dto
	 * @return
	 */
	@RequestMapping(path="/getCopyData")
	@ResponseBody
	public  VoucherDTO  getCopyData(VoucherDTO dto){
        VoucherDTO voucherDTO = new VoucherDTO();
		try {
            voucherDTO = voucherService.getCopyData(dto);
        } catch (Exception e) {
		    logger.error("获取凭证数据异常", e);
        }
	    return voucherDTO;
	}

    /**
     * 根据会计期间、凭证号进行凭证信息的查询（上一张、下一张）
     * @param dto
     * @param beforeOrAfter 参数：before-上一张，after-下一张
     * @return
     */
    @RequestMapping(path="/beforeOrAfterVoucher")
    @ResponseBody
    public  VoucherDTO  beforeOrAfterVoucher(VoucherDTO dto, String beforeOrAfter, String type){
        VoucherDTO voucherDTO = new VoucherDTO();
        try {
            voucherDTO = voucherService.beforeOrAfterVoucher(dto, beforeOrAfter, type);
        } catch (Exception e) {
            logger.error("获取凭证数据异常", e);
        }
        return voucherDTO;
    }

	/**
	 * 赤字提醒校验
	 * @param data2 科目信息
	 * @param data3 专项信息
	 * @param voucherNo
	 * @param yearMonth
	 * @param voucherDate
	 * @param auxNumber
	 * @param createBy
	 * @param oldVoucherNo
	 * @return
	 */
	@PostMapping("/deficitRemind")
	@ResponseBody
	public InvokeResult deficitRemind(String data2,String data3,String voucherNo,
									String yearMonth,String voucherDate,String auxNumber,
									String createBy,String oldVoucherNo){
		VoucherDTO dto=new VoucherDTO();
		dto.setVoucherNo(voucherNo);
		dto.setYearMonth(yearMonth);
		dto.setVoucherDate(voucherDate);
		dto.setAuxNumber(auxNumber);
		dto.setCreateBy(createBy);
		dto.setOldVoucherNo(oldVoucherNo);
		List<VoucherDTO> list2;
		List<VoucherDTO> list3;
		try {
			list2 = readJson(data2, List.class, VoucherDTO.class);
			list3 = readJson(data3, List.class, VoucherDTO.class);
			return voucherService.deficitRemind(list2,list3,dto);
		} catch (Exception e) {
			logger.error("赤字提醒异常！",e);
			return InvokeResult.failure("赤字校验异常！");
		}
	}

	/**
	 * 新增凭证信息
	 * @param data2 科目信息
	 * @param data3 专项信息
	 * @param voucherNo
	 * @param yearMonth
	 * @param voucherDate
	 * @param auxNumber
	 * @param createBy
	 * @param oldVoucherNo
	 * @return
	 */
	@SysLog(value = "新增凭证信息")
	@PostMapping("/saveVoucher")
	@ResponseBody
	public InvokeResult saveVoucher(String data2,String data3,String voucherNo,
									String yearMonth,String voucherDate,String auxNumber,
									String createBy,String oldVoucherNo){
		VoucherDTO dto=new VoucherDTO();
		dto.setVoucherNo(voucherNo);
		dto.setYearMonth(yearMonth);
		dto.setVoucherDate(voucherDate);
		dto.setAuxNumber(auxNumber);
		dto.setCreateBy(createBy);
		dto.setOldVoucherNo(oldVoucherNo);
		List<VoucherDTO> list2;
		List<VoucherDTO> list3;
        try {
            list2 = readJson(data2, List.class, VoucherDTO.class);
			list3 = readJson(data3, List.class, VoucherDTO.class);
			return voucherService.saveVoucher(list2,list3,dto);
        } catch (Exception e) {
			logger.error("凭证保存异常！",e);
			return InvokeResult.failure("凭证保存异常！");
        }

	}

    /**
     * 编辑凭证信息
     * @param data2 科目信息
     * @param data3 专项信息
     * @param voucherNo
     * @param yearMonth
     * @param voucherDate
     * @param auxNumber
     * @param createBy
     * @param oldVoucherNo
     * @return
     */
    @SysLog(value = "编辑凭证信息")
    @PostMapping("/updateVoucher")
    @ResponseBody
    public InvokeResult updateVoucher(String data2,String data3,String voucherNo,
                                    String yearMonth,String voucherDate,String auxNumber,
                                    String createBy,String oldVoucherNo){
        VoucherDTO dto=new VoucherDTO();
        dto.setVoucherNo(voucherNo);
        dto.setYearMonth(yearMonth);
        dto.setVoucherDate(voucherDate);
		dto.setAuxNumber(auxNumber);
        dto.setCreateBy(createBy);
        dto.setOldVoucherNo(oldVoucherNo);
        List<VoucherDTO> list2;
        List<VoucherDTO> list3;
        try {
            list2 = readJson(data2, List.class, VoucherDTO.class);
            list3 = readJson(data3, List.class, VoucherDTO.class);
            return voucherService.updateVoucher(list2,list3,dto);
        } catch (Exception e) {
            logger.error("凭证编辑异常！",e);
            return InvokeResult.failure("凭证编辑异常！");
        }
    }

	/**
	 * 编辑自动生成凭证信息
	 * @param data2 科目信息
	 * @param voucherNo
	 * @param yearMonth
	 * @param voucherDate
	 * @param auxNumber
	 * @param createBy
	 * @param oldVoucherNo
	 * @return
	 */
	@SysLog(value = "编辑凭证信息")
	@PostMapping("/autoupdateVoucher")
	@ResponseBody
	public InvokeResult updateVoucher2(String data2,String voucherNo,
									  String yearMonth,String voucherDate,String auxNumber,
									  String createBy,String oldVoucherNo){
		VoucherDTO dto=new VoucherDTO();
		dto.setVoucherNo(voucherNo);
		dto.setYearMonth(yearMonth);
		dto.setVoucherDate(voucherDate);
		dto.setAuxNumber(auxNumber);
		dto.setCreateBy(createBy);
		dto.setOldVoucherNo(oldVoucherNo);
		List<VoucherDTO> list2;
		try {
			list2 = readJson(data2, List.class, VoucherDTO.class);
			return voucherService.updateVoucher2(list2, dto);
		} catch (Exception e) {
			if (e.getMessage().startsWith("摘要修改错误")) {
				return InvokeResult.failure("摘要修改错误，原因：只允许在原有的摘要之后追加信息！");
			}
			logger.error("凭证编辑异常！",e);
			return InvokeResult.failure("凭证编辑异常！");
		}
	}

    /**
     * 获取泛型的Collection Type
     * @param jsonStr json字符串
     * @param collectionClass 泛型的Collection
     * @param elementClasses 元素类型
     */
    public static <T> T readJson(String jsonStr, Class<?> collectionClass, Class<?>... elementClasses) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        JavaType javaType = mapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);

        return mapper.readValue(jsonStr, javaType);

    }

}
