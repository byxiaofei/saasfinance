package com.sinosoft.domain;

import javax.persistence.*;

/**
 * @Auther: luodejun
 * @Date: 2020/5/14 13:53
 * @Description:
 */
@Entity
@Table(name = "interfaceinfo")
public class InterfaceInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private String branchCode;
    @Column
    private String policDate;
    @Column
    private String fileName;
    @Column
    private String loadTime;
    @Column
    private String resultInfo;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getPolicDate() {
        return policDate;
    }

    public void setPolicDate(String policDate) {
        this.policDate = policDate;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getLoadTime() {
        return loadTime;
    }

    public void setLoadTime(String loadTime) {
        this.loadTime = loadTime;
    }

    public String getResultInfo() {
        return resultInfo;
    }

    public void setResultInfo(String resultInfo) {
        this.resultInfo = resultInfo;
    }
}
