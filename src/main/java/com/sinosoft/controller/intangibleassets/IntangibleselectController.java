package com.sinosoft.controller.intangibleassets;

import com.sinosoft.common.DataGrid;
import com.sinosoft.common.InvokeResult;
import com.sinosoft.common.SysLog;
import com.sinosoft.domain.intangibleassets.IntangibleAccAssetInfoId;
import com.sinosoft.dto.intangibleassets.IntangibleAccAssetInfoDTO;
import com.sinosoft.service.intangibleassets.IntangibleAccAssetInfoService;
import com.sinosoft.service.intangibleassets.IntangibleChangeSelectService;
import com.sinosoft.service.intangibleassets.IntangibleassetsCardSelectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.crypto.Data;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/intangibleselect")
public class IntangibleselectController {
    private Logger logger = LoggerFactory.getLogger(IntangibleAssetsController.class);
    @Resource
    private IntangibleAccAssetInfoService intangibleAccAssetInfoService;
    @Resource
    private IntangibleassetsCardSelectService intangibleassetsCardSelectService;
    @Resource
    private IntangibleChangeSelectService intangibleChangeSelectService;
    @RequestMapping("/")
    public String page(){ return "intangibleassets/intangibleassetsselect/intangiblecardselect"; }
    /**
     * 查询全部无形卡片管理
     * @param
     * @param
     * @param
     * @return
     */
    @RequestMapping(path = "/list")
    @ResponseBody
    public DataGrid qryIntangAssettype (int page,int rows,IntangibleAccAssetInfoDTO intangibleAccAssetInfoDTO){
        Page<?> result = null;
        try{
            result = intangibleAccAssetInfoService.qryIntangAssetInfo(page,rows,intangibleAccAssetInfoDTO);
        }catch (Exception e){
            logger.error("无形资产查询异常",e);
        }
        return new DataGrid(result);
    }
    /*
     *
     * 台账查询
     * */
    @RequestMapping(path = "/deprelist")
    @ResponseBody
    public List<?> qryAssetDepre (IntangibleAccAssetInfoDTO accAssetInfoDTO){
        List<?> res = intangibleassetsCardSelectService.getAssetDDepre(accAssetInfoDTO);
        return res;
    }
   /* *//**
     * 无形资产类别查询
     * @param value
     * @return
     *//*
    @RequestMapping(path = "/assetType")
    @ResponseBody
    public List<?> assetType(@Param("value") String value){
        return intangibleAccAssetInfoService.qryAssetTypeTree(value);
    }
*/

    /**
     * 导出无形资产卡片查询信息表
     */
    @RequestMapping(path="/intangiblecardselectdownload")
    public void export(HttpServletRequest request, HttpServletResponse response, String name, String queryConditions, String cols){
        try{
            intangibleChangeSelectService.exportByCondition(request, response, name, queryConditions, cols);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @RequestMapping(path = "/print")
    public ModelAndView pagePrint(IntangibleAccAssetInfoDTO accAssetInfoDTO){
//        Map<String, Object> map = new HashMap<>();
//        map.put("accAssetInfoDTO",accAssetInfoDTO);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("intangibleassets/intangiblecardselectprint");
        modelAndView.addObject("map",accAssetInfoDTO);
        return modelAndView;
    }
}
