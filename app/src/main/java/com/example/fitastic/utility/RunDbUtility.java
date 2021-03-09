package com.example.fitastic.utility;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.TimeZone;

public class RunDbUtility {

    public static long getMostRecentRun(ArrayList<String> epochs) {
        Date largest = convertEpochToDate(Long.valueOf(epochs.get(0)));
        int largestIndex = 0;
        for (int i = 0; i < epochs.size(); i++) {
            Date temp = convertEpochToDate(Long.valueOf(epochs.get(i)));
            if (largest.before(temp)) {
                largest = temp;
                largestIndex = i;
            }
        }

        return Long.valueOf(epochs.get(largestIndex));
    }

    public static Date convertEpochToDate(long epochTime) {
        Date date = new Date(epochTime);
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        format.setTimeZone(TimeZone.getDefault());
        String formatted = format.format(date);
        return date;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Bitmap stringToBitmap(String input) {
        // convert coded string to byte array
        byte[] data = Base64.getDecoder().decode(input);
        // decode byte array to bitmap
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    public static String calculatePace(String dist, String time){
        double distkm = Double.parseDouble(dist)/1000;
        double runtime = Double.parseDouble(time) / 60;
        double dpace = runtime / distkm;
        int mpace = (int)dpace;
        int space = (int)((dpace - mpace) * 60);
//        String sspace = new DecimalFormat("#.##").format(dpace);
        String pace = mpace + "'" + space;
        return pace;

    }

    public static String calculateDistance(String dist){
        double ddist = Double.parseDouble(dist)/1000;
        String dist1 = new DecimalFormat("#.##").format(ddist) + " km";
        return dist1;
    }

    public static String calculateDuration(String time){

        int totalSecs = Integer.parseInt(time);
        int hours = totalSecs / 3600;
        int minutes = (totalSecs % 3600) / 60;
        int seconds = totalSecs % 60;
        String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        return  timeString;

    }
}
