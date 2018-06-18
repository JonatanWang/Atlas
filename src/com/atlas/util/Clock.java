package com.atlas.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;

/**
 *
 * @author Wang Zheng-Yu <zhengyuw@kth.se>
 */
public class Clock extends java.util.Timer{
    
    /**
     * Get the current time when a collection of data is loaded
     * @return A string of current time
     */
    public String currentTime()  {
        
        LocalDateTime now = LocalDateTime.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        int day = now.getDayOfMonth();
        int hour = now.getHour();
        int minute = now.getMinute();
        int second = now.getSecond();
        int millis = now.get(ChronoField.MILLI_OF_SECOND);
        return "Realtime Line Chart @"
                + year + "-" + month + "-" + day + " "
                + hour + ":" + minute + ":" + second;
    }
}
