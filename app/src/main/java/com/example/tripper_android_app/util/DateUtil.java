package com.example.tripper_android_app.util;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    /*
     *
     *
     *
     */
    private static final String TAG = "TAG_DateUtil";

    public static String date4day(String dateStr, int number) throws ParseException {
        String transDate = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = null;
        dateStr = dateStr.replace("-", "");
        dateStr = dateStr.replace("/", "");
        date = dateFormat.parse(dateStr);
        System.out.println("format::"+date.toString());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR,number);
        date = calendar.getTime();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        transDate = dateFormat.format(date);
        return transDate;
    }

}
