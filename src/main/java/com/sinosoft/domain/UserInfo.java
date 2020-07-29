package com.sinosoft.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sinosoft.common.SpringContextUtil;
import com.sinosoft.util.Assert;
import com.sinosoft.util.EncryptService;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


/**
 *
 */
@Entity
@Table(name = "userinfo")
public class UserInfo implements java.io.Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final String INIT_USERCODE = "12345678";//初始密码
    // Fields    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(nullable = false, unique = true)
    private String userCode;
    private String userName;
    private String enName; //英文名称
    private String comCode;  //所属机构
    @JsonIgnore
    private String password;
    private String tel;     //座机号
    private String email;
    private String phone;   //手机号
    private String itemCode;    //专项代码
    private String manageCode;  //管理机构
    private String deptCode;  //账套
    private String useFlag;
    private String salt;
    private String createBy;
    private String createTime;
    private String lastModifyBy;
    private String lastModifyTime;
    @Transient
    private String currentLoginAccount;
    @Transient
    private String currentLoginAccountType;
    @Transient
    private String currentAccountName;
    @Transient
    private String currentLoginManageBranch;
    @Transient
    private String currentManageBranchName;
    @Transient
    private String currentManageBranchFlag;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getLastModifyBy() {
        return lastModifyBy;
    }

    public void setLastModifyBy(String lastModifyBy) {
        this.lastModifyBy = lastModifyBy;
    }

    public String getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(String lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    @OneToMany(mappedBy = "userInfo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UserRole> userRole = new HashSet<UserRole>();

    public UserInfo() {
    }

    public UserInfo(int id) {
        this.id = id;
    }


    public UserInfo(String userCode, String password) {
        checkUserAccount(userCode);
        this.salt = UUID.randomUUID().toString();
        if (!StringUtils.isEmpty(password)) {
            this.password = encryptPassword(password);
        } else {
            this.password = encryptPassword(INIT_USERCODE);
        }
        this.userCode = userCode;
    }

    protected static EncryptService passwordEncryptService;

    protected static EncryptService getPasswordEncryptService() {
        if (passwordEncryptService == null) {
            //passwordEncryptService = (EncryptService) BeanFactory.getBean("encryptService");
            passwordEncryptService = (EncryptService) SpringContextUtil.getBean("encryptService");
        }
        return passwordEncryptService;
    }

    protected static void setPasswordEncryptService(EncryptService passwordEncryptService) {
        UserInfo.passwordEncryptService = passwordEncryptService;
    }

    protected String encryptPassword(String password) {
        return getPasswordEncryptService().encryptPassword(password, salt + userCode);
    }

    private void checkUserAccount(String userAccount) {
        Assert.notBlank(userAccount, "用户代码不能为空.");
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getComCode() {
        return comCode;
    }

    public void setComCode(String comCode) {
        this.comCode = comCode;
    }

    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Set<UserRole> getUserRole() {
        return userRole;
    }

    @JsonIgnore public void setUserRole(Set<UserRole> userRole) {
        this.userRole = userRole;
    }

    public String getUseFlag() {
        return useFlag;
    }

    public void setUseFlag(String useFlag) {
        this.useFlag = useFlag;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getManageCode() {
        return manageCode;
    }

    public void setManageCode(String manageCode) {
        this.manageCode = manageCode;
    }

    public String getCurrentLoginAccount() {
        return currentLoginAccount;
    }

    public void setCurrentLoginAccount(String currentLoginAccount) {
        this.currentLoginAccount = currentLoginAccount;
    }

    public String getCurrentLoginAccountType() {
        return currentLoginAccountType;
    }

    public void setCurrentLoginAccountType(String currentLoginAccountType) {
        this.currentLoginAccountType = currentLoginAccountType;
    }

    public String getCurrentAccountName() {
        return currentAccountName;
    }

    public void setCurrentAccountName(String currentAccountName) {
        this.currentAccountName = currentAccountName;
    }

    public String getCurrentLoginManageBranch() {
        return currentLoginManageBranch;
    }

    public void setCurrentLoginManageBranch(String currentLoginManageBranch) {
        this.currentLoginManageBranch = currentLoginManageBranch;
    }

    public String getCurrentManageBranchName() {
        return currentManageBranchName;
    }

    public void setCurrentManageBranchName(String currentManageBranchName) {
        this.currentManageBranchName = currentManageBranchName;
    }

    public String getCurrentManageBranchFlag() {
        return currentManageBranchFlag;
    }

    public void setCurrentManageBranchFlag(String currentManageBranchFlag) {
        this.currentManageBranchFlag = currentManageBranchFlag;
    }
}
