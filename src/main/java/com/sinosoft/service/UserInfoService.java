package com.sinosoft.service;

import com.sinosoft.domain.UserInfo;
import org.springframework.data.domain.Page;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;

public interface UserInfoService {

	public static final String USER_ISEXISTE = "USER_ISEXIST";

	/**
	 * 分页查询
	 * @param page 起始页
	 * @param rows 记录数
	 * @param userInfo
	 * @return
	 */
	Page<UserInfo> qryUserInfo(int page, int rows, UserInfo userInfo);

	/**
	 * 创建用户
	 * @param userInfo
	 * @return
	 */
	String saveUserInfo(UserInfo userInfo);

	/**
	 * 更新用户
	 * @param userInfo
	 * @return
	 */
	void updateUserInfo(UserInfo userInfo);

	/**
	 * 重置密码
	 * @param id  用户ID
	 */
	void resetPwd(Long id);

	/**
	 * 当前登录用户修改个人密码
	 * @param newpass
	 */
	void resetPassword(String newpass);

	/**
	 * 用户状态变更
	 * @param id 用户ID
	 * @param useFlag 使用状态
	 */
	void changeUseFlag(Long id, String useFlag);

	void userToRole(String roleId, Long userId, BigInteger userBAId);

	void userToBranchAndAccount(String accountId, Long userId, Integer branchId);

	/**
	 * 将符合条件的数据导出至Excel中
	 * @param request
	 * @param response
	 * @param name 导出文件名称
	 * @param queryConditions 导出数据限制条件
	 * @param cols 导出列
	 */
	void exportByCondition(HttpServletRequest request, HttpServletResponse response, String name,
								  String queryConditions, String cols);
}
