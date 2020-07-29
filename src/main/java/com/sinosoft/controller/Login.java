package com.sinosoft.controller;

import com.sinosoft.common.Constant;
import com.sinosoft.common.CurrentTime;
import com.sinosoft.common.InvokeResult;
import com.sinosoft.domain.*;
import com.sinosoft.repository.*;
import com.sinosoft.util.CaptchaUtil;
import com.sinosoft.util.IPUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller
public class Login {
	private static final Logger LOGGER = LoggerFactory.getLogger(Login.class);
	@Resource
	private UserInfoRepository userInfoRepository;
	@Resource
	private SysLoginLogRepository sysLoginLogRepository;
	@Resource
	private AccountInfoRepository accountInfoRepository;
	@Resource
	private BranchInfoRepository branchInfoRepository;
	@Resource
	private UserBranchAccountRepository userBranchAccountRepository;

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(HttpServletRequest request) {
		 Subject currentUser = SecurityUtils.getSubject();
		 System.out.println(currentUser.isAuthenticated());
	        if(currentUser.isAuthenticated()){
	        	request.getSession().setAttribute("t", request.getParameter("t"));
	        	//将待办中的参数增加到session中。
	        	if(request.getParameter("companyCode") != null && !"".equals(request.getParameter("companyCode")) ){
	        		request.getSession().setAttribute("companyCode", request.getParameter("companyCode"));
	        	}
	        	if(request.getParameter("deptNo") != null && !"".equals(request.getParameter("deptNo")) ){
	        		request.getSession().setAttribute("deptNo", request.getParameter("deptNo"));
	        	}
	        	if(request.getParameter("taskYear") != null && !"".equals(request.getParameter("taskYear")) ){
	        		request.getSession().setAttribute("taskYear", request.getParameter("taskYear"));
	        	}
	        	if(request.getParameter("taskMonth") != null && !"".equals(request.getParameter("taskMonth")) ){
	        		request.getSession().setAttribute("taskMonth", request.getParameter("taskMonth"));
	        	}
	            return "redirect:index.do";
	        }
	        
	        String userCode=request.getSession().getAttribute("userId")!=null ? request.getSession().getAttribute("userId").toString() : "";
	        String quickLink=request.getSession().getAttribute("quickLink")!=null ? request.getSession().getAttribute("quickLink").toString() : "";
	        if(!StringUtils.isEmpty(userCode)&& StringUtils.isEmpty(quickLink)){
	        	String sql=" select * from userinfo u where u.useflag='1' and u.usercode='"+userCode+"'";
	        	List<UserInfo> userlist=(List<UserInfo>) userInfoRepository.queryBySql(sql,UserInfo.class);
	    		if(null!=userlist&&userlist.size()>0&&!userlist.isEmpty()){
	    			UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(userCode,"");
	    			SecurityUtils.getSubject().login(usernamePasswordToken);
	    			 return "redirect:shouye.do";
	    		}else{
	    			//说明该用户没有访问该项目的权限,需要处理这块的逻辑；提示用户没有权限查询
	    			request.setAttribute("errMessage", "用户不存在，请联系管理员");
	    			return "unauthorized";
	    		}
    		}else if(!StringUtils.isEmpty(userCode)&&!StringUtils.isEmpty(quickLink)){
    			String sql=" select * from userinfo u where u.useflag='1' and u.usercode='"+userCode+"'";
	        	List<UserInfo> userlist=(List<UserInfo>) userInfoRepository.queryBySql(sql,UserInfo.class);
	    		if(null!=userlist&&userlist.size()>0&&!userlist.isEmpty()){
	    			UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(userCode,"");
	    			SecurityUtils.getSubject().login(usernamePasswordToken);
	    			 return "redirect:index.do";
	    		}else{
	    			//说明该用户没有访问该项目的权限,需要处理这块的逻辑；提示用户没有权限查询
	    			request.setAttribute("errMessage", "用户不存在，请联系管理员");
	    			return "unauthorized";
	    		}
    		}else{
    			request.setAttribute("t", request.getParameter("t"));
    			return "login";
    		}
	        
	}
	
