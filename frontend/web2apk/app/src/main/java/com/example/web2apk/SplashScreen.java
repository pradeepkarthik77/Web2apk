package com.example.web2apk;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity
{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        SharedPreferences sharedPreferences = getSharedPreferences("Splash",MODE_PRIVATE);

        ImageView logo = findViewById(R.id.splash_icon);
        TextView title = findViewById(R.id.splash_title);

        SpannableStringBuilder builder = new SpannableStringBuilder();
        String part1 = "Web2";
        String part2 = "APK";
        builder.append(part1);
        builder.append(part2);

        ForegroundColorSpan redColorSpan = new ForegroundColorSpan(Color.BLACK);
        builder.setSpan(redColorSpan, 0, part1.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        int color = Color.parseColor("#428DFF");

        ForegroundColorSpan blueColorSpan = new ForegroundColorSpan(color);
        builder.setSpan(blueColorSpan, part1.length(), part1.length()+part2.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        title.setText(builder);

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(2000);

        logo.startAnimation(fadeIn);
        title.startAnimation(fadeIn);

        new Handler().postDelayed(() -> {

            Intent mainIntent;

            if(sharedPreferences.getBoolean("isWelcomed",false))
            {
                mainIntent = new Intent(SplashScreen.this, MainActivity.class);
            }
            else{
                mainIntent = new Intent(SplashScreen.this, User_OnBoard.class);
            }
            startActivity(mainIntent);
            finish();
        }, 2500);
    }
}
