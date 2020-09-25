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
    private RecyclerView rvBlog, rvPhoto;
    private MainActivity activity;
    private CommonTask groupGetAllTask;
    private TextView tvBlogName, tvDate, tvTime;


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
        tvDate.setText("出發日期：" + startDate);
//取得List

        //第一天景點
        List<String> spotList1 = getSpots();
        showSpots(spotList1);
        //第二天景點
        List<String> spotList2 = getSpots2();
        showSpots2(spotList2);
        //第三天景點
        List<String> spotList3 = getSpots3();
        showSpots3(spotList3);
        //第四天景點
        List<String> spotList4 = getSpots4();
        showSpots4(spotList4);
        //第五天景點
        List<String> spotList5 = getSpots5();
        showSpots5(spotList5);
        //第六天景點
        List<String> spotList6 = getSpots6();
        showSpots6(spotList6);

        List<String> dayList = getDays();
        showdays(dayList);

    }

    //創建Adapter
    private class BlogSpotAdapter extends RecyclerView.Adapter {
        private LayoutInflater layoutInflater;
        private int imageSize;
        private List<String> dayList;
        private List<String> spotList1;
        private List<String> spotList2;
        private List<String> spotList3;
        private List<String> spotList4;
        private List<String> spotList5;
        private List<String> spotList6;

        BlogSpotAdapter(Context context, List<String> spotList) {
            layoutInflater = LayoutInflater.from(context);
            this.spotList1 = spotList;
            this.spotList2 = spotList;
            imageSize = getResources().getDisplayMetrics().widthPixels / 2;
        }

        @Override
        public int getItemViewType(int position) {
            int day1count = spotList1.size() + 1;
            int day2count = spotList2.size() + 1;
            int day3count = spotList3.size() + 1;
            int day4count = spotList4.size() + 1;
            int day5count = spotList5.size() + 1;
            int day6count = spotList6.size() + 1;

            if (position == 0 || position == day1count || position == day1count + day2count || position == day1count + day2count + day3count || position == day1count + day2count + day3count + day4count || position == day1count + day2count + day3count + day4count + day5count || position == day1count + day2count + day3count + day4count + day5count + day6count) {
                return 2;
            } else if (position > 0 && position < day1count) {
                return 1;
            } else if (position > day1count && position < day1count + day2count) {
                return 1;
            } else if (position > day1count + day2count && position < day1count + day2count + day3count) {
                return 1;
            } else if (position > day1count + day2count + day3count && position < day1count + day2count + day3count + day4count) {
                return 1;
            } else if (position > day1count + day2count + day3count + day4count && position < day1count + day2count + day3count + day4count + day5count) {
                return 1;
            } else if (position > day1count + day2count + day3count + day4count + day5count ) {
                return 1;
            } else {
                return 0;
            }
        }

        void setSpots(List<String> spotList) {
            this.spotList1 = spotList;
        }

        void setDays(List<String> dayList) {
            this.dayList = dayList;
        }

        void setSpots2(List<String> spotList2) {
            this.spotList2 = spotList2;
        }

        void setSpots3(List<String> spotList3) {
            this.spotList3 = spotList3;
        }

        void setSpots4(List<String> spotList4) {
            this.spotList4 = spotList4;
        }

        void setSpots5(List<String> spotList5) {
            this.spotList5 = spotList5;
        }

        void setSpots6(List<String> spotList6) {
            this.spotList6 = spotList6;
        }

        @Override
        public int getItemCount() {
            int nameNum = dayList == null ? 0 : dayList.size();
            int spotNum1 = spotList1 == null ? 0 : spotList1.size();
            int spotNum2 = spotList2 == null ? 0 : spotList2.size();
            int spotNum3 = spotList3 == null ? 0 : spotList3.size();
            int spotNum4 = spotList4 == null ? 0 : spotList4.size();
            int spotNum5 = spotList5 == null ? 0 : spotList5.size();
            int spotNum6 = spotList6 == null ? 0 : spotList6.size();

            return spotNum1 + nameNum + spotNum2 + spotNum3 + spotNum4 + spotNum5 + spotNum6;
        }

        //根據viewType 顯示不同RecyclerView
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == 1) {
                View itemView = layoutInflater.inflate(R.layout.item_view_createblog_spot, parent, false);
                return new ViewHolderSpot(itemView);
            } else {
                View itemView = layoutInflater.inflate(R.layout.item_view_createblog_day, parent, false);
                return new ViewHolderDay(itemView);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            int day1count = spotList1.size() + 1;
            int day2count = day1count + spotList2.size() + 1;
            int day3count = day2count + spotList3.size() + 1;
            int day4count = day3count + spotList4.size() + 1;
            int day5count = day4count + spotList5.size() + 1;
            int day6count = day5count + spotList6.size() + 1;


            if (position == 0 || position == day1count || position == day2count || position == day3count || position == day4count || position == day5count || position == day6count) {
                String blog_day = "";
                if (position == 0) {
                    blog_day = dayList.get(position);
                }
                if (position == day1count) {
                    blog_day = dayList.get(position - spotList1.size());
                }
                if (position == day2count) {
                    blog_day = dayList.get(position - spotList1.size() - spotList2.size());
                }

                if (position == day3count) {
                    blog_day = dayList.get(position - spotList1.size() - spotList2.size() - spotList3.size());
                }

                if (position == day4count) {
                    blog_day = dayList.get(position - spotList1.size() - spotList2.size() - spotList3.size() - spotList4.size());
                }
                if (position == day5count) {
                    blog_day = dayList.get(position - spotList1.size() - spotList2.size() - spotList3.size() - spotList4.size() - spotList5.size());
                }

                if (position == day6count) {
                    blog_day = dayList.get(position - spotList1.size() - spotList2.size() - spotList3.size() - spotList4.size() - spotList5.size() - spotList6.size());
                }

                ViewHolderDay viewHolderDay = (ViewHolderDay) holder;
                viewHolderDay.tvDay.setText(blog_day);

            }


            if (position > 0 && position < day1count) {
                String blog_spot = spotList1.get(position - 1);
                ViewHolderSpot viewHolderSpot = (ViewHolderSpot) holder;
                viewHolderSpot.tvLocationName.setText(blog_spot);
            }

            if (position > day1count && position <  day2count) {
                String blog_spot = spotList2.get(position - 1 - day1count);
                ViewHolderSpot viewHolderSpot = (ViewHolderSpot) holder;
                viewHolderSpot.tvLocationName.setText(blog_spot);
            }

            if (position > day2count && position <  day3count) {
                String blog_spot = spotList3.get(position - 1 -  day2count);
                ViewHolderSpot viewHolderSpot = (ViewHolderSpot) holder;
                viewHolderSpot.tvLocationName.setText(blog_spot);
            }


            if (position > day3count && position < day4count) {
                String blog_spot = spotList4.get(position - 1 -  day3count);
                ViewHolderSpot viewHolderSpot = (ViewHolderSpot) holder;
                viewHolderSpot.tvLocationName.setText(blog_spot);
            }

            if (position >  day4count && position <  day5count) {
                String blog_spot = spotList5.get(position - 1  - day4count);
                ViewHolderSpot viewHolderSpot = (ViewHolderSpot) holder;
                viewHolderSpot.tvLocationName.setText(blog_spot);

            }

            if (position >  day5count && position <  day6count ) {
                String blog_spot = spotList6.get(position - 1  - day5count);
                ViewHolderSpot viewHolderSpot = (ViewHolderSpot) holder;
                viewHolderSpot.tvLocationName.setText(blog_spot);

            }


        }

        //秀景點的Adapter
        class ViewHolderSpot extends RecyclerView.ViewHolder {
            ImageView ivPoint, ivTextForm;
            TextView tvLocationName, tvInput;
            TextInputEditText etBlog;
            ImageButton ibInsertPic;


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
        class ViewHolderDay extends RecyclerView.ViewHolder {
            TextView tvDay;

            ViewHolderDay(View itemView) {
                super(itemView);
                tvDay = itemView.findViewById(R.id.tvDay);

            }
        }

    }
