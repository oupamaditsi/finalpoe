package com.example.tastemapperapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private Button btnLogin;
    private TextView tvRegisterLink;
    private EditText etEmail, etPassword;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        btnLogin = findViewById(R.id.btnLogin);
        tvRegisterLink = findViewById(R.id.tvRegisterLink);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        // Navigate to Register screen
        tvRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });

        // Handle login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
    }

    private void loginUser() {
        // Get input values
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        // Show loading state
        btnLogin.setText("Logging in...");
        btnLogin.setEnabled(false);

        // Sign in with Firebase Authentication
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Login successful
                            FirebaseUser user = mAuth.getCurrentUser();

                            if (user != null) {
                                // Check if email is verified
                                if (user.isEmailVerified()) {
                                    Toast.makeText(Login.this, "Login successful!", Toast.LENGTH_SHORT).show();

                                    // Navigate to Main Activity
                                    Intent intent = new Intent(Login.this, MainActivity.class);
                                    startActivity(intent);
                                    finish(); // Prevent going back to login screen
                                } else {
                                    // Email not verified
                                    Toast.makeText(Login.this, "Please verify your email address before logging in.", Toast.LENGTH_LONG).show();
                                    mAuth.signOut(); // Sign out until email is verified
                                    resetLoginButton();
                                }
                            }
                        } else {
                            // Login failed
                            String errorMessage = "Login failed";
                            if (task.getException() != null) {
                                errorMessage = task.getException().getMessage();
                                // User-friendly error messages
                                if (errorMessage.contains("invalid credential") || errorMessage.contains("password is invalid")) {
                                    errorMessage = "Invalid email or password";
                                } else if (errorMessage.contains("user not found")) {
                                    errorMessage = "No account found with this email";
                                } else if (errorMessage.contains("network error")) {
                                    errorMessage = "Network error. Please check your connection";
                                }
                            }
                            Toast.makeText(Login.this, errorMessage, Toast.LENGTH_LONG).show();
                            resetLoginButton();
                        }
                    }
                });
    }

    private void resetLoginButton() {
        btnLogin.setText("Login");
        btnLogin.setEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is already signed in and verified
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && currentUser.isEmailVerified()) {
            // User is already logged in and verified, redirect to main activity
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reset button state when returning to login screen
        resetLoginButton();
    }
}