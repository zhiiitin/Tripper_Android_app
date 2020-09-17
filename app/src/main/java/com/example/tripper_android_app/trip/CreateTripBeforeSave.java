package com.example.tripper_android_app.trip;

import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.example.tripper_android_app.MainActivity;
import com.example.tripper_android_app.R;
import com.example.tripper_android_app.util.Common;

import static android.content.Context.MODE_PRIVATE;

/**
 * 建立行程頁面——儲存前頁面展示
 *
 * @author cooperhsieh
 * @version 2020.09.14
 */


public class CreateTripBeforeSave extends Fragment {
    private final static String TAG = "Before_Save";
    private MainActivity activity;
    private Spinner spChoosePpl;
    private ImageButton btManageGroupPpl;
    private TextView textChoseGroupPpl, textShowTitle, textShowSDate, textShowSTime;
    private Switch switchGroup;
    private SharedPreferences preferences;
    private Trip_M trip_m;

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
        return inflater.inflate(R.layout.fragment_create_trip_before_save, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = view.findViewById(R.id.toolbar_TripSave);
        toolbar.setTitle("建立行程");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorForWhite));
        activity.setSupportActionBar(toolbar);


        textChoseGroupPpl = view.findViewById(R.id.textChoseGroupPpl);
        spChoosePpl = view.findViewById(R.id.spChoosePpl);
        btManageGroupPpl = view.findViewById(R.id.btManageGroupPpl);
        switchGroup = view.findViewById(R.id.switchGroup);

        textShowTitle = view.findViewById(R.id.textShowTitle);
        textShowSDate = view.findViewById(R.id.textShowDate);
        textShowSTime = view.findViewById(R.id.textShowSTime);

        //取得前幾頁輸入的資料
        Bundle bundle = getArguments();
        if (bundle != null) {
            Trip_M tripM = (Trip_M) bundle.getSerializable("createTrip");
            if (tripM != null) {
                textShowTitle.setText(trip_m.getTripTitle());
                textShowSTime.setText(trip_m.getStartDate());
                textShowSDate.setText(trip_m.getStartTime());
            }
        }


        //揪團功能開關
        switchGroup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    textChoseGroupPpl.setVisibility(View.VISIBLE);
                    spChoosePpl.setVisibility(View.VISIBLE);
                    btManageGroupPpl.setVisibility(View.VISIBLE);
                } else {
                    textChoseGroupPpl.setVisibility(View.GONE);
                    spChoosePpl.setVisibility(View.GONE);
                    btManageGroupPpl.setVisibility(View.GONE);
                }

            }
        });

        // 管理揪團按鈕
        btManageGroupPpl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferences = activity.getSharedPreferences("groupSetting", MODE_PRIVATE);
                preferences.edit()
                        .putString("tripId", "aaa")
                        .apply();
                Navigation.findNavController(v)
                        .navigate(R.id.action_createTripBeforeSave_to_groupManergeFragment);
            }
        });


    }
}