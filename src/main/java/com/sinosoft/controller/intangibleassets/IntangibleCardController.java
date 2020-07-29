package com.sinosoft.controller.intangibleassets;

import com.alibaba.fastjson.JSONObject;
import com.sinosoft.common.DataGrid;
import com.sinosoft.common.InvokeResult;
import com.sinosoft.common.SysLog;
import com.sinosoft.controller.VoucherController;
import com.sinosoft.domain.intangibleassets.IntangibleAccAssetInfo;
import com.sinosoft.domain.intangibleassets.IntangibleAccAssetInfoId;
import com.sinosoft.dto.fixedassets.AccAssetInfoDTO;
import com.sinosoft.dto.intangibleassets.IntangibleAccAssetInfoDTO;
import com.sinosoft.service.intangibleassets.IntangibleAccAssetInfoService;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/intangiblecard")
public class IntangibleCardController {
    private Logger logger = LoggerFactory.getLogger(IntangibleAssetsController.class);
    @Resource
    private IntangibleAccAssetInfoService intangibleAccAssetInfoService;
    @Resource
    private VoucherController voucherController;
    @RequestMapping("/")
    public String page(){ return "intangibleassets/intangiblecard"; }
    /**
     * 查询全部无形卡片管理
     * @param
     * @param
     * @param
     * @return
     */
    @RequestMapping(path = "list")
    @ResponseBody
    public DataGrid qryIntangAssettype (int page,int rows, IntangibleAccAssetInfoDTO intangibleAccAssetInfoDTO){
        Page<?> result = null;
        try{
            result = intangibleAccAssetInfoService.qryIntangAssetInfo(page,rows,intangibleAccAssetInfoDTO);
        }catch (Exception e){
            logger.error("无形资产卡片查询异常",e);
        }
        return new DataGrid(result);
    }
    /**
     * 打印卡片所有数据
     * @param
     * @param
     * @param
     * @return
     */
    @RequestMapping(path = "/listPrint")
    @ResponseBody
    public List<?> getIntangibleCardList (IntangibleAccAssetInfoDTO intangibleAccAssetInfoDTO){
       // System.out.println("---------------------------123");
          List<?> lsit= intangibleAccAssetInfoService.getIntangibleCardList(intangibleAccAssetInfoDTO);
       // System.out.println("---------------------------456");
           return lsit;
    }
    /**
     * 修改全部无形卡片管理
     * @param
     * @param
     * @param
     * @return
     */
    @RequestMapping(path = "edit")
    @ResponseBody
    public void EditIntangAssettype (IntangibleAccAssetInfoDTO intangibleAccAssetInfoDTO){
        System.out.println("---"+intangibleAccAssetInfoDTO.getAssetCode());
     ///   List<?> res = intangibleAccAssetInfoService.qryIntangAssetInfo(intangibleAccAssetInfoDTO);
       // return ;
    }

    /**
     * 新增无形资产卡片
     * @param dto
     * @return
     */
    @SysLog(value = "新增无形资产卡片")  //这里添加了AOP的自定义注解
    @RequestMapping(path = "/add")
    @ResponseBody
    public InvokeResult addIntangibleCard(IntangibleAccAssetInfoDTO dto){
        try{
              return intangibleAccAssetInfoService.add(dto);
        }catch (Exception e){
            logger.error("新增无形资产卡片异常", e);
            return InvokeResult.failure("新增无形资产卡片失败，请联系管理员！");
        }
    }
    /**
     * 修改无形资产卡片
     * @param dto
     * @return
     */
    @SysLog(value = "编辑无形资产卡片")  //这里添加了AOP的自定义注解
    @RequestMapping(path = "/update")
    @ResponseBody
    public InvokeResult update(IntangibleAccAssetInfoDTO dto){
        try{
            return intangibleAccAssetInfoService.update(dto);
        }catch (Exception e){
            logger.error("修改无形资产卡片异常", e);
            return InvokeResult.failure("修改无形资产卡片失败，请联系管理员！");
        }
    }
//    /**
//     * 加载固定资产类别编码中维护的类别
//     * @return
//     */
//    @RequestMapping(path = "/assetType")
//    @ResponseBody
//    public List<?> qryAssetType(){
//        return intangibleAccAssetInfoService.qryAssetType();
//    }

    /**
     * 获取新增卡片编码
     * @return
     */
    @RequestMapping(path = "/getNewCardCode")
    @ResponseBody
    public String getNewCardCode(){
        return intangibleAccAssetInfoService.getNewCardCode();
    }

//    @SysLog(value = "无形资产卡片删除")  //这里添加了AOP的自定义注解
//    @RequestMapping(path = "/delete")
//    @ResponseBody
//    public InvokeResult delete(IntangibleAccAssetInfoId inta){
//        return intangibleAccAssetInfoService.delete(inta.getCenterCode(), inta.getBranchCode(), inta.getAccBookType(), inta.getAccBookCode(), inta.getCodeType(), inta.getCardCode());
//    }

    @SysLog(value = "删除无形资产卡片")  //这里添加了AOP的自定义注解
    @RequestMapping(path = "/delete")
    @ResponseBody
    public InvokeResult delete(String IntangibleAccAssetInfoIds){
        try {
            List<IntangibleAccAssetInfoId> list;
            list= voucherController.readJson(IntangibleAccAssetInfoIds,List.class,IntangibleAccAssetInfoId.class);
            return intangibleAccAssetInfoService.delete(list);
        }catch (Exception e){
            logger.error("无形资产卡片删除异常", e);
            return InvokeResult.failure("无形资产卡片删除失败，请联系管理员！");
        }
    }


