package com.example.tripper_android_app.group;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.tripper_android_app.MainActivity;
import com.example.tripper_android_app.R;
import com.example.tripper_android_app.blog.BlogMainFragment;
import com.example.tripper_android_app.blog.Blog_Comment;
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
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class GroupCommentFragment extends Fragment {

    private static final String TAG = "Group_Comment_Fragment";
    private MainActivity activity;
    private RecyclerView rvGroupComment ;
    private CommonTask blogDeleteTask,blogGetAllTask,commentFinishTask ;
    private EditText messageEdit ;
    private TextView tvMagic ;
    private ImageView sendBtn ;
    private List<Blog_Comment> commentList ;
    private SharedPreferences preferences;
    private CommentAdapter commentAdapter;
    private Bundle bundle ;
    private String tripId ;
    private List<ImageTask> imageTasks;
    private Blog_Comment blog_comment;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
        imageTasks =new ArrayList<>();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group_comment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bundle = getArguments();
        if(bundle != null){
            tripId = bundle.getString("tripId");
        }

        //ToolBar
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("布告欄");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorForWhite));
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Drawable upArrow = ContextCompat.getDrawable(activity, R.drawable.abc_ic_ab_back_material);
        if (upArrow != null) {
            upArrow.setColorFilter(ContextCompat.getColor(activity, R.color.colorForWhite), PorterDuff.Mode.SRC_ATOP);
            activity.getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }

        rvGroupComment = view.findViewById(R.id.rvGroupComment);
        messageEdit = view.findViewById(R.id.messageEdit);
        sendBtn = view.findViewById(R.id.sendBtn);
        tvMagic = view.findViewById(R.id.textView5);

        rvGroupComment.setLayoutManager(new LinearLayoutManager(activity));
        commentList = getComment();
        showComments(commentList);

        //神奇小按鈕
        tvMagic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageEdit.setText("大家抱歉，我臨時有事不能參加，要先行告退了");
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferences = activity.getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
                String comment = messageEdit.getText().toString().trim();
                if (comment.length() <= 0) {
                    Common.showToast(activity, R.string.textNameIsInvalid);
                    return;
                }
                String memberId= preferences.getString("memberId",null);
                if (Common.networkConnected(activity)) {
                    String url = Common.URL_SERVER + "Trip_Group_Servlet";
                    Blog_Comment blog_comment = new Blog_Comment(tripId,comment,memberId);
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
                        messageEdit.setText("");

                    }
                } else {
                    Common.showToast(activity, R.string.textNoNetwork);
                }

            }
        });
    }

    private  List<Blog_Comment> getComment() {

        List<Blog_Comment> blog_comments= new ArrayList<>();


        if (Common.networkConnected(activity)) {
            //Servlet
            String url = Common.URL_SERVER + "Trip_Group_Servlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getCommentNote");
            jsonObject.addProperty("id",tripId);
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
        }
        commentAdapter = (CommentAdapter)rvGroupComment.getAdapter();
        if (commentAdapter == null) {
            rvGroupComment.setAdapter(new CommentAdapter(activity,commentList));
        } else {
            commentAdapter.setBlogs(commentList);
            //刷新頁面
            commentAdapter.notifyDataSetChanged();
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
            holder.ivPic.setImageResource(blog_comment.getIvImage());
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
                                                String Url = Common.URL_SERVER + "Trip_Group_Servlet";
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
                                        String url = Common.URL_SERVER + "Trip_Group_Servlet";
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
                                            CommentAdapter.this.notifyDataSetChanged();
                                            CommentAdapter.this.commentList.remove(blog_comment);
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                Navigation.findNavController(rvGroupComment).popBackStack();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}