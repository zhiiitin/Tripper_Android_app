package com.example.tripper_android_app.goal;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tripper_android_app.MainActivity;
import com.example.tripper_android_app.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class Goal_HomePage extends Fragment {
    private MainActivity activity;
    private ImageView ivStar;
    private TextView tvBG3, tvLocCount, tvTripCount, tvGoalDone, tvGoalCount;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_goal__home_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("個人頁面");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorForWhite));
        activity.setSupportActionBar(toolbar);


        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottomBar);
        NavController navController = Navigation.findNavController(activity, R.id.goal_HomePage);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        Menu itemMenu = bottomNavigationView.getMenu();
        itemMenu.getItem(2).setChecked(true);


        BottomNavigationView bottomNavigationViewTop = view.findViewById(R.id.navigation);
        NavController navControllerTop = Navigation.findNavController(activity, R.id.goal_HomePage);
        NavigationUI.setupWithNavController(bottomNavigationViewTop, navControllerTop);
        Menu itemMenu2 = bottomNavigationViewTop.getMenu();
        itemMenu2.getItem(2).setChecked(true);

        ivStar = view.findViewById(R.id.ivStar);
        ivStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_goal_HomePage_to_goalListFragment);
            }
        });

        tvBG3 = view.findViewById(R.id.tvBG3);
        tvBG3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_goal_HomePage_to_goalListFragment);
            }
        });

        tvGoalDone = view.findViewById(R.id.tvGoalDone);
        tvGoalDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_goal_HomePage_to_goalListFragment);
            }
        });

        tvGoalCount = view.findViewById(R.id.tvGoalCount);
        tvGoalCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_goal_HomePage_to_goalListFragment);
            }
        });
    }
}