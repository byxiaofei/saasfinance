package com.sinosoft.dto;

import com.sinosoft.domain.SpecialInfo;

public class SpecialInfoDTO {

//--------------------------（开始：这个顺序是用于专项信息上传时，接收上传的Excel文件信息的，如需改动请告知@Wangyuge
    //专项代码
    private String specialCode ;
    //专项简称
    private String specialName ;
    //专项全称
    private String specialNameP ;
    //上级专项代码
    private String endflag ;
    private String superSpecial ;
    private long id ;
//-------------------------------结束）
    private String account ;
    private String useflag ;
    private String temp ;
    private String createoper ;
    private String createoperName ;
    private String createtime ;
    private String updateoper ;
    private String updateoperName ;
    private String updatetime ;
    //专项的父级编码存储的是父级的id，这里将id转为对应的编码；
    private String superSpecialName ;
    private String endflagName ;
    //专项编码的前半部分（大写字母）
    private String specialCodeFather ;
    //专项编码的后半部分（小写字母）
    private String specialCodeChild ;
    //子专项的个数
    private int countNum ;
    private int flag;


    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getSpecialCode() { return specialCode; }
    public void setSpecialCode(String specialCode) { this.specialCode = specialCode; }
    public String getSpecialName() { return specialName; }
    public void setSpecialName(String specialName) { this.specialName = specialName; }
    public String getSpecialNameP() { return specialNameP; }
    public void setSpecialNameP(String specialNameP) { this.specialNameP = specialNameP; }
    public String getSuperSpecial() { return superSpecial; }
    public void setSuperSpecial(String superSpecial) { this.superSpecial = superSpecial; }
    public String getEndflag() { return endflag; }
    public void setEndflag(String endflag) { this.endflag = endflag; }
    public String getAccount() { return account; }
    public void setAccount(String account) { this.account = account; }
    public String getUseflag() { return useflag; }
    public void setUseflag(String useflag) { this.useflag = useflag; }
    public String getTemp() { return temp; }
    public void setTemp(String temp) { this.temp = temp; }
    public String getCreateoper() { return createoper; }
    public void setCreateoper(String createoper) { this.createoper = createoper; }
    public String getCreatetime() { return createtime; }
    public void setCreatetime(String createtime) { this.createtime = createtime; }
    public String getUpdateoper() { return updateoper; }
    public void setUpdateoper(String updateoper) { this.updateoper = updateoper; }
    public String getUpdatetime() { return updatetime; }
    public void setUpdatetime(String updatetime) { this.updatetime = updatetime; }

    public String getCreateoperName() {
        return createoperName;
    }

    public void setCreateoperName(String createoperName) {
        this.createoperName = createoperName;
    }

    public String getUpdateoperName() {
        return updateoperName;
    }

    public void setUpdateoperName(String updateoperName) {
        this.updateoperName = updateoperName;
    }

    public String getSuperSpecialName() { return superSpecialName; }
    public void setSuperSpecialName(String superSpecialName) { this.superSpecialName = superSpecialName; }
    public String getEndflagName() { return endflagName; }
    public void setEndflagName(String endflagName) { this.endflagName = endflagName; }
    public String getSpecialCodeFather() { return specialCodeFather; }
    public void setSpecialCodeFather(String specialCodeFather) { this.specialCodeFather = specialCodeFather; }
    public String getSpecialCodeChild() { return specialCodeChild; }
    public void setSpecialCodeChild(String specialCodeChild) { this.specialCodeChild = specialCodeChild; }
    public int getCountNum() { return countNum; }
    public void setCountNum(int countNum) { this.countNum = countNum; }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public SpecialInfoDTO() {
    }

