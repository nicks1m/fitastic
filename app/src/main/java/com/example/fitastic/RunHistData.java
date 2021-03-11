package com.example.fitastic;

public class RunHistData {

    Long distance,duration,speed;

    RunHistData(){

    }

    public RunHistData(Long distance, Long duration, Long speed) {
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
}
