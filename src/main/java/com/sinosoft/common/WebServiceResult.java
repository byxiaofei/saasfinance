package com.sinosoft.common;

import com.sinosoft.util.XMLUtil;
import com.sun.xml.internal.ws.util.xml.XmlUtil;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * @Auther: luodejun
 * @Date: 2020/5/21 17:08
 * @Description:
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ResultMessage")
@XmlType(propOrder = {"status","voucherNo","returnMessage"})
public class WebServiceResult implements Serializable {

    // 返回信息。
    private String returnMessage;

    //  凭证号
    private String voucherNo;

    // 返回的状态“success”“fail”
    private String status;

    public String getReturnMessage() {
        return returnMessage;
    }

    public void setReturnMessage(String returnMessage) {
        this.returnMessage = returnMessage;
    }

    public String getVoucherNo() {
        return voucherNo;
    }

    public void setVoucherNo(String voucherNo) {
        this.voucherNo = voucherNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "WebServiceResult{" +
                "returnMessage='" + returnMessage + '\'' +
                ", voucherNo='" + voucherNo + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    // 如果有错误，直接把信息扔出去，并给fail状态
    public static WebServiceResult failure(String message ){
        WebServiceResult result = new WebServiceResult();
        result.setStatus("fail");
        result.returnMessage = message;
        return result;
    }

    public static WebServiceResult success(String voucherNo,String returnMessage){
        WebServiceResult result = new WebServiceResult();
        result.setStatus("success");
        result.voucherNo = voucherNo;
        result.returnMessage = returnMessage;
        return result;
    }

    public static WebServiceResult success(String voucherNo){
        WebServiceResult result = new WebServiceResult();
        result.setStatus("success");
        result.voucherNo = voucherNo;
        result.setReturnMessage("凭证保存成功");
        return result;
    }

    public static void main(String[] args) {
//        WebServiceResult result = new WebServiceResult();
//        result.setReturnMessage("成功");
//        result.setStatus("success");
//        result.setVoucherNo("123123213123123123123");
        WebServiceResult result = WebServiceResult.failure("错误信息我就不一一详述了！");
        String msg = XMLUtil.convertToXml(result);
        System.out.println(msg);

    }
}
