package com.example.fitastic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openRun(View v) {
        if (v.getId() == R.id.runButton) {
            Intent i = new Intent(this, RunActivity.class);
            startActivity(i);
        }
     }
    public void openLogin(View v) {
        if (v.getId() == R.id.loginButton) {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        }
    }
}