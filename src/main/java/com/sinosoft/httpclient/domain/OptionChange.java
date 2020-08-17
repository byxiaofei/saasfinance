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
    //交易数据
    private Long idl;
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
    //售后工单号
    private String rwoOrderNo;
    //账单号
    private String rwoInvoiceNo;
    //采购发票号
    private String procurementInvoiceNo;
    //选装件唯一ID
    private String optionId;
    //零件编号
    private String partsNo;
    //选装件代码
    private String optionCode;
    //选装件描述
    private String optionDescription;
    //选装件来源
    private String sourceCode;
    //选装件类别代码
    private String optionCategoryCode;
    //选装件价格
    private BigDecimal price;
    //选装件不含税价格
    private BigDecimal priceWithoutTax;
    //原数量
    private BigDecimal originQuantity;
    //现数量
    private BigDecimal currentQuantity;
    //原预估成本
    private BigDecimal originEstimateCost;
    //现预估成本
    private BigDecimal currentEstimateCost;
    //原实际成本
    private BigDecimal originActualCost;
    //现实际成本
    private BigDecimal currentActualCost;
    //原选装件状态
    private String originOptionStatus;
    //现选装件状态
    private String currentOptionStatus;
    //选装件成本变动
    private BigDecimal optionCostChange;
    //业务发生时间
    private Date businessDate;
    //操作日期
    private Date operationDate;
    //业务类型
    private String transactionType;

    public Integer getId(){return id;}

    public void setId(Integer id){this.id=id;}

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

    public Long getIdl() {
        return idl;
    }

    public void setIdl(Long idl) {
        this.idl = idl;
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

    public String getRwoOrderNo() {
        return rwoOrderNo;
    }

    public void setRwoOrderNo(String rwoOrderNo) {
        this.rwoOrderNo = rwoOrderNo;
    }

    public String getRwoInvoiceNo() {
        return rwoInvoiceNo;
    }

    public void setRwoInvoiceNo(String rwoInvoiceNo) {
        this.rwoInvoiceNo = rwoInvoiceNo;
    }

    public String getProcurementInvoiceNo() {
        return procurementInvoiceNo;
    }

    public void setProcurementInvoiceNo(String procurementInvoiceNo) {
        this.procurementInvoiceNo = procurementInvoiceNo;
    }

    public String getOptionId() {
        return optionId;
    }

    public void setOptionId(String optionId) {
        this.optionId = optionId;
    }

    public String getPartsNo() {
        return partsNo;
    }

    public void setPartsNo(String partsNo) {
        this.partsNo = partsNo;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getPriceWithoutTax() {
        return priceWithoutTax;
    }

    public void setPriceWithoutTax(BigDecimal priceWithoutTax) {
        this.priceWithoutTax = priceWithoutTax;
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

    public BigDecimal getOriginActualCost() {
        return originActualCost;
    }

    public void setOriginActualCost(BigDecimal originActualCost) {
        this.originActualCost = originActualCost;
    }

    public BigDecimal getCurrentActualCost() {
        return currentActualCost;
    }

    public void setCurrentActualCost(BigDecimal currentActualCost) {
        this.currentActualCost = currentActualCost;
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

    public Date getOperationDate() {
        return operationDate;
    }

    public void setOperationDate(Date operationDate) {
        this.operationDate = operationDate;
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
                "id='"+ id +'\''+
                "dealerNo='" + dealerNo + '\'' +
                ", companyNo='" + companyNo + '\'' +
                ", id=" + idl +
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
                ", rwoOrderNo='" + rwoOrderNo + '\'' +
                ", rwoInvoiceNo='" + rwoInvoiceNo + '\'' +
                ", procurementInvoiceNo='" + procurementInvoiceNo + '\'' +
                ", optionId='" + optionId + '\'' +
                ", partsNo='" + partsNo + '\'' +
                ", optionCode='" + optionCode + '\'' +
                ", optionDescription='" + optionDescription + '\'' +
                ", sourceCode='" + sourceCode + '\'' +
                ", optionCategoryCode='" + optionCategoryCode + '\'' +
                ", price=" + price +
                ", priceWithoutTax=" + priceWithoutTax +
                ", originQuantity=" + originQuantity +
                ", currentQuantity=" + currentQuantity +
                ", originEstimateCost=" + originEstimateCost +
                ", currentEstimateCost=" + currentEstimateCost +
                ", originActualCost=" + originActualCost +
                ", currentActualCost=" + currentActualCost +
                ", originOptionStatus='" + originOptionStatus + '\'' +
                ", currentOptionStatus='" + currentOptionStatus + '\'' +
                ", optionCostChange=" + optionCostChange +
                ", businessDate=" + businessDate +
                ", operationDate=" + operationDate +
                ", transactionType='" + transactionType + '\'' +
                '}';
    }
}
