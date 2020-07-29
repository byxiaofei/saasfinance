package com.sinosoft.service.account;

import com.sinosoft.dto.account.ProfitLossCarryDownSubjectDTO;

import java.util.List;

public interface ProfitLossCarryDownSubjectService {
    /**
     * 损益结转科目查询
     * @param rightsInterestsCode
     * @return
     */
    List<?> queryProfitLossCarryDownSubject(String rightsInterestsCode);

    /**
     * 保存损益结转科目设置信息
     * @param dto
     */
    void saveProfitLossCarryDownSubject(ProfitLossCarryDownSubjectDTO dto);

    /**
     * 批量保存损益结转科目设置信息
     * @param profitLossCodes 损益结转科目代码
     * @param rightsInterestsCode 权益科目代码
     */
    void saveProfitLossCarryDownSubjectAll(String profitLossCodes, String rightsInterestsCode);

    List<?> codeSelect(String type);
}
