package com.sinosoft.domain.Report;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "reportcompute")
public class ReportCompute {

    @EmbeddedId
    private ReportComputeId id;//联合主键
    private Integer computeLever;//计算优先级，1为最大优先级
    private String reportName;
    private String d1; //
    private String d1K;
    private String d1V;
    private String d2;
    private String d2K;
    private String d2V;
    private String d3;
    private String d3K;
    private String d3V;
    private String d4;
    private String d4K;
    private String d4V;
    private String d5;
    private String d5K;
    private String d5V;
    private String d6;
    private String d6K;
    private String d6V;
    private String d7;
    private String d7K;
    private String d7V;
    private String d8;
    private String d8K;
    private String d8V;
    private String d9;
    private String d9K;
    private String d9V;
    private String d10;
    private String d10K;
    private String d10V;
    private String d11;
    private String d11K;
    private String d11V;
    private String d12;
    private String d12K;
    private String d12V;
    private String d13;
    private String d13K;
    private String d13V;
    private String d14;
    private String d14K;
    private String d14V;
    private String d15;
    private String d15K;
    private String d15V;
    private String d16;
    private String d16K;
    private String d16V;
    private String d17;
    private String d17K;
    private String d17V;
    private String d18;
    private String d18K;
    private String d18V;
    private String d19;
    private String d19K;
    private String d19V;
    private String d20;
    private String d20K;
    private String d20V;
    private String d21;
    private String d21K;
    private String d21V;
    private String d22;
    private String d22K;
    private String d22V;
    private String d23;
    private String d23K;
    private String d23V;
    private String d24;
    private String d24K;
    private String d24V;
    private String d25;
    private String d25K;
    private String d25V;
    private String d26;
    private String d26K;
    private String d26V;
    private String d27;
    private String d27K;
    private String d27V;
    private String d28;
    private String d28K;
    private String d28V;
    private String d29;
    private String d29K;
    private String d29V;
    private String d30;
    private String d30K;
    private String d30V;

    private String createBy;
    private String createTime;
//    private String lastModifyBy;
//    private String lastModifyTime;

    public ReportComputeId getId() {
        return id;
    }

    public void setId(ReportComputeId id) {
        this.id = id;
    }

    public Integer getComputeLever() {
        return computeLever;
    }

