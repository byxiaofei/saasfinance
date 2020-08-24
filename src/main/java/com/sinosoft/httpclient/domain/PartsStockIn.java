package com.sinosoft.httpclient.domain;

import java.math.BigDecimal;

public class PartsStockIn {

    //零件编号
    private String partsNo;

    //DN 号
    private String dnNo;

    //说明 长度200
    private String description;
    //原厂配件标记 长度 10
    private String genuineFlag;
    //parts分析代码
    private String partsAnalysisCode;
    //业务发生日期
    private String businessDate;
    // 采购订单编号
    private String poNo;
    //入库数量
    private Integer quantity;
    //配件单位成本
    private BigDecimal partsUnitCost;
    //供应商编号
    private String supplierNo;
    //供应商编号描述 长度200
    private String supplierDescription;


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

    public String getBusinessDate() {
        return businessDate;
    }

    public void setBusinessDate(String businessDate) {
        this.businessDate = businessDate;
    }

    public String getPoNo() {
        return poNo;
    }

    public void setPoNo(String poNo) {
        this.poNo = poNo;
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

    public String getSupplierNo() {
        return supplierNo;
    }

    public void setSupplierNo(String supplierNo) {
        this.supplierNo = supplierNo;
    }

    public String getSupplierDescription() {
        return supplierDescription;
    }

    public void setSupplierDescription(String supplierDescription) {
        this.supplierDescription = supplierDescription;
    }

    public String getDnNo() {
        return dnNo;
    }

    public void setDnNo(String dnNo) {
        this.dnNo = dnNo;
    }

    @Override
    public String toString() {
        return "PartsStockIn{" +
                "partsNo='" + partsNo + '\'' +
                ", description='" + description + '\'' +
                ", genuineFlag='" + genuineFlag + '\'' +
                ", partsAnalysisCode='" + partsAnalysisCode + '\'' +
                ", businessDate='" + businessDate + '\'' +
                ", poNo='" + poNo + '\'' +
                ", dnNo='" + dnNo + '\'' +
                ", quantity=" + quantity +
                ", partsUnitCost=" + partsUnitCost +
                ", supplierNo='" + supplierNo + '\'' +
                ", supplierDescription='" + supplierDescription + '\'' +
                '}';
    }
}
