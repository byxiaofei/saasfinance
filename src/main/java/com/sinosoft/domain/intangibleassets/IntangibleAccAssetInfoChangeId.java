package com.sinosoft.domain.intangibleassets;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class IntangibleAccAssetInfoChangeId implements Serializable {
    private String centerCode ;//主键一 核算单位
    private String branchCode ;//主键二  基层单位
    private String accBookType ;//主键三 账套类型
    private String accBookCode ;//主键四 账套编码
    private String changeCode;  //主键五 序号

    public IntangibleAccAssetInfoChangeId() {
    }

    public IntangibleAccAssetInfoChangeId(String centerCode, String branchCode, String accBookType, String accBookCode, String changeCode) {
        this.centerCode = centerCode;
        this.branchCode = branchCode;
        this.accBookType = accBookType;
        this.accBookCode = accBookCode;
        this.changeCode = changeCode;
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

    public String getChangeCode() {
        return changeCode;
    }

    public void setChangeCode(String changeCode) {
        this.changeCode = changeCode;
    }
}
