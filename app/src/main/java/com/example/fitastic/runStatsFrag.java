package com.example.fitastic;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
 * Use the {@link runStatsFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class runStatsFrag extends Fragment {

    private DatabaseReference mDatabase;
    private FirebaseAuth auth;
    private LineChart mChart;
    private NavController controller;
    private String runid;

    private Button back_btn;

    ArrayList<Entry> pace = new ArrayList<>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public runStatsFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment runStatsFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static runStatsFrag newInstance(String param1, String param2) {
        runStatsFrag fragment = new runStatsFrag();
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
            Bundle b = getArguments();
            runid = b.getString("id");
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_run_stats, container, false);
        controller = Navigation.findNavController(container);
        back_btn = v.findViewById(R.id.back_btn);
        back_btn.setOnClickListener(v1->{
            controller.navigate(R.id.action_runStatsFrag_to_homeFrag);
        });
        System.out.println(runid);
//.child(String.valueOf(runid))
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference dist_ref = mDatabase.child("Users").child(auth.getCurrentUser().getUid()).child("Runs").child(runid);
        pace = new ArrayList();
        dist_ref.limitToLast(7).addValueEventListener(new ValueEventListener() {
            int counter = 0;
            double total_distance = 0;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                    pace.add(new Entry(counter, Float.valueOf(dataSnapshot.child("speed").getValue().toString())));
                    counter += 1;
                    total_distance = total_distance + Double.parseDouble(dataSnapshot.child("speed").getValue().toString());



                //CREATE CHART
                mChart = v.findViewById(R.id.line_chart);
                System.out.println(pace.size());
                LineDataSet set1 = new LineDataSet(pace, " Distance");

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

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Reaad Fail", "Error");
            }
        });


        return v;
    }
}