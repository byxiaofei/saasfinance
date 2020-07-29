package com.sinosoft.dto;

import com.sinosoft.domain.SubjectInfo;

public class SubjectInfoDTO {

	//--------------------------（开始：这个顺序是用于科目信息上传时，接收上传的Excel文件信息的，如需改动请告知@Wangyuge
	private String subjectCode;
	private String subjectName;
	private String specialId;
	private String endFlag;
	private String subjectType;
	private String direction;
	private String id;
	//-------------------------------结束）
	private String subjectCodeEnd;
	private String subjectTypeName;
	private String superSubject;
	private String allSubject;
	private String directionName;
	private String endFlagName;
	private String specialIdName;
	private String level;
	private String levelEnd;
	private String account;
	private String accountName;
	private String useflag;
	private String useflagName;
	private String temp;
	private String createOper;
	private String createOperName;
	private String createTime;
	private String updateOper;
	private String updateOperName;
	private String updateTime;

	public String getSpecialIdName() {
		return specialIdName;
	}

	public void setSpecialIdName(String specialIdName) {
		this.specialIdName = specialIdName;
	}

	public String getAllSubject() {
		return allSubject;
	}

	public void setAllSubject(String allSubject) {
		this.allSubject = allSubject;
	}

	public String getSpecialId() {
		return specialId;
	}

	public void setSpecialId(String specialId) {
		this.specialId = specialId;
	}

	public String getLevelEnd() {
		return levelEnd;
	}

	public void setLevelEnd(String levelEnd) {
		this.levelEnd = levelEnd;
	}

	public String getSubjectCodeEnd() {
		return subjectCodeEnd;
	}

	public void setSubjectCodeEnd(String subjectCodeEnd) {
		this.subjectCodeEnd = subjectCodeEnd;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
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

	public String getSubjectTypeName() {
		return subjectTypeName;
	}

	public void setSubjectTypeName(String subjectTypeName) {
		this.subjectTypeName = subjectTypeName;
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

	public String getDirectionName() {
		return directionName;
	}

	public void setDirectionName(String directionName) {
		this.directionName = directionName;
	}

	public String getEndFlag() {
		return endFlag;
	}

	public void setEndFlag(String endFlag) {
		this.endFlag = endFlag;
	}

	public String getEndFlagName() {
		return endFlagName;
	}

	public void setEndFlagName(String endFlagName) {
		this.endFlagName = endFlagName;
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

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getUseflag() {
		return useflag;
	}

	public void setUseflag(String useflag) {
		this.useflag = useflag;
	}

	public String getUseflagName() {
		return useflagName;
	}

	public void setUseflagName(String useflagName) {
		this.useflagName = useflagName;
	}

	public String getTemp() {
		return temp;
	}

	public void setTemp(String temp) {
		this.temp = temp;
	}

	public String getCreateOper() {
		return createOper;
	}

	public void setCreateOper(String createOper) {
		this.createOper = createOper;
	}

	public String getCreateOperName() {
		return createOperName;
	}

	public void setCreateOperName(String createOperName) {
		this.createOperName = createOperName;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getUpdateOper() {
		return updateOper;
	}

	public void setUpdateOper(String updateOper) {
		this.updateOper = updateOper;
	}

	public String getUpdateOperName() {
		return updateOperName;
	}

	public void setUpdateOperName(String updateOperName) {
		this.updateOperName = updateOperName;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public static SubjectInfo toNewEntity(SubjectInfo info){
		SubjectInfo newSubjectInfo = new SubjectInfo();
		newSubjectInfo.setId(info.getId());
		newSubjectInfo.setSubjectCode(info.getSubjectCode());
		newSubjectInfo.setSubjectName(info.getSubjectName());
		newSubjectInfo.setSubjectType(info.getSubjectType());
		newSubjectInfo.setSuperSubject(info.getSuperSubject());
		newSubjectInfo.setAllSubject(info.getAllSubject());
		newSubjectInfo.setDirection(info.getDirection());
		newSubjectInfo.setEndFlag(info.getEndFlag());
		newSubjectInfo.setSpecialId(info.getSpecialId());
		newSubjectInfo.setLevel(info.getLevel());
		newSubjectInfo.setAccount(info.getAccount());
		newSubjectInfo.setUseflag(info.getUseflag());
		newSubjectInfo.setTemp(info.getTemp());
		newSubjectInfo.setCreateOper(info.getCreateOper());
		newSubjectInfo.setCreateTime(info.getCreateTime());
		newSubjectInfo.setUpdateOper(info.getUpdateOper());
		newSubjectInfo.setUpdateTime(info.getUpdateTime());
		return newSubjectInfo;
	}
}
