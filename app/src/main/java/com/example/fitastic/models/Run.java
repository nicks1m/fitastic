package com.example.fitastic.models;

import android.graphics.Bitmap;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

public class Run {
    private Bitmap routeImg;
    private float distance;
    private double runDuration;
    private float speed;
    private Date time;
    private Timestamp timestamp;

    public Run(Bitmap routeImg, float distance, double time, float speed) {
        this.routeImg = routeImg;
        this.distance = distance;
        this.runDuration = time;
        this.speed = speed;
        this.time = generateTime();
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    private Date generateTime() {
        return Calendar.getInstance().getTime();
    }

    public Bitmap getRouteImg() {
        return routeImg;
    }

    public float getDistance() {
        return distance;
    }

    public double getRunDuration() {
        return runDuration;
    }

    public float getSpeed() {
        return speed;
    }

    public Date getTime() {
        return time;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}
