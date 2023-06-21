package com.example.smiletogether_dentalapp.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class WorkDay implements Serializable{
    private String day;
    private String startTime;
    private String endTime;

    public WorkDay() {
    }

    public WorkDay(String day, String startTime, String endTime) {
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    protected WorkDay(Parcel in) {
        day = in.readString();
        startTime = in.readString();
        endTime = in.readString();
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return day + ": " + startTime + " - " + endTime;
    }


}



