package com.example.fitastic;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class LogRunFragment extends Fragment {

    /* Log Run fragment provides users a fragment to map their runs with, this would be an instance
    of google maps which would need to remember the users start point of the run. Then a line would
    be drawn throughout the route of the run until the end point. Finally a prompt should be
    displayed showing stats such as average pace, distance, time ..
     */

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
            /* In future Latitudes and Longitudes would need to be retrieved from the users
             * location to map their run */
            // create a latitude and longitude object for london's location
            LatLng london = new LatLng(51.5074, - 0.1278);
            // add marker to googleMap instance
            googleMap.addMarker(new MarkerOptions().position(london).title("Marker in London"));
            // set position of camera to london marker, zoom factor of 20f
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(london, 20f));
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_log_run, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }
}