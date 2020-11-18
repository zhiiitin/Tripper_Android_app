package com.example.tripper_android_app.setting.member;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.tripper_android_app.MainActivity;
import com.example.tripper_android_app.R;
import com.example.tripper_android_app.task.CommonTask;
import com.example.tripper_android_app.util.Common;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.MODE_PRIVATE;

public class Register_Normal_Fragment extends Fragment {
    private final static String TAG = "TAG_NormalFragment";
    private MainActivity activity;
    private TextInputEditText etAccount, etPassword, etNickName, etPassword2,etPhone ,etCode;
    private ImageButton ibRegister;
    private FirebaseAuth firebaseAuth;
    private String verificationId;
    private CardView btSendPhone , btVerification;
    private int checkCode = 0 ;
    private PhoneAuthProvider.ForceResendingToken resendToken;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
        setHasOptionsMenu(true);
        firebaseAuth = FirebaseAuth.getInstance();
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
        toolbar.setTitle("註冊會員");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorForWhite));
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Drawable upArrow = ContextCompat.getDrawable(activity, R.drawable.abc_ic_ab_back_material);
        if (upArrow != null) {
            upArrow.setColorFilter(ContextCompat.getColor(activity, R.color.colorForWhite), PorterDuff.Mode.SRC_ATOP);
            activity.getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }


        etAccount = view.findViewById(R.id.etAccount);
        etPassword = view.findViewById(R.id.etPassword);
        etPassword2 = view.findViewById(R.id.etPassword2);
        etNickName = view.findViewById(R.id.etNickname);
        ibRegister = view.findViewById(R.id.btRegister);
        etPhone = view.findViewById(R.id.etPhone);
        btSendPhone = view.findViewById(R.id.btSendPhone);
        etCode = view.findViewById(R.id.etCode);
        btVerification = view.findViewById(R.id.btVerification);

        btSendPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = "+886"+etPhone.getText().toString().trim();

                if (phone.isEmpty()){
                    etPhone.setError("此欄位不能為空值");
                    return;
                }

                sendVerificationCode(phone);
            }
        });

        btVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String verificationCode = etCode.getText().toString().trim();
                if (verificationCode.isEmpty()) {
                    etCode.setError("驗證碼欄有誤");
                    return;
                }
                verifyPhoneNumberWithCode(verificationId, verificationCode);
            }
        });

        ibRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = etAccount.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String password2 = etPassword2.getText().toString().trim();
                String nickname = etNickName.getText().toString().trim();


                if (account.isEmpty()) {
                    etAccount.setError("此欄位不能為空值");
                    return;
                }

                if (password.isEmpty()) {
                    etPassword.setError("此欄位不能為空值");
                    return;
                }

                if (password2.isEmpty()) {
                    etPassword2.setError("此欄位不能為空值");
                    return;
                }

                if (password.length() < 6 || password.length() > 12) {
                    etPassword.setError("請輸入6~12位字元");
                    return;
                }

                if (!password2.equals(password)) {
                    etPassword2.setError("與密碼欄位不符");
                    return;
                }

                if (nickname.isEmpty()) {
                    etNickName.setError("此欄位不能為空值");
                    return;
                }

                if (nickname.length() > 10) {
                    etPassword.setError("暱稱限定10字元");
                    return;
                }

