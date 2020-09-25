package com.example.tripper_android_app.trip;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
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
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tripper_android_app.MainActivity;
import com.example.tripper_android_app.R;
import com.example.tripper_android_app.setting.member.Member;
import com.example.tripper_android_app.task.CommonTask;
import com.example.tripper_android_app.task.ImageTask;
import com.example.tripper_android_app.util.Common;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.content.Context.MODE_PRIVATE;
import static androidx.navigation.Navigation.findNavController;

/**
 * 行程主頁面
 *
 * @author cooperhsieh
 * @version 2020.09.09
 */


public class Trip_HomePage extends Fragment {
    private final static String TAG = "TAG_TripHomePage";
    private MainActivity activity;
    private RecyclerView rvTripMainList;
    private List<Trip_M> tripMs;
    private CommonTask tripGetAllTask;
    private List<ImageTask> imageTasks;
    private TextView tvUserName;
    private ImageView ivUserPic;
    private RecyclerView rvTripHome;
    private ImageTask tripImageTask;
    private List<Trip_M> tripList;
    //show member
    private Member member;
    private FirebaseAuth auth;
    private FirebaseUser mUser;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
        setHasOptionsMenu(true);
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_trip__home_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorForWhite));
        rvTripMainList = view.findViewById(R.id.rvTripMainList);
        rvTripMainList.setLayoutManager(new LinearLayoutManager(activity));
        activity.setSupportActionBar(toolbar);

        mUser = auth.getCurrentUser();
        ivUserPic = view.findViewById(R.id.ivUserPic);
        tvUserName = view.findViewById(R.id.tvUserName);


        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottomBar);
        NavController navController = Navigation.findNavController(activity, R.id.trip_HomePage);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);


        BottomNavigationView bottomNavigationViewTop = view.findViewById(R.id.navigation);
        NavController navControllerTop = Navigation.findNavController(activity, R.id.trip_HomePage);
        NavigationUI.setupWithNavController(bottomNavigationViewTop, navControllerTop);
        Menu itemMenu = bottomNavigationViewTop.getMenu();
        itemMenu.getItem(0).setChecked(true);

        tripMs = new ArrayList<>();
        tripMs = getTripMs();
        showTripList(tripMs);

    }

    private void showTripList(List<Trip_M> tripMs) {
        if(tripMs == null || tripMs.isEmpty()){
            Common.showToast(activity, "尚未建立任何行程");
            return;
        }
        TripListAdapter tripListAdapter = (TripListAdapter) rvTripMainList.getAdapter();
        if(tripListAdapter == null){
            rvTripMainList.setAdapter(new TripListAdapter(activity, tripMs));
        }else{
            tripListAdapter.setTripMs(tripMs);
            tripListAdapter.notifyDataSetChanged();
        }
    }

    private List<Trip_M> getTripMs() {
        List<Trip_M> tripMs = new ArrayList<>();
        if(Common.networkConnected(activity)){
            String url = Common.URL_SERVER + "TripServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAll");
            // TODO 暫時用hardcode, 正常需要讀prefile
            jsonObject.addProperty("memberId", 1);
            String jsonOut = jsonObject.toString();
            tripGetAllTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = tripGetAllTask.execute().get();
                Type type = new TypeToken<List<Trip_M>>(){}.getType();
                tripMs = new Gson().fromJson(jsonIn, type);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }else {
            Common.showToast(activity, "請確認網路連線");
        }
        return tripMs;
        //顯示會員資料
       // showMember();

        //行程recyclerview
