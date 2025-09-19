package com.example.designs;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class home_page extends AppCompatActivity {

    private static final String TAG = "HomePage";
    private LinearLayout galleryButton;
    private LinearLayout ordersButton;
    private LinearLayout measurementsButton;
    private LinearLayout customDesignsButton;
    private LinearLayout messagesButton;
    private LinearLayout profileButton;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableEdgeToEdge();
        setContentView(R.layout.activity_home_page);
        userEmail = getIntent().getStringExtra("user_email");


        if (userEmail == null || userEmail.isEmpty()) {
            Log.e(TAG, "User email not found in intent extras when launching home_page. Redirecting to login.");
            Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_LONG).show();
            Intent loginIntent = new Intent(this, login_page.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(loginIntent);
            finish();
            return;
        } else {
            Log.d(TAG, "Home Page loaded for user: " + userEmail);
        }

        initializeViews();
        setupButtonClickListeners();
    }

    private void enableEdgeToEdge() {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setSystemBarColors();
        Log.d(TAG, "Edge-to-edge display enabled.");
    }

    private void setSystemBarColors() {
        WindowInsetsControllerCompat windowInsetsController =
                new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());

        windowInsetsController.setAppearanceLightStatusBars(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            windowInsetsController.setAppearanceLightNavigationBars(true);
        }
        Log.d(TAG, "System bar colors set for light appearance.");
    }

    private void initializeViews() {
        galleryButton = findViewById(R.id.buttongallery);
        ordersButton = findViewById(R.id.buttonorders);
        measurementsButton = findViewById(R.id.buttonmeasurements);
        customDesignsButton = findViewById(R.id.buttoncustomdesigns);
        messagesButton = findViewById(R.id.buttonmessages);
        profileButton = findViewById(R.id.buttonprofile);
        Log.d(TAG, "All views initialized.");
    }

    private void setupButtonClickListeners() {
        setupGalleryButtonClickListener();
        setupOrdersButtonClickListener();
        setupMeasurementsButtonClickListener();
        setupCustomDesignsButtonClickListener();
        setupMessagesButtonClickListener();
        setupProfileButtonClickListener();
        Log.d(TAG, "All button click listeners set up.");
    }

    private void setupGalleryButtonClickListener() {
        galleryButton.setOnClickListener(v -> {
            Intent intent = new Intent(home_page.this, GalleryActivity.class);
            startActivity(intent);
        });
    }

    private void setupOrdersButtonClickListener() {
        ordersButton.setOnClickListener(v -> {
            Intent intent = new Intent(home_page.this, OrdersPage.class);
            intent.putExtra("user_email", userEmail);
            startActivity(intent);
        });
    }

    private void setupMeasurementsButtonClickListener() {
        measurementsButton.setOnClickListener(v -> {
            Intent intent = new Intent(home_page.this, MeasurementsPage.class);
            intent.putExtra("user_email", userEmail);
            startActivity(intent);
        });
    }

    private void setupCustomDesignsButtonClickListener() {
        customDesignsButton.setOnClickListener(v -> {
            Intent intent = new Intent(home_page.this, CustomsDesignsPage.class);
            intent.putExtra("user_email", userEmail);
            startActivity(intent);
        });
    }

    private void setupMessagesButtonClickListener() {
        messagesButton.setOnClickListener(v -> {
            Intent intent = new Intent(home_page.this, MessagesPage.class);
            intent.putExtra("user_email", userEmail);
            startActivity(intent);
        });
    }

    private void setupProfileButtonClickListener() {
        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(home_page.this, ProfilePage.class);
            intent.putExtra("user_email", userEmail);
            startActivity(intent);
        });
    }
}