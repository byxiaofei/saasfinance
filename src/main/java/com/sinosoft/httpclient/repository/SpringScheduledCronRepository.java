package com.sinosoft.httpclient.repository;

import com.sinosoft.httpclient.domain.SpringScheduledCron;

import com.sinosoft.repository.BaseRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface SpringScheduledCronRepository extends JpaRepository<SpringScheduledCron, Integer> {
    SpringScheduledCron findByCronKey(String cronKey);

    /**
     * 更新定时任务cron表达式
     *
     * @param newCron
     * @param cronKey
     * @return
     */
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "update bz_spring_scheduled_cron set cron_expression=?1 where cron_key=?2", nativeQuery = true)
    int updateCronExpressionByCronKey(String newCron, String cronKey);

    /**
     * 更新定时任务状态
     *
     * @param status
     * @param cronKey
     * @return
     */
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "update bz_spring_scheduled_cron set status=?1 where cron_key=?2", nativeQuery = true)
    int updateStatusByCronKey(Integer status, String cronKey);

}
