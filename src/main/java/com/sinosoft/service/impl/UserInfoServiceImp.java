package com.sinosoft.service.impl;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.sinosoft.common.CurrentTime;
import com.sinosoft.common.CurrentUser;
import com.sinosoft.common.CusSpecification;
import com.sinosoft.domain.UserBranchAccount;
import com.sinosoft.dto.UserinfoDTO;
import com.sinosoft.repository.*;
import com.sinosoft.domain.UserInfo;
import com.sinosoft.domain.UserRole;
import com.sinosoft.service.UserInfoService;
import com.sinosoft.util.ExcelUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.*;

@Service
public class UserInfoServiceImp implements UserInfoService{
	private static final String INIT_USERCODE="12345678";//初始密码
	@Resource
	private UserInfoRepository userInfoRepository;
	@Resource
	private UserRoleRepository userRoleRepository;
	@Resource
	private RoleInfoRepository roleInfoRepository;
	@Resource
	private UserBranchAccountRepository userBranchAccountRepository;
	@Resource
	private BranchInfoRepository branchInfoRepository;
	@Resource
	private AccountInfoRepository accountInfoRepository;

	@Override
	public Page<UserInfo> qryUserInfo(int page, int rows, UserInfo userInfo) {
		//return userInfoRepository.findAll(new PageRequest((page - 1), rows));
		Page<UserInfo> UserInfoPage = userInfoRepository.findAll(new CusSpecification<UserInfo>().and(
				CusSpecification.Cnd.like("userName", userInfo.getUserName()),
				CusSpecification.Cnd.like("userCode", userInfo.getUserCode()),
				CusSpecification.Cnd.eq("comCode", userInfo.getComCode()),
				CusSpecification.Cnd.eq("deptCode", userInfo.getDeptCode())
		).asc("id"),new PageRequest((page - 1), rows));


			List<UserInfo> userInfoList = UserInfoPage.getContent();
			for(UserInfo userInfoTemp:userInfoList){                    //增强for
				userInfoTemp.setEmail(userInfoTemp.getEmail().replaceAll("(.{3})(.*?)(.{2})(@.*)","$1***$3$4"));
				userInfoTemp.setPhone(userInfoTemp.getPhone().replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2"));
			}
		return UserInfoPage;
	}

	@Transactional
	public String saveUserInfo(UserInfo userInfo) {

		if(userInfoRepository.findByUserCode(userInfo.getUserCode()).size() > 0){
			return USER_ISEXISTE;
		}else{
			UserInfo u = new UserInfo(userInfo.getUserCode(), INIT_USERCODE);
			userInfo.setPassword(u.getPassword());

			userInfo.setUseFlag("1");
			userInfo.setCreateBy(CurrentUser.getCurrentUser().getId() + "");
			userInfo.setCreateTime(CurrentTime.getCurrentTime());

			userInfoRepository.save(userInfo);
			return "success";
		}
	}

	@Transactional
	public void updateUserInfo(UserInfo userInfo) {

		UserInfo u = userInfoRepository.findById(userInfo.getId()).get();

		u.setUserCode(userInfo.getUserCode());
		u.setUserName(userInfo.getUserName());
		u.setComCode(userInfo.getComCode());
		if (!u.getManageCode().equals(userInfo.getManageCode())) {
			cleanUBAR(u, userInfo);

			u.setManageCode(userInfo.getManageCode());
		}
		//u.setDeptCode(userInfo.getDeptCode());// 已弃用字段
		u.setTel(userInfo.getTel());
		u.setPhone(userInfo.getPhone());
		u.setEnName(userInfo.getEnName());
		u.setEmail(userInfo.getEmail());
		u.setItemCode(userInfo.getItemCode());
		u.setLastModifyBy(CurrentUser.getCurrentUser().getId() + "");
		u.setLastModifyTime(CurrentTime.getCurrentTime());

		userInfoRepository.save(u);
	}

	private void cleanUBAR(UserInfo oldU, UserInfo newU){
		/*List<String> oldManageCode = Arrays.asList(oldU.getManageCode().split(","));
			List<String> newManageCode = Arrays.asList(newU.getManageCode().split(","));*/

			/*
				在使用 Arrays.asList（）转化数组成为list的时候，生成了ArrayList，表面上看是 java.util.ArrayList，实际上是Arrays的内部类ArrayList，两个ArrayList，都是继承 AbstractList，，这他妈就是坑，不进去看源码仔细了解，还以为是一个ArrayList。
				Arrays的内部类ArrayList没有重写 AbstractList的add和remove方法，再去看AbstractList的add和remove方法，居然是直接抛出异常 java.lang.UnsupportedOperationException，没有任何处理
				所以，在使用Arrays.asList（）做转化的时候，如果要进行修改操作，就要再转化一次
			 */

		List<String> oldManageCode = new ArrayList<>(Arrays.asList(oldU.getManageCode().split(",")));
		List<String> newManageCode = new ArrayList<>(Arrays.asList(newU.getManageCode().split(",")));

		// 取差集，即得到被删除的机构ID
		oldManageCode.removeAll(newManageCode);
		// 删除此用户被删除管理机构下此前关联的用户机构账套以及用户机构账套角色关联
		// 先查询被删除管理机构下用户机构关联了哪些账套，再依次删用户机构账套角色关联，再删用户机构账套关联
		for (String str : oldManageCode) {
			Integer branchId = Integer.valueOf(str);
			List<UserBranchAccount> listUBA = userBranchAccountRepository.findByUserIdAndBranchId(oldU.getId(), branchId);
			if (listUBA!=null && listUBA.size()>0) {
				for (UserBranchAccount uba : listUBA) {
					List<UserRole> listUR = userRoleRepository.findByUserIdAndBranchIdAndAccountId(oldU.getId(), branchId, uba.getAccountInfo().getId());
					if (listUR!=null && listUR.size()>0) {
						for (UserRole ur : listUR) {
							userRoleRepository.delete(ur);
						}
					}
					userBranchAccountRepository.delete(uba);
				}
			}
		}
	}

	@Transactional
	public void resetPwd(Long id) {
		UserInfo userinfo= userInfoRepository.findById(id).get();
		UserInfo u = new UserInfo(userinfo.getUserCode(), INIT_USERCODE);
		userinfo.setPassword(u.getPassword());
		userInfoRepository.save(userinfo);
	}

	/**
	 * 当前登录用户修改个人密码
	 * @param newpass
	 */
	@Transactional
	public void resetPassword(String newpass) {
		UserInfo userinfo= userInfoRepository.findById(CurrentUser.getCurrentUser().getId()).get();
		UserInfo u = new UserInfo(userinfo.getUserCode(), newpass);
		userinfo.setPassword(u.getPassword());
		userInfoRepository.save(userinfo);
	}

	@Transactional
	public void changeUseFlag(Long id, String useFlag) {
		UserInfo userInfo = userInfoRepository.findById(id).get();
		userInfo.setUseFlag(useFlag);
		userInfo.setLastModifyBy(CurrentUser.getCurrentUser().getId()+"");
		userInfo.setLastModifyTime(CurrentTime.getCurrentTime());
		userInfoRepository.save(userInfo);
	}

	@Override
	@Transactional
	public void userToRole(String roleId, Long userId, BigInteger userBAId) {

		String[] role_ids = roleId.split(",");
		//去掉重复项
		List<String> list = new ArrayList<String>();
		for(String a:role_ids){
			if(!a.equals("") && !list.contains(a)){
				list.add(a);
			}
		}

		UserBranchAccount userBranchAccount = userBranchAccountRepository.findById(userBAId).get();

		Integer branchId = userBranchAccount.getBranchInfo().getId();
		Integer accountId = userBranchAccount.getAccountInfo().getId();

		List<?> userRoles = userRoleRepository.findByUserIdAndBranchIdAndAccountId(userId, branchId, accountId);

		if (list.size()>0) {
			if (userRoles!=null && userRoles.size()>0) {
				for (Object obj: userRoles) {
					UserRole userRole = (UserRole) obj;
					String id = String.valueOf(userRole.getRoleInfo().getId());
					if (!list.contains(id)) {
						userRoleRepository.delete(userRole);
					} else {
						list.remove(id);
					}
				}
				userRoleRepository.flush();
			}

			//添加用户角色信息
			for(String mid : list){
				UserRole userRole= new UserRole();
				userRole.setRoleInfo(roleInfoRepository.findById(Long.valueOf(mid)).get());
				userRole.setUserInfo(userInfoRepository.findById(userId).get());
				userRole.setBranchInfo(branchInfoRepository.findById(branchId).get());
				userRole.setAccountInfo(accountInfoRepository.findById(accountId).get());
				userRoleRepository.save(userRole);
			}
		} else {
			if (userRoles!=null && userRoles.size()>0) {
				for (Object obj: userRoles) {
					UserRole userRole = (UserRole) obj;
					userRoleRepository.delete(userRole);
				}
			}
		}
	}

	@Override
	@Transactional
	public void userToBranchAndAccount(String accountId, Long userId, Integer branchId) {

		String[] account_ids = accountId.split(",");
		//去掉重复项
		List<String> list = new ArrayList<String>();
		for(String a:account_ids){
			if(!a.equals("") && !list.contains(a)){
				list.add(a);
			}
		}

		List<?> userBranchAccounts = userBranchAccountRepository.findByUserIdAndBranchId(userId, branchId);

		if(list.size()>0){
			if (userBranchAccounts!=null && userBranchAccounts.size()>0) {
				for (Object obj: userBranchAccounts) {
					UserBranchAccount userBranchAccount = (UserBranchAccount) obj;
					String id = String.valueOf(userBranchAccount.getAccountInfo().getId());
					if (!list.contains(id)) {
						userBranchAccountRepository.delete(userBranchAccount);
					} else {
						list.remove(id);
					}
				}
				userBranchAccountRepository.flush();
			}

			//添加用户机构账套信息
			for(String mid : list){
				UserBranchAccount userBranchAccount= new UserBranchAccount();
				userBranchAccount.setUserInfo(userInfoRepository.findById(userId).get());
				userBranchAccount.setBranchInfo(branchInfoRepository.findById(branchId).get());
				userBranchAccount.setAccountInfo(accountInfoRepository.findById(Integer.valueOf(mid)).get());
				userBranchAccountRepository.save(userBranchAccount);
			}
		} else {
			if (userBranchAccounts!=null && userBranchAccounts.size()>0) {
				for (Object obj: userBranchAccounts) {
					UserBranchAccount userBranchAccount = (UserBranchAccount) obj;
					userBranchAccountRepository.delete(userBranchAccount);
				}
			}
		}
	}

	@Override
	public void exportByCondition(HttpServletRequest request, HttpServletResponse response, String name, String queryConditions, String cols) {
		ExcelUtil excelUtil=new ExcelUtil();
		StringBuffer sql=new StringBuffer("select u.id as id,u.user_code as userCode,u.user_name as userName,"+
				"u.en_name as EnglishName,b.com_name as ComCodeName,u.manage_code as manageCode,u.dept_code as deptCode,"+
				"u.item_code as itemCode,u.tel as Tel,u.phone as Phone,u.email as email,u.salt as Salt,"+
				" (select c.code_name from codemanage c where c.code_code=u.use_flag and c.code_type='useFlag') as useFlag ,"+
				"u.create_by as createBy,u.create_time as createTime,u.last_modify_by as lastModifyBy,u.last_modify_time as lastModifyTime "+" " +
				"from userinfo u  left join branchinfo b on u.com_code=b.id where 1=1");
		UserinfoDTO userinfoDTO=new UserinfoDTO();
		try {
			userinfoDTO = new ObjectMapper().readValue(queryConditions, UserinfoDTO.class);
		} catch (Exception e){
			e.printStackTrace();
		}

		int paramsNo = 1;
		Map<Integer, Object> params = new HashMap<>();

		if (userinfoDTO.getUserCode() != null && !"".equals(userinfoDTO.getUserCode())) {
			sql.append(" and u.user_code like ?" + paramsNo);
			params.put(paramsNo, "%"+userinfoDTO.getUserCode()+"%");
			paramsNo++;
		}
		if (userinfoDTO.getUserName() != null && !"".equals(userinfoDTO.getUserName())) {
			sql.append(" and u.user_name like ?" + paramsNo);
			params.put(paramsNo, "%"+userinfoDTO.getUserName()+"%");
			paramsNo++;
		}
		if (userinfoDTO.getComCode() != null && !"".equals(userinfoDTO.getComCode())) {
			sql.append(" and u.com_code like ?" + paramsNo);
			params.put(paramsNo, "%"+userinfoDTO.getComCode()+"%");
			paramsNo++;
		}
        if (userinfoDTO.getDeptCode() != null && !"".equals(userinfoDTO.getDeptCode())) {
            sql.append(" and u.dept_code like ?" + paramsNo);
			params.put(paramsNo, "%"+userinfoDTO.getDeptCode()+"%");
			paramsNo++;
        }

		try {
			// 根据条件查询导出数据集
			List<?> dataList = userInfoRepository.queryBySqlSC(sql.toString(),params);


			for (int i = 0; i < dataList.size(); i++) {
				HashMap<String, String> map = (HashMap<String, String>) dataList.get(i);
				map.get("Phone");
				map.get("email");
				map.put("email",map.get("email").replaceAll("(.{3})(.*?)(.{2})(@.*)","$1***$3$4") );
				map.put("Phone", map.get("Phone").replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2") );

			}

			// 导出
			excelUtil.exportu(request, response, name, cols, dataList);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
