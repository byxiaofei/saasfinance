package com.sinosoft.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


/**
 */
@Entity
@Table(name = "subjectinfo")
public class SubjectInfo implements java.io.Serializable {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private long id;
	@Column(nullable=false,unique=true)
	private String subjectCode;
    private String subjectName;
    private String subjectType;
    private String superSubject;
	private String allSubject;
    private String direction;
    private String endFlag;
	private String specialId;
    private String level;
    private String account;
	private String useflag;
	private String temp;
    private String createOper;
	private String createTime;
	private String updateOper;
	private String updateTime;

/* @OneToMany(mappedBy = "parent",cascade=CascadeType.ALL,fetch=FetchType.LAZY)
    private Set<MenuInfo> children = new HashSet<MenuInfo>();*/


	public SubjectInfo() {}

	public SubjectInfo(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getSubjectCode() {
		return subjectCode;
	}

	public void setSubjectCode(String subjectCode) {
		this.subjectCode = subjectCode;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public String getSubjectType() {
		return subjectType;
	}

	public void setSubjectType(String subjectType) {
		this.subjectType = subjectType;
	}

	public String getSuperSubject() {
		return superSubject;
	}

	public void setSuperSubject(String superSubject) {
		this.superSubject = superSubject;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getEndFlag() {
		return endFlag;
	}

	public void setEndFlag(String endFlag) {
		this.endFlag = endFlag;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getUseflag() {
		return useflag;
	}

	public void setUseflag(String useflag) {
		this.useflag = useflag;
	}

	public String getTemp() {
		return temp;
	}

	public void setTemp(String temp) {
		this.temp = temp;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getCreateOper() {
		return createOper;
	}

	public void setCreateOper(String createOper) {
		this.createOper = createOper;
	}

	public String getUpdateOper() {
		return updateOper;
	}

	public void setUpdateOper(String updateOper) {
		this.updateOper = updateOper;
	}

	public String getSpecialId() {
		return specialId;
	}

	public void setSpecialId(String specialId) {
		this.specialId = specialId;
	}

	public String getAllSubject() {
		return allSubject;
	}

	public void setAllSubject(String allSubject) {
		this.allSubject = allSubject;
	}
}
