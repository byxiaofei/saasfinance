package com.sinosoft.httpclient.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 获取上次调用时间到当前时间，所有配件发票校验的数据
 */

public class PartsVerificationDTO {

    private Integer id;
    //
    private Integer batch;
    //  经销商 GSSN 号
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
    private String supplierNoDescription;
    //发票校验集合
    private List<InvoiceVerifyDTO> verifyInvoices;
    //DN 校验集合
    private List<DnVerifyDTO> verifyDns;
    //差异
    private BigDecimal variance;
    //配件采购总价
    private BigDecimal acceptTotalAmount;
    //  操作日期
    private String operationDate;
    //发票金额(不含税)
    private BigDecimal purchaseInvoiceNet;
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

    public List<InvoiceVerifyDTO> getVerifyInvoices() {
        return verifyInvoices;
    }

    public void setVerifyInvoices(List<InvoiceVerifyDTO> verifyInvoices) {
        this.verifyInvoices = verifyInvoices;
    }

    public List<DnVerifyDTO> getVerifyDns() {
        return verifyDns;
    }

    public void setVerifyDns(List<DnVerifyDTO> verifyDns) {
        this.verifyDns = verifyDns;
    }

    public Integer getBatch() {
        return batch;
    }

    public void setBatch(Integer batch) {
        this.batch = batch;
    }

    public BigDecimal getPurchaseInvoiceNet() {
        return purchaseInvoiceNet;
    }

    public void setPurchaseInvoiceNet(BigDecimal purchaseInvoiceNet) {
        this.purchaseInvoiceNet = purchaseInvoiceNet;
    }
}
