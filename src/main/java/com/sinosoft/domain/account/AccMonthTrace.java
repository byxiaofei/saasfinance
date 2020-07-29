package com.sinosoft.domain.account;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "accmonthtrace")
public class AccMonthTrace {
    @EmbeddedId
    private AccMonthTraceId id ;

    @Column(nullable = true)
    private String accMonthStat ;//会计月度状态
    @Column(nullable = true)
    private String createBy ;//操作人
    @Column(nullable = true)
    private String createTime ;//操作时间
    @Column(nullable = true)
    private String temp ;//备用

    public AccMonthTrace() {  }
    public AccMonthTrace(AccMonthTraceId id) { this.id = id; }

    public AccMonthTraceId getId() { return id; }
    public void setId(AccMonthTraceId id) { this.id = id; }

    public String getAccMonthStat() { return accMonthStat; }
    public void setAccMonthStat(String accMonthStat) { this.accMonthStat = accMonthStat; }

    public String getCreateBy() { return createBy; }
    public void setCreateBy(String createBy) { this.createBy = createBy; }

    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }

    public String getTemp() { return temp; }
    public void setTemp(String temp) { this.temp = temp; }
}
