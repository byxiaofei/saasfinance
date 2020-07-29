package com.sinosoft.controller.fixedassets;

import com.sinosoft.common.DataGrid;
import com.sinosoft.dto.fixedassets.AccAssetInfoDTO;

import com.sinosoft.service.fixedassets.FixedassetsCardSelectService;
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
import java.util.List;
/*
* 固定资产卡片查询
* */
@Controller
@RequestMapping("/fixedassetscardselect")
public class FixedAssetsCardSelectController {
    private Logger logger = LoggerFactory.getLogger(FixedAssetsCardSelectController.class);

    @Resource
    private FixedassetsCardSelectService fixedassetsCardSelectService;

    @RequestMapping("/")
    public String page(){ return "fixedassets/fixedassetsselect/fixedassetscardselects"; }


//    /**
//     * 加载固定资产类别编码中维护的类别
//     * @return
//     */
//    @RequestMapping(path = "/assetType")
//    @ResponseBody
//    public List<?> qryAssetType(){
//        return fixedassetsCardService.qryAssetType();
//    }

  /*  *//**
     * 加载专项中维护的BM类专项
     * @return
     *//*
    @RequestMapping(path = "/unitCode")
    @ResponseBody
    public List<?> qryuUnitCode(){
        return fixedassetsCardService.qryuUnitCode();
    }

    *//**
     * 通过固定资产编码类别名称查询 使用年限()
     * @param assetType
     * @return
     *//*
    @RequestMapping(path = "/depYears")
    @ResponseBody
    public String getDepYears(@Param("assetType")String assetType){
        return fixedassetsCardService.getDepYears(assetType);
    }

    *//**
     * 通过固定资产编码类别名称查询 折旧方法()
     * @param assetType
     * @return
     *//*
    @RequestMapping(path = "/depType")
    @ResponseBody
    public String getDepType(@Param("assetType")String assetType){
        return fixedassetsCardService.getDepType(assetType);
    }

    */
    /**
     * 查询全部固定卡片管理
     * @param
     * @param
     * @param
     * @return
     */
    @RequestMapping(path = "/list")
    @ResponseBody
    public DataGrid qryIntangAssettype (int page, int rows, AccAssetInfoDTO accAssetInfoDTO){
        accAssetInfoDTO.setAssetCode(accAssetInfoDTO.getAssetCode());
        Page<?> result = null;
        try {
            result =  fixedassetsCardSelectService.qryAccAssetInfo(page,rows, accAssetInfoDTO);
        } catch (Exception e) {
            logger.error("固定资产卡片查询异常",e);
        }
        return new DataGrid(result);

    }
    /**
     * 打印查询卡片信息
     * @param
     * @param
     * @param
     * @return
     */
    @RequestMapping(path = "/listPrint")
    @ResponseBody
    public  List<?> getAssetsCardSelect(AccAssetInfoDTO accAssetInfoDTO){
        return fixedassetsCardSelectService.getAssetsCardSelect( accAssetInfoDTO);
    }
    /*
    *
    * 台账查询
    * */
    @RequestMapping(path = "/deprelist")
    @ResponseBody
    public List<?> qryAssetDepre (AccAssetInfoDTO accAssetInfoDTO){
        List<?> res = fixedassetsCardSelectService.getAssetDDepre(accAssetInfoDTO);
        return res;
    }





    /**
     * 固定资产类别查询
     * @param value
     * @return
     *//*
    @RequestMapping(path = "/assetType")
    @ResponseBody
    public List<?> assetType(@Param("value") String value){
        return fixedassetsCardService.qryAssetTypeTree(value);
    }
*/

    /**
     * 判断当前卡片是否计提过
     * @param dto
     * @return
     */
  /*  @RequestMapping(path = "/depreciation")
    @ResponseBody
    public InvokeResult depreciation(AccAssetInfoDTO dto){
        return fixedassetsCardService.depreciation(dto);
    }
*/
    /**
     * 固定资产编号获取
     * @param assetType
     * @return
     */
  /*  @RequestMapping(path = "/fCode")
    @ResponseBody
    public String fCode(@Param("assetType")String assetType){
        return fixedassetsCardService.fCode(assetType);
    }
*/

    /**
     * 导出固定资产卡片查询信息表
     */
    @RequestMapping(path="/fixedassetscardselectsdownload")
    public void export(HttpServletRequest request, HttpServletResponse response, String name, String queryConditions, String cols){
        fixedassetsCardSelectService.exportByCondition(request, response, name, queryConditions, cols);
    }
    @RequestMapping(path = "/print")
    public ModelAndView pagePrint(AccAssetInfoDTO accAssetInfoDTO){
//        Map<String, Object> map = new HashMap<>();
//        map.put("accAssetInfoDTO",accAssetInfoDTO);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("fixedassets/fixedassetscardselectprint");
        modelAndView.addObject("map",accAssetInfoDTO);
        return modelAndView;
    }
}
