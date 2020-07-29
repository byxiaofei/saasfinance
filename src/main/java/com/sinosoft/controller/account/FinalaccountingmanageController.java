package com.sinosoft.controller.account;

import com.sinosoft.common.InvokeResult;
import com.sinosoft.common.SysLog;
import com.sinosoft.dto.account.AccMonthTraceDTO;
import com.sinosoft.service.account.FinalAccountingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

//决算期管理

@Controller
@RequestMapping(value = "/finalaccountingmanage")
public class FinalaccountingmanageController {
    private Logger logger = LoggerFactory.getLogger(FinalaccountingmanageController.class);
    @Resource
    private FinalAccountingService finalAccountingService ;
    @RequestMapping("/")
    public String Page(){
        return "account/finalaccountingmanage";
    }


    /**
     * 决算管理界面数据展示
     * @param dto
     * @return
     */
    @RequestMapping(path = "/list")
    @ResponseBody
    public List<?> getFinalAccountingListData(AccMonthTraceDTO dto){
        return finalAccountingService.getFinalAccountingListData(dto);
    }

    /**
     * 决算
     * @param dto
     * @return
     */
    @SysLog(value = "生成损益结转凭证")  //这里添加了AOP的自定义注解
    @RequestMapping(path = "/final")
    @ResponseBody
    public InvokeResult finalAccounting(AccMonthTraceDTO dto){
        try {
            return finalAccountingService.finalAccounting(dto);
        } catch (Exception e) {
            logger.error("生成损益结转凭证", e);
            return InvokeResult.failure("生成损益结转凭证失败，请联系管理员！");
        }
    }

    /**
     * 反决算
     * @param dto
     * @return
     */
    @SysLog(value = "回退损益结转凭证")  //这里添加了AOP的自定义注解
    @RequestMapping(path = "/unFinal")
    @ResponseBody
    public InvokeResult unFinalAccounting(AccMonthTraceDTO dto){
        try{
            return finalAccountingService.unFinalAccounting(dto);
        }catch (Exception e){
            logger.error("回退损益结转凭证异常", e);
            return InvokeResult.failure("回退损益结转凭证失败，请联系管理员！");
        }
    }

}