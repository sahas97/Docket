package com.example.docket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    // Variables
    SharedPreferences OnBoardingScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);  // hide the statusBar
        setContentView(R.layout.activity_main);
        // hide the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        new Handler().postDelayed(() -> {

            OnBoardingScreen = getSharedPreferences("OnBoardingScreen", MODE_PRIVATE);
            boolean isFirstTime = OnBoardingScreen.getBoolean("firstTime", true);

            // if user is installing app first time show on board screen
            if (isFirstTime){
                SharedPreferences.Editor editor = OnBoardingScreen.edit();
                editor.putBoolean("firstTime", false);
                editor.apply();

                // in hear i use version check because some phone doesn't support slider animation in on bord screen
                if(android.os.Build.VERSION.SDK_INT > 25){
                    // Code For Android Version higher than Android 7.1 go to onboard screen
                    startActivity(new Intent(MainActivity.this,OnboardScreenActivity.class));
                } else{
                    // Code For Android Version lower than Android 7.1 go to start activity
                    startActivity(new Intent(MainActivity.this,GetStartActivity.class));
                }
                // if not first time go to login activity
            }else {
                 Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                 startActivity(intent);
            }

            finish();

        },2000);
    }
}