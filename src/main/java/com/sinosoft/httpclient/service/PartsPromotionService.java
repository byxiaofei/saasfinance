package com.sinosoft.httpclient.service;

import com.sinosoft.httpclient.domain.JsonToPartsPromotion;

import java.util.List;

public interface PartsPromotionService {

    String savePartsPromotionList(List<JsonToPartsPromotion> jsonToPartsPromotionList);
}
