package com.example.tripper_android_app.setting.member;

import android.app.Activity;
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
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

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


public class Register_Login_Fragment extends Fragment {
    private final static String TAG = "TAG_LoginFragment" ;
    private Activity activity ;
    private TextInputEditText etAccount , etPassword ;
    private ImageButton ibLogin ;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_register__login_, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottomBar);
        NavController navController = Navigation.findNavController(activity, R.id.nav_fragment);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        Menu itemMenu = bottomNavigationView.getMenu();
        itemMenu.getItem(4).setChecked(true);

        etAccount = view.findViewById(R.id.etAccount_login);
        etPassword = view.findViewById(R.id.etPassword_login);
        ibLogin = view.findViewById(R.id.btLogin);

        ibLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Common.networkConnected(activity)){
                    String account = etAccount.getText().toString().trim();
                    String password = etPassword.getText().toString().trim();
                    String Url = Common.URL_SERVER + "MemberServlet" ;
                    Member member = new Member(account,password);
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action","logIn");
                    jsonObject.addProperty("member" ,new Gson().toJson(member));
                    int count = 0 ;
                    try{
                        String result = new CommonTask(Url,jsonObject.toString()).execute().get();
                        count = Integer.parseInt(result);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                    if(count == 0 ){
                        Common.showToast(activity, "帳號或密碼錯誤");
                    }


                    else{
                        Common.showToast(activity,"登入成功！");
                        SharedPreferences pref = activity.getSharedPreferences(Common.PREF_FILE,MODE_PRIVATE);
                        pref.edit()
                                .putBoolean("login", true)
                                .putString("account", account)
                                .putString("password", password)
                                .apply();
                                              ////登入成功後，把資訊存入偏好設定檔
                        JsonObject jsonObject2 = new JsonObject();
                        jsonObject2.addProperty("action","getProfile");
                        jsonObject2.addProperty("account",account);
                        try {
                            String jsonIn = new CommonTask(Url,jsonObject2.toString()).execute().get();
                            Type listtype = new TypeToken<Member>() {
                            }.getType();
                            member = new Gson().fromJson(jsonIn, listtype);
                            member.setLoginType(1);

                        }catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("Member", member);
                        Navigation.findNavController(ibLogin).navigate(R.id.action_register_Login_Fragment_to_register_Member_Fragment,bundle);

                    }
                }else{
                    Common.showToast(activity, "no network connection found");
                }
            }

        });
    }
}