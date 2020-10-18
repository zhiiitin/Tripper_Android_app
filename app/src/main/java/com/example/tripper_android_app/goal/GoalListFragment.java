package com.example.tripper_android_app.goal;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tripper_android_app.MainActivity;
import com.example.tripper_android_app.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class GoalListFragment extends Fragment {
    private MainActivity activity;
    private ImageButton ibHiking, ibMountainBike, ibCar, ibFlight,
            ibRocket, ibPositiveVote, ibTrophy, ibFire, ibHands, dialog_ibDown;
    private ImageView dialog_ivBig;
    private TextView tvHiking, tvMountainBike, tvCar, tvFlight,
            tvRocket, tvPositiveVote, tvTrophy, tvFire, tvHands, dialog_tvGoal;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
        setHasOptionsMenu(true);
    }

    // 使用AlertDialog建立彈出的成就字卡
    private void showDialog(int imageId, String articleText) {
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.popupAnimation);
        View view = View.inflate(activity, R.layout.alert_dialog_view, null);
        dialog_ivBig = (ImageView) view.findViewById(R.id.dialog_ivBig);
        dialog_ivBig.setImageResource(imageId);
        dialog_tvGoal = (TextView) view.findViewById(R.id.dialog_tvGoal);
        dialog_tvGoal.setText(articleText);
        dialog_ibDown = (ImageButton) view.findViewById(R.id.dialog_ibDown);
        dialog_ibDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        // 將彈出的成就字卡設定成圓角邊框
        WindowManager.LayoutParams layoutParams = alertDialog.getWindow().getAttributes();
        layoutParams.width = 970;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        window.setAttributes(layoutParams);
        window.setBackgroundDrawableResource(R.drawable.edit_bg_yellow);
        window.setContentView(view);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_goal_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
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
                showDialog(R.drawable.ic_goal_hiking_big, "只要蒐集到5個景點就可以解鎖該成就哦！");
            }
        });

        tvHiking = view.findViewById(R.id.tvHiking);
        tvHiking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(R.drawable.ic_goal_hiking_big, "只要蒐集到5個景點就可以解鎖該成就哦！");
            }
        });


        // 成就2：旅遊卡達恰
        ibMountainBike = view.findViewById(R.id.ibMountainBike);
        ibMountainBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(R.drawable.ic_goal_mountain_bike_big, "只要蒐集到5個景點就可以解鎖該成就哦！");
            }
        });


        tvMountainBike = view.findViewById(R.id.tvMountainBike);
        tvMountainBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(R.drawable.ic_goal_mountain_bike_big,"只要蒐集到5個景點就可以解鎖該成就哦！");
            }
        });


        // 成就3：旅遊特快車
        ibCar = view.findViewById(R.id.ibCar);
        ibCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(R.drawable.ic_goal_car_big,"只要蒐集到5個景點就可以解鎖該成就哦！");
            }
        });

        tvCar = view.findViewById(R.id.tvCar);
        tvCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(R.drawable.ic_goal_car_big, "只要蒐集到5個景點就可以解鎖該成就哦！");
            }
        });


        // 成就4：空軍一號
        ibFlight = view.findViewById(R.id.ibFlight);
        ibFlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(R.drawable.ic_goal_flight_big, "只要蒐集到5個景點就可以解鎖該成就哦！");
            }
        });

        tvFlight = view.findViewById(R.id.tvFlight);
        tvFlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(R.drawable.ic_goal_flight_big, "只要蒐集到5個景點就可以解鎖該成就哦！");
            }
        });


        // 成就5：火箭效率
        ibRocket = view.findViewById(R.id.ibRocket);
        ibRocket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(R.drawable.ic_goal_rocket_big, "只要蒐集到5個景點就可以解鎖該成就哦！");
            }
        });

        tvRocket = view.findViewById(R.id.tvRocket);
        tvRocket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(R.drawable.ic_goal_rocket_big, "只要蒐集到5個景點就可以解鎖該成就哦！");
            }
        });


        // 成就6：網誌達人
        ibPositiveVote = view.findViewById(R.id.ibPositiveVote);
        ibPositiveVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(R.drawable.ic_goal_positive_vote_big, "只要蒐集到5個景點就可以解鎖該成就哦！");
            }
        });

        tvPositiveVote = view.findViewById(R.id.tvPositiveVote);
        tvPositiveVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(R.drawable.ic_goal_positive_vote_big, "只要蒐集到5個景點就可以解鎖該成就哦！");
            }
        });


        // 成就7：瀏覽點閱王
        ibTrophy = view.findViewById(R.id.ibTrophy);
        ibTrophy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(R.drawable.ic_goal_trophy_big, "只要蒐集到5個景點就可以解鎖該成就哦！");
            }
        });

        tvTrophy = view.findViewById(R.id.tvTrophy);
        tvTrophy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(R.drawable.ic_goal_trophy_big, "只要蒐集到5個景點就可以解鎖該成就哦！");
            }
        });


        // 成就8：超人氣行程
        ibFire = view.findViewById(R.id.ibFire);
        ibFire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(R.drawable.ic_goal_fire_big, "只要蒐集到5個景點就可以解鎖該成就哦！");
            }
        });

        tvFire = view.findViewById(R.id.tvFire);
        tvFire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(R.drawable.ic_goal_fire_big, "只要蒐集到5個景點就可以解鎖該成就哦！");
            }
        });


        // 成就9：揪團高手
        ibHands = view.findViewById(R.id.ibHands);
        ibHands.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(R.drawable.ic_goal_hands_big, "只要蒐集到5個景點就可以解鎖該成就哦！");
            }
        });

        tvHands = view.findViewById(R.id.tvHands);
        tvHands.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(R.drawable.ic_goal_hands_big, "只要蒐集到5個景點就可以解鎖該成就哦！");
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