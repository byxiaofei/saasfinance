package com.sinosoft.httpclient.service;

import com.sinosoft.httpclient.domain.JsonToPartsInvoice;

import java.util.List;

public interface PartsInvoiceService {
    /**
     * 保存所有零件结账及退账的数据
     * @param jsonToPartsInvoices
     * @return
     */
    String savePartsInvoiceList(List<JsonToPartsInvoice> jsonToPartsInvoices);
}
