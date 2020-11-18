package com.example.tripper_android_app.explore;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.tripper_android_app.MainActivity;
import com.example.tripper_android_app.R;
import com.example.tripper_android_app.blog.BlogTripListFragment;
import com.example.tripper_android_app.location.Location;
import com.example.tripper_android_app.location.LocationListFragment;
import com.example.tripper_android_app.task.CommonTask;
import com.example.tripper_android_app.task.ImageTask;
import com.example.tripper_android_app.util.Common;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class ExploreFragment2 extends Fragment {
    private static final String TAG = "TAG_LocationListFragment";
    private MainActivity activity;
    private RecyclerView rvAllSpot,rvHot;
    private List<Location> locations,locations2;
    private List<ImageTask> locImageTasks1,locImageTasks2;
    private CommonTask locGetAllTask1,locGetAllTask2;
    private CommonTask locDeleteTask1,locDeleteTask2;
    private TextView tvHot;
    private ImageView ivText;





    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_explore2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvAllSpot = view.findViewById(R.id.rvAllSpot);
        rvHot = view.findViewById(R.id.rvHot);
        rvHot.setLayoutManager( new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.HORIZONTAL));
        rvAllSpot.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.HORIZONTAL));
        tvHot = view.findViewById(R.id.tvHot);
        rvAllSpot.setOnFlingListener(null);
        rvHot.setOnFlingListener(null);
        ivText = view.findViewById(R.id.ivText);
        // 圖片另外處理，此部份是未帶圖片
        locations = getLocations();
        showLocations(locations);
        locations2 = getHotLocations();
        showHotLocations(locations2);
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(rvAllSpot);
        PagerSnapHelper pagerSnapHelper1 = new PagerSnapHelper();
        pagerSnapHelper1.attachToRecyclerView(rvHot);

        SearchView searchView =  view.findViewById(R.id.svGroup);
        // searchView監聽器
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.isEmpty()){
                    showLocations(locations);
                    showHotLocations(locations2);
                    rvHot.setVisibility(View.VISIBLE);
                    tvHot.setVisibility(View.VISIBLE);
                    ivText.setVisibility(View.VISIBLE);

                }else{
                    List<Location> searchLocs = new ArrayList<Location>();
                    // 搜尋內容(景點名稱及地址)不分大小寫
                    for(Location loc : locations){
                        if(loc.getName().toUpperCase().contains(newText.toUpperCase()) ||
                                loc.getAddress().toUpperCase().contains(newText.toUpperCase())){
                            searchLocs.add(loc);
                            rvHot.setVisibility(View.GONE);
                            tvHot.setVisibility(View.GONE);
                            ivText.setVisibility(View.GONE);

                        }
                    }
                    showLocations(searchLocs);

                }
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

        });



    }

    @SuppressLint("LongLogTag")
    private List<Location> getHotLocations() {
        List<Location> locations = null;
        // 檢查網路狀態
        if(Common.networkConnected(activity)){
            String url = Common.URL_SERVER + "ExploreServlet";
            JsonObject jsonObject   = new JsonObject();
            jsonObject.addProperty("action", "getHotSpot");
            String jsonOut = jsonObject.toString();
            locGetAllTask1 = new CommonTask(url, jsonOut);
            try{
                // 考慮資料量問題，文字跟圖片分開，文字部份可以先抓ID
                String jsonIn = locGetAllTask1.execute().get();
                Type listytpe = new TypeToken<List<Location>>(){}.getType();
                locations = new Gson().fromJson(jsonIn, listytpe);
            }catch (Exception e){
                Log.d(TAG, e.toString());
            }
        } else{
            Common.showToast(activity, "請確認網路連線是否正常");
        }
        return locations;
    }


    private void showHotLocations(List<Location> locations) {
        if(locations == null || locations.isEmpty()){
            Common.showToast(activity, "目前尚未有景點資料");
            return;
        }
        // 景點資訊不一定是全路，也有可能是過濾條件
        LocationAdapter2 locationAdapter = (LocationAdapter2)rvHot.getAdapter();
        // 如果locationAdapter不存在就建立新的
        if(locationAdapter == null){
            rvHot.setAdapter(new LocationAdapter2(activity, locations));
        }else{
            locationAdapter.setLocations(locations);
            locationAdapter.notifyDataSetChanged();
        }

    }

    @SuppressLint("LongLogTag")
    private List<Location> getLocations() {
        List<Location> locations = null;
        // 檢查網路狀態
        if(Common.networkConnected(activity)){
            String url = Common.URL_SERVER + "LocationServlet";
            JsonObject jsonObject   = new JsonObject();
            jsonObject.addProperty("action", "getAll");
            String jsonOut = jsonObject.toString();
            locGetAllTask2 = new CommonTask(url, jsonOut);
            try{
                // 考慮資料量問題，文字跟圖片分開，文字部份可以先抓ID
                String jsonIn = locGetAllTask2.execute().get();
                Type listytpe = new TypeToken<List<Location>>(){}.getType();
                locations = new Gson().fromJson(jsonIn, listytpe);
            }catch (Exception e){
                Log.d(TAG, e.toString());
            }
        } else{
            Common.showToast(activity, "請確認網路連線是否正常");
        }
        return locations;
    }


    private void showLocations(List<Location> locations) {
        if(locations == null || locations.isEmpty()){
            Common.showToast(activity, "目前尚未有景點資料");
            return;
        }
        // 景點資訊不一定是全路，也有可能是過濾條件
        LocationAdapter1 locationAdapter1 = (LocationAdapter1)rvAllSpot.getAdapter();
        // 如果locationAdapter不存在就建立新的
        if(locationAdapter1 == null){
            rvAllSpot.setAdapter(new LocationAdapter1(activity, locations));
        }else{
            locationAdapter1.setLocations(locations);
            locationAdapter1.notifyDataSetChanged();
        }

    }


    private class LocationAdapter1 extends RecyclerView.Adapter<MyLocationViewHolder> {
        private LayoutInflater layoutInflater;
        private List<Location> locations;
        private int imageSize;



        public LocationAdapter1(Context context, List<Location> locations) {
            layoutInflater = LayoutInflater.from(context);
            this.locations = locations;
            // 設定圖片尺吋，螢幕寬度除以4
            imageSize = getResources().getDisplayMetrics().widthPixels / 4;
        }

        // 過濾後的LIST
        void setLocations(List<Location> locations){
            this.locations = locations;
        }


        @NonNull
        @Override
        public MyLocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.item_view_all_location,parent,false);
            return new MyLocationViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyLocationViewHolder holder, int position) {
            final Location location = locations.get(position);
            String url = Common.URL_SERVER + "LocationServlet";
            String locId = location.getLocId();
            ImageTask imageTask = new ImageTask(url, locId, imageSize, holder.ivLocImage);
            locImageTasks1 = new ArrayList<ImageTask>();
            imageTask.execute();
            locImageTasks1.add(imageTask);
            if(position != 0 ){
                if(location.getLocType().equals("1")){
                    holder.ivHotPic.setVisibility(View.VISIBLE);
                }
            }
            holder.tvAddress.setText(location.getAddress());
            holder.tvName.setText(location.getName());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("location", location);
                    Navigation.findNavController(rvAllSpot).navigate(R.id.action_exploreFragment_to_exploreLocationDetailFragment,bundle);
                }
            });
        }

        @Override
        public int getItemCount() {
            return locations == null ? 0 : locations.size();
        }
    }

    public static class MyLocationViewHolder extends RecyclerView.ViewHolder {
        ImageView ivLocImage,ivHotPic;
        TextView tvLocName, tvAddress,tvName;
        public MyLocationViewHolder(View itemView) {
            super(itemView);
            ivLocImage = itemView.findViewById(R.id.ivLocationPic);
            tvAddress = (TextView) itemView.findViewById(R.id.tvAddress1);
            tvLocName= (TextView) itemView.findViewById(R.id.tvLocationName);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            ivHotPic = itemView.findViewById(R.id.ivHotPic);
        }
    }

    private class LocationAdapter2 extends RecyclerView.Adapter<MyHotSpotViewHolder> {
        private LayoutInflater layoutInflater;
        private List<Location> locations2;
        private int imageSize;


        public LocationAdapter2(Context context, List<Location> locations2) {
            layoutInflater = LayoutInflater.from(context);
            this.locations2 = locations2;
            // 設定圖片尺吋，螢幕寬度除以4
            imageSize = getResources().getDisplayMetrics().widthPixels / 4;
        }
        // 過濾後的LIST
        void setLocations(List<Location> locations2){
            this.locations2 = locations2;
        }



        @NonNull
        @Override
        public MyHotSpotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.item_view_all_location,parent,false);
            return new MyHotSpotViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyHotSpotViewHolder holder, int position) {
            final Location location = locations2.get(position);
            String url = Common.URL_SERVER + "LocationServlet";
            String locId = location.getLocId();
            ImageTask imageTask = new ImageTask(url, locId, imageSize, holder.ivLocImage);
            locImageTasks2 = new ArrayList<ImageTask>();
            imageTask.execute();
            locImageTasks2.add(imageTask);
            holder.tvAddress.setText(location.getAddress());
            holder.tvName.setText(location.getName());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("location", location);
                    Navigation.findNavController(rvAllSpot).navigate(R.id.action_exploreFragment_to_exploreLocationDetailFragment,bundle);
                }
            });

        }

        @Override
        public int getItemCount() {
            return locations2 == null ? 0 : locations2.size();
        }
    }

    private class MyHotSpotViewHolder extends RecyclerView.ViewHolder {
        ImageView ivLocImage;
        TextView tvLocName, tvAddress,tvName;
        public MyHotSpotViewHolder(@NonNull View itemView) {
            super(itemView);
            ivLocImage = itemView.findViewById(R.id.ivLocationPic);
            tvAddress = (TextView) itemView.findViewById(R.id.tvAddress1);
            tvLocName= (TextView) itemView.findViewById(R.id.tvLocationName);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        if(locGetAllTask1 != null){
            locGetAllTask1.cancel(true);
            locGetAllTask1 = null;

        }

        if(locImageTasks1 != null && locImageTasks1.size() > 0){
            for(ImageTask imageTask : locImageTasks1){
                imageTask.cancel(true);
            }
            locImageTasks1.clear();
        }

        if(locDeleteTask1 != null){
            locDeleteTask1.cancel(true);
            locDeleteTask1 = null;
        }
        if(locGetAllTask2 != null){
            locGetAllTask2.cancel(true);
            locGetAllTask2 = null;

        }

        if(locImageTasks2 != null && locImageTasks2.size() > 0){
            for(ImageTask imageTask : locImageTasks2){
                imageTask.cancel(true);
            }
            locImageTasks2.clear();
        }

        if(locDeleteTask2 != null){
            locDeleteTask2.cancel(true);
            locDeleteTask2 = null;
        }
    }
}

