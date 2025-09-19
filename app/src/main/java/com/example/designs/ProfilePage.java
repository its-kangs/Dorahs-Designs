package com.example.designs;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ProfilePage extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String PROFILE_IMAGE_FILE_NAME = "profile_image.png";

    EditText nameField, emailField, phoneField, locationField;
    Button btnSave, btnMeasurements, btnChangePassword, btnLogout;
    ImageView profileImage, editIcon;

    DatabaseHelper dbHelper;
    String userEmail;

    private ActivityResultLauncher<String> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        dbHelper = new DatabaseHelper(this);
        userEmail = getIntent().getStringExtra("user_email");

        nameField = findViewById(R.id.nameField);
        emailField = findViewById(R.id.emailField);
        phoneField = findViewById(R.id.phoneField);
        locationField = findViewById(R.id.locationField);

        btnSave = findViewById(R.id.btnSave);
        btnMeasurements = findViewById(R.id.btnMeasurements);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnLogout = findViewById(R.id.btnLogout);

        profileImage = findViewById(R.id.profileImage);
        editIcon = findViewById(R.id.editIcon);

        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            profileImage.setImageBitmap(bitmap);
                            saveImageToInternalStorage(bitmap);
                            Toast.makeText(this, "Profile image updated", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            Log.e(TAG, "Failed to save image: " + e.getMessage(), e);
                            Toast.makeText(this, "Failed to save image. Please try again.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
        loadProfile();
        loadProfileImage();

        editIcon.setOnClickListener(v -> openGallery());

        btnSave.setOnClickListener(v -> saveProfileAndUpdateUI());

        btnChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChangePasswordActivity.class);
            intent.putExtra("email", userEmail);
            startActivity(intent);
        });

        btnMeasurements.setOnClickListener(v -> {
            Intent intent = new Intent(this, MeasurementsPage.class);
            intent.putExtra("user_email", userEmail);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {

                    SharedPreferences sharedPref = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.remove("loggedInUserEmail");
                    editor.apply();

                    Intent loginIntent = new Intent(this, login_page.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(loginIntent);
                    finish();
                }
        );
    }


    private void loadProfile() {
        if (userEmail != null) {
            SQLiteDatabase db = null;
            Cursor cursor = null;
            try {
                db = dbHelper.getReadableDatabase();
                cursor = db.query(DatabaseHelper.TABLE_USERS,
                        null,
                        DatabaseHelper.COLUMN_USER_EMAIL + " = ?",
                        new String[]{userEmail},
                        null, null, null);

                if (cursor != null && cursor.moveToFirst()) {
                    nameField.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_NAME)));
                    emailField.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_EMAIL)));


                    String phone = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_PHONE));
                    String location = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_LOCATION));


                    if (phone != null) {
                        phoneField.setText(phone);
                    } else {
                        phoneField.setText("");
                    }
                    if (location != null) {
                        locationField.setText(location);
                    } else {
                        locationField.setText("");
                    }



                } else {
                    Toast.makeText(this, "Could not load user data", Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "No user data found for email: " + userEmail);

                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading profile for email: " + userEmail, e);
                Toast.makeText(this, "Error loading user data", Toast.LENGTH_SHORT).show();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                if (db != null) {
                    db.close();
                }
            }
        } else {
            Toast.makeText(this, "User email not found. Please log in again.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "User email is null on ProfilePage creation.");

            Intent intent = new Intent(this, login_page.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private void saveProfileAndUpdateUI() {
        String phone = phoneField.getText().toString().trim();
        String location = locationField.getText().toString().trim();

        if (phone.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Phone and location cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean updated = dbHelper.updateUserProfile(userEmail, phone, location);
        if (updated) {
            Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                profileImage.setImageBitmap(bitmap);
                saveImageToInternalStorage(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveImageToInternalStorage(Bitmap bitmap) throws IOException {
        FileOutputStream fos = openFileOutput(PROFILE_IMAGE_FILE_NAME, MODE_PRIVATE);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        fos.close();
    }

    private void loadProfileImage() {
        File imageFile = new File(getFilesDir(), PROFILE_IMAGE_FILE_NAME);
        if (imageFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            profileImage.setImageBitmap(bitmap);
            Log.d(TAG, "Profile image loaded from internal storage.");
        } else {
            Log.d(TAG, "No profile image found in internal storage.");

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}