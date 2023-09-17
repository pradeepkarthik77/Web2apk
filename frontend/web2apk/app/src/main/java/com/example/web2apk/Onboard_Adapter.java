package com.example.web2apk;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;

public class Onboard_Adapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;

    private Integer[] onboard_animations = {R.raw.welcome_anim,R.raw.welcome_anim2,R.raw.welcome_anim3};
    private String[] onboard_text = new String[]{"Welcome to Web2APK!\n\nEasily turn any website into a mobile app in just a few clicks!","Customize your app's look and features!\n\nAdjust the App Icon, Name, Colors, Permissions, and Preferences.","Enough Talking, Let's Get Started!!!"};

    public Onboard_Adapter(Context context)
    {
        this.context=context;
    }
    @Override
    public int getCount() {
        return this.onboard_animations.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater=(LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE
        );
        View view =layoutInflater.inflate(R.layout.onboard_fragment,null);

        LottieAnimationView lottieAnimationView = view.findViewById(R.id.onboard_animation);
        lottieAnimationView.setAnimation(onboard_animations[position]);

        TextView textView = view.findViewById(R.id.onboard_text);
        textView.setText(onboard_text[position]);

        if(position == 2)
        {
            MaterialButton materialButton = view.findViewById(R.id.start_btn);
            materialButton.setVisibility(View.VISIBLE);
            materialButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences sharedPreferences = context.getSharedPreferences("Splash",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isWelcomed",true);
                    editor.commit();
                    Intent newIntent = new Intent(context, MainActivity.class);
                    newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(newIntent);
                }
            });
        }

        ViewPager viewPager=(ViewPager) container;
        viewPager.addView(view,0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewPager viewPager=(ViewPager) container;
        View view=(View) object;
        viewPager.removeView(view);
    }
}