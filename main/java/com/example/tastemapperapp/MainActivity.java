package com.example.tastemapperapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Gravity;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private FirebaseAuth mAuth;
    private final Handler connectionHandler = new Handler();
    private boolean wasConnected = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Setup map
        setupMap();

        // Setup click listeners
        setupClickListeners();

        // Setup bottom navigation
        setupBottomNavigation();
        startConnectionMonitor();

    }

    private void setupMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;

        // Configure map settings
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.setOnMarkerClickListener(this);

        // Check location permission
        if (hasLocationPermission()) {
            enableMyLocation();
            moveToUserLocation();
        } else {
            requestLocationPermission();
        }

        // Add sample restaurants (will be replaced with Firestore data later)
        addSampleRestaurants();
    }

    private void addSampleRestaurants() {

        addRestaurantMarker(-26.2041, 28.0473, "The Test Kitchen", "Fine Dining • R1200");
        addRestaurantMarker(-26.1950, 28.0346, "Moyo Zoo Lake", "African Cuisine • R250");
        addRestaurantMarker(-26.2096, 28.0402, "Trattoria Milano", "Italian • R350");
        addRestaurantMarker(-26.1985, 28.0531, "Nando's Rosebank", "Portuguese • R150");
        addRestaurantMarker(-26.1449, 28.0369, "The Grillhouse", "Steakhouse • R550");

        addRestaurantMarker(-26.2100, 28.0450, "The Wing Republic", "Wings & Casual Dining • R120");
        addRestaurantMarker(-26.2045, 28.0420, "Salvation Café", "Cafe • R80");
        addRestaurantMarker(-26.2070, 28.0410, "Locos Restaurant", "Mexican Cuisine • R200");


        addRestaurantMarker(-26.2050, 28.0500, "The Artivist", "Modern Cuisine • R400");
        addRestaurantMarker(-26.1965, 28.0380, "RocoGo Braamfontein", "Coffee & Snacks • R60");
        addRestaurantMarker(-26.2020, 28.0490, "The Devonshire Corner Restaurant", "Casual Dining • R250");
        addRestaurantMarker(-26.2085, 28.0435, "86 Public Pizzeria", "Pizza • R150");
        addRestaurantMarker(-26.2030, 28.0480, "Great Dane", "Pub & Grill • R300");
        addRestaurantMarker(-26.1995, 28.0460, "Olive and Plates", "Mediterranean • R350");
        addRestaurantMarker(-26.2000, 28.0440, "Cheeky's Street Bar", "Bar & Snacks • R120");
    }


    private void addRestaurantMarker(double lat, double lng, String title, String snippet) {
        LatLng location = new LatLng(lat, lng);

        MarkerOptions markerOptions = new MarkerOptions()
                .position(location)
                .title(title)
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        googleMap.addMarker(markerOptions);
    }

    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    private void enableMyLocation() {
        if (googleMap != null && hasLocationPermission()) {
            try {
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            } catch (SecurityException e) {
                Toast.makeText(this, "Location permission required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void moveToUserLocation() {
        if (hasLocationPermission()) {
            try {
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, location -> {
                            if (location != null && googleMap != null) {
                                LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 14f));
                            } else {
                                // Default to Johannesburg if location not available
                                LatLng johannesburg = new LatLng(-26.2041, 28.0473);
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(johannesburg, 12f));
                            }
                        })
                        .addOnFailureListener(this, e -> {
                            // Handle failure to get location
                            Toast.makeText(this, "Failed to get location: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            moveToDefaultLocation();
                        });
            } catch (SecurityException e) {
                // Handle permission revoked at runtime
                Toast.makeText(this, "Location permission was revoked", Toast.LENGTH_SHORT).show();
                moveToDefaultLocation();
            }
        } else {
            moveToDefaultLocation();
        }
    }

    private void moveToDefaultLocation() {
        if (googleMap != null) {
            LatLng johannesburg = new LatLng(-26.2041, 28.0473);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(johannesburg, 12f));
        }
    }
    private void setupClickListeners() {

        /* Profile icon click
        ImageView profileIcon = findViewById(R.id.profileIcon);
        profileIcon.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });*/

        // Add restaurant FAB click
        FloatingActionButton addRestaurantFab = findViewById(R.id.addRestaurantFab);
        addRestaurantFab.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddRestaurantActivity.class);
            startActivity(intent);
        });

        ImageView syncIcon = findViewById(R.id.sync);
        syncIcon.setOnClickListener(v -> {
            if (isConnectedToInternet()) {
                showSlideNotification("Already connected to internet. Syncing data...", true);
                // TODO: syncData();
            } else {
                showSlideNotification("No internet connection!", false);
                showReconnectDialog();
            }
        });
        ImageView settingsIcon = findViewById(R.id.settings);
        settingsIcon.setOnClickListener(v -> showSettingsPopup());







    }

    public static class Translations {
        public static String get(Context context, String key) {
            SharedPreferences prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
            String language = prefs.getString("language", "english");

            switch (key) {
                case "settings_title":
                    return language.equals("english") ? "Settings" : "Izilungiselelo";
                case "language_label":
                    return language.equals("english") ? "Language:" : "Ulimi:";
                case "close":
                    return language.equals("english") ? "Close" : "Vala";
                case "restaurant_location":
                    return language.equals("english") ? "Location:" : "Indawo:";
                case "language_changed":
                    return language.equals("english") ? "Language changed!" : "Ulimi lushintshile!";
                case "nav_home":
                    return language.equals("english") ? "Home" : "Ikhaya";
                case "nav_gallery":
                    return language.equals("english") ? "Gallery" : "Igalari";
                case "nav_profile":
                    return language.equals("english") ? "Profile" : "Iphrofayela";
                default:
                    return key;
            }
        }
    }




    private void showSettingsPopup() {
        FrameLayout rootLayout = findViewById(android.R.id.content);

        CardView cardView = new CardView(this);
        cardView.setCardElevation(12);
        cardView.setRadius(24);
        cardView.setCardBackgroundColor(Color.WHITE);
        cardView.setUseCompatPadding(true);
        cardView.setContentPadding(40, 40, 40, 40);
        int widthInDp = 300;
        float scale = getResources().getDisplayMetrics().density;
        int widthInPx = (int) (widthInDp * scale + 0.5f);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView title = new TextView(this);
        title.setText(Translations.get(this, "settings_title"));
        title.setTextSize(20);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(Color.BLACK);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, 24);

        TextView langLabel = new TextView(this);
        langLabel.setText(Translations.get(this, "language_label"));
        langLabel.setTextSize(16);
        langLabel.setTextColor(Color.DKGRAY);
        langLabel.setPadding(0, 0, 0, 16);

        RadioGroup radioGroup = new RadioGroup(this);
        radioGroup.setOrientation(RadioGroup.VERTICAL);

        RadioButton englishButton = new RadioButton(this);
        englishButton.setText("English");
        RadioButton zuluButton = new RadioButton(this);
        zuluButton.setText("Zulu");

        radioGroup.addView(englishButton);
        radioGroup.addView(zuluButton);

        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        String language = prefs.getString("language", "english");
        if (language.equals("english")) englishButton.setChecked(true);
        else zuluButton.setChecked(true);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            SharedPreferences.Editor editor = prefs.edit();
            if (checkedId == englishButton.getId()) editor.putString("language", "english");
            else editor.putString("language", "zulu");
            editor.apply();

            // Immediately update UI
            updateLanguageUI();
            showSlideNotification(Translations.get(this, "language_changed"), true);
        });

        Button closeBtn = new Button(this);
        closeBtn.setText(Translations.get(this, "close"));
        closeBtn.setAllCaps(false);
        closeBtn.setBackgroundColor(Color.parseColor("#2196F3"));
        closeBtn.setTextColor(Color.WHITE);
        closeBtn.setPadding(40, 20, 40, 20);

        closeBtn.setOnClickListener(btn -> rootLayout.removeView(cardView));

        layout.addView(title);
        layout.addView(langLabel);
        layout.addView(radioGroup);
        layout.addView(closeBtn);

        cardView.addView(layout);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
               widthInPx,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.CENTER;
        cardView.setLayoutParams(params);

        rootLayout.addView(cardView);
    }



    private void updateLanguageUI() {


        // Update BottomNavigationView titles
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.getMenu().findItem(R.id.navigation_home)
                .setTitle(Translations.get(this, "nav_home"));
        bottomNavigation.getMenu().findItem(R.id.navigation_gallery)
                .setTitle(Translations.get(this, "nav_gallery"));
        bottomNavigation.getMenu().findItem(R.id.navigation_profile)
                .setTitle(Translations.get(this, "nav_profile"));

        // Update open popups dynamically (settings / restaurant popups)
        FrameLayout rootLayout = findViewById(android.R.id.content);
        for (int i = 0; i < rootLayout.getChildCount(); i++) {
            if (rootLayout.getChildAt(i) instanceof CardView) {
                CardView cardView = (CardView) rootLayout.getChildAt(i);
                LinearLayout layout = (LinearLayout) cardView.getChildAt(0);
                for (int j = 0; j < layout.getChildCount(); j++) {
                    if (layout.getChildAt(j) instanceof Button) {
                        Button btn = (Button) layout.getChildAt(j);
                        btn.setText(Translations.get(this, "close"));
                    } else if (layout.getChildAt(j) instanceof TextView) {
                        TextView tv = (TextView) layout.getChildAt(j);
                        if (tv.getText().toString().equals("Settings") || tv.getText().toString().equals("Izilungiselelo")) {
                            tv.setText(Translations.get(this, "settings_title"));
                        } else if (tv.getText().toString().equals("Language:") || tv.getText().toString().equals("Ulimi:")) {
                            tv.setText(Translations.get(this, "language_label"));
                        }
                    }
                }
            }
        }
    }






    private void showSlideNotification(String message, boolean success) {
        // Create parent layout
        FrameLayout rootLayout = findViewById(android.R.id.content);
        Context context = MainActivity.this;

        // CardView container
        CardView cardView = new CardView(context);
        cardView.setCardElevation(12);
        cardView.setRadius(16);
        cardView.setCardBackgroundColor(success ? Color.parseColor("#4CAF50") : Color.parseColor("#F44336")); // green or red
        cardView.setUseCompatPadding(true);

        // Text message
        TextView textView = new TextView(context);
        textView.setText(message);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(16);
        textView.setPadding(40, 30, 40, 30);
        textView.setGravity(Gravity.CENTER);

        // Add text inside card
        cardView.addView(textView);

        // Layout parameters (slide from top)
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.TOP;
        params.setMargins(32, 80, 32, 0);
        cardView.setLayoutParams(params);

        // Initially hidden (off screen)
        cardView.setTranslationY(-300);
        cardView.setAlpha(0f);

        // Add to layout
        rootLayout.addView(cardView);

        // Slide in animation
        cardView.animate()
                .translationY(0)
                .alpha(1f)
                .setDuration(400)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        // Auto dismiss after 3 seconds
        new Handler().postDelayed(() -> {
            cardView.animate()
                    .translationY(-300)
                    .alpha(0f)
                    .setDuration(300)
                    .withEndAction(() -> rootLayout.removeView(cardView))
                    .start();
        }, 3000);
    }


    private boolean isConnectedToInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }


    private void showReconnectDialog() {
        // Create the card view container
        CardView cardView = new CardView(MainActivity.this);
        cardView.setCardElevation(12);
        cardView.setRadius(24);
        cardView.setCardBackgroundColor(Color.WHITE);
        cardView.setUseCompatPadding(true);
        cardView.setContentPadding(40, 40, 40, 40);

        // Inner layout
        LinearLayout layout = new LinearLayout(MainActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);

        // Title
        TextView title = new TextView(MainActivity.this);
        title.setText("No Internet Connection");
        title.setTextSize(20);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(Color.BLACK);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, 20);

        // Message
        TextView message = new TextView(MainActivity.this);
        message.setText("Your device is currently offline.\nPlease reconnect to continue syncing, or auto sync when it reconnect automatically.");
        message.setTextSize(16);
        message.setTextColor(Color.DKGRAY);
        message.setGravity(Gravity.CENTER);
        message.setPadding(0, 0, 0, 30);

        // Reconnect button
        Button reconnectButton = new Button(MainActivity.this);
        reconnectButton.setText("Reconnect");
        reconnectButton.setAllCaps(false);
        reconnectButton.setTextColor(Color.WHITE);
        reconnectButton.setBackgroundColor(Color.parseColor("#2196F3"));
        reconnectButton.setPadding(40, 20, 40, 20);

        // Build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(cardView);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Reconnect button click → open system network settings
        reconnectButton.setOnClickListener(btn -> {
            Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
            startActivity(intent);
            dialog.dismiss();
        });

        // Add everything
        layout.addView(title);
        layout.addView(message);
        layout.addView(reconnectButton);
        cardView.addView(layout);

        dialog.show();
    }



    private void startConnectionMonitor() {
        connectionHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean isConnected = isConnectedToInternet();

                if (isConnected && !wasConnected) {
                    // Just reconnected
                    showSlideNotification("Back online! Syncing data...", true);


                } else if (!isConnected && wasConnected) {

                    // Just lost connection
                    showSlideNotification("No connection. Continue using the app, we’ll sync later.", false);


                }

                // Remember last state
                wasConnected = isConnected;

                // Run again in 1 second
                connectionHandler.postDelayed(this, 1000);
            }
        }, 1000);
    }











    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);

        // Update titles according to language
        bottomNavigation.getMenu().findItem(R.id.navigation_home)
                .setTitle(Translations.get(this, "nav_home"));
        bottomNavigation.getMenu().findItem(R.id.navigation_gallery)
                .setTitle(Translations.get(this, "nav_gallery"));
        bottomNavigation.getMenu().findItem(R.id.navigation_profile)
                .setTitle(Translations.get(this, "nav_profile"));

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_home) {
                return true;
            } else if (itemId == R.id.navigation_gallery) {
                showSampleRestaurantDetail();
                return true;
            } else if (itemId == R.id.navigation_profile) {
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }


    private void showSampleRestaurantDetail() {
        // Create a sample restaurant detail
        Intent intent = new Intent(this, RestaurantDetailActivity.class);
        intent.putExtra("restaurant_name", "Featured Restaurant");
        intent.putExtra("restaurant_info", "Fine Dining • $$$");
        intent.putExtra("latitude", -26.2041);
        intent.putExtra("longitude", 28.0473);
        startActivity(intent);
    }


    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        LatLng position = marker.getPosition();
        showRestaurantPopup(marker.getTitle(), marker.getSnippet(), position);
        return true;
    }

    private void showRestaurantPopup(String name, String info, LatLng location) {
        // Root layout
        FrameLayout rootLayout = findViewById(android.R.id.content);

        // CardView container
        CardView cardView = new CardView(this);
        cardView.setCardElevation(12);
        cardView.setRadius(24);
        cardView.setCardBackgroundColor(Color.WHITE);
        cardView.setUseCompatPadding(true);
        cardView.setContentPadding(40, 40, 40, 40);

        // Layout for contents
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);

        // Restaurant Name
        TextView title = new TextView(this);
        title.setText(name);
        title.setTextSize(20);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(Color.BLACK);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, 16);

        // Restaurant Info
        TextView message = new TextView(this);
        message.setText(info);
        message.setTextSize(16);
        message.setTextColor(Color.DKGRAY);
        message.setGravity(Gravity.CENTER);
        message.setPadding(0, 0, 0, 16);

        // Location (reverse geocoded)
        TextView locationView = new TextView(this);
        locationView.setText(getAddressFromLatLng(location));
        locationView.setTextSize(14);
        locationView.setTextColor(Color.GRAY);
        locationView.setGravity(Gravity.CENTER);
        locationView.setPadding(0, 0, 0, 24);

         // Close Button
        Button closeBtn = new Button(this);
        closeBtn.setText(getTextByLanguage("Close", "Vala"));
        closeBtn.setAllCaps(false);
        closeBtn.setBackgroundColor(Color.parseColor("#2196F3"));
        closeBtn.setTextColor(Color.WHITE);
        closeBtn.setPadding(40, 20, 40, 20);

        closeBtn.setOnClickListener(v -> rootLayout.removeView(cardView));


        // Add views to layout
        layout.addView(title);
        layout.addView(message);
        layout.addView(locationView);
        layout.addView(closeBtn);
        cardView.addView(layout);

        // Layout params for center
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.CENTER;
        cardView.setLayoutParams(params);

        // Add CardView to root
        rootLayout.addView(cardView);
    }

    private String getTextByLanguage(String english, String zulu) {
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        String language = prefs.getString("language", "english");
        return language.equals("zulu") ? zulu : english;
    }



    private String getAddressFromLatLng(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                android.location.Address address = addresses.get(0);
                String city = address.getLocality() != null ? address.getLocality() : "";
                String street = address.getThoroughfare() != null ? address.getThoroughfare() : "";
                String feature = address.getFeatureName() != null ? address.getFeatureName() : "";
                return "Location: " + feature + ", " + street + ", " + city;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // fallback if reverse geocoding fails
        return String.format("Location: %.4f, %.4f", latLng.latitude, latLng.longitude);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
                moveToUserLocation();
            } else {
                Toast.makeText(this, "Location permission denied. Using default location.", Toast.LENGTH_LONG).show();
                moveToUserLocation();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is authenticated
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // Redirect to login if not authenticated
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        connectionHandler.removeCallbacksAndMessages(null);
    }


}