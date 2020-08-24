package com.sinosoft.httpclient.service;

import com.sinosoft.httpclient.dto.PartsVerificationDTO;
import com.sinosoft.httpclient.dto.ServiceInvoiceDTO;

import java.util.List;

/**
 *所有售后结账、退账的相关数据
 */
public interface ServiceInvoiceService {
    /**
     *获取业务数据信息
     */
    String getServiceInvoiceService(List<ServiceInvoiceDTO> ServiceInvoiceDTOList,String loadTime);


}
