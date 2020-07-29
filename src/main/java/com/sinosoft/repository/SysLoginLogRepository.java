package com.sinosoft.repository;

import com.sinosoft.domain.SysLoginLog;

import java.util.List;

public interface SysLoginLogRepository extends BaseRepository<SysLoginLog, Integer>  {

    List<?> findBySessionId(String sessionId);
}
