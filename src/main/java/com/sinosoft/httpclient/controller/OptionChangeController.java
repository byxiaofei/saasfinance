package com.sinosoft.httpclient.controller;

import com.alibaba.fastjson.JSONArray;
import com.sinosoft.httpclient.domain.OptionChange;
import com.sinosoft.httpclient.service.HttpClient;
import com.sinosoft.httpclient.service.OptionChangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value="/testOptionChange")
public class OptionChangeController {
    @Autowired
    private HttpClient httpClient;
    @Autowired
    private OptionChangeService optionChangeService;
    /**
    *OptionChange 接口解析报文
    */
    @RequestMapping(value = "/1")
    public void getOptionChange(){
        String url="https://otrplus-cn-test.api.mercedes-benz.com.cn/api/accounting/option-change";
        //添加参数
        Map<String,Long>uriMap =new HashMap<>(6);
        Long startTime =new Date().getTime();  //开始时间传参
        Long endTime = new Date().getTime();
        uriMap.put("startTime",Long.parseLong("1596001003220"));
        uriMap.put("endTime",endTime);
        String returnStr =httpClient.sendGet(url,uriMap);
        String str;

        if (returnStr.equals("接口调用失败")){
            str = "接口调用失败";
        }else {
            List<OptionChange> optionChanges = JSONArray.parseArray(returnStr,OptionChange.class);
            //保存入库
            str =optionChangeService.saveoptionChangeList(optionChanges);
        }
        System.out.println(str);
    }
    public static void main(String[] args){
        String returnMessage="["+
                                "{"+
                                        "\"dealerNo\": \"GS0031211\", " +
                                        "\"companyNo\": \"GS0031211\", " +
                                        "\"commissionNo\": \"0782864110\", " +
                                        "\"vin\": \"LE4ZG4CB6HL101989\", " +
                                        "\"fin\": \"LE42131421L101989\", " +
                                        "\"baumuster\": \"2131421\", " +
                                        "\"nst\": \"CBB\", " +
                                        "\"brand\": \"MB\", " +
                                        "\"origin\": \"PbP\", " +
                                        "\"model\": \"E200\", " +
                                        "\"typeClass\": \"E\", " +
                                        "\"vehicleStatus\": \"BOOKED_INVOICED\", " +
                                        "\"optionId\": \"OPTN51CD9C26E7C04EBDA62A66ACF245DF08\", " +
                                        "\"optionCode\": \"pp\", " +
                                        "\"optionDescription\": \"sourcecode 为 p!!\", " +
                                        "\"sourceCode\": \"P\", " +
                                        "\"optionCategoryCode\": \"p0\", " +
                                        "\"originQuantity\": \"1\", " +
                                        "\"currentQuantity\": \"0\", " +
                                        "\"originEstimateCost\": \"12\", " +
                                        "\"originActualCost\": \"23\", " +
                                        "\"currentActualCost\": \"23\", " +
                                        "\"originOptionStatus\": \"BOOKED_INVOICED\", " +
                                        "\"currentOptionStatus\": \"DELETED\", " +
                                        "\"transactionType\": \"OPTION_DELETE\", " +
                                        "\"optionCostChange\": \"-23\", " +
                                        "\"businessDate\": \"2019-12-19\", " +
                                        "\"triggerDate\": \"2019-12-19\", " +
                                    "}"
                                +"]";
        List<OptionChange> optionChangeList = JSONArray.parseArray(returnMessage,OptionChange.class);
        System.out.println(optionChangeList);
    }
}
