package com.example.fitastic;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.fitastic.models.Run;
import com.example.fitastic.services.TrackingService;
import com.example.fitastic.utility.PermissionUtility;
import com.example.fitastic.utility.RunDbUtility;
import com.example.fitastic.viewmodels.StartFragViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;


public class startFrag extends Fragment implements EasyPermissions.PermissionCallbacks{

    /* StartFrag fragment controls the run feature of the app. To achieve this it uses google maps
    *  to display locations and routes. Before a user can go on a run, location permissions also
    *  need to be accepted, this is handled through startFrag. Also stats such as distance, time and
    *  pace are shown to the screen whilst on the run.
    *
    *  This fragment depends upon TrackingService which actually gets the location updates and
    *  StartFrag just listens to changes from the service. Additionally StartFragViewModel aids
    *  in binding StartFrag to TrackingService so start frag can access the service's location
    *  variables and listen for changes to them. Furthermore StartFragViewModel also aids
    *  in sending run data to firebase by parsing it to MainRepository.
    */

    // debug
    private static String TAG = "StartFrag";

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
    private int seconds;
    private int minutes;
    private int hours;

    public startFrag() {
        // Required empty public constructor
    }

    public static startFrag newInstance() {
        startFrag fragment = new startFrag();
        return fragment;
    }

