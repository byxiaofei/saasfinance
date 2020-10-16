package com.sinosoft.service.account;

import com.sinosoft.common.InvokeResult;
import com.sinosoft.dto.VoucherDTO;
import com.sinosoft.dto.account.AccMainVoucherDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface VoucherAccountingService {

    List<?> qryVoucherList(VoucherDTO dto);

    InvokeResult accounting(VoucherDTO dto);

    InvokeResult accounting2(VoucherDTO dto);

    InvokeResult revokeAccounting(VoucherDTO dto);

    VoucherDTO qryYearMonth();


}
