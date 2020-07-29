package com.sinosoft.service.account;

import com.sinosoft.domain.account.AccCurrencyExchangeRates;
import com.sinosoft.dto.account.AccCurrencyExchangeRatesDTO;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @Auther: luodejun
 * @Date: 2020/3/24 11:36
 * @Description:
 */
public interface AccCurrencyExchangeRatesService {


    /**
     *
     * 功能描述:    查询全部币种汇率信息
     *
     */
    Page<?> qryAccCurrencyExchangeRatesInfo(int page, int rows , AccCurrencyExchangeRatesDTO params);
    /**
     *
     * 功能描述:    查询凭证币种汇率
     *
     */
    List<?> qryAccCurrencyExchangeRatesInfo(String unbaseCurrencyCode, String unbaseCurrencyName);

    /**
     *
     * 功能描述:    新增币种汇率信息
     *
     */
    String saveCurrencyExchangeRatesInfo(AccCurrencyExchangeRatesDTO accCurrencyExchangeRatesDTO);

    /**
     *
     * 功能描述:    修改币种汇率信息
     *
     */
    String updateCurrencyExchangeRatesInfo(AccCurrencyExchangeRatesDTO accCurrencyExchangeRatesDTO);

    /**
     *
     * 功能描述:    删除币种汇率信息
     *
     */
    String deleteCurrencyExchangeRatesInfo(AccCurrencyExchangeRatesDTO accCurrencyExchangeRatesDTO);

    /**
     *
     * 功能描述:    查询原币下拉框内容
     *
     */
    List<?> qryUnBaseCurrencyCode(String unBaseCurrencyCode);
}
