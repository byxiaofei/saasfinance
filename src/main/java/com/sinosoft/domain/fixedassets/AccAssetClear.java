package com.sinosoft.domain.fixedassets;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * @author jiangyc
 * @Description
 * @create 2019-03-26 13:57
 */
@Entity
@Table(name = "accassetclear")
public class AccAssetClear {
    @EmbeddedId
    private AccAssetClearId id;     //联合主键id
    private String payWay1;         //收付方式
    private String accountCode1;    //收付款帐号
    private String checkNo1;        //支票号
    private String auxNumber1;      //附件张数
    private BigDecimal soldMoney;   //固定资产出售金额
    private String payWay2;         //收付方式
    private String accountCode2;    //收付款帐号
    private String checkNo2;        //支票号
    private String auxNumber2;      //附件张数
    private String temp;            //附件张数

    public AccAssetClearId getId() {
        return id;
    }

    public void setId(AccAssetClearId id) {
        this.id = id;
    }

    public String getPayWay1() {
        return payWay1;
    }

    public void setPayWay1(String payWay1) {
        this.payWay1 = payWay1;
    }

    public String getAccountCode1() {
        return accountCode1;
    }

    public void setAccountCode1(String accountCode1) {
        this.accountCode1 = accountCode1;
    }

    public String getCheckNo1() {
        return checkNo1;
    }

    public void setCheckNo1(String checkNo1) {
        this.checkNo1 = checkNo1;
    }

    public String getAuxNumber1() {
        return auxNumber1;
    }

    public void setAuxNumber1(String auxNumber1) {
        this.auxNumber1 = auxNumber1;
    }

    public BigDecimal getSoldMoney() {
        return soldMoney;
    }

    public void setSoldMoney(BigDecimal soldMoney) {
        this.soldMoney = soldMoney;
    }

    public String getPayWay2() {
        return payWay2;
    }

    public void setPayWay2(String payWay2) {
        this.payWay2 = payWay2;
    }

    public String getAccountCode2() {
        return accountCode2;
    }

    public void setAccountCode2(String accountCode2) {
        this.accountCode2 = accountCode2;
    }

    public String getCheckNo2() {
        return checkNo2;
    }

    public void setCheckNo2(String checkNo2) {
        this.checkNo2 = checkNo2;
    }

    public String getAuxNumber2() {
        return auxNumber2;
    }

    public void setAuxNumber2(String auxNumber2) {
        this.auxNumber2 = auxNumber2;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }
}
