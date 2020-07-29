package com.sinosoft.service.account;

import com.sinosoft.dto.account.AccCurrencyExchangeRatesDTO;
import org.springframework.data.domain.Page;

/**
 * @Auther: luodejun
 * @Date: 2020/3/26 19:06
 * @Description:
 */
public interface AccCurrencyExchangeRatesHisService {

    Page<?> qryAccCurrencyExchangeRatesHisInfo(int page, int rows , AccCurrencyExchangeRatesDTO params);
}
