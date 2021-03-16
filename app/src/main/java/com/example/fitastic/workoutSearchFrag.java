package com.example.fitastic;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.fitastic.diet.Recipe;
//import com.google.firebase.auth.ExportedUserRecord;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
//import com.google.firebase.auth.ListUsersPage;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
    private ArrayList<Workout> list;



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


//        ListUsersPage list = null;
//        try {
//            list = FirebaseAuth.getInstance().listUsers(null);
//        } catch (FirebaseAuthException e) {
//            e.printStackTrace();
//        }
//        while(list != null) {
//            int i = 0;
//            for (ExportedUserRecord user : list.getValues()) {
//                s[i] = user.getUid();
//                i++;
//            }
//            list = list.getNextPage();
//        }
//
//        ref = FirebaseDatabase.getInstance().getReference().child("Users").child(s[1]);


        mListener = ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                list = new ArrayList<Workout>();
//                System.out.println("does it get here?");
//                for(DataSnapshot workout_snapshot : snapshot.getChildren()){
//                    System.out.println(workout_snapshot.getKey());
//                    list.add(w);
//                }

                list = new ArrayList<>();

                HashMap favorites = (HashMap) snapshot.getValue();
                if (favorites != null) {
                    for (Object workout : favorites.keySet()) {
                        try {
                            String title = (String) snapshot.child(workout.toString()).getKey().toString();
                            Long sets = (Long) snapshot.child(workout.toString()).child("Sets").getValue();
                            Long reps = (Long) snapshot.child(workout.toString()).child("Reps").getValue();
                            list.add(new Workout(sets, reps, title));
                        } catch(NullPointerException e) {
                            System.out.println("DB ERR");
                        }
                    }
                }

                recyclerView = v.findViewById(R.id.recyclerViewWorkout11);

                if(list.isEmpty()){
                    System.out.println("DB ERR");
                } else {
                    if (getContext() == null) {
                        System.out.println("muie");
                    } else {
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        RecyclerViewAdapterWorkout adapter = new RecyclerViewAdapterWorkout((getContext()), list);
                        recyclerView.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Workout read Fail", "Error workout");
            }
        });



        return v;
    }


}