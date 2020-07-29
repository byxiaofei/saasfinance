package com.sinosoft.controller.synthesize;

import com.sinosoft.common.InvokeResult;
import com.sinosoft.dto.VoucherDTO;
import com.sinosoft.service.synthesize.QueryAccsumService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping(value = "/queryaccsum")
public class QueryAccsumController {
    private Logger logger = LoggerFactory.getLogger(QueryAccsumController.class);
    @Resource
    private QueryAccsumService queryAccsumService;

    @RequestMapping("/")
    public String page(){ return "synthesize/queryaccsum"; }

    /**
     * 科目总账查询
     * @param voucherDTO
     * @return
     */
    @RequestMapping("/list")
    @ResponseBody
    public List<?> queryAccsum(VoucherDTO voucherDTO){
        List<?> list = null;
        try {
            long time = System.currentTimeMillis();
            list = queryAccsumService.queryAccsum(voucherDTO);
            System.out.println("科目总账查询用时："+(System.currentTimeMillis()-time)+" ms");
        } catch (Exception e) {
            logger.error("科目总账查询异常", e);
        }
        return list;
    }

    @RequestMapping(path = "/ishasdata")
    @ResponseBody
    public InvokeResult isHasData(VoucherDTO dto){

        String msg = queryAccsumService.isHasData(dto);
        if ("NOTEXIST".equals(msg)){
            return InvokeResult.failure("暂无数据可导出！");
        }else {
            return InvokeResult.success();
        }

    }

    @RequestMapping("/export")
    @ResponseBody
    public void exportData(HttpServletRequest request, HttpServletResponse response,String name,String queryConditions){
        queryAccsumService.download(request,response,name,queryConditions);
    }
    /**
     * 跳转到科目总账查询打印页面
     * @param request
     * @return
     */
    @RequestMapping("/print")
    public ModelAndView pagePrint(VoucherDTO dto, String centerCode, HttpServletRequest request){

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("synthesize/queryaccsumprint");
        modelAndView.addObject("map",dto);
        modelAndView.addObject("centerCode",centerCode);
        return modelAndView;
    }
}
