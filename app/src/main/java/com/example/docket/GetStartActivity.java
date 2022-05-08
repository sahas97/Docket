package com.example.docket;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class GetStartActivity extends AppCompatActivity {

    // variables
    Button signUp, logIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);  // hide the statusBar
        setContentView(R.layout.activity_get_start);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide(); // hide the action bar
        }

        // Hooks
        logIn = findViewById(R.id.login_btn);
        signUp = findViewById(R.id.signup_btn);

        // when clicking sign up
        signUp.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
            finish();
        });

        // when clicking log in
        logIn.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        });

    }
}