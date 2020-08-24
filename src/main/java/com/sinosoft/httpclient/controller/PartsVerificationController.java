package com.sinosoft.httpclient.controller;

import com.alibaba.fastjson.JSONArray;
import com.sinosoft.httpclient.dto.PartsVerificationDTO;
import com.sinosoft.httpclient.service.HttpClient;
import com.sinosoft.httpclient.service.PartsVerificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/partsVerification")
public class PartsVerificationController {

    private Logger logger = LoggerFactory.getLogger(PartsVerificationController.class);

    @Resource
    HttpClient httpClient;

    @Resource
    PartsVerificationService partsVerificationService;

    /**
     *
     */
    @RequestMapping(value = "/test")
    public void getPartsVerification(){

        try {
            String url = "https://otrplus-cn-test.api.mercedes-benz.com.cn/api/accounting/parts-verification";

            // 添加参数
            Map<String, Long> uriMap = new HashMap<>(6);
            Long startTime = new Date().getTime();   //开始时间需要传参
            Long endTime = new Date().getTime();

            uriMap.put("startTime",Long.parseLong("1594310400000"));
            uriMap.put("endTime",Long.parseLong("1597039093220"));

            String returnStr = httpClient.sendGet(url,uriMap);
            System.out.println(returnStr );
            String str=null  ;
            if(returnStr.equals("接口调用失败")){
                str = "接口调用失败"; // TODO 循环请求或者 其他原因导致请求失败，具体原因分析
            }else{
                List<PartsVerificationDTO> partsVerificationList = JSONArray.parseArray(returnStr, PartsVerificationDTO.class);
                //保存入库
                System.out.println(partsVerificationList);
                str =  partsVerificationService.getPartsVerification(partsVerificationList,"1597039093220");
            }
            System.out.println(str);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("当前异常结果为："+e);
        }
    }

}


