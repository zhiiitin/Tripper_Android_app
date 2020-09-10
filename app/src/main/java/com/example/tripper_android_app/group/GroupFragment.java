package com.example.tripper_android_app.group;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.tripper_android_app.R;
import com.example.tripper_android_app.task.CommonTask;
import com.example.tripper_android_app.task.ImageTask;
import com.example.tripper_android_app.trip.Trip_M;
import com.example.tripper_android_app.util.Common;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class GroupFragment extends Fragment {
    private static final String TAG = "TAG_GroupListFragment";
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rvGroup;
    private Activity activity;
    private CommonTask groupGetAllTask;
    private ImageTask groupImageTask;
    private List<Trip_M> groupList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("行程");
        return inflater.inflate(R.layout.fragment_group, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottomBar);
        NavController navController = Navigation.findNavController(activity, R.id.groupFragment);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        SearchView searchView = view.findViewById(R.id.svGroup);
        swipeRefreshLayout = view.findViewById(R.id.srlGroup);
        rvGroup = view.findViewById(R.id.rvGroup);
        rvGroup.setLayoutManager(new LinearLayoutManager(activity));

        groupList = getGroups();
        showGroups(groupList);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                showGroups(groupList);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    showGroups(groupList);
                } else {
                    List<Trip_M> searchGroup = new ArrayList<>();
                    for (Trip_M group : groupList) {
                        if (group.getTripTitle().toUpperCase().contains(newText.toUpperCase())) {
                            searchGroup.add(group);
                        }
                    }
                    showGroups(searchGroup);
                }
                return true;
            }
        });

    }

    private List<Trip_M> getGroups() {
        List<Trip_M> groupList = null;
        if (Common.networkConnected(activity)) {
            String Url = Common.URL_SERVER + "Trip_M_Servlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAll");
            String jsonOut = jsonObject.toString();
            groupGetAllTask = new CommonTask(Url, jsonOut);
            try {
                String jsonIn = groupGetAllTask.execute().get();
                Type listtype = new TypeToken<List<Trip_M>>() {
                }.getType();
                groupList = new Gson().fromJson(jsonIn, listtype);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Common.showToast(activity, "No Internet");
        }
        return groupList;
    }

    private void showGroups(List<Trip_M> groupList) {
        if (groupList == null || groupList.isEmpty()) {
            Common.showToast(activity, "搜尋不到行程");
        }
        GroupAdapter groupAdapter = (GroupAdapter) rvGroup.getAdapter();
        if (groupAdapter == null) {
            rvGroup.setAdapter(new GroupAdapter(activity, groupList));
        } else {
            groupAdapter.setGroups(groupList);
            groupAdapter.notifyDataSetChanged();
        }
    }

    private class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        private List<Trip_M> groupList;
        private int imageSize;

        GroupAdapter(Context context, List<Trip_M> groupList) {
            layoutInflater = LayoutInflater.from(context);
            this.groupList = groupList;
            imageSize = getResources().getDisplayMetrics().widthPixels / 4;

        }

        void setGroups(List<Trip_M> groupList) {
            this.groupList = groupList;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView tvTitle, tvDate, tvCount;

            MyViewHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.ivGroup);
                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvDate = itemView.findViewById(R.id.tvDate);
                tvCount = itemView.findViewById(R.id.tvCount);
            }
        }

        @Override
        public int getItemCount() {
            return groupList == null ? 0 : groupList.size();
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.item_view_group, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {

            final Trip_M group = groupList.get(position);
            String Url = Common.URL_SERVER + "Trip_M_Servlet";
            int id = group.getMemberId();
            groupImageTask = new ImageTask(Url, id, imageSize, myViewHolder.imageView);
            groupImageTask.execute();

            myViewHolder.tvTitle.setText(group.getTripTitle());
            myViewHolder.tvDate.setText(group.getStartDate());
            myViewHolder.tvCount.setText("已參與人數：" + group.getpMax());


        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(groupGetAllTask != null){
            groupGetAllTask.cancel(true);
            groupGetAllTask = null ;
        }
        if(groupImageTask !=null){
            groupImageTask.cancel(true);
            groupImageTask = null ;
        }

    }
}