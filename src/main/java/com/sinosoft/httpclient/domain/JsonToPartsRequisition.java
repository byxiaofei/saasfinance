package com.sinosoft.httpclient.domain;

import javax.persistence.Entity;
import java.util.List;


public class JsonToPartsRequisition {

    private int id;
    //  经销商 GSSN 号
    private String dealerNo;
    //  记账公司 GSSN 号
    private String companyNo;
    //  客户名称
    private String customerName;
    //  公司名称
    private String companyName;
    //  组装/客户唯一标识
    private String customerId;
    //  公司唯一标识
    private String companyId;
    //  fin
    private String fin;
    //  vin
    private String vin;
    //  车牌号
    private String registrationNo;
    //  文档类型
    private String docType;
    //  单号
    private String docNo;
    //  日期
    private String docDate;
    //  操作日期
    private String operationDate;
    //  工单号码
    private String orderNo;
    //  Parts 集合
    private List<RequisitionParts> requisitionParts;

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getFin() {
        return fin;
    }

    public void setFin(String fin) {
        this.fin = fin;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getRegistrationNo() {
        return registrationNo;
    }

    public void setRegistrationNo(String registrationNo) {
        this.registrationNo = registrationNo;
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

    public String getDocDate() {
        return docDate;
    }

    public void setDocDate(String docDate) {
        this.docDate = docDate;
    }

    public String getOperationDate() {
        return operationDate;
    }

    public void setOperationDate(String operationDate) {
        this.operationDate = operationDate;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public List<RequisitionParts> getRequisitionParts() {
        return requisitionParts;
    }

    public void setRequisitionParts(List<RequisitionParts> requisitionParts) {
        this.requisitionParts = requisitionParts;
    }

    @Override
    public String toString() {
        return "JsonToPartsRequisition{" +
                "id=" + id +
                ", dealerNo='" + dealerNo + '\'' +
                ", companyNo='" + companyNo + '\'' +
                ", customerName='" + customerName + '\'' +
                ", companyName='" + companyName + '\'' +
                ", customerId='" + customerId + '\'' +
                ", companyId='" + companyId + '\'' +
                ", fin='" + fin + '\'' +
                ", vin='" + vin + '\'' +
                ", registrationNo='" + registrationNo + '\'' +
                ", docType='" + docType + '\'' +
                ", docNo='" + docNo + '\'' +
                ", docDate='" + docDate + '\'' +
                ", operationDate='" + operationDate + '\'' +
                ", orderNo='" + orderNo + '\'' +
                ", requisitionParts=" + requisitionParts +
                '}';
    }
}
