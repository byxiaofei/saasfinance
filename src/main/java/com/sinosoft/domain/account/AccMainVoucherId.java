package com.sinosoft.domain.account;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * 当月记账凭证主表的联合主键
 * 核算单位、基层单位、账套类型、账套编码、年月、凭证号
 */
@Embeddable
public class AccMainVoucherId implements Serializable {

    private String centerCode ;//主键一
    private String branchCode ;//主键二  基层单位
    private String accBookType ;//主键三
    private String accBookCode ;//主键四
    private String yearMonthDate ;//主键五
    private String voucherNo ;//主键六   凭证号

    public AccMainVoucherId() { }
    public AccMainVoucherId(String centerCode, String branchCode, String accBookType, String accBookCode, String yearMonthDate,String voucherNo) {
        this.setCenterCode(centerCode);
        this.setBranchCode(branchCode);
        this.setAccBookType(accBookType);
        this.setAccBookCode(accBookCode);
        this.setYearMonthDate(yearMonthDate);
        this.setVoucherNo(voucherNo);
    }

    public String getCenterCode() {
        return centerCode;
    }

    public void setCenterCode(String centerCode) {
        this.centerCode = centerCode;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getAccBookType() {
        return accBookType;
    }

    public void setAccBookType(String accBookType) {
        this.accBookType = accBookType;
    }

    public String getAccBookCode() {
        return accBookCode;
    }

    public void setAccBookCode(String accBookCode) {
        this.accBookCode = accBookCode;
    }

    public String getYearMonthDate() {
        return yearMonthDate;
    }

    public void setYearMonthDate(String yearMonthDate) {
        this.yearMonthDate = yearMonthDate;
    }

    public String getVoucherNo() {
        return voucherNo;
    }

    public void setVoucherNo(String voucherNo) {
        this.voucherNo = voucherNo;
    }
}