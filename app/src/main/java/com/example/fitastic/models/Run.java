package com.example.fitastic.models;

import android.graphics.Bitmap;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

public class Run {

    /* Model to store runs under. Stores durations, time when run was started, speed, distance and
    *  the image of the route in a Bitmap format. Generates the date on its own without input.
    */

    // run variables
    private Bitmap routeImg;
    private float distance;
    private double runDuration;
    private float speed;
    private Date time;
    private Timestamp timestamp;

    // construct a run
    public Run(Bitmap routeImg, float distance, double time, float speed) {
        this.routeImg = routeImg;
        this.distance = distance;
        this.runDuration = time;
        this.speed = speed;
        this.time = generateTime();
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    // generate date
    private Date generateTime() {
        return Calendar.getInstance().getTime();
    }

    // routeImg getter
    public Bitmap getRouteImg() {
        return routeImg;
    }

    // distance getter
    public float getDistance() {
        return distance;
    }

    // duration getter
    public double getRunDuration() {
        return runDuration;
    }

    // speed getter
    public float getSpeed() {
        return speed;
    }

    // time getter
    public Date getTime() {
        return time;
    }
}
