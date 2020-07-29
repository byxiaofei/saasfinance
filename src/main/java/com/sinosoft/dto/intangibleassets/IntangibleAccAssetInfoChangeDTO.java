package com.sinosoft.dto.intangibleassets;

/**
 * @author jiangyc
 * @Description无形卡片变动表
 * @create 2019-03-26 12:39
 */

public class IntangibleAccAssetInfoChangeDTO {
    private String centerCode;      //核算单位
    private String branchCode;      //基层单位
    private String accBookType;     //账套类型
    private String accBookCode;     //账套编码
    private String changeCode;      //序号
    private String changeCode1;      //序号
    private String codeType;            //管理类别编码
    private String cardCode;            //卡片编码
    private String cardCode1;
    private String assetType;           //固定资产类别编码
    private String assetCode;           //固定资产编号
    private String assetCode1;
    private String useStartDate;         //启用时间
    private String useStartDate1;         //启用时间
    private String assetName;           //固定资产名称
    private String changeDate;          //变动年月
    private String changeDate1;
    private String organization;        //存放地点
    private String  unitCode;           //使用部门
    // private String createBy;            //制单人
    private String changeType;          //变更类型
    private String changeTypeName;          //变更类型名称
    private String changeOldData;       //变更前数据
    private String changeNewData;       //变更后数据
    private String changeReason;        //变动说明
    private String operatorBranch;      //操作员单位
    private String operatorCode;        //操作员
    private String handleDate;          //操作时间
    private String temp;                //备用
    private String specification;        //规格
    private String useFlag;                //使用状态
    private String useFlagName;                //使用状态
    private String depTypeName;            //折旧方法名称
    private String clearFlag;                //清理状态
    private String clearFlagName;       //清理

    public String getSelectIntangibleYear() {
        return selectIntangibleYear;
    }

    public void setSelectIntangibleYear(String selectIntangibleYear) {
        this.selectIntangibleYear = selectIntangibleYear;
    }

    private String selectIntangibleYear;        //无形资产变动查询年份

    public String getClearFlagName() {
        return clearFlagName;
    }

    public void setClearFlagName(String clearFlagName) {
        this.clearFlagName = clearFlagName;
    }

    public String getUseFlagName() {
        return useFlagName;
    }

    public void setUseFlagName(String useFlagName) {
        this.useFlagName = useFlagName;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getDepTypeName() {
        return depTypeName;
    }

    public void setDepTypeName(String depTypeName) {
        this.depTypeName = depTypeName;
    }

    public String getClearFlag() {
        return clearFlag;
    }

    public void setClearFlag(String clearFlag) {
        this.clearFlag = clearFlag;
    }

    public String getChangeTypeName() {
        return changeTypeName;
    }

    public void setChangeTypeName(String changeTypeName) {
        this.changeTypeName = changeTypeName;
    }

    public String getChangeCode1() {
        return changeCode1;
    }

    public void setChangeCode1(String changeCode1) {
        this.changeCode1 = changeCode1;
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

    public String getUseStartDate() {
        return useStartDate;
    }

    public void setUseStartDate(String useStartDate) {
        this.useStartDate = useStartDate;
    }

    public String getUseStartDate1() {
        return useStartDate1;
    }

    public void setUseStartDate1(String useStartDate1) {
        this.useStartDate1 = useStartDate1;
    }

    public String getChangeDate1() {
        return changeDate1;
    }

    public void setChangeDate1(String changeDate1) {
        this.changeDate1 = changeDate1;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
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

    public String getChangeCode() {
        return changeCode;
    }

    public void setChangeCode(String changeCode) {
        this.changeCode = changeCode;
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

    public String getChangeOldData() {
        return changeOldData;
    }

    public void setChangeOldData(String changeOldData) {
        this.changeOldData = changeOldData;
    }

    public String getChangeNewData() {
        return changeNewData;
    }

    public void setChangeNewData(String changeNewData) {
        this.changeNewData = changeNewData;
    }

    public String getChangeReason() {
        return changeReason;
    }

    public void setChangeReason(String changeReason) {
        this.changeReason = changeReason;
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

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getUseFlag() {
        return useFlag;
    }

    public void setUseFlag(String useFlag) {
        this.useFlag = useFlag;
    }
}
