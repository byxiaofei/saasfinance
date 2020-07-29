package com.sinosoft.controller.fixedassets;

import com.alibaba.fastjson.JSONObject;
import com.sinosoft.common.DataGrid;
import com.sinosoft.common.InvokeResult;
import com.sinosoft.common.JsonAnalyzeException;
import com.sinosoft.common.SysLog;
import com.sinosoft.controller.VoucherController;
import com.sinosoft.domain.fixedassets.AccAssetInfo;
import com.sinosoft.domain.fixedassets.AccAssetInfoId;
import com.sinosoft.dto.fixedassets.AccAssetInfoDTO;
import com.sinosoft.service.fixedassets.ChangeManageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
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
 * @Description 固定卡片信息变动管理
 * @create
 */
@Controller
@RequestMapping("/changemanage")
public class ChangeManageController {
    private Logger logger = LoggerFactory.getLogger(ChangeManageController.class);
    @RequestMapping("/")
    public String page(){ return "fixedassets/changemanage"; }
    @Resource
    private ChangeManageService changeManageService;
    /**
     * 查询卡片信息
     * @param
     * @param
     * @param
     * @return
     */
    @RequestMapping(path = "list")
    @ResponseBody
    public DataGrid qryChangemanageList (int page,int rows,AccAssetInfoDTO accAssetInfoDTO){
        Page<?> result = null;
        try{
            result = changeManageService.qrychangeList(page,rows,accAssetInfoDTO);
        }catch (Exception e){
            logger.error("卡片查询异常",e);
        }
        return new DataGrid(result);
    }
    /**
     * 打印获取卡片全部信息
     * @param
     * @param
     * @param
     * @return
     */
    @RequestMapping(path = "/listPrint")
    @ResponseBody
    public List<?> getChangeManageLists (AccAssetInfoDTO accAssetInfoDTO){
         List<?>  result = changeManageService.getChangeManageList(accAssetInfoDTO);
        return result;
    }

    /**
     * 部门变更
     * @param
     * @param
     * @param
     * @return
     */
    @RequestMapping(path = "depChange")
    @ResponseBody
    @SysLog(value = "固定资产卡片部门变动")
    public InvokeResult depChange(AccAssetInfoDTO accAssetInfoDTO){
        try{
            String msg = changeManageService.depChange(accAssetInfoDTO);
            if(msg.equals("fail")){
                return InvokeResult.failure("卡片变动失败！");
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
    @SysLog(value = "固定资产卡片清理")
    public InvokeResult cleanCard(AccAssetInfoDTO acc){
        try{
            String msg = changeManageService.cleanCard(acc);
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
            return InvokeResult.failure("操作失败！请联系系统管理员！");
        }

    }

    /**
     * 处理使用状态变动
     * @param accAssetInfoDTO
     * @return
     */
    @RequestMapping(path = "useChange")
    @ResponseBody
    @SysLog(value = "固定资产卡片使用状态变动")
    public InvokeResult useChange(AccAssetInfoDTO accAssetInfoDTO){
        try{
            String msg = changeManageService.useChange(accAssetInfoDTO);
            if(msg.equals("fail")){
                return InvokeResult.failure("卡片变动失败！");
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
     * 类别变更
     * @param
     * @param
     * @param
     * @return
     */
    @RequestMapping(path = "typeChange")
    @ResponseBody
    @SysLog(value = "固定资产卡片类别变更")
    public InvokeResult typeChange(AccAssetInfoDTO accAssetInfoDTO){
        try{
            String msg = changeManageService.typeChange(accAssetInfoDTO);
            return InvokeResult.success();
        }catch(Exception e){
            logger.error("",e);
            return InvokeResult.failure("操作失败！请联系系统管理员。");
        }

    }
    @RequestMapping(path = "assetType")
    @ResponseBody
    public List<?> getAssetType(AccAssetInfoDTO accAssetInfoDTO){
        return changeManageService.AssetType(accAssetInfoDTO);

    }

    /**
     * 导出固定资产卡片信息变动管理信息表
     */
    @RequestMapping(path="/fixedchangemanagedownload")
    public void export(HttpServletRequest request, HttpServletResponse response, String name, String queryConditions, String cols){
        changeManageService.exportByCondition(request, response, name, queryConditions, cols);
    }


    @RequestMapping(path = "/print")
    public ModelAndView pagePrint(AccAssetInfoDTO accAssetInfoDTO){
        Map<String, Object> map = new HashMap<>();
        String ss= JSONObject.toJSONString(accAssetInfoDTO);
        map.put("accAssetInfoDTO",ss);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("fixedassets/changemanageprint");
        modelAndView.addObject("map",map);
        return modelAndView;
    }


}
