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
import java.util.HashMap;

public class CustomsDesignsPage extends AppCompatActivity {

    private static final String TAG = "CustomsDesignsPage";
    private static final int PICK_IMAGE_REQUEST = 1;

    ImageView imageDesign;
    EditText editDesignName, editDescription, editEstimatedCost;
    Spinner spinnerMaterial;
    Button btnUploadImage, btnSaveDesign;

    Uri imageUri;

    DatabaseHelper dbHelper;
    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_customs_designs_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imageDesign = findViewById(R.id.imagedesign);
        editDesignName = findViewById(R.id.editdesignName);
        editDescription = findViewById(R.id.editdescription);
        editEstimatedCost = findViewById(R.id.editestimatedCost);
        spinnerMaterial = findViewById(R.id.spinnermaterial);
        btnUploadImage = findViewById(R.id.btnuploadImage);
        btnSaveDesign = findViewById(R.id.btnsaveDesign);

        dbHelper = new DatabaseHelper(this);

        userEmail = getIntent().getStringExtra("user_email");
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

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.materials_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMaterial.setAdapter(adapter);

        HashMap<String, String> materialCostMap = new HashMap<>();
        materialCostMap.put("Cotton", "5000ksh");
        materialCostMap.put("Silk", "10000ksh");
        materialCostMap.put("Denim", "3000ksh");
        materialCostMap.put("Chiffon", "15000ksh");
        materialCostMap.put("Lace", "20000ksh");
        materialCostMap.put("Ankara", "4000ksh");


        spinnerMaterial.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                String selectedMaterial = parent.getItemAtPosition(position).toString();
                if (materialCostMap.containsKey(selectedMaterial)) {
                    editEstimatedCost.setText(materialCostMap.get(selectedMaterial));
                } else {
                    editEstimatedCost.setText("");
                }
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                editEstimatedCost.setText("");
            }
        });
        editEstimatedCost.setEnabled(false);

        btnUploadImage.setOnClickListener(v -> openImagePicker());
        btnSaveDesign.setOnClickListener(v -> saveDesign());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            if (imageUri != null) {
                final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
                getContentResolver().takePersistableUriPermission(imageUri, takeFlags);
                imageDesign.setImageURI(imageUri);
                Log.d(TAG, "Image selected: " + imageUri.toString());
            } else {
                Log.e(TAG, "Image URI is null after selection.");
                Toast.makeText(this, "Failed to get image URI.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.d(TAG, "Image selection cancelled or failed. RequestCode: " + requestCode + ", ResultCode: " + resultCode);
        }
    }

    private void saveDesign() {
        String name = editDesignName.getText().toString().trim();
        String desc = editDescription.getText().toString().trim();
        String costStr = editEstimatedCost.getText().toString().trim();
        String material = spinnerMaterial.getSelectedItem().toString();


        if (imageUri == null) {
            Toast.makeText(this, "Please upload an image for your design.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (name.isEmpty()) {
            editDesignName.setError("Design Name is required.");
            editDesignName.requestFocus();
            return;
        }
        if (desc.isEmpty()) {
            editDescription.setError("Description is required.");
            editDescription.requestFocus();
            return;
        }
        if (costStr.isEmpty()) {
            Toast.makeText(this, "Estimated cost not set. Please select a material.", Toast.LENGTH_SHORT).show();
            return;
        }


        double unitPriceDouble = 0.0;
        try {

            String numericCostStr = costStr.replace("ksh", "").trim();
            unitPriceDouble = Double.parseDouble(numericCostStr);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Error parsing estimated cost: " + costStr, e);
            Toast.makeText(this, "Invalid estimated cost format.", Toast.LENGTH_SHORT).show();
            return;
        }


        Log.d(TAG, "Attempting to save custom design for user: " + userEmail);
        Log.d(TAG, "Name: " + name + ", Material: " + material + ", Cost: " + costStr);


        long designId = dbHelper.insertCustomDesign(
                userEmail,
                imageUri.toString(),
                name,
                desc,
                material,
                costStr);


        if (designId != -1) {
            Toast.makeText(this, "Design '" + name + "' saved successfully!", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Custom design saved to DB with ID: " + designId);


            Intent ordersIntent = new Intent(CustomsDesignsPage.this, OrdersPage.class);
            ordersIntent.putExtra(OrdersPage.EXTRA_IMAGE_URI, imageUri.toString());
            ordersIntent.putExtra(OrdersPage.EXTRA_DESIGN_NAME, name);
            ordersIntent.putExtra(OrdersPage.EXTRA_UNIT_PRICE, unitPriceDouble);
            ordersIntent.putExtra(OrdersPage.EXTRA_USER_EMAIL, userEmail);
            ordersIntent.putExtra(OrdersPage.EXTRA_DESIGN_ID, (int) designId);

            startActivity(ordersIntent);
            finish();

        } else {
            Toast.makeText(this, "Failed to save design. Please try again.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Failed to insert custom design into database for user: " + userEmail);
        }
    }
}