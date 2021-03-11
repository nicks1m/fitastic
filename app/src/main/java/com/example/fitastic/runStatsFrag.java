package com.example.fitastic;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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

import org.w3c.dom.Text;

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
    private TextView date;
    private String route;
    private String duration;
    private String speed;
    private String distance;
    private ImageView img_route;

    private TextView data_distance;
    private TextView data_duration;
    private TextView data_weather;
    private TextView data_speed;

    private Bitmap bitmap;


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

    @RequiresApi(api = Build.VERSION_CODES.O)
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
        //set image

        img_route = v.findViewById(R.id.route_img);

        DatabaseReference ref2 = mDatabase.child("Users").child(auth.getCurrentUser().getUid()).child("Runs").child(runid);

        ref2.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange( DataSnapshot dataSnapshot) {
                        route = dataSnapshot.child("bitmap").getValue().toString();
                        distance = dataSnapshot.child("distance").getValue().toString().substring(0,5);
                        duration = dataSnapshot.child("duration").getValue().toString();
                        speed = dataSnapshot.child("speed").getValue().toString();
                        bitmap = RunDbUtility.stringToBitmap(route);
                img_route.setImageBitmap(Bitmap.createScaledBitmap(bitmap,400,250,false));

                data_distance = v.findViewById(R.id.data_dist_run);
                data_distance.setText(RunDbUtility.calculateDistance(distance));
                data_duration = v.findViewById(R.id.data_duration_run);
                data_duration.setText(RunDbUtility.calculateDuration(duration));
                data_speed = v.findViewById(R.id.data_pace_run);
                data_speed.setText(RunDbUtility.calculatePace(distance,duration));
                data_weather = v.findViewById(R.id.data_weather_run);
            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        date = v.findViewById(R.id.label_date);
        String fdate = String.valueOf(RunDbUtility.convertEpochToDate(Long.valueOf(runid)));
        date.setText(fdate.replaceAll("GMT",""));





        DatabaseReference pace_ref = mDatabase.child("Users").child(auth.getCurrentUser().getUid()).child("Runs").child(runid).child("statDump");
        pace = new ArrayList();
        pace_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            int counter = 0;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    pace.add(new Entry(counter, Float.valueOf(data.child("Speed (min per km)").getValue().toString())));
                    counter += 1;
                }





                //CREATE CHART
                mChart = v.findViewById(R.id.line_chart);
                System.out.println(pace.size());
                LineDataSet set1 = new LineDataSet(pace, "Pace");

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
//                float td = (float)(total_distance/1000);
//                DecimalFormat df = new DecimalFormat("##.##");
//                df.setRoundingMode(RoundingMode.DOWN);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Reaad Fail", "Error");
            }
        });


        return v;
    }

//    public String calculatePace(String dist, String time){
//        double distkm = Double.parseDouble(dist)/1000;
//        double runtime = Double.parseDouble(time) / 60;
//        double dpace = runtime / distkm;
//        String pace = new DecimalFormat("#.##").format(dpace) + " min/km";
//        return pace;
//
//    }
//
//    public String calculateDistance(String dist){
//        double ddist = Double.parseDouble(dist)/1000;
//      String dist1 = new DecimalFormat("#.##").format(ddist) + " km";
//      return dist1;
//    }
//
//    public String calculateDuration(String time){
//
//        int totalSecs = Integer.parseInt(time);
//        int hours = totalSecs / 3600;
//        int minutes = (totalSecs % 3600) / 60;
//        int seconds = totalSecs % 60;
//        String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
//        return  timeString;

//    }
}