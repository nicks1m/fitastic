package com.example.fitastic;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link createCustomWorkout#newInstance} factory method to
 * create an instance of this fragment.
 */
public class createCustomWorkout extends Fragment {

    private EditText exercise_title;
    private EditText exercise_reps;
    private EditText exercise_set;
    private Button add_exercise;
    private Button save_exercise;
    private List<EditText>custom_workout;

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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_create_custom_workout, container, false);
        custom_workout = new ArrayList<>();
        save_exercise = v.findViewById(R.id.btn_save_exercise);
        save_exercise.setOnClickListener(v1->{
            saveExercises();
        });

        add_exercise = v.findViewById(R.id.btn_add_exercise);
        add_exercise.setOnClickListener(v1->{
            addExercise();
        });
        return v;
    }

    public void addExercise(){
//use array list to store EDTs and iterate to get values and push to firebasere

//        LinearLayout layout = getView().findViewById(R.id.exercise_layout);

        LinearLayout layout_box = new LinearLayout(getContext());
        LinearLayout layout_container = getView().findViewById(R.id.layout_container);

        exercise_title = new EditText(getContext());
        exercise_title.setText("Title");
        exercise_title.setEms(7);
        exercise_title.setTextColor(getResources().getColor(R.color.cyberYellow));
        exercise_set = new EditText(getContext());
        exercise_set.setText("Set");
        exercise_set.setEms(7);
        exercise_set.setTextColor(getResources().getColor(R.color.cyberYellow));
        exercise_reps = new EditText(getContext());
        exercise_reps.setText("Reps");
        exercise_reps.setEms(3);
        exercise_reps.setTextColor(getResources().getColor(R.color.cyberYellow));

        //Add to arraylist
        custom_workout.add(exercise_title);
        custom_workout.add(exercise_set);
        custom_workout.add(exercise_reps);

        //Create LinearLayout to store
        layout_box.addView(exercise_title);
        layout_box.addView(exercise_set);
        layout_box.addView(exercise_reps);

        //Add to parent layout container
        layout_container.addView(layout_box);



    }

    public void saveExercises(){
        for(int i = 0; i < custom_workout.size(); i++){
            System.out.println(custom_workout.get(i).getText().toString());
        }

        //Create new ArraylIST that stores <Exercise> and push editText into it before firebase???


    }

}