package com.sinosoft.httpclient.service;

import com.sinosoft.httpclient.domain.JsonToWarrantyConfirm;

import java.util.List;

public interface WarrantyConfirmService {
    /**
     * ��ȡ�ϴε���ʱ�䵽��ǰʱ�䣬��������ȷ�ϵ�����
     * @param jsonToWarrantyConfirmList
     * @return
     */
    String saveWarrantyConfirmList(List<JsonToWarrantyConfirm> jsonToWarrantyConfirmList);

}
