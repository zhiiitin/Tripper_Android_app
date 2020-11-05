package com.example.tripper_android_app.group;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tripper_android_app.MainActivity;
import com.example.tripper_android_app.R;
import com.example.tripper_android_app.friends.FriendsListFragment;
import com.example.tripper_android_app.setting.member.Member;
import com.example.tripper_android_app.task.CommonTask;
import com.example.tripper_android_app.task.ImageTask;
import com.example.tripper_android_app.util.CircleImageView;
import com.example.tripper_android_app.util.Common;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.content.Context.MODE_PRIVATE;


public class GroupMbrListFragment extends Fragment {

    private static final String TAG = "TAG_GroupMbrListFragment";
    private RecyclerView rvMemberList;
    private MainActivity activity;
    private CommonTask groupMbrTask,hostTask;
    private List<Member> memberList;
    private String tripId ;
    private int memberId ;
    private FirebaseUser mUser;
    private FirebaseAuth auth;
    private ImageView ivHostPic ;
    private TextView tvHostName ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
        setHasOptionsMenu(true);
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_group_mbr_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        if(bundle != null) {
            tripId = bundle.getString("tripId");
            memberId = bundle.getInt("memberId");
        }
        //ToolBar
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("成員列表");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorForWhite));
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Drawable upArrow = ContextCompat.getDrawable(activity, R.drawable.abc_ic_ab_back_material);
        if (upArrow != null) {
            upArrow.setColorFilter(ContextCompat.getColor(activity, R.color.colorForWhite), PorterDuff.Mode.SRC_ATOP);
            activity.getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }

        ivHostPic = view.findViewById(R.id.ivHostPic);
        tvHostName = view.findViewById(R.id.tvHostName);
//抓取Host資料
        String Url = Common.URL_SERVER + "MemberServlet";
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "getProfileById");
        jsonObject.addProperty("memberId", memberId);
        Member member = null ;
        try {
            hostTask = new CommonTask(Url, jsonObject.toString());
            String jsonIn = hostTask.execute().get();
            Type listtype = new TypeToken<Member>() {
            }.getType();
            member = new Gson().fromJson(jsonIn, listtype);

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }if(member != null){
            tvHostName.setText(member.getNickName());
        }

        showHost(member);

        mUser = auth.getCurrentUser();



        rvMemberList = view.findViewById(R.id.rvMemberList);
        rvMemberList.setLayoutManager(new LinearLayoutManager(activity));

        memberList = getMemberList();
        showMemberList(memberList);

    }

    private List<Member> getMemberList() {
        memberList = new ArrayList<>();
        if(Common.networkConnected(activity)){
            String url = Common.URL_SERVER + "Trip_Group_Servlet";

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getGroupMbrList");
            jsonObject.addProperty("tripId", tripId);
            String jsonOut = jsonObject.toString();
            Log.d(TAG, "getMbrList jsonOut:: " + jsonOut);
            groupMbrTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = groupMbrTask.execute().get();
                Log.d(TAG, "getFriendsList jsonIn::" + jsonIn);
                Type listType = new TypeToken<List<Member>>(){}.getType();
                memberList = new Gson().fromJson(jsonIn, listType);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

        }else {
            Common.showToast(activity, "請確認網路連線");
        }
        return memberList;
    }

    private void showMemberList(List<Member> memberList) {
        if(memberList == null || memberList.isEmpty()){

            return;
        }
        MemberListAdapter memberListAdapter = (MemberListAdapter)rvMemberList.getAdapter();
        if(memberListAdapter == null){
            rvMemberList.setAdapter(new MemberListAdapter(activity, memberList));
        }else {
            memberListAdapter.setmemberList(memberList);
            memberListAdapter.notifyDataSetChanged();
        }
    }

    private class MemberListAdapter extends RecyclerView.Adapter<MemberListViewHolder>{
        private LayoutInflater layoutInflater;
        private List<Member> memberList;
        private int imageSize;

        // 建立adapter建構子
        MemberListAdapter(Context context, List<Member> memberList){
            layoutInflater = LayoutInflater.from(context);
            this.memberList = memberList;
            imageSize = getResources().getDisplayMetrics().widthPixels / 3;
        }

        void setmemberList(List<Member> memberList) {
            this.memberList = memberList;
        }

        @Override
        public int getItemCount() {
            return memberList == null ? 0 : memberList.size();
        }

        @NonNull
        @Override
        public MemberListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // 載入要放在recycleView的itemView
            View itemView = layoutInflater.inflate(R.layout.item_group_mbrlist, parent, false);
            return new MemberListViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MemberListViewHolder friendsViewHolder, int position) {
            Member member = memberList.get(position);

            // 取得照片
            String url = Common.URL_SERVER + "MemberServlet";
            int memberId = member.getId();
            int imageSize = getResources().getDisplayMetrics().widthPixels / 3;
            Bitmap bitmap = null ;
            try {
                bitmap = new ImageTask(url, memberId, imageSize).execute().get();
            } catch (Exception e) {
                e.printStackTrace();
            }//若此帳號之資料庫有照片，便使用資料庫的照
            if (bitmap != null) {
                friendsViewHolder.civPic.setImageBitmap(bitmap);
            } else {
               friendsViewHolder.civPic.setImageResource(R.drawable.ic_nopicture);
            }

            friendsViewHolder.tvNickname.setText(member.getNickName());

        }
    }

    class MemberListViewHolder extends RecyclerView.ViewHolder {
        TextView tvNickname;
        CircleImageView civPic;
        public MemberListViewHolder(View v) {
            super(v);
            civPic = v.findViewById(R.id.ivMbrPic);
            tvNickname = v.findViewById(R.id.tvMbrName);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (groupMbrTask != null) {
            groupMbrTask.cancel(true);
            groupMbrTask = null;
        }
        if (hostTask != null) {
            hostTask.cancel(true);
            hostTask = null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Navigation.findNavController(this.getView()).popBackStack();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showHost(Member member) {
        String Url = Common.URL_SERVER + "MemberServlet";
        int id = member.getId();
        int imageSize = getResources().getDisplayMetrics().widthPixels / 4;
        Bitmap bitmap = null;
        try {
            bitmap = new ImageTask(Url, id, imageSize).execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (bitmap != null && bitmap.getByteCount() != 0) {
            ivHostPic.setImageBitmap(bitmap);
        } else {
            Log.d("image", R.drawable.ic_nopicture + "");
            //ivPhoto.setImageResource(R.drawable.ic_nopicture);
        }
    }
}