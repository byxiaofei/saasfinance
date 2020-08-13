package com.sinosoft.httpclient.service.impl;

import com.sinosoft.httpclient.domain.JsonToPartsInvoice;
import com.sinosoft.httpclient.domain.PartsInvoice;
import com.sinosoft.httpclient.domain.PartsInvoiceIn;
import com.sinosoft.httpclient.repository.PartsInvoiceRespository;
import com.sinosoft.httpclient.service.PartsInvoiceService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
@Service
public class PartsInvoiceServiceImpl implements PartsInvoiceService
{
    @Resource
    private PartsInvoiceRespository partsInvoiceRespository;
    @Override
    public String savePartsInvoiceList(List<JsonToPartsInvoice> jsonToPartsInvoicesList) {
        List<PartsInvoice> partsInvoices = new ArrayList<>();
        for(int i = 0;i < jsonToPartsInvoicesList.size();i++){
            JsonToPartsInvoice temp = jsonToPartsInvoicesList.get(i);
            PartsInvoice partsInvoice = new PartsInvoice();
            partsInvoice.setCompanyNo(temp.getCompanyNo());
            partsInvoice.setDealerNo(temp.getDealerNo());
            partsInvoice.setDocType(temp.getDocType());
            partsInvoice.setDocNo(temp.getDocNo());
            partsInvoice.setBizType(temp.getBizType());
            partsInvoice.setDocDate(temp.getDocDate());
            partsInvoice.setCustomerName(temp.getCustomerName());
            partsInvoice.setCompanyName(temp.getCompanyName());
            partsInvoice.setFranchise(temp.getFranchise());
            partsInvoice.setOrderNo(temp.getOrderNo());
            partsInvoice.setOperationDate(temp.getOperationDate());
            for(int j = 0;j < temp.getInvoiceParts().size();j++){
                PartsInvoiceIn temp1= temp.getInvoiceParts().get(j);
                partsInvoice.setLine(temp1.getLine());
                partsInvoice.setPartsNo(temp1.getPartsNo());
                partsInvoice.setPartsAnalysisCode(temp1.getPartsAnalysisCode());
                partsInvoice.setDepartmentCode(temp1.getDepartmentCode());
                partsInvoice.setQuantity(temp1.getQuantity());
                partsInvoice.setPartsUnitCost(temp1.getPartsUnitCost());
                partsInvoice.setUnitSellingPrice(temp1.getUnitSellingPrice());
                partsInvoice.setTotalPrice(temp1.getTotalPrice());
                partsInvoice.setDiscountRate(temp1.getDiscountRate());
                partsInvoice.setDiscountAmount(temp1.getDiscountAmount());
                partsInvoice.setContribution(temp1.getContribution());
                partsInvoice.setNetValue(temp1.getNetValue());
                partsInvoice.setVatRate(temp1.getVatRate());
                partsInvoice.setVatAmount(temp1.getVatAmount());
                partsInvoice.setCustomerTypeNo(temp1.getCustomerTypeNo());
            }
            partsInvoices.add(partsInvoice);
        }
        partsInvoiceRespository.saveAll(partsInvoices);
        partsInvoiceRespository.flush();
        return "success";
    }
}
