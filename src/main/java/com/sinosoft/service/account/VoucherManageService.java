package com.sinosoft.service.account;

import com.sinosoft.common.InvokeResult;
import com.sinosoft.dto.VoucherDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface VoucherManageService {
    List<?> getApproveListData(VoucherDTO dto);

    /**
     * 删除凭证信息
     * @param voucherNo
     * @param yearMonth
     * @return
     */
    String deleteVoucher(String voucherNo, String yearMonth);
    /**
     * 凭证编号重新排序(由于占用已有凭证号或者删除凭证)
     *
     * 当占用已有凭证操作时必须先调用此方法再保存凭证信息；
     * 当删除操作时必须先执行删除操作再调用此方法。
     *
     * 是否需要基层单位、核算单位、账套类型？
     *
     * @param centerCode 核算单位
     * @param branchCode 基层单位
     * @param accBookType 账套类型
     * @param accBookCode 账套编码
     * @param yearMonth 会计期间
     * @param currentVoucherNo 当前凭证号 eg：180200005 or 18JS00005
     * @param type 操作类型（occupy：录入时占用已有凭证操作，del：删除操作）
     */
    public void voucherAnewSortBecauseOccupyOrDel (String centerCode, String branchCode, String accBookType, String accBookCode, String yearMonth, String currentVoucherNo, String type);

    /**
     * 对整个会计期间的凭证进行重新排序
     * @param yearMonth
     * @return
     */
    InvokeResult rearrangementVoucher(String yearMonth);

    /**
     * 占用或删除或重排凭证号后 固定 无形资产中凭证号也要修改
     * @param centerCode
     * @param branchCode
     * @param accBookType
     * @param accBookCode
     * @param list
     */
    void fixedasetAndIntangVoucher(String centerCode, String branchCode, String accBookType, String accBookCode,List<Map<String,String>> list);
    //
    List<?> qryVoucherCodeForCheck(String code,String direction);

    public void exportByCondition(HttpServletRequest request, HttpServletResponse response, String name,
                                  String queryConditions, String cols);


    /**
     *  凭证管理中，所有凭证信息的分录形式导出
     * @param request
     * @param response
     * @param name
     * @param queryConditions
     * @param cols
     */
    public void exportAboutDetailsByCondition(HttpServletRequest request, HttpServletResponse response, String name, String queryConditions, String cols);
}
