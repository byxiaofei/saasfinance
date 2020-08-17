package com.sinosoft.httpclient.service.impl;

import com.sinosoft.httpclient.domain.OptionChange;
import com.sinosoft.httpclient.repository.OptionChangeRespository;
import com.sinosoft.httpclient.service.OptionChangeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class OptionChangeServiceImpI implements OptionChangeService {

    @Resource
    private OptionChangeRespository optionChangeRespository;

    @Override
    public String saveoptionChangeList(List<OptionChange> optionChangeList) {
        optionChangeRespository.saveAll(optionChangeList);
        optionChangeRespository.flush();
        return "success";
    }
}
