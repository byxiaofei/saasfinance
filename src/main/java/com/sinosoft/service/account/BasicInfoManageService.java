package com.sinosoft.service.account;

import com.sinosoft.domain.AccountInfo;
import com.sinosoft.domain.BranchInfo;
import com.sinosoft.dto.BranchInfoDTO;
import com.sinosoft.dto.CodeManageDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BasicInfoManageService {
    /**
     * 查询基本信息
     * @param codeType
     * @return
     */
    public List<?> qryBasicInfo(String codeType);

    /**
     * 新增基本信息
     * @param dto
     * @return
     */
    public String addBasicInfo(CodeManageDTO dto);

    /**
     * 编辑基本信息
     * @param dto
     * @return
     */
    public String updateBasicInfo(CodeManageDTO dto);

    /**
     * 删除基本信息
     * @param dto
     * @return
     */
    public String deleteBasicInfo(CodeManageDTO dto);

    /**
     * 上移或下移
     * @param type
     * @param dto
     * @return
     */
    public String upOrDownBasicInfo(String type, CodeManageDTO dto);
}
