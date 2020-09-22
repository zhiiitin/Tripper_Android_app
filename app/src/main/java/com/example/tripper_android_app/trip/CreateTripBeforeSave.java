package com.example.tripper_android_app.trip;

import android.content.Context;
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
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.example.tripper_android_app.MainActivity;
import com.example.tripper_android_app.R;
import com.example.tripper_android_app.location.Location;
import com.example.tripper_android_app.location.Location_D;
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
    private Trip_M tripM;
    private RecyclerView rvShowLocList;
    private Spinner spDay;
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

        rvShowLocList = view.findViewById(R.id.rvShowLoc);
        rvShowLocList.setLayoutManager(new LinearLayoutManager(activity));

        // 挑選景點完顯示recycleView
        spDay = view.findViewById(R.id.spDay);
//        spDay.setOnItemSelectedListener(listener);
//        spinnerShowLoc();


        //取得前頁輸入的資料
        Bundle bundle = getArguments();
        Trip_M tripM = (Trip_M) bundle.getSerializable("tripM");

        textShowTitle.setText(tripM.getTripTitle());
        textShowSDate.setText(tripM.getStartDate());
        textShowSTime.setText(tripM.getStartTime());

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

        // 儲存行程
        ImageButton btSaveTrip = view.findViewById(R.id.btSaveTrip);
        btSaveTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String tripTitle = textShowTitle.getText().toString().trim();
                String startDate = textShowSDate.getText().toString().trim();
                String startTime = textShowSTime.getText().toString().trim();
//                int dayCount = Integer.parseInt(spDay.getSelectedItem().toString().trim());
//                int pMax = Integer.parseInt(spChoosePpl.getSelectedItem().toString().trim());
//                int status = Inter 揪團狀態碼


                //Navigation.findNavController(v).navigate(R.id.action_createTripBeforeSave_to_tripHasSavedPage);
            }
        });
    }

//    private void showLocList(List<Location_D> locations) {
//        if (locations == null || locations.isEmpty()) {
//            return;
//        }
//        Log.d(TAG, "enter showLocList");
//        //day4Locations.addAll(locations);
//        SLocAdapter sLocAdapter = (SLocAdapter) rvShowLocList.getAdapter();
//        if (sLocAdapter == null) {
//            Log.d(TAG, "enter adpter is null");
//            rvShowLocList.setAdapter(new SLocAdapter(activity, locations));
//        } else {
//            Log.d(TAG, "enter adpter is not null");
//            sLocAdapter.setLocationDList(locations);
//            sLocAdapter.notifyDataSetChanged();
//        }
//    }

//    class SLocAdapter extends RecyclerView.Adapter<SLocAdapter.DayViewHolder> {
//        private LayoutInflater layoutInflater;
//        private List<Location_D> locationDLists;
//        private View visibleView;
//
//
//        SLocAdapter(Context context, List<Location_D> locationDLists) {
//            layoutInflater = LayoutInflater.from(context);
//            this.locationDLists = locationDLists;
//        }
//
//        void setLocationDList(List<Location_D> locationDLists) {
//            this.locationDLists = locationDLists;
//        }
//
//        @Override
//        public int getItemCount() {
//            return locationDLists == null ? 0 : locationDLists.size();
//        }
//
//        @NonNull
//        @Override
//        public SLocAdapter.DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            View itemView = layoutInflater.inflate(R.layout.item_view_detail_cardview_2, parent, false);
//            return new DayViewHolder(itemView);
//        }
//
//        private class DayViewHolder extends RecyclerView.ViewHolder {
//            TextView textLocChosen1;
//            TextView textLocAddChosen1;
//            TextView textLocStayTimeChosen1;
//            TextView textShowLocMemo1;
//            ImageButton btExpand1;
//            LinearLayout expandableView1;
//
//            public DayViewHolder(@NonNull View itemView) {
//                super(itemView);
//                textLocChosen1 = itemView.findViewById(R.id.textLocChosen1);
//                textLocAddChosen1 = itemView.findViewById(R.id.textLocAddChosen1);
//                textLocStayTimeChosen1 = itemView.findViewById(R.id.textLocStayTimeChosen1);
//                textShowLocMemo1 = itemView.findViewById(R.id.textShowLocMemo1);
//                btExpand1 = itemView.findViewById(R.id.btExpand1);
//                expandableView1 = itemView.findViewById(R.id.expandableView1);
//            }
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull final SLocAdapter.DayViewHolder holder, int position) {
//            Location_D locationDetail = locationDLists.get(position);
//            holder.textLocChosen1.setText(locationDetail.getName());
//            holder.textLocAddChosen1.setText(locationDetail.getAddress());
//            holder.textLocStayTimeChosen1.setText(locationDetail.getStayTimes());
//            holder.textShowLocMemo1.setText(locationDetail.getMemos());
//
//            //CardView上面的倒三角收合功能
//            holder.btExpand1.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    switch (holder.expandableView1.getVisibility()) {
//                        case View.VISIBLE:
//                            holder.expandableView1.setVisibility(View.GONE);
//                            break;
//                        case View.GONE:
//                            if (visibleView != null) {
//                                visibleView.setVisibility(View.GONE);
//                            }
//                            holder.expandableView1.setVisibility(View.VISIBLE);
//                            visibleView = holder.expandableView1;
//                            break;
//                        case View.INVISIBLE:
//                            break;
//                    }
//                }
//            });
//        }
//    }
//
//    //Spinner 選擇天數對應的recyclerView
//    public void spinnerShowLoc() {
//        String daySelected = Common.spinnerSelect;
//        Log.d(TAG, "daySelected :: " + daySelected);
//        if (daySelected != null && !daySelected.isEmpty()) {
//            switch (daySelected) {
//                case "1":
//                    showLocList(Common.locationDs1);
//                    break;
//                case "2":
//                    showLocList(Common.locationDs2);
//                    break;
//                case "3":
//                    showLocList(Common.locationDs3);
//                    break;
//                case "4":
//                    showLocList(Common.locationDs4);
//                    break;
//                case "5":
//                    showLocList(Common.locationDs5);
//                    break;
//                case "6":
//                    showLocList(Common.locationDs6);
//                    break;
//            }
//        }
//    }
//
//    //Spinner 選擇天數跳景點
//    Spinner.OnItemSelectedListener listener = new Spinner.OnItemSelectedListener() {
//        @Override
//        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//            spinnerShowLoc();
//        }
//
//        @Override
//        public void onNothingSelected(AdapterView<?> parent) {
//
//        }
//    };



}