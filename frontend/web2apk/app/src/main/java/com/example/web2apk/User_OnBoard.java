package com.example.web2apk;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import me.relex.circleindicator.CircleIndicator;

public class User_OnBoard extends AppCompatActivity
{
    ViewPager viewPager;
    CircleIndicator circleIndicator;

    Onboard_Adapter onboardAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_viewpage);

        viewPager = findViewById(R.id.welcome_viewpage);
        circleIndicator = findViewById(R.id.welcome_indicator);
        onboardAdapter = new Onboard_Adapter(this);

        viewPager.setAdapter(onboardAdapter);
        circleIndicator.setViewPager(viewPager);
    }
}
