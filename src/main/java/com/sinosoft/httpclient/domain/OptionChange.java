package com.sinosoft.httpclient.domain;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name ="bz_optionchange")

public class OptionChange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;
    //经销商GSSN号
    private String dealerNo;
    //记账公司GSSN号
    private String companyNo;
    //生产号
    private String commissionNo;
    //美版底盘号
    private String vin;
    //欧版底盘号
    private String fin;
    //baumuster
    private String baumuster;
    //nst
    private String nst;
    //车辆品牌
    private String brand;
    //车辆来源
    private String origin;
    //车型
    private String model;
    //车款
    private String typeClass;
    //车辆状态
    private String vehicleStatus;
    //选装件唯一ID
    private String optionId;
    //选装件代码
    private String optionCode;
    //选装件描述
    private String optionDescription;
    //选装件来源
    private String sourceCode;
    //选装件类别代码
    private String optionCategoryCode;
    //原数量
    private BigDecimal originQuantity;
    //现数量
    private BigDecimal currentQuantity;
    //原预估成本
    private BigDecimal originEstimateCost;
    //现预估成本
    private BigDecimal currentEstimateCost;
    //原选装件状态
    private String originOptionStatus;
    //现选装件状态
    private String currentOptionStatus;
    //选装件成本变动
    private BigDecimal optionCostChange;
    //业务发生时间
    private Date businessDate;
    //操作日期
    private Date triggerDate;
    //业务类型
    private String transactionType;
    //原实际成本
    private BigDecimal currentActualCost;

    public void setCurrentActualCost(BigDecimal currentActualCost){
        this.currentActualCost = currentActualCost;
    }
    public BigDecimal getCurrentActualCost(){
        return currentActualCost;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDealerNo() {
        return dealerNo;
    }

    public void setDealerNo(String dealerNo) {
        this.dealerNo = dealerNo;
    }

    public String getCompanyNo() {
        return companyNo;
    }

    public void setCompanyNo(String companyNo) {
        this.companyNo = companyNo;
    }

    public String getCommissionNo() {
        return commissionNo;
    }

    public void setCommissionNo(String commissionNo) {
        this.commissionNo = commissionNo;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getFin() {
        return fin;
    }

    public void setFin(String fin) {
        this.fin = fin;
    }

    public String getBaumuster() {
        return baumuster;
    }

    public void setBaumuster(String baumuster) {
        this.baumuster = baumuster;
    }

    public String getNst() {
        return nst;
    }

    public void setNst(String nst) {
        this.nst = nst;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getTypeClass() {
        return typeClass;
    }

    public void setTypeClass(String typeClass) {
        this.typeClass = typeClass;
    }

    public String getVehicleStatus() {
        return vehicleStatus;
    }

    public void setVehicleStatus(String vehicleStatus) {
        this.vehicleStatus = vehicleStatus;
    }

    public String getOptionId() {
        return optionId;
    }

    public void setOptionId(String optionId) {
        this.optionId = optionId;
    }

    public String getOptionCode() {
        return optionCode;
    }

    public void setOptionCode(String optionCode) {
        this.optionCode = optionCode;
    }

    public String getOptionDescription() {
        return optionDescription;
    }

    public void setOptionDescription(String optionDescription) {
        this.optionDescription = optionDescription;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public String getOptionCategoryCode() {
        return optionCategoryCode;
    }

    public void setOptionCategoryCode(String optionCategoryCode) {
        this.optionCategoryCode = optionCategoryCode;
    }

    public BigDecimal getOriginQuantity() {
        return originQuantity;
    }

    public void setOriginQuantity(BigDecimal originQuantity) {
        this.originQuantity = originQuantity;
    }

    public BigDecimal getCurrentQuantity() {
        return currentQuantity;
    }

    public void setCurrentQuantity(BigDecimal currentQuantity) {
        this.currentQuantity = currentQuantity;
    }

    public BigDecimal getOriginEstimateCost() {
        return originEstimateCost;
    }

    public void setOriginEstimateCost(BigDecimal originEstimateCost) {
        this.originEstimateCost = originEstimateCost;
    }

    public BigDecimal getCurrentEstimateCost() {
        return currentEstimateCost;
    }

    public void setCurrentEstimateCost(BigDecimal currentEstimateCost) {
        this.currentEstimateCost = currentEstimateCost;
    }

    public String getOriginOptionStatus() {
        return originOptionStatus;
    }

    public void setOriginOptionStatus(String originOptionStatus) {
        this.originOptionStatus = originOptionStatus;
    }

    public String getCurrentOptionStatus() {
        return currentOptionStatus;
    }

    public void setCurrentOptionStatus(String currentOptionStatus) {
        this.currentOptionStatus = currentOptionStatus;
    }

    public BigDecimal getOptionCostChange() {
        return optionCostChange;
    }

    public void setOptionCostChange(BigDecimal optionCostChange) {
        this.optionCostChange = optionCostChange;
    }

    public Date getBusinessDate() {
        return businessDate;
    }

    public void setBusinessDate(Date businessDate) {
        this.businessDate = businessDate;
    }

    public Date getTriggerDate() {
        return triggerDate;
    }

    public void setTriggerDate(Date triggerDate) {
        this.triggerDate = triggerDate;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    @Override
    public String toString() {
        return "OptionChange{" +
                "id=" + id +
                ", dealerNo='" + dealerNo + '\'' +
                ", companyNo='" + companyNo + '\'' +
                ", commissionNo='" + commissionNo + '\'' +
                ", vin='" + vin + '\'' +
                ", fin='" + fin + '\'' +
                ", baumuster='" + baumuster + '\'' +
                ", nst='" + nst + '\'' +
                ", brand='" + brand + '\'' +
                ", origin='" + origin + '\'' +
                ", model='" + model + '\'' +
                ", typeClass='" + typeClass + '\'' +
                ", vehicleStatus='" + vehicleStatus + '\'' +
                ", optionId='" + optionId + '\'' +
                ", optionCode='" + optionCode + '\'' +
                ", optionDescription='" + optionDescription + '\'' +
                ", sourceCode='" + sourceCode + '\'' +
                ", optionCategoryCode='" + optionCategoryCode + '\'' +
                ", originQuantity=" + originQuantity +
                ", currentQuantity=" + currentQuantity +
                ", originEstimateCost=" + originEstimateCost +
                ", currentEstimateCost=" + currentEstimateCost +
                ", originOptionStatus='" + originOptionStatus + '\'' +
                ", currentOptionStatus='" + currentOptionStatus + '\'' +
                ", optionCostChange=" + optionCostChange +
                ", businessDate=" + businessDate +
                ", triggerDate=" + triggerDate +
                ", transactionType='" + transactionType + '\'' +
                ", currentActualCost='" + currentActualCost + '\''+
        '}';
    }
}
