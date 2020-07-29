package com.sinosoft.dto.parsing;

import com.sinosoft.util.XMLUtil;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: luodejun
 * @Date: 2020/5/14 09:49
 * @Description:
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "voucher")
@XmlType(propOrder = {"company"})
public class RootElement implements Serializable {

    private  List<Company> company;

    public List<Company> getCompany() {
        return company;
    }

    public void setCompany(List<Company> company) {
        this.company = company;
    }

    @Override
    public String toString() {
        return "RootElement{" +
                "company=" + company +
                '}';
    }

    public static void main(String[] args) {
//        String xml  ="<?xml version=\"1.0\" encoding=\"utf-8\"?> \n" +
//                "<voucher> \n" +
//                    "<company> \n" +
//                        "<id> 12 </id> \n" +
//                        "<info> 李承天 </info> \n" +
//                        "<phone>123123123</phone> \n" +
//                    "</company>" +
//                "<company> \n" +
//                    "<id> 13 </id> \n" +
//                    "<info> 李天羽 </info> \n" +
//                    "<phone>444444</phone> \n" +
//                "</company>" +
//                "<company> \n" +
//                    "<id> 12 </id> \n" +
//                    "<info> 李狗蛋 </info> \n" +
//                    "<phone>555555</phone> \n" +
//                "</company>" +
//                "</voucher>";
//
//        Object o = XMLUtil.convertXmlStrToObject(RootElement.class, xml);
//        RootElement msg = (RootElement) o ;
//        List<Company> company = msg.company;
//        System.out.println(company.size());
//        for(Company listMsg : company){
//            System.out.println(listMsg.getId());
//            System.out.println(listMsg.getInfo());
//            System.out.println(listMsg.getPhoen());
//        }
//        System.out.println(msg.company);
        Company company2 = new Company();
        company2.setId("1");
        company2.setInfo("12312");
        company2.setPhoen("131231231231231");
        Company company = new Company();
        company.setPhoen("32323434343");
        company.setInfo("5454545");
        company.setId("3");
        RootElement rootElements = new RootElement();
        List<Company> list = new ArrayList<>();
        list.add(company);
        list.add(company2);
        rootElements.setCompany(list);
        String path = "E:\\test\\xml文件.xml";
        XMLUtil.convertToXml(rootElements,path);

    }
}
