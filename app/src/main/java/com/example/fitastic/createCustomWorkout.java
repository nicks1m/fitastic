package com.example.fitastic;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link createCustomWorkout#newInstance} factory method to
 * create an instance of this fragment.
 */
public class createCustomWorkout extends Fragment {

    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    private EditText edit_workout_title;
    private EditText exercise_title;
    private EditText exercise_reps;
    private EditText exercise_set;
    private Button add_exercise;
    private Button save_exercise;
    private List<EditText>custom_workout_edt;
    private List<Exercise>custom_workout;
    private List<String>custom_workout_firebase;
    private String custom_workout_title;
    private ValueEventListener mListener;
    private DatabaseReference ref;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public createCustomWorkout() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment createCustomWorkout.
     */
    // TODO: Rename and change types and number of parameters
    public static createCustomWorkout newInstance(String param1, String param2) {
        createCustomWorkout fragment = new createCustomWorkout();
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
            custom_workout_title = b.getString("custom_workout_title");
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_create_custom_workout, container, false);




        //Initialize array to store EditTexts, and another for Strings
        custom_workout_edt = new ArrayList<>();
        custom_workout = new ArrayList<>();
        custom_workout_firebase = new ArrayList<>();

        //Firebase Init
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        ref = mDatabase.child("Users").child(auth.getCurrentUser().getUid()).child("custom_workout").child(custom_workout_title);

        mListener = ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot workout_snapshot : dataSnapshot.getChildren())
                    for(DataSnapshot workout_snapshot2: workout_snapshot.getChildren())
                        //Adds Button with Title after retrieving data from firebase
                        custom_workout_firebase.add(workout_snapshot2.getValue().toString());
                System.out.println("Add complete!");
                System.out.println(custom_workout_firebase);
//                System.out.println(workout_snapshot2.getValue().toString());
//                    addExercise(workout_snapshot.getValue());

                //TODO:
                //Create array list and push children into it
                //Pass array list into bundle to next fragment to initialize respective data

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Reaad Fail", "Error");
            }


        });

//        ref.OnSuccessListener(new OnSuccessListener<Void>(){});


        edit_workout_title = v.findViewById(R.id.workout_title);
        edit_workout_title.setText(custom_workout_title);

        save_exercise = v.findViewById(R.id.btn_save_exercise);
        save_exercise.setOnClickListener(v1->{
            saveExercises();
        });

        add_exercise = v.findViewById(R.id.btn_add_exercise);
        add_exercise.setOnClickListener(v1->{
            addExercise("Name","Set","Reps");
        });




        loadExercises();
        return v;
    }

    public void addExercise(String title, String set, String reps){
         //use array list to store EDTs and iterate to get values and push to firebasere

//        LinearLayout layout = getView().findViewById(R.id.exercise_layout);

        LinearLayout layout_box = new LinearLayout(getContext());
        LinearLayout layout_container = getView().findViewById(R.id.layout_container);

        exercise_title = new EditText(getContext());
        exercise_title.setText(title);
        exercise_title.setEms(7);
        exercise_title.setTextColor(getResources().getColor(R.color.cyberYellow));
        exercise_set = new EditText(getContext());
        exercise_set.setText(set);
        exercise_set.setEms(7);
        exercise_set.setTextColor(getResources().getColor(R.color.cyberYellow));
        exercise_reps = new EditText(getContext());
        exercise_reps.setText(reps);
        exercise_reps.setEms(3);
        exercise_reps.setTextColor(getResources().getColor(R.color.cyberYellow));

        //Add to arraylist
        custom_workout_edt.add(exercise_title);
        custom_workout_edt.add(exercise_set);
        custom_workout_edt.add(exercise_reps);

        //Create LinearLayout to store
        layout_box.addView(exercise_title);
        layout_box.addView(exercise_set);
        layout_box.addView(exercise_reps);

        //Add to parent layout container
        layout_container.addView(layout_box);



    }

    public void loadExercises(){

        for(int i = 0; i < custom_workout_firebase.size(); i ++){
            System.out.println(custom_workout_firebase.get(i)+custom_workout_firebase.get(i+1)+custom_workout_firebase.get(i+2));
            addExercise(custom_workout_firebase.get(i),custom_workout_firebase.get(i+1),custom_workout_firebase.get(i+2));
            i += 2;
        }
    }

    public void saveExercises(){
        for(int i = 0; i < custom_workout_edt.size(); i++){
//            System.out.println(custom_workout_edt.get(i).getText().toString());
            String workout_title = custom_workout_edt.get(i).getText().toString();
            String workout_set = custom_workout_edt.get(i+1).getText().toString();
            String workout_reps = custom_workout_edt.get(i+2).getText().toString();
            Exercise newExercise = new Exercise(workout_title,workout_reps,workout_set);
            custom_workout.add(newExercise);
//            System.out.println(custom_workout.get(i).getTitle()+ custom_workout.get(i).getSet()+custom_workout.get(i).getReps());
            i = i + 2;
        }
        for(int i = 0; i < custom_workout.size();i++){
                        System.out.println(custom_workout.get(i).getTitle()+ custom_workout.get(i).getSet()+custom_workout.get(i).getReps());
        }

        saveToFirebaseDB();


    }

    public void saveToFirebaseDB(){

        DatabaseReference ref = mDatabase.child("Users").child(auth.getCurrentUser().getUid()).child("custom_workout").child(custom_workout_title);
        ref.setValue(custom_workout);

    }

    public void onDestroy() {
        //Remove event listener when frag is inactive to prevent async callbacks
        super.onDestroy();
        ref.removeEventListener(mListener);
    }

}