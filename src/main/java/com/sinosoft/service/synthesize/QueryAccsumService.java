package com.sinosoft.service.synthesize;

import com.sinosoft.dto.VoucherDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface QueryAccsumService {
    /**
     * 科目总账查询
     *
     * @param dto
     * @return
     */
    List<?> queryAccsum(VoucherDTO dto);

    String isHasData(VoucherDTO dto);

    void download(HttpServletRequest request, HttpServletResponse response, String name, String queryConditions);
}
