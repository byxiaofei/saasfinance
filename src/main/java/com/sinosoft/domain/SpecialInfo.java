package com.sinosoft.domain;


import javax.persistence.*;

@Entity
@Table(name = "specialinfo")
public class SpecialInfo implements java.io.Serializable {


    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id ;

    private String specialCode ;
    private String specialName ;
    private String specialNameP ;
    @Column(nullable = true)
    private String superSpecial ;
    private String endflag ;
    private String account ;
    private String useflag ;
    @Column(nullable = true)
    private String temp ;
    private String createoper ;
    private String createtime ;
    private String updateoper ;
    private String updatetime ;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getSpecialCode() { return specialCode; }
    public void setSpecialCode(String specialCode) { this.specialCode = specialCode;  }
    public String getSpecialName() { return specialName; }
    public void setSpecialName(String specialName) { this.specialName = specialName; }
    public String getSpecialNameP() {  return specialNameP; }
    public void setSpecialNameP(String specialNameP) { this.specialNameP = specialNameP; }
    public String getSuperSpecial() {  return superSpecial; }
    public void setSuperSpecial(String superSpecial) {  this.superSpecial = superSpecial;  }
    public String getEndflag() { return endflag; }
    public void setEndflag(String endflag) {  this.endflag = endflag; }
    public String getAccount() {   return account; }
    public void setAccount(String account) { this.account = account; }
    public String getUseflag() {  return useflag;  }
    public void setUseflag(String useflag) {  this.useflag = useflag; }
    public String getTemp() {  return temp;  }
    public void setTemp(String temp) {  this.temp = temp; }
    public String getCreateoper() { return createoper; }
    public void setCreateoper(String createoper) { this.createoper = createoper; }
    public String getCreatetime() { return createtime; }
    public void setCreatetime(String createtime) { this.createtime = createtime; }
    public String getUpdateoper() { return updateoper; }
    public void setUpdateoper(String updateoper) { this.updateoper = updateoper; }
    public String getUpdatetime() { return updatetime; }
    public void setUpdatetime(String updatetime) { this.updatetime = updatetime; }

    public SpecialInfo() { }
    public SpecialInfo(long id) { this.id = id ; }

    @Override
    public String toString() {
        return "SpecialInfo{" +
                "id=" + id +
                ", specialCode='" + specialCode + '\'' +
                ", specialName='" + specialName + '\'' +
                ", specialNameP='" + specialNameP + '\'' +
                ", superSpecial='" + superSpecial + '\'' +
                ", endflag='" + endflag + '\'' +
                ", account='" + account + '\'' +
                ", useflag='" + useflag + '\'' +
                ", temp='" + temp + '\'' +
                ", createoper='" + createoper + '\'' +
                ", createtime='" + createtime + '\'' +
                ", updateoper='" + updateoper + '\'' +
                ", updatetime='" + updatetime + '\'' +
                '}';
    }
}
