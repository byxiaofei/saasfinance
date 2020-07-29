package com.sinosoft.domain.account;

import java.io.Serializable;

public class ProfitLossCarryDownSubjectId implements Serializable {
    private String profitLossCode;//主键一 损益科目代码
    private String account;//主键二  账套编码

    public ProfitLossCarryDownSubjectId() { }

    public ProfitLossCarryDownSubjectId(String profitLossCode, String account) {
        this.profitLossCode = profitLossCode;
        this.account = account;
    }

    public String getProfitLossCode() { return profitLossCode; }

    public void setProfitLossCode(String profitLossCode) { this.profitLossCode = profitLossCode; }

    public String getAccount() { return account; }

    public void setAccount(String account) { this.account = account; }
}
