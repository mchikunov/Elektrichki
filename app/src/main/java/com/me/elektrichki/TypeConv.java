package com.me.elektrichki;

import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TypeConv {
    static String my = "\\|";


    @TypeConverter
    public static String fromRouteTimes(List<String> routeTimes) {

       if (routeTimes==null) return null;

        StringBuilder sb = new StringBuilder();

        for (String route : routeTimes
                ) {
            sb.append(route).append("|");
        }
        return sb.toString();
    }


    @TypeConverter
    public static List<String> toRoute(String data) {
        if (data==null) return null;
        return Arrays.asList(data.split(my));
    }


    @TypeConverter
    public static String fromAlarmTimes(List<Integer> alarmTimes) {
    if (alarmTimes==null) return null;
        StringBuilder sb = new StringBuilder();

        for (Integer route : alarmTimes
                ) {
            sb.append(route).append("|");
        }
        return sb.toString();
    }


    @TypeConverter
    public static List<Integer> toAlarm(String data) {
        if (data==null) return null;

        List<String> in = new ArrayList<>(Arrays.asList(data.split(my)));
        List<Integer> out = new ArrayList<>();
        for (String s: in
             ) {
            if (!s.equals(""))
            out.add(Integer.parseInt(s));
        }
        return out;
    }


}
