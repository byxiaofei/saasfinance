package com.sinosoft.httpclient.controller;

import com.alibaba.fastjson.JSONArray;
import com.sinosoft.httpclient.domain.Tasksdetailsinfo;
import com.sinosoft.httpclient.dto.PartsVerificationDTO;
import com.sinosoft.httpclient.service.HttpClient;
import com.sinosoft.httpclient.service.PartsVerificationService;
import com.sinosoft.httpclient.service.TasksdetailsService;
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
public class PartsVerificationController implements ScheduledOfTask {

    private Logger logger = LoggerFactory.getLogger(PartsVerificationController.class);

    @Resource
    HttpClient httpClient;

    @Resource
    PartsVerificationService partsVerificationService;
    @Resource
    TasksdetailsService tasksdetailsService;
    /**
     *
     */
    @Override
    public void execute() {

        try {
            long start = System.currentTimeMillis();
            Long endTime = new Date().getTime();
            Tasksdetailsinfo tasksdetailsinfo = new Tasksdetailsinfo();
            tasksdetailsinfo.setBatch("Parts_Verification");
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

            String returnStr = httpClient.sendGet(url,uriMap);
            System.out.println(returnStr );
            String str=null  ;
            if(returnStr.equals("接口调用失败")){
                str = "接口调用失败"; // TODO 循环请求或者 其他原因导致请求失败，具体原因分析
            }else{
                List<PartsVerificationDTO> partsVerificationList = JSONArray.parseArray(returnStr, PartsVerificationDTO.class);
                //保存入库
                System.out.println(partsVerificationList);
                str =  partsVerificationService.getPartsVerification(partsVerificationList,tasksdetailsinfo.getEndTime());
            }
            System.out.println("Parts_Verification 接口调用耗时："+(System.currentTimeMillis()-start)+"ms");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("当前异常结果为："+e);
        }
    }

}


