package com.sinosoft.service;

import com.sinosoft.dto.SysLoginLogDTO;
import org.springframework.data.domain.Page;

public interface SysLoginLogService {
    /**
     * 根据查询条件查询系统登录日志信息
     * @param page
     * @param rows
     * @param sysLoginLogDTO
     * @return
     */
    public Page<SysLoginLogDTO> qrySysLoginLog(int page, int rows, SysLoginLogDTO sysLoginLogDTO);
}
