package com.sinosoft.controller.fixedassets;

import com.sinosoft.common.DataGrid;
import com.sinosoft.common.InvokeResult;
import com.sinosoft.common.SysLog;
import com.sinosoft.dto.account.AccRemarkManageDTO;
import com.sinosoft.dto.fixedassets.AccAssetCodeTypeDTO;
import com.sinosoft.service.fixedassets.CategoryCodingService;
import org.hibernate.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
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

/**
 * @author jiangyc
 * @Description
 * @create 2019-03-26 16:47
 */
@Controller
@RequestMapping("/categoryCoding")
public class CategoryCodingController {
    private Logger logger = LoggerFactory.getLogger(CategoryCodingController.class);

    @Resource
    private CategoryCodingService categoryCodingService;

    @RequestMapping("/")
    public String page(){
        return "fixedassets/assettype";
    }

    /**
     * 固定资产类别编码信息查看
     * @param dto
     * @return
     */
    @RequestMapping(path = "/list")
    @ResponseBody
    public DataGrid getCategoryCodingListData(int page,int rows, AccAssetCodeTypeDTO dto){
        Page<?> result = null;
        try {
            result = categoryCodingService.qryAccAssetCodeTypeList(page,rows,dto);
        } catch (Exception e) {
            logger.error("固定资产类别编码查询异常",e);
        }
        return new DataGrid(result);
    }
    @RequestMapping(path = "/listPrint")
    @ResponseBody
    public List<?> getAccAssetCodeTypeList( AccAssetCodeTypeDTO dto){
        List<?> result = null;
        try {
            result = categoryCodingService.getAccAssetCodeTypeList(dto);
        } catch (Exception e) {
            logger.error("固定资产类别编码查询异常",e);
        }
        return result;
    }

    /**
     * 新增 固定资产类别编码
     * @return
     */
    @SysLog(value = "新增固定资产类别编码")  //这里添加了AOP的自定义注解
    @RequestMapping(path = "/add")
    @ResponseBody
    public InvokeResult saveCategoryCodingData(AccAssetCodeTypeDTO dto){
//        Page<IntangibleAccAssetCodeTypeDTO> result = categoryCodingService.add(page, rows, dto);
//        return new DataGrid(result);
        try{
            return categoryCodingService.add(dto);
        }catch (Exception e){
            logger.error("新增固定资产编码异常", e);
            return InvokeResult.failure("新增固定资产编码失败，请联系管理员！");
        }
    }

    /**
     * 新增下级 固定资产类别编码
     * @return
     */
    @SysLog(value = "新增下级固定资产类别编码")  //这里添加了AOP的自定义注解
    @RequestMapping(path = "/addLowerLevel")
    @ResponseBody
    public InvokeResult addLowerLevel(AccAssetCodeTypeDTO dto){
        try{
            return categoryCodingService.addLowerLevel(dto);
        }catch (Exception e){
            logger.error("新增固定资产编码异常", e);
            return InvokeResult.failure("新增固定资产编码失败，请联系管理员！");
        }
    }

    /**
     * 删除 固定资产类别编码
     * @param centerCode
     * @param branchCode
     * @param accBookType
     * @param accBookCode
     * @param codeType
     * @param assetType
     * @return
     */
    @SysLog(value = "删除固定资产类别编码")  //这里添加了AOP的自定义注解
    @RequestMapping(path = "/delete")
    @ResponseBody
    public InvokeResult deleteCategoryCodingData(@Param("centerCode")String centerCode, @Param("branchCode")String branchCode,
                                                 @Param("accBookType")String accBookType, @Param("accBookCode")String accBookCode,
                                                 @Param("codeType")String codeType, @Param("assetType")String assetType){
       try{
             return categoryCodingService.delete(centerCode, branchCode, accBookType, accBookCode, codeType, assetType);
        }catch (Exception e){
            logger.error("固定资产类别编码删除异常", e);
            return InvokeResult.failure("固定资产编码删除失败，请联系管理员！");
        }
    }


    /**
     * 修改 固定资产类别编码
     * @param dto
     * @return
     */
    @SysLog(value = "编辑固定资产类别编码")  //这里添加了AOP的自定义注解
    @RequestMapping(path = "/update")
    @ResponseBody
    public InvokeResult updateCategoryCodingData(AccAssetCodeTypeDTO dto){
        try{
            return categoryCodingService.update(dto);
        }catch (Exception e){
            logger.error("修改固定资产编码异常", e);
            return InvokeResult.failure("修改固定资产编码失败，请联系管理员！");
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
        return categoryCodingService.getSpecialId(specialId);
    }
    /**
     * 判断科目专项是否为末级
     * @param
     * @return
     */
    @RequestMapping(path = "/judgesubject")
    @ResponseBody
    public InvokeResult judgesubject(@Param("itemCodes")String itemCodes,@Param("articleCodes")String articleCodes){
        return categoryCodingService.judgesubject(itemCodes,articleCodes);
    }
    /**
     * 判断该类别是否被卡片使用
     * @param dto
     * @return
     */
    @RequestMapping(path = "/isUse")
    @ResponseBody
    public InvokeResult isUse(AccAssetCodeTypeDTO dto){
        return categoryCodingService.isUse(dto);
    }
    /**
     * 判断该类别是否有下级
     * @param dto
     * @return
     */
    @RequestMapping(path = "/lowerlevel")
    @ResponseBody
    public String lowerlevel(AccAssetCodeTypeDTO dto){
        return categoryCodingService.lowerlevel(dto);
    }

    //固定资产类别编码打印
    @RequestMapping(path = "/print")
    public ModelAndView pagePrint(AccAssetCodeTypeDTO dto, HttpServletRequest request){
        Map<String, Object> map = new HashMap<>();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("fixedassets/assettypeprint");
        modelAndView.addObject("map",dto);
        return modelAndView;

    }

}
