package com.sinosoft.service.account;

import com.sinosoft.dto.VoucherDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface VoucherPrintService {
    /**
     * 凭证打印查询（按打印样式处理）
     * @param dto
     * @return
     */
    List<?> queryVoucherPrintList(VoucherDTO dto);

    /**
     * 单个凭证导出
     * @param request
     * @param response
     * @param voucherDTO
     */
    void  voucherExport(HttpServletRequest request, HttpServletResponse response,VoucherDTO voucherDTO);
}
