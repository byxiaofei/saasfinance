package com.sinosoft.controller.account;

import com.sinosoft.common.DataGrid;
import com.sinosoft.common.InvokeResult;
import com.sinosoft.common.SysLog;
import com.sinosoft.service.account.AccCheckInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/acccheckinfo")
public class AccCheckInfoController {
    private Logger logger = LoggerFactory.getLogger(AccCheckInfoController.class);

    @Resource
    private AccCheckInfoService accCheckInfoService;

    @RequestMapping("/")
    public String page(){
        return "account/acccheckinfo";
    }

    /**
     * 对账查询
     * @param year
     * @return
     */
    @RequestMapping(path="/list")
    @ResponseBody
    /*public DataGrid qryAccCheckInfo(String year, String generalLedgerAndDetail, String generalLedgerAndAssist, String assistAndDetail){
        return new DataGrid(accCheckInfoService.qryAccCheckInfo(year)) ;
    }*/
    public List qryAccCheckInfo(String year, String generalLedgerAndDetail, String generalLedgerAndAssist, String assistAndDetail){
        return accCheckInfoService.qryAccCheckInfo(year);
    }

    /**
     * 账务对账
     * @param yearMonthDate 会计期间
     * @param generalLedgerAndDetail 总账与明细账
     * @param generalLedgerAndAssist 总账与辅助账
     * @param assistAndDetail 辅助账与明细账
     * @return
     */
    @SysLog(value = "账务对账")
    @RequestMapping(path = "/compute")
    @ResponseBody
    public InvokeResult computeAccCheckInfo(@RequestParam String yearMonthDate, String isCarry, String generalLedgerAndDetail, String generalLedgerAndAssist, String assistAndDetail){
        try {
            String result = accCheckInfoService.computeAccCheckInfo(yearMonthDate, isCarry, generalLedgerAndDetail, generalLedgerAndAssist, assistAndDetail);
            if (result!=null && !"".equals(result)) {
                if ("lastNotCheck".equals(result)) {
                    return InvokeResult.failure("上个期间未对账或对账失败，本期不允许对账！");
                } else if ("noData".equals(result)) {
                    return InvokeResult.failure("无对账数据，对账失败！");
                } else if ("existNotGene".equals(result)){
                    return InvokeResult.failure("本月存在未记账凭证，无法对账！");
                } else if ("GDNotCheck".equals(result)){
                    return InvokeResult.failure("本月固定资产尚未对账或对账不平衡！");
                } else if ("WXNotCheck".equals(result)){
                    return InvokeResult.failure("本月无形资产尚未对账或对账不平衡！");
                } else if ("GDfailcheck".equals(result)){
                    return InvokeResult.failure("本月固定资产对账失败！");
                } else if ("WXfailcheck".equals(result)){
                    return InvokeResult.failure("本月无形资产对账失败！");
                } else if ("GDNotDepre".equals(result)){
                    return InvokeResult.failure("本月固定资产尚未进行计提折旧！");
                } else if ("WXNotDepre".equals(result)){
                    return InvokeResult.failure("本月无形资产尚未进行计提摊销！");
                } else if ("GDNotVoucher".equals(result)){
                    return InvokeResult.failure("本月固定资产尚未生成计提折旧凭证！");
                } else if ("WXNotVoucher".equals(result)){
                    return InvokeResult.failure("本月无形资产尚未生成计提摊销凭证！");
                } else if ("notmessage".equals(result)){
                    //"该会计期间没有对账数据,默认对账成功！"
                    return InvokeResult.failure("notmessage");
                } else {
                    return InvokeResult.failure(result);
                }
            }
            return InvokeResult.success();
        } catch (Exception e) {
            logger.error("对账处理异常", e);
            return InvokeResult.failure("对账异常！");
        }
    }
    /**
     * 对账结果查询
     * @param yearMonthDate
     * @return
     */
    @RequestMapping(path = "/qrycompute")
    @ResponseBody
    public InvokeResult qryComputeAccCheckInfo(@RequestParam String yearMonthDate, String generalLedgerAndDetail, String generalLedgerAndAssist, String assistAndDetail){
        try {
            List<?> result = accCheckInfoService.qryComputeAccCheckInfo(yearMonthDate, generalLedgerAndDetail, generalLedgerAndAssist, assistAndDetail);
            return InvokeResult.success(result);
        } catch (Exception e) {
            logger.error("对账结果查询异常", e);
            return InvokeResult.failure("对账结果查询异常！");
        }
    }
}
