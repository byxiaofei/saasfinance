package com.sinosoft.httpclient.service;

import com.sinosoft.httpclient.domain.VehicleInvoice;

import java.util.List;

public interface VehicleInvoiceService {

    /**
     * 保存所有车辆销售订单结账、退账的数+据
     * @param vehicleInvoiceList
     * @return
     */
    String saveVehicleInvoiceList(List<VehicleInvoice> vehicleInvoiceList);
}
