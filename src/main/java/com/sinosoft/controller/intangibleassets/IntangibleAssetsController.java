package com.sinosoft.controller.intangibleassets;

import com.sinosoft.common.DataGrid;
import com.sinosoft.common.InvokeResult;
import com.sinosoft.common.SysLog;
import com.sinosoft.dto.intangibleassets.IntangibleAccAssetCodeTypeDTO;
import com.sinosoft.service.intangibleassets.IntangibleAssetsService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jiangyc
 * @Description
 * @create 2019-03-26 16:47
 */
@Controller
@RequestMapping("/intangibleAssets")
public class IntangibleAssetsController {
    private Logger logger = LoggerFactory.getLogger(IntangibleAssetsController.class);

    @Resource
    private IntangibleAssetsService intangibleAssetsService;

    @RequestMapping("/")
    public String page(){
        return "intangibleassets/intangibleassets";
    }

    /**
     * 无形资产类别编码信息查看
     * @param dto
     * @return
     */
    @RequestMapping(path = "/list")
    @ResponseBody
    public DataGrid getIntangibleAssetsListData(int page,int rows, IntangibleAccAssetCodeTypeDTO dto){
        Page<?> result = null;
        try{
            result = intangibleAssetsService.qryIntangibleAccAssetCodeTypeList(page,rows,dto);
        }catch (Exception e){
            logger.error("无形资产类别编码查询异常",e);
        }
        return new DataGrid(result);
    }
    /**
     * 打印无形资产类别编码信息
     * @param dto
     * @return
     */
    @RequestMapping(path = "/listPrint")
    @ResponseBody
    public List<?> getAssetsList(IntangibleAccAssetCodeTypeDTO dto){
        return intangibleAssetsService.getAssetsList(dto);
    }
    /**
     * 新增 无形资产类别编码
     * @return
     */
    @SysLog(value = "新增无形资产类别编码")  //这里添加了AOP的自定义注解
    @RequestMapping(path = "/add")
    @ResponseBody
    public InvokeResult saveIntangibleAssetsData(IntangibleAccAssetCodeTypeDTO dto){
//        Page<IntangibleIntangibleAccAssetCodeTypeDTO> result = intangibleAssetsService.add(page, rows, dto);
//        return new DataGrid(result);
        try{
             return intangibleAssetsService.add(dto);
        }catch (Exception e){
            logger.error("新增无形资产编码异常", e);
            return InvokeResult.failure("新增无形资产编码失败，请联系管理员！");
        }
    }

    /**
     * 新增下级 无形资产类别编码
     * @return
     */
    @SysLog(value = "新增下级无形资产类别编码")  //这里添加了AOP的自定义注解
    @RequestMapping(path = "/addLowerLevel")
    @ResponseBody
    public InvokeResult addLowerLevel(IntangibleAccAssetCodeTypeDTO dto){
        try{
           return intangibleAssetsService.addLowerLevel(dto);
        }catch (Exception e){
            logger.error("新增无形资产编码异常", e);
            return InvokeResult.failure("新增无形资产编码失败，请联系管理员！");
        }
    }

    /**
     * 删除 无形资产类别编码
     * @param accBookType
     * @param accBookCode
     * @param codeType
     * @param assetType
     * @return
     */
    @SysLog(value = "删除无形资产类别编码")  //这里添加了AOP的自定义注解
    @RequestMapping(path = "/delete")
    @ResponseBody
    public InvokeResult deleteIntangibleAssetsData( @Param("accBookType")String accBookType, @Param("accBookCode")String accBookCode,
                                                 @Param("codeType")String codeType, @Param("assetType")String assetType){
        try{
            return intangibleAssetsService.delete(accBookType, accBookCode, codeType, assetType);
        }catch (Exception e){
            logger.error("无形资产类别编码删除异常", e);
            return InvokeResult.failure("无形资产编码删除失败，请联系管理员！");
        }
    }


    /**
     * 修改 无形资产类别编码
     * @param dto
     * @return
     */
    @SysLog(value = "编辑无形资产类别编码")  //这里添加了AOP的自定义注解
    @RequestMapping(path = "/update")
    @ResponseBody
    public InvokeResult updateIntangibleAssetsData(IntangibleAccAssetCodeTypeDTO dto){
        try{
            return intangibleAssetsService.update(dto);
        }catch (Exception e){
            logger.error("修改无形资产编码异常", e);
            return InvokeResult.failure("修改无形资产编码失败，请联系管理员！");
        }
    }

    /**
     * 根据科目代码获取科目专项
     * @param specialId
     * @return
     */
    @RequestMapping(path = "/getSpecialId")
    @ResponseBody
    public String getSpecialId(@Param("specialId")String specialId){
        return intangibleAssetsService.getSpecialId(specialId);
    }
    /**
     * 判断科目专项是否为末级
     * @param
     * @return
     */
    @RequestMapping(path = "/judgesubject")
    @ResponseBody
    public InvokeResult judgesubject(@Param("itemCodes")String itemCodes,@Param("articleCodes")String articleCodes){
        return intangibleAssetsService.judgesubject(itemCodes,articleCodes);
    }
    /**
     * 判断该类别是否被卡片使用
     * @param dto
     * @return
     */
    @RequestMapping(path = "/isUse")
    @ResponseBody
    public InvokeResult isUse(IntangibleAccAssetCodeTypeDTO dto){
        return intangibleAssetsService.isUse(dto);
    }

    /**
     * 判断该类别是否有下级
     * @param dto
     * @return
     */
    @RequestMapping(path = "/lowerlevel")
    @ResponseBody
    public String lowerlevel(IntangibleAccAssetCodeTypeDTO dto){
        return intangibleAssetsService.lowerlevel(dto);
    }

    //固定资产类别编码打印
    @RequestMapping(path = "/print")
    public ModelAndView pagePrint(IntangibleAccAssetCodeTypeDTO dto, HttpServletRequest request){
        Map<String, Object> map = new HashMap<>();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("intangibleassets/intangibleassetsprint");
        modelAndView.addObject("map",dto);
        return modelAndView;

    }


}
