package com.sinosoft.httpclient.controller;

import com.alibaba.fastjson.JSONArray;
import com.sinosoft.httpclient.config.SecretKey;
import com.sinosoft.httpclient.domain.JsonToPartsInvoice;
import com.sinosoft.httpclient.domain.Tasksdetailsinfo;
import com.sinosoft.httpclient.service.HttpClient;
import com.sinosoft.httpclient.service.PartsInvoiceService;
import com.sinosoft.httpclient.service.TasksdetailsService;
import com.sinosoft.httpclient.task.ScheduledOfTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PartsInvoiceController  implements ScheduledOfTask {

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
            long start = System.currentTimeMillis();
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

            for(int i = 0 ; i < 2 ; i ++ ){
                String headerValue ;
                if( i == 0 ){
                    headerValue = SecretKey.FIRST_KEY_MESSAGE;
                }else{
                    headerValue = SecretKey.SECOND_KEY_MESSAGE;
                }
                //通过url和uriMap拼接调用远端的接口，返回结果
                String returnMessage = httpClient.sendGet(url,uriMap,headerValue);
                System.out.println(returnMessage);
                String message;
                if(returnMessage.equals("接口调用失败")){
                    message = "接口调用失败";  //TODO 循环请求...
                }else{
                    List<JsonToPartsInvoice> jsonToPartsInvoices = JSONArray.parseArray(returnMessage, JsonToPartsInvoice.class);
                    for(JsonToPartsInvoice jsonToPartsInvoice : jsonToPartsInvoices){
                        String companyNo = jsonToPartsInvoice.getCompanyNo();
                        if(SecretKey.FIRST_COMPANY_NO.equals(companyNo)){
                            jsonToPartsInvoice.setCompanyNo(SecretKey.FIRST_BRANCH_CODE);
                        }else if(SecretKey.SECOND_COMPANY_NO.equals(companyNo)){
                            jsonToPartsInvoice.setCompanyNo(SecretKey.SECOND_BRANCH_CODE);
                        }
                    }
                    //保存入库
                    message = partsInvoiceService.savePartsInvoiceList(jsonToPartsInvoices,tasksdetailsinfo.getEndTime());
                }
                System.out.println("第"+(i+1)+"次Parts_Invoice 接口调用耗时："+(System.currentTimeMillis()-start)+"ms");
                System.out.println("第"+(i+1)+"个接口调用完毕！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("当前异常结果为："+e);
        }
    }

}
