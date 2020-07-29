package com.sinosoft.controller.fixedassets;

import com.alibaba.fastjson.JSONObject;
import com.sinosoft.dto.fixedassets.AccAssetInfoDTO;
import com.sinosoft.service.fixedassets.FixedassetsCardSelectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;

/*
* 卡片查询
* */
@Controller
@RequestMapping("/fixedassetsdepreselect")
public class FixedAssetsDepreSelectController {
    private Logger logger = LoggerFactory.getLogger(FixedAssetsDepreSelectController.class);

    @Resource
    private FixedassetsCardSelectService fixedassetsCardSelectService;
    @RequestMapping("/")
  // public String page1(){ return "fixedassets/fixedassetsselect/fixedassetsdepreselect"; }
    public String page1(){ return "fixedassets/fixedassetsselect/fixedassetsdepreselects"; }

//

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
     *固定资产折旧查询主页面
     * @return
     */

    @RequestMapping(path = "depreselect")
    @ResponseBody
    public List<?> qryAssetDepreSelect (String centerCode,String  branchCode,String  levelstart,String  levelend,String  yearMonthData ){
        List<?> res = fixedassetsCardSelectService.getAssetDepreSelect(centerCode,branchCode,levelstart,levelend,yearMonthData);
        return res;
    }

    /**
     *固定资产折旧查询点击类别后 登记信息页面
     * @return
     */

    @RequestMapping(path = "/depreMessage")
    @ResponseBody
    public List<?> qryAssetDepreMessage (AccAssetInfoDTO accAssetInfoDTO ){
        List<?> res = fixedassetsCardSelectService.getAssetDepreMessage( accAssetInfoDTO);
        return res;
    }
    @RequestMapping(path = "/pagecount")
    @ResponseBody
    public Integer getPageCount(AccAssetInfoDTO accAssetInfoDTO ){
        return  fixedassetsCardSelectService.getAssetDepreMessagePage( accAssetInfoDTO);

    }
    @RequestMapping(path = "/getAssetdepremessageprint")
    @ResponseBody
    public List<?> getAssetDepreMessageprint(AccAssetInfoDTO accAssetInfoDTO ){
        return  fixedassetsCardSelectService.getAssetDepreMessageprint( accAssetInfoDTO);

    }
    @RequestMapping(path = "/print")
    public ModelAndView pagePrint(AccAssetInfoDTO accAssetInfoDTO){
//        Map<String, Object> map = new HashMap<>();
//        map.put("accAssetInfoDTO",accAssetInfoDTO);
        ModelAndView modelAndView = new ModelAndView();
       // accAssetInfoDTO.setCenterCode();
        String accAssetinfo=JSONObject.toJSONString(accAssetInfoDTO);
        modelAndView.setViewName("fixedassets/fixedassetsdepreselectprint");
        modelAndView.addObject("map",accAssetinfo);
        return modelAndView;
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
}
