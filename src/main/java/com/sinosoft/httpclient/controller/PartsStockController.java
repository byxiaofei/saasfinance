package com.sinosoft.httpclient.controller;

import com.alibaba.fastjson.JSONArray;
import com.sinosoft.httpclient.domain.JsonToPartsStock;
import com.sinosoft.httpclient.service.HttpClient;
import com.sinosoft.httpclient.service.PartsStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping(value = "/testPartsStock")
public class PartsStockController {
    @Autowired
    private HttpClient httpClient ;
    @Autowired
    private PartsStockService partsStockService;

    /**
     * 3. Parts stock in / checking 接口接收解析报文
     */
    @RequestMapping(value = "/1")
    public void getVehicleStock() {
        String url = "https://otrplus-cn-test.api.mercedes-benz.com.cn/api/accounting/parts-stock-change";
        //添加参数
        Map<String, Long> uriMap = new HashMap<>(6);
        Long startTime = new Date().getTime();   //开始时间需要传参
        Long endTime = new Date().getTime();
        uriMap.put("startTime", Long.parseLong("1596001003220"));
        uriMap.put("endTime", endTime);

        String returnStr = httpClient.sendGet(url, uriMap);
        System.out.println(returnStr);
        String str;
        if (returnStr.equals("接口调用失败")) {
            str = "接口调用失败"; // TODO 循环请求或者 其他原因导致请求失败，具体原因分析
        } else {
            List<JsonToPartsStock> partsStockList = (List<JsonToPartsStock>) JSONArray.parseArray(returnStr, JsonToPartsStock.class);
            //保存入库
            str = partsStockService.savePartsStockListList(partsStockList);
        }
        System.out.println(str);
    }

    public static void main(String[] args) {
        //模拟请求返回报文结果
        String returnStr = "[" +
                "{ "+
                    "\"dealerNo\": \"GS0031211\","+
                    "\"companyNo\": \"GS0031211\","+
                    "\"transactionType\": \"UNRECEIVE\","+
                    "\"stockInParts\": " +
                    "[" +
                            "{ "+
                                "\"partsNo\": \"A0008204206\","+
                                "\"description\": \"冷藏箱\","+
                                "\"genuineFlag\": \"N\",\n" +
                                "\"partsAnalysisCode\": \"G\","+
                                "\"businessDate\": \"2019-11-14\","+
                                "\"poNo\": \"3284\","+
                                "\"quantity\": -10,"+
                                "\"partsUnitCost\": 2024.58,"+
                                "\"supplierNo\": \"VEH00001\","+
                                "\"supplierDescription\": \"利星行汽车服务有限公司\" "+
                            "}" +
                    "]," +
                    "\"operationDate\": \"2019-11-14\" "+
                "}" +
        "]";

        List<JsonToPartsStock> vehicleStocks = (List<JsonToPartsStock>) JSONArray.parseArray(returnStr, JsonToPartsStock.class);

        System.out.println(vehicleStocks);

    }

}
