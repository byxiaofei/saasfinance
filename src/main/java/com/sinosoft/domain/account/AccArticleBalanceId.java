package com.sinosoft.domain.account;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class AccArticleBalanceId implements Serializable {
    private String centerCode ;//核算单位
    private String branchCode ;//基层单位
    private String accBookType ;//账套类型
    private String accBookCode ;//账套编码
    private String yearMonthDate ;//年月
    private String itemCode ;//科目代码
    private String directionIdx ;//科目方向段
    private String directionOther ;//专项方向段

    public AccArticleBalanceId() {}
    public AccArticleBalanceId(String centerCode, String branchCode, String accBookType, String accBookCode, String yearMonthDate, String itemCode, String directionIdx, String directionOther) {
        this.centerCode = centerCode;
        this.branchCode = branchCode;
        this.accBookType = accBookType;
        this.accBookCode = accBookCode;
        this.yearMonthDate = yearMonthDate;
        this.itemCode = itemCode;
        this.directionIdx = directionIdx;
        this.directionOther = directionOther;
    }

    public String getCenterCode() { return centerCode; }
    public void setCenterCode(String centerCode) { this.centerCode = centerCode; }

    public String getBranchCode() { return branchCode;}
    public void setBranchCode(String branchCode) { this.branchCode = branchCode; }

    public String getAccBookType() { return accBookType; }
    public void setAccBookType(String accBookType) { this.accBookType = accBookType; }

    public String getAccBookCode() { return accBookCode; }
    public void setAccBookCode(String accBookCode) { this.accBookCode = accBookCode; }

    public String getYearMonthDate() { return yearMonthDate; }
    public void setYearMonthDate(String yearMonthDate) { this.yearMonthDate = yearMonthDate; }

    public String getItemCode() { return itemCode; }
    public void setItemCode(String itemCode) { this.itemCode = itemCode; }

    public String getDirectionIdx() { return directionIdx; }
    public void setDirectionIdx(String directionIdx) { this.directionIdx = directionIdx; }

    public String getDirectionOther() { return directionOther; }
    public void setDirectionOther(String directionOther) { this.directionOther = directionOther; }
}
