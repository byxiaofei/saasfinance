package com.sinosoft.httpclient.dto;

import java.util.Date;
import java.util.List;

public class ServiceInvoiceDTO {
    private String id;
    //批次
    private String batch;
    //  记账公司 GSSN 号
    private String companyNo;
    //  经销商 GSSN 号
    private String dealerNo;
    // 账单类型:I-打印账单 C-退款单
    private String invoicePrintType;
    // 业务类型 W-工单
    private String bizType;
    //账单编号/退款编号
    private String invoiceNo;
    //账单/退款单日期
    private Date docDate;
    //客户名称
    private String customerName;
    //公司名称
    private String companyName;
    //品牌
    private String brand;
    //车型
    private String model;
    // 乘用车--PC/商务车--CV
    private String type;
    //车牌号
    private String registrationNo;
    //fin
    private String fin;
    //RWO 工单号码
    private String orderNumber;
    //Service parts invoice 集合
    private List<ServicePartsInvoiceDTO> invoiceServiceParts;
    //Service labors invoice 集合
    private List <ServiceLaborInvoiceDTO>invoiceServiceLabors;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDealerNo() {
        return dealerNo;
    }

    public void setDealerNo(String dealerNo) {
        this.dealerNo = dealerNo;
    }

    public String getCompanyNo() {
        return companyNo;
    }

    public void setCompanyNo(String companyNo) {
        this.companyNo = companyNo;
    }

    public String getInvoicePrintType() {
        return invoicePrintType;
    }

    public void setInvoicePrintType(String invoicePrintType) {
        this.invoicePrintType = invoicePrintType;
    }

    public String getBizType() {
        return bizType;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
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

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public List<ServicePartsInvoiceDTO> getInvoiceServiceParts() {
        return invoiceServiceParts;
    }

    public void setInvoiceServiceParts(List<ServicePartsInvoiceDTO> invoiceServiceParts) {
        this.invoiceServiceParts = invoiceServiceParts;
    }

    public List<ServiceLaborInvoiceDTO> getInvoiceServiceLabors() {
        return invoiceServiceLabors;
    }

    public void setInvoiceServiceLabors(List<ServiceLaborInvoiceDTO> invoiceServiceLabors) {
        this.invoiceServiceLabors = invoiceServiceLabors;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }
}
