package com.sinosoft.dto.intangibleassets;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

//@JsonIgnoreProperties(ignoreUnknown = true)
public class IntangibleAccAssetInfoDTO {
    private String centerCode ;//主键一 核算单位
    private String branchCode ;//主键二  基层单位
    private String accBookType ;//主键三 账套类型
    private String accBookCode ;//主键四 账套编码
    private String codeType;//主键五 管理类别编码
    private String cardCode1;//主键六 卡片编码
    private String cardCode;//主键六 卡片编码
    private String  assetType;
    private String assetTypeName;//卡片类型名称
    private String  assetCode1; //无形资产编码
    private String  assetCode;
    private String  assetName;
    private String  metricName;
    private String  manufactor;
    private String  specification;
    private String  serialNumber;
    private String  useStartDate1; //使用日期
    private String  useStartDate;
    private BigDecimal remainsValue;
    private BigDecimal  remainsRate;
    private String  initDepreAmount;
    private BigDecimal  initDepreMoney;
    private String      unitCode;
    private String      useFlag;
    private BigDecimal  assetOriginValue1;
    private BigDecimal  assetOriginValue; //原值
    private BigDecimal  assetNetValue1;
    private BigDecimal  assetNetValue; //净值
    private BigDecimal  impairment;
    private String      depreFlag;
    private BigDecimal  addedTax;
    private BigDecimal  inputTax;
    private BigDecimal  sum;
    private String      payWay;
    private String      payCode;
    private BigDecimal  depYears;
    private BigDecimal  depYears1; //摊销年限 预计使用月份
    private String      endDepreAmount;
    private BigDecimal  endDepreMoney; //期末累计摊销额
    private BigDecimal  endDepreMoney1;
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
   /* private BigDecimal depreMoney1;//累计摊销额
    private BigDecimal depreMoney;//累计摊销额*/
    private String        depreUtilDate;//摊销至日期
    //private String depreToDate;//数据表增加摊销至日期
    //private String depreFromDate; //摊销起始日期
    private String articleCode1;  //资产专项
    private String changeMessage;  //变动信息
    private String changeDate;  //变动年月
    private String changeType;  //变动类型
    private String changeReason ;//变动原因
    private String changeOldData;        //变更前数据  显示数据用
    private String changeOldData1;        //变更前数据 保存显示的数据当做变更前数据
    private String isNotVoucher; //是否已经生成凭证
    private String stopCard; //是否包括已停用卡片
    private String cleanCard; //是否包括已清理卡片
    private String      depType; //摊销方法
    private BigDecimal monthDepreMoney; //本月折旧金额
  /*  private BigDecimal clearIncome;     //清理收入
    private BigDecimal clearIncomeTallage; //清理收入税额*/
     private String cardCode2;
     private  String madeBy;
     //接收页面发送的无形资产卡片编码等数据数据
     private String data1;

    public String getData1() {
        return data1;
    }

    public void setData1(String data1) {
        this.data1 = data1;
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

    public String getCodeType() {
        return codeType;
    }

    public void setCodeType(String codeType) {
        this.codeType = codeType;
    }

    public String getCardCode1() {
        return cardCode1;
    }

    public void setCardCode1(String cardCode1) {
        this.cardCode1 = cardCode1;
    }

    public String getCardCode() {
        return cardCode;
    }

    public void setCardCode(String cardCode) {
        this.cardCode = cardCode;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public String getAssetTypeName() {
        return assetTypeName;
    }

    public void setAssetTypeName(String assetTypeName) {
        this.assetTypeName = assetTypeName;
    }

    public String getAssetCode1() {
        return assetCode1;
    }

    public void setAssetCode1(String assetCode1) {
        this.assetCode1 = assetCode1;
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

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public String getManufactor() {
        return manufactor;
    }

    public void setManufactor(String manufactor) {
        this.manufactor = manufactor;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getUseStartDate1() {
        return useStartDate1;
    }

    public void setUseStartDate1(String useStartDate1) {
        this.useStartDate1 = useStartDate1;
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

    public BigDecimal getAssetOriginValue1() {
        return assetOriginValue1;
    }

    public void setAssetOriginValue1(BigDecimal assetOriginValue1) {
        this.assetOriginValue1 = assetOriginValue1;
    }

    public BigDecimal getAssetOriginValue() {
        return assetOriginValue;
    }

    public void setAssetOriginValue(BigDecimal assetOriginValue) {
        this.assetOriginValue = assetOriginValue;
    }

    public BigDecimal getAssetNetValue1() {
        return assetNetValue1;
    }

    public void setAssetNetValue1(BigDecimal assetNetValue1) {
        this.assetNetValue1 = assetNetValue1;
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

    public BigDecimal getDepYears1() {
        return depYears1;
    }

    public void setDepYears1(BigDecimal depYears1) {
        this.depYears1 = depYears1;
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

    public BigDecimal getEndDepreMoney1() {
        return endDepreMoney1;
    }

    public void setEndDepreMoney1(BigDecimal endDepreMoney1) {
        this.endDepreMoney1 = endDepreMoney1;
    }

    public String getVoucherNo() {
        return voucherNo;
    }

    public void setVoucherNo(String voucherNo) {
        this.voucherNo = voucherNo;
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

    public String getClearFlag() {
        return clearFlag;
    }

    public void setClearFlag(String clearFlag) {
        this.clearFlag = clearFlag;
    }

    public String getDepreUtilDate() {
        return depreUtilDate;
    }

    public void setDepreUtilDate(String depreUtilDate) {
        this.depreUtilDate = depreUtilDate;
    }

    public String getArticleCode1() {
        return articleCode1;
    }

    public void setArticleCode1(String articleCode1) {
        this.articleCode1 = articleCode1;
    }

    public String getChangeMessage() {
        return changeMessage;
    }

    public void setChangeMessage(String changeMessage) {
        this.changeMessage = changeMessage;
    }

    public String getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(String changeDate) {
        this.changeDate = changeDate;
    }

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public String getChangeReason() {
        return changeReason;
    }

    public void setChangeReason(String changeReason) {
        this.changeReason = changeReason;
    }

    public String getChangeOldData() {
        return changeOldData;
    }

    public void setChangeOldData(String changeOldData) {
        this.changeOldData = changeOldData;
    }

    public String getChangeOldData1() {
        return changeOldData1;
    }

    public void setChangeOldData1(String changeOldData1) {
        this.changeOldData1 = changeOldData1;
    }

    public String getIsNotVoucher() {
        return isNotVoucher;
    }

    public void setIsNotVoucher(String isNotVoucher) {
        this.isNotVoucher = isNotVoucher;
    }

    public String getStopCard() {
        return stopCard;
    }

    public void setStopCard(String stopCard) {
        this.stopCard = stopCard;
    }

    public String getCleanCard() {
        return cleanCard;
    }

    public void setCleanCard(String cleanCard) {
        this.cleanCard = cleanCard;
    }

    public String getDepType() {
        return depType;
    }

    public void setDepType(String depType) {
        this.depType = depType;
    }

    public BigDecimal getMonthDepreMoney() {
        return monthDepreMoney;
    }

    public void setMonthDepreMoney(BigDecimal monthDepreMoney) {
        this.monthDepreMoney = monthDepreMoney;
    }

    public String getCardCode2(){return  cardCode2;}

    public void setCardCode2(String cardCode2){this.cardCode2 = cardCode2;}

    public String getMadeBy(){return madeBy;}

    public void setMadeBy(String madeBy){this.madeBy=madeBy;}
}
