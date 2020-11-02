package com.example.tripper_android_app.blog;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tripper_android_app.MainActivity;
import com.example.tripper_android_app.R;
import com.example.tripper_android_app.explore.Explore;
import com.example.tripper_android_app.task.CommonTask;
import com.example.tripper_android_app.util.Common;
import com.example.tripper_android_app.util.DateUtil;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static androidx.navigation.Navigation.findNavController;


public class CreateBlogFragment extends Fragment {
    private static final String TAG = "TAG_Create_BlogFragment";
    private RecyclerView rvBlog, rvPhoto;
    private MainActivity activity;
    private CommonTask groupGet1Task, InsertNoteTask, getImageTask;
    private TextView tvBlogName, tvDate, tvTime;
    private String startDate, tripId;
    private Button btDay1, btDay2, btDay3, btDay4, btDay5, btDay6;
    private int num_columns = 4;
    private byte[] photo;
    private static final int REQ_TAKE_PICTURE = 0;
    private static final int REQ_PICK_PICTURE = 1;
    private static final int REQ_CROP_PICTURE = 2;
    private Uri contentUri;
    private List<Bitmap> bitmapList = new ArrayList<>();
    private BlogPic blogPic = null;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
        setHasOptionsMenu(true);
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
        toolbar.setTitle("建立網誌");
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
        rvBlog.setItemViewCacheSize(10);
//取得前頁bungle
        Bundle bundle = getArguments();
        String blogName = bundle.getString("tripName");
        startDate = bundle.getString("tripDate");
        tripId = bundle.getString("tripId");
        tvBlogName.setText(blogName);
        tvDate.setText("出發日期：" + startDate);
//取得List

        //第一天景點
        final List<Blog_SpotInfo> spotList1 = getSpots();
        showSpots(spotList1);
        //第二天景點
        List<Blog_SpotInfo> spotList2 = null;
        try {
            spotList2 = getSpots2();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        showSpots2(spotList2);
        //第三天景點
        List<Blog_SpotInfo> spotList3 = null;
        try {
            spotList3 = getSpots3();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        showSpots3(spotList3);
        //第四天景點
        List<Blog_SpotInfo> spotList4 = null;
        try {
            spotList4 = getSpots4();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        showSpots4(spotList4);
        //第五天景點
        List<Blog_SpotInfo> spotList5 = null;
        try {
            spotList5 = getSpots5();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        showSpots5(spotList5);
        //第六天景點
        List<Blog_SpotInfo> spotList6 = null;
        try {
            spotList6 = getSpots6();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        showSpots6(spotList6);

        List<String> dayList = null;
        try {
            dayList = getDays();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        showdays(dayList);

        final List<Blog_SpotInfo> finalSpotList2 = spotList2;
        final List<Blog_SpotInfo> finalSpotList3 = spotList3;
        final List<Blog_SpotInfo> finalSpotList4 = spotList4;
        final List<Blog_SpotInfo> finalSpotList5 = spotList5;
        final List<Blog_SpotInfo> finalSpotList6 = spotList6;

//實作按下天數按鈕，跳轉到position
        btDay1 = view.findViewById(R.id.btDay1);

        btDay1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rvBlog.smoothScrollToPosition(0);
            }
        });
        btDay2 = view.findViewById(R.id.btDay2);
        if (finalSpotList2.size() == 0) {
            btDay2.setVisibility(View.GONE);
        }
        btDay2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rvBlog.smoothScrollToPosition(spotList1.size() + 1 + 1);
            }
        });
        btDay3 = view.findViewById(R.id.btDay3);
        if (finalSpotList3.size() == 0) {
            btDay3.setVisibility(View.GONE);
        }

        btDay3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rvBlog.smoothScrollToPosition(spotList1.size() + 1 + finalSpotList2.size() + 1 + 1);
            }
        });
        btDay4 = view.findViewById(R.id.btDay4);
        if (finalSpotList4.size() == 0) {
            btDay4.setVisibility(View.GONE);
        }
        btDay4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rvBlog.smoothScrollToPosition(spotList1.size() + 1 + finalSpotList2.size() + 1 + finalSpotList3.size() + 1 + 1);
            }
        });
        btDay5 = view.findViewById(R.id.btDay5);
        if (finalSpotList5.size() == 0) {
            btDay5.setVisibility(View.GONE);
        }
        btDay5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rvBlog.smoothScrollToPosition(spotList1.size() + 1 + 1 + finalSpotList2.size() + 1 + finalSpotList3.size() + 1 + finalSpotList4.size() + 1);

            }
        });
        btDay6 = view.findViewById(R.id.btDay6);
        if (finalSpotList6.size() == 0) {
            btDay6.setVisibility(View.GONE);
        }
        btDay6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rvBlog.smoothScrollToPosition(spotList1.size() + 1 + 1 + finalSpotList2.size() + 1 + finalSpotList3.size() + 1 + finalSpotList4.size() + 1 + finalSpotList5.size() + 1);

            }
        });
    }

    //--------------------------------BottonBar-----------------------------------
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.app_bar_button_blog_next_step, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Navigation.findNavController(tvBlogName).navigate(R.id.action_createBlogFragment_to_create_Blog_Location_List);
                return true;
            case R.id.btNextStep:
                Bundle bundle = new Bundle();
                bundle.putString("tripId", tripId);
                findNavController(tvBlogName).navigate(R.id.action_createBlogFragment_to_createBlogFinishFragment, bundle);
                return true;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //-------------------------------------------------------------------------------
