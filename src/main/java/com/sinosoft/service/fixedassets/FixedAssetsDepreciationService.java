package com.sinosoft.service.fixedassets;

import com.sinosoft.common.InvokeResult;
import com.sinosoft.dto.fixedassets.AccGCheckInfoDTO;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author jiangyc
 * @Description
 * @create 2019-04-09 18:18
 */
public interface FixedAssetsDepreciationService {

    List<?> qryAccGCheckInfo(AccGCheckInfoDTO dto);

    InvokeResult depreciation(AccGCheckInfoDTO dto);

    InvokeResult revokeDepreciation(AccGCheckInfoDTO dto);

    InvokeResult addVoucher(AccGCheckInfoDTO dto) throws Exception;

    InvokeResult revokeVoucher(AccGCheckInfoDTO dto);
}
