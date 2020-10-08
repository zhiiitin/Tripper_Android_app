package com.example.tripper_android_app.blog;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuView;
import androidx.appcompat.widget.Toolbar;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.tripper_android_app.MainActivity;
import com.example.tripper_android_app.R;
import com.example.tripper_android_app.task.CommonTask;
import com.example.tripper_android_app.task.ImageTask;
import com.example.tripper_android_app.util.Common;
import com.example.tripper_android_app.util.DateUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class BlogTripListFragment extends Fragment {
    private MainActivity activity;
    private RecyclerView rvDays,rvLocation;
    private CommonTask getDaysCommonTask, deleteCommonTask;
    private List<Blog_Day> blog_days;
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
        int id = preferences.getInt("TripListId", 0);
        String date = preferences.getString("DATE", "");

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

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.bottom_bar_menu, menu);
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








    public class DaysAdapter extends RecyclerView.Adapter<DaysAdapter.MyDaysViewHolder> {
        private Context context;
        private List<Blog_Day> blog_days;


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
            holder.tvDays.setText(blog_day.getDayTitle());
            holder.imDays.setImageResource(R.drawable.layout_box_line);



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
                private List<Blog_SpotInformation> blog_location,blog_location2;
                private TextView tvDays;
//              private RecyclerView rvLocation;
//              private LocationAdapter locationAdapter;





                public MyDaysViewHolder(@NonNull View itemView) {
                    super(itemView);

                    imDays = itemView.findViewById(R.id.imDays);
                    rvLocation = itemView.findViewById(R.id.rvloca0);
                    rvLocation.setLayoutManager(new LinearLayoutManager(activity));
                    try {
                        blog_location = getBlog_location();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    showLocations(blog_location);
                    tvDays = itemView.findViewById(R.id.tvDays);


                }
                private void showLocations( List<Blog_SpotInformation> blog_location) {
                    if (blog_location == null || blog_location.isEmpty()) {
                        Common.showToast(activity, R.string.textNoSpotsFound);
                        return;
                    }

                    LocationAdapter locationAdapter = (LocationAdapter) rvLocation.getAdapter();
                    if (locationAdapter == null) {
                        rvLocation.setAdapter(new LocationAdapter(activity, blog_location));
                    } else {
                        locationAdapter.set_locations1(blog_location);

                    }
                }


                private List<Blog_SpotInformation> getBlog_location() throws ParseException {
                    List<Blog_SpotInformation> blog_location = null;

                    preferences = activity.getSharedPreferences(Common.PREF_FILE, Context.MODE_PRIVATE);
                    int id = preferences.getInt("TripListId", 0);
                    String date = preferences.getString("DATE", "");

                        String date2 = DateUtil.date4day(date, 0);
                        if (Common.networkConnected(activity)) {
                            //Servlet
                            String url = Common.URL_SERVER + "BlogServlet";
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("action", "getSpotName");
                            jsonObject.addProperty("id", id);
                            jsonObject.addProperty("dateD", date2);
                            String jsonOut = jsonObject.toString();
                            getDaysCommonTask = new CommonTask(url, jsonOut);
                            Log.d("####1" , "test");
                            try {
                                String jsonIn = getDaysCommonTask.execute().get();
                                Type listType = new TypeToken<List<Blog_SpotInformation>>() {
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
            }
        }

        public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.MyLocationViewHolder> {
            private Context context;
            private  List<Blog_SpotInformation> blog_location,blog_location2;
            private int lastPosition = -1;


            LocationAdapter(Context context, List<Blog_SpotInformation> blog_location) {
                this.context = context;
                this.blog_location = blog_location;
            }

            public void set_locations1( List<Blog_SpotInformation> blog_location) {
                this.blog_location = blog_location;
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

//    @Override
//    public void onStop() {
//        super.onStop();
//        if (getDaysCommonTask != null) {
//            getDaysCommonTask .cancel(true);
//            getDaysCommonTask  = null;
//        }
//        if (deleteCommonTask != null) {
//            deleteCommonTask.cancel(true);
//            deleteCommonTask = null;
//        }
//    }
    }
