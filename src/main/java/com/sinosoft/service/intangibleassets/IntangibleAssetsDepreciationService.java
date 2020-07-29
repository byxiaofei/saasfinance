package com.sinosoft.service.intangibleassets;

import com.sinosoft.common.InvokeResult;
import com.sinosoft.dto.fixedassets.AccGCheckInfoDTO;
import com.sinosoft.dto.intangibleassets.AccWCheckInfoDTO;

import java.util.List;

/**
 * @author jiangyc
 * @Description
 * @create 2019-04-09 19:27
 */
public interface IntangibleAssetsDepreciationService {

    List<?> qryAccWCheckInfo(AccWCheckInfoDTO dto);

    InvokeResult depreciation(AccWCheckInfoDTO dto);

    InvokeResult revokeDepreciation(AccWCheckInfoDTO dto);

    InvokeResult addVoucher(AccWCheckInfoDTO dto) throws Exception;

    InvokeResult revokeVoucher(AccWCheckInfoDTO dto);
}
