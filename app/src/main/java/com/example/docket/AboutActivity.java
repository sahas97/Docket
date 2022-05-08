package com.example.docket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

public class AboutActivity extends AppCompatActivity {

    // Variables
    public Button okBtn;
    ImageView backHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);  // hide the statusBar
        setContentView(R.layout.activity_about);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        backHome = findViewById(R.id.about_back_btn);
        okBtn = findViewById(R.id.ok_button);

        // ok btn press
        okBtn.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
        });

        // back arrow press
        backHome.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
        });
    }

}