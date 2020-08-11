package com.sinosoft.httpclient.service.impl;

import com.sinosoft.httpclient.domain.VehicleInvoice;
import com.sinosoft.httpclient.repository.VehicleInvoiceRespository;
import com.sinosoft.httpclient.service.VehicleInvoiceService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class VehicleInvoiceServiceImpl implements VehicleInvoiceService {

    @Resource
    private VehicleInvoiceRespository vehicleInvoiceRespository;

    @Override
    public String saveVehicleInvoiceList(List<VehicleInvoice> vehicleInvoiceList) {
        vehicleInvoiceRespository.saveAll(vehicleInvoiceList);
        vehicleInvoiceRespository.flush();
        return "success";
    }
}
