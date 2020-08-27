package com.sinosoft.httpclient.repository;

import com.sinosoft.httpclient.domain.OptionChange;
import com.sinosoft.repository.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OptionChangeRespository extends BaseRepository<OptionChange,Integer> {
}
