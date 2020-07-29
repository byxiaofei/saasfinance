package com.sinosoft.controller.intangibleassets;
import com.sinosoft.common.InvokeResult;

import com.sinosoft.common.SysLog;
import com.sinosoft.dto.fixedassets.AccAssetInfoDTO;
import com.sinosoft.dto.intangibleassets.IntangibleAccAssetInfoDTO;
import com.sinosoft.service.fixedassets.ChangeManageService;
import com.sinosoft.service.intangibleassets.IntangibleAccAssetInfoService;
import com.sinosoft.service.intangibleassets.IntangibleChangeManageService;
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
import java.util.List;
import java.util.Map;

/**
 * @author zhangst
 * @Description 无形卡片信息变动管理
 * @create
 */
@Controller
@RequestMapping("/intangiblechangemanage")
public class IntangibleChangeManageController {
    private Logger logger = LoggerFactory.getLogger(IntangibleChangeManageController.class);
    @RequestMapping("/")
    public String page(){ return "intangibleassets/intangiblechangemanage"; }
   @Resource
    private IntangibleChangeManageService intangibleChangeManageService;
    /**
     * 查询卡片信息
     * @param
     * @param
     * @param
     * @return
     */
   /*@RequestMapping(path = "list")
    @ResponseBody
    public List<?> qryChangemanageList (IntangibleAccAssetInfoDTO accAssetInfoDTO){
        List<?> res = changeManageService.qrychangeList(accAssetInfoDTO);
        return res;
    }*/
    /**
     * 部门变更和其他变更
     * @param
     * @param
     * @param
     * @return
     */
   @RequestMapping(path = "depChange")
    @ResponseBody
   @SysLog(value = "无形资产卡片使用状态变动")
    public InvokeResult depChange(IntangibleAccAssetInfoDTO accAssetInfoDTO){
        try{
            String msg = intangibleChangeManageService.useflagChange(accAssetInfoDTO);
            if(msg.equals("fail")){
                return  InvokeResult.failure("变动失败");
            }
            if(msg.equals("voucherFail")){
               return InvokeResult.failure("voucherFail");
            }
            return InvokeResult.success();
        }catch(Exception e){
            logger.error("",e);
            return InvokeResult.failure("操作失败！请联系系统管理员。");
        }

    }

    /**
     * 卡片清理
     */
    @RequestMapping(path = "cleanCard")
    @ResponseBody
    @SysLog(value = "无形资产卡片清理")
    public InvokeResult cleanCard(IntangibleAccAssetInfoDTO inTangibleAcc){
        try{
            String msg = intangibleChangeManageService.cleanCard(inTangibleAcc);
            if(msg.equals("fail")){
                return InvokeResult.failure("变动失败");
            }
            if(msg.equals("voucherFail")){
                return InvokeResult.failure("voucherFail");
            }
            if(msg.equals("clearYearDateFail")){
                return InvokeResult.failure("清理生效年月所在会计期间不存在或已折旧!");
            }
            return InvokeResult.success();
        }catch(Exception e){
            logger.error("",e);
            return InvokeResult.failure("操作失败！请联系系统管理员。");
        }

    }


    /**
     * 处理使用状态变动
     * @param accAssetInfoDTO
     * @return
     */
    @RequestMapping(path = "useChange")
    @ResponseBody
    @SysLog(value = "无形资产卡片使用状态变动")
    public InvokeResult useChange(IntangibleAccAssetInfoDTO accAssetInfoDTO){
        try{
            String msg = intangibleChangeManageService.useChange(accAssetInfoDTO);
            if(msg.equals("fail")){
                return InvokeResult.failure("卡片变动失败！");
            }
//            if(msg.equals("voucherFail")){
//                return InvokeResult.failure("voucherFail");
//            }

            return InvokeResult.success();
        }catch(Exception e){
            logger.error("",e);
            return InvokeResult.failure("操作失败！请联系系统管理员。");
        }

    }

    /**
     * 类别变更
     * @param
     * @param
     * @param
     * @return
     */
   @RequestMapping(path = "typeChange")
    @ResponseBody
   @SysLog(value = "无形资产卡片摊销信息变动")
    public InvokeResult typeChange(IntangibleAccAssetInfoDTO accAssetInfoDTO){
        try{
            String msg = intangibleChangeManageService.typeChange(accAssetInfoDTO);
            return InvokeResult.success();
        }catch(Exception e){
            logger.error("",e);
            return InvokeResult.failure("操作失败！请联系系统管理员。");
        }

    }
   /*
   获取类别下拉框
   * */
    @RequestMapping(path = "assetType")
    @ResponseBody
    public List<?> getAssetType(IntangibleAccAssetInfoDTO accAssetInfoDTO){
        return intangibleChangeManageService.getAssetType(accAssetInfoDTO);

    }
        /*
        无形类别编码表获取使用年限
         */
    @RequestMapping(path = "depreYear")
    @ResponseBody
    public String getDepreYear(IntangibleAccAssetInfoDTO accAssetInfoDTO){
        return intangibleChangeManageService.getDepreYear(accAssetInfoDTO);

    }

    /**
     * 导出无形资产卡片信息变动管理信息表
     */
    @RequestMapping(path="/intangiblechangemanagedownload")
    public void export(HttpServletRequest request, HttpServletResponse response, String name, String queryConditions, String cols){
        try{
            intangibleChangeManageService.exportByCondition(request, response, name, queryConditions, cols);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
//    @RequestMapping(path = "/print")
//    public ModelAndView pagePrint(AccAssetInfoDTO accAssetInfoDTO){
//        Map<String, Object> map = new HashMap<>();
//        map.put("accAssetInfoDTO",accAssetInfoDTO);
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.setViewName("intangibleassets/changemanageprint");
//        modelAndView.addObject("map",map);
//        return modelAndView;
//    }
}
