package com.example.tripper_android_app.blog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tripper_android_app.MainActivity;
import com.example.tripper_android_app.R;
import com.example.tripper_android_app.task.CommonTask;
import com.example.tripper_android_app.task.ImageTask;
import com.example.tripper_android_app.util.CircleImageView;
import com.example.tripper_android_app.util.Common;
import com.example.tripper_android_app.util.TimeCountUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.zip.Inflater;



import static android.content.Context.MODE_PRIVATE;


public class BlogMainFragment extends Fragment {

    private ImageView ivBackground,ivThumbs,ivTripList,btSend;
    private MainActivity activity;
    private TextView tvDescription, textDescription,detail_page_do_comment,txLike;
    private static final String TAG = "TAG_Blog_Main_Fragment";
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rvBlog,rvComment;
    private CommonTask blogGetAllTask, blogDeleteTask,getImageTask,commentFinishTask;
    private List<ImageTask> imageTasks;
    private List<BlogD> blogList;
    private List<Blog_Comment> commentList ;
    private SharedPreferences preferences;
    private CommentAdapter commentAdapter;
    private BlogPic blogPic = null ;
    private HorizontalScrollView horizontalScrollView;
    private Blog_Comment blog_comment;
    private Dialog mDialog;


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
        detail_page_do_comment = view.findViewById(R.id.detail_page_do_comment);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        rvBlog.setLayoutManager(new LinearLayoutManager(activity));
        blogList = getBlogs();
        showBlogs(blogList);
        String url = Common.URL_SERVER+"BlogServlet";
        String userId = bundle.getString("BlogId");
        int imageSize = 500;
        ImageTask imageTask = new ImageTask(url,userId,imageSize,ivBackground);
        imageTask.execute();
        imageTasks.add(imageTask);
        txLike = view.findViewById(R.id.txLikeIcon);

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
                txLike.setText("1");



            }
        });

        detail_page_do_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDialog();
            }
        });



    }

    private void showDialog() {

        final View view = LayoutInflater.from(activity).inflate(R.layout.dialog_comment, null);
        mDialog = new Dialog(activity, R.style.dialogstyle);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
        Window window = mDialog.getWindow();
        window.setContentView(view);
        WindowManager.LayoutParams pl = window.getAttributes();
        pl.gravity=Gravity.BOTTOM;
        pl.width= WindowManager.LayoutParams.MATCH_PARENT;
        pl.height= WindowManager.LayoutParams.WRAP_CONTENT;
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setAttributes(pl);
        TextView tvComment;
        ImageButton imComment;
        ImageView ivBack;
        ivBack = view.findViewById(R.id.ivBack);
        tvComment = view.findViewById(R.id.tvComment);
        imComment = view.findViewById(R.id.imComment);
        tvComment.setText("留言區");
        rvComment = view.findViewById(R.id.rvComment);
        rvComment.setLayoutManager(new LinearLayoutManager(activity));
        commentList = getComment();
        showComments(commentList);
        EditText detail_page_do_comment = view.findViewById(R.id.detail_page_do_comment);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.cancel();
            }
        });
        btSend = view.findViewById(R.id.sendBtn);
        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferences = activity.getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
                Bundle bundle = getArguments();
                String blogId = bundle.getString("BlogId");
                String comment = detail_page_do_comment.getText().toString();
                if (comment.length() <= 0) {
                    Common.showToast(activity, R.string.textNameIsInvalid);
                    return;
                }
                String memberId= preferences.getString("memberId",null);
                if (Common.networkConnected(activity)) {
                    String url = Common.URL_SERVER + "BlogServlet";
                    Blog_Comment blog_comment = new Blog_Comment(blogId,comment,memberId);
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "insertBlogComment");
                    jsonObject.addProperty("blog_comment", new Gson().toJson(blog_comment));

                    int count = 0;
                    try {
                        String result = new CommonTask(url, jsonObject.toString()).execute().get();
                        count = Integer.parseInt(result);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                    if (count == 0) {
                        Common.showToast(activity, R.string.textUpdateFail);
                    } else  {
                        Common.showToast(activity, R.string.textUpdateSuccess);
                        commentList = getComment();
                        showComments(commentList);
                        detail_page_do_comment.setText("");




                    }
                } else {
                    Common.showToast(activity, R.string.textNoNetwork);
                }
            }


        });







    }



    private  List<Blog_Comment> getComment() {

        List<Blog_Comment> blog_comments= new ArrayList<>();
        Bundle bundle = getArguments();
        String blogId = bundle.getString("BlogId");


        if (Common.networkConnected(activity)) {
            //Servlet
            String url = Common.URL_SERVER + "BlogServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getCommentNote");
            jsonObject.addProperty("id",blogId);
            String jsonOut = jsonObject.toString();
            blogGetAllTask = new CommonTask(url, jsonOut);

            try {
                String jsonIn = blogGetAllTask.execute().get();
                Type listType = new TypeToken<List<Blog_Comment>>() {
                }.getType();

                blog_comments= new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(activity, R.string.textNoNetwork);
        }
        return blog_comments;
    }

    private void showComments(List<Blog_Comment> commentList) {
        if (commentList == null|| commentList.isEmpty()) {
            Common.showToast(activity, R.string.textNoSpotsFound);

        }
         commentAdapter = (CommentAdapter)rvComment.getAdapter();
        if (commentAdapter == null) {
            rvComment.setAdapter(new CommentAdapter(activity, BlogMainFragment.this.commentList));
        } else {
            commentAdapter.setBlogs(commentList);
            //刷新頁面
            commentAdapter.notifyDataSetChanged();
        }
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
        String blogId = bundle.getString("BlogId");


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
            holder.tvLocation.setText(blogD.getLocationName());
            String str = blogD.getS_Date();
            SimpleDateFormat format = new SimpleDateFormat("MM-dd");
            Date date = null;
            try {
                date = format.parse(str);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String date1 = format.format(date);
            holder.tvBlogDescription.setText(blogD.getBlogNote());
            holder.tvDays.setText(blogD.getS_Date());
            holder.imDays.setImageResource(R.drawable.layout_box_line);
            holder.tvDate.setText("日期:");

            preferences = activity.getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
            if(position == 0 ){
                preferences.edit()
                        .putString("TripListId",blogD.getBlogId())
                        .putString("DATEE",blogD.getS_Date())
                        .apply();
            }
            holder.tvSpotName.setText("景點描述:");
            holder.tvPic.setText("景點照片:(向左滑動照片)");
            String url = Common.URL_SERVER + "BlogServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getSpotImage");
            jsonObject.addProperty("blog_Id", blogD.getBlogId());
            jsonObject.addProperty("loc_Id", blogD.getLocationId());
            getImageTask = new CommonTask(url, jsonObject.toString());
            horizontalScrollView.post(new Runnable() {
                public void run() {
                    horizontalScrollView.scrollTo(0, horizontalScrollView.getBottom());
                }
            });
            blogPic = new BlogPic();
            try {
                String jsonIn = getImageTask.execute().get();
                Type listType = new TypeToken<BlogPic>() {
                }.getType();

                blogPic = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if(blogPic != null) {
                if (blogPic.getPic1() != null) {
                    byte[] img1 = Base64.decode(blogPic.getPic1(), Base64.DEFAULT);
                    Glide.with(activity).load(img1).into(holder.ivPic);
                    holder.ivPic.setVisibility(View.VISIBLE);
                    holder.ivPic.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                            View view = getLayoutInflater().inflate(R.layout.dialog_imageview, null);
                            alertDialog.setView(view);
                            ImageView ivPhoto = view.findViewById(R.id.ivPhoto);
                            String url = Common.URL_SERVER + "BlogServlet";
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("action", "getSpotImage");
                            jsonObject.addProperty("blog_Id", blogD.getBlogId());
                            jsonObject.addProperty("loc_Id", blogD.getLocationId());
                            getImageTask = new CommonTask(url, jsonObject.toString());
                            blogPic = new BlogPic();
                            try {
                                String jsonIn = getImageTask.execute().get();
                                Type listType = new TypeToken<BlogPic>() {
                                }.getType();

                                blogPic = new Gson().fromJson(jsonIn, listType);
                            } catch (Exception e) {
                                Log.e(TAG, e.toString());
                            }
                            if (blogPic.getPic1() != null) {
                                byte[] img1 = Base64.decode(blogPic.getPic1(), Base64.DEFAULT);
                                Glide.with(activity).load(img1).into(ivPhoto);
                                //將白色部分設為透明
                                alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                alertDialog.setCancelable(true);
                                alertDialog.show();

                            }
                        }
                    });
                    holder.ivPic.setVisibility(View.VISIBLE);
                   }else {
                        horizontalScrollView.setVisibility(View.GONE);
                        holder.tvPic.setVisibility(View.GONE);
                }if (blogPic.getPic2() != null) {
                    byte[] img2 = Base64.decode(blogPic.getPic2(), Base64.DEFAULT);
                    Glide.with(activity).load(img2).into(holder.ivPic1);
                    holder.ivPic1.setVisibility(View.VISIBLE);
                    holder.ivPic1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                            View view = getLayoutInflater().inflate(R.layout.dialog_imageview, null);
                            alertDialog.setView(view);
                            ImageView ivPhoto = view.findViewById(R.id.ivPhoto);
                            String url = Common.URL_SERVER + "BlogServlet";
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("action", "getSpotImage");
                            jsonObject.addProperty("blog_Id", blogD.getBlogId());
                            jsonObject.addProperty("loc_Id", blogD.getLocationId());
                            getImageTask = new CommonTask(url, jsonObject.toString());
                            blogPic = new BlogPic();
                            try {
                                String jsonIn = getImageTask.execute().get();
                                Type listType = new TypeToken<BlogPic>() {
                                }.getType();

                                blogPic = new Gson().fromJson(jsonIn, listType);
                            } catch (Exception e) {
                                Log.e(TAG, e.toString());
                            }
                            if (blogPic.getPic2() != null) {
                                byte[] img2 = Base64.decode(blogPic.getPic2(), Base64.DEFAULT);
                                Glide.with(activity).load(img2).into(ivPhoto);
                                //將白色部分設為透明
                                alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                alertDialog.setCancelable(true);
                                alertDialog.show();

                            }
                        }
                    });

                }if (blogPic.getPic3() != null) {
                    byte[] img3 = Base64.decode(blogPic.getPic3(), Base64.DEFAULT);
                    Glide.with(activity).load(img3).into(holder.ivPic2);
                    holder.ivPic2.setVisibility(View.VISIBLE);
                    holder.ivPic2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                            View view = getLayoutInflater().inflate(R.layout.dialog_imageview, null);
                            alertDialog.setView(view);
                            ImageView ivPhoto = view.findViewById(R.id.ivPhoto);
                            String url = Common.URL_SERVER + "BlogServlet";
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("action", "getSpotImage");
                            jsonObject.addProperty("blog_Id", blogD.getBlogId());
                            jsonObject.addProperty("loc_Id", blogD.getLocationId());
                            getImageTask = new CommonTask(url, jsonObject.toString());
                            blogPic = new BlogPic();
                            try {
                                String jsonIn = getImageTask.execute().get();
                                Type listType = new TypeToken<BlogPic>() {
                                }.getType();

                                blogPic = new Gson().fromJson(jsonIn, listType);
                            } catch (Exception e) {
                                Log.e(TAG, e.toString());
                            }
                            if (blogPic.getPic3() != null) {
                                byte[] img3 = Base64.decode(blogPic.getPic3(), Base64.DEFAULT);
                                Glide.with(activity).load(img3).into(ivPhoto);
                                //將白色部分設為透明
                                alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                alertDialog.setCancelable(true);
                                alertDialog.show();

                            }

                        }

                    });
                }if (blogPic.getPic4() != null) {
                    byte[] img4 = Base64.decode(blogPic.getPic4(), Base64.DEFAULT);
                    Glide.with(activity).load(img4).into(holder.ivPic3);
                    holder.ivPic3.setVisibility(View.VISIBLE);
                    holder.ivPic3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                            View view = getLayoutInflater().inflate(R.layout.dialog_imageview, null);
                            alertDialog.setView(view);
                            ImageView ivPhoto = view.findViewById(R.id.ivPhoto);
                            String url = Common.URL_SERVER + "BlogServlet";
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("action", "getSpotImage");
                            jsonObject.addProperty("blog_Id", blogD.getBlogId());
                            jsonObject.addProperty("loc_Id", blogD.getLocationId());
                            getImageTask = new CommonTask(url, jsonObject.toString());
                            blogPic = new BlogPic();
                            try {
                                String jsonIn = getImageTask.execute().get();
                                Type listType = new TypeToken<BlogPic>() {
                                }.getType();

                                blogPic = new Gson().fromJson(jsonIn, listType);
                            } catch (Exception e) {
                                Log.e(TAG, e.toString());
                            }
                            if (blogPic.getPic4() != null) {
                                byte[] img2 = Base64.decode(blogPic.getPic4(), Base64.DEFAULT);
                                Glide.with(activity).load(img2).into(ivPhoto);
                                //將白色部分設為透明
                                alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                alertDialog.setCancelable(true);
                                alertDialog.show();

                            }

                        }
                    });
                }

            }





        }


        @Override
        public int getItemCount() {
            return blogList == null ? 0 : blogList.size();
        }

        private class MyViewHolder extends RecyclerView.ViewHolder {
            private ImageView ivPic1,ivPic2,ivPic3,ivPic;
            private TextView tvLocation,tvDays,tvBlogDescription,tvSpotName,tvPic,tvDate;
            private ImageButton imDays;
            public MyViewHolder(@NonNull View itemView) {
                super(itemView);

                tvBlogDescription = itemView.findViewById(R.id.tvBlogDescription);
                tvDays = itemView.findViewById(R.id.tvDays);
                tvLocation = itemView.findViewById(R.id.tvLocation);
                ivPic1 = itemView.findViewById(R.id.ivSpot2);
                ivPic2 = itemView.findViewById(R.id.ivSpot3);
                ivPic3 = itemView.findViewById(R.id.ivSpot4);
                ivPic= itemView.findViewById(R.id.ivSpot1);
                imDays = itemView.findViewById(R.id.imDays2);
                tvPic = itemView.findViewById(R.id.tvPic);
                tvSpotName= itemView.findViewById(R.id.tvSpotName);
                horizontalScrollView = itemView.findViewById(R.id.horizontalScrollView);
                tvDate = itemView.findViewById(R.id.tvDate);


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


    private class CommentAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private Context context;
        private List<Blog_Comment> commentList;
        private LayoutInflater layoutInflater;
        private int imageSize;

        CommentAdapter(Context context ,List<Blog_Comment> commentList){
            layoutInflater = LayoutInflater.from(context);
            this.context = context;
            this.commentList = commentList;
        }
        void setBlogs(List<Blog_Comment> commentList){
            this.commentList = commentList;
        }
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.item_blog_comment,parent,false);
            return new MyViewHolder(itemView) ;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            final  Blog_Comment blog_comment = commentList.get(position);
            holder.tvComment.setText(""+ "" +blog_comment.getContent());
            holder.tvName.setText(blog_comment.getName());
            holder.ivPic.setImageResource(blog_comment.ivImage);
            String icoUrl = Common.URL_SERVER + "MemberServlet";
            //從MEMBER資料表 娶回來的資料無法秀在上面
            String member_Id = blog_comment.getMember_ID();
            ImageTask imageTask1 = new ImageTask(icoUrl,member_Id, imageSize, holder.ivPic);
            imageTask1.execute();
            imageTasks.add(imageTask1);
            TimeCountUtil timeCountUtil = new TimeCountUtil();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = null;
            try {
                date = format.parse(blog_comment.getDate());
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            holder.tvDate.setText(timeCountUtil.timeCount(date));
            preferences = activity.getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
            String userId = preferences.getString("memberId",null);
            String memberId = blog_comment.getMember_ID();
            if(userId.equals(memberId)){
                holder.ivEdit.setVisibility(View.VISIBLE);
            }

            holder.ivEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final PopupMenu popupMenu = new PopupMenu(activity, v, Gravity.END);
                    popupMenu.inflate(R.menu.blog_comment_edit_menu);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            switch (item.getItemId()) {
                                case R.id.blogCommentEdit:
                                     holder.editComment.setVisibility(View.VISIBLE);
                                     holder.tvComment.setVisibility(View.GONE);
                                     holder.editComment.setText(blog_comment.getContent());
                                     holder.ivSave.setVisibility(View.VISIBLE);
                                     holder.ivBack.setVisibility(View.VISIBLE);
                                     holder.ivBack.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             holder.ivBack.setVisibility(View.GONE);
                                             holder.ivSave.setVisibility(View.GONE);
                                             holder.editComment.setVisibility(View.GONE);
                                             holder.tvComment.setVisibility(View.VISIBLE);
                                             holder.tvComment.setText(blog_comment.getContent());
                                         }
                                     });
                                     holder.ivSave.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             String comment = holder.editComment.getText().toString().trim();
                                             if (comment.length() <= 0) {
                                                 Common.showToast(activity, R.string.textNameIsInvalid);
                                                 return;
                                             }
                                             SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                             Date dt=new Date();
                                             String dts=sdf.format(dt);
                                             String name= blog_comment.getName();
                                             int comId = blog_comment.getComId();
                                             String tripId = blog_comment.getBlogId();
                                             Blog_Comment blogComment = new Blog_Comment(tripId,name,comment,member_Id,dts,comId);
                                             if (Common.networkConnected(activity)) {
                                                 String Url = Common.URL_SERVER + "BlogServlet";
                                                 JsonObject jsonObject = new JsonObject();
                                                 jsonObject.addProperty("action", "updateComment");
                                                 jsonObject.addProperty("comment", new Gson().toJson(blogComment));
                                                 commentFinishTask = new CommonTask(Url,jsonObject.toString());
                                                 int count = 0;
                                                 try {
                                                     String jsonIn = commentFinishTask.execute().get();
                                                     count = Integer.parseInt(jsonIn);
                                                     if (count >= 1) {
                                                         Log.e(TAG, "Update sucessful");
                                                         Common.showToast(activity, "留言修改成功");
                                                         holder.ivSave.setVisibility(View.GONE);
                                                         holder.ivBack.setVisibility(View.GONE);
                                                         holder.editComment.setVisibility(View.GONE);
                                                         holder.tvComment.setVisibility(View.VISIBLE);
                                                         TimeCountUtil timeCountUtil = new TimeCountUtil();
                                                         SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                         Date date = null;
                                                         try {
                                                             date = format.parse(blog_comment.getDate());
                                                         } catch (ParseException e) {
                                                             // TODO Auto-generated catch block
                                                             e.printStackTrace();
                                                         }
                                                         commentList = getComment();
                                                         showComments(commentList);
                                                         commentAdapter.notifyDataSetChanged();
                                                         holder.tvDate.setText(timeCountUtil.timeCount(date));
                                                         holder.tvComment.setText(comment);
                                                     } else {
                                                         Common.showToast(activity, "留言修改失敗");
                                                     }
                                                 } catch (Exception e) {
                                                     Log.e(TAG, e.toString());
                                                 }
                                             }



                                         }
                                     });

                                     break;
                                case R.id.blogCommentDelete:
                                    if (Common.networkConnected(activity)) {
                                        String url = Common.URL_SERVER + "BlogServlet";
                                        JsonObject jsonObject = new JsonObject();
                                        jsonObject.addProperty("action", "deleteComment");
                                        jsonObject.addProperty("comId",blog_comment.getComId());
                                        int count = 0;
                                        try {
                                            blogDeleteTask = new CommonTask(url, jsonObject.toString());
                                            String result = blogDeleteTask.execute().get();
                                            count = Integer.parseInt(result);
                                        } catch (Exception e) {
                                            Log.e(TAG, e.toString());
                                        }
                                        if (count == 0) {
                                            Common.showToast(activity, R.string.textDeleteFail);
                                        } else {
                                            commentList.remove(blog_comment);
                                           BlogMainFragment.CommentAdapter.this.notifyDataSetChanged();
                                           BlogMainFragment.CommentAdapter.this.commentList.remove(blog_comment);
                                            // 外面spots也必須移除選取的spot
                                            Common.showToast(activity, R.string.textDeleteCommentSuccess);
                                        }
                                    } else {
                                        Common.showToast(activity, R.string.textNoNetwork);
                                    }

                            }
                            return true;


                        }


                    });
                    popupMenu.show();
                }

            });




        }


        @Override
        public int getItemCount() {
            return commentList.size();
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvName,tvComment,tvDate;
        CircleImageView ivPic;
        ImageView ivEdit,ivSave,ivBack;
        EditText editComment;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvComment = itemView.findViewById(R.id.tvComment);
            tvName = itemView.findViewById(R.id.tvName);
            ivPic = itemView.findViewById(R.id.ivUser);
            tvDate = itemView.findViewById(R.id.tvDate);
            ivEdit = itemView.findViewById(R.id.ivEdit);
            ivSave = itemView.findViewById(R.id.ivSave);
            ivBack = itemView.findViewById(R.id.ivBack);
            editComment = itemView.findViewById(R.id.editComment);

        }
    }


}