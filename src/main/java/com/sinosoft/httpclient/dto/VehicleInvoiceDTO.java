package com.sinosoft.httpclient.dto;

import java.math.BigDecimal;

public class VehicleInvoiceDTO {

    // 经销商 GSSN 号
    private String dealerNo;
    // 记账公司 GSSN 号
    private String companyNo;
    // 账单类型
    private String invoiceType;
    // 账单/退款单编号
    private String invoiceNo;
    // 原账单编号
    private String originInvoiceNo;
    // 生产号
    private String commissionNo;
    // 美版底盘号
    private String vin;
    // 欧版底盘号
    private String fin;
    // baumuster
    private String baumuster;
    // nst
    private String nst;
    // 品牌
    private String brand;
    // 产地
    private String origin;
    // 车型
    private String model;
    // 车款
    private String typeClass;
    //  引擎号
    private String engineNo;
    // 车辆入库状态
    private String bookInStatus;
    // 入库日期
    private String bookInDate;
    // 车辆中文描述
    private String description;
    // 订单号
    private String orderId;
    // 销售类型
    private String salesType;
    // 客户姓名
    private String customerName;
    // 公司名称
    private String companyName;
    // 车辆价格（不含消费税）
    private BigDecimal vehiclePriceWithoutConsumtionTax;
    // 消费税
    private BigDecimal consumptionTax;
    // 不含增值税含消费税，车辆价格 计
    private BigDecimal vehiclePrice;
    // 增值税
    private BigDecimal vatTax;
    // 车辆成本
    private BigDecimal vehicleCost;
    // 首笔定金
    private BigDecimal deposit;
    // 开票日期
    private String retailInvoiceDate;
    // 退票日期
    private String creditDate;
    // 操作日期
    private String operationDate;


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

    public String getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getOriginInvoiceNo() {
        return originInvoiceNo;
    }

    public void setOriginInvoiceNo(String originInvoiceNo) {
        this.originInvoiceNo = originInvoiceNo;
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

    public String getBookInStatus() {
        return bookInStatus;
    }

    public void setBookInStatus(String bookInStatus) {
        this.bookInStatus = bookInStatus;
    }

    public String getBookInDate() {
        return bookInDate;
    }

    public void setBookInDate(String bookInDate) {
        this.bookInDate = bookInDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getSalesType() {
        return salesType;
    }

    public void setSalesType(String salesType) {
        this.salesType = salesType;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public BigDecimal getVehiclePriceWithoutConsumtionTax() {
        return vehiclePriceWithoutConsumtionTax;
    }

    public void setVehiclePriceWithoutConsumtionTax(BigDecimal vehiclePriceWithoutConsumtionTax) {
        this.vehiclePriceWithoutConsumtionTax = vehiclePriceWithoutConsumtionTax;
    }

    public BigDecimal getConsumptionTax() {
        return consumptionTax;
    }

    public void setConsumptionTax(BigDecimal consumptionTax) {
        this.consumptionTax = consumptionTax;
    }

    public BigDecimal getVehiclePrice() {
        return vehiclePrice;
    }

    public void setVehiclePrice(BigDecimal vehiclePrice) {
        this.vehiclePrice = vehiclePrice;
    }

    public BigDecimal getVatTax() {
        return vatTax;
    }

    public void setVatTax(BigDecimal vatTax) {
        this.vatTax = vatTax;
    }

    public BigDecimal getVehicleCost() {
        return vehicleCost;
    }

    public void setVehicleCost(BigDecimal vehicleCost) {
        this.vehicleCost = vehicleCost;
    }

    public BigDecimal getDeposit() {
        return deposit;
    }

    public void setDeposit(BigDecimal deposit) {
        this.deposit = deposit;
    }

    public String getRetailInvoiceDate() {
        return retailInvoiceDate;
    }

    public void setRetailInvoiceDate(String retailInvoiceDate) {
        this.retailInvoiceDate = retailInvoiceDate;
    }

    public String getCreditDate() {
        return creditDate;
    }

    public void setCreditDate(String creditDate) {
        this.creditDate = creditDate;
    }

    public String getOperationDate() {
        return operationDate;
    }

    public void setOperationDate(String operationDate) {
        this.operationDate = operationDate;
    }

    @Override
    public String toString() {
        return "VehicleInvoiceDTO{" +
                "dealerNo='" + dealerNo + '\'' +
                ", companyNo='" + companyNo + '\'' +
                ", invoiceType='" + invoiceType + '\'' +
                ", invoiceNo='" + invoiceNo + '\'' +
                ", originInvoiceNo='" + originInvoiceNo + '\'' +
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
                ", bookInStatus='" + bookInStatus + '\'' +
                ", bookInDate='" + bookInDate + '\'' +
                ", description='" + description + '\'' +
                ", orderId='" + orderId + '\'' +
                ", salesType='" + salesType + '\'' +
                ", customerName='" + customerName + '\'' +
                ", companyName='" + companyName + '\'' +
                ", vehiclePriceWithoutConsumtionTax=" + vehiclePriceWithoutConsumtionTax +
                ", consumptionTax=" + consumptionTax +
                ", vehiclePrice=" + vehiclePrice +
                ", vatTax=" + vatTax +
                ", vehicleCost=" + vehicleCost +
                ", deposit=" + deposit +
                ", retailInvoiceDate='" + retailInvoiceDate + '\'' +
                ", creditDate='" + creditDate + '\'' +
                ", operationDate='" + operationDate + '\'' +
                '}';
    }
}
