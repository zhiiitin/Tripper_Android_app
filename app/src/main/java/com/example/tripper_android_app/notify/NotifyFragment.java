package com.example.tripper_android_app.notify;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.tripper_android_app.MainActivity;
import com.example.tripper_android_app.R;
import com.example.tripper_android_app.fcm.AppMessage;
import com.example.tripper_android_app.task.CommonTask;
import com.example.tripper_android_app.task.ImageTask;
import com.example.tripper_android_app.util.CircleImageView;
import com.example.tripper_android_app.util.Common;
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
    private final static int BLOG_TYPE = 1;
    private final static int GROUP_TYPE = 2;
    private final static int TRIP_TYPE = 3;
    private MainActivity activity;
    private RecyclerView rvNotifyList;
    private List<Notify> notifies = new ArrayList<>();
    private Gson gson = new Gson();
    private NotifyAdapter notifyAdapter;

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
        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottomBar);
        NavController navController = Navigation.findNavController(activity, R.id.notify_Fragment);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        Toolbar toolbar = view.findViewById(R.id.notifyToolbar);
        toolbar.setTitle("通知訊息");
        SharedPreferences pref = activity.getSharedPreferences(Common.PREF_FILE,
                MODE_PRIVATE);
        int memberId = Integer.parseInt(pref.getString("memberId",""));
        rvNotifyList = view.findViewById(R.id.rvNotifyList);
        rvNotifyList.setLayoutManager(new LinearLayoutManager(activity));
        // 取得該帳號所有通知訊息
        notifies = getAllNotify(memberId);
        showNotifies(notifies);

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
            return notifies == null ? 0 : notifies.size();
        }

        @Override
        public int getItemViewType(int position) {
            Notify notify = notifies.get(position);
            switch (notify.getMsgType()){
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
            switch (viewType){
                case FRIEND_TYPE:
                    itemView = layoutInflater.inflate(R.layout.item_view_friend_apply, parent, false);
                    return new NotifyViewHolder(itemView);
                case BLOG_TYPE:
                    break;
            }
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull NotifyViewHolder holder, int position) {
            Notify notify = notifies.get(position);

            // TODO 取得頭相照片
            if(holder.civPic != null ){
                if(Common.networkConnected(activity)){
                   String url = Common.URL_SERVER + "";
                }else {
                    Common.showToast(activity, "請確認網路連線狀態");
                }
            }

            holder.tvMsgBody.setText(notify.getMsgBody());
            holder.tvNotifyDateTime.setText(notify.getNotifyDateTime());

            // TODO 點擊同意or拒絕處理事件
            View.OnClickListener btClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            };

            holder.btApply.setOnClickListener(btClickListener);
            holder.btReject.setOnClickListener(btClickListener);
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




}