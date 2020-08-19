package com.sinosoft.httpclient.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class ServiceInvoiceDTO {
    private String id;
    //批次
    private String batch;
    //  记账公司 GSSN 号
    private String companyNo;
    //  经销商 GSSN 号
    private String dealerNo;
    // 账单类型:I-打印账单 C-退款单
    private String invoicePrintType;
    // 业务类型 W-工单
    private String bizType;
    //账单编号/退款编号
    private String invoiceNo;
    //账单/退款单日期
    private String docDate;
    //客户名称
    private String customerName;
    //公司名称
    private String companyName;
    //品牌
    private String brand;
    //车型
    private String model;
    // 乘用车--PC/商务车--CV
    private String type;
    //车牌号
    private String registrationNo;
    //fin
    private String fin;
    //RWO 工单号码
    private String orderNumber;
    //Service parts invoice 集合
    private List<ServicePartsInvoiceDTO> invoiceServiceParts;
    //Service labors invoice 集合
    private List <ServiceLaborInvoiceDTO>invoiceServiceLabors;
    //科目账务数据
    //主营收入-配件-MB-维修-零售  totalPrice 销售总价(贷方)
    private BigDecimal totalPrice=new BigDecimal("0.00");
    //主营成本-配件-MB-维修-零售（借方）
    private BigDecimal totalpartsUnitCost=new BigDecimal("0.00");
    //库存商品-在用配件-配件发出(贷方)
    private  BigDecimal stockPrice=new BigDecimal("0.00");
    //主营收入-配件折扣-MB-维修折扣  折扣金额（存在生成账务数据）（借方）
    private BigDecimal totalDiscountAmount=new BigDecimal("0.00");
    //应交税金-增值税-销项税额(贷方)
    private  BigDecimal totalVatAmount=new  BigDecimal("0.00");
    //主营收入-工时-MB-机修-零售  totalPrice 销售总价
    private BigDecimal LaborTotalPrice=new BigDecimal("0.00");
    //主营收入-MB-工时折扣-机修  折扣金额（存在生成账务数据）
    private BigDecimal LaborDiscountAmount=new BigDecimal("0.00");
    //应收账款-集团外-省内
    //应收账款-集团外-省内（借方-贷方；根据业务不同借贷金额相反，根据业务场景，算出应收账款）
    private  BigDecimal accountsReceivableAmount=new BigDecimal("0.00");
    private  BigDecimal sumC=new BigDecimal("0.00");
    private  BigDecimal sumD=new BigDecimal("0.00");



    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getInvoicePrintType() {
        return invoicePrintType;
    }

    public void setInvoicePrintType(String invoicePrintType) {
        this.invoicePrintType = invoicePrintType;
    }

    public String getBizType() {
        return bizType;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }


    public String getDocDate() {
        return docDate;
    }

    public void setDocDate(String docDate) {
        this.docDate = docDate;
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

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRegistrationNo() {
        return registrationNo;
    }

    public void setRegistrationNo(String registrationNo) {
        this.registrationNo = registrationNo;
    }

    public String getFin() {
        return fin;
    }

    public void setFin(String fin) {
        this.fin = fin;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public List<ServicePartsInvoiceDTO> getInvoiceServiceParts() {
        return invoiceServiceParts;
    }

    public void setInvoiceServiceParts(List<ServicePartsInvoiceDTO> invoiceServiceParts) {
        this.invoiceServiceParts = invoiceServiceParts;
    }

    public List<ServiceLaborInvoiceDTO> getInvoiceServiceLabors() {
        return invoiceServiceLabors;
    }

    public void setInvoiceServiceLabors(List<ServiceLaborInvoiceDTO> invoiceServiceLabors) {
        this.invoiceServiceLabors = invoiceServiceLabors;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getTotalpartsUnitCost() {
        return totalpartsUnitCost;
    }

    public void setTotalpartsUnitCost(BigDecimal totalpartsUnitCost) {
        this.totalpartsUnitCost = totalpartsUnitCost;
    }

    public BigDecimal getStockPrice() {
        return stockPrice;
    }

    public void setStockPrice(BigDecimal stockPrice) {
        this.stockPrice = stockPrice;
    }

    public BigDecimal getTotalDiscountAmount() {
        return totalDiscountAmount;
    }

    public void setTotalDiscountAmount(BigDecimal totalDiscountAmount) {
        this.totalDiscountAmount = totalDiscountAmount;
    }

    public BigDecimal getTotalVatAmount() {
        return totalVatAmount;
    }

    public void setTotalVatAmount(BigDecimal totalVatAmount) {
        this.totalVatAmount = totalVatAmount;
    }

    public BigDecimal getLaborTotalPrice() {
        return LaborTotalPrice;
    }

    public void setLaborTotalPrice(BigDecimal laborTotalPrice) {
        LaborTotalPrice = laborTotalPrice;
    }

    public BigDecimal getLaborDiscountAmount() {
        return LaborDiscountAmount;
    }

    public void setLaborDiscountAmount(BigDecimal laborDiscountAmount) {
        LaborDiscountAmount = laborDiscountAmount;
    }

    public BigDecimal getAccountsReceivableAmount() {
        return accountsReceivableAmount;
    }

    public void setAccountsReceivableAmount(BigDecimal accountsReceivableAmount) {
        this.accountsReceivableAmount = accountsReceivableAmount;
    }

    public BigDecimal getSumC() {
        return sumC;
    }

    public void setSumC(BigDecimal sumC) {
        this.sumC = sumC;
    }

    public BigDecimal getSumD() {
        return sumD;
    }

    public void setSumD(BigDecimal sumD) {
        this.sumD = sumD;
    }
}
