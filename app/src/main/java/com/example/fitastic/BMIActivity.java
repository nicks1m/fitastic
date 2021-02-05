package com.example.fitastic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
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

        calculateBMI = (Button) findViewById(R.id.calculate_bmi);

        calculateBMI.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //To implement once able to open fragment from bmi activity. Numberformatexception being thrown, resolve it.
                // Use bundle to send data to next fragment

                //initializeDetails();
                //userBMI = calculateBMI(getWeight(),getHeight());
                BMIFrag bmi = new BMIFrag();
                androidx.fragment.app.FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                ft1.replace(R.id.bmiFlplaceholder, bmi);
                ft1.commit();
            }
        } );
    }

    public void initializeDetails(){
        //Get User Weight
        userWeight = Double.parseDouble(String.valueOf(findViewById(R.id.userWeight))) ;
        //Get User Height
        userHeight = Double.parseDouble(String.valueOf(findViewById(R.id.userHeight)));
        setWeight(userWeight);
        setHeight(userHeight);
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