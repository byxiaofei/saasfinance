package com.sinosoft.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


/**
 */
@Entity
@Table(name = "roleinfo")
public class RoleInfo{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(nullable=false,unique=true)
	private String roleCode;
	
    @Column(nullable=false,unique=true)
    private String roleName;
    
    @Column
    private String remark;

    @Column
	private String createBy;
    @Column
    private String createTime;
	@Column
    private String lastModifyTime;
	@Column
    private String lastModifyBy;

    @OneToMany(mappedBy = "roleInfo",cascade= CascadeType.ALL,fetch= FetchType.LAZY)
    private Set<RoleMenu> rolemenu = new HashSet<RoleMenu>();


    @OneToMany(mappedBy = "roleInfo",cascade= CascadeType.ALL,fetch= FetchType.LAZY)
    private Set<UserRole> userRole= new HashSet<UserRole>();
    
    public RoleInfo() {}
    
    public RoleInfo(int id) {
		this.id = id;
	}
    
	public long getId() {
		return id;
	}

	public void setId(long id) {
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

	public Set<RoleMenu> getRolemenu() {
		return rolemenu;
	}

	@JsonIgnore public void setRolemenu(Set<RoleMenu> rolemenu) {
		this.rolemenu = rolemenu;
	}

	public Set<UserRole> getUserRole() {
		return userRole;
	}

	@JsonIgnore public void setUserRole(Set<UserRole> userRole) {
		this.userRole = userRole;
	}

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getLastModifyTime() {
		return lastModifyTime;
	}

	public void setLastModifyTime(String lastModifyTime) {
		this.lastModifyTime = lastModifyTime;
	}

	public String getLastModifyBy() {
		return lastModifyBy;
	}

	public void setLastModifyBy(String lastModifyBy) {
		this.lastModifyBy = lastModifyBy;
	}
}
