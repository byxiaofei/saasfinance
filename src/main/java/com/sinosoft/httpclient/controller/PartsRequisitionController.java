package com.sinosoft.httpclient.controller;


import com.alibaba.fastjson.JSONArray;
import com.sinosoft.httpclient.config.SecretKey;
import com.sinosoft.httpclient.domain.JsonToPartsRequisition;
import com.sinosoft.httpclient.domain.Tasksdetailsinfo;
import com.sinosoft.httpclient.service.HttpClient;
import com.sinosoft.httpclient.service.PartsRequisitionService;
import com.sinosoft.httpclient.service.TasksdetailsService;
import com.sinosoft.httpclient.service.impl.PartsRequisitionServiceImpl;
import com.sinosoft.httpclient.task.ScheduledOfTask;
import com.sinosoft.repository.SubjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PartsRequisitionController  implements ScheduledOfTask {

    private Logger logger = LoggerFactory.getLogger(PartsRequisitionController.class);

    @Resource
    PartsRequisitionService partsRequisitionService;

    @Resource
    HttpClient httpClient;
    @Resource
    TasksdetailsService tasksdetailsService;
    /**
     * 6. Parts Requisition 接口解析报文,范本
     */
    @Override
    public void execute() {
        try {
            long start = System.currentTimeMillis();
            Long endTime = new Date().getTime();
            Tasksdetailsinfo tasksdetailsinfo = new Tasksdetailsinfo();
            tasksdetailsinfo.setBatch("Parts_Requisition");
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

            for(int i = 0 ; i < 2 ; i ++){

                String headerValue ;
                if( i == 0 ){
                    headerValue = SecretKey.FIRST_KEY_MESSAGE;
                }else{
                    headerValue = SecretKey.SECOND_KEY_MESSAGE;
                }
                String returnStr = httpClient.sendGet(url,uriMap,headerValue);
                String string;
                if(returnStr.equals("接口调用失败")){
                    string = "接口调用失败"; // TODO 循环请求 或者其他原因导致的请求失败的原因
                }else{
                    List<JsonToPartsRequisition> jsonToPartsRequisitions = JSONArray.parseArray(returnStr, JsonToPartsRequisition.class);
                    System.out.println(jsonToPartsRequisitions);
                    for(JsonToPartsRequisition jsonToPartsRequisition : jsonToPartsRequisitions){
                        String companyNo = jsonToPartsRequisition.getCompanyNo();
                        if(SecretKey.FIRST_COMPANY_NO.equals(companyNo)){
                            jsonToPartsRequisition.setCompanyNo(SecretKey.FIRST_BRANCH_CODE);
                        }else if(SecretKey.SECOND_COMPANY_NO.equals(companyNo)){
                            jsonToPartsRequisition.setCompanyNo(SecretKey.SECOND_BRANCH_CODE);
                        }
                    }
                    //  保存入库
                    string = partsRequisitionService.savePartsRequisitionList(jsonToPartsRequisitions,tasksdetailsinfo.getEndTime());
                }
                System.out.println("第"+(i+1)+"次Parts_Requisition 接口调用耗时："+(System.currentTimeMillis()-start)+"ms");
                System.out.println("第"+(i+1)+"个接口调用完毕！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("当前异常结果为："+e);
        }
    }

}
