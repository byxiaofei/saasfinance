package com.sinosoft.controller;

import com.sinosoft.common.DataGrid;
import com.sinosoft.common.SysLog;
import com.sinosoft.dto.SysOperationLogDTO;
import com.sinosoft.service.SysOperationLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
@RequestMapping("/sysoperationlog")
public class SysOperationLogController {
    private Logger logger = LoggerFactory.getLogger(SysOperationLogController.class);

    @Resource
    private SysOperationLogService sysOperationLogService;

    @RequestMapping("/")
    public String page(){
        return "system/syslog/sysoperationlog";
    }

    @RequestMapping(path="/list")
    @ResponseBody
    public DataGrid qrySysOperationLog(@RequestParam int page, @RequestParam int rows, SysOperationLogDTO sysOperationLogDTO){
        Page<SysOperationLogDTO> res = sysOperationLogService.qrySysOperationLog(page, rows, sysOperationLogDTO);
        return new DataGrid(res);
    }
}
