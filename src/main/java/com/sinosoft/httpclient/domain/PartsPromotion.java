package com.sinosoft.httpclient.domain;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "bz_partspromotion")
public class PartsPromotion {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //  经销商 GSSN 号
    private String dealerNo;
    //  记账公司 GSSN 号
    private String companyNo;
    //  类型
    private String transactionType;
    //  组装/拆分日期
    private String promotionDate;
    //  操作日期
    private String operationDate;
    //  零件编号
    private String partsNo;
    //  1- 总成件；2-零件
    private String flag;
    //  Y-原厂 N-非原厂
    private String genuineFlag;
    //  说明
    private String description;
    //  Parts 分析代码
    private String partsAnalysisCode;
    //  数量
    private BigDecimal quantity;
    //  配件单位成本
    private BigDecimal partsUnitCost;

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

    public String getPromotionDate() {
        return promotionDate;
    }

    public void setPromotionDate(String promotionDate) {
        this.promotionDate = promotionDate;
    }

    public String getOperationDate() {
        return operationDate;
    }

    public void setOperationDate(String operationDate) {
        this.operationDate = operationDate;
    }

    public String getPartsNo() {
        return partsNo;
    }

    public void setPartsNo(String partsNo) {
        this.partsNo = partsNo;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getGenuineFlag() {
        return genuineFlag;
    }

    public void setGenuineFlag(String genuineFlag) {
        this.genuineFlag = genuineFlag;
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

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPartsUnitCost() {
        return partsUnitCost;
    }

    public void setPartsUnitCost(BigDecimal partsUnitCost) {
        this.partsUnitCost = partsUnitCost;
    }

    @Override
    public String toString() {
        return "PartsPromotionDTO{" +
                "id=" + id +
                ", dealerNo='" + dealerNo + '\'' +
                ", companyNo='" + companyNo + '\'' +
                ", transactionType='" + transactionType + '\'' +
                ", promotionDate='" + promotionDate + '\'' +
                ", operationDate='" + operationDate + '\'' +
                ", partsNo='" + partsNo + '\'' +
                ", flag='" + flag + '\'' +
                ", genuineFlag='" + genuineFlag + '\'' +
                ", description='" + description + '\'' +
                ", partsAnalysisCode='" + partsAnalysisCode + '\'' +
                ", quantity='" + quantity + '\'' +
                ", partsUnitCost='" + partsUnitCost + '\'' +
                '}';
    }
}
