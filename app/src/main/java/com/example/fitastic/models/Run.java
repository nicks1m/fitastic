package com.example.fitastic.models;

import android.graphics.Bitmap;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class Run {
    private Bitmap routeImg;
    private float distance;
    private double runDuration;
    private float speed;
    private String date;
    private Date time;

    public Run(Bitmap routeImg, float distance, double time, float speed) {
        this.routeImg = routeImg;
        this.distance = distance;
        this.runDuration = time;
        this.speed = speed;
        this.date = generateDate();
        this.time = generateTime();
    }

    private String generateDate() {
        return DateFormat.getDateInstance().format(Calendar.DATE);
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

    public String getDate() {
        return date;
    }

    public Date getTime() {
        return time;
    }
}
