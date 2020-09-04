package com.example.tripper_android_app.setting.member;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tripper_android_app.R;


public class Register_Member_Fragment extends Fragment {
    private final static String TAG = "TAG_MemberFragment" ;
    private FragmentActivity activity ;
    private TextView tvId,tvNickName,tvLoginType ;

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
        Bundle bundle = getArguments() ;
        Member member = (Member)bundle.getSerializable("member");

        tvId.setText(member.getId());
        tvNickName.setText(member.getNickName());
        tvLoginType.setText(member.getLoginType()+"");
    }
}