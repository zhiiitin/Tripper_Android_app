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
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.tripper_android_app.MainActivity;
import com.example.tripper_android_app.R;
import com.example.tripper_android_app.task.CommonTask;
import com.example.tripper_android_app.util.Common;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import static android.content.Context.MODE_PRIVATE;


public class RegisterChangePwFragment extends Fragment {
    private static final String TAG = "TAG_ChangePWFragment";
    private MainActivity activity;
    private EditText etoldPassword,etNewPassword1,etNewPassword2 ;
    private ImageButton ibSaved ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity)getActivity();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register_change_pw, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//ToolBar
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("修改密碼");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorForWhite));
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Drawable upArrow = ContextCompat.getDrawable(activity, R.drawable.abc_ic_ab_back_material);
        if (upArrow != null) {
            upArrow.setColorFilter(ContextCompat.getColor(activity, R.color.colorForWhite), PorterDuff.Mode.SRC_ATOP);
            activity.getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }

        etoldPassword = view.findViewById(R.id.etoldPassword);
        etNewPassword1 = view.findViewById(R.id.etNewPassword1);
        etNewPassword2 = view.findViewById(R.id.etNewPassword2);
        ibSaved = view.findViewById(R.id.ibSaved);



        ibSaved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String oldPassword = etoldPassword.getText().toString().trim();
                String newPassword1 = etNewPassword1.getText().toString().trim();
                String newPassword2 = etNewPassword2.getText().toString().trim();

                if (oldPassword.isEmpty()) {
                    etoldPassword.setError("此欄位不得為空");
                    return;
                }
                if (newPassword1.isEmpty()) {
                    etNewPassword1.setError("此欄位不得為空");
                    return;
                }
                if (newPassword2.isEmpty()) {
                    etNewPassword2.setError("此欄位不得為空");
                    return;
                }

                SharedPreferences pref = activity.getSharedPreferences(Common.PREF_FILE,
                        MODE_PRIVATE);
                String password = pref.getString("memberPw", null);
                String account = pref.getString("account", null);
                if (!oldPassword.equals(password)) {
                    etoldPassword.setError("原密碼輸入錯誤");
                    return;
                }

                if (!newPassword2.equals(newPassword1)) {
                    etNewPassword2.setError("與新密碼欄位不一致");
                    return;
                }

                if (Common.networkConnected(activity)) {
                    String url = Common.URL_SERVER + "MemberServlet";
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "UpdatePassword");
                    jsonObject.addProperty("account", account);
                    jsonObject.addProperty("newPassword", newPassword1);
                    int count = 0;
                    try {
                        String result = new CommonTask(url, jsonObject.toString()).execute().get();
                        count = Integer.parseInt(result);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                    if (count == 1) {
                        Common.showToast(activity, "密碼修改成功");
                        pref.edit().putString("password",newPassword1).apply();
                        Navigation.findNavController(etNewPassword1).popBackStack();
                    }
                } else {
                    Common.showToast(activity,"請檢查網路");
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Navigation.findNavController(etNewPassword1).popBackStack();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}