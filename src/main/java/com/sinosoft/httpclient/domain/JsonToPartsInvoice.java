package com.sinosoft.httpclient.domain;

import java.util.Date;
import java.util.List;

public class JsonToPartsInvoice {
    //交易数据唯一标识
    private long id;
    //记账公司 GSSN 号
    private String companyNo;
    //经销商 GSSN 号
    private String dealerNo;
    //Parts - 零件外销工单
    private String bizType;
    //I - 打印账单
    //C - 退款单
    private String docType;
    //账单编号/退款编号
    private String docNo;
    //账单/退款单日期
    private Date docDate;
    //客户名称
    private String customerName;
    //公司名称
    private String companyName;
    //客户唯一标识
    private String customerId;
    //公司唯一标识
    private String companyId;
    //品牌
    private String franchise;
    //车型
    private String model;
    //车牌号
    private String registrationNo;
    //fin
    private String fin;
    //工单号码
    private String orderNo;
    //操作日期
    private Date operationDate;
    //Parts invoice集合
    private List<PartsInvoiceIn> invoiceParts;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCompanyNo() {
        return companyNo;
    }

    public void setCompanyNo(String companyNo) {
        this.companyNo = companyNo;
    }

    public String getDealerNo() {
        return dealerNo;
    }

    public void setDealerNo(String dealerNo) {
        this.dealerNo = dealerNo;
    }

    public String getBizType() {
        return bizType;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getDocNo() {
        return docNo;
    }

    public void setDocNo(String docNo) {
        this.docNo = docNo;
    }

    public Date getDocDate() {
        return docDate;
    }

    public void setDocDate(Date docDate) {
        this.docDate = docDate;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getFranchise() {
        return franchise;
    }

    public void setFranchise(String franchise) {
        this.franchise = franchise;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getRegistrationNo() {
        return registrationNo;
    }

    public void setRegistrationNo(String registrationNo) {
        this.registrationNo = registrationNo;
    }

    public String getFin() {
        return fin;
    }

    public void setFin(String fin) {
        this.fin = fin;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Date getOperationDate() {
        return operationDate;
    }

    public void setOperationDate(Date operationDate) {
        this.operationDate = operationDate;
    }

    public List<PartsInvoiceIn> getInvoiceParts() {
        return invoiceParts;
    }

    public void setInvoiceParts(List<PartsInvoiceIn> invoiceParts) {
        this.invoiceParts = invoiceParts;
    }

    @Override
    public String toString() {
        return "JsonToPartsInvoice{" +
                "id=" + id +
                ", companyNo='" + companyNo + '\'' +
                ", dealerNo='" + dealerNo + '\'' +
                ", bizType='" + bizType + '\'' +
                ", docType='" + docType + '\'' +
                ", docNo='" + docNo + '\'' +
                ", docDate=" + docDate +
                ", customerName='" + customerName + '\'' +
                ", companyName='" + companyName + '\'' +
                ", customerId='" + customerId + '\'' +
                ", companyId='" + companyId + '\'' +
                ", franchise='" + franchise + '\'' +
                ", model='" + model + '\'' +
                ", registrationNo='" + registrationNo + '\'' +
                ", fin='" + fin + '\'' +
                ", orderNo='" + orderNo + '\'' +
                ", operationDate=" + operationDate +
                ", invoiceParts=" + invoiceParts +
                '}';
    }
}
