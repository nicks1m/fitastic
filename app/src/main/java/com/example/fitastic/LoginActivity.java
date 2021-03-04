package com.example.fitastic;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;


public class LoginActivity extends AppCompatActivity {

    //Create Buttons for page
    Button regButton;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        regButton = (Button) findViewById(R.id.btn_goto_register);
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewActivity();
            }
        });
        loginButton = (Button) findViewById(R.id.btn_login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewActivity2();
            }
        });
    }

    //Opens Register Activity Page
    public void openNewActivity(){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    //Opens Home Activity Page
    public void openNewActivity2(){
        Intent intent = new Intent(this, NavPage.class);
        startActivity(intent);
    }

}

