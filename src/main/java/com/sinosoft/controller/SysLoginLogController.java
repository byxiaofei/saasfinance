package com.sinosoft.controller;

import com.sinosoft.common.DataGrid;
import com.sinosoft.common.SysLog;
import com.sinosoft.dto.SysLoginLogDTO;
import com.sinosoft.service.SysLoginLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
@RequestMapping("/sysloginlog")
public class SysLoginLogController {
    private Logger logger = LoggerFactory.getLogger(SysLoginLogController.class);

    @Resource
    private SysLoginLogService sysLoginLogService;

    @RequestMapping("/")
    public String page(){
        return "system/syslog/sysloginlog";
    }

    @RequestMapping(path="/list")
    @ResponseBody
    public DataGrid qrySysOperationLog(@RequestParam int page, @RequestParam int rows, SysLoginLogDTO sysLoginLogDTO){
        Page<SysLoginLogDTO> res = sysLoginLogService.qrySysLoginLog(page, rows, sysLoginLogDTO);
        return new DataGrid(res);
    }
}