    public SpecialInfoDTO(long id, String specialCode, String specialName, String specialNameP, String endflag, String account, String superSpecial, String createoper, String createoperName, String updateoper, String updateoperName, String createtime, String updatetime, String temp, String endflagName) {
        this.id = id;
        this.specialCode = specialCode;
        this.specialName = specialName;
        this.specialNameP = specialNameP;
        this.superSpecial = superSpecial;
        this.endflag = endflag;
        this.account = account;
        this.createoper = createoper;
        this.createoperName = createoperName;
        this.updateoper = updateoper;
        this.updateoperName = updateoperName;
        this.createtime = createtime;
        this.updatetime = updatetime;
        this.temp = temp;
        this.endflagName = endflagName;
    }

    public static SpecialInfoDTO toDTO(SpecialInfo info){
        SpecialInfoDTO dto = new SpecialInfoDTO();
       /* String father;
        String child;
        father = info.getSpecialCode().substring()*/
        dto.setId(info.getId());
        dto.setSpecialCode(info.getSpecialCode());
       /*
        dto.setSpecialCodeFather();
        dto.setSpecialCodeChild();
        */
        dto.setSpecialName(info.getSpecialName());
        dto.setSpecialNameP(info.getSpecialNameP());
        dto.setSuperSpecial(info.getSuperSpecial());
        dto.setEndflag(info.getEndflag());
        if("0".equals(info.getEndflag())){
            dto.setEndflagName("末级");
        }else{
            dto.setEndflagName("非末级");
        }
        dto.setAccount(info.getAccount());
        dto.setCreateoper(info.getCreateoper());
        dto.setCreatetime(info.getCreatetime());
        dto.setUpdateoper(info.getUpdateoper());
        dto.setUpdatetime(info.getUpdatetime());
        dto.setTemp(info.getTemp());
        return dto;
    }

    public static SpecialInfo toInfo(SpecialInfoDTO dto){
        SpecialInfo info = new SpecialInfo();
        info.setId(dto.getId());
        info.setSpecialCode(dto.getSpecialCode());
        info.setSpecialName(dto.getSpecialName());
        info.setSpecialNameP(dto.getSpecialNameP());
        info.setSuperSpecial(dto.getSuperSpecial());
        info.setEndflag(dto.getEndflag());
        info.setAccount(dto.getAccount());
        info.setCreateoper(dto.getCreateoper());
        info.setCreatetime(dto.getCreatetime());
        info.setUpdateoper(dto.getUpdateoper());
        info.setUpdatetime(dto.getUpdatetime());
        info.setTemp(dto.getTemp());
        return info;
    }

    public static SpecialInfo toNewEntity(SpecialInfo info){
        SpecialInfo newSpecialInfo = new SpecialInfo();
        newSpecialInfo.setId(info.getId());
        newSpecialInfo.setSpecialCode(info.getSpecialCode());
        newSpecialInfo.setSpecialName(info.getSpecialName());
        newSpecialInfo.setSpecialNameP(info.getSpecialNameP());
        newSpecialInfo.setSuperSpecial(info.getSuperSpecial());
        newSpecialInfo.setEndflag(info.getEndflag());
        newSpecialInfo.setAccount(info.getAccount());
        newSpecialInfo.setUseflag(info.getUseflag());
        newSpecialInfo.setTemp(info.getTemp());
        newSpecialInfo.setCreateoper(info.getCreateoper());
        newSpecialInfo.setCreatetime(info.getCreatetime());
        newSpecialInfo.setUpdateoper(info.getUpdateoper());
        newSpecialInfo.setUpdatetime(info.getUpdatetime());
        return newSpecialInfo;
    }

    @Override
    public String toString() {
        return "SpecialInfoDTO [id=" +id+", specialCode="+ specialCode
                +", specialName="+specialName+", specialNameP="+specialNameP
                +", superSpecial="+superSpecial+", endflag="+endflag
                +", endflagName="+endflagName
                +", superSpecialName="+superSpecialName
                +", specialCodeFather="+specialCodeFather
                +", specialCodeChild="+specialCodeChild
                +", account=" + account +", useflag=" + useflag
                +", temp=" +temp+", createoper=" + createoper + ", createoperName" + createoperName
                +", createtime=" + createtime +", updateoper=" + updateoper + ", updateoperName" + updateoperName
                +", updatetime=" + updatetime + ']';
    }
}
