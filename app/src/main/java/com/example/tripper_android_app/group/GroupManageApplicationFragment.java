package com.example.tripper_android_app.group;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.tripper_android_app.MainActivity;
import com.example.tripper_android_app.R;
import com.example.tripper_android_app.fcm.AppMessage;
import com.example.tripper_android_app.setting.member.Member;
import com.example.tripper_android_app.task.CommonTask;
import com.example.tripper_android_app.task.ImageTask;
import com.example.tripper_android_app.trip.TripGroup;
import com.example.tripper_android_app.util.CircleImageView;
import com.example.tripper_android_app.util.Common;
import com.example.tripper_android_app.util.SendMessage;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.content.Context.MODE_PRIVATE;


public class GroupManageApplicationFragment extends Fragment {
    private final static String TAG = "TAG_GroupApplication";
    private MainActivity activity;
    private RecyclerView rvApplication;
    private List<Member> memberList;
    private String tripId ,tripName;
    private CommonTask ApplicationMbrTask;
    SharedPreferences pref = null;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_group_manage_application, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        if(bundle != null) {
            tripId = bundle.getString("tripId");
            tripName = bundle.getString("tripName");
        }

        pref = activity.getSharedPreferences(Common.PREF_FILE,
                MODE_PRIVATE);

//ToolBar
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("申請名單");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorForWhite));
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Drawable upArrow = ContextCompat.getDrawable(activity, R.drawable.abc_ic_ab_back_material);
        if (upArrow != null) {
            upArrow.setColorFilter(ContextCompat.getColor(activity, R.color.colorForWhite), PorterDuff.Mode.SRC_ATOP);
            activity.getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }

        rvApplication = view.findViewById(R.id.rvApplication);
        rvApplication.setLayoutManager(new LinearLayoutManager(activity));

        memberList = getMemberList();
        showMemberList(memberList);

    }

    private List<Member> getMemberList() {
        memberList = new ArrayList<>();
        if(Common.networkConnected(activity)){
            String url = Common.URL_SERVER + "Trip_Group_Servlet";

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getApplicationList");
            jsonObject.addProperty("tripId", tripId);
            String jsonOut = jsonObject.toString();
            Log.d(TAG, "getApplicationList jsonOut:: " + jsonOut);
            ApplicationMbrTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = ApplicationMbrTask.execute().get();
                Log.d(TAG, "getApplicationList jsonIn::" + jsonIn);
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
        ApplicationListAdapter applicationListAdapter = (ApplicationListAdapter)rvApplication.getAdapter();
        if(applicationListAdapter == null){
            rvApplication.setAdapter(new  ApplicationListAdapter(activity, memberList));
        }else {
            applicationListAdapter.setmemberList(memberList);
            applicationListAdapter.notifyDataSetChanged();
        }
    }
    private class ApplicationListAdapter extends RecyclerView.Adapter<ApplicationListViewHolder>{
        private LayoutInflater layoutInflater;
        private List<Member> memberList;
        private int imageSize;

        // 建立adapter建構子
        ApplicationListAdapter(Context context, List<Member> memberList){
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
        public ApplicationListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // 載入要放在recycleView的itemView
            View itemView = layoutInflater.inflate(R.layout.item_view_group_application, parent, false);
            return new ApplicationListViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ApplicationListViewHolder applicationListViewHolder, int position) {
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
                applicationListViewHolder.civPic.setImageBitmap(bitmap);
            } else {
                applicationListViewHolder.civPic.setImageResource(R.drawable.ic_nopicture);
            }
            applicationListViewHolder.tvNickname.setText(member.getNickName());
//按下同意按鈕
            applicationListViewHolder.ibAgree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Common.networkConnected(activity)) {
                        String url = Common.URL_SERVER + "TripServlet";

                        TripGroup tripGroup = new TripGroup(tripId, memberId);

                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("action", "agreeJoinGroup");
                        jsonObject.addProperty("tripGroup", new Gson().toJson(tripGroup));
                        int count = 0;
                        try {
                            String result = new CommonTask(url, jsonObject.toString()).execute().get();
                            count = Integer.parseInt(result);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (count == 1) {
                            //確認及拒絕按鈕消失，顯示已參加
                            applicationListViewHolder.ibAgree.setVisibility(View.GONE);
                            applicationListViewHolder.ibDisagree.setVisibility(View.GONE);
                            applicationListViewHolder.ibHasJoin.setVisibility(View.VISIBLE);
                            sendAccessMsg(memberId);
                        }
                    } else {
                        Common.showToast(activity, "請檢查網路連線");
                    }
                }
            });

//按下拒絕按鈕
            applicationListViewHolder.ibDisagree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Common.networkConnected(activity)) {
                        String url = Common.URL_SERVER + "TripServlet";

                        TripGroup tripGroup = new TripGroup(tripId, memberId);

                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("action", "deleteGroup");
                        jsonObject.addProperty("tripGroup", new Gson().toJson(tripGroup));
                        int count = 0;
                        try {
                            String result = new CommonTask(url, jsonObject.toString()).execute().get();
                            count = Integer.parseInt(result);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (count == 1) {
                            memberList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position,memberList.size());
                        }
                    } else {
                        Common.showToast(activity, "請檢查網路連線");
                    }
                }
            });

        }
    }

    class ApplicationListViewHolder extends RecyclerView.ViewHolder {
        TextView tvNickname;
        CircleImageView civPic;
        ImageButton ibAgree , ibDisagree ,ibHasJoin;
        public ApplicationListViewHolder(View v) {
            super(v);
            civPic = v.findViewById(R.id.ivPeoplePic);
            tvNickname = v.findViewById(R.id.tvFriendName);
            ibAgree = v.findViewById(R.id.ibAgree);
            ibDisagree = v.findViewById(R.id.ibDisagree);
            ibHasJoin = v.findViewById(R.id.ibHasJoin);
        }
    }

    //toolbar 左上角返回按鈕
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

    @Override
    public void onStop() {
        super.onStop();
        if(ApplicationMbrTask != null){
            ApplicationMbrTask.cancel(true);
            ApplicationMbrTask = null;
        }

    }
//通知該成員已通過申請
    private void sendAccessMsg(int recId){

        AppMessage message = null;
        String msgType = Common.GROUP_TYPE ;
        String memberIdStr = pref.getString("memberId",null);
        int memberId = Integer.parseInt(memberIdStr);
        String title =  "揪團通知";
        String body =  "你已成功加入「" + tripName + "」的揪團！";
        int stat = 0;
        int sendId = memberId ;
        message = new AppMessage(msgType , memberId , title , body ,stat , sendId , recId);
        SendMessage sendMessage = new SendMessage(activity, message);
        sendMessage.sendMessage();
    }
}