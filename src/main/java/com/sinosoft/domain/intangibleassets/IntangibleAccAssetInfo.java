package com.sinosoft.domain.intangibleassets;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "intangibleaccassetinfo")
public class IntangibleAccAssetInfo {
    @EmbeddedId
    private  IntangibleAccAssetInfoId id;//联合主键
    private String  assetType;
    private String  assetCode;
    private String  assetName;
//    private String  metricName;
//    private String  manufactor;
//    private String  specification;
//    private String  serialNumber;
    private String  useStartDate;
    private BigDecimal remainsValue;
    private BigDecimal  remainsRate;
    private String  initDepreAmount;
    private BigDecimal  initDepreMoney;
    private String      unitCode;
    private String      useFlag;
    private BigDecimal  assetOriginValue;
    private BigDecimal  assetNetValue;
    private BigDecimal  impairment;
    private String      depreFlag;
    private BigDecimal  addedTax;
    private BigDecimal  inputTax;
    private BigDecimal  sum;
    private String      payWay;
    private String      payCode;
    private BigDecimal  depYears;
    private String      depType;
    private String      endDepreAmount;
    private BigDecimal  endDepreMoney;
    private String      voucherNo;
    private String      depreFromDate;
    private String      depreToDate;
    private String      clearYearMonth;
    private String      clearCode;
    private BigDecimal  clearfee;
    private String      clearReason;
    private String      clearDate;
    private String      clearOperatorBranch;
    private String      clearOperatorCode;
    private String      createOper;
    private String      createTime;
    private String      updateOper;
    private String      updateTime;
    private String      temp;
    private String      clearFlag;
   // private String depreToDate;//数据表增加摊销至日期
   // private String depreFromDate; //摊销起始日期



    public IntangibleAccAssetInfo() {
    }

    public String getDepreFromDate() {
        return depreFromDate;
    }

    public void setDepreFromDate(String depreFromDate) {
        this.depreFromDate = depreFromDate;
    }

    public String getDepreToDate() {
        return depreToDate;
    }

    public void setDepreToDate(String depreToDate) {
        this.depreToDate = depreToDate;
    }

    public String getClearFlag() {
        return clearFlag;
    }

    public void setClearFlag(String clearFlag) {
        this.clearFlag = clearFlag;
    }

    public String getDepType() {
        return depType;
    }

    public void setDepType(String depType) {
        this.depType = depType;
    }

    public IntangibleAccAssetInfoId getId() {
        return id;
    }

    public void setId(IntangibleAccAssetInfoId id) {
        this.id = id;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getUseStartDate() {
        return useStartDate;
    }

    public void setUseStartDate(String useStartDate) {
        this.useStartDate = useStartDate;
    }

    public BigDecimal getRemainsValue() {
        return remainsValue;
    }

    public void setRemainsValue(BigDecimal remainsValue) {
        this.remainsValue = remainsValue;
    }

    public BigDecimal getRemainsRate() {
        return remainsRate;
    }

    public void setRemainsRate(BigDecimal remainsRate) {
        this.remainsRate = remainsRate;
    }

    public String getInitDepreAmount() {
        return initDepreAmount;
    }

    public void setInitDepreAmount(String initDepreAmount) {
        this.initDepreAmount = initDepreAmount;
    }

    public BigDecimal getInitDepreMoney() {
        return initDepreMoney;
    }

    public void setInitDepreMoney(BigDecimal initDepreMoney) {
        this.initDepreMoney = initDepreMoney;
    }

    public String getUnitCode() { return unitCode; }

    public void setUnitCode(String unitCode) { this.unitCode = unitCode; }

    public String getUseFlag() {
        return useFlag;
    }

    public void setUseFlag(String useFlag) {
        this.useFlag = useFlag;
    }

    public BigDecimal getAssetOriginValue() {
        return assetOriginValue;
    }

    public void setAssetOriginValue(BigDecimal assetOriginValue) {
        this.assetOriginValue = assetOriginValue;
    }

    public BigDecimal getAssetNetValue() {
        return assetNetValue;
    }

    public void setAssetNetValue(BigDecimal assetNetValue) {
        this.assetNetValue = assetNetValue;
    }

    public BigDecimal getImpairment() {
        return impairment;
    }

    public void setImpairment(BigDecimal impairment) {
        this.impairment = impairment;
    }

    public String getDepreFlag() {
        return depreFlag;
    }

    public void setDepreFlag(String depreFlag) {
        this.depreFlag = depreFlag;
    }

    public BigDecimal getAddedTax() {
        return addedTax;
    }

    public void setAddedTax(BigDecimal addedTax) {
        this.addedTax = addedTax;
    }

    public BigDecimal getInputTax() {
        return inputTax;
    }

    public void setInputTax(BigDecimal inputTax) {
        this.inputTax = inputTax;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }

    public String getPayWay() {
        return payWay;
    }

    public void setPayWay(String payWay) {
        this.payWay = payWay;
    }

    public String getPayCode() {
        return payCode;
    }

    public void setPayCode(String payCode) {
        this.payCode = payCode;
    }

    public BigDecimal getDepYears() {
        return depYears;
    }

    public void setDepYears(BigDecimal depYears) {
        this.depYears = depYears;
    }

    public String getEndDepreAmount() {
        return endDepreAmount;
    }

    public void setEndDepreAmount(String endDepreAmount) {
        this.endDepreAmount = endDepreAmount;
    }

    public BigDecimal getEndDepreMoney() {
        return endDepreMoney;
    }

    public void setEndDepreMoney(BigDecimal endDepreMoney) {
        this.endDepreMoney = endDepreMoney;
    }

    public String getVoucherNo() {
        return voucherNo;
    }

    public void setVoucherNo(String voucherNo) {
        this.voucherNo = voucherNo;
    }

    public String getClearYearMonth() {
        return clearYearMonth;
    }

    public void setClearYearMonth(String clearYearMonth) {
        this.clearYearMonth = clearYearMonth;
    }

    public String getClearCode() {
        return clearCode;
    }

    public void setClearCode(String clearCode) {
        this.clearCode = clearCode;
    }

    public BigDecimal getClearfee() {
        return clearfee;
    }

    public void setClearfee(BigDecimal clearfee) {
        this.clearfee = clearfee;
    }

    public String getClearReason() {
        return clearReason;
    }

    public void setClearReason(String clearReason) {
        this.clearReason = clearReason;
    }

    public String getClearDate() {
        return clearDate;
    }

    public void setClearDate(String clearDate) {
        this.clearDate = clearDate;
    }

    public String getClearOperatorBranch() {
        return clearOperatorBranch;
    }

    public void setClearOperatorBranch(String clearOperatorBranch) {
        this.clearOperatorBranch = clearOperatorBranch;
    }

    public String getClearOperatorCode() {
        return clearOperatorCode;
    }

    public void setClearOperatorCode(String clearOperatorCode) {
        this.clearOperatorCode = clearOperatorCode;
    }

    public String getCreateOper() {
        return createOper;
    }

    public void setCreateOper(String createOper) {
        this.createOper = createOper;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateOper() {
        return updateOper;
    }

    public void setUpdateOper(String updateOper) {
        this.updateOper = updateOper;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }
}
