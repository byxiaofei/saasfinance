package com.sinosoft.httpclient.domain;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "bz_partsrequisition")
public class PartsRequisition {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    //  经销商 GSSN 号
    private String dealerNo;
    //  记账公司 GSSN 号
    private String companyNo;
    //  客户名称
    private String customerName;
    //  公司名称
    private String companyName;
    //  客户唯一标识
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
    // 日期
    private String docDate;
    //  操作日期
    private String operationDate;
    //  工单号码
    private String orderNo;
    //  行号
    private String line;
    //  零件编号
    private String partsNo;
    //  说明
    private String description;
    //  原厂配件标记
    private String genuineFlag;
    //  Parts 分析代码
    private String partsAnalysisCode;
    //  数量
    private BigDecimal quantity;
    //  配件单位成本
    private BigDecimal partsUnitCost;
    //  ICT 参考号
    private String ictOrder;
    //  需求方经销商
    private String ictCompany;

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

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getPartsNo() {
        return partsNo;
    }

    public void setPartsNo(String partsNo) {
        this.partsNo = partsNo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGenuineFlag() {
        return genuineFlag;
    }

    public void setGenuineFlag(String genuineFlag) {
        this.genuineFlag = genuineFlag;
    }

    public String getPartsAnalysisCode() {
        return partsAnalysisCode;
    }

    public void setPartsAnalysisCode(String partsAnalysisCode) {
        this.partsAnalysisCode = partsAnalysisCode;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPartsUnitCost() {
        return partsUnitCost;
    }

    public void setPartsUnitCost(BigDecimal partsUnitCost) {
        this.partsUnitCost = partsUnitCost;
    }

    public String getIctOrder() {
        return ictOrder;
    }

    public void setIctOrder(String ictOrder) {
        this.ictOrder = ictOrder;
    }

    public String getIctCompany() {
        return ictCompany;
    }

    public void setIctCompany(String ictCompany) {
        this.ictCompany = ictCompany;
    }

    @Override
    public String toString() {
        return "PartsRequisition{" +
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
                ", line='" + line + '\'' +
                ", partsNo='" + partsNo + '\'' +
                ", description='" + description + '\'' +
                ", genuineFlag='" + genuineFlag + '\'' +
                ", partsAnalysisCode='" + partsAnalysisCode + '\'' +
                ", quantity=" + quantity +
                ", partsUnitCost=" + partsUnitCost +
                ", ictOrder='" + ictOrder + '\'' +
                ", ictCompany='" + ictCompany + '\'' +
                '}';
    }
}
