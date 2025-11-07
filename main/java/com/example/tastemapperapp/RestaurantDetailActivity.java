package com.example.tastemapperapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RestaurantDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);

        // Back button click
        findViewById(R.id.backButton).setOnClickListener(v -> {
            onBackPressed();
        });

        // Favorite button click
        findViewById(R.id.favoriteButton).setOnClickListener(v -> {
            Toast.makeText(this, "Added to favorites!", Toast.LENGTH_SHORT).show();
        });

        // Action buttons
        findViewById(R.id.callButton).setOnClickListener(v -> {
            Toast.makeText(this, "Call functionality coming soon!", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.directionsButton).setOnClickListener(v -> {
            Toast.makeText(this, "Directions functionality coming soon!", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.websiteButton).setOnClickListener(v -> {
            Toast.makeText(this, "Website functionality coming soon!", Toast.LENGTH_SHORT).show();
        });

        // Review buttons
        findViewById(R.id.writeReviewButton).setOnClickListener(v -> {
            Toast.makeText(this, "Write review functionality coming soon!", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.viewAllReviewsButton).setOnClickListener(v -> {
            Toast.makeText(this, "View all reviews functionality coming soon!", Toast.LENGTH_SHORT).show();
        });
    }
}