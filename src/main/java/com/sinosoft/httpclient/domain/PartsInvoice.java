package com.sinosoft.httpclient.domain;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "bz_partsinvoice")
public class PartsInvoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
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

    //list数据
    //行号
    private String line;
    //零件编号
    private String partsNo;
    //摘要
    private String description;
    //parts分析代码
    private String partsAnalysisCode;
    //部门代码
    private String departmentCode;
    //数量
    private BigDecimal quantity;
    //配件单位成本
    private BigDecimal partsUnitCost;
    //单位售价
    private BigDecimal unitSellingPrice;
    //总销售价格
    private BigDecimal totalPrice;
    //折扣百分比
    private BigDecimal discountRate;
    //折扣金额
    private BigDecimal discountAmount;
    //分担比例
    private BigDecimal contribution;
    //净价（不含税）
    private BigDecimal netValue;
    //增值税税率
    private BigDecimal vatRate;
    //增值税税额
    private BigDecimal vatAmount;
    //客户类型编号
    private String customerTypeNo;

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

    public String getPartsAnalysisCode() {
        return partsAnalysisCode;
    }

    public void setPartsAnalysisCode(String partsAnalysisCode) {
        this.partsAnalysisCode = partsAnalysisCode;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
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

    public BigDecimal getUnitSellingPrice() {
        return unitSellingPrice;
    }

    public void setUnitSellingPrice(BigDecimal unitSellingPrice) {
        this.unitSellingPrice = unitSellingPrice;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(BigDecimal discountRate) {
        this.discountRate = discountRate;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getContribution() {
        return contribution;
    }

    public void setContribution(BigDecimal contribution) {
        this.contribution = contribution;
    }

    public BigDecimal getNetValue() {
        return netValue;
    }

    public void setNetValue(BigDecimal netValue) {
        this.netValue = netValue;
    }

    public BigDecimal getVatRate() {
        return vatRate;
    }

    public void setVatRate(BigDecimal vatRate) {
        this.vatRate = vatRate;
    }

    public BigDecimal getVatAmount() {
        return vatAmount;
    }

    public void setVatAmount(BigDecimal vatAmount) {
        this.vatAmount = vatAmount;
    }

    public String getCustomerTypeNo() {
        return customerTypeNo;
    }

    public void setCustomerTypeNo(String customerTypeNo) {
        this.customerTypeNo = customerTypeNo;
    }

    @Override
    public String toString() {
        return "PartsInvoice{" +
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
                ", line='" + line + '\'' +
                ", partsNo='" + partsNo + '\'' +
                ", description='" + description + '\'' +
                ", partsAnalysisCode='" + partsAnalysisCode + '\'' +
                ", departmentCode='" + departmentCode + '\'' +
                ", quantity=" + quantity +
                ", partsUnitCost=" + partsUnitCost +
                ", unitSellingPrice=" + unitSellingPrice +
                ", totalPrice=" + totalPrice +
                ", discountRate=" + discountRate +
                ", discountAmount=" + discountAmount +
                ", contribution=" + contribution +
                ", netValue=" + netValue +
                ", vatRate=" + vatRate +
                ", vatAmount=" + vatAmount +
                ", customerTypeNo='" + customerTypeNo + '\'' +
                '}';
    }
}
