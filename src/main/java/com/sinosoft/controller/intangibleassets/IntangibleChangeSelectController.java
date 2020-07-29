package com.sinosoft.controller.intangibleassets;

import com.sinosoft.common.DataGrid;
import com.sinosoft.common.InvokeResult;
import com.sinosoft.common.SysLog;
import com.sinosoft.dto.intangibleassets.IntangibleAccAssetInfoChangeDTO;
import com.sinosoft.dto.intangibleassets.IntangibleAccAssetInfoDTO;
import com.sinosoft.service.intangibleassets.IntangibleChangeManageService;
import com.sinosoft.service.intangibleassets.IntangibleChangeSelectService;
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
 * @Description 无形卡片信息变动管理
 * @create
 */
@Controller
@RequestMapping("/intangiblechangeselect")
public class IntangibleChangeSelectController {
    private Logger logger = LoggerFactory.getLogger(IntangibleChangeSelectController.class);
    @RequestMapping("/")
    public String page(){ return "intangibleassets/intangiblechangeselect"; }
   @Resource
    private IntangibleChangeSelectService intangibleChangeSelectService;
    /**
     * 查询卡片信息
     * @param
     * @param
     * @param
     * @return
     */
  @RequestMapping(path = "list")
    @ResponseBody
    public DataGrid qryChangemanageList (int page,int rows, IntangibleAccAssetInfoChangeDTO accAssetInfoDTO){
      Page<?> result = null;
      try{
          result = intangibleChangeSelectService.qrychangeList(page,rows,accAssetInfoDTO);
      }catch (Exception e){
          logger.error("无形资产卡片变更查询异常",e);
      }
        return new DataGrid(result);
    }

   @RequestMapping(path = "revoke")
    @ResponseBody
    @SysLog(value = "无形资产卡片变动单撤销")
    public InvokeResult revoke(String changeCodes){
        try{
            return intangibleChangeSelectService.revoke(changeCodes);
        }catch(Exception e){
            logger.error("无形资产卡片变动单撤销失败",e);
            return InvokeResult.failure("无形资产卡片变动单撤销失败！请联系系统管理员。");
        }

    }
    /*
    * 无形资产变动查询 卡片查看
    * */
    @RequestMapping(path = "/intangibleselect")
    @ResponseBody
    public List<?> intangibleselect(IntangibleAccAssetInfoChangeDTO accAssetInfoDTO){

      return  intangibleChangeSelectService.intangibleselect(accAssetInfoDTO);


    }
    /**
     * 判断当前卡片是否摊销过
     * @param dto
     * @return
     */
    @RequestMapping(path = "/isdepre")
    @ResponseBody
    public InvokeResult depreciation(IntangibleAccAssetInfoChangeDTO dto){
        return intangibleChangeSelectService.depreciation(dto);
    }

    /**
     * 类别变更
     * @param
     * @param
     * @param
     * @return
     */
  /* @RequestMapping(path = "typeChange")
    @ResponseBody
    public InvokeResult typeChange(IntangibleAccAssetInfoDTO accAssetInfoDTO){
        try{
            String msg = intangibleChangeManageService.typeChange(accAssetInfoDTO);
            return InvokeResult.success();
        }catch(Exception e){
            logger.error("",e);
            return InvokeResult.failure("操作失败！请联系系统管理员。");
        }

    }
   *//*
   获取类别下拉框
   * *//*
    @RequestMapping(path = "assetType")
    @ResponseBody
    public List<?> getAssetType(IntangibleAccAssetInfoDTO accAssetInfoDTO){
        return intangibleChangeManageService.getAssetType(accAssetInfoDTO);

    }
        *//*
        无形类别编码表获取使用年限
         *//*
    @RequestMapping(path = "depreYear")
    @ResponseBody
    public String getDepreYear(IntangibleAccAssetInfoDTO accAssetInfoDTO){
        return intangibleChangeManageService.getDepreYear(accAssetInfoDTO);

    }*/

    /**
     * 判断选中变动单是否符合要求
     * @param
     * @return
     */
    @RequestMapping(path = "/revokejudge")
    @ResponseBody
    public InvokeResult revokejudge(String changeCodes){
        return intangibleChangeSelectService.revokejudge(changeCodes);
    }
    @RequestMapping("/print")
    @ResponseBody
    public ModelAndView page(IntangibleAccAssetInfoChangeDTO dto, HttpServletRequest request){

        Map<String, Object> map = new HashMap<>();
        map.put("changeType", dto.getChangeType());
        map.put("changeCode",dto.getChangeCode());
        map.put("changeDate",dto.getChangeDate());
        map.put("cardCode",dto.getCardCode());
        map.put("assetCode",dto.getAssetCode());
        map.put("startDate",dto.getUseStartDate());
        map.put("assetName",dto.getAssetName());
        map.put("changeOldData",dto.getChangeOldData());
        map.put("changeNewData",dto.getChangeNewData());
        map.put("useFlag",dto.getUseFlag());
        map.put("depreName",dto.getDepTypeName());
        map.put("changeReason",dto.getChangeReason());
        map.put("operator",dto.getOperatorCode());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("intangibleassets/cardchangeprint");
        modelAndView.addObject("map",map);
        return modelAndView;
    }


}
