package com.sinosoft.service.account;

import com.sinosoft.domain.account.AccCheckInfo;

import java.util.List;

public interface AccCheckInfoService {
    /**
     * 查询对账信息
     * @param year
     * @return
     */
    List<?> qryAccCheckInfo(String year);

    /**
     * 账务对账
     * @param yearMonthDate 会计期间
     * @param generalLedgerAndDetail 总账与明细账
     * @param generalLedgerAndAssist 总账与辅助账
     * @param assistAndDetail 辅助账与明细账
     * @return
     */
    String computeAccCheckInfo(String yearMonthDate, String isCarry, String generalLedgerAndDetail, String generalLedgerAndAssist, String assistAndDetail);

    /**
     * 对账结果查询
     * @param yearMonthDate
     * @return
     */
    List<?> qryComputeAccCheckInfo(String yearMonthDate, String generalLedgerAndDetail, String generalLedgerAndAssist, String assistAndDetail);
}
