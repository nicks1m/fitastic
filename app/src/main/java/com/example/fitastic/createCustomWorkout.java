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

import java.lang.reflect.Array;
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
    private String custom_workout_title;
    private ValueEventListener mListener;
    private DatabaseReference ref;
    private LinearLayout layout_container;


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


        layout_container = v.findViewById(R.id.layout_container);

        //Initialize array to store EditTexts
        custom_workout_edt = new ArrayList<>();

        //Firebase Init
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //Receives bundle from previous page which contains workout name, hence look for that path and set path.
        ref = mDatabase.child("Users").child(auth.getCurrentUser().getUid()).child("custom_workout").child(custom_workout_title);
        //Single Value Event is used so it does not keep refreshing the data (This lead to duplication problems when trying to save data)
        ref.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot workout_snapshot : dataSnapshot.getChildren()){
                    //Create Exercises based on the child nodes under the path that was set.
                   addExercise(workout_snapshot.child("title").getValue().toString(),
                               workout_snapshot.child("reps").getValue().toString(),
                               workout_snapshot.child("set").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Reaad Fail", "Error");
            }

        });


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

        return v;
    }

    public void addExercise(String title, String set, String reps){
         //use array list to store EDTs and iterate to get values and push to firebasere

        //Create layout container for the three fields ( title, set, reps )
        LinearLayout layout_box = new LinearLayout(getContext());

        //Create the EditTexts for the three fields and add it to the linear layout

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

        //Add them to ArrayList so that we can copy them into another ArrayList to hold it in String format.
        custom_workout_edt.add(exercise_title);
        custom_workout_edt.add(exercise_set);
        custom_workout_edt.add(exercise_reps);

        //Add the EditTexts to the Layout container we created earlier
        layout_box.addView(exercise_title);
        layout_box.addView(exercise_set);
        layout_box.addView(exercise_reps);

        //Add to parent layout container
        layout_container.addView(layout_box);



    }

    public void saveExercises(){
        //Initialise ArrayList and push data from the EditText ArrayList to this ArrayList which holds a list of the <Exercise> objects
        custom_workout = new ArrayList<>();
        for(int i = 0; i < custom_workout_edt.size(); i++){
            String workout_title = custom_workout_edt.get(i).getText().toString();
            String workout_set = custom_workout_edt.get(i+1).getText().toString();
            String workout_reps = custom_workout_edt.get(i+2).getText().toString();
            Exercise newExercise = new Exercise(workout_title,workout_set,workout_reps);
            custom_workout.add(newExercise);
            i = i + 2;
        }

        //Push this list to the Firebase DB, for e.g if there are 3 exercises, there will be 3 child nodes, starting from 0 , 1 , 2.
        saveToFirebaseDB();

    }


    public void saveToFirebaseDB(){
        //Set path to the current title of custom Workout so it will create a new one if there isn't one that exists. (Will overwrite existing please note and implement
        //a function check.
        DatabaseReference ref = mDatabase.child("Users").child(auth.getCurrentUser().getUid()).child("custom_workout").child(edit_workout_title.getText().toString());
        ref.setValue(custom_workout);

    }


}