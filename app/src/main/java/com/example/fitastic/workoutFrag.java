package com.example.fitastic;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link workoutFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class workoutFrag extends Fragment {

    ArrayList<Button>btnList;
    private Button btn_add;
    private Button custom_workout;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public workoutFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SecondFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static workoutFrag newInstance(String param1, String param2) {
        workoutFrag fragment = new workoutFrag();
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
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_workout, container, false);


        loadCustomWorkout();


        btn_add =  v.findViewById(R.id.add_workout);
        btn_add.setOnClickListener(v1 -> {
           addButton("custom workout");
        });
        return v;
    }

    public void addButton(String title){
        LinearLayout layout = getView().findViewById(R.id.workout_layout);
        custom_workout = new Button(getContext());
        custom_workout.setText(title);
        layout.addView(custom_workout);

    }

    public void loadCustomWorkout(){
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        DatabaseReference ref = mDatabase.child("Users").child(auth.getCurrentUser().getUid()).child("custom_workout");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot workout_snapshot : dataSnapshot.getChildren())
                    //Adds Button with Title after retrieving data from firebase
                    addButton(workout_snapshot.getKey());
                //TODO:
                //Create array list and push children into it
                //Pass array list into bundle to next fragment to initialize respective data
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Reaad Fail", "Error");
            }
        });
    }

}

//For Reference only
//    FirebaseDatabase database = FirebaseDatabase.getInstance();
//    DatabaseReference myRef = database.getReference();
//myRef.addValueEventListener(new ValueEventListener() {
//@Override
//public void onDataChange(DataSnapshot dataSnapshot) {
//        for(DataSnapshot item_snapshot:dataSnapshot.getChildren()) {
//
//        Log.d("item id ",item_snapshot.child("item_id").getValue().toString());
//        Log.d("item desc",item_snapshot.child("item_desc").getValue().toString());
//        }
//        }
//        }