package com.sinosoft.domain;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "sysloginlog")
public class SysLoginLog implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    private long userId; //用户Id
    private String userCode; //用户Id
    private String comId; //机构ID
    private String accountId; //账套ID
    private String ip; //ip地址
    private String loginDate; //登录时间
    private String outDate; //登出时间
    private String loginFlag; //登录状态
    private String loginResult; //登录结果
    private String sessionId;//sessionID

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

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getSessionId() {return sessionId; }

    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
}
