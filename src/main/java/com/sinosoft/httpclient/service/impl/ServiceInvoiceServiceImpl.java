package com.sinosoft.httpclient.service.impl;

import com.sinosoft.httpclient.domain.PartsVerification;
import com.sinosoft.httpclient.domain.ServiceInvoice;
import com.sinosoft.httpclient.dto.*;
import com.sinosoft.httpclient.repository.ServiceInvoiceRespository;
import com.sinosoft.httpclient.service.ServiceInvoiceService;
import com.sinosoft.httpclient.utils.DtoToEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class ServiceInvoiceServiceImpl implements ServiceInvoiceService {

    @Resource
    ServiceInvoiceRespository respository;

    @Override
    public String getServiceInvoiceService(List<ServiceInvoiceDTO> ServiceInvoiceDTOList) {

      List<ServiceInvoice>serviceInvoiceList=new ArrayList<>();
      for (ServiceInvoiceDTO serviceInvoiceDTO:ServiceInvoiceDTOList){
          serviceInvoiceDTO.setBatch(serviceInvoiceDTO.getId());
          List<ServicePartsInvoiceDTO> invoiceVerifyDTOList = serviceInvoiceDTO.getInvoiceServiceParts();
          List<ServiceLaborInvoiceDTO> DnVerifyDTOList = serviceInvoiceDTO.getInvoiceServiceLabors();
          if (!invoiceVerifyDTOList.isEmpty()){
              for (ServicePartsInvoiceDTO servicePartsInvoiceDTO:invoiceVerifyDTOList){
                  ServiceInvoice ServicePartsInvoice=new ServiceInvoice();
                  ServicePartsInvoice  = (ServiceInvoice) DtoToEntity.populate(serviceInvoiceDTO, ServicePartsInvoice);
                  ServicePartsInvoice  = (ServiceInvoice) DtoToEntity.populate(servicePartsInvoiceDTO, ServicePartsInvoice);
                  serviceInvoiceList.add(ServicePartsInvoice);
              }

          }
          if (!DnVerifyDTOList.isEmpty()){
              for (ServiceLaborInvoiceDTO serviceLaborInvoiceDTO:DnVerifyDTOList){
                  ServiceInvoice ServiceLaborInvoice=new ServiceInvoice();
                  ServiceLaborInvoice  = (ServiceInvoice) DtoToEntity.populate(serviceInvoiceDTO, ServiceLaborInvoice);
                  ServiceLaborInvoice  = (ServiceInvoice) DtoToEntity.populate(serviceLaborInvoiceDTO, ServiceLaborInvoice);
                  serviceInvoiceList.add(ServiceLaborInvoice);
              }
          }

      }

        respository.saveAll(serviceInvoiceList);
        respository.flush();

        return "success";
    }


}
