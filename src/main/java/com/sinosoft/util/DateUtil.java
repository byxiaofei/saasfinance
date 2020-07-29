package com.sinosoft.util;

import sun.util.resources.cldr.es.CalendarData_es_AR;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;

/**
 * @Auther: luodejun
 * @Date: 2020/5/29 15:47
 * @Description:
 */
public class DateUtil {

    public static String getDateTimeFormat(String date){
        date=date.trim();
        String a1 = "[0-9]{4}[0-9]{2}[0-9]{2}[0-9]{2}[0-9]{2}[0-9]{2}";//yyyyMMddHHmmss
        String a2 = "[0-9]{4}[0-9]{2}[0-9]{2}";//yyyyMMdd
        String a3 = "[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}";//yyyy-MM-dd HH:mm:ss
        String a4 = "[0-9]{4}-[0-9]{2}-[0-9]{2}";//yyyy-MM-dd
        String a5= "[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}";//yyyy-MM-dd  HH:mm
        boolean datea1 = Pattern.compile(a1).matcher(date).matches();
        if(datea1){
            return "yyyyMMddHHmmss";
        }
        boolean datea2 = Pattern.compile(a2).matcher(date).matches();
        if(datea2){
            return "yyyyMMdd";
        }
        boolean datea3 = Pattern.compile(a3).matcher(date).matches();
        if(datea3){
            return "yyyy-MM-dd HH:mm:ss";
        }
        boolean datea4 = Pattern.compile(a4).matcher(date).matches();
        if(datea4){
            return "yyyy-MM-dd";
        }
        boolean datea5 = Pattern.compile(a5).matcher(date).matches();
        if(datea5){
            return "yyyy-MM-dd HH:mm";
        }
        return "";
    }


    public static String getDateTimeFormatToGeneralLedger(String date){
        date=date.trim();
        String a1 = "[0-9]{4}[0-9]{2}";//yyyyMM
        String a4 = "[0-9]{4}-[0-9]{2}-[0-9]{2}";//yyyy-MM-dd
        boolean datea1 = Pattern.compile(a1).matcher(date).matches();
        if(datea1){
            return "success";
        }
        boolean datea4 = Pattern.compile(a4).matcher(date).matches();
        if(datea4){
            return "success";
        }
        return "";
    }

    /**
     *
     * 功能描述:    某年某月的最后一天
     *
     */
    public static String getLastDayOfMonth(int year,int month){
        Calendar cal = Calendar.getInstance();
        // 设置年份
        cal.set(Calendar.YEAR,year);
        //  设置月份
        cal.set(Calendar.MONTH,month-1);
        //  获取某月最小的一天
        int LastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        //  设置日历年份中月份的最小天数
        cal.set(Calendar.DAY_OF_MONTH,LastDay);
        // 格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String LastDayOfMonth = sdf.format(cal.getTime());
        return LastDayOfMonth;
    }

    /**
     *
     * 功能描述:    某年某月的第一天
     *
     */
    public static String getFirstDayOfMonth(int year, int month){
        Calendar cal = Calendar.getInstance();
        // 设置年份
        cal.set(Calendar.YEAR,year);
        //  设置月份
        cal.set(Calendar.MONTH,month-1);
        //  获取某月最小的一天
        int firstDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
        //  设置日历年份中月份的最小天数
        cal.set(Calendar.DAY_OF_MONTH,firstDay);
        // 格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String firstDayOfMonth = sdf.format(cal.getTime());
        return firstDayOfMonth;
    }

    public static void main(String[] args) {
        String firstDayOfMonth = getFirstDayOfMonth(2020, 2);
        System.out.println(firstDayOfMonth);
        String lastDayOfMonth = getLastDayOfMonth(2019, 2);
        System.out.println(lastDayOfMonth);
    }
}
