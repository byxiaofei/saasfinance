package com.sinosoft.domain.account;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "accsubvoucherhis")
public class AccSubVoucherHis {

    @EmbeddedId
    private AccSubVoucherId  id;
    private String itemCode ;//      一级科目代码
    private String f01 ;
    private String f02 ;
    private String f03 ;
    private String f04 ;
    private String f05 ;
    private String f06 ;
    private String f07 ;
    private String f08 ;
    private String f09 ;
    private String f10 ;
    private String f11 ;
    private String f12 ;
    private String f13 ;
    private String f14 ;
    private String f15 ;
    private String f16 ;
    private String f17 ;
    private String f18 ;
    private String f19 ;
    private String f20 ;
    private String f21 ;
    private String f22 ;
    private String f23 ;
    private String f24 ;
    private String f25 ;
    private String f26 ;
    private String f27 ;
    private String f28 ;
    private String f29 ;
    private String f30 ;
    private String s01 ;
    private String s02 ;
    private String s03 ;
    private String s04 ;
    private String s05 ;
    private String s06 ;
    private String s07 ;
    private String s08 ;
    private String s09 ;
    private String s10 ;
    private String s11 ;
    private String s12 ;
    private String s13 ;
    private String s14 ;
    private String s15 ;
    private String s16 ;
    private String s17 ;
    private String s18 ;
    private String s19 ;
    private String s20 ;
    private String d01;//单位
    private String d02;//数量
    private String d03;//结算类型
    private String d04;//结算单号
    private String d05;//结算日期

    private String directionIdx ; //科目方向段
    private String directionIdxName ;// 科目方向段名
    private String directionOther ;//专项方向段
    private String remark ;//摘要
    private String flag;//标注
    private String checkNo ;//支票号
    private String invoiceNo ;//发票号
    private String currency ;//原币别编码
    private BigDecimal exchangeRate ;//当前汇率
    private BigDecimal debitSource ;//原币借方金额
    private BigDecimal creditSource ;//原币贷方金额
    private BigDecimal debitDest ;//本位币借方金额
    private BigDecimal creditDest ;//本位币贷方金额

    private String createBy ;//创建人
    private String createTime ;//创建时间
    private String modifyReason ;//修改原因
    private String lastModifyBy ;//最后一次修改人
    private String lastModifyTime ;//最后一次修改时间
    private String temp ;//备注
    private String temp1 ;
    private String temp2 ;
    private String temp3 ;
    private String temp4 ;
    private String temp5 ;

    public AccSubVoucherId getId() {
        return id;
    }

