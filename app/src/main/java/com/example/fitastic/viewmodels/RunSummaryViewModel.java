package com.example.fitastic.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.fitastic.repositories.MainRepository;
import com.example.fitastic.utility.RunDbUtility;

import java.util.ArrayList;

public class RunSummaryViewModel extends ViewModel {

    // debug
    private static String TAG = "RunSummaryViewModel";

    // post changes of the most recent run stats
    public MutableLiveData<ArrayList<String>> stats = new MutableLiveData<ArrayList<String>>();

    public void initialiseEpochTimes() {
        // update epoch times on main repo
        MainRepository.updateRunEpochTimes();
    }

    // gets recent run epoch time and causes main repo to post stats of that run
    public void getRecentRunStats(ArrayList<String> strings) {
        ArrayList<String> epochs = strings;
        // get most recent epoch
        long value = RunDbUtility.getMostRecentRun(epochs);
        // get that run by epoch time
        MainRepository.getRunByEpoch(String.valueOf(value));
    }

    // post stats
    public void handleRecentRun(ArrayList<String> strings) {
        ArrayList<String> recentRun = strings;
        stats.postValue(recentRun);
    }
}
