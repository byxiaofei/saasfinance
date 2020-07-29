package com.sinosoft.domain.account;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * 摘要设置的联合主键
 * 账套类型、账套编码、摘要编码
 */
@Embeddable
public class RemarkId implements Serializable {

    private static final long serialVersionUID = 1L ;

    private String AccBookType ; //账套类型
    private String AccBookCode ; //账套编码
    private String RemarkCode ; //摘要编码

    public RemarkId() { }

    public RemarkId(String AccBookType,String AccBookCode,String remarkCode) {
        this.AccBookType = AccBookType;
        this.AccBookCode = AccBookCode;
        this.RemarkCode = remarkCode;
    }

    public String getAccBookType() { return AccBookType; }
    public void setAccBookType(String accBookType) { AccBookType = accBookType; }

    public String getAccBookCode() { return AccBookCode; }
    public void setAccBookCode(String accBookCode) { AccBookCode = accBookCode; }

    public String getRemarkCode() { return RemarkCode; }
    public void setRemarkCode(String remarkCode) { RemarkCode = remarkCode; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RemarkId remarkId = (RemarkId) o;
        return Objects.equals(AccBookType, remarkId.AccBookType) &&
                Objects.equals(AccBookCode, remarkId.AccBookCode) &&
                Objects.equals(RemarkCode, remarkId.RemarkCode);
    }

    @Override
    public int hashCode() { return Objects.hash(AccBookType, AccBookCode, RemarkCode); }
}
