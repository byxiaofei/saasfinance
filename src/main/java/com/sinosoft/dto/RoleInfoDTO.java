package com.sinosoft.dto;

import com.sinosoft.domain.RoleInfo;


public class RoleInfoDTO {
	private String id;
	
	private String roleCode;
	
    private String roleName;
    
    private String remark;
    
	private String sort;
	
	private String order;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	/**
	 * 通过dto转换成实体类
	 * @param dto
	 * @return
	 */
	public static RoleInfo toEntity(RoleInfoDTO dto) {
		RoleInfo info = new RoleInfo();
		if (dto.getId() != null)
			info.setId(dto.getId().equals("") ? 0 : Integer.parseInt(dto
					.getId()));
		info.setRemark(dto.getRemark());
		info.setRoleCode(dto.getRoleCode());
		info.setRoleName(dto.getRoleName());
		return info;

	}

	/**
	 * 通过实体类转换成DTO
	 * @param info
	 * @return
	 */
	public static RoleInfoDTO toDto(RoleInfo info) {
		
		RoleInfoDTO dto = new RoleInfoDTO();
		dto.setId(String.valueOf(info.getId()));
		dto.setRemark(info.getRemark());
		dto.setRoleCode(info.getRoleCode());
		dto.setRoleName(info.getRoleName());
		return dto;

	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}
}
