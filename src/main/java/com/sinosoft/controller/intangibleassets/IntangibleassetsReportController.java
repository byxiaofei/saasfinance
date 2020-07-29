package com.sinosoft.controller.intangibleassets;

import com.sinosoft.controller.fixedassets.FixedassetsReportController;
import com.sinosoft.service.intangibleassets.IntangibleassetsReportService;
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
 * @create 2019-04-22 19:07
 */
@Controller
@RequestMapping("/intangibleassetsreport")
public class IntangibleassetsReportController {
    private Logger logger = LoggerFactory.getLogger(IntangibleassetsReportController.class);

    @Resource
    private IntangibleassetsReportService intangibleassetsReportService;

    @RequestMapping("/")
    private String page(){
        return "intangibleassets/intangibleassetsreport";
    }

    /**
     * 无形资产报表查询
     * @param yearMonthDate
     * @return
     */
    @RequestMapping(path = "/list")
    @ResponseBody
    public List<?> qryReportInfo(String yearMonthDate, String startLevel, String endLevel){
        return intangibleassetsReportService.qryReportInfo(yearMonthDate, startLevel, endLevel);
    }

    /**
     * 会计期间获取
     * @return
     */
    @RequestMapping(path = "/yearMonthDate")
    @ResponseBody
    public List<?> getYearMonthDate(){
        return intangibleassetsReportService.getYearMonthDate();
    }

    /**
     * 起始层级获取
     * @return
     */
    @RequestMapping(path = "/startLevel")
    @ResponseBody
    public List<?> getStartLevel(){
        return intangibleassetsReportService.getStartLevel();
    }

    /**
     * 终止层级获取
     * @return
     */
    @RequestMapping(path = "/endLevel")
    @ResponseBody
    public List<?> geteENndLevel(){
        return intangibleassetsReportService.geteEndLevel();
    }
}
