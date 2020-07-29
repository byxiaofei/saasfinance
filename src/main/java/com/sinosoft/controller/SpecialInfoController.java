package com.sinosoft.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.sinosoft.common.DataGrid;
import com.sinosoft.common.InvokeResult;
import com.sinosoft.common.SysLog;
import com.sinosoft.domain.SpecialInfo;
import com.sinosoft.domain.UserInfo;
import com.sinosoft.dto.SpecialInfoDTO;
import com.sinosoft.service.SpecialInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Auther: wangyuge
 *
 */
@Controller
@RequestMapping("/specialinfo")
public class SpecialInfoController {
    private Logger logger = LoggerFactory.getLogger(SpecialInfoController.class);

    @Resource
    private SpecialInfoService specialInfoService ;


    @RequestMapping("/")
    public String page(){
        return "system/systemspecialmanage";
    }

   /* *//**
     *查询全部的专项信息
     *
     *//*
    @RequestMapping(path="/list")
    @ResponseBody
    public DataGrid qrySpecialMessage (@RequestParam int page, @RequestParam int rows, SpecialInfo specialInfo) {
        Page<SpecialInfo> res = specialInfoService.qrySpecialMessage(page,rows,specialInfo) ;
        return new DataGrid(res);
    }*/


    /**
     * 按双击展示树状下拉框查，或手动输入询专项信息
     *
     */
    @RequestMapping(path="/Searchlist")
    @ResponseBody
    public DataGrid qrySpecial (@RequestParam int page, @RequestParam int rows, SpecialInfo specialInfo,@RequestParam int flag){
        long start = System.currentTimeMillis();
        Page<?> res = specialInfoService.qrySpecial(page,rows,specialInfo,flag) ;
        System.out.println("专项体系查询耗时："+(System.currentTimeMillis()-start)+"ms");
        return new DataGrid(res);
    }

    /**
     * 通过Id查询
     *
     */
    @RequestMapping(path ="/search")
    @ResponseBody
    public SpecialInfo searchSpecial(long id){
        return specialInfoService.searchSpecial(id);
    }

    /**
     * 获取树状下拉菜单
     * @return
     */
    @RequestMapping(path="/codelist")
    @ResponseBody
    public List<?> qrySpecialCode(String value){
        return specialInfoService.qrySpecialCode(value);
    }

    /**
     * 查询所有父级转向的id 和 对应的SpecialCode
     * @return
     */
    @RequestMapping(path="/superspeciallist")
    @ResponseBody
    public List<?> qrySuperSpecialList(){return specialInfoService.qrySuperSpecialList();}


    /**
     * 保存新建的子项专项信息
     * @param dto
     * @param id
     * @return
     */
    @SysLog(value = "新增专项信息")
    @RequestMapping(path="/saveChild")
    @ResponseBody
    public InvokeResult saveSpecial(SpecialInfoDTO dto,long id){
        try{
            String msg = specialInfoService.saveSpecial(dto,id);
            if(msg.equals("SPECIAL_ISEXIST")){
                return InvokeResult.failure("专项编码已存在");
            }else if(msg.equals("wrongSpell")){
                return InvokeResult.failure("专项编码的格式不正确");
            }
            return InvokeResult.success();
        }catch(Exception e){
            e.printStackTrace();
            return InvokeResult.failure("创建异常");
        }
    }

    /**
     * 保存新建的一级专项信息
     * @param dto
     * @return
     */
    @SysLog(value = "新增专项信息")
    @RequestMapping(path="/save")
    @ResponseBody
    public InvokeResult saveSpecial(SpecialInfoDTO dto){
        try{
            String msg = specialInfoService.saveSpecial(dto);
            if(msg.equals("SPECIAL_ISEXIST")){
                return InvokeResult.failure("专项编码已存在");
            }else if(msg.equals("wrongSpell")){
                return InvokeResult.failure("专项编码的格式不正确");
            }
            return InvokeResult.success();
        }catch(Exception e){

            return InvokeResult.failure("创建异常");
        }

    }

    /**
     * 通过id查询查询子专项的个数
     * @return
     */
    @RequestMapping(path="/countnum")
    @ResponseBody
    public int countChildNum(long id){
        return specialInfoService.countChildNum(id);
    }

    /**
     * 编辑专项信息
     * @param dto
     * @param id
     * @return
     */
    //
    @SysLog(value = "编辑专项信息")  //这里添加了AOP的自定义注解
    @RequestMapping(path="/update")
    @ResponseBody
    public InvokeResult editSpecial(SpecialInfoDTO dto,long id){
        try{
            String msg = specialInfoService.editSpecial(dto,id);
            if(msg.equals("ACCSUB_ISEXISTE")) {
                return InvokeResult.failure("此专项已使用凭证，不能编辑");
            }else if ("EXISTE".equals(msg)){
                return InvokeResult.failure("存在下级，不允许编辑");
            }else if ("SPECIAL_ISEXIST".equals(msg)){
                return InvokeResult.failure("专项编码已存在");
            }else if(msg.equals("wrongSpell")){
                return InvokeResult.failure("专项编码的格式不正确");
            }

            return InvokeResult.success();
        }catch(Exception e){
            logger.error("系统专项编辑异常", e);
            return InvokeResult.failure("编辑专项操作失败");
        }

    }

    /**
     * 删除专项ById
     * @param id
     */
    //
    @SysLog(value = "删除专项信息")  //这里添加了AOP的自定义注解
    @RequestMapping(path="/delete")
    @ResponseBody
    public InvokeResult deleteSpecial(long id){
        try{
            String msg = specialInfoService.deleteSpecial(id);

            if(msg.equals("ACCSUB_ISEXISTE")){
                return InvokeResult.failure("此专项已使用凭证，不能删除");
            }else if ("EXISTE".equals(msg)){
                return InvokeResult.failure("存在下级，不允许编辑");
            }
            return InvokeResult.success();
        }catch(Exception e){
            e.printStackTrace();
            logger.error("删除专项异常", e);
            return InvokeResult.failure("操作失败");
        }
    }

    /**
     * 导出系统专项列表
     */
    @RequestMapping(path="/specialdownload")
    public void export(HttpServletRequest request, HttpServletResponse response, String name, String queryConditions, String cols){
        specialInfoService.exportByCondition(request, response, name, queryConditions, cols);
    }
}
