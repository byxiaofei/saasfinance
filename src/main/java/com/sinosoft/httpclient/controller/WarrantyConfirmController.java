package com.sinosoft.httpclient.controller;

import com.alibaba.fastjson.JSONArray;
import com.sinosoft.httpclient.domain.JsonToWarrantyConfirm;
import com.sinosoft.httpclient.domain.Tasksdetailsinfo;
import com.sinosoft.httpclient.service.HttpClient;
import com.sinosoft.httpclient.service.TasksdetailsService;
import com.sinosoft.httpclient.service.WarrantyConfirmService;
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
public class WarrantyConfirmController  {

    private Logger logger = LoggerFactory.getLogger(WarrantyConfirmController.class);

    @Resource
    WarrantyConfirmService warrantyConfirmService;
    @Resource
    HttpClient httpClient;
    @Resource
    TasksdetailsService tasksdetailsService;

    public void execute() {
        try {
            long start = System.currentTimeMillis();

            Long endTime = new Date().getTime();
            Tasksdetailsinfo tasksdetailsinfo = new Tasksdetailsinfo();
            tasksdetailsinfo.setBatch("Warranty_Confirm");
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
            System.out.println("Warranty_Confirm 接口调用耗时："+(System.currentTimeMillis()-start)+"ms");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("当前异常结果为："+e);
        }
    }
}
