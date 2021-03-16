package com.example.fitastic;

public class Exercise {

    private String title;
    private String reps;
    private String set;
    private String time;
    private String kg;

    public Exercise(String title,String set, String reps, String kg){
        this.title = title;
        this.set = set;
        this.reps = reps;
        this.kg = kg;
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

    public String getKg() {
        return kg;
    }

    public void setKg(String kg) {
        this.kg = kg;
    }
}
