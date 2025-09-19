package com.example.designs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.NumberFormat;
import java.util.Locale;

public class OrdersPage extends AppCompatActivity {

    private static final String TAG = "OrdersPage";

    public static final String EXTRA_IMAGE_URI = "imageUri";
    public static final String EXTRA_DESIGN_NAME = "designName";
    public static final String EXTRA_UNIT_PRICE = "unitPrice";
    public static final String EXTRA_USER_EMAIL = "userEmail";
    public static final String EXTRA_DESIGN_ID = "designId";

    ImageView orderImage;
    TextView designNameText, unitPriceText, summaryText;
    EditText quantityInput, streetInput, cityInput, postalCodeInput;
    Spinner paymentSpinner;
    Button placeOrderButton;

    DatabaseHelper dbHelper;
    String userEmail;
    String designName;
    String imageUriString;
    double unitPriceDouble;
    int designId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_orders_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        orderImage = findViewById(R.id.orderImage);
        designNameText = findViewById(R.id.designName);
        unitPriceText = findViewById(R.id.unitPrice);
        summaryText = findViewById(R.id.summaryText);
        quantityInput = findViewById(R.id.quantityInput);
        streetInput = findViewById(R.id.streetInput);
        cityInput = findViewById(R.id.cityInput);
        postalCodeInput = findViewById(R.id.postalCodeInput);
        paymentSpinner = findViewById(R.id.paymentSpinner);
        placeOrderButton = findViewById(R.id.placeOrderButton);

        dbHelper = new DatabaseHelper(this);


        userEmail = getIntent().getStringExtra(EXTRA_USER_EMAIL);
        if (userEmail == null || userEmail.isEmpty()) {
            SharedPreferences sharedPref = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
            userEmail = sharedPref.getString("loggedInUserEmail", null);
            if (userEmail == null || userEmail.isEmpty()) {
                Log.e(TAG, "User email not found in Intent or SharedPreferences. Redirecting to login.");
                Toast.makeText(this, "User session expired. Please log in again.", Toast.LENGTH_LONG).show();
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


        Intent intent = getIntent();
        imageUriString = intent.getStringExtra(EXTRA_IMAGE_URI);
        designName = intent.getStringExtra(EXTRA_DESIGN_NAME);
        unitPriceDouble = intent.getDoubleExtra(EXTRA_UNIT_PRICE, 0.0);
        designId = intent.getIntExtra(EXTRA_DESIGN_ID, -1);


        int imageResId = intent.getIntExtra(EXTRA_IMAGE_URI, -1);
        if (imageResId != -1) {
            orderImage.setImageResource(imageResId);
            Log.d(TAG, "Loaded image from drawable resource ID: " + imageResId);
        } else {

            imageUriString = intent.getStringExtra(EXTRA_IMAGE_URI);
            if (imageUriString != null && !imageUriString.isEmpty()) {
                try {
                    Uri imageUri = Uri.parse(imageUriString);
                    orderImage.setImageURI(imageUri);
                    Log.d(TAG, "Loaded image from URI: " + imageUriString);
                } catch (Exception e) {
                    orderImage.setImageResource(R.drawable.ic_image_placeholder);
                    Log.e(TAG, "Failed to parse image URI, using placeholder.", e);
                }
            } else {
                orderImage.setImageResource(R.drawable.ic_image_placeholder);
                Log.w(TAG, "No image provided, using placeholder.");
            }
        }




        designNameText.setText(designName != null ? designName : getString(R.string.default_design_name));
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "KE"));
        unitPriceText.setText(getString(R.string.unit_price_format, currencyFormat.format(unitPriceDouble)));

        setupPaymentSpinner();


        quantityInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { /* Not used */ }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    int quantity = Integer.parseInt(s.toString());
                    if (quantity > 0) updateSummary(quantity);
                    else updateSummary(0);
                } catch (NumberFormatException e) {
                    updateSummary(0);
                }
            }
            @Override public void afterTextChanged(Editable s) { /* Not used */ }
        });

        updateSummary(0);

        placeOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placeOrder();
            }
        });


}
    private void setupPaymentSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.payment_methods,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paymentSpinner.setAdapter(adapter);
    }

    private void placeOrder() {
        placeOrderButton.setEnabled(false);

        String quantityStr = quantityInput.getText().toString().trim();
        if (quantityStr.isEmpty()) {
            quantityInput.setError(getString(R.string.error_quantity_required));
            quantityInput.requestFocus();
            placeOrderButton.setEnabled(true);
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) {
                quantityInput.setError(getString(R.string.error_quantity_positive));
                quantityInput.requestFocus();
                placeOrderButton.setEnabled(true);
                return;
            }
        } catch (NumberFormatException e) {
            quantityInput.setError(getString(R.string.error_invalid_quantity));
            quantityInput.requestFocus();
            placeOrderButton.setEnabled(true);
            return;
        }

        String street = streetInput.getText().toString().trim();
        String city = cityInput.getText().toString().trim();
        String postal = postalCodeInput.getText().toString().trim();

        if (street.isEmpty()) { streetInput.setError(getString(R.string.error_street_required)); streetInput.requestFocus(); placeOrderButton.setEnabled(true); return; }
        if (city.isEmpty()) { cityInput.setError(getString(R.string.error_city_required)); cityInput.requestFocus(); placeOrderButton.setEnabled(true); return; }
        if (postal.isEmpty()) { postalCodeInput.setError(getString(R.string.error_postal_required)); postalCodeInput.requestFocus(); placeOrderButton.setEnabled(true); return; }

        String fullAddress = street + ", " + city + ", " + postal;
        String paymentMethod = paymentSpinner.getSelectedItem().toString();

        double subtotal = unitPriceDouble * quantity;
        double shippingFee = 200.0;
        double total = subtotal + shippingFee;

        updateSummary(subtotal, shippingFee, total);

        Log.d(TAG, "Attempting to insert order for user: " + userEmail);
        Log.d(TAG, "Design ID: " + designId + ", Name: " + designName + ", Qty: " + quantity);
        Log.d(TAG, "Address: " + fullAddress + ", Payment: " + paymentMethod);
        Log.d(TAG, "Prices: Unit=" + unitPriceDouble + ", Subtotal=" + subtotal + ", Shipping=" + shippingFee + ", Total=" + total);

        boolean inserted = dbHelper.insertOrder(
                userEmail,
                designId,
                designName,
                imageUriString,
                quantity,
                street,
                city,
                postal,
                paymentMethod,
                String.valueOf(unitPriceDouble),
                String.valueOf(subtotal),
                String.valueOf(shippingFee),
                String.valueOf(total)
        );

        if (inserted) {
            Toast.makeText(this, getString(R.string.toast_order_success, designName), Toast.LENGTH_LONG).show();

            finish();
        } else {
            Toast.makeText(this, getString(R.string.toast_order_failed), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Failed to insert order into database for user: " + userEmail);
        }

        placeOrderButton.setEnabled(true);
    }

    private void updateSummary(int currentQuantity) {
        double subtotal = unitPriceDouble * currentQuantity;
        double shippingFee = 200.0;
        double total = subtotal + shippingFee;
        updateSummary(subtotal, shippingFee, total);
    }

    private void updateSummary(double subtotal, double shippingFee, double total) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "KE"));
        summaryText.setText(
                getString(R.string.summary_format,
                        currencyFormat.format(subtotal),
                        currencyFormat.format(shippingFee),
                        currencyFormat.format(total)
                )
        );
    }
}