package com.example.web2apk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.context = this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        Spannable wordtoSpan = new SpannableString("Web2APK");
        TextView toolbar_title = toolbar.findViewById(R.id.tool_title);
        toolbar_title.setText(wordtoSpan);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        home_fragment simpleFragment = new home_fragment();
        fragmentTransaction.add(R.id.home_fragment, simpleFragment);
        fragmentTransaction.commit();

        ExtendedFloatingActionButton fab = findViewById(R.id.new_app_fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AddNewApp.class);
                startActivity(intent);
            }
        });

        NestedScrollView nestedScrollView = findViewById(R.id.nested_scrollview);

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                // the delay of the extension of the FAB is set for 12 items
                if (scrollY > oldScrollY + 12 && fab.isExtended()) {
                    fab.shrink();
                }

                // the delay of the extension of the FAB is set for 12 items
                if (scrollY < oldScrollY - 12 && !fab.isExtended()) {
                    fab.extend();
                }

                // if the nestedScrollView is at the first item of the list then the
                // extended floating action should be in extended state
                if (scrollY == 0) {
                    fab.extend();
                }
            }
        });

    }
}