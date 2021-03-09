package com.example.fitastic.viewmodels;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.fitastic.models.Run;
import com.example.fitastic.repositories.MainRepository;
import com.example.fitastic.services.TrackingService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;

public class StartFragViewModel extends ViewModel {

    // debug
    public static String TAG = "StartFragViewModel";

    // observable boolean from start frag will be used to pause service
    private MutableLiveData<Boolean> isTracking = new MutableLiveData<Boolean>();
    // observable binder from start frag used to create connection to service
    private MutableLiveData<TrackingService.myBinder> mBinder= new MutableLiveData<TrackingService.myBinder>();

    // facilitates connection between client to service
    private ServiceConnection serviceConnection = new ServiceConnection() {
        // callback for on service connection
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "ServiceConnected");
            // create instance of nested binder class inside service
            TrackingService.myBinder binder = (TrackingService.myBinder) service;
            // add that binder to the mBinder of this class so client can observe it
            mBinder.postValue(binder);
        }

        // callback for on service disconnect
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "ServiceDisconnected");
            mBinder.postValue(null);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void insertRun(Run r) {
        MainRepository.insertRun(label, r);
        label = 0;
        count = 0;
    }

    private long label = 0;
    private int count = 0;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveStat(float totalDistance, double time) {
        if (label == 0) {
            label = Instant.now().toEpochMilli();
        }

        Float[] stat = new Float[3];
        // km
        totalDistance /= 1000;
        BigDecimal db = new BigDecimal(totalDistance).setScale(2, RoundingMode.HALF_UP);
        stat[0] = db.floatValue();

        // min
        time /= 60;
        BigDecimal dba = new BigDecimal(time).setScale(2, RoundingMode.HALF_UP);
        stat[1] = dba.floatValue();

        // min/km
        float speed = (float) (time / totalDistance);
        BigDecimal d = new BigDecimal(speed).setScale(2, RoundingMode.HALF_UP);

        stat[2] = d.floatValue();
        MainRepository.addStat(label, count++, stat);
    }

    // get if tracking
    public LiveData<Boolean> isTracking() {
        return isTracking;
    }

    public void setIsTracking(MutableLiveData<Boolean> isTracking) {
        this.isTracking = isTracking;
    }

    // get service connection used to connect client to service
    public ServiceConnection getServiceConnection() {
        return serviceConnection;
    }

    // get binder of tracking service binder
    public MutableLiveData<TrackingService.myBinder> getBinder() {
        return mBinder;
    }
}