    public void setId(AccSubVoucherId id) {
        this.id = id;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getF01() {
        return f01;
    }

    public void setF01(String f01) {
        this.f01 = f01;
    }

    public String getF02() {
        return f02;
    }

    public void setF02(String f02) {
        this.f02 = f02;
    }

    public String getF03() {
        return f03;
    }

    public void setF03(String f03) {
        this.f03 = f03;
    }

    public String getF04() {
        return f04;
    }

    public void setF04(String f04) {
        this.f04 = f04;
    }

    public String getF05() {
        return f05;
    }

    public void setF05(String f05) {
        this.f05 = f05;
    }

    public String getF06() {
        return f06;
    }

    public void setF06(String f06) {
        this.f06 = f06;
    }

    public String getF07() {
        return f07;
    }

    public void setF07(String f07) {
        this.f07 = f07;
    }

    public String getF08() {
        return f08;
    }

    public void setF08(String f08) {
        this.f08 = f08;
    }

    public String getF09() {
        return f09;
    }

    public void setF09(String f09) {
        this.f09 = f09;
    }

    public String getF10() {
        return f10;
    }

    public void setF10(String f10) {
        this.f10 = f10;
    }

    public String getF11() {
        return f11;
    }

    public void setF11(String f11) {
        this.f11 = f11;
    }

    public String getF12() {
        return f12;
    }

    public void setF12(String f12) {
        this.f12 = f12;
    }

    public String getF13() {
        return f13;
    }

    public void setF13(String f13) {
        this.f13 = f13;
    }

    public String getF14() {
        return f14;
    }

    public void setF14(String f14) {
        this.f14 = f14;
    }

    public String getF15() {
        return f15;
    }

    public void setF15(String f15) {
        this.f15 = f15;
    }

    public String getF16() {
        return f16;
    }

    public void setF16(String f16) {
        this.f16 = f16;
    }

    public String getF17() {
        return f17;
    }

    public void setF17(String f17) {
        this.f17 = f17;
    }

    public String getF18() {
        return f18;
    }

    public void setF18(String f18) {
        this.f18 = f18;
    }

    public String getF19() {
        return f19;
    }

    public void setF19(String f19) {
        this.f19 = f19;
    }

    public String getF20() {
        return f20;
    }

    public void setF20(String f20) {
        this.f20 = f20;
    }

    public String getF21() {
        return f21;
    }

    public void setF21(String f21) {
        this.f21 = f21;
    }

    public String getF22() {
        return f22;
    }

    public void setF22(String f22) {
        this.f22 = f22;
    }

    public String getF23() {
        return f23;
    }

    public void setF23(String f23) {
        this.f23 = f23;
    }

    public String getF24() {
        return f24;
    }

    public void setF24(String f24) {
        this.f24 = f24;
    }

    public String getF25() {
        return f25;
    }

    public void setF25(String f25) {
        this.f25 = f25;
    }

    public String getF26() {
        return f26;
    }

    public void setF26(String f26) {
        this.f26 = f26;
    }

    public String getF27() {
        return f27;
    }

    public void setF27(String f27) {
        this.f27 = f27;
    }

    public String getF28() {
        return f28;
    }

    public void setF28(String f28) {
        this.f28 = f28;
    }

    public String getF29() {
        return f29;
    }

    public void setF29(String f29) {
        this.f29 = f29;
    }

    public String getF30() {
        return f30;
    }

    public void setF30(String f30) {
        this.f30 = f30;
    }

    public String getS01() {
        return s01;
    }

    public void setS01(String s01) {
        this.s01 = s01;
    }

    public String getS02() {
        return s02;
    }

    public void setS02(String s02) {
        this.s02 = s02;
    }

    public String getS03() {
        return s03;
    }

    public void setS03(String s03) {
        this.s03 = s03;
    }

    public String getS04() {
        return s04;
    }

    public void setS04(String s04) {
        this.s04 = s04;
    }

    public String getS05() {
        return s05;
    }

    public void setS05(String s05) {
        this.s05 = s05;
    }

    public String getS06() {
        return s06;
    }

    public void setS06(String s06) {
        this.s06 = s06;
    }

    public String getS07() {
        return s07;
    }

    public void setS07(String s07) {
        this.s07 = s07;
    }

    public String getS08() {
        return s08;
    }

    public void setS08(String s08) {
        this.s08 = s08;
    }

    public String getS09() {
        return s09;
    }

    public void setS09(String s09) {
        this.s09 = s09;
    }

    public String getS10() {
        return s10;
    }

    public void setS10(String s10) {
        this.s10 = s10;
    }

    public String getS11() {
        return s11;
    }

    public void setS11(String s11) {
        this.s11 = s11;
    }

    public String getS12() {
        return s12;
    }

    public void setS12(String s12) {
        this.s12 = s12;
    }

    public String getS13() {
        return s13;
    }

    public void setS13(String s13) {
        this.s13 = s13;
    }

    public String getS14() {
        return s14;
    }

    public void setS14(String s14) {
        this.s14 = s14;
    }

    public String getS15() {
        return s15;
    }

    public void setS15(String s15) {
        this.s15 = s15;
    }

    public String getS16() {
        return s16;
    }

    public void setS16(String s16) {
        this.s16 = s16;
    }

    public String getS17() {
        return s17;
    }

    public void setS17(String s17) {
        this.s17 = s17;
    }

    public String getS18() {
        return s18;
    }

    public void setS18(String s18) {
        this.s18 = s18;
    }

    public String getS19() {
        return s19;
    }

    public void setS19(String s19) {
        this.s19 = s19;
    }

    public String getS20() {
        return s20;
    }

    public void setS20(String s20) {
        this.s20 = s20;
    }

    public String getD01() {
        return d01;
    }

    public void setD01(String d01) {
        this.d01 = d01;
    }

    public String getD02() {
        return d02;
    }

    public void setD02(String d02) {
        this.d02 = d02;
    }

    public String getD03() {
        return d03;
    }

    public void setD03(String d03) {
        this.d03 = d03;
    }

    public String getD04() {
        return d04;
    }

    public void setD04(String d04) {
        this.d04 = d04;
    }

    public String getD05() {
        return d05;
    }

    public void setD05(String d05) {
        this.d05 = d05;
    }

    public String getDirectionIdx() {
        return directionIdx;
    }

    public void setDirectionIdx(String directionIdx) {
        this.directionIdx = directionIdx;
    }

    public String getDirectionIdxName() {
        return directionIdxName;
    }

    public void setDirectionIdxName(String directionIdxName) {
        this.directionIdxName = directionIdxName;
    }

    public String getDirectionOther() {
        return directionOther;
    }

    public void setDirectionOther(String directionOther) {
        this.directionOther = directionOther;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getFlag() { return flag; }

    public void setFlag(String flag) { this.flag = flag; }

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

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public BigDecimal getDebitSource() {
        return debitSource;
    }

    public void setDebitSource(BigDecimal debitSource) {
        this.debitSource = debitSource;
    }

    public BigDecimal getCreditSource() {
        return creditSource;
    }

    public void setCreditSource(BigDecimal creditSource) {
        this.creditSource = creditSource;
    }

    public BigDecimal getDebitDest() {
        return debitDest;
    }

    public void setDebitDest(BigDecimal debitDest) {
        this.debitDest = debitDest;
    }

    public BigDecimal getCreditDest() {
        return creditDest;
    }

    public void setCreditDest(BigDecimal creditDest) {
        this.creditDest = creditDest;
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
}
