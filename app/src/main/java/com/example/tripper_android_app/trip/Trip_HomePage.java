package com.example.tripper_android_app.trip;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tripper_android_app.MainActivity;
import com.example.tripper_android_app.R;
import com.example.tripper_android_app.group.GroupFragment;
import com.example.tripper_android_app.location.Location_D;
import com.example.tripper_android_app.setting.member.Member;
import com.example.tripper_android_app.task.CommonTask;
import com.example.tripper_android_app.task.ImageTask;
import com.example.tripper_android_app.util.Common;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;


import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static android.content.Context.MODE_PRIVATE;
import static androidx.navigation.Navigation.findNavController;

/**
 * 行程主頁面
 *
 * @author cooperhsieh
 * @version 2020.09.09
 */


public class Trip_HomePage extends Fragment {
    private final static String TAG = "TAG_TripHomePage";
    private MainActivity activity;
    private RecyclerView rvTripMainList;
    private List<Trip_M> tripMs;
    private CommonTask tripGetAllTask;
    private CommonTask tripDeleteTask;
    private List<ImageTask> imageTasks;
    private TextView textUserName, tvHomeInfo, tvHomeInfo2;
    private ImageView ivUserPic;
    private RecyclerView rvTripHome;
    private ImageTask tripImageTask;
    private List<Trip_M> tripList;
    private SwipeRefreshLayout swipes;
    private ImageButton editTrip;
    private Trip_M tripM;
    private LinearLayout tripMainLayout;
    //show member
    private Member member;
    private FirebaseAuth auth;
    private FirebaseUser mUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
        setHasOptionsMenu(true);
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_trip__home_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("個人頁面");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorForWhite));
        rvTripMainList = view.findViewById(R.id.rvTripMainList);
        rvTripMainList.setLayoutManager(new LinearLayoutManager(activity));
        activity.setSupportActionBar(toolbar);

        tvHomeInfo = view.findViewById(R.id.tvHomeInfo);
        tvHomeInfo2 = view.findViewById(R.id.tvHomeInfo2);
        tripMainLayout = view.findViewById(R.id.tripMainLayout);


        mUser = auth.getCurrentUser();
        ivUserPic = view.findViewById(R.id.ivUserPic);
        textUserName = view.findViewById(R.id.textUserName);

        //顯示會員資料
        showMember();

        tripMs = new ArrayList<>();
        tripMs = getTripMs();
        showTripList(tripMs);

        //刷新頁面
        swipes = view.findViewById(R.id.swipeHomePage);
        swipes.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipes.setRefreshing(true);
                showTripList(tripMs);
                swipes.setRefreshing(false);
            }
        });


        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottomBar);
        NavController navController = Navigation.findNavController(activity, R.id.trip_HomePage);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        BottomNavigationView bottomNavigationViewTop = view.findViewById(R.id.navigation);
        NavController navControllerTop = Navigation.findNavController(activity, R.id.trip_HomePage);
        NavigationUI.setupWithNavController(bottomNavigationViewTop, navControllerTop);
        Menu itemMenu = bottomNavigationViewTop.getMenu();
        itemMenu.getItem(0).setChecked(true);
        // 每次進到畫面都先更新token
        if (Common.isLogin(activity)) {
            Common.getTokenSendServer(activity);
        }


    }

    private void showTripList(List<Trip_M> tripMs) {
        if (tripMs == null || tripMs.isEmpty()) {
            tvHomeInfo.setVisibility(View.VISIBLE);
            tvHomeInfo2.setVisibility(View.VISIBLE);
            Common.showToast(activity, "尚未建立任何行程");
        } else {
            tripMainLayout.setVisibility(View.VISIBLE);
            tvHomeInfo.setVisibility(View.GONE);
            tvHomeInfo2.setVisibility(View.GONE);
            TripListAdapter tripListAdapter = (TripListAdapter) rvTripMainList.getAdapter();
            if (tripListAdapter == null) {
                rvTripMainList.setAdapter(new TripListAdapter(activity, tripMs));
            } else {
                tripListAdapter.setTripMs(tripMs);
                tripListAdapter.notifyDataSetChanged();
            }
        }
    }

    private List<Trip_M> getTripMs() {
        SharedPreferences pref = activity.getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
        List<Trip_M> tripMs = new ArrayList<>();
        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "TripServlet";
            String id = pref.getString("memberId", Common.PREF_FILE + "");
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAll");
            // 抓取會員ID
            jsonObject.addProperty("memberId", id);
            String jsonOut = jsonObject.toString();
            tripGetAllTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = tripGetAllTask.execute().get();
                Type type = new TypeToken<List<Trip_M>>() {
                }.getType();
                tripMs = new Gson().fromJson(jsonIn, type);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            Common.showToast(activity, "請確認網路連線");
        }
        return tripMs;


        //行程recyclerview
