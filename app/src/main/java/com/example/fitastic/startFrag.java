package com.example.fitastic;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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

import com.example.fitastic.services.TrackingService;
import com.example.fitastic.utility.PermissionUtility;
import com.example.fitastic.viewmodels.StartFragViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

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

    // actions for service
    private final String ACTION_START_OR_RESUME = "ACTION_START_OR_RESUME";
    private final String ACTION_PAUSE = "ACTION_PAUSE";
    private final String ACTION_STOP = "ACTION_STOP";

    // permissions
    private final int LOCATION_PERMISSION_CODE = 1;

    // navigation to run history
    private NavController controller;

    // UI components
    private TextView distanceView;
    private TextView averagePaceView;
    private Button logsBtn;
    private Button startBtn;
    private Button statsBtn;

    // accessing service/binding to it
    private TrackingService mService;
    private StartFragViewModel mViewModel;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public startFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment startFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static startFrag newInstance(String param1, String param2) {
        startFrag fragment = new startFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    // Lifecycle methods
    // initialise non graphical components
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

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
                } else {
                    Log.d(TAG, "unbound from service ");
                    // otherwise destroy instance if unbound
                    mService = null;
                }
            }
        });
    }

    // initialise graphical components
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_start, container, false);

        // initialise graphical components
        startBtn = root.findViewById(R.id.runStartBtn);
        logsBtn = root.findViewById(R.id.runLogsBtn);
        distanceView = root.findViewById(R.id.distanceData);
        averagePaceView = root.findViewById(R.id.averagePaceData);
        startBtn = root.findViewById(R.id.runStartBtn);
        statsBtn = root.findViewById(R.id.runStatBtn);

        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // initialise map fragment
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        // get nav controller
        controller = Navigation.findNavController(view);

        // set on click for button to bind this frag to tracking service
        startBtn.setOnClickListener(v -> {
            if (mService == null) {
                startService();
            }
        });
    }

    // start service onResume
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onResume() {
        super.onResume();
        //startService();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mViewModel.getBinder() != null) {
            getActivity().unbindService(mViewModel.getServiceConnection());
        }
    }

    // start service from this frag
    @RequiresApi(api = Build.VERSION_CODES.O)
        public void startService() {
        Intent serviceIntent = new Intent(requireContext(), TrackingService.class);
        getActivity().startService(serviceIntent);

        bindService();
    }

    // bind this frag to service
    private void bindService() {
        Intent serviceIntent = new Intent(requireContext(), TrackingService.class);
        getActivity().bindService(serviceIntent, mViewModel.getServiceConnection(), Context.BIND_AUTO_CREATE);
    }

    // opens run history fragment
    public void openRun(View v) {
        controller.navigate(R.id.action_startFrag_to_onRunFrag);
    }

    /* Map functionality */
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
            requestLocation();
        }
    };

    // begins tracking user location using service
    @SuppressLint("MissingPermission")
    public void enableUserLocation() {
        // will obtain user location and place marker on map
        if (mService != null) {
            mService.getPathPoints().observe(this, new Observer<ArrayList<ArrayList<LatLng>>>() {
                @Override
                public void onChanged(ArrayList<ArrayList<LatLng>> arrayLists) {
                    Log.i(TAG, "onChanged: Lat: " + arrayLists.get(0).get(0).latitude + " Long: " +
                            arrayLists.get(0).get(0).latitude);
                }
            });
        }
    }

    // requests location permissions from user
    public void requestLocation() {
        if (PermissionUtility.hasLocationPermission(requireContext())) {
            enableUserLocation();
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


}