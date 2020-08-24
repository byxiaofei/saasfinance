package com.sinosoft.httpclient.controller;

import com.alibaba.fastjson.JSONArray;
import com.sinosoft.httpclient.domain.VehicleInvoice;
import com.sinosoft.httpclient.domain.VehicleStock;
import com.sinosoft.httpclient.service.HttpClient;
import com.sinosoft.httpclient.service.VehicleInvoiceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/testVehicleInvoice")
public class VehicleInvoiceController {


    private Logger logger = LoggerFactory.getLogger(VehicleInvoiceController.class);

    @Resource
    VehicleInvoiceService vehicleInvoiceService;

    @Resource
    HttpClient httpClient;

    /**
     *
     */
    @RequestMapping(value = "/1")
    public void getVehicleInvoice(){

        try {
            String url = "https://otrplus-cn-test.api.mercedes-benz.com.cn/api/accounting/vehicle-invoice";

            // 添加参数
            Map<String, Long> uriMap = new HashMap<>(6);
            Long startTime = new Date().getTime();   //开始时间需要传参
            Long endTime = new Date().getTime();
            uriMap.put("startTime", Long.parseLong("1595841099999"));
            uriMap.put("endTime", endTime);

            String returnStr = httpClient.sendGet(url, uriMap);
            String str;
            if (returnStr.equals("接口调用失败")) {
                str = "接口调用失败"; // TODO 循环请求或者 其他原因导致请求失败，具体原因分析
            } else {
                List<VehicleInvoice> vehicleInvoices = JSONArray.parseArray(returnStr, VehicleInvoice.class);
                //保存入库
                str = vehicleInvoiceService.saveVehicleInvoiceList(vehicleInvoices);
            }
            System.out.println(str);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("当前异常结果为："+e);
        }

    }


    @RequestMapping(value = "/2")
    public String getVehicleInvoice1(){
        String returnStr = "[\n" +
                "    {\n" +
                "        \"id\": 144041,\n" +
                "        \"dealerNo\": \"GS0036160\",\n" +
                "        \"companyNo\": \"lyytzx01\",\n" +
                "        \"invoiceType\": \"Credit\",\n" +
                "        \"invoiceNo\": \"20200727NC0002\",\n" +
                "        \"originInvoiceNo\": \"20200727NI0004\",\n" +
                "        \"commissionNo\": \"0788657173\",\n" +
                "        \"vin\": \"WDDSJ4DB0JN626327\",\n" +
                "        \"fin\": \"WDD1173431N626327\",\n" +
                "        \"baumuster\": \"1173431\",\n" +
                "        \"nst\": \"CN8\",\n" +
                "        \"brand\": \"MB\",\n" +
                "        \"origin\": \"CBU\",\n" +
                "        \"model\": \"CLA200\",\n" +
                "        \"typeClass\": \"CLA\",\n" +
                "        \"engineNo\": \"27091031551972\",\n" +
                "        \"bookInStatus\": \"BOOKED_IN\",\n" +
                "        \"bookInDate\": \"2018-03-28\",\n" +
                "        \"description\": \"CLA 200 动感型\",\n" +
                "        \"orderId\": \"O200727551546\",\n" +
                "        \"salesType\": \"R\",\n" +
                "        \"customerName\": \"七月十五生产问题\",\n" +
                "        \"companyName\": null,\n" +
                "        \"customerId\": \"1x0Kt8LYTw2mILgVSPvrHA\",\n" +
                "        \"companyId\": null,\n" +
                "        \"vehiclePriceWithoutConsumtionTax\": 2976505.60,\n" +
                "        \"vehiclePrice\": 3265486.73,\n" +
                "        \"consumptionTax\": 288981.13,\n" +
                "        \"vatTax\": 375675.47,\n" +
                "        \"vehicleCost\": 218879.32,\n" +
                "        \"deposit\": 10000.00,\n" +
                "        \"salesLocation\": null,\n" +
                "        \"retailInvoiceDate\": null,\n" +
                "        \"creditDate\": \"2020-07-27\",\n" +
                "        \"operationDate\": \"2020-07-27\"\n" +
                "    }\n" +
                "]";
        List<VehicleInvoice> vehicleInvoices = JSONArray.parseArray(returnStr, VehicleInvoice.class);
        String finalresultMessage = vehicleInvoiceService.saveVehicleInvoiceList(vehicleInvoices);
        System.out.println(finalresultMessage);
        return finalresultMessage;
    }

    public static void main(String[] args) {
        String returnMessage = "[\n" +
                "    {\n" +
                "        \"id\": 144041,\n" +
                "        \"dealerNo\": \"GS0036160\",\n" +
                "        \"companyNo\": \"GS0036160\",\n" +
                "        \"invoiceType\": \"Credit\",\n" +
                "        \"invoiceNo\": \"20200727NC0002\",\n" +
                "        \"originInvoiceNo\": \"20200727NI0004\",\n" +
                "        \"commissionNo\": \"0788657173\",\n" +
                "        \"vin\": \"WDDSJ4DB0JN626327\",\n" +
                "        \"fin\": \"WDD1173431N626327\",\n" +
                "        \"baumuster\": \"1173431\",\n" +
                "        \"nst\": \"CN8\",\n" +
                "        \"brand\": \"MB\",\n" +
                "        \"origin\": \"CBU\",\n" +
                "        \"model\": \"CLA200\",\n" +
                "        \"typeClass\": \"CLA\",\n" +
                "        \"engineNo\": \"27091031551972\",\n" +
                "        \"bookInStatus\": \"BOOKED_IN\",\n" +
                "        \"bookInDate\": \"2018-03-28\",\n" +
                "        \"description\": \"CLA 200 动感型\",\n" +
                "        \"orderId\": \"O200727551546\",\n" +
                "        \"salesType\": \"R\",\n" +
                "        \"customerName\": \"七月十五生产问题\",\n" +
                "        \"companyName\": null,\n" +
                "        \"customerId\": \"1x0Kt8LYTw2mILgVSPvrHA\",\n" +
                "        \"companyId\": null,\n" +
                "        \"vehiclePriceWithoutConsumtionTax\": 2976505.60,\n" +
                "        \"vehiclePrice\": 3265486.73,\n" +
                "        \"consumptionTax\": 288981.13,\n" +
                "        \"vatTax\": 375675.47,\n" +
                "        \"vehicleCost\": 218879.32,\n" +
                "        \"deposit\": 10000.00,\n" +
                "        \"salesLocation\": null,\n" +
                "        \"retailInvoiceDate\": null,\n" +
                "        \"creditDate\": \"2020-07-27\",\n" +
                "        \"operationDate\": \"2020-07-27\"\n" +
                "    }\n" +
                "]";
        List<VehicleInvoice> vehicleInvoiceList = JSONArray.parseArray(returnMessage,VehicleInvoice.class);
        System.out.println(vehicleInvoiceList);
        // 保存入库
//        String message = vehicleInvoiceService.saveVehicleInvoiceList(vehicleInvoiceList);
//        System.out.println(message);
    }

}
