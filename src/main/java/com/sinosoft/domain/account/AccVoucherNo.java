package com.sinosoft.domain.account;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author jiangyc
 * @Description
 * @create 2019-03-13 15:50
 */

@Entity
@Table(name = "accvoucherno")
public class AccVoucherNo {
    @EmbeddedId
    private AccVoucherNoId  id;
    private String voucherNo;//凭证号
    private String maxFlag;//标志
    private String temp;

    public AccVoucherNoId getId() {
        return id;
    }

    public void setId() {
    }
    public void setId(AccVoucherNoId id) {
        this.id = id;
    }

    public String getVoucherNo() {
        return voucherNo;
    }

    public void setVoucherNo(String voucherNo) {
        this.voucherNo = voucherNo;
    }

    public String getMaxFlag() {
        return maxFlag;
    }

    public void setMaxFlag(String maxFlag) {
        this.maxFlag = maxFlag;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }
}
