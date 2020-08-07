package com.sinosoft.httpclient.service;

import com.sinosoft.httpclient.domain.VehicleStock;

import java.util.List;

public interface VehicleStockService {

    /**
     * 保存车辆库存变动接口返回数据
     * @param vehicleStockList
     * @return
     */
     String savevehicleStockList(List<VehicleStock> vehicleStockList);
}
