package com.sinosoft.httpclient.domain;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class ConfigureManageId implements Serializable {

    private String branchCode;
    private String subjectCode;
    private String interfaceInfo;
    private String interfaceType;

    public ConfigureManageId(){
        super();
    }

    public ConfigureManageId(String branchCode, String subjectCode, String interfaceInfo, String interfaceType) {
        this.branchCode = branchCode;
        this.subjectCode = subjectCode;
        this.interfaceInfo = interfaceInfo;
        this.interfaceType = interfaceType;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getInterfaceInfo() {
        return interfaceInfo;
    }

    public void setInterfaceInfo(String interfaceInfo) {
        this.interfaceInfo = interfaceInfo;
    }

    public String getInterfaceType() {
        return interfaceType;
    }

    public void setInterfaceType(String interfaceType) {
        this.interfaceType = interfaceType;
    }

    @Override
    public String toString() {
        return "ConfigureManageId{" +
                "branchCode='" + branchCode + '\'' +
                ", subjectCode='" + subjectCode + '\'' +
                ", interfaceInfo='" + interfaceInfo + '\'' +
                ", interfaceType='" + interfaceType + '\'' +
                '}';
    }
}
