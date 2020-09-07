package com.example.tripper_android_app.location;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.tripper_android_app.R;
import com.example.tripper_android_app.setting.member.Common;
import com.example.tripper_android_app.setting.member.CommonTask;
import com.example.tripper_android_app.task.ImageTask;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class LocationListFragment extends Fragment {
    private static final String TAG = "TAG_LocationListFragment";
    private Activity activity;
    private RecyclerView rvLocation;
    private ImageView ivLocImage;
    private List<Location> locations;
    private List<ImageTask> locImageTasks;
    private CommonTask locGetAllTask;
    private CommonTask locDeleteTask;
    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("景點列表");
        return inflater.inflate(R.layout.fragment_location_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SearchView searchView = view.findViewById(R.id.searchView);
        // 下拉更新元件
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        rvLocation = view.findViewById(R.id.rvLocation);
        rvLocation.setLayoutManager(new LinearLayoutManager(activity));

        // 圖片另外處理，此部份是未帶圖片
        locations = getLocations();
        showLocations(locations);

        //下拉監聽
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 有下拉動作就播放動畫
                swipeRefreshLayout.setRefreshing(true);
                // 顯示旅遊景點
                showLocations(locations);
                // 停止動畫
                swipeRefreshLayout.setRefreshing(false);

            }
        });

        // searchView監聽器
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.isEmpty()){
                    showLocations(locations);
                }else{
                    List<Location> searchLocs = new ArrayList<Location>();
                    // 搜尋內容(景點名稱及地址)不分大小寫
                    for(Location loc : locations){
                        if(loc.getName().toUpperCase().contains(newText.toUpperCase()) ||
                                loc.getAddress().toUpperCase().contains(newText.toUpperCase())){
                            searchLocs.add(loc);
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

        FloatingActionButton btAdd = view.findViewById(R.id.btAdd);
        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v)
                        .navigate(R.id.action_location_List_Fragment_to_location_Insert_Fragment);
            }
        });

    }

    @SuppressLint("LongLogTag")
    private List<Location> getLocations() {
        List<Location> locations = null;
        // 檢查網路狀態
        if(Common.netWorkConnected(activity)){
            String url = Common.URL_SERVER + "LocationServlet";
            JsonObject jsonObject   = new JsonObject();
            jsonObject.addProperty("action", "getAll");
            String jsonOut = jsonObject.toString();
            locGetAllTask = new CommonTask(url, jsonOut);
            try{
                // 考慮資料量問題，文字跟圖片分開，文字部份可以先抓ID
                String jsonIn = locGetAllTask.execute().get();
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
        }
        // 景點資訊不一定是全路，也有可能是過濾條件
        LocationAdapter locationAdapter = (LocationAdapter)rvLocation.getAdapter();
        // 如果locationAdapter不存在就建立新的
        if(locationAdapter == null){
            rvLocation.setAdapter(new LocationAdapter(activity, locations));
        }else{
            locationAdapter.setLocations(locations);
            locationAdapter.notifyDataSetChanged();
        }
    }


    private class LocationAdapter extends RecyclerView.Adapter<MyViewHolder>{
        private LayoutInflater layoutInflater;
        private List<Location> locations;
        private int imageSize;

        LocationAdapter(Context context, List<Location> locations){
            layoutInflater = LayoutInflater.from(context);
            this.locations = locations;
            // 設定圖片尺吋，螢幕寬度除以4
            imageSize = getResources().getDisplayMetrics().widthPixels / 4;
        }

        // 過濾後的LIST
        void setLocations(List<Location> locations){
            this.locations = locations;
        }


        @Override
        public int getItemCount() {
            return locations == null ? 0 : locations.size();
        }


        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.item_view_location, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
            final Location location = locations.get(position);
            String url = Common.URL_SERVER + "LocationServlet";
            String locId = location.getLogId();
            ImageTask imageTask = new ImageTask(url, locId, imageSize);
            imageTask.execute();
            locImageTasks.add(imageTask);
            myViewHolder.tvLocName.setText(location.getName());
            myViewHolder.tvAdress.setText(location.getAddress());
            myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("location", location);
//                    Navigation.findNavController(v)
//                            .navigate(R.id.);
                }
            });
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView ivLocImage;
        TextView tvLocName, tvAdress;

        public MyViewHolder(View itemView) {
            super(itemView);
            ivLocImage = itemView.findViewById(R.id.ivLocationPic);
            tvLocName = itemView.findViewById(R.id.tvLocName);
            tvAdress = itemView.findViewById(R.id.tvAdress);
        }
    }


}