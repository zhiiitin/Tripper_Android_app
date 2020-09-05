package com.example.tripper_android_app.setting.member;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.tripper_android_app.R;

import static android.content.Context.MODE_PRIVATE;


public class Register_Member_Fragment extends Fragment {
    private final static String TAG = "TAG_MemberFragment";
    private FragmentActivity activity;
    private TextView tvId, tvNickName, tvLoginType;
    private ImageButton ibLogout ;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_register__member_, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final NavController navController = Navigation.findNavController(view);
        tvId = view.findViewById(R.id.tvId_member);
        tvNickName = view.findViewById(R.id.tvNickname_member);
        tvLoginType = view.findViewById(R.id.tvLoginType_member);
        ibLogout = view.findViewById(R.id.btLogout);

        Bundle bundle = getArguments();
        if (bundle == null || bundle.getSerializable("Member") == null) {
            Common.showToast(activity, "尚未登入會員");
            navController.popBackStack();
            return;
        } else {
            Member member = (Member) bundle.getSerializable("Member");

            String id = member.getId() + "";
            String nickname = member.getNickName();
            tvId.setText(id);
            tvNickName.setText(nickname);
            if(member.getLoginType() == 1){
                tvLoginType.setText("一般登入");
            }
        }

        ibLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = activity.getSharedPreferences(Common.PREF_FILE,
                        MODE_PRIVATE);
                pref.edit().putBoolean("login", false).apply();
                Navigation.findNavController(ibLogout).navigate(R.id.action_register_Member_Fragment_to_register_main_Fragment);
                Common.showToast(activity,"帳號已登出");
            }
        });
    }
}