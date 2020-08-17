package com.sinosoft.httpclient.dto;

import java.math.BigDecimal;

public class ServicePartsInvoiceDTO {

    //  项目行编号
    private String lineNumber;
    // 部门代码(行)
    private String departmentCode;
    // 零件编号
    private String partsNo;
    // 说明
    private String description;
    //账单编号/退款编号
    private String partsAnalysisCode;
    //数量
    private Integer quantity;
    //配件单位成本
    private BigDecimal partsUnitCost;
    //原厂标记
    private String partGenuineFlag;
    //销售类型
    private String salesType;
    //账单编号/退款编号
    private String customerTypeNo;
    //账单编号/退款编号
    private String customerTypeNoDescription;
    //销售单价
    private BigDecimal unitNetPrice;
    //销售总价
    private BigDecimal totalPrice;
    //数量
    private BigDecimal discountRate;
    //折扣百分比
    private BigDecimal discountAmount;
    //分担比例
    private BigDecimal contribution;
    //分担金额
    private BigDecimal contributionAmount;
    //净价
    private BigDecimal netPrice;
    //套餐标志
    private String packageMark;
    //套餐代码
    private String packageCode;
    //套餐内序号
    private Integer packageSequence;
    //增值税税率
    private BigDecimal vatRate;
    //增值税税额
    private BigDecimal vatAmount;


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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPartsUnitCost() {
        return partsUnitCost;
    }

    public void setPartsUnitCost(BigDecimal partsUnitCost) {
        this.partsUnitCost = partsUnitCost;
    }

    public String getPartGenuineFlag() {
        return partGenuineFlag;
    }

    public void setPartGenuineFlag(String partGenuineFlag) {
        this.partGenuineFlag = partGenuineFlag;
    }

    public String getSalesType() {
        return salesType;
    }

    public void setSalesType(String salesType) {
        this.salesType = salesType;
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

    public BigDecimal getUnitNetPrice() {
        return unitNetPrice;
    }

    public void setUnitNetPrice(BigDecimal unitNetPrice) {
        this.unitNetPrice = unitNetPrice;
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

    public Integer getPackageSequence() {
        return packageSequence;
    }

    public void setPackageSequence(Integer packageSequence) {
        this.packageSequence = packageSequence;
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
}
