package com.sinosoft.service.fixedassets;

import com.sinosoft.domain.fixedassets.AssetType;
import com.sinosoft.dto.fixedassets.AssetTypeDTO;
import org.springframework.data.domain.Page;
import java.util.List;

/**
 * 固定资产类别编码
 */
public interface AssettypeService {
    public static final String ASSETTYPE_ISEXISTE = "ASSETTYPE_ISEXIST";

    /**
     * 查询全部固定资产类别
     * @param page
     * @param rows
     * @param assetType
     * @return
     */
    public Page<AssetTypeDTO> qryAssettype(int page, int rows, AssetType assetType) ;

    /**
     * 查看固定资产类别编码详细信息ByID
     * @param id
     * @return
     */
    public AssetTypeDTO qryAssettypeById(long id);

    /**
     * 查询上级固定资产类别编码
     * @return
     */
    public List<?> qrySuperCode();

    /**
     * 保存新建固定资产类别
     * @param assetType
     * @return
     */
    public String saveAssettype(AssetType assetType);
    /**
     * 删除固定资产专项类别
     * @param id
     * @return
     */
    public String deleteAssettype(long id);


}
