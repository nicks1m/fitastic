package com.example.fitastic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    private TextView homemsg;
    private TextView recentDate;
    private TextView recentDistance;
    private TextView recentPace;
    private TextView recentTime;

    private TextView recentDate2;
    private TextView recentDistance2;
    private TextView recentPace2;
    private TextView recentTime2;

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
        recentDate = v.findViewById(R.id.dateview);
        recentDistance = v.findViewById(R.id.distanceview);
        recentPace = v.findViewById(R.id.paceview);
        recentTime = v.findViewById(R.id.timeview);

        recentDate2 = v.findViewById(R.id.dateview2);
        recentDistance2 = v.findViewById(R.id.distanceview2);
        recentPace2 = v.findViewById(R.id.paceview2);
        recentTime2 = v.findViewById(R.id.timeview2);

        recycleBtn = v.findViewById(R.id.recycleBtn);




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


        recycleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(homeFrag.this.getActivity(), ShowDataActivity.class));
            }
        });


        DatabaseReference ref2 = mDatabase.child("Users").child(auth.getCurrentUser().getUid()).child("run_history");

        ref2.limitToFirst(2).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot) {

                for (DataSnapshot workout_snapshot : dataSnapshot.getChildren()) {
                    String RADate = workout_snapshot.child("date").getValue().toString();
                    String RADistance = workout_snapshot.child("distance").getValue().toString();
                    String RAPace = workout_snapshot.child("pace").getValue().toString();
                    String RATime = workout_snapshot.child("time").getValue().toString();


                    recentDate.setText(RADate);
                    recentDistance.setText(RADistance);
                    recentPace.setText(RAPace);
                    recentTime.setText(RATime);

                    break;



                }

                for (DataSnapshot workout_snapshot : dataSnapshot.getChildren()) {



                    String RADate2 = workout_snapshot.child("date").getValue().toString();
                    String RADistance2 = workout_snapshot.child("distance").getValue().toString();
                    String RAPace2 = workout_snapshot.child("pace").getValue().toString();
                    String RATime2 = workout_snapshot.child("time").getValue().toString();

                    recentDate2.setText(RADate2);
                    recentDistance2.setText(RADistance2);
                    recentPace2.setText(RAPace2);
                    recentTime2.setText(RATime2);

                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });



        // Inflate the layout for this fragment
        return v;
    }
}