package com.sinosoft.httpclient.service.impl;

import com.sinosoft.httpclient.domain.Tasksdetailsinfo;
import com.sinosoft.httpclient.repository.TasksdetailsinfoRespository;
import com.sinosoft.httpclient.service.TasksdetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class TasksdetailsServiceImpl implements TasksdetailsService {

    @Autowired
    TasksdetailsinfoRespository tasksdetailsinfoRespository;


    /**
     * 保存接口信息
     * @param tasksdetailsinfo
     * @return
     */
    public String saveTasksdetails(Tasksdetailsinfo tasksdetailsinfo){
        tasksdetailsinfoRespository.saveTasksdetails(tasksdetailsinfo.getUrl(),tasksdetailsinfo.getStartTime(),tasksdetailsinfo.getEndTime(),tasksdetailsinfo.getFlag(),tasksdetailsinfo.getBatch());
        return  "success";
    }


    /**
     * 查询接口信息 （limit 1）
     */
    public Tasksdetailsinfo findTasksdetails(Tasksdetailsinfo tasksdetailsinfo){
        List<Tasksdetailsinfo> list  = tasksdetailsinfoRespository.findLimitOne(tasksdetailsinfo.getBatch());
        return  (Tasksdetailsinfo)list.get(0);
    }
}


