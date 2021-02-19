package com.example.fitastic;

import java.util.ArrayList;
import java.util.List;

public class User {
    public String name, email, dob, displayname,reps,set,time,title,bmi;

    List<Exercise>custom_workout = new ArrayList();


    public User(){

    }

    public User(String name, String email, String dob) {
        this.name = name;
        this.email = email;
        this.dob = dob;
        this.displayname = "";
        this.bmi = "";

    }

//    public void custom_workout(String title){
//        this.title = title;
//        exercise(title,reps,set,time);
//
//    }

//    public void exercise(String title, String reps, String set, String time){
//        this.reps = reps;
//        this.set = set;
//        this.time = time;
//    }



}
