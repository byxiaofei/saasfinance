package com.sinosoft.httpclient.controller;


import com.alibaba.fastjson.JSONArray;
import com.sinosoft.httpclient.domain.JsonToPartsRequisition;
import com.sinosoft.httpclient.service.HttpClient;
import com.sinosoft.httpclient.service.PartsRequisitionService;
import com.sinosoft.httpclient.service.impl.PartsRequisitionServiceImpl;
import com.sinosoft.repository.SubjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private Logger logger = LoggerFactory.getLogger(PartsRequisitionController.class);

    @Resource
    PartsRequisitionService partsRequisitionService;

    @Resource
    HttpClient httpClient;

    /**
     * 6. Parts Requisition 接口解析报文,范本
     */
    @RequestMapping("/1")
    public void getPartsRequisition(){
        try {
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
                string = partsRequisitionService.savePartsRequisitionList(jsonToPartsRequisitions,"1597039093220");
            }
            System.out.println(string);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("当前异常结果为："+e);
        }
    }

}
