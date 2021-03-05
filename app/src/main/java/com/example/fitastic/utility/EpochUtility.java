package com.example.fitastic.utility;

import java.util.ArrayList;

public class EpochUtility {

    public static long getMostRecentRun(ArrayList<String> epochs) {
        long largest = Long.valueOf(epochs.get(0));
        for (int i = 0; i < epochs.size() - 1; i++) {
            long temp = Long.valueOf(epochs.get(0));
            if (largest < temp)
                largest = temp;
        }

        return largest;
    }
}
