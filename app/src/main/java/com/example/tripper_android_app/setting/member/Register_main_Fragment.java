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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
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
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import static android.content.Context.MODE_PRIVATE;


public class Register_main_Fragment extends Fragment {
    private static final String TAG = "TAG_MainFragment";
    private static final int REQ_SIGN_IN = 101;
    private MainActivity activity;
    private ImageButton ivRegister_Google ,ibLogin ;
    private GoogleSignInClient client;
    private FirebaseAuth auth;
    private TextView ivRegister_hand , btToFill ,tvResetPassword ;
    private EditText etAccount , etPassword ;
    private CallbackManager callbackManager;
    private String name, email;

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

        FacebookSdk.sdkInitialize(activity);
        callbackManager = CallbackManager.Factory.create();

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

        etAccount = view.findViewById(R.id.etAccount);
        etPassword = view.findViewById(R.id.etPassword);
        ibLogin = view.findViewById(R.id.ibLogin);
        btToFill = view.findViewById(R.id.btToFill);
        tvResetPassword = view.findViewById(R.id.tvResetPassword);
//神奇小按鈕
        btToFill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etAccount.setText("regina");
                etPassword.setText("password");
            }
        });
//登入系統
        ibLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.networkConnected(activity)) {
                    String account = etAccount.getText().toString().trim();
                    String password = etPassword.getText().toString().trim();
                    String Url = Common.URL_SERVER + "MemberServlet";
                    Member member = new Member(account, password);
                    member.setLoginType(1);
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "logIn");
                    jsonObject.addProperty("member", new Gson().toJson(member));
                    int count = 0;
                    try {
                        String result = new CommonTask(Url, jsonObject.toString()).execute().get();
                        count = Integer.parseInt(result);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                    if (count == 0) {
                        Common.showToast(activity, "帳號或密碼錯誤");
                    } else {
                        Common.showToast(activity, "登入成功！");
                        SharedPreferences pref = activity.getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
                        pref.edit()
                                .putBoolean("login", true)
                                .putString("account", account)
                                .putString("password",password)
                                .apply();
                        ////登入成功後，把資訊存入偏好設定檔

                        Navigation.findNavController(ibLogin).navigate(R.id.action_register_main_Fragment_to_trip_HomePage);

                    }
                } else {
                    Common.showToast(activity, "no network connection found");
                }
            }

        });


//進入一般註冊頁面
        ivRegister_hand = view.findViewById(R.id.btRegister_hand);
        ivRegister_hand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_register_main_Fragment_to_register_NormalFragment);
            }
        });

//進入FB登入頁面
        final ImageButton ivRegister_FB = view.findViewById(R.id.btRegister_FB);
        ivRegister_FB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInFB();
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

//忘記密碼頁面
        tvResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_register_main_Fragment_to_registerResetpasswordFragment);
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

                        String accountDb = account.getEmail();
                        String password = "password" ;
                        String nickname = account.getDisplayName();
                        String photoStr = account.getPhotoUrl().toString();
                        Bundle bundle = new Bundle();
                        bundle.putString("photoUrl",photoStr);

                        Log.e("DisplayName", account.getDisplayName());
                        Log.e("GivenName", account.getGivenName());

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
                     Navigation.findNavController(ivRegister_Google).navigate(R.id.action_register_main_Fragment_to_trip_HomePage,bundle);

                } else {
                    Log.e(TAG, "GoogleSignInAccount is null");
                }
            } catch (Exception e) {
                // Google Sign In failed, update UI appropriately
                Log.e(TAG, "Google sign in failed");
            }
        }
        else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
            super.onActivityResult(requestCode, resultCode, data);

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
                            Common.showToast(activity,"登入成功！");
                            Navigation.findNavController(ivRegister_Google).navigate(R.id.action_register_main_Fragment_to_trip_HomePage);

                        } else {
                            Exception exception = task.getException();
                            String message = exception == null ? "Sign in fail." : exception.getMessage();
                            Log.e(TAG, message);

                        }
                    }
                });
    }


}