package com.sinosoft.dto.account;

public class ProfitLossCarryDownSubjectDTO {
    private String profitLossCode;//损益科目代码
    private String profitLossCodeName;
    private String account;//账套编码
    private String rightsInterestsCode;//权益科目代码
    private String rightsInterestsCodeName;
    private String endFlag;//末级标志
    private String createBy;
    private String createByName;
    private String createTime;
    private String lastModifyBy;
    private String lastModifyByName;
    private String lastModifyTime;
    private String temp;

    public ProfitLossCarryDownSubjectDTO() { }

    public ProfitLossCarryDownSubjectDTO(String profitLossCode, String profitLossCodeName, String rightsInterestsCode, String endFlag) {
        this.profitLossCode = profitLossCode;
        this.profitLossCodeName = profitLossCodeName;
        this.rightsInterestsCode = rightsInterestsCode;
        this.endFlag = endFlag;
    }

    public ProfitLossCarryDownSubjectDTO(String profitLossCode, String profitLossCodeName, String rightsInterestsCode, String rightsInterestsCodeName, String endFlag) {
        this.profitLossCode = profitLossCode;
        this.profitLossCodeName = profitLossCodeName;
        this.rightsInterestsCode = rightsInterestsCode;
        this.rightsInterestsCodeName = rightsInterestsCodeName;
        this.endFlag = endFlag;
    }

    public String getProfitLossCode() {
        return profitLossCode;
    }

    public void setProfitLossCode(String profitLossCode) {
        this.profitLossCode = profitLossCode;
    }

    public String getProfitLossCodeName() {
        return profitLossCodeName;
    }

    public void setProfitLossCodeName(String profitLossCodeName) {
        this.profitLossCodeName = profitLossCodeName;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getRightsInterestsCode() {
        return rightsInterestsCode;
    }

    public void setRightsInterestsCode(String rightsInterestsCode) {
        this.rightsInterestsCode = rightsInterestsCode;
    }

    public String getRightsInterestsCodeName() {
        return rightsInterestsCodeName;
    }

    public void setRightsInterestsCodeName(String rightsInterestsCodeName) {
        this.rightsInterestsCodeName = rightsInterestsCodeName;
    }

    public String getEndFlag() {
        return endFlag;
    }

    public void setEndFlag(String endFlag) {
        this.endFlag = endFlag;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getCreateByName() {
        return createByName;
    }

    public void setCreateByName(String createByName) {
        this.createByName = createByName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getLastModifyBy() {
        return lastModifyBy;
    }

    public void setLastModifyBy(String lastModifyBy) {
        this.lastModifyBy = lastModifyBy;
    }

    public String getLastModifyByName() {
        return lastModifyByName;
    }

    public void setLastModifyByName(String lastModifyByName) {
        this.lastModifyByName = lastModifyByName;
    }

    public String getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(String lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }
}
