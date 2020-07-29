package com.sinosoft.dto.parsing;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * @Auther: luodejun
 * @Date: 2020/5/13 20:52
 * @Description:
 */

public class VoucherHeader  {

    @XmlElement(name = "company")
    private String company;
    @XmlElement(name = "createBy")
    private String createBy;
    @XmlElement(name = "voucherType")
    private String voucherType;
    @XmlElement(name = "voucharDate")
    private String voucharDate;
    @XmlElement(name = "yearMonth")
    private String yearMonth;
    @XmlElement(name = "auxNumber")
    private String auxNumber;
    @XmlElement(name = "entryNumber")
    private String entryNumber;
    @XmlElement(name = "accbookType")
    private String accbookType;
    @XmlElement(name = "accbookCode")
    private String accbookCode;
    @XmlElement(name = "branchCode")
    private String branchCode;
    @XmlElement(name = "centerCode")
    private String centerCode;
    @XmlElement(name = "fileName")
    private String fileName;
    @XmlElement(name = "generateWay")
    private String generateWay;
    @XmlElement(name = "dataSource")
    private String dataSource;

    public String getGenerateWay() {
        return generateWay;
    }
    @XmlTransient
    public void setGenerateWay(String generateWay) {
        this.generateWay = generateWay;
    }

    public String getDataSource() {
        return dataSource;
    }
    @XmlTransient
    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    @XmlTransient
    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }
    @XmlTransient
    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }
    @XmlTransient
    public String getVoucherType() {
        return voucherType;
    }

    public void setVoucherType(String voucherType) {
        this.voucherType = voucherType;
    }
    @XmlTransient
    public String getVoucharDate() {
        return voucharDate;
    }

    public void setVoucharDate(String voucharDate) {
        this.voucharDate = voucharDate;
    }
    @XmlTransient
    public String getYearMonth() {
        return yearMonth;
    }

    public void setYearMonth(String yearMonth) {
        this.yearMonth = yearMonth;
    }
    @XmlTransient
    public String getAuxNumber() {
        return auxNumber;
    }

    public void setAuxNumber(String auxNumber) {
        this.auxNumber = auxNumber;
    }
    @XmlTransient
    public String getEntryNumber() {
        return entryNumber;
    }

    public void setEntryNumber(String entryNumber) {
        this.entryNumber = entryNumber;
    }
    @XmlTransient
    public String getAccbookType() {
        return accbookType;
    }

    public void setAccbookType(String accbookType) {
        this.accbookType = accbookType;
    }
    @XmlTransient
    public String getAccbookCode() {
        return accbookCode;
    }

    public void setAccbookCode(String accbookCode) {
        this.accbookCode = accbookCode;
    }
    @XmlTransient
    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }
    @XmlTransient
    public String getCenterCode() {
        return centerCode;
    }

    public void setCenterCode(String centerCode) {
        this.centerCode = centerCode;
    }
    @XmlTransient
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
