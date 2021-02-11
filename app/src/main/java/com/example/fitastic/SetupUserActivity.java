package com.example.fitastic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

public class SetupUserActivity extends AppCompatActivity {

    private Button go_bmi;
    private EditText displayName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_user);

        go_bmi = findViewById(R.id.go_bmi);
        displayName = findViewById(R.id.displayName);


        go_bmi.setOnClickListener(v->{
            if(!TextUtils.isEmpty(displayName.getText())){
                //to-add : pass display name into firebase database
                openBMI();

            }
        });


    }

    public void openBMI(){
        Intent intent = new Intent(this, BMIActivity.class);
        startActivity(intent);
    }


}