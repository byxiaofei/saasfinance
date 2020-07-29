package com.sinosoft.dto.parsing;

import com.sinosoft.util.XMLUtil;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import org.apache.xmlbeans.impl.values.XmlUnionImpl;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.List;

/**
 * @Auther: luodejun
 * @Date: 2020/5/13 20:33
 * @Description:
 */
@XmlRootElement(name = "voucher")
@XmlAccessorType(XmlAccessType.FIELD)
public class XMLParsingDTO {

    @XmlElement(name = "voucher_header")
    public VoucherHeader voucherHeader;

    @XmlElement(name = "voucher_body")
    public VoucherBody voucherBody;

    @XmlTransient
    public VoucherHeader getVoucherHeader() {
        return voucherHeader;
    }

    public void setVoucherHeader(VoucherHeader voucherHeader) {
        this.voucherHeader = voucherHeader;
    }

    @XmlTransient
    public VoucherBody getVoucherBody() {
        return voucherBody;
    }

    public void setVoucherBody(VoucherBody voucherBody) {
        this.voucherBody = voucherBody;
    }

    public static void main(String[] args) {
        String reportXml ="<?xml version=\"1.0\" encoding=\"utf-8\"?> \n" +
                "<voucher loadbatch = \"20999999\"> \n" +
                    "<voucher_header>  \n" +
                        "<company>1001000000</company>  \n" +
                    "</voucher_header>  \n" +
                    "<voucher_body>  \n" +
                        "<entry>  \n" +
                            "<remarkName>01</remarkName> \n" +
                            "<subjectCode>11</subjectCode> \n" +
                            "<subjectName></subjectName>  \n" +
                        "</entry>  \n" +
                    "</voucher_body>  \n" +
                " </voucher> \n" ;
        Object o = XMLUtil.convertXmlStrToObject(XMLParsingDTO.class, reportXml);
        XMLParsingDTO msg = (XMLParsingDTO) o;
        VoucherHeader voucherHeader = msg.getVoucherHeader();
        System.out.println(voucherHeader.getCompany());
        List<Entry> entry = msg.voucherBody.getEntry();
        for(Entry entry1 : entry){
            System.out.println(entry1.getRemarkName());
            System.out.println(entry1.getSubjectCode());
            System.out.println(entry1.getSubjectName());
        }
        System.out.println(msg);

        System.out.println("----------------------------------");
        String s = XMLUtil.convertToXml(msg);

        System.out.println(s);
    }


}
