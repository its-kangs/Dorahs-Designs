package com.example.designs;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.material.color.DynamicColors;

public class register_page extends AppCompatActivity {

    private TextView textLogin;
    private Button registerbtn;
    private EditText nameInput, emailInput, passwordInput;

    private static final String LOGIN_TEXT = "Login";
    private static final String HAVE_ACCOUNT_TEXT = "Have an account? ";
    private static final String FULL_TEXT = HAVE_ACCOUNT_TEXT + LOGIN_TEXT;
    private static final int CLICK_DEBOUNCE_TIME_MS = 500;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DynamicColors.applyIfAvailable(this);
        setEdgeToEdge();

        setContentView(R.layout.activity_register_page);

        // UI references
        textLogin = findViewById(R.id.textLogin);
        registerbtn = findViewById(R.id.registerbtn);
        nameInput = findViewById(R.id.Name);
        emailInput = findViewById(R.id.email);
        passwordInput = findViewById(R.id.password);

        dbHelper = new DatabaseHelper(this);

        setClickableLoginText();
        setRegisterButton();
    }

    private void setClickableLoginText() {
        SpannableString spannableString = new SpannableString(FULL_TEXT);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                widget.setEnabled(false);
                navigateToLoginPage();
                widget.postDelayed(() -> widget.setEnabled(true), CLICK_DEBOUNCE_TIME_MS);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setColor(ContextCompat.getColor(register_page.this, R.color.blue));
            }
        };

        int start = HAVE_ACCOUNT_TEXT.length();
        int end = FULL_TEXT.length();
        spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textLogin.setText(spannableString);
        textLogin.setMovementMethod(LinkMovementMethod.getInstance());
        textLogin.setHighlightColor(Color.TRANSPARENT);
    }

    private void setRegisterButton() {
        registerbtn.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(register_page.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dbHelper.isEmailRegistered(email)) {
                Toast.makeText(register_page.this, "Email already registered", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean success = dbHelper.registerUser(name, email, password);
            if (success) {
                Toast.makeText(register_page.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(register_page.this, login_page.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(register_page.this, "Registration failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToLoginPage() {
        Intent intent = new Intent(register_page.this, login_page.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void setEdgeToEdge() {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        controller.hide(WindowInsetsCompat.Type.systemBars());
        controller.setAppearanceLightStatusBars(true);
        controller.setAppearanceLightNavigationBars(true);
    }
}

