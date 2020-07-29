package com.sinosoft.service;

import com.sinosoft.domain.BranchInfo;
import com.sinosoft.dto.BranchInfoDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BranchInfoService {
    /**
     * 根据查询条件查询全部机构信息
     * @param page
     * @param rows
     * @param branchInfo
     * @return
     */
    public Page<BranchInfoDTO> qryBranchInfo(int page, int rows, BranchInfo branchInfo);

    /**
     * 新增机构信息
     * @param dto
     */
    public void saveBranchInfo(BranchInfoDTO dto);

    /**
     * 修改机构信息
     * @param id
     * @param dto
     */
    public void updateBranchInfo(int id, BranchInfoDTO dto);

    /**
     * 根据机构ID删除机构信息
     * @param id
     * @return
     */
    public String deleteBranchInfo(int id);

    /**
     * 初始化账套关联机构（递归查询）
     * @param id
     * @return
     */
    public List<?> initBranchTreeRecursion(Integer id);
    /**
     * 初始化账套关联机构
     * @param id
     * @return
     */
    public List<?> initBranchTree(Integer id);

    public List<?> getAccountByUserIdAndBranchId(Long userId, Integer branchId);

    public List<?> getBranchAccountByUserId(Long userId);

    public List<BranchInfo> getManageBranchByUserId(Long userId);
}
