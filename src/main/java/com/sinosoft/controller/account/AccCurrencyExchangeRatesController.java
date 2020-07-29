package com.sinosoft.controller.account;

import com.sinosoft.common.DataGrid;
import com.sinosoft.common.InvokeResult;
import com.sinosoft.common.SysLog;
import com.sinosoft.dto.account.AccCurrencyExchangeRatesDTO;
import com.sinosoft.service.account.AccCurrencyExchangeRatesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Auther: luodejun
 * @Date: 2020/3/24 11:34
 * @Description:
 */
@Controller
@RequestMapping(value = "/currencyexchangerates")
public class AccCurrencyExchangeRatesController {

    private Logger logger = LoggerFactory.getLogger(AccCurrencyExchangeRatesController.class);

    @Resource
    private AccCurrencyExchangeRatesService accCurrencyExchangeRatesService;

    @RequestMapping("/")
    public String Page(){
        return "account/currencyexchangerates";
    }

    /**
     * @Description:    查询全部信息
     * @Author: luodejun
     * @Date: 2020/3/24 14:06
     * @Param:
     * @Return:
     * @Exception:
     */
    @RequestMapping("/list")
    @ResponseBody
    public DataGrid qryAll(@RequestParam int page, @RequestParam int rows, AccCurrencyExchangeRatesDTO accCurrencyExchangeRatesDTO){
        Page<?> result = accCurrencyExchangeRatesService.qryAccCurrencyExchangeRatesInfo(page,rows,accCurrencyExchangeRatesDTO);
        return new DataGrid(result);
    }

    /**
     * @Description:    根据原币编码、原币名称查询对应结果
     * @Author: luodejun
     * @Date: 2020/3/25 9:22
     * @Param:  [unbaseCurrencyCode, unbaseCurrencyName]
     * @Return: java.util.List<?>
     * @Exception:
     */
    @RequestMapping("/resultList")
    @ResponseBody
    public List<?> qryByCodeAndName(AccCurrencyExchangeRatesDTO accCurrencyExchangeRatesDTO){
        System.out.println(accCurrencyExchangeRatesDTO.getUnbaseCurrencyCode());
        System.out.println(accCurrencyExchangeRatesDTO.getUnbaseCurrencyName());
        return accCurrencyExchangeRatesService.qryAccCurrencyExchangeRatesInfo(accCurrencyExchangeRatesDTO.getUnbaseCurrencyCode(),accCurrencyExchangeRatesDTO.getUnbaseCurrencyName());
    }

    /**
     *
     * 功能描述:    保存新建的币种汇率信息
     *
     */
    @SysLog(value = "新建汇率记录")
    @RequestMapping("/save")
    @ResponseBody
    public InvokeResult saveCurrencyExchangeRates(AccCurrencyExchangeRatesDTO accCurrencyExchangeRatesDTO){
        try {
            if("".equals(accCurrencyExchangeRatesDTO.getBaseCurrencyCode()) || accCurrencyExchangeRatesDTO.getBaseCurrencyCode() == null ){
                 return InvokeResult.failure("没有本币编码");
            }
            if("".equals(accCurrencyExchangeRatesDTO.getBaseCurrencyName()) || accCurrencyExchangeRatesDTO.getBaseCurrencyName() == null){
                return InvokeResult.failure("没有本币名称");
            }
            if("".equals(accCurrencyExchangeRatesDTO.getUnbaseCurrencyCode()) || accCurrencyExchangeRatesDTO.getUnbaseCurrencyCode() == null){
                return InvokeResult.failure("没有原币编码");
            }
            if("".equals(accCurrencyExchangeRatesDTO.getUnbaseCurrencyName()) || accCurrencyExchangeRatesDTO.getUnbaseCurrencyName() == null){
                return InvokeResult.failure("没有原币名称");
            }
            if("".equals(accCurrencyExchangeRatesDTO.getExchangeRate()) || accCurrencyExchangeRatesDTO.getExchangeRate() == null){
                return InvokeResult.failure("没有汇率");
            }
            if(!"CNY".equals(accCurrencyExchangeRatesDTO.getBaseCurrencyCode())){
                return InvokeResult.failure("本币编码不为CNY");
            }
            if(!"人民币".equals(accCurrencyExchangeRatesDTO.getBaseCurrencyName())){
                return InvokeResult.failure("本币名称不为:人民币");
            }
            String msg = accCurrencyExchangeRatesService.saveCurrencyExchangeRatesInfo(accCurrencyExchangeRatesDTO);
            if ("fail".equals(msg)){
                return InvokeResult.failure("本币兑原币信息已存在，请查询信息并修改！");
            }
            return InvokeResult.success("success");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("汇率信息新增失败",e);
        }
        return InvokeResult.failure("新增数据失败!");
    }

