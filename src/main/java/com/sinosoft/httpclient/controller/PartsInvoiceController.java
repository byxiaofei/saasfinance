package com.sinosoft.httpclient.controller;

import com.alibaba.fastjson.JSONArray;
import com.sinosoft.httpclient.domain.JsonToPartsInvoice;
import com.sinosoft.httpclient.domain.PartsInvoice;
import com.sinosoft.httpclient.domain.Tasksdetailsinfo;
import com.sinosoft.httpclient.service.HttpClient;
import com.sinosoft.httpclient.service.PartsInvoiceService;
import com.sinosoft.httpclient.service.TasksdetailsService;
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
public class PartsInvoiceController implements ScheduledOfTask {

    private Logger logger = LoggerFactory.getLogger(PartsInvoiceController.class);

    @Resource
    HttpClient httpClient;
    @Resource
    PartsInvoiceService partsInvoiceService;
    @Resource
    TasksdetailsService tasksdetailsService;
    @Override
    public void execute() {
        try {

            Long endTime = new Date().getTime();
            Tasksdetailsinfo tasksdetailsinfo = new Tasksdetailsinfo();
            tasksdetailsinfo.setBatch("Parts_Invoice");
            tasksdetailsinfo = tasksdetailsService.findTasksdetails(tasksdetailsinfo);

            String url = tasksdetailsinfo.getUrl();
            //添加参数
            Map<String, Long> uriMap = new HashMap<>(6);

            uriMap.put("startTime",Long.parseLong(tasksdetailsinfo.getEndTime()));
            uriMap.put("endTime", endTime);

            //保存接口结束日期
            tasksdetailsinfo.setStartTime(tasksdetailsinfo.getEndTime());
            tasksdetailsinfo.setEndTime(endTime.toString());
            tasksdetailsService.saveTasksdetails(tasksdetailsinfo);

            //通过url和uriMap拼接调用远端的接口，返回结果
            String returnMessage = httpClient.sendGet(url,uriMap);
            System.out.println(returnMessage);
            String message;
            if(returnMessage.equals("接口调用失败")){
                message = "接口调用失败";  //TODO 循环请求...
            }else{
                List<JsonToPartsInvoice> jsonToPartsInvoices = JSONArray.parseArray(returnMessage, JsonToPartsInvoice.class);
                //保存入库
                message = partsInvoiceService.savePartsInvoiceList(jsonToPartsInvoices,tasksdetailsinfo.getEndTime());
            }
            System.out.println(message);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("当前异常结果为："+e);
        }
    }
//    @RequestMapping(value = "/2")
//    public String getPartsInvoice1(){
//        String returnStr = "[\n" +
//                "    {\n" +
//                "        \"id\": 345,\n" +
//                "        \"companyNo\": \"lyytzx01\",\n" +
//                "        \"dealerNo\": \"GS0036160\",\n" +
//                "        \"docType\": \"C\",\n" +
//                "        \"docNo\": \"SC202003064707\",\n" +
//                "        \"bizType\": \"OTC\",\n" +
//                "        \"docDate\": \"2020-03-06\",\n" +
//                "        \"customerName\": \"贺伟泽\",\n" +
//                "        \"companyName\": null,\n" +
//                "        \"customerId\": \"7e3vCum1RMyipRf_Qw40wQ\",\n" +
//                "        \"companyId\": null,\n" +
//                "        \"franchise\": \"S\",\n" +
//                "        \"model\": \"4530\",\n" +
//                "        \"registrationNo\": \"津MWW092\",\n" +
//                "        \"fin\": \"WME4530431Y088706\",\n" +
//                "        \"orderNo\": \"2003051020006h\",\n" +
//                "        \"invoiceParts\": [\n" +
//                "            {\n" +
//                "                \"id\": 899,\n" +
//                "                \"line\": \"1\",\n" +
//                "                \"partsNo\": \"QALCNPC89996125\",\n" +
//                "                \"description\": \"me 女士手包-粉色\",\n" +
//                "                \"partsAnalysisCode\": \"P\",\n" +
//                "                \"departmentCode\": \"P\",\n" +
//                "                \"quantity\": 1.000000,\n" +
//                "                \"partsUnitCost\": 75.000000,\n" +
//                "                \"unitSellingPrice\": 75.000000,\n" +
//                "                \"totalPrice\": -84.75,\n" +
//                "                \"discountRate\": 0.00,\n" +
//                "                \"discountAmount\": 0.00,\n" +
//                "                \"contribution\": null,\n" +
//                "                \"netValue\": -75.00,\n" +
//                "                \"vatRate\": 0.130000,\n" +
//                "                \"vatAmount\": -9.75,\n" +
//                "                \"salesType\": \"I\",\n" +
//                "                \"customerTypeNo\": \"TTI00050\",\n" +
//                "                \"customerTypeNoDescription\": \"售后免费账户\",\n" +
//                "                \"genuineFlag\": \"Y\"\n" +
//                "            }\n" +
//                "        ],\n" +
//                "        \"operationDate\": \"2020-07-06\"\n" +
//                "    }\n" +
//                "]";;
//        List<JsonToPartsInvoice> jsonToPartsInvoices = JSONArray.parseArray(returnStr, JsonToPartsInvoice.class);
//        String finalresultMessage = partsInvoiceService.savePartsInvoiceList(jsonToPartsInvoices,"2020-08-26");
//        System.out.println(finalresultMessage);
//        return finalresultMessage;
//    }

}
