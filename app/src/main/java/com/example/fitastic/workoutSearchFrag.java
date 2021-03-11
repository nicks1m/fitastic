package com.example.fitastic;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link workoutSearchFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class workoutSearchFrag extends Fragment {

    private RecyclerView recyclerView;
    private String title;
    private TextView header;
    private DatabaseReference ref;
    private ValueEventListener mListener;
    private ArrayList<String> list = new ArrayList<String>();



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public workoutSearchFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment workoutSearchFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static workoutSearchFrag newInstance(String param1, String param2) {
        workoutSearchFrag fragment = new workoutSearchFrag();
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
            title = b.getString("ex");
            System.out.println(title);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_workout_search, container, false);


        header = v.findViewById(R.id.textViewRelated);
        header.setText("" + title + " related workouts");

        ref = FirebaseDatabase.getInstance().getReference().child("Workout").child(title);

        mListener = ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                System.out.println("does it get here?");
                for(DataSnapshot workout_snapshot : snapshot.getChildren()){
                    System.out.println(workout_snapshot.getKey());
                    list.add(workout_snapshot.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Workout read Fail", "Error workout");
            }
        });

        recyclerView = v.findViewById(R.id.recyclerViewWorkout);

        if(list.isEmpty()){
            System.out.println("DB ERR");
        } else {
            recyclerView.setAdapter(new RecyclerViewAdapterWorkout(getContext(), list));
        }

        return v;
    }
}