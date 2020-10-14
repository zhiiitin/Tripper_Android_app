package com.example.tripper_android_app.blog;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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


import com.example.tripper_android_app.MainActivity;
import com.example.tripper_android_app.R;
import com.example.tripper_android_app.task.CommonTask;
import com.example.tripper_android_app.task.ImageTask;
import com.example.tripper_android_app.util.Common;
import com.example.tripper_android_app.util.DateUtil;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static androidx.navigation.Navigation.findNavController;

public class BlogTripListFragment extends Fragment {



    private MainActivity activity;
    private RecyclerView rvDays,rvLocation;
    private CommonTask getDaysCommonTask, deleteCommonTask;
    private List<Blog_Day> blog_days;
    private List<Blog_SpotInformation> blog_location;
    private static final String TAG = "TAG_TripList_Fragment";
    private SharedPreferences preferences;
    private SwipeRefreshLayout swipeRefreshLayout;






    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        activity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blog_trip_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("行程列表");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorForWhite));
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rvDays = view.findViewById(R.id.rvDays);
        rvDays.setLayoutManager(new LinearLayoutManager(activity));
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        blog_days = getDays();
        showDays(blog_days);






    }
    private List<Blog_Day> getDays() {
        List<Blog_Day> blog_days = null;
        preferences = activity.getSharedPreferences(Common.PREF_FILE, Context.MODE_PRIVATE);
        String id = preferences.getString("TripListId", null);
        if (Common.networkConnected(activity)) {
            //Servlet
            String url = Common.URL_SERVER + "BlogServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "findDateById");
            jsonObject.addProperty("id", id);
            String jsonOut = jsonObject.toString();
            getDaysCommonTask = new CommonTask(url, jsonOut);

            try {
                String jsonIn = getDaysCommonTask.execute().get();
                Type listType = new TypeToken<List<Blog_Day>>() {
                }.getType();

                blog_days = new Gson().fromJson(jsonIn, listType);

            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(activity, R.string.textNoNetwork);
        }
        return blog_days;
    }

////    @Override
////    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
////        super.onCreateOptionsMenu(menu, inflater);
////        inflater.inflate(R.menu.bottom_bar_menu, menu);
////    }
////
////    @Override
////    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
////
////        switch (item.getItemId()) {
////            case android.R.id.home:
////                Navigation.findNavController(rvDays).popBackStack();
////                break;
////            default:
////                break;
////        }
////        return super.onOptionsItemSelected(item);
////    }

    private void showDays(List<Blog_Day> blog_days) {
        if (blog_days == null || blog_days.isEmpty()) {
            Common.showToast(activity, R.string.textNoSpotsFound);

        }
        BlogTripListFragment.DaysAdapter daysAdapter = (DaysAdapter) rvDays.getAdapter();
        if (daysAdapter == null) {
            rvDays.setAdapter(new BlogTripListFragment.DaysAdapter(activity, blog_days));
        } else {
            daysAdapter.set_BlogDays(blog_days);
            //刷新頁面
            daysAdapter.notifyDataSetChanged();
        }
    }
    private List<Blog_SpotInformation> getBlog_location() throws ParseException {
        List<Blog_SpotInformation> blog_location = null;
        String date,date1 ;
        int number;
        preferences = activity.getSharedPreferences(Common.PREF_FILE, Context.MODE_PRIVATE);
        String id = preferences.getString("TripListId", "");
        date = preferences.getString("DATEE","");

        for(number= 0 ; number <blog_days.size();number++ ){


            date1 = DateUtil.date4day(date ,number);

            if (Common.networkConnected(activity)) {
                //Servlet
//
                String url = Common.URL_SERVER + "BlogServlet";
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("action", "getSpotName");
                jsonObject.addProperty("id", id);
                jsonObject.addProperty("dateD", date1);
                String jsonOut = jsonObject.toString();
                CommonTask commonTask = new CommonTask(url, jsonOut);
                Log.d("####1", "test");
                try {
                    String jsonIn = commonTask.execute().get();
                    Type listType = new TypeToken<List<Blog_SpotInformation>>() {
                    }.getType();

                    blog_location = new Gson().fromJson(jsonIn, listType);
                    System.out.println(blog_location);

                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            } else {
                Common.showToast(activity, R.string.textNoNetwork);


            }





        }
        return blog_location;

    }









    public class DaysAdapter extends RecyclerView.Adapter<DaysAdapter.MyDaysViewHolder> {
        private Context context;
        private List<Blog_Day> blog_days;
        ;


        DaysAdapter(Context context, List<Blog_Day> blog_days) {

            this.context = context;
            this.blog_days = blog_days;

        }

        @NonNull
        @Override
        public MyDaysViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.item_view_blog_triplist, parent, false);

            return new MyDaysViewHolder(itemView);

        }

        @Override
        public void onBindViewHolder(@NonNull MyDaysViewHolder holder, int position) {
            final Blog_Day  blog_day= blog_days.get(position);
            if(position ==0){
                holder.tvDays.setText("第一天");
                holder.tvDate.setText(blog_day.getDayTitle());
            }else if(position==1 ){
                holder.tvDays.setText("第二天");
                holder.tvDate.setText(blog_day.getDayTitle());
            }else if(position == 2){
                holder.tvDays.setText("第三天");
                holder.tvDate.setText(blog_day.getDayTitle());
            }else if(position == 3) {
                holder.tvDays.setText("第四天");
                holder.tvDate.setText(blog_day.getDayTitle());
            }else if(position == 4) {
                holder.tvDays.setText("第五天");
                holder.tvDate.setText(blog_day.getDayTitle());
            }else if(position == 5) {
                holder.tvDays.setText("第六天");
                holder.tvDate.setText(blog_day.getDayTitle());
            }

            try {
                blog_location = getBlog_location();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            holder.imDays.setImageResource(R.drawable.layout_box_line);
            rvLocation.setLayoutManager(new LinearLayoutManager(activity));
            holder.showLocations(blog_location);


        }




        @Override
            public int getItemCount() {
                return blog_days == null ? 0 : blog_days.size();
            }

            public void set_BlogDays (List<Blog_Day> blog_days){
                this.blog_days = blog_days;
            }


            public class MyDaysViewHolder extends RecyclerView.ViewHolder {
                private ImageButton imDays;
                private List<Blog_SpotInformation> blog_location;
                private TextView tvDays,tvDate;
//              private RecyclerView rvLocation;
//              private LocationAdapter locationAdapter;


                private void showLocations( List<Blog_SpotInformation> blog_location) {
                    if (blog_location == null || blog_location.isEmpty()) {
                        Common.showToast(activity, R.string.textNoSpotsFound);
                        return;
                    }

                    LocationAdapter locationAdapter = (LocationAdapter) rvLocation.getAdapter();
                    if (locationAdapter == null) {
                        rvLocation.setAdapter(new LocationAdapter(activity, blog_location));
                    } else {
                        locationAdapter.set_locations(blog_location);

                    }
                }


                public MyDaysViewHolder(@NonNull View itemView) {
                    super(itemView);

                    imDays = itemView.findViewById(R.id.imDays);
                    rvLocation = itemView.findViewById(R.id.rvloca0);
                    tvDays = itemView.findViewById(R.id.tvDays);
                    tvDate = itemView.findViewById(R.id.tvDate);


                }



            }
    }




    public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.MyLocationViewHolder> {
            private  Context context;

            private int lastPosition = -1;


            LocationAdapter(Context context, List<Blog_SpotInformation> blog_location) {
               this.context = context;
               BlogTripListFragment.this.blog_location = blog_location;
            }

            public void set_locations( List<Blog_SpotInformation> blog_location) {
                BlogTripListFragment.this.blog_location = blog_location;
            }

            @Override
            public int getItemCount() {

                return blog_location == null ? 0 : blog_location.size();

            }




            @NonNull
            @Override
            public MyLocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(context).inflate(R.layout.item_view_triplistlocation, parent, false);
                return new MyLocationViewHolder(itemView);
            }

            @Override
            public void onBindViewHolder(@NonNull MyLocationViewHolder holder, int position) {

                    final Blog_SpotInformation blog_spotInformation = blog_location.get(position);

                    holder.tvLocation.setText(blog_spotInformation.getSpotName());
                    holder.tvStayTime.setText("一小時");
                    holder.ivMark.setImageResource(R.drawable.mark);
                    holder.ivLine.setImageResource(R.drawable.line);



                lastPosition = getItemCount()-1 ;
                if (position == lastPosition) {
                    holder.tvStayTime.setVisibility(View.GONE);
                    holder.ivLine.setVisibility(View.GONE);
                }

            }



//

//
//            public void set_locations3(List<BlogD> blog_location3) {
//                this.blog_location3 = blog_location3;
//            }
//
//            public void set_locations4(List<BlogD> blog_location4) {
//                this.blog_location4 = blog_location4;
//            }
//
//            public void set_locations5(List<BlogD> blog_location5) {
//                this.blog_location5 = blog_location5;
//            }
//
//            public void set_locations6(List<BlogD> blog_location6) {
//                this.blog_location6 = blog_location6;
//            }


            public class MyLocationViewHolder extends RecyclerView.ViewHolder {
                private ImageView ivMark, ivLine;
                private TextView tvStayTime, tvLocation;
                private LinearLayoutManager layoutManager;


                public MyLocationViewHolder(View itemView) {
                    super(itemView);
                    ivLine = itemView.findViewById(R.id.ivLine);
                    ivMark = itemView.findViewById(R.id.ivMark);
                    tvLocation = itemView.findViewById(R.id.tvLocation);
                    tvStayTime = itemView.findViewById(R.id.tvStayTime);


                }
            }

        }

    @Override
    public void onStop() {
        super.onStop();
        if (getDaysCommonTask != null) {
            getDaysCommonTask .cancel(true);
            getDaysCommonTask  = null;
        }
        if (deleteCommonTask != null) {
            deleteCommonTask.cancel(true);
            deleteCommonTask = null;
        }
    }
        }
