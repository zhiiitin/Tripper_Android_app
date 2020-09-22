package com.example.tripper_android_app.trip;


import android.annotation.SuppressLint;
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

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;


import com.example.tripper_android_app.MainActivity;
import com.example.tripper_android_app.R;
import com.example.tripper_android_app.location.Location_D;
import com.example.tripper_android_app.util.Common;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static androidx.navigation.Navigation.findNavController;

/**
 * 建立行程頁面
 *
 * @author cooper
 * @version 2020.09.07
 */

public class Create_Trip_Fragment extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private static final String TAG = "TAG_Date";
    private MainActivity activity;
    private TextView textDate, textTime, textChoseGroupPpl;
    private EditText etTripTitle;
    private Spinner spDay, spChoosePpl;
    private static int year, month, day, hour, minute;
    private RecyclerView rvLocSelectedList;
    private SharedPreferences preference;
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
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textDate = view.findViewById(R.id.textDate);
        textTime = view.findViewById(R.id.textTime);
        etTripTitle = view.findViewById(R.id.etTripTitle);
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
        spinnerShowLoc();


        // spinner監聽器，顯示不同天的景點清單
        spDay = view.findViewById(R.id.spDay);
        spDay.setOnItemSelectedListener(listener);


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


        preference = activity.getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
        loadPreferences();

        // 挑選景點
        Button btSelectLoc = view.findViewById(R.id.btAddNewLoc);
        btSelectLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.spinnerSelect = spDay.getSelectedItem().toString().trim();
                //存取標題，出發日期，時間
                savePreferences();
                Navigation.findNavController(v)
                        .navigate(R.id.action_create_Trip_Fragment_to_create_Trip_LocationList);
            }
        });

        //揪團功能開關
        textChoseGroupPpl = view.findViewById(R.id.textChoseGroupPpl);
        spChoosePpl = view.findViewById(R.id.spChoosePpl);
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch switchGroup = view.findViewById(R.id.switchGroup);
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

    }


    private void showLocList(List<Location_D> locations) {
        if (locations == null || locations.isEmpty()) {
            return;
        }
        Log.d(TAG, "enter showLocList");
        //day4Locations.addAll(locations);
        SelectLocAdapter selectLocAdapter = (SelectLocAdapter) rvLocSelectedList.getAdapter();
        if (selectLocAdapter == null) {
            Log.d(TAG, "enter adpter is null");
            rvLocSelectedList.setAdapter(new SelectLocAdapter(activity, locations));
        } else {
            Log.d(TAG, "enter adpter is not null");
            selectLocAdapter.setLocationDs(locations);
            selectLocAdapter.notifyDataSetChanged();
        }
    }

    class SelectLocAdapter extends RecyclerView.Adapter<SelectLocAdapter.MyDayViewHolder> {
        private LayoutInflater layoutInflater;
        private List<Location_D> locationDs;
        private View visibleView;

        SelectLocAdapter(Context context, List<Location_D> locationDs) {
            layoutInflater = LayoutInflater.from(context);
            this.locationDs = locationDs;
        }

        void setLocationDs(List<Location_D> locationDs) {
            this.locationDs = locationDs;
        }

        @Override
        public int getItemCount() {
            return locationDs == null ? 0 : locationDs.size();
        }

        @NonNull
        @Override
        public MyDayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.item_view_location_detail_card, parent, false);
            return new MyDayViewHolder(itemView);
        }

        private class MyDayViewHolder extends RecyclerView.ViewHolder {
            TextView textLocChosen, textLocAddChosen, textLocStayTimeChosen, textShowLocMemo;
            ImageButton btExpand;
            LinearLayout expandableView;

            public MyDayViewHolder(View itemView) {
                super(itemView);
                textLocChosen = itemView.findViewById(R.id.textLocChosen);
                textLocAddChosen = itemView.findViewById(R.id.textLocAddChosen);
                textLocStayTimeChosen = itemView.findViewById(R.id.textLocStayTimeChosen);
                textShowLocMemo = itemView.findViewById(R.id.textShowLocMemo);
                btExpand = itemView.findViewById(R.id.btExpand);
                expandableView = itemView.findViewById(R.id.expandableView);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull final MyDayViewHolder myDayViewHolder, final int position) {
            Location_D locationDetail = locationDs.get(position);
            myDayViewHolder.textLocChosen.setText(locationDetail.getName());
            myDayViewHolder.textLocAddChosen.setText(locationDetail.getAddress());
            myDayViewHolder.textLocStayTimeChosen.setText(locationDetail.getStayTimes());
            myDayViewHolder.textShowLocMemo.setText(locationDetail.getMemos());


            //CardView上面的More Button功能
            myDayViewHolder.btExpand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final PopupMenu popupMenu = new PopupMenu(activity, v, Gravity.END);
                    popupMenu.inflate(R.menu.location_more_card_list);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @SuppressLint("LongLogTag")
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.mShowmore:
                                    switch (myDayViewHolder.expandableView.getVisibility()) {
                                        case View.VISIBLE:
                                            myDayViewHolder.expandableView.setVisibility(View.GONE);
                                            break;
                                        case View.GONE:
                                            if (visibleView != null) {
                                                visibleView.setVisibility(View.GONE);
                                            }
                                            myDayViewHolder.expandableView.setVisibility(View.VISIBLE);
                                            visibleView = myDayViewHolder.expandableView;
                                            break;
                                        case View.INVISIBLE:
                                            break;
                                    }
                                    break;
                                case R.id.mDeleteLoc:
                                    int newPosition = myDayViewHolder.getAdapterPosition();
                                    locationDs.remove(newPosition);
                                    notifyItemRemoved(newPosition);
                                    notifyItemChanged(newPosition, locationDs.size());
                                    break;
                            }
                            return true;
                        }
                    });
                    popupMenu.show();
                }
            });
        }

    }

    //Spinner 選擇天數跳景點
    Spinner.OnItemSelectedListener listener = new Spinner.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            spinnerShowLoc();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };


    //toolbar 右上角下一步按鈕顯示
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.app_bar_button_next_step, menu);
    }

    //toolbar 左上角返回按鈕+下一步按鈕的監聽器
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.btNextStep) {
            Bundle bundle = new Bundle();

            String title = etTripTitle.getText().toString().trim();
            String sDate = textDate.getText().toString().trim();
            String sTime = textTime.getText().toString().trim();

            Trip_M tripM = new Trip_M(title, sDate, sTime);
            bundle.putSerializable("tripM", tripM);

            findNavController(this.getView()).navigate(R.id.action_create_Trip_Fragment_to_createTripBeforeSave, bundle);
            return true;
        }
        switch (item.getItemId()) {
            case android.R.id.home:
                Navigation.findNavController(textDate).navigate(R.id.action_create_Trip_Fragment_to_trip_HomePage);
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    //Spinner 選擇天數對應的recyclerView
    public void spinnerShowLoc() {
        String daySelected = Common.spinnerSelect;
        Log.d(TAG, "daySelected :: " + daySelected);
        if (daySelected != null && !daySelected.isEmpty()) {
            switch (daySelected) {
                case "1":
                    showLocList(Common.locationDs1);
                    break;
                case "2":
                    showLocList(Common.locationDs2);
                    break;
                case "3":
                    showLocList(Common.locationDs3);
                    break;
                case "4":
                    showLocList(Common.locationDs4);
                    break;
                case "5":
                    showLocList(Common.locationDs5);
                    break;
                case "6":
                    showLocList(Common.locationDs6);
                    break;
            }
        }
    }

    //暫存標題/出發時間/出發日期
    public void savePreferences() {
        String tripTitle = etTripTitle.getText().toString();
        String tripDate = textDate.getText().toString();
        String tripTime = textTime.getText().toString();

        preference.edit()
                .putString("tripTitle", tripTitle)
                .putString("tripDate", tripDate)
                .putString("tripTime", tripTime)
                .apply();
    }


    //讀取標題/出發時間/出發日期
    public void loadPreferences() {
        String tripTitle = preference.getString("tripTitle", Common.DEFAULT_FILE);
        String tripDate = preference.getString("tripDate", Common.DEFAULT_FILE);
        String tripTime = preference.getString("tripTime", Common.DEFAULT_FILE);

        etTripTitle.setText(tripTitle);
        textDate.setText(tripDate);
        textTime.setText(tripTime);
    }

    //刪除暫存檔
    public void deletePreferences() {
        preference.edit()
                .clear()
                .commit();
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

    @Override
    public void onStart() {
        super.onStart();
        deletePreferences();
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