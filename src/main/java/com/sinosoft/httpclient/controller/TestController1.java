package com.sinosoft.httpclient.controller;

import com.alibaba.fastjson.JSONArray;
import com.sinosoft.httpclient.domain.ConfigureManage;
import com.sinosoft.httpclient.domain.ConfigureManageId;
import com.sinosoft.httpclient.domain.VehicleInvoice;
import com.sinosoft.httpclient.repository.ConfigureManageRespository;
import com.sinosoft.httpclient.service.VehicleInvoiceService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "/test")
public class TestController1 {

    @Resource
    private ConfigureManageRespository configureManageRespository;

    @Resource
    private VehicleInvoiceService vehicleInvoiceService;

    @RequestMapping("/1")
    public void testDomainInfo(){
        List<ConfigureManage> configureManages = configureManageRespository.queryConfigureManageByInterfaceInfo("2");
        for(ConfigureManage configureManage : configureManages){
            ConfigureManageId id = configureManage.getId();
            System.out.println("这里是：查询出的数据库中主键的信息展示："+id.toString());
            System.out.println("分割线----------------------------------");
            System.out.println("这里展示:ConfigureManage的信息："+configureManage.toString());
            System.out.println("-----------------上述完成一条信息的展示-------------------------------");
        }
    }



    @RequestMapping("/second")
    public String saveSecondInterfaceInfo(){
        String returnMessage = "[\n" +
                "    {\n" +
                "        \"id\": 144041,\n" +
                "        \"dealerNo\": \"GS0036160\",\n" +
                "        \"companyNo\": \"1011101000\",\n" +
                "        \"invoiceType\": \"Credit\",\n" +
                "        \"invoiceNo\": \"20200727NC0002\",\n" +
                "        \"originInvoiceNo\": \"20200727NI0004\",\n" +
                "        \"commissionNo\": \"0788657173\",\n" +
                "        \"vin\": \"WDDSJ4DB0JN626327\",\n" +
                "        \"fin\": \"WDD1173431N626327\",\n" +
                "        \"baumuster\": \"1173431\",\n" +
                "        \"nst\": \"CN8\",\n" +
                "        \"brand\": \"MB\",\n" +
                "        \"origin\": \"CBU\",\n" +
                "        \"model\": \"CLA200\",\n" +
                "        \"typeClass\": \"CLA\",\n" +
                "        \"engineNo\": \"27091031551972\",\n" +
                "        \"bookInStatus\": \"BOOKED_IN\",\n" +
                "        \"bookInDate\": \"2018-03-28\",\n" +
                "        \"description\": \"CLA 200 动感型\",\n" +
                "        \"orderId\": \"O200727551546\",\n" +
                "        \"salesType\": \"R\",\n" +
                "        \"customerName\": \"七月十五生产问题\",\n" +
                "        \"companyName\": null,\n" +
                "        \"customerId\": \"1x0Kt8LYTw2mILgVSPvrHA\",\n" +
                "        \"companyId\": null,\n" +
                "        \"vehiclePriceWithoutConsumtionTax\": 2976505.60,\n" +
                "        \"vehiclePrice\": 3265486.73,\n" +
                "        \"consumptionTax\": 288981.13,\n" +
                "        \"vatTax\": 375675.47,\n" +
                "        \"vehicleCost\": 218879.32,\n" +
                "        \"deposit\": 10000.00,\n" +
                "        \"salesLocation\": null,\n" +
                "        \"retailInvoiceDate\": null,\n" +
                "        \"creditDate\": \"2020-07-27\",\n" +
                "        \"operationDate\": \"2020-07-27\"\n" +
                "    }\n" +
                "]";
        List<VehicleInvoice> vehicleInvoiceList = JSONArray.parseArray(returnMessage,VehicleInvoice.class);
        System.out.println(vehicleInvoiceList);
        // 保存入库
        String message = vehicleInvoiceService.saveVehicleInvoiceList(vehicleInvoiceList);
        System.out.println(message);
        return message;
    }


    public static void main(String[] args) {
//        String  strMsg = "123123123123123123131";
//        String[] strings  = new String[4];
//        strings[0] = "123";
//        strings[1] = "111";
//        strings[2] = "222";
//        strings[3] = "3333";
//        System.out.println(strings.length);
//        System.out.println(strings[0]+strings[1]+strings[2]+strings[3]);
//
//        for(int i = 0 ; i<strings.length ; i++){
//            System.out.println(strings[i]);
//        }
//
//        System.out.println("String类型的字符串长度为"+strMsg.length());

        BigDecimal bigDecimal = new BigDecimal("14412.5351");
        BigDecimal bigDecimal1 = bigDecimal.setScale(2, RoundingMode.HALF_UP);
        System.out.println("当前为四位小数："+bigDecimal);
        System.out.println("当前为四舍五入小数:"+bigDecimal1.toString());

        try {
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String format = sdf.format(date);
            System.out.println(format);
            long time = date.getTime();
            System.out.println(time);

            String yearMonth = "2020-08-14 00:00:00";
            String yearMonthLast = "2020-08-10 23:59:59";
            Date parse = sdf.parse(yearMonth);
            Date parse1 = sdf.parse(yearMonthLast);
            System.out.println(parse.getTime());
            System.out.println(parse1.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
