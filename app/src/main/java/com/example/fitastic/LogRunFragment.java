package com.example.fitastic;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LogRunFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LogRunFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LogRunFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LogRunFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LogRunFragment newInstance(String param1, String param2) {
        LogRunFragment fragment = new LogRunFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    // validate log form
    public boolean validateForm(int speed, int distance, int time) {
        // check if input is empty


        return true;
    }

    public boolean isInputEmpty(int x) {
        return false;
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
        return inflater.inflate(R.layout.fragment_log_run, container, false);
    }
    // executes after views for fragment has been created
    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        // add onClick listener to submit button within log form
        ((Button) v.findViewById(R.id.btnSubmitLog)).setOnClickListener(x -> {
            int speed = R.id.tvAvgSpeed;
            int distance = R.id.tvDistance;
            int time = R.id.tvTime;

            // validate inputs
            if (validateForm(speed, distance, time)) {
                // parse data to sql from here
            }
        });

    }
}