package com.sinosoft.repository.account;

import com.sinosoft.domain.account.AccCurrencyExchangeRates;
import com.sinosoft.domain.account.AccCurrencyExchangeRatesId;
import com.sinosoft.dto.account.AccCurrencyExchangeRatesDTO;
import com.sinosoft.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @Auther: luodejun
 * @Date: 2020/3/24 10:18
 * @Description:
 */
@Repository
public interface AccCurrencyExchangeRatesRespository extends BaseRepository<AccCurrencyExchangeRates,AccCurrencyExchangeRatesId> {

    /**
     *
     * 功能描述:    查询币种配置信息
     *
     */
    @Query(value = "select * from acccurrencyexchangerates limit ?1,?2",nativeQuery = true)
    List<AccCurrencyExchangeRatesDTO> qryAllInfo(int page, int rows);

    /**
     *
     * 功能描述:    原币代码下拉
     *
     */
    @Query(value = "select a.unbase_currency_code as value,a.unbase_currency_code as text from acccurrencyexchangerates a ",nativeQuery = true)
    List<Map<String,Object>> findUnBaseCurrencyCodes();

    /**
     *
     * 功能描述:    根据原币编码和名称查询
     *
     */
    @Query(value = "select * from acccurrencyexchangerates a where a.unbase_currency_code = ?1 and a.unbase_currency_name like '%?2%'",nativeQuery = true)
    List<AccCurrencyExchangeRates> findAllByUnBaseCodeAndUnBaseName(String unbaseCurrencyCode,String unbaseCurrencyName);

    /**
     *
     * 功能描述:    查询是否有重复的汇率信息
     *
     */
    @Query(value = "select * from acccurrencyexchangerates a where a.base_currency_code = ?1 and a.unbase_currency_code = ?2",nativeQuery = true)
    List<AccCurrencyExchangeRates> findRepeatMessage(String baseCurrencyCode, String  unbaseCurrencyCode);
}
