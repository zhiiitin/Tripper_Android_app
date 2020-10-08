package com.example.tripper_android_app.trip;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
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
import com.example.tripper_android_app.blog.DateAndId;
import com.example.tripper_android_app.location.Location_D;
import com.example.tripper_android_app.task.CommonTask;
import com.example.tripper_android_app.task.ImageTask;
import com.example.tripper_android_app.util.Common;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

/**
 * @author cooperhsieh
 * 2020/10/02
 * 行程修改頁面
 */

public class updateTripFragment extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private static final String TAG = "TAG_Date";
    private MainActivity activity;
    private TextView textDate, textTime, textChoseGroupPpl;
    private EditText etTripTitle;
    private Spinner spDay, spChoosePpl;
    private Switch switchGroup;
    private static int year, month, day, hour, minute;
    private RecyclerView rvLocChosen;
    private Map<String, List<Trip_LocInfo>> tripLocInfoList;



    private int lastPosition;
    private SharedPreferences preference;
    private int status = 0;
    private CommonTask tripGetAllTask;
    //照片
    private ImageButton ibChangeLocPic, ibUpdateTrip;
    private ImageView ivLocPic; // 行程封面照
    private byte[] b_photo;
    private static final int REQ_TAKE_PICTURE = 0;
    private static final int REQ_PICK_PICTURE = 1;
    private static final int REQ_CROP_PICTURE = 2;
    private Uri contentUri;
    private Trip_M tripM;
    private Bundle bundle;


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
        return inflater.inflate(R.layout.fragment_update_trip, container, false);
    }


    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textDate = view.findViewById(R.id.showDate);
        textTime = view.findViewById(R.id.showTime);
        etTripTitle = view.findViewById(R.id.showTripTitle);
        textChoseGroupPpl = view.findViewById(R.id.textChoseGroupPpl);
        ibUpdateTrip = view.findViewById(R.id.ibUpdateTrip);
        ivLocPic = view.findViewById(R.id.ivLocPic);
        spDay = view.findViewById(R.id.spDay);



        //toolbar設定
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("修改行程");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorForWhite));
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //帶HomePage資料
        bundle = getArguments();
        if (bundle == null || bundle.getSerializable("tripM") == null) {
            Common.showToast(activity, "搜尋不到行程");
            Navigation.findNavController(view).popBackStack();
            return;
        }
        tripM = (Trip_M) bundle.getSerializable("tripM");
        showTripPic();


        //recyclerview
        rvLocChosen = view.findViewById(R.id.rvLocChosen);
        rvLocChosen.setLayoutManager(new LinearLayoutManager(activity));
        tripLocInfoList = new TreeMap<>();
        getLocInfo();
        // spinner select by day
        List<Trip_LocInfo> locInfoList = new ArrayList<>();
        locInfoList = Common.map2.get("1");
        if(locInfoList == null ){
            Log.d("###", "enter null");
        }else {
            Log.d("###", "enter not null");
        }
        Log.d("###TEST :: " ,locInfoList.get(1).getTrip_Id()+"");
        showLocList(locInfoList);






        // 挑選景點完顯示recycleView
        spinnerShowLoc();


        // spinner監聽器，顯示不同天的景點清單
        spDay.setOnItemSelectedListener(listener);


        //日期選擇
        Button btPickDate = view.findViewById(R.id.btPickDate);
        btPickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(activity, updateTripFragment.this,
                        updateTripFragment.year, updateTripFragment.month, updateTripFragment.day);
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
                        updateTripFragment.this,
                        updateTripFragment.hour, updateTripFragment.minute, false)
                        .show();
            }
        });


        // 挑選景點
        Button btSelectLoc = view.findViewById(R.id.btAddNewLoc);
        btSelectLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.spinnerSelect = spDay.getSelectedItem().toString().trim();
                //存取標題，出發日期，時間
                savePreferences();
                Navigation.findNavController(v)
                        .navigate(R.id.action_updateTripFragment_to_updateTripLocList);
            }
        });

        //揪團功能開關
        textChoseGroupPpl = view.findViewById(R.id.textChoseGroupPpl);
        spChoosePpl = view.findViewById(R.id.spChoosePpl);

        Log.d("####", tripM.getStatus() + "");

        switchGroup = view.findViewById(R.id.switchGroup);
        switchGroup.setChecked(tripM.getStatus() == 0 ? false : true);
        //如果有開啟，會顯示已存的揪團人數
        if (switchGroup.isChecked()) {
            textChoseGroupPpl.setVisibility(View.VISIBLE);
            spChoosePpl.setVisibility(View.VISIBLE);
//            String selectedItem = bundle.getString(Integer.parseInt(tripM.getpMax()));
//            spChoosePpl.getSelectedItem();
//            Spinner sp = sdgdfgdfgdfg


        } else {
            textChoseGroupPpl.setVisibility(View.GONE);
            spChoosePpl.setVisibility(View.GONE);
        }

        switchGroup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    textChoseGroupPpl.setVisibility(View.VISIBLE);
                    spChoosePpl.setVisibility(View.VISIBLE);
                    status = 1;
                } else {
                    textChoseGroupPpl.setVisibility(View.GONE);
                    spChoosePpl.setVisibility(View.GONE);
                    status = 3;
                }
            }
        });


        //更換行程封面圖片
        ibChangeLocPic = view.findViewById(R.id.ibChangeLocPic);
        ivLocPic = view.findViewById(R.id.ivLocPic);
        ibChangeLocPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTypeDialog();
            }
        });

        //暫存檔資料
        preference = activity.getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);


