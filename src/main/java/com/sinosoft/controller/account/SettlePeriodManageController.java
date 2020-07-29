package com.sinosoft.controller.account;

import com.sinosoft.common.InvokeResult;
import com.sinosoft.common.SysLog;
import com.sinosoft.dto.account.AccMonthTraceDTO;
import com.sinosoft.service.account.SettlePeriodService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

//结算期管理

@Controller
@RequestMapping(value = "/settleperiodmanage")
public class SettlePeriodManageController {
    @Resource
    private SettlePeriodService settlePeriodService ;
    private Logger logger = LoggerFactory.getLogger(SettlePeriodManageController.class);
    @RequestMapping("/")
    public String Page(){
        return "account/settleperiodmanage";
    }


    /**
     * 结算期管理界面数据展示
     * @param dto
     * @return
     */
    @RequestMapping(path = "/list")
    @ResponseBody
    public List<?> getSettlePderioListData(AccMonthTraceDTO dto){
        return settlePeriodService.getSettlePderioListData(dto);
    }

    /**
     * 结算期管理界面 获取追加信息
     * @return
     */
    @RequestMapping(path = "/addTo")
    @ResponseBody
    public AccMonthTraceDTO addTo(){
        return settlePeriodService.addTo();
    }

    /**
     * 结算期管理界面 保存追加信息
     * @param dto
     * @return
     */
    @SysLog(value = "追加会计期间")  //这里添加了AOP的自定义注解
    @RequestMapping(path = "/save")
    @ResponseBody
    public InvokeResult save(AccMonthTraceDTO dto){
        try {
            return settlePeriodService.save(dto);
        } catch (Exception e) {
            logger.error("追加会计期间异常", e);
            return InvokeResult.failure("会计期间追加失败，请联系管理员！");
        }
    }

    /**
     * 结转
     * @param dto
     * @return
     */
    @SysLog(value = "结转")  //这里添加了AOP的自定义注解
    @RequestMapping(path = "/settle")
    @ResponseBody
    public InvokeResult settle(AccMonthTraceDTO dto){
        try {
            return settlePeriodService.settle(dto);
        } catch (Exception e) {
            logger.error("结转异常", e);
            return InvokeResult.failure("会计期间结转失败，请联系管理员！");
        }
    }

    /**
     * 反结转
     * @param dto
     * @return
     */
    @SysLog(value = "反结转")  //这里添加了AOP的自定义注解
    @RequestMapping(path = "/unSettle")
    @ResponseBody
    public InvokeResult unSettle(AccMonthTraceDTO dto){
        try {
            return settlePeriodService.unSettle(dto);
        } catch (Exception e) {
            logger.error("反结转异常", e);
            return InvokeResult.failure("会计期间反结转失败，请联系管理员！");
        }
    }

}