package com.sinosoft.common;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.SimpleFormatter;

/**
 * @Auther: hanchuanxing
 * @Date: 2019/1/17 19:50
 * @Description:
 */
public final class CurrentTime {

    private String currentTime;

    public static String getCurrentTime(){
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sf.format(new Date());
    }
    public static String getCurrentYear(){
        SimpleDateFormat sf = new SimpleDateFormat("yyyy");
        return sf.format(new Date());
    }
    public static String getCurrentDate(){
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        return sf.format(new Date());
    }
}
