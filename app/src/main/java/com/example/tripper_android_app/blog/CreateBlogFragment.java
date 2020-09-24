package com.example.tripper_android_app.blog;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tripper_android_app.MainActivity;
import com.example.tripper_android_app.R;
import com.example.tripper_android_app.task.CommonTask;
import com.example.tripper_android_app.trip.Trip_M;
import com.example.tripper_android_app.util.Common;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class CreateBlogFragment extends Fragment {
    private static final String TAG = "TAG_Create_Blog_Fragment";
    private RecyclerView rvBlog , rvPhoto;
    private MainActivity activity;
    private CommonTask groupGetAllTask;
    private TextView tvBlogName,tvDate,tvTime ;
    private List<String> spotList;
    private List<String> dayList;
    private  int i=0;
    private  int day1Num = 0 ;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_blog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//ToolBar
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("編輯網誌");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorForWhite));
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Drawable upArrow = ContextCompat.getDrawable(activity, R.drawable.abc_ic_ab_back_material);
        if (upArrow != null) {
            upArrow.setColorFilter(ContextCompat.getColor(activity, R.color.colorForWhite), PorterDuff.Mode.SRC_ATOP);
            activity.getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }

        tvBlogName = view.findViewById(R.id.tvBlogName);
        tvDate = view.findViewById(R.id.tvDate);
        tvTime = view.findViewById(R.id.tvTime);
//RecyclerView
        rvBlog = view.findViewById(R.id.rvBlog);
        rvBlog.setLayoutManager(new LinearLayoutManager(activity));

        rvPhoto = view.findViewById(R.id.rvPhoto);
//取得前頁bungle
        Bundle bundle = getArguments();
        String blogName = bundle.getString("tripName");
        String startDate = bundle.getString("tripDate");
        tvBlogName.setText(blogName);
        tvDate.setText("出發日期："+startDate);
//取得List
        spotList = getSpots();
        showSpots(spotList);

        dayList = getDays();
        showdays(dayList);

    }
//創建Adapter
    private class BlogSpotAdapter extends RecyclerView.Adapter{
        private LayoutInflater layoutInflater;
        private int imageSize;
        private List<String> spotList ;
        private List<String> dayList ;

        BlogSpotAdapter(Context context,List<String> spotList){
            layoutInflater = LayoutInflater.from(context);
            this.spotList = spotList ;
            imageSize = getResources().getDisplayMetrics().widthPixels / 2;
        }

        @Override
        public int getItemViewType(int position) {
            if(position == 0){
                return 2 ;
            }else if(position >0 && position < spotList.size()+1){
                return 1;
            }else {
                return 2;
            }

        }

        void setSpots(List<String> spotList) {
            this.spotList = spotList;
        }

        void setDays(List<String> dayList) {
            this.dayList = dayList;
        }


        @Override
        public int getItemCount() {
            int spotNum = spotList == null ? 0 : spotList.size();
            int nameNum = dayList == null ? 0 :dayList.size();
            return spotNum + nameNum ;
        }
//根據viewType 顯示不同RecyclerView
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if(viewType == 1){
                View itemView = layoutInflater.inflate(R.layout.item_view_createblog_spot, parent, false);
                return new ViewHolderSpot(itemView);
            }
            else {
                View itemView = layoutInflater.inflate(R.layout.item_view_createblog_day, parent, false);
                return new ViewHolderDay(itemView);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            int day1count = spotList.size()+1;

            if(position == 0 || position == day1count ){
                final String blog_day = dayList.get(i);
                ViewHolderDay viewHolderDay = (ViewHolderDay) holder ;
                viewHolderDay.tvDay.setText(blog_day);
                i ++ ;
            }
            else if(position > 0 && position < day1count){
                final String blog_spot = spotList.get(day1Num);
                ViewHolderSpot viewHolderSpot = (ViewHolderSpot) holder;
                viewHolderSpot.tvLocationName.setText(blog_spot);
                day1Num ++ ;
            }

//            if(holder instanceof ViewHolderSpot){
//                final String blog_spot = spotList.get(position-1);
//                ViewHolderSpot viewHolderSpot = (ViewHolderSpot) holder;
//                viewHolderSpot.tvLocationName.setText(blog_spot);
//            }else if(holder instanceof ViewHolderDay) {
//                final String blog_day;
//                if (position == 0) {
//                    blog_day = dayList.get(position);
//                } else{
//                    blog_day = dayList.get(position - spotList.size());
//            }
//                ViewHolderDay viewHolderDay = (ViewHolderDay) holder ;
//                viewHolderDay.tvDay.setText(blog_day);
//            }


        }
//秀景點的Adapter
        class ViewHolderSpot extends RecyclerView.ViewHolder {
            ImageView ivPoint,ivTextForm;
            TextView tvLocationName, tvInput;
            TextInputEditText etBlog ;
            ImageButton ibInsertPic ;


            ViewHolderSpot(View itemView) {
                super(itemView);
                ivPoint = itemView.findViewById(R.id.ivPoint);
                ivTextForm = itemView.findViewById(R.id.ivTextForm);
                tvLocationName = itemView.findViewById(R.id.tvLocationName);
                tvInput = itemView.findViewById(R.id.tvInput);
                etBlog = itemView.findViewById(R.id.etBlog);
                ibInsertPic = itemView.findViewById(R.id.ibInsertPic);
            }
        }
        //秀第幾天的Adapter
        class ViewHolderDay extends RecyclerView.ViewHolder{
            TextView tvDay;

            ViewHolderDay(View itemView){
                super(itemView);
                tvDay = itemView.findViewById(R.id.tvDay);
            }
        }

    }
//----------------取得景點List並放入Adapter--------------------

    private List<String> getSpots() {
        List<String> spotList = new ArrayList<>();
        spotList.add("軍艦岩");
        spotList.add("東北角");
        spotList.add("淡水");
        spotList.add("台北101");

        return spotList;
    }

    private void showSpots(List<String> spotList) {
        if (spotList == null || spotList.isEmpty()) {
            Common.showToast(activity, "搜尋不到行程");
        }
        BlogSpotAdapter blogSpotAdapter = (BlogSpotAdapter) rvBlog.getAdapter();
        if (blogSpotAdapter == null) {
            rvBlog.setAdapter(new BlogSpotAdapter(activity, spotList));
        } else {
            blogSpotAdapter.setSpots(spotList);
            blogSpotAdapter.notifyDataSetChanged();
        }
    }

    //----------------取得景點List並放入Adapter--------------------

    private List<String> getDays() {
        List<String> dayList = new ArrayList<>();

        dayList.add("Day-1");
        dayList.add("Day-2");
        dayList.add("Day-3");
        dayList.add("Day-4");
        return dayList;
    }

    private void showdays(List<String> dayList) {
        if (dayList == null || dayList.isEmpty()) {
            Common.showToast(activity, "搜尋不到行程");
        }
        BlogSpotAdapter blogSpotAdapter = (BlogSpotAdapter) rvBlog.getAdapter();
        if (blogSpotAdapter == null) {
            rvBlog.setAdapter(new BlogSpotAdapter(activity, dayList));
        } else {
            blogSpotAdapter.setDays(dayList);
            blogSpotAdapter.notifyDataSetChanged();
        }
    }



}
//a= onsize()+1
//b= a+twosize()+1
//c =b+three()+1
//int = 0
//if(p==0 || p == onesize()+1 || p == b || p==c){
//    listDay(i)
//        i++;
//        }
//if(p>0 && p < onesize()+1){
//    litstone.(num1)
//        num ++
//        }
//if(p>a && p<b){
//listtwo.(num2)
// num2 ++
//}
