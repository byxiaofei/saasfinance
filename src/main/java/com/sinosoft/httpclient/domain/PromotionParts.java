package com.sinosoft.httpclient.domain;

public class PromotionParts {

    //  总成行唯一标识
    private int id;
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
    private String quantity;
    //  配件单位成本
    private String partsUnitCost;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPartsUnitCost() {
        return partsUnitCost;
    }

    public void setPartsUnitCost(String partsUnitCost) {
        this.partsUnitCost = partsUnitCost;
    }

    @Override
    public String toString() {
        return "PromotionParts{" +
                "id=" + id +
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
