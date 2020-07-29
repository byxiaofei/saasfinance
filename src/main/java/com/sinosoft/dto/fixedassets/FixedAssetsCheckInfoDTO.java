package com.sinosoft.dto.fixedassets;

/**
 * @author jiangyc
 * @Description
 * @create 2019-04-18 15:10
 */

import java.math.BigDecimal;

/**
 * 对账信息DTO
 */
public class FixedAssetsCheckInfoDTO {
    private BigDecimal GDOriginValue;//固定资产账套原值
    private BigDecimal ZWOriginValue;//账务账套原值
    private BigDecimal GDEndDepreMoney;//固定资产账套累计折旧
    private BigDecimal ZWEndDepreMoney;//账务账套累计折旧
    private String flag;//结果

    public BigDecimal getGDOriginValue() {
        return GDOriginValue;
    }

    public void setGDOriginValue(BigDecimal GDOriginValue) {
        this.GDOriginValue = GDOriginValue;
    }

    public BigDecimal getZWOriginValue() {
        return ZWOriginValue;
    }

    public void setZWOriginValue(BigDecimal ZWOriginValue) {
        this.ZWOriginValue = ZWOriginValue;
    }

    public BigDecimal getGDEndDepreMoney() {
        return GDEndDepreMoney;
    }

    public void setGDEndDepreMoney(BigDecimal GDEndDepreMoney) {
        this.GDEndDepreMoney = GDEndDepreMoney;
    }

    public BigDecimal getZWEndDepreMoney() {
        return ZWEndDepreMoney;
    }

    public void setZWEndDepreMoney(BigDecimal ZWEndDepreMoney) {
        this.ZWEndDepreMoney = ZWEndDepreMoney;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}
