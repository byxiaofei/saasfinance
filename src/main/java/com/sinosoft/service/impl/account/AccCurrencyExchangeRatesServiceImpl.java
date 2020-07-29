package com.sinosoft.service.impl.account;

import com.sinosoft.common.CurrentUser;
import com.sinosoft.domain.account.AccCurrencyExchangeRates;
import com.sinosoft.domain.account.AccCurrencyExchangeRatesHis;
import com.sinosoft.dto.account.AccCurrencyExchangeRatesDTO;
import com.sinosoft.repository.account.AccCurrencyExchangeRatesHisRespository;
import com.sinosoft.repository.account.AccCurrencyExchangeRatesRespository;
import com.sinosoft.service.account.AccCurrencyExchangeRatesService;
import com.sinosoft.service.fixedassets.CategoryCodingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Auther: luodejun
 * @Date: 2020/3/24 11:44
 * @Description:
 */
@Service
public class AccCurrencyExchangeRatesServiceImpl implements AccCurrencyExchangeRatesService {

    @Resource
    private AccCurrencyExchangeRatesRespository accCurrencyExchangeRatesRespository;

    @Resource
    private AccCurrencyExchangeRatesHisRespository accCurrencyExchangeRatesHisRespository;

    @Resource
    private CategoryCodingService categoryCodingService;

    @Override
    public Page<?> qryAccCurrencyExchangeRatesInfo(int page, int rows, AccCurrencyExchangeRatesDTO params) {
        Page<AccCurrencyExchangeRates> result ;

        result = accCurrencyExchangeRatesRespository.findAll(new PageRequest((page - 1), rows));
        List<AccCurrencyExchangeRates> content = result.getContent();
        List<AccCurrencyExchangeRatesDTO> listDto = new ArrayList<>();
        if(content != null && content.size() > 0){
            for(AccCurrencyExchangeRates msg : content){
                AccCurrencyExchangeRatesDTO dto = AccCurrencyExchangeRatesDTO.toDTO(msg);
                listDto.add(dto);
            }
        }
        Page<?> page2 = new PageImpl<>(listDto,result.getPageable(),result.getTotalElements());
        return page2;
    }

