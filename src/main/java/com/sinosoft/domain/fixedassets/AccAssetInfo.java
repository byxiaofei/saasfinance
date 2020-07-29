package com.sinosoft.domain.fixedassets;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * @author jiangyc
 * @Description
 * @create 2019-03-26 11:24
 */
@Entity
@Table(name = "accassetinfo")
public class AccAssetInfo {
    @EmbeddedId
    private AccAssetInfoId id;          //联合主键id
    private String assetType;           //固定资产类别编码
    private String assetCode;              //固定资产编号
    private String assetName;           //固定资产名称
    private String metricName;          //计量单位
    private String manufactor;          //建造/制造商
    private String specification;       //规格说明
    private String serialNumber;        //序列号
    private String useStartDate;        //启用年月
    private String unitCode;            //使用部门
    private String quantity;            //数量（面积）
    private String sourceFlag;          //来源
    private String useFlag;             //使用状态
    private String organization;        //存放地点
    private String storageWay;          //存放方式
    private BigDecimal assetOriginValue;//固定资产原值
    private BigDecimal assetNetValue;   //固定资产净值
    private BigDecimal depYears;        //使用年限
    private String depType;             //折旧方法
    private BigDecimal impairment;      //减值准备
    private BigDecimal addedTax;        //增值税率
    private BigDecimal sum;             //金额
    private BigDecimal inputTax;        //进项税额
    private String payWay;              //付款方式
    private String payCode;             //付款专项
    private BigDecimal remainsRate;     //预计残值率
    private BigDecimal remainsValue;    //预计残值
    private BigDecimal predictClearFee; //预计清理费用
    private String formulaCode;         //折旧公式编码
    private String initDepreAmount;     //期初折旧月份
    private BigDecimal initDepreMoney;  //期初折旧金额
    private String endDepreAmount;      //期末累计折旧月份
    private BigDecimal endDepreMoney;   //期末累计折旧金额
    private String clearFlag;           //清理状态
    private String clearYearMonth;      //清理生效年月
    private String clearCode;           //清理原因
    private BigDecimal clearfee;        //清理费用
    private String clearReason;         //清理原因说明
    private String clearDate;           //清理操作日期
    private String clearOperatorBranch; //清理操作员单位
    private String clearOperatorCode;   //清理操作人
    private String voucherNo;           //凭证号
    private String depreFromDate;       //固定资产折旧起始日期
    private String depreToDate;         //固定资产折旧至日期
    private String depreFlag;           //折旧计提状态
    private String createOper;          //录入人
    private String createTime;          //录入时间
    private String updateOper;          //修改人
    private String updateTime;          //修改时间
    private String temp;                //备注
    private BigDecimal clearIncome;     //清理收入
    private BigDecimal clearIncomeTallage; //清理收入税额

    public BigDecimal getClearIncome() {
        return clearIncome;
    }

    public void setClearIncome(BigDecimal clearIncome) {
        this.clearIncome = clearIncome;
    }

    public BigDecimal getClearIncomeTallage() {
        return clearIncomeTallage;
    }

    public void setClearIncomeTallage(BigDecimal clearIncomeTallage) {
        this.clearIncomeTallage = clearIncomeTallage;
    }

    public AccAssetInfoId getId() {
        return id;
    }

    public void setId(AccAssetInfoId id) {
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

    public String getUseStartDate() {
        return useStartDate;
    }

    public void setUseStartDate(String useStartDate) {
        this.useStartDate = useStartDate;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getSourceFlag() {
        return sourceFlag;
    }

    public void setSourceFlag(String sourceFlag) {
        this.sourceFlag = sourceFlag;
    }

    public String getUseFlag() {
        return useFlag;
    }

    public void setUseFlag(String useFlag) {
        this.useFlag = useFlag;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getStorageWay() {
        return storageWay;
    }

    public void setStorageWay(String storageWay) {
        this.storageWay = storageWay;
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

    public BigDecimal getDepYears() {
        return depYears;
    }

    public void setDepYears(BigDecimal depYears) {
        this.depYears = depYears;
    }

    public String getDepType() {
        return depType;
    }

    public void setDepType(String depType) {
        this.depType = depType;
    }

    public BigDecimal getImpairment() {
        return impairment;
    }

    public void setImpairment(BigDecimal impairment) {
        this.impairment = impairment;
    }

    public BigDecimal getAddedTax() {
        return addedTax;
    }

    public void setAddedTax(BigDecimal addedTax) {
        this.addedTax = addedTax;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }

    public BigDecimal getInputTax() {
        return inputTax;
    }

    public void setInputTax(BigDecimal inputTax) {
        this.inputTax = inputTax;
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

    public BigDecimal getRemainsRate() {
        return remainsRate;
    }

    public void setRemainsRate(BigDecimal remainsRate) {
        this.remainsRate = remainsRate;
    }

    public BigDecimal getRemainsValue() {
        return remainsValue;
    }

    public void setRemainsValue(BigDecimal remainsValue) {
        this.remainsValue = remainsValue;
    }

    public BigDecimal getPredictClearFee() {
        return predictClearFee;
    }

    public void setPredictClearFee(BigDecimal predictClearFee) {
        this.predictClearFee = predictClearFee;
    }

    public String getFormulaCode() {
        return formulaCode;
    }

    public void setFormulaCode(String formulaCode) {
        this.formulaCode = formulaCode;
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

    public String getClearFlag() {
        return clearFlag;
    }

    public void setClearFlag(String clearFlag) {
        this.clearFlag = clearFlag;
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

    public String getDepreFlag() {
        return depreFlag;
    }

    public void setDepreFlag(String depreFlag) {
        this.depreFlag = depreFlag;
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
