package com.example.tripper_android_app.goal;

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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tripper_android_app.MainActivity;
import com.example.tripper_android_app.R;
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

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.content.Context.MODE_PRIVATE;


public class Goal_HomePage extends Fragment {
    public static final String TAG = "TAG_GoalHomePage";
    private MainActivity activity;
    private ImageView ivUserPic, ivStar;
    private TextView textUserName, tvBG3, tvTripCount, tvBlogCount, tvGoalDone, tvGoalCount;
    // show member
    private Member member;
    private FirebaseAuth auth;
    private FirebaseUser mUser;
    // 成就使用
    private Goal goal;
    private List<Goal> goals;
    private int goalId, goalCond1, goalCond2;


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
        return inflater.inflate(R.layout.fragment_goal__home_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 設定toolbar
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("個人頁面");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorForWhite));
        activity.setSupportActionBar(toolbar);

        mUser = auth.getCurrentUser();
        ivUserPic = view.findViewById(R.id.ivUserPic);
        textUserName = view.findViewById(R.id.textUserName);

        // 顯示會員資料
        showMember();

        // 從偏好設定檔取得memberId
        SharedPreferences pref = activity.getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
        int memberId = Integer.parseInt(pref.getString("memberId", null));

        // 檢查網路連線
        if(Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "GoalServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getGoalByMember");
            jsonObject.addProperty("memberId", memberId);
            Log.d(TAG,"CurrentMemberId: " + " " + memberId);
            String jsonOut = jsonObject.toString();
            Log.d(TAG, "Goal_jsonOut:" + jsonOut);
            CommonTask getSearchDataTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = getSearchDataTask.execute().get();
                Type listType = new TypeToken<List<Goal>>() {
                }.getType();
                goals = new Gson().fromJson(jsonIn, listType);
                goal = goals.get(0);
                if(goal == null){
                    Common.showToast(activity, "搜尋不到該帳號資訊");
                } else {
                    // 於成就主頁載入時，計算並顯示建立的"行程數"
                    tvTripCount = view.findViewById(R.id.tvTripCount);
                    goalCond1 = goal.getGoalCond1();
                    tvTripCount.setText(String.valueOf(goalCond1));
                    // 於成就主頁載入時，計算並顯示建立的"網誌數"
                    tvBlogCount = view.findViewById(R.id.tvBlogCount);
                    goalCond2 = goal.getGoalCond2();
                    tvBlogCount.setText(String.valueOf(goalCond2));
                    // 於成就主頁載入時，計算並顯示解鎖的"成就數"
                    tvGoalCount = view.findViewById(R.id.tvGoalCount);
                    goalId = goal.getGoalId();
                    tvGoalCount.setText(String.valueOf(goalId));
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottomBar);
        NavController navController = Navigation.findNavController(activity, R.id.goal_HomePage);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        Menu itemMenu = bottomNavigationView.getMenu();
        itemMenu.getItem(2).setChecked(true);

        BottomNavigationView bottomNavigationViewTop = view.findViewById(R.id.navigation);
        NavController navControllerTop = Navigation.findNavController(activity, R.id.goal_HomePage);
        NavigationUI.setupWithNavController(bottomNavigationViewTop, navControllerTop);
        Menu itemMenu2 = bottomNavigationViewTop.getMenu();
        itemMenu2.getItem(2).setChecked(true);
        // 每次進到畫面都先更新token
        if (Common.isLogin(activity)) {
            Common.getTokenSendServer(activity);
        }

        // 點擊進入成就清單頁面
        ivStar = view.findViewById(R.id.ivStar);
        ivStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("goalId", goalId);
                Navigation.findNavController(v).navigate(R.id.action_goal_HomePage_to_goalListFragment, bundle);
            }
        });

        tvBG3 = view.findViewById(R.id.tvBG3);
        tvBG3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("goalId", goalId);
                Navigation.findNavController(v).navigate(R.id.action_goal_HomePage_to_goalListFragment, bundle);
            }
        });

        tvGoalDone = view.findViewById(R.id.tvGoalDone);
        tvGoalDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("goalId", goalId);
                Navigation.findNavController(v).navigate(R.id.action_goal_HomePage_to_goalListFragment, bundle);
            }
        });

        tvGoalCount = view.findViewById(R.id.tvGoalCount);
        tvGoalCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("goalId", goalId);
                Navigation.findNavController(v).navigate(R.id.action_goal_HomePage_to_goalListFragment, bundle);
            }
        });
    }

    // show 使用者資訊
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
                String nickname = member.getNickName();
                textUserName.setText(" " + nickname + " ");

            } else {
                Common.showToast(activity, "no network connection found");
            }
            showMemberPic();
        }
    }

    // show UserPic
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
            // 若此帳號之資料庫有照片，便使用資料庫的照片
            if (bitmap != null) {
                ivUserPic.setImageBitmap(bitmap);
            } else {
                // 否則連接到第三方大頭照
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

}