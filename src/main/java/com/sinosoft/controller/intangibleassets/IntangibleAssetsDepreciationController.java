package com.sinosoft.controller.intangibleassets;

import com.sinosoft.common.InvokeResult;
import com.sinosoft.common.SysLog;
import com.sinosoft.dto.fixedassets.AccGCheckInfoDTO;
import com.sinosoft.dto.intangibleassets.AccWCheckInfoDTO;
import com.sinosoft.service.intangibleassets.IntangibleAssetsDepreciationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author jiangyc
 * @Description
 * @create 2019-03-26 16:47
 */
@Controller
@RequestMapping("/intangibleassetsdepreciation")
public class IntangibleAssetsDepreciationController {
    private Logger logger = LoggerFactory.getLogger(IntangibleAssetsDepreciationController.class);

    @Resource
    private IntangibleAssetsDepreciationService intangibleAssetsDepreciationService;

    @RequestMapping("/")
    public String page(){
        return "intangibleassets/intangibleassetsdepreciation.html";
    }

    /**
     * 查询无形资产会计期间表中所有信息
     * @param dto
     * @return
     */
    @RequestMapping("/list")
    @ResponseBody
    public List<?> qryAccWCheckInfo(AccWCheckInfoDTO dto){
        return intangibleAssetsDepreciationService.qryAccWCheckInfo(dto);
    }

    /**
     * 无形资产摊销计提
     * @param dto
     * @return
     */
    @SysLog(value = "无形资产摊销计提")  //这里添加了AOP的自定义注解
    @RequestMapping(path = "/depreciation")
    @ResponseBody
    public InvokeResult depreciation(AccWCheckInfoDTO dto){
            try{
            return intangibleAssetsDepreciationService.depreciation(dto);
        }catch (Exception e){
            logger.error("无形资产折旧异常", e);
            return InvokeResult.failure("无形资产折旧失败，请联系管理员！");
        }
    }

    /**
     * 无形资产折旧反处理
     * @param dto
     * @return
     */
    @SysLog(value = "无形资产摊销反处理")  //这里添加了AOP的自定义注解
    @RequestMapping(path = "/revokeDepreciation")
    @ResponseBody
    public InvokeResult revokeDepreciation(AccWCheckInfoDTO dto){
     try{
          return intangibleAssetsDepreciationService.revokeDepreciation(dto);
        }catch (Exception e){
            logger.error("折旧反处理异常",e);
            return InvokeResult.failure("折旧反处理失败，请联系管理员！");
        }
    }

    /**
     * 无形资产摊销管理 凭证生成
     * @param dto
     * @return
     */
    @SysLog(value = "无形资产摊销生成凭证")  //这里添加了AOP的自定义注解
    @RequestMapping(path = "/addVoucher")
    @ResponseBody
    public InvokeResult addVoucher(AccWCheckInfoDTO dto){
        try{
            return intangibleAssetsDepreciationService.addVoucher(dto);
        }catch (Exception e){
            logger.error("凭证生成异常",e);
            if(e.getMessage().equals("false")){
                return InvokeResult.failure("科目体系中该科目下存在专项，但类别编码该科目下专项为null,因此凭证生成失败");
            }
            return InvokeResult.failure("凭证生成异常，请联系管理员！");
        }
    }

    /**
     * 无形资产折旧管理 删除凭证
     * @param dto
     * @return
     */
    @SysLog(value = "无形资产摊销删除凭证")  //这里添加了AOP的自定义注解
    @RequestMapping(path = "/revokeVoucher")
    @ResponseBody
    public InvokeResult revokeVoucher(AccWCheckInfoDTO dto){
        try{
                return intangibleAssetsDepreciationService.revokeVoucher(dto);
            }catch (Exception e){
                logger.error("凭证删除异常",e);
                return InvokeResult.failure("凭证删除异常，请联系管理员！");
            }
    }
}
