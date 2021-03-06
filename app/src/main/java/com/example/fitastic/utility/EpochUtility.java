package com.example.fitastic.utility;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class EpochUtility {

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
}
