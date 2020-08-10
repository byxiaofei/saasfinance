package com.sinosoft.httpclient.controller;

import com.alibaba.fastjson.JSONArray;
import com.sinosoft.httpclient.domain.VehicleInvoice;
import com.sinosoft.httpclient.domain.VehicleStock;
import com.sinosoft.httpclient.service.HttpClient;
import com.sinosoft.httpclient.service.VehicleInvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/testVehicleInvoice")
public class VehicleInvoiceController {

    @Autowired
    VehicleInvoiceService vehicleInvoiceService;

    @Autowired
    HttpClient httpClient;

    /**
     *
     */
    @RequestMapping(value = "/1")
    public void getVehicleInvoice(){

        String url = "https://otrplus-cn-test.api.mercedes-benz.com.cn/api/accounting/vehicle-invoice";

        // 添加参数
        Map<String, Long> uriMap = new HashMap<>(6);
        Long startTime = new Date().getTime();   //开始时间需要传参
        Long endTime = new Date().getTime();
        uriMap.put("startTime",Long.parseLong("1596001003220"));
        uriMap.put("endTime", endTime);

        String returnStr = httpClient.sendGet(url,uriMap);
        String str  ;
        if(returnStr.equals("接口调用失败")){
            str = "接口调用失败"; // TODO 循环请求或者 其他原因导致请求失败，具体原因分析
        }else{
            List<VehicleInvoice> vehicleInvoices = JSONArray.parseArray(returnStr, VehicleInvoice.class);
            //保存入库
            str =  vehicleInvoiceService.saveVehicleInvoiceList(vehicleInvoices);
        }


        System.out.println(str);

    }

    public static void main(String[] args) {
        String returnMessage = "[ " +
                                    "{ " +
                                        "\"dealerNo\": \"GS0031211\", " +
                                        "\"companyNo\": \"GS0031211\", " +
                                        "\"invoiceType\": \"Invoice\", " +
                                        "\"commissionNo\": \"12345\", " +
                                        "\"vin\": \"vin1\", " +
                                        "\"fin\": \"fin1\", " +
                                        "\"baumuster\": \"2052431\"," +
                                        "\"nst\": \"nst12345\", " +
                                        "\"brand\": \"MB\", " +
                                        "\"origin\": \"origin12345\", " +
                                        "\"model\": \"model12345\", " +
                                        "\"engineNo\": \"engine12345\", " +
                                        "\"bookInStatus\": \"BOOKED_IN\", " +
                                        "\"bookInDate\": \"2019-09-01\", " +
                                        "\"description\": \"梅赛德斯-奔驰\", " +
                                        "\"orderId\": \"orderId09876\", " +
                                        "\"salesType\": \"E\", " +
                                        "\"customerName\": \"张三\", " +
                                        "\"companyName\": \"company12345\", " +
                                        "\"vehiclePriceWithoutConsumtionTax\": 88888, " +
                                        "\"vehiclePrice\": 99999, " +
                                        "\"consumptionTax\": 11111, " +
                                        "\"vatTax\": 2, " +
                                        "\"vehicleCost\": 200, " +
                                        "\"deposit\": 2, " +
                                        "\"salesLocation\": \"China\", " +
                                        "\"retailInvoiceDate\": \"2019-10-02\", " +
                                        "\"operationDate\": \"2019-11-05\" " +
                                    "} " +
                                "]";
        List<VehicleInvoice> vehicleInvoiceList = JSONArray.parseArray(returnMessage,VehicleInvoice.class);
        System.out.println(vehicleInvoiceList);
        // 保存入库
//        String message = vehicleInvoiceService.saveVehicleInvoiceList(vehicleInvoiceList);
//        System.out.println(message);
    }

}
