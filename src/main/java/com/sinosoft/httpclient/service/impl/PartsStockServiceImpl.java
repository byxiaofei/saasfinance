package com.sinosoft.httpclient.service.impl;

import com.sinosoft.httpclient.domain.JsonToPartsStock;
import com.sinosoft.httpclient.domain.PartsStock;
import com.sinosoft.httpclient.domain.PartsStockIn;
import com.sinosoft.httpclient.repository.PartsStockRespository;
import com.sinosoft.httpclient.service.PartsStockService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class PartsStockServiceImpl implements PartsStockService {
    @Resource
    private PartsStockRespository partsStockRespository;
    /**
     * 保存 获取上次调用时间到当前时间，所有配件库存变动相关的数据，包括入库、退库、库存盘点
     * @param jsonTopartsStockList
     * @return
     */
    @Override
    public String savePartsStockListList(List<JsonToPartsStock> jsonTopartsStockList) {
        List<PartsStock> partsStocks = new ArrayList<>();
        for(int i=0;i<jsonTopartsStockList.size();i++){
            JsonToPartsStock temp =jsonTopartsStockList.get(i);
            PartsStock partsStock = new PartsStock();
            //转成数据库对象
            partsStock.setCompanyNo(temp.getCompanyNo());
            partsStock.setDealerNo(temp.getDealerNo());
            partsStock.setOperationDate(temp.getOperationDate());
            partsStock.setTransactionType(temp.getTransactionType());
            for(int j=0;j<temp.getStockInParts().size();j++){
                PartsStockIn temp1 =temp.getStockInParts().get(j);

                partsStock.setBusinessDate(temp1.getBusinessDate());
                partsStock.setDescription(temp1.getDescription());
                partsStock.setGenuineFlag(temp1.getGenuineFlag());
                partsStock.setPartsAnalysisCode(temp1.getPartsAnalysisCode());
                partsStock.setPartsNo(temp1.getPartsNo());
                partsStock.setPartsUnitCost(temp1.getPartsUnitCost());
                partsStock.setPoNo(temp1.getPoNo());
                partsStock.setQuantity(temp1.getQuantity());
                partsStock.setSupplierDescription(temp1.getSupplierDescription());
                partsStock.setSupplierNo(temp1.getSupplierNo());

            }

            partsStocks.add(partsStock);
        }


        partsStockRespository.saveAll(partsStocks);
        partsStockRespository.flush();
        //保存日志

        return "success";
    }

}
