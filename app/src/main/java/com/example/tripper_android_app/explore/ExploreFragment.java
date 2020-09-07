package com.example.tripper_android_app.explore;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
<<<<<<< HEAD
import androidx.fragment.app.FragmentActivity;
=======
>>>>>>> 會員註冊功能
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
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
<<<<<<< HEAD
=======
import com.example.tripper_android_app.task.CommonTask;
import com.example.tripper_android_app.task.ImageTask;
import com.example.tripper_android_app.util.Common;
>>>>>>> 會員註冊功能
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import java.util.ArrayList;

public class ExploreFragment extends Fragment {


    private Activity activity;
    private static final String TAG = "TAG_ExploreListFragment";
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rvExplore;
<<<<<<< HEAD
    private CommonTask exploreGetAllTask,exploreDeleteTask;
    private List<com.example.tripper_android_app.task.ImageTask> imageTasks;
=======
    private CommonTask exploreGetAllTask, exploreDeleteTask;
    private List<ImageTask> imageTasks;
>>>>>>> 會員註冊功能
    private List<Explore> explores;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
<<<<<<< HEAD
        activity= getActivity();
        imageTasks = new ArrayList<>();
=======
        activity = getActivity();
>>>>>>> 會員註冊功能
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_explore, container, false);


    }

    @Override
<<<<<<< HEAD
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottomBar);
        NavController navController = Navigation.findNavController(activity, R.id.nav_fragment);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
=======
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottomBar);
        NavController navController = Navigation.findNavController(activity, R.id.exploreFragment);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

>>>>>>> 會員註冊功能
        activity.setTitle(R.string.Explore);
        SearchView searchView = view.findViewById(R.id.searchView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        rvExplore = view.findViewById(R.id.rvExplore);

        rvExplore.setLayoutManager(new LinearLayoutManager(activity));
        explores = getExplores();
        showExplores(explores);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                showExplores(explores);
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
                    showExplores(explores);
                } else {
                    List<Explore> searchExplores = new ArrayList<>();
                    // 搜尋原始資料內有無包含關鍵字(不區別大小寫)
                    for (Explore explore : explores) {
                        if (explore.getTittleName().toUpperCase().contains(newText.toUpperCase())) {

                            searchExplores.add(explore);
                        }
                    }
                    showExplores(searchExplores);
                }
                return true;
            }
        });


    }

    private void showExplores(List<Explore> explores) {
        if (explores == null || explores.isEmpty()) {
            Common.showToast(activity, R.string.textNoSpotsFound);
        }
        ExploreAdapter exploreAdapter = (ExploreAdapter) rvExplore.getAdapter();
        if (exploreAdapter == null) {
            rvExplore.setAdapter(new ExploreAdapter(activity, explores));
        } else {
            exploreAdapter.setExplores(explores);
            //刷新頁面
            exploreAdapter.notifyDataSetChanged();
        }

    }

    private List<Explore> getExplores() {
        List<Explore> explores = null;
        if (Common.networkConnected(activity)) {
            //Servlet
            String url = Common.URL_SERVER + "ExploreServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAll");
            String jsonOut = jsonObject.toString();
            exploreGetAllTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = exploreGetAllTask.execute().get();
                Type listType = new TypeToken<List<Explore>>() {
                }.getType();
                explores = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(activity, R.string.textNoNetwork);
        }
        return explores;
    }

    private class ExploreAdapter extends RecyclerView.Adapter<ExploreAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        //這是serach view 的 List 為了留查詢後的資料
        private List<Explore> explores;
        private int imageSize;
<<<<<<< HEAD
        private Context context;
=======

>>>>>>> 會員註冊功能
        ExploreAdapter(Context context, List<Explore> explores) {
            layoutInflater = LayoutInflater.from(context);
            this.explores = explores;
            this.context = context;
            /* 螢幕寬度除以4當作將圖的尺寸 */
            imageSize = 500 ;
        }


        void setExplores(List<Explore> explores) {
            this.explores = explores;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
<<<<<<< HEAD
            View itemView = layoutInflater.inflate(R.layout.item_view_explore,parent,false);
            return  new MyViewHolder(itemView);
        }

=======
            View itemView = layoutInflater.inflate(R.layout.item_view_explore, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            final Explore explore = explores.get(position);
            String url = Common.URL_SERVER + "";
            int id = explore.getId();
            ImageTask imageTask = new ImageTask(url, id, imageSize, holder.ivBlogPic);
            imageTask.execute();
            imageTasks.add(imageTask);
            holder.tvUseName.setText(explore.getUserName());
            holder.tvBlogName.setText(explore.getBlogName());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //填寫網誌路徑

                }
            });


        }
>>>>>>> 會員註冊功能

        @Override
        public int getItemCount() {
            return explores == null ? 0 : explores.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView ivBlogPic;
            TextView tvUseName, tvBlogName;

            MyViewHolder(View itemView) {
                super(itemView);
                ivBlogPic = itemView.findViewById(R.id.ivBlogPic);
                tvBlogName = itemView.findViewById(R.id.tvBlogName);
                tvUseName = itemView.findViewById(R.id.tvUserName);
            }
        }
        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            final Explore explore = explores.get(position);
            String url = Common.URL_SERVER+ "ExploreServlet";
            int id = explore.getId();
            com.example.tripper_android_app.task.ImageTask imageTask = new com.example.tripper_android_app.task.ImageTask(url,id,imageSize,holder.ivBlogPic);
            imageTask.execute();
            holder.ivBlogPic.setScaleType(ImageView.ScaleType.FIT_XY);
            imageTasks.add(imageTask);
            holder.tvUseName.setText(explore.getUserName());
            holder.tvBlogName.setText(explore.getTittleName());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //填寫網誌路徑

                }
            });


        }

    }

    public void onStop() {
        super.onStop();
        if (exploreGetAllTask != null) {
            exploreGetAllTask.cancel(true);
            exploreGetAllTask = null;
        }

        if (imageTasks != null && imageTasks.size() > 0) {
            for (com.example.tripper_android_app.task.ImageTask imageTask : imageTasks) {
                imageTask.cancel(true);
            }
            imageTasks.clear();
        }

        if (exploreGetAllTask != null) {
            exploreDeleteTask.cancel(true);
            exploreDeleteTask = null;
        }
    }
}