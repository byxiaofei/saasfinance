package com.sinosoft.httpclient.domain;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "bz_warrantyconfirm")
public class WarrantyConfirm {
    //交易数据唯一标识
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //经销商 GSSN 号
    private String dealerNo;
    //记账公司 GSSN 号
    private String companyNo;
    //claim编号-OTR
    private String claimNoOfOtr;
    //claim唯一标识
    private String claimId;
    //账单编号
    private String invoiceNo;
    //账单日期
    private Date invoiceDate;
    //确认日期
    private Date confirmDate;
    //客户类型编号
    private String customerTypeNo;
    //客户类型编号描述
    private String customerTypeNoDescription;
    //RWO工单号
    private String rwoNo;
    //客户名称
    private String customerName;
    //公司名称
    private String companyName;
    //车牌号
    private String registrationNo;
    //欧版底盘号
    private String fin;
    //系统账单总计（不含税）
    private BigDecimal invoiceTotal;
    //系统零件总计
    private BigDecimal partsTotal;
    //系统工时总计
    private BigDecimal laborTotal;
    //系统杂项总计
    private BigDecimal sundriesTotal;
    //系统手续费总计
    private BigDecimal handlingFeeTotal;

    //list集合
    //Credit note号
    private Integer creditNoteNo;
    //Credit note日期
    private Date creditNoteDate;
    //赔付的零件金额（不含税）
    private BigDecimal compensateParts;
    //赔付的工时金额（不含税）
    private BigDecimal compensateLabor;
    //赔付的杂项金额（不含税）
    private BigDecimal compensateSundries;
    //赔付的手续金额（不含税）
    private BigDecimal compensateHandlingFee;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public String getClaimNoOfOtr() {
        return claimNoOfOtr;
    }

    public void setClaimNoOfOtr(String claimNoOfOtr) {
        this.claimNoOfOtr = claimNoOfOtr;
    }

    public String getClaimId() {
        return claimId;
    }

    public void setClaimId(String claimId) {
        this.claimId = claimId;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public Date getConfirmDate() {
        return confirmDate;
    }

    public void setConfirmDate(Date confirmDate) {
        this.confirmDate = confirmDate;
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

    public String getRwoNo() {
        return rwoNo;
    }

    public void setRwoNo(String rwoNo) {
        this.rwoNo = rwoNo;
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

    public BigDecimal getInvoiceTotal() {
        return invoiceTotal;
    }

    public void setInvoiceTotal(BigDecimal invoiceTotal) {
        this.invoiceTotal = invoiceTotal;
    }

    public BigDecimal getPartsTotal() {
        return partsTotal;
    }

    public void setPartsTotal(BigDecimal partsTotal) {
        this.partsTotal = partsTotal;
    }

    public BigDecimal getLaborTotal() {
        return laborTotal;
    }

    public void setLaborTotal(BigDecimal laborTotal) {
        this.laborTotal = laborTotal;
    }

    public BigDecimal getSundriesTotal() {
        return sundriesTotal;
    }

    public void setSundriesTotal(BigDecimal sundriesTotal) {
        this.sundriesTotal = sundriesTotal;
    }

    public BigDecimal getHandlingFeeTotal() {
        return handlingFeeTotal;
    }

    public void setHandlingFeeTotal(BigDecimal handlingFeeTotal) {
        this.handlingFeeTotal = handlingFeeTotal;
    }

    public Integer getCreditNoteNo() {
        return creditNoteNo;
    }

    public void setCreditNoteNo(Integer creditNoteNo) {
        this.creditNoteNo = creditNoteNo;
    }

    public Date getCreditNoteDate() {
        return creditNoteDate;
    }

    public void setCreditNoteDate(Date creditNoteDate) {
        this.creditNoteDate = creditNoteDate;
    }

    public BigDecimal getCompensateParts() {
        return compensateParts;
    }

    public void setCompensateParts(BigDecimal compensateParts) {
        this.compensateParts = compensateParts;
    }

    public BigDecimal getCompensateLabor() {
        return compensateLabor;
    }

    public void setCompensateLabor(BigDecimal compensateLabor) {
        this.compensateLabor = compensateLabor;
    }

    public BigDecimal getCompensateSundries() {
        return compensateSundries;
    }

    public void setCompensateSundries(BigDecimal compensateSundries) {
        this.compensateSundries = compensateSundries;
    }

    public BigDecimal getCompensateHandlingFee() {
        return compensateHandlingFee;
    }

    public void setCompensateHandlingFee(BigDecimal compensateHandlingFee) {
        this.compensateHandlingFee = compensateHandlingFee;
    }

    @Override
    public String toString() {
        return "WarrantyConfirm{" +
                "id=" + id +
                ", dealerNo='" + dealerNo + '\'' +
                ", companyNo='" + companyNo + '\'' +
                ", claimNoOfOtr='" + claimNoOfOtr + '\'' +
                ", claimId='" + claimId + '\'' +
                ", invoiceNo='" + invoiceNo + '\'' +
                ", invoiceDate=" + invoiceDate +
                ", confirmDate=" + confirmDate +
                ", customerTypeNo='" + customerTypeNo + '\'' +
                ", customerTypeNoDescription='" + customerTypeNoDescription + '\'' +
                ", rwoNo='" + rwoNo + '\'' +
                ", customerName='" + customerName + '\'' +
                ", companyName='" + companyName + '\'' +
                ", registrationNo='" + registrationNo + '\'' +
                ", fin='" + fin + '\'' +
                ", invoiceTotal=" + invoiceTotal +
                ", partsTotal=" + partsTotal +
                ", laborTotal=" + laborTotal +
                ", sundriesTotal=" + sundriesTotal +
                ", handlingFeeTotal=" + handlingFeeTotal +
                ", creditNoteNo=" + creditNoteNo +
                ", creditNoteDate=" + creditNoteDate +
                ", compensateParts=" + compensateParts +
                ", compensateLabor=" + compensateLabor +
                ", compensateSundries=" + compensateSundries +
                ", compensateHandlingFee=" + compensateHandlingFee +
                '}';
    }
}
