package com.sinosoft.domain.Report;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "reportinfo")
public class ReportInfo {

    @EmbeddedId
    private ReportInfoId id;//联合主键
    private String reportName; //报表名称
    private Integer reportStyle;//报表类型
    private Integer colCount ;//表头列数
    private String createBy;
    private String createTime;
    private String lastModifyBy;
    private String lastModifyTime;

    public ReportInfoId getId() {
        return id;
    }

    public void setId(ReportInfoId id) {
        this.id = id;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public Integer getReportStyle() {
        return reportStyle;
    }

    public void setReportStyle(Integer reportStyle) {
        this.reportStyle = reportStyle;
    }

    public Integer getColCount() {
        return colCount;
    }

    public void setColCount(Integer colCount) {
        this.colCount = colCount;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getLastModifyBy() {
        return lastModifyBy;
    }

    public void setLastModifyBy(String lastModifyBy) {
        this.lastModifyBy = lastModifyBy;
    }

    public String getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(String lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }
}
