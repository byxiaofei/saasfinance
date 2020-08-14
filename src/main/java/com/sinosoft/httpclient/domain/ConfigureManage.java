package com.sinosoft.httpclient.domain;


import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "bz_configuremanage")
public class ConfigureManage {

    @EmbeddedId
    private ConfigureManageId id;// 联合主键
    private String subjectName;
    private String specialCode;
    private String specialSuperCode;
    private String createTime;
    private String temp1;
    private String temp2;
    private String temp3;

    public ConfigureManageId getId() {
        return id;
    }

    public void setId(ConfigureManageId id) {
        this.id = id;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSpecialCode() {
        return specialCode;
    }

    public void setSpecialCode(String specialCode) {
        this.specialCode = specialCode;
    }

    public String getSpecialSuperCode() {
        return specialSuperCode;
    }

    public void setSpecialSuperCode(String specialSuperCode) {
        this.specialSuperCode = specialSuperCode;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getTemp1() {
        return temp1;
    }

    public void setTemp1(String temp1) {
        this.temp1 = temp1;
    }

    public String getTemp2() {
        return temp2;
    }

    public void setTemp2(String temp2) {
        this.temp2 = temp2;
    }

    public String getTemp3() {
        return temp3;
    }

    public void setTemp3(String temp3) {
        this.temp3 = temp3;
    }

    @Override
    public String toString() {
        return "ConfigureManage{" +
                "id=" + id +
                ", subjectName='" + subjectName + '\'' +
                ", specialCode='" + specialCode + '\'' +
                ", specialSuperCode='" + specialSuperCode + '\'' +
                ", createTime='" + createTime + '\'' +
                ", temp1='" + temp1 + '\'' +
                ", temp2='" + temp2 + '\'' +
                ", temp3='" + temp3 + '\'' +
                '}';
    }
}
