package com.sinosoft.domain;

import javax.persistence.*;
import java.math.BigInteger;


/**
 *
 */
@Entity
@Table(name = "userrole")
public class UserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private BigInteger id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserInfo userInfo;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private RoleInfo roleInfo;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    private BranchInfo branchInfo;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private AccountInfo accountInfo;

    @Column
    private String remark;

    public UserRole() {}

    public UserRole(BigInteger id) {
        this.id = id;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public RoleInfo getRoleInfo() {
        return roleInfo;
    }

    public void setRoleInfo(RoleInfo roleInfo) {
        this.roleInfo = roleInfo;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public BranchInfo getBranchInfo() {return branchInfo;}

    public void setBranchInfo(BranchInfo branchInfo) {this.branchInfo = branchInfo;}

    public AccountInfo getAccountInfo() {return accountInfo;}

    public void setAccountInfo(AccountInfo accountInfo) {this.accountInfo = accountInfo;}
}
