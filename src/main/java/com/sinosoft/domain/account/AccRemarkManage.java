package com.sinosoft.domain.account;

import javax.persistence.*;

@Entity
@Table(name = "accremarkmanage")
public class AccRemarkManage {
   private static final long serialVersionUID = 1L;
    //主键（AccBookCode、AccBookName、RemarkCode）
  /*  @EmbeddedId
    private RemarkId id ; //联合主键*/
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private long id ;
  @Column(nullable = true)
    private String CenterCode ; //核算单位
    @Column
    private String AccBookType ; //账套类型
    private String AccBookCode ; //账套编码
    private String RemarkCode ; //摘要编码

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

    public String getCenterCode() { return CenterCode; }
    public void setCenterCode(String centerCode) { CenterCode = centerCode; }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
  //  public void setRemarkCode(String id) { this.id.setRemarkCode(id); }

//------------------------------------------------------------------
    public String getAccBookType() { return AccBookType; }
    public void setAccBookType(String accBookType) { AccBookType = accBookType; }

    public String getAccBookCode() { return AccBookCode; }
    public void setAccBookCode(String accBookCode) { AccBookCode = accBookCode; }

    public String getRemarkCode() { return RemarkCode; }
    public void setRemarkCode(String remarkCode) { RemarkCode = remarkCode; }
//------------------------------------------------------------------
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
}
