package com.sinosoft.service.fixedassets;

/**
 * @author jiangyc
 * @Description
 * @create 2019-03-29 15:04
 */

import com.sinosoft.common.InvokeResult;
import com.sinosoft.dto.fixedassets.AccAssetInfoDTO;
import org.springframework.data.domain.Page;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 固定资产卡片管理Service
 */
public interface FixedassetsCardSelectService {

   // InvokeResult add(AccAssetInfoDTO dto);

   // List<?> qryAssetType();

   // List<?> qryuUnitCode();

  //  String getDepYears(String assetType);

 //   String getDepType(String assetType);

   // String getNewCardCode();
    Page<?> qryAccAssetInfo(int page,int rows,AccAssetInfoDTO accAssetInfoDTO);
    List<?> getAssetsCardSelect(AccAssetInfoDTO acc);
  //  InvokeResult delete(AccAssetInfoId fix);

   // InvokeResult stopUse(AccAssetInfoDTO dto);

   // List<?> qryAssetTypeTree(String value);

     InvokeResult depreciation(AccAssetInfoDTO dto);
     List<?> getAssetDDepre(AccAssetInfoDTO acc);
     List<?>  getAssetDepreMessage(AccAssetInfoDTO accAssetInfoDTO);
     List<?> getAssetDepreSelect(String centerCode, String branchCode, String levelstart, String levelend, String yearMonthData);
  //  String fCode(String assetType);
     List<?> getAssetVoucherSelect(String date1, String date2);
    List<?> getAssetDepreMessageprint(AccAssetInfoDTO accAssetInfoDTO );
   Integer getAssetDepreMessagePage(AccAssetInfoDTO accAssetInfoDTO);
    void exportByCondition(HttpServletRequest request, HttpServletResponse response, String name,
                           String queryConditions, String cols);

}
