package com.example.tripper_android_app.setting.member;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.Navigation;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.tripper_android_app.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Register_Normal_Fragment extends Fragment {
    private final static String TAG = "TAG_NormalFragment" ;
    private FragmentActivity activity ;
    private TextInputEditText etAccount , etPassword ,etNickName ;
    private ImageButton ibRegister ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();

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
        etAccount = view.findViewById(R.id.etAccount);
        etPassword = view.findViewById(R.id.etPassword);
        etNickName = view.findViewById(R.id.etNickname);
        ibRegister = view.findViewById(R.id.btRegister);
        ibRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = etAccount.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String nickname = etNickName.getText().toString().trim();

                if(account.isEmpty()){
                    etAccount.setError("此欄位不能為空值");
                    return;
                }

                if(password.isEmpty()){
                    etPassword.setError("此欄位不能為空值");
                    return;
                }

                if(password.length() <6 ||password.length() > 12 ){
                    etPassword.setError("請輸入6~12位字元");
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
                        Common.showToast(activity, "帳號創建失敗");
                    }
                    else{
                        Common.showToast(activity,"帳號創建成功！");
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("member", member);
                        Navigation.findNavController(ibRegister).navigate(R.id.action_register_NormalFragment_to_register_Member_Fragment , bundle);
                    }
                }else{
                    Common.showToast(activity, "no network connection found");
                }
            }
        });
    }
}