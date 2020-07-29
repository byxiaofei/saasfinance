package com.sinosoft.service;

import com.sinosoft.domain.AccountInfo;
import com.sinosoft.dto.AccountInfoDTO;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface AccountInfoService {
    /**
     * 根据查询条件查询全部账套信息
     * @param page
     * @param rows
     * @param accountInfo
     * @return
     */
    public Page<AccountInfoDTO> qryAccountInfo(int page, int rows, AccountInfo accountInfo);

    /**
     * 新增账套信息
     * @param dto
     */
    public void saveAccountInfo(AccountInfoDTO dto);

    /**
     * 修改账套信息
     * @param id
     * @param dto
     */
    public void updateAccountInfo(int id, AccountInfoDTO dto);

    /**
     * 根据账套ID删除账套信息
     * @param id
     * @return
     */
    public String deleteAccountInfo(int id);

    /**
     * 账套机构关联
     * @param branchId 机构ID
     * @param accountId 账套ID
     */
    public void saveBranchAccount(String branchId, Integer accountId);

    /**
     * 根据账套编码获取账套ID
     * @param accountCode
     * @return
     */
    public Integer getAccountIdByAccountCode(String accountCode);

    /**
     * 根据用户ID、机构ID获取账套 Tree树信息
     * @param userId
     * @param branchId
     * @return
     */
    List<Map<String, Object>> getAccountByUserIdAndBranchId(Long userId, Integer branchId);
}
