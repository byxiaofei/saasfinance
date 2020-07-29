package com.sinosoft.common;

import com.sinosoft.domain.UserInfo;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

/**
 * 系统常量
 *
 */
public class CurrentUser {
	public static Subject getSubject() {
		return SecurityUtils.getSubject();
	}
	public static UserInfo getCurrentUser() {
		return (UserInfo) getSubject().getPrincipal();
	}

	public static String getCurrentLoginAccount() {
		return getCurrentUser().getCurrentLoginAccount();
	}
	public static String getCurrentLoginAccountType() {
		return getCurrentUser().getCurrentLoginAccountType();
	}
	public static String getCurrentAccountName() {
		return getCurrentUser().getCurrentAccountName();
	}
	public static String getCurrentLoginManageBranch() {
		return getCurrentUser().getCurrentLoginManageBranch();
	}
	public static String getCurrentManageBranchName() {
		return getCurrentUser().getCurrentManageBranchName();
	}
	public static String getCurrentManageBranchFlag() {
		return getCurrentUser().getCurrentManageBranchFlag();
	}
}