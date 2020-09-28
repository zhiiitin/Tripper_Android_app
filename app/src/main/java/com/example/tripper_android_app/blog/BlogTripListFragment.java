package com.example.tripper_android_app.blog;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.tripper_android_app.MainActivity;
import com.example.tripper_android_app.R;
import com.example.tripper_android_app.task.CommonTask;
import com.example.tripper_android_app.util.Common;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class BlogTripListFragment extends Fragment {
    private MainActivity activity;
    private RecyclerView rvDays, rvLoaction;
    private CommonTask getDaysCommonTask, deleteCommonTask;
    private List<BlogD> blog_days, blog_location;
    private static final String TAG = "TAG_TripList_Fragment";
    private SharedPreferences preferences;

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
        rvLoaction = view.findViewById(R.id.rvloca0);
        rvDays.setLayoutManager(new LinearLayoutManager(activity));
        blog_days = getDays();
        showDays(blog_days);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.blog_edit_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                Navigation.findNavController(rvDays).popBackStack();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDays(List<BlogD> blog_days) {
        if (blog_days == null || blog_days.isEmpty()) {
            Common.showToast(activity, R.string.textNoSpotsFound);

        }
        BlogTripListFragment.DaysAdapter daysAdapter = (DaysAdapter) rvDays.getAdapter();
        if (daysAdapter == null) {
            rvDays.setAdapter(new BlogTripListFragment.DaysAdapter(activity, blog_days));
        } else {
            daysAdapter.setBlogs(blog_days);
            //刷新頁面
            daysAdapter.notifyDataSetChanged();
        }
    }




    private List<BlogD> getDays() {
        List<BlogD> blog_days = null;
        preferences = activity.getSharedPreferences(Common.PREF_FILE, Context.MODE_PRIVATE);
        int id = preferences.getInt("TripListId", 0);


        if (Common.networkConnected(activity)) {
            //Servlet
            String url = Common.URL_SERVER + "BlogServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "findById");
            jsonObject.addProperty("id", id);
            String jsonOut = jsonObject.toString();
            getDaysCommonTask = new CommonTask(url, jsonOut);

            try {
                String jsonIn = getDaysCommonTask.execute().get();
                Type listType = new TypeToken<List<BlogD>>() {
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


    public class DaysAdapter extends RecyclerView.Adapter<DaysAdapter.MyViewHolder> {
        private Context context;
        private List<BlogD> blog_days,blog_location;
        private LayoutInflater layoutInflater;

        private void showLocation(List<BlogD> blog_location) {
            if (blog_location == null || blog_location.isEmpty()) {
                Common.showToast(activity, R.string.textNoSpotsFound);

            }
            LocationAdapter locationAdapter = new LocationAdapter(activity, blog_location);
            rvLoaction.getAdapter();
            if (locationAdapter == null) {
                rvLoaction.setAdapter(new BlogTripListFragment.LocationAdapter(activity, blog_location));
            } else {
                locationAdapter.setBlog_location(blog_location);
                //刷新頁面
                locationAdapter.notifyDataSetChanged();
            }
        }
        private List<BlogD> getBlog_location() {
            List<BlogD> blog_location = null;
            preferences = activity.getSharedPreferences(Common.PREF_FILE, Context.MODE_PRIVATE);
            int id = preferences.getInt("TripListId", 0);


            if (Common.networkConnected(activity)) {
                //Servlet
                String url = Common.URL_SERVER + "BlogServlet";
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("action", "findById");
                jsonObject.addProperty("id", id);
                String jsonOut = jsonObject.toString();
                getDaysCommonTask = new CommonTask(url, jsonOut);

                try {
                    String jsonIn = getDaysCommonTask.execute().get();
                    Type listType = new TypeToken<List<BlogD>>() {
                    }.getType();

                    blog_location = new Gson().fromJson(jsonIn, listType);

                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            } else {
                Common.showToast(activity, R.string.textNoNetwork);
            }
            return blog_location;
        }

        DaysAdapter(Context context, List<BlogD> blog_days) {
            layoutInflater = LayoutInflater.from(context);
            this.context = context;
            this.blog_days = blog_days;

        }



        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.item_view_blog_triplist, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            final BlogD blogD = blog_days.get(position);
            if(position != 0) {
                BlogD b = blog_days.get(position - 1);
                if(blogD.getS_Date().equals(b.getS_Date())) {
                   holder.itemView.setVisibility(View.GONE);
                }
            }
            holder.tvDays.setText(blogD.getS_Date());




        }

        @Override
        public int getItemCount() {


            return blog_days == null ? 0 : blog_days.size();
        }

        public void setBlogs(List<BlogD> blog_days) {
            this.blog_days = blog_days;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            private TextView tvDays;
            private RecyclerView recyclerView;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                tvDays = itemView.findViewById(R.id.tvDays);

                rvLoaction = itemView.findViewById(R.id.rvloca0);
                rvLoaction.setLayoutManager(new LinearLayoutManager(activity));
                blog_location = getBlog_location();
                showLocation(blog_location);


            }
        }
    }

    public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.Holder>{
        private  Context context;
        private  List<BlogD> blog_location;
        private  LayoutInflater layoutInflater;
        LocationAdapter(Context context , List<BlogD> blog_location){
            this.blog_location = blog_location;
            this.context = context;
            layoutInflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.item_view_triplistlocation,parent,false);
            return new Holder(itemView);
        }
        public void setBlog_location(List<BlogD> blog_location) {
            this.blog_location = blog_location;
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            BlogD blogD = blog_location.get(position);




        }



        @Override
        public int getItemCount() {
            return blog_location == null ? 0 : blog_location.size();
        }

        public class Holder extends  RecyclerView.ViewHolder {
            private TextView tvLocation,tvStayTime;
            private ImageView ivMark,ivLine;

            public Holder(@NonNull View itemView) {
                super(itemView);

                tvLocation = itemView.findViewById(R.id.tvLocation);
                tvStayTime = itemView.findViewById(R.id.tvStayTime);
                ivLine = itemView.findViewById(R.id.ivLine);
                ivMark = itemView.findViewById(R.id.ivMark);
            }
        }
    }
}