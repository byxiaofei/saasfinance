package com.sinosoft.controller.synthesize;

import com.sinosoft.common.InvokeResult;
import com.sinosoft.dto.VoucherDTO;
import com.sinosoft.service.synthesize.SpecialSubjectDetailAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "/specialsubjectdetailaccount")
public class SpecialSubjectDetailAccountController {
    private Logger logger = LoggerFactory.getLogger(SpecialSubjectDetailAccountController.class);
    @Resource
    private SpecialSubjectDetailAccountService specialSubjectDetailAccountService;

    @RequestMapping("/")
    public String page(){ return "synthesize/specialsubjectdetailaccount"; }

    /**
     * 专项科目明细账查询
     * @param voucherDTO
     * @return
     */
    @RequestMapping("/list")
    @ResponseBody
    public List<?> querySpecialSubjectDetailAccountList(VoucherDTO voucherDTO){
        List<?> list = null;
        try {
            list = specialSubjectDetailAccountService.querySpecialSubjectDetailAccountList(voucherDTO);
        } catch (Exception e) {
            logger.error("专项科目明细账查询异常",e);
        }
        return list;
    }

    /**
     * 科目代码范围内是否存在专项科目校验
     * @param subjectCode
     * @return
     */
    @RequestMapping("/checkSpecialSubject")
    @ResponseBody
    public InvokeResult checkSpecialSubject(String subjectCode){
        try {
            String result = specialSubjectDetailAccountService.checkSpecialSubject(subjectCode);
            if (result!=null&&!"".equals(result)) {
                if ("true".equals(result)) {
                    return InvokeResult.success();
                } else {
                    return  InvokeResult.failure("科目范围内无专项科目！");
                }
            } else {
                return  InvokeResult.failure("请录入科目代码！");
            }
        } catch (Exception e) {
            logger.error("专项科目校验异常", e);
            return InvokeResult.failure("专项科目校验异常！");
        }
    }

    /**
     * 根据专项名称模糊查询专项树，不限专项类别(一级专项)
     * 参数可为空，也支持多查询，即多专项名称(用英文逗号隔开)
     * @param value 可为空，支持多查询(用英文逗号隔开)
     * @return
     */
    @RequestMapping("/specialTree")
    @ResponseBody
    public List<?> querySpecialTree(String value){
        List<?> list = null;
        try {
            list = specialSubjectDetailAccountService.querySpecialTree(value);
        } catch (Exception e) {
            logger.error("专项树查询异常",e);
        }
        return list;
    }

    /**
     * 根据科目名称模糊查询科目树，不限科目类别，如果匹配的是非末级，则非末级下的所有子级全部展示
     * @param value 可为空
     * @return
     */
    @RequestMapping("/subjectTree")
    @ResponseBody
    public List<?> querySubjectTree(String value){
        List<?> list = null;
        try {
            list = specialSubjectDetailAccountService.querySubjectTree(value);
        } catch (Exception e) {
            logger.error("科目树查询异常",e);
        }
        return list;
    }

    @RequestMapping("/ishasdata")
    @ResponseBody
    public InvokeResult isHasData(VoucherDTO voucherDTO){
        String msg = specialSubjectDetailAccountService.isHasData(voucherDTO);
        if ("NOTEXIST".equals(msg)){
            return InvokeResult.failure("当前条件下，暂无数据可导出！");
        }else {
            return InvokeResult.success();
        }
    }

    @RequestMapping("/export")
    @ResponseBody
    public void exportData(HttpServletRequest request, HttpServletResponse response,VoucherDTO voucherDTO, String specialId,String accounRules,String yearMonth,String yearMonthDate){
        specialSubjectDetailAccountService.exportData(request,response,voucherDTO,accounRules,yearMonth,yearMonthDate);
    }
    /**
     * 跳转到专项科目明细账打印页面
     * @param request
     * @return
     */
    @RequestMapping("/print")
    public ModelAndView pagePrint(VoucherDTO dto, String centerCode, HttpServletRequest request){

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("synthesize/specialsubjectdetailaccountprint");
        modelAndView.addObject("map",dto);
        modelAndView.addObject("centerCode",centerCode);
        return modelAndView;
    }
}
