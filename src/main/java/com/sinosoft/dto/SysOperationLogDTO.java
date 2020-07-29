package com.sinosoft.dto;

import com.sinosoft.domain.SysOperationLog;

import java.util.Date;

public class SysOperationLogDTO {
    private long id;
    private long userId; //用户Id
    private String userCode; //用户编码
    private String userName; //用户名
    private String operation; //操作
    private String method; //方法名
    private String params; //参数
    private String ip; //ip地址
    private String createDate; //操作时间
    private String createDateStart; //操作开始时间
    private String createDateEnd; //操作结束时间

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) { this.userId = userId; }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) { this.userCode = userCode; }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getCreateDateStart() {
        return createDateStart;
    }

    public void setCreateDateStart(String createDateStart) {
        this.createDateStart = createDateStart;
    }

    public String getCreateDateEnd() {
        return createDateEnd;
    }

    public void setCreateDateEnd(String createDateEnd) { this.createDateEnd = createDateEnd; }

    /**
     * 将SysOperationLog实体类数据转换为SysOperationLogDTO数据
     * @param sysOperationLog
     * @return
     */
    public static SysOperationLogDTO toDTO(SysOperationLog sysOperationLog){
        SysOperationLogDTO sysOperationLogDTO = new SysOperationLogDTO();
        sysOperationLogDTO.setId(sysOperationLog.getId());
        sysOperationLogDTO.setUserId(sysOperationLog.getUserId());
        sysOperationLogDTO.setOperation(sysOperationLog.getOperation());
        sysOperationLogDTO.setMethod(sysOperationLog.getMethod());
        sysOperationLogDTO.setIp(sysOperationLog.getIp());
        sysOperationLogDTO.setParams(sysOperationLog.getParams());
        sysOperationLogDTO.setCreateDate(sysOperationLog.getCreateDate());

        return sysOperationLogDTO;
    }
}
