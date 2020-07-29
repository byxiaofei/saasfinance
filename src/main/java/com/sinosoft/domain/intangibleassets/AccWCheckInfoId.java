package com.sinosoft.domain.intangibleassets;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * @author jiangyc
 * @Description
 * @create 2019-04-09 19:39
 */
@Embeddable
public class AccWCheckInfoId implements Serializable {
    private String centerCode;//核算单位
    private String yearMonthDate;//凭证年月
    private String accBookType;//账套类型
    private String accBookCode;//账套编码

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
}
