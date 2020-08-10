package com.sinosoft.httpclient.repository;

import com.sinosoft.httpclient.domain.VehicleInvoice;
import com.sinosoft.repository.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleInvoiceRespository extends BaseRepository<VehicleInvoice,Integer> {
}
