package com.sinosoft.domain.account;



import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Auther: luodejun
 * @Date: 2020/3/23 21:43
 * @Description:
 */
@Entity
@Table(name = "acccurrencyexchangerates")
public class AccCurrencyExchangeRates implements Serializable {

    @EmbeddedId
    private AccCurrencyExchangeRatesId id;// 主键ID

    @Column(nullable = true)
    private String baseCurrencyName;//基础货币名称

    @Column(nullable = true)
    private String unbaseCurrencyName;//非基础货币名称

    @Column(nullable = true)
    private BigDecimal exchangeRate;//汇率

    @Column(nullable = true)
    private String  createPerson;//创建人

    @Column(nullable = true)
    private String createTime;//创建时间

    @Column(nullable = true)
    private String updatePerson;//修改人

    @Column(nullable = true)
    private String updateTime;//修改时间

    @Column(nullable = true)
    private String remark;//备用字段

    @Column(nullable = true)
    private String remark1;//备用字段1

    public AccCurrencyExchangeRatesId getId() {
        return id;
    }

    public void setId(AccCurrencyExchangeRatesId id) {
        this.id = id;
    }

    public String getBaseCurrencyName() {
        return baseCurrencyName;
    }

    public void setBaseCurrencyName(String baseCurrencyName) {
        this.baseCurrencyName = baseCurrencyName;
    }

    public String getUnbaseCurrencyName() {
        return unbaseCurrencyName;
    }

    public void setUnbaseCurrencyName(String unbaseCurrencyName) {
        this.unbaseCurrencyName = unbaseCurrencyName;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public String getCreatePerson() {
        return createPerson;
    }

    public void setCreatePerson(String createPerson) {
        this.createPerson = createPerson;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdatePerson() {
        return updatePerson;
    }

    public void setUpdatePerson(String updatePerson) {
        this.updatePerson = updatePerson;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRemark1() {
        return remark1;
    }

    public void setRemark1(String remark1) {
        this.remark1 = remark1;
    }
}
