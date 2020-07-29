package com.sinosoft.controller.fixedassets;

import com.sinosoft.common.DataGrid;
import com.sinosoft.common.InvokeResult;
import com.sinosoft.common.SysLog;
import com.sinosoft.dto.fixedassets.AccGCheckInfoDTO;
import com.sinosoft.service.fixedassets.FixedAssetsDepreciationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping("/fixedassetsdepreciation")
public class FixedAssetsDepreciationController {
    private Logger logger = LoggerFactory.getLogger(FixedAssetsDepreciationController.class);

    @Resource
    private FixedAssetsDepreciationService fixedAssetsDepreciationService;

    @RequestMapping("/")
    public String page(){ return "fixedassets/fixedassetsdepreciation"; }

    /**
     * 查询固定资产会计期间表中所有信息
     * @param dto
     * @return
     */
    @RequestMapping("/list")
    @ResponseBody
    public List<?> qryAccGCheckInfo(AccGCheckInfoDTO dto){
        List<?> result = fixedAssetsDepreciationService.qryAccGCheckInfo(dto);
        return result;
    }

    /**
     * 固定资产折旧计提
     * @param dto
     * @return
     */
    @SysLog(value = "固定资产折旧计提")  //这里添加了AOP的自定义注解
    @RequestMapping(path = "/depreciation")
    @ResponseBody
    public InvokeResult depreciation(AccGCheckInfoDTO dto){
        try{
              return fixedAssetsDepreciationService.depreciation(dto);
        }catch (Exception e){
            logger.error("固定资产折旧异常", e);
            return InvokeResult.failure("固定资产折旧失败，请联系管理员！");
        }
    }

    /**
     * 固定资产折旧反处理
     * @param dto
     * @return
     */
    @SysLog(value = "固定资产折旧反处理")  //这里添加了AOP的自定义注解
    @RequestMapping(path = "/revokeDepreciation")
    @ResponseBody
    public InvokeResult revokeDepreciation(AccGCheckInfoDTO dto){
        try{
            return fixedAssetsDepreciationService.revokeDepreciation(dto);
        }catch (Exception e){
            logger.error("折旧反处理异常",e);
            return InvokeResult.failure("折旧反处理失败，请联系管理员！");
        }
    }

    /**
     * 固定资产折旧管理 凭证生成
     * @param dto
     * @return
     */
    @SysLog(value = "固定资产折旧生成凭证")  //这里添加了AOP的自定义注解
    @RequestMapping(path = "/addVoucher")
    @ResponseBody
    public InvokeResult addVoucher(AccGCheckInfoDTO dto){
        try{
               return fixedAssetsDepreciationService.addVoucher(dto);
        }catch (Exception e){
            logger.error("凭证生成异常",e);
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

    /**
     * 固定资产折旧管理 删除凭证
     * @param dto
     * @return
     */
    @SysLog(value = "固定资产折旧删除凭证")  //这里添加了AOP的自定义注解
    @RequestMapping(path = "/revokeVoucher")
    @ResponseBody
    public InvokeResult revokeVoucher(AccGCheckInfoDTO dto){
        try{
            return fixedAssetsDepreciationService.revokeVoucher(dto);
        }catch (Exception e){
            logger.error("凭证删除异常",e);
            return InvokeResult.failure("凭证删除异常，请联系管理员！");
        }
    }

}
