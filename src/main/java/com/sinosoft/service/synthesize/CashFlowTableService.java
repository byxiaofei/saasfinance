package com.sinosoft.service.synthesize;

import com.sinosoft.dto.VoucherDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface CashFlowTableService {
    /**
     * 现金流量明细表查询
     * @param dto
     * @return
     */
    public List<?> queryCashFlowTable(VoucherDTO dto);

    public String isHasData(VoucherDTO dto);
    public void exportData(HttpServletRequest request, HttpServletResponse response, VoucherDTO dto, String Date1, String Date2);
}
