package com.sinosoft.httpclient.controller;

import com.alibaba.fastjson.JSONArray;
import com.sinosoft.httpclient.domain.JsonToPartsPromotion;
import com.sinosoft.httpclient.domain.PartsPromotion;
import com.sinosoft.httpclient.domain.Tasksdetailsinfo;
import com.sinosoft.httpclient.service.HttpClient;
import com.sinosoft.httpclient.service.PartsPromotionService;
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
public class PartsPromotionController {

    private Logger logger = LoggerFactory.getLogger(PartsPromotionController.class);

    @Resource
    private PartsPromotionService partsPromotionService;

    @Resource
    private HttpClient httpClient;
    @Resource
    TasksdetailsService tasksdetailsService;

    public void execute() {
        try {
            long start = System.currentTimeMillis();
            Long endTime = new Date().getTime();
            Tasksdetailsinfo tasksdetailsinfo = new Tasksdetailsinfo();
            tasksdetailsinfo.setBatch("Parts_Promotion");
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
            System.out.println(returnStr);
            String str;
            if(returnStr.equals("调用接口失败")){
                str = "接口调用失败"; // TODO 循环调用请求或者其他原因导致的请求失败，具体原因在进行分析
            }else{
                List<JsonToPartsPromotion> jsonToPartsPromotion = JSONArray.parseArray(returnStr, JsonToPartsPromotion.class);
                // 保存入库
                str = partsPromotionService.savePartsPromotionList(jsonToPartsPromotion,tasksdetailsinfo.getEndTime());
            }
            System.out.println("Parts_Promotion 接口调用耗时："+(System.currentTimeMillis()-start)+"ms");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("当前异常结果为："+e);
        }
    }
}
