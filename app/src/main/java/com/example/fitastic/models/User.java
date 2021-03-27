package com.example.fitastic.models;

import com.example.fitastic.models.Exercise;

import java.util.ArrayList;
import java.util.List;

public class User {
    public String name, email, dob, displayname,reps,set,time,title,bmi;
    public int points;

    List<Exercise>custom_workout = new ArrayList();


    public User(){

    }

    public User(String name, String email, String dob) {
        this.name = name;
        this.email = email;
        this.dob = dob;
        this.displayname = "";
        this.bmi = "";
        this.points = 0;

    }



}
