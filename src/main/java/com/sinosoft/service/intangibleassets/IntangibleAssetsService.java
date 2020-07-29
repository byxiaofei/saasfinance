package com.sinosoft.service.intangibleassets;

/**
 * @author jiangyc
 * @Description
 * @create 2019-03-26 17:10
 */

import com.sinosoft.common.InvokeResult;
import com.sinosoft.dto.intangibleassets.IntangibleAccAssetCodeTypeDTO;
import org.springframework.data.domain.Page;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 无形资产类别编码Service
 */
public interface IntangibleAssetsService {

    Page<?> qryIntangibleAccAssetCodeTypeList(int page,int rows,IntangibleAccAssetCodeTypeDTO dto);
    List<?> getAssetsList(IntangibleAccAssetCodeTypeDTO dto);
    InvokeResult add(IntangibleAccAssetCodeTypeDTO dto);

    InvokeResult addLowerLevel(IntangibleAccAssetCodeTypeDTO dto);

    InvokeResult update(IntangibleAccAssetCodeTypeDTO dto);

    String getSpecialId(String id);
    InvokeResult judgesubject(String itemCodes,String articleCodes);
    InvokeResult delete( String accBookType, String accBookCode, String codeType, String assetType);

    InvokeResult isUse(IntangibleAccAssetCodeTypeDTO dto);
    String lowerlevel(IntangibleAccAssetCodeTypeDTO dto);

    void exportByCondition(HttpServletRequest request, HttpServletResponse response, String name,
                           String queryConditions, String cols);
}
