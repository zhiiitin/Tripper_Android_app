package com.example.tripper_android_app.notify;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tripper_android_app.MainActivity;
import com.example.tripper_android_app.R;
import com.example.tripper_android_app.fcm.AppMessage;
import com.example.tripper_android_app.friends.Friends;
import com.example.tripper_android_app.task.CommonTask;
import com.example.tripper_android_app.task.ImageTask;
import com.example.tripper_android_app.util.CircleImageView;
import com.example.tripper_android_app.util.Common;
import com.example.tripper_android_app.util.SendMessage;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.content.Context.MODE_PRIVATE;

public class NotifyFragment extends Fragment {
    private final static int FRIEND_TYPE = 0;
    private final static int NORMAL_MSG_TYPE = 4;
    private final static int BLOG_TYPE = 1;
    private final static int GROUP_TYPE = 2;
    private final static int TRIP_TYPE = 3;
    private MainActivity activity;
    private RecyclerView rvNotifyList;
    private List<Notify> notifies = new ArrayList<>();
    private List<ImageTask> imageTasks = new ArrayList<>();
    private Gson gson = new Gson();
    private NotifyAdapter notifyAdapter;
    private SharedPreferences pref = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notify, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = view.findViewById(R.id.notifyToolbar);
        activity.setSupportActionBar(toolbar);
        toolbar.setTitle("通知訊息");
        pref = activity.getSharedPreferences(Common.PREF_FILE,
                MODE_PRIVATE);
        int memberId = Integer.parseInt(pref.getString("memberId",""));
        rvNotifyList = view.findViewById(R.id.rvNotifyList);
        rvNotifyList.setLayoutManager(new LinearLayoutManager(activity));
        // 取得該帳號所有通知訊息
        notifies = getAllNotify(memberId);
        showNotifies(notifies);

        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottomBar);
        NavController navController = Navigation.findNavController(activity, R.id.notifyFragment);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        Menu itemMenu = bottomNavigationView.getMenu();
        itemMenu.getItem(3).setChecked(true);

    }

    private void showNotifies(List<Notify> notifies) {
        if(notifies == null || notifies.isEmpty()){
            return;
        }
        notifyAdapter = (NotifyAdapter) rvNotifyList.getAdapter();
        if(notifyAdapter == null) {
            rvNotifyList.setAdapter(new NotifyAdapter(activity, notifies));
        } else {
            notifyAdapter.setNotifies(notifies);
            notifyAdapter.notifyDataSetChanged();
        }

    }

    private List<Notify> getAllNotify(int memberId) {
        List<Notify> notifies = new ArrayList<>();
        if(Common.networkConnected(activity)){
            String url = Common.URL_SERVER + "FCMServlet";
            JsonObject jsonObject  = new JsonObject();
            jsonObject.addProperty("action", "getNotify");
            jsonObject.addProperty("memberId", memberId);
            String jsonOut = jsonObject.toString();
            System.out.println("");
            CommonTask getNotifyTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = getNotifyTask.execute().get();
                Type typelist = new TypeToken<List<Notify>>(){}.getType();
                notifies = gson.fromJson(jsonIn, typelist);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else {
            Common.showToast(activity, "請確認網路連線狀態");
        }
        return notifies;
    }

    private class NotifyAdapter extends RecyclerView.Adapter<NotifyAdapter.NotifyViewHolder>{
        private LayoutInflater layoutInflater;
        private List<Notify> notifies;
        private int imageSize;

        // 建構子
        NotifyAdapter(Context context, List<Notify> notifies){
            layoutInflater = LayoutInflater.from(context);
            this.notifies = notifies;
            imageSize = getResources().getDisplayMetrics().widthPixels / 4;
        }

        void setNotifies(List<Notify> notifies){
            this.notifies = notifies;
        }


        @Override
        public int getItemCount() {
            System.out.println("count::" + notifies.size());
            return notifies == null ? 0 : notifies.size();
        }

        @Override
        public int getItemViewType(int position) {
            // 取得訊息類型
            Notify notify = notifies.get(position);

            switch (notify.getMsgType()){
                case "N":
                    return NORMAL_MSG_TYPE;
                case "F":
                    return FRIEND_TYPE;
                case "B":
                    return BLOG_TYPE;
                case "G":
                    return GROUP_TYPE;
                case "T":
                    return TRIP_TYPE;
            }
            return super.getItemViewType(position);
        }

        @NonNull
        @Override
        public NotifyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = null;
            // 依message type取得不同的item

            if(viewType == FRIEND_TYPE) {
                itemView = layoutInflater.inflate(R.layout.item_view_friend_apply, parent, false);
                return new NotifyViewHolder(itemView);
            }else {
                itemView = layoutInflater.inflate(R.layout.item_view_normal_message, parent, false);
                return new NotifyViewHolder(itemView);
            }
//            switch (viewType){
//                case FRIEND_TYPE:
//                    itemView = layoutInflater.inflate(R.layout.item_view_friend_apply, parent, false);
//                    return new NotifyViewHolder(itemView);
//                case BLOG_TYPE:
//                    break;
//            }
//            return new NotifyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull NotifyViewHolder holder, int position) {
            final Notify notify = notifies.get(position);

            // 依寄送的人來取得頭像
            final int memberId = notify.getSendId();
            if(holder.civPic != null ){
                if(Common.networkConnected(activity)){
                   String url = Common.URL_SERVER + "MemberServlet";
                    ImageTask imageTask = new ImageTask(url, memberId, imageSize, holder.civPic);
                    imageTask.execute();
                    imageTasks.add(imageTask);
                }else {
                    Common.showToast(activity, "請確認網路連線狀態");
                }
            }

            holder.tvMsgBody.setText(notify.getMsgBody());
            holder.tvNotifyDateTime.setText(notify.getNotifyDateTime());

            // 點擊同意or拒絕處理事件
            View.OnClickListener btClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JsonObject jsonObject = new JsonObject();
                    String status = "";
                    switch (v.getId()){
                        case R.id.btApply:
                            // 當點選同意時，新增一筆資料且更改原申請資料的狀態碼
                            status = "apply";
                            // 新增成為好友的推播訊息for申請人
                            sendReplyMsg(notify.getReciverId(), notify.getSendId(), status);
                            break;
                        case R.id.btReject:
                            // 當點選拒絕時，刪除原交友的申請資料
                            status = "delete";
                            break;
                        default:
                            break;
                    }
                    jsonObject.addProperty("action", status);
                    jsonObject.addProperty("memberId", notify.getReciverId());
                    jsonObject.addProperty("friendId", notify.getSendId());
                    if(Common.networkConnected(activity)){
                        String url = Common.URL_SERVER + "FriendsServlet";
                        // 更改好友關係狀態 friends table
                        CommonTask updateFriendStatusTask = new CommonTask(url, jsonObject.toString());
                        try {
                            updateFriendStatusTask.execute().get();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Common.showToast(activity, "請確認網路連線狀態");
                    }

                    // 新增成為好友的通知訊息for自己
                    // 將好友邀請訊息狀態改為已讀
                    addSuccessMsg(memberId, notify);



                }
            };

            if(holder.btApply != null && holder.btReject != null){
                holder.btApply.setOnClickListener(btClickListener);
                holder.btReject.setOnClickListener(btClickListener);
            }

        }

        private class NotifyViewHolder extends RecyclerView.ViewHolder {
            TextView tvMsgBody, tvNotifyDateTime;
            Button btApply, btReject;
            CircleImageView civPic;
            public NotifyViewHolder(@NonNull View v) {
                super(v);
                tvMsgBody = v.findViewById(R.id.tvMsgBody);
                tvNotifyDateTime = v.findViewById(R.id.tvNotifyDateTime);
                btApply = v.findViewById(R.id.btApply);
                btReject = v.findViewById(R.id.btReject);
                civPic = v.findViewById(R.id.civPic);
            }
        }
    }

    // 傳送回覆交友狀態訊息
    private void sendReplyMsg(int memberId, int friendId, String reply) {
        if (reply.equals("apply")) {
            AppMessage message = null;
            String account = pref.getString("account","");
            // msg_type, member_id, msg_title, msg_body, msg_stat, send_id, reciver_id
            String msgType = Common.NORMAL_MSG_TYPE;
            String title =  "好友同意通知";
            String body = account + "已同意加您為好友！";
            int stat = 0;

            message = new AppMessage(msgType, memberId, title, body, stat, memberId, friendId);
            SendMessage sendMessage = new SendMessage(activity, message);
            boolean isSendOk = sendMessage.sendMessage();
            if(isSendOk){
                Common.showToast(activity, "已發出好友邀請!");
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!Common.isLogin(activity)) {
            Navigation.findNavController(this.getView()).navigate(R.id.action_notifyFragment_to_register_main_Fragment);
            Common.showToast(activity,"請先登入會員");
        }
    }

    // 新增好友成功
    private void addSuccessMsg(int memberId, Notify notify) {
        AppMessage message = null;
        // msg_type, member_id, msg_title, msg_body, msg_stat, send_id, reciver_id
        String msgType = Common.NORMAL_MSG_TYPE;
        String title =  "好友新增成功";
        String body = "您已與"+ notify.getNickname() +"成為好友！";
        int stat = 0;
        // 發給自己的通知
        message = new AppMessage(msgType, memberId, title, body, stat, memberId, memberId);
        JsonObject jsonObject = new JsonObject();
        if(Common.networkConnected(activity)){
            String url = Common.URL_SERVER + "FCMServlet";
            jsonObject.addProperty("action","add");
            jsonObject.addProperty("appMessage", new Gson().toJson(message));
            // 塞資料至後
            CommonTask task = new CommonTask(url, jsonObject.toString());
            try {
                task.execute().get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else {
            Common.showToast(activity, "請確認網路連線狀態");
        }


    }

}