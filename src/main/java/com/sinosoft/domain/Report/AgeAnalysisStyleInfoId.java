package com.sinosoft.domain.Report;


import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class AgeAnalysisStyleInfoId implements Serializable {
    private Integer version;//报表版本
    private Integer headLevel;//层级
    private Integer number;//序号

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getHeadLevel() {
        return headLevel;
    }

    public void setHeadLevel(Integer headLevel) {
        this.headLevel = headLevel;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }
}
