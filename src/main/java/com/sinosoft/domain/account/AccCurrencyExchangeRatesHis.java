package com.sinosoft.domain.account;


import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Auther: luodejun
 * @Date: 2020/3/23 21:43
 * @Description:
 */
@Entity
@Table(name = "acccurrencyexchangerateshis")
public class AccCurrencyExchangeRatesHis implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = true)
    private String baseCurrencyCode;

    @Column(nullable = true)
    private String baseCurrencyName;

    @Column(nullable = true)
    private String unbaseCurrencyCode;

    @Column(nullable = true)
    private String unbaseCurrencyName;

    @Column(nullable = true)
    private String currencyCode;

    @Column(nullable = true)
    private  BigDecimal exchangeRate;

    @Column(nullable = true)
    private String updateTime;

    @Column(nullable = true)
    private String updatePerson;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBaseCurrencyCode() {
        return baseCurrencyCode;
    }

    public void setBaseCurrencyCode(String baseCurrencyCode) {
        this.baseCurrencyCode = baseCurrencyCode;
    }

    public String getBaseCurrencyName() {
        return baseCurrencyName;
    }

    public void setBaseCurrencyName(String baseCurrencyName) {
        this.baseCurrencyName = baseCurrencyName;
    }

    public String getUnbaseCurrencyCode() {
        return unbaseCurrencyCode;
    }

    public void setUnbaseCurrencyCode(String unbaseCurrencyCode) {
        this.unbaseCurrencyCode = unbaseCurrencyCode;
    }

    public String getUnbaseCurrencyName() {
        return unbaseCurrencyName;
    }

    public void setUnbaseCurrencyName(String unbaseCurrencyName) {
        this.unbaseCurrencyName = unbaseCurrencyName;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdatePerson() {
        return updatePerson;
    }

    public void setUpdatePerson(String updatePerson) {
        this.updatePerson = updatePerson;
    }
}
