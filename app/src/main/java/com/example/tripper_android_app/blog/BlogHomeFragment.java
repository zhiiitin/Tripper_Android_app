package com.example.tripper_android_app.blog;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.tripper_android_app.MainActivity;
import com.example.tripper_android_app.R;
import com.example.tripper_android_app.group.GroupFragment;
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


public class BlogHomeFragment extends Fragment {

        private static final String TAG = "TAG_BlogHomeFragment";
        private SwipeRefreshLayout swipeRefreshLayout;
        private RecyclerView rvBlog;
        private MainActivity activity;
        private CommonTask blogGetAllTask;
        private ImageTask blogImageTask;
        private List<Trip_M> blogList;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            activity = (MainActivity)getActivity();

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_blog_home, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            
            swipeRefreshLayout = view.findViewById(R.id.srlBlog_Home);
            rvBlog = view.findViewById(R.id.rvBlog_Home);
            rvBlog.setLayoutManager(new LinearLayoutManager(activity));

            blogList = getBlogs();
            showBlogs(blogList);

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    swipeRefreshLayout.setRefreshing(true);
                    showBlogs(blogList);
                    swipeRefreshLayout.setRefreshing(false);
                }
            });


        }

        private List<Trip_M> getBlogs() {
            List<Trip_M> blogList = null;
            if (Common.networkConnected(activity)) {
                String Url = Common.URL_SERVER + "Trip_M_Servlet"; /////////
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("action", "getAll");
                String jsonOut = jsonObject.toString();
                blogGetAllTask = new CommonTask(Url, jsonOut);
                try {
                    String jsonIn = blogGetAllTask.execute().get();
                    Type listtype = new TypeToken<List<Trip_M>>() {  /////
                    }.getType();
                    blogList = new Gson().fromJson(jsonIn, listtype);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Common.showToast(activity, "No Internet");
            }
            return blogList;
        }

        private void showBlogs(List<Trip_M> groupList) {
            if (groupList == null || groupList.isEmpty()) {
                Common.showToast(activity, "搜尋不到行程");
            }
            BlogAdapter  blogAdapter = (BlogAdapter) rvBlog.getAdapter();
            if (blogAdapter == null) {
                rvBlog.setAdapter(new BlogAdapter(activity, blogList));
            } else {
                blogAdapter.setBlogs(blogList);
                blogAdapter.notifyDataSetChanged();
            }
        }

        private class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.MyViewHolder> {
            private LayoutInflater layoutInflater;
            private List<Trip_M> blogList;
            private int imageSize;

            BlogAdapter(Context context, List<Trip_M> blogList) { ///
                layoutInflater = LayoutInflater.from(context);
                this.blogList = blogList;
                imageSize = getResources().getDisplayMetrics().widthPixels / 2;

            }

            void setBlogs(List<Trip_M> blogList) {
                this.blogList = blogList;
            }

            class MyViewHolder extends RecyclerView.ViewHolder {
                ImageView imageView;
                TextView tvTitle, tvDate, tvCount;

                MyViewHolder(View itemView) {
                    super(itemView);
                    imageView = itemView.findViewById(R.id.ivBlog);
                    tvTitle = itemView.findViewById(R.id.tvTitle_Blog);
                    tvDate = itemView.findViewById(R.id.tvDate);
                    tvCount = itemView.findViewById(R.id.tvCount);
                }
            }

            @Override
            public int getItemCount() {
                return blogList == null ? 0 : blogList.size();
            }

            @NonNull
            @Override
            public BlogAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = layoutInflater.inflate(R.layout.item_view_blog_home, parent, false);
                return new BlogAdapter.MyViewHolder(itemView);
            }

            @Override
            public void onBindViewHolder(@NonNull BlogAdapter.MyViewHolder myViewHolder, int position) {

                final Trip_M blog = blogList.get(position);
                String Url = Common.URL_SERVER + "Trip_M_Servlet";
                int id = blog.getMemberId();
                blogImageTask = new ImageTask(Url, id, imageSize, myViewHolder.imageView);
                blogImageTask.execute();

                myViewHolder.tvTitle.setText(blog.getTripTitle());

            }
        }

        @Override
        public void onStop() {
            super.onStop();
            if(blogGetAllTask != null){
                blogGetAllTask.cancel(true);
                blogGetAllTask = null ;
            }
            if(blogImageTask !=null){
                blogImageTask.cancel(true);
                blogImageTask = null ;
            }

        }
    }