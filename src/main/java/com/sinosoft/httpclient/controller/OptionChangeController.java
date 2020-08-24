package com.sinosoft.httpclient.controller;

import com.alibaba.fastjson.JSONArray;
import com.sinosoft.httpclient.domain.OptionChange;
import com.sinosoft.httpclient.service.HttpClient;
import com.sinosoft.httpclient.service.OptionChangeService;
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
@RequestMapping(value="/testOptionChange")
public class OptionChangeController {

    private Logger logger = LoggerFactory.getLogger(OptionChangeController.class);

    @Resource
    private HttpClient httpClient;
    @Resource
    private OptionChangeService optionChangeService;
    /**
    *OptionChange 接口解析报文
    */
    @RequestMapping(value = "/1")
    public void getOptionChange(){
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("当前异常结果为："+e);
        }
    }
}