//創建Adapter
    private class BlogSpotAdapter extends RecyclerView.Adapter {
        private LayoutInflater layoutInflater;
        private int imageSize;
        private List<String> dayList;
        private List<Blog_SpotInfo> spotList1;
        private List<Blog_SpotInfo> spotList2;
        private List<Blog_SpotInfo> spotList3;
        private List<Blog_SpotInfo> spotList4;
        private List<Blog_SpotInfo> spotList5;
        private List<Blog_SpotInfo> spotList6;

        BlogSpotAdapter(Context context, List<Blog_SpotInfo> spotList) {
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
            } else if (position > day1count + day2count + day3count + day4count + day5count) {
                return 1;
            } else {
                return 0;
            }
        }

        void setSpots(List<Blog_SpotInfo> spotList) {
            this.spotList1 = spotList;
        }

        void setDays(List<String> dayList) {
            this.dayList = dayList;
        }

        void setSpots2(List<Blog_SpotInfo> spotList2) {
            this.spotList2 = spotList2;
        }

        void setSpots3(List<Blog_SpotInfo> spotList3) {
            this.spotList3 = spotList3;
        }

        void setSpots4(List<Blog_SpotInfo> spotList4) {
            this.spotList4 = spotList4;
        }

        void setSpots5(List<Blog_SpotInfo> spotList5) {
            this.spotList5 = spotList5;
        }

        void setSpots6(List<Blog_SpotInfo> spotList6) {
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
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            int spotNum1 = spotList1 == null ? 0 : spotList1.size();
            int spotNum2 = spotList2 == null ? 0 : spotList2.size();
            int spotNum3 = spotList3 == null ? 0 : spotList3.size();
            int spotNum4 = spotList4 == null ? 0 : spotList4.size();
            int spotNum5 = spotList5 == null ? 0 : spotList5.size();
            int spotNum6 = spotList6 == null ? 0 : spotList6.size();

            int day1count = spotNum1 + 1;
            int day2count = day1count + spotNum2 + 1;
            int day3count = day2count + spotNum3 + 1;
            int day4count = day3count + spotNum4 + 1;
            int day5count = day4count + spotNum5 + 1;
            int day6count = day5count + spotNum6 + 1;


            if (position == 0 || position == day1count || position == day2count || position == day3count || position == day4count || position == day5count || position == day6count) {
                String blog_day = "";

                if (position == 0) {
                    blog_day = dayList.get(position);
                }
                if (position == day1count && spotNum2 != 0) {
                    blog_day = dayList.get(position - spotNum1);
                }
                if (position == day2count && spotNum3 != 0) {
                    blog_day = dayList.get(position - spotNum1 - spotNum2);
                }

                if (position == day3count && spotNum4 != 0) {
                    blog_day = dayList.get(position - spotNum1 - spotNum2 - spotNum3);
                }

                if (position == day4count && spotNum5 != 0) {
                    blog_day = dayList.get(position - spotNum1 - spotNum2 - spotNum3 - spotNum4);
                }
                if (position == day5count && spotNum6 != 0) {
                    blog_day = dayList.get(position - spotNum1 - spotNum2 - spotNum3 - spotNum4 - spotNum5);
                }

                if (position == day6count) {
                    blog_day = dayList.get(position - spotNum1 - spotNum2 - spotNum3 - spotNum4 - spotNum5 - spotNum6);
                }

                ViewHolderDay viewHolderDay = (ViewHolderDay) holder;
                viewHolderDay.tvDay.setText(blog_day);

            }

//第一天景點列表-------------------------------------------------------------------------------
            if (position > 0 && position < day1count) {

                final Blog_SpotInfo blog_spot = spotList1.get(position - 1);
                final ViewHolderSpot viewHolderSpot = (ViewHolderSpot) holder;
                viewHolderSpot.tvLocationName.setText(blog_spot.getName());
//點擊新增照片進入到新增相片頁面
                viewHolderSpot.ibInsertPic.setOnClickListener(new View.OnClickListener() {  //點擊顯示照片recyclerView
                    @Override
                    public void onClick(View v) {
                        String spotName = blog_spot.getName();
                        String locId = blog_spot.getLoc_Id();
                        String blogID = blog_spot.getTrip_Id();
                        Bundle bundle = new Bundle();
                        bundle.putString("spotName", spotName);
                        bundle.putString("locId", locId);
                        bundle.putString("blogID", blogID);
                        Navigation.findNavController(v).navigate(R.id.action_createBlogFragment_to_createBlogPicFragment, bundle);
                    }
                });
//將選擇完且上傳的照片show出來
                String url = Common.URL_SERVER + "BlogServlet";
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("action", "getSpotImage");
                jsonObject.addProperty("blog_Id", blog_spot.getTrip_Id());
                jsonObject.addProperty("loc_Id", blog_spot.getLoc_Id());
                getImageTask = new CommonTask(url, jsonObject.toString());
                blogPic = new BlogPic();
                try {
                    String jsonIn = getImageTask.execute().get();
                    Type listType = new TypeToken<BlogPic>() {
                    }.getType();

                    blogPic = new Gson().fromJson(jsonIn, listType);
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
                if (blogPic != null) {
                    if (blogPic.getPic1() != null) {
                        byte[] img1 = Base64.decode(blogPic.getPic1(), Base64.DEFAULT);
                        Glide.with(activity).load(img1).into(viewHolderSpot.ivSpot1);
                        viewHolderSpot.ivSpot1.setVisibility(View.VISIBLE);
                    }
                    if (blogPic.getPic2() != null) {
                        byte[] img2 = Base64.decode(blogPic.getPic2(), Base64.DEFAULT);
                        Glide.with(activity).load(img2).into(viewHolderSpot.ivSpot2);
                        viewHolderSpot.ivSpot2.setVisibility(View.VISIBLE);
                    }
                    if (blogPic.getPic3() != null) {
                        byte[] img3 = Base64.decode(blogPic.getPic3(), Base64.DEFAULT);
                        Glide.with(activity).load(img3).into(viewHolderSpot.ivSpot3);
                        viewHolderSpot.ivSpot3.setVisibility(View.VISIBLE);
                    }
                    if (blogPic.getPic4() != null) {
                        byte[] img4 = Base64.decode(blogPic.getPic4(), Base64.DEFAULT);
                        Glide.with(activity).load(img4).into(viewHolderSpot.ivSpot4);
                        viewHolderSpot.ivSpot4.setVisibility(View.VISIBLE);
                    }
                }
//進到挑選照片頁面前，先將note存偏好設定
                SharedPreferences pref = activity.getSharedPreferences(Common.PREF_FILE,
                        MODE_PRIVATE);
                String note = pref.getString("blog_note" + blog_spot.getTrip_Id() + blog_spot.getLoc_Id(), null);
                if (note != null && !note.isEmpty()) {
                    viewHolderSpot.etBlog.setText(note);
                }
//------------------------------

                viewHolderSpot.etBlog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewHolderSpot.ibSave.setVisibility(View.VISIBLE);
                    }
                });
