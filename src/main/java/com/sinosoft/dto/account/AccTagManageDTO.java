package com.sinosoft.dto.account;

/**
 * @Auther: hanchuanxing
 * @Date: 2019/2/20 10:25
 * @Description:
 */
public class AccTagManageDTO {
    private long id;
    private String accBookType;
    private String accBookCode;
    private String accBookName;
    private String tagCode;
    private String ntagCode;
    private String centerCode;
    private String tagName;
    private String endFlag;
    private String flag;
    private String upperTag;
    private String createBy;
    private String createByName;
    private String createTime;
    private String modifyReason;
    private String lastModifyBy;
    private String lastModifyByName;
    private String LastModifyTime;
    private String temp;
    private String existLowerOrUse;

    public AccTagManageDTO() {
    }

    public AccTagManageDTO(long id, String centerCode, String accBookType, String accBookCode, String tagCode, String tagName, String endFlag, String upperTag) {
        this.id = id;
        this.centerCode = centerCode;
        this.accBookType = accBookType;
        this.accBookCode = accBookCode;
        this.tagCode = tagCode;
        this.tagName = tagName;
        this.endFlag = endFlag;
        this.upperTag = upperTag;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNtagCode() {
        return ntagCode;
    }

    public void setNtagCode(String ntagCode) {
        this.ntagCode = ntagCode;
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

    public String getAccBookName() {
        return accBookName;
    }

    public void setAccBookName(String accBookName) {
        this.accBookName = accBookName;
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

    public String getCreateByName() {
        return createByName;
    }

    public void setCreateByName(String createByName) {
        this.createByName = createByName;
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

    public String getLastModifyByName() {
        return lastModifyByName;
    }

    public void setLastModifyByName(String lastModifyByName) {
        this.lastModifyByName = lastModifyByName;
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

    public String getExistLowerOrUse() {
        return existLowerOrUse;
    }

    public void setExistLowerOrUse(String existLowerOrUse) {
        this.existLowerOrUse = existLowerOrUse;
    }
}
