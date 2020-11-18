package com.example.tripper_android_app.explore;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
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

import com.example.tripper_android_app.MainActivity;
import com.example.tripper_android_app.R;
import com.example.tripper_android_app.task.CommonTask;
import com.example.tripper_android_app.task.ImageTask;
import com.example.tripper_android_app.util.CircleImageView;
import com.example.tripper_android_app.util.Common;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class ExploreFragment1 extends Fragment {
    private MainActivity activity;
    private static final String TAG = "TAG_ExploreListFragment";
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rvExplore, rvLocation;
    private CommonTask exploreGetAllTask, exploreDeleteTask, articleDeleteTask;

    private List<ImageTask> imageTasks;
    private List<Explore> explores;
    private SharedPreferences preferences;
    private ExploreAdapter exploreAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
        imageTasks = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_explore1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SearchView searchView = view.findViewById(R.id.svGroup);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        rvExplore = view.findViewById(R.id.rvExplore);
        rvExplore.setLayoutManager(new LinearLayoutManager(activity));
        explores = getExplores();
        showExplores(explores);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                explores = getExplores();
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
                        if (explore.getTittleName().toUpperCase().contains(newText.toUpperCase()) || explore.getNickName().toUpperCase().contains(newText.toUpperCase())) {

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
         exploreAdapter = (ExploreAdapter) rvExplore.getAdapter();
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
            preferences = activity.getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
            String userId = preferences.getString("memberId", null);
            String url = Common.URL_SERVER + "ExploreServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAll");
            jsonObject.addProperty("loginUserId", userId);
            String jsonOut = jsonObject.toString();
            exploreGetAllTask = new CommonTask(url, jsonOut);

            try {
                String jsonIn = exploreGetAllTask.execute().get();
                Type listType = new TypeToken<List<Explore>>() {
                }.getType();

                explores = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
//                Log.e(TAG, e.toString());
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
        private Context context;

        ExploreAdapter(Context context, List<Explore> explores) {
            layoutInflater = LayoutInflater.from(context);
            this.explores = explores;
            this.context = context;


            /* 螢幕寬度除以4當作將圖的尺寸 */
            imageSize = 500;
        }


        void setExplores(List<Explore> explores) {
            this.explores = explores;

        }

        @NonNull
        @Override
        public ExploreAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


            View itemView = layoutInflater.inflate(R.layout.item_view_explore, parent, false);
            return new ExploreAdapter.MyViewHolder(itemView);
        }


        @Override
        public int getItemCount() {
            return explores == null ? 0 : explores.size();
        }


        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView ivBlogPic, ivUser, ivThumbs;
            TextView tvUseName, tvBlogName, tvDate, tvThumbs;
            CircleImageView circleImageView;


            MyViewHolder(View itemView) {
                super(itemView);
                ivBlogPic = itemView.findViewById(R.id.ivBlog);
                tvBlogName = itemView.findViewById(R.id.tvTitle_Blog);
                tvUseName = itemView.findViewById(R.id.tvAccount);
                ivUser = itemView.findViewById(R.id.ivUser);
                tvDate = itemView.findViewById(R.id.tvDate);
                tvThumbs = itemView.findViewById(R.id.tvThumbs);
                ivThumbs = itemView.findViewById(R.id.ivThumbs);

            }
        }

        @SuppressLint("WrongConstant")
        @Override
        public void onBindViewHolder(@NonNull final ExploreAdapter.MyViewHolder holder, int position) {
            final Explore explore = explores.get(position);
            String url = Common.URL_SERVER + "BlogServlet";
            String id = explore.getBlogId();
            String userId = explore.getUserId();
            ImageTask imageTask = new ImageTask(url, id, imageSize, holder.ivBlogPic);
            imageTask.execute();
            holder.ivBlogPic.setScaleType(ImageView.ScaleType.FIT_XY);
            String icoUrl = Common.URL_SERVER + "MemberServlet";
            //從MEMBER資料表 娶回來的資料無法秀在上面
            ImageTask imageTask1 = new ImageTask(icoUrl, userId, imageSize, holder.ivUser);
            imageTask1.execute();
            imageTasks.add(imageTask);
            //大頭貼
            imageTasks.add(imageTask1);
            holder.tvUseName.setText(explore.getNickName());
            holder.tvBlogName.setText(explore.getTittleName());
            String str = explore.getDateTime();
            System.out.println(str);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date = null;
            try {
                date = format.parse(str);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String date1 = format.format(date);
            holder.tvDate.setText("網誌建立日期：" + date1);
            ImageView goodIcon = holder.ivThumbs;
            if (userId == null) {
                explore.setArticleGoodStatus(false);
                goodIcon.setColorFilter(Color.parseColor("#424242"));
            }else{
                if (explore.isArticleGoodStatus()) {
                    explore.setArticleGoodStatus(true);
                    holder.ivThumbs.setColorFilter(Color.RED);
                } else {
                    goodIcon.setColorFilter(Color.parseColor("#424242"));
                    explore.setArticleGoodStatus(false);
                }
            }
            holder.ivThumbs.setImageResource(R.drawable.icnthumbs);
            holder.tvThumbs.setText((explore.getArticleGoodCount() + "個人按讚"));
            holder.tvThumbs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("BlogID",explore.getBlogId());
                    Navigation.findNavController(rvExplore).navigate(R.id.action_exploreFragment_to_likeFragment,bundle);
                }
            });
            holder.ivThumbs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!explore.isArticleGoodStatus()) {
                        if (Common.networkConnected(activity)) {
                            String insertGoodUrl = Common.URL_SERVER + "ArticleServlet";
                            preferences = activity.getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
                            String userId1 = preferences.getString("memberId", null);
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("action", "articleGoodInsert");
                            jsonObject.addProperty("articleId", explore.getBlogId());
                            jsonObject.addProperty("loginUserId", userId1);
//                        jsonObject.addProperty("articleGood", new Gson().toJson(articleGood));
                            int count = 0;
                            try {
                                String result = new CommonTask(insertGoodUrl, jsonObject.toString()).execute().get();
                                count = Integer.parseInt(result);
                            } catch (Exception e) {
                                Log.e(TAG, e.toString());
                            }
                            if (count == 0) {
                                Common.showToast(activity, "點讚失敗");
                            } else {
                                explore.setArticleGoodCount((explore.getArticleGoodCount() + 1));
                                holder.tvThumbs.setText((explore.getArticleGoodCount() + ""));
                                goodIcon.setColorFilter(Color.RED);
                                explore.setArticleGoodStatus(true);
                            }
                        } else {
                            Common.showToast(activity, "取得連線失敗");
                        }

                    } else {
                        if (Common.networkConnected(activity)) {
                            String deleteGoodUrl = Common.URL_SERVER + "ArticleServlet";
                            preferences = activity.getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
                            String userId2 = preferences.getString("memberId", null);
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("action", "articleGoodDelete");
                            jsonObject.addProperty("articleId", explore.getBlogId());
                            jsonObject.addProperty("userId", userId2);
                            int count = 0;
                            try {
                                articleDeleteTask = new CommonTask(deleteGoodUrl, jsonObject.toString());
                                String result = articleDeleteTask.execute().get();
                                count = Integer.parseInt(result);
                            } catch (Exception e) {
                                Log.e(TAG, e.toString());
                            }
                            if (count == 0) { //如果選擇的資料已經沒東西
                                Common.showToast(activity, "取消失敗");
                            } else {
                                explore.setArticleGoodCount(explore.getArticleGoodCount() - 1);
                                holder.tvThumbs.setText(((explore.getArticleGoodCount()) + ""));

                                goodIcon.setColorFilter(Color.parseColor("#424242"));
                                explore.setArticleGoodStatus(false);
                            }
                        } else {
                            Common.showToast(activity, "取消讚連線失敗");
                        }

                    }

                }
            });

            holder.ivBlogPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("UserId", explore.getUserId());
                    bundle.putString("BlogId", explore.getBlogId());
                    bundle.putString("BlogTitle", explore.getTittleName());
                    bundle.putString("BlogDesc", explore.getBlogDesc());
                    bundle.putString("UserName", explore.getNickName());
                    Navigation.findNavController(v).navigate(R.id.action_exploreFragment_to_blogMainFragment, bundle);

                }
            });

        }


    }


    @Override
    public void onStop() {
        super.onStop();
        if (exploreGetAllTask != null) {
            exploreGetAllTask.cancel(true);
            exploreGetAllTask = null;
        }

        if (imageTasks != null && imageTasks.size() > 0) {
            for (ImageTask imageTask : imageTasks) {
                imageTask.cancel(true);
            }
            imageTasks.clear();
        }

        if (exploreDeleteTask != null) {
            exploreDeleteTask.cancel(true);
        }
    }

}



