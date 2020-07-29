package com.sinosoft.dto;

import com.sinosoft.domain.AccountInfo;

public class AccountInfoDTO {
    private int id;
    private String accountType;
    private String accountTypeName;
    private String accountCode;
    private String accountName;
    private String useFlag;
    private String useFlagName;
    private String remark;
    private String createBy;
    private String createByName;
    private String createTime;
    private String lastModifyBy;
    private String lastModifyByName;
    private String lastModifyTime;
    //是否初始化账套
    private String initAccount;
    //套初始化的参考账套
    private String referToAccount;
    //不允许删除标志 Y表示不允许删除
    private String notAllowDel;
    private String comCode;
    private String comName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getAccountTypeName() {
        return accountTypeName;
    }

    public void setAccountTypeName(String accountTypeName) {
        this.accountTypeName = accountTypeName;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getUseFlag() {
        return useFlag;
    }

    public void setUseFlag(String useFlag) {
        this.useFlag = useFlag;
    }

    public String getUseFlagName() {
        return useFlagName;
    }

    public void setUseFlagName(String useFlagName) {
        this.useFlagName = useFlagName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public String getInitAccount() {
        return initAccount;
    }

    public void setInitAccount(String initAccount) {
        this.initAccount = initAccount;
    }

    public String getReferToAccount() {
        return referToAccount;
    }

    public void setReferToAccount(String referToAccount) {
        this.referToAccount = referToAccount;
    }

    public String getNotAllowDel() { return notAllowDel; }

    public void setNotAllowDel(String notAllowDel) { this.notAllowDel = notAllowDel; }

    public String getComCode() { return comCode; }

    public void setComCode(String comCode) { this.comCode = comCode; }

    public String getComName() { return comName; }

    public void setComName(String comName) { this.comName = comName; }

    public static AccountInfoDTO toDTO(AccountInfo accountInfo){
        AccountInfoDTO dto = new AccountInfoDTO();
        dto.setId(accountInfo.getId());
        dto.setAccountType(accountInfo.getAccountType());
        dto.setAccountCode(accountInfo.getAccountCode());
        dto.setAccountName(accountInfo.getAccountName());
        dto.setUseFlag(accountInfo.getUseFlag());
        dto.setRemark(accountInfo.getRemark());
        dto.setCreateBy(accountInfo.getCreateBy());
        dto.setCreateTime(accountInfo.getCreateTime());
        dto.setLastModifyBy(accountInfo.getLastModifyBy());
        dto.setLastModifyTime(accountInfo.getLastModifyTime());
        return dto;
    }
    public static AccountInfo toEntity(AccountInfoDTO dto){
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setId(dto.getId());
        accountInfo.setAccountType(dto.getAccountType());
        accountInfo.setAccountCode(dto.getAccountCode());
        accountInfo.setAccountName(dto.getAccountName());
        accountInfo.setUseFlag(dto.getUseFlag());
        accountInfo.setRemark(dto.getRemark());
        accountInfo.setCreateBy(dto.getCreateBy());
        accountInfo.setCreateTime(dto.getCreateTime());
        accountInfo.setLastModifyBy(dto.getLastModifyBy());
        accountInfo.setLastModifyTime(dto.getLastModifyTime());
        return accountInfo;
    }
}
