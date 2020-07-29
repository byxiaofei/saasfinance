package com.sinosoft.dto.account;

import com.sinosoft.domain.account.AccRemarkManage;
import com.sinosoft.domain.account.RemarkId;

public class AccRemarkManageDTO {
    private long id ;
    private String CenterCode ; //核算单位
    private String AccBookType ; //账套类型
    private String AccBookCode ; //账套编码
    private String RemarkCode ; //摘要编码
//    private RemarkId id ; //联合主键
    private String RemarkName ; //摘要名称
    private String Flag ; //状态
    private String ItemCode ; //科目编码
    private String ItemName ; //科目名称
    private String CreateBy ; //创建人
    private String CreateTime ; //创建时间
    private String ModifyReason ; //修改原因
    private String LastModifyBy ; //最后一次修改人
    private String LastModifyTime ; //最后一次修改时间
    private String Temp ; //备注

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getCenterCode() { return CenterCode; }
    public void setCenterCode(String centerCode) { CenterCode = centerCode; }

    public String getAccBookType() { return AccBookType; }
    public void setAccBookType(String accBookType) { AccBookType = accBookType; }

    public String getAccBookCode() { return AccBookCode; }
    public void setAccBookCode(String accBookCode) { AccBookCode = accBookCode; }

    public String getRemarkCode() { return RemarkCode; }
    public void setRemarkCode(String remarkCode) { RemarkCode = remarkCode; }

    public String getRemarkName() { return RemarkName; }
    public void setRemarkName(String remarkName) { RemarkName = remarkName; }

    public String getFlag() { return Flag; }
    public void setFlag(String flag) { Flag = flag; }

    public String getItemCode() { return ItemCode; }
    public void setItemCode(String itemCode) { ItemCode = itemCode; }

    public String getItemName() { return ItemName; }
    public void setItemName(String itemName) { ItemName = itemName; }

    public String getCreateBy() { return CreateBy; }
    public void setCreateBy(String createBy) { CreateBy = createBy; }

    public String getCreateTime() { return CreateTime; }
    public void setCreateTime(String createTime) { CreateTime = createTime; }

    public String getModifyReason() { return ModifyReason; }
    public void setModifyReason(String modifyReason) { ModifyReason = modifyReason; }

    public String getLastModifyBy() { return LastModifyBy; }
    public void setLastModifyBy(String lastModifyBy) { LastModifyBy = lastModifyBy; }

    public String getLastModifyTime() { return LastModifyTime; }
    public void setLastModifyTime(String lastModifyTime) { LastModifyTime = lastModifyTime; }

    public String getTemp() { return Temp; }
    public void setTemp(String temp) { Temp = temp; }

    public AccRemarkManageDTO() { }
    public AccRemarkManageDTO( String AccBookType,String AccBookCode,String RemarkCode) {
        this.AccBookType = AccBookType;
        this.AccBookCode = AccBookCode;
        this.RemarkCode = RemarkCode ;
    }

    public static AccRemarkManageDTO toDTO(AccRemarkManage accRemarkManage){
        AccRemarkManageDTO dto = new AccRemarkManageDTO();
        dto.setId(accRemarkManage.getId());
        dto.setCenterCode(accRemarkManage.getCenterCode());
        //-------------------------------------------------------
        dto.setAccBookType(accRemarkManage.getAccBookType());
        dto.setAccBookCode(accRemarkManage.getAccBookCode());
        dto.setRemarkCode(accRemarkManage.getRemarkCode());
       /* dto.setAccBookType(accRemarkManage.getId().getAccBookType());
        dto.setAccBookCode(accRemarkManage.getId().getAccBookCode());
        dto.setRemarkCode(accRemarkManage.getId().getRemarkCode());*/
        dto.setRemarkName(accRemarkManage.getRemarkName());
        dto.setFlag(accRemarkManage.getFlag());
        dto.setItemCode(accRemarkManage.getItemCode());
        dto.setItemName(accRemarkManage.getItemName());
        dto.setCreateBy(accRemarkManage.getCreateBy());
        dto.setCreateTime(accRemarkManage.getCreateTime());
        dto.setModifyReason(accRemarkManage.getModifyReason());
        dto.setLastModifyBy(accRemarkManage.getLastModifyBy());
        dto.setLastModifyTime(accRemarkManage.getLastModifyTime());
        dto.setTemp(accRemarkManage.getTemp());
        return dto ;
    }
    public static AccRemarkManage toEntity(AccRemarkManageDTO dto){
        AccRemarkManage accRemarkManage = new AccRemarkManage();
       // RemarkId id = new RemarkId(dto.getAccBookType(),dto.getAccBookCode(),dto.getRemarkCode()) ;
      //  accRemarkManage.setId(id);
        accRemarkManage.setId(dto.getId());
        accRemarkManage.setCenterCode(dto.getCenterCode());
        //------------------------------
        accRemarkManage.setAccBookType(dto.getAccBookType());
        accRemarkManage.setAccBookCode(dto.getAccBookCode());
        accRemarkManage.setRemarkCode(dto.getRemarkCode());
        //--------------------------------------
        accRemarkManage.setRemarkName(dto.getRemarkName());
        accRemarkManage.setFlag(dto.getFlag());
        accRemarkManage.setItemCode(dto.getItemCode());
        accRemarkManage.setItemName(dto.getItemName());
        accRemarkManage.setCreateBy(dto.getCreateBy());
        accRemarkManage.setCreateTime(dto.getCreateTime());
        accRemarkManage.setModifyReason(dto.getModifyReason());
        accRemarkManage.setLastModifyBy(dto.getLastModifyBy());
        accRemarkManage.setLastModifyTime(dto.getLastModifyTime());
        accRemarkManage.setTemp(dto.getTemp());
        return accRemarkManage ;
    }

    @Override
    public String toString() {
        return "AccRemarkManageDTO{" +
                "id=" + id +
                ", CenterCode='" + CenterCode + '\'' +
                ", AccBookType='" + AccBookType + '\'' +
                ", AccBookCode='" + AccBookCode + '\'' +
                ", RemarkCode='" + RemarkCode + '\'' +
                ", RemarkName='" + RemarkName + '\'' +
                ", Flag='" + Flag + '\'' +
                ", ItemCode='" + ItemCode + '\'' +
                ", ItemName='" + ItemName + '\'' +
                ", CreateBy='" + CreateBy + '\'' +
                ", CreateTime='" + CreateTime + '\'' +
                ", ModifyReason='" + ModifyReason + '\'' +
                ", LastModifyBy='" + LastModifyBy + '\'' +
                ", LastModifyTime='" + LastModifyTime + '\'' +
                ", Temp='" + Temp + '\'' +
                '}';
    }
}
