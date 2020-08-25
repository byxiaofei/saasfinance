package com.sinosoft.httpclient.service;

import com.sinosoft.httpclient.domain.Tasksdetailsinfo;

import java.util.List;

public interface TasksdetailsService {

    /**
     * 保存接口信息
     * @param tasksdetailsinfo
     * @return
     */
    public String saveTasksdetails(Tasksdetailsinfo tasksdetailsinfo);


    /**
     * 查询接口信息 （limit 1）
     */
    public Tasksdetailsinfo findTasksdetails(Tasksdetailsinfo Tasksdetailsinfo);
}
