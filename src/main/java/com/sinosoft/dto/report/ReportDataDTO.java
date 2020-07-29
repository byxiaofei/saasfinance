package com.sinosoft.dto.report;

import com.sinosoft.domain.Report.ReportData;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ReportDataDTO {

    private String yearMonthDate ;//会计期间
    private String JJreportType ;//报表类型
    private String JJreportName ;//报表名称
    private String unit ;//单位

    private String version;//版本号
    private String number;
    private String accBookCode;
    private String accBookType;
    private String reportCode;
    private String needAccBookCode;

    private String level;
    private String sheetName;
    private Integer rowNum;
    private Integer cellNum;
    private List<ReportData> dataList;
    private Map<String,BigDecimal> BalanceDetails; //保障基金余额明细表 最下方备注数据



    private String name1;
    private String name2;

    private String name3k;
    private String name3v;
    private String name4k;
    private String name4v;

    private String name5;
    private String name6;

    private String name7k;
    private String name7v;
    private String name8k;
    private String name8v;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
    public String getName1() {
        return name1;
    }

    public void setName1(String name1) {
        this.name1 = name1;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public String getName3k() {
        return name3k;
    }

    public void setName3k(String name3k) {
        this.name3k = name3k;
    }

    public String getName3v() {
        return name3v;
    }

    public void setName3v(String name3v) {
        this.name3v = name3v;
    }

    public String getName4k() {
        return name4k;
    }

    public void setName4k(String name4k) {
        this.name4k = name4k;
    }

    public String getName4v() {
        return name4v;
    }

    public void setName4v(String name4v) {
        this.name4v = name4v;
    }

    public String getName5() {
        return name5;
    }

    public void setName5(String name5) {
        this.name5 = name5;
    }

    public String getName6() {
        return name6;
    }

    public void setName6(String name6) {
        this.name6 = name6;
    }

    public String getName7k() {
        return name7k;
    }

    public void setName7k(String name7k) {
        this.name7k = name7k;
    }

    public String getName7v() {
        return name7v;
    }

    public void setName7v(String name7v) {
        this.name7v = name7v;
    }

    public String getName8k() {
        return name8k;
    }

    public void setName8k(String name8k) {
        this.name8k = name8k;
    }

    public String getName8v() {
        return name8v;
    }

    public void setName8v(String name8v) {
        this.name8v = name8v;
    }

    public Map<String, BigDecimal> getBalanceDetails() {
        return BalanceDetails;
    }

    public void setBalanceDetails(Map<String, BigDecimal> balanceDetails) {
        BalanceDetails = balanceDetails;
    }

    public List<ReportData> getDataList() {
        return dataList;
    }

    public void setDataList(List<ReportData> dataList) {
        this.dataList = dataList;
    }

    public Integer getRowNum() {
        return rowNum;
    }

    public void setRowNum(Integer rowNum) {
        this.rowNum = rowNum;
    }

    public Integer getCellNum() {
        return cellNum;
    }

    public void setCellNum(Integer cellNum) {
        this.cellNum = cellNum;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getYearMonthDate() {
        return yearMonthDate;
    }

    public void setYearMonthDate(String yearMonthDate) {
        this.yearMonthDate = yearMonthDate;
    }

    public String getJJreportType() {
        return JJreportType;
    }

    public void setJJreportType(String JJreportType) {
        this.JJreportType = JJreportType;
    }

    public String getJJreportName() {
        return JJreportName;
    }

    public void setJJreportName(String JJreportName) {
        this.JJreportName = JJreportName;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getAccBookCode() {
        return accBookCode;
    }

    public void setAccBookCode(String accBookCode) {
        this.accBookCode = accBookCode;
    }

    public String getReportCode() {
        return reportCode;
    }

    public void setReportCode(String reportCode) {
        this.reportCode = reportCode;
    }

    public String getNeedAccBookCode() {
        return needAccBookCode;
    }

    public void setNeedAccBookCode(String needAccBookCode) {
        this.needAccBookCode = needAccBookCode;
    }

    public String getAccBookType() {
        return accBookType;
    }

    public void setAccBookType(String accBookType) {
        this.accBookType = accBookType;
    }
}