    public void setComputeLever(Integer computeLever) {
        this.computeLever = computeLever;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getD1() {
        return d1;
    }

    public void setD1(String d1) {
        this.d1 = d1;
    }

    public String getD1K() {
        return d1K;
    }

    public void setD1K(String d1K) {
        this.d1K = d1K;
    }

    public String getD1V() {
        return d1V;
    }

    public void setD1V(String d1V) {
        this.d1V = d1V;
    }

    public String getD2() {
        return d2;
    }

    public void setD2(String d2) {
        this.d2 = d2;
    }

    public String getD2K() {
        return d2K;
    }

    public void setD2K(String d2K) {
        this.d2K = d2K;
    }

    public String getD2V() {
        return d2V;
    }

    public void setD2V(String d2V) {
        this.d2V = d2V;
    }

    public String getD3() {
        return d3;
    }

    public void setD3(String d3) {
        this.d3 = d3;
    }

    public String getD3K() {
        return d3K;
    }

    public void setD3K(String d3K) {
        this.d3K = d3K;
    }

    public String getD3V() {
        return d3V;
    }

    public void setD3V(String d3V) {
        this.d3V = d3V;
    }

    public String getD4() {
        return d4;
    }

    public void setD4(String d4) {
        this.d4 = d4;
    }

    public String getD4K() {
        return d4K;
    }

    public void setD4K(String d4K) {
        this.d4K = d4K;
    }

    public String getD4V() {
        return d4V;
    }

    public void setD4V(String d4V) {
        this.d4V = d4V;
    }

    public String getD5() {
        return d5;
    }

    public void setD5(String d5) {
        this.d5 = d5;
    }

    public String getD5K() {
        return d5K;
    }

    public void setD5K(String d5K) {
        this.d5K = d5K;
    }

    public String getD5V() {
        return d5V;
    }

    public void setD5V(String d5V) {
        this.d5V = d5V;
    }

    public String getD6() {
        return d6;
    }

    public void setD6(String d6) {
        this.d6 = d6;
    }

    public String getD6K() {
        return d6K;
    }

    public void setD6K(String d6K) {
        this.d6K = d6K;
    }

    public String getD6V() {
        return d6V;
    }

    public void setD6V(String d6V) {
        this.d6V = d6V;
    }

    public String getD7() {
        return d7;
    }

    public void setD7(String d7) {
        this.d7 = d7;
    }

    public String getD7K() {
        return d7K;
    }

    public void setD7K(String d7K) {
        this.d7K = d7K;
    }

    public String getD7V() {
        return d7V;
    }

    public void setD7V(String d7V) {
        this.d7V = d7V;
    }

    public String getD8() {
        return d8;
    }

    public void setD8(String d8) {
        this.d8 = d8;
    }

    public String getD8K() {
        return d8K;
    }

    public void setD8K(String d8K) {
        this.d8K = d8K;
    }

    public String getD8V() {
        return d8V;
    }

    public void setD8V(String d8V) {
        this.d8V = d8V;
    }

    public String getD9() {
        return d9;
    }

    public void setD9(String d9) {
        this.d9 = d9;
    }

    public String getD9K() {
        return d9K;
    }

    public void setD9K(String d9K) {
        this.d9K = d9K;
    }

    public String getD9V() {
        return d9V;
    }

    public void setD9V(String d9V) {
        this.d9V = d9V;
    }

    public String getD10() {
        return d10;
    }

    public void setD10(String d10) {
        this.d10 = d10;
    }

    public String getD10K() {
        return d10K;
    }

    public void setD10K(String d10K) {
        this.d10K = d10K;
    }

    public String getD10V() {
        return d10V;
    }

    public void setD10V(String d10V) {
        this.d10V = d10V;
    }

    public String getD11() {
        return d11;
    }

    public void setD11(String d11) {
        this.d11 = d11;
    }

    public String getD11K() {
        return d11K;
    }

    public void setD11K(String d11K) {
        this.d11K = d11K;
    }

    public String getD11V() {
        return d11V;
    }

    public void setD11V(String d11V) {
        this.d11V = d11V;
    }

    public String getD12() {
        return d12;
    }

    public void setD12(String d12) {
        this.d12 = d12;
    }

    public String getD12K() {
        return d12K;
    }

    public void setD12K(String d12K) {
        this.d12K = d12K;
    }

    public String getD12V() {
        return d12V;
    }

    public void setD12V(String d12V) {
        this.d12V = d12V;
    }

    public String getD13() {
        return d13;
    }

    public void setD13(String d13) {
        this.d13 = d13;
    }

    public String getD13K() {
        return d13K;
    }

    public void setD13K(String d13K) {
        this.d13K = d13K;
    }

    public String getD13V() {
        return d13V;
    }

    public void setD13V(String d13V) {
        this.d13V = d13V;
    }

    public String getD14() {
        return d14;
    }

    public void setD14(String d14) {
        this.d14 = d14;
    }

    public String getD14K() {
        return d14K;
    }

    public void setD14K(String d14K) {
        this.d14K = d14K;
    }

    public String getD14V() {
        return d14V;
    }

    public void setD14V(String d14V) {
        this.d14V = d14V;
    }

    public String getD15() {
        return d15;
    }

    public void setD15(String d15) {
        this.d15 = d15;
    }

    public String getD15K() {
        return d15K;
    }

    public void setD15K(String d15K) {
        this.d15K = d15K;
    }

    public String getD15V() {
        return d15V;
    }

    public void setD15V(String d15V) {
        this.d15V = d15V;
    }

    public String getD16() {
        return d16;
    }

    public void setD16(String d16) {
        this.d16 = d16;
    }

    public String getD16K() {
        return d16K;
    }

    public void setD16K(String d16K) {
        this.d16K = d16K;
    }

    public String getD16V() {
        return d16V;
    }

    public void setD16V(String d16V) {
        this.d16V = d16V;
    }

    public String getD17() {
        return d17;
    }

    public void setD17(String d17) {
        this.d17 = d17;
    }

    public String getD17K() {
        return d17K;
    }

    public void setD17K(String d17K) {
        this.d17K = d17K;
    }

    public String getD17V() {
        return d17V;
    }

    public void setD17V(String d17V) {
        this.d17V = d17V;
    }

    public String getD18() {
        return d18;
    }

    public void setD18(String d18) {
        this.d18 = d18;
    }

    public String getD18K() {
        return d18K;
    }

    public void setD18K(String d18K) {
        this.d18K = d18K;
    }

    public String getD18V() {
        return d18V;
    }

    public void setD18V(String d18V) {
        this.d18V = d18V;
    }

    public String getD19() {
        return d19;
    }

    public void setD19(String d19) {
        this.d19 = d19;
    }

    public String getD19K() {
        return d19K;
    }

    public void setD19K(String d19K) {
        this.d19K = d19K;
    }

    public String getD19V() {
        return d19V;
    }

    public void setD19V(String d19V) {
        this.d19V = d19V;
    }

    public String getD20() {
        return d20;
    }

    public void setD20(String d20) {
        this.d20 = d20;
    }

    public String getD20K() {
        return d20K;
    }

    public void setD20K(String d20K) {
        this.d20K = d20K;
    }

    public String getD20V() {
        return d20V;
    }

    public void setD20V(String d20V) {
        this.d20V = d20V;
    }

    public String getD21() {
        return d21;
    }

    public void setD21(String d21) {
        this.d21 = d21;
    }

    public String getD21K() {
        return d21K;
    }

    public void setD21K(String d21K) {
        this.d21K = d21K;
    }

    public String getD21V() {
        return d21V;
    }

    public void setD21V(String d21V) {
        this.d21V = d21V;
    }

    public String getD22() {
        return d22;
    }

    public void setD22(String d22) {
        this.d22 = d22;
    }

    public String getD22K() {
        return d22K;
    }

    public void setD22K(String d22K) {
        this.d22K = d22K;
    }

    public String getD22V() {
        return d22V;
    }

    public void setD22V(String d22V) {
        this.d22V = d22V;
    }

    public String getD23() {
        return d23;
    }

    public void setD23(String d23) {
        this.d23 = d23;
    }

    public String getD23K() {
        return d23K;
    }

    public void setD23K(String d23K) {
        this.d23K = d23K;
    }

    public String getD23V() {
        return d23V;
    }

    public void setD23V(String d23V) {
        this.d23V = d23V;
    }

    public String getD24() {
        return d24;
    }

    public void setD24(String d24) {
        this.d24 = d24;
    }

    public String getD24K() {
        return d24K;
    }

    public void setD24K(String d24K) {
        this.d24K = d24K;
    }

    public String getD24V() {
        return d24V;
    }

    public void setD24V(String d24V) {
        this.d24V = d24V;
    }

    public String getD25() {
        return d25;
    }

    public void setD25(String d25) {
        this.d25 = d25;
    }

    public String getD25K() {
        return d25K;
    }

    public void setD25K(String d25K) {
        this.d25K = d25K;
    }

    public String getD25V() {
        return d25V;
    }

    public void setD25V(String d25V) {
        this.d25V = d25V;
    }

    public String getD26() {
        return d26;
    }

    public void setD26(String d26) {
        this.d26 = d26;
    }

    public String getD26K() {
        return d26K;
    }

    public void setD26K(String d26K) {
        this.d26K = d26K;
    }

    public String getD26V() {
        return d26V;
    }

    public void setD26V(String d26V) {
        this.d26V = d26V;
    }

    public String getD27() {
        return d27;
    }

    public void setD27(String d27) {
        this.d27 = d27;
    }

    public String getD27K() {
        return d27K;
    }

    public void setD27K(String d27K) {
        this.d27K = d27K;
    }

    public String getD27V() {
        return d27V;
    }

    public void setD27V(String d27V) {
        this.d27V = d27V;
    }

    public String getD28() {
        return d28;
    }

    public void setD28(String d28) {
        this.d28 = d28;
    }

    public String getD28K() {
        return d28K;
    }

    public void setD28K(String d28K) {
        this.d28K = d28K;
    }

    public String getD28V() {
        return d28V;
    }

    public void setD28V(String d28V) {
        this.d28V = d28V;
    }

    public String getD29() {
        return d29;
    }

    public void setD29(String d29) {
        this.d29 = d29;
    }

    public String getD29K() {
        return d29K;
    }

    public void setD29K(String d29K) {
        this.d29K = d29K;
    }

    public String getD29V() {
        return d29V;
    }

    public void setD29V(String d29V) {
        this.d29V = d29V;
    }

    public String getD30() {
        return d30;
    }

    public void setD30(String d30) {
        this.d30 = d30;
    }

    public String getD30K() {
        return d30K;
    }

    public void setD30K(String d30K) {
        this.d30K = d30K;
    }

    public String getD30V() {
        return d30V;
    }

    public void setD30V(String d30V) {
        this.d30V = d30V;
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

}
