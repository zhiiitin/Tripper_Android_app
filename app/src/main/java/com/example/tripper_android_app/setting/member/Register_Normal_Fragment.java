package com.example.tripper_android_app.setting.member;

import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.tripper_android_app.MainActivity;
import com.example.tripper_android_app.R;
import com.example.tripper_android_app.task.CommonTask;
import com.example.tripper_android_app.util.Common;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import static android.content.Context.MODE_PRIVATE;

public class Register_Normal_Fragment extends Fragment {
    private final static String TAG = "TAG_NormalFragment" ;
    private MainActivity activity ;
    private TextInputEditText etAccount , etPassword ,etNickName ,etPassword2 ;
    private ImageButton ibRegister ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_register_normal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("一般登入");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorForWhite));
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Drawable upArrow = ContextCompat.getDrawable(activity, R.drawable.abc_ic_ab_back_material);
        if(upArrow != null) {
            upArrow.setColorFilter(ContextCompat.getColor(activity, R.color.colorForWhite), PorterDuff.Mode.SRC_ATOP);
            activity.getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }

        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottomBar);
        NavController navController = Navigation.findNavController(activity, R.id.nav_fragment);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        Menu itemMenu = bottomNavigationView.getMenu();
        itemMenu.getItem(4).setChecked(true);

        etAccount = view.findViewById(R.id.etAccount);
        etPassword = view.findViewById(R.id.etPassword);
        etPassword2 = view.findViewById(R.id.etPassword2);
        etNickName = view.findViewById(R.id.etNickname);
        ibRegister = view.findViewById(R.id.btRegister);
        ibRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = etAccount.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String password2 = etPassword2.getText().toString().trim();
                String nickname = etNickName.getText().toString().trim();

                if(account.isEmpty()){
                    etAccount.setError("此欄位不能為空值");
                    return;
                }

                if(password.isEmpty()){
                    etPassword.setError("此欄位不能為空值");
                    return;
                }

                if(password2.isEmpty()){
                    etPassword2.setError("此欄位不能為空值");
                    return;
                }

                if(password.length() <6 ||password.length() > 12 ){
                    etPassword.setError("請輸入6~12位字元");
                    return;
                }

                if(!password2.equals(password) ){
                    etPassword2.setError("與密碼欄位不符");
                    return;
                }

                if(nickname.isEmpty()){
                    etNickName.setError("此欄位不能為空值");
                    return;
                }

                if(nickname.length() > 10 ){
                    etPassword.setError("暱稱限定10字元");
                    return;
                }

                if(Common.networkConnected(activity)){
                    String Url = Common.URL_SERVER + "MemberServlet" ;
                    Member member = new Member(0,account,password,nickname);
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action","memberInsert");
                    jsonObject.addProperty("member" ,new Gson().toJson(member));

                    int count = 0 ;
                    try{
                        String result = new CommonTask(Url,jsonObject.toString()).execute().get();
                        count = Integer.parseInt(result);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                    if(count == 0 ){
                        Common.showToast(activity, "此帳號已有人使用");
                    }
                    else{
                        Common.showToast(activity,"帳號創建成功！");
                        SharedPreferences pref = activity.getSharedPreferences(Common.PREF_FILE,
                                MODE_PRIVATE);
                        pref.edit()
                                .putBoolean("login", true)
                                .putString("account", account)
                                .putString("password", password)
                                .apply();       ////登入成功後，把資訊存入偏好設定檔

                        JsonObject jsonObject2 = new JsonObject();
                        jsonObject2.addProperty("action","getProfile");
                        jsonObject2.addProperty("account",account);
                        try {
                            String jsonIn = new CommonTask(Url,jsonObject2.toString()).execute().get();
                            Type listtype = new TypeToken<Member>() {
                            }.getType();
                            member = new Gson().fromJson(jsonIn, listtype);

                        }catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("Member", member);
                        Navigation.findNavController(ibRegister).navigate(R.id.action_register_NormalFragment_to_register_Member_Fragment,bundle);
                    }
                }else{
                    Common.showToast(activity, "no network connection found");
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String message = "";
        switch (item.getItemId()) {
            case android.R.id.home:    //此返回鍵ID是固定的
                Navigation.findNavController(this.getView()).popBackStack();
                return true;

        }
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
        return true;
    }
}