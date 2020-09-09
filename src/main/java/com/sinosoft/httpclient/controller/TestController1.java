package com.sinosoft.httpclient.controller;

import freemarker.template.SimpleDate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestController1{

    public static void main(String[] args) {

        try {
            String yearMonth = "202011";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
            Date parse = sdf.parse(yearMonth);
            long time = parse.getTime();
            System.out.println(time);

            System.out.println("---------------     当前是时间的转换      -------------------");
            SimpleDateFormat sdfLast = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long time1 = 1596001003220L;
            long time2 = 1598423581399L;
            long time4 = 1597695003220L;
            Date date = new Date(time1);
            String format = sdfLast.format(date);
            System.out.println("time1的时间位:"+format);
            Date date1 = new Date(time2);
            String format1 = sdfLast.format(date1);
            System.out.println("time2的时间为："+format1);
            Date date2 = new Date(time4);
            String format2 = sdfLast.format(date2);
            System.out.println("time4的时间为："+format2);

            System.out.println("----------------到此划开分割---------------------");
            String firstTime = "2020-09-03 00:00:00";
            Date parse1 = sdfLast.parse(firstTime);
            System.out.println("2020-09-03 开始日期"+parse1.getTime());
            System.out.println("当前日期为:"+new Date().getTime());

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}