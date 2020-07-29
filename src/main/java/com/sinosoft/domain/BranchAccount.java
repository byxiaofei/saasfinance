package com.sinosoft.domain;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "branchaccount")
public class BranchAccount implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @ManyToOne
    @JoinColumn(name="branch_id",nullable=false)
    private BranchInfo branchInfo;
    @ManyToOne
    @JoinColumn(name="account_id",nullable=false)
    private AccountInfo accountInfo;
    @Column
    private String remark;

    public BranchAccount(){}

    public BranchAccount(int id){this.id = id;}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
