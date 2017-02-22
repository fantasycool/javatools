package com.frio.tools.datetime;

import java.util.Calendar;
import java.util.Date;

public class DateTimeRange {
    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;

    public DateTimeRange(int startHour, int startMinute, int endHour, int endMinute) {
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
    }

    public boolean validateInRange(Date entryTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(entryTime);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        long start = startHour * 60 + startMinute;
        long end = endHour * 60 + endMinute;
        long current = hour * 60 + minute;
        if (current >= start && current <= end) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        StringBuilder sb = new StringBuilder();
        sb.append(startHour).append(startMinute).append(endHour).append(endMinute);
        return Integer.valueOf(sb.toString());
    }
}