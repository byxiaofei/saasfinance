package com.sinosoft.httpclient.controller;

import com.alibaba.fastjson.JSONArray;
import com.sinosoft.httpclient.domain.VehicleStock;
import com.sinosoft.httpclient.service.HttpClient;
import com.sinosoft.httpclient.service.VehicleStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/testVehicleStock")
public class VehicleStockController {
    @Autowired
    private HttpClient httpClient ;

    @Autowired
    private VehicleStockService vehicleStockService;

    /**
     * VehicleStock 接口接受解析报文
     */
    @RequestMapping(value = "/1")
    public void getVehicleStock(){
        String url = "https://api.weixin.qq.com/cgi-bin/token";
        //添加参数
        Map<String, Long> uriMap = new HashMap<>(6);
        long startTime = new Date().getTime();
        long endTime = new Date().getTime();
        uriMap.put("startTime",startTime);
        uriMap.put("endTime", endTime);

        String returnStr1 = httpClient.sendGet(url,uriMap);

        //模拟请求返回报文结果
        String returnStr = "[" +
                                "{ "+
                                    "\"dealerNo\": \"GS0031211\","+
                                    "\"companyNo\": \"GS0031211\","+
                                    "\"transactionType\": \"STOCK_IN\","+
                                    "\"stockChangeDate\": \"2019-11-15\","+
                                    "\"commissionNo\": \"0688644948\","+
                                    "\"vin\": \"WDDWH4CB4HF559394\","+
                                    "\"fin\": \"WDD2052421F559394\","+
                                    "\"baumuster\": \"2052421\","+
                                    "\"nst\": \"CN2\","+
                                    "\"brand\": \"MB\","+
                                    "\"origin\": \"CBU\","+
                                    "\"model\": \"C200\","+
                                    "\"typeClass\": \"C\","+
                                    "\"engineNo\": \"27492031031662\","+
                                    "\"originVehicleStatus\": \"NOT_BOOKED\","+
                                    "\"currentVehicleStatus\": \"BOOKED_IN\","+
                                    "\"description\": \"C 200 旅行轿车\","+
                                    "\"vehicleCurrentCost\": 0,"+
                                    "\"vehicleOldCost\": 0,"+
                                    "\"vehicleCostChange\": 0,"+
                                    "\"operationDate\": \"2019-11-15\""+
                                 "}," +
                               "{"+
                                    "\"dealerNo\": \"GS0031211\","+
                                    "\"companyNo\": \"GS0031211\","+
                                    "\"transactionType\": \"COST_CHANGE\","+
                                    "\"stockChangeDate\": \"2019-11-15\","+
                                    "\"commissionNo\": \"0688644948\","+
                                    "\"vin\": \"WDDWH4CB4HF559394\","+
                                    "\"fin\": \"WDD2052421F559394\","+
                                    "\"baumuster\": \"2052421\","+
                                    "\"nst\": \"CN2\","+
                                    "\"brand\": \"MB\","+
                                    "\"origin\": \"CBU\","+
                                    "\"model\": \"C200\","+
                                    "\"typeClass\": \"C\","+
                                    "\"engineNo\": \"27492031031662\","+
                                    "\"originVehicleStatus\": \"BOOKED_IN\","+
                                    "\"currentVehicleStatus\": \"BOOKED_IN\","+
                                    "\"description\": \"C 200 旅行轿车\","+
                                    "\"vehicleCurrentCost\": 332510.09,"+
                                    "\"vehicleOldCost\": 0,"+
                                    "\"vehicleCostChange\": 332510.09,"+
                                    "\"operationDate\": \"2019-11-16\" "+
                              "}" +
                           "]";

        List<VehicleStock> vehicleStockList = (List<VehicleStock>) JSONArray.parseArray(returnStr, VehicleStock.class);
        //保存入库
       String str =  vehicleStockService.savevehicleStockList(vehicleStockList);


        System.out.println(str);

    }

    public static void main(String[] args) {
        //模拟请求返回报文结果
        String returnStr = "[" +
                "{ "+
                "\"dealerNo\": \"GS0031211\","+
                "\"companyNo\": \"GS0031211\","+
                "\"transactionType\": \"STOCK_IN\","+
                "\"stockChangeDate\": \"2019-11-15\","+
                "\"commissionNo\": \"0688644948\","+
                "\"vin\": \"WDDWH4CB4HF559394\","+
                "\"fin\": \"WDD2052421F559394\","+
                "\"baumuster\": \"2052421\","+
                "\"nst\": \"CN2\","+
                "\"brand\": \"MB\","+
                "\"origin\": \"CBU\","+
                "\"model\": \"C200\","+
                "\"typeClass\": \"C\","+
                "\"engineNo\": \"27492031031662\","+
                "\"originVehicleStatus\": \"NOT_BOOKED\","+
                "\"currentVehicleStatus\": \"BOOKED_IN\","+
                "\"description\": \"C 200 旅行轿车\","+
                "\"vehicleCurrentCost\": 0,"+
                "\"vehicleOldCost\": 0,"+
                "\"vehicleCostChange\": 0,"+
                "\"operationDate\": \"2019-11-15\""+
                "}," +
                "{"+
                "\"dealerNo\": \"GS0031211\","+
                "\"companyNo\": \"GS0031211\","+
                "\"transactionType\": \"COST_CHANGE\","+
                "\"stockChangeDate\": \"2019-11-15\","+
                "\"commissionNo\": \"0688644948\","+
                "\"vin\": \"WDDWH4CB4HF559394\","+
                "\"fin\": \"WDD2052421F559394\","+
                "\"baumuster\": \"2052421\","+
                "\"nst\": \"CN2\","+
                "\"brand\": \"MB\","+
                "\"origin\": \"CBU\","+
                "\"model\": \"C200\","+
                "\"typeClass\": \"C\","+
                "\"engineNo\": \"27492031031662\","+
                "\"originVehicleStatus\": \"BOOKED_IN\","+
                "\"currentVehicleStatus\": \"BOOKED_IN\","+
                "\"description\": \"C 200 旅行轿车\","+
                "\"vehicleCurrentCost\": 332510.09,"+
                "\"vehicleOldCost\": 0,"+
                "\"vehicleCostChange\": 332510.09,"+
                "\"operationDate\": \"2019-11-16\" "+
                "}" +
                "]";

        List<VehicleStock> vehicleStocks = (List<VehicleStock>) JSONArray.parseArray(returnStr, VehicleStock.class);

        System.out.println(vehicleStocks);

    }

}
