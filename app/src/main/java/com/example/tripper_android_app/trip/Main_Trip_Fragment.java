package com.example.tripper_android_app.trip;

import android.app.Activity;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.Toolbar;

import com.example.tripper_android_app.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Main_Trip_Fragment extends Fragment {
    Activity activity;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
<<<<<<< HEAD

=======
>>>>>>> 9ea312be79812340a5d4129735cfa8edab926f19
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.app_bar_button, menu);
<<<<<<< HEAD

=======
>>>>>>> 9ea312be79812340a5d4129735cfa8edab926f19
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.btCreateTrip){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_trip_, container, false);

    }

    @Override
<<<<<<< HEAD

=======
>>>>>>> 9ea312be79812340a5d4129735cfa8edab926f19
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottomBar);
        NavController navController = Navigation.findNavController(activity, R.id.nav_fragment);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
<<<<<<< HEAD

//    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottomBar);
//        NavController navController = Navigation.findNavController(activity, R.id.fragment);
//        NavigationUI.setupWithNavController(bottomNavigationView, navController);

=======
>>>>>>> 9ea312be79812340a5d4129735cfa8edab926f19
    }


}