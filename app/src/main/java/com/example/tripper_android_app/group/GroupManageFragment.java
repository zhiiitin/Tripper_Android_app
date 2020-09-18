package com.example.tripper_android_app.group;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tripper_android_app.MainActivity;
import com.example.tripper_android_app.R;
import com.example.tripper_android_app.setting.member.Member;
import com.example.tripper_android_app.task.CommonTask;
import com.example.tripper_android_app.task.ImageTask;
import com.example.tripper_android_app.trip.TripGroup;
import com.example.tripper_android_app.trip.TripGroupMember;
import com.example.tripper_android_app.util.Common;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.content.Context.MODE_PRIVATE;

public class GroupManageFragment extends Fragment {
    private MainActivity activity;
    private ImageView ivPeoplePic;
    private TextView tvFriendName;
    private ImageButton ibKick;
    private SharedPreferences preferences;
    private TripGroup tripGroup;
    private List<TripGroupMember> tripGroupMembers;
    private TripGroupMember tripGroupMember;
    private CommonTask tripGroupGetIdTask;
    private CommonTask memberGetProfileTask;
    private RecyclerView rvTripGroupList;
    private List<ImageTask> imageTasks;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
        setHasOptionsMenu(true);
        imageTasks = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_group_manage, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = view.findViewById(R.id.groupManageToolbar);
        toolbar.setTitle("管理揪團人數");
        rvTripGroupList = view.findViewById(R.id.rvTripGroupList);
        rvTripGroupList.setLayoutManager(new LinearLayoutManager(activity));
        tvFriendName = view.findViewById(R.id.tvFriendName);
        ivPeoplePic = view.findViewById(R.id.ivPeoplePic);
        preferences = activity.getSharedPreferences("groupSetting", MODE_PRIVATE);
        // 取得trip_group參與人員ID與資料
        tripGroupMembers = getGroupList();
        showGroupList(tripGroupMembers);



    }
    // 呈現在管理揪團人員的資料
    private void showGroupList(List<TripGroupMember> tripGroupMembers) {
        if(tripGroupMembers == null || tripGroupMembers.isEmpty()){
            Common.showToast(activity, "尚未有參與揪團人員");
            return;
        }

        TripGroupAdapter tripGroupAdapter = (TripGroupAdapter) rvTripGroupList.getAdapter();
        if(tripGroupAdapter == null){
            Log.d("###", "tripGroupAdapter null 的情況");
            rvTripGroupList.setAdapter(new TripGroupAdapter(activity, tripGroupMembers));
        }else {
            Log.d("###", "tripGroupAdapter 已存在 的情況");
            tripGroupAdapter.tripGroupMembers = tripGroupMembers;
            tripGroupAdapter.notifyDataSetChanged();
        }
    }


    private List<TripGroupMember> getGroupList() {
        List<TripGroupMember> tripGroupMembers = null;
        if(Common.networkConnected(activity)){
            String tripId = preferences.getString("tripId", "noData");
            if(!tripId.equals("noData")){
                String url = Common.URL_SERVER + "Trip_Group_Servlet";
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("action", "findGroupTripId");
                jsonObject.addProperty("tripId", tripId);
                String jsonOut = jsonObject.toString();
                // TODO
                Log.d("#getGroupList jsonOut::", jsonOut);
                tripGroupGetIdTask = new CommonTask(url, jsonOut);
                try {
                    String jsonIn = tripGroupGetIdTask.execute().get();
                    Log.d("#getGroupList jsonOut::", jsonIn);
                    Type listtype = new TypeToken<List<TripGroupMember>>(){}.getType();
                    tripGroupMembers = new Gson().fromJson(jsonIn, listtype);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                Common.showToast(activity, "找不到該筆揪團資料");
            }
        } else {
            Common.showToast(activity, "請確認網路連線是否正常");
        }
        return tripGroupMembers;
    }

    private class TripGroupAdapter extends RecyclerView.Adapter<TripGroupAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        private List<TripGroupMember> tripGroupMembers;
        private int imageSize;

        TripGroupAdapter(Context context, List<TripGroupMember> tripGroupMembers){
            layoutInflater = LayoutInflater.from(context);
            this.tripGroupMembers = tripGroupMembers;
            imageSize = getResources().getDisplayMetrics().widthPixels / 4 ;
            Log.d("####","1");
            Log.d("### 1111 size", tripGroupMembers.size() + "");
        }

        @Override
        public int getItemCount() {
            Log.d("### size", tripGroupMembers.size() + "");
            return tripGroupMembers == null ? 0 : tripGroupMembers.size();
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.item_view_group_manage, parent, false);
            Log.d("####","2");
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int position) {
            final TripGroupMember tripGroupMember = tripGroupMembers.get(position);
            String nickName = tripGroupMember.getNickName();
            final int memberId = tripGroupMember.getMemberId();
            String url = Common.URL_SERVER + "MemberServlet";
            ImageTask imageTask = new ImageTask(url, memberId, imageSize, myViewHolder.ivPeoplePic);
            imageTask.execute();
            imageTasks.add(imageTask);
            myViewHolder.tvNickName.setText(nickName);
            myViewHolder.ibKick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = Common.URL_SERVER + "Trip_Group_Servlet";
                    // tripGroupDelete and tripId
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "tripGroupDelete");
                    jsonObject.addProperty("tripId", tripGroupMember.getTripId());
                    jsonObject.addProperty("memberId", tripGroupMember.getMemberId());
                    String jsonOut = jsonObject.toString();
                    CommonTask deleteTask = new CommonTask(url, jsonOut);
                    try {
                        String result = deleteTask.execute().get();
                        int count = Integer.parseInt(result);
                        if(count > 0){
                            tripGroupMembers.remove(position);
                            showGroupList(tripGroupMembers);
                            Common.showToast(activity, "刪除成功");
                        }else {
                            Common.showToast(activity, "刪除失敗");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        private class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView ivPeoplePic;
            TextView tvNickName;
            ImageButton ibKick;
            public MyViewHolder(View itemView) {
                super(itemView);
                ivPeoplePic = itemView.findViewById(R.id.ivPeoplePic);
                tvNickName = itemView.findViewById(R.id.tvFriendName);
                ibKick = itemView.findViewById(R.id.ibKick);
            }

        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(tripGroupGetIdTask != null){
            tripGroupGetIdTask.cancel(true);
            tripGroupGetIdTask = null;

        }

        if(imageTasks != null && imageTasks.size() > 0){
            for(ImageTask imageTask : imageTasks){
                imageTask.cancel(true);
            }
            imageTasks.clear();
        }
    }
}