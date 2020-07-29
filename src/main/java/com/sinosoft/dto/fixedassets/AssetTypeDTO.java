package com.sinosoft.dto.fixedassets;

import com.sinosoft.domain.fixedassets.AssetType;

/**
 * 固定资产类别编码
 */
public class AssetTypeDTO {
    private long id ;
    private String level;
    private String endFlag ;
    private String useFlag ;
    private String codeType ;
    private String superCode ;
    private String assettypeCode ;
    private String shortName;
    private String fullName;
    private double residualValueRate ;
    private String assetAccountCode ;
    private String deprAccountCode ;
    private String specificAssets ;
    private String deprMethod ;
    private String deprLife ;
    private String remark ;
    private String createBy ;
    private String createTime ;
    private String lastModifyBy ;
    private String lastModifyTime ;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public String getEndFlag() { return endFlag; }
    public void setEndFlag(String endFlag) { this.endFlag = endFlag; }
    public String getUseFlag() { return useFlag; }
    public void setUseFlag(String useFlag) { this.useFlag = useFlag; }
    public String getCodeType() { return codeType; }
    public void setCodeType(String codeType) { this.codeType = codeType; }
    public String getSuperCode() { return superCode; }
    public void setSuperCode(String superCode) { this.superCode = superCode; }
    public String getAssettypeCode() { return assettypeCode; }
    public void setAssettypeCode(String assettypeCode) { this.assettypeCode = assettypeCode; }
    public String getShortName() { return shortName; }
    public void setShortName(String shortName) { this.shortName = shortName; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public double getResidualValueRate() { return residualValueRate; }
    public void setResidualValueRate(double residualValueRate) { this.residualValueRate = residualValueRate; }
    public String getAssetAccountCode() { return assetAccountCode; }
    public void setAssetAccountCode(String assetAccountCode) { this.assetAccountCode = assetAccountCode; }
    public String getDeprAccountCode() { return deprAccountCode; }
    public void setDeprAccountCode(String deprAccountCode) { this.deprAccountCode = deprAccountCode; }
    public String getSpecificAssets() { return specificAssets; }
    public void setSpecificAssets(String specificAssets) { this.specificAssets = specificAssets; }
    public String getDeprMethod() { return deprMethod; }
    public void setDeprMethod(String deprMethod) { this.deprMethod = deprMethod; }
    public String getDeprLife() { return deprLife; }
    public void setDeprLife(String deprLife) { this.deprLife = deprLife; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public String getCreateBy() { return createBy; }
    public void setCreateBy(String createBy) { this.createBy = createBy; }
    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
    public String getLastModifyBy() { return lastModifyBy; }
    public void setLastModifyBy(String lastModifyBy) { this.lastModifyBy = lastModifyBy; }
    public String getLastModifyTime() { return lastModifyTime; }
    public void setLastModifyTime(String lastModifyTime) { this.lastModifyTime = lastModifyTime; }

    public static AssetTypeDTO toDTO(AssetType ass){
        AssetTypeDTO dto = new AssetTypeDTO();
        dto.setId(ass.getId());
        dto.setLevel(ass.getLevel());
        dto.setEndFlag(ass.getEndFlag());
        dto.setSuperCode(ass.getSuperCode());
        dto.setAssettypeCode(ass.getAssettypeCode());
        dto.setShortName(ass.getShortName());
        dto.setFullName(ass.getFullName());
        dto.setResidualValueRate(ass.getResidualValueRate());
        dto.setAssetAccountCode(ass.getAssetAccountCode());
        dto.setDeprAccountCode(ass.getDeprAccountCode());
        dto.setSpecificAssets(ass.getSpecificAssets());
        dto.setDeprMethod(ass.getDeprMethod());
        dto.setDeprLife(ass.getDeprLife());
        dto.setRemark(ass.getRemark());
        dto.setCreateBy(ass.getCreateBy());
        dto.setCreateTime(ass.getCreateTime());
        dto.setLastModifyBy(ass.getLastModifyBy());
        dto.setLastModifyTime(ass.getLastModifyTime());
        dto.setCodeType(ass.getCodeType());
        dto.setUseFlag(ass.getUseFlag());
        return dto ;
    }

    @Override
    public String toString() {
        return "AssetTypeDTO{" +
                "id=" + id +
                ", level='" + level + '\'' +
                ", endFlag='" + endFlag + '\'' +
                ", useFlag='" + useFlag + '\'' +
                ", codeType='" + codeType + '\'' +
                ", superCode='" + superCode + '\'' +
                ", assettypeCode='" + assettypeCode + '\'' +
                ", shortName='" + shortName + '\'' +
                ", fullName='" + fullName + '\'' +
                ", residualValueRate=" + residualValueRate +
                ", assetAccountCode='" + assetAccountCode + '\'' +
                ", deprAccountCode='" + deprAccountCode + '\'' +
                ", specificAssets='" + specificAssets + '\'' +
                ", deprMethod='" + deprMethod + '\'' +
                ", deprLife='" + deprLife + '\'' +
                ", remark='" + remark + '\'' +
                ", createBy='" + createBy + '\'' +
                ", createTime='" + createTime + '\'' +
                ", lastModifyBy='" + lastModifyBy + '\'' +
                ", lastModifyTime='" + lastModifyTime + '\'' +
                '}';
    }
}
