package com.sinosoft.httpclient.controller;


import com.alibaba.fastjson.JSONArray;
import com.sinosoft.httpclient.domain.JsonToPartsRequisition;
import com.sinosoft.httpclient.service.HttpClient;
import com.sinosoft.httpclient.service.PartsRequisitionService;
import com.sinosoft.repository.SubjectRepository;
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
    SubjectRepository subjectRepository;


    @Resource
    HttpClient httpClient;

    /**
     * 6. Parts Requisition 接口解析报文,范本
     */
    @RequestMapping("/1")
    public void getPartsRequisition(){
        String url = "https://otrplus-cn-test.api.mercedes-benz.com.cn/api/accounting/parts-requisition";
        // 添加参数
        Map<String,Long> urlMap = new HashMap<>();
        urlMap.put("startTime",Long.valueOf("1597000000000"));
        urlMap.put("endTime",Long.valueOf("1597075199000"));

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


    @RequestMapping("/2")
    public String getPartsRequisiton(){
        String returnStr = "[\n" +
                "    {\n" +
                "        \"id\": 13538,\n" +
                "        \"dealerNo\": \"GS0036160\",\n" +
                "        \"companyNo\": \"lyytzx01\",\n" +
                "        \"customerName\": \"李三先生\",\n" +
                "        \"companyName\": null,\n" +
                "        \"customerId\": \"9-7hCq38Seqd1iO_HJOJPQ\",\n" +
                "        \"companyId\": null,\n" +
                "        \"fin\": \"LE42131481L268857\",\n" +
                "        \"vin\": \"LE4ZG4JB9KL268857\",\n" +
                "        \"registrationNo\": \"浙A99999\",\n" +
                "        \"docType\": \"R\",\n" +
                "        \"docNo\": \"D202008100001\",\n" +
                "        \"docDate\": \"2020-08-10\",\n" +
                "        \"operationDate\": \"2020-08-10\",\n" +
                "        \"orderNo\": \"2008101003bhvh\",\n" +
                "        \"requisitionParts\": [\n" +
                "            {\n" +
                "                \"id\": 13538,\n" +
                "                \"line\": \"3\",\n" +
                "                \"partsNo\": \"A0009898301    UCN8\",\n" +
                "                \"description\": \"机油 5W40 208L 229.5\",\n" +
                "                \"genuineFlag\": \"Y\",\n" +
                "                \"partsAnalysisCode\": null,\n" +
                "                \"quantity\": 7.00,\n" +
                "                \"partsUnitCost\": 52.0400,\n" +
                "                \"ictCompany\": null,\n" +
                "                \"ictOrder\": null\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": 13539,\n" +
                "        \"dealerNo\": \"GS0036160\",\n" +
                "        \"companyNo\": \"lyytzx01\",\n" +
                "        \"customerName\": \"李三先生\",\n" +
                "        \"companyName\": null,\n" +
                "        \"customerId\": \"9-7hCq38Seqd1iO_HJOJPQ\",\n" +
                "        \"companyId\": null,\n" +
                "        \"fin\": \"LE42131481L268857\",\n" +
                "        \"vin\": \"LE4ZG4JB9KL268857\",\n" +
                "        \"registrationNo\": \"浙A99999\",\n" +
                "        \"docType\": \"R\",\n" +
                "        \"docNo\": \"D202008100001\",\n" +
                "        \"docDate\": \"2020-08-10\",\n" +
                "        \"operationDate\": \"2020-08-10\",\n" +
                "        \"orderNo\": \"2008101003bhvh\",\n" +
                "        \"requisitionParts\": [\n" +
                "            {\n" +
                "                \"id\": 13539,\n" +
                "                \"line\": \"4\",\n" +
                "                \"partsNo\": \"A2701800109\",\n" +
                "                \"description\": \"机滤\",\n" +
                "                \"genuineFlag\": \"Y\",\n" +
                "                \"partsAnalysisCode\": null,\n" +
                "                \"quantity\": 1.00,\n" +
                "                \"partsUnitCost\": 52.1908,\n" +
                "                \"ictCompany\": null,\n" +
                "                \"ictOrder\": null\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "]";


        List<JsonToPartsRequisition> jsonToPartsRequisitions = JSONArray.parseArray(returnStr, JsonToPartsRequisition.class);
        //  保存入库
        String finalresultMessage = partsRequisitionService.savePartsRequisitionList(jsonToPartsRequisitions);
        System.out.println(finalresultMessage);
        return finalresultMessage;

    }
}
