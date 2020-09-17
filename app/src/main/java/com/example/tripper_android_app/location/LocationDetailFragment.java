package com.example.tripper_android_app.location;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tripper_android_app.MainActivity;
import com.example.tripper_android_app.R;
import com.example.tripper_android_app.task.ImageTask;
import com.example.tripper_android_app.util.Common;

public class LocationDetailFragment extends Fragment {
    private static final String TAG = "LocationDetailFragment";
    private MainActivity activity;
    private Location location;
    private ImageView ivLocImage;
    private TextView tvLocName, tvAddress, tvDesc;
    private ImageButton ibBack;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_location_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("景點資訊");
        Bundle bundle = getArguments();
        if(bundle == null || bundle.getSerializable("location") == null){
            Navigation.findNavController(view)
                    .popBackStack();
            return;
        }
        location = (Location) bundle.getSerializable("location");
        ivLocImage = view.findViewById(R.id.ivLocImage);
        tvLocName = view.findViewById(R.id.tvLocName);
        tvAddress = view.findViewById(R.id.tvAddress);
        tvDesc = view.findViewById(R.id.tvDesc);
        ibBack = view.findViewById(R.id.ibBack);
        showLocation();
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v)
                        .popBackStack();
            }
        });
    }

    private void showLocation() {
        String url = Common.URL_SERVER + "LocationServlet";
        String locId = location.getLocId();
        int imageSize = getResources().getDisplayMetrics().widthPixels / 4;
        Bitmap bitmap = null;
        try {
            bitmap = new ImageTask(url, locId, imageSize).execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(bitmap != null){
            ivLocImage.setImageBitmap(bitmap);
        } else {
            ivLocImage.setImageResource(R.drawable.no_image);
        }
        tvLocName.setText(location.getName());
        tvAddress.setText(location.getAddress());
        tvDesc.setText(location.getInfo());
    }
}