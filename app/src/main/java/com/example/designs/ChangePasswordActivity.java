package com.example.designs;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ChangePasswordActivity extends AppCompatActivity {

    EditText newPasswordField, confirmPasswordField;
    Button saveButton;
    DatabaseHelper dbHelper;
    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        dbHelper = new DatabaseHelper(this);


        userEmail = getIntent().getStringExtra("email");
        newPasswordField = findViewById(R.id.newpass);
        confirmPasswordField = findViewById(R.id.password);
        saveButton = findViewById(R.id.loginbtn);

        saveButton.setOnClickListener(v -> {
            String newPassword = newPasswordField.getText().toString().trim();
            String confirmPassword = confirmPasswordField.getText().toString().trim();

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else if (userEmail == null) {
                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            } else {
                boolean updated = dbHelper.updatePassword(userEmail, newPassword);
                if (updated) {
                    Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, login_page.class));
                    finish();
                } else {
                    Toast.makeText(this, "Failed to update password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
