package com.sinosoft.dto;

import com.sinosoft.domain.BranchInfo;
import org.apache.commons.lang.StringUtils;

public class BranchInfoDTO {
	private int id;
	private String comCode;
	private String comName;
	private String comEname;
	private String address;
	private String eaddress;
	private String postcode;
	private String phone;
	private String facsimile;
	private String superCom;
	private String superComCode;
	private String superComName;
	private String level;
	private String manager;
	private String accountant;
	private String ein;
	private String leader;
	private String cashier;
	private String flag;
	private String flagName;
	private String remark;
	private String createBy;
	private String createByName;
	private String createTime;
	private String lastModifyBy;
	private String lastModifyByName;
	private String lastModifyTime;
	//是否初始化账套
	private String initAccount;
	//套初始化的参考账套
	private String referToAccount;
	//虚拟机构
	private String isVirtual;
	public String getIsVirtual() {
		return isVirtual;
	}
	public void setIsVirtual(String isVirtual) {
		this.isVirtual = isVirtual;
	}
	public int getId() { return id; }
	public void setId(int id) { this.id = id; }

	public String getComCode() { return comCode; }
	public void setComCode(String comCode) { this.comCode = comCode; }

	public String getComName() { return comName; }
	public void setComName(String comName) { this.comName = comName; }

	public String getComEname() { return comEname; }
	public void setComEname(String comEname) { this.comEname = comEname; }

	public String getAddress() { return address; }
	public void setAddress(String address) { this.address = address; }

	public String getEaddress() { return eaddress; }
	public void setEaddress(String eaddress) { this.eaddress = eaddress; }

	public String getPostcode() { return postcode; }
	public void setPostcode(String postcode) { this.postcode = postcode; }

	public String getPhone() { return phone; }
	public void setPhone(String phone) { this.phone = phone; }

	public String getFacsimile() { return facsimile; }
	public void setFacsimile(String facsimile) { this.facsimile = facsimile; }

	public String getSuperCom() { return superCom; }
	public void setSuperCom(String superCom) { this.superCom = superCom; }

	public String getSuperComCode() { return superComCode; }
	public void setSuperComCode(String superComCode) { this.superComCode = superComCode; }

	public String getSuperComName() { return superComName; }
	public void setSuperComName(String superComName) { this.superComName = superComName; }

	public String getLevel() { return level; }
	public void setLevel(String level) { this.level = level; }

	public String getManager() { return manager; }
	public void setManager(String manager) { this.manager = manager; }

	public String getAccountant() { return accountant; }
	public void setAccountant(String accountant) { this.accountant = accountant; }

	public String getEin() { return ein; }
	public void setEin(String ein) { this.ein = ein; }

	public String getLeader() { return leader; }
	public void setLeader(String leader) { this.leader = leader; }

	public String getCashier() { return cashier; }
	public void setCashier(String cashier) { this.cashier = cashier; }

	public String getFlag() { return flag; }
	public void setFlag(String flag) { this.flag = flag; }

	public String getFlagName() { return flagName; }
	public void setFlagName(String flagName) { this.flagName = flagName; }

	public String getRemark() { return remark; }
	public void setRemark(String remark) { this.remark = remark; }

	public String getCreateBy() { return createBy; }
	public void setCreateBy(String createBy) { this.createBy = createBy; }

	public String getCreateByName() { return createByName; }
	public void setCreateByName(String createByName) { this.createByName = createByName; }

	public String getCreateTime() { return createTime; }
	public void setCreateTime(String createTime) { this.createTime = createTime; }

	public String getLastModifyBy() { return lastModifyBy; }
	public void setLastModifyBy(String lastModifyBy) { this.lastModifyBy = lastModifyBy; }

	public String getLastModifyByName() { return lastModifyByName; }
	public void setLastModifyByName(String lastModifyByName) { this.lastModifyByName = lastModifyByName; }

	public String getLastModifyTime() { return lastModifyTime; }
	public void setLastModifyTime(String lastModifyTime) { this.lastModifyTime = lastModifyTime; }

	public String getInitAccount() { return initAccount; }
	public void setInitAccount(String initAccount) { this.initAccount = initAccount; }

	public String getReferToAccount() { return referToAccount; }
	public void setReferToAccount(String referToAccount) { this.referToAccount = referToAccount; }

