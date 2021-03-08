package com.example.fitastic.utility;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.DateFormat;
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
}
