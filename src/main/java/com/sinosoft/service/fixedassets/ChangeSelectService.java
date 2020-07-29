package com.sinosoft.service.fixedassets;
/**
 * @author zhangst
 * @Description
 * @create
 */

import com.sinosoft.common.InvokeResult;
import com.sinosoft.domain.fixedassets.AccAssetInfoChange;
import com.sinosoft.dto.fixedassets.AccAssetInfoChangeDTO;
import com.sinosoft.dto.fixedassets.AccAssetInfoDTO;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 固定资产卡片信息变动查询
 */
public interface ChangeSelectService {

    Page<?> qrychangeList(int page,int rows,AccAssetInfoChangeDTO accAssetInfoDTO);
    InvokeResult  revoke(String changeCodes);
    List<?>  cardselect(AccAssetInfoDTO accAssetInfoDTO);
 /*   String isDepre(AccAssetInfoChange accAssetInfoChange);*/
    /*  String typeChange(AccAssetInfoDTO accAssetInfoDTO);
    List<?> AssetType(AccAssetInfoDTO accAssetInfoDTO);*/
    InvokeResult Isdepreciation(AccAssetInfoChangeDTO dto);
    InvokeResult revokejudge(String changeCodes);
}
