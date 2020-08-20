package com.sinosoft.httpclient.service;

import com.sinosoft.httpclient.domain.JsonToWarrantyConfirm;

import java.util.List;

public interface WarrantyConfirmService {
    /**
     * 获取上次调用时间到当前时间，所有索赔确认的数据
     * @param jsonToWarrantyConfirmList
     * @return
     */
    String saveWarrantyConfirmList(List<JsonToWarrantyConfirm> jsonToWarrantyConfirmList);

}
