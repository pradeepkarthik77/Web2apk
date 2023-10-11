package com.example.web2apk;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;

public class home_fragment extends Fragment {

    // Default constructor
    public home_fragment() {
        // Required empty public constructor
    }

    // Factory method to create a new instance of this fragment
    public static home_fragment newInstance() {
        home_fragment fragment = new home_fragment();
        Bundle args = new Bundle();
        // Add any arguments here if needed
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.home_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize views here if needed

        LottieAnimationView lottieAnimationView = view.findViewById(R.id.home_animation);
        lottieAnimationView.setAnimation(R.raw.home_anim);
    }
}
