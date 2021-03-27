package com.example.fitastic;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WorkoutInProgFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WorkoutInProgFrag extends Fragment {

    private String custom_workout_title;
    private TextView workout_title;
    private TextView ex_name;
    private TextView ex_reps;
    private EditText countdown;
    private Button start_countdown;
    private Button next_ex;
    private CountDownTimer cdown;
    private int seconds;
    private int current_ex;
    private String current_exs;
    private String totalEx;
    private int totalExercise;
    private ArrayList<String> list_of_exercises;
    private TextView label_ex;
    private int current_set;
    private TextView label_set;
    private NavController controller;
    private ArrayList prepArray;
    private ProgressBar prog_bar;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public WorkoutInProgFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment workout_in_prog_frag.
     */
    // TODO: Rename and change types and number of parameters
    public static WorkoutInProgFrag newInstance(String param1, String param2) {
        WorkoutInProgFrag fragment = new WorkoutInProgFrag();
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
            list_of_exercises = new ArrayList<>();
            list_of_exercises = b.getStringArrayList("array");
            custom_workout_title = b.getString("custom_workout_title");
            totalEx = b.getString("exercises");
            totalExercise = Integer.parseInt(totalEx) + 1;
            totalEx = String.valueOf(totalExercise);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_workout_in_prog_frag, container, false);

        controller = Navigation.findNavController(container);
        seconds = 60;
        prog_bar = v.findViewById(R.id.progress_bar_workout);
        ex_name = v.findViewById(R.id.exercise_name);
        ex_name.setText(list_of_exercises.get(1));

        current_set = 1;
        label_set = v.findViewById(R.id.label_set);
        label_set.setText("SET " + current_set + "/" + list_of_exercises.get(2));

        ex_reps = v.findViewById(R.id.label_reps);
        ex_reps.setText("REPS: " + list_of_exercises.get(3));

        current_exs = list_of_exercises.get(0);
        current_ex = Integer.parseInt(current_exs) + 1;
        label_ex = v.findViewById(R.id.label_exercise);
        label_ex.setText("Exercise " + current_ex + "/" + totalEx);


        next_ex = v.findViewById(R.id.next_exercise);
        next_ex.setVisibility(View.INVISIBLE);

        workout_title = v.findViewById(R.id.wip_title);
        workout_title.setText(custom_workout_title);

        countdown = v.findViewById(R.id.timer);

        start_countdown = v.findViewById(R.id.start_timer);

        start_countdown.setOnClickListener(v1->{
            seconds = (Integer.parseInt(countdown.getText().toString()))*1000;
            double secondss = Double.parseDouble(countdown.getText().toString()) * 1000;
            start_countdown.setVisibility(View.INVISIBLE);
            cdown = new CountDownTimer(seconds, 1000) {

                public void onTick(long millisUntilFinished) {
                    countdown.setText("" + millisUntilFinished / 1000);
                    updateProgressBar((millisUntilFinished/secondss)*100);

                }

                public void onFinish() {

                    //TODO:play audio to signify end of first set.
                    countdown.setText("60");
                    updateProgressBar(0);
                    start_countdown.setVisibility(View.VISIBLE);
                    current_set = current_set +1;

                    if(checkComplete(current_set,Integer.parseInt(list_of_exercises.get(2)))){
                    ex_name.setText("Complete!");
                    label_set.setVisibility(View.INVISIBLE);
                    label_ex.setVisibility(View.INVISIBLE);
                    ex_reps.setVisibility(View.INVISIBLE);
                    start_countdown.setVisibility(View.INVISIBLE);
                    next_ex.setVisibility(View.VISIBLE);
                    next_ex.setOnClickListener(v->{
                            prepArray = prepArray();
                        if(checkIfArrayEmpty(prepArray)){
                            System.out.println("Workout complete");
                           controller.navigate(R.id.action_workout_in_prog_frag_to_workoutFrag);
                        } else {
                            System.out.println("Moving to next exercise");
                            Bundle args = new Bundle();
                            args.putString("exercises", list_of_exercises.get(list_of_exercises.size() - 4));
                            args.putStringArrayList("array", prepArray);
                            args.putString("custom_workout_title", custom_workout_title);
                            controller.navigate(R.id.action_workout_in_prog_frag_self, args);

                        }

                    });

                    } else
                    label_set.setText("SET " + current_set + "/" + list_of_exercises.get(2));
                }
            }.start();
        });

        return v;
    }

    private void updateProgressBar(double percent){
        prog_bar.setProgress((int)percent);
    }

    public boolean checkIfArrayEmpty(ArrayList prepArray){
        if(prepArray.isEmpty()){
            return true;
        }
        return false;
    }

    public ArrayList prepArray(){
        //loop array <4 remove exercise and pass thru bundle
        for(int i = 0; i < 4; i ++){
            System.out.println("removing : " + list_of_exercises.get(0));
            list_of_exercises.remove(0);

        }
        return list_of_exercises;
    }

    public boolean checkComplete(int current, int total){
        if(current == total + 1){
            return true;
        }
        return false;
    }
}

//Method to navigate between exercises
//Pull ArrayList<String> of exercises, for loop i  < 4 (for first excercise)
//Once first exercise is done, remove from Array, pass array to bundle. Array should be short of one exercise by now.
//Repeat and use a check to see if Array is null. If array is null, go to completion page saying workout completed !