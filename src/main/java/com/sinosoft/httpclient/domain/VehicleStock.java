package com.sinosoft.httpclient.domain;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "bz_vehiclestock")
public class VehicleStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    //经销商 GSSN 号
    private String dealerNo;
    //记账公司 GSSN 号
    private String companyNo;
    //业务类型
    private String transactionType;
    //库存变更 时录入的 日期
    private String stockChangeDate;
    //生产号
    private String commissionNo;
    //美版底盘号
    private String vin;
    //欧版底盘号
    private String fin;
    private String baumuster;
    private String nst;
    //品牌
    private String brand;
    //产地
    private String origin;
    //车型
    private String model;
    //车款
    private String typeClass;
    //引擎号
    private String engineNo;
    //原车辆状 态
    private String originVehicleStatus;
    //当前车辆 状态
    private String currentVehicleStatus;
    //车辆中文 描述
    private String description;
    //车辆最新成本
    private String vehicleCurrentCost;
    //车辆上一次成本
    private BigDecimal vehicleOldCost;
    //车辆成本变动
    private BigDecimal vehicleCostChange;
    //操作日期
    private String operationDate;


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

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getStockChangeDate() {
        return stockChangeDate;
    }

    public void setStockChangeDate(String stockChangeDate) {
        this.stockChangeDate = stockChangeDate;
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

    public String getEngineNo() {
        return engineNo;
    }

    public void setEngineNo(String engineNo) {
        this.engineNo = engineNo;
    }

    public String getOriginVehicleStatus() {
        return originVehicleStatus;
    }

    public void setOriginVehicleStatus(String originVehicleStatus) {
        this.originVehicleStatus = originVehicleStatus;
    }

    public String getCurrentVehicleStatus() {
        return currentVehicleStatus;
    }

    public void setCurrentVehicleStatus(String currentVehicleStatus) {
        this.currentVehicleStatus = currentVehicleStatus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVehicleCurrentCost() {
        return vehicleCurrentCost;
    }

    public void setVehicleCurrentCost(String vehicleCurrentCost) {
        this.vehicleCurrentCost = vehicleCurrentCost;
    }

    public BigDecimal getVehicleOldCost() {
        return vehicleOldCost;
    }

    public void setVehicleOldCost(BigDecimal vehicleOldCost) {
        this.vehicleOldCost = vehicleOldCost;
    }

    public BigDecimal getVehicleCostChange() {
        return vehicleCostChange;
    }

    public void setVehicleCostChange(BigDecimal vehicleCostChange) {
        this.vehicleCostChange = vehicleCostChange;
    }

    public String getOperationDate() {
        return operationDate;
    }

    public void setOperationDate(String operationDate) {
        this.operationDate = operationDate;
    }

    @Override
    public String toString() {
        return "VehicleStockDTO{" +
                "dealerNo='" + dealerNo + '\'' +
                ", companyNo='" + companyNo + '\'' +
                ", transactionType='" + transactionType + '\'' +
                ", stockChangeDate='" + stockChangeDate + '\'' +
                ", commissionNo='" + commissionNo + '\'' +
                ", vin='" + vin + '\'' +
                ", fin='" + fin + '\'' +
                ", baumuster='" + baumuster + '\'' +
                ", nst='" + nst + '\'' +
                ", brand='" + brand + '\'' +
                ", origin='" + origin + '\'' +
                ", model='" + model + '\'' +
                ", typeClass='" + typeClass + '\'' +
                ", engineNo='" + engineNo + '\'' +
                ", originVehicleStatus='" + originVehicleStatus + '\'' +
                ", currentVehicleStatus='" + currentVehicleStatus + '\'' +
                ", description='" + description + '\'' +
                ", vehicleCurrentCost='" + vehicleCurrentCost + '\'' +
                ", vehicleOldCost=" + vehicleOldCost +
                ", vehicleCostChange=" + vehicleCostChange +
                ", operationDate='" + operationDate + '\'' +
                '}';
    }
}
