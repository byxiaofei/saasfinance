package com.sinosoft.service.account;

import com.sinosoft.common.InvokeResult;
import com.sinosoft.dto.account.AccMonthTraceDTO;

import java.util.List;

/**
 * @author jiangyc
 * @Description
 * @create 2019-03-12 10:24
 */
public interface FinalAccountingService {
    List<?> getFinalAccountingListData(AccMonthTraceDTO dto);

    InvokeResult finalAccounting(AccMonthTraceDTO dto);

    InvokeResult unFinalAccounting(AccMonthTraceDTO dto);
}
