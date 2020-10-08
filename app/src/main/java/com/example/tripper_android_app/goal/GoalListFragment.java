package com.example.tripper_android_app.goal;

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

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.tripper_android_app.MainActivity;
import com.example.tripper_android_app.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class GoalListFragment extends Fragment {
    private MainActivity activity;
    private ImageButton ibHiking, ibMountainBike, ibCar, ibFlight,
            ibRocket, ibPositiveVote, ibTrophy, ibFire, ibHands;
    private TextView tvHiking, tvMountainBike, tvCar, tvFlight,
            tvRocket, tvPositiveVote, tvTrophy, tvFire, tvHands;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_goal_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 設定toolbar
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("我的成就");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorForWhite));
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 讓toolbar的返回箭頭變白色
        Drawable upArrow = ContextCompat.getDrawable(activity, R.drawable.abc_ic_ab_back_material);
        if(upArrow != null) {
            upArrow.setColorFilter(ContextCompat.getColor(activity, R.color.colorForWhite), PorterDuff.Mode.SRC_ATOP);
            activity.getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }

        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottomBar);
        NavController navController = Navigation.findNavController(activity, R.id.goal_HomePage);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        Menu itemMenu = bottomNavigationView.getMenu();
        itemMenu.getItem(2).setChecked(true);


        // 成就1：旅行初心者
        ibHiking = view.findViewById(R.id.ibHiking);
        ibHiking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        tvHiking = view.findViewById(R.id.tvHiking);
        tvHiking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        // 成就2：旅遊卡達恰
        ibMountainBike = view.findViewById(R.id.ibMountainBike);
        ibMountainBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        tvMountainBike = view.findViewById(R.id.tvMountainBike);
        tvMountainBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        // 成就3：旅遊特快車
        ibCar = view.findViewById(R.id.ibCar);
        ibCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        tvCar = view.findViewById(R.id.tvCar);
        tvCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        // 成就4：空軍一號
        ibFlight = view.findViewById(R.id.ibFlight);
        ibFlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        tvFlight = view.findViewById(R.id.tvFlight);
        tvFlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        // 成就5：火箭效率
        ibRocket = view.findViewById(R.id.ibRocket);
        ibRocket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        tvRocket = view.findViewById(R.id.tvRocket);
        tvRocket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        // 成就6：網誌達人
        ibPositiveVote = view.findViewById(R.id.ibPositiveVote);
        ibPositiveVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        tvPositiveVote = view.findViewById(R.id.tvPositiveVote);
        tvPositiveVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        // 成就7：瀏覽點閱王
        ibTrophy = view.findViewById(R.id.ibTrophy);
        ibTrophy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        tvTrophy = view.findViewById(R.id.tvTrophy);
        tvTrophy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        // 成就8：超人氣行程
        ibFire = view.findViewById(R.id.ibFire);
        ibFire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        tvFire = view.findViewById(R.id.tvFire);
        tvFire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        // 成就9：揪團高手
        ibHands = view.findViewById(R.id.ibHands);
        ibHands.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        tvHands = view.findViewById(R.id.tvHands);
        tvHands.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Navigation.findNavController(ibRocket).popBackStack();
                return true;
            default:
                break;
        }
        return true;
    }

}