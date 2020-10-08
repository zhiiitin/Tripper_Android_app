package com.example.tripper_android_app.blog;

import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tripper_android_app.MainActivity;
import com.example.tripper_android_app.R;
import com.example.tripper_android_app.explore.Explore;
import com.example.tripper_android_app.explore.ExploreFragment;
import com.example.tripper_android_app.setting.member.Member;
import com.example.tripper_android_app.task.CommonTask;
import com.example.tripper_android_app.task.ImageTask;
import com.example.tripper_android_app.util.Common;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static androidx.navigation.Navigation.findNavController;


public class BlogMainFragment extends Fragment {

    private ImageView ivBackground,ivThumbs,ivTripList;
    private MainActivity activity;
    private TextView tvDescription, textDescription;
    private static final String TAG = "TAG_Blog_Main_Fragment";
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rvBlog;
    private CommonTask blogGetAllTask, blogDeleteTask;
    private List<ImageTask> imageTasks;
    private List<BlogD> blogList;
    private SharedPreferences preferences;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        activity = (MainActivity) getActivity();
        imageTasks =new ArrayList<>();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_blog_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        final Bundle bundle = getArguments();
        String blogTitle  = bundle.getString("BlogTitle");
        String blogDesc = bundle.getString("BlogDesc");
        toolbar.setTitle(blogTitle);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorForWhite));
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ivBackground = view.findViewById(R.id.ivBackground);
        ivBackground.setScaleType(ImageView.ScaleType.FIT_XY);
        tvDescription = view.findViewById(R.id.tvDescription);
        textDescription = view.findViewById(R.id.textDescription);
        ivThumbs = view.findViewById(R.id.ivThumbs);
        ivTripList = view.findViewById(R.id.ivTripList);
        ivThumbs.setImageResource(R.drawable.iconthumbss);
        ivTripList.setImageResource(R.drawable.icontriplist);
        tvDescription.setText("網誌描述：");
        textDescription.setText(" "+" "+blogDesc);
        rvBlog = view.findViewById(R.id.rvBlog);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        rvBlog.setLayoutManager(new LinearLayoutManager(activity));
        blogList = getBlogs();
        showBlogs(blogList);
        String url = Common.URL_SERVER+"ExploreServlet";
        int userId = bundle.getInt("UserId");
        int imageSize = 500;
        ImageTask imageTask = new ImageTask(url,userId,imageSize,ivBackground);
        imageTask.execute();
        imageTasks.add(imageTask);

        ivTripList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Navigation.findNavController(rvBlog).navigate(R.id.action_blogMainFragment_to_blogTripListFragment);
            }
        });

        ivThumbs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                 ivThumbs.setColorFilter(Color.RED);

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
                Navigation.findNavController(rvBlog).popBackStack();
                break;
            case R.id.blogEditFragment:
                Navigation.findNavController(rvBlog).navigate(R.id.action_blogMainFragment_to_blogEditFragment);
                break;
            default:
                break;
        }




        return super.onOptionsItemSelected(item);
    }

    private void showBlogs(List<BlogD> blogList) {
        if (blogList == null|| blogList.isEmpty()) {
            Common.showToast(activity, R.string.textNoSpotsFound);

        }
        BlogMainFragment.BlogAdapter blogAdapter = (BlogMainFragment.BlogAdapter) rvBlog.getAdapter();
        if (blogAdapter == null) {
            rvBlog.setAdapter(new BlogMainFragment.BlogAdapter(activity, blogList));
        } else {
            blogAdapter.setBlogs(blogList);
            //刷新頁面
//            blogAdapter.notifyDataSetChanged();
        }
    }

    private List<BlogD> getBlogs() {
        List<BlogD> blogDList= null;
        Bundle bundle = getArguments();
        int blogId = bundle.getInt("BlogId");


        if (Common.networkConnected(activity)) {
            //Servlet
            String url = Common.URL_SERVER + "BlogServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "findById");
            jsonObject.addProperty("id",blogId);
            String jsonOut = jsonObject.toString();
            blogGetAllTask = new CommonTask(url, jsonOut);

            try {
                String jsonIn = blogGetAllTask.execute().get();
                Type listType = new TypeToken<List<BlogD>>() {
                }.getType();

                blogDList= new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(activity, R.string.textNoNetwork);
        }
        return blogDList;
    }


    private class BlogAdapter  extends RecyclerView.Adapter<BlogAdapter.MyViewHolder> {

        private LayoutInflater layoutInflater;
        private List<BlogD> blogList;
        private Context context;
        private int imageSize;

        BlogAdapter(Context context, List<BlogD> blogList) {
             layoutInflater = LayoutInflater.from(context);
            this.blogList = blogList;
            this.context = context;



            /* 螢幕寬度除以4當作將圖的尺寸 */
            imageSize = 500;
        }


        void setBlogs(List<BlogD> blogList) {
            this.blogList = blogList;

        }
        @NonNull
        @Override
        public BlogAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView =layoutInflater.inflate(R.layout.item_view_blog_main,parent,false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull BlogAdapter.MyViewHolder holder, int position) {
            final BlogD blogD = blogList.get(position);
            String dateTime = blogD.getS_Date();
            SimpleDateFormat format = new SimpleDateFormat("MM-dd");
            try {
                Date date = format.parse(dateTime);
                System.out.println(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            holder.tvLocation.setText(blogD.getLocationName());
            holder.tvBlogDescription.setText(blogD.getBlogNote());
            holder.tvDays.setText(blogD.getS_Date());
            holder.imDays.setImageResource(R.drawable.layout_box_line);
            preferences = activity.getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
            if(position == 0 ){
                preferences.edit()
                        .putInt("TripListId",blogD.getBlogId())
                        .putString("DATE",blogD.getS_Date())
                        .apply();
            }




        }

        @Override
        public int getItemCount() {
            return blogList == null ? 0 : blogList.size();
        }

        private class MyViewHolder extends RecyclerView.ViewHolder {
            private ImageView ivPic1,ivPic2,ivPic3,ivPic;
            private TextView tvLocation,tvDays,tvBlogDescription;
            private ImageButton imDays;
            public MyViewHolder(@NonNull View itemView) {
                super(itemView);

                tvBlogDescription = itemView.findViewById(R.id.tvBlogDescription);
                tvDays = itemView.findViewById(R.id.tvDays);
                tvLocation = itemView.findViewById(R.id.tvLocation);
                ivPic1 = itemView.findViewById(R.id.ivPic1);
                ivPic2 = itemView.findViewById(R.id.ivPic2);
                ivPic3 = itemView.findViewById(R.id.ivPic3);
                ivPic= itemView.findViewById(R.id.ivPic);
                imDays = itemView.findViewById(R.id.imDays2);


            }
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        if (blogGetAllTask != null) {
            blogGetAllTask .cancel(true);
            blogGetAllTask  = null;
        }

        if (imageTasks != null && imageTasks.size() > 0) {
            for (ImageTask imageTask : imageTasks) {
                imageTask.cancel(true);
            }
            imageTasks.clear();
        }
        if (blogDeleteTask != null) {
            blogDeleteTask.cancel(true);
            blogDeleteTask = null;
        }
    }


}