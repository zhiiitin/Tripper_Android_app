package com.example.tripper_android_app.trip;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tripper_android_app.MainActivity;
import com.example.tripper_android_app.R;

import static androidx.navigation.Navigation.findNavController;

/**
 * 行程建立儲存後頁面
 *
 * @author cooperhsieh
 * @version 2020 09 17
 */
public class TripHasSavedPage extends Fragment {
    private MainActivity activity;
    private TextView textSavedShowTitle;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trip_has_saved_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = view.findViewById(R.id.toolBarTripSaved);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorForWhite));
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textSavedShowTitle = view.findViewById(R.id.textSavedShowTitle);

    }

    //ToolBar 右上角icon
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.app_bar_button_edit_trip, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Navigation.findNavController(textSavedShowTitle).navigate(R.id.action_tripHasSavedPage_to_trip_HomePage);
                break;
            case R.id.btEditTrip:
                findNavController(this.getView()).navigate(R.id.action_tripHasSavedPage_to_create_Trip_Fragment);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}