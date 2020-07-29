package com.sinosoft.service.intangibleassets;
/**
 * @author zhangst
 * @Description
 * @create
 */

import com.sinosoft.common.InvokeResult;
import com.sinosoft.dto.intangibleassets.IntangibleAccAssetInfoChangeDTO;
import org.springframework.data.domain.Page;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 无形资产卡片信息变动查询
 */
public interface IntangibleChangeSelectService {

    Page<?> qrychangeList(int page, int rows, IntangibleAccAssetInfoChangeDTO accAssetInfoDTO);
    InvokeResult  revoke(String changeCodes);
    List<?> intangibleselect(IntangibleAccAssetInfoChangeDTO acc);
    InvokeResult depreciation(IntangibleAccAssetInfoChangeDTO dto);
    void exportByCondition(HttpServletRequest request, HttpServletResponse response, String name,
                           String queryConditions, String cols);

    InvokeResult revokejudge(String changeCodes);
}