//                register(nickname,email,password);

                if (checkCode == 1) {
                    if (Common.networkConnected(activity)) {
                        String Url = Common.URL_SERVER + "MemberServlet";
                        Member member = null;

                        member = new Member(0, account, password, nickname);
                        member.setLoginType(0);

                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("action", "memberInsert");
                        jsonObject.addProperty("member", new Gson().toJson(member));

                        int count = 0;
                        try {
                            String result = new CommonTask(Url, jsonObject.toString()).execute().get();
                            count = Integer.parseInt(result);
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                        if (count == 0) {
                            etAccount.setError("此帳號已有人使用");
                            Common.showToast(activity, "此帳號已有人使用");
                        } else {
                            Common.showToast(activity, "帳號創建成功！");
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("member", member);
                            //action_register_NormalFragment_to_register_Member_Fragment
                            // Navigation.findNavController(ibRegister).navigate(R.id.action_setting_Fragment_to_register_Normal_Fragment , bundle);
                            SharedPreferences pref = activity.getSharedPreferences(Common.PREF_FILE,
                                    MODE_PRIVATE);
                            pref.edit()
                                    .putBoolean("login", true)
                                    .putString("account", account)
                                    .putString("password", password)
                                    .apply();       ////登入成功後，把資訊存入偏好設定檔


                            Navigation.findNavController(ibRegister).navigate(R.id.action_register_NormalFragment_to_register_Member_Fragment);
                        }
                    } else {
                        Common.showToast(activity, "no network connection found");
                    }
                } else {
                    etCode.setError("驗證碼錯誤");
                    return;
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

    //檢查信箱格式
    public static boolean isEmail(String email) {
        if (null == email || "".equals(email)) return false;
        Pattern p = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
        Matcher m = p.matcher(email);
        return m.matches();
    }
// Firebase 註冊信箱
    private void register(final String username, final String email, String password){

        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                        }else {
                            Toast.makeText(activity, "此信箱已註冊 or 信箱格式錯誤",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void sendVerificationCode(String phone) {

        // 設定簡訊語系為繁體中文
        firebaseAuth.setLanguageCode("zh-Hant");
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone, // 電話號碼，驗證碼寄送的電話號碼
                60, // 驗證碼失效時間，設為60秒代表多次呼叫verifyPhoneNumber()，過了60秒才會發送第2次
                TimeUnit.SECONDS, // 設定時間單位為秒
                activity,
                verifyCallbacks); // 監聽電話驗證的狀態
    }

    private void verifyPhoneNumberWithCode(String verificationId, String verificationCode) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, verificationCode);
        firebaseAuthWithPhoneNumber(credential);
    }
    private void firebaseAuthWithPhoneNumber(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            checkCode = 1 ;
                            Common.showToast(activity,"認證成功！");
                        } else {
                            Exception exception = task.getException();
                            String message = exception == null ? "Sign in fail." : exception.getMessage();
                            Common.showToast(activity,message);
                        }
                    }
                });
    }


//    private void sendVerificationCode(String phoneNum){
//        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNum,60, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD,mCallBack);
//    }
//
//    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
//            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//
//        @Override
//        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//            super.onCodeSent(s, forceResendingToken);
//            verificationId = s ;
//        }
//
//        @Override
//        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
//            String code = phoneAuthCredential.getSmsCode();
//            if(code != null){
//                verifyCode(code);
//            }
//        }
//
//        @Override
//        public void onVerificationFailed(@NonNull FirebaseException e) {
//            Common.showToast(activity,e.getMessage());
//            Log.e("onVerificationFailed",e.getMessage());
//
//        }
//    };

//    private void verifyCode(String code){
//        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId,code);
//        signInWithCredential(credential);
//    }
//
//    private void signInWithCredential(PhoneAuthCredential credential){
//        firebaseAuth.signInWithCredential(credential)
//                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if(task.isSuccessful()){
//                                checkCode = 1 ;
//                        }else {
//                            Common.showToast(activity,task.getException().getMessage());
//                            System.out.println(task.getException().getMessage());
//                        }
//                    }
//                });
//    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verifyCallbacks
            = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        /** This callback will be invoked in two situations:
         1 - Instant verification. In some cases the phone number can be instantly
         verified without needing to send or enter a verification code.
         2 - Auto-retrieval. On some devices Google Play services can automatically
         detect the incoming verification SMS and perform verification without
         user action. */
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
            Log.d(TAG, "onVerificationCompleted: " + credential);
            Common.showToast(activity,"已發送簡訊");
        }

        /**
         * 發送驗證碼填入的電話號碼格式錯誤，或是使用模擬器發送都會產生發送錯誤，
         * 使用模擬器發送會產生下列執行錯誤訊息：
         * App validation failed. Is app running on a physical device?
         */
        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Log.e(TAG, "onVerificationFailed: " + e.getMessage());
        }
        /**
         * The SMS verification code has been sent to the provided phone number,
         * we now need to ask the user to enter the code and then construct a credential
         * by combining the code with a verification ID.
         */
        @Override
        public void onCodeSent(@NonNull String id, @NonNull PhoneAuthProvider.ForceResendingToken token) {
            Log.d(TAG, "onCodeSent: " + id);
            verificationId = id;
            resendToken = token;
            // 顯示填寫驗證碼版面

        }
    };


}