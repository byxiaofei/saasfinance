package com.sinosoft.httpclient.repository;

import com.sinosoft.httpclient.domain.ConfigureManage;
import com.sinosoft.httpclient.domain.Tasksdetailsinfo;
import com.sinosoft.repository.BaseRepository;
import org.quartz.SimpleTrigger;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface TasksdetailsinfoRespository extends BaseRepository<Tasksdetailsinfo,Integer> {

    @Query(value = "SELECT  *  FROM taskschedulingdetailsinfo WHERE  batch = ?   ORDER BY end_time LIMIT 1",nativeQuery = true)
    public  List<Tasksdetailsinfo> findLimitOne(String batch);

/*
    @Modifying
    @Query(value = "insert into AccMonthTrace (acc_book_code, acc_book_type, center_code, year_month_date, acc_month_stat, create_by, create_time, temp) values(?1,?2,?3,?4,?5,?6,?7,?8)", nativeQuery = true)
    void addYearMonthDate(String AccBookCode, String AccBookType, String centerCode, String nextYMD, String flag, String createBy, String createTime, String temp);
*/


    @Transactional
    @Modifying
    @Query(value = "INSERT INTO taskschedulingdetailsinfo (url,start_time,end_time,flag,batch) VALUES(?,?,?,?,?)",nativeQuery = true)
    public void saveTasksdetails(String url,String startTime,String endTime,String flag,String batch);

}
