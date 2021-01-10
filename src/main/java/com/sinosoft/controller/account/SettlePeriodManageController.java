package com.sinosoft.controller.account;

import com.sinosoft.common.CurrentUser;
import com.sinosoft.common.InvokeResult;
import com.sinosoft.common.SysLog;
import com.sinosoft.domain.account.AccMonthTrace;
import com.sinosoft.domain.account.AccMonthTraceId;
import com.sinosoft.dto.account.AccMonthTraceDTO;
import com.sinosoft.repository.account.AccMonthTraceRespository;
import com.sinosoft.service.account.SettlePeriodService;
import com.sinosoft.util.CommonStatusCode;
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

    @Resource
    private AccMonthTraceRespository accMonthTraceRespository;

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
        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String accBookType = CurrentUser.getCurrentLoginAccountType();
        String accBookCode = CurrentUser.getCurrentLoginAccount();
        try {
            AccMonthTrace accMonthTrace =  accMonthTraceRespository.findAccMonthTraceByYearMonthDate(centerCode,accBookType,accBookCode, dto.getYearMonthDate());
            if(accMonthTrace.getId().getAccBookCode()!=null){
                String temp = accMonthTrace.getTemp();
                if(temp == null){
                    temp = "0";
                }
                if(temp.equals(CommonStatusCode.END_FINAL_CODE_STATUS)){
                    return InvokeResult.failure("当前正在结转，请不要在点击！");
                }
            }else{
                return InvokeResult.failure("查询不到当前的会计期间。");
            }

            // 修改状态为2  2 是不进行操作
            accMonthTrace.setTemp(CommonStatusCode.END_FINAL_CODE_STATUS);
            accMonthTraceRespository.saveAndFlush(accMonthTrace);
            accMonthTrace = null;
            InvokeResult settle = settlePeriodService.settle(dto);

            return settle;
        } catch (Exception e) {
            logger.error("结转异常", e);
            return InvokeResult.failure("会计期间结转失败，请联系管理员！");
        }finally {
            // 无论是否异常，都需要进行在把状态修改为1 变为可进行操作的。
            accMonthTraceRespository.updateFlag1AboutTemp(CommonStatusCode.START_FINAL_CODE_STATUS,dto.getYearMonthDate(),centerCode);
            accMonthTraceRespository.flush();
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
        String centerCode = CurrentUser.getCurrentLoginManageBranch();
        String accBookType = CurrentUser.getCurrentLoginAccountType();
        String accBookCode = CurrentUser.getCurrentLoginAccount();
        try {
            AccMonthTrace accMonthTrace =  accMonthTraceRespository.findAccMonthTraceByYearMonthDate(centerCode,accBookType,accBookCode, dto.getYearMonthDate());
            if(accMonthTrace.getId().getAccBookCode()!=null){
                String temp = accMonthTrace.getTemp();
                if(temp == null){
                    temp = "0";
                }
                if(temp.equals(CommonStatusCode.END_FINAL_CODE_STATUS)){
                    return InvokeResult.failure("当前正在结转，请不要在点击！");
                }
            }else{
                return InvokeResult.failure("查询不到当前的会计期间。");
            }
            // 修改状态为2  2 是不进行操作
            InvokeResult invokeResult = settlePeriodService.unSettle(dto);
            return invokeResult;
        } catch (Exception e) {
            logger.error("反结转异常", e);
            return InvokeResult.failure("会计期间反结转失败，请联系管理员！");
        }finally {
            // 无论是否异常，都需要进行在把状态修改为1 变为可进行操作的。
            accMonthTraceRespository.updateFlag1AboutTemp(CommonStatusCode.START_FINAL_CODE_STATUS,dto.getYearMonthDate(),centerCode);
            accMonthTraceRespository.flush();
        }
    }

}