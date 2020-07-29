package com.sinosoft.domain.account;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;


@Entity
@Table(name = "profitlosscarrydownsubject")
public class ProfitLossCarryDownSubject implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private ProfitLossCarryDownSubjectId id;
    @Column(nullable = true)
    private String rightsInterestsCode;//权益科目代码
    private String createBy;
    private String createTime;
    @Column(nullable = true)
    private String lastModifyBy;
    @Column(nullable = true)
    private String lastModifyTime;
    @Column(nullable = true)
    private String temp;

    public ProfitLossCarryDownSubjectId getId() {
        return id;
    }

    public void setId(ProfitLossCarryDownSubjectId id) {
        this.id = id;
    }

    public String getRightsInterestsCode() {
        return rightsInterestsCode;
    }

    public void setRightsInterestsCode(String rightsInterestsCode) {
        this.rightsInterestsCode = rightsInterestsCode;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
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
