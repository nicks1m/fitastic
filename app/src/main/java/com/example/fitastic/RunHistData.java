package com.example.fitastic;

public class RunHistData {

    Long distance,duration,speed;
    String bitmap, date;

    RunHistData(){

    }

    public RunHistData(String date, String bitmap, Long distance, Long duration, Long speed) {
        this.date = date;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