	@RequestMapping(path="/login",method= RequestMethod.POST)
	@ResponseBody
	public InvokeResult LoginIn(@RequestParam String username, @RequestParam String password,@RequestParam String checkCode, HttpServletRequest request){
		String comId = request.getParameter("comId");
		String accountId = request.getParameter("accountId");
		UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(username, password);

		System.out.println("username================="+username);
		System.out.println("loginTime================="+CurrentTime.getCurrentTime());

		SysLoginLog sysLoginLog = new SysLoginLog();
		sysLoginLog.setUserCode(username);
		sysLoginLog.setIp(IPUtils.getClientIp(request));
		if (comId!=null&&!"".equals(comId)) { sysLoginLog.setComId(comId); }
		if (accountId!=null&&!"".equals(accountId)) { sysLoginLog.setAccountId(accountId); }
		sysLoginLog.setLoginDate(CurrentTime.getCurrentTime());
		//设置sessionId
		sysLoginLog.setSessionId(request.getRequestedSessionId());
		//新增验证码功能
		HttpSession session = request.getSession();
		int tempFlag = checkCode(checkCode, session);

		List<UserInfo> userinfo  = (List<UserInfo>)userInfoRepository.findByUserCode(username);
		if (tempFlag != 1) {
			if (tempFlag == -1) {// 验证码为空
				return InvokeResult.failure("请输入验证码");
			} else {
				return InvokeResult.failure("验证码错误");
			}
		} else if (userinfo.size() > 0 && userinfo != null) {

			sysLoginLog.setUserId(userinfo.get(0).getId());

			String flag = userinfo.get(0).getUseFlag();
			if("0".equals(flag)){

				saveSysLoginLog(sysLoginLog, "0", "该用户已停用，请与管理员联系");

				return InvokeResult.failure("该用户已停用，请与管理员联系");
			}

			try{
				SecurityUtils.getSubject().login(usernamePasswordToken);
				if (comId!=null && !"".equals(comId) && !(Arrays.asList(userinfo.get(0).getManageCode().split(",")).contains(comId))) {

					saveSysLoginLog(sysLoginLog, "0", "您无权限操作此机构");

					return InvokeResult.failure("您无权限操作此机构");
				} else {
					// 用户所属机构或管理机构停用是否需要判断处理

				}
				if (accountId!=null && !"".equals(accountId) && comId!=null && !"".equals(comId)) {
					List<UserBranchAccount> list = userBranchAccountRepository.findByUserIdAndBranchId(userinfo.get(0).getId(), Integer.valueOf(comId));
					boolean accountFlag = false;
					for (UserBranchAccount uba : list) {
						if (String.valueOf(uba.getAccountInfo().getId()).equals(accountId)) {
							accountFlag = true;
							break;
						}
					}

					if (!accountFlag) {
						saveSysLoginLog(sysLoginLog, "0", "您无权限操作此账套");

						return InvokeResult.failure("您无权限操作此账套");
					}
				}
				request.getSession().setAttribute("t", request.getParameter("t"));


				AccountInfo accountInfo = null;
				BranchInfo branchInfo = null;

				if (comId!=null&&!"".equals(comId)) {
					branchInfo = branchInfoRepository.findById(Integer.valueOf(comId)).get();
				} else {
					branchInfo = branchInfoRepository.findById(Integer.valueOf(userinfo.get(0).getManageCode().split(",")[0])).get();
					comId = String.valueOf(branchInfo.getId());
				}

				if (accountId!=null&&!"".equals(accountId)) {
					accountInfo = accountInfoRepository.findById(Integer.valueOf(accountId)).get();
				} else {
					List<UserBranchAccount> list = null;
					if (comId!=null&&!"".equals(comId)) {
						list = userBranchAccountRepository.findByUserIdAndBranchId(userinfo.get(0).getId(), Integer.valueOf(comId));
					} else {
						list = userBranchAccountRepository.findByUserId(userinfo.get(0).getId());
					}
					Integer id = list.get(0).getAccountInfo().getId();
					accountInfo = accountInfoRepository.findById(id).get();
				}

				UserInfo sessionUserInfo= (UserInfo) request.getSession().getAttribute("currentUser");

				//当前登录账套编码
				sessionUserInfo.setCurrentLoginAccount(accountInfo.getAccountCode());
				//当前登录账套类型编码
				sessionUserInfo.setCurrentLoginAccountType(accountInfo.getAccountType());
				//当前登录账套名称
				sessionUserInfo.setCurrentAccountName(accountInfo.getAccountName());
				//当前登录用户管理机构
				sessionUserInfo.setCurrentLoginManageBranch(branchInfo.getComCode());
				//当前登录用户管理机构名称
				sessionUserInfo.setCurrentManageBranchName(branchInfo.getComName());
				//当前登录用户管理机构状态
				sessionUserInfo.setCurrentManageBranchFlag(branchInfo.getFlag());

				request.getSession().setAttribute("currentUser", sessionUserInfo);

				saveSysLoginLog(sysLoginLog, "1", "登录成功");

				if (Constant.urlList ==null || Constant.urlList.size()==0) {
					String urlSql = "select s.id as id,s.path as path from sysoperationdburl s where 1=1 and s.flag = 1 and s.effect = 1";
					List<?> list = branchInfoRepository.queryBySqlSC(urlSql);
					if (list!=null && list.size()>0) {
						List<String> urlList = new ArrayList<String>();
						for (Object o : list) {
							String url = (String) ((Map<String, Object>) o).get("path");
							urlList.add(url);
						}
						Constant.urlList = urlList;
					}
				}

				return InvokeResult.success();
			}catch(UnknownAccountException e){
				LOGGER.error(e.getMessage(),e);

				saveSysLoginLog(sysLoginLog, "0", "用户名或密码不正确");

				return InvokeResult.failure("用户名或密码不正确");
			}catch(LockedAccountException e){
				LOGGER.error(e.getMessage(),e);

				saveSysLoginLog(sysLoginLog, "0", "该用户已禁用，请与管理员联系");

				return InvokeResult.failure("该用户已禁用，请与管理员联系");
			}catch(AuthenticationException e){
				LOGGER.error(e.getMessage(),e);

				saveSysLoginLog(sysLoginLog, "0", "用户名或密码不正确");

				return InvokeResult.failure("用户名或密码不正确");
			}catch(Exception e){
				LOGGER.error(e.getMessage(),e);

				saveSysLoginLog(sysLoginLog, "0", "登录失败");

				return InvokeResult.failure("登录失败");
			}
		} else {
			saveSysLoginLog(sysLoginLog, "0", "系统无此用户");

			return InvokeResult.failure("系统无此用户");
		}
	}
	@RequestMapping(value = "/loginOut",method= RequestMethod.POST)
	@ResponseBody
	public InvokeResult loginOut(HttpServletRequest request) {
		List<?> list = sysLoginLogRepository.findBySessionId(request.getRequestedSessionId());
		SysLoginLog sysLoginLog = null;
		if (list!=null && list.size()>0) {
			sysLoginLog = (SysLoginLog) list.get(0);
			sysLoginLog.setOutDate(CurrentTime.getCurrentTime());
		}
		try{
			SecurityUtils.getSubject().logout();
			if (sysLoginLog!=null)
				sysLoginLogRepository.save(sysLoginLog);
			return InvokeResult.success();
		}catch(Exception e){
			LOGGER.error(e.getMessage(),e);
			return InvokeResult.success("退出失败");
		}
	}

