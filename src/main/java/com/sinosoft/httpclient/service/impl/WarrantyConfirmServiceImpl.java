package com.sinosoft.httpclient.service.impl;

import com.sinosoft.httpclient.domain.JsonToWarrantyConfirm;
import com.sinosoft.httpclient.domain.WarrantyConfirm;
import com.sinosoft.httpclient.domain.WarrantyCreditNote;
import com.sinosoft.httpclient.repository.WarrantyConfirmRespository;
import com.sinosoft.httpclient.service.WarrantyConfirmService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
@Service
public class WarrantyConfirmServiceImpl implements WarrantyConfirmService {
    @Resource
    WarrantyConfirmRespository warrantyConfirmRespository;

    @Override
    public String saveWarrantyConfirmList(List<JsonToWarrantyConfirm> jsonToWarrantyConfirmList) {
        WarrantyConfirm warrantyConfirm = new WarrantyConfirm();
        List<WarrantyConfirm> warrantyConfirms = new ArrayList<>();
        for(int i = 0 ;i < jsonToWarrantyConfirmList.size();i++){
            JsonToWarrantyConfirm temp = jsonToWarrantyConfirmList.get(i);
            warrantyConfirm.setDealerNo(temp.getDealerNo());
            warrantyConfirm.setCompanyNo(temp.getCompanyNo());
            warrantyConfirm.setClaimNoOfOtr(temp.getClaimNoOfOtr());
            warrantyConfirm.setClaimId(temp.getClaimId());
            warrantyConfirm.setInvoiceNo(temp.getInvoiceNo());
            warrantyConfirm.setInvoiceDate(temp.getInvoiceDate());
            warrantyConfirm.setConfirmDate(temp.getConfirmDate());
            warrantyConfirm.setCustomerTypeNo(temp.getCustomerTypeNo());
            warrantyConfirm.setCustomerTypeNoDescription(temp.getCustomerTypeNoDescription());
            warrantyConfirm.setRwoNo(temp.getRwoNo());
            warrantyConfirm.setCustomerName(temp.getCustomerName());
            warrantyConfirm.setCompanyName(temp.getCompanyName());
            warrantyConfirm.setRegistrationNo(temp.getRegistrationNo());
            warrantyConfirm.setFin(temp.getFin());
            warrantyConfirm.setInvoiceTotal(temp.getInvoiceTotal());
            warrantyConfirm.setPartsTotal(temp.getPartsTotal());
            warrantyConfirm.setLaborTotal(temp.getLaborTotal());
            warrantyConfirm.setSundriesTotal(temp.getSundriesTotal());
            warrantyConfirm.setHandlingFeeTotal(temp.getHandlingFeeTotal());
            for(int j = 0; j < temp.getWarrantyCreditNotes().size(); j++){
                WarrantyCreditNote temp1 = temp.getWarrantyCreditNotes().get(j);
                warrantyConfirm.setCreditNoteNo(temp1.getCreditNoteNo());
                warrantyConfirm.setCreditNoteDate(temp1.getCreditNoteDate());
                warrantyConfirm.setCompensateParts(temp1.getCompensateParts());
                warrantyConfirm.setCompensateLabor(temp1.getCompensateLabor());
                warrantyConfirm.setCompensateSundries(temp1.getCompensateSundries());
                warrantyConfirm.setCompensateHandlingFee(temp1.getCompensateHandlingFee());
            }
            warrantyConfirms.add(warrantyConfirm);
        }
        warrantyConfirmRespository.saveAll(warrantyConfirms);
        warrantyConfirmRespository.flush();
        return "success";
    }
}
