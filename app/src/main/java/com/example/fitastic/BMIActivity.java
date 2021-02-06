package com.example.fitastic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import java.math.*;
import android.widget.Button;
import android.widget.Toast;

public class BMIActivity extends AppCompatActivity {

    Button calculateBMI;
    private EditText textUserWeight;
    private EditText textUserHeight;
    private double weight;
    private double height;
    private double userBMI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b_m_i);

        //BMI button init
        calculateBMI = (Button) findViewById(R.id.calculate_bmi);

        calculateBMI.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //To implement once able to open fragment from bmi activity. Numberformatexception being thrown, resolve it.
                // Use bundle to send data to next fragment

                // Toast.maketoast Error if fields are blank




                initializeDetails();

                if(weight <= 0 || height <= 0 || textUserWeight == null || textUserHeight == null ){
                    Context context = getApplicationContext();
                    CharSequence text = "Input is invalid, please enter an appropriate Height and Weight";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    return;
                }

                calculateBMI(getWeight(),getHeight());
                //Create Fragment, bundle (to pass data to fragment)
                BMIFrag bmi = new BMIFrag();
                Bundle bundle = new Bundle();
                bundle.putDouble("BMI", userBMI);
                bmi.setArguments(bundle);
                //Begin Fragment Transact
                androidx.fragment.app.FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                ft1.replace(R.id.bmiFlplaceholder, bmi);
                ft1.commit();
            }
        } );
    }

    public void initializeDetails(){
        //Get User Weight
        try {
            textUserWeight = (findViewById(R.id.userWeight));
            weight = Double.parseDouble(textUserWeight.getText().toString());
            //Get User Height
            textUserHeight = (findViewById(R.id.userHeight));
            height = Double.parseDouble(textUserHeight.getText().toString());
            setWeight(weight);
            setHeight(height);
        } catch(NumberFormatException e){

        }

    }


    //gets weight and height and calculate BMI of user
    public void calculateBMI(double weight, double height){
        double bmi = 0;
        bmi = (weight/(Math.pow(height,2)))*10000;
        userBMI = bmi;
        System.out.println(userBMI);
    }





    //getters and setters

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