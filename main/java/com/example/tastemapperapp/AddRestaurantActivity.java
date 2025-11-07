package com.example.tastemapperapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.textfield.TextInputLayout;

public class AddRestaurantActivity extends AppCompatActivity {

    private String language = "english";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_restaurant);

        // Load saved language
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        language = prefs.getString("language", "english");

        // Update all UI texts
        updateTexts();

        // Back button click
        findViewById(R.id.backButton).setOnClickListener(v -> onBackPressed());

        // Save button click
        findViewById(R.id.saveButton).setOnClickListener(v -> {
            showSlideNotification(getTextByLanguage(
                    "Save functionality coming soon!",
                    "Ukugcina kuza maduze!"
            ), true);
        });

        // Submit button click
        findViewById(R.id.submitButton).setOnClickListener(v -> {
            showSlideNotification(getTextByLanguage(
                    "Add Restaurant functionality coming soon!",
                    "Ukungeza iRestaurant kuza maduze!"
            ), true);
        });

        // Change location button click
        findViewById(R.id.changeLocationButton).setOnClickListener(v -> {
            showSlideNotification(getTextByLanguage(
                    "Map location picker coming soon!",
                    "Ukukhetha indawo kuMap kuza maduze!"
            ), true);
        });

        // Photo upload area click
        findViewById(R.id.photoContainer).setOnClickListener(v -> {
            showSlideNotification(getTextByLanguage(
                    "Photo upload coming soon!",
                    "Ukulayisha izithombe kuza maduze!"
            ), true);
        });
    }

    private void updateTexts() {


        ImageView backButton = findViewById(R.id.backButton);
        backButton.setContentDescription(getTextByLanguage("Back", "Emuva"));

        // Header save button
        TextView saveButton = findViewById(R.id.saveButton);
        saveButton.setText(getTextByLanguage("Save", "Gcina"));

        // Submit button
        Button submitButton = findViewById(R.id.submitButton);
        submitButton.setText(getTextByLanguage("Add Restaurant", "Faka iRestaurant"));

        // Change location button
        Button changeLocationButton = findViewById(R.id.changeLocationButton);
        changeLocationButton.setText(getTextByLanguage("Change Location on Map", "Shintsha Indawo kuMap"));

        // Form hints
        ((TextInputLayout)findViewById(R.id.nameInputLayout))
                .setHint(getTextByLanguage("Restaurant Name", "Igama LeRestaurant"));
        ((TextInputLayout)findViewById(R.id.cuisineInputLayout))
                .setHint(getTextByLanguage("Cuisine Type", "Uhlobo Lokudla"));
        ((TextInputLayout)findViewById(R.id.descriptionInputLayout))
                .setHint(getTextByLanguage("Description", "Incazelo"));
        ((TextInputLayout)findViewById(R.id.addressInputLayout))
                .setHint(getTextByLanguage("Full Address", "Ikheli Eliphelele"));

        // Section labels
        ((TextView)findViewById(R.id.pageTitle))
                .setText(getTextByLanguage("Add New Restaurant", "Faka iRestaurant Entsha"));
        ((TextView)findViewById(R.id.photoTitle))
                .setText(getTextByLanguage("Restaurant Photos", "Izithombe ZeRestaurant"));
        ((TextView)findViewById(R.id.photoSubtitle))
                .setText(getTextByLanguage("Add up to 5 photos of the restaurant", "Faka izithombe ezi-5 zeRestaurant"));
        ((TextView)findViewById(R.id.infoTitle))
                .setText(getTextByLanguage("Basic Information", "Ulwazi Oluyisisekelo"));
        ((TextView)findViewById(R.id.locationTitle))
                .setText(getTextByLanguage("Location", "Indawo"));
        ((TextView)findViewById(R.id.mapPreviewLabel))
                .setText(getTextByLanguage("Location on Map", "Indawo kuMap"));
    }

    private void showSlideNotification(String message, boolean success) {
        FrameLayout rootLayout = findViewById(android.R.id.content);
        Context context = AddRestaurantActivity.this;

        // CardView container
        CardView cardView = new CardView(context);
        cardView.setCardElevation(12);
        cardView.setRadius(16);
        cardView.setCardBackgroundColor(success ? Color.parseColor("#4CAF50") : Color.parseColor("#F44336"));
        cardView.setUseCompatPadding(true);

        // Text inside CardView
        TextView textView = new TextView(context);
        textView.setText(message);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(16);
        textView.setPadding(40, 30, 40, 30);
        textView.setGravity(Gravity.CENTER);
        cardView.addView(textView);

        // Layout params
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.TOP;
        params.setMargins(32, 80, 32, 0);
        cardView.setLayoutParams(params);

        cardView.setTranslationY(-300);
        cardView.setAlpha(0f);
        rootLayout.addView(cardView);

        // Slide-in animation
        cardView.animate()
                .translationY(0)
                .alpha(1f)
                .setDuration(400)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        // Auto-dismiss after 3 seconds
        new Handler().postDelayed(() -> cardView.animate()
                .translationY(-300)
                .alpha(0f)
                .setDuration(300)
                .withEndAction(() -> rootLayout.removeView(cardView))
                .start(), 3000);
    }

    private String getTextByLanguage(String english, String zulu) {
        return language.equals("zulu") ? zulu : english;
    }
}
