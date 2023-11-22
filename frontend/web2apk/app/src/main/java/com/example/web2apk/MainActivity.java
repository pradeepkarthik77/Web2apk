package com.example.web2apk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private Context context;

    private RecyclerView recyclerView;

    private LinearLayout no_apps;

    private CardAdapter cardAdapter;

    private BroadcastReceiver receiver;

    private ImageButton home_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.context = this;
        this.no_apps = findViewById(R.id.log_linear);
        this.home_btn = findViewById(R.id.open_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        Spannable wordtoSpan = new SpannableString("Web2APK");
        TextView toolbar_title = toolbar.findViewById(R.id.tool_title);
        toolbar_title.setText(wordtoSpan);

        SharedPreferences sharedPreferences = getSharedPreferences("DbState",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if(!sharedPreferences.getBoolean("isCreated",false))
        {
            DBHandler dbHandler = new DBHandler(getApplicationContext());
            dbHandler.createDB();
            editor.putBoolean("isCreated",true);
            editor.commit();
        }

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
                if (scrollY > oldScrollY + 12 && fab.isExtended()) {
                    fab.shrink();
                }

                if (scrollY < oldScrollY - 12 && !fab.isExtended()) {
                    fab.extend();
                }

                if (scrollY == 0) {
                    fab.extend();
                }
            }
        });

        this.home_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://pradeepkarthik77.github.io/Web2APK_collection/";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });

        this.recyclerView = findViewById(R.id.main_recycler);
        this.cardAdapter = new CardAdapter(this);
        this.recyclerView.setLayoutManager(new GridLayoutManager(this,1));
        this.recyclerView.setAdapter(cardAdapter);

        // Check if the RecyclerView has no elements
        if (recyclerView.getAdapter() != null && recyclerView.getAdapter().getItemCount() == 0) {
            this.no_apps.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            this.no_apps.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        // Register the LocalBroadcastReceiver
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Refresh your RecyclerView here
                cardAdapter = new CardAdapter(context);
                recyclerView.setAdapter(cardAdapter);
                cardAdapter.notifyDataSetChanged();
                // Check if the RecyclerView has no elements
                if (recyclerView.getAdapter() != null && recyclerView.getAdapter().getItemCount() == 0) {
                    no_apps.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    no_apps.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        };

        LocalBroadcastManager.getInstance(this)
                .registerReceiver(receiver, new IntentFilter("com.example.web2apk.ACTION_REFRESH"));

    }

    @Override
    protected void onDestroy() {
        // Unregister the LocalBroadcastReceiver when the activity is destroyed
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onDestroy();
    }
}