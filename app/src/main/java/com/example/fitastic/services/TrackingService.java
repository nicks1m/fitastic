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
    private boolean isTracking = false;

    // facilitates connection of client to this server, actually a nested class in this service
    private IBinder mBinder = new myBinder();

    // used to obtain latitudes/longitudes of user location
    private FusedLocationProviderClient locationClient;

    // pending intent action
    private final String ACTION_SHOW_STARTFRAG = "ACTION_SHOW_STARTFRAG";

    // notification
    private final String NOTIFICATION_CHANNEL_ID = "NOTIFICATION_CHANNEL_ID";
    private final String NOTIFICATION_CHANNEL_NAME = "NOTIFICATION_CHANNEL_NAME";
    private final int NOTIFICATION_ID = 1;

    // holds points observable
    public MutableLiveData<ArrayList<ArrayList<LatLng>>> pathPoints;

    public ArrayList<ArrayList<LatLng>> polylines;
    public ArrayList<LatLng> polyline;

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
        isTracking = true;
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
        LocationRequest locationRequest = new LocationRequest()
                .setInterval(5000L) /* aprox time for request to happen (5sec) */
                .setFastestInterval(4000L) /* minimum time request will be received if one comes early  */
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);  /* gives accurate location result */
        if (PermissionUtility.hasLocationPermission(this)) {
            // start location collection
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

    public void addEmptyPolyline() {
        polylines.add(new ArrayList<LatLng>());
        polyline = new ArrayList<LatLng>();
        polylines.add(polyline);
        pathPoints.postValue(polylines);
    }

    // when app force quit will kill service
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
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

    public boolean isTracking() {
        return isTracking;
    }

    public void setTracking(boolean b) {
        isTracking = b;
    }

    public LiveData<ArrayList<ArrayList<LatLng>>> getPathPoints() {
        return pathPoints;
    }
}

/*
package com.example.fitastic.services;

        import android.annotation.SuppressLint;
        import android.app.Notification;
        import android.app.NotificationChannel;
        import android.app.NotificationManager;
        import android.app.PendingIntent;
        import android.content.Context;
        import android.content.Intent;
        import android.location.Location;
        import android.os.Build;
        import android.os.IBinder;
        import android.os.Looper;
        import android.util.Log;

        import androidx.annotation.NonNull;
        import androidx.annotation.Nullable;
        import androidx.annotation.RequiresApi;
        import androidx.core.app.NotificationCompat;
        import androidx.core.app.NotificationManagerCompat;
        import androidx.lifecycle.LifecycleService;

        import com.example.fitastic.LoginActivity;
        import com.example.fitastic.NavPage;
        import com.example.fitastic.R;
        import com.example.fitastic.utility.PermissionUtility;
        import com.google.android.gms.location.FusedLocationProviderClient;
        import com.google.android.gms.location.LocationCallback;
        import com.google.android.gms.location.LocationRequest;
        import com.google.android.gms.location.LocationResult;

public class TrackingService extends LifecycleService {


/*
    //private static boolean //isTracking = false;

    private FusedLocationProviderClient locationClient;

    private final String ACTION_START_OR_RESUME = "ACTION_START_OR_RESUME";
    private final String ACTION_PAUSE = "ACTION_PAUSE";
    private final String ACTION_STOP = "ACTION_STOP";
    private final String ACTION_SHOW_STARTFRAG = "ACTION_SHOW_STARTFRAG";

    private final String NOTIFICATION_CHANNEL_ID = "NOTIFICATION_CHANNEL_ID";
    private final String NOTIFICATION_CHANNEL_NAME = "NOTIFICATION_CHANNEL_NAME";
    private final int NOTIFICATION_ID = 1;

    private boolean isFirstRun = true;

    // callback whenever location is received from FusedLocationProvideClient
    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Log.i("TrackingService", String.valueOf("Latitude: " + locationResult.getLastLocation().getLatitude()) + " Longitude: " +
                    String.valueOf(locationResult.getLastLocation().getLongitude()));
            //Intent i = new Intent(this, startFrag)
        }
    };

    // called whenever intent sent to this service
    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        // get action from intent
        if (intent != null) {
            switch (intent.getExtras().getString("action")) {
                case ACTION_START_OR_RESUME:
                    isTracking = true;
                    if (isFirstRun) {
                        locationClient = new FusedLocationProviderClient(this);
                        Log.i("Tracking Action", "Action start/resume: ");
                        startForegroundService();
                        locationRequest();
                        isFirstRun = false;
                    }
                    break;
                case ACTION_PAUSE: Log.i("TrackingAction", "Action pause: ");
                    isTracking = false;
                    break;
                case ACTION_STOP: Log.i("TrackingAction", "Action stop: ");
                    isTracking = false;
                    break;
                default:
                    break;
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    // starts the service
    private void startForegroundService() {
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

        // start foreground service with notification
        startForeground(NOTIFICATION_ID, notification);
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
        LocationRequest locationRequest = new LocationRequest()
                .setInterval(5000L)
                .setFastestInterval(4000L)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (PermissionUtility.hasLocationPermission(this)) {
            // start location collection
            locationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        }
        else {
            Log.d("TrackingService", "No location permission detected");
        }
    }
}
*/
