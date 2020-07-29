package com.sinosoft.controller.fixedassets;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPObject;
import com.sinosoft.common.DataGrid;
import com.sinosoft.common.InvokeResult;
import com.sinosoft.common.SysLog;
import com.sinosoft.controller.VoucherController;
import com.sinosoft.domain.fixedassets.AccAssetInfoId;
import com.sinosoft.dto.fixedassets.AccAssetInfoDTO;
import com.sinosoft.service.fixedassets.FixedassetsCardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/fixedassetscard")
public class FixedAssetsCardController {
    private Logger logger = LoggerFactory.getLogger(FixedAssetsCardController.class);

    @Resource
    private FixedassetsCardService fixedassetsCardService;
    @Resource
    private VoucherController voucherController;

    @RequestMapping("/")
    public String page(){ return "fixedassets/fixedassetscard"; }

    @SysLog(value = "新增固定资产卡片")  //这里添加了AOP的自定义注解
    @RequestMapping(path = "/add")
    @ResponseBody
    public InvokeResult addFixedassetsCard(AccAssetInfoDTO dto){
        try{
            return fixedassetsCardService.add(dto);
        }catch (Exception e){
            logger.error("固定资产新增卡片失败", e);
            return InvokeResult.failure("固定资产卡片新增失败，请联系管理员！");
        }
    }
    /**
     * 修改卡片
     * @param
     * @return
     */
    @SysLog(value = "编辑固定资产卡片")  //这里添加了AOP的自定义注解
    @RequestMapping(path = "/update")
    @ResponseBody
    public InvokeResult update(AccAssetInfoDTO dto){
        try{
             return fixedassetsCardService.update(dto);
        }catch (Exception e){
            logger.error("固定资产修改卡片失败", e);
            return InvokeResult.failure("固定资产卡片修改失败，请联系管理员！");
        }
    }
//    /**
//     * 加载固定资产类别编码中维护的类别
//     * @return
//     */
//    @RequestMapping(path = "/assetType")
//    @ResponseBody
//    public List<?> qryAssetType(){
//        return fixedassetsCardService.qryAssetType();
//    }

    /**
     * 加载专项中维护的BM类专项
     * @return
     */
    @RequestMapping(path = "/unitCode")
    @ResponseBody
    public List<?> qryuUnitCode(){
        return fixedassetsCardService.qryuUnitCode();
    }

    /**
     * 加载专项中维护的BM类专项（只展示使用状态下的数据）
     * @return
     */
    @RequestMapping(path = "/unitCodeUseFlagOne")
    @ResponseBody
    public List<?> qryUnitCodeByUseFlagOne(){
        return fixedassetsCardService.qryUnitCodeByUseFlagOne();
    }

    /**
     * 通过固定资产编码类别名称查询 使用年限()
     * @param assetType
     * @return
     */
    @RequestMapping(path = "/depYears")
    @ResponseBody
    public String getDepYears(@Param("assetType")String assetType){
        return fixedassetsCardService.getDepYears(assetType);
    }

    /**
     * 通过固定资产编码类别名称查询 折旧方法()
     * @param assetType
     * @return
     */
    @RequestMapping(path = "/depType")
    @ResponseBody
    public String getDepType(@Param("assetType")String assetType){
        return fixedassetsCardService.getDepType(assetType);
    }

    /**
     * 获取新增卡片编码
     * @return
     */
    @RequestMapping(path = "/getNewCardCode")
    @ResponseBody
    public String getNewCardCode(){
        return fixedassetsCardService.getNewCardCode();
    }
    /**
     * 查询全部固定卡片管理
     * @param
     * @param
     * @param
     * @return
     */
    @RequestMapping(path = "list")
    @ResponseBody
    public DataGrid qryIntangAssettype (int page,int rows, AccAssetInfoDTO accAssetInfoDTO){
        accAssetInfoDTO.setAssetCode(accAssetInfoDTO.getAssetCode());
        Page<?> result = null;
        try{
            result = fixedassetsCardService.qryAccAssetInfo(page,rows,accAssetInfoDTO);
        }catch( Exception e){
            logger.error("固定资产卡片查询异常",e);
        }
        return new DataGrid(result);
    }

    /**
     * 打印时获取全部数据
     * @param accAssetInfoDTO
     * @return
     */
    @RequestMapping(path = "/listPrint")
    @ResponseBody
    public List<?> getAccAssetInfos(AccAssetInfoDTO accAssetInfoDTO){
        accAssetInfoDTO.setAssetCode(accAssetInfoDTO.getAssetCode());
        return   fixedassetsCardService.getAccAssetInfo(accAssetInfoDTO);
    }
//    /**
//     *删除卡片编码
//     * @return
//     */
//    @SysLog(value = "固定资产卡片删除")  //这里添加了AOP的自定义注解
//    @RequestMapping(path = "/delete")
//    @ResponseBody
//    public InvokeResult delete(AccAssetInfoId accAssetInfoId){
//        return fixedassetsCardService.delete(accAssetInfoId);
//    }
    /**
     *删除卡片编码
     * @return
     */
    @SysLog(value = "固定资产卡片删除")  //这里添加了AOP的自定义注解
    @RequestMapping(path = "/delete")
    @ResponseBody
    public InvokeResult delete(String AccAssetInfoIds){
        try {
            List<AccAssetInfoId> list;
            list= voucherController.readJson(AccAssetInfoIds,List.class,AccAssetInfoId.class);
            return fixedassetsCardService.delete(list);
        } catch (Exception e) {
            logger.error("固定资产卡片删除异常！",e);
            return InvokeResult.failure("固定资产卡片删除异常！");
        }
    }

