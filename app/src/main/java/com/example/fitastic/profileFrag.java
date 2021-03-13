package com.example.fitastic;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import androidx.fragment.app.Fragment;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.fitastic.utility.RunDbUtility;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link profileFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class profileFrag extends Fragment {

    private DatabaseReference mDatabase;
    private FirebaseAuth auth;
    private Button go_points;
    private Button logout;
    private String displayName;
    private TextView display_Name;
    private TextView data_7runs,data_7runs_pace,data_runs,data_longest,data_pace,data_dist;
    private NavController controller;
    private Button runlogs;
    private Button btn_friends;
    private Button go_settings;

    private int total_runs = 0;
    private double total_dist = 0;
    private double total_duration = 0;
    private double total_speed = 0;
    private double longest_run = 0;

    private ArrayList<Entry>distance_data;

    private LineChart mChart;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public profileFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment profileFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static profileFrag newInstance(String param1, String param2) {
        profileFrag fragment = new profileFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        display_Name = v.findViewById(R.id.display_name);
        logout = v.findViewById(R.id.btn_logout);
        go_points = v.findViewById(R.id.btn_points);
        runlogs = v.findViewById(R.id.btn_runLog);
        data_7runs = v.findViewById(R.id.data_7runs);
        data_7runs_pace = v.findViewById(R.id.data_7runs_pace);
        btn_friends = v.findViewById(R.id.btn_friends);
        go_settings = v.findViewById(R.id.btn_settings);

        data_runs  = v.findViewById(R.id.data_runs);
        data_dist  = v.findViewById(R.id.data_dist);
        data_longest  = v.findViewById(R.id.data_longest);
        data_pace  = v.findViewById(R.id.data_pace);




        controller = Navigation.findNavController(container);



        //Get display name associated with user id.
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //Get Stats of User
        DatabaseReference stats_ref = mDatabase.child("Users").child(auth.getCurrentUser().getUid()).child("Runs");
        stats_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot stat : snapshot.getChildren()){
                 total_runs += 1;
                 total_dist += Double.parseDouble(stat.child("distance").getValue().toString());
                 total_duration += Double.parseDouble(stat.child("duration").getValue().toString());
                 total_speed += Double.parseDouble(stat.child("speed").getValue().toString());
                 if(Double.parseDouble(stat.child("distance").getValue().toString()) > longest_run){
                     longest_run = Double.parseDouble(stat.child("distance").getValue().toString());
                 }
                }
                data_runs.setText(String.valueOf(total_runs));
                data_dist.setText(RunDbUtility.calculateDistance(String.valueOf(total_dist)));
                data_longest.setText((RunDbUtility.calculateDistance(String.valueOf(longest_run))));
                data_pace.setText(RunDbUtility.calculatePace(String.valueOf(total_dist),String.valueOf(total_duration)));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference ref = mDatabase.child("Users").child(auth.getCurrentUser().getUid()).child("display name");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String key = (String) dataSnapshot.getValue();
                //Load display name into TextView
                display_Name.setText(key);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Reaad Fail", "Error");
            }
        });

        go_points.setOnClickListener(v1 -> {
            pointsFrag points = new pointsFrag();
            openPoints(v1);
        });

        go_settings.setOnClickListener(v1 -> {
            SettingsFragment settings = new SettingsFragment();
            openSettings(v1);
        });

        //RUN LOG PAGE
        runlogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(profileFrag.this.getActivity(), ShowDataActivity.class));
            }
        });
        //END RUN LOG

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                signOut();
                startActivity(intent);
            }
        });

        btn_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), findFriends.class);
                startActivity(intent);
            }
        });







        //TODO OPTIMIZE GRAPHS, ADD ANOTHER CHART FOR PACE

        DatabaseReference dist_ref = mDatabase.child("Users").child(auth.getCurrentUser().getUid()).child("Runs");
        distance_data = new ArrayList();
        dist_ref.limitToLast(7).addValueEventListener(new ValueEventListener() {
            int counter = 0;
            double total_distance = 0;
            double total_time = 0;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dist_snapshot : dataSnapshot.getChildren()) {
                    distance_data.add(new Entry(counter, Float.parseFloat(dist_snapshot.child("distance").getValue().toString())/1000));
                    counter += 1;
                    total_distance = total_distance + Double.parseDouble(dist_snapshot.child("distance").getValue().toString());
                    total_time += Double.parseDouble(dist_snapshot.child("duration").getValue().toString());

                }

                //CREATE CHART
                mChart = v.findViewById(R.id.line_chart);
                System.out.println(distance_data.size());
                LineDataSet set1 = new LineDataSet(distance_data, " Distance");

                set1.setFillAlpha(110);
                set1.setLineWidth(2f);
                set1.setCircleColor(R.color.colorPrimary);
                set1.setColor(R.color.colorPrimary);
                set1.setValueTextSize(10f);
                ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                dataSets.add(set1);
                LineData data = new LineData(dataSets);
                mChart.setData(data);
                mChart.setScaleEnabled(true);
                mChart.getAxisLeft().setDrawGridLines(false);
                mChart.getXAxis().setDrawGridLines(false);
                mChart.getAxisRight().setDrawGridLines(false);
                mChart.getDescription().setEnabled(false);
                float td = (float)(total_distance/1000);
                DecimalFormat df = new DecimalFormat("##.##");
                df.setRoundingMode(RoundingMode.DOWN);
                data_7runs.setText("total dist. " + df.format(td) + " km");
                data_7runs_pace.setText("avg. pace " + RunDbUtility.calculatePace(String.valueOf(total_distance),String.valueOf(total_time)));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Reaad Fail", "Error");
            }
        });


//
//
//         for(int i = 0; i < 10; i++){
//             distance_data.add(new Entry(i,0f));
//         }

//       mChart.setDragEnabled(false);

        // Inflate the layout for this fragment
        return v;



    }

    public void openPoints(View v) {
        controller.navigate(R.id.action_profileFrag_to_pointsFrag);
    }

    public void openSettings(View v) {
        controller.navigate(R.id.action_profileFrag_to_settingsFragment);
    }

    private void signOut() {
        auth.signOut();
//        updateUI(null);
    }
}