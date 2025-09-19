package com.example.designs;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {

    private static final String TAG = "GalleryActivity";
    private RecyclerView recyclerView;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        userEmail = getIntent().getStringExtra("user_email");

        if (userEmail == null || userEmail.isEmpty()) {
            SharedPreferences sharedPref = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
            userEmail = sharedPref.getString("loggedInUserEmail", null);

            if (userEmail == null || userEmail.isEmpty()) {
                Toast.makeText(this, "User session not found. Please log in again.", Toast.LENGTH_LONG).show();
                Log.e(TAG, "User email is null or empty. Redirecting to login.");
                Intent loginIntent = new Intent(this, login_page.class);
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(loginIntent);
                finish();
                return;
            } else {
                Log.d(TAG, "User email retrieved from SharedPreferences: " + userEmail);
            }
        } else {
            Log.d(TAG, "User email retrieved from Intent: " + userEmail);
        }


        recyclerView = findViewById(R.id.galleryRecyclerView);


        List<GalleryItem> galleryItemList = new ArrayList<>();
        galleryItemList.add(new GalleryItem(R.drawable.dress1, "Elegant Gown", 18000.00));
        galleryItemList.add(new GalleryItem(R.drawable.dress2, "Summer Flowy Dress", 12500.00));
        galleryItemList.add(new GalleryItem(R.drawable.dress3, "Cocktail Dress", 22000.00));
        galleryItemList.add(new GalleryItem(R.drawable.dress4, "Casual Denim Look", 9500.00));
        galleryItemList.add(new GalleryItem(R.drawable.dress5, "Formal Evening Wear", 35000.00));
        galleryItemList.add(new GalleryItem(R.drawable.dress6, "African Print Dress", 11000.00));
        galleryItemList.add(new GalleryItem(R.drawable.dress7, "Boho Chic Dress", 14000.00));
        galleryItemList.add(new GalleryItem(R.drawable.dress8, "Minimalist Jumpsuit", 16000.00));
        galleryItemList.add(new GalleryItem(R.drawable.dress9, "Wedding Guest Dress", 28000.00));
        galleryItemList.add(new GalleryItem(R.drawable.dress10, "Office Blazer Dress", 19500.00));

        GalleryAdapter adapter = new GalleryAdapter(this, galleryItemList, userEmail);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);
    }
}
