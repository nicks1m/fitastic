package com.example.fitastic;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link workout_in_prog_frag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class workout_in_prog_frag extends Fragment {

    private String custom_workout_title;
    private TextView workout_title;
    private TextView countdown;
    private Button start_countdown;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public workout_in_prog_frag() {
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
    public static workout_in_prog_frag newInstance(String param1, String param2) {
        workout_in_prog_frag fragment = new workout_in_prog_frag();
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

        View v = inflater.inflate(R.layout.fragment_workout_in_prog_frag, container, false);
        // Inflate the layout for this fragment
        workout_title = v.findViewById(R.id.wip_title);
        workout_title.setText(custom_workout_title);

        countdown = v.findViewById(R.id.timer);

        start_countdown = v.findViewById(R.id.start_timer);
        start_countdown.setOnClickListener(v1->{
            start_countdown.setVisibility(View.INVISIBLE);
            new CountDownTimer(30000, 1000) {

                public void onTick(long millisUntilFinished) {
                    countdown.setText("" + millisUntilFinished / 1000);
                }

                public void onFinish() {
                    countdown.setText("Finished");
                    start_countdown.setVisibility(View.VISIBLE);
                }
            }.start();
        });

        return v;
    }
}