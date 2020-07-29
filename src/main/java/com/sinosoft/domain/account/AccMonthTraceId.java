package com.sinosoft.domain.account;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * 会计月度表的联合主键
 *核算单位、凭证年月、账套类型、账套编码
 */
@Embeddable
public class AccMonthTraceId implements Serializable {
    private String centerCode ;//核算单位
    private String yearMonthDate ;//凭证年月
    private String accBookType ;//账套类型
    private String accBookCode ;//账套编码

    public AccMonthTraceId() {}
    public AccMonthTraceId(String centerCode, String yearMonthDate, String accBookType, String accBookCode) {
        this.centerCode = centerCode;
        this.yearMonthDate = yearMonthDate;
        this.accBookType = accBookType;
        this.accBookCode = accBookCode;
    }

    public String getCenterCode() { return centerCode; }

    public void setCenterCode(String centerCode) { this.centerCode = centerCode; }

    public String getYearMonthDate() { return yearMonthDate; }

    public void setYearMonthDate(String yearMonthDate) { this.yearMonthDate = yearMonthDate; }

    public String getAccBookType() { return accBookType; }

    public void setAccBookType(String accBookType) { this.accBookType = accBookType; }

    public String getAccBookCode() { return accBookCode; }

    public void setAccBookCode(String accBookCode) { this.accBookCode = accBookCode; }
}
