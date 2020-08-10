package com.sinosoft.httpclient.domain;

import javax.persistence.*;

@Entity
@Table(name = "taskschedulingdetailsinfo")
public class TaskSchedulingDetailsInfo {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    // 地址信息
    private String url;
    // 开始时间
    private String startTime;
    // 结束时间
    private String endTime;
    // 状态
    private String flag;
    // 标号
    private String batch;
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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
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
        return "TaskSchedulingDetailsInfo{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", flag='" + flag + '\'' +
                ", batch='" + batch + '\'' +
                ", temp1='" + temp1 + '\'' +
                ", temp2='" + temp2 + '\'' +
                ", temp3='" + temp3 + '\'' +
                '}';
    }
}
