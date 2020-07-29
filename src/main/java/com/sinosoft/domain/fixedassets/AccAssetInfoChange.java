package com.sinosoft.domain.fixedassets;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author jiangyc
 * @Description
 * @create 2019-03-26 12:39
 */
@Entity
@Table(name = "accassetinfochange")
public class AccAssetInfoChange {
    @EmbeddedId
    private AccAssetInfoChangeId id;    //联合主键id
    private String codeType;            //管理类别编码
    private String cardCode;            //卡片编码
    private String assetType;           //固定资产类别编码
    private String assetCode;           //固定资产编号
    private String changeDate;          //变动年月
    private String changeType;          //变更类型
    private String changeOldData;       //变更前数据
    private String changeNewData;       //变更后数据
    private String changeReason;        //变动说明
    private String operatorBranch;      //操作员单位
    private String operatorCode;        //操作员
    private String handleDate;          //操作时间
    private String temp;                //备用

    public AccAssetInfoChangeId getId() {
        return id;
    }

    public void setId(AccAssetInfoChangeId id) {
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

    @Override
    public String toString() {
        return "AccAssetInfoChange{" +
                "id=" + id +
                ", codeType='" + codeType + '\'' +
                ", cardCode='" + cardCode + '\'' +
                ", assetType='" + assetType + '\'' +
                ", assetCode='" + assetCode + '\'' +
                ", changeDate='" + changeDate + '\'' +
                ", changeType='" + changeType + '\'' +
                ", changeOldData='" + changeOldData + '\'' +
                ", changeNewData='" + changeNewData + '\'' +
                ", changeReason='" + changeReason + '\'' +
                ", operatorBranch='" + operatorBranch + '\'' +
                ", operatorCode='" + operatorCode + '\'' +
                ", handleDate='" + handleDate + '\'' +
                ", temp='" + temp + '\'' +
                '}';
    }
}
