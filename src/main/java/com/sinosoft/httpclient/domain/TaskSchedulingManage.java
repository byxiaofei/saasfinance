package com.sinosoft.httpclient.domain;

import javax.persistence.*;

@Entity
@Table(name = "taskschedulingmanage")
public class TaskSchedulingManage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    // 地址路径
    private String url;
    // 地址信息
    private String remark;
    // 备用字段1
    private String temp1;
    // 备用字段2
    private String temp2;
    // 备用字段3
    private String temp3;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTemp1() {
        return temp1;
    }

    public void setTemp1(String temp1) {
        this.temp1 = temp1;
    }

    public String getTemp2() {
        return temp2;
    }

    public void setTemp2(String temp2) {
        this.temp2 = temp2;
    }

    public String getTemp3() {
        return temp3;
    }

    public void setTemp3(String temp3) {
        this.temp3 = temp3;
    }

    @Override
    public String toString() {
        return "TaskSchedulingManage{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", remark='" + remark + '\'' +
                ", temp1='" + temp1 + '\'' +
                ", temp2='" + temp2 + '\'' +
                ", temp3='" + temp3 + '\'' +
                '}';
    }
}
