package com.sinosoft.dto.testxml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Auther: luodejun
 * @Date: 2020/5/20 16:45
 * @Description:
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "user")
//@XmlType(propOrder = {
//        "userId",
//        "userName",
//        "password",
//        "birthday",
//        "money",   })
public class User implements Serializable {

    // 用户Id
    private int userId;
    // 用户名
    private String userName;
    // 用户密码
    private String password;
    // 用户生日
    private Date birthday;
    // 用户钱包
    private double money;

    private List<Computer> computers;

    public User (){
        super();
    }
    public User(int userId, String userName, String password, Date birthday, double money) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.birthday = birthday;
        this.money = money;
    }


    public List<Computer> getComputers() {
        return computers;
    }

    public void setComputers(List<Computer> computers) {
        this.computers = computers;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", birthday=" + birthday +
                ", money=" + money +
                '}';
    }

}
