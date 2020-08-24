package com.sinosoft.httpclient.domain;

import com.sinosoft.httpclient.dto.ServicePartsInvoiceDTO;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * 所有售后结账、退账的相关数据
 */
@Entity
@Table(name = "bz_serviceinvoice")
public class ServiceInvoice {

    // 所有售后结账、退账的相关数据唯一标识
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String batch;
    //  经销商 GSSN 号
    private String dealerNo;
    //  记账公司 GSSN 号
    private String companyNo;

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

    //ServicePartsInvoiceDTO

    //  项目行编号
    private String lineNumber;
    // 部门代码(行)
    private String departmentCode;
    // 零件编号
    private String partsNo;
    // 说明
    private String description;
    //Parts 分析代码
    private String partsAnalysisCode;
    //数量
    private Integer quantity;
    //配件单位成本
    private BigDecimal partsUnitCost;
    //原厂标记
    private String partGenuineFlag;
    //销售类型
    private String salesType;
    //客户类型编号
    private String customerTypeNo;
    //客户类型编号 描述
    private String customerTypeNoDescription;
    //销售单价
    private BigDecimal unitNetPrice;
    //销售总价
    private BigDecimal totalPrice;
    //折扣百分比
    private BigDecimal discountRate;
    //折扣金额
    private BigDecimal discountAmount;
    //分担比例
    private BigDecimal contribution;
    //分担金额
    private BigDecimal contributionAmount;
    //净价
    private BigDecimal netPrice;
    //套餐标志
    private String packageMark;
    //套餐代码
    private String packageCode;
    //套餐内序号
    private  Integer packageSequence;
    //增值税税率
    private BigDecimal vatRate;
    //增值税税额
    private BigDecimal vatAmount;

    //ServiceLaborInvoiceDTO

    // 工时代码
    private String laborCode;

    //工时率代码 S-外加工
    private String laborRateCode;
    //标准时间
    private String standardHour;
    //零售工时率
    private BigDecimal laborRetailRate;
    //供应商编号
    private String supplierNo;
    //供应商编号描 述
    private String supplierNoDescription;
    //sublet 预估成 本
    private BigDecimal subletEstimateCost;

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(String lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public String getPartsNo() {
        return partsNo;
    }

    public void setPartsNo(String partsNo) {
        this.partsNo = partsNo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPartsAnalysisCode() {
        return partsAnalysisCode;
    }

    public void setPartsAnalysisCode(String partsAnalysisCode) {
        this.partsAnalysisCode = partsAnalysisCode;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPartsUnitCost() {
        return partsUnitCost;
    }

    public void setPartsUnitCost(BigDecimal partsUnitCost) {
        this.partsUnitCost = partsUnitCost;
    }

    public String getPartGenuineFlag() {
        return partGenuineFlag;
    }

    public void setPartGenuineFlag(String partGenuineFlag) {
        this.partGenuineFlag = partGenuineFlag;
    }

    public String getSalesType() {
        return salesType;
    }

    public void setSalesType(String salesType) {
        this.salesType = salesType;
    }

    public String getCustomerTypeNo() {
        return customerTypeNo;
    }

    public void setCustomerTypeNo(String customerTypeNo) {
        this.customerTypeNo = customerTypeNo;
    }

    public String getCustomerTypeNoDescription() {
        return customerTypeNoDescription;
    }

    public void setCustomerTypeNoDescription(String customerTypeNoDescription) {
        this.customerTypeNoDescription = customerTypeNoDescription;
    }

    public BigDecimal getUnitNetPrice() {
        return unitNetPrice;
    }

    public void setUnitNetPrice(BigDecimal unitNetPrice) {
        this.unitNetPrice = unitNetPrice;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(BigDecimal discountRate) {
        this.discountRate = discountRate;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getContribution() {
        return contribution;
    }

    public void setContribution(BigDecimal contribution) {
        this.contribution = contribution;
    }

    public BigDecimal getContributionAmount() {
        return contributionAmount;
    }

    public void setContributionAmount(BigDecimal contributionAmount) {
        this.contributionAmount = contributionAmount;
    }

    public BigDecimal getNetPrice() {
        return netPrice;
    }

    public void setNetPrice(BigDecimal netPrice) {
        this.netPrice = netPrice;
    }

    public String getPackageMark() {
        return packageMark;
    }

    public void setPackageMark(String packageMark) {
        this.packageMark = packageMark;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public Integer getPackageSequence() {
        return packageSequence;
    }

    public void setPackageSequence(Integer packageSequence) {
        this.packageSequence = packageSequence;
    }

    public BigDecimal getVatRate() {
        return vatRate;
    }

    public void setVatRate(BigDecimal vatRate) {
        this.vatRate = vatRate;
    }

    public BigDecimal getVatAmount() {
        return vatAmount;
    }

    public void setVatAmount(BigDecimal vatAmount) {
        this.vatAmount = vatAmount;
    }

    public String getLaborCode() {
        return laborCode;
    }

    public void setLaborCode(String laborCode) {
        this.laborCode = laborCode;
    }

    public String getLaborRateCode() {
        return laborRateCode;
    }

    public void setLaborRateCode(String laborRateCode) {
        this.laborRateCode = laborRateCode;
    }

    public String getStandardHour() {
        return standardHour;
    }

    public void setStandardHour(String standardHour) {
        this.standardHour = standardHour;
    }

    public BigDecimal getLaborRetailRate() {
        return laborRetailRate;
    }

    public void setLaborRetailRate(BigDecimal laborRetailRate) {
        this.laborRetailRate = laborRetailRate;
    }

    public String getSupplierNo() {
        return supplierNo;
    }

    public void setSupplierNo(String supplierNo) {
        this.supplierNo = supplierNo;
    }

    public String getSupplierNoDescription() {
        return supplierNoDescription;
    }

    public void setSupplierNoDescription(String supplierNoDescription) {
        this.supplierNoDescription = supplierNoDescription;
    }

    public BigDecimal getSubletEstimateCost() {
        return subletEstimateCost;
    }

    public void setSubletEstimateCost(BigDecimal subletEstimateCost) {
        this.subletEstimateCost = subletEstimateCost;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }
}
