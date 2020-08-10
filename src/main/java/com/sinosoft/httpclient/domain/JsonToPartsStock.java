package com.sinosoft.httpclient.domain;

import java.util.List;

//@Entity
//@Table(name = "bz_partsstock")
public class JsonToPartsStock {

 //   @Id
  //  @GeneratedValue(strategy = GenerationType.IDENTITY)
   // @Column(name = "id")
    private  Integer id;
    //经销商 GSSN 号
    private String dealerNo;
    //记账公司 GSSN 号
    private String companyNo;
    //业务类型
    private String transactionType;
    //操作日期
    private String operationDate;
    //入库 parts 集合
    private List<PartsStockIn> stockInParts;

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

    public String getOperationDate() {
        return operationDate;
    }

    public void setOperationDate(String operationDate) {
        this.operationDate = operationDate;
    }

    public List<PartsStockIn> getStockInParts() {
        return stockInParts;
    }

    public void setStockInParts(List<PartsStockIn> stockInParts) {
        this.stockInParts = stockInParts;
    }

    @Override
    public String toString() {
        return "PartsStock{" +
                "id=" + id +
                ", dealerNo='" + dealerNo + '\'' +
                ", companyNo='" + companyNo + '\'' +
                ", transactionType='" + transactionType + '\'' +
                ", operationDate='" + operationDate + '\'' +
                ", stockInParts=" + stockInParts +
                '}';
    }



}
