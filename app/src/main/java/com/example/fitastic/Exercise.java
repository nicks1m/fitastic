package com.example.fitastic;

public class Exercise {

    private String title;
    private String reps;
    private String set;
    private String time;

    public Exercise(String title,String reps, String set){
        this.title = title;
        this.reps = reps;
        this.set = set;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReps() {
        return reps;
    }

    public void setReps(String reps) {
        this.reps = reps;
    }

    public String getSet() {
        return set;
    }

    public void setSet(String set) {
        this.set = set;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
