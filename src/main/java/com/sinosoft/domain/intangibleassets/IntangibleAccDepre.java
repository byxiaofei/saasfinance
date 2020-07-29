package com.sinosoft.domain.intangibleassets;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "intangibleaccdepre")
public class IntangibleAccDepre {
    @EmbeddedId
    private IntangibleAccDepreId id;
    private String   voucherNo;
    private BigDecimal monthDepreMoney;
    private BigDecimal   allDepreMoney;
    private BigDecimal   monthDepreQuantity;
    private BigDecimal   allDepreQuantity;
    private String   unitCode;
    private BigDecimal   currentNetValue;
    private String   currentDepreMethod;
    private BigDecimal   currentOriginValue;
    private String   workLoadUnit;
    private String   operatorBranch;
    private String   operatorCode;
    private String   handleDate;
    private String   voucherFlag;
    private String   depreFromDate;
    private String   temp;

    public IntangibleAccDepreId getId() {
        return id;
    }

    public void setId(IntangibleAccDepreId id) {
        this.id = id;
    }

    public String getVoucherNo() {
        return voucherNo;
    }

    public void setVoucherNo(String voucherNo) {
        this.voucherNo = voucherNo;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
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

    public IntangibleAccDepre() {
    }
}