//將備註心得傳回資料庫
                viewHolderSpot.ibSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String blogNote = viewHolderSpot.etBlog.getText().toString().trim();
                        SharedPreferences pref = activity.getSharedPreferences(Common.PREF_FILE,
                                MODE_PRIVATE);
                        pref.edit().putString("blog_note" + blog_spot.getTrip_Id() + blog_spot.getLoc_Id(), blogNote).apply();

                        String url = Common.URL_SERVER + "BlogServlet";
                        Blog_Note blog_note = new Blog_Note(blog_spot.getLoc_Id(), blog_spot.getTrip_Id(), blogNote);
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("action", "insertBlogNote");
                        jsonObject.addProperty("blog_note", new Gson().toJson(blog_note));
                        InsertNoteTask = new CommonTask(url, jsonObject.toString());
                        int count = 0;
                        try {
                            String jsonIn = InsertNoteTask.execute().get();
                            count = Integer.parseInt(jsonIn);
                            if (count == 1) {
                                Log.e(TAG, "Note saved sucessful");
                                Common.showToast(activity, "儲存成功！");
                                viewHolderSpot.etBlog.setText(blogNote);
                            } else {
                                Common.showToast(activity, "請檢查網路");
                            }
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                    }
                });

            }
//第二天景點列表-------------------------------------------------------------------------------
            if (position > day1count && position < day2count) {
                final Blog_SpotInfo blog_spot = spotList2.get(position - 1 - day1count);
                final ViewHolderSpot viewHolderSpot = (ViewHolderSpot) holder;
                viewHolderSpot.tvLocationName.setText(blog_spot.getName());

//點擊新增照片進入到新增相片頁面
                viewHolderSpot.ibInsertPic.setOnClickListener(new View.OnClickListener() {  //點擊顯示照片recyclerView
                    @Override
                    public void onClick(View v) {
                        String spotName = blog_spot.getName();
                        String locId = blog_spot.getLoc_Id();
                        String blogID = blog_spot.getTrip_Id();
                        Bundle bundle = new Bundle();
                        bundle.putString("spotName", spotName);
                        bundle.putString("locId", locId);
                        bundle.putString("blogID", blogID);
                        Navigation.findNavController(v).navigate(R.id.action_createBlogFragment_to_createBlogPicFragment, bundle);
                    }
                });
//將選擇完且上傳的照片show出來
                String url = Common.URL_SERVER + "BlogServlet";
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("action", "getSpotImage");
                jsonObject.addProperty("blog_Id", blog_spot.getTrip_Id());
                jsonObject.addProperty("loc_Id", blog_spot.getLoc_Id());
                getImageTask = new CommonTask(url, jsonObject.toString());
                blogPic = new BlogPic();
                try {
                    String jsonIn = getImageTask.execute().get();
                    Type listType = new TypeToken<BlogPic>() {
                    }.getType();

                    blogPic = new Gson().fromJson(jsonIn, listType);
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
                if (blogPic != null) {
                    if (blogPic.getPic1() != null) {
                        byte[] img1 = Base64.decode(blogPic.getPic1(), Base64.DEFAULT);
                        Glide.with(activity).load(img1).into(viewHolderSpot.ivSpot1);
                        viewHolderSpot.ivSpot1.setVisibility(View.VISIBLE);
                    }
                    if (blogPic.getPic2() != null) {
                        byte[] img2 = Base64.decode(blogPic.getPic2(), Base64.DEFAULT);
                        Glide.with(activity).load(img2).into(viewHolderSpot.ivSpot2);
                        viewHolderSpot.ivSpot2.setVisibility(View.VISIBLE);
                    }
                    if (blogPic.getPic3() != null) {
                        byte[] img3 = Base64.decode(blogPic.getPic3(), Base64.DEFAULT);
                        Glide.with(activity).load(img3).into(viewHolderSpot.ivSpot3);
                        viewHolderSpot.ivSpot3.setVisibility(View.VISIBLE);
                    }
                    if (blogPic.getPic4() != null) {
                        byte[] img4 = Base64.decode(blogPic.getPic4(), Base64.DEFAULT);
                        Glide.with(activity).load(img4).into(viewHolderSpot.ivSpot4);
                        viewHolderSpot.ivSpot4.setVisibility(View.VISIBLE);
                    }
                }
//進到挑選照片頁面前，先將note存偏好設定
                SharedPreferences pref = activity.getSharedPreferences(Common.PREF_FILE,
                        MODE_PRIVATE);
                String note = pref.getString("blog_note" + blog_spot.getTrip_Id() + blog_spot.getLoc_Id(), null);
                if (note != null && !note.isEmpty()) {
                    viewHolderSpot.etBlog.setText(note);
                }
//-------------------------
//將備註心得傳回資料庫
                viewHolderSpot.ibSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String blogNote = viewHolderSpot.etBlog.getText().toString().trim();
                        SharedPreferences pref = activity.getSharedPreferences(Common.PREF_FILE,
                                MODE_PRIVATE);
                        pref.edit().putString("blog_note" + blog_spot.getTrip_Id() + blog_spot.getLoc_Id(), blogNote).apply();
                        if (Common.networkConnected(activity)) {
                            String url = Common.URL_SERVER + "BlogServlet";
                            Blog_Note blog_note = new Blog_Note(blog_spot.getLoc_Id(), blog_spot.getTrip_Id(), blogNote);
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("action", "insertBlogNote");
                            jsonObject.addProperty("blog_note", new Gson().toJson(blog_note));
                            InsertNoteTask = new CommonTask(url, jsonObject.toString());
                            int count = 0;
                            try {
                                String jsonIn = InsertNoteTask.execute().get();
                                count = Integer.parseInt(jsonIn);
                                if (count == 1) {
                                    Log.e(TAG, "Note saved sucessful");
                                    Common.showToast(activity, "儲存成功！");
                                    viewHolderSpot.etBlog.setText(blogNote);

                                } else {
                                    Common.showToast(activity, "請檢查網路");
                                }
                            } catch (Exception e) {
                                Log.e(TAG, e.toString());
                            }
                        }
                    }
                });
            }
