package com.sinosoft.service.account;

import com.sinosoft.common.InvokeResult;
import com.sinosoft.dto.VoucherDTO;
import com.sinosoft.dto.account.AccMainVoucherDTO;
import org.springframework.data.domain.Page;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface VoucherApproveService {
    List<?> getApproveListData(VoucherDTO dto);

    InvokeResult reviewPass(VoucherDTO dto);

    InvokeResult reviewBack(VoucherDTO dto);

     void exportByCondition(HttpServletRequest request, HttpServletResponse response, String name,
                                  String queryConditions, String cols);
}
