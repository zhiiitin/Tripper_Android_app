package com.example.tripper_android_app.setting;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.tripper_android_app.R;
import com.example.tripper_android_app.setting.member.Common;
import com.example.tripper_android_app.setting.member.CommonTask;
import com.example.tripper_android_app.setting.member.Member;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import static android.content.Context.MODE_PRIVATE;

public class Setting_Fragment extends Fragment {
    private final static String TAG = "TAG_SettingFragment";
    private FragmentActivity activity;
    private CommonTask loginTask;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        activity.setTitle("設定");
        return inflater.inflate(R.layout.fragment_setting_, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


//        ImageButton ibCreateLocation = view.findViewById(R.id.ibCreateLocation);
//        ibCreateLocation.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Navigation.findNavController(v)
//                        .navigate(R.id.action_setting_Fragment_to_location_List_Fragment);
//            }
//        });

        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottomBar);
        NavController navController = Navigation.findNavController(activity, R.id.setting_Fragment);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);


        ImageButton ibCreateLocation = view.findViewById(R.id.ibCreateLocation);
        ibCreateLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v)
                        .navigate(R.id.action_setting_Fragment_to_locationListFragment);
            }
        });




        ImageButton ibRegister = view.findViewById(R.id.ibRegister);
        ibRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_setting_Fragment_to_register_main_Fragment);
            }
        });

        ImageButton ibMember = view.findViewById(R.id.ibMember);
        ibMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = activity.getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
                boolean login = pref.getBoolean("login", false);
                if (login) {
                    String account = pref.getString("account", "");
                    String password = pref.getString("password", ""); //帳密取出來送去server檢查
                    if (isUserValid(account, password) == false) {
                        //防止舊手機在密碼修改後，卻能繼續使用
                        pref.edit().putBoolean("login", false).apply();  //立刻將登入狀態改為false
                    }
                    String Url = Common.URL_SERVER + "MemberServlet";
                    JsonObject jsonObject2 = new JsonObject();
                    jsonObject2.addProperty("action", "getProfile");
                    jsonObject2.addProperty("account", account);
                    Member member = null;
                    try {
                        String jsonIn = new CommonTask(Url, jsonObject2.toString()).execute().get();
                        Type listtype = new TypeToken<Member>() {
                        }.getType();
                        member = new Gson().fromJson(jsonIn, listtype);
                        member.setLoginType(1);

                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("Member", member);
                    Navigation.findNavController(v).navigate(R.id.action_setting_Fragment_to_register_Member_Fragment, bundle);
//若登入狀態為false 直接不帶bundle導入下一頁，讓memberFragment判斷
                } else {
                    Navigation.findNavController(v).navigate(R.id.action_setting_Fragment_to_register_Member_Fragment);
                }
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
            if (isUserValid(account, password) == false) {
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
}