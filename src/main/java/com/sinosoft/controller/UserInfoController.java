package com.sinosoft.controller;

import com.sinosoft.common.DataGrid;
import com.sinosoft.common.InvokeResult;
import com.sinosoft.common.SysLog;
import com.sinosoft.domain.UserInfo;
import com.sinosoft.service.UserInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;

@Controller
@RequestMapping("/userinfo")
public class UserInfoController {
	@Resource
	private UserInfoService userInfoService;

	private Logger logger = LoggerFactory.getLogger(UserInfoController.class);

	@RequestMapping("/")
	public String page(){
		return "system/userinfo";
	}

	@SysLog(value = "新增用户信息")  //这里添加了AOP的自定义注解
	@PostMapping("/add")
	@ResponseBody
	public InvokeResult add(UserInfo userInfo){

		try{
			String msg = userInfoService.saveUserInfo(userInfo);
			if(msg.equals("USER_ISEXIST")){
				return InvokeResult.failure("用户编码已存在");
			}

			return InvokeResult.success();
		}catch(Exception e){
			logger.error("创建用户异常", e);
			return InvokeResult.failure("创建用户失败！请联系系统管理员。");
		}
	}

	@SysLog(value = "编辑用户信息")  //这里添加了AOP的自定义注解
	@PostMapping("/update")
	@ResponseBody
	public InvokeResult update(UserInfo userInfo){

		try{
			userInfoService.updateUserInfo(userInfo);

			return InvokeResult.success();
		}catch(Exception e){
			logger.error("更新用户失败", e);
			return InvokeResult.failure("更新用户失败！请联系系统管理员。");
		}
	}



	@RequestMapping("/list")
	@ResponseBody
	public DataGrid qryByConditions(@RequestParam int page, @RequestParam int rows, UserInfo userInfo){
		Page<UserInfo> res = userInfoService.qryUserInfo(page,rows,userInfo);
		return new DataGrid(res);
	}

	@SysLog(value = "用户密码重置")  //这里添加了AOP的自定义注解
	@PostMapping("/resetPwd/{id}")
	@ResponseBody
	public InvokeResult resetPwd(@PathVariable(name = "id") Long id){
		try{
			userInfoService.resetPwd(id);
			return InvokeResult.success();
		}catch (Exception e){
			logger.error("重置密码失败", e);
			return InvokeResult.failure("用户密码重置失败！请联系系统管理员。");
		}
	}

	/**
	 * 当前登录用户修改个人密码
	 * @param newpass
	 */
	@SysLog(value = "用户修改密码")  //这里添加了AOP的自定义注解
	@RequestMapping(path="/newpass",method=RequestMethod.POST)
	@ResponseBody
	public String resetPassword(@RequestParam String newpass,HttpServletRequest hrq){
		try{
			userInfoService.resetPassword(newpass);
		}catch(Exception e){
			newpass="错误！";
			e.printStackTrace();
		}
		return newpass;
	}

	@SysLog(value = "启用/停用用户")  //这里添加了AOP的自定义注解
	@PostMapping("/changeUseFlag")
	@ResponseBody
	public InvokeResult changeUseFlag(long id, String useFlag){
		try{
			userInfoService.changeUseFlag(id, useFlag);
			return InvokeResult.success();
		}catch (Exception e){
			if(useFlag.equals("1")){
				logger.error("用户启用失败", e);
				return InvokeResult.failure("用户启用失败！请联系系统管理员。");
			}else{
				return InvokeResult.failure("用户停用失败！请联系系统管理员。");
			}
		}
	}

	@SysLog(value = "用户关联角色")  //这里添加了AOP的自定义注解
	@PostMapping("/userToRole")
	@ResponseBody
	public InvokeResult userToRole(String roleId, Long userId, BigInteger userBAId){
		try{
			userInfoService.userToRole(roleId, userId, userBAId);
			return InvokeResult.success();
		}catch (Exception e){
			logger.error("用户关联角色失败", e);
			return InvokeResult.failure("关联角色失败！请联系系统管理员。");
		}
	}

	@SysLog(value = "用户关联账套")  //这里添加了AOP的自定义注解
	@PostMapping("/userToBranchAndAccount")
	@ResponseBody
	public InvokeResult userToBranchAndAccount(String accountId, Long userId,Integer branchId){
		try{
			userInfoService.userToBranchAndAccount(accountId, userId, branchId);
			return InvokeResult.success();
		}catch (Exception e){
			logger.error("用户关联账套失败", e);
			return InvokeResult.failure("关联账套失败！请联系系统管理员。");
		}
	}

	/**
	 * 导出系统专项列表
	 */
	@RequestMapping(path="/userinfodownload")
	public void export(HttpServletRequest request, HttpServletResponse response, String name, String queryConditions, String cols){
		userInfoService.exportByCondition(request, response, name, queryConditions, cols);
	}

}
