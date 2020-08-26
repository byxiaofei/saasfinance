package com.sinosoft.httpclient.service;

import com.sinosoft.httpclient.domain.OptionChange;

import java.util.List;

public interface OptionChangeService {
    /**
     * 保存所有选装件变动的相关返回数据
     * @param ListOptionChange
     * @return
     */
    String saveoptionChangeListTest(List<OptionChange> ListOptionChange);

    String saveoptionChangeList(List<OptionChange> ListOptionChange,String loadTime);

}
