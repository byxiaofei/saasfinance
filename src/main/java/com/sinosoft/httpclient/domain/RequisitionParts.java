package com.sinosoft.httpclient.domain;

import java.math.BigDecimal;

public class RequisitionParts {

    //  行号
    private String line;
    //  零件编号
    private String partsNo;
    //  说明
    private String description;
    //  原厂配件标记
    private String genuineFlag;
    //  Parts 分析代码
    private String partsAnalysisCode;
    //  数量
    private BigDecimal quantity;
    //  配件单位成本
    private BigDecimal partsUnitCost;
    //  ICT 参考号
    private String ictCompany;
    //  需求方经销商
    private String ictOrder;

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
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

    public String getGenuineFlag() {
        return genuineFlag;
    }

    public void setGenuineFlag(String genuineFlag) {
        this.genuineFlag = genuineFlag;
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

    public String getIctCompany() {
        return ictCompany;
    }

    public void setIctCompany(String ictCompany) {
        this.ictCompany = ictCompany;
    }

    public String getIctOrder() {
        return ictOrder;
    }

    public void setIctOrder(String ictOrder) {
        this.ictOrder = ictOrder;
    }

    @Override
    public String toString() {
        return "RequisitionParts{" +
                "line='" + line + '\'' +
                ", partsNo='" + partsNo + '\'' +
                ", description='" + description + '\'' +
                ", genuineFlag='" + genuineFlag + '\'' +
                ", partsAnalysisCode='" + partsAnalysisCode + '\'' +
                ", quantity='" + quantity + '\'' +
                ", partsUnitCost='" + partsUnitCost + '\'' +
                ", ictCompany='" + ictCompany + '\'' +
                ", ictOrder='" + ictOrder + '\'' +
                '}';
    }
}