//----------------取得景點List並放入Adapter--------------------


    //第一天----------------------------------------
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

    //第二天----------------------------------------
    private List<String> getSpots2() {
        List<String> spotList = new ArrayList<>();
        spotList.add("鹽寮");
        spotList.add("水源地");
        spotList.add("七星潭");

        return spotList;
    }

    private void showSpots2(List<String> spotList) {
        if (spotList == null || spotList.isEmpty()) {
            Common.showToast(activity, "搜尋不到行程");
        }
        BlogSpotAdapter blogSpotAdapter = (BlogSpotAdapter) rvBlog.getAdapter();
        if (blogSpotAdapter == null) {
            rvBlog.setAdapter(new BlogSpotAdapter(activity, spotList));
        } else {
            blogSpotAdapter.setSpots2(spotList);
            blogSpotAdapter.notifyDataSetChanged();
        }
    }

    //第三天----------------------------------------
    private List<String> getSpots3() {
        List<String> spotList = new ArrayList<>();
        spotList.add("台南");
        spotList.add("億載金城");
        spotList.add("大東夜市");


        return spotList;
    }

    private void showSpots3(List<String> spotList) {
        if (spotList == null || spotList.isEmpty()) {
            Common.showToast(activity, "搜尋不到行程");
        }
        BlogSpotAdapter blogSpotAdapter = (BlogSpotAdapter) rvBlog.getAdapter();
        if (blogSpotAdapter == null) {
            rvBlog.setAdapter(new BlogSpotAdapter(activity, spotList));
        } else {
            blogSpotAdapter.setSpots3(spotList);
            blogSpotAdapter.notifyDataSetChanged();
        }
    }

    //第四天----------------------------------------
    private List<String> getSpots4() {
        List<String> spotList = new ArrayList<>();
        spotList.add("桃園機場");
        spotList.add("合興車站");

        return spotList;
    }

    private void showSpots4(List<String> spotList) {
        if (spotList == null || spotList.isEmpty()) {
            Common.showToast(activity, "搜尋不到行程");
        }
        BlogSpotAdapter blogSpotAdapter = (BlogSpotAdapter) rvBlog.getAdapter();
        if (blogSpotAdapter == null) {
            rvBlog.setAdapter(new BlogSpotAdapter(activity, spotList));
        } else {
            blogSpotAdapter.setSpots4(spotList);
            blogSpotAdapter.notifyDataSetChanged();
        }
    }

    //第五天----------------------------------------
    private List<String> getSpots5() {
        List<String> spotList = new ArrayList<>();
        spotList.add("星星部落");


        return spotList;
    }

    private void showSpots5(List<String> spotList) {
        if (spotList == null || spotList.isEmpty()) {
            Common.showToast(activity, "搜尋不到行程");
        }
        BlogSpotAdapter blogSpotAdapter = (BlogSpotAdapter) rvBlog.getAdapter();
        if (blogSpotAdapter == null) {
            rvBlog.setAdapter(new BlogSpotAdapter(activity, spotList));
        } else {
            blogSpotAdapter.setSpots5(spotList);
            blogSpotAdapter.notifyDataSetChanged();
        }
    }

    //第六天----------------------------------------
    private List<String> getSpots6() {
        List<String> spotList = new ArrayList<>();
        spotList.add("中正紀念堂");
        spotList.add("陽明山");
        spotList.add("東北角");
        spotList.add("女王頭");

        return spotList;
    }

    private void showSpots6(List<String> spotList) {
        if (spotList == null || spotList.isEmpty()) {
            Common.showToast(activity, "搜尋不到行程");
        }
        BlogSpotAdapter blogSpotAdapter = (BlogSpotAdapter) rvBlog.getAdapter();
        if (blogSpotAdapter == null) {
            rvBlog.setAdapter(new BlogSpotAdapter(activity, spotList));
        } else {
            blogSpotAdapter.setSpots6(spotList);
            blogSpotAdapter.notifyDataSetChanged();
        }
    }

    //----------------取得天數List並放入Adapter--------------------

    private List<String> getDays() {
        List<String> dayList = new ArrayList<>();

        dayList.add("Day-1");
        dayList.add("Day-2");
        dayList.add("Day-3");
        dayList.add("Day-4");
        dayList.add("Day-5");
        dayList.add("Day-6");
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
