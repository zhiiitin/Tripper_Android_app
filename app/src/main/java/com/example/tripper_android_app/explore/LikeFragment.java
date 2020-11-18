package com.example.tripper_android_app.explore;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.SearchView;
import android.widget.TextView;


import com.example.tripper_android_app.MainActivity;
import com.example.tripper_android_app.R;
import com.example.tripper_android_app.blog.BlogMainFragment;
import com.example.tripper_android_app.blog.Blog_Comment;
import com.example.tripper_android_app.task.CommonTask;
import com.example.tripper_android_app.task.ImageTask;
import com.example.tripper_android_app.util.CircleImageView;
import com.example.tripper_android_app.util.Common;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class LikeFragment extends Fragment {
    private MainActivity activity;
    private List<ImageTask> imageTasks;
    private static final String TAG = "TAG_Likes_Fragment";
    private CircleImageView circleImageView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SearchView svLikes;
    private RecyclerView rvLikes;
    private List<Like> likes;
    private CommonTask getAllLikeTask,deleteLikeTask;
    private LikesAdapter likesAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        activity = (MainActivity) getActivity();
        imageTasks = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_like, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Likes");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorForWhite));
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rvLikes = view.findViewById(R.id.rvLike);
        rvLikes.setLayoutManager(new LinearLayoutManager(activity));
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        svLikes = view.findViewById(R.id.svLike);
        likes = getLikes();
        showLikes(likes);

        svLikes.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    showLikes(likes);
                } else {
                    List<Like> searchLike= new ArrayList<>();
                    // 搜尋原始資料內有無包含關鍵字(不區別大小寫)
                    for (Like like : likes) {
                        if (like.getAccountId().toUpperCase().contains(newText.toUpperCase()) || like.getName().toUpperCase().contains(newText.toUpperCase())) {

                            searchLike.add(like);
                        }
                    }
                    showLikes(searchLike);
                }
                return true;
            }
        });

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.blog_edit_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                Navigation.findNavController(rvLikes).popBackStack();
                break;

            default:
                break;
        }




        return super.onOptionsItemSelected(item);
    }

    private  List<Like> getLikes() {

        List<Like> likes= new ArrayList<>();
        Bundle bundle = getArguments();
        String blogId = bundle.getString("BlogID");


        if (Common.networkConnected(activity)) {
            //Servlet
            String url = Common.URL_SERVER + "ExploreServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getLikes");
            jsonObject.addProperty("blogId",blogId);
            String jsonOut = jsonObject.toString();
            getAllLikeTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = getAllLikeTask.execute().get();
                Type listType = new TypeToken<List<Like>>() {
                }.getType();

                likes= new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(activity, R.string.textNoNetwork);
        }
        return likes;
    }

    private void showLikes(List<Like> likes) {
        if (likes == null|| likes.isEmpty()) {
            Common.showToast(activity, "沒有人按讚");

        }
        likesAdapter = (LikesAdapter) rvLikes.getAdapter();
        if (likesAdapter == null) {
            rvLikes.setAdapter(new LikesAdapter(activity, likes));
        } else {
            likesAdapter.setLikes(likes);
            //刷新頁面
            likesAdapter.notifyDataSetChanged();
        }
    }

    private class LikesAdapter extends RecyclerView.Adapter<LikeFragment.MyViewHolder> {
        private LayoutInflater inflater;
        private Context context;
        private List<Like> likes;
        private int imageSize ;

        public LikesAdapter(Context context, List<Like> likes) {
            this.context = context;
            this.likes = likes;
            inflater = LayoutInflater.from(context);
            imageSize = 500;

        }
        void setLikes(List<Like> likes){
            this.likes = likes;
        }

        @NonNull
        @Override
        public LikeFragment.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = inflater.inflate(R.layout.item_view_like,parent,false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull LikeFragment.MyViewHolder holder, int position) {
                final Like like = likes.get(position);
                holder.tvName.setText(like.getName());
                holder.tvAccount.setText(like.getAccountId());
                String icoUrl = Common.URL_SERVER + "MemberServlet";
            //從MEMBER資料表 娶回來的資料無法秀在上面
                String member_Id = like.getUserId();
                ImageTask imageTask1 = new ImageTask(icoUrl,member_Id, imageSize, holder.ivUser);
                imageTask1.execute();
                imageTasks.add(imageTask1);

        }

        @Override
        public int getItemCount() {
            return likes == null ? 0 :likes.size();
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvAccount,tvName;
        CircleImageView ivUser;

        public MyViewHolder(View itemView) {
            super(itemView);

            ivUser = itemView.findViewById(R.id.ivUser);
            tvAccount = itemView.findViewById(R.id.tvAccount);
            tvName = itemView.findViewById(R.id.tvName);

        }
    }
}