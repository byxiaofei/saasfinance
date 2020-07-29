package com.sinosoft.controller.account;

import com.sinosoft.common.DataGrid;
import com.sinosoft.domain.account.AccCurrencyExchangeRatesHis;
import com.sinosoft.dto.account.AccCurrencyExchangeRatesDTO;
import com.sinosoft.service.account.AccCurrencyExchangeRatesHisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * @Auther: luodejun
 * @Date: 2020/3/26 19:04
 * @Description:
 */
@Controller
@RequestMapping(value = "/currencyexchangerateshis")
public class AccCurrencyExchangeRatesHisController {

    private Logger logger = LoggerFactory.getLogger(AccCurrencyExchangeRatesHisController.class);

    @Resource
    private AccCurrencyExchangeRatesHisService accCurrencyExchangeRatesHisService;

    @RequestMapping("/")
    public String Page(){
        return "account/currencyexchangerateshis";
    }

    /**
     * @Description:    全部查询/条件查询
     * @Author: luodejun
     * @Date: 2020/3/27 11:12
     * @Param:  [page, rows, accCurrencyExchangeRatesDTO]
     * @Return: com.sinosoft.common.DataGrid
     * @Exception:
     */
    @RequestMapping("/list")
    @ResponseBody
    public DataGrid qryAll(@RequestParam int page, @RequestParam int rows, AccCurrencyExchangeRatesDTO accCurrencyExchangeRatesDTO){
        Page<?> result = accCurrencyExchangeRatesHisService.qryAccCurrencyExchangeRatesHisInfo(page, rows, accCurrencyExchangeRatesDTO);
        DataGrid<?> dataGrid = new DataGrid<>(result);
        return dataGrid;
    }

}
