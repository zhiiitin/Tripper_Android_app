package com.example.tripper_android_app.group;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.tripper_android_app.MainActivity;
import com.example.tripper_android_app.R;
import com.example.tripper_android_app.blog.Blog_SpotInfo;
import com.example.tripper_android_app.blog.CreateBlogFragment;
import com.example.tripper_android_app.blog.DateAndId;
import com.example.tripper_android_app.fcm.AppMessage;
import com.example.tripper_android_app.location.Location;
import com.example.tripper_android_app.setting.member.Member;
import com.example.tripper_android_app.task.CommonTask;
import com.example.tripper_android_app.task.ImageTask;
import com.example.tripper_android_app.trip.TripGroup;
import com.example.tripper_android_app.trip.TripHasSavedPage;
import com.example.tripper_android_app.trip.Trip_LocInfo;
import com.example.tripper_android_app.trip.Trip_M;
import com.example.tripper_android_app.util.Common;
import com.example.tripper_android_app.util.DateUtil;
import com.example.tripper_android_app.util.SendMessage;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.content.Context.MODE_PRIVATE;
import static androidx.navigation.Navigation.findNavController;

/**
揪團頁面，點擊想參加按鈕
Cooper
2020 10 27
 */

public class GroupTripPage extends Fragment {
    private final static String TAG = "TAG_GroupInfo";
    private MainActivity activity;
    private TextView textSavedShowTitle, textShowSDate, textShowSTime;
    private RecyclerView rvShowTripD;
    private Button btDay1, btDay2, btDay3, btDay4, btDay5, btDay6;
    private Trip_M tripM;
    private CommonTask tripGetAllTask;
    private String startDate, tripId , tripName ;
    private int hostId ;
    private ImageButton btJoinGroup ,ibExitGroup,ibMbrFill,ibCheckGroup;
    private CommonTask tripGet1Task;
    private int checkCount = 0 , mbrStatus = 0 ;
    private Bundle bundle2 = new Bundle();
    private ImageTask groupImageTask;
    private ImageView ivBackImage ;
    SharedPreferences pref = null;


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
        return inflater.inflate(R.layout.fragment_group_trip_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("行程瀏覽");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorForWhite));
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pref = activity.getSharedPreferences(Common.PREF_FILE,
                MODE_PRIVATE);



        textSavedShowTitle = view.findViewById(R.id.textSavedShowTitle);
        textShowSDate = view.findViewById(R.id.textShowSDate);
        textShowSTime = view.findViewById(R.id.textShowSTime);
        ivBackImage = view.findViewById(R.id.ivBackImage);




        //recyclerview
        rvShowTripD = view.findViewById(R.id.rvShowTripD);
        rvShowTripD.setLayoutManager(new LinearLayoutManager(activity));


        //從上一頁bundle帶過來（不包含圖片）
        final Bundle bundle = getArguments();
        tripName = bundle.getString("tripTitle");
        tripId = bundle.getString("tripId");
        startDate = bundle.getString("startDate");
        hostId = bundle.getInt("memberId");
        mbrStatus = bundle.getInt("mbrStatus");
        final String startTime = bundle.getString("startTime");
        int imageSize = getResources().getDisplayMetrics().widthPixels / 2;
        String Url = Common.URL_SERVER + "Trip_M_Servlet";
        groupImageTask = new ImageTask(Url, tripId, imageSize,ivBackImage);
        groupImageTask.execute();

        Log.e("mbrStatus", String.valueOf(mbrStatus));

        bundle2.putString("tripId",tripId);
        bundle2.putInt("memberId",hostId);

        textSavedShowTitle.setText(tripName);
        textShowSDate.setText(startDate);
        textShowSTime.setText(startTime);
        //按下參加按鈕後，顯示已送出要求按鈕
        ibExitGroup = view.findViewById(R.id.ibExiGroup);
        ibMbrFill = view.findViewById(R.id.ibMbrFill);
        ibCheckGroup = view.findViewById(R.id.ibCheckGroup);

        //參加揪團人數按鈕
        btJoinGroup = view.findViewById(R.id.btJoinGroup);
        //判斷是否為主揪人所建的行程，true = 不顯示想參加
        final SharedPreferences pref = activity.getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
        //int status = bundle.getInt("status");
        String mId = pref.getString("memberId",  null);
        final String mId2 = bundle.getInt("memberId")+"";
        Log.e(TAG, "mid## " + mId + " and " + "memberId " + mId2);

        //判斷是否已參加---------------------------------------
        String url = Common.URL_SERVER +"Trip_Group_Servlet";

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action","getMyGroup");
        jsonObject.addProperty("trip_Id",tripId);
        jsonObject.addProperty("memberId",mId);

        try {
            String result = new CommonTask(url, jsonObject.toString()).execute().get();
            checkCount = Integer.parseInt(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //---------------------------------------------------

        //揪團狀態碼 - 1 -> 等待審核
        //           2 -> 成功加入

        if (mId.equals(mId2)) {
            btJoinGroup.setVisibility(View.GONE);
            checkCount = 3 ;
        }
        else if(checkCount == 1){
            btJoinGroup.setVisibility(View.GONE);
            ibCheckGroup.setVisibility(View.VISIBLE);

        }
        else if(checkCount == 2){
            btJoinGroup.setVisibility(View.GONE);
            ibExitGroup.setVisibility(View.VISIBLE);
        }

         else {
            if (mbrStatus != 1) {
                btJoinGroup.setVisibility(View.VISIBLE);
                btJoinGroup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Common.networkConnected(activity)) {
                            String url = Common.URL_SERVER + "TripServlet";

                            String tripId = bundle.getString("tripId");
                            //參加人的會員ID
                            int memberId = Integer.parseInt(pref.getString("memberId", ""));

                            TripGroup tripGroup = new TripGroup(tripId, memberId);

                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("action", "insertGroup");
                            jsonObject.addProperty("tripGroup", new Gson().toJson(tripGroup));


                            int count = 0;
                            try {
                                String result = new CommonTask(url, jsonObject.toString()).execute().get();
                                count = Integer.parseInt(result);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (count == 0) {
                                Common.showToast(activity, "已申請加入！");
                                btJoinGroup.setVisibility(View.GONE);
                                ibCheckGroup.setVisibility(View.VISIBLE);
                                sendJoinMsg(memberId);
                            } else {
                                Common.showToast(activity, "加入揪團失敗！");
                                Log.d(TAG, "Trip Fail: " + count);
                            }
                        } else {
                            Common.showToast(activity, "請檢查網路連線");
                        }
                    }
                });
            }else {
                btJoinGroup.setVisibility(View.GONE);
                ibMbrFill.setVisibility(View.VISIBLE);
            }
        }

         ibExitGroup.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 new AlertDialog.Builder(activity)
                         .setTitle("退出揪團")
                         .setIcon(R.drawable.trippericon)
                         .setMessage("確定退出「"+tripName+"」的揪團嗎？")
                         .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                             @Override
                             public void onClick(DialogInterface dialog, int which) {
                                 if (Common.networkConnected(activity)) {
                                     String url = Common.URL_SERVER + "TripServlet";

                                     String tripId = bundle.getString("tripId");
                                     //參加人的會員ID
                                     int memberId = Integer.parseInt(pref.getString("memberId", ""));

                                     TripGroup tripGroup = new TripGroup(tripId, memberId);

                                     JsonObject jsonObject = new JsonObject();
                                     jsonObject.addProperty("action", "deleteGroup");
                                     jsonObject.addProperty("tripGroup", new Gson().toJson(tripGroup));

                                     int count = 0;
                                     try {
                                         String result = new CommonTask(url, jsonObject.toString()).execute().get();
                                         count = Integer.parseInt(result);
                                     } catch (Exception e) {
                                         e.printStackTrace();
                                     }
                                     if (count == 1) {
                                         Common.showToast(activity,"已退出此揪團");
                                         ibExitGroup.setVisibility(View.GONE);
                                         btJoinGroup.setVisibility(View.VISIBLE);
                                         checkCount = 0 ;
                                     }
                                 } else {
                                     Common.showToast(activity, "請檢查網路連線");
                                 }
                             }
                         })
                         .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                             @Override
                             public void onClick(DialogInterface dialog, int which) {
                                 dialog.cancel();
                             }
                         })
                         .setCancelable(true)
                         .show();
             }
         });




        //取得list
        //第一天景點
        final List<Trip_LocInfo> locList1 = getLoc1();
        showLocList1(locList1);

        //第二天景點
        List<Trip_LocInfo> locList2 = null;
        try {
            locList2 = getLoc2();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        showLocList2(locList2);

        //第三天景點
        List<Trip_LocInfo> locList3 = null;
        try {
            locList3 = getLoc3();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        showLocList3(locList3);

        //第四天景點
        List<Trip_LocInfo> locList4 = null;
        try {
            locList4 = getLoc4();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        showLocList4(locList4);

        //第五天景點
        List<Trip_LocInfo> locList5 = null;
        try {
            locList5 = getLoc5();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        showLocList5(locList5);

        //第六天景點
        List<Trip_LocInfo> locList6 = null;
        try {
            locList6 = getLoc6();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        showLocList6(locList6);

        List<String> dayList = null;
        try {
            dayList = getDays();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        showDays(dayList);

        final List<Trip_LocInfo> finalLocList2 = locList2;
        final List<Trip_LocInfo> finalLocList3 = locList3;
        final List<Trip_LocInfo> finalLocList4 = locList4;
        final List<Trip_LocInfo> finalLocList5 = locList5;
        final List<Trip_LocInfo> finalLocList6 = locList6;

        //Button 跳至對應天數
        btDay1 = view.findViewById(R.id.btDay1);
        btDay1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rvShowTripD.smoothScrollToPosition(0);
            }
        });

        btDay2 = view.findViewById(R.id.btDay2);
        if (finalLocList2.size() == 0) {
            btDay2.setVisibility(View.GONE);
        }
        btDay2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rvShowTripD.smoothScrollToPosition(locList1.size() + 1 + 1 + 2);
            }
        });

        btDay3 = view.findViewById(R.id.btDay3);
        if (finalLocList3.size() == 0) {
            btDay3.setVisibility(View.GONE);
        }
        btDay3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rvShowTripD.smoothScrollToPosition(locList1.size() + 1 + finalLocList2.size() + 1 + 1 + 2);
            }
        });

        btDay4 = view.findViewById(R.id.btDay4);
        if (finalLocList4.size() == 0) {
            btDay4.setVisibility(View.GONE);
        }
        btDay4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rvShowTripD.smoothScrollToPosition(locList1.size() + 1 + finalLocList2.size() + 1 + finalLocList3.size() + 1 + 1 + 2);
            }
        });

        btDay5 = view.findViewById(R.id.btDay5);
        if (finalLocList5.size() == 0) {
            btDay5.setVisibility(View.GONE);
        }
        btDay5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rvShowTripD.smoothScrollToPosition(locList1.size() + 1 + 1 + finalLocList2.size() + 1 + finalLocList3.size() + 1 + finalLocList4.size() + 1);
            }
        });

        btDay6 = view.findViewById(R.id.btDay6);
        if (finalLocList6.size() == 0) {
            btDay6.setVisibility(View.GONE);
        }
        btDay6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rvShowTripD.smoothScrollToPosition(locList1.size() + 1 + 1 + finalLocList2.size() + 1 + finalLocList3.size() + 1 + finalLocList4.size() + 1 + finalLocList5.size() + 1);
            }
        });


    }

    //創建RecyclerView 的 Adapter
    private class GroupLocAdapter extends RecyclerView.Adapter {
        private LayoutInflater layoutInflater;
        private View visibleView;
        private int imageSize;
        private List<String> dayList;
        private List<Trip_LocInfo> tripList1;
        private List<Trip_LocInfo> tripList2;
        private List<Trip_LocInfo> tripList3;
        private List<Trip_LocInfo> tripList4;
        private List<Trip_LocInfo> tripList5;
        private List<Trip_LocInfo> tripList6;

        GroupLocAdapter (Context context, List<Trip_LocInfo> locList) {
            layoutInflater = LayoutInflater.from(context);
            this.tripList1 = locList;
            this.tripList2 = locList;
        }

        @Override
        public int getItemViewType(int position) {
            int day1count = tripList1.size() + 1;
            int day2count = tripList2.size() + 1;
            int day3count = tripList3.size() + 1;
            int day4count = tripList4.size() + 1;
            int day5count = tripList5.size() + 1;
            int day6count = tripList6.size() + 1;

            if (position == 0 || position == day1count ||
                    position == day1count + day2count ||
                    position == day1count + day2count + day3count ||
                    position == day1count + day2count + day3count + day4count ||
                    position == day1count + day2count + day3count + day4count + day5count ||
                    position == day1count + day2count + day3count + day4count + day5count + day6count) {
                return 2;
            } else if (position > 0 && position < day1count) {
                return 1;
            } else if (position > day1count && position < day1count + day2count) {
                return 1;
            } else if (position > day1count + day2count && position < day1count + day2count + day3count) {
                return 1;
            } else if (position > day1count + day2count + day3count && position < day1count + day2count + day3count + day4count) {
                return 1;
            } else if (position > day1count + day2count + day3count + day4count && position < day1count + day2count + day3count + day4count + day5count) {
                return 1;
            } else if (position > day1count + day2count + day3count + day4count + day5count) {
                return 1;
            } else {
                return 0;
            }
        }

        void setDays(List<String> dayList) {
            this.dayList = dayList;
        }

        void setLocs1(List<Trip_LocInfo> locList) {
            this.tripList1 = locList;
        }

        void setLocs2(List<Trip_LocInfo> locList) {
            this.tripList2 = locList;
        }

        void setLocs3(List<Trip_LocInfo> locList) {
            this.tripList3 = locList;
        }

        void setLocs4(List<Trip_LocInfo> locList) {
            this.tripList4 = locList;
        }

        void setLocs5(List<Trip_LocInfo> locList) {
            this.tripList5 = locList;
        }

        void setLocs6(List<Trip_LocInfo> locList) {
            this.tripList6 = locList;
        }

        @Override
        public int getItemCount() {
            int nameNum = dayList == null ? 0 : dayList.size();
            int locNum1 = tripList1 == null ? 0 : tripList1.size();
            int locNum2 = tripList2 == null ? 0 : tripList2.size();
            int locNum3 = tripList3 == null ? 0 : tripList3.size();
            int locNum4 = tripList4 == null ? 0 : tripList4.size();
            int locNum5 = tripList5 == null ? 0 : tripList5.size();
            int locNum6 = tripList6 == null ? 0 : tripList6.size();

            return locNum1 + nameNum + locNum2 + locNum3 + locNum4 + locNum5 + locNum6;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == 1) {
                View itemView = layoutInflater.inflate(R.layout.item_view_location_detail_card, parent, false);
                return new GroupLocAdapter.ViewHolderLoc(itemView);
            } else {
                View itemView = layoutInflater.inflate(R.layout.item_view_createblog_day, parent, false);
                return new GroupLocAdapter.ViewHolderDay(itemView);
            }
        }

        class ViewHolderLoc extends RecyclerView.ViewHolder {
            TextView LocChosen, LocAddChosen, LocStayTimeChosen, ShowLocMemo;
            ImageButton btExpand;
            LinearLayout expandableView;

            public ViewHolderLoc(View itemView) {
                super(itemView);
                LocChosen = itemView.findViewById(R.id.textLocChosen);
                LocAddChosen = itemView.findViewById(R.id.textLocAddChosen);
                LocStayTimeChosen = itemView.findViewById(R.id.textLocStayTimeChosen);
                ShowLocMemo = itemView.findViewById(R.id.textShowLocMemo);
                btExpand = itemView.findViewById(R.id.btExpand);
                expandableView = itemView.findViewById(R.id.expandableView);
            }
        }

        //秀第幾天的Adapter
        class ViewHolderDay extends RecyclerView.ViewHolder {
            TextView tvDay;
            CardView cvDay;

            ViewHolderDay(View itemView) {
                super(itemView);
                tvDay = itemView.findViewById(R.id.tvDay);
                cvDay = itemView.findViewById(R.id.cvDay);

            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            int locNum1 = tripList1 == null ? 0 : tripList1.size();
            int locNum2 = tripList2 == null ? 0 : tripList2.size();
            int locNum3 = tripList3 == null ? 0 : tripList3.size();
            int locNum4 = tripList4 == null ? 0 : tripList4.size();
            int locNum5 = tripList5 == null ? 0 : tripList5.size();
            int locNum6 = tripList6 == null ? 0 : tripList6.size();


            int day1count = locNum1 + 1;
            int day2count = day1count + locNum2 + 1;
            int day3count = day2count + locNum3 + 1;
            int day4count = day3count + locNum4 + 1;
            int day5count = day4count + locNum5 + 1;
            int day6count = day5count + locNum6 + 1;

            if (position == 0 ||
                    position == day1count ||
                    position == day2count ||
                    position == day3count ||
                    position == day4count ||
                    position == day5count ||
                    position == day6count) {
                String trip_day = "";

                if (position == 0) {
                    trip_day = dayList.get(position);
                }
                if (position == day1count && locNum2 != 0) {
                    trip_day = dayList.get(position - locNum1);
                }
                if (position == day2count && locNum3 != 0) {
                    trip_day = dayList.get(position - locNum1 - locNum2);
                }

                if (position == day3count && locNum4 != 0) {
                    trip_day = dayList.get(position - locNum1 - locNum2 - locNum3);
                }

                if (position == day4count && locNum5 != 0) {
                    trip_day = dayList.get(position - locNum1 - locNum2 - locNum3 - locNum4);
                }
                if (position == day5count && locNum6 != 0) {
                    trip_day = dayList.get(position - locNum1 - locNum2 - locNum3 - locNum4 - locNum5);
                }

                if (position == day6count) {
                    trip_day = dayList.get(position - locNum1 - locNum2 - locNum3 - locNum4 - locNum5 - locNum6);
                }

                GroupTripPage.GroupLocAdapter.ViewHolderDay viewHolderDay = (GroupTripPage.GroupLocAdapter.ViewHolderDay) holder;
                viewHolderDay.tvDay.setText(trip_day);
                if(trip_day.length() < 1){
                    viewHolderDay.cvDay.setVisibility(View.GONE);
                }else{
                    viewHolderDay.cvDay.setVisibility(View.VISIBLE);
                }
            }

            if (position > 0 && position < day1count) {
                Trip_LocInfo trip_locInfo = tripList1.get(position - 1);
                final GroupLocAdapter.ViewHolderLoc viewHolderLoc = (GroupLocAdapter.ViewHolderLoc) holder;
                viewHolderLoc.LocChosen.setText(trip_locInfo.getName());
                viewHolderLoc.LocAddChosen.setText(trip_locInfo.getAddress());
                viewHolderLoc.LocStayTimeChosen.setText(trip_locInfo.getStaytime());
                viewHolderLoc.ShowLocMemo.setText(trip_locInfo.getMemo());
                //CardView上面的More Button功能
                viewHolderLoc.btExpand.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final PopupMenu popupMenu = new PopupMenu(activity, v, Gravity.END);
                        popupMenu.inflate(R.menu.trip_saved_more_button_menu);
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @SuppressLint("LongLogTag")
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.mShowmore:
                                        switch (viewHolderLoc.expandableView.getVisibility()) {
                                            case View.VISIBLE:
                                                viewHolderLoc.expandableView.setVisibility(View.GONE);
                                                break;
                                            case View.GONE:
                                                if (visibleView != null) {
                                                    visibleView.setVisibility(View.GONE);
                                                }
                                                viewHolderLoc.expandableView.setVisibility(View.VISIBLE);
                                                visibleView = viewHolderLoc.expandableView;
                                                break;
                                            case View.INVISIBLE:
                                                break;
                                        }
                                        break;
                                }
                                return true;
                            }
                        });
                        popupMenu.show();
                    }
                });
            }

            if (position > day1count && position < day2count) {
                Trip_LocInfo trip_locInfo = tripList2.get(position - 1 - day1count);
                final ViewHolderLoc viewHolderLoc = (ViewHolderLoc) holder;
                viewHolderLoc.LocChosen.setText(trip_locInfo.getName());
                viewHolderLoc.LocAddChosen.setText(trip_locInfo.getAddress());
                viewHolderLoc.LocStayTimeChosen.setText(trip_locInfo.getStaytime());
                viewHolderLoc.ShowLocMemo.setText(trip_locInfo.getMemo());
                viewHolderLoc.btExpand.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final PopupMenu popupMenu = new PopupMenu(activity, v, Gravity.END);
                        popupMenu.inflate(R.menu.trip_saved_more_button_menu);
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @SuppressLint("LongLogTag")
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.mShowmore:
                                        switch (viewHolderLoc.expandableView.getVisibility()) {
                                            case View.VISIBLE:
                                                viewHolderLoc.expandableView.setVisibility(View.GONE);
                                                break;
                                            case View.GONE:
                                                if (visibleView != null) {
                                                    visibleView.setVisibility(View.GONE);
                                                }
                                                viewHolderLoc.expandableView.setVisibility(View.VISIBLE);
                                                visibleView = viewHolderLoc.expandableView;
                                                break;
                                            case View.INVISIBLE:
                                                break;
                                        }
                                        break;
                                }
                                return true;
                            }
                        });
                        popupMenu.show();
                    }
                });
            }

            if (position > day2count && position < day3count) {
                Trip_LocInfo trip_locInfo = tripList3.get(position - 1 - day2count);
                final GroupLocAdapter.ViewHolderLoc viewHolderLoc = (GroupLocAdapter.ViewHolderLoc) holder;
                viewHolderLoc.LocChosen.setText(trip_locInfo.getName());
                viewHolderLoc.LocAddChosen.setText(trip_locInfo.getAddress());
                viewHolderLoc.LocStayTimeChosen.setText(trip_locInfo.getStaytime());
                viewHolderLoc.ShowLocMemo.setText(trip_locInfo.getMemo());
                viewHolderLoc.btExpand.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final PopupMenu popupMenu = new PopupMenu(activity, v, Gravity.END);
                        popupMenu.inflate(R.menu.trip_saved_more_button_menu);
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @SuppressLint("LongLogTag")
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.mShowmore:
                                        switch (viewHolderLoc.expandableView.getVisibility()) {
                                            case View.VISIBLE:
                                                viewHolderLoc.expandableView.setVisibility(View.GONE);
                                                break;
                                            case View.GONE:
                                                if (visibleView != null) {
                                                    visibleView.setVisibility(View.GONE);
                                                }
                                                viewHolderLoc.expandableView.setVisibility(View.VISIBLE);
                                                visibleView = viewHolderLoc.expandableView;
                                                break;
                                            case View.INVISIBLE:
                                                break;
                                        }
                                        break;
                                }
                                return true;
                            }
                        });
                        popupMenu.show();
                    }
                });
            }

            if (position > day3count && position < day4count) {
                Trip_LocInfo trip_locInfo = tripList4.get(position - 1 - day3count);
                final GroupLocAdapter.ViewHolderLoc viewHolderLoc = (GroupLocAdapter.ViewHolderLoc) holder;
                viewHolderLoc.LocChosen.setText(trip_locInfo.getName());
                viewHolderLoc.LocAddChosen.setText(trip_locInfo.getAddress());
                viewHolderLoc.LocStayTimeChosen.setText(trip_locInfo.getStaytime());
                viewHolderLoc.ShowLocMemo.setText(trip_locInfo.getMemo());
                viewHolderLoc.btExpand.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final PopupMenu popupMenu = new PopupMenu(activity, v, Gravity.END);
                        popupMenu.inflate(R.menu.trip_saved_more_button_menu);
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @SuppressLint("LongLogTag")
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.mShowmore:
                                        switch (viewHolderLoc.expandableView.getVisibility()) {
                                            case View.VISIBLE:
                                                viewHolderLoc.expandableView.setVisibility(View.GONE);
                                                break;
                                            case View.GONE:
                                                if (visibleView != null) {
                                                    visibleView.setVisibility(View.GONE);
                                                }
                                                viewHolderLoc.expandableView.setVisibility(View.VISIBLE);
                                                visibleView = viewHolderLoc.expandableView;
                                                break;
                                            case View.INVISIBLE:
                                                break;
                                        }
                                        break;
                                }
                                return true;
                            }
                        });
                        popupMenu.show();
                    }
                });
            }

            if (position > day4count && position < day5count) {
                Trip_LocInfo trip_locInfo = tripList5.get(position - 1 - day4count);
                final GroupLocAdapter.ViewHolderLoc viewHolderLoc = (GroupLocAdapter.ViewHolderLoc) holder;
                viewHolderLoc.LocChosen.setText(trip_locInfo.getName());
                viewHolderLoc.LocAddChosen.setText(trip_locInfo.getAddress());
                viewHolderLoc.LocStayTimeChosen.setText(trip_locInfo.getStaytime());
                viewHolderLoc.ShowLocMemo.setText(trip_locInfo.getMemo());
                viewHolderLoc.btExpand.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final PopupMenu popupMenu = new PopupMenu(activity, v, Gravity.END);
                        popupMenu.inflate(R.menu.trip_saved_more_button_menu);
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @SuppressLint("LongLogTag")
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.mShowmore:
                                        switch (viewHolderLoc.expandableView.getVisibility()) {
                                            case View.VISIBLE:
                                                viewHolderLoc.expandableView.setVisibility(View.GONE);
                                                break;
                                            case View.GONE:
                                                if (visibleView != null) {
                                                    visibleView.setVisibility(View.GONE);
                                                }
                                                viewHolderLoc.expandableView.setVisibility(View.VISIBLE);
                                                visibleView = viewHolderLoc.expandableView;
                                                break;
                                            case View.INVISIBLE:
                                                break;
                                        }
                                        break;
                                }
                                return true;
                            }
                        });
                        popupMenu.show();
                    }
                });
            }

            if (position > day5count && position < day6count) {
                Trip_LocInfo trip_locInfo = tripList6.get(position - 1 - day5count);
                final GroupLocAdapter.ViewHolderLoc viewHolderLoc = (GroupLocAdapter.ViewHolderLoc) holder;
                viewHolderLoc.LocChosen.setText(trip_locInfo.getName());
                viewHolderLoc.LocAddChosen.setText(trip_locInfo.getAddress());
                viewHolderLoc.LocStayTimeChosen.setText(trip_locInfo.getStaytime());
                viewHolderLoc.ShowLocMemo.setText(trip_locInfo.getMemo());
                viewHolderLoc.btExpand.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final PopupMenu popupMenu = new PopupMenu(activity, v, Gravity.END);
                        popupMenu.inflate(R.menu.trip_saved_more_button_menu);
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @SuppressLint("LongLogTag")
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.mShowmore:
                                        switch (viewHolderLoc.expandableView.getVisibility()) {
                                            case View.VISIBLE:
                                                viewHolderLoc.expandableView.setVisibility(View.GONE);
                                                break;
                                            case View.GONE:
                                                if (visibleView != null) {
                                                    visibleView.setVisibility(View.GONE);
                                                }
                                                viewHolderLoc.expandableView.setVisibility(View.VISIBLE);
                                                visibleView = viewHolderLoc.expandableView;
                                                break;
                                            case View.INVISIBLE:
                                                break;
                                        }
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


    }


    //----------------取得景點List並放入Adapter--------------------

    //Day 1
    private List<Trip_LocInfo> getLoc1() {
        List<Trip_LocInfo> locList = new ArrayList<>();
        if (Common.networkConnected(activity)) {
            String Url = Common.URL_SERVER + "Trip_D_Servlet";
            DateAndId dateAndId = new DateAndId(startDate, tripId);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getLocName");
            jsonObject.addProperty("dateAndId", new Gson().toJson(dateAndId));
            String jsonOut = jsonObject.toString();
            tripGet1Task = new CommonTask(Url, jsonOut);
            try {
                String jsonIn = tripGet1Task.execute().get();
                Type listtype = new TypeToken<List<Trip_LocInfo>>() {
                }.getType();
                locList = new Gson().fromJson(jsonIn, listtype);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Common.showToast(activity, "No Internet");
        }

        return locList;
    }

    private void showLocList1(List<Trip_LocInfo> locList) {
        if (locList == null || locList.isEmpty()) {
            Common.showToast(activity, "搜尋不到行程");
        }
        GroupTripPage.GroupLocAdapter tripLocAdapter = (GroupTripPage.GroupLocAdapter) rvShowTripD.getAdapter();
        if (tripLocAdapter == null) {
            rvShowTripD.setAdapter(new GroupTripPage.GroupLocAdapter(activity, locList));
        } else {
            tripLocAdapter.setLocs1(locList);
            tripLocAdapter.notifyDataSetChanged();
        }
    }

    //Day 2
    private List<Trip_LocInfo> getLoc2() throws ParseException {
        String date2 = DateUtil.date4day(startDate, 1);
        List<Trip_LocInfo> locList = new ArrayList<>();
        if (Common.networkConnected(activity)) {
            String Url = Common.URL_SERVER + "Trip_D_Servlet";
            DateAndId dateAndId = new DateAndId(date2, tripId);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getLocName");
            jsonObject.addProperty("dateAndId", new Gson().toJson(dateAndId));
            String jsonOut = jsonObject.toString();
            tripGet1Task = new CommonTask(Url, jsonOut);
            try {
                String jsonIn = tripGet1Task.execute().get();
                Type listtype = new TypeToken<List<Trip_LocInfo>>() {
                }.getType();
                locList = new Gson().fromJson(jsonIn, listtype);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Common.showToast(activity, "No Internet");
        }

        return locList;
    }

    private void showLocList2(List<Trip_LocInfo> locList) {
        GroupTripPage.GroupLocAdapter tripLocAdapter = (GroupTripPage.GroupLocAdapter) rvShowTripD.getAdapter();
        if (tripLocAdapter == null) {
            rvShowTripD.setAdapter(new GroupTripPage.GroupLocAdapter(activity, locList));
        } else {
            tripLocAdapter.setLocs2(locList);
            tripLocAdapter.notifyDataSetChanged();
        }
    }

    //Day 3
    private List<Trip_LocInfo> getLoc3() throws ParseException {
        String date3 = DateUtil.date4day(startDate, 2);
        List<Trip_LocInfo> locList = new ArrayList<>();
        if (Common.networkConnected(activity)) {
            String Url = Common.URL_SERVER + "Trip_D_Servlet";
            DateAndId dateAndId = new DateAndId(date3, tripId);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getLocName");
            jsonObject.addProperty("dateAndId", new Gson().toJson(dateAndId));
            String jsonOut = jsonObject.toString();
            tripGet1Task = new CommonTask(Url, jsonOut);
            try {
                String jsonIn = tripGet1Task.execute().get();
                Type listtype = new TypeToken<List<Trip_LocInfo>>() {
                }.getType();
                locList = new Gson().fromJson(jsonIn, listtype);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Common.showToast(activity, "No Internet");
        }

        return locList;
    }

    private void showLocList3(List<Trip_LocInfo> locList) {
        GroupTripPage.GroupLocAdapter tripLocAdapter = (GroupTripPage.GroupLocAdapter) rvShowTripD.getAdapter();
        if (tripLocAdapter == null) {
            rvShowTripD.setAdapter(new GroupTripPage.GroupLocAdapter(activity, locList));
        } else {
            tripLocAdapter.setLocs3(locList);
            tripLocAdapter.notifyDataSetChanged();
        }
    }

    //Day 4
    private List<Trip_LocInfo> getLoc4() throws ParseException {
        String date4 = DateUtil.date4day(startDate, 3);
        List<Trip_LocInfo> locList = new ArrayList<>();
        if (Common.networkConnected(activity)) {
            String Url = Common.URL_SERVER + "Trip_D_Servlet";
            DateAndId dateAndId = new DateAndId(date4, tripId);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getLocName");
            jsonObject.addProperty("dateAndId", new Gson().toJson(dateAndId));
            String jsonOut = jsonObject.toString();
            tripGet1Task = new CommonTask(Url, jsonOut);
            try {
                String jsonIn = tripGet1Task.execute().get();
                Type listtype = new TypeToken<List<Trip_LocInfo>>() {
                }.getType();
                locList = new Gson().fromJson(jsonIn, listtype);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Common.showToast(activity, "No Internet");
        }

        return locList;
    }

    private void showLocList4(List<Trip_LocInfo> locList) {
        GroupTripPage.GroupLocAdapter tripLocAdapter = (GroupTripPage.GroupLocAdapter) rvShowTripD.getAdapter();
        if (tripLocAdapter == null) {
            rvShowTripD.setAdapter(new GroupTripPage.GroupLocAdapter(activity, locList));
        } else {
            tripLocAdapter.setLocs4(locList);
            tripLocAdapter.notifyDataSetChanged();
        }
    }

    //Day 5
    private List<Trip_LocInfo> getLoc5() throws ParseException {
        String date5 = DateUtil.date4day(startDate, 4);
        List<Trip_LocInfo> locList = new ArrayList<>();
        if (Common.networkConnected(activity)) {
            String Url = Common.URL_SERVER + "Trip_D_Servlet";
            DateAndId dateAndId = new DateAndId(date5, tripId);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getLocName");
            jsonObject.addProperty("dateAndId", new Gson().toJson(dateAndId));
            String jsonOut = jsonObject.toString();
            tripGet1Task = new CommonTask(Url, jsonOut);
            try {
                String jsonIn = tripGet1Task.execute().get();
                Type listtype = new TypeToken<List<Trip_LocInfo>>() {
                }.getType();
                locList = new Gson().fromJson(jsonIn, listtype);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Common.showToast(activity, "No Internet");
        }

        return locList;
    }

    private void showLocList5(List<Trip_LocInfo> locList) {
        GroupTripPage.GroupLocAdapter tripLocAdapter = (GroupTripPage.GroupLocAdapter) rvShowTripD.getAdapter();
        if (tripLocAdapter == null) {
            rvShowTripD.setAdapter(new GroupTripPage.GroupLocAdapter(activity, locList));
        } else {
            tripLocAdapter.setLocs5(locList);
            tripLocAdapter.notifyDataSetChanged();
        }
    }

    //Day 6
    private List<Trip_LocInfo> getLoc6() throws ParseException {
        String date6 = DateUtil.date4day(startDate, 5);
        List<Trip_LocInfo> locList = new ArrayList<>();
        if (Common.networkConnected(activity)) {
            String Url = Common.URL_SERVER + "Trip_D_Servlet";
            DateAndId dateAndId = new DateAndId(date6, tripId);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getLocName");
            jsonObject.addProperty("dateAndId", new Gson().toJson(dateAndId));
            String jsonOut = jsonObject.toString();
            tripGet1Task = new CommonTask(Url, jsonOut);
            try {
                String jsonIn = tripGet1Task.execute().get();
                Type listtype = new TypeToken<List<Trip_LocInfo>>() {
                }.getType();
                locList = new Gson().fromJson(jsonIn, listtype);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Common.showToast(activity, "No Internet");
        }

        return locList;
    }

    private void showLocList6(List<Trip_LocInfo> locList) {
        GroupTripPage.GroupLocAdapter tripLocAdapter = (GroupTripPage.GroupLocAdapter) rvShowTripD.getAdapter();
        if (tripLocAdapter == null) {
            rvShowTripD.setAdapter(new GroupTripPage.GroupLocAdapter(activity, locList));
        } else {
            tripLocAdapter.setLocs6(locList);
            tripLocAdapter.notifyDataSetChanged();
        }
    }

    //----------------取得天數List並放入Adapter--------------------

    private List<String> getDays() throws ParseException {
        List<String> dayList = new ArrayList<>();

        dayList.add(startDate);
        dayList.add(startDate = DateUtil.date4day(startDate, 1));
        dayList.add(startDate = DateUtil.date4day(startDate, 1));
        dayList.add(startDate = DateUtil.date4day(startDate, 1));
        dayList.add(startDate = DateUtil.date4day(startDate, 1));
        dayList.add(startDate = DateUtil.date4day(startDate, 1));
        return dayList;
    }

    private void showDays(List<String> dayList) {
        if (dayList == null || dayList.isEmpty()) {
            Common.showToast(activity, "搜尋不到行程");
        }
        GroupTripPage.GroupLocAdapter tripLocAdapter = (GroupTripPage.GroupLocAdapter) rvShowTripD.getAdapter();
        if (tripLocAdapter == null) {
//            rvShowTripD.setAdapter(new TripLocAdapter(activity, dayList));
        } else {
            tripLocAdapter.setDays(dayList);
            tripLocAdapter.notifyDataSetChanged();
        }
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

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        if (checkCount == 2 || checkCount == 3) {
            inflater.inflate(R.menu.app_bar_group_member, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Navigation.findNavController(textSavedShowTitle).navigate(R.id.action_groupTripPage_to_groupFragment);
                break;

            case R.id.btMemberList:
                Navigation.findNavController(textSavedShowTitle).navigate(R.id.action_groupTripPage_to_groupMbrListFragment,bundle2);
        }
        return super.onOptionsItemSelected(item);
    }

    //發送請求推播
    private void sendJoinMsg(int memberId){

        AppMessage message = null;
        String msgType = Common.GROUP_TYPE ;
        String nickName = pref.getString("nickName","");
        String title =  "申請揪團通知";
        String body = nickName + "申請加入「" + tripName + "」的揪團！";
        int stat = 0;
        int sendId = memberId ;
        int recId = hostId ;
        message = new AppMessage(msgType , memberId , title , body ,stat , sendId , recId);
        SendMessage sendMessage = new SendMessage(activity, message);
        sendMessage.sendMessage();
    }

}

