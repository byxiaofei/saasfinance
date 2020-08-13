package com.sinosoft.httpclient.controller;

import com.alibaba.fastjson.JSONArray;
import com.sinosoft.httpclient.domain.JsonToWarrantyConfirm;
import com.sinosoft.httpclient.service.HttpClient;
import com.sinosoft.httpclient.service.WarrantyConfirmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/testWarrantyConfirm")
public class WarrantyConfirmController {
    @Autowired
    WarrantyConfirmService warrantyConfirmService;
    @Autowired
    HttpClient httpClient;

    @RequestMapping(value = "/1")
    public void getWarrantyConfirm(){
        String url = "https://otrplus-cn-test.api.mercedes-benz.com.cn/api/accounting/warranty-confirm";
        //添加参数
        Map<String,Long> uriMap = new HashMap<>(6);
        Long startTime = new Date().getTime();//开始时间需要传递
        Long endTime = new Date().getTime();
        uriMap.put("startTime",Long.parseLong("1574647200000"));
        uriMap.put("endTime",endTime);
        //通过url和uriMap拼接调用远端的接口，返回结果
        String returnMessage = httpClient.sendGet(url,uriMap);
        System.out.println(returnMessage);
        String message;
        if(returnMessage.equals("接口调用失败")){
            message = "接口调用失败";  //TODO 循环请求...
        }else{
            List<JsonToWarrantyConfirm> jsonToWarrantyConfirms = JSONArray.parseArray(returnMessage, JsonToWarrantyConfirm.class);
            //保存入库
            message = warrantyConfirmService.saveWarrantyConfirmList(jsonToWarrantyConfirms);
        }
        System.out.println(message);
    }
}
