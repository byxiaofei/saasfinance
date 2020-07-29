package com.sinosoft.controller.fixedassets;

import com.sinosoft.common.InvokeResult;
import com.sinosoft.service.fixedassets.FixedassetsReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author jiangyc
 * @Description
 * @create 2019-04-19 17:17
 */
@Controller
@RequestMapping("/fixedassetsreport")
public class FixedassetsReportController {
    private Logger logger = LoggerFactory.getLogger(FixedassetsReportController.class);

    @Resource
    private FixedassetsReportService fixedassetsReportService;

    @RequestMapping("/")
    private String page(){
        return "fixedassets/fixedassetsreport";
    }

    /**
     * 固定资产报表查询
     * @param yearMonthDate
     * @return
     */
    @RequestMapping(path = "/list")
    @ResponseBody
    public List<?> qryReportInfo(String yearMonthDate, String startLevel, String endLevel){
        return fixedassetsReportService.qryReportInfo(yearMonthDate, startLevel, endLevel);
    }

    /**
     * 会计期间获取
     * @return
     */
    @RequestMapping(path = "/yearMonthDate")
    @ResponseBody
    public List<?> getYearMonthDate(){
        return fixedassetsReportService.getYearMonthDate();
    }

    /**
     * 起始层级获取
     * @return
     */
    @RequestMapping(path = "/startLevel")
    @ResponseBody
    public List<?> getStartLevel(){
        return fixedassetsReportService.getStartLevel();
    }

    /**
     * 终止层级获取
     * @return
     */
    @RequestMapping(path = "/endLevel")
    @ResponseBody
    public List<?> geteENndLevel(){
        return fixedassetsReportService.geteEndLevel();
    }

}
