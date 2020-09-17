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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.example.tripper_android_app.location.Location;
import com.example.tripper_android_app.task.CommonTask;
import com.example.tripper_android_app.util.Common;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

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
    private TextView textChoseGroupPpl, textShowTitle, textShowSDate, textShowSTime;
    private Switch switchGroup;
    private SharedPreferences preferences;
    private Trip_M trip_m;
    private RecyclerView rvDay_Loc;
    private List<Trip_M> tripMList;
    private CommonTask DayGetAllTask;


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
        switchGroup = view.findViewById(R.id.switchGroup);

        textShowTitle = view.findViewById(R.id.textShowTitle);
        textShowSDate = view.findViewById(R.id.textShowSDate);
        textShowSTime = view.findViewById(R.id.textShowSTime);


        //取得前頁輸入的資料
        Bundle bundle = getArguments();
        Trip_M trip_m = (Trip_M) bundle.getSerializable("createTrip");

        textShowTitle.setText(trip_m.getTripTitle());
        textShowSDate.setText(trip_m.getStartDate());
        textShowSTime.setText(trip_m.getStartTime());

//        //recyclerView
//        rvDay_Loc = view.findViewById(R.id.rvDay_Loc);
//        rvDay_Loc.setLayoutManager(new LinearLayoutManager(activity));
//        tripMList = getTripMList();
//        showDayPick(tripMList);


        //揪團功能開關
        switchGroup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    textChoseGroupPpl.setVisibility(View.VISIBLE);
                    spChoosePpl.setVisibility(View.VISIBLE);
                } else {
                    textChoseGroupPpl.setVisibility(View.GONE);
                    spChoosePpl.setVisibility(View.GONE);
                }

            }
        });


        ImageButton btSaveTrip = view.findViewById(R.id.btSaveTrip);
        btSaveTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String tripTitle = textShowTitle.getText().toString().trim();
//                String startDate = textShowSDate.getText().toString().trim();
//                String startTime = textShowSTime.getText().toString().trim();
//                int dayCount = Integer.parseInt(spDay.getSelectedItem().toString().trim());
//                int pMax = Integer.parseInt(spChoosePpl.getSelectedItem().toString().trim());
//                int status = Inter 揪團狀態碼
//                存景點ID
//                停留時間
//                行程備註


                Navigation.findNavController(v).navigate(R.id.action_createTripBeforeSave_to_tripHasSavedPage);

        // 管理揪團按鈕
//        btManageGroupPpl.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                preferences = activity.getSharedPreferences("groupSetting", MODE_PRIVATE);
//                preferences.edit()
//                        .putString("tripId", "aaa")
//                        .apply();
//                Navigation.findNavController(v)
//                        .navigate(R.id.action_createTripBeforeSave_to_groupManergeFragment);
            }
        });


    }

//    private List<Trip_M> getTripMList() {
//        List<Trip_M> tripMList = null;
//
//        if (Common.netWorkConnected(activity)) {
//            String url = Common.URL_SERVER + "Trip_M_Servlet";
//            JsonObject jsonObject = new JsonObject();
//            jsonObject.addProperty("action", "getAll");
//            String jsonOut = jsonObject.toString();
//            DayGetAllTask = new CommonTask(url, jsonOut);
//
//            try {
//                String jsonIn = DayGetAllTask.execute().get();
//                Type listType = new TypeToken<List<Location>>() {
//                }.getType();
//                tripMList = new Gson().fromJson(jsonIn, listType);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } else {
//            Common.showToast(activity, "No NetWork Connection available");
//        }
//        return tripMList;
//    }
//
//    private void showDayPick(List<Trip_M> tripMList) {
//        DayPickAdapter dayPickAdapter = (DayPickAdapter) rvDay_Loc.getAdapter();
//        if (dayPickAdapter == null) {
//            rvDay_Loc.setAdapter(new DayPickAdapter(activity, tripMList));
//        } else {
//            dayPickAdapter.setDayPick(tripMList);
//            dayPickAdapter.notifyDataSetChanged();
//        }
//    }
//
//    private class DayPickAdapter extends RecyclerView.Adapter<DayPickAdapter.MyViewHolder> {
//        private LayoutInflater layoutInflater;
//        private List<Trip_M> tripMList;
//
//
//        void setDayPick(List<Trip_M> tripMList) {
//            this.tripMList = tripMList;
//        }
//
//        class MyViewHolder extends RecyclerView.ViewHolder {
//            TextView textDaysChosen;
//            Spinner spinner;
//
//            public MyViewHolder(@NonNull View itemView) {
//                super(itemView);
//                textDaysChosen = itemView.findViewById(R.id.textDaysChosen);
//                spinner = itemView.findViewById(R.id.spDay);
//            }
//        }
//
//
//        @Override
//        public int getItemCount() {
//            return tripMList.size();
//        }
//
//        @NonNull
//        @Override
//        public DayPickAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            View itemView = layoutInflater.inflate(R.layout.item_view_location_detail_card, parent, false);
//            return new MyViewHolder(itemView);
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull DayPickAdapter.MyViewHolder holder, int position) {
//            final Trip_M trip_m = tripMList.get(position);
//            holder.textDaysChosen.setText(trip_m.getDayCount());
//            holder.spinner.setAdapter();
//
//
//        }
//
//
//    }


}
