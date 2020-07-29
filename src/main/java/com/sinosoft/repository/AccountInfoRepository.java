package com.sinosoft.repository;

import com.sinosoft.domain.AccountInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public interface AccountInfoRepository extends BaseRepository<AccountInfo, Integer> {

    List<?> findByAccountTypeAndAccountCode(String accountType, String accountCode);

    /**
     * 检查账套编码是否已存在
     * @param accountType
     * @param accountCode
     * @return
     */
    @Query(value="select a.id ,a.use_flag as useFlag from AccountInfo a where a.account_type = ?1 and a.account_code = ?2" , nativeQuery = true)
    List<Map<String, Object>> checkExistsAccountCode(String accountType, String accountCode);
    /**
     * 检查账套编码是否已存在
     * @param accountCode
     * @return
     */
    @Query(value="select a.id from AccountInfo a where a.accountCode = ?1")
    List<Map<String, Object>> checkExistsAccountCode(String accountCode);
    /**
     * 检查账套名称是否已存在
     * @param accountName
     * @return
     */
    @Query(value="select a.id from AccountInfo a where a.accountName = ?1")
    List<Map<String, Object>> checkExistsAccountName(String accountName);
    /**
     * 根据用户ID、机构ID获取账套 Tree树信息
     * @param userId
     * @param branchId
     * @return
     */
    @Query(value = "select  a.id as id,a.account_name as text,uba.id aid from branchaccount b left join accountinfo a on a.id = b.account_id left join userbranchaccount uba on uba.branch_id = b.branch_id and uba.account_id = a.id and uba.user_id = ?1 where b.branch_id = ?2 order by a.id", nativeQuery = true)
    List<Map<String, Object>> findByUserIdAndBranchId(Long userId, Integer branchId);
}
