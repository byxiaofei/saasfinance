package com.sinosoft.domain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;


/**
 */
@Entity
@Table(name = "rolemenu")
public class RoleMenu{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@ManyToOne
	@JoinColumn(name="role_id",nullable=false)
	private RoleInfo roleInfo;
	
	@ManyToOne
	@JoinColumn(name="menu_id",nullable=false)
	private MenuInfo menuInfo;
	
    @Column
    private String remark;
    
    public RoleMenu(){}
    
    public RoleMenu(int id){this.id = id;}
    
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public RoleInfo getRoleInfo() {
		return roleInfo;
	}

	public void setRoleInfo(RoleInfo roleInfo) {
		this.roleInfo = roleInfo;
	}

	public MenuInfo getMenuInfo() {
		return menuInfo;
	}

	public void setMenuInfo(MenuInfo menuInfo) {
		this.menuInfo = menuInfo;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}