	private void saveSysLoginLog (SysLoginLog sysLoginLog, String loginFlag, String loginResult){
		sysLoginLog.setLoginFlag(loginFlag);
		sysLoginLog.setLoginResult(loginResult);
		sysLoginLogRepository.save(sysLoginLog);
	}

	private int checkCode(String checkCode, HttpSession session) {
		int flag;
		String realKey = (String) session.getAttribute("randomString");
		// 如果两个验证码一致，并移除sesssion域中的属性值
		session.removeAttribute("randomString");
		String userKey = checkCode; // 用戶输入验证码

		// 不区分大小写
		if (realKey != null && !realKey.trim().equals("")) {
			realKey = realKey.toLowerCase();
		}
		if (userKey != null && !userKey.trim().equals("")) {
			userKey = userKey.toLowerCase();
		}
		if (realKey == null || realKey == "") {
			return  0;
		}
		// 验证码
		if (userKey == null || userKey == "") {
			return -1;
		} else if (realKey.equals(userKey) || realKey == userKey) {
			return  1;
		} else {
			return  0;
		}

	}

	@RequestMapping(path="/loadimage",method= RequestMethod.GET)
	public void loadimage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			response.setContentType("image/jpeg");//设置相应类型,告诉浏览器输出的内容为图片
			response.setHeader("Pragma", "No-cache");//设置响应头信息，告诉浏览器不要缓存此内容
			response.setHeader("Cache-Control", "no-cache");
			response.setDateHeader("Expire", 0);
			CaptchaUtil.outputCaptcha(request, response);//输出验证码图片方法
		} catch (Exception e) {
			System.out.println("获取验证码失败>>>>   "+ e);
		}
	}

}
