package com.sinosoft.service.fixedassets;

/**
 * @author jiangyc
 * @Description
 * @create 2019-03-29 15:04
 */

import com.sinosoft.common.InvokeResult;
import com.sinosoft.domain.fixedassets.AccAssetInfoId;
import com.sinosoft.dto.fixedassets.AccAssetInfoDTO;
import com.sinosoft.dto.fixedassets.FixedAssetsCardDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 固定资产卡片管理Service
 */
public interface FixedassetsCardService {

    InvokeResult add(AccAssetInfoDTO dto);
    InvokeResult update(AccAssetInfoDTO dto);
    List<?> qryAssetType();
    List<?> getAccAssetInfo(AccAssetInfoDTO acc);

    List<?> qryuUnitCode();

    String getDepYears(String assetType);

    String getDepType(String assetType);

    String getNewCardCode();
    Page<?> qryAccAssetInfo(int page,int rows,AccAssetInfoDTO accAssetInfoDTO);
//    InvokeResult delete(AccAssetInfoId fix);
    InvokeResult delete(List<AccAssetInfoId> list);
    InvokeResult stopUse(AccAssetInfoDTO dto);

    List<?> qryAssetTypeTree(String value);

    InvokeResult depreciation(AccAssetInfoDTO dto);

    String fCode(String assetType);
    //固定资产折旧方法获取
    String deprMethod(String assetType);
    void exportByCondition(HttpServletRequest request, HttpServletResponse response, String name,
                           String queryConditions, String cols);

    String getNetSurplusRate(String assetType,String codeType);


    InvokeResult checkDate(String useStartDate);

    InvokeResult zjUpdate(String cardCode, String depreFlag);

    InvokeResult copAssetCode(String assetType);

    List<?> getChangeMessage(AccAssetInfoDTO acc);

    InvokeResult checkDepreToDate(String depreUtilDate);

    List<?> getFixedAssetCodeList(String fixedAssetType);

    List<?> qryBankPayMethodTree(String value);

    /**
     *
     * 功能描述: 加载专项中维护的BM类专项（只展示使用状态下的数据）
     *
     */
    List<?> qryUnitCodeByUseFlagOne();
}
