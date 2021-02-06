package com.example.fitastic;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DecimalFormat;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BMIFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BMIFrag extends Fragment {

    private TextView bmiText;
    private TextView bmiMsg;
    private String strBmi;
    private double BMI;
    private Button go_nav;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BMIFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BMIFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static BMIFrag newInstance(String param1, String param2) {
        BMIFrag fragment = new BMIFrag();
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
        View v = inflater.inflate(R.layout.fragment_b_m_i, container, false);

        //Get "BMI" from activity
        BMI = getArguments().getDouble("BMI");
        //Get BMI (2dp) convert to string
        DecimalFormat f = new DecimalFormat("##.0");
        strBmi = "" + f.format(BMI);
        bmiText = (TextView)v.findViewById(R.id.bmiText);
        bmiMsg = (TextView)v.findViewById(R.id.bmiMsg);
        bmiText.setText(strBmi);
        bmiMsg.setText(bmiRange(BMI));
        //Doesnt work not sure why..
//        bmiText.setText(Double.toString(BMI));


        go_nav = v.findViewById(R.id.go_nav);
        go_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),NavPage.class);
                startActivity(intent);
            }
        });

        return v;
    }

    //If else healthy range
    public String bmiRange(double BMI) {
        if (BMI > 30.0) {
            return "You are Obese!";
        } else if (BMI > 25.0 && BMI < 29.9) {
            return "You are Overweight!";
        } else if (BMI > 18.5 && BMI < 24.9) {
            return "You are Healthy!";
        } else {
            return "You are Underweight!";
        }
    }





}