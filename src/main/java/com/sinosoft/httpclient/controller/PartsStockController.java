package com.sinosoft.httpclient.controller;

import com.alibaba.fastjson.JSONArray;
import com.sinosoft.httpclient.config.SecretKey;
import com.sinosoft.httpclient.domain.JsonToPartsStock;
import com.sinosoft.httpclient.domain.Tasksdetailsinfo;
import com.sinosoft.httpclient.service.HttpClient;
import com.sinosoft.httpclient.service.PartsStockService;
import com.sinosoft.httpclient.service.TasksdetailsService;
import com.sinosoft.httpclient.task.ScheduledOfTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.xml.crypto.Data;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Component
public class PartsStockController implements ScheduledOfTask{

    private Logger logger = LoggerFactory.getLogger(PartsStockController.class);

    @Resource
    private HttpClient httpClient ;

    @Resource
    private PartsStockService partsStockService;
    @Resource
    TasksdetailsService tasksdetailsService;
    /**
     * 3. Parts stock in / checking 接口接收解析报文
     */
    @Override
    public void execute() {
        try {
            long start = System.currentTimeMillis();
            Long endTime = new Date().getTime();
            Tasksdetailsinfo tasksdetailsinfo = new Tasksdetailsinfo();
            tasksdetailsinfo.setBatch("Parts_Stock_In_Checking");
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

                String returnStr = httpClient.sendGet(url, uriMap,headerValue);
                String str;
                if (returnStr.equals("接口调用失败")) {
                    str = "接口调用失败"; // TODO 循环请求或者 其他原因导致请求失败，具体原因分析
                } else {
                    List<JsonToPartsStock> partsStockList = (List<JsonToPartsStock>) JSONArray.parseArray(returnStr, JsonToPartsStock.class);
                    //保存入库
                    System.out.println(partsStockList);
                    for(JsonToPartsStock jsonToPartsStock : partsStockList){
                        String companyNo = jsonToPartsStock.getCompanyNo();
                        if(SecretKey.FIRST_COMPANY_NO.equals(companyNo)){
                            jsonToPartsStock.setCompanyNo(SecretKey.FIRST_BRANCH_CODE);
                        }else if(SecretKey.SECOND_COMPANY_NO.equals(companyNo)){
                            jsonToPartsStock.setCompanyNo(SecretKey.SECOND_BRANCH_CODE);
                        }
                    }
                    str = partsStockService.savePartsStockListList(partsStockList,tasksdetailsinfo.getEndTime());
                }
                System.out.println("第"+(i+1)+"次Parts_Stock_In_Checking 接口调用耗时："+(System.currentTimeMillis()-start)+"ms");
                System.out.println("第"+(i+1)+"个接口调用完毕！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("当前异常结果为："+e);
        }
    }

}
