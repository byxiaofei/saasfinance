package com.sinosoft.service.synthesize;

import com.sinosoft.common.InvokeResult;
import com.sinosoft.dto.VoucherDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface SpecialSubjectDetailAccountService {
    /**
     * 专项科目明细账查询
     * @param dto
     * @return
     */
    List<?> querySpecialSubjectDetailAccountList(VoucherDTO dto);
    /**
     * 科目代码范围内是否存在专项科目校验
     * @param subjectCode
     * @return
     */
    String checkSpecialSubject(String subjectCode);
    /**
     * 根据专项名称模糊查询专项树，不限专项类别(一级专项)
     * 参数可为空，也支持多查询，即多专项名称(用英文逗号隔开)
     * @param value 可为空，支持多查询(用英文逗号隔开)
     * @return
     */
    List<?> querySpecialTree(String value);
    /**
     * 根据科目名称模糊查询科目树，不限科目类别，如果匹配的是非末级，则非末级下的所有子级全部展示
     * @param value 可为空
     * @return
     */
    List<?> querySubjectTree(String value);

    String isHasData(VoucherDTO voucherDTO);

    void exportData(HttpServletRequest request, HttpServletResponse response, VoucherDTO voucherDTO, String accounRules,String Date1,String Date2);
}
