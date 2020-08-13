package com.sinosoft.httpclient.dto;

import java.math.BigDecimal;

/**
 * 发票校验集合
 */
public class InvoiceVerifyDTO {

    private String purchaseInvoiceNo;
    //  记账公司 GSSN 号
    private String purchaseInvoiceReference;
    //  校验编号
    private BigDecimal purchaseInvoiceNet;
    //  校验日期
    private BigDecimal purchaseVat;
    //  操作日期
    private BigDecimal totalAmount;



    public String getPurchaseInvoiceNo() {
        return purchaseInvoiceNo;
    }

    public void setPurchaseInvoiceNo(String purchaseInvoiceNo) {
        this.purchaseInvoiceNo = purchaseInvoiceNo;
    }

    public String getPurchaseInvoiceReference() {
        return purchaseInvoiceReference;
    }

    public void setPurchaseInvoiceReference(String purchaseInvoiceReference) {
        this.purchaseInvoiceReference = purchaseInvoiceReference;
    }

    public BigDecimal getPurchaseInvoiceNet() {
        return purchaseInvoiceNet;
    }

    public void setPurchaseInvoiceNet(BigDecimal purchaseInvoiceNet) {
        this.purchaseInvoiceNet = purchaseInvoiceNet;
    }

    public BigDecimal getPurchaseVat() {
        return purchaseVat;
    }

    public void setPurchaseVat(BigDecimal purchaseVat) {
        this.purchaseVat = purchaseVat;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

}
