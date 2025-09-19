package com.example.designs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import com.google.android.material.color.DynamicColors;

public class login_page extends AppCompatActivity {

    private TextView textSignUp;
    private Button loginButton;
    private EditText emailInput, passwordInput;

    private static final String SIGN_UP_TEXT = "Sign up";
    public static final String DONT_HAVE_PASSWORD_TEXT = "Don't have a password? ";
    private static final String FULL_TEXT = DONT_HAVE_PASSWORD_TEXT + SIGN_UP_TEXT;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DynamicColors.applyIfAvailable(this);
        setEdgeToEdge();
        setContentView(R.layout.activity_login_page);

        textSignUp = findViewById(R.id.textSignUp);
        loginButton = findViewById(R.id.loginbtn);
        emailInput = findViewById(R.id.email);
        passwordInput = findViewById(R.id.password);

        dbHelper = new DatabaseHelper(this);

        setClickableSignUpText();
        setupLoginButton();
    }

    private void setupLoginButton() {
        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(login_page.this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
                return;
            }


            boolean isValid = dbHelper.validateLogin(email, password);

            if (isValid) {
                Toast.makeText(login_page.this, "Login successful", Toast.LENGTH_SHORT).show();
                SharedPreferences sharedPref = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("loggedInUserEmail", email);
                editor.apply();

                Intent intent = new Intent(login_page.this, home_page.class);
                intent.putExtra("user_email", email);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(login_page.this, "Invalid email or password.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setClickableSignUpText() {
        SpannableString spannableString = new SpannableString(FULL_TEXT);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                widget.setClickable(false);
                Intent intent = new Intent(login_page.this, register_page.class);
                intent.setAction(Intent.ACTION_VIEW);
                startActivity(intent);
                widget.postDelayed(() -> widget.setClickable(true), 500);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setColor(getColor(R.color.blue));
            }
        };

        int start = DONT_HAVE_PASSWORD_TEXT.length();
        int end = FULL_TEXT.length();
        spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textSignUp.setText(spannableString);
        textSignUp.setMovementMethod(LinkMovementMethod.getInstance());
        textSignUp.setHighlightColor(Color.TRANSPARENT);
    }

    private void setEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(getWindow().getDecorView(), (view, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(insets.left, 0, insets.right, insets.bottom);
            return windowInsets;
        });

        WindowInsetsControllerCompat windowInsetsController = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        windowInsetsController.hide(WindowInsetsCompat.Type.statusBars());
        windowInsetsController.setAppearanceLightNavigationBars(true);
    }
}
