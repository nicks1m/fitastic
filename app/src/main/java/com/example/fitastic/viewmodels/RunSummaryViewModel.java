package com.example.fitastic.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.fitastic.repositories.MainRepository;
import com.example.fitastic.utility.EpochUtility;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class RunSummaryViewModel extends ViewModel {

    // debug
    private static String TAG = "RunSummaryViewModel";

    // post changes of run summary
    public MutableLiveData<ArrayList<String>> stats = new MutableLiveData<ArrayList<String>>();

    public void initialiseEpochTimes() {
        MainRepository.updateRunEpochTimes();

    }

    public void getRecentRunStats(ArrayList<String> strings) {
        ArrayList<String> epochs = strings;
        long value = EpochUtility.getMostRecentRun(epochs);
        MainRepository.getRunByEpoch(String.valueOf(value));
    }

    public void handleRecentRun(ArrayList<String> strings) {
        ArrayList<String> recentRun = strings;
        stats.postValue(recentRun);
    }
}
