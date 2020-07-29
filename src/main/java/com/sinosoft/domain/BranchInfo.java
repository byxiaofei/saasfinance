package com.sinosoft.domain;

import javax.persistence.*;

@Entity
@Table(name = "branchinfo")
public class BranchInfo implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;
	@Column(nullable = false, unique = true)
	private String comCode;
	@Column(nullable = false)
    private String comName;
    private String comEname;
	@Column(nullable = false)
	private String address;
	private String eaddress;
	private String postcode;
	private String phone;
	private String facsimile;
	private String superCom;
	@Column(nullable = false)
	private String level;
	private String manager;
	private String accountant;
	private String ein;
	private String leader;
	private String cashier;
	@Column(nullable = false)
	private String flag;
	private String remark;
	@Column(nullable = false)
	private String createBy;
	@Column(nullable = false)
	private String createTime;
	private String lastModifyBy;
	private String lastModifyTime;
	private String isVirtual;

	public String getIsVirtual() {
		return isVirtual;
	}

	public void setIsVirtual(String isVirtual) {
		this.isVirtual = isVirtual;
	}

	public BranchInfo() {}

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

	public String getRemark() { return remark; }
	public void setRemark(String remark) { this.remark = remark; }

	public String getCreateBy() { return createBy; }
	public void setCreateBy(String createBy) { this.createBy = createBy; }

	public String getCreateTime() { return createTime; }
	public void setCreateTime(String createTime) { this.createTime = createTime; }

	public String getLastModifyBy() { return lastModifyBy; }
	public void setLastModifyBy(String lastModifyBy) { this.lastModifyBy = lastModifyBy; }

	public String getLastModifyTime() { return lastModifyTime; }
	public void setLastModifyTime(String lastModifyTime) { this.lastModifyTime = lastModifyTime; }
}
