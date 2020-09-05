package com.example.tripper_android_app.setting.member;

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
import android.widget.TextView;

import com.example.tripper_android_app.R;


public class Register_Member_Fragment extends Fragment {
    private final static String TAG = "TAG_MemberFragment";
    private FragmentActivity activity;
    private TextView tvId, tvNickName, tvLoginType;


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

        Bundle bundle = getArguments();
        if (bundle == null || bundle.getSerializable("Member") == null) {
            Common.showToast(activity, "Profile not found");
            navController.popBackStack();
            return;
        } else {
            Member member = (Member) bundle.getSerializable("Member");


            String id = member.getId() + "";
            String nickname = member.getNickName();
            String loginType = member.getLoginType() + "";
            tvId.setText(id);
            tvNickName.setText(nickname);
            tvLoginType.setText(loginType);
        }
    }
}