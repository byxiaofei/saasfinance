package com.sinosoft.service.intangibleassets;

import com.sinosoft.common.InvokeResult;
import com.sinosoft.dto.fixedassets.AccAssetInfoDTO;
import com.sinosoft.dto.intangibleassets.IntangibleAccAssetInfoDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 无形资产卡片凭证管理Service
 */
public interface IntangibleCardVoucherService {

    InvokeResult createVoucher(IntangibleAccAssetInfoDTO dto) throws Exception;

    InvokeResult revokeVoucher(IntangibleAccAssetInfoDTO dto);

    void exportByCondition(HttpServletRequest request, HttpServletResponse response, String name,
                           String queryConditions, String cols);
}
