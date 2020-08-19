package com.sinosoft.httpclient.service;

import com.sinosoft.httpclient.dto.PartsVerificationDTO;

import java.util.List;

/**
 * 获取上次调用时间到当前时间，所有配件发票校验的数据
 */
public interface PartsVerificationService {
   
    /**
     *
     * @param partsVerificationList
     * @return
     */
    String getPartsVerification(List<PartsVerificationDTO> partsVerificationList);

}
