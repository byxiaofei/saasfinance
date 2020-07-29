package com.sinosoft.controller.fixedassets;

import com.alibaba.fastjson.JSONObject;
import com.sinosoft.common.InvokeResult;
import com.sinosoft.common.SysLog;
import com.sinosoft.dto.fixedassets.AccAssetInfoDTO;
import com.sinosoft.service.fixedassets.FixedassetsCardService;
import com.sinosoft.service.fixedassets.FixedassetsCardVoucherService;
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
@RequestMapping("/fixedassetscardvoucher")
public class FixedAssetsCardVoucherController {
    private Logger logger = LoggerFactory.getLogger(FixedAssetsCardVoucherController.class);

    @Resource
    private FixedassetsCardVoucherService fixedassetsCardVoucherService;

    @RequestMapping("/")
    public String page(){ return "fixedassets/fixedassetscardvoucher"; }

    @SysLog(value = "固定资产卡片凭证生成")  //这里添加了AOP的自定义注解
    @RequestMapping(path = "/createVoucher")
    @ResponseBody
    public InvokeResult createVoucher(AccAssetInfoDTO dto){
        try{
        return fixedassetsCardVoucherService.createVoucher(dto);
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

    @SysLog(value = "固定资产卡片凭证回退")  //这里添加了AOP的自定义注解
    @RequestMapping(path = "/revokeVoucher")
    @ResponseBody
    public InvokeResult revokeVoucher(AccAssetInfoDTO dto){
        try{
                return fixedassetsCardVoucherService.revokeVoucher(dto);
        }catch (Exception e){
            logger.error("凭证回退异常", e);
            return InvokeResult.failure("凭证回退失败，请联系管理员！");
        }
    }

    /**
     * 导出固定资产凭证管理信息表
     */
    @RequestMapping(path="/fixedassetscardvoucherdownload")
    public void export(HttpServletRequest request, HttpServletResponse response, String name, String queryConditions, String cols){
        fixedassetsCardVoucherService.exportByCondition(request, response, name, queryConditions, cols);
    }

    /**
     * 打印
     * @param accAssetInfoDTO
     * @return
     */
    @RequestMapping(path = "/print")
    public ModelAndView pagePrint(AccAssetInfoDTO accAssetInfoDTO){
        Map<String, Object> map = new HashMap<>();
        String ss= JSONObject.toJSONString(accAssetInfoDTO);
        map.put("accAssetInfoDTO",ss);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("fixedassets/fixedassetscardvoucherprint");
        modelAndView.addObject("map",map);
        return modelAndView;
    }
}
