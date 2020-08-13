package com.sinosoft.httpclient.dto;

import java.math.BigDecimal;

public class ServiceLaborInvoiceDTO {

    //  项目行编号
    private String lineNumber;
    // 部门代码(行)
    private String departmentCode;
    // 工时代码
    private String laborCode;
    // 说明
    private String description;
    //工时率代码 S-外加工
    private String laborRateCode;
    //标准时间
    private String standardHour;
    //零售工时率
    private BigDecimal laborRetailRate;
    //销售类型
    private String salesType;
    //销售总价
    private BigDecimal totalPrice;
    //折扣百分比
    private BigDecimal discountRate;
    //折扣金额
    private BigDecimal discountAmount;
    //分担比例
    private BigDecimal contribution;
    //分担金额
    private BigDecimal contributionAmount;
    //净价
    private BigDecimal netPrice;
    //套餐标记
    private String packageMark;
    //套餐代码
    private String packageCode;
    //套餐内序号
    private BigDecimal packageSequence;
    //供应商编号
    private String supplierNo;
    //供应商编号描 述
    private String supplierNoDescription;
    //sublet 预估成 本
    private BigDecimal subletEstimateCost;
    //增值税税率
    private BigDecimal vatRate;
    //增值税税额
    private BigDecimal  vatAmount;
    // 客户类型编号
    private String customerTypeNo;
    //客户类型编号 描述
    private String customerTypeNoDescription;

    public String getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(String lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public String getLaborCode() {
        return laborCode;
    }

    public void setLaborCode(String laborCode) {
        this.laborCode = laborCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLaborRateCode() {
        return laborRateCode;
    }

    public void setLaborRateCode(String laborRateCode) {
        this.laborRateCode = laborRateCode;
    }

    public String getStandardHour() {
        return standardHour;
    }

    public void setStandardHour(String standardHour) {
        this.standardHour = standardHour;
    }

    public BigDecimal getLaborRetailRate() {
        return laborRetailRate;
    }

    public void setLaborRetailRate(BigDecimal laborRetailRate) {
        this.laborRetailRate = laborRetailRate;
    }

    public String getSalesType() {
        return salesType;
    }

    public void setSalesType(String salesType) {
        this.salesType = salesType;
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

    public BigDecimal getContributionAmount() {
        return contributionAmount;
    }

    public void setContributionAmount(BigDecimal contributionAmount) {
        this.contributionAmount = contributionAmount;
    }

    public BigDecimal getNetPrice() {
        return netPrice;
    }

    public void setNetPrice(BigDecimal netPrice) {
        this.netPrice = netPrice;
    }

    public String getPackageMark() {
        return packageMark;
    }

    public void setPackageMark(String packageMark) {
        this.packageMark = packageMark;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public BigDecimal getPackageSequence() {
        return packageSequence;
    }

    public void setPackageSequence(BigDecimal packageSequence) {
        this.packageSequence = packageSequence;
    }

    public String getSupplierNo() {
        return supplierNo;
    }

    public void setSupplierNo(String supplierNo) {
        this.supplierNo = supplierNo;
    }

    public String getSupplierNoDescription() {
        return supplierNoDescription;
    }

    public void setSupplierNoDescription(String supplierNoDescription) {
        this.supplierNoDescription = supplierNoDescription;
    }

    public BigDecimal getSubletEstimateCost() {
        return subletEstimateCost;
    }

    public void setSubletEstimateCost(BigDecimal subletEstimateCost) {
        this.subletEstimateCost = subletEstimateCost;
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

    public String getCustomerTypeNoDescription() {
        return customerTypeNoDescription;
    }

    public void setCustomerTypeNoDescription(String customerTypeNoDescription) {
        this.customerTypeNoDescription = customerTypeNoDescription;
    }
}
