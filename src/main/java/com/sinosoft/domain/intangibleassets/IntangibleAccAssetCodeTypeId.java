package com.sinosoft.domain.intangibleassets;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * @author jiangyc
 * @Description
 * @create 2019-03-28 19:21
 */
@Embeddable
public class IntangibleAccAssetCodeTypeId implements Serializable{
    private String accBookType;     //账套类型
    private String accBookCode;     //账套编码
    private String codeType;        //管理类别编码
    private String assetType;       //无形资产类别编码

    public IntangibleAccAssetCodeTypeId() {
    }

    public IntangibleAccAssetCodeTypeId(String centerCode, String branchCode, String accBookType, String accBookCode, String codeType, String assetType) {
        this.accBookType = accBookType;
        this.accBookCode = accBookCode;
        this.codeType = codeType;
        this.assetType = assetType;
    }


    public String getAccBookType() {
        return accBookType;
    }

    public void setAccBookType(String accBookType) {
        this.accBookType = accBookType;
    }

    public String getAccBookCode() {
        return accBookCode;
    }

    public void setAccBookCode(String accBookCode) {
        this.accBookCode = accBookCode;
    }

    public String getCodeType() {
        return codeType;
    }

    public void setCodeType(String codeType) {
        this.codeType = codeType;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }
}
