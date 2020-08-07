package com.sinosoft.httpclient.service.impl;

import com.sinosoft.httpclient.domain.VehicleStock;
import com.sinosoft.httpclient.repository.VehicleStockRespository;
import com.sinosoft.httpclient.service.VehicleStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class VehicleStockServiceImpl implements VehicleStockService {

    @Resource
    private VehicleStockRespository vehicleStockRespository;

    /**
     * 保存车辆库存变动接口返回数据
     * @param vehicleStockList
     * @return
     */
    @Override
    public String savevehicleStockList(List<VehicleStock> vehicleStockList) {

        vehicleStockRespository.saveAll(vehicleStockList);
        vehicleStockRespository.flush();
        //保存日志

        return "success";
    }
}
