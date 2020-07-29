package com.sinosoft.domain;


import javax.persistence.*;
import java.math.BigInteger;


/**
 *
 */
@Entity
@Table(name = "userbranchaccount")
public class UserBranchAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private BigInteger id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserInfo userInfo;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    private BranchInfo branchInfo;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private AccountInfo accountInfo;

    @Column
    private String remark;

    public UserBranchAccount() {
    }

    public UserBranchAccount(BigInteger id) {
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

    public BranchInfo getBranchInfo() {
        return branchInfo;
    }

    public void setBranchInfo(BranchInfo branchInfo) {
        this.branchInfo = branchInfo;
    }

    public AccountInfo getAccountInfo() {
        return accountInfo;
    }

    public void setAccountInfo(AccountInfo accountInfo) {
        this.accountInfo = accountInfo;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
