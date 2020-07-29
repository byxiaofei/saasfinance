package com.sinosoft.controller;

import com.sinosoft.common.DataGrid;
import com.sinosoft.common.InvokeResult;
import com.sinosoft.common.SysLog;
import com.sinosoft.domain.RoleInfo;
import com.sinosoft.service.RoleInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.List;

/**
 * @Auther: hanchuanxing
 * @Date: 2019/1/11 19:41
 * @Description:
 */

@Controller
@RequestMapping("/roleinfo")
public class RoleInfoController {

    @Resource
    private RoleInfoService roleInfoService;
    private Logger logger = LoggerFactory.getLogger(RoleInfoController.class);

    @RequestMapping("/")
    public String page(){
        return "system/roleinfo";
    }

    @RequestMapping("/list")
    @ResponseBody
    public DataGrid qryByConditions(@RequestParam int page, @RequestParam int rows, RoleInfo roleInfo){
        Page<RoleInfo> res = roleInfoService.qryRoleInfo(page,rows,roleInfo);
        return new DataGrid(res);
    }

    @SysLog(value = "新增角色信息")  //这里添加了AOP的自定义注解
    @PostMapping("/add")
    @ResponseBody
    public InvokeResult add(RoleInfo roleInfo){

        try{
            String msg = roleInfoService.saveRoleInfo(roleInfo);
            if(msg.equals("ROLE_ISEXIST")){
                return InvokeResult.failure("角色编码已存在");
            }

            return InvokeResult.success();
        }catch(Exception e){
            logger.error("创建角色异常", e);
            return InvokeResult.failure("创建角色失败！请联系系统管理员。");
        }
    }

    @SysLog(value = "删除角色信息")  //这里添加了AOP的自定义注解
    @PostMapping("/del/{id}")
    @ResponseBody
    public InvokeResult del(@PathVariable(name = "id") Long id){
        try{
            roleInfoService.delRole(id);
            return InvokeResult.success();
        }catch (Exception e){
            logger.error("删除角色异常", e);
            return InvokeResult.failure("删除角色失败！请联系系统管理员。");
        }
    }

    @SysLog(value = "编辑角色信息")  //这里添加了AOP的自定义注解
    @PostMapping("/update")
    @ResponseBody
    public InvokeResult update(RoleInfo roleInfo){
        try{
            roleInfoService.updateRoleInfo(roleInfo);
            return InvokeResult.success();
        }catch(Exception e){
            logger.error("更新角色异常", e);
            return InvokeResult.failure("更新角色失败！请联系系统管理员。");
        }
    }

    @SysLog(value = "角色关联菜单")  //这里添加了AOP的自定义注解
    @PostMapping("/roleToMenu")
    @ResponseBody
    public InvokeResult roleToMenu(String menuId, Long roleId){
        try{
            roleInfoService.roleToMenu(menuId, roleId);
            return InvokeResult.success();
        }catch (Exception e){
            logger.error("角色关联菜单异常", e);
            return InvokeResult.failure("角色关联菜单失败！请联系系统管理员。");
        }
    }

    @RequestMapping("/getRoleByUserIdAndUserBAId")
    @ResponseBody
    public List<?> getRoleByUserIdAndUserBAId(Long userId, BigInteger userBAId){
        return roleInfoService.getRoleByUserIdAndUserBAId(userId, userBAId);
    }
}
