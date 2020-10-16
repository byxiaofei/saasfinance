package com.sinosoft.controller.account;

import com.sinosoft.common.InvokeResult;
import com.sinosoft.common.SysLog;
import com.sinosoft.dto.VoucherDTO;
import com.sinosoft.service.VoucherService;
import com.sinosoft.service.account.VoucherAccountingService;
import com.sinosoft.service.impl.account.VoucherApproveServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

//凭证记账与反记账
@Controller
@RequestMapping(value = "/voucheraccounting")
public class VoucherAccountingController {
    private Logger logger = LoggerFactory.getLogger(VoucherAccountingController.class);
    @Resource
    private VoucherAccountingService voucherAccountingService ;

    @RequestMapping("/")
    public String Page(){
        return "account/voucheraccounting";
    }

    /**
     * 凭证记账与反记账页面进行凭证信息查询
     * @param dto
     * @return
     */
    @RequestMapping(path="/list")
    @ResponseBody
    public List<?> qryVoucherList(VoucherDTO dto){
        return voucherAccountingService.qryVoucherList(dto);
    }

    /**
     * 会计期间默认值
     * @param
     * @return
     */
    @RequestMapping(path="/getDate")
    @ResponseBody
    public VoucherDTO qryYearMonth(){
        return voucherAccountingService.qryYearMonth();
    }

    /**
     * 记账
     * @param dto
     * @return
     */
    @SysLog(value = "凭证记账")  //这里添加了AOP的自定义注解
    @PostMapping("/accounting")
    @ResponseBody
    public InvokeResult accounting(VoucherDTO dto){
        try {
            if(dto.getVoucherNo().split(",").length==1){
                return voucherAccountingService.accounting2(dto);
            }else if (dto.getVoucherNo().split(",").length >1){
                return voucherAccountingService.accounting(dto);
            }else{
                return InvokeResult.failure("请选择凭证信息");
            }
        } catch (Exception e) {
            logger.error("凭证记账异常", e);
            return InvokeResult.failure("凭证记账失败，请联系管理员！");
        }
    }

    /**
     * 反记账
     * @param dto
     * @return
     */
    @SysLog(value = "凭证反记账")  //这里添加了AOP的自定义注解
    @PostMapping("/revokeAccounting")
    @ResponseBody
    public InvokeResult revokeAccounting(VoucherDTO dto){
        try {
            return voucherAccountingService.revokeAccounting(dto);
        } catch (Exception e) {
            logger.error("凭证反记账异常", e);
            return InvokeResult.failure("凭证反记账失败，请联系管理员！");
        }
    }
}
