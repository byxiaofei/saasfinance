package com.sinosoft.dto;

import com.sinosoft.domain.SysLoginLog;

public class SysLoginLogDTO {
    private long id;
    private long userId; //用户Id
    private String userCode; //用户Id
    private String userName; //用户名称
    private String comId; //机构ID
    private String comName; //机构名称
    private String accountId; //账套ID
    private String accountName; //账套名称
    private String ip; //ip地址
    private String loginDate; //登录时间
    private String outDate; //登出时间
    private String loginFlag; //登录状态
    private String loginResult; //登录结果
    private String sessionId;//sessionID
    private String loginDateStart; //操作开始时间
    private String loginDateEnd; //操作结束时间

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(String loginDate) {
        this.loginDate = loginDate;
    }

    public String getOutDate() { return outDate; }

    public void setOutDate(String outDate) { this.outDate = outDate; }

    public String getLoginFlag() {
        return loginFlag;
    }

    public void setLoginFlag(String loginFlag) {
        this.loginFlag = loginFlag;
    }

    public String getLoginResult() {
        return loginResult;
    }

    public void setLoginResult(String loginResult) {
        this.loginResult = loginResult;
    }

    public String getComId() {
        return comId;
    }

    public void setComId(String comId) {
        this.comId = comId;
    }

    public String getComName() {
        return comName;
    }

    public void setComName(String comName) {
        this.comName = comName;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getSessionId() {return sessionId; }

    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getLoginDateStart() { return loginDateStart; }

    public void setLoginDateStart(String loginDateStart) { this.loginDateStart = loginDateStart; }

    public String getLoginDateEnd() { return loginDateEnd; }

    public void setLoginDateEnd(String loginDateEnd) { this.loginDateEnd = loginDateEnd; }

    public static SysLoginLogDTO toDTO(SysLoginLog sysLoginLog){
        SysLoginLogDTO dto = new SysLoginLogDTO();
        dto.setId(sysLoginLog.getId());
        dto.setUserId(sysLoginLog.getUserId());
        dto.setUserCode(sysLoginLog.getUserCode());
        dto.setIp(sysLoginLog.getIp());
        dto.setComId(sysLoginLog.getComId());
        dto.setAccountId(sysLoginLog.getAccountId());
        dto.setLoginDate(sysLoginLog.getLoginDate());
        dto.setOutDate(sysLoginLog.getOutDate());
        dto.setLoginFlag(sysLoginLog.getLoginFlag());
        dto.setLoginResult(sysLoginLog.getLoginResult());
        dto.setSessionId(sysLoginLog.getSessionId());
        return dto;
    }
}
