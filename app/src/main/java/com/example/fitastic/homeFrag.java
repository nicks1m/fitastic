package com.example.fitastic;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.text.DateFormat;
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





        // Inflate the layout for this fragment
        return v;
    }
}