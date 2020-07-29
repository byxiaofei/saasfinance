package com.sinosoft.dto;


import java.util.StringTokenizer;

public class MenuInfoDTO {
	private String menuName;
	private String menuCode;
	private String menuIcon;
	private String temp;
	private String script;
	private int id;
	private String superMenu;
	private String childCount;
	private String menuOrder;
	private String createoper;
	private String createoperName;
	private String createTime;
	private String updateoper;
	private String updateoperName;
	private String updateTime;
	public String getMenuName() {
		return menuName;
	}
	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}
	public String getMenuCode() {
		return menuCode;
	}
	public void setMenuCode(String menuCode) {
		this.menuCode = menuCode;
	}
	public String getMenuIcon() {
		return menuIcon;
	}
	public void setMenuIcon(String menuIcon) {
		this.menuIcon = menuIcon;
	}
	public String getTemp() {
		return temp;
	}
	public void setTemp(String temp) {
		this.temp = temp;
	}
	public String getScript() {
		return script;
	}
	public void setScript(String script) {
		this.script = script;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getSuperMenu() {
		return superMenu;
	}
	public void setSuperMenu(String superMenu) {
		this.superMenu = superMenu;
	}
	public String getChildCount() {
		return childCount;
	}
	public void setChildCount(String childCount) {
		this.childCount = childCount;
	}
	public String getMenuOrder() {
		return menuOrder;
	}
	public void setMenuOrder(String menuOrder) {
		this.menuOrder = menuOrder;
	}
	public String getCreateoper() { return createoper; }
	public void setCreateoper(String createoper) { this.createoper = createoper; }
	public String getCreateoperName() { return createoperName; }
	public void setCreateoperName(String createoperName) { this.createoperName = createoperName; }
	public String getCreateTime() { return createTime; }
	public void setCreateTime(String createTime) { this.createTime = createTime; }
	public String getUpdateoper() { return updateoper; }
	public void setUpdateoper(String updateoper) { this.updateoper = updateoper; }
	public String getUpdateoperName() { return updateoperName; }
	public void setUpdateoperName(String updateoperName) { this.updateoperName = updateoperName; }
	public String getUpdateTime() { return updateTime; }
	public void setUpdateTime(String updateTime) { this.updateTime = updateTime; }
}
