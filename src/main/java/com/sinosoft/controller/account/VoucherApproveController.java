package com.sinosoft.controller.account;

import com.sinosoft.common.InvokeResult;
import com.sinosoft.common.SysLog;
import com.sinosoft.dto.VoucherDTO;
import com.sinosoft.service.account.VoucherApproveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

//凭证复核

@Controller
@RequestMapping(value = "/voucherapprove")
public class VoucherApproveController {
    private Logger logger = LoggerFactory.getLogger(VoucherApproveController.class);
    @Resource
    private VoucherApproveService voucherApproveService ;
    @RequestMapping("/")
    public String Page(){
        return "account/voucherapprove";
    }

    /**
     * 凭证复核页面进行凭证信息查询
     * @param dto
     * @return
     */
    @RequestMapping(path="/list")
    @ResponseBody
    public List<?> getApproveListData(VoucherDTO dto){
        return voucherApproveService.getApproveListData(dto);
    }

    /**
     * 复核通过，根据传入后台的凭证号修改凭证状态
     * @param dto
     * @return
     */
    @PostMapping("/reviewPass")
    @ResponseBody
    @SysLog(value = "凭证复核通过")
    public InvokeResult reviewPass(VoucherDTO dto){
        try {
            return voucherApproveService.reviewPass(dto);
        } catch (Exception e) {
            logger.error("凭证复核异常", e);
            return InvokeResult.failure("凭证复核失败，请联系管理员！");
        }
    }
    /**
     * 撤销复核，根据传入后台的凭证号修改凭证状态
     * @param dto
     * @return
     */
    @PostMapping("/reviewBack")
    @ResponseBody
    @SysLog(value = "凭证复核撤销")
    public InvokeResult reviewBack(VoucherDTO dto){
        try {
            return voucherApproveService.reviewBack(dto);
        } catch (Exception e) {
            logger.error("凭证撤销异常", e);
            return InvokeResult.failure("凭证撤销失败，请联系管理员！");
        }
    }

    /**
     * 导出凭证复核列表
     */
    @RequestMapping(path="/voucherapprovedownload")
    public void export(HttpServletRequest request, HttpServletResponse response, String name, String queryConditions, String cols){
        voucherApproveService.exportByCondition(request, response, name, queryConditions, cols);
    }
}