//第三天景點列表-------------------------------------------------------------------------------
            if (position > day2count && position < day3count) {
                final Blog_SpotInfo blog_spot = spotList3.get(position - 1 - day2count);
                final ViewHolderSpot viewHolderSpot = (ViewHolderSpot) holder;
                viewHolderSpot.tvLocationName.setText(blog_spot.getName());
//點擊新增照片進入到新增相片頁面
                viewHolderSpot.ibInsertPic.setOnClickListener(new View.OnClickListener() {  //點擊顯示照片recyclerView
                    @Override
                    public void onClick(View v) {
                        String spotName = blog_spot.getName();
                        String locId = blog_spot.getLoc_Id();
                        String blogID = blog_spot.getTrip_Id();
                        Bundle bundle = new Bundle();
                        bundle.putString("spotName", spotName);
                        bundle.putString("locId", locId);
                        bundle.putString("blogID", blogID);
                        Navigation.findNavController(v).navigate(R.id.action_createBlogFragment_to_createBlogPicFragment, bundle);
                    }
                });
//將選擇完且上傳的照片show出來
                String url = Common.URL_SERVER + "BlogServlet";
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("action", "getSpotImage");
                jsonObject.addProperty("blog_Id", blog_spot.getTrip_Id());
                jsonObject.addProperty("loc_Id", blog_spot.getLoc_Id());
                getImageTask = new CommonTask(url, jsonObject.toString());
                blogPic = new BlogPic();
                try {
                    String jsonIn = getImageTask.execute().get();
                    Type listType = new TypeToken<BlogPic>() {
                    }.getType();

                    blogPic = new Gson().fromJson(jsonIn, listType);
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
                if (blogPic != null) {
                    if (blogPic.getPic1() != null) {
                        byte[] img1 = Base64.decode(blogPic.getPic1(), Base64.DEFAULT);
                        Glide.with(activity).load(img1).into(viewHolderSpot.ivSpot1);
                        viewHolderSpot.ivSpot1.setVisibility(View.VISIBLE);
                    }
                    if (blogPic.getPic2() != null) {
                        byte[] img2 = Base64.decode(blogPic.getPic2(), Base64.DEFAULT);
                        Glide.with(activity).load(img2).into(viewHolderSpot.ivSpot2);
                        viewHolderSpot.ivSpot2.setVisibility(View.VISIBLE);
                    }
                    if (blogPic.getPic3() != null) {
                        byte[] img3 = Base64.decode(blogPic.getPic3(), Base64.DEFAULT);
                        Glide.with(activity).load(img3).into(viewHolderSpot.ivSpot3);
                        viewHolderSpot.ivSpot3.setVisibility(View.VISIBLE);
                    }
                    if (blogPic.getPic4() != null) {
                        byte[] img4 = Base64.decode(blogPic.getPic4(), Base64.DEFAULT);
                        Glide.with(activity).load(img4).into(viewHolderSpot.ivSpot4);
                        viewHolderSpot.ivSpot4.setVisibility(View.VISIBLE);
                    }
                }
//進到挑選照片頁面前，先將note存偏好設定
                SharedPreferences pref = activity.getSharedPreferences(Common.PREF_FILE,
                        MODE_PRIVATE);
                String note = pref.getString("blog_note" + blog_spot.getTrip_Id() + blog_spot.getLoc_Id(), null);
                if (note != null && !note.isEmpty()) {
                    viewHolderSpot.etBlog.setText(note);
                }
//--------------------------
                viewHolderSpot.etBlog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewHolderSpot.ibSave.setVisibility(View.VISIBLE);
                    }
                });
//將備註心得傳回資料庫
                viewHolderSpot.ibSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String blogNote = viewHolderSpot.etBlog.getText().toString().trim();
                        SharedPreferences pref = activity.getSharedPreferences(Common.PREF_FILE,
                                MODE_PRIVATE);
                        pref.edit().putString("blog_note" + blog_spot.getTrip_Id() + blog_spot.getLoc_Id(), blogNote).apply();

                        if (Common.networkConnected(activity)) {
                            String url = Common.URL_SERVER + "BlogServlet";
                            Blog_Note blog_note = new Blog_Note(blog_spot.getLoc_Id(), blog_spot.getTrip_Id(), blogNote);
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("action", "insertBlogNote");
                            jsonObject.addProperty("blog_note", new Gson().toJson(blog_note));
                            InsertNoteTask = new CommonTask(url, jsonObject.toString());
                            int count = 0;
                            try {
                                String jsonIn = InsertNoteTask.execute().get();
                                count = Integer.parseInt(jsonIn);
                                if (count == 1) {
                                    Log.e(TAG, "Note saved sucessful");
                                    Common.showToast(activity, "儲存成功！");
                                    viewHolderSpot.etBlog.setText(blogNote);

                                } else {
                                    Common.showToast(activity, "請檢查網路");
                                }
                            } catch (Exception e) {
                                Log.e(TAG, e.toString());
                            }
                        }
                    }
                });
            }
//第四天景點列表-------------------------------------------------------------------------------
            if (position > day3count && position < day4count) {
                final Blog_SpotInfo blog_spot = spotList4.get(position - 1 - day3count);
                final ViewHolderSpot viewHolderSpot = (ViewHolderSpot) holder;
                viewHolderSpot.tvLocationName.setText(blog_spot.getName());
//點擊新增照片進入到新增相片頁面
                viewHolderSpot.ibInsertPic.setOnClickListener(new View.OnClickListener() {  //點擊顯示照片recyclerView
                    @Override
                    public void onClick(View v) {
                        String spotName = blog_spot.getName();
                        String locId = blog_spot.getLoc_Id();
                        String blogID = blog_spot.getTrip_Id();
                        Bundle bundle = new Bundle();
                        bundle.putString("spotName", spotName);
                        bundle.putString("locId", locId);
                        bundle.putString("blogID", blogID);
                        Navigation.findNavController(v).navigate(R.id.action_createBlogFragment_to_createBlogPicFragment, bundle);
                    }
                });
//將選擇完且上傳的照片show出來
                String url = Common.URL_SERVER + "BlogServlet";
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("action", "getSpotImage");
                jsonObject.addProperty("blog_Id", blog_spot.getTrip_Id());
                jsonObject.addProperty("loc_Id", blog_spot.getLoc_Id());
                getImageTask = new CommonTask(url, jsonObject.toString());
                blogPic = new BlogPic();
                try {
                    String jsonIn = getImageTask.execute().get();
                    Type listType = new TypeToken<BlogPic>() {
                    }.getType();

                    blogPic = new Gson().fromJson(jsonIn, listType);
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
                if (blogPic != null) {
                    if (blogPic.getPic1() != null) {
                        byte[] img1 = Base64.decode(blogPic.getPic1(), Base64.DEFAULT);
                        Glide.with(activity).load(img1).into(viewHolderSpot.ivSpot1);
                        viewHolderSpot.ivSpot1.setVisibility(View.VISIBLE);
                    }
                    if (blogPic.getPic2() != null) {
                        byte[] img2 = Base64.decode(blogPic.getPic2(), Base64.DEFAULT);
                        Glide.with(activity).load(img2).into(viewHolderSpot.ivSpot2);
                        viewHolderSpot.ivSpot2.setVisibility(View.VISIBLE);
                    }
                    if (blogPic.getPic3() != null) {
                        byte[] img3 = Base64.decode(blogPic.getPic3(), Base64.DEFAULT);
                        Glide.with(activity).load(img3).into(viewHolderSpot.ivSpot3);
                        viewHolderSpot.ivSpot3.setVisibility(View.VISIBLE);
                    }
                    if (blogPic.getPic4() != null) {
                        byte[] img4 = Base64.decode(blogPic.getPic4(), Base64.DEFAULT);
                        Glide.with(activity).load(img4).into(viewHolderSpot.ivSpot4);
                        viewHolderSpot.ivSpot4.setVisibility(View.VISIBLE);
                    }
                }

//進到挑選照片頁面前，先將note存偏好設定
                SharedPreferences pref = activity.getSharedPreferences(Common.PREF_FILE,
                        MODE_PRIVATE);
                String note = pref.getString("blog_note" + blog_spot.getTrip_Id() + blog_spot.getLoc_Id(), null);
                if (note != null && !note.isEmpty()) {
                    viewHolderSpot.etBlog.setText(note);
                }
//-----------------
                viewHolderSpot.etBlog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewHolderSpot.ibSave.setVisibility(View.VISIBLE);
                    }
                });
