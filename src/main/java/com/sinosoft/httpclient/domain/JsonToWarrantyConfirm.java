package com.sinosoft.httpclient.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class JsonToWarrantyConfirm {
    //��������Ψһ��ʶ
    private Long id;
    //������ GSSN ��
    private String dealerNo;
    //���˹�˾ GSSN ��
    private String companyNo;
    //claim���-OTR
    private String claimNoOfOtr;
    //claimΨһ��ʶ
    private String claimId;
    //�˵����
    private String invoiceNo;
    //�˵�����
    private Date invoiceDate;
    //ȷ������
    private Date confirmDate;
    //�ͻ����ͱ��
    private String customerTypeNo;
    //�ͻ����ͱ������
    private String customerTypeNoDescription;
    //RWO������
    private String rwoNo;
    //�ͻ�����
    private String customerName;
    //��˾����
    private String companyName;
    //���ƺ�
    private String registrationNo;
    //ŷ����̺�
    private String fin;
    //ϵͳ�˵��ܼƣ�����˰��
    private BigDecimal invoiceTotal;
    //ϵͳ����ܼ�
    private BigDecimal partsTotal;
    //ϵͳ��ʱ�ܼ�
    private BigDecimal laborTotal;
    //ϵͳ�����ܼ�
    private BigDecimal sundriesTotal;
    //ϵͳ�������ܼ�
    private BigDecimal handlingFeeTotal;
    //Warranty creditnote ����
    private List<WarrantyCreditNote> warrantyCreditNotes;

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

    public List<WarrantyCreditNote> getWarrantyCreditNotes() {
        return warrantyCreditNotes;
    }

    public void setWarrantyCreditNotes(List<WarrantyCreditNote> warrantyCreditNotes) {
        this.warrantyCreditNotes = warrantyCreditNotes;
    }

    @Override
    public String toString() {
        return "JsonToWarrantyConfirm{" +
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
                ", warrantyCreditNotes=" + warrantyCreditNotes +
                '}';
    }
}
