package com.sinosoft.repository.account;

import com.sinosoft.domain.account.AccCurrencyExchangeRatesHis;
import com.sinosoft.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @Auther: luodejun
 * @Date: 2020/3/24 10:18
 * @Description:
 */
@Repository
public interface AccCurrencyExchangeRatesHisRespository extends BaseRepository<AccCurrencyExchangeRatesHis,Integer> {

}
