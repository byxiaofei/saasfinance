package com.sinosoft.httpclient.repository;

import com.sinosoft.httpclient.domain.PartsInvoice;
import com.sinosoft.repository.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartsInvoiceRespository extends BaseRepository<PartsInvoice,Long> {
}
