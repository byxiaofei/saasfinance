package com.sinosoft.service.impl.account;

import com.sinosoft.dto.account.AccCurrencyExchangeRatesDTO;
import com.sinosoft.repository.account.AccCurrencyExchangeRatesHisRespository;
import com.sinosoft.service.account.AccCurrencyExchangeRatesHisService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: luodejun
 * @Date: 2020/3/26 19:07
 * @Description:
 */
@Service
public class AccCurrencyExchangeRatesHisServiceImpl implements AccCurrencyExchangeRatesHisService {

    @Resource
    private AccCurrencyExchangeRatesHisRespository accCurrencyExchangeRatesHisRespository;

    @Override
    public Page<?> qryAccCurrencyExchangeRatesHisInfo(int page, int rows, AccCurrencyExchangeRatesDTO params) {

        StringBuffer sql = new StringBuffer();
        sql.append("select t.id as id ,t.base_currency_code as baseCurrencyCode ,t.base_currency_name as baseCurrencyName , t.unbase_currency_code as unbaseCurrencyCode , t.unbase_currency_name as unbaseCurrencyName , t.exchange_rate as exchangeRate, " +
                "t.update_time as updateTime , t.update_person as updatePerson from acccurrencyexchangerateshis t where 1 = 1 ");

        int paramsNo = 1;
        Map<Integer, Object> params2 = new HashMap<>();

        if(params.getBeginTime() != null && !"".equals(params.getBeginTime())){
            sql.append("and t.update_time >= ?" + paramsNo);
            params2.put(paramsNo, params.getBeginTime());
            paramsNo++;
        }
        if(params.getEndTime() != null && !"".equals(params.getEndTime())){
            sql.append("and t.update_time <= ?" + paramsNo);
            params2.put(paramsNo, params.getEndTime());
            paramsNo++;
        }

        sql.append(" order by t.update_time desc");
        System.out.println(sql);
        Page<?> res = accCurrencyExchangeRatesHisRespository.queryByPageOne(page,rows,sql.toString(), params2);
        return res;
    }
}
