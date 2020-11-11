package com.example.tripper_android_app.group;

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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tripper_android_app.MainActivity;
import com.example.tripper_android_app.R;
import com.example.tripper_android_app.task.CommonTask;
import com.example.tripper_android_app.task.ImageTask;
import com.example.tripper_android_app.trip.Trip_M;
import com.example.tripper_android_app.util.Common;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class Group1Fragment extends Fragment {


        private static final String TAG = "TAG_GroupListFragment";
        private SwipeRefreshLayout swipeRefreshLayout;
        private RecyclerView rvGroup;
        private MainActivity activity;
        private CommonTask groupGetAllTask ,groupGetCountTask;
        private ImageTask groupImageTask;
        private List<Trip_M> groupList;
        private int mbrStatus = 0;


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            activity = (MainActivity) getActivity();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_group2, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);


            swipeRefreshLayout = view.findViewById(R.id.srlBlog_Home);
            rvGroup = view.findViewById(R.id.rvGroup);
            rvGroup.setLayoutManager(new LinearLayoutManager(activity));

            groupList = getGroups();
            showGroups(groupList);

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    swipeRefreshLayout.setRefreshing(true);
                    groupList = getGroups();
                    showGroups(groupList);
                    swipeRefreshLayout.setRefreshing(false);
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
                imageSize = getResources().getDisplayMetrics().widthPixels / 2;

            }

            void setGroups(List<Trip_M> groupList) {
                this.groupList = groupList;
            }

            class MyViewHolder extends RecyclerView.ViewHolder {
                ImageView imageView,ivFill,ivWillFill;
                TextView tvTitle, tvDate, tvCount;

                MyViewHolder(View itemView) {
                    super(itemView);
                    imageView = itemView.findViewById(R.id.groupTripPic);
                    tvTitle = itemView.findViewById(R.id.tvTitle_Blog);
                    tvDate = itemView.findViewById(R.id.tvDate);
                    tvCount = itemView.findViewById(R.id.tvCount);
                    ivFill = itemView.findViewById(R.id.ivFill);
                    ivWillFill = itemView.findViewById(R.id.ivWillFill);
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
            public void onBindViewHolder(@NonNull GroupAdapter.MyViewHolder myViewHolder, int position) {

                final Trip_M group = groupList.get(position);
                String Url = Common.URL_SERVER + "Trip_M_Servlet";
                String tripId = group.getTripId();
//            int id = group.getMemberId();
                groupImageTask = new ImageTask(Url, tripId, imageSize, myViewHolder.imageView);
                groupImageTask.execute();
//透過網路搜尋揪團參與人數
                String url = Common.URL_SERVER + "Trip_Group_Servlet";
                String trip_Id = group.getTripId();
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("action", "getGroupCount");
                jsonObject.addProperty("trip_Id" , trip_Id);
                String jsonOut = jsonObject.toString();
                groupGetCountTask = new CommonTask(url, jsonOut);
                int count = 0 ;
                try {
                    String result = groupGetCountTask.execute().get();
                    count = Integer.parseInt(result) + 1;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                myViewHolder.tvTitle.setText(group.getTripTitle());
                myViewHolder.tvDate.setText("出發日：" + group.getStartDate());
                myViewHolder.tvCount.setText("已參與人數："+ count + "/" + group.getpMax());

                if((group.getpMax() - count) == 2 || (group.getpMax() - count) == 1){
                    myViewHolder.ivWillFill.setVisibility(View.VISIBLE);
                }
//人數滿時顯示人數已滿
                if(count == group.getpMax()){
                    myViewHolder.tvCount.setVisibility(View.GONE);
                    myViewHolder.ivFill.setVisibility(View.VISIBLE);
                    //狀態等於1，表示成員已滿
                    mbrStatus = 1 ;
                }


//點擊頁面傳到行程頁面
                myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString("tripTitle", group.getTripTitle());
                        bundle.putString("tripId", group.getTripId());
                        bundle.putInt("memberId",group.getMemberId());
                        bundle.putString("startDate",group.getStartDate());
                        bundle.putString("startTime", group.getStartTime());
                        bundle.putInt("status",group.getStatus());
                        bundle.putInt("mbrStatus",mbrStatus);
                        Navigation.findNavController(v).navigate(R.id.action_groupFragment_to_groupTripPage, bundle);
                    }
                });

            }
        }


        @Override
        public void onStop() {
            super.onStop();
            if (groupGetAllTask != null) {
                groupGetAllTask.cancel(true);
                groupGetAllTask = null;
            }
            if (groupImageTask != null) {
                groupImageTask.cancel(true);
                groupImageTask = null;
            }

        }

    @Override
    public void onResume() {
        super.onResume();
        groupList = getGroups();
        showGroups(groupList);
    }
}
