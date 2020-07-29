package com.sinosoft.domain.account;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "acccheckresult")
public class AccCheckResult implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(nullable = false)
    private String centerCode ;//核算单位
    @Column(nullable = false)
    private String accBookType ;//账套类型
    @Column(nullable = false)
    private String accBookCode ;//账套编码
    @Column(nullable = false)
    private String yearMonthDate ;//凭证年月
    @Column(nullable = false)
    private String isBalance ;//平衡情况，0-平衡 1-不平
    @Column(nullable = true)
    private String itemCode;//科目及名称
    @Column(nullable = true)
    private String detail ;//详细
    @Column(nullable = false)
    private String checkType ;//对账类型，0-总账与明细账 1-总账与辅助账 2-辅助账与明细账

    public AccCheckResult() { }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCenterCode() {
        return centerCode;
    }

    public void setCenterCode(String centerCode) {
        this.centerCode = centerCode;
    }

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

    public String getYearMonthDate() {
        return yearMonthDate;
    }

    public void setYearMonthDate(String yearMonthDate) {
        this.yearMonthDate = yearMonthDate;
    }

    public String getIsBalance() {
        return isBalance;
    }

    public void setIsBalance(String isBalance) {
        this.isBalance = isBalance;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getCheckType() {
        return checkType;
    }

    public void setCheckType(String checkType) {
        this.checkType = checkType;
    }
}
