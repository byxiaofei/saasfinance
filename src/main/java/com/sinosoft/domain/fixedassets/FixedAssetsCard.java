package com.sinosoft.domain.fixedassets;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "fixedassetscard",uniqueConstraints = {@UniqueConstraint(columnNames = {"codeType","fixedAssetsCode"})})
public class FixedAssetsCard implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(nullable = false)
    private String codeType;
    @Column(nullable = false)
    private String cardCoed;
    @Column(nullable = false)
    private String manageType;
    @Column(nullable = false)
    private String assettype;
    @Column(nullable = false)
    private String fixedAssetsCode;
    @Column(nullable = false)
    private String fixedAssetsName;
    private String manufacturer;
    private String specification;
    private String serialNumber;
    @Column(nullable = false)
    private String sourceFlag;
    @Column(nullable = false)
    private String measureUnit;
    private Double amount;
    @Column(nullable = false)
    private String useDept;
    @Column(nullable = false)
    private Date enabledDate;
    @Column(nullable = false)
    private Double estimatedGrossUsage;
    @Column(nullable = false)
    private Double originalValue;
    @Column(nullable = false)
    private Double netValue;
    @Column(nullable = false)
    private String useFlag;
    private String remark;
    private String storeAddr;
    @Column(nullable = false)
    private Double estimatedSalvageRate;
    @Column(nullable = false)
    private Double estimatedResidualValue;
    @Column(nullable = false)
    private Double estimatedCleanCost;
    @Column(nullable = false)
    private String deprMethod;
    @Column(nullable = false)
    private Double deprBeginNum;
    @Column(nullable = false)
    private Double deprBeginMoney;
    @Column(nullable = false)
    private Date deprStartData;
    @Column(nullable = false)
    private Date deprToDate;
    @Column(nullable = false)
    private Double deprEndSumNum;
    @Column(nullable = false)
    private Double deprEndSumMoney;
    @Column(nullable = false)
    private String deprCalculationFlag;
    @Column(nullable = false)
    private String createBy;
    @Column(nullable = false)
    private String createTime;
    private String lastModifyBy;
    private String lastModifyTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCodeType() {
        return codeType;
    }

    public void setCodeType(String codeType) {
        this.codeType = codeType;
    }

    public String getCardCoed() {
        return cardCoed;
    }

    public void setCardCoed(String cardCoed) {
        this.cardCoed = cardCoed;
    }

    public String getManageType() {
        return manageType;
    }

    public void setManageType(String manageType) {
        this.manageType = manageType;
    }

    public String getAssettype() {
        return assettype;
    }

    public void setAssettype(String assettype) {
        this.assettype = assettype;
    }

    public String getFixedAssetsCode() {
        return fixedAssetsCode;
    }

    public void setFixedAssetsCode(String fixedAssetsCode) {
        this.fixedAssetsCode = fixedAssetsCode;
    }

    public String getFixedAssetsName() {
        return fixedAssetsName;
    }

    public void setFixedAssetsName(String fixedAssetsName) {
        this.fixedAssetsName = fixedAssetsName;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
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

    public String getSourceFlag() {
        return sourceFlag;
    }

    public void setSourceFlag(String sourceFlag) {
        this.sourceFlag = sourceFlag;
    }

    public String getMeasureUnit() {
        return measureUnit;
    }

    public void setMeasureUnit(String measureUnit) {
        this.measureUnit = measureUnit;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getUseDept() {
        return useDept;
    }

    public void setUseDept(String useDept) {
        this.useDept = useDept;
    }

    public Date getEnabledDate() {
        return enabledDate;
    }

    public void setEnabledDate(Date enabledDate) {
        this.enabledDate = enabledDate;
    }

    public Double getEstimatedGrossUsage() {
        return estimatedGrossUsage;
    }

    public void setEstimatedGrossUsage(Double estimatedGrossUsage) {
        this.estimatedGrossUsage = estimatedGrossUsage;
    }

    public Double getOriginalValue() {
        return originalValue;
    }

    public void setOriginalValue(Double originalValue) {
        this.originalValue = originalValue;
    }

    public Double getNetValue() {
        return netValue;
    }

    public void setNetValue(Double netValue) {
        this.netValue = netValue;
    }

    public String getUseFlag() {
        return useFlag;
    }

    public void setUseFlag(String useFlag) {
        this.useFlag = useFlag;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getStoreAddr() {
        return storeAddr;
    }

    public void setStoreAddr(String storeAddr) {
        this.storeAddr = storeAddr;
    }

    public Double getEstimatedSalvageRate() {
        return estimatedSalvageRate;
    }

    public void setEstimatedSalvageRate(Double estimatedSalvageRate) {
        this.estimatedSalvageRate = estimatedSalvageRate;
    }

    public Double getEstimatedResidualValue() {
        return estimatedResidualValue;
    }

    public void setEstimatedResidualValue(Double estimatedResidualValue) {
        this.estimatedResidualValue = estimatedResidualValue;
    }

    public Double getEstimatedCleanCost() {
        return estimatedCleanCost;
    }

    public void setEstimatedCleanCost(Double estimatedCleanCost) {
        this.estimatedCleanCost = estimatedCleanCost;
    }

    public String getDeprMethod() {
        return deprMethod;
    }

    public void setDeprMethod(String deprMethod) {
        this.deprMethod = deprMethod;
    }

    public Double getDeprBeginNum() {
        return deprBeginNum;
    }

    public void setDeprBeginNum(Double deprBeginNum) {
        this.deprBeginNum = deprBeginNum;
    }

    public Double getDeprBeginMoney() {
        return deprBeginMoney;
    }

    public void setDeprBeginMoney(Double deprBeginMoney) {
        this.deprBeginMoney = deprBeginMoney;
    }

    public Date getDeprStartData() {
        return deprStartData;
    }

    public void setDeprStartData(Date deprStartData) {
        this.deprStartData = deprStartData;
    }

    public Date getDeprToDate() {
        return deprToDate;
    }

    public void setDeprToDate(Date deprToDate) {
        this.deprToDate = deprToDate;
    }

    public Double getDeprEndSumNum() {
        return deprEndSumNum;
    }

    public void setDeprEndSumNum(Double deprEndSumNum) {
        this.deprEndSumNum = deprEndSumNum;
    }

    public Double getDeprEndSumMoney() {
        return deprEndSumMoney;
    }

    public void setDeprEndSumMoney(Double deprEndSumMoney) {
        this.deprEndSumMoney = deprEndSumMoney;
    }

    public String getDeprCalculationFlag() {
        return deprCalculationFlag;
    }

    public void setDeprCalculationFlag(String deprCalculationFlag) {
        this.deprCalculationFlag = deprCalculationFlag;
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

    public String getLastModifyBy() {
        return lastModifyBy;
    }

    public void setLastModifyBy(String lastModifyBy) {
        this.lastModifyBy = lastModifyBy;
    }

    public String getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(String lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }
}