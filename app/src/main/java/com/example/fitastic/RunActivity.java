package com.example.fitastic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

public class RunActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);

        LogRunFragment log = new LogRunFragment();
        RunHistoryFragment history = new RunHistoryFragment();

        // get log button
        findViewById(R.id.run_logBtn).setOnClickListener(v -> {// begin transaction
            FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
            ft1.replace(R.id.flPlaceholder, log);
            ft1.commit();
        });

        // get run history button
        findViewById(R.id.run_historyBtn).setOnClickListener(v -> {
            FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
            ft2.replace(R.id.flPlaceholder, history);
            ft2.commit();
        });
    }
}