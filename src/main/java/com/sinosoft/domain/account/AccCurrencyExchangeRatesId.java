package com.sinosoft.domain.account;


import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * @Auther: luodejun
 * @Date: 2020/3/24 09:54
 * @Description:    联合主键 (基础货币编码/非基础货币编码/货币编码)
 */
@Embeddable
public class AccCurrencyExchangeRatesId  implements Serializable {

    private String baseCurrencyCode;//基础货币编码

    private String unbaseCurrencyCode;//非基础货币编码

    private String currencyCode;//货币编码(以基础货币为准)


    public String getBaseCurrencyCode() {
        return baseCurrencyCode;
    }

    public void setBaseCurrencyCode(String baseCurrencyCode) {
        this.baseCurrencyCode = baseCurrencyCode;
    }

    public String getUnbaseCurrencyCode() {
        return unbaseCurrencyCode;
    }

    public void setUnbaseCurrencyCode(String unbaseCurrencyCode) {
        this.unbaseCurrencyCode = unbaseCurrencyCode;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }
}
