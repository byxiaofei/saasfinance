package com.sinosoft.service;

import com.sinosoft.dto.SysOperationLogDTO;
import org.springframework.data.domain.Page;

public interface SysOperationLogService {
    /**
     * 根据查询条件查询系统操作日志信息
     * @param page
     * @param rows
     * @param sysOperationLogDTO
     * @return
     */
    public Page<SysOperationLogDTO> qrySysOperationLog(int page, int rows, SysOperationLogDTO sysOperationLogDTO);
}
