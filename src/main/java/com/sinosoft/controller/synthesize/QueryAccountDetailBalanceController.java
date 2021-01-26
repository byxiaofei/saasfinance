package com.sinosoft.controller.synthesize;

import com.sinosoft.common.InvokeResult;
import com.sinosoft.dto.VoucherDTO;
import com.sinosoft.service.synthesize.QueryAccountDetailBalanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/queryaccountdetailbalance")
public class QueryAccountDetailBalanceController {
    @Autowired
    private QueryAccountDetailBalanceService queryAccountDetailBalanceService;
    private Logger logger = LoggerFactory.getLogger(QueryAccountDetailBalanceController.class);

    @RequestMapping("/")
    public String page (){ return "synthesize/queryaccountdetailbalance"; }

    /**
     * 科目余额查询
     */
    @RequestMapping(path="/list")
    @ResponseBody
    public List<?> queryAccountDetailBalance(VoucherDTO dto, String cumulativeAmount){
        return queryAccountDetailBalanceService.queryAccountDetailBalance(dto, cumulativeAmount);
    }

    @RequestMapping("/ishasdata")
    @ResponseBody
    public InvokeResult isHasData(VoucherDTO dto, String cumulativeAmount){

        String msg = queryAccountDetailBalanceService.isHasData(dto,cumulativeAmount);
        if ("NOTEXIST".equals(msg)){
            return InvokeResult.failure("当前条件下，暂无数据可导出！");
        }else {
            return InvokeResult.success();
        }
    }

    @RequestMapping("/export")
    @ResponseBody
    public void exportData(HttpServletRequest request, HttpServletResponse response, VoucherDTO dto, String cumulativeAmount, String Date1, String Date2){
        queryAccountDetailBalanceService.exportData(request,response,dto,cumulativeAmount,Date1,Date2);
    }
    /**
     * 跳转到科目余额表打印页面
     * @param request
     * @return
     */
    @RequestMapping("/print")
    public ModelAndView pagePrint(VoucherDTO dto, String cumulativeAmount, String centerCode, HttpServletRequest request){

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("synthesize/queryaccountdetailbalanceprint");
        modelAndView.addObject("map",dto);
        modelAndView.addObject("centerCode",centerCode);
        modelAndView.addObject("cumulativeAmount",cumulativeAmount);
        return modelAndView;
    }
}
