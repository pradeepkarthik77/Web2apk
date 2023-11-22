package com.example.web2apk;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class QRCodeActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_code_activity);

        String apkLink = getIntent().getStringExtra("APK_LINK");

        // Check if apkLink is not null before using it
        if (apkLink != null) {
            // Display the value in a toast
            Toast.makeText(this, "APK Link: " + apkLink, Toast.LENGTH_SHORT).show();
        } else {
            // Handle the case where apkLink is null (optional)
            Toast.makeText(this, "APK Link is null", Toast.LENGTH_SHORT).show();
        }

    }
}