//        rvTripHome = view.findViewById(R.id.rvTripHomePage);
//        rvTripHome.setLayoutManager(new LinearLayoutManager(activity));
//        tripList = getTrip();
//        showTrip(tripList);
    }

    private class TripListAdapter extends RecyclerView.Adapter<TripListAdapter.TripListViewHolder> {
        private LayoutInflater layoutInflater;
        private List<Trip_M> tripMs;
        private int imageSize;

        TripListAdapter(Context context, List<Trip_M> tripMs) {
            layoutInflater = LayoutInflater.from(context);
            this.tripMs = tripMs;
            imageSize = getResources().getDisplayMetrics().widthPixels / 4;
        }

        void setTripMs(List<Trip_M> tripMs) {
            this.tripMs = tripMs;
        }

        @Override
        public int getItemCount() {
            return tripMs == null ? 0 : tripMs.size();
        }

        @NonNull
        @Override
        public TripListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.item_view_trip_home, parent, false);
            return new TripListViewHolder(itemView);
        }

        private class TripListViewHolder extends RecyclerView.ViewHolder {
            ImageView ivLocPic2;
            TextView tvTitle;
            ImageButton editMore;

            public TripListViewHolder(View itemView) {
                super(itemView);
                ivLocPic2 = itemView.findViewById(R.id.ivLocPic2);
                tvTitle = itemView.findViewById(R.id.textLocName);
                editMore = itemView.findViewById(R.id.ibEditTrip);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull TripListViewHolder tripListViewHolder, final int position) {
            final Trip_M tripM = tripMs.get(position);
            String url = Common.URL_SERVER + "TripServlet";
            final String tripId = tripM.getTripId();
            ImageTask imageTask = new ImageTask(url, tripId, imageSize, tripListViewHolder.ivLocPic2);
            imageTasks = new ArrayList<>();
            imageTask.execute();
            imageTasks.add(imageTask);
            Log.d("tripTitle", tripM.getTripTitle());
            tripListViewHolder.tvTitle.setText(tripM.getTripTitle());

            final Bundle bundle = new Bundle();
            bundle.putString("tripId", tripM.getTripId());
            bundle.putString("tripTitle", tripM.getTripTitle());
            bundle.putString("startDate", tripM.getStartDate());
            bundle.putString("startTime", tripM.getStartTime());
            bundle.putInt("status", tripM.getStatus());

            //點擊整張卡片，帶到行程完整資訊頁面
            tripListViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Navigation.findNavController(v).navigate(R.id.action_trip_HomePage_to_tripHasSavedPage, bundle);
                }
            });


            //卡片上的more button for 修改行程 & 刪除行程
            tripListViewHolder.editMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    PopupMenu popupMenu = new PopupMenu(activity, view, Gravity.END);
                    popupMenu.inflate(R.menu.trip_homepage_more_menu);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.tripHomepageEdit:
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("tripM", tripM);

                                    //連線取得行程的景點後，帶去編輯行程頁面
                                    if (Common.networkConnected(activity)) {
                                        String url = Common.URL_SERVER + "TripServlet";

                                        JsonObject jsonObject = new JsonObject();
                                        jsonObject.addProperty("action", "showLocName");
                                        jsonObject.addProperty("tripId", tripId);
                                        String jsonOut = jsonObject.toString();
                                        tripGetAllTask = new CommonTask(url, jsonOut);
                                        try {
                                            String jsonIn = tripGetAllTask.execute().get();
                                            Type type = new TypeToken<Map<String, List<Location_D>>>() {
                                            }.getType();
                                            Common.map = new Gson().fromJson(jsonIn, type);
                                            if (Common.map == null) {
                                                Log.d("DB抓景點資料進map，map空值顯示：", "enter Common.map null");
                                            } else {

                                                Log.d("DB抓景點資料進map，map有東西顯示：", "enter Common.map not null");
                                            }
                                        } catch (ExecutionException | InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        Common.showToast(activity, "請確認網路連線");
                                    }


                                    Navigation.findNavController(view).navigate(R.id.action_trip_HomePage_to_updateTripFragment, bundle);
                                    break;

                                case R.id.tripHomepageDelete:
                                    if (Common.netWorkConnected(activity)) {
                                        String Url = Common.URL_SERVER + "TripServlet";
                                        JsonObject jsonObject = new JsonObject();
                                        jsonObject.addProperty("action", "delete");
                                        jsonObject.addProperty("tripId", tripM.getTripId());
                                        int count = 0;
                                        try {
                                            tripDeleteTask = new CommonTask(Url, jsonObject.toString());
                                            String result = tripDeleteTask.execute().get();
                                            count = Integer.parseInt(result);
                                        } catch (Exception e) {
                                            Log.e(TAG, e.toString());
                                        }
                                        if (count > 0) {
                                            Common.showToast(activity, "刪除失敗");
                                        } else {
                                            tripMs.remove(position);
                                            showTripList(tripMs);
                                            Common.showToast(activity, "刪除成功");
                                        }
                                    } else {
                                        Common.showToast(activity, R.string.textNoNetwork);
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

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        //TODO 判斷是否為會員
        SharedPreferences pref = activity.getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
        boolean login = pref.getBoolean("login", false);
        if (login) {
            inflater.inflate(R.menu.app_bar_button, menu);
        }

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.btCreateTrip) {
            findNavController(this.getView()).navigate(R.id.action_trip_HomePage_to_create_Trip_Fragment);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    //show 使用者資訊
    private void showMember() {
        SharedPreferences pref = activity.getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
        boolean login = pref.getBoolean("login", false);
        if (login) {
            if (Common.networkConnected(activity)) {
                String Url = Common.URL_SERVER + "MemberServlet";
                String account = pref.getString("account", "");
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("action", "getProfile");
                jsonObject.addProperty("account", account);
                try {
                    String jsonIn = new CommonTask(Url, jsonObject.toString()).execute().get();
                    Type listtype = new TypeToken<Member>() {
                    }.getType();
                    member = new Gson().fromJson(jsonIn, listtype);
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
                if (member == null) {
                    pref.edit().putBoolean("login", false).apply();
                    Navigation.findNavController(ivUserPic).navigate(R.id.action_trip_HomePage_to_register_main_Fragment2);

                } else {
                    String userName = member.getNickName();
                    textUserName.setText(userName);

                }
                String nickname = member.getNickName();
                textUserName.setText(" " + nickname + " ");
                pref.edit().putString("memberId", member.getId() + "").apply();


            }
        } else {
            Common.showToast(activity, "no network connection found");
        }
        showMemberPic();

        
    }


    //show UserPic
    private void showMemberPic() {
        if (mUser != null) {
            String Url = Common.URL_SERVER + "MemberServlet";
            int id = member.getId();
            int imageSize = getResources().getDisplayMetrics().widthPixels / 3;
            Bitmap bitmap = null;
            try {
                bitmap = new ImageTask(Url, id, imageSize).execute().get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //若此帳號之資料庫有照片，便使用資料庫的照
            if (bitmap != null) {
                ivUserPic.setImageBitmap(bitmap);
            } else {
                //否則連接到第三方大頭照
                String fbPhotoURL = mUser.getPhotoUrl().toString();
                Glide.with(this).load(fbPhotoURL).into(ivUserPic);
            }

        } else {

            String Url = Common.URL_SERVER + "MemberServlet";
            int id = member.getId();
            int imageSize = getResources().getDisplayMetrics().widthPixels / 3;
            Bitmap bitmap = null;
            try {
                bitmap = new ImageTask(Url, id, imageSize).execute().get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (bitmap != null) {
                ivUserPic.setImageBitmap(bitmap);
            } else {
                ivUserPic.setImageResource(R.drawable.ic_nopicture);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!Common.isLogin(activity)) {
            Navigation.findNavController(this.getView()).navigate(R.id.action_trip_HomePage_to_register_main_Fragment);
            Common.showToast(activity, "請先登入會員");
        }
    }
}



