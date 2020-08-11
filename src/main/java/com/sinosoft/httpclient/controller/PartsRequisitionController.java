package com.sinosoft.httpclient.controller;


import com.alibaba.fastjson.JSONArray;
import com.sinosoft.httpclient.domain.JsonToPartsRequisition;
import com.sinosoft.httpclient.service.HttpClient;
import com.sinosoft.httpclient.service.PartsRequisitionService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/testPartsRequisition")
public class PartsRequisitionController {

    @Resource
    PartsRequisitionService partsRequisitionService;

    @Resource
    HttpClient httpClient;

    /**
     * 6. Parts Requisition 接口解析报文
     */
    @RequestMapping("/1")
    public void getPartsRequisition(){
        String url = "https://otrplus-cn-test.api.mercedes-benz.com.cn/api/accounting/parts-requisition";
        // 添加参数
        Map<String,Long> urlMap = new HashMap<>();
        urlMap.put("startTime",Long.valueOf("1597000000000"));
        urlMap.put("endTime",new Date().getTime());

        String returnStr = httpClient.sendGet(url,urlMap);
        System.out.println(returnStr);
        String string;
        if(returnStr.equals("接口调用失败")){
            string = "接口调用失败"; // TODO 循环请求 或者其他原因导致的请求失败的原因
        }else{
            List<JsonToPartsRequisition> jsonToPartsRequisitions = JSONArray.parseArray(returnStr, JsonToPartsRequisition.class);
            //  保存入库
            string = partsRequisitionService.savePartsRequisitionList(jsonToPartsRequisitions);
        }
        System.out.println(string);
    }
}
