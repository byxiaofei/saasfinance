package com.sinosoft.httpclient.service.impl;

import com.sinosoft.httpclient.domain.JsonToPartsRequisition;
import com.sinosoft.httpclient.domain.PartsRequisition;
import com.sinosoft.httpclient.domain.RequisitionParts;
import com.sinosoft.httpclient.repository.PartsRequisitionRespository;
import com.sinosoft.httpclient.service.PartsRequisitionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class PartsRequisitionServiceImpl implements PartsRequisitionService {


    @Resource
    private PartsRequisitionRespository partsRequisitionRespository;


    /**
     * 保存
     * @param jsonToPartsRequisitionList
     * @return
     */
    @Override
    public String savePartsRequisitionList(List<JsonToPartsRequisition> jsonToPartsRequisitionList) {
        List<PartsRequisition> partsRequisitions = new ArrayList<>();
        for(int i= 0; i<jsonToPartsRequisitionList.size(); i++){
            JsonToPartsRequisition jsonToPartsRequisition = jsonToPartsRequisitionList.get(i);
            PartsRequisition partsRequisition = new PartsRequisition();
            partsRequisition.setDealerNo(jsonToPartsRequisition.getDealerNo());
            partsRequisition.setCompanyNo(jsonToPartsRequisition.getCompanyNo());
            partsRequisition.setCustomerName(jsonToPartsRequisition.getCustomerName());
            partsRequisition.setCompanyName(jsonToPartsRequisition.getCompanyName());
            partsRequisition.setCustomerId(jsonToPartsRequisition.getCustomerId());
            partsRequisition.setCompanyId(jsonToPartsRequisition.getCompanyId());
            partsRequisition.setFin(jsonToPartsRequisition.getFin());
            partsRequisition.setVin(jsonToPartsRequisition.getVin());
            partsRequisition.setRegistrationNo(jsonToPartsRequisition.getRegistrationNo());
            partsRequisition.setDocType(jsonToPartsRequisition.getDocType());
            partsRequisition.setDocNo(jsonToPartsRequisition.getDocNo());
            partsRequisition.setDocDate(jsonToPartsRequisition.getDocDate());
            partsRequisition.setOperationDate(jsonToPartsRequisition.getOperationDate());
            partsRequisition.setOrderNo(jsonToPartsRequisition.getOrderNo());
            for(int j = 0 ; j < jsonToPartsRequisition.getRequisitionParts().size(); j++){
                RequisitionParts requisitionParts = jsonToPartsRequisition.getRequisitionParts().get(j);
                partsRequisition.setLine(requisitionParts.getLine());
                partsRequisition.setPartsNo(requisitionParts.getPartsNo());
                partsRequisition.setDescription(requisitionParts.getDescription());
                partsRequisition.setGenuineFlag(requisitionParts.getGenuineFlag());
                partsRequisition.setPartsAnalysisCode(requisitionParts.getPartsAnalysisCode());
                partsRequisition.setQuantity(requisitionParts.getQuantity());
                partsRequisition.setPartsUnitCost(requisitionParts.getPartsUnitCost());
                partsRequisition.setIctCompany(requisitionParts.getIctCompany());
                partsRequisition.setIctOrder(requisitionParts.getIctOrder());
            }
            partsRequisitions.add(partsRequisition);
        }

        partsRequisitionRespository.saveAll(partsRequisitions);
        partsRequisitionRespository.flush();

        //  保存日志

        return "success";
    }
}
