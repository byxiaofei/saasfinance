package com.sinosoft.httpclient.domain;

import javax.persistence.*;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class JsonToPartsPromotion {

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
    //  Parts 组装集合
    private List<PromotionParts> promotionParts;

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

    public List<PromotionParts> getPromotionParts() {
        return promotionParts;
    }

    public void setPromotionParts(List<PromotionParts> promotionParts) {
        this.promotionParts = promotionParts;
    }

    @Override
    public String toString() {
        return "PartsPromotion{" +
                "id=" + id +
                ", dealerNo='" + dealerNo + '\'' +
                ", companyNo='" + companyNo + '\'' +
                ", transactionType='" + transactionType + '\'' +
                ", promotionDate='" + promotionDate + '\'' +
                ", operationDate='" + operationDate + '\'' +
                ", promotionParts=" + promotionParts +
                '}';
    }
}
