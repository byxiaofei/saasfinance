package com.sinosoft.service.synthesize;

import com.sinosoft.dto.VoucherDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface SpecialSubjectBalanceService {
    /**
     * 专项科目余额表查询
     * @param dto
     * @param cumulativeAmount 显示累计发生额（0-否 1-是）
     * @return
     */
    List<?> querySpecialSubjectBalanceList(VoucherDTO dto, String cumulativeAmount);
    String isHasData(VoucherDTO voucherDTO, String cumulativeAmount);
    void exportData(HttpServletRequest request, HttpServletResponse response, VoucherDTO voucherDTO ,String accounRules,String cumulativeAmount, String yearMonth, String yearMonthDate);

}
