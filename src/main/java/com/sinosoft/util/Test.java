package com.sinosoft.util;

import com.sinosoft.domain.SpecialInfo;
import com.sinosoft.dto.VoucherDTO;
import com.sinosoft.dto.testxml.Computer;
import com.sinosoft.dto.testxml.User;
import com.sinosoft.repository.SpecialInfoRepository;
import com.sinosoft.service.SpecialInfoService;

import javax.annotation.Resource;
import java.util.*;

import static java.io.File.separator;

/**
 * @Auther: luodejun
 * @Date: 2020/5/6 21:42
 * @Description:
 */
public class Test {

    public static void main(String[] args) {
//        String str = "123,4565,,123,5";
//        String[] result = str.split(",");
//        for(int i = 0 ;i <result.length;i++){
//            System.out.println(result[i]);
//        }
//        String str1 = "123,4565,,,";
////        String[] result1 = str1.split(",");
////        System.out.println(result1.length);
////        for(int i = 0 ;i <result1.length;i++){
////            System.out.println("result1:"+result1[i]);
////        }
////        String str2 = "123,434,,656";
////        String[] result2 = str2.split(",");
////        System.out.println(result2.length);
////        for(int i = 0 ;i<result2.length ; i++){
////            System.out.println("result2:"+result2[i]);
////        }
//        String  str = "";
//        str = str + "3333,";
//        for(int i =0;i<2;i++){
//            str = message(str);
//            System.out.println(str);
//        }
//        List<VoucherDTO> list = new ArrayList<>();
//        for (int i = 0; i < 2; i++) {
//            VoucherDTO dto = new VoucherDTO();
//            dto.setVoucherNo("3"+i);
//            list.add(dto);
//        }
//        Map<String,Object> maps = new HashMap<>();
//        maps.put("resultMsg","123123123");
//        maps.put("list2",list);
//        List<VoucherDTO> list2 = (List<VoucherDTO>) maps.get("list2");
//        for (VoucherDTO msg : list2){
//            System.out.println(msg.getVoucherNo());
//        }
//        System.out.println(maps.get("resultMsg"));

//        User user = new User(1, "Steven", "@sun123", new Date(), 1000.0);
//        User user = new User();
//        user.setUserId(1);
//        user.setUserName("Steven");
//        user.setBirthday(new Date());
//        user.setPassword("@Sun123");
//        user.setMoney(1000.0);
//        System.out.println("---将对象转换成string类型的xml Start---");
//        // 将对象转换成string类型的xml
//        String str = XMLUtil.convertToXml(user);
//        // 输出
//        System.out.println(str);
//        System.out.println("---将对象转换成string类型的xml End---");
//        System.out.println();
//        System.out.println("---将String类型的xml转换成对象 Start---");
//        User userTest = (User) XMLUtil.convertXmlStrToObject(User.class, str);
//        System.out.println(userTest);
//        System.out.println("---将String类型的xml转换成对象 End---");

//        String path = "D:\\user.xml";
//        System.out.println("---将对象转换成File类型的xml Start---");
//        XMLUtil.convertToXml(user, path);
//        System.out.println("---将对象转换成File类型的xml End---");
//        System.out.println();
//        System.out.println("---将File类型的xml转换成对象 Start---");
//        User user2 = (User) XMLUtil.convertXmlFileToObject(User.class, path);
//        System.out.println(user2);
//        System.out.println("---将File类型的xml转换成对象 End---");

        System.out.println("______________________________");

//        User user = new User(1, "Steven", "@sun123", new Date(), 1000.0);
//        List<Computer> list = new ArrayList<Computer>();
//        list.add(new Computer("xxxMMeedd", "asus", new Date(), 4455.5));
//        list.add(new Computer("lenvoXx", "lenvo", new Date(), 4999));
//        user.setComputers(list);
//        String path = "D:\\user.xml";
//        System.out.println("---将对象转换成File类型的xml Start---");
//        XMLUtil.convertToXml(user, path);
//        System.out.println("---将对象转换成File类型的xml End---");
//        System.out.println();
//        System.out.println("---将File类型的xml转换成对象 Start---");
//        User user2 = (User) XMLUtil.convertXmlFileToObject(User.class, path);
//        System.out.println(user2);
//        System.out.println("---将File类型的xml转换成对象 End---");

        String str =  "SST_1011101000_20200602_00ed.xml";
        int i = str.indexOf("_");
        System.out.println(i);
        String substring = str.substring(0, i);
        System.out.println(substring);
        int j = str.lastIndexOf(".");
        System.out.println(j);
        String substring1 = str.substring(0, j);
        System.out.println(substring1);

    }
    public static String message(String string){
        string  = string + "123123,";
        return string;
    }


}
