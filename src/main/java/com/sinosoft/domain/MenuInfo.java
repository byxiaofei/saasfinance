package com.sinosoft.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


/**
 */
@Entity
@Table(name = "menuinfo")
public class MenuInfo{
	@Id
	/*@GeneratedValue(strategy=GenerationType.AUTO)*/
	/*@GeneratedValue(strategy=GenerationType.IDENTITY)*/
	@Column(name="id")
	private int id;
	@Column(nullable=false,unique=true)
	private String menuCode;
    private String menuName;
    private String childCount;
    private String menuIcon;
    private String script;
    private String temp;
    private String superMenu;
    private String menuOrder;
    private String createoper;
	private String createTime;
	private String updateoper;
	private String updateTime;

/* @OneToMany(mappedBy = "parent",cascade=CascadeType.ALL,fetch=FetchType.LAZY)
    private Set<MenuInfo> children = new HashSet<MenuInfo>();*/
    
    @OneToMany(mappedBy = "menuInfo",cascade= CascadeType.ALL,fetch= FetchType.LAZY)
    private Set<RoleMenu> rolemenu = new HashSet<RoleMenu>();

    public MenuInfo() {}
    
    public MenuInfo(int id) {
    	this.id = id;
    }
    
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Set<RoleMenu> getRolemenu() {
		return rolemenu;
	}

	@JsonIgnore public void setRolemenu(Set<RoleMenu> rolemenu) {
		this.rolemenu = rolemenu;
	}

	public String getMenuCode() {
		return menuCode;
	}

	public void setMenuCode(String menuCode) {
		this.menuCode = menuCode;
	}

	public String getMenuName() {
		return menuName;
	}

	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}

	public String getChildCount() {
		return childCount;
	}

	public void setChildCount(String childCount) {
		this.childCount = childCount;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public String getTemp() {
		return temp;
	}

	public void setTemp(String temp) {
		this.temp = temp;
	}

	public String getSuperMenu() {
		return superMenu;
	}

	public void setSuperMenu(String superMenu) {
		this.superMenu = superMenu;
	}

	public String getMenuIcon() {
		return menuIcon;
	}

	public void setMenuIcon(String menuIcon) {
		this.menuIcon = menuIcon;
	}

	public String getMenuOrder() {
		return menuOrder;
	}

	public void setMenuOrder(String menuOrder) {
		this.menuOrder = menuOrder;
	}

	public String getCreateoper() { return createoper; }

	public void setCreateoper(String createoper) { this.createoper = createoper; }

	public String getCreateTime() { return createTime; }

	public void setCreateTime(String createTime) { this.createTime = createTime; }

	public String getUpdateoper() { return updateoper; }

	public void setUpdateoper(String updateoper) { this.updateoper = updateoper; }

	public String getUpdateTime() { return updateTime; }

	public void setUpdateTime(String updateTime) { this.updateTime = updateTime; }
}
