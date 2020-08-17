package com.sinosoft.httpclient.service.impl;

import com.alibaba.druid.sql.visitor.functions.If;
import com.sinosoft.httpclient.domain.PartsVerification;
import com.sinosoft.httpclient.dto.DnVerifyDTO;
import com.sinosoft.httpclient.dto.InvoiceVerifyDTO;
import com.sinosoft.httpclient.dto.PartsVerificationDTO;
import com.sinosoft.httpclient.repository.PartsVerificationRespository;
import com.sinosoft.httpclient.service.PartsVerificationService;
import com.sinosoft.httpclient.utils.DtoToEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class PartsVerificationServiceImpl implements PartsVerificationService {

    @Resource
    PartsVerificationRespository respository;

    @Override
    public String getPartsVerification(List<PartsVerificationDTO> partsVerificationList) {

        //获取时间区间所有的发票信息
        List<PartsVerification> partsVerificationEntityList=new ArrayList<>();

        for (PartsVerificationDTO partsVerificationDTO:partsVerificationList) {
            partsVerificationDTO.setBatch(partsVerificationDTO.getId());
            List<InvoiceVerifyDTO> invoiceVerifyDTOList = partsVerificationDTO.getVerifyInvoices();
            List<DnVerifyDTO> DnVerifyDTOList = partsVerificationDTO.getVerifyDns();
            if(!invoiceVerifyDTOList.isEmpty()){
                for (InvoiceVerifyDTO invoiceVerify : invoiceVerifyDTOList) {
                    PartsVerification InvoiceVerify=new PartsVerification();
                    InvoiceVerify  = (PartsVerification) DtoToEntity.populate(partsVerificationDTO, InvoiceVerify);
                    InvoiceVerify = (PartsVerification) DtoToEntity.populate(invoiceVerify, InvoiceVerify);
                    partsVerificationEntityList.add(InvoiceVerify);
                }
            }
            if(!DnVerifyDTOList.isEmpty()){
                for (DnVerifyDTO dnVerifyDTO : DnVerifyDTOList) {
                    PartsVerification DnVerify=new PartsVerification();
                    DnVerify  = (PartsVerification) DtoToEntity.populate(partsVerificationDTO, DnVerify);
                    DnVerify = (PartsVerification) DtoToEntity.populate(dnVerifyDTO, DnVerify);
                    partsVerificationEntityList.add(DnVerify);
                }
            }
        }
        respository.saveAll(partsVerificationEntityList);
        respository.flush();

       return "success";
    }

    @Override
    public void savePartsVerification() {

    }
}
