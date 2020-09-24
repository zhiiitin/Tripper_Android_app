package com.example.tripper_android_app.trip;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Spinner;
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

import static androidx.navigation.Navigation.findNavController;

/**
 * 行程建立儲存後頁面
 *
 * @author cooperhsieh
 * @version 2020 09 17
 */
public class TripHasSavedPage extends Fragment {
    private final static String TAG = "TAG_TripInfo";
    private MainActivity activity;
    private TextView textSavedShowTitle, textShowSDate, textShowSTime;
    private RecyclerView rvShowTripD;
    private Spinner spShowDays;
    private Trip_M trips;
    private CommonTask tripDeleteTask;
    private CommonTask tripGetAllTask;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
        setHasOptionsMenu(true);
        trips = (Trip_M) (getArguments() != null ? getArguments().getSerializable("tripDetail") : null);
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
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorForWhite));
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textSavedShowTitle = view.findViewById(R.id.textSavedShowTitle);
        textShowSDate = view.findViewById(R.id.textShowSDate);
        textShowSTime = view.findViewById(R.id.textShowSTime);

//        //從上一頁bundle帶過來（不包含圖片）
//        if (trips != null) {
//            textDetailTripTitle.setText(location.getName());
//            textDetailTripAdd.setText(location.getAddress());
//            textLocInfo.setText(location.getInfo());
//            showLocPic();
//        }

        //Spinner
        spShowDays = view.findViewById(R.id.spShowDays);


        //揪團人數按鈕
        ImageButton btManageGroupPpl = view.findViewById(R.id.btManageGroupPpl);
        btManageGroupPpl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v)
                        .navigate(R.id.action_tripHasSavedPage_to_groupManageFragment);
            }
        });

        //刪除行程按鈕
//        ImageButton btDeleteTrip = view.findViewById(R.id.btDeleteTrip);
//        btDeleteTrip.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (Common.networkConnected(activity)) {
//                    String url = Common.URL_SERVER + "TripServlet";
//                    JsonObject jsonObject = new JsonObject();
//                    jsonObject.addProperty("action", "booksDelete");
//                    jsonObject.addProperty("tripId", trips.getTripId());
//                    int count = 0;
//                    try {
//                        tripDeleteTask = new CommonTask(url, jsonObject.toString());
//                        String result = tripDeleteTask.execute().get();
//                        count = Integer.parseInt(result);
//                    } catch (Exception e) {
//                        Log.e(TAG, e.toString());
//                    }
//                    if (count == 0) {
//                        Common.showToast(activity, "刪除失敗");
//                    } else {
//                        tripList.remove(trips);
//                        Adapter.this.notifyDataSetChanged();
//                        // 外面也必須移除選取的spot
//                        TripHasSavedPage.this.tripList.remove(trips);
//                        Common.showToast(activity, "刪除成功");
//                    }
//                } else {
//                    Common.showToast(activity, "網路無法連線");
//                }
//
//
//            }
//        });


    }

    private List<Trip_M> getTrip() {
        List<Trip_M> tripList = null;
        if (Common.networkConnected(activity)) {
            String Url = Common.URL_SERVER + "Trip_M_Servlet"; /////////
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAll");
            String jsonOut = jsonObject.toString();
            tripGetAllTask = new CommonTask(Url, jsonOut);
            try {
                String jsonIn = tripGetAllTask.execute().get();
                Type listtype = new TypeToken<List<Trip_M>>() {  /////
                }.getType();
                tripList = new Gson().fromJson(jsonIn, listtype);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Common.showToast(activity, "No Internet");
        }
        return tripList;
    }

    private void showTrip(List<Trip_M> tripMList) {
//        if (tripMList == null || tripMList.isEmpty()) {
//            Common.showToast(activity, "搜尋不到行程");
//        }
//        Trip_HomePage.TripAdapter tripAdapter = (Trip_HomePage.TripAdapter) rvTripHome.getAdapter();
//        if (tripAdapter == null) {
//            rvTripHome.setAdapter(new Trip_HomePage.TripAdapter(activity, tripList));
//        } else {
//            tripAdapter.setTrips(tripList);
//            tripAdapter.notifyDataSetChanged();
//        }
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