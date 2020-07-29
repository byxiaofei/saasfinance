package com.sinosoft.service.synthesize;

import com.sinosoft.dto.VoucherDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface QueryAccountDetailBalanceService {
    public List<?> queryAccountDetailBalance(VoucherDTO dto, String cumulativeAmount);
    String isHasData(VoucherDTO dto, String cumulativeAmount);
    void exportData(HttpServletRequest request, HttpServletResponse response, VoucherDTO dto, String cumulativeAmount, String Date1, String Date2);
}
