package com.sinosoft.dto.fixedassets;

import java.math.BigDecimal;

/**
 * @author jiangyc
 * @Description
 * @create 2019-03-29 15:06
 */
public class AccAssetInfoDTO {
    private String centerCode;          //核算单位
    private String branchCode;          //基层单位
    private String accBookType;         //账套类型
    private String accBookCode;         //账套编码
    private String codeType;            //管理类别编码
    private String cardCode;            //卡片编码
    private String cardCode1;
    private String assetType;           //固定资产类别编码
    private String assetTypeName;        //固定资产类别名称
    private String assetCode;              //固定资产编号
    private String assetComplexName;    //固定资产类别编码名称
    private String  assetCode1;
    private String assetCode2;
    private String assetName;           //固定资产名称
    private String metricName;          //计量单位
    private String manufactor;          //建造/制造商
    private String specification;       //规格说明
    private String serialNumber;        //序列号
    private String useStartDate;        //启用年月
    private String useStartDate1;
    private String unitCode;            //使用部门
    private String unitCode1;            //使用部门编码
    private String quantity;            //数量（面积）
    private String sourceFlag;          //来源
    private String useFlag;             //使用状态
    private String organization;        //存放地点
    private String organization1;       //存放地点编码
    private String storageWay;          //存放方式
    private BigDecimal assetOriginValue;//固定资产原值
    private BigDecimal assetOriginValue1;
    private BigDecimal assetNetValue;   //固定资产净值
    private BigDecimal assetNetValue1;
    private BigDecimal depYears;        //使用年限
    private BigDecimal depYears1;
    private String depType;             //折旧方法
    private String  deprMethod;         //codemanage表中折旧方法名称
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
    private String operatorBranch;      //操作员单位
    private String operatorCode;        //制单人
    private String handleDate;          //制单日期
    private String endDepreAmount;      //期末累计折旧月份
    private BigDecimal endDepreMoney;   //期末累计折旧金额
    private BigDecimal endDepreMoney1;
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
    private String changeMessage;       //变更信息
    private String stopCard;            //是否包括已停用卡片
    private String cleanCard;           //是否包括已清理卡片
    private String isNotVoucher;        //是否已凭证
    private BigDecimal monthDepreMoney; //本月折旧金额
    private String changeDate;          //变动日期
    private String changeType;          //变动类型
    private String changeOldData;        //变更前数据  显示数据用
    private String changeOldData1;        //变更前数据 保存显示的数据当做变更前数据
    private String changeReason;        //变动原因
    private BigDecimal clearIncome;     //清理收入
    private BigDecimal clearIncomeTallage; //清理收入税额
    private Integer pagestart;             //分页起始
    private Integer pagerow;               //分页行数
    private String createBy;
    private String data1;
    public String getData1() {
        return data1;
    }

    public void setData1(String data1) {
        this.data1 = data1;
    }

    public Integer getPagestart() {
        return pagestart;
    }

    public void setPagestart(Integer pagestart) {
        this.pagestart = pagestart;
    }

    public Integer getPagerow() {
        return pagerow;
    }

    public void setPagerow(Integer pagerow) {
        this.pagerow = pagerow;
    }

    public String getUnitCode1() {
        return unitCode1;
    }

    public void setUnitCode1(String unitCode1) {
        this.unitCode1 = unitCode1;
    }

    public String getOrganization1() {
        return organization1;
    }

    public void setOrganization1(String organization1) {
        this.organization1 = organization1;
    }

    public String getAssetComplexName() {
        return assetComplexName;
    }

    public void setAssetComplexName(String assetComplexName) {
        this.assetComplexName = assetComplexName;
    }

    public String getAssetTypeName() {
        return assetTypeName;
    }

    public void setAssetTypeName(String assetTypeName) {
        this.assetTypeName = assetTypeName;
    }

    public String getDeprMethod() {
        return deprMethod;
    }

    public void setDeprMethod(String deprMethod) {
        this.deprMethod = deprMethod;
    }

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
    public String getChangeOldData1() {
        return changeOldData1;
    }

