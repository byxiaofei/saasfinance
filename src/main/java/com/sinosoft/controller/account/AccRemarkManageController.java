package com.sinosoft.controller.account;


import com.sinosoft.common.DataGrid;
import com.sinosoft.common.InvokeResult;
import com.sinosoft.common.SysLog;
import com.sinosoft.domain.account.AccRemarkManage;
import com.sinosoft.dto.account.AccRemarkManageDTO;
import com.sinosoft.service.VoucherService;
import com.sinosoft.service.account.AccRemarkManageService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping(value = "/accremarkmanage")
public class AccRemarkManageController {

    @Resource
    private AccRemarkManageService accRemarkManageService ;
    @Resource
    private VoucherService voucherService;

    @RequestMapping("/")
    public String Page(){
        return "account/accremarkmanage";
    }


    /**
     * 查询全部摘要信息
     * @param page
     * @param rows
     * @param accRemarkManageDTO
     * @return
     */
    @RequestMapping("/list")
    @ResponseBody
    public DataGrid qryAll(@RequestParam int page, @RequestParam int rows, AccRemarkManageDTO accRemarkManageDTO){
        Page<AccRemarkManageDTO> result = accRemarkManageService.qryAccRemarkManage(page,rows,accRemarkManageDTO);
        return new DataGrid(result) ;
    }

    /**
     * 根据摘要代码、摘要名称查询对应结果
     * @param RemarkCode
     * @param RemarkName
     * @return
     */
    @RequestMapping("/resultlist")
    @ResponseBody
    public List<?> qryByCodeAndName(String RemarkCode,String RemarkName){
        return accRemarkManageService.qryByCodeAndName(RemarkCode,RemarkName) ;
    }

    /**
     * 查询abstractCode下拉框内容
     * @return
     */
    @RequestMapping("/codelist")
    @ResponseBody
    public List<?> qryCodeList (String type){
        return accRemarkManageService.qryCodeList(type);
    }

    /**
     * 保存新建的摘要信息
     * @param accRemarkManageDTO
     * @return
     */
    @SysLog(value = "新建摘要记录")
    @RequestMapping("/save")
    @ResponseBody
    public InvokeResult saveAccRemark(AccRemarkManageDTO accRemarkManageDTO){
        try{
            String msg = accRemarkManageService.saveAccRemarkManage(accRemarkManageDTO);
            if(msg.equals("ACCREMARKMANAGE_ISEXISTE")){
                return InvokeResult.failure("摘要已存在") ;
            }else if(msg.equals("error")){
                return InvokeResult.failure("保存失败") ;
            }

        }catch (Exception e){
            e.printStackTrace();
            return InvokeResult.failure("保存异常");
        }
        return InvokeResult.success() ;
    }

    /**
     * 编辑摘要信息
     * @param id
     * @param accRemarkManageDTO
     * @return
     */
    @SysLog(value = "编辑摘要记录")
    @RequestMapping("/edit")
    @ResponseBody
    public InvokeResult editAccRemark(long id,AccRemarkManageDTO accRemarkManageDTO){
        try{
            System.out.println("accRemarkManageDTO-->Controller："+accRemarkManageDTO);
            String msg = accRemarkManageService.editAccRemarkManage(id, accRemarkManageDTO);
            if(msg.equals("ACCREMARKMANAGE_ISEXISTE")){
                return InvokeResult.failure("该摘要已存在！") ;
            }
        }catch (Exception e){
            e.printStackTrace();
            return InvokeResult.failure("编辑异常") ;
        }
        return InvokeResult.success();
    }

    /**
     * 删除摘要信息
     * @param AccBookType
     * @param AccBookCode
     * @param RemarkCode
     * @return
     */
    @SysLog(value = "删除摘要记录")
    @RequestMapping("/delete")
    @ResponseBody
    public InvokeResult deleteAccRemark(String AccBookType,String AccBookCode,String RemarkCode){
        try {
            String msg = accRemarkManageService.deleteAccRemarkManage(AccBookType,AccBookCode,RemarkCode);
            if(msg.equals("error")){
                return InvokeResult.failure(msg);
            }
        }catch (Exception e){
            e.printStackTrace();
            return InvokeResult.failure("删除异常") ;
        }
        return InvokeResult.success();
    }
    @RequestMapping(path="/subjectList")
    @ResponseBody
    public List<?> getCheckData(String value){
        return accRemarkManageService.qrySubjectCodeForCheck(value);
    }
}