    /**
     *
     * 功能描述:    查询abstractCode下拉框内容
     *
     */
    @RequestMapping("/codelist")
    @ResponseBody
    public List<?> qryCodeList( String unbaseCode){
        return accCurrencyExchangeRatesService.qryUnBaseCurrencyCode(unbaseCode);
    }

    /**
     *
     * 功能描述:    编辑汇率信息
     *
     */
    @SysLog(value = "编辑汇率信息")
    @RequestMapping("/edit")
    @ResponseBody
    public InvokeResult editCurrencyExchangeRates(AccCurrencyExchangeRatesDTO accCurrencyExchangeRatesDTO){
        try {
            if("".equals(accCurrencyExchangeRatesDTO.getBaseCurrencyCode()) || accCurrencyExchangeRatesDTO.getBaseCurrencyCode() == null ){
                return InvokeResult.failure("没有本币编码");
            }
            if("".equals(accCurrencyExchangeRatesDTO.getBaseCurrencyName()) || accCurrencyExchangeRatesDTO.getBaseCurrencyName() == null){
                return InvokeResult.failure("没有本币名称");
            }
            if("".equals(accCurrencyExchangeRatesDTO.getUnbaseCurrencyCode()) || accCurrencyExchangeRatesDTO.getUnbaseCurrencyCode() == null){
                return InvokeResult.failure("没有原币编码");
            }
            if("".equals(accCurrencyExchangeRatesDTO.getUnbaseCurrencyName()) || accCurrencyExchangeRatesDTO.getUnbaseCurrencyName() == null){
                return InvokeResult.failure("没有原币名称");
            }
            if("".equals(accCurrencyExchangeRatesDTO.getExchangeRate()) || accCurrencyExchangeRatesDTO.getExchangeRate() == null){
                return InvokeResult.failure("没有汇率");
            }
            if(!"CNY".equals(accCurrencyExchangeRatesDTO.getBaseCurrencyCode())){
                return InvokeResult.failure("本币编码不为:CNY");
            }
            System.out.println(accCurrencyExchangeRatesDTO.getBaseCurrencyName());
            if(!"人民币".equals(accCurrencyExchangeRatesDTO.getBaseCurrencyName())){
                return InvokeResult.failure("本币名称不为:人民币");
            }
            String msg = accCurrencyExchangeRatesService.updateCurrencyExchangeRatesInfo(accCurrencyExchangeRatesDTO);
            if("fail".equals(msg)){
                return InvokeResult.failure("原币编码与本币编码作为汇率映射，不能修改其编码，想对应其他编码关系，请新增。");
            }
            return InvokeResult.success("success");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("汇率信息修改失败",e);
        }
        return InvokeResult.failure("修改失败");
    }

    @SysLog(value = "删除汇率信息")
    @RequestMapping("/delete")
    @ResponseBody
    public InvokeResult deleteCurrencyExchangeRates(AccCurrencyExchangeRatesDTO accCurrencyExchangeRatesDTO){
        try {
            System.out.println(accCurrencyExchangeRatesDTO);
            String msg = accCurrencyExchangeRatesService.deleteCurrencyExchangeRatesInfo(accCurrencyExchangeRatesDTO);
            if("fail".equals(msg)){
                return InvokeResult.failure("数据丢失，删除失败。请去数据库中维护数据");
            }
            return  InvokeResult.success("删除成功!");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("汇率信息删除失败",e);
        }
        return InvokeResult.failure("删除失败!");
    }
}