    public void setChangeOldData1(String changeOldData1) {
        this.changeOldData1 = changeOldData1;
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

    public String getIsNotVoucher() {
        return isNotVoucher;
    }

    public void setIsNotVoucher(String isNotVoucher) {
        this.isNotVoucher = isNotVoucher;
    }

    public String getCenterCode() {
        return centerCode;
    }

    public String getCardCode1() {
        return cardCode1;
    }

    public void setCardCode1(String cardCode1) {
        this.cardCode1 = cardCode1;
    }

    public String getAssetCode1() {
        return assetCode1;
    }

    public void setAssetCode1(String assetCode1) {
        this.assetCode1 = assetCode1;
    }

    public String getAssetCode2() {
        return assetCode2;
    }

    public void setAssetCode2(String assetCode2) {
        this.assetCode2 = assetCode2;
    }

    public String getUseStartDate1() {
        return useStartDate1;
    }

    public void setUseStartDate1(String useStartDate1) {
        this.useStartDate1 = useStartDate1;
    }

    public BigDecimal getAssetOriginValue1() {
        return assetOriginValue1;
    }

    public void setAssetOriginValue1(BigDecimal assetOriginValue1) {
        this.assetOriginValue1 = assetOriginValue1;
    }

    public BigDecimal getAssetNetValue1() {
        return assetNetValue1;
    }

    public void setAssetNetValue1(BigDecimal assetNetValue1) {
        this.assetNetValue1 = assetNetValue1;
    }

    public BigDecimal getDepYears1() {
        return depYears1;
    }

    public void setDepYears1(BigDecimal depYears1) {
        this.depYears1 = depYears1;
    }

    public BigDecimal getEndDepreMoney1() {
        return endDepreMoney1;
    }

    public void setEndDepreMoney1(BigDecimal endDepreMoney1) {
        this.endDepreMoney1 = endDepreMoney1;
    }

    public String getChangeMessage() {
        return changeMessage;
    }

    public void setChangeMessage(String changeMessage) {
        this.changeMessage = changeMessage;
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

    public BigDecimal getMonthDepreMoney() {
        return monthDepreMoney;
    }

    public void setMonthDepreMoney(BigDecimal monthDepreMoney) {
        this.monthDepreMoney = monthDepreMoney;
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

    public String getCreateBy(){return  createBy;}

    public void setCreateBy(String createBy){this.createBy=createBy;}

    @Override
    public String toString() {
        return "AccAssetInfoDTO{" +
                "centerCode='" + centerCode + '\'' +
                ", branchCode='" + branchCode + '\'' +
                ", accBookType='" + accBookType + '\'' +
                ", accBookCode='" + accBookCode + '\'' +
                ", codeType='" + codeType + '\'' +
                ", cardCode='" + cardCode + '\'' +
                ", cardCode1='" + cardCode1 + '\'' +
                ", assetType='" + assetType + '\'' +
                ", assetCode=" + assetCode +
                ", assetCode1='" + assetCode1 + '\'' +
                ", assetCode2='" + assetCode2 + '\'' +
                ", assetName='" + assetName + '\'' +
                ", metricName='" + metricName + '\'' +
                ", manufactor='" + manufactor + '\'' +
                ", specification='" + specification + '\'' +
                ", serialNumber='" + serialNumber + '\'' +
                ", useStartDate='" + useStartDate + '\'' +
                ", useStartDate1='" + useStartDate1 + '\'' +
                ", unitCode='" + unitCode + '\'' +
                ", quantity='" + quantity + '\'' +
                ", sourceFlag='" + sourceFlag + '\'' +
                ", useFlag='" + useFlag + '\'' +
                ", organization='" + organization + '\'' +
                ", storageWay='" + storageWay + '\'' +
                ", assetOriginValue=" + assetOriginValue +
                ", assetOriginValue1=" + assetOriginValue1 +
                ", assetNetValue=" + assetNetValue +
                ", assetNetValue1=" + assetNetValue1 +
                ", depYears=" + depYears +
                ", depYears1=" + depYears1 +
                ", depType='" + depType + '\'' +
                ", impairment=" + impairment +
                ", addedTax=" + addedTax +
                ", sum=" + sum +
                ", inputTax=" + inputTax +
                ", payWay='" + payWay + '\'' +
                ", payCode='" + payCode + '\'' +
                ", remainsRate=" + remainsRate +
                ", remainsValue=" + remainsValue +
                ", predictClearFee=" + predictClearFee +
                ", formulaCode='" + formulaCode + '\'' +
                ", initDepreAmount='" + initDepreAmount + '\'' +
                ", initDepreMoney=" + initDepreMoney +
                ", operatorBranch='" + operatorBranch + '\'' +
                ", operatorCode='" + operatorCode + '\'' +
                ", handleDate='" + handleDate + '\'' +
                ", endDepreAmount='" + endDepreAmount + '\'' +
                ", endDepreMoney=" + endDepreMoney +
                ", endDepreMoney1=" + endDepreMoney1 +
                ", clearFlag='" + clearFlag + '\'' +
                ", clearYearMonth='" + clearYearMonth + '\'' +
                ", clearCode='" + clearCode + '\'' +
                ", clearfee=" + clearfee +
                ", clearReason='" + clearReason + '\'' +
                ", clearDate='" + clearDate + '\'' +
                ", clearOperatorBranch='" + clearOperatorBranch + '\'' +
                ", clearOperatorCode='" + clearOperatorCode + '\'' +
                ", voucherNo='" + voucherNo + '\'' +
                ", depreFromDate='" + depreFromDate + '\'' +
                ", depreToDate='" + depreToDate + '\'' +
                ", depreFlag='" + depreFlag + '\'' +
                ", createOper='" + createOper + '\'' +
                ", createTime='" + createTime + '\'' +
                ", updateOper='" + updateOper + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", temp='" + temp + '\'' +
                ", changeMessage='" + changeMessage + '\'' +
                ", stopCard='" + stopCard + '\'' +
                ", cleanCard='" + cleanCard + '\'' +
                ", isNotVoucher='" + isNotVoucher + '\'' +
                ", monthDepreMoney=" + monthDepreMoney +
                ", changeDate='" + changeDate + '\'' +
                ", changeType='" + changeType + '\'' +
                ", changeOldData='" + changeOldData + '\'' +
                ", changeReason='" + changeReason + '\'' +
                ", createBy='" + createBy + '\'' +
                '}';
    }
}
