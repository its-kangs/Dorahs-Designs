package com.example.designs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MeasurementsPage extends AppCompatActivity {

    private static final String TAG = "MeasurementsPage";

    ImageView selectedImageView;
    EditText inputBust, inputWaist, inputHips, inputHeight, inputShoulder, inputArmLength, inputNotes;
    Spinner materialSpinner;
    Button btnSave, btnWatchTutorial;

    DatabaseHelper dbHelper;
    String userEmail;
    int selectedImageRes;
    String designName;
    double unitPrice;
    int designId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_measurements_page);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        selectedImageView = findViewById(R.id.selectedImageView);
        inputBust = findViewById(R.id.inputBust);
        inputWaist = findViewById(R.id.inputWaist);
        inputHips = findViewById(R.id.inputHips);
        inputHeight = findViewById(R.id.inputHeight);
        inputShoulder = findViewById(R.id.inputshoulder);
        inputArmLength = findViewById(R.id.inputArmLength);
        inputNotes = findViewById(R.id.inputNotes);
        materialSpinner = findViewById(R.id.materialSpinner);
        btnSave = findViewById(R.id.btnSaveMeasurements);
        btnWatchTutorial = findViewById(R.id.btnWatchTutorial);

        dbHelper = new DatabaseHelper(this);


        Intent incomingIntent = getIntent();
        selectedImageRes = incomingIntent.getIntExtra("selectedImageRes", -1);
        userEmail = incomingIntent.getStringExtra("user_email");
        designName = incomingIntent.getStringExtra(OrdersPage.EXTRA_DESIGN_NAME);
        unitPrice = incomingIntent.getDoubleExtra(OrdersPage.EXTRA_UNIT_PRICE, 0.0);
        designId = incomingIntent.getIntExtra(OrdersPage.EXTRA_DESIGN_ID, -1);

        if (userEmail == null || userEmail.isEmpty()) {
            SharedPreferences sharedPref = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
            userEmail = sharedPref.getString("loggedInUserEmail", null);

            if (userEmail == null || userEmail.isEmpty()) {
                Log.e(TAG, "User email not found in Intent or SharedPreferences. Redirecting to login.");
                Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_LONG).show();
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


        if (selectedImageRes != -1) {
            selectedImageView.setImageResource(selectedImageRes);
            Log.d(TAG, "Image displayed from resource ID: " + selectedImageRes);
        } else {
            selectedImageView.setImageResource(R.drawable.default_measurement_guide);
            Log.w(TAG, "No image resource ID found. Using default placeholder.");
        }


        if (designName == null || designName.isEmpty()) {
            designName = "Gallery Item";
        }
        if (designId == -1) {
            designId = selectedImageRes != -1 ? selectedImageRes : 0;
        }


        setupMaterialSpinner();

        btnSave.setOnClickListener(v -> saveMeasurementsToDatabase());


        btnWatchTutorial.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/jvGEVbgIXPU"));
            startActivity(intent);
        });
    }

    private void setupMaterialSpinner() {
        String[] materials = {"Cotton", "Silk", "Denim", "Lace", "Ankara", "Chiffon"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, materials);
        materialSpinner.setAdapter(adapter);
    }

    private void saveMeasurementsToDatabase() {
        String bust = inputBust.getText().toString().trim();
        String waist = inputWaist.getText().toString().trim();
        String hips = inputHips.getText().toString().trim();
        String height = inputHeight.getText().toString().trim();
        String shoulder = inputShoulder.getText().toString().trim();
        String armLength = inputArmLength.getText().toString().trim();
        String notes = inputNotes.getText().toString().trim();
        String material = materialSpinner.getSelectedItem().toString();


        if (bust.isEmpty() || waist.isEmpty() || hips.isEmpty() || height.isEmpty() ||
                shoulder.isEmpty() || armLength.isEmpty()) {
            Toast.makeText(this, "Please fill in all measurement fields.", Toast.LENGTH_LONG).show();
            return;
        }

        if (!isNumeric(bust) || !isNumeric(waist) || !isNumeric(hips) ||
                !isNumeric(height) || !isNumeric(shoulder) || !isNumeric(armLength)) {
            Toast.makeText(this, "Please enter valid numbers for all measurement fields.", Toast.LENGTH_LONG).show();
            return;
        }

        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(this, "User email not found. Cannot save measurements.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "User email is null or empty when trying to save measurements. This should have been caught earlier.");
            return;
        }


        boolean inserted = dbHelper.insertMeasurements(userEmail, selectedImageRes, bust, waist, hips, height, shoulder, armLength, notes, material);

        if (inserted) {
            Toast.makeText(this, "Measurements saved successfully!", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Measurements saved to DB for user: " + userEmail);


            Intent intent = new Intent(MeasurementsPage.this, OrdersPage.class);
            intent.putExtra(OrdersPage.EXTRA_USER_EMAIL, userEmail);
            intent.putExtra(OrdersPage.EXTRA_IMAGE_URI, selectedImageRes); //
            intent.putExtra(OrdersPage.EXTRA_DESIGN_NAME, designName);
            intent.putExtra(OrdersPage.EXTRA_UNIT_PRICE, unitPrice);
            intent.putExtra(OrdersPage.EXTRA_DESIGN_ID, designId); // Pass the designId

            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Failed to save measurements. Please try again.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Failed to insert measurements into database for user: " + userEmail);
        }
    }


    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}