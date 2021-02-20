package com.example.fitastic.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.fitastic.LoginActivity;
import com.example.fitastic.NavPage;
import com.example.fitastic.R;
import com.example.fitastic.utility.PermissionUtility;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class TrackingService extends Service {

    /* Foreground tracking service means it can't be killed by the system when resources are low */

    // debug
    private static String TAG = "TrackingService";

    // controls whether the service is tracking user location or not
    private MutableLiveData<Boolean> isTracking = new MutableLiveData<Boolean>();

    // facilitates connection of client to this server, actually a nested class in this service
    private IBinder mBinder = new myBinder();

    // used to obtain latitudes/longitudes of user location
    private FusedLocationProviderClient locationClient;

    // holds points observable this is observed from client to detect changes to this list
    public MutableLiveData<ArrayList<ArrayList<LatLng>>> pathPoints;
    public ArrayList<ArrayList<LatLng>> polylines;
    public ArrayList<LatLng> polyline;

    // constants
    // pending intent action used when notification is clicked
    private final String ACTION_SHOW_STARTFRAG = "ACTION_SHOW_STARTFRAG";

    // values for how frequent locations are requested (3 sec)
    private final long LOCATION_REQUEST_INTERVAL = 3000L;
    private final long FASTEST_LOCATION_REQUEST_INTERVAL = 3000L;

    // notification
    private final String NOTIFICATION_CHANNEL_ID = "NOTIFICATION_CHANNEL_ID";
    private final String NOTIFICATION_CHANNEL_NAME = "NOTIFICATION_CHANNEL_NAME";
    private final int NOTIFICATION_ID = 1;

    // called whenever intent sent to this service
    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        // check if intent received is not null
        if (intent != null) {
            // start service
            startForegroundService();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    // when app force quit will kill service
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    // starts the service
    public void startForegroundService() {
        // this needed to show notification on android system
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // notification channels unsupported for android versions below android O
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager);
        }

        // create notification with notification channel
        Notification notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setAutoCancel(false)
                .setOngoing(true)
                .setSmallIcon(R.drawable.runningman)
                .setContentTitle("Fitastic - Run")
                .setContentText("00:00:00")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(getActivityPendingIntent()).build();

        // initialise FusedLocationProvideClient
        locationClient = new FusedLocationProviderClient(this);

        // start foreground service with notification
        startForeground(NOTIFICATION_ID, notification);
        //isTracking = true;
        // initialise arraylists/mutableLiveData
        pathPoints = new MutableLiveData<ArrayList<ArrayList<LatLng>>>();
        polylines = new ArrayList<ArrayList<LatLng>>();
        polyline = new ArrayList<LatLng>();

        // start tracking location
        locationRequest();
    }

    // create activity pending intent
    private PendingIntent getActivityPendingIntent() {
        Intent i = new Intent(this, NavPage.class);
        i.putExtra("action", ACTION_SHOW_STARTFRAG);
        return PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    // create notification channel
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel(NotificationManager notificationManager) {
        // create notification
        NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW);

        notificationManager.createNotificationChannel(channel);
    }

    // cant detect EasyPermissions
    @SuppressLint("MissingPermission")
    public void locationRequest() {
        // create a location request
        LocationRequest locationRequest = new LocationRequest()
                .setInterval(LOCATION_REQUEST_INTERVAL) /* aprox time for request to happen (5sec) */
                .setFastestInterval(FASTEST_LOCATION_REQUEST_INTERVAL) /* minimum time request will be received if one comes early  */
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);  /* gives accurate location result */
        if (PermissionUtility.hasLocationPermission(this)) {
            // start location collection parse locationCallback already made
            locationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        }
        else {
            Log.d(TAG, "No location permission detected");
        }
    }

    // callback whenever location is received from FusedLocationProvideClient
    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Log.d(TAG, String.valueOf("Latitude: " + locationResult.getLastLocation().getLatitude()) + " Longitude: " +
                    String.valueOf(locationResult.getLastLocation().getLongitude()));
            polyline.add(new LatLng(locationResult.getLastLocation().getLatitude(),
                    locationResult.getLastLocation().getLongitude()));
            polylines.add(polyline);
            pathPoints.postValue(polylines);
        }
    };

    // used to remove updates either when client is paused or finished run
    public void removeLocationUpdates() {
        if (locationClient != null) {
            locationClient.removeLocationUpdates(locationCallback);
        }
    }

    // deals with clients binding to it
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // used to retrieve an instance of this service from the client to bind to it
    public class myBinder extends Binder {
        // returns an instance of the service
        public TrackingService getService() {
            return TrackingService.this;
        }
    }

    public LiveData<ArrayList<ArrayList<LatLng>>> getPathPoints() {
        return pathPoints;
    }
}