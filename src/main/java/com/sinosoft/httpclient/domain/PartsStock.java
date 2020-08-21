package com.sinosoft.httpclient.domain;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "bz_partsstock")
public class PartsStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private  Integer id;
    //经销商 GSSN 号
    private String dealerNo;
    //记账公司 GSSN 号
    private String companyNo;
    //业务类型
    private String transactionType;
    //操作日期
    private String operationDate;

    //LIST 数据
    //零件编号
    private String partsNo;
    //说明 长度200
    private String description;
    //原厂配件标记 长度 10
    private String genuineFlag;
    //parts 分析代码
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


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getOperationDate() {
        return operationDate;
    }

    public void setOperationDate(String operationDate) {
        this.operationDate = operationDate;
    }




    @Override
    public String toString() {
        return "PartsStock{" +
                "id=" + id +
                ", dealerNo='" + dealerNo + '\'' +
                ", companyNo='" + companyNo + '\'' +
                ", transactionType='" + transactionType + '\'' +
                ", operationDate='" + operationDate + '\'' +
                ", partsNo='" + partsNo + '\'' +
                ", description='" + description + '\'' +
                ", genuineFlag='" + genuineFlag + '\'' +
                ", partsAnalysisCode='" + partsAnalysisCode + '\'' +
                ", businessDate='" + businessDate + '\'' +
                ", poNo='" + poNo + '\'' +
                ", quantity=" + quantity +
                ", partsUnitCost=" + partsUnitCost +
                ", supplierNo='" + supplierNo + '\'' +
                ", supplierDescription='" + supplierDescription + '\'' +
                '}';
    }

}

