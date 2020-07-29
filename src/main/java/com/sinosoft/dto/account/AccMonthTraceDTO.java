package com.sinosoft.dto.account;

import java.io.Serializable;

/**
 * @author jiangyc
 * @Description
 * @create 2019-03-12 10:24
 */
public class AccMonthTraceDTO implements Serializable{
    private static final long serialVersionUID = 1L;

    private String centerCode;//核算单位

    private String yearMonthDate;//凭证年月
    private String settlePeriod1;//开始年月
    private String settlePeriod2;//结束年月

    private String accMonthStat;//会计月度状态
    private String accBookType;//账套类型
    private String accBookCode;//账套编码
    private String createBy;//
    private String createByName;//
    private String createTime;//
    private String temp;//备用

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getCenterCode() {
        return centerCode;
    }

    public void setCenterCode(String centerCode) {
        this.centerCode = centerCode;
    }

    public String getYearMonthDate() {
        return yearMonthDate;
    }

    public void setYearMonthDate(String yearMonthDate) {
        this.yearMonthDate = yearMonthDate;
    }

    public String getSettlePeriod1() {
        return settlePeriod1;
    }

    public void setSettlePeriod1(String settlePeriod1) {
        this.settlePeriod1 = settlePeriod1;
    }

    public String getSettlePeriod2() {
        return settlePeriod2;
    }

    public void setSettlePeriod2(String settlePeriod2) {
        this.settlePeriod2 = settlePeriod2;
    }

    public String getAccMonthStat() {
        return accMonthStat;
    }

    public void setAccMonthStat(String accMonthStat) {
        this.accMonthStat = accMonthStat;
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

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getCreateByName() {
        return createByName;
    }

    public void setCreateByName(String createByName) {
        this.createByName = createByName;
    }
}
