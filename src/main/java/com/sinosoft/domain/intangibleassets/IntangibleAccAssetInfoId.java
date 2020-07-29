package com.sinosoft.domain.intangibleassets;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class IntangibleAccAssetInfoId implements Serializable {
    private String centerCode ;//主键一 核算单位
    private String branchCode ;//主键二  基层单位
    private String accBookType ;//主键三 账套类型
    private String accBookCode ;//主键四 账套编码
    private String codeType;//主键五 管理类别编码
    private String cardCode;//主键六 卡片编码

    public IntangibleAccAssetInfoId() {
    }

    public IntangibleAccAssetInfoId(String centerCode, String branchCode, String accBookType, String accBookCode, String codeType, String cardCode) {
        this.centerCode = centerCode;
        this.branchCode = branchCode;
        this.accBookType = accBookType;
        this.accBookCode = accBookCode;
        this.codeType = codeType;
        this.cardCode = cardCode;
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

    public String getCodeType() {
        return codeType;
    }

    public void setCodeType(String codeType) {
        this.codeType = codeType;
    }

    public String getCardCode() {
        return cardCode;
    }

    public void setCardCode(String cardCode) {
        this.cardCode = cardCode;
    }
}
