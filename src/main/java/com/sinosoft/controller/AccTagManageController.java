package com.sinosoft.controller;

import com.sinosoft.common.DataGrid;
import com.sinosoft.common.InvokeResult;
import com.sinosoft.common.SysLog;
import com.sinosoft.domain.AccTagManage;
import com.sinosoft.dto.account.AccTagManageDTO;
import com.sinosoft.service.account.AccTagManageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Auther: hanchuanxing
 * @Date: 2019/2/19 17:29
 * @Description: 标注管理控制器类
 */
@Controller
@RequestMapping("/accTagManage")
public class AccTagManageController {

    @Resource
    private AccTagManageService accTagManageService;

    private Logger logger = LoggerFactory.getLogger(AccTagManageController.class);

    @RequestMapping("/")
    public String page(){
        return "account/acctagmanage";
    }

    @RequestMapping("/querytagjournalvoucher")
    public String page1(){
        return "account/querytagjournalvoucher";
    }

    @SysLog(value = "创建标注信息")  //这里添加了AOP的自定义注解
    @PostMapping("/add")
    @ResponseBody
    public InvokeResult add(AccTagManageDTO accTagManageDTO){
        try{
            String msg = accTagManageService.save(accTagManageDTO);
            if("Tag_ISEXIST".equals(msg)){
                return InvokeResult.failure("标注编码已存在");
            }

            return InvokeResult.success();
        }catch (Exception e){
            logger.error("创建标注异常",e);
            return InvokeResult.failure("创建标注失败！请联系系统管理员。");
        }
    }

    @PostMapping("/list")
    @ResponseBody
    public DataGrid qryByConditions(@RequestParam int page, @RequestParam int rows, AccTagManageDTO accTagManageDTO){
        Page<?> res = accTagManageService.qryAccTagManage(page,rows,accTagManageDTO);
        return new DataGrid(res);
    }

    @SysLog(value = "编辑标注信息")  //这里添加了AOP的自定义注解
    @PostMapping("/update")
    @ResponseBody
    public InvokeResult update(AccTagManageDTO accTagManageDTO){
        try{
            String msg = accTagManageService.update(accTagManageDTO);
            if ("existLower".equals(msg)) {
                return InvokeResult.failure("存在下级的标注不允许编辑");
            } else if ("use".equals(msg)) {
                return InvokeResult.failure("已经使用过的标注不允许编辑");
            }else if("Tag_ISEXIST".equals(msg)) {
                return InvokeResult.failure("标注编码已存在");
            }

            return InvokeResult.success();
        }catch (Exception e){
            logger.error("编辑标注异常",e);
            return InvokeResult.failure("编辑标注失败！请联系系统管理员。");
        }
    }

    @SysLog(value = "删除标注信息")  //这里添加了AOP的自定义注解
    @PostMapping("/del/{id}")
    @ResponseBody
    public InvokeResult del(@PathVariable(name = "id")long id){
        try{
            String result = accTagManageService.delTag(id);
            if ("existLower".equals(result)) {
                return InvokeResult.failure("存在下级的标注不允许删除");
            } else if ("use".equals(result)) {
                return InvokeResult.failure("已经使用过的标注不允许删除");
            }
            return InvokeResult.success();
        }catch (Exception e){
            logger.error("删除标注异常", e);
            return InvokeResult.failure("删除标注失败！请联系系统管理员。");
        }
    }

    /**
     * 标注分录查询
     * @return
     */
    @RequestMapping("/querylist")
    @ResponseBody
    public List<?> queryTagJournalVoucher(){
        return null;
    }

    /**
     * 获取树状下拉菜单
     * @return
     */
    @RequestMapping(path="/tagcodelist")
    @ResponseBody
    public List<?> qryTagCode(String value){
        return accTagManageService.qryTagCode(value);
    }

}
