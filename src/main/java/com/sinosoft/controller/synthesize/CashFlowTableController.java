package com.sinosoft.controller.synthesize;

import com.sinosoft.common.InvokeResult;
import com.sinosoft.dto.VoucherDTO;
import com.sinosoft.service.synthesize.CashFlowTableService;
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

@Controller
@RequestMapping("/cashflowtable")
public class CashFlowTableController {
    private Logger logger = LoggerFactory.getLogger(CashFlowTableController.class);

    @Resource
    private CashFlowTableService cashFlowTableService;

    @RequestMapping("/")
    public String page (){ return "synthesize/cashflowtable"; }

    /**
     * 现金流量明细表查询
     * @param dto
     * @return
     */
    @RequestMapping(path="/list")
    @ResponseBody
    public List<?> queryCashFlowTable(VoucherDTO dto){
        return cashFlowTableService.queryCashFlowTable(dto);
    }

    @RequestMapping(path = "/ishasdata")
    @ResponseBody
    public InvokeResult IsHasData(VoucherDTO dto){

        String msg = cashFlowTableService.isHasData(dto);
        if("NOTEXIST".equals(msg)){
            return InvokeResult.failure("暂无数据可导出");
        }
       return InvokeResult.success();
    }
    /**
     * 跳转到现金流量明细表打印页面
     * @param request
     * @return
     */
    @RequestMapping("/print")
    public ModelAndView pagePrint(@RequestParam String yearMonth, @RequestParam String yearMonthDate, @RequestParam String voucherDateStart, @RequestParam String voucherDateEnd, @RequestParam String voucherGene,@RequestParam String centerCode, HttpServletRequest request){
        Map<String, Object> map = new HashMap<>();
        map.put("yearMonth", yearMonth);//会计期间开始时间
        map.put("yearMonthDate", yearMonthDate);//
        map.put("voucherDateStart", voucherDateStart);//凭证日期
        map.put("voucherDateEnd", voucherDateEnd);//
        map.put("centerCode", centerCode);//
        map.put("voucherGene", voucherGene);//包含未记账凭证(0:否，1:是；可为空)
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("synthesize/cashflowtableprint");
        modelAndView.addObject("map",map);
        return modelAndView;
    }
    @RequestMapping(path = "/export")
    public void exportData(HttpServletRequest request, HttpServletResponse response,VoucherDTO dto,String Date1,String Date2){
        cashFlowTableService.exportData(request,response,dto,Date1,Date2);
    }
}
