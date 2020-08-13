package com.sinosoft.httpclient.service;

import com.sinosoft.httpclient.dto.PartsVerificationDTO;

import java.util.List;

/**
 * 获取上次调用时间到当前时间，所有配件发票校验的数据
 */
public interface PartsVerificationService {
    /**
     *获取业务数据信息
     */
    String getPartsVerification(List<PartsVerificationDTO> partsVerificationList);

    /**
     * 保存业务数据信息
     */
    void  savePartsVerification();
}
