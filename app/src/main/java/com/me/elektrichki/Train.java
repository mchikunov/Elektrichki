package com.me.elektrichki;

import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "train")
public class Train {

    @PrimaryKey(autoGenerate = true)
    public int trainId;



    String fromDest;
    String toDest;
    String codeFrom;
    String codeTo;

    String getStartTime;
    String getTitle;
    String getNumber;
    String getStops;
    String getDuration;
    String getEndTime;





    public Train(String fromDest, String toDest, String codeFrom, String codeTo, String getStartTime, String getTitle, String getNumber, String getStops, String getDuration, String getEndTime) {
        this.fromDest = fromDest;
        this.toDest = toDest;
        this.codeFrom = codeFrom;
        this.codeTo = codeTo;
        this.getStartTime = getStartTime;
        this.getTitle = getTitle;
        this.getNumber = getNumber;
        this.getStops = getStops;
        this.getDuration = getDuration;
        this.getEndTime = getEndTime;



    }


    public String getGetStartTime() {
        return getStartTime;
    }

    public void setGetStartTime(String getStartTime) {
        this.getStartTime = getStartTime;
    }

    public String getGetTitle() {
        return getTitle;
    }

    public void setGetTitle(String getTitle) {
        this.getTitle = getTitle;
    }

    public String getGetNumber() {
        return getNumber;
    }

    public void setGetNumber(String getNumber) {
        this.getNumber = getNumber;
    }

    public String getGetStops() {
        return getStops;
    }

    public void setGetStops(String getStops) {
        this.getStops = getStops;
    }

    public String getGetDuration() {
        return getDuration;
    }

    public void setGetDuration(String getDuration) {
        this.getDuration = getDuration;
    }

    public String getGetEndTime() {
        return getEndTime;
    }

    public void setGetEndTime(String getEndTime) {
        this.getEndTime = getEndTime;
    }

    public String getFromDest() {
        return fromDest;
    }

    public void setFromDest(String from) {
        this.fromDest = fromDest;
    }

    public String getToDest() {
        return toDest;
    }

    public void setToDest(String toDest) {
        this.toDest = toDest;
    }

    @Override
    public String toString() {
        return "Train{" +
                "from='" + fromDest + '\'' +
                ", to='" + toDest + '\'' +
                ", getStartTime='" + getStartTime + '\'' +
                ", getTitle='" + getTitle + '\'' +
                ", getNumber='" + getNumber + '\'' +
                ", getStops='" + getStops + '\'' +
                ", getDuration='" + getDuration + '\'' +
                ", getEndTime='" + getEndTime + '\'' +
                '}';
    }
}
