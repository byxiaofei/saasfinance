package com.sinosoft.domain.account;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "acccheckinfo")
public class AccCheckInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private AccMonthTraceId id ;//主键ID

    @Column(nullable = true)
    private String isCarry ;//是否已结转
    @Column(nullable = true)
    private String isCheck;//是否已对账
    @Column(nullable = true)
    private String createBy ;//对账人
    @Column(nullable = true)
    private String createTime ;//对账时间

    public AccCheckInfo() {  }
    public AccCheckInfo(AccMonthTraceId id) { this.id = id; }

    public AccMonthTraceId getId() { return id; }
    public void setId(AccMonthTraceId id) { this.id = id; }

    public String getIsCarry() { return isCarry; }
    public void setIsCarry(String isCarry) { this.isCarry = isCarry; }

    public String getIsCheck() { return isCheck; }
    public void setIsCheck(String isCheck) { this.isCheck = isCheck; }

    public String getCreateBy() { return createBy; }
    public void setCreateBy(String createBy) { this.createBy = createBy; }

    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
}
