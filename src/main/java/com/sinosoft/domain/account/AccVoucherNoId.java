package com.sinosoft.domain.account;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author jiangyc
 * @Description
 * @create 2019-03-13 15:50
 */

@Embeddable
public class AccVoucherNoId implements Serializable{
    private String centerCode;//核算单位
    private String yearMonthDate;//凭证年月
    private String accBookType;//账套类型
    private String accBookCode;//账套编码

    public AccVoucherNoId() {
    }

    public AccVoucherNoId(String centerCode, String yearMonthDate, String accBookType, String accBookCode) {
        this.centerCode = centerCode;
        this.yearMonthDate = yearMonthDate;
        this.accBookType = accBookType;
        this.accBookCode = accBookCode;
    }


    public String getCenterCode() {

        return centerCode;
    }

    public void setCenterCode(String centerCode) {
        this.centerCode = centerCode;
    }

    public String getYearMonthDate() {
        return yearMonthDate;
    }

    public void setYearMonthDate(String yearMonthDate) {
        this.yearMonthDate = yearMonthDate;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccVoucherNoId that = (AccVoucherNoId) o;
        return Objects.equals(centerCode, that.centerCode) &&
                Objects.equals(yearMonthDate, that.yearMonthDate) &&
                Objects.equals(accBookType, that.accBookType) &&
                Objects.equals(accBookCode, that.accBookCode);
    }

    @Override
    public int hashCode() {

        return Objects.hash(centerCode, yearMonthDate, accBookType, accBookCode);
    }

}
