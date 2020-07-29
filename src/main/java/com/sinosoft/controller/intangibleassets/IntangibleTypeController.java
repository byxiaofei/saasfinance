package com.sinosoft.controller.intangibleassets;

import com.sinosoft.service.intangibleassets.IntangibleAssetsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/intangibleassets")
public class IntangibleTypeController {
    @RequestMapping("/")
    public String page(){ return "intangibleassets/intangibleassets"; }

    @Resource
    private IntangibleAssetsService intangibleAssetsService;

    /**
     * 导出无形资产编码类别信息表
     */
    @RequestMapping(path="/intangibleassetsdownload")
    public void export(HttpServletRequest request, HttpServletResponse response, String name, String queryConditions, String cols){
        intangibleAssetsService.exportByCondition(request, response, name, queryConditions, cols);
    }
}
