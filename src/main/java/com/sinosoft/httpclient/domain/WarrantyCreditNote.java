package com.sinosoft.httpclient.domain;

import java.math.BigDecimal;
import java.util.Date;

public class WarrantyCreditNote {
    //Credit note��
    private Integer creditNoteNo;
    //Credit note����
    private Date creditNoteDate;
    //�⸶�����������˰��
    private BigDecimal compensateParts;
    //�⸶�Ĺ�ʱ������˰��
    private BigDecimal compensateLabor;
    //�⸶�����������˰��
    private BigDecimal compensateSundries;
    //�⸶������������˰��
    private BigDecimal compensateHandlingFee;

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
        return "WarrantyCreditNote{" +
                "creditNoteNo=" + creditNoteNo +
                ", creditNoteDate=" + creditNoteDate +
                ", compensateParts=" + compensateParts +
                ", compensateLabor=" + compensateLabor +
                ", compensateSundries=" + compensateSundries +
                ", compensateHandlingFee=" + compensateHandlingFee +
                '}';
    }
}
