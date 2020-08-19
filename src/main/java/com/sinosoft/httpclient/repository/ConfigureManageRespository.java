package com.sinosoft.httpclient.repository;

import com.sinosoft.httpclient.domain.ConfigureManage;
import com.sinosoft.httpclient.domain.ConfigureManageId;
import com.sinosoft.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ConfigureManageRespository extends BaseRepository<ConfigureManage,ConfigureManageId> {

    /**
     * 根据接口信息查找对应科目代码信息
     * @param interfaceInfo
     * @return
     */
    @Query(value = "select * from bz_configuremanage  where interface_info = ?1  order by interface_type,temp1 asc",nativeQuery = true)
    List<ConfigureManage> queryConfigureManageByInterfaceInfo(String interfaceInfo);

    /**
     * 根据接口信息和接口类型查找对应的科目代码信息
     * @param interfaceInfo
     * @param interfaceType
     * @return
     */
    @Query(value = "select * from bz_configuremanage  where interface_info = ?1 and interface_type = ?2  and branch_code = ?3 order by interface_type,temp1 asc",nativeQuery = true)
    List<ConfigureManage> queryConfigureManagesByInterfaceInfoAndInterfaceTypeAndBranchCode(String interfaceInfo,String interfaceType,String branchCode);
    /**
     * 根据接口信息和接口类型查找对应的科目代码信息
     * @param interfaceInfo
     * @param interfaceType
     * @return
     */
    @Query(value = "select subject_code as subjectInfo,special_code as specialInfo from bz_configuremanage where interface_info = ?1 and interface_type = ?2 order by interface_type,temp1 asc",nativeQuery = true)
    List<Map<String,Object>> queryConfigureManageByInterfaceInfoAndInterfaceType(String interfaceInfo,String interfaceType);
}
