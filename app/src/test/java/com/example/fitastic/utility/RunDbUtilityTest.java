package com.example.fitastic.utility;

import org.junit.Test;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import static org.junit.Assert.*;

public class RunDbUtilityTest {

    @Test
    public void calculatePace() {
        // distance in km
        String dist = "7970";
        String time = "2287";
        double distkm = Double.parseDouble(dist)/1000;
        // time in min
        double runtime = Double.parseDouble(time) / 60;
        // calculate pace
        double dpace = runtime / distkm;

        int mpace = (int)dpace;
        int space = (int)((dpace - mpace) * 60);
//        String output
//        String pace = mpace + "\"" + space;

        int expectedmpace = 4;
        int expectedspace = 46;

        assertEquals(expectedmpace,mpace);
        assertEquals(expectedspace,space,1);




    }
    @Test
    // calculate distance
    public void calculateDistance() {
        String dist = "2345";
        // get distance in km
        double ddist = Double.parseDouble(dist)/1000;
        System.out.println(ddist);
        // format distance
        DecimalFormat df = new DecimalFormat("0.00");
        df.setRoundingMode(RoundingMode.DOWN);
        String dist1 = df.format(ddist) + " km";
        String expected = "2.34 km";
        assertEquals(expected,dist1);

    }
//    @Test
//    // calculate duration
//    public static String calculateDuration(String time) {
//        // time in s
//        int totalSecs = Integer.parseInt(time);
//        // work out hours, minutes, seconds
//        int hours = totalSecs / 3600;
//        int minutes = (totalSecs % 3600) / 60;
//        int seconds = totalSecs % 60;
//        String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
//        return  timeString;
//
//    }
}