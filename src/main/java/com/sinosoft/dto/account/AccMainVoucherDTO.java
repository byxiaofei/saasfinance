package com.sinosoft.dto.account;

import java.util.Date;

public class AccMainVoucherDTO {

    private String centerCode ;//核算单位
    private String branchCode ;//基层单位
    private String accBookType ;//账套类型
    private String accBookCode ;//账套编码
    private String yearMonth ;//年月
    private String voucherNo ;//凭证号
    private String voucherType ;//凭证类型
    private String generateWay ;//录入方式
    private String voucherDate ;//凭证日期
    private String auxNumber ;//附件张数
    private String createBy ;//制单人
    private String createBranchCode ;//制单人单位
    private String approveBy ;//复核人
    private String approveBranchCode ;//复核人单位
    private Date   approveDate ;//复核日期
    private String geneBy ;//记账人
    private String geneBranchCode ;//记账人单位
    private Date   geneDate ;//记账日期
    private String voucherFlag ;//生成凭证标志
    private String flag ;//标志
    private String createTime ;//创建时间
    private String modifyReason ;//修改原因
    private String lastModifyBy ;
    private String lastModifyTime ;
    private String temp ;

    private String subjectCode ;//科目代码
    private String subjectCodeAn ;//对方科目代码
    private float  Money ;
    private String Status ;//凭证状态
    private String remarkCode ;//摘要代码
    private String remarkName ;//摘要名称
    private String specialCode ;//专项代码
    private String specialNameP ;//专项全称

    public String getCenterCode() { return centerCode; }
    public void setCenterCode(String centerCode) { this.centerCode = centerCode; }

    public String getBranchCode() { return branchCode; }
    public void setBranchCode(String branchCode) { this.branchCode = branchCode; }

    public String getAccBookType() { return accBookType; }
    public void setAccBookType(String accBookType) { this.accBookType = accBookType; }

    public String getAccBookCode() { return accBookCode; }
    public void setAccBookCode(String accBookCode) { this.accBookCode = accBookCode; }

    public String getYearMonth() { return yearMonth; }
    public void setYearMonth(String yearMonth) { this.yearMonth = yearMonth; }

    public String getVoucherNo() { return voucherNo; }
    public void setVoucherNo(String voucherNo) { this.voucherNo = voucherNo; }

    public String getVoucherType() { return voucherType; }
    public void setVoucherType(String voucherType) { this.voucherType = voucherType; }

    public String getGenerateWay() { return generateWay; }
    public void setGenerateWay(String generateWay) { this.generateWay = generateWay; }

    public String getVoucherDate() { return voucherDate; }
    public void setVoucherDate(String voucherDate) { this.voucherDate = voucherDate; }

    public String getAuxNumber() { return auxNumber; }
    public void setAuxNumber(String auxNumber) { this.auxNumber = auxNumber; }

    public String getCreateBy() { return createBy; }
    public void setCreateBy(String createBy) { this.createBy = createBy; }

    public String getCreateBranchCode() { return createBranchCode; }
    public void setCreateBranchCode(String createBranchCode) { this.createBranchCode = createBranchCode; }

    public String getApproveBy() { return approveBy; }
    public void setApproveBy(String approveBy) { this.approveBy = approveBy; }

    public String getApproveBranchCode() { return approveBranchCode; }
    public void setApproveBranchCode(String approveBranchCode) { this.approveBranchCode = approveBranchCode; }

    public Date getApproveDate() { return approveDate; }
    public void setApproveDate(Date approveDate) { this.approveDate = approveDate; }

    public String getGeneBy() { return geneBy; }
    public void setGeneBy(String geneBy) { this.geneBy = geneBy; }

    public String getGeneBranchCode() { return geneBranchCode; }
    public void setGeneBranchCode(String geneBranchCode) { this.geneBranchCode = geneBranchCode; }

    public Date getGeneDate() { return geneDate; }
    public void setGeneDate(Date geneDate) { this.geneDate = geneDate; }

    public String getVoucherFlag() { return voucherFlag; }
    public void setVoucherFlag(String voucherFlag) { this.voucherFlag = voucherFlag; }

    public String getFlag() { return flag; }
    public void setFlag(String flag) { this.flag = flag; }

    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }

    public String getModifyReason() { return modifyReason; }
    public void setModifyReason(String modifyReason) { this.modifyReason = modifyReason; }

    public String getLastModifyBy() { return lastModifyBy; }
    public void setLastModifyBy(String lastModifyBy) { this.lastModifyBy = lastModifyBy; }

    public String getLastModifyTime() { return lastModifyTime; }
    public void setLastModifyTime(String lastModifyTime) { this.lastModifyTime = lastModifyTime; }

    public String getTemp() { return temp; }
    public void setTemp(String temp) { this.temp = temp; }
}
