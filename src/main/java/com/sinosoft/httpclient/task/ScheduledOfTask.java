package com.sinosoft.httpclient.task;

import com.sinosoft.httpclient.config.SpringUtils;
import com.sinosoft.httpclient.config.StatusEnum;
import com.sinosoft.httpclient.domain.SpringScheduledCron;
import com.sinosoft.httpclient.repository.SpringScheduledCronRepository;

/**
 * @author yudong
 * @date 2019/5/11
 */
public interface ScheduledOfTask extends Runnable {

    /**
     * 定时任务方法
     */
    void execute();

    /**
     * 实现控制定时任务启用或禁用的功能
     */
    @Override
    default void run() {
        SpringScheduledCronRepository repository = SpringUtils.getBean(SpringScheduledCronRepository.class);
        SpringScheduledCron scheduledCron = repository.findByCronKey(this.getClass().getName());
        if (StatusEnum.DISABLED.getCode().equals(scheduledCron.getStatus())) {
            // 任务是禁用状态
            return;
        }
        execute();
    }
}
