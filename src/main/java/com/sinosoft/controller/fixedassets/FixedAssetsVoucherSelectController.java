package com.sinosoft.controller.fixedassets;

import com.sinosoft.dto.fixedassets.AccAssetInfoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import com.sinosoft.service.fixedassets.FixedassetsCardSelectService;
/*
* 卡片查询
* */
@Controller
@RequestMapping("/fixedassetsvoucherselect")
public class FixedAssetsVoucherSelectController {
    private Logger logger = LoggerFactory.getLogger(FixedAssetsVoucherSelectController.class);

    @Resource
    private FixedassetsCardSelectService fixedassetsCardSelectService;

    @RequestMapping("/")
    public String page2(){ return "fixedassets/fixedassetsselect/fixedassetsvoucherselect"; }



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
     * @param
     * @return
     *//*
    @RequestMapping(path = "/depType")
    @ResponseBody
    public String getDepType(@Param("assetType")String assetType){
        return fixedassetsCardService.getDepType(assetType);
    }

    */

    /*
    * 固定资产折旧凭证查询
    * */
    @RequestMapping(path="/voucherselect")
    @ResponseBody
    public List<?> qryAssetVoucherSelect(String date1,String date2){
        List<?> res = fixedassetsCardSelectService.getAssetVoucherSelect(date1,date2);
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
}
