package com.example.docket;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    // Variables
    TextInputLayout signup_mail, signup_password;
    Button signup_btn, back_login_btn;
    ImageView get_start_btn;

    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);  // hide the statusBar
        setContentView(R.layout.activity_sign_up);

        // hide the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Hooks
        signup_mail = findViewById(R.id.email);
        signup_password = findViewById(R.id.password);
        signup_btn = findViewById(R.id.signup_Button);
        back_login_btn = findViewById(R.id.signup_login_btn);
        get_start_btn = findViewById(R.id.signup_back_btn);

        fAuth = FirebaseAuth.getInstance(); // fire base authentication

        // when clicking sighup button
        signup_btn.setOnClickListener(view -> {
            String mail = Objects.requireNonNull(signup_mail.getEditText()).getText().toString().trim();
            String password = Objects.requireNonNull(signup_password.getEditText()).getText().toString().trim();

            if (mail.isEmpty() || password.isEmpty()) {
                Toast.makeText(getApplicationContext(), "All fields are Required", Toast.LENGTH_SHORT).show();
            } else if (password.length() < 7) {
                Toast.makeText(getApplicationContext(), "Password should have minimum 8 characters or digits.", Toast.LENGTH_SHORT).show();
            } else {
                // register the user to firebase
                fAuth.createUserWithEmailAndPassword(mail, password).addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        // if registration is successfully go to the HomeActivity
                        Toast.makeText(getApplicationContext(), "Registration is Successfully", Toast.LENGTH_SHORT).show();
                        sentEmailVerification();
                    } else {
                        // if registration is failed error is showed as toast massage
                        Toast.makeText(SignUpActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // when clicking have an account
        back_login_btn.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        });

        // when clicking back arrow
        get_start_btn.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), GetStartActivity.class));
            finish();
        });

    }

    // send email verification method
    private void sentEmailVerification() {

        FirebaseUser firebaseUser = fAuth.getCurrentUser();

        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener(task -> {
                Toast.makeText(getApplicationContext(), "verification link is send to yor mail please check your inbox and verify it and Log In again.", Toast.LENGTH_SHORT).show();
                fAuth.signOut();
                finish();
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            });
        } else {
            Toast.makeText(getApplicationContext(), "Failed to send Verification Email. chek again and try.", Toast.LENGTH_SHORT).show();
        }
    }
}