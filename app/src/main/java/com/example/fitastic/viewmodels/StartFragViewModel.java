package com.example.fitastic.viewmodels;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.fitastic.services.TrackingService;

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
