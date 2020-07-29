package com.sinosoft.controller.intangibleassets;

import com.alibaba.fastjson.JSON;
import com.sinosoft.common.InvokeResult;
import com.sinosoft.common.SysLog;
import com.sinosoft.dto.intangibleassets.IntangibleAccAssetInfoDTO;
import com.sinosoft.service.intangibleassets.IntangibleAccAssetInfoService;
import com.sinosoft.service.intangibleassets.IntangibleCardVoucherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/intangiblecardvoucher")
public class IntangibleCardVoucherController {
    @Resource
    private IntangibleCardVoucherService intangibleCardVoucherService;
    @RequestMapping("/")
    public String page(){ return "intangibleassets/intangiblecardvoucher"; }
    private Logger logger = LoggerFactory.getLogger(IntangibleCardVoucherController.class);

    @SysLog(value = "无形资产卡片凭证生成")  //这里添加了AOP的自定义注解
    @RequestMapping(path = "/createVoucher")
    @ResponseBody
    public InvokeResult createVoucher(IntangibleAccAssetInfoDTO dto){
        try{
        return intangibleCardVoucherService.createVoucher(dto);
        }catch (Exception e){
            logger.error("凭证生成异常", e);
            System.out.println(e.getMessage());
            if(e.getMessage().equals("false")){
                return InvokeResult.failure("科目体系中该科目下存在专项，但类别编码该科目下专项为null,因此凭证生成失败");
            }
            if(e.getMessage().equals("unitfalse")){
                return InvokeResult.failure("科目体系中该科目下存在专项，但卡片维护中使用部门为null,因此凭证生成失败");
            }
            return InvokeResult.failure("凭证生成异常，请联系管理员！");
        }
    }

    @SysLog(value = "无形资产卡片凭证回退")  //这里添加了AOP的自定义注解
    @RequestMapping(path = "/revokeVoucher")
    @ResponseBody
    public InvokeResult revokeVoucher(IntangibleAccAssetInfoDTO dto){
        try{
        return intangibleCardVoucherService.revokeVoucher(dto);
    }catch (Exception e){
        logger.error("凭证回退异常", e);
        return InvokeResult.failure("凭证回退失败，请联系管理员！");
    }
    }

    /**
     * 导出无形资产凭证管理信息表
     */
    @RequestMapping(path="/intangiblecardvoucherdownload")
    public void export(HttpServletRequest request, HttpServletResponse response, String name, String queryConditions, String cols){
        try{
            intangibleCardVoucherService.exportByCondition(request, response, name, queryConditions, cols);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * 打印
     * @param accAssetInfoDTO
     * @return
     */
    @RequestMapping(path = "/print")
    public ModelAndView pagePrint(IntangibleAccAssetInfoDTO accAssetInfoDTO){
        Map<String, Object> map = new HashMap<>();
        String ss= JSON.toJSONString(accAssetInfoDTO);
        map.put("accAssetInfoDTO",ss);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("intangibleassets/intangiblecardvoucherprint");
        modelAndView.addObject("map",map);
        return modelAndView;
    }
}