    /**
     * 固定资产卡片停用
     * @param dto
     * @return
     */
    @SysLog(value = "固定资产卡片停用")  //这里添加了AOP的自定义注解
    @RequestMapping(path = "/stopUse")
    @ResponseBody
    public InvokeResult stopUse(AccAssetInfoDTO dto){
        try{
            return fixedassetsCardService.stopUse(dto);
        }catch (Exception e){
            logger.error("固定资产卡片停用失败",e);
            return InvokeResult.failure("固定资产卡片停用失败，请联系管理员");
        }
    }

    /**
     * 固定资产类别查询
     * @param value
     * @return
     */
    @RequestMapping(path = "/assetType")
    @ResponseBody
    public List<?> assetType(@Param("value") String value){
        return fixedassetsCardService.qryAssetTypeTree(value);
    }


    /**
     * 判断当前卡片是否计提过
     * @param dto
     * @return
     */
    @RequestMapping(path = "/depreciation")
    @ResponseBody
    public InvokeResult depreciation(AccAssetInfoDTO dto){
        return fixedassetsCardService.depreciation(dto);
    }

    /**
     * 固定资产编号获取
     * @param assetType
     * @return
     */
    @RequestMapping(path = "/fCode")
    @ResponseBody
    public String fCode(@Param("assetType")String assetType){
        return fixedassetsCardService.fCode(assetType);
    }
    /**
     * 固定资产折旧方法获取
     * @param assetType
     * @return
     */
    @RequestMapping(path = "/deprMethod")
    @ResponseBody
    public String deprMethod(@Param("assetType")String assetType){
        return fixedassetsCardService.deprMethod(assetType);
    }
    /**
      * 获取预计残值率
     * @param assetType
     * @return
             */
    @RequestMapping(path = "/getNetSurplusRate")
    @ResponseBody
    public String getNetSurplusRate(@Param("assetType")String assetType,@Param("codeType")String codeType){
        return fixedassetsCardService.getNetSurplusRate(assetType,codeType);
    }

    /**
     * 启用日期校验
     * @param useStartDate
     * @return
     */
    @RequestMapping(path = "/checkDate")
    @ResponseBody
    public InvokeResult checkDate(@Param("useStartDate")String useStartDate){
        return fixedassetsCardService.checkDate(useStartDate);
    }
    /**
     * 获得变动信息
     * @param
     * @return
     */
    @RequestMapping(path = "/getchange")
    @ResponseBody
    public List<?> getChangeMessage(AccAssetInfoDTO acc){
        return fixedassetsCardService.getChangeMessage( acc);
    }

    /**
     * 导出固定资产编码类别信息表
     */
    @RequestMapping(path="/fixedassetscarddownload")
    public void export(HttpServletRequest request, HttpServletResponse response, String name, String queryConditions, String cols){
        fixedassetsCardService.exportByCondition(request, response, name, queryConditions, cols);
    }



    /**
     * 固定资产折旧状态修改
     * @param cardCode
     * @param depreFlag
     * @return
     */
    @SysLog(value = "固定资产卡片折旧计提状态修改")  //这里添加了AOP的自定义注解
    @RequestMapping(path = "/zjUpdate")
    @ResponseBody
    public InvokeResult zjUpdate(@Param("cardCode")String cardCode, @Param("depreFlag")String depreFlag){
        try{
            return fixedassetsCardService.zjUpdate(cardCode, depreFlag);
        }catch (Exception e){
            logger.error("固定资产折旧状态修改异常",e);
            return InvokeResult.failure("修改失败！",e);
        }
    }

    /**
     * 复制操作下 获取资产编号
     * @return
     */
    @RequestMapping(path = "/copAssetCode")
    @ResponseBody
    public InvokeResult copAssetCode(@Param("assetType")String assetType){
        return fixedassetsCardService.copAssetCode(assetType);
    }

    /**
     * 折旧至日期 时间校验
     * @return
     */
    @RequestMapping(path = "/checkDepreToDate")
    @ResponseBody
    public InvokeResult checkDepreToDate(@Param("depreUtilDate")String depreUtilDate){
        return fixedassetsCardService.checkDepreToDate(depreUtilDate);
    }

    /**
     * 根据固定资产类别查询相关固定资产编码
     * @param fixedAssetType
     * @return
     */
    @RequestMapping(path = "/assetcodelist")
    @ResponseBody
    public List<?> getFixedAssetCodeList(String fixedAssetType){
        return fixedassetsCardService.getFixedAssetCodeList(fixedAssetType);
    }

    @RequestMapping(path = "/print")
    public ModelAndView pagePrint( AccAssetInfoDTO accAssetInfoDTO){
        Map<String, Object> map = new HashMap<>();
        String ss=JSONObject.toJSONString(accAssetInfoDTO);
        map.put("accAssetInfoDTO",ss);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("fixedassets/fixedassetscardprint");

        modelAndView.addObject("map",map);
        return modelAndView;
    }

    /**
     * @Description:    查询付款银行信息树
     * @Author: luodejun
     * @Date: 2020/3/31 17:21
     * @Param:  [value]
     * @Return: java.util.List<?>
     * @Exception:
     */
    @RequestMapping(path = "/bankPayMethod")
    @ResponseBody
    public List<?> bankPayMethod(@Param("value") String value){
        return fixedassetsCardService.qryBankPayMethodTree(value);
    }

}
