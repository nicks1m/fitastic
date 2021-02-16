package com.example.fitastic;

//This class is a helper interface to calculate any stats pertaining to user's run.

public class RunHelper {

    //This function takes in two args, distance(km) and time(mins) to calculate the average pace of the user.
    public static String calculatePace(double dist, int time){
        double avg = time/dist;
        String pace = "" + avg;
        return pace;
    }

    //This function takes in the time(in seconds) and converts it to a format of HOURS:MINS:SECONDS
    public static String calculateTime(){
        return "";
    }

    //This function takes in the total distance of run in METRES and converts it into a (double)km, format eg: 5.05km
    public static String calculateDistance(){
        return "";
    }
}
