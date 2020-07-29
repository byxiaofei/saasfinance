package com.sinosoft.dto.account;

import com.sinosoft.common.CurrentUser;
import com.sinosoft.domain.account.AccCurrencyExchangeRates;
import com.sinosoft.domain.account.AccCurrencyExchangeRatesHis;
import com.sinosoft.domain.account.AccCurrencyExchangeRatesId;
//import lombok.Data;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;

/**
 * @Auther: luodejun
 * @Date: 2020/3/24 11:41
 * @Description:
 */
public class AccCurrencyExchangeRatesDTO {

    private Integer id;

    private String baseCurrencyCode;

    private String baseCurrencyName;

    private String unbaseCurrencyCode;

    private String unbaseCurrencyName;

    private String currencyCode;

    private String exchangeRate;

    private String createPerson;

    private String updatePerson;

    private String updateTime;

    private String beginTime;

    private String endTime;

    public static AccCurrencyExchangeRatesDTO toDTO(AccCurrencyExchangeRates accCurrencyExchangeRates){
        AccCurrencyExchangeRatesDTO dto = new AccCurrencyExchangeRatesDTO();
        dto.setBaseCurrencyCode(accCurrencyExchangeRates.getId().getBaseCurrencyCode());
        dto.setBaseCurrencyName(accCurrencyExchangeRates.getBaseCurrencyName());
        dto.setUnbaseCurrencyCode(accCurrencyExchangeRates.getId().getUnbaseCurrencyCode());
        dto.setUnbaseCurrencyName(accCurrencyExchangeRates.getUnbaseCurrencyName());
        dto.setCurrencyCode(accCurrencyExchangeRates.getId().getCurrencyCode());
        dto.setExchangeRate(accCurrencyExchangeRates.getExchangeRate().toString());
        dto.setCreatePerson(accCurrencyExchangeRates.getCreatePerson());
        dto.setUpdatePerson(accCurrencyExchangeRates.getUpdatePerson());
        return dto;
    }

    public static AccCurrencyExchangeRates toEntity(AccCurrencyExchangeRatesDTO accCurrencyExchangeRatesDTO){
        SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = sdf.format(new Date());
        AccCurrencyExchangeRates entity = new AccCurrencyExchangeRates();
        AccCurrencyExchangeRatesId accEntity = new AccCurrencyExchangeRatesId();
        accEntity.setUnbaseCurrencyCode(accCurrencyExchangeRatesDTO.getUnbaseCurrencyCode());
        accEntity.setCurrencyCode(accCurrencyExchangeRatesDTO.getBaseCurrencyCode());
        accEntity.setBaseCurrencyCode(accCurrencyExchangeRatesDTO.getBaseCurrencyCode());
        entity.setId(accEntity);
        entity.setUnbaseCurrencyName(accCurrencyExchangeRatesDTO.getUnbaseCurrencyName());
        entity.setExchangeRate(new BigDecimal(accCurrencyExchangeRatesDTO.getExchangeRate()));
        entity.setBaseCurrencyName(accCurrencyExchangeRatesDTO.getBaseCurrencyName());
        entity.setUpdatePerson(CurrentUser.getCurrentUser().getUserName());// 修改人
        entity.setUpdateTime(format);// 修改时间
        return entity;
    }

    public static AccCurrencyExchangeRatesHis toHisEntity(AccCurrencyExchangeRatesDTO accCurrencyExchangeRatesDTO){
        SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = sdf.format(new Date());
        AccCurrencyExchangeRatesHis hisEntity = new AccCurrencyExchangeRatesHis();
        hisEntity.setBaseCurrencyCode(accCurrencyExchangeRatesDTO.getBaseCurrencyCode());
        hisEntity.setBaseCurrencyName(accCurrencyExchangeRatesDTO.getBaseCurrencyName());
        hisEntity.setUnbaseCurrencyCode(accCurrencyExchangeRatesDTO.getUnbaseCurrencyCode());
        hisEntity.setUnbaseCurrencyName(accCurrencyExchangeRatesDTO.getUnbaseCurrencyName());
        hisEntity.setCurrencyCode(accCurrencyExchangeRatesDTO.getBaseCurrencyCode());
        hisEntity.setExchangeRate(new BigDecimal(accCurrencyExchangeRatesDTO.getExchangeRate()));
        hisEntity.setUpdatePerson(CurrentUser.getCurrentUser().getUserName());// 修改人
        hisEntity.setUpdateTime(format);// 修改时间
        return hisEntity;
    }

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

    public String getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(String exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public String getCreatePerson() {
        return createPerson;
    }

    public void setCreatePerson(String createPerson) {
        this.createPerson = createPerson;
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

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
