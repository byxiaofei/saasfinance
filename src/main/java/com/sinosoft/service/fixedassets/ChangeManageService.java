package com.sinosoft.service.fixedassets;
/**
 * @author zhangst
 * @Description
 * @create
 */

import com.sinosoft.dto.fixedassets.AccAssetInfoDTO;
import org.springframework.data.domain.Page;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 固定资产卡片信息变动管理
 */
public interface ChangeManageService {

    Page<?> qrychangeList(int page,int rows,AccAssetInfoDTO accAssetInfoDTO);
    String  depChange(AccAssetInfoDTO accAssetInfoDTO);
    String typeChange(AccAssetInfoDTO accAssetInfoDTO);
    List<?> AssetType(AccAssetInfoDTO accAssetInfoDTO);
    String  cleanCard(AccAssetInfoDTO acc);
    String  useChange(AccAssetInfoDTO acc);
    List<?> getChangeManageList(AccAssetInfoDTO acc);
    void exportByCondition(HttpServletRequest request, HttpServletResponse response, String name,
                           String queryConditions, String cols);
}
