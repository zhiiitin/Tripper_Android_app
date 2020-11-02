package com.example.tripper_android_app.trip;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.tripper_android_app.MainActivity;
import com.example.tripper_android_app.R;
import com.example.tripper_android_app.blog.Blog_SpotInfo;
import com.example.tripper_android_app.blog.CreateBlogFragment;
import com.example.tripper_android_app.blog.DateAndId;
import com.example.tripper_android_app.location.Location;
import com.example.tripper_android_app.setting.member.Member;
import com.example.tripper_android_app.task.CommonTask;
import com.example.tripper_android_app.util.Common;
import com.example.tripper_android_app.util.DateUtil;
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
    private Button btDay1, btDay2, btDay3, btDay4, btDay5, btDay6;
    private Trip_M tripM;
    private CommonTask tripGetAllTask;
    private String startDate, tripId;
    private ImageButton btManageGroupPpl;

    private int num_columns = 4;
    private byte[] photo;
    private static final int REQ_TAKE_PICTURE = 0;
    private static final int REQ_PICK_PICTURE = 1;
    private static final int REQ_CROP_PICTURE = 2;
    private Uri contentUri;
    private Bitmap bitmap;

    private CommonTask tripGet1Task;


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
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("行程瀏覽");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorForWhite));
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        textSavedShowTitle = view.findViewById(R.id.textSavedShowTitle);
        textShowSDate = view.findViewById(R.id.textShowSDate);
        textShowSTime = view.findViewById(R.id.textShowSTime);
//        btJoinGroup = view.findViewById(R.id.btJoinGroup);

        //recyclerview
        rvShowTripD = view.findViewById(R.id.rvShowTripD);
        rvShowTripD.setLayoutManager(new LinearLayoutManager(activity));


        //從上一頁bundle帶過來（不包含圖片）
        Bundle bundle = getArguments();
        String TripName = bundle.getString("tripTitle");
        tripId = bundle.getString("tripId");
        startDate = bundle.getString("startDate");
        String startTime = bundle.getString("startTime");

        textSavedShowTitle.setText(TripName);
        textShowSDate.setText(startDate);
        textShowSTime.setText(startTime);


        //揪團人數按鈕
        btManageGroupPpl = view.findViewById(R.id.btManageGroupPpl);
        //判斷是否為主揪人所建的行程，true = 顯示按鈕
        SharedPreferences pref = activity.getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
        int status = bundle.getInt("status");
        String mId = pref.getString("memberId", Common.PREF_FILE + "");
        Log.e(TAG, "STAUS## " + status + " and " + "memberId " + mId);

        if (mId.equals(pref.getString("memberId", Common.PREF_FILE + "")) && status == 1) {
            btManageGroupPpl.setVisibility(View.VISIBLE);
            btManageGroupPpl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Navigation.findNavController(v)
                            .navigate(R.id.action_tripHasSavedPage_to_groupManageFragment);
                }
            });
        } else {
            btManageGroupPpl.setVisibility(View.GONE);
        }


        btManageGroupPpl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v)
                        .navigate(R.id.action_tripHasSavedPage_to_groupManageFragment);
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
    private class TripLocAdapter extends RecyclerView.Adapter {
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

        TripLocAdapter(Context context, List<Trip_LocInfo> locList) {
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
                return new ViewHolderLoc(itemView);
            } else {
                View itemView = layoutInflater.inflate(R.layout.item_view_createblog_day, parent, false);
                return new ViewHolderDay(itemView);
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

            ViewHolderDay(View itemView) {
                super(itemView);
                tvDay = itemView.findViewById(R.id.tvDay);

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

                ViewHolderDay viewHolderDay = (ViewHolderDay) holder;
                viewHolderDay.tvDay.setText(trip_day);
            }

            if (position > 0 && position < day1count) {
                Trip_LocInfo trip_locInfo = tripList1.get(position - 1);
                final ViewHolderLoc viewHolderLoc = (ViewHolderLoc) holder;
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

            if (position > day3count && position < day4count) {
                Trip_LocInfo trip_locInfo = tripList4.get(position - 1 - day3count);
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

            if (position > day4count && position < day5count) {
                Trip_LocInfo trip_locInfo = tripList5.get(position - 1 - day4count);
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

            if (position > day5count && position < day6count) {
                Trip_LocInfo trip_locInfo = tripList6.get(position - 1 - day5count);
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
        TripLocAdapter tripLocAdapter = (TripLocAdapter) rvShowTripD.getAdapter();
        if (tripLocAdapter == null) {
            rvShowTripD.setAdapter(new TripLocAdapter(activity, locList));
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
        TripLocAdapter tripLocAdapter = (TripLocAdapter) rvShowTripD.getAdapter();
        if (tripLocAdapter == null) {
            rvShowTripD.setAdapter(new TripLocAdapter(activity, locList));
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
        TripLocAdapter tripLocAdapter = (TripLocAdapter) rvShowTripD.getAdapter();
        if (tripLocAdapter == null) {
            rvShowTripD.setAdapter(new TripLocAdapter(activity, locList));
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
        TripLocAdapter tripLocAdapter = (TripLocAdapter) rvShowTripD.getAdapter();
        if (tripLocAdapter == null) {
            rvShowTripD.setAdapter(new TripLocAdapter(activity, locList));
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
        TripLocAdapter tripLocAdapter = (TripLocAdapter) rvShowTripD.getAdapter();
        if (tripLocAdapter == null) {
            rvShowTripD.setAdapter(new TripLocAdapter(activity, locList));
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
        TripLocAdapter tripLocAdapter = (TripLocAdapter) rvShowTripD.getAdapter();
        if (tripLocAdapter == null) {
            rvShowTripD.setAdapter(new TripLocAdapter(activity, locList));
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
        TripLocAdapter tripLocAdapter = (TripLocAdapter) rvShowTripD.getAdapter();
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Navigation.findNavController(textSavedShowTitle).popBackStack(R.id.trip_HomePage, false);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}