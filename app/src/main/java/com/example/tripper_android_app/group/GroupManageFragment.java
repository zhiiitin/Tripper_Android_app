package com.example.tripper_android_app.group;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private final static String TAG = "TAG_GroupManage";
    private MainActivity activity;
    private SharedPreferences preferences;
    private String tripId ;
    private TripGroup tripGroup;
    private List<Member> tripGroupMembers;
    private TripGroupMember tripGroupMember;
    private CommonTask tripGroupGetIdTask,ApplicationMbrTask;
    private CommonTask memberGetProfileTask;
    private RecyclerView rvTripGroupList;
    private List<ImageTask> imageTasks;
    private Bundle bundle2 = new Bundle();
    private CardView cvApplication ;
    private ImageView ivRedCircle ;
    private TextView tvCount ;
    private List<Member> memberList;
    private  int count = 0 ;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
        setHasOptionsMenu(true);

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
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorForWhite));
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ivRedCircle = view.findViewById(R.id.ivRedCircle);
        tvCount = view.findViewById(R.id.tvMbrCount);

        //取得前頁bundle
        Bundle bundle = getArguments();
        tripId = bundle.getString("tripId");


        bundle2.putString("tripId",tripId);

        rvTripGroupList = view.findViewById(R.id.rvTripGroupList);
        rvTripGroupList.setLayoutManager(new LinearLayoutManager(activity));

        preferences = activity.getSharedPreferences("groupSetting", MODE_PRIVATE);
        // 取得trip_group參與人員ID與資料
        tripGroupMembers = getGroupList();
        showGroupList(tripGroupMembers);
        //申請名單按鈕
        cvApplication = view.findViewById(R.id.cvApplication);
        cvApplication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_groupManageFragment_to_groupManageApplicationFragment, bundle2);
            }
        });


        if(Common.networkConnected(activity)){
            memberList = new ArrayList<>();
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

        if(memberList != null && memberList.size() > 0){
            ivRedCircle.setVisibility(View.VISIBLE);
            tvCount.setText(memberList.size()+"");
        }
    }


    //toolbar 左上角返回按鈕
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Navigation.findNavController(this.getView()).popBackStack();
                return true;
            case R.id.btApplicationList:
                Navigation.findNavController(this.getView()).navigate(R.id.action_groupManageFragment_to_groupManageApplicationFragment, bundle2);
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //顯示申請名單按鈕
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
            inflater.inflate(R.menu.group_application_list, menu);
    }


    // 呈現在管理揪團人員的資料
    private void showGroupList(List<Member> tripGroupMembers) {
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


    private List<Member> getGroupList() {
        tripGroupMembers = new ArrayList<>();;
        if(Common.networkConnected(activity)){
            //String tripId = preferences.getString("tripId", "noData");

            if(!tripId.equals("noData")){
                String url = Common.URL_SERVER + "Trip_Group_Servlet";
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("action", "getGroupMbrList");
                jsonObject.addProperty("tripId", tripId);
                String jsonOut = jsonObject.toString();
                // TODO
                Log.d("#getGroupMbrList jsonOut::", jsonOut);
                tripGroupGetIdTask = new CommonTask(url, jsonOut);
                try {
                    String jsonIn = tripGroupGetIdTask.execute().get();
                    Log.d("#getGroupList jsonOut::", jsonIn);
                    Type listtype = new TypeToken<List<Member>>(){}.getType();
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
        private List<Member> tripGroupMembers;


        TripGroupAdapter(Context context, List<Member> tripGroupMembers){
            layoutInflater = LayoutInflater.from(context);
            this.tripGroupMembers = tripGroupMembers;
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
            final Member tripGroupMember = tripGroupMembers.get(position);
            int memberId = tripGroupMember.getId();
            String url = Common.URL_SERVER + "MemberServlet";
            int imageSize = getResources().getDisplayMetrics().widthPixels / 3;
            myViewHolder.tvNickName.setText(tripGroupMember.getNickName());
            Bitmap bitmap = null ;
            try {
                bitmap = new ImageTask(url, memberId, imageSize).execute().get();
            } catch (Exception e) {
                e.printStackTrace();
            }//若此帳號之資料庫有照片，便使用資料庫的照
            if (bitmap != null) {
                myViewHolder.ivPeoplePic.setImageBitmap(bitmap);
            } else {
                myViewHolder.ivPeoplePic.setImageResource(R.drawable.ic_nopicture);
            }
            //將成員踢除
            myViewHolder.ibKick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Common.networkConnected(activity)) {
                        String url = Common.URL_SERVER + "TripServlet";
                        //參加人的會員ID

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
                            tripGroupMembers.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position,tripGroupMembers.size());
                            Common.showToast(activity, "已將"+tripGroupMember.getNickName()+"踢除");

                        }
                    } else {
                        Common.showToast(activity, "請檢查網路連線");
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