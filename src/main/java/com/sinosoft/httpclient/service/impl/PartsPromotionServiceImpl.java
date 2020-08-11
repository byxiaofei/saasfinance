package com.sinosoft.httpclient.service.impl;

import com.sinosoft.httpclient.domain.JsonToPartsPromotion;
import com.sinosoft.httpclient.domain.PartsPromotion;
import com.sinosoft.httpclient.domain.PromotionParts;
import com.sinosoft.httpclient.dto.PartsPromotionDTO;
import com.sinosoft.httpclient.repository.PartsPromotionRespository;
import com.sinosoft.httpclient.service.PartsPromotionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class PartsPromotionServiceImpl implements PartsPromotionService {

    @Resource
    private PartsPromotionRespository partsPromotionRespository;


    @Override
    public String savePartsPromotionList(List<JsonToPartsPromotion> jsonToPartsPromotionList) {
        List<PartsPromotion> partsPromotionList = new ArrayList<>();
        for(int i = 0 ;i <jsonToPartsPromotionList.size(); i++){
            JsonToPartsPromotion jsonToPartsPromotion = jsonToPartsPromotionList.get(i);
            PartsPromotion partsPromotion = new PartsPromotion();
            partsPromotion.setCompanyNo(jsonToPartsPromotion.getCompanyNo());
            partsPromotion.setDealerNo(jsonToPartsPromotion.getDealerNo());
            partsPromotion.setTransactionType(jsonToPartsPromotion.getTransactionType());
            partsPromotion.setPromotionDate(jsonToPartsPromotion.getPromotionDate());
            partsPromotion.setOperationDate(jsonToPartsPromotion.getOperationDate());
            for(int j = 0 ;j <jsonToPartsPromotion.getPromotionParts().size(); j++){
                PromotionParts promotionParts = jsonToPartsPromotion.getPromotionParts().get(j);

                partsPromotion.setPartsNo(promotionParts.getPartsNo());
                partsPromotion.setFlag(promotionParts.getFlag());
                partsPromotion.setGenuineFlag(promotionParts.getGenuineFlag());
                partsPromotion.setDescription(promotionParts.getDescription());
                partsPromotion.setPartsAnalysisCode(promotionParts.getPartsAnalysisCode());
                partsPromotion.setQuantity(promotionParts.getQuantity());
                partsPromotion.setPartsUnitCost(promotionParts.getPartsUnitCost());

            }
            partsPromotionList.add(partsPromotion);
        }

        partsPromotionRespository.saveAll(partsPromotionList);
        partsPromotionRespository.flush();

        //  保存日志


        return "success";
    }
}
