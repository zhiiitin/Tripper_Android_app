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
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.tripper_android_app.MainActivity;
import com.example.tripper_android_app.R;
import com.example.tripper_android_app.task.CommonTask;
import com.example.tripper_android_app.util.Common;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import static android.content.Context.MODE_PRIVATE;


public class Register_main_Fragment extends Fragment {
    private static final String TAG = "TAG_MainFragment";
    private static final int REQ_SIGN_IN = 101;
    private MainActivity activity;
    private ImageButton ivRegister_Google ;
    private GoogleSignInClient client;
    private FirebaseAuth auth;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
        auth = FirebaseAuth.getInstance();
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                // 由google-services.json轉出
                .requestIdToken(getString(R.string.default_web_client_id))
                // 要求輸入email
                .requestEmail()
                .build();
        client = GoogleSignIn.getClient(activity, options);
        setHasOptionsMenu(true);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_register_main_, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//ToolBar
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("會員註冊");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorForWhite));
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Drawable upArrow = ContextCompat.getDrawable(activity, R.drawable.abc_ic_ab_back_material);
        if(upArrow != null) {
            upArrow.setColorFilter(ContextCompat.getColor(activity, R.color.colorForWhite), PorterDuff.Mode.SRC_ATOP);
            activity.getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }
//BottomNavigation
        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottomBar);
        NavController navController = Navigation.findNavController(activity, R.id.nav_fragment);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        Menu itemMenu = bottomNavigationView.getMenu();
        itemMenu.getItem(4).setChecked(true);

        ImageButton ivRegister_hand = view.findViewById(R.id.btRegister_hand);
        ImageButton ivRegister_login = view.findViewById(R.id.btRegister_Login);

//        ivRegister_hand.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Navigation.findNavController(v).navigate(R.id.action_register_main_Fragment_to_register_NormalFragment);
//            }
//        });
//        ivRegister_login.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Navigation.findNavController(v).navigate(R.id.action_register_main_Fragment_to_register_Login_Fragment);
//
//            }
//        });

//進入一般註冊頁面
        ivRegister_hand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_register_main_Fragment_to_register_NormalFragment);
            }
        });
//進入一般登入頁面
        ivRegister_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_register_main_Fragment_to_register_Login_Fragment);

            }
        });
//進入FB登入頁面
        ImageButton ivRegister_FB = view.findViewById(R.id.btRegister_FB);
        ivRegister_FB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_register_main_Fragment_to_FB_Login_Fragment);
            }
        });
//進入Google登入頁面
        ivRegister_Google = view.findViewById(R.id.btRegister_Google);
        ivRegister_Google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInGoogle();
            }
        });

    }

    private void signInGoogle() {
        Intent signInIntent = client.getSignInIntent();
        startActivityForResult(signInIntent, REQ_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_SIGN_IN) {
            // 取得裝置上的Google登入帳號
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account);

                        String accountDb = account.getGivenName();
                        String password = "password" ;
                        String nickname = account.getDisplayName();


                        SharedPreferences pref = activity.getSharedPreferences(Common.PREF_FILE,
                                MODE_PRIVATE);
                        pref.edit()
                                .putBoolean("login", true)
                                .putString("account", accountDb)
                                .putString("password", password)
                                .apply();

                        String Url = Common.URL_SERVER + "MemberServlet" ;
                        Member member = new Member();
                        member.setAccount(accountDb);
                        member.setNickName(nickname);
                        member.setPassword(password);
                        member.setLoginType(1);
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("action","memberGBInsert");
                        jsonObject.addProperty("member" ,new Gson().toJson(member));

                        try{
                            String result = new CommonTask(Url,jsonObject.toString()).execute().get();
                            int count = Integer.parseInt(result);
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                     Navigation.findNavController(ivRegister_Google).navigate(R.id.action_register_main_Fragment_to_register_Member_Fragment);

                } else {
                    Log.e(TAG, "GoogleSignInAccount is null");
                }
            } catch (Exception e) {
                // Google Sign In failed, update UI appropriately
                Log.e(TAG, "Google sign in failed");
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        // get the unique ID for the Google account
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // 登入成功轉至下頁；失敗則顯示錯誤訊息
                        if (task.isSuccessful()) {
                            Common.showToast(activity,"GOOGLE登入成功！");
                        } else {
                            Exception exception = task.getException();
                            String message = exception == null ? "登入失敗" : exception.getMessage();
                            Common.showToast(activity,message);
                        }

                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String message = "";
        switch (item.getItemId()) {
            case android.R.id.home:    //此返回鍵ID是固定的
                Navigation.findNavController(this.getView()).navigate(R.id.action_register_main_Fragment_to_setting_Fragment);
                return true;

        }
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
        return true;
    }


}