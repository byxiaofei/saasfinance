package com.sinosoft.domain.intangibleassets;

import com.sinosoft.domain.fixedassets.AccGCheckInfoId;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author jiangyc
 * @Description
 * @create 2019-04-09 19:39
 */
@Entity
@Table(name = "accwcheckinfo")
public class AccWCheckInfo {
    @EmbeddedId
    private AccWCheckInfoId id;//联合主键
    private String flag;//无形资产折旧状态
    private String createBy1;//无形资产折旧-计提操作人
    private String createTime1;//无形资产折旧-计提时间
    private String createBy2;//无形资产折旧-生成凭证操作人
    private String createTime2;//无形资产折旧-生成凭证时间
    private String createBy3;//无形资产折旧-反计提操作人
    private String createTime3;//无形资产折旧-反计提时间
    private String createBy4;//无形资产折旧-凭证回退操作人
    private String createTime4;//无形资产折旧-凭证回退时间
    private String isCheck;//无形资产是否已经对账
    private String checkBy;//无形资产对账人
    private String checkTime;//无形资产对账时间

    public AccWCheckInfoId getId() {
        return id;
    }

    public void setId(AccWCheckInfoId id) {
        this.id = id;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getCreateBy1() {
        return createBy1;
    }

    public void setCreateBy1(String createBy1) {
        this.createBy1 = createBy1;
    }

    public String getCreateTime1() {
        return createTime1;
    }

    public void setCreateTime1(String createTime1) {
        this.createTime1 = createTime1;
    }

    public String getCreateBy2() {
        return createBy2;
    }

    public void setCreateBy2(String createBy2) {
        this.createBy2 = createBy2;
    }

    public String getCreateTime2() {
        return createTime2;
    }

    public void setCreateTime2(String createTime2) {
        this.createTime2 = createTime2;
    }

    public String getCreateBy3() {
        return createBy3;
    }

    public void setCreateBy3(String createBy3) {
        this.createBy3 = createBy3;
    }

    public String getCreateTime3() {
        return createTime3;
    }

    public void setCreateTime3(String createTime3) {
        this.createTime3 = createTime3;
    }

    public String getCreateBy4() {
        return createBy4;
    }

    public void setCreateBy4(String createBy4) {
        this.createBy4 = createBy4;
    }

    public String getCreateTime4() {
        return createTime4;
    }

    public void setCreateTime4(String createTime4) {
        this.createTime4 = createTime4;
    }

    public String getIsCheck() {
        return isCheck;
    }

    public void setIsCheck(String isCheck) {
        this.isCheck = isCheck;
    }

    public String getCheckBy() {
        return checkBy;
    }

    public void setCheckBy(String checkBy) {
        this.checkBy = checkBy;
    }

    public String getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(String checkTime) {
        this.checkTime = checkTime;
    }
}
