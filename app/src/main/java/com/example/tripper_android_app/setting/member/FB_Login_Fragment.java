package com.example.tripper_android_app.setting.member;

import android.content.Intent;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tripper_android_app.MainActivity;
import com.example.tripper_android_app.R;
import com.example.tripper_android_app.task.CommonTask;
import com.example.tripper_android_app.util.Common;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;


import java.util.Arrays;



import static android.content.Context.MODE_PRIVATE;


public class FB_Login_Fragment extends Fragment {
    private static final String TAG = "TAG_FBFragment";
    private MainActivity activity;
    private FirebaseAuth auth;
    private CallbackManager callbackManager;
    private String name, email;
    private LoginButton loginButton;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
        //Initialize FaceBook SDK
        FacebookSdk.sdkInitialize(activity);
        //Initialize Firebase
        auth = FirebaseAuth.getInstance();
        callbackManager = CallbackManager.Factory.create();
        setHasOptionsMenu(true);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_f_b__login_, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//ToolBar
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("FaceBook登入");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorForWhite));
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Drawable upArrow = ContextCompat.getDrawable(activity, R.drawable.abc_ic_ab_back_material);
        if (upArrow != null) {
            upArrow.setColorFilter(ContextCompat.getColor(activity, R.color.colorForWhite), PorterDuff.Mode.SRC_ATOP);
            activity.getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }
//BottomNavigation
        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottomBar);
        NavController navController = Navigation.findNavController(activity, R.id.nav_fragment);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        Menu itemMenu = bottomNavigationView.getMenu();
        itemMenu.getItem(4).setChecked(true);

        loginButton = view.findViewById(R.id.login_button);


        // If using in a fragment
//        loginButton.setFragment(this);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInFB();
            }
        });

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                Navigation.findNavController(loginButton).navigate(R.id.action_FB_Login_Fragment_to_register_Member_Fragment);
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });


    }

    private void signInFB() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                signInFirebase(loginResult.getAccessToken());
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            email = object.getString("email");
                            name = object.getString("name");


                            Log.d(TAG, "email:" + email);
                            Log.d(TAG, "name:" + name);
                            ;

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                Bundle bundle = new Bundle();
                bundle.putString("fields", "id,name,email,gender");
                request.setParameters(bundle);
                request.executeAsync();
                //將資料傳入DB


            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, "facebook:onError", error);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void signInFirebase(AccessToken token) {
        Log.d(TAG, "signInFirebase:" + token);


        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // 登入成功轉至下頁；失敗則顯示錯誤訊息
                        if (task.isSuccessful()) {
                            if (Common.networkConnected(activity)) {

                                String account = email;
                                String password = "password";
                                String nickname = name;

                                SharedPreferences pref = activity.getSharedPreferences(Common.PREF_FILE,
                                        MODE_PRIVATE);
                                pref.edit()
                                        .putBoolean("login", true)
                                        .putString("account", account)
                                        .putString("password", password)
                                        .apply();

                                String Url = Common.URL_SERVER + "MemberServlet";
                                Member member = new Member();
                                member.setAccount(account);
                                member.setNickName(nickname);
                                member.setPassword(password);
                                member.setLoginType(2);
                                JsonObject jsonObject = new JsonObject();
                                jsonObject.addProperty("action", "memberGBInsert");
                                jsonObject.addProperty("member", new Gson().toJson(member));
                                try {
                                    String result = new CommonTask(Url, jsonObject.toString()).execute().get();
                                    int count = Integer.parseInt(result);
                                } catch (Exception e) {
                                    Log.e(TAG, e.toString());
                                }

                            } else {
                                Log.e(TAG, "Internet is null");
                            }
                            Navigation.findNavController(loginButton).navigate(R.id.action_FB_Login_Fragment_to_register_Member_Fragment);
                            Common.showToast(activity,"登入成功！");
                        } else {
                            Exception exception = task.getException();
                            String message = exception == null ? "Sign in fail." : exception.getMessage();
                            Log.e(TAG, message);

                        }
                    }
                });
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        // 檢查user是否已經登入，是則FirebaseUser物件不為null
//        FirebaseUser user = auth.getCurrentUser();
//        if (user != null) {
//            Navigation.findNavController(this.getView())
//                    .navigate(R.id.action_FB_Login_Fragment_to_register_Member_Fragment);
//        }
//    }

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