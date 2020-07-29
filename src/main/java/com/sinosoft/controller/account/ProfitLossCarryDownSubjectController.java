package com.sinosoft.controller.account;

import com.sinosoft.common.InvokeResult;
import com.sinosoft.common.SysLog;
import com.sinosoft.dto.account.ProfitLossCarryDownSubjectDTO;
import com.sinosoft.service.account.ProfitLossCarryDownSubjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping(value = "/profitlosscarrydownsubject")
public class ProfitLossCarryDownSubjectController {
    private Logger logger = LoggerFactory.getLogger(ProfitLossCarryDownSubjectController.class);
    @Resource
    private ProfitLossCarryDownSubjectService profitLossCarryDownSubjectService;


    @RequestMapping("/")
    public String Page(){
        return "account/profitlosscarrydownsubject";
    }

    /**
     * 损益结转科目查询
     * @param rightsInterestsCode
     * @return
     */
    @RequestMapping("/list")
    @ResponseBody
    public List<?> queryProfitLossCarryDownSubject(String rightsInterestsCode){
        List<?> list = null;
        try {
            list = profitLossCarryDownSubjectService.queryProfitLossCarryDownSubject(rightsInterestsCode);
        } catch (Exception e) {
            logger.error("损益结转科目查询异常",e);
        }
        return list;
    }
    @SysLog(value="保存损益结转科目设置信息")
    @RequestMapping("/save")
    @ResponseBody
    public InvokeResult saveProfitLossCarryDownSubject(ProfitLossCarryDownSubjectDTO dto){
        try {
            profitLossCarryDownSubjectService.saveProfitLossCarryDownSubject(dto);
        } catch (Exception e) {
            logger.error("编辑损益结转科目异常",e);
            return InvokeResult.failure("保存失败！");
        }
        return InvokeResult.success();
    }

    @SysLog(value="保存损益结转科目设置信息")
    @RequestMapping("/saveAll")
    @ResponseBody
    public InvokeResult saveProfitLossCarryDownSubject(String profitLossCodes, String rightsInterestsCode){
        try {
            /*System.out.println("dataAll数据的字符长度："+dataAll.length());
            List<ProfitLossCarryDownSubjectDTO> list = VoucherController.readJson(dataAll, List.class, ProfitLossCarryDownSubjectDTO.class);
            profitLossCarryDownSubjectService.saveProfitLossCarryDownSubjectAll(list);*/
            System.out.println("profitLossCodes数据的字符长度："+profitLossCodes.length());
            profitLossCarryDownSubjectService.saveProfitLossCarryDownSubjectAll(profitLossCodes, rightsInterestsCode);
        } catch (Exception e) {
            String message = "保存失败！";
            if ("本年利润科目代码设置错误！".equals(e.getMessage())) {
                message = e.getMessage();
            }
            if ("损益科目代码不存在！".equals(e.getMessage())) {
                message = e.getMessage();
            }
            logger.error("编辑损益结转科目异常",e);
            return InvokeResult.failure(message);
        }
        return InvokeResult.success();
    }

    @RequestMapping("/combobox")
    @ResponseBody
    public List<?> codeSelect(@RequestParam String type) {
        return profitLossCarryDownSubjectService.codeSelect(type);
    }
}
