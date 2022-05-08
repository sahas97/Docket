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

public class LoginActivity extends AppCompatActivity {

    // Variables
    TextInputLayout login_email, login_password;
    Button forget_password, login_btn, go_signup_btn;
    ImageView back_to_get_start;

    private FirebaseAuth fAuth; // firebase authentication

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);  // hide the statusBar
        setContentView(R.layout.activity_login);

        // hide the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // login hooks
        login_email = findViewById(R.id.email);
        login_password = findViewById(R.id.password);
        login_btn = findViewById(R.id.login_form_btn);
        forget_password = findViewById(R.id.forget_password_btn);
        go_signup_btn = findViewById(R.id.back_to_signup);
        back_to_get_start = findViewById(R.id.login_back_btn);

        fAuth = FirebaseAuth.getInstance();  //for accessing a Firebase Database get an instance by calling getInstance()
        FirebaseUser firebaseUser = fAuth.getCurrentUser(); // get the current user

        // if user already lodged in he or she will redirect to Home screen
        if (firebaseUser != null) {
            finish();
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }

        // clicking the login button
        login_btn.setOnClickListener(view -> {

            // Variables
            String mail = Objects.requireNonNull(login_email.getEditText()).getText().toString().trim();
            String password = Objects.requireNonNull(login_password.getEditText()).getText().toString().trim();


            // check the password and email field is empty
            if (mail.isEmpty() || password.isEmpty()) {
                Toast.makeText(getApplicationContext(), "All fields are Required", Toast.LENGTH_SHORT).show();
            } else {
                // if not login the user to firebase
                fAuth.signInWithEmailAndPassword(mail, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        checkMailVerification();
                    } else {
                        Toast.makeText(getApplicationContext(), "Yor haven't internet connection or\nAccount doesn't exit.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // when clicking create an account
        go_signup_btn.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
            finish();
        });

        // when clicking back arrow
        back_to_get_start.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), GetStartActivity.class));
            finish();
        });

        // when clicking forget password
        forget_password.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), ForgetPasswordActivity.class));
            finish();
        });

    }

    // checked the user is verify his or her email at the first time of installation
    private void checkMailVerification() {
        FirebaseUser firebaseUser = fAuth.getCurrentUser();

        // if user verified email go to home screen
        assert firebaseUser != null;
        if (firebaseUser.isEmailVerified()) {
            Toast.makeText(getApplicationContext(), "Logged In.", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        } else {
            // if not show the toast massage and sign out the user
            Toast.makeText(getApplicationContext(), "Verify your mail first", Toast.LENGTH_SHORT).show();
            fAuth.signOut();
        }
    }
}