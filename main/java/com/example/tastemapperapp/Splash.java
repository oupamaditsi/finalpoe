package com.example.tastemapperapp;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Get reference to the Get Started button
        Button getStartedBtn = findViewById(R.id.getStartedBtn);

        // Set a click listener
        getStartedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to MainActivity
                Intent intent = new Intent(Splash.this, Register.class);
                startActivity(intent);
                finish(); // Optional: remove splash from back stack
            }
        });
    }
}
