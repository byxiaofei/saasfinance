package com.sinosoft.service.fixedassets;

/**
 * @author jiangyc
 * @Description
 * @create 2019-03-29 15:04
 */

import com.sinosoft.common.InvokeResult;
import com.sinosoft.domain.fixedassets.AccAssetInfoId;
import com.sinosoft.dto.fixedassets.AccAssetInfoDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 固定资产卡片凭证管理Service
 */
public interface FixedassetsCardVoucherService {

    InvokeResult createVoucher(AccAssetInfoDTO dto) throws  Exception;

    InvokeResult revokeVoucher(AccAssetInfoDTO dto);

    void exportByCondition(HttpServletRequest request, HttpServletResponse response, String name,
                           String queryConditions, String cols);
    /**
     * 获取指定年月的最后一天
     * @param year
     * @param month
     * @return 日期字符串（yyyy-MM-dd）
     */
    String getLastDayOfYearMonth(int year, int month);
}
