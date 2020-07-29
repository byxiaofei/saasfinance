package com.sinosoft.service.intangibleassets;

import com.sinosoft.common.InvokeResult;
import com.sinosoft.domain.intangibleassets.IntangibleAccAssetInfo;
import com.sinosoft.domain.intangibleassets.IntangibleAccAssetInfoId;
import com.sinosoft.dto.fixedassets.AccAssetInfoDTO;
import com.sinosoft.dto.intangibleassets.IntangibleAccAssetInfoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 无形资产卡片
 */
public interface IntangibleAccAssetInfoService {
    /**
     * 查询全部无形资产
     * @param
     * @param
     * @param
     * @return
     */
    Page<?> qryIntangAssetInfo(int page,int rows, IntangibleAccAssetInfoDTO intangAssetInfo) ;
    List<?> getIntangibleCardList(IntangibleAccAssetInfoDTO intangAssetInfo);
    InvokeResult add(IntangibleAccAssetInfoDTO dto);
    InvokeResult update(IntangibleAccAssetInfoDTO dto);
    List<?> qryAssetType();

    String getNewCardCode();

//    InvokeResult delete(String centerCode, String branchCode, String accBookType, String accBookCode, String codeType, String cardCode);
    InvokeResult delete(List<IntangibleAccAssetInfoId> list);

    InvokeResult stopUse(IntangibleAccAssetInfoDTO dto);

    List<?> qryAssetTypeTree(String value);

    InvokeResult depreciation(IntangibleAccAssetInfoDTO dto);

    String getDepYears(@Param("assetType")String assetType);

    String fCode(String assetType);
    //根据资产类别获取摊销方法
    String deprMethod(String assetType);

    void exportByCondition(HttpServletRequest request, HttpServletResponse response, String name,
                           String queryConditions, String cols);


    InvokeResult checkDate(String useStartDate);

    InvokeResult zjUpdate(String cardCode, String depreFlag);

    InvokeResult copAssetCode(String assetType);

    List<?> getChangeMessage(IntangibleAccAssetInfoDTO acc);

    InvokeResult checkDepreToDate(String depreUtilDate);
    List<?> getAssetCodeList(String intangAssetType);
}
