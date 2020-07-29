package com.sinosoft.controller.fixedassets;

import com.sinosoft.common.DataGrid;
import com.sinosoft.common.InvokeResult;
import com.sinosoft.domain.fixedassets.AssetType;
import com.sinosoft.dto.fixedassets.AssetTypeDTO;
import com.sinosoft.service.fixedassets.AssettypeService;
import com.sinosoft.service.fixedassets.CategoryCodingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/assettype")
public class AssettypeController {
    private Logger logger = LoggerFactory.getLogger(AssettypeController.class);

    @Resource
    private AssettypeService assettypeService ;

    @Resource
    private CategoryCodingService categoryCodingService;

    @RequestMapping("/")
    public String page(){ return "fixedassets/assettype"; }

    /**
     * 查询全部固定资产类别
     * @param page
     * @param rows
     * @param assetType
     * @return
     */
    @RequestMapping(path = "list")
    @ResponseBody
    public DataGrid qryAssettype (@RequestParam int page, @RequestParam int rows, AssetType assetType){
        Page<AssetTypeDTO> res = assettypeService.qryAssettype(page,rows,assetType);
        return new DataGrid(res) ;
    }

    /**
     * 查看固定资产类别编码详细信息ByID
     * @param id
     * @return
     */
    @RequestMapping(path = "qryById")
    @ResponseBody
    public AssetTypeDTO qryAssettypeById(long id){
        AssetTypeDTO dto = assettypeService.qryAssettypeById(id);
        return dto;
    }

    /**
     * 查询上级固定资产类别编码
     * @return
     */
    @RequestMapping(path = "supercode")
    @ResponseBody
    public List<?> qrySuperCode(){
        return assettypeService.qrySuperCode();
    }
    /**
     * 保存新增的固定资产类别
     * @param assetType
     * @return
     */
    @RequestMapping(path = "save")
    @ResponseBody
    public InvokeResult saveAssettype(AssetType assetType){
        try{
            String msg = assettypeService.saveAssettype(assetType);
            /*System.out.println("msg:"+msg);
            System.out.println("新增的编码项:"+assetType.getAssettypeCode());*/
            if(msg.equals("ASSETTYPE_ISEXIST"))
                return InvokeResult.failure("固定资产类别已存在！");
            else if(msg.equals("error")) {
                return InvokeResult.failure("保存异常！");
            }
                return InvokeResult.success("保存成功！");
        }catch (Exception e){
            e.printStackTrace();
            return InvokeResult.failure("保存失败！");
        }
    }

    @RequestMapping(path = "delete")
    @ResponseBody
    public InvokeResult deleteAssettype(long id){
        try{
            System.out.println("deleteController");
            assettypeService.deleteAssettype(id);
            return InvokeResult.success("删除成功！") ;
        }catch (Exception e){
            e.printStackTrace();
            return InvokeResult.failure("删除异常！");
        }

    }

    /**
     * 导出固定资产编码类别信息表
     */
    @RequestMapping(path="/categorydownload")
    public void export(HttpServletRequest request, HttpServletResponse response, String name, String queryConditions, String cols){
        categoryCodingService.exportByCondition(request, response, name, queryConditions, cols);
    }

}