//將備註心得傳回資料庫
                viewHolderSpot.ibSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String blogNote = viewHolderSpot.etBlog.getText().toString().trim();
                        SharedPreferences pref = activity.getSharedPreferences(Common.PREF_FILE,
                                MODE_PRIVATE);
                        pref.edit().putString("blog_note" + blog_spot.getTrip_Id() + blog_spot.getLoc_Id(), blogNote).apply();
                        if (Common.networkConnected(activity)) {
                            String url = Common.URL_SERVER + "BlogServlet";
                            Blog_Note blog_note = new Blog_Note(blog_spot.getLoc_Id(), blog_spot.getTrip_Id(), blogNote);
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("action", "insertBlogNote");
                            jsonObject.addProperty("blog_note", new Gson().toJson(blog_note));
                            InsertNoteTask = new CommonTask(url, jsonObject.toString());
                            int count = 0;
                            try {
                                String jsonIn = InsertNoteTask.execute().get();
                                count = Integer.parseInt(jsonIn);
                                if (count == 1) {
                                    Log.e(TAG, "Note saved sucessful");
                                    Common.showToast(activity, "儲存成功！");
                                    viewHolderSpot.etBlog.setText(blogNote);

                                } else {
                                    Common.showToast(activity, "請檢查網路");
                                }
                            } catch (Exception e) {
                                Log.e(TAG, e.toString());
                            }
                        }
                    }
                });
            }
//第五天景點列表-------------------------------------------------------------------------------
            if (position > day4count && position < day5count) {
                final Blog_SpotInfo blog_spot = spotList5.get(position - 1 - day4count);
                final ViewHolderSpot viewHolderSpot = (ViewHolderSpot) holder;
                viewHolderSpot.tvLocationName.setText(blog_spot.getName());
                //點擊新增照片進入到新增相片頁面
                viewHolderSpot.ibInsertPic.setOnClickListener(new View.OnClickListener() {  //點擊顯示照片recyclerView
                    @Override
                    public void onClick(View v) {
                        String spotName = blog_spot.getName();
                        String locId = blog_spot.getLoc_Id();
                        String blogID = blog_spot.getTrip_Id();
                        Bundle bundle = new Bundle();
                        bundle.putString("spotName", spotName);
                        bundle.putString("locId", locId);
                        bundle.putString("blogID", blogID);
                        Navigation.findNavController(v).navigate(R.id.action_createBlogFragment_to_createBlogPicFragment, bundle);
                    }
                });
//將選擇完且上傳的照片show出來
                String url = Common.URL_SERVER + "BlogServlet";
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("action", "getSpotImage");
                jsonObject.addProperty("blog_Id", blog_spot.getTrip_Id());
                jsonObject.addProperty("loc_Id", blog_spot.getLoc_Id());
                getImageTask = new CommonTask(url, jsonObject.toString());
                blogPic = new BlogPic();
                try {
                    String jsonIn = getImageTask.execute().get();
                    Type listType = new TypeToken<BlogPic>() {
                    }.getType();

                    blogPic = new Gson().fromJson(jsonIn, listType);
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
                if (blogPic != null) {
                    if (blogPic.getPic1() != null) {
                        byte[] img1 = Base64.decode(blogPic.getPic1(), Base64.DEFAULT);
                        Glide.with(activity).load(img1).into(viewHolderSpot.ivSpot1);
                        viewHolderSpot.ivSpot1.setVisibility(View.VISIBLE);
                    }
                    if (blogPic.getPic2() != null) {
                        byte[] img2 = Base64.decode(blogPic.getPic2(), Base64.DEFAULT);
                        Glide.with(activity).load(img2).into(viewHolderSpot.ivSpot2);
                        viewHolderSpot.ivSpot2.setVisibility(View.VISIBLE);
                    }
                    if (blogPic.getPic3() != null) {
                        byte[] img3 = Base64.decode(blogPic.getPic3(), Base64.DEFAULT);
                        Glide.with(activity).load(img3).into(viewHolderSpot.ivSpot3);
                        viewHolderSpot.ivSpot3.setVisibility(View.VISIBLE);
                    }
                    if (blogPic.getPic4() != null) {
                        byte[] img4 = Base64.decode(blogPic.getPic4(), Base64.DEFAULT);
                        Glide.with(activity).load(img4).into(viewHolderSpot.ivSpot4);
                        viewHolderSpot.ivSpot4.setVisibility(View.VISIBLE);
                    }
                }
//進到挑選照片頁面前，先將note存偏好設定
                SharedPreferences pref = activity.getSharedPreferences(Common.PREF_FILE,
                        MODE_PRIVATE);
                String note = pref.getString("blog_note" + blog_spot.getTrip_Id() + blog_spot.getLoc_Id(), null);
                if (note != null && !note.isEmpty()) {
                    viewHolderSpot.etBlog.setText(note);
                }

//----------------------
                viewHolderSpot.etBlog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewHolderSpot.ibSave.setVisibility(View.VISIBLE);
                    }
                });
//將備註心得傳回資料庫
                viewHolderSpot.ibSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String blogNote = viewHolderSpot.etBlog.getText().toString().trim();
