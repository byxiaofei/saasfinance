package com.sinosoft.domain.intangibleassets;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "intangibleaccassetinfochange")
public class IntangibleAccAssetInfoChange {
    @EmbeddedId
    private  IntangibleAccAssetInfoChangeId id;//联合主键

    private String  codeType;
    private String  cardCode;
    private String  assetType;
    private String  assetCode;
    private String  changeDate;
    private String  changeType;
    private String  changeOldData;
    private String  changeNewData;
    private String  changeReason;
    private String  operatorBranch;
    private String  operatorCode;
    private String  handleDate;
    private String     temp;

    public IntangibleAccAssetInfoChange() {
    }

    public IntangibleAccAssetInfoChangeId getId() {
        return id;
    }

    public void setId(IntangibleAccAssetInfoChangeId id) {
        this.id = id;
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

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }

    public String getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(String changeDate) {
        this.changeDate = changeDate;
    }

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public String getChangeOldData() {
        return changeOldData;
    }

    public void setChangeOldData(String changeOldData) {
        this.changeOldData = changeOldData;
    }

    public String getChangeNewData() {
        return changeNewData;
    }

    public void setChangeNewData(String changeNewData) {
        this.changeNewData = changeNewData;
    }

    public String getChangeReason() {
        return changeReason;
    }

    public void setChangeReason(String changeReason) {
        this.changeReason = changeReason;
    }

    public String getOperatorBranch() {
        return operatorBranch;
    }

    public void setOperatorBranch(String operatorBranch) {
        this.operatorBranch = operatorBranch;
    }

    public String getOperatorCode() {
        return operatorCode;
    }

    public void setOperatorCode(String operatorCode) {
        this.operatorCode = operatorCode;
    }

    public String getHandleDate() {
        return handleDate;
    }

    public void setHandleDate(String handleDate) {
        this.handleDate = handleDate;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }
}

