package com.example.tripper_android_app.blog;

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
import android.view.MenuInflater;
import android.view.MenuItem;
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

import static android.content.Context.MODE_PRIVATE;
import static androidx.navigation.Navigation.findNavController;


public class Blog_HomePage extends Fragment {
    private final static String TAG = "TAG_BlodHPageFragment";
    private MainActivity activity;
    private ImageView ivUserPic;
    private TextView tvUserName;
    private Member member ;
    private FirebaseAuth auth;
    private FirebaseUser mUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
        setHasOptionsMenu(true);
        auth = FirebaseAuth.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_blog__home_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUser = auth.getCurrentUser();
        //ToolBar
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorForWhite));
        activity.setSupportActionBar(toolbar);


        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottomBar);
        NavController navController = Navigation.findNavController(activity, R.id.trip_HomePage);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        Menu itemMenu = bottomNavigationView.getMenu();
        itemMenu.getItem(2).setChecked(true);

        BottomNavigationView bottomNavigationViewTop = view.findViewById(R.id.navigation);
        NavController navControllerTop = Navigation.findNavController(activity, R.id.trip_HomePage);
        NavigationUI.setupWithNavController(bottomNavigationViewTop, navControllerTop);
        Menu itemMenu2 = bottomNavigationViewTop.getMenu();
        itemMenu2.getItem(1).setChecked(true);

        ivUserPic = view.findViewById(R.id.ivUserPic);
        tvUserName = view.findViewById(R.id.tvUserName);

        showMember();


    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        SharedPreferences pref = activity.getSharedPreferences(Common.PREF_FILE,MODE_PRIVATE);
        boolean login = pref.getBoolean("login", false);
        if(login) {
            inflater.inflate(R.menu.app_bar_button_blog, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.btCreateBlog) {
            findNavController(this.getView()).navigate(R.id.action_blog_HomePage_to_create_Blog_Location_List);
            return true;

        }
        return super.onOptionsItemSelected(item);
    }
//show 使用者資訊
    private void showMember(){
        SharedPreferences pref = activity.getSharedPreferences(Common.PREF_FILE,MODE_PRIVATE);
        boolean login = pref.getBoolean("login", false);
        if(login){
            if(Common.networkConnected(activity)){
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
                tvUserName.setText(nickname);

            }else {
                Common.showToast(activity, "no network connection found");
            }
            showMemberPic();

        }
    }

    private void showMemberPic(){
        if(mUser != null){
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

        }else {

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