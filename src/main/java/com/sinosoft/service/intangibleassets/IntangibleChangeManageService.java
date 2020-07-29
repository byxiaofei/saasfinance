package com.sinosoft.service.intangibleassets;
/**
 * @author zhangst
 * @Description
 * @create
 */

import com.sinosoft.dto.fixedassets.AccAssetInfoDTO;
import com.sinosoft.dto.intangibleassets.IntangibleAccAssetInfoDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 固定资产卡片信息变动管理
 */
public interface IntangibleChangeManageService {

   // List<?> qrychangeList(AccAssetInfoDTO accAssetInfoDTO);
    String  useflagChange(IntangibleAccAssetInfoDTO accAssetInfoDTO);
    String  useChange(IntangibleAccAssetInfoDTO accAssetInfoDTO);

    String  cleanCard(IntangibleAccAssetInfoDTO inTangibleAcc);
    String  typeChange(IntangibleAccAssetInfoDTO accAssetInfoDTO);
    List<?> getAssetType(IntangibleAccAssetInfoDTO accAssetInfoDTO);
    String getDepreYear(IntangibleAccAssetInfoDTO accAssetInfoDTO);
    void exportByCondition(HttpServletRequest request, HttpServletResponse response, String name,
                           String queryConditions, String cols);
}