//        //修改所有資料至DB
        ibUpdateTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Common.networkConnected(activity)) {
                    String url = Common.URL_SERVER + "TripServlet";

                    int memberId = Integer.parseInt(preference.getString("memberId", ""));
                    Log.e(TAG, "show MemberId when Update: " + memberId);

                    String tripId = tripM.getTripId();
                    String tripTitle = etTripTitle.getText().toString();
                    String startDate = textDate.getText().toString().trim();
                    String startTime = textTime.getText().toString().trim();
                    int dayCount = Integer.parseInt(spDay.getSelectedItem().toString().trim());
                    int pMax = Integer.parseInt(spChoosePpl.getSelectedItem().toString());

                    //行程標題
                    if (tripTitle.length() > 10 || tripTitle.isEmpty()) {
                        etTripTitle.setError("請輸入小於10字元的標題");
                        return;
                    }

                    //出發日期
                    if (startDate.isEmpty()) {
                        textDate.setError("請選擇出發日期");
                        return;
                    }

                    //出發時間
                    if (startTime.isEmpty()) {
                        textTime.setError("請選擇出發時間");
                        return;
                    }
                    // String tripId, int memberId, String tripTitle, String startDate, String startTime, int dayCount, int pMax, int status
                    Trip_M tripMaster = new Trip_M(tripId, memberId, tripTitle, startDate, startTime, dayCount, pMax, status);


                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "update");
                    jsonObject.addProperty("tripM", new Gson().toJson(tripMaster));
                    jsonObject.addProperty("locationD", new Gson().toJson(Common.map));

                    // TODO 處理照片
                    if (b_photo != null) {
                        jsonObject.addProperty("imageBase64", Base64.encodeToString(b_photo, Base64.DEFAULT));
                    }
                    int count = 0;
                    try {
                        String result = new CommonTask(url, jsonObject.toString()).execute().get();
                        count = Integer.parseInt(result);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                    if (count == 0) {
                        Common.showToast(activity, "修改行程成功");
                    } else {
                        Common.showToast(activity, "修改行程失敗");
                        Log.d(TAG, "Trip Fail: " + count);
                    }
                } else {
                    Common.showToast(activity, "請檢查網路連線");
                }
                Navigation.findNavController(v)
                        .popBackStack(R.id.trip_HomePage, false);
//                deletePreferences();
            }
        });
