package com.example.fitastic;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import java.math.*;
import android.widget.Button;

public class BMIActivity extends AppCompatActivity {

    Button calculateBMI;
    private double userWeight;
    private double userHeight;
    private double weight;
    private double height;
    private double userBMI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b_m_i);

        //Get User Weight
        userWeight = Double.parseDouble(String.valueOf(findViewById(R.id.userWeight))) ;
        //Get User Height
        userHeight = Double.parseDouble(String.valueOf(findViewById(R.id.userHeight)));
        setWeight(userWeight);
        setHeight(userHeight);

        calculateBMI.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                userBMI = calculateBMI(getWeight(),getHeight());
            }
        } );
    }


    //gets weight and height and calculate BMI of user
    public double calculateBMI(double weight, double height){
        double bmi = 0;
        bmi = (weight/(Math.pow(height,2)))*10000;
        return bmi;
    }

    public double getWeight(){
        return this.weight;
    }

    public double getHeight(){
        return this.height;
    }

    public void setWeight(double weight){
        this.weight = weight;

    }

    public void setHeight(double height){
        this.height = height;
    }
}