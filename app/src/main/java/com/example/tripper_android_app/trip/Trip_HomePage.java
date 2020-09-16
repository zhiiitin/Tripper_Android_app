package com.example.tripper_android_app.trip;

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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.example.tripper_android_app.MainActivity;
import com.example.tripper_android_app.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;


import static androidx.navigation.Navigation.findNavController;

/**
 * 行程主頁面
 * @author cooperhsieh
 * @version 2020.09.09
 */


public class Trip_HomePage extends Fragment {
    private MainActivity activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
        setHasOptionsMenu(true);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorForWhite));
        activity.setSupportActionBar(toolbar);


        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottomBar);
        NavController navController = Navigation.findNavController(activity, R.id.trip_HomePage);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);


        BottomNavigationView bottomNavigationViewTop = view.findViewById(R.id.navigation);
        NavController navControllerTop = Navigation.findNavController(activity, R.id.trip_HomePage);
        NavigationUI.setupWithNavController(bottomNavigationViewTop, navControllerTop);
        Menu itemMenu = bottomNavigationViewTop.getMenu();
        itemMenu.getItem(0).setChecked(true);


    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.app_bar_button, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.btCreateTrip) {
            findNavController(this.getView()).navigate(R.id.action_trip_HomePage_to_create_Trip_Fragment);
            return true;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trip__home_page, container, false);

    }

//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottomBar);
//        NavController navController = Navigation.findNavController(activity, R.id.nav_fragment);
//        NavigationUI.setupWithNavController(bottomNavigationView, navController);
//    }

}

//    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottomBar);
//        NavController navController = Navigation.findNavController(activity, R.id.fragment);
//        NavigationUI.setupWithNavController(bottomNavigationView, navController);

