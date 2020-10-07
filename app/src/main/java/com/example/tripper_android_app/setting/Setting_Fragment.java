package com.example.tripper_android_app.setting;

import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.tripper_android_app.MainActivity;
import com.example.tripper_android_app.R;
import com.example.tripper_android_app.setting.member.Member;
import com.example.tripper_android_app.task.CommonTask;
import com.example.tripper_android_app.util.Common;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import static android.content.Context.MODE_PRIVATE;

public class Setting_Fragment extends Fragment {
    private final static String TAG = "TAG_SettingFragment";
    private MainActivity activity;
    private CommonTask loginTask;
    private ImageButton ibRegister, ibMember, ibFriends;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_setting_, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//ToolBar
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("設定");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorForWhite));
        activity.setSupportActionBar(toolbar);
        Drawable upArrow = ContextCompat.getDrawable(activity, R.drawable.abc_ic_ab_back_material);
        if (upArrow != null) {
            upArrow.setColorFilter(ContextCompat.getColor(activity, R.color.colorForWhite), PorterDuff.Mode.SRC_ATOP);
            activity.getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }


//BottomNavigation
        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottomBar);
        final NavController navController = Navigation.findNavController(activity, R.id.setting_Fragment);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);


        ImageButton ibCreateLocation = view.findViewById(R.id.ibCreateLocation);

        ibCreateLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v)
                        .navigate(R.id.action_setting_Fragment_to_locationListFragment);
            }
        });


//進入雙鐵時刻表頁面
        ImageButton ibTrain = view.findViewById(R.id.ibTrain);
        ibTrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v)
                        .navigate(R.id.action_setting_Fragment_to_railwayTimeListFragment);
            }
        });


//進入註冊會員頁面
        ibRegister = view.findViewById(R.id.ibRegister);
        ibRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_setting_Fragment_to_register_main_Fragment);
            }
        });


//進入會員資料頁面
        ibMember = view.findViewById(R.id.ibMember);
        ibMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = activity.getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
                boolean login = pref.getBoolean("login", false);
                if (login) {
                    String account = pref.getString("account", "");
                    String password = pref.getString("password", ""); //帳密取出來送去server檢查
                    if (!isUserValid(account, password)) {
                        //防止舊手機在密碼修改後，卻能繼續使用
                        pref.edit().putBoolean("login", false).apply();  //立刻將登入狀態改為false
                    }

                    Navigation.findNavController(v).navigate(R.id.action_setting_Fragment_to_register_Member_Fragment);

                } else {
                    Navigation.findNavController(v).navigate(R.id.action_setting_Fragment_to_register_main_Fragment);
                    Common.showToast(activity, "請先登入會員");
                }
            }
        });

        ibFriends = view.findViewById(R.id.ibFriends);
        ibFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_setting_Fragment_to_friendsListFragment);
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();

        SharedPreferences pref = activity.getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
        boolean login = pref.getBoolean("login", false);
        if (login) {
            String account = pref.getString("account", "");
            String password = pref.getString("password", ""); //帳密取出來送去server檢查
            if (!isUserValid(account, password)) {
                //防止舊手機在密碼修改後，卻能繼續使用
                pref.edit().putBoolean("login", false).apply();  //立刻將登入狀態改為false
            }
        }
    }

    private boolean isUserValid(String account, String password) {
        String url = Common.URL_SERVER + "MemberServlet";
        Member member = new Member(account, password);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "logIn");
        jsonObject.addProperty("member", new Gson().toJson(member));
        loginTask = new CommonTask(url, jsonObject.toString());
        int count = 0;
        boolean isUserValid = false;
        try {
            String jsonIn = loginTask.execute().get();
            count = Integer.parseInt(jsonIn);
            if (count == 1) {
                isUserValid = true;
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return isUserValid;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 從偏好設定檔中取得登入狀態」
        SharedPreferences pref = activity.getSharedPreferences(Common.PREF_FILE,
                MODE_PRIVATE);
        boolean login = pref.getBoolean("login", false);
        if (login) {
            ibRegister.setVisibility(View.GONE);
            ibFriends.setVisibility(View.VISIBLE);
            ibMember.setVisibility(View.VISIBLE);
        } else {
            ibRegister.setVisibility(View.VISIBLE);
            ibFriends.setVisibility(View.GONE);
            ibMember.setVisibility(View.GONE);//如果登入狀態為false ，登出鈕便會消失
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(loginTask != null){
            loginTask.cancel(true);
            loginTask = null ;
        }
    }
}