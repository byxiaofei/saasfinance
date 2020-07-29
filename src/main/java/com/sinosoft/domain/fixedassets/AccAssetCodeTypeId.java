package com.sinosoft.domain.fixedassets;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * @author jiangyc
 * @Description
 * @create 2019-03-26 11:05
 */

@Embeddable
public class AccAssetCodeTypeId implements Serializable {
    private String accBookType;     //账套类型
    private String accBookCode;     //账套编码
    private String codeType;        //管理类别编码
    private String assetType;       //固定资产类别编码

    public AccAssetCodeTypeId() {
    }

    public AccAssetCodeTypeId(String centerCode, String branchCode, String accBookType, String accBookCode, String codeType, String assetType) {
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
