package com.sinosoft.controller.account;

import com.sinosoft.common.InvokeResult;
import com.sinosoft.common.SysLog;
import com.sinosoft.controller.VoucherController;
import com.sinosoft.dto.VoucherDTO;
import com.sinosoft.service.VoucherService;
import com.sinosoft.service.account.VoucherManageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping(value = "/vouchermanage")
public class VoucherManageController {
    private Logger logger = LoggerFactory.getLogger(VoucherManageController.class);
    @Resource
    private VoucherManageService voucherManageService ;
    @Resource
    private VoucherService voucherService;
    @RequestMapping("/")
    public String Page(){
        return "account/certificatemanage";
    }

    /**
     * 凭证管理页面进行凭证信息查询
     * @param dto
     * @return
     */
    @RequestMapping(path="/list")
    @ResponseBody
    public List<?> getApproveListData(VoucherDTO dto){

        return voucherManageService.getApproveListData(dto);
    }

    /**
     * 删除凭证信息
     * @param voucherNo
     * @param yearMonth
     * @return
     */
    @SysLog(value = "删除凭证信息")
    @RequestMapping(path="/delete")
    @ResponseBody
    public InvokeResult deleteVoucher(String voucherNo, String yearMonth){
        try {
            String result = voucherManageService.deleteVoucher(voucherNo, yearMonth);
            if (!(voucherNo!=null && !"".equals(voucherNo) && !"null".equals(voucherNo)) || !(yearMonth!=null && !"".equals(yearMonth) && !"null".equals(yearMonth))) {
                InvokeResult.failure("参数不能为空");
            }
            if ("exist".equals(result)) {
                return InvokeResult.failure("在凭证号"+voucherNo+"之后存在已复核的凭证，不允许删除！");
            } else if ("notFindVoucher".equals(result)){
                return InvokeResult.failure("无效的凭证");
            }
        } catch (Exception e) {
            logger.error("删除凭证信息异常", e);
            return InvokeResult.failure("");
        }
        return InvokeResult.success();
    }

    /**
     * 凭证重新排序
     * @param yearMonth
     * @return
     */
    @SysLog(value = "凭证重新排序")
    @RequestMapping(path="/rearrangement")
    @ResponseBody
    public InvokeResult rearrangementVoucher(@RequestParam String yearMonth){
        try {
            return voucherManageService.rearrangementVoucher(yearMonth);
        } catch (Exception e) {
            logger.error("凭证重新排序异常", e);
            return InvokeResult.failure(yearMonth+" 会计期间凭证重新排序异常，请联系系统管理员！");
        }
    }

    @RequestMapping(path="/super")
    @ResponseBody
    public List<?> getCheckData(String code, String onlyLastStage){
        //System.out.println("code-"+code+"  dir-"+direction);
        /*return voucherManageService.qryVoucherCodeForCheck(code,direction);*/
        return voucherService.qrySubjectCodeForCheck(code, onlyLastStage);
    }

    /**
     * 导出凭证管理列表
     */
    @RequestMapping(path="/vouchermanagedownload")
    public void export(HttpServletRequest request, HttpServletResponse response, String name, String queryConditions, String cols){
        String path = request.getSession().getServletContext().getRealPath("/");
        System.out.println(path);
        voucherManageService.exportByCondition(request, response, name, queryConditions, cols);
    }
}
