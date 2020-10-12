package com.example.tripper_android_app.trip;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.tripper_android_app.MainActivity;
import com.example.tripper_android_app.R;
import com.example.tripper_android_app.location.Location;
import com.example.tripper_android_app.location.Location_D;
import com.example.tripper_android_app.task.ImageTask;
import com.example.tripper_android_app.util.Common;
import com.example.tripper_android_app.util.DateUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * @author cooperhsieh
 * 2020/10/03
 * 景點詳細資料for update行程
 */
public class UpdateTripLocDetail extends Fragment implements TimePickerDialog.OnTimeSetListener {

    private static final String TAG = "TAG_LocConfirm";
    private MainActivity activity;
    private GoogleMap map;
    private Location location;
    private TextView textStayTime, etTripTitle, textDate, textTime, textView11;
    private EditText etMemo;
    private String startDate;
    private ImageView locPic;
    private static int hour, minute;
    private Spinner spDay;
    private SharedPreferences preference;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
        location = (Location) (getArguments() != null ? getArguments().getSerializable("locationDetail") : null);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_trip_location_detail, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textStayTime = view.findViewById(R.id.textStayTime);
        etMemo = view.findViewById(R.id.etMemo);
        spDay = view.findViewById(R.id.spDay);

        etTripTitle = view.findViewById(R.id.etTripTitle);
        textDate = view.findViewById(R.id.textDate);
        textTime = view.findViewById(R.id.textTime);


        //停留時間挑選
        Button btStayTime = view.findViewById(R.id.btStayTime);
        btStayTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(
                        activity, UpdateTripLocDetail.this,
                        UpdateTripLocDetail.hour, UpdateTripLocDetail.minute, false).show();
            }
        });

        //MapView
        MapView mapView = view.findViewById(R.id.mapView);
        // 呼叫MapView.onCreate()與onStart()才可正常顯示地圖
        mapView.onCreate(savedInstanceState);
        mapView.onStart();
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                showLocation(location);
            }
        });

        final TextView textDetailTripTitle = view.findViewById(R.id.textDetailTripTitle);
        final TextView textDetailTripAdd = view.findViewById(R.id.textDetailTripAdd);
        TextView textLocInfo = view.findViewById(R.id.textLocInfo);
        locPic = view.findViewById(R.id.locPic);

        //從上一頁bundle帶過來（不包含圖片）
        if (location != null) {
            textDetailTripTitle.setText(location.getName());
            textDetailTripAdd.setText(location.getAddress());
            textLocInfo.setText(location.getInfo());
            showLocPic();
        }

        //景點確認按鈕帶資料
        ImageButton btConfirmLoc = view.findViewById(R.id.btConfirmLoc);
        btConfirmLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = textDetailTripTitle.getText().toString().trim();
                String address = textDetailTripAdd.getText().toString().trim();
                String stayTimes = textStayTime.getText().toString().trim();
                String memos = etMemo.getText().toString().trim();
                String daySelected = Common.spinnerSelect;
                String locId = location.getLocId();
                Log.d("###Detail daySelected", daySelected + "");
                if (daySelected == null || daySelected.isEmpty()) {
                    Common.showToast(activity, "請先選擇天數");
                    Navigation.findNavController(v).popBackStack();
                }
                // 處理出發日期
//                if(!daySelected.equals("1")){
//
//                    startDate = preference.getString("tripDate", Common.DEFAULT_FILE);
//                    try {
//                        startDate = DateUtil.date4day(startDate, Integer.parseInt(daySelected) );
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                }

                //檢查停留時間是否輸入
                if (textStayTime.getText().toString().equals("")) {
                    textStayTime.setError("請選擇停留時間");
                    Common.showToast(activity, "請選擇停留時間");
                    return;
                }

                List<Location_D> locationDs = Common.map.get(daySelected);
                if (locationDs == null || locationDs.isEmpty()) {
                    locationDs = new ArrayList<>();
                }
                // TODO 讀取出發日期 根據Spinner存入不同的天數
                preference = activity.getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
                startDate = preference.getString("tripDate", Common.DEFAULT_FILE);
                if ("2".equals(daySelected)) {
                    try {
                        startDate = DateUtil.date4day(startDate, 1);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else if ("3".equals(daySelected)) {
                    try {
                        startDate = DateUtil.date4day(startDate, 2);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else if ("4".equals(daySelected)) {
                    try {
                        startDate = DateUtil.date4day(startDate, 3);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else if ("5".equals(daySelected)) {
                    try {
                        startDate = DateUtil.date4day(startDate, 4);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else if ("6".equals(daySelected)) {
                    try {
                        startDate = DateUtil.date4day(startDate, 5);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                Log.e(TAG, "StartDate::" + daySelected);



                // String tripId, String transId, String name, String address, String locId,  String memos, String stayTimes,  String startDate
                Location_D locationD = new Location_D(name, address, locId, memos, stayTimes, startDate);
                locationDs.add(locationD);
                Common.map.put(daySelected, locationDs);

                Navigation.findNavController(v).popBackStack(R.id.updateTripFragment, false);
            }
        });
    }

    //顯示景點圖片
    private void showLocPic() {
        String url = Common.URL_SERVER + "LocationServlet";
        String locId = location.getLocId();
        int imageSize = getResources().getDisplayMetrics().widthPixels / 4;
        Bitmap bitmap = null;
        try {
            bitmap = new ImageTask(url, locId, imageSize).execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (bitmap != null) {
            locPic.setImageBitmap(bitmap);
        } else {
            locPic.setImageResource(R.drawable.ic_nopicture);
        }
    }


    private void showLocation(Location location) {
        if (location.getLatitude() < -180 || location.getLongitude() < -180) {
            Common.showToast(activity, "Location not found");
            return;
        }
        addMarker(location);
    }

    // 打標記並移動地圖至標記所在地
    private void addMarker(Location location) {
        LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
        String title = location.getName();
        String snippet = location.getAddress();
        map.addMarker(new MarkerOptions()
                .position(position)
                .title(title)
                .snippet(snippet));
        moveMap(position);
    }

    private void moveMap(LatLng latLng) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .zoom(18)
                .build();
        CameraUpdate cameraUpdate = CameraUpdateFactory
                .newCameraPosition(cameraPosition);
        map.animateCamera(cameraUpdate);
    }


    @Override
    /* 覆寫OnTimeSetListener.onTimeSet()以處理時間挑選完成事件。
       時間挑選完成會呼叫此方法，並傳入選取的時與分 */
    public void onTimeSet(TimePicker view, int hour, int minute) {
        UpdateTripLocDetail.hour = hour;
        UpdateTripLocDetail.minute = minute;
        updateDisplay();
    }

    private void updateDisplay() {
        textStayTime.setText(new StringBuilder().append(" ").append(pad(hour)).append(":")
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
}