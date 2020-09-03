package com.example.tripper_android_app.setting;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.tripper_android_app.R;

public class Setting_Fragment extends Fragment {
<<<<<<< HEAD
=======
    private FragmentActivity activity;
>>>>>>> cb9d26730f22cf41d68246b0fa1fd1aae940a746

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
<<<<<<< HEAD

=======
        activity = getActivity();
>>>>>>> cb9d26730f22cf41d68246b0fa1fd1aae940a746
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
<<<<<<< HEAD
        super.onCreateView(inflater, container, savedInstanceState);
=======
        activity.setTitle("設定");
>>>>>>> cb9d26730f22cf41d68246b0fa1fd1aae940a746
        return inflater.inflate(R.layout.fragment_setting_, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
<<<<<<< HEAD
=======
        ImageButton ibCreateLocation = view.findViewById(R.id.ibCreateLocation);
        ibCreateLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v)
                        .navigate(R.id.action_setting_Fragment_to_createLocationFragment);
            }
        });
>>>>>>> cb9d26730f22cf41d68246b0fa1fd1aae940a746
    }
}