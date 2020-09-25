package com.example.tripper_android_app.trip;

import android.content.Context;
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
import android.widget.TextView;

import com.example.tripper_android_app.MainActivity;
import com.example.tripper_android_app.R;
import com.example.tripper_android_app.task.CommonTask;
import com.example.tripper_android_app.task.ImageTask;
import com.example.tripper_android_app.util.Common;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static androidx.navigation.Navigation.findNavController;

/**
 * 行程主頁面
 * @author cooperhsieh
 * @version 2020.09.09
 */


public class Trip_HomePage extends Fragment {
    private MainActivity activity;
    private RecyclerView rvTripMainList;
    private List<Trip_M> tripMs;
    private CommonTask tripGetAllTask;
    private List<ImageTask> imageTasks;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
        setHasOptionsMenu(true);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorForWhite));
        rvTripMainList = view.findViewById(R.id.rvTripMainList);
        rvTripMainList.setLayoutManager(new LinearLayoutManager(activity));
        activity.setSupportActionBar(toolbar);


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
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.app_bar_button, menu);
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trip__home_page, container, false);

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

}



