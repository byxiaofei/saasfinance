package com.sinosoft.repository;

import com.sinosoft.domain.AccSegmentDefine;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @Auther: luodejun
 * @Date: 2020/4/24 15:47
 * @Description:
 */
public interface AccSegmentDefineRespository extends BaseRepository<AccSegmentDefine, String> {

    List<AccSegmentDefine> findByTemp(String temp);

    //  查询专项对应列
    @Query(value = "select * from accsegmentdefine where segment_col =  ?1 ",nativeQuery = true)
    List<?> queryAccSegmentDefine(String segmentCol);
}
