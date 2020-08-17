package com.sinosoft.httpclient.domain;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "bz_parts_verification")
public class PartsVerification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //  经销商 GSSN 号
    //批次
    private Integer batch;
    private String dealerNo;
    //  记账公司 GSSN 号
    private String companyNo;
    //  校验编号
    private String verifyNo;
    //  校验日期
    private String verifyDate;
    //  操作日期
    private String supplierNo;
    //  供应商编号
    private String supplierNoDescription;//发票校验集合
    //差异
    private BigDecimal variance;
    //配件采购总价
    private BigDecimal acceptTotalAmount;
    //  操作日期
    private String operationDate;


    //InvoiceVerifyDTO
    //  采购发票号
    private String purchaseInvoiceNo;
    //采购发票参考（财务人员 手工录入）
    private String purchaseInvoiceReference;
    //  发票金额(不含税)
    private BigDecimal purchaseInvoiceNet;
    //  增值税金额
    private BigDecimal purchaseVat;
    // 价税合计
    private BigDecimal totalAmount;

    //verifyDns
    //  发货单号
    private String dnNo;
    //  配件入库成本
    private BigDecimal acceptAmount;

    public String getPurchaseInvoiceNo() {
        return purchaseInvoiceNo;
    }

    public void setPurchaseInvoiceNo(String purchaseInvoiceNo) {
        this.purchaseInvoiceNo = purchaseInvoiceNo;
    }

    public String getPurchaseInvoiceReference() {
        return purchaseInvoiceReference;
    }

    public void setPurchaseInvoiceReference(String purchaseInvoiceReference) {
        this.purchaseInvoiceReference = purchaseInvoiceReference;
    }



    public String getDnNo() {
        return dnNo;
    }

    public void setDnNo(String dnNo) {
        this.dnNo = dnNo;
    }

    public BigDecimal getPurchaseInvoiceNet() {
        return purchaseInvoiceNet;
    }

    public void setPurchaseInvoiceNet(BigDecimal purchaseInvoiceNet) {
        this.purchaseInvoiceNet = purchaseInvoiceNet;
    }

    public BigDecimal getPurchaseVat() {
        return purchaseVat;
    }

    public void setPurchaseVat(BigDecimal purchaseVat) {
        this.purchaseVat = purchaseVat;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getAcceptAmount() {
        return acceptAmount;
    }

    public void setAcceptAmount(BigDecimal acceptAmount) {
        this.acceptAmount = acceptAmount;
    }


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

    public String getVerifyNo() {
        return verifyNo;
    }

    public void setVerifyNo(String verifyNo) {
        this.verifyNo = verifyNo;
    }

    public String getVerifyDate() {
        return verifyDate;
    }

    public void setVerifyDate(String verifyDate) {
        this.verifyDate = verifyDate;
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

    public BigDecimal getVariance() {
        return variance;
    }

    public void setVariance(BigDecimal variance) {
        this.variance = variance;
    }

    public BigDecimal getAcceptTotalAmount() {
        return acceptTotalAmount;
    }

    public void setAcceptTotalAmount(BigDecimal acceptTotalAmount) {
        this.acceptTotalAmount = acceptTotalAmount;
    }

    public String getOperationDate() {
        return operationDate;
    }

    public void setOperationDate(String operationDate) {
        this.operationDate = operationDate;
    }

    public Integer getBatch() {
        return batch;
    }

    public void setBatch(Integer batch) {
        this.batch = batch;
    }
}
