package com.example.fitastic;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.Instant;

public class RunHistData {

    Long distance,duration,speed;
    String bitmap;
    long date;

    RunHistData(){

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public RunHistData(String bitmap, Long distance, Long duration, Long speed) {
        this.bitmap = bitmap;
        this.distance = distance;
        this.duration = duration;
        this.speed = speed;
    }

    public Long getDistance() {
        return distance;
    }

    public void setDistance(Long distance) {
        this.distance = distance;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Long getSpeed() {
        return speed;
    }

    public void setSpeed(Long speed) {
        this.speed = speed;
    }

    public String getBitmap() {
        return bitmap;
    }

    public void setBitmap(String image) {
        this.bitmap = image;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
