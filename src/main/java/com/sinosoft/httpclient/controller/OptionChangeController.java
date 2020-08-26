package com.sinosoft.httpclient.controller;

import com.alibaba.fastjson.JSONArray;
import com.sinosoft.httpclient.domain.OptionChange;
import com.sinosoft.httpclient.domain.Tasksdetailsinfo;
import com.sinosoft.httpclient.service.HttpClient;
import com.sinosoft.httpclient.service.OptionChangeService;
import com.sinosoft.httpclient.service.TasksdetailsService;
import com.sinosoft.httpclient.service.VehicleStockService;
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
public class OptionChangeController implements ScheduledOfTask {

    private Logger logger = LoggerFactory.getLogger(OptionChangeController.class);

    @Resource
    private HttpClient httpClient;
    @Resource
    private OptionChangeService optionChangeService;
    @Resource
    TasksdetailsService tasksdetailsService;
    /**
    *OptionChange 接口解析报文
    */
    @Override
    public void execute() {
        try {
            Long endTime = new Date().getTime();
            Tasksdetailsinfo tasksdetailsinfo = new Tasksdetailsinfo();
            tasksdetailsinfo.setBatch("Option_Change");
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

            String returnStr =httpClient.sendGet(url,uriMap);
            String str;

            if (returnStr.equals("接口调用失败")){
                str = "接口调用失败";
            }else {
                List<OptionChange> optionChanges = JSONArray.parseArray(returnStr,OptionChange.class);
                //保存入库
                str =optionChangeService.saveoptionChangeList(optionChanges,tasksdetailsinfo.getEndTime());
            }
            System.out.println(str);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("当前异常结果为："+e);
        }
    }
}