//        loadPreferences();
    }


    private void showLocList(List<Trip_LocInfo> tripDs) {
        Log.d(TAG, "enter showLocList");
        ShowTripLocAdapter showTripLocAdapter = (ShowTripLocAdapter) rvLocChosen.getAdapter();
        if (showTripLocAdapter == null) {
            Log.d(TAG, "enter Adapter is null");
            rvLocChosen.setAdapter(new ShowTripLocAdapter(activity, tripDs));
        } else {
            Log.d(TAG, "enter Adapter is not null");
            showTripLocAdapter.setLocationDs(tripDs);
            showTripLocAdapter.notifyDataSetChanged();

        }
    }

    private void getLocInfo() {

       // Map<String, List<Trip_LocInfo>> showLocNames = new TreeMap<>();

        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "Trip_D_Servlet";
            Trip_M tripM = (Trip_M) bundle.getSerializable("tripM");
            String tripId = tripM.getTripId();

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "showLocName");
            jsonObject.addProperty("tripId", tripId);
            String jsonOut = jsonObject.toString();
            tripGetAllTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = tripGetAllTask.execute().get();
                Type type = new TypeToken<Map<String, List<Trip_LocInfo>>>() {
                }.getType();
                Common.map2 = new Gson().fromJson(jsonIn, type);
                if(Common.map2 == null ){
                    Log.d("###", "enter Common.map2 null");
                }else {
                    Log.d("###", "enter Common.map2 not null");
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            Common.showToast(activity, "請確認網路連線");
        }


    }

    private class ShowTripLocAdapter extends RecyclerView.Adapter<ShowTripLocAdapter.TripLocVH> {
        private LayoutInflater layoutInflater;
        private List<Trip_LocInfo> tripList ;    //.map2.get(1); // 1 = spinner 天數
        private View visibleView;

        ShowTripLocAdapter(Context context, List<Trip_LocInfo> tripList) {
            layoutInflater = LayoutInflater.from(context);
            this.tripList = tripList;
        }

        void setLocationDs( List<Trip_LocInfo> tripList) {
            this.tripList = tripList;
        }

        @Override
        public int getItemCount() {
            return tripList == null ? 0 : tripList.size();
        }

        @NonNull
        @Override
        public TripLocVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.item_view_location_detail_card, parent, false);
            return new TripLocVH(itemView);
        }

        class TripLocVH extends RecyclerView.ViewHolder {
            TextView textLocChosen, textLocAddChosen, textLocStayTimeChosen, textShowLocMemo;
            ImageButton btExpand;
            LinearLayout expandableView;

            public TripLocVH(@NonNull View itemView) {
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
        public void onBindViewHolder(@NonNull final TripLocVH tripLocVH, final int position) {
            final Trip_LocInfo tripLocInfo = (Trip_LocInfo) tripList.get(position);

            Log.d(TAG, "onBindViewHolder-position: " + position);

            tripLocVH.textLocChosen.setText(tripLocInfo.getName());
            tripLocVH.textLocAddChosen.setText(tripLocInfo.getAddress());
            tripLocVH.textLocStayTimeChosen.setText(tripLocInfo.getStaytime());
            tripLocVH.textShowLocMemo.setText(tripLocInfo.getMemo());

            //CardView上面的More Button功能
            tripLocVH.btExpand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final PopupMenu popupMenu = new PopupMenu(activity, v, Gravity.END);
                    popupMenu.inflate(R.menu.trip_memo_card_list);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @SuppressLint("LongLogTag")
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.mShowmore:
                                    switch (tripLocVH.expandableView.getVisibility()) {
                                        case View.VISIBLE:
                                            tripLocVH.expandableView.setVisibility(View.GONE);
                                            break;
                                        case View.GONE:
                                            if (visibleView != null) {
                                                visibleView.setVisibility(View.GONE);
                                            }
                                            tripLocVH.expandableView.setVisibility(View.VISIBLE);
                                            visibleView = tripLocVH.expandableView;
                                            break;
                                        case View.INVISIBLE:
                                            break;
                                    }
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
            //原本取索引所以要改成取對應的值+1
            Log.d("###::", position + 1 + "");
            Common.spinnerSelect = position + 1 + "";
            spinnerShowLoc();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };


    //toolbar 左上角返回按鈕
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Navigation.findNavController(textDate).popBackStack(R.id.trip_HomePage, false);
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    //Spinner 選擇天數對應的recyclerView
    public void spinnerShowLoc() {
        String selected = Common.spinnerSelect;
        Log.d(TAG, "daySelected: " + selected);
//        showLocList(Common.map.get(selected));
    }


    /* 覆寫OnDateSetListener.onDateSet()以處理日期挑選完成事件。
           日期挑選完成會呼叫此方法，並傳入選取的年月日 */
    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        updateTripFragment.year = year;
        updateTripFragment.month = month;
        updateTripFragment.day = day;
        updateDisplay();
    }

    /* 覆寫OnTimeSetListener.onTimeSet()以處理時間挑選完成事件。
       時間挑選完成會呼叫此方法，並傳入選取的時與分 */
    @Override
    public void onTimeSet(TimePicker view, int hour, int minute) {
        updateTripFragment.hour = hour;
        updateTripFragment.minute = minute;
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

    //開啟相機&相簿訪問請求
    private void showTypeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final AlertDialog dialog = builder.create();
        final View view = View.inflate(getActivity(), R.layout.dialog_select_photo, null);
        TextView tv_select_gallery = (TextView) view.findViewById(R.id.tv_select_gallery);
        TextView tv_select_camera = (TextView) view.findViewById(R.id.tv_select_camera);
        tv_select_gallery.setOnClickListener(new View.OnClickListener() {
            // 在相簿中選取
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    openAlbum();
                }
                dialog.dismiss();
            }
        });
        tv_select_camera.setOnClickListener(new View.OnClickListener() {// 呼叫照相機
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                file = new File(file, "picture.jpg");
                contentUri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".provider", file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);

                if (intent.resolveActivity(activity.getPackageManager()) != null) {
                    startActivityForResult(intent, REQ_TAKE_PICTURE);
                    dialog.dismiss();
                } else {
                    Common.showToast(activity, "no camera app found");
                }

            }
        });
        dialog.setView(view);
        dialog.show();

    }

    //開啟相簿
    private void openAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQ_PICK_PICTURE);
    }

    //材切相片
    private void crop(Uri sourceImageUri) {
        File file = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        file = new File(file, "picture_cropped.jpg");
        Uri destinationUri = Uri.fromFile(file);
        UCrop.of(sourceImageUri, destinationUri)
//                .withAspectRatio(16, 9) // 設定裁減比例
//                .withMaxResultSize(500, 500) // 設定結果尺寸不可超過指定寬高
                .start(activity, this, REQ_CROP_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQ_TAKE_PICTURE:
                    crop(contentUri);
                    break;
                case REQ_PICK_PICTURE:
                    crop(intent.getData());
                    break;
                case REQ_CROP_PICTURE:
                    handleCropResult(intent);
                    break;
            }
        }
    }


    //相片材切後
    private void handleCropResult(Intent intent) {
        Uri resultUri = UCrop.getOutput(intent);
        if (resultUri == null) {
            return;
        }
        Bitmap bitmap = null;
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                bitmap = BitmapFactory.decodeStream(
                        activity.getContentResolver().openInputStream(resultUri));
            } else {
                ImageDecoder.Source source =
                        ImageDecoder.createSource(activity.getContentResolver(), resultUri);
                bitmap = ImageDecoder.decodeBitmap(source);
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            b_photo = out.toByteArray();
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
        if (bitmap != null) {
            ivLocPic.setImageBitmap(bitmap);
        } else {
            ivLocPic.setImageResource(R.drawable.default_bg_pc);
        }
    }


    //TODO 從首頁帶行程過來
    private void showTripPic() {
        String url = Common.URL_SERVER + "TripServlet";
        String tripId = tripM.getTripId();

        int imageSize = getResources().getDisplayMetrics().widthPixels / 4;
        Bitmap bitmap = null;
        try {
            bitmap = new ImageTask(url, tripId, imageSize).execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (bitmap != null) {
            ivLocPic.setImageBitmap(bitmap);
        } else {
            ivLocPic.setImageResource(R.drawable.ic_nopicture);
        }

        etTripTitle.setText(tripM.getTripTitle());
        textDate.setText(tripM.getStartDate());
        textTime.setText(tripM.getStartTime());

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
                .remove("tripTitle")
                .remove("tripDate")
                .remove("tripTime")
                .apply();
    }


    @Override
    public void onStart() {
        super.onStart();
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


