package com.sinosoft.httpclient.controller;

import com.alibaba.fastjson.JSONArray;
import com.sinosoft.httpclient.domain.JsonToPartsInvoice;
import com.sinosoft.httpclient.domain.PartsInvoice;
import com.sinosoft.httpclient.service.HttpClient;
import com.sinosoft.httpclient.service.PartsInvoiceService;
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

    @Override
    public void execute() {
        try {
            String url = "https://otrplus-cn-test.api.mercedes-benz.com.cn/api/accounting/parts-invoice";
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
                List<JsonToPartsInvoice> jsonToPartsInvoices = JSONArray.parseArray(returnMessage, JsonToPartsInvoice.class);
                //保存入库
                message = partsInvoiceService.savePartsInvoiceList(jsonToPartsInvoices);
            }
            System.out.println(message);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("当前异常结果为："+e);
        }
    }

}
