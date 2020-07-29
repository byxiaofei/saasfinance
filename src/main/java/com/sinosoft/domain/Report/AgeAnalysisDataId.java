package com.sinosoft.domain.Report;


import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class AgeAnalysisDataId implements Serializable {

    private String centerCode;//核算单位
    private String branchCode;//基层单位
    private String accBookType;//账套类型
    private String accBookCode;//账套编码
    private String ageAnalysisType;//账龄类型
    private Integer version;//版本号
    private String computeDate; //计算日期
    private String unit;//单位
    private Integer number;//行号

    public AgeAnalysisDataId() {}

    public AgeAnalysisDataId(String centerCode, String branchCode, String accBookType, String accBookCode, String ageAnalysisType, Integer version, String computeDate, String unit, Integer number) {
        this.centerCode = centerCode;
        this.branchCode = branchCode;
        this.accBookType = accBookType;
        this.accBookCode = accBookCode;
        this.ageAnalysisType = ageAnalysisType;
        this.version = version;
        this.computeDate = computeDate;
        this.unit = unit;
        this.number = number;
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

    public String getAgeAnalysisType() {
        return ageAnalysisType;
    }

    public void setAgeAnalysisType(String ageAnalysisType) {
        this.ageAnalysisType = ageAnalysisType;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getComputeDate() {
        return computeDate;
    }

    public void setComputeDate(String computeDate) {
        this.computeDate = computeDate;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Integer getNumber() { return number; }

    public void setNumber(Integer number) { this.number = number; }
}
