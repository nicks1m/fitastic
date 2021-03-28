package com.example.fitastic.utility;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.TimeZone;

public class RunDbUtility {

    /* RunDbUtility is a class which adds utility methods for storing and receiving runs from
    *  firebase like decoding the Base64 string back to an bitmap image. Also adds methods to get
    *  distance, speed, time in correct formatting.
    */

    // gets most recent run from a list of epoch times
    public static long getMostRecentRun(ArrayList<String> epochs) {
        // assume largest is first index and convert it to date object
        Date largest = convertEpochToDate(Long.valueOf(epochs.get(0)));
        // saved largest index
        int largestIndex = 0;
        // iterate through list
        for (int i = 0; i < epochs.size(); i++) {
            // convert temp to date
            Date temp = convertEpochToDate(Long.valueOf(epochs.get(i)));
            // test if temp is before largest
            if (largest.before(temp)) {
                // copy index and date
                largest = temp;
                largestIndex = i;
            }
        }
        // return largest
        return Long.valueOf(epochs.get(largestIndex));
    }

    // convert epoch time to date object
    public static Date convertEpochToDate(long epochTime) {
        // create date with epoch
        Date date = new Date(epochTime);
        // format date
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        format.setTimeZone(TimeZone.getDefault());
        String formatted = format.format(date);
        // return date
        return date;
    }

    // decodes Base64 string to a bitmap
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Bitmap stringToBitmap(String input) {
        // convert coded string to byte array
        byte[] data = Base64.getDecoder().decode(input);
        // decode byte array to bitmap
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    // calculate pace
    public static String calculatePace(String dist, String time) {
        // distance in km
        String sspace = "";
        double distkm = Double.parseDouble(dist)/1000;
        // time in min
        double runtime = Double.parseDouble(time) / 60;
        // calculate pace
        double dpace = runtime / distkm;
        int mpace = (int)dpace;
        int space = (int)((dpace - mpace) * 60);
//        if(space < 10){
//            sspace = "0" + space;
//        }
//        String sspace = new DecimalFormat("#.##").format(dpace);
        String pace = mpace + "\"" + space;
        return pace;
    }

    // calculate distance
    public static String calculateDistance(String dist) {
        // get distance in km
        double ddist = Double.parseDouble(dist)/1000;
        // format distance
        DecimalFormat df = new DecimalFormat("0.00");
        df.setRoundingMode(RoundingMode.DOWN);
        String dist1 = df.format(ddist) + " km";
        return dist1;
    }

    // calculate duration
    public static String calculateDuration(String time) {
        // time in s
        int totalSecs = Integer.parseInt(time);
        // work out hours, minutes, seconds
        int hours = totalSecs / 3600;
        int minutes = (totalSecs % 3600) / 60;
        int seconds = totalSecs % 60;
        String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        return  timeString;

    }
}
