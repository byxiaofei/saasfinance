package com.sinosoft.service.impl;

import com.sinosoft.domain.InterfaceInfo;
import com.sinosoft.repository.InterfaceInfoRespository;
import com.sinosoft.service.InterfaceInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;

@Service
public class InterfaceInfoServiceImpl implements InterfaceInfoService {

    @Resource
    private InterfaceInfoRespository interfaceInfoRespository;

    @Override
    public void successSave(String branchinfo, String loadTime, String errorMsg) {
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setBranchCode(branchinfo);
        interfaceInfo.setResultInfo("success");
        interfaceInfo.setLoadTime(loadTime);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formatTime = sdf.format(System.currentTimeMillis());
        interfaceInfo.setPolicDate(formatTime);
        interfaceInfo.setFileName(errorMsg);
        interfaceInfoRespository.save(interfaceInfo);
        interfaceInfoRespository.flush();
    }

    @Override
    public void failSave(String branchinfo, String loadTime, String errorMsg) {
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setBranchCode(branchinfo);
        interfaceInfo.setResultInfo("fail");
        interfaceInfo.setLoadTime(loadTime);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formatTime = sdf.format(System.currentTimeMillis());
        interfaceInfo.setPolicDate(formatTime);
        interfaceInfo.setFileName(errorMsg);
        interfaceInfoRespository.save(interfaceInfo);
        interfaceInfoRespository.flush();
    }
}
