package com.example.fitastic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link homeFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class homeFrag extends Fragment {

    private TextView time;
    private TextView date;
    private TextView dpoints;
    private TextView homemsg;

    private LinearLayout layoutcontainer;

    private TextView recentDate;
    private TextView recentDistance;
    private TextView recentPace;
    private TextView recentTime;

    private NavController controller;



    private Button recycleBtn;

    private FirebaseAuth auth;
    private DatabaseReference mDatabase;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public homeFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FirstFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static homeFrag newInstance(String param1, String param2) {
        homeFrag fragment = new homeFrag();
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
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        homemsg = v.findViewById(R.id.home_msg);
        date = v.findViewById(R.id.data_date);
        time = v.findViewById(R.id.data_time);
        dpoints = v.findViewById(R.id.data_points);
        layoutcontainer = v.findViewById(R.id.scrollcontainer);

        controller = Navigation.findNavController(container);
//        recentDate = v.findViewById(R.id.dateview);
//        recentDistance = v.findViewById(R.id.distanceview);
//        recentPace = v.findViewById(R.id.paceview);
//        recentTime = v.findViewById(R.id.timeview);




        String mydate = DateFormat.getDateInstance().format(Calendar.DATE);
        Date mytime = Calendar.getInstance().getTime();
        String strDate = DateFormat.getDateInstance(DateFormat.MEDIUM).format(mytime);
        String strTime = DateFormat.getTimeInstance(DateFormat.SHORT).format(mytime);
        date.setText(strDate);
        time.setText(strTime);


        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = mDatabase.child("Users").child(auth.getCurrentUser().getUid()).child("display name");

         ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String key = (String) dataSnapshot.getValue();
                //Load display name into TextView
                String msg = "welcome, " + key;
                homemsg.setText(msg);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Reaad Fail", "Error");
            }
        });

        DatabaseReference pointsRef = mDatabase.child("Users").child(auth.getCurrentUser().getUid()).child("points").child("tierpoints");

        pointsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String key;
                if(dataSnapshot.getValue() == null){
                    key = "null";
                }
                else {
                    key = String.valueOf(dataSnapshot.getValue());
                }
                //Load points into TextView
                dpoints.setText(key);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Reaad Fail", "Error");
            }
        });

        DatabaseReference ref2 = mDatabase.child("Users").child(auth.getCurrentUser().getUid()).child("Runs");

        ref2.limitToFirst(6).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot) {

                for (DataSnapshot run_snapshot : dataSnapshot.getChildren()) {
                    addRun(run_snapshot.getKey(),
                            run_snapshot.child("distance").getValue().toString().substring(0,5),
                            run_snapshot.child("duration").getValue().toString(),
                            run_snapshot.child("speed").getValue().toString());
                }
             }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





        // Inflate the layout for this fragment
        return v;
    }

    public void addRun(String date, String distance, String duration, String pace){

        LinearLayout layout_box = new LinearLayout(getContext());
        layout_box.setMinimumHeight(500);
        layout_box.setPadding(0,0,0,100);
        layout_box.setBackgroundColor(getResources().getColor(R.color.challengeGrey));
        //epoch / timestamp
        recentDate = new TextView(getContext());
        recentDate.setText(date);
        recentDate.setPadding(20,10,20,10);

        //duration seconds
        recentTime = new TextView(getContext());
        recentTime.setEms(3);
        recentTime.setText(duration + "s");
        //distance is metres
        recentDistance = new TextView(getContext());
        recentDistance.setEms(4);
        recentDistance.setText(distance + "km");
        //speed is metres per second
        recentPace = new TextView(getContext());
        recentPace.setEms(3);
        recentPace.setText(pace + "m/s");


        layout_box.addView(recentDate);
        layout_box.addView(recentDistance);
        layout_box.addView(recentTime);
        layout_box.addView(recentPace);
        layout_box.setOnClickListener(v->{
            Bundle args = new Bundle();
            args.putString("id", date);
            controller.navigate(R.id.action_homeFrag_to_runStatsFrag, args);
        });

        layoutcontainer.addView(layout_box);
    }
}