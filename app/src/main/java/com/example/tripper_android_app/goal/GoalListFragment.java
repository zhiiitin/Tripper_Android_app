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
    public static final String TAG = "TAG_GoalListFragment";
    private MainActivity activity;
    private ImageButton ibHiking, ibMountainBike, ibCar, ibFlight,
            ibRocket, ibPositiveVote, ibTrophy, ibFire, ibHands, dialog_ibDown;
    private ImageView dialog_ivBig;
    private TextView tvHiking, tvMountainBike, tvCar, tvFlight,
            tvRocket, tvPositiveVote, tvTrophy, tvFire, tvHands, dialog_tvGoal;
    private int goalBundle;

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
        // AlertDialog預設的顯示位置是window的位置
        Window window = alertDialog.getWindow();
        // 將window設定在底部位置
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.popupAnimation);
        View view = View.inflate(activity, R.layout.alert_dialog_view, null);
        // 讓成就字卡show出的圖會依照不同的成就去顯示對應成就的圖示
        dialog_ivBig = view.findViewById(R.id.dialog_ivBig);
        dialog_ivBig.setImageResource(imageId);
        // 讓成就字卡show出的文字會依照不同的成就去顯示對應成就的條件
        dialog_tvGoal = view.findViewById(R.id.dialog_tvGoal);
        dialog_tvGoal.setText(articleText);
        // 點擊成就字卡上的"向下按鈕"會讓字卡下滑消失
        dialog_ibDown = view.findViewById(R.id.dialog_ibDown);
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
                showDialog(R.drawable.ic_goal_hiking_big,
                        "只要建立完成" + "1個行程" + "、1個網誌，" +
                                "\n並參與1個揪團" + "，就可以解鎖該成就哦！");
            }
        });

        tvHiking = view.findViewById(R.id.tvHiking);
        tvHiking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(R.drawable.ic_goal_hiking_big,
                        "只要建立完成" + "1個行程" + "、1個網誌，" +
                                "\n並參與1個揪團" + "，就可以解鎖該成就哦！");
            }
        });


        // 成就2：旅遊卡達恰
        ibMountainBike = view.findViewById(R.id.ibMountainBike);
        ibMountainBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(R.drawable.ic_goal_mountain_bike_big,
                        "只要建立完成" + "3個行程" + "、3個網誌，" +
                                "\n並參與3個揪團" + "，就可以解鎖該成就哦！");
            }
        });


        tvMountainBike = view.findViewById(R.id.tvMountainBike);
        tvMountainBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(R.drawable.ic_goal_mountain_bike_big,
                        "只要建立完成" + "3個行程" + "、3個網誌，" +
                                "\n並參與3個揪團" + "，就可以解鎖該成就哦！");
            }
        });


        // 成就3：旅遊特快車
        ibCar = view.findViewById(R.id.ibCar);
        ibCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(R.drawable.ic_goal_car_big,
                        "只要建立完成" + "5個行程" + "、5個網誌，" +
                                "\n並參與5個揪團" + "，就可以解鎖該成就哦！");
            }
        });

        tvCar = view.findViewById(R.id.tvCar);
        tvCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(R.drawable.ic_goal_car_big,
                        "只要建立完成" + "5個行程" + "、5個網誌，" +
                                "\n並參與5個揪團" + "，就可以解鎖該成就哦！");
            }
        });


        // 成就4：空軍一號
        ibFlight = view.findViewById(R.id.ibFlight);
        ibFlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(R.drawable.ic_goal_flight_big,
                        "只要建立完成" + "7個行程" + "、7個網誌，" +
                                "\n並參與7個揪團" + "，就可以解鎖該成就哦！");
            }
        });

        tvFlight = view.findViewById(R.id.tvFlight);
        tvFlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(R.drawable.ic_goal_flight_big,
                        "只要建立完成" + "7個行程" + "、7個網誌，" +
                                "\n並參與7個揪團" + "，就可以解鎖該成就哦！");
            }
        });


        // 成就5：火箭效率
        ibRocket = view.findViewById(R.id.ibRocket);
        ibRocket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(R.drawable.ic_goal_rocket_big,
                        "只要建立完成" + "10個行程" + "、10個網誌，" +
                                "\n並參與10個揪團" + "，就可以解鎖該成就哦！");
            }
        });

        tvRocket = view.findViewById(R.id.tvRocket);
        tvRocket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(R.drawable.ic_goal_rocket_big,
                        "只要建立完成" + "10個行程" + "、10個網誌，" +
                                "\n並參與10個揪團" + "，就可以解鎖該成就哦！");
            }
        });


        // 成就6：網誌達人
        ibPositiveVote = view.findViewById(R.id.ibPositiveVote);
        ibPositiveVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(R.drawable.ic_goal_positive_vote_big,
                        "只要建立完成" + "15個行程" + "、15個網誌，" +
                                "\n並參與15個揪團" + "，就可以解鎖該成就哦！");
            }
        });

        tvPositiveVote = view.findViewById(R.id.tvPositiveVote);
        tvPositiveVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(R.drawable.ic_goal_positive_vote_big,
                        "只要建立完成" + "15個行程" + "、15個網誌，" +
                                "\n並參與15個揪團" + "，就可以解鎖該成就哦！");
            }
        });


        // 成就7：瀏覽點閱王
        ibTrophy = view.findViewById(R.id.ibTrophy);
        ibTrophy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(R.drawable.ic_goal_trophy_big,
                        "只要建立完成" + "18個行程" + "、18個網誌，" +
                                "\n並參與18個揪團" + "，就可以解鎖該成就哦！");
            }
        });

        tvTrophy = view.findViewById(R.id.tvTrophy);
        tvTrophy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(R.drawable.ic_goal_trophy_big,
                        "只要建立完成" + "18個行程" + "、18個網誌，" +
                                "\n並參與18個揪團" + "，就可以解鎖該成就哦！");
            }
        });


        // 成就8：超人氣行程
        ibFire = view.findViewById(R.id.ibFire);
        ibFire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(R.drawable.ic_goal_fire_big,
                        "只要建立完成" + "19個行程" + "、19個網誌，" +
                                "\n並參與19個揪團" + "，就可以解鎖該成就哦！");
            }
        });

        tvFire = view.findViewById(R.id.tvFire);
        tvFire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(R.drawable.ic_goal_fire_big,
                        "只要建立完成" + "19個行程" + "、19個網誌，" +
                                "\n並參與19個揪團" + "，就可以解鎖該成就哦！");
            }
        });


        // 成就9：揪團高手
        ibHands = view.findViewById(R.id.ibHands);
        ibHands.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(R.drawable.ic_goal_hands_big,
                        "只要建立完成" + "20個行程" + "、20個網誌，" +
                                "\n並參與20個揪團" + "，就可以解鎖該成就哦！");
            }
        });

        tvHands = view.findViewById(R.id.tvHands);
        tvHands.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(R.drawable.ic_goal_hands_big,
                        "只要建立完成" + "20個行程" + "、20個網誌，" +
                                "\n並參與20個揪團" + "，就可以解鎖該成就哦！");
            }
        });

        /* 依照成就DB的欄位，成就ID的數字與達成的成就個數意義相同(ID剛好等於成就個數)，
           故將成就ID利用Bundle從"成就主頁"帶到"我的成就"頁面去判斷與顯示成就解鎖的圖示。 */
        Bundle bundle = getArguments();
        if(bundle == null){
            Navigation.findNavController(view).popBackStack();
            return;
        }
        goalBundle = bundle.getInt("goalId");
        if (goalBundle >= 1) {
            ibHiking.setImageResource(R.drawable.ic_goal_hiking);
        }
        if (goalBundle >= 2) {
            ibMountainBike.setImageResource(R.drawable.ic_goal_mountain_bike);
        }
        if (goalBundle >= 3) {
            ibCar.setImageResource(R.drawable.ic_goal_car);
        }
        if (goalBundle >= 4) {
            ibFlight.setImageResource(R.drawable.ic_goal_flight);
        }
        if (goalBundle >= 5) {
            ibRocket.setImageResource(R.drawable.ic_goal_rocket);
        }
        if (goalBundle >= 6) {
            ibPositiveVote.setImageResource(R.drawable.ic_goal_positive_vote);
        }
        if (goalBundle >= 7) {
            ibTrophy.setImageResource(R.drawable.ic_goal_trophy);
        }
        if (goalBundle >= 8) {
            ibFire.setImageResource(R.drawable.ic_goal_fire);
        }
        if (goalBundle >= 9) {
            ibHands.setImageResource(R.drawable.ic_goal_hands);
        }
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