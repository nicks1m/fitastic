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
    private Button go_points;
    private FirebaseAuth auth;
    private Button logout;
    private String displayName;
    private TextView display_Name;
    private TextView data_runs;
    private NavController controller;
    private Button runlogs;

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
        data_runs = v.findViewById(R.id.data_7runs);

        controller = Navigation.findNavController(container);

        //Get display name associated with user id.
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

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


        DatabaseReference dist_ref = mDatabase.child("Users").child(auth.getCurrentUser().getUid()).child("Runs");
        distance_data = new ArrayList();
        dist_ref.limitToLast(7).addValueEventListener(new ValueEventListener() {
            int counter = 0;
            double total_distance = 0;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dist_snapshot : dataSnapshot.getChildren()) {
                    distance_data.add(new Entry(counter, Float.valueOf(dist_snapshot.child("distance").getValue().toString())));
                    counter += 1;
                    total_distance = total_distance + Double.parseDouble(dist_snapshot.child("distance").getValue().toString());

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
                data_runs.setText(df.format(td) + "km");
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

    private void signOut() {
        auth.signOut();
//        updateUI(null);
    }
}