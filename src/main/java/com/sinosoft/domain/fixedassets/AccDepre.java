package com.sinosoft.domain.fixedassets;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * @author jiangyc
 * @Description
 * @create 2019-03-26 13:00
 */
@Entity
@Table(name = "accdepre")
public class AccDepre {
    @EmbeddedId
    private AccDepreId id;                  //联合主键id
    private String voucherNo;               //凭证号
    private BigDecimal monthDepreMoney;     //当月折旧金额
    private BigDecimal allDepreMoney;       //累计已折旧金额
    private BigDecimal monthDepreQuantity;  //当月折旧量(小时公里月等)
    private BigDecimal allDepreQuantity;    //累计已折旧量(小时公里月等)
    private String unitCode;                //当月使用部门
    private BigDecimal currentNetValue;     //当前净值
    private String currentDepreMethod;      //当月折旧方法
    private BigDecimal currentOriginValue;  //当前原值
    private String workLoadUnit;            //当前原值
    private String operatorBranch;          //操作员单位
    private String operatorCode;            //操作员
    private String handleDate;              //操作日期
    private String voucherFlag;             //生成凭证标志
    private String depreFromDate;           //固定资产清理日期
    private String temp;                    //备用

    public AccDepreId getId() {
        return id;
    }

    public void setId(AccDepreId id) {
        this.id = id;
    }

    public String getVoucherNo() {
        return voucherNo;
    }

    public void setVoucherNo(String voucherNo) {
        this.voucherNo = voucherNo;
    }

    public BigDecimal getMonthDepreMoney() {
        return monthDepreMoney;
    }

    public void setMonthDepreMoney(BigDecimal monthDepreMoney) {
        this.monthDepreMoney = monthDepreMoney;
    }

    public BigDecimal getAllDepreMoney() {
        return allDepreMoney;
    }

    public void setAllDepreMoney(BigDecimal allDepreMoney) {
        this.allDepreMoney = allDepreMoney;
    }

    public BigDecimal getMonthDepreQuantity() {
        return monthDepreQuantity;
    }

    public void setMonthDepreQuantity(BigDecimal monthDepreQuantity) {
        this.monthDepreQuantity = monthDepreQuantity;
    }

    public BigDecimal getAllDepreQuantity() {
        return allDepreQuantity;
    }

    public void setAllDepreQuantity(BigDecimal allDepreQuantity) {
        this.allDepreQuantity = allDepreQuantity;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public BigDecimal getCurrentNetValue() {
        return currentNetValue;
    }

    public void setCurrentNetValue(BigDecimal currentNetValue) {
        this.currentNetValue = currentNetValue;
    }

    public String getCurrentDepreMethod() {
        return currentDepreMethod;
    }

    public void setCurrentDepreMethod(String currentDepreMethod) {
        this.currentDepreMethod = currentDepreMethod;
    }

    public BigDecimal getCurrentOriginValue() {
        return currentOriginValue;
    }

    public void setCurrentOriginValue(BigDecimal currentOriginValue) {
        this.currentOriginValue = currentOriginValue;
    }

    public String getWorkLoadUnit() {
        return workLoadUnit;
    }

    public void setWorkLoadUnit(String workLoadUnit) {
        this.workLoadUnit = workLoadUnit;
    }

    public String getOperatorBranch() {
        return operatorBranch;
    }

    public void setOperatorBranch(String operatorBranch) {
        this.operatorBranch = operatorBranch;
    }

    public String getOperatorCode() {
        return operatorCode;
    }

    public void setOperatorCode(String operatorCode) {
        this.operatorCode = operatorCode;
    }

    public String getHandleDate() {
        return handleDate;
    }

    public void setHandleDate(String handleDate) {
        this.handleDate = handleDate;
    }

    public String getVoucherFlag() {
        return voucherFlag;
    }

    public void setVoucherFlag(String voucherFlag) {
        this.voucherFlag = voucherFlag;
    }

    public String getDepreFromDate() {
        return depreFromDate;
    }

    public void setDepreFromDate(String depreFromDate) {
        this.depreFromDate = depreFromDate;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }
}
