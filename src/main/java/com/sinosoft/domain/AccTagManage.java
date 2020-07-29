package com.sinosoft.domain;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @Auther: hanchuanxing
 * @Date: 2019/2/18 17:45
 * @Description:
 */

@Entity
@Table(name = "acctagmanage")
public class AccTagManage implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private String centerCode;
    @Column
    private String accBookType;
    @Column
    private String accBookCode;
    @Column
    private String tagCode;
    @Column
    private String tagName;
    @Column
    private String endFlag;
    @Column
    private String flag;
    @Column
    private String upperTag;
    @Column
    private String createBy;
    @Column
    private String createTime;
    @Column
    private String modifyReason;
    @Column
    private String lastModifyBy;
    @Column
    private String LastModifyTime;
    @Column
    private String temp;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getTagCode() {
        return tagCode;
    }

    public void setTagCode(String tagCode) {
        this.tagCode = tagCode;
    }

    public String getCenterCode() {
        return centerCode;
    }

    public void setCenterCode(String centerCode) {
        this.centerCode = centerCode;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getEndFlag() {
        return endFlag;
    }

    public void setEndFlag(String endFlag) {
        this.endFlag = endFlag;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getUpperTag() {
        return upperTag;
    }

    public void setUpperTag(String upperTag) {
        this.upperTag = upperTag;
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

    public String getModifyReason() {
        return modifyReason;
    }

    public void setModifyReason(String modifyReason) {
        this.modifyReason = modifyReason;
    }

    public String getLastModifyBy() {
        return lastModifyBy;
    }

    public void setLastModifyBy(String lastModifyBy) {
        this.lastModifyBy = lastModifyBy;
    }

    public String getLastModifyTime() {
        return LastModifyTime;
    }

    public void setLastModifyTime(String lastModifyTime) {
        LastModifyTime = lastModifyTime;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }
}