	/**
	 * 将BranchInfo实体类数据转换为BranchInfoDTO数据
	 * 其中flagName属性已自动转换(1-有效,2-无效)；superComName、createByName、lastModifyByName属性需自行设置
	 * @param branchInfo
	 * @return BranchInfoDTO
	 */
	public static BranchInfoDTO toDTO(BranchInfo branchInfo){
		BranchInfoDTO dto = new BranchInfoDTO();
		dto.setId(branchInfo.getId());
		dto.setComCode(branchInfo.getComCode());
		dto.setComName(branchInfo.getComName());
		dto.setComEname(branchInfo.getComEname());
		dto.setAddress(branchInfo.getAddress());
		dto.setEaddress(branchInfo.getEaddress());
		dto.setPostcode(branchInfo.getPostcode());
		dto.setPhone(branchInfo.getPhone());
		dto.setFacsimile(branchInfo.getFacsimile());
		if (StringUtils.isNotEmpty(branchInfo.getSuperCom())) {
			dto.setSuperCom(branchInfo.getSuperCom());
		}
		dto.setLevel(branchInfo.getLevel());
		dto.setManager(branchInfo.getManager());
		dto.setAccountant(branchInfo.getAccountant());
		dto.setEin(branchInfo.getEin());
		dto.setLeader(branchInfo.getLeader());
		dto.setCashier(branchInfo.getCashier());
		dto.setFlag(branchInfo.getFlag());
		if("1".equals(branchInfo.getFlag())){
			dto.setFlagName("有效");
		}else{
			dto.setFlagName("无效");
		}
		dto.setRemark(branchInfo.getRemark());
		dto.setCreateBy(branchInfo.getCreateBy());
		dto.setCreateTime(branchInfo.getCreateTime());
		dto.setLastModifyBy(branchInfo.getLastModifyBy());
		dto.setLastModifyTime(branchInfo.getLastModifyTime());
		dto.setIsVirtual(branchInfo.getIsVirtual());
		return dto;
	}

	/**
	 * 将BranchInfoDTO数据转换为BranchInfo实体类数据
	 * @param dto
	 * @return BranchInfo
	 */
	public static BranchInfo toEntity(BranchInfoDTO dto){
		BranchInfo entity = new BranchInfo();
		entity.setId(dto.getId());
		entity.setComCode(dto.getComCode());
		entity.setComName(dto.getComName());
		entity.setComEname(dto.getComEname());
		entity.setAddress(dto.getAddress());
		entity.setEaddress(dto.getEaddress());
		entity.setPostcode(dto.getPostcode());
		entity.setPhone(dto.getPhone());
		entity.setFacsimile(dto.getFacsimile());
		if (StringUtils.isNotEmpty(dto.getSuperCom())) {
			entity.setSuperCom(dto.getSuperCom());
		}
		entity.setLevel(dto.getLevel());
		entity.setManager(dto.getManager());
		entity.setAccountant(dto.getAccountant());
		entity.setEin(dto.getEin());
		entity.setLeader(dto.getLeader());
		entity.setCashier(dto.getCashier());
		entity.setFlag(dto.getFlag());
		entity.setRemark(dto.getRemark());
		entity.setCreateBy(dto.getCreateBy());
		entity.setCreateTime(dto.getCreateTime());
		entity.setLastModifyBy(dto.getLastModifyBy());
		entity.setLastModifyTime(dto.getLastModifyTime());
		entity.setIsVirtual(dto.getIsVirtual());
		return entity;
	}

	/**
	 * 将BranchInfoDTO数据转换为BranchInfo实体类数据
	 * @param branchInfo
	 * @return
	 */
	public BranchInfo toEntity(BranchInfo branchInfo){
		if(branchInfo==null){
			branchInfo = new BranchInfo();
		}
		branchInfo.setId(this.id);
		branchInfo.setComCode(this.comCode);
		branchInfo.setComName(this.comName);
		branchInfo.setComEname(this.comEname);
		branchInfo.setAddress(this.address);
		branchInfo.setEaddress(this.eaddress);
		branchInfo.setPostcode(this.postcode);
		branchInfo.setPhone(this.phone);
		branchInfo.setFacsimile(this.facsimile);
		if (StringUtils.isNotEmpty(this.superCom)) {
			branchInfo.setSuperCom(this.superCom);
		}
		branchInfo.setLevel(this.level);
		branchInfo.setManager(this.manager);
		branchInfo.setAccountant(this.accountant);
		branchInfo.setEin(this.ein);
		branchInfo.setLeader(this.leader);
		branchInfo.setCashier(this.cashier);
		branchInfo.setFlag(this.flag);
		branchInfo.setRemark(this.remark);
		branchInfo.setCreateBy(this.getCreateBy());
		branchInfo.setCreateTime(this.createTime);
		branchInfo.setLastModifyBy(this.lastModifyBy);
		branchInfo.setLastModifyTime(this.lastModifyTime);
		branchInfo.setIsVirtual(this.getIsVirtual());

		return branchInfo;
	}

	@Override
	public String toString() {
		return "BranchInfoDTO [id=" + id + ", comCode='" + comCode + ", comName='"
				+ comName + ", comEname='" + comEname + ", address='" + address + ", eaddress='"
				+ eaddress+ ", postcode='" + postcode+ ", phone='" + phone + ", facsimile='"
				+ facsimile + ", superCom='" + superCom + ", superComName='" + superComName
				+ ", level='" + level + ", manager='" + manager + ", accountant='" + accountant
				+ ", ein='" + ein + ", leader='" + leader + ", cashier='" + cashier + ", flag='"
				+ flag + ", flagName='" + flagName + ", remark='" + remark + ", createBy='"
				+ createBy + ", createByName='" + createByName + ", createTime='" + createTime
				+ ", lastModifyBy='" + lastModifyBy + ", lastModifyByName='" + lastModifyByName
				+", lastModifyTime='" + lastModifyTime + ", initAccount='" + initAccount
				+", referToAccount='" + referToAccount + ']';
	}
}
