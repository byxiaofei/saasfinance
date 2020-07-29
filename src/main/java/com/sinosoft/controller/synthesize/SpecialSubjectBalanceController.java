package com.sinosoft.controller.synthesize;

import com.sinosoft.common.InvokeResult;
import com.sinosoft.dto.VoucherDTO;
import com.sinosoft.service.VoucherService;
import com.sinosoft.service.synthesize.SpecialSubjectBalanceService;
import com.sinosoft.service.synthesize.SpecialSubjectDetailAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping(value = "/specialsubjectbalance")
public class SpecialSubjectBalanceController {
    private Logger logger = LoggerFactory.getLogger(SpecialSubjectBalanceController.class);
    @Resource
    private SpecialSubjectBalanceService specialSubjectBalanceService;
    @Resource
    private SpecialSubjectDetailAccountService specialSubjectDetailAccountService;
    @Resource
    private VoucherService voucherService;
    @RequestMapping("/")
    public String page(){ return "synthesize/specialsubjectbalance"; }

    /**
     * 专项科目余额表查询
     * @param voucherDTO
     * @param cumulativeAmount 显示累计发生额（0-否 1-是）
     * @return
     */
    @RequestMapping("/list")
    @ResponseBody
    public List<?> querySpecialSubjectBalanceList(VoucherDTO voucherDTO, String cumulativeAmount){
        List<?> list = null;
        try {
            list = specialSubjectBalanceService.querySpecialSubjectBalanceList(voucherDTO, cumulativeAmount);
        } catch (Exception e) {
            logger.error("专项科目余额表查询异常",e);
        }
        return list;
    }
    /**
     * 跳转到专项科目余额表查看明细页面
     * @param subjectCode 专项代码
     * @param detailCode 专项代码
     * @param yearMonth 当前会计期间(开始时间)
     * @param request
     * @return
     */
    @RequestMapping("/look")
    public ModelAndView pageAssist(@RequestParam String subjectCode,@RequestParam String centerCode,  @RequestParam String detailCode, @RequestParam String detailName,@RequestParam String yearMonth, @RequestParam String yearMonthDate, @RequestParam String subjectNameP, @RequestParam String specialNameP, @RequestParam String accounRules, @RequestParam String voucherGene, HttpServletRequest request){
        Map<String, Object> map = new HashMap<>();
        map.put("lookSubjectCode", subjectCode);//科目编码
        map.put("lookCenterCode", centerCode);
        map.put("lookDetailCode", detailCode);//专项代码
        map.put("lookDetailName", detailName);//专项名称
        map.put("lookSubjectNameP", subjectNameP);
        map.put("lookSpecialNameP",specialNameP);//专项名称
        map.put("lookYearMonth", yearMonth);//当前会计期间(开始时间)
        map.put("lookYearMonthDate", yearMonthDate);//当前会计期间(结束时间)
        map.put("lookAccounRules", accounRules);
        map.put("lookVoucherGene", voucherGene);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("synthesize/lookspecialsubjectbalance");
        modelAndView.addObject("map",map);
        return modelAndView;
    }



    @RequestMapping("/ishasdata")
    @ResponseBody
    public InvokeResult isHasData(VoucherDTO voucherDTO, String cumulativeAmount){

        String msg = specialSubjectBalanceService.isHasData(voucherDTO,cumulativeAmount);
        if ("NOTEXIST".equals(msg)){
            return InvokeResult.failure("当前条件下，暂无数据可导出！");
        }else {
            return InvokeResult.success();
        }
    }

    @RequestMapping("/export")
    @ResponseBody
    public void exportData(HttpServletRequest request, HttpServletResponse response, VoucherDTO voucherDTO,String accounRules, String cumulativeAmount, String yearMonth, String yearMonthDate){
        specialSubjectBalanceService.exportData(request,response,voucherDTO,accounRules,cumulativeAmount,yearMonth,yearMonthDate);
    }
    /**
     * 跳转到专项科目余额表打印页面
     * @param request
     * @return
     */
    @RequestMapping("/print")
    public ModelAndView pagePrint(VoucherDTO dto, String cumulativeAmount, String centerCode, HttpServletRequest request){

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("synthesize/specialsubjectbalanceprint");
        modelAndView.addObject("map",dto);
        modelAndView.addObject("centerCode",centerCode);
        modelAndView.addObject("cumulativeAmount",cumulativeAmount);
        return modelAndView;
    }
}
