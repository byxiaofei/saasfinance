package com.sinosoft.httpclient.domain;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

public class PartsInvoiceIn {
    //行号
    private String line;
    //零件编号
    private String partsNo;
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
        return "PartsInvoiceIn{" +
                "line='" + line + '\'' +
                ", partsNo='" + partsNo + '\'' +
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
