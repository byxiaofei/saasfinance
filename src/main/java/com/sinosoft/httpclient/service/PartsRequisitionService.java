package com.sinosoft.httpclient.service;

import com.sinosoft.httpclient.domain.JsonToPartsRequisition;

import java.util.List;

public interface PartsRequisitionService {

    String savePartsRequisitionList(List<JsonToPartsRequisition> jsonToPartsRequisitionList);
}
