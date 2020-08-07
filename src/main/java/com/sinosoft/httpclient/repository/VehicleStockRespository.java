package com.sinosoft.httpclient.repository;

import com.sinosoft.httpclient.dao.VehicleStockDTO;
import com.sinosoft.repository.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleStockRespository extends BaseRepository<VehicleStockDTO,Integer>{

}
