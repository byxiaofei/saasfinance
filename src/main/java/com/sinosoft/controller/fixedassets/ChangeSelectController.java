package com.sinosoft.controller.fixedassets;

import com.sinosoft.common.DataGrid;
import com.sinosoft.common.InvokeResult;
import com.sinosoft.common.SysLog;
import com.sinosoft.domain.fixedassets.AccAssetInfoChange;
import com.sinosoft.dto.fixedassets.AccAssetInfoChangeDTO;
import com.sinosoft.dto.fixedassets.AccAssetInfoDTO;
import com.sinosoft.service.fixedassets.ChangeManageService;
import com.sinosoft.service.fixedassets.ChangeSelectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhangst
 * @Description 固定卡片信息变动查询
 * @create
 */
@Controller
@RequestMapping("/changeselect")
public class ChangeSelectController {
    private Logger logger = LoggerFactory.getLogger(ChangeSelectController.class);
    @RequestMapping("/")
    public String page(){ return "fixedassets/changeselect"; }
   @Resource
    private ChangeSelectService changeSelectService;

    /**
     * 查询卡片信息
     * @param
     * @param
     * @param
     * @return
     */
    @RequestMapping(path = "list")
    @ResponseBody
    public DataGrid qryChangemanageList (int page,int rows,AccAssetInfoChangeDTO accAssetInfoDTO){
        Page<?> result = null;
        try{
            result = changeSelectService.qrychangeList(page,rows,accAssetInfoDTO);
        }catch (Exception e){
            logger.error("卡片查询异常",e);
        }
        return new DataGrid(result);

    }
    /**
     * 撤销
     * @param
     * @param
     * @param
     * @return
     */
    @SysLog(value = "撤销固定资产卡片变动单")  //这里添加了AOP的自定义注解
    @RequestMapping(path = "depChange")
    @ResponseBody
    public InvokeResult revoke(String changeCodes){
        try{
            return changeSelectService.revoke(changeCodes);
        }catch(Exception e){
            logger.error("无形资产变动单撤销失败",e);
            return InvokeResult.failure("操作失败！请联系系统管理员。");
        }

    }
    /**
     *卡片查看
     * @param
     * @param
     * @param
     * @return
     */
    @RequestMapping(path = "/cardselect")
    @ResponseBody
    public List<?> cardselect(AccAssetInfoDTO accAssetInfoDTO){
        return  changeSelectService.cardselect(accAssetInfoDTO);
    }
    /**
     *判断是否折旧
     * @param
     * @param
     * @param
     * @return
     */
  @RequestMapping(path = "/isdepre")
    @ResponseBody
    public InvokeResult isDepre(AccAssetInfoChangeDTO accAssetInfoChange){
      return   changeSelectService.Isdepreciation( accAssetInfoChange);


    }
    /**
     * 判断选中变动单是否符合要求
     * @param
     * @return
     */
    @RequestMapping(path = "/revokejudge")
    @ResponseBody
    public InvokeResult revokejudge(String changeCodes){
        return changeSelectService.revokejudge(changeCodes);
    }
/*
    *//**
     * 类别变更
     * @param
     * @param
     * @param
     * @return
     *//*
    @RequestMapping(path = "typeChange")
    @ResponseBody
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
*/

    @RequestMapping("/print")
    @ResponseBody
    public ModelAndView page(AccAssetInfoChangeDTO dto, HttpServletRequest request){

        Map<String, Object> map = new HashMap<>();
        map.put("changeType", dto.getChangeType());
        map.put("changeCode",dto.getChangeCode());
        map.put("changeDate",dto.getChangeDate());
        map.put("cardCode",dto.getCardCode());
        map.put("assetCode",dto.getAssetCode());
        map.put("startDate",dto.getUseStartDate());
        map.put("assetName",dto.getAssetName());
        map.put("specification",dto.getSpecification());
        map.put("changeOldData",dto.getChangeOldData());
        map.put("changeNewData",dto.getChangeNewData());
        map.put("useFlag",dto.getUseFlag());
        map.put("depreName",dto.getDepTypeName());
        map.put("changeReason",dto.getChangeReason());
        map.put("operator",dto.getOperatorCode());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("fixedassets/cardchangeprint");
        modelAndView.addObject("map",map);
        return modelAndView;
    }

}
