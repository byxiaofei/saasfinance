package com.sinosoft.domain.account;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "accmainvoucher")
public class AccMainVoucher {

    @EmbeddedId
    private AccMainVoucherId  id;
    private String voucherType ;//凭证类型
    private String generateWay ;//录入方式

    private String   voucherDate ;//凭证日期
    @Column(nullable = true)
    private Integer auxNumber ;//附件张数

    private String createBy ;//制单人
    @Column(nullable = true)
    private String createBranchCode ;//制单人单位

    private String approveBy ;//复核人
    @Column(nullable = true)
    private String approveBranchCode ;//复核人单位
    @Column(nullable = true)
    private String   approveDate ;//复核日期

    private String geneBy ;//记账人
    private String geneBranchCode ;//记账人单位
    private String   geneDate ;//记账日期

    private String voucherFlag ;//生成凭证标志
    private String flag ;//标志
    private String createTime ;//创建时间
    private String modifyReason ;//修改原因
    private String lastModifyBy ;
    private String lastModifyTime ;
    private String dataSource;//数据来源
    private String temp ;
    private String temp1 ;
    private String temp2 ;
    private String temp3 ;
    private String temp4 ;
    private String temp5 ;

    public AccMainVoucherId getId() {
        return id;
    }

    public void setId(AccMainVoucherId id) {
        this.id = id;
    }

    public String getVoucherType() {
        return voucherType;
    }

    public void setVoucherType(String voucherType) {
        this.voucherType = voucherType;
    }

    public String getGenerateWay() {
        return generateWay;
    }

    public void setGenerateWay(String generateWay) {
        this.generateWay = generateWay;
    }

    public String getVoucherDate() {
        return voucherDate;
    }

    public void setVoucherDate(String voucherDate) {
        this.voucherDate = voucherDate;
    }

    public Integer getAuxNumber() {
        return auxNumber;
    }

    public void setAuxNumber(Integer auxNumber) {
        this.auxNumber = auxNumber;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getCreateBranchCode() {
        return createBranchCode;
    }

    public void setCreateBranchCode(String createBranchCode) {
        this.createBranchCode = createBranchCode;
    }

    public String getApproveBy() {
        return approveBy;
    }

    public void setApproveBy(String approveBy) {
        this.approveBy = approveBy;
    }

    public String getApproveBranchCode() {
        return approveBranchCode;
    }

    public void setApproveBranchCode(String approveBranchCode) {
        this.approveBranchCode = approveBranchCode;
    }

    public String getGeneBy() {
        return geneBy;
    }

    public void setGeneBy(String geneBy) {
        this.geneBy = geneBy;
    }

    public String getGeneBranchCode() {
        return geneBranchCode;
    }

    public void setGeneBranchCode(String geneBranchCode) {
        this.geneBranchCode = geneBranchCode;
    }

    public String getVoucherFlag() {
        return voucherFlag;
    }

    public void setVoucherFlag(String voucherFlag) {
        this.voucherFlag = voucherFlag;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getModifyReason() {
        return modifyReason;
    }

    public void setModifyReason(String modifyReason) {
        this.modifyReason = modifyReason;
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

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getTemp1() {
        return temp1;
    }

    public void setTemp1(String temp1) {
        this.temp1 = temp1;
    }

    public String getTemp2() {
        return temp2;
    }

    public void setTemp2(String temp2) {
        this.temp2 = temp2;
    }

    public String getTemp3() {
        return temp3;
    }

    public void setTemp3(String temp3) {
        this.temp3 = temp3;
    }

    public String getTemp4() {
        return temp4;
    }

    public void setTemp4(String temp4) {
        this.temp4 = temp4;
    }

    public String getTemp5() {
        return temp5;
    }

    public void setTemp5(String temp5) {
        this.temp5 = temp5;
    }

    public String getApproveDate() {
        return approveDate;
    }

    public void setApproveDate(String approveDate) {
        this.approveDate = approveDate;
    }

    public String getGeneDate() {
        return geneDate;
    }

    public void setGeneDate(String geneDate) {
        this.geneDate = geneDate;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }
}
