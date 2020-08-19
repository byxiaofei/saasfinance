package com.sinosoft.httpclient.service;

import com.sinosoft.dto.VoucherDTO;
import com.sinosoft.httpclient.domain.VehicleInvoice;

import java.util.Map;

public interface AccountingMethod {
    /**
     * 机构、制单日期校验、凭证头部信息生成
     * @param companyNo
     * @param PreparationDate
     * @return
     */
     Map InformationVerification(String companyNo, String PreparationDate, StringBuilder errorMsg);

    /**
     * 凭证分录信息生成
     * @param map
     * @param interfaceInfo
     * @param interfaceType
     * @param errorMsg
     * @param accbookCode
     * @return
     */
     Map<String,Object> AccountingEntryInformation(Map<String,Object>map, String interfaceInfo, String interfaceType, StringBuilder errorMsg, String accbookCode );


}
