package com.sinosoft.repository.fixedassets;

import com.sinosoft.domain.fixedassets.AssetType;
import com.sinosoft.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 固定资产类别编码
 */
@Repository
public interface AssettypeRepository extends BaseRepository<AssetType,Long> {
    @Query(value = "select a.id from assettype a where  a.assettype_code = ?1", nativeQuery = true)
    List<Map<String, Object>> findByAssettypeCode(String assettypeCode);

    /**
     * 查询一级固定资产类别编码
     * @return
     */
    @Query(value = "select a.id AS id,a.assettype_code as text  from assettype a where a.super_code is null order by a.id", nativeQuery = true)
    List<Map<String, Object>> findSuperAssettypeCode();

    /**
     * 查询非一级编码
     * @param superAssetypeCode
     * @return
     */
    @Query(value = "select a.id as id,a.assettype_code as text  from assettype a where a.super_code=?1 order by a.id", nativeQuery = true)
    List<Map<String, Object>> findChildAssettypeCode(Integer superAssetypeCode);

}
