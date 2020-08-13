package com.sinosoft.httpclient.dto;

import java.math.BigDecimal;

/**
 * DN 校验集合
 */
public class DnVerifyDTO {

    //  记账公司 GSSN 号
    private String dnNo;
    //  校验编号
    private BigDecimal acceptAmount;

    public String getDnNo() {
        return dnNo;
    }

    public void setDnNo(String dnNo) {
        this.dnNo = dnNo;
    }

    public BigDecimal getAcceptAmount() {
        return acceptAmount;
    }

    public void setAcceptAmount(BigDecimal acceptAmount) {
        this.acceptAmount = acceptAmount;
    }
}
