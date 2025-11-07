package com.example.tastemapperapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private TextView userNameText, userEmailText, emailVerificationText, logoutButton;
    private TextInputEditText currentPasswordEditText, newPasswordEditText, confirmPasswordEditText;
    private MaterialButton changePasswordButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        initializeViews();
        setupClickListeners();
        loadUserData();
        setupBottomNavigation();
    }

    private void initializeViews() {
        userNameText = findViewById(R.id.userNameText);
        userEmailText = findViewById(R.id.userEmailText);
        emailVerificationText = findViewById(R.id.emailVerificationText);
        logoutButton = findViewById(R.id.logoutButton);

        currentPasswordEditText = findViewById(R.id.currentPasswordEditText);
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        changePasswordButton = findViewById(R.id.changePasswordButton);
        progressBar = findViewById(R.id.progressBar);

        currentPasswordEditText.setHint(getTextByLanguage("Current password", "Iphasiwedi yamanje"));
        newPasswordEditText.setHint(getTextByLanguage("New password", "Iphasiwedi entsha"));
        confirmPasswordEditText.setHint(getTextByLanguage("Confirm new password", "Qinisekisa iphasiwedi entsha"));

        TextView passwordRequirements = findViewById(R.id.passwordRequirements);
        passwordRequirements.setText(getTextByLanguage(
                "• At least 6 characters\n• Include letters and numbers",
                "• Okungenani izinhlamvu ezi-6\n• Faka izinhlamvu nezinombolo"
        ));

        changePasswordButton.setText(getTextByLanguage("Change Password", "Shintsha iphasiwedi"));
        logoutButton.setText(getTextByLanguage("Logout", "Phuma"));
    }

    private void setupClickListeners() {
        findViewById(R.id.backButton).setOnClickListener(v -> onBackPressed());
        logoutButton.setOnClickListener(v -> logoutUser());
        changePasswordButton.setOnClickListener(v -> changePassword());
    }

    private void loadUserData() {
        if (currentUser != null) {
            String displayName = currentUser.getDisplayName();
            userNameText.setText(displayName != null && !displayName.isEmpty() ?
                    displayName : getTextByLanguage("User", "Umsebenzisi"));
            userEmailText.setText(currentUser.getEmail());
            emailVerificationText.setText(currentUser.isEmailVerified() ?
                    getTextByLanguage("✓ Email Verified", "✓ I-imeyili iqinisekisiwe") :
                    getTextByLanguage("! Email Not Verified", "! I-imeyili ayiqinisekisiwe"));
        } else {
            Toast.makeText(this, getTextByLanguage("Please log in again", "Sicela ungene futhi"), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, Login.class));
            finish();
        }
    }

    private void changePassword() {
        String currentPassword = currentPasswordEditText.getText().toString().trim();
        String newPassword = newPasswordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(currentPassword)) {
            currentPasswordEditText.setError(getTextByLanguage("Current password is required", "Iphasiwedi yamanje iyadingeka"));
            currentPasswordEditText.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(newPassword)) {
            newPasswordEditText.setError(getTextByLanguage("New password is required", "Iphasiwedi entsha iyadingeka"));
            newPasswordEditText.requestFocus();
            return;
        }
        if (newPassword.length() < 6) {
            newPasswordEditText.setError(getTextByLanguage("Password must be at least 6 characters", "Iphasiwedi kumele ibe nezincwadi ezi-6 okungenani"));
            newPasswordEditText.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordEditText.setError(getTextByLanguage("Please confirm your new password", "Sicela uqinisekise iphasiwedi entsha"));
            confirmPasswordEditText.requestFocus();
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            confirmPasswordEditText.setError(getTextByLanguage("Passwords do not match", "Amaphasiwedi awafani"));
            confirmPasswordEditText.requestFocus();
            return;
        }

        showLoading(true);

        AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), currentPassword);
        currentUser.reauthenticate(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                currentUser.updatePassword(newPassword).addOnCompleteListener(task1 -> {
                    showLoading(false);
                    if (task1.isSuccessful()) {
                        Toast.makeText(ProfileActivity.this, getTextByLanguage(
                                "Password updated successfully!", "Iphasiwedi ibuyekeziwe ngempumelelo!"
                        ), Toast.LENGTH_SHORT).show();
                        clearPasswordFields();
                    } else {
                        Toast.makeText(ProfileActivity.this, getTextByLanguage(
                                "Failed to update password: ", "Iphasiwedi ayikwazanga ukubuyekezwa: "
                        ) + task1.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                showLoading(false);
                Toast.makeText(ProfileActivity.this, getTextByLanguage(
                        "Authentication failed: Invalid current password",
                        "Ukuqinisekisa kwehlulekile: Iphasiwedi yamanje ayivumelekile"
                ), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);

        bottomNavigation.getMenu().findItem(R.id.navigation_home)
                .setTitle(MainActivity.Translations.get(this, "nav_home"));
        bottomNavigation.getMenu().findItem(R.id.navigation_gallery)
                .setTitle(MainActivity.Translations.get(this, "nav_gallery"));
        bottomNavigation.getMenu().findItem(R.id.navigation_profile)
                .setTitle(MainActivity.Translations.get(this, "nav_profile"));

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_profile) {
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                return true;
            }
            return true;
        });
    }

    private void clearPasswordFields() {
        currentPasswordEditText.setText("");
        newPasswordEditText.setText("");
        confirmPasswordEditText.setText("");
    }

    private void logoutUser() {
        mAuth.signOut();
        Toast.makeText(this, getTextByLanguage("Logged out successfully", "Uphumile ngempumelelo"), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            changePasswordButton.setVisibility(View.INVISIBLE);
            changePasswordButton.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            changePasswordButton.setVisibility(View.VISIBLE);
            changePasswordButton.setEnabled(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            loadUserData();
        }
    }

    private String getTextByLanguage(String english, String zulu) {
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        String language = prefs.getString("language", "english");
        return language.equals("zulu") ? zulu : english;
    }
}
