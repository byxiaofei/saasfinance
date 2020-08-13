package com.sinosoft.httpclient.controller;

import com.alibaba.fastjson.JSONArray;
import com.sinosoft.httpclient.dto.PartsVerificationDTO;
import com.sinosoft.httpclient.dto.ServiceInvoiceDTO;
import com.sinosoft.httpclient.service.HttpClient;
import com.sinosoft.httpclient.service.ServiceInvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/serviceInvoice")
public class ServiceInvoiceController {

    @Autowired
    ServiceInvoiceService serviceInvoiceService;
    @Autowired
    HttpClient httpClient;
    @RequestMapping("/get_service_invoice")
    public  void getServiceInvoiceService(){

        String url = "https://otrplus-cn-test.api.mercedes-benz.com.cn/api/accounting/service-invoice";

        // 添加参数
        Map<String, Long> uriMap = new HashMap<>(6);
        Long startTime = new Date().getTime();   //开始时间需要传参
        Long endTime = new Date().getTime();

        uriMap.put("startTime",Long.parseLong("1595241003220"));
        uriMap.put("endTime",Long.parseLong("1597039093220"));

        String returnStr = httpClient.sendGet(url,uriMap);
        System.out.println(returnStr );
        String str=null  ;
        if(returnStr.equals("接口调用失败")){
            str = "接口调用失败"; // TODO 循环请求或者 其他原因导致请求失败，具体原因分析
        }else{
            List<ServiceInvoiceDTO> serviceInvoiceDTOList = JSONArray.parseArray(returnStr, ServiceInvoiceDTO.class);
            //保存入库
            System.out.println(serviceInvoiceDTOList);
            str =  serviceInvoiceService.getServiceInvoiceService(serviceInvoiceDTOList);
        }

    }
    //http://192.168.43.139:8081/serviceInvoice/get_service_invoice

}
