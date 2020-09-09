package com.sinosoft.httpclient.controller;

import com.alibaba.fastjson.JSONArray;
import com.sinosoft.httpclient.config.SecretKey;
import com.sinosoft.httpclient.domain.Tasksdetailsinfo;
import com.sinosoft.httpclient.domain.VehicleInvoice;
import com.sinosoft.httpclient.service.HttpClient;
import com.sinosoft.httpclient.service.TasksdetailsService;
import com.sinosoft.httpclient.service.VehicleInvoiceService;
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
public class VehicleInvoiceController implements ScheduledOfTask{


    private Logger logger = LoggerFactory.getLogger(VehicleInvoiceController.class);

    @Resource
    VehicleInvoiceService vehicleInvoiceService;

    @Resource
    HttpClient httpClient;
    @Resource
    TasksdetailsService tasksdetailsService;

    /**
     * 第二个接口数据信息
     */
    @Override
    public void execute() {

        try {
            long start = System.currentTimeMillis();
            Long endTime = new Date().getTime();
            Tasksdetailsinfo tasksdetailsinfo = new Tasksdetailsinfo();
            tasksdetailsinfo.setBatch("Vehicle_Invoice");
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
                    List<VehicleInvoice> vehicleInvoices = JSONArray.parseArray(returnStr, VehicleInvoice.class);
                    // 这里需要把所有的业务机构 映射到 账务机构上
                    for(VehicleInvoice vehicleInvoice : vehicleInvoices){
                        String companyNo = vehicleInvoice.getCompanyNo();
                        if(SecretKey.FIRST_COMPANY_NO.equals(companyNo)){
                            vehicleInvoice.setCompanyNo(SecretKey.FIRST_BRANCH_CODE);
                        }else if(SecretKey.SECOND_COMPANY_NO.equals(companyNo)){
                            vehicleInvoice.setCompanyNo(SecretKey.SECOND_BRANCH_CODE);
                        }
                    }
                    //保存入库
                    str = vehicleInvoiceService.saveVehicleInvoiceList(vehicleInvoices,tasksdetailsinfo.getEndTime());
                }
                System.out.println("Vehicle_Invoice 接口调用耗时："+(System.currentTimeMillis()-start)+"ms");
                System.out.println("第"+(i+1)+"个接口调用完毕！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("当前异常结果为："+e);
        }

    }

}
