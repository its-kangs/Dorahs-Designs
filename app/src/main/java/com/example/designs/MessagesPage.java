package com.example.designs;

import android.content.Intent;
import android.os.Bundle;
import android.net.Uri;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MessagesPage extends AppCompatActivity {


    LinearLayout llWhatsApp, llCall, llEmail;

    String dorahPhoneNumber = "+254111935732";
    String dorahEmail = "dorahdesigns@gmail.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_messages_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        llWhatsApp = findViewById(R.id.llWhatsApp);
        llCall = findViewById(R.id.llCall);
        llEmail = findViewById(R.id.llEmail);


        llWhatsApp.setOnClickListener(v -> openWhatsApp());
        llCall.setOnClickListener(v -> openDialer());
        llEmail.setOnClickListener(v -> sendEmail());


    }

    private void openWhatsApp() {
        String url = "https://wa.me/" + dorahPhoneNumber.replace("+", "");
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        try {
            startActivity(i);
        } catch (Exception e) {
            Toast.makeText(this, "WhatsApp not installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void openDialer() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + dorahPhoneNumber));
        startActivity(intent);
    }

    private void sendEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:" + dorahEmail));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Inquiry about tailoring from App"); // More specific subject
        try {
            startActivity(Intent.createChooser(emailIntent, "Send email using...")); // Better prompt
        } catch (Exception e) {
            Toast.makeText(this, "No email app installed.", Toast.LENGTH_SHORT).show();
        }
    }
}