//        rvTripHome = view.findViewById(R.id.rvTripHomePage);
//        rvTripHome.setLayoutManager(new LinearLayoutManager(activity));
//        tripList = getTrip();
//        showTrip(tripList);


    }


    private List<Trip_M> getTrip() {
        List<Trip_M> tripList = null;
        if (Common.networkConnected(activity)) {
            String Url = Common.URL_SERVER + "Trip_M_Servlet"; /////////
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAll");
            String jsonOut = jsonObject.toString();
            tripGetAllTask = new CommonTask(Url, jsonOut);
            try {
                String jsonIn = tripGetAllTask.execute().get();
                Type listtype = new TypeToken<List<Trip_M>>() {  /////
                }.getType();
                tripList = new Gson().fromJson(jsonIn, listtype);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Common.showToast(activity, "No Internet");
        }
        return tripList;
    }

    private void showTrip(List<Trip_M> tripMList) {
        if (tripMList == null || tripMList.isEmpty()) {
            Common.showToast(activity, "搜尋不到行程");
        }
        TripAdapter tripAdapter = (TripAdapter) rvTripHome.getAdapter();
        if (tripAdapter == null) {
            rvTripHome.setAdapter(new TripAdapter(activity, tripList));
        } else {
            tripAdapter.setTrips(tripList);
            tripAdapter.notifyDataSetChanged();
        }
    }

    private class TripAdapter extends RecyclerView.Adapter<TripAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        private List<Trip_M> tripList;
        private int imageSize;

        TripAdapter(Context context, List<Trip_M> tripList) {
            layoutInflater = LayoutInflater.from(context);
            this.tripList = tripList;
            imageSize = getResources().getDisplayMetrics().widthPixels / 2;
        }

        void setTrips(List<Trip_M> tripList) {
            this.tripList = tripList;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView tvTripName;

            MyViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTripName = itemView.findViewById(R.id.textLocName);
                imageView = itemView.findViewById(R.id.ivLocPic2);
            }
        }

        @Override
        public int getItemCount() {
            return tripList == null ? 0 : tripList.size();
        }

        @NonNull
        @Override
        public TripAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.item_view_trip_home, parent, false);
            return new TripAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull TripAdapter.MyViewHolder holder, int position) {
            final Trip_M trips = tripList.get(position);
            String Url = Common.URL_SERVER + "Trip_M_Servlet";
            int id = trips.getMemberId();

            tripImageTask = new ImageTask(Url, id, imageSize, holder.imageView);
            tripImageTask.execute();

            holder.tvTripName.setText(trips.getTripTitle());
            String tripId = trips.getTripId();

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("tripDetail", trips);
                    Navigation.findNavController(v).navigate(R.id.tripHasSavedPage, bundle);
                }
            });

        }
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        SharedPreferences pref = activity.getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
        boolean login = pref.getBoolean("login", false);
        if (login) {
            inflater.inflate(R.menu.app_bar_button, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.btCreateTrip) {
            findNavController(this.getView()).navigate(R.id.action_trip_HomePage_to_create_Trip_Fragment);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //show 使用者資訊
    private void showMember() {
        SharedPreferences pref = activity.getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
        boolean login = pref.getBoolean("login", false);
        if (login) {
            if (Common.networkConnected(activity)) {
                String Url = Common.URL_SERVER + "MemberServlet";
                String account = pref.getString("account", "");
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("action", "getProfile");
                jsonObject.addProperty("account", account);
                try {
                    String jsonIn = new CommonTask(Url, jsonObject.toString()).execute().get();
                    Type listtype = new TypeToken<Member>() {
                    }.getType();
                    member = new Gson().fromJson(jsonIn, listtype);

                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
                String nickname = member.getNickName();
                tvUserName.setText(nickname);

            } else {
                Common.showToast(activity, "no network connection found");
            }
            showMemberPic();

        }
    }

    private class TripListAdapter extends RecyclerView.Adapter<TripListAdapter.TripListViewHolder> {
        private LayoutInflater layoutInflater;
        private List<Trip_M> tripMs;
        private int imageSize;

        TripListAdapter(Context context, List<Trip_M> tripMs){
            layoutInflater = LayoutInflater.from(context);
            this.tripMs = tripMs;
            imageSize = getResources().getDisplayMetrics().widthPixels / 4;
        }

        void setTripMs(List<Trip_M> tripMs){
            this.tripMs = tripMs;
        }

        @Override
        public int getItemCount() {
            return tripMs == null ? 0 : tripMs.size();
        }

        @NonNull
        @Override
        public TripListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.item_view_blog_home,parent,false);
            return new TripListViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull TripListViewHolder tripListViewHolder, int position) {
            Trip_M tripM = tripMs.get(position);
            String url = Common.URL_SERVER + "TripServlet";
            String tripId = tripM.getTripId();
            ImageTask imageTask = new ImageTask(url, tripId, imageSize, tripListViewHolder.ivBlog);
            imageTasks = new ArrayList<>();
            imageTask.execute();
            imageTasks.add(imageTask);
            Log.d("tripTitle", tripM.getTripTitle());
            tripListViewHolder.tvTitle.setText(tripM.getTripTitle());

        }

        private class TripListViewHolder extends RecyclerView.ViewHolder {
            ImageView ivBlog;
            TextView tvTitle;
            public TripListViewHolder(View itemView) {
                super(itemView);
                ivBlog = itemView.findViewById(R.id.ivBlog);
                tvTitle = itemView.findViewById(R.id.tvTitle_Blog);
            }
        }
    }
    //show UserPic
    private void showMemberPic() {
        if (mUser != null) {
            String Url = Common.URL_SERVER + "MemberServlet";
            int id = member.getId();
            int imageSize = getResources().getDisplayMetrics().widthPixels / 3;
            Bitmap bitmap = null;
            try {
                bitmap = new ImageTask(Url, id, imageSize).execute().get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //若此帳號之資料庫有照片，便使用資料庫的照
            if (bitmap != null) {
                ivUserPic.setImageBitmap(bitmap);
            } else {
                //否則連接到第三方大頭照
                String fbPhotoURL = mUser.getPhotoUrl().toString();
                Glide.with(this).load(fbPhotoURL).into(ivUserPic);
            }

        } else {

            String Url = Common.URL_SERVER + "MemberServlet";
            int id = member.getId();
            int imageSize = getResources().getDisplayMetrics().widthPixels / 3;
            Bitmap bitmap = null;
            try {
                bitmap = new ImageTask(Url, id, imageSize).execute().get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (bitmap != null) {
                ivUserPic.setImageBitmap(bitmap);
            } else {
                ivUserPic.setImageResource(R.drawable.ic_nopicture);
            }
        }
    }

}



