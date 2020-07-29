package com.sinosoft.service.fixedassets;

/**
 * @author jiangyc
 * @Description
 * @create 2019-03-26 17:10
 */

import com.sinosoft.common.InvokeResult;
import com.sinosoft.dto.fixedassets.AccAssetCodeTypeDTO;
import org.springframework.data.domain.Page;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 固定资产类别编码Service
 */
public interface CategoryCodingService {

    Page<?> qryAccAssetCodeTypeList(int page,int rows,AccAssetCodeTypeDTO dto);
    List<?> getAccAssetCodeTypeList(AccAssetCodeTypeDTO dto);
    InvokeResult add(AccAssetCodeTypeDTO dto);

    InvokeResult addLowerLevel(AccAssetCodeTypeDTO dto);

    InvokeResult update(AccAssetCodeTypeDTO dto);

    String getSpecialId(String id);
    InvokeResult judgesubject(String itemCodes,String articleCodes);
    InvokeResult delete(String centerCode, String branchCode, String accBookType, String accBookCode, String codeType, String assetType);

    InvokeResult isUse(AccAssetCodeTypeDTO dto);

    void exportByCondition(HttpServletRequest request, HttpServletResponse response, String name,
                                  String queryConditions, String cols);
    String lowerlevel(AccAssetCodeTypeDTO dto);
    Page<?> getPage(int page,int rows,List<?> list);
}