//將文字先存入偏好設定檔
                        SharedPreferences pref = activity.getSharedPreferences(Common.PREF_FILE,
                                MODE_PRIVATE);
                        pref.edit().putString("blog_note" + blog_spot.getTrip_Id() + blog_spot.getLoc_Id(), blogNote).apply();
                        if (Common.networkConnected(activity)) {
                            String url = Common.URL_SERVER + "BlogServlet";
                            Blog_Note blog_note = new Blog_Note(blog_spot.getLoc_Id(), blog_spot.getTrip_Id(), blogNote);
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("action", "insertBlogNote");
                            jsonObject.addProperty("blog_note", new Gson().toJson(blog_note));
                            InsertNoteTask = new CommonTask(url, jsonObject.toString());
                            int count = 0;
                            try {
                                String jsonIn = InsertNoteTask.execute().get();
                                count = Integer.parseInt(jsonIn);
                                if (count == 1) {
                                    Log.e(TAG, "Note saved sucessful");
                                    Common.showToast(activity, "儲存成功！");
                                    viewHolderSpot.etBlog.setText(blogNote);

                                } else {
                                    Common.showToast(activity, "請檢查網路");
                                }
                            } catch (Exception e) {
                                Log.e(TAG, e.toString());
                            }
                        }
                    }
                });

            }
//第六天景點列表-------------------------------------------------------------------------------
            if (position > day5count && position < day6count) {
                final Blog_SpotInfo blog_spot = spotList6.get(position - 1 - day5count);
                final ViewHolderSpot viewHolderSpot = (ViewHolderSpot) holder;
                viewHolderSpot.tvLocationName.setText(blog_spot.getName());
                //點擊新增照片進入到新增相片頁面
                viewHolderSpot.ibInsertPic.setOnClickListener(new View.OnClickListener() {  //點擊顯示照片recyclerView
                    @Override
                    public void onClick(View v) {
                        String spotName = blog_spot.getName();
                        String locId = blog_spot.getLoc_Id();
                        String blogID = blog_spot.getTrip_Id();
                        Bundle bundle = new Bundle();
                        bundle.putString("spotName", spotName);
                        bundle.putString("locId", locId);
                        bundle.putString("blogID", blogID);
                        Navigation.findNavController(v).navigate(R.id.action_createBlogFragment_to_createBlogPicFragment, bundle);
                    }
                });
//將選擇完且上傳的照片show出來
                String url = Common.URL_SERVER + "BlogServlet";
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("action", "getSpotImage");
                jsonObject.addProperty("blog_Id", blog_spot.getTrip_Id());
                jsonObject.addProperty("loc_Id", blog_spot.getLoc_Id());
                getImageTask = new CommonTask(url, jsonObject.toString());
                blogPic = new BlogPic();
                try {
                    String jsonIn = getImageTask.execute().get();
                    Type listType = new TypeToken<BlogPic>() {
                    }.getType();

                    blogPic = new Gson().fromJson(jsonIn, listType);
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
                if (blogPic != null) {
                    if (blogPic.getPic1() != null) {
                        byte[] img1 = Base64.decode(blogPic.getPic1(), Base64.DEFAULT);
                        Glide.with(activity).load(img1).into(viewHolderSpot.ivSpot1);
                        viewHolderSpot.ivSpot1.setVisibility(View.VISIBLE);
                    }
                    if (blogPic.getPic2() != null) {
                        byte[] img2 = Base64.decode(blogPic.getPic2(), Base64.DEFAULT);
                        Glide.with(activity).load(img2).into(viewHolderSpot.ivSpot2);
                        viewHolderSpot.ivSpot2.setVisibility(View.VISIBLE);
                    }
                    if (blogPic.getPic3() != null) {
                        byte[] img3 = Base64.decode(blogPic.getPic3(), Base64.DEFAULT);
                        Glide.with(activity).load(img3).into(viewHolderSpot.ivSpot3);
                        viewHolderSpot.ivSpot3.setVisibility(View.VISIBLE);
                    }
                    if (blogPic.getPic4() != null) {
                        byte[] img4 = Base64.decode(blogPic.getPic4(), Base64.DEFAULT);
                        Glide.with(activity).load(img4).into(viewHolderSpot.ivSpot4);
                        viewHolderSpot.ivSpot4.setVisibility(View.VISIBLE);
                    }
                }
//進到挑選照片頁面前，先將note存偏好設定
                SharedPreferences pref = activity.getSharedPreferences(Common.PREF_FILE,
                        MODE_PRIVATE);
                String note = pref.getString("blog_note" + blog_spot.getTrip_Id() + blog_spot.getLoc_Id(), null);
                if (note != null && !note.isEmpty()) {
                    viewHolderSpot.etBlog.setText(note);
                }

//----------------------
                viewHolderSpot.etBlog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewHolderSpot.ibSave.setVisibility(View.VISIBLE);
                    }
                });
