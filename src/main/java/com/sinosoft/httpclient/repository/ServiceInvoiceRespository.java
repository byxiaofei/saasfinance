package com.sinosoft.httpclient.repository;

import com.sinosoft.httpclient.domain.ServiceInvoice;
import com.sinosoft.repository.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceInvoiceRespository extends BaseRepository<ServiceInvoice,Integer> {
}
