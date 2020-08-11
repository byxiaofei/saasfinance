package com.sinosoft.httpclient.repository;

import com.sinosoft.httpclient.domain.TaskSchedulingManage;
import com.sinosoft.repository.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskSchedulingManageRespository extends BaseRepository<TaskSchedulingManage,Integer> {
}
