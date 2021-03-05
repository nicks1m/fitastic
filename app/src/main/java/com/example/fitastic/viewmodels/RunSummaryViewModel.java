package com.example.fitastic.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.fitastic.repositories.MainRepository;
import com.example.fitastic.utility.EpochUtility;

import java.util.ArrayList;

public class RunSummaryViewModel extends ViewModel {

    // debug
    private static String TAG = "RunSummaryViewModel";

    // post changes of run summary
    public MutableLiveData<ArrayList<String>> stats;

    public void getRecentRunStats() {
        MainRepository.updateRunEpochTimes();
        ArrayList<String> epochs = MainRepository.epochTimes;
        long value = EpochUtility.getMostRecentRun(epochs);
        MainRepository.getRunByEpoch(String.valueOf(value));
        ArrayList<String> recentRun = MainRepository.recentRun;
        stats.postValue(recentRun);
    }
}
