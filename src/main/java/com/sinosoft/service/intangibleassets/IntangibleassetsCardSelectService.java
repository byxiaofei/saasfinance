package com.sinosoft.service.intangibleassets;

/**
 * @author jiangyc
 * @Description
 * @create 2019-03-29 15:04
 */

import com.sinosoft.common.InvokeResult;
import com.sinosoft.dto.fixedassets.AccAssetInfoDTO;
import com.sinosoft.dto.intangibleassets.IntangibleAccAssetInfoDTO;

import java.util.List;

/**
 * 无形资产卡片管理Service
 */
public interface IntangibleassetsCardSelectService {



   // List<?> qryAccAssetInfo(AccAssetInfoDTO accAssetInfoDTO);

   //  InvokeResult depreciation(AccAssetInfoDTO dto);
     List<?> getAssetDDepre(IntangibleAccAssetInfoDTO acc);
  //   List<?>  getAssetDepreMessage(String centerCode, String branchCode, String accBookType, String accBookCode, String codeType, String assetType);
   //  List<?> getAssetDepreSelect(String centerCode, String branchCode, String levelstart, String levelend, String yearMonthData);
  //  String fCode(String assetType);
 //List<?> getAssetVoucherSelect(String date1, String date2);
}
