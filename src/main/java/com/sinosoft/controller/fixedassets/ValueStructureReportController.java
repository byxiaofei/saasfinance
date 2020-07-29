package com.sinosoft.controller.fixedassets;

import com.sinosoft.service.fixedassets.FixedassetsReportService;
import com.sinosoft.service.fixedassets.ValueStructureReportService;
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
@RequestMapping("/valuestructurereport")
public class ValueStructureReportController {
    private Logger logger = LoggerFactory.getLogger(ValueStructureReportController.class);

    @Resource
    private ValueStructureReportService valueStructureReportService;

    @RequestMapping("/")
    private String page(){
        return "fixedassets/valuestructurereport";
    }

    /**
     * 固定资产报表查询
     * @param yearMonthDate
     * @return
     */
    @RequestMapping(path = "/list")
    @ResponseBody
    public List<?> qryReportInfo(String yearMonthDate, String startLevel, String endLevel){
        return valueStructureReportService.qryReportInfo(yearMonthDate, startLevel, endLevel);
    }

    /**
     * 会计期间获取
     * @return
     */
    @RequestMapping(path = "/yearMonthDate")
    @ResponseBody
    public List<?> getYearMonthDate(){
        return valueStructureReportService.getYearMonthDate();
    }

    /**
     * 起始层级获取
     * @return
     */
    @RequestMapping(path = "/startLevel")
    @ResponseBody
    public List<?> getStartLevel(){
        return valueStructureReportService.getStartLevel();
    }

    /**
     * 终止层级获取
     * @return
     */
    @RequestMapping(path = "/endLevel")
    @ResponseBody
    public List<?> geteENndLevel(){
        return valueStructureReportService.geteEndLevel();
    }

}
