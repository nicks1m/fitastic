package com.example.fitastic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NavPage extends AppCompatActivity {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_page);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        navController = Navigation.findNavController(this,  R.id.fragment3);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        try {
            // if navPage destroyed onCreate called again, check intent
            if (getIntent().getExtras().getString("action") != null)
                checkAction(getIntent());
        } catch (java.lang.NullPointerException e) {

        }
    }

    // if navPage resumed onCreated not called, check intent action
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        checkAction(intent);
    }

    public void checkAction(Intent intent) {
        if (intent.getExtras().getString("action").equals("ACTION_SHOW_STARTFRAG")) {
            navController.navigate(R.id.action_global_to_startFrag);
        }
    }
}