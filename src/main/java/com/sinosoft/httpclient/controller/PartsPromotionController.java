package com.sinosoft.httpclient.controller;

import com.alibaba.fastjson.JSONArray;
import com.sinosoft.httpclient.domain.JsonToPartsPromotion;
import com.sinosoft.httpclient.domain.PartsPromotion;
import com.sinosoft.httpclient.service.HttpClient;
import com.sinosoft.httpclient.service.PartsPromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/testPartsPromotion")
public class PartsPromotionController {

    @Autowired
    private PartsPromotionService partsPromotionService;

    @Autowired
    private HttpClient httpClient;

    @RequestMapping(value = "/1")
    public void getPartsPromotionInfo(){
        String url = "https://otrplus-cn-test.api.mercedes-benz.com.cn/api/accounting/parts-promotion";
        // 添加参数
        Map<String,Long> urlMap = new HashMap<>();
        Long startTime = new Date().getTime();
        Long endTime = new Date().getTime();
        urlMap.put("startTime",Long.parseLong("1595841003220"));
        urlMap.put("endTime",endTime);
        String returnStr = httpClient.sendGet(url,urlMap);
        System.out.println(returnStr);
        String str;
        if(returnStr.equals("调用接口失败")){
            str = "接口调用失败"; // TODO 循环调用请求或者其他原因导致的请求失败，具体原因在进行分析
        }else{
            List<JsonToPartsPromotion> jsonToPartsPromotion = JSONArray.parseArray(returnStr, JsonToPartsPromotion.class);
            // 保存入库
            partsPromotionService.savePartsPromotionList(jsonToPartsPromotion);
        }
    }
}
