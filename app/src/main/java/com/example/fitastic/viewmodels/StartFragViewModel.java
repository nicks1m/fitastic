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

public class StartFragViewModel extends ViewModel {

    /* StartFragViewModel facilitates inputting run information to Firebase. Furthermore, it bind
    *  StartFrag to TrackingService so it can receive the users location. To achieve this, it stores
    *  the TrackingService binder, and creates a ServiceConnection which StartFrag can access.
    */

    // debug
    public static String TAG = "StartFragViewModel";

    // observable boolean from start frag will be used to pause service
    private MutableLiveData<Boolean> isTracking = new MutableLiveData<Boolean>();

    // observable binder from start frag used to create connection to service
    private MutableLiveData<TrackingService.myBinder> mBinder = new MutableLiveData<TrackingService.myBinder>();

    // store epoch label and stat count
    private long label = 0;
    private int count = 0;

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

    // inserts run to firebase
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void insertRun(Run r) {
        MainRepository.insertRun(label, r);
        label = 0;
        count = 0;
    }

    // saves stat to database whilst on run, used to look at information regarding the stats at
    // certain points on the run
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveStat(float totalDistance, double time) {
        // if no label has been created yet
        if (label == 0) {
            // get epoch time
            label = Instant.now().toEpochMilli();
        }

        // hold stats
        Float[] stat = new Float[3];
        // get distance in km
        totalDistance /= 1000;
        // convert distance to two decimal places
        BigDecimal db = new BigDecimal(totalDistance).setScale(2, RoundingMode.HALF_UP);
        // write distance
        stat[0] = db.floatValue();

        // convert time to seconds
        time /= 60;
        // convert time to two decimal places
        BigDecimal dba = new BigDecimal(time).setScale(2, RoundingMode.HALF_UP);
        // write time
        stat[1] = dba.floatValue();

        // calculate speed in min per km
        float speed = (float) (time / totalDistance);
        // convert speed to 2 decimal places
        BigDecimal d = new BigDecimal(speed).setScale(2, RoundingMode.HALF_UP);
        // write speed
        stat[2] = d.floatValue();
        // save stat to firebase
        MainRepository.addStat(label, count++, stat);
    }

    // creates an epoch time for run if none is detected
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void initialiseStatLabel() {
        // if no label exists create one
        if (label == 0) {
            label = Instant.now().toEpochMilli();
        }
        // save label to firebase
        MainRepository.initStat(label);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addInitialRunDirectory() {
        MainRepository.insertRunDirectory();
    }

    // get service connection used to connect client to service
    public ServiceConnection getServiceConnection() {
        return serviceConnection;
    }

    // get binder of tracking service binder allows StartFrag to get this binder
    public MutableLiveData<TrackingService.myBinder> getBinder() {
        return mBinder;
    }

    // destroys binder
    public void destroyBinder() {
        mBinder.postValue(null);
    }

    // isTracking
    public LiveData<Boolean> isTracking() {
        return isTracking;
    }

    // isTracking setter
    public void setIsTracking(MutableLiveData<Boolean> isTracking) {
        this.isTracking = isTracking;
    }
}
