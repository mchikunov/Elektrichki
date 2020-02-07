package com.me.elektrichki;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.List;


@Entity(tableName = "route")
public class Route {
    @PrimaryKey(autoGenerate = true)
   int id;



    private int routeId;

    private String fromDest;
   private String toDest;

    @TypeConverters({TypeConv.class})
   private List<String> routeTimes;

    @TypeConverters({TypeConv.class})
   private List<Integer> alarmTimes;



    public Route(String fromDest, String toDest, List<String> routeTimes, List<Integer> alarmTimes) {
        this.fromDest = fromDest;
        this.toDest = toDest;
        this.routeTimes = routeTimes;
        this.alarmTimes = alarmTimes;
    }

    @Ignore
    public Route(String fromDest, String toDest, List<String> routeTimes, int routeId) {
        this.fromDest = fromDest;
        this.toDest = toDest;
        this.routeTimes = routeTimes;
        this.routeId = routeId;
    }







    public String getFromDest() {
        return fromDest;
    }

    public void setFromDest(String fromDest) {
        this.fromDest = fromDest;
    }

    public String getToDest() {
        return toDest;
    }

    public void setToDest(String toDest) {
        this.toDest = toDest;
    }


    public List<String>  getRouteTimes() {
        return routeTimes;
    }

    public void setRouteTimes(List<String> routeTimes) {
        this.routeTimes = routeTimes;
    }

    public Integer getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }


    public List<Integer> getAlarmTimes() {
        return alarmTimes;
    }

    public void setAlarmTimes(List<Integer> alarmTimes) {
        this.alarmTimes = alarmTimes;
    }
}