    @Override
    public List<?> qryAccCurrencyExchangeRatesInfo(String unbaseCurrencyCode, String unbaseCurrencyName) {
        List<AccCurrencyExchangeRates> accList = new ArrayList<>();

        StringBuffer sql = new StringBuffer();
        sql.append("select a.base_currency_code as baseCurrencyCode , a.base_currency_name as baseCurrencyName ,a.unbase_currency_code as unbaseCurrencyCode , a.unbase_currency_name as unbaseCurrencyName,a.exchange_rate as exchangeRate from acccurrencyexchangerates a");
        sql.append(" where 1 = 1 ");

        int paramsNo = 1;
        Map<Integer, Object> params = new HashMap<>();

        if(unbaseCurrencyCode != null && !unbaseCurrencyCode.equals("")){
            sql.append(" and a.unbase_currency_code = ?" + paramsNo);
            params.put(paramsNo, unbaseCurrencyCode);
            paramsNo++;
        }
        if(unbaseCurrencyName != null && !unbaseCurrencyName.equals("")){
            sql.append(" and a.unbase_currency_name like ?" + paramsNo);
            params.put(paramsNo, "%"+unbaseCurrencyName+"%");
            paramsNo++;
        }

        List<?> list = accCurrencyExchangeRatesRespository.queryBySqlSC(sql.toString(), params);
        return list;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String saveCurrencyExchangeRatesInfo(AccCurrencyExchangeRatesDTO accCurrencyExchangeRatesDTO) {
        AccCurrencyExchangeRates accCurrencyExchangeRates = AccCurrencyExchangeRatesDTO.toEntity(accCurrencyExchangeRatesDTO);
        AccCurrencyExchangeRatesHis accCurrencyExchangeRatesHis = AccCurrencyExchangeRatesDTO.toHisEntity(accCurrencyExchangeRatesDTO);
        SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = sdf.format(new Date());
        accCurrencyExchangeRates.setCreateTime(format);// 基础表中创建时间
        accCurrencyExchangeRates.setCreatePerson(CurrentUser.getCurrentUser().getUserName());// 基础表中创建人
        List<AccCurrencyExchangeRates> repeatMessage = accCurrencyExchangeRatesRespository.findRepeatMessage(
                accCurrencyExchangeRatesDTO.getBaseCurrencyCode(),
                accCurrencyExchangeRatesDTO.getUnbaseCurrencyCode());
        //  如果能根据本币和原币编码查询到，将不能新增，让他去编辑修改
        if(repeatMessage != null && repeatMessage.size() > 0){
            return "fail";
        }
        accCurrencyExchangeRatesRespository.save(accCurrencyExchangeRates);
        accCurrencyExchangeRatesHisRespository.save(accCurrencyExchangeRatesHis);
        return "success";
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String updateCurrencyExchangeRatesInfo(AccCurrencyExchangeRatesDTO accCurrencyExchangeRatesDTO ) {
        AccCurrencyExchangeRates accCurrencyExchangeRates = AccCurrencyExchangeRatesDTO.toEntity(accCurrencyExchangeRatesDTO);
        AccCurrencyExchangeRatesHis accCurrencyExchangeRatesHis = AccCurrencyExchangeRatesDTO.toHisEntity(accCurrencyExchangeRatesDTO);
        List<AccCurrencyExchangeRates> repeatMessage = accCurrencyExchangeRatesRespository.findRepeatMessage(
                accCurrencyExchangeRatesDTO.getBaseCurrencyCode(),
                accCurrencyExchangeRatesDTO.getUnbaseCurrencyCode());
        //如果查的到，说明save 进行的是update 不是insert
        if(repeatMessage != null && repeatMessage.size() > 0){
            accCurrencyExchangeRates.setCreateTime(repeatMessage.get(0).getCreateTime());
            accCurrencyExchangeRates.setCreatePerson(repeatMessage.get(0).getCreatePerson());
            accCurrencyExchangeRatesRespository.save(accCurrencyExchangeRates);
            accCurrencyExchangeRatesHisRespository.save(accCurrencyExchangeRatesHis);
            return "success";
        }
        return "fail";
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String deleteCurrencyExchangeRatesInfo(AccCurrencyExchangeRatesDTO accCurrencyExchangeRatesDTO) {
        AccCurrencyExchangeRates accCurrencyExchangeRates = AccCurrencyExchangeRatesDTO.toEntity(accCurrencyExchangeRatesDTO);
        AccCurrencyExchangeRatesHis accCurrencyExchangeRatesHis = AccCurrencyExchangeRatesDTO.toHisEntity(accCurrencyExchangeRatesDTO);
        List<AccCurrencyExchangeRates> repeatMessage = accCurrencyExchangeRatesRespository.findRepeatMessage(
                accCurrencyExchangeRatesDTO.getBaseCurrencyCode(),
                accCurrencyExchangeRatesDTO.getUnbaseCurrencyCode());
        //  看能不能查到，查到在进行删除
        if(repeatMessage != null && repeatMessage.size() > 0 ){
            accCurrencyExchangeRatesHis.setExchangeRate(new BigDecimal("0.00"));
            accCurrencyExchangeRatesRespository.delete(accCurrencyExchangeRates);
            accCurrencyExchangeRatesHisRespository.save(accCurrencyExchangeRatesHis);
            return "success";
        }
        return "fail";
    }

    @Override
    public List<?> qryUnBaseCurrencyCode(String unBaseCurrencyCode) {
        List<?> codeList = new ArrayList<>();
//        System.out.println(unBaseCurrencyCode);
//        if("unBaseCurrencyCode".equals(unBaseCurrencyCode)){
//            System.out.println("这里");
//        }
//        System.out.println("上面没进去");
        if(unBaseCurrencyCode.equals("unbaseCurrencyCode")){
//            System.out.println("到没到这里");
            codeList = accCurrencyExchangeRatesRespository.findUnBaseCurrencyCodes();
        }
//        System.out.println(codeList.size()+"长度"+codeList.isEmpty());
        return codeList;
    }
}
