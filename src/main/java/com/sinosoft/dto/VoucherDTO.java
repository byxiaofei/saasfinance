package com.sinosoft.dto;

import java.io.Serializable;
import java.util.List;

public class VoucherDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String voucherNo;//凭证号
	private String voucherNoStart;//开始凭证号
	private String voucherNoEnd;//结束凭证号

	private String yearMonth;//会计期间
	private String yearMonthDate;//会计期间

	private String voucherDate;//制单日期
	private String voucherDateStart;//开始制单日期
	private String voucherDateEnd;//结束制单日期

	private String createBy;//制单人
	private String approveBy;//审批人
	private String geneBy;//记账人
	private String approveDate;//审核日期
	private String auxNumber;//附件张数
	private String oldVoucherNo;//已有凭证号
	private String tagCode;//标注名称
	private String tagCodeS;//标注编码
	private String remarkName;//摘要
	private String subjectCode;//科目代码
	private String subjectName;//科目名称
	private String subjectNameP;//科目全称
	private String generateWay;//凭证录入方式
    private String voucherType;//凭证类型
    private String voucherFlag;//凭证状态
	private String id;
	private String specialCode;
	private String specialName;
	private String specialNameP;//专项全称

	private String debit;//借方
	private String credit;//贷方

    private String source;//金额
	private String sourceDirection;//金额方向
	private String moneyStart;//最小金额
	private String moneyEnd;//最大金额

	private String subjectCodeS;//科目代码
	private String subjectNameS;//科目名称
	private String specialCodeS;//专项代码
	private String specialSuperCodeS;//一级专项代码，与specialCodeS对应

	private String unitPrice;//单价
	private String unitNum;//数量（单位）
	private String settlementType;//结算类型
	private String settlementTypeName;//结算类型
	private String settlementNo;//结算单号
	private String settlementDate;//结算日期

	private String itemCode1;//科目代码（指借方科目代码）
	private String itemCode2;//对方科目代码（指贷方科目代码）
	private String level;//科目层级
	private String levelEnd;//科目层级

	private String accounRules;//核算规则
	private String orderingRule;//排序规则
	private String voucherGene;//是否包含未记账凭证（0-否 1-是）

	private String needCheckGeneBy;//需要比较记账人(暂用于12月反结转时反记账决算凭证)

	private String endFlag;// 0-末级，1-非末级

    private String temp;

    private String dataSource;

	private String checkNo ;//发票号
	private String invoiceNo ;//发票号

	private List<VoucherDTO> data2;
	private List<VoucherDTO> data3;

	private String copyType;//为1时是凭证复制 为2时是凭证对冲 ,为3时是查看或编辑,为4时是凭证批量打印（凭证打印界面）

	//  财套类型
	private String accBookType;
	//财套编码
	private String accBookCode;
	//  业务机构
	private String branchCode;
	//  核算中心
	private String centerCode;

	public String getAccBookType() {
		return accBookType;
	}

	public void setAccBookType(String accBookType) {
		this.accBookType = accBookType;
	}

	public String getAccBookCode() {
		return accBookCode;
	}

	public void setAccBookCode(String accBookCode) {
		this.accBookCode = accBookCode;
	}

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public String getCenterCode() {
		return centerCode;
	}

	public void setCenterCode(String centerCode) {
		this.centerCode = centerCode;
	}

	public String getYearMonthDate() {
		return yearMonthDate;
	}

	public void setYearMonthDate(String yearMonthDate) {
		this.yearMonthDate = yearMonthDate;
	}

	public String getVoucherNoStart() {
		return voucherNoStart;
	}

	public void setVoucherNoStart(String voucherNoStart) {
		this.voucherNoStart = voucherNoStart;
	}

	public String getVoucherNoEnd() {
		return voucherNoEnd;
	}

	public void setVoucherNoEnd(String voucherNoEnd) {
		this.voucherNoEnd = voucherNoEnd;
	}

	public String getVoucherDateStart() {
		return voucherDateStart;
	}

	public void setVoucherDateStart(String voucherDateStart) {
		this.voucherDateStart = voucherDateStart;
	}

	public String getVoucherDateEnd() {
		return voucherDateEnd;
	}

	public void setVoucherDateEnd(String voucherDateEnd) {
		this.voucherDateEnd = voucherDateEnd;
	}

	public String getMoneyStart() {
		return moneyStart;
	}

	public void setMoneyStart(String moneyStart) {
		this.moneyStart = moneyStart;
	}

	public String getMoneyEnd() {
		return moneyEnd;
	}

	public void setMoneyEnd(String moneyEnd) {
		this.moneyEnd = moneyEnd;
	}

	public String getApproveBy() {
		return approveBy;
	}

	public void setApproveBy(String approveBy) {
		this.approveBy = approveBy;
	}

	public String getGeneBy() {
		return geneBy;
	}

	public void setGeneBy(String geneBy) {
		this.geneBy = geneBy;
	}

	public String getApproveDate() {
		return approveDate;
	}

	public void setApproveDate(String approveDate) {
		this.approveDate = approveDate;
	}

	public String getCopyType() {
		return copyType;
	}

	public void setCopyType(String copyType) {
		this.copyType = copyType;
	}

	public List<VoucherDTO> getData2() {
		return data2;
	}

	public void setData2(List<VoucherDTO> data2) {
		this.data2 = data2;
	}

	public List<VoucherDTO> getData3() {
		return data3;
	}

	public void setData3(List<VoucherDTO> data3) {
		this.data3 = data3;
	}

	public String getVoucherFlag() {
        return voucherFlag;
    }

    public void setVoucherFlag(String voucherFlag) {
        this.voucherFlag = voucherFlag;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

	public String getGenerateWay() { return generateWay; }

	public void setGenerateWay(String generateWay) { this.generateWay = generateWay; }

	public String getVoucherType() {
        return voucherType;
    }

    public void setVoucherType(String voucherType) {
        this.voucherType = voucherType;
    }

    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSpecialCode() {
		return specialCode;
	}

	public void setSpecialCode(String specialCode) {
		this.specialCode = specialCode;
	}

	public String getSpecialName() {
		return specialName;
	}

	public void setSpecialName(String specialName) {
		this.specialName = specialName;
	}

	public String getSpecialNameP() { return specialNameP; }

	public void setSpecialNameP(String specialNameP) { this.specialNameP = specialNameP; }

	public String getVoucherNo() {
		return voucherNo;
	}

	public void setVoucherNo(String voucherNo) {
		this.voucherNo = voucherNo;
	}

	public String getYearMonth() {
		return yearMonth;
	}

	public void setYearMonth(String yearMonth) {
		this.yearMonth = yearMonth;
	}

	public String getVoucherDate() {
		return voucherDate;
	}

	public void setVoucherDate(String voucherDate) {
		this.voucherDate = voucherDate;
	}

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public String getAuxNumber() {
		return auxNumber;
	}

	public void setAuxNumber(String auxNumber) {
		this.auxNumber = auxNumber;
	}

	public String getOldVoucherNo() {
		return oldVoucherNo;
	}

	public void setOldVoucherNo(String oldVoucherNo) {
		this.oldVoucherNo = oldVoucherNo;
	}

	public String getRemarkName() {
		return remarkName;
	}

	public void setRemarkName(String remarkName) {
		this.remarkName = remarkName;
	}

	public String getSubjectCode() {
		return subjectCode;
	}

	public void setSubjectCode(String subjectCode) {
		this.subjectCode = subjectCode;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getTagCode() {
		return tagCode;
	}

	public void setTagCode(String tagCode) {
		this.tagCode = tagCode;
	}

	public String getDebit() {
		return debit;
	}

	public void setDebit(String debit) {
		this.debit = debit;
	}

	public String getCredit() {
		return credit;
	}

	public void setCredit(String credit) {
		this.credit = credit;
	}

	public String getSubjectCodeS() {
		return subjectCodeS;
	}

	public void setSubjectCodeS(String subjectCodeS) {
		this.subjectCodeS = subjectCodeS;
	}

	public String getSubjectNameS() {
		return subjectNameS;
	}

	public void setSubjectNameS(String subjectNameS) {
		this.subjectNameS = subjectNameS;
	}

	public String getSpecialCodeS() {
		return specialCodeS;
	}

	public void setSpecialCodeS(String specialCodeS) {
		this.specialCodeS = specialCodeS;
	}

	public String getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(String unitPrice) {
		this.unitPrice = unitPrice;
	}

	public String getUnitNum() {
		return unitNum;
	}

	public void setUnitNum(String unitNum) {
		this.unitNum = unitNum;
	}

	public String getSettlementType() {
		return settlementType;
	}

	public void setSettlementType(String settlementType) {
		this.settlementType = settlementType;
	}

	public String getSettlementTypeName() {
		return settlementTypeName;
	}

	public void setSettlementTypeName(String settlementTypeName) {
		this.settlementTypeName = settlementTypeName;
	}

	public String getSettlementNo() {
		return settlementNo;
	}

	public void setSettlementNo(String settlementNo) {
		this.settlementNo = settlementNo;
	}

	public String getSettlementDate() {
		return settlementDate;
	}

	public void setSettlementDate(String settlementDate) {
		this.settlementDate = settlementDate;
	}

	public String getSpecialSuperCodeS() { return specialSuperCodeS; }

	public void setSpecialSuperCodeS(String specialSuperCodeS) { this.specialSuperCodeS = specialSuperCodeS; }

	public String getTagCodeS() {
		return tagCodeS;
	}

	public void setTagCodeS(String tagCodeS) {
		this.tagCodeS = tagCodeS;
	}

	public String getItemCode1() { return itemCode1; }

	public void setItemCode1(String itemCode1) { this.itemCode1 = itemCode1; }

	public String getItemCode2() { return itemCode2; }

	public void setItemCode2(String itemCode2) { this.itemCode2 = itemCode2; }

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getLevelEnd() {
		return levelEnd;
	}

	public void setLevelEnd(String levelEnd) {
		this.levelEnd = levelEnd;
	}

	public String getSubjectNameP() {
		return subjectNameP;
	}

	public void setSubjectNameP(String subjectNameP) {
		this.subjectNameP = subjectNameP;
	}

	public String getSourceDirection() {
		return sourceDirection;
	}

	public void setSourceDirection(String sourceDirection) {
		this.sourceDirection = sourceDirection;
	}

	public String getAccounRules() {
		return accounRules;
	}

	public void setAccounRules(String accounRules) {
		this.accounRules = accounRules;
	}

	public String getVoucherGene() {
		return voucherGene;
	}

	public void setVoucherGene(String voucherGene) {
		this.voucherGene = voucherGene;
	}

	public String getOrderingRule() {
		return orderingRule;
	}

	public void setOrderingRule(String orderingRule) {
		this.orderingRule = orderingRule;
	}

	public String getNeedCheckGeneBy() {
		return needCheckGeneBy;
	}

	public void setNeedCheckGeneBy(String needCheckGeneBy) {
		this.needCheckGeneBy = needCheckGeneBy;
	}

	public String getEndFlag() {
		return endFlag;
	}

	public void setEndFlag(String endFlag) {
		this.endFlag = endFlag;
	}

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public String getCheckNo() {
		return checkNo;
	}

	public void setCheckNo(String checkNo) {
		this.checkNo = checkNo;
	}

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}
}
