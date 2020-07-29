package com.sinosoft.service.account;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface QueryDetailAccountService {
    /**
     * 联查明细账
     * @param yearMonth
     * @param beginDate
     * @param endDate
     * @param directionIdx
     * @return
     */
    List<?> queryDetaiList(String yearMonth, String beginDate, String endDate, String directionIdx, String specialNameP);
    /**
     * 联查辅助明细账
     * @return
     */
    List<?> queryAssistList(String yearMonth, String beginDate, String endDate, String directionIdx, String directionOther, String specialSuperCodeS, String specialNameP);

    /**
     * 联查明细账与联查辅助明细账的开始时间
     * @return
     */
    public List<?> getBeginDateList();

    /**
     * 联查明细账导出
     * @param request
     * @param response
     * @param
     * @param
     * @param
     * @param
     */
    void exportData(HttpServletRequest request, HttpServletResponse response,String yearMonth, String beginDate, String endDate, String directionIdx, String specialNameP,String detaiItemName,String dateText);
    void exportDatafz(HttpServletRequest request, HttpServletResponse response,String yearMonth, String beginDate, String endDate, String directionIdx,
                    String specialNameP,String specialSuperCodeS,String directionOther,String itemName,String dateText,String otherName,String directionOtherName,String directionOthers);

}