    // initialise non graphical components
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // debug
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
                    // get a reference to service using binder in view model
                    mService = myBinder.getService();
                    // begin tracking
                    mService.startForegroundService();
                    // start receiving location points to this frag
                    enableUserLocation();
                } else {
                    // handle unbinding to service
                    Log.d(TAG, "unbound from service ");
                    // otherwise destroy instance if unbound
                    mService = null;
                    // unbind local connection
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

        // debug
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

        // multiplay button on click to navigate to multiplay page
        mpBtn.setOnClickListener(v->{
            controller.navigate(R.id.action_startFrag_to_multiPlayServer);
        });

        // end run button on click to navigate to run summary page
        endRunBtn.setOnClickListener(v -> {
            endRun();
        });
    }

    // start service from this frag
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startService() {
        // create intent holding service class
        Intent serviceIntent = new Intent(requireContext(), TrackingService.class);
        // start service using intent
        getActivity().startService(serviceIntent);
        // bind this frag to the service
        bindService();
    }

    // bind this frag to service
    private void bindService() {
        // create intent holding service class
        Intent serviceIntent = new Intent(requireContext(), TrackingService.class);
        // get server connection
        localConnection = mViewModel.getServiceConnection();
        // bind service to activity using intent and connection, create new one if none detected
        getActivity().bindService(serviceIntent, localConnection, Context.BIND_AUTO_CREATE);
    }

    // call back for when map is ready
    private OnMapReadyCallback callback = new OnMapReadyCallback() {
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
        if (mService != null) {
            // observes pathPoints in the service, if any changes are posted to it from the service
            // then this observer will detect the changes and its onChanged method will execute
            mService.getPathPoints().observe(getViewLifecycleOwner(), new Observer<ArrayList<ArrayList<LatLng>>>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onChanged(ArrayList<ArrayList<LatLng>> arrayLists) {
                    // debug
                    Log.d(TAG, "pathPoint change");

                    // add the lat/lng to this polyline, this polyline stores all locations saved whilst
                    // on the run
                    polyline.add(new LatLng(arrayLists.get(arrayLists.size()-1).get(arrayLists.get(arrayLists.size() -1).size() -1).latitude,
                            arrayLists.get(arrayLists.size()-1).get(arrayLists.get(arrayLists.size() -1).size() -1).longitude));

                    // get the current time
                    double thisTime = getTimeInSeconds();
                    // calculate distance using local method calculatePenultimateLastPolylineDistance
                    // only works out distance for the second last location to the last location update
                    float distance = calculatePenultimateLastPolylineDistance(polyline);

                    // increment distance whilst running
                    distanceWhilstRunning += distance;

                    // used to convert distanceWhilstRunning to two decimal places
                    BigDecimal db = new BigDecimal(distanceWhilstRunning).setScale(2, RoundingMode.HALF_UP);
                    distanceWhilstRunning = db.floatValue();

                    // init speed
                    float speed = 0;
                    // checks if last time is greater than 0, the way speed is calculated is at the end
                    // of onChanged the current time is set to lastTime and at beginning of onChanged thisTime
                    // is set to current, this is to work out the current time interval
                    if (lastTime > 0)
                        speed = (float) (distance / (thisTime - lastTime));

                    // used to convert speed to two decimal places
                    BigDecimal dba = new BigDecimal(speed).setScale(2, RoundingMode.HALF_UP);
                    speed = dba.floatValue();

                    // set text of distance and speed labels corresponding variables uses run utility to
                    // get to desired format
                    distanceView.setText(RunDbUtility.calculateDistance(String.valueOf(distanceWhilstRunning)));
                    averagePaceView.setText(RunDbUtility.calculatePace(String.valueOf(distanceWhilstRunning), String.valueOf(getTimeInSeconds())));

                    // used to retrieve stats whilst on runs and post to firebase
                    // if its first stat then initialise the run location within firebase
                    if (count == 1)
                        mViewModel.initialiseStatLabel();

                    // if distance is greater than the distance interval
                    if (distanceWhilstRunning > (distanceInterval * count)) {
                        // save stat
                        mViewModel.saveStat(distanceInterval * count, thisTime);
                        // increment count to listen for next distance interval
                        count++;
                    }

                    // set lastTime to thisTime, used to work out speed
                    lastTime = thisTime;
                    // draws a line from the last location to second last location on googleMap
                    drawLatestPolyline();
                    // moves the camera to the last location
                    moveCameraToUser();
                }
            });
        }
    }

    // draw latest points from polyline to map
    private void drawLatestPolyline() {
        // second last location
        LatLng penultimate;
        // last location
        LatLng last = null;
        try {
            // get second last and last
            penultimate = polyline.get(polyline.size() - 2);
            last = polyline.get(polyline.size() - 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            // catch any exceptions
            penultimate = null;
        }

        // if location are not null
        if (penultimate != null && last != null) {
            // create a new polyline options with the locations
            PolylineOptions options = new PolylineOptions()
                    .color(Color.BLACK)
                    .width(12f)
                    .add(penultimate)
                    .add(last);

            // draw the options to map
            map.addPolyline(options);
        }
    }

    // move camera to last point in list
    private void moveCameraToUser() {
        // checks if there are enough points to move camera
        if (!polyline.isEmpty() && polyline.size() > 2) {
            // move map camera to last lat/lng
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(polyline.get(polyline.size()-1), 17f));
        }
    }

    // either starts the binding to tracking service or will pause/resume location updates
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void toggleRun() {
        // bind to service if no instance detected
        if (mService == null) {
            // prepare fragment to begin runnign
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
                // start timer on screen
                startTimer();
                // begin location updates
                mService.locationRequest();
                // set end run btn to invisible
                endRunBtn.setVisibility(View.INVISIBLE);
                // set mp button to invisible
                mpBtn.setVisibility(View.INVISIBLE);
            }
            else {
                isPaused = true;
                // pause tracking
                startBtn.setText("Start");
                // pause location updates
                mService.removeLocationUpdates();
                // pause timer
                pauseTimer();
                // create new index for polyline, this is importance as when you pause the run, you need
                // a new ArrayList for the polyline because if you begin adding locations to the old
                // polyline then the last point before the pause will be drawn to the first point after
                polylines.add(polyline);
                polyline = new ArrayList<LatLng>();
                // set end run to visible
                endRunBtn.setVisibility(View.VISIBLE);
                // set mp button to visible
                mpBtn.setVisibility(View.VISIBLE);
            }
        }
    }

    // ends run saves run to db unbind from service and initialises run variables for next run
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
            // uses local method calculatePolylineDistance to work out distance
            distance += calculatePolylineDistance(getPolylines().get(i));
        }

        // speed in m/s
        float avgSpeed = Math.round(distance/time);

        // save time and distance in ways lambda map snapshot can access
        double realTime = time;
        float finalDistance = distanceWhilstRunning;
        // create a snapshot (image) of the map, bmp is the bitmap image of the zoomed out route
        map.snapshot(bmp -> {
            // round distance and speed to two decimal places
            BigDecimal d = new BigDecimal(finalDistance).setScale(2, RoundingMode.HALF_UP);
            BigDecimal s = new BigDecimal(avgSpeed).setScale(2, RoundingMode.HALF_UP);
            // create a run using bitmap and run information
            Run r = new Run(bmp, d.floatValue(), realTime, s.floatValue());
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

    // lifecycle method called whenever start frag is navigated to
    @Override
    public void onResume() {
        super.onResume();
        // debug
        Log.d(TAG, "onResume: ");
        // pause location updates if an instance of service is detected
        if (mService != null)
            mService.removeLocationUpdates();
    }

    // get distance whilst on run between penultimate/last latlng's
    private float calculatePenultimateLastPolylineDistance(ArrayList<LatLng> polyline) {
        // if polyline hasnt got enough locations return 0
        if (polyline.size() <= 1)
            return 0.0f;

        // second last location
        LatLng penultimate = new LatLng(polyline.get(polyline.size() - 2).latitude,
                polyline.get(polyline.size() - 2).longitude);
        // last location
        LatLng last = new LatLng(polyline.get(polyline.size() - 1).latitude,
                polyline.get(polyline.size() - 1).longitude);
        // hold distance
        float[] result = new float[1];
        // work out distance between last/second last location hold in result array
        Location.distanceBetween(penultimate.latitude, penultimate.longitude,
                last.latitude, last.longitude, result);
        // return the result
        return result[0];
    }

    // calculates distance of the whole polyline
    private float calculatePolylineDistance(ArrayList<LatLng> polyline) {
        // init distance
        float distance = 0.0f;

        // iterate through polyline
        for (int i = 0; i < polyline.size() - 2; i++) {
            // second last location
            LatLng penultimate = polyline.get(i);
            // last location
            LatLng last = polyline.get(i + 1);
            // hold distance between last and second last
            float[] result = new float[1];
            // work out distance between last/second last location hold in result array
            Location.distanceBetween(
                    penultimate.latitude,
                    penultimate.longitude,
                    last.latitude,
                    last.longitude,
                    result
            );
            // increment distance by result
            distance += result[0];
        }
        return distance;
    }

    // zooms out to entire route of run to take an image
    private void zoomOutToRoute() {
        // will create an area the map needs to zoom to from latitudes/longitudes collected from run
        LatLngBounds.Builder boundBuilder = new LatLngBounds.Builder();
        // adjust size if size is not enough
        int size = polylines.size();
        if (size == 1) {
            size = 2;
        }
        // iterate through lat/lng and include them in the bound
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < polylines.get(i).size() - 1; j++) {
                // add all locations to bounds
                boundBuilder.include(polylines.get(i).get(j));
            }
        }

        // zoom out camera, the camera is moved to a way that ensures all locations within the bound can be
        // seen
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(boundBuilder.build(),
                mapView.getMeasuredWidth(),
                mapView.getMeasuredHeight(),
                (int) (mapView.getMeasuredHeight() * 0.05f)
                ));
    }

    // starts the timer
    public void startTimer() {
        // timer task is tasks used by the timer
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (!isPaused) {
                    // increments time
                    time++;
                    // set text of text view
                    timerView.setText(getTimerText());
                }
            }
        };
        // schedule timer task every second
        timer.scheduleAtFixedRate(timerTask, 0, 1000);
    }

    // gets the string format of time to display to timer
    private String getTimerText() {
        // get rounded time
        int rounded = (int) Math.round(time);

        // work out time in sec, min, hr
        seconds = ((rounded % 86400) % 3600) % 60;
        minutes = ((rounded % 86400) % 3600) / 60;
        hours = (rounded % 86400) / 3600;

        // return formatted string
        return  String.format("%02d", hours) + ":" +
                String.format("%02d", minutes) + ":" +
                String.format("%02d", seconds);
    }

    // converts string timer text to time in seconds
    private int getTimeInSeconds() {
        // get timer text
        String string = (String) timerView.getText();
        // create a string array new element whenever : is read
        String[] times = string.split(":");

        // return seconds
        return ((Integer.parseInt(times[0]) * 60) * 60) +
                (Integer.parseInt(times[1]) * 60) +
                Integer.parseInt(times[2]);
    }

    // pauses timer
    public void pauseTimer() {
        timerTask.cancel();
        timer.cancel();
    }

    // stops timer
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
            // all permissions required to use the run page
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

    // permission granted callback
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        // do nothing
    }

    // permission denied callback
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

    // is tracking
    private boolean isTracking() {
        return this.isTracking;
    }

    // polyline getter
    public ArrayList<ArrayList<LatLng>> getPolylines() {
        return polylines;
    }

}