//將備註心得傳回資料庫
                viewHolderSpot.ibSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String blogNote = viewHolderSpot.etBlog.getText().toString().trim();
                        SharedPreferences pref = activity.getSharedPreferences(Common.PREF_FILE,
                                MODE_PRIVATE);
                        pref.edit().putString("blog_note" + blog_spot.getTrip_Id() + blog_spot.getLoc_Id(), blogNote).apply();
                        if (Common.networkConnected(activity)) {
                            String url = Common.URL_SERVER + "BlogServlet";
                            Blog_Note blog_note = new Blog_Note(blog_spot.getLoc_Id(), blog_spot.getTrip_Id(), blogNote);
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("action", "insertBlogNote");
                            jsonObject.addProperty("blog_note", new Gson().toJson(blog_note));
                            InsertNoteTask = new CommonTask(url, jsonObject.toString());
                            int count = 0;
                            try {
                                String jsonIn = InsertNoteTask.execute().get();
                                count = Integer.parseInt(jsonIn);
                                if (count == 1) {
                                    Log.e(TAG, "Note saved sucessful");
                                    Common.showToast(activity, "儲存成功！");
                                    viewHolderSpot.etBlog.setText(blogNote);

                                } else {
                                    Common.showToast(activity, "請檢查網路");
                                }
                            } catch (Exception e) {
                                Log.e(TAG, e.toString());
                            }
                        }
                    }
                });
            }
        }

        //秀景點的ViewHolder
        class ViewHolderSpot extends RecyclerView.ViewHolder {
            ImageView ivPoint, ivTextForm, ivSpot1, ivSpot2, ivSpot3, ivSpot4;
            TextView tvLocationName, tvInput;
            TextInputEditText etBlog;
            ImageButton ibInsertPic, ibSave, ibAdd;

            ViewHolderSpot(View itemView) {
                super(itemView);
                ivPoint = itemView.findViewById(R.id.ivPoint);
                ivTextForm = itemView.findViewById(R.id.ivTextForm);
                tvLocationName = itemView.findViewById(R.id.tvLocationName);
                tvInput = itemView.findViewById(R.id.tvInput);
                etBlog = itemView.findViewById(R.id.etBlog);
                ibInsertPic = itemView.findViewById(R.id.ibInsertPic);
                ivSpot1 = itemView.findViewById(R.id.ivSpot1);
                ivSpot2 = itemView.findViewById(R.id.ivSpot2);
                ivSpot3 = itemView.findViewById(R.id.ivSpot3);
                ivSpot4 = itemView.findViewById(R.id.ivSpot4);
                ibAdd = itemView.findViewById(R.id.ibAdd);
                ibSave = itemView.findViewById(R.id.ibSave);
            }
        }

        //秀第幾天的ViewHolder
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
    private List<Blog_SpotInfo> getSpots() {
        List<Blog_SpotInfo> spotList = new ArrayList<>();
        if (Common.networkConnected(activity)) {
            String Url = Common.URL_SERVER + "Trip_D_Servlet";
            DateAndId dateAndId = new DateAndId(startDate, tripId);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getSpotName");
            jsonObject.addProperty("dateAndId", new Gson().toJson(dateAndId));
            String jsonOut = jsonObject.toString();
            groupGet1Task = new CommonTask(Url, jsonOut);
            try {
                String jsonIn = groupGet1Task.execute().get();
                Type listtype = new TypeToken<List<Blog_SpotInfo>>() {
                }.getType();
                spotList = new Gson().fromJson(jsonIn, listtype);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Common.showToast(activity, "No Internet");
        }

        return spotList;
    }

    private void showSpots(List<Blog_SpotInfo> spotList) {
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
    private List<Blog_SpotInfo> getSpots2() throws ParseException {
        String date2 = DateUtil.date4day(startDate, 1);
        List<Blog_SpotInfo> spotList = new ArrayList<>();
        if (Common.networkConnected(activity)) {
            String Url = Common.URL_SERVER + "Trip_D_Servlet";
            DateAndId dateAndId = new DateAndId(date2, tripId);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getSpotName");
            jsonObject.addProperty("dateAndId", new Gson().toJson(dateAndId));
            String jsonOut = jsonObject.toString();
            groupGet1Task = new CommonTask(Url, jsonOut);
            try {
                String jsonIn = groupGet1Task.execute().get();
                Type listtype = new TypeToken<List<Blog_SpotInfo>>() {
                }.getType();
                spotList = new Gson().fromJson(jsonIn, listtype);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Common.showToast(activity, "No Internet");
        }

        return spotList;
    }

    private void showSpots2(List<Blog_SpotInfo> spotList) {
//        if (spotList == null || spotList.isEmpty()) {
//            Common.showToast(activity, "搜尋不到行程");
//        }
        BlogSpotAdapter blogSpotAdapter = (BlogSpotAdapter) rvBlog.getAdapter();
        if (blogSpotAdapter == null) {
            rvBlog.setAdapter(new BlogSpotAdapter(activity, spotList));
        } else {
            blogSpotAdapter.setSpots2(spotList);
            blogSpotAdapter.notifyDataSetChanged();
        }
    }

    //第三天----------------------------------------
    private List<Blog_SpotInfo> getSpots3() throws ParseException {
        String date3 = DateUtil.date4day(startDate, 2);
        List<Blog_SpotInfo> spotList = new ArrayList<>();
        if (Common.networkConnected(activity)) {
            String Url = Common.URL_SERVER + "Trip_D_Servlet";
            DateAndId dateAndId = new DateAndId(date3, tripId);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getSpotName");
            jsonObject.addProperty("dateAndId", new Gson().toJson(dateAndId));
            String jsonOut = jsonObject.toString();
            groupGet1Task = new CommonTask(Url, jsonOut);
            try {
                String jsonIn = groupGet1Task.execute().get();
                Type listtype = new TypeToken<List<Blog_SpotInfo>>() {
                }.getType();
                spotList = new Gson().fromJson(jsonIn, listtype);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Common.showToast(activity, "No Internet");
        }

        return spotList;
    }

    private void showSpots3(List<Blog_SpotInfo> spotList) {
//        if (spotList == null || spotList.isEmpty()) {
//            Common.showToast(activity, "搜尋不到行程");
//        }
        BlogSpotAdapter blogSpotAdapter = (BlogSpotAdapter) rvBlog.getAdapter();
        if (blogSpotAdapter == null) {
            rvBlog.setAdapter(new BlogSpotAdapter(activity, spotList));
        } else {
            blogSpotAdapter.setSpots3(spotList);
            blogSpotAdapter.notifyDataSetChanged();
        }
    }

    //第四天----------------------------------------
    private List<Blog_SpotInfo> getSpots4() throws ParseException {
        String date4 = DateUtil.date4day(startDate, 3);
        List<Blog_SpotInfo> spotList = new ArrayList<>();
        if (Common.networkConnected(activity)) {
            String Url = Common.URL_SERVER + "Trip_D_Servlet";
            DateAndId dateAndId = new DateAndId(date4, tripId);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getSpotName");
            jsonObject.addProperty("dateAndId", new Gson().toJson(dateAndId));
            String jsonOut = jsonObject.toString();
            groupGet1Task = new CommonTask(Url, jsonOut);
            try {
                String jsonIn = groupGet1Task.execute().get();
                Type listtype = new TypeToken<List<Blog_SpotInfo>>() {
                }.getType();
                spotList = new Gson().fromJson(jsonIn, listtype);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Common.showToast(activity, "No Internet");
        }

        return spotList;
    }

    private void showSpots4(List<Blog_SpotInfo> spotList) {
//        if (spotList == null || spotList.isEmpty()) {
//            Common.showToast(activity, "搜尋不到行程");
//        }
        BlogSpotAdapter blogSpotAdapter = (BlogSpotAdapter) rvBlog.getAdapter();
        if (blogSpotAdapter == null) {
            rvBlog.setAdapter(new BlogSpotAdapter(activity, spotList));
        } else {
            blogSpotAdapter.setSpots4(spotList);
            blogSpotAdapter.notifyDataSetChanged();
        }
    }

    //第五天----------------------------------------
    private List<Blog_SpotInfo> getSpots5() throws ParseException {
        String date5 = DateUtil.date4day(startDate, 4);
        List<Blog_SpotInfo> spotList = new ArrayList<>();
        if (Common.networkConnected(activity)) {
            String Url = Common.URL_SERVER + "Trip_D_Servlet";
            DateAndId dateAndId = new DateAndId(date5, tripId);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getSpotName");
            jsonObject.addProperty("dateAndId", new Gson().toJson(dateAndId));
            String jsonOut = jsonObject.toString();
            groupGet1Task = new CommonTask(Url, jsonOut);
            try {
                String jsonIn = groupGet1Task.execute().get();
                Type listtype = new TypeToken<List<Blog_SpotInfo>>() {
                }.getType();
                spotList = new Gson().fromJson(jsonIn, listtype);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Common.showToast(activity, "No Internet");
        }

        return spotList;
    }

    private void showSpots5(List<Blog_SpotInfo> spotList) {
//        if (spotList == null || spotList.isEmpty()) {
//            Common.showToast(activity, "搜尋不到行程");
//        }
        BlogSpotAdapter blogSpotAdapter = (BlogSpotAdapter) rvBlog.getAdapter();
        if (blogSpotAdapter == null) {
            rvBlog.setAdapter(new BlogSpotAdapter(activity, spotList));
        } else {
            blogSpotAdapter.setSpots5(spotList);
            blogSpotAdapter.notifyDataSetChanged();
        }
    }

    //第六天----------------------------------------
    private List<Blog_SpotInfo> getSpots6() throws ParseException {
        String date6 = DateUtil.date4day(startDate, 5);
        List<Blog_SpotInfo> spotList = new ArrayList<>();
        if (Common.networkConnected(activity)) {
            String Url = Common.URL_SERVER + "Trip_D_Servlet";
            DateAndId dateAndId = new DateAndId(date6, tripId);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getSpotName");
            jsonObject.addProperty("dateAndId", new Gson().toJson(dateAndId));
            String jsonOut = jsonObject.toString();
            groupGet1Task = new CommonTask(Url, jsonOut);
            try {
                String jsonIn = groupGet1Task.execute().get();
                Type listtype = new TypeToken<List<Blog_SpotInfo>>() {
                }.getType();
                spotList = new Gson().fromJson(jsonIn, listtype);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Common.showToast(activity, "No Internet");
        }

        return spotList;
    }

    private void showSpots6(List<Blog_SpotInfo> spotList) {
//        if (spotList == null || spotList.isEmpty()) {
//            Common.showToast(activity, "搜尋不到行程");
//        }
        BlogSpotAdapter blogSpotAdapter = (BlogSpotAdapter) rvBlog.getAdapter();
        if (blogSpotAdapter == null) {
            rvBlog.setAdapter(new BlogSpotAdapter(activity, spotList));
        } else {
            blogSpotAdapter.setSpots6(spotList);
            blogSpotAdapter.notifyDataSetChanged();
        }
    }

    //----------------取得天數List並放入Adapter--------------------

    private List<String> getDays() throws ParseException {
        List<String> dayList = new ArrayList<>();

        dayList.add(startDate);
        dayList.add(startDate = DateUtil.date4day(startDate, 1));
        dayList.add(startDate = DateUtil.date4day(startDate, 1));
        dayList.add(startDate = DateUtil.date4day(startDate, 1));
        dayList.add(startDate = DateUtil.date4day(startDate, 1));
        dayList.add(startDate = DateUtil.date4day(startDate, 1));
        return dayList;
    }

    private void showdays(List<String> dayList) {
        if (dayList == null || dayList.isEmpty()) {
            Common.showToast(activity, "搜尋不到行程");
        }
        BlogSpotAdapter blogSpotAdapter = (BlogSpotAdapter) rvBlog.getAdapter();
        if (blogSpotAdapter == null) {
//            rvBlog.setAdapter(new BlogSpotAdapter(activity, dayList));
        } else {
            blogSpotAdapter.setDays(dayList);
            blogSpotAdapter.notifyDataSetChanged();
        }
    }


    //對話視窗 挑選照片
    private void showTypeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final AlertDialog dialog = builder.create();
        final View view = View.inflate(getActivity(), R.layout.dialog_select_photo, null);
        TextView tv_select_gallery = (TextView) view.findViewById(R.id.tv_select_gallery);
        TextView tv_select_camera = (TextView) view.findViewById(R.id.tv_select_camera);
        tv_select_gallery.setOnClickListener(new View.OnClickListener() {
            // 在相簿中選取
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    openAlbum();
                }
                dialog.dismiss();
            }
        });
        tv_select_camera.setOnClickListener(new View.OnClickListener() {// 呼叫照相機
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                file = new File(file, "picture.jpg");
                contentUri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".provider", file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);

                if (intent.resolveActivity(activity.getPackageManager()) != null) {
                    startActivityForResult(intent, REQ_TAKE_PICTURE);
                    dialog.dismiss();
                } else {
                    Common.showToast(activity, "no camera app found");
                }

            }
        });

        dialog.setView(view);
        dialog.show();

    }

    private void openAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQ_PICK_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQ_TAKE_PICTURE:
                    crop(contentUri);
                    break;
                case REQ_PICK_PICTURE:
                    crop(intent.getData());
                    break;
                case REQ_CROP_PICTURE:
                    handleCropResult(intent);
                    break;
            }
        }
    }

    private void crop(Uri sourceImageUri) {
        File file = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        file = new File(file, "picture_cropped.jpg");
        Uri destinationUri = Uri.fromFile(file);
        UCrop.of(sourceImageUri, destinationUri)
                .withAspectRatio(16, 9) // 設定裁減比例
//                .withMaxResultSize(500, 500) // 設定結果尺寸不可超過指定寬高
                .start(activity, this, REQ_CROP_PICTURE);
    }

    private void handleCropResult(Intent intent) {
        Uri resultUri = UCrop.getOutput(intent);
        if (resultUri == null) {
            return;
        }
        Bitmap bitmap = null;
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                bitmap = BitmapFactory.decodeStream(
                        activity.getContentResolver().openInputStream(resultUri));
            } else {
                ImageDecoder.Source source =
                        ImageDecoder.createSource(activity.getContentResolver(), resultUri);
                bitmap = ImageDecoder.decodeBitmap(source);
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            photo = out.toByteArray();

        } catch (IOException e) {
            Log.e("TAG", e.toString());
        }
        if (bitmap != null) {
            bitmapList.add(bitmap);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(groupGet1Task!= null){
            groupGet1Task.cancel(true);
            groupGet1Task = null ;
        }
        if(InsertNoteTask!= null){
            InsertNoteTask.cancel(true);
            InsertNoteTask = null ;
        }
        if(getImageTask!= null){
            getImageTask.cancel(true);
            getImageTask = null ;
        }

    }
}
