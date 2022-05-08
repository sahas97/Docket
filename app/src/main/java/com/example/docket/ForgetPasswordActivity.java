package com.example.docket;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ForgetPasswordActivity extends AppCompatActivity {

    // variables
    private TextInputEditText forget_email;
    ImageView back_btn;
    Button password_re_btn;

    FirebaseAuth fAuth; // fire base authentication

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);  // hide the statusBar
        setContentView(R.layout.activity_forget_password);

        // hide the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Hooks
        forget_email = findViewById(R.id.forget_password_email);
        back_btn = findViewById(R.id.forget_back_btn);
        password_re_btn = findViewById(R.id.password_recovery_button);

        fAuth = FirebaseAuth.getInstance();

        // when click on back arrow
        back_btn.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), GetStartActivity.class);
            startActivity(intent);
            finish();
        });

        // recover button press
        password_re_btn.setOnClickListener(view -> {
            String mail = Objects.requireNonNull(forget_email.getText()).toString().trim();

            // if user not entered email address
            if (mail.isEmpty()){
                Toast.makeText(getApplicationContext(), "Enter your mail first", Toast.LENGTH_SHORT).show();
            }else {
                // send recover link
                fAuth.sendPasswordResetEmail(mail).addOnCompleteListener(task -> {

                    // if verification is success
                    if(task.isSuccessful()){
                        Toast.makeText(getApplicationContext(),"Mail sent, now you can recover password using verification link which sent to your E mail account.", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(ForgetPasswordActivity.this, LoginActivity.class));
                    }
                    // if not
                    else{
                        Toast.makeText(getApplicationContext(),"Email is wrong or account doesn't exit. ", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
}