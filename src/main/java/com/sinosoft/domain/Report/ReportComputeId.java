package com.sinosoft.domain.Report;


import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class ReportComputeId implements Serializable {

    private String centerCode;//核算单位
    private String branchCode;//基层单位
    private String accBookType;//账套类型
    private String accBookCode;//账套编码
    private String reportCode;//报表编码
    private String version;//版本号
    private Integer number;//字段排序

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

    public String getReportCode() {
        return reportCode;
    }

    public void setReportCode(String reportCode) {
        this.reportCode = reportCode;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }
}
