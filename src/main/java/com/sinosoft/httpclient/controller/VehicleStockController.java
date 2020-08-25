package com.sinosoft.httpclient.controller;

import com.alibaba.fastjson.JSONArray;
import com.sinosoft.httpclient.domain.VehicleStock;
import com.sinosoft.httpclient.dto.VehicleStockDTO;
import com.sinosoft.httpclient.service.HttpClient;
import com.sinosoft.httpclient.service.VehicleStockService;
import com.sinosoft.httpclient.task.ScheduledOfTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class VehicleStockController  implements ScheduledOfTask {

    private Logger logger = LoggerFactory.getLogger(VehicleStockController.class);

    @Resource
    private HttpClient httpClient ;

    @Resource
    private VehicleStockService vehicleStockService;

    /**
     * VehicleStock 接口接收解析报文
     */
    @Override
    public void execute() {
        try {
            String url = "https://otrplus-cn-test.api.mercedes-benz.com.cn/api/accounting/vehicle-stock-change";
            //添加参数
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
                List<VehicleStock> vehicleStockList = (List<VehicleStock>) JSONArray.parseArray(returnStr, VehicleStock.class);
                //保存入库
                 str =  vehicleStockService.savevehicleStockList(vehicleStockList);
            }
            System.out.println(str);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("当前异常结果为："+e);
        }

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
