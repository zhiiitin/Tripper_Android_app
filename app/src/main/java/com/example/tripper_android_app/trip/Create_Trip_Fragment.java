package com.example.tripper_android_app.trip;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Layout;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;


import com.example.tripper_android_app.MainActivity;
import com.example.tripper_android_app.R;
import com.example.tripper_android_app.location.Location_D;
import com.example.tripper_android_app.util.Common;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 建立行程頁面
 *
 * @author cooper
 * @version 2020.09.07
 */

public class Create_Trip_Fragment extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private static final String TAG = "TAG_Date";
    private MainActivity activity;
    private TextView textDate, textTime;
    private EditText etTripTitle;
    private Spinner spDay;
    private static int year, month, day, hour, minute;
    private SharedPreferences preferences;
    private RecyclerView rvLocSelectedList;
    //private List<Location_D> day4Locations;


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
        return inflater.inflate(R.layout.fragment_create__trip_, container, false);

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textDate = view.findViewById(R.id.textDate);
        textTime = view.findViewById(R.id.textTime);
        rvLocSelectedList = view.findViewById(R.id.rvLocChosen);
        rvLocSelectedList.setLayoutManager(new LinearLayoutManager(activity));
        //day4Locations = new ArrayList<>();
        //toolbar設定
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("建立行程");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorForWhite));
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 挑選景點完顯示recycleView
        String daySelected = Common.spinnerSelect;
        Log.d(TAG, "daySelected :: " + daySelected);
        if(daySelected !=null && !daySelected.isEmpty()){
            switch (daySelected) {
                case "1":
                    showLocList(Common.locationDs1);
                    break;
                case "2":
                    showLocList(Common.locationDs2);
                    break;
                case "3":
                    showLocList(Common.locationDs1);
                    break;
                case "4":
                    showLocList(Common.locationDs1);
                    break;
                case "5":
                    showLocList(Common.locationDs1);
                    break;
                case "6":
                    showLocList(Common.locationDs1);
                    break;
            }
        }

        // spinner監聽器，顯示不同天的景點清單


        //日期選擇
        Button btPickDate = view.findViewById(R.id.btPickDate);
        btPickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(activity, Create_Trip_Fragment.this,
                        Create_Trip_Fragment.year, Create_Trip_Fragment.month, Create_Trip_Fragment.day);
                // 取得DatePicker物件方可設定可選取的日期區間
                DatePicker datePicker = datePickerDialog.getDatePicker();

                // 設定可選取的開始日為今日
                Calendar calendar = Calendar.getInstance(); //getInstance()取得今天的日期時間

                //今天轉成的毫秒
                datePicker.setMinDate(calendar.getTimeInMillis());
                // 設定可選取的結束日為一個月後，amount:可選擇的往後幾個月
                calendar.add(Calendar.MONTH, 6);
                datePicker.setMaxDate(calendar.getTimeInMillis());

                // 最後要呼叫show()方能顯示
                datePickerDialog.show();
            }
        });
        //時間選擇
        Button btPickTime = view.findViewById(R.id.btPickTime);
        btPickTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(
                        activity,
                        Create_Trip_Fragment.this,
                        Create_Trip_Fragment.hour, Create_Trip_Fragment.minute, false)
                        .show();
            }
        });

        spDay = view.findViewById(R.id.spDay);
        etTripTitle = view.findViewById(R.id.etTripTitle);


        // 挑選景點
        Button btSelectLoc = view.findViewById(R.id.btAddNewLoc);
        btSelectLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.spinnerSelect = spDay.getSelectedItem().toString().trim();
                Navigation.findNavController(v)
                        .navigate(R.id.action_create_Trip_Fragment_to_create_Trip_LocationList);
            }


        });


        Button btNext = view.findViewById(R.id.btNext);
        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                String tripTitle = etTripTitle.getText().toString().trim();
                String startDate = textDate.getText().toString().trim();
                String startTime = textTime.getText().toString().trim();
                int dayCount = Integer.parseInt(spDay.getSelectedItem().toString().trim());
                preferences = activity.getSharedPreferences(Common.PREF_FILE, Context.MODE_PRIVATE);
                preferences.edit()
                        .putString("tripTitle", tripTitle)
                        .putString("startDate", startDate)
                        .putString("startTime", startTime)
                        .putInt("dayCount", dayCount)
                        .apply();
                Trip_M trip_m = new Trip_M(tripTitle, startDate, startTime, dayCount);
                bundle.putSerializable("createTrip", trip_m);
               Navigation.findNavController(v).navigate(R.id.action_create_Trip_Fragment_to_createTripBeforeSave, bundle);

            }
        });
    }

    private void showLocList(List<Location_D> locations) {
        if(locations == null || locations.isEmpty()){
            return;
        }
        Log.d(TAG, "enter showLocList");
        //day4Locations.addAll(locations);
        SelectLocAdapter selectLocAdapter = (SelectLocAdapter) rvLocSelectedList.getAdapter();
        if(selectLocAdapter == null ){
            Log.d(TAG, "enter adpter is null");
            rvLocSelectedList.setAdapter(new SelectLocAdapter(activity, locations));
        }else {
            Log.d(TAG, "enter adpter is not null");
            selectLocAdapter.setLocationDs(locations);
            selectLocAdapter.notifyDataSetChanged();
        }
    }

    private class SelectLocAdapter extends RecyclerView.Adapter<SelectLocAdapter.MyDayViewHolder> {
        private LayoutInflater layoutInflater;
        private List<Location_D> locationDs;

        SelectLocAdapter(Context context, List<Location_D> locationDs){
            layoutInflater = LayoutInflater.from(context);
            this.locationDs = locationDs;
        }

        void setLocationDs(List<Location_D> locationDs){
            this.locationDs = locationDs;
        }

        @Override
        public int getItemCount() {
            return locationDs == null ? 0 : locationDs.size();
        }

        @NonNull
        @Override
        public MyDayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.item_view_loc_and_memo_card, parent, false);
            return new MyDayViewHolder(itemView);
        }

        private class MyDayViewHolder extends RecyclerView.ViewHolder {
            TextView tvStayTime, tvLocName, tvAddress, tvMemo;
            public MyDayViewHolder(View itemView) {
                super(itemView);
                tvStayTime = itemView.findViewById(R.id.textShowStayTime);
                tvLocName = itemView.findViewById(R.id.textLocName);
                tvAddress = itemView.findViewById(R.id.textAddress);
                tvMemo = itemView.findViewById(R.id.textShowMemo);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull MyDayViewHolder myDayViewHolder, int position) {
            Location_D locationDtail = locationDs.get(position);
            myDayViewHolder.tvLocName.setText(locationDtail.getName());
            myDayViewHolder.tvAddress.setText(locationDtail.getAddress());
            myDayViewHolder.tvStayTime.setText(locationDtail.getStayTimes());
            myDayViewHolder.tvMemo.setText(locationDtail.getMemos());
        }


    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Navigation.findNavController(textDate).popBackStack();
                return true;
            default:
                break;
        }
        return true;
    }

    /* 覆寫OnDateSetListener.onDateSet()以處理日期挑選完成事件。
           日期挑選完成會呼叫此方法，並傳入選取的年月日 */
    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        Create_Trip_Fragment.year = year;
        Create_Trip_Fragment.month = month;
        Create_Trip_Fragment.day = day;
        updateDisplay();
    }

    /* 覆寫OnTimeSetListener.onTimeSet()以處理時間挑選完成事件。
       時間挑選完成會呼叫此方法，並傳入選取的時與分 */
    @Override
    public void onTimeSet(TimePicker view, int hour, int minute) {
        Create_Trip_Fragment.hour = hour;
        Create_Trip_Fragment.minute = minute;
        updateDisplay();
    }


    /* 將指定的日期顯示在TextView上。
    一月的值是0而非1，所以「month + 1」後才顯示 */
    private void updateDisplay() {
        textDate.setText(new StringBuilder().append(year).append("-")
                .append(pad(month + 1)).append("-").append(pad(day)));


        textTime.setText(new StringBuilder().append(" ").append(pad(hour)).append(":")
                .append(pad(minute)));
    }

    /* 若數字有十位數，直接顯示；
       若只有個位數則補0後再顯示，例如7會改成07後再顯示 */
    private String pad(int number) {
        if (number >= 10) {
            return String.valueOf(number);
        } else {
            return "0" + number;
        }
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        String tripTitle = etTripTitle.getText().toString().trim();
//        String startDate = textDate.getText().toString().trim();
//        String startTime = textTime.getText().toString().trim();
//        //int dayCount = Integer.parseInt(spDay.getSelectedItem().toString().trim());
//        preferences = activity.getSharedPreferences(Common.PREF_FILE, Context.MODE_PRIVATE);
//        etTripTitle.setText(preferences.getString("tripTitle",""));
//        textDate.setText(preferences.getString("startDate",""));
//        textTime.setText(preferences.getString("startTime",""));
//
//        //spDay.setSelection(preferences.getInt("dayCount",0));
//
//    }


}