    /**
     * 无形资产卡片停用
     * @param dto
     * @return
     */
    @SysLog(value = "无形资产卡片停用")//这里添加了AOP的自定义注解
    @RequestMapping(path = "/stopUse")
    @ResponseBody
    public InvokeResult stopUse(IntangibleAccAssetInfoDTO dto){
        try{
            return intangibleAccAssetInfoService.stopUse(dto);
        }catch (Exception e){
            logger.error("无形资产卡片停用失败",e);
            return InvokeResult.failure("无形资产卡片停用失败，请联系管理员");
        }
    }

    /**
     * 通过无形资产编码类别名称查询 使用年限()
     * @param assetType
     * @return
     */
    @RequestMapping(path = "/depYears")
    @ResponseBody
    public String getDepYears(@Param("assetType")String assetType){
        return intangibleAccAssetInfoService.getDepYears(assetType);
    }

    /**
     * 无形资产类别查询
     * @param value
     * @return
     */
    @RequestMapping(path = "/assetType")
    @ResponseBody
    public List<?> assetType(@Param("value") String value){
        return intangibleAccAssetInfoService.qryAssetTypeTree(value);
    }

    /**
     * 判断当前卡片是否摊销过
     * @param dto
     * @return
     */
    @RequestMapping(path = "/depreciation")
    @ResponseBody
    public InvokeResult depreciation(IntangibleAccAssetInfoDTO dto){
        return intangibleAccAssetInfoService.depreciation(dto);
    }

    /**
     * 获取无形资产编号
     * @param assetType
     * @return
     */
    @RequestMapping(path = "/fCode")
    @ResponseBody
    public String fCode(@Param("assetType")String assetType){
        return intangibleAccAssetInfoService.fCode(assetType);
    }
    /**
     * 获取无形资产摊销方法
     * @param assetType
     * @return
     */
    @RequestMapping(path = "/deprMethod")
    @ResponseBody
    public String deprMethod(@Param("assetType")String assetType){
        return intangibleAccAssetInfoService.deprMethod(assetType);
    }

    /**
     * 导出无形资产卡片管理信息表
     */
    @RequestMapping(path="/intangiblecarddownload")
    public void export(HttpServletRequest request, HttpServletResponse response, String name, String queryConditions, String cols){
        try{
            intangibleAccAssetInfoService.exportByCondition(request, response, name, queryConditions, cols);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 启用日期校验
     * @param useStartDate
     * @return
     */
    @RequestMapping(path = "/checkDate")
    @ResponseBody
    public InvokeResult checkDate(@Param("useStartDate")String useStartDate){
        return intangibleAccAssetInfoService.checkDate(useStartDate);
    }

    /**
     * 无形资产折旧状态修改
     * @param cardCode
     * @param depreFlag
     * @return
     */
    @SysLog(value = "无形资产卡片折旧计提状态修改")  //这里添加了AOP的自定义注解
    @RequestMapping(path = "/zjUpdate")
    @ResponseBody
    public InvokeResult zjUpdate(@Param("cardCode")String cardCode, @Param("depreFlag")String depreFlag){
        try{
            return intangibleAccAssetInfoService.zjUpdate(cardCode, depreFlag);
        }catch (Exception e){
            return InvokeResult.failure("无形资产卡片折旧计提状态修改失败！",e);
        }
    }

    /**
     * 复制操作下 获取资产编号
     * @return
     */
    @RequestMapping(path = "/copAssetCode")
    @ResponseBody
    public InvokeResult copAssetCode(@Param("assetType")String assetType){
        return intangibleAccAssetInfoService.copAssetCode(assetType);
    }
    /**
     * 获取变动信息
     * @return
     */
    @RequestMapping(path = "/getChange")
    @ResponseBody
    public List<?> getChangeMessage(IntangibleAccAssetInfoDTO acc){
        return intangibleAccAssetInfoService.getChangeMessage(acc);
    }

    /**
     * 摊销至日期 时间校验
     * @return
     */
    @RequestMapping(path = "/checkDepreToDate")
    @ResponseBody
    public InvokeResult checkDepreToDate(@Param("depreUtilDate")String depreUtilDate){
        return intangibleAccAssetInfoService.checkDepreToDate(depreUtilDate);
    }

    /**
     * 根据无形资产类别 查询无形资产编码
     * @param intangAssetType
     * @return
     */
    @RequestMapping(path = "/assetcodelist")
    @ResponseBody
    public List<?> getAssetCodeList(String intangAssetType){
        return intangibleAccAssetInfoService.getAssetCodeList(intangAssetType);
    }
    @RequestMapping(path = "/print")
    public ModelAndView pagePrint(IntangibleAccAssetInfoDTO accAssetInfoDTO){
        Map<String, Object> map = new HashMap<>();
        String ss=JSONObject.toJSONString(accAssetInfoDTO);
        map.put("accAssetInfoDTO",ss);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("intangibleassets/intangiblecardprint");
        modelAndView.addObject("map",map);
        return modelAndView;
    }
}
