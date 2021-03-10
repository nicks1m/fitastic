package com.example.fitastic;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.NavHost;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.fitastic.models.Run;
import com.example.fitastic.services.TrackingService;
import com.example.fitastic.utility.PermissionUtility;
import com.example.fitastic.utility.RunDbUtility;
import com.example.fitastic.viewmodels.StartFragViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link startFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class startFrag extends Fragment implements EasyPermissions.PermissionCallbacks{

    /* start Frag contains the start run page of the app. Contains a google maps and means of
     * navigation to run logs fragment. This page allows users to start a run, view their route on
     * the map, end their run and visit the run logs showing their run history.
     */

    // debug
    private static String TAG = "StartFrag";
    int x = 0;

    // if fragment is tracking or not
    private boolean isTracking = false;

    // calculate distance/speed whilst on run
    private float distanceWhilstRunning = 0.0f;
    private double lastTime;
    final float distanceInterval = 1000.0f;
    int count = 1;

    // permissions
    private final int LOCATION_PERMISSION_CODE = 1;

    // navigation to run history
    private NavController controller;

    // UI components
    private TextView distanceView;
    private TextView averagePaceView;
    private TextView timerView;
    private Button logsBtn;
    private Button startBtn;
    private Button statsBtn;
    private Button endRunBtn;
    private Button mpBtn;


    // map
    private GoogleMap map;
    private View mapView;

    // hold latitudes/longitudes whilst on run
    private ArrayList<ArrayList<LatLng>> polylines = new ArrayList<ArrayList<LatLng>>();
    private ArrayList<LatLng> polyline = new ArrayList<LatLng>();

    // accessing service/binding to it
    private TrackingService mService;
    private StartFragViewModel mViewModel;

    // hold connection to service
    private ServiceConnection localConnection;

    // timer
    private Timer timer;
    private TimerTask timerTask;
    private Double time = 0.0;
    private boolean isPaused = true;

    public startFrag() {
        // Required empty public constructor
    }

    public static startFrag newInstance() {
        startFrag fragment = new startFrag();
        return fragment;
    }

    // Lifecycle methods
    // initialise non graphical components
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate: ");

        // get corresponding view model for start frag
        mViewModel = new ViewModelProvider(requireActivity()).get(StartFragViewModel.class);

        // observe changes on binder within view model
        mViewModel.getBinder().observe(this, new Observer<TrackingService.myBinder>() {
            // when change detected create an instance of the service and start it
            // or destroy instance if binder is null (run finished)
            @Override
            public void onChanged(TrackingService.myBinder myBinder) {
                // if the binder is bound to service
                if (myBinder != null) {
                    Log.d(TAG, "bound to service ");
                    // get a reference to service
                    mService = myBinder.getService();
                    mService.startForegroundService();
                    enableUserLocation();
                } else {
                    Log.d(TAG, "unbound from service ");
                    // otherwise destroy instance if unbound
                    mService = null;

                    if (localConnection != null)
                        requireContext().unbindService(localConnection);
                }
            }
        });
    }

    // initialise graphical components
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView: ");
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_start, container, false);

        // initialise graphical components
        startBtn = root.findViewById(R.id.runStartBtn);
        distanceView = root.findViewById(R.id.distanceLabelRun);
        averagePaceView = root.findViewById(R.id.speedLabelRun);
        timerView = root.findViewById(R.id.runTimerView);
        startBtn = root.findViewById(R.id.runStartBtn);
        statsBtn = root.findViewById(R.id.runStatBtn);
        endRunBtn = root.findViewById(R.id.runEndButton);
        mpBtn = root.findViewById(R.id.btn_mp);
        mpBtn.setOnClickListener(v->{
            controller.navigate(R.id.action_startFrag_to_multiPlayServer);
        });

        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, "onViewCreated: ");
        // initialise map fragment
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        mapView = mapFragment.getView();

        // get nav controller
        controller = Navigation.findNavController(view);

        // set on click for button to bind this frag to tracking service
        startBtn.setOnClickListener(v -> {
            timer = new Timer();
            toggleRun();
        });

        endRunBtn.setOnClickListener(v -> {
            endRun();
        });
    }


    // start service from this frag
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startService() {
        Intent serviceIntent = new Intent(requireContext(), TrackingService.class);
        getActivity().startService(serviceIntent);

        bindService();
    }

    // bind this frag to service
    private void bindService() {
        Intent serviceIntent = new Intent(requireContext(), TrackingService.class);
        localConnection = mViewModel.getServiceConnection();
        getActivity().bindService(serviceIntent, localConnection, Context.BIND_AUTO_CREATE);
    }

    // opens run history fragment
    private void openLogs() {
        controller.navigate(R.id.action_startFrag_to_runHistoryFragment);
    }

    // call back for when map is ready
    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            // reference map allows polylines to be drawn to it and camera manipulation
            map = googleMap;
            // request location permissions
            requestLocation();
        }
    };

    // begins tracking user location using service
    @SuppressLint("MissingPermission")
    private void enableUserLocation() {
        // will obtain user location and place marker on map
        if (mService != null) {
            mService.getPathPoints().observe(getViewLifecycleOwner(), new Observer<ArrayList<ArrayList<LatLng>>>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onChanged(ArrayList<ArrayList<LatLng>> arrayLists) {
                    Log.d(TAG, "pathPoint change");

                    if (x > 20)
                        x++;

                    double thisTime = getTimeInSeconds();

                    polyline.add(new LatLng(arrayLists.get(arrayLists.size()-1).get(arrayLists.get(arrayLists.size() -1).size() -1).latitude,
                            arrayLists.get(arrayLists.size()-1).get(arrayLists.get(arrayLists.size() -1).size() -1).longitude));

                    float distance = calculatePenultimateLastPolylineDistance(polyline);

                    BigDecimal d = new BigDecimal(distance).setScale(2, RoundingMode.HALF_UP);
                    distance = d.floatValue();

                    distanceWhilstRunning += distance;

                    BigDecimal db = new BigDecimal(distanceWhilstRunning).setScale(2, RoundingMode.HALF_UP);
                    distanceWhilstRunning = db.floatValue();

                    float speed = 0;
                    if (lastTime > 0)
                        speed = (float) (distance / (thisTime - lastTime));

                    BigDecimal dba = new BigDecimal(speed).setScale(2, RoundingMode.HALF_UP);
                    speed = dba.floatValue();

                    distanceView.setText(RunDbUtility.calculateDistance(String.valueOf(distanceWhilstRunning)));
                    averagePaceView.setText(RunDbUtility.calculatePace(String.valueOf(distanceWhilstRunning), String.valueOf(getTimeInSeconds())));

                    if (count == 1)
                        mViewModel.initialiseStatLabel();

                    if (distanceWhilstRunning > (distanceInterval * count)) {
                        mViewModel.saveStat(distanceInterval * count, thisTime);
                        count++;
                    }

                    x++;

                    lastTime = thisTime;
                    drawLatestPolyline();
                    moveCameraToUser();
                }
            });
        }
    }

    // draw points from polyline to map
    private void drawLatestPolyline() {
        LatLng penultimate;
        LatLng last = null;
        try {
            penultimate = polyline.get(polyline.size() - 2);
            last = polyline.get(polyline.size() - 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            penultimate = null;
        }

        if (penultimate != null && last != null) {
            PolylineOptions options = new PolylineOptions()
                    .color(Color.BLACK)
                    .width(12f)
                    .add(penultimate)
                    .add(last);

            map.addPolyline(options);
        }
    }

    // move camera to last point in list
    private void moveCameraToUser() {
        if (!polyline.isEmpty() && polyline.size() > 2) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(polyline.get(polyline.size()-1), 17f));
        }
    }

    // either starts the binding to tracking service or will pause/resume location updates
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void toggleRun() {
        // bind to service if no instance detected
        if (mService == null) {
            isTracking = true;
            isPaused = false;
            startBtn.setText("Stop");
            startTimer();
            startService();
        } else { /* else pause/resume location updates */
            isTracking = !isTracking;
            if (isTracking()) {
                isPaused = false;
                // resume tracking
                startBtn.setText("Stop");
                startTimer();
                mService.locationRequest();
                endRunBtn.setVisibility(View.INVISIBLE);
            }
            else {
                isPaused = true;
                // pause tracking
                startBtn.setText("Start");
                mService.removeLocationUpdates();
                pauseTimer();
                // create new index for polyline
                polylines.add(polyline);
                polyline = new ArrayList<LatLng>();
                endRunBtn.setVisibility(View.VISIBLE);
            }
        }
    }

    // ends run saves run to db unbind from service and reinitialises run variables
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void endRun() {
        // zooms out to see the entire route that the user has covered
        zoomOutToRoute();

        // distance in meters
        float distance = 0.0f;

        // holds distance
        int size = polylines.size();
        if (size == 1)
            size++;

        // works out distance of each polyline
        for (int i = 0; i < size - 1; i++) {
            distance += calculatePolylineDistance(getPolylines().get(i));
        }

        // speed in m/s
        float avgSpeed = Math.round(distance/time);

        double realTime = time;
        float finalDistance = distance;
        // create a snapshot of the map, bmp is the bitmap image of the zoomed out route
        map.snapshot(bmp -> {
            // create a run using bitmap and run information
            Run r = new Run(bmp, finalDistance, realTime, avgSpeed);
            // insert run to database
            mViewModel.insertRun(r);
        });

        // remove location variables to reset fragment
        mService = null;
        mViewModel.destroyBinder();
        polylines = new ArrayList<ArrayList<LatLng>>();
        polyline = new ArrayList<LatLng>();
        distanceWhilstRunning = 0.0f;
        lastTime = 0.0;
        count = 1;

        // navigate to next frag
        controller.navigate(R.id.action_startFrag_to_runSummary);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        if (mService != null)
            mService.removeLocationUpdates();
    }

    public ArrayList<ArrayList<LatLng>> getPolylines() {
        return polylines;
    }

    // get distance whilst on run, between penultimate/last latlng's
    private float calculatePenultimateLastPolylineDistance(ArrayList<LatLng> polyline) {
        if (polyline.size() <= 1)
            return 0.0f;
        LatLng penultimate = new LatLng(polyline.get(polyline.size() - 2).latitude,
                polyline.get(polyline.size() - 2).longitude);

        LatLng last = new LatLng(polyline.get(polyline.size() - 1).latitude,
                polyline.get(polyline.size() - 1).longitude);

        float[] result = new float[1];

        Location.distanceBetween(penultimate.latitude, penultimate.longitude,
                last.latitude, last.longitude, result);

        return result[0];
    }

    private float calculatePolylineDistance(ArrayList<LatLng> polyline) {
        float distance = 0.0f;

        //try {
            for (int i = 0; i < polyline.size() - 2; i++) {
                 LatLng penultimate = polyline.get(i);
                 LatLng last = polyline.get(i + 1);
                 float[] result = new float[1];

                    Location.distanceBetween(
                        penultimate.latitude,
                        penultimate.longitude,
                        last.latitude,
                        last.longitude,
                        result
                 );

                distance += result[0];
            }
            return distance;
    }

    // zooms out to entire route of run to take an image
    private void zoomOutToRoute() {
        // will create an area the map needs to zoom to from latitudes/longitudes collected from run
        LatLngBounds.Builder boundBuilder = new LatLngBounds.Builder();

        int size = polylines.size();
        if (size == 1) {
            size = 2;
        }
        // iterate through lat/lng and include them in the bound
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < polylines.get(i).size() - 1; j++) {
                boundBuilder.include(polylines.get(i).get(j));
            }
        }

        // zoom out camera to the bounds collected
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(boundBuilder.build(),
                mapView.getMeasuredWidth(),
                mapView.getMeasuredHeight(),
                (int) (mapView.getMeasuredHeight() * 0.05f)
                ));
    }


    public void startTimer() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (!isPaused) {
                    time++;
                    // set text of text view
                    timerView.setText(getTimerText());
                }
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 1000);
    }

    int seconds;
    int minutes;
    int hours;

    private String getTimerText() {
        int rounded = (int) Math.round(time);

        seconds = ((rounded % 86400) % 3600) % 60;
        minutes = ((rounded % 86400) % 3600) / 60;
        hours = (rounded % 86400) / 3600;

        return  String.format("%02d", hours) + ":" +
                String.format("%02d", minutes) + ":" +
                String.format("%02d", seconds);
    }

    private int getTimeInSeconds() {
        String string = (String) timerView.getText();
        String[] times = string.split(":");

        return ((Integer.parseInt(times[0]) * 60) * 60) +
                (Integer.parseInt(times[1]) * 60) +
                Integer.parseInt(times[2]);
    }

    public void pauseTimer() {
        timerTask.cancel();
        timer.cancel();
    }

    public void stopTimer() {
        timer.cancel();
        timerTask.cancel();
        time = 0.0;
    }

    // requests location permissions from user
    public void requestLocation() {
        if (PermissionUtility.hasLocationPermission(requireContext())) {
            // do nothing
        }
        else {
            String[] perms = {
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
            };

            // request perms for location
            EasyPermissions.requestPermissions(this,
                    "Location needed for app",
                    LOCATION_PERMISSION_CODE,
                    perms);
        }
    }

    // permission request callback
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // parse params to easy library to make request
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        // do nothing
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            // show app setting to accept perm
            new AppSettingsDialog.Builder(this).build().show();
        } else {
            // otherwise request location again
            requestLocation();
        }
    }

    private boolean isTracking() {
        return this.isTracking;
    }

}