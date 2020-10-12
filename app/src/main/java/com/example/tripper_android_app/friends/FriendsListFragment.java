package com.example.tripper_android_app.friends;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;


import com.example.tripper_android_app.MainActivity;
import com.example.tripper_android_app.R;
import com.example.tripper_android_app.setting.member.Member;
import com.example.tripper_android_app.task.CommonTask;
import com.example.tripper_android_app.task.ImageTask;
import com.example.tripper_android_app.util.CircleImageView;
import com.example.tripper_android_app.util.Common;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.content.Context.MODE_PRIVATE;

public class FriendsListFragment extends Fragment {
    private final static String TAG = "TAG_FriendsList";
    private MainActivity activity;
    private RecyclerView rvFriendsList;
    private TextView tvNickname;
    private CircleImageView civPic;
    private SwipeRefreshLayout swipe;
    private List<Member> friends;
    private List<ImageTask> imageTasks;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friends_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = view.findViewById(R.id.friendsListToolbar);
        toolbar.setTitle("好友管理");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorForWhite));
        activity.setSupportActionBar(toolbar);
        // 先判斷是否已登入
        if(!Common.isLogin(activity)){
            Common.showToast(activity, "請先進行登入");
            return;
        }

        rvFriendsList = view.findViewById(R.id.rvFriendsList);
        rvFriendsList.setLayoutManager(new LinearLayoutManager(activity));
        friends = new ArrayList<>();
        friends = getFriendsList();
        showFriendsList(friends);

        // 刷新好友列表
        swipe = view.findViewById(R.id.swipeRefreshLayout);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe.setRefreshing(true);
                showFriendsList(friends);
                swipe.setRefreshing(false);
            }
        });

        // 搜尋好友
        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.isEmpty()){
                    showFriendsList(friends);
                }else {
                    List<Member> searchFriends = new ArrayList<>();
                    for(Member friend: friends) {
                        if(friend.getAccount().toUpperCase().contains(newText.toUpperCase())
                        || friend.getNickName().toUpperCase().contains(newText.toUpperCase())){
                            searchFriends.add(friend);
                        }
                    }
                    showFriendsList(searchFriends);
                }
                return true;
            }
        });

        // 新增好友
        FloatingActionButton btEnterAddFriend = view.findViewById(R.id.btEnterAddFriend);
        btEnterAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v)
                        .navigate(R.id.action_friendsListFragment_to_friendsAddFragment);
            }
        });

        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottomBar);
        NavController navController = Navigation.findNavController(activity, R.id.setting_Fragment);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        Menu itemMenu = bottomNavigationView.getMenu();
        itemMenu.getItem(4).setChecked(true);
    }

    private List<Member> getFriendsList() {
        List<Member> friends = new ArrayList<>();
        if(Common.networkConnected(activity)){
            String url = Common.URL_SERVER + "FriendsServlet";
            SharedPreferences pref = activity.getSharedPreferences(Common.PREF_FILE,
                    MODE_PRIVATE);
            int memberId = Integer.parseInt(pref.getString("memberId","0"));
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAll");
            jsonObject.addProperty("memberId", memberId);
            String jsonOut = jsonObject.toString();
            Log.d(TAG, "getFriendsList jsonOut:: " + jsonOut);
            CommonTask getFriendsListTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = getFriendsListTask.execute().get();
                Log.d(TAG, "getFriendsList jsonIn::" + jsonIn);
                Type listType = new TypeToken<List<Member>>(){}.getType();
                friends = new Gson().fromJson(jsonIn, listType);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

        }else {
            Common.showToast(activity, "請確認網路連線");
        }
        return friends;
    }

    private void showFriendsList(List<Member> friends) {
        if(friends == null || friends.isEmpty()){
            //Common.showToast(activity, "目前尚未有好友");
            return;
        }
        FriendsAdapter friendsAdapter = (FriendsAdapter)rvFriendsList.getAdapter();
        if(friendsAdapter == null){
           rvFriendsList.setAdapter(new FriendsAdapter(activity, friends));
        }else {
            friendsAdapter.setFriends(friends);
            friendsAdapter.notifyDataSetChanged();
        }
    }

    private class FriendsAdapter extends RecyclerView.Adapter<FriendsViewHolder>{
        private LayoutInflater layoutInflater;
        private List<Member> friends;
        private int imageSize;

        // 建立adapter建構子
        FriendsAdapter(Context context, List<Member> friends){
            layoutInflater = LayoutInflater.from(context);
            this.friends = friends;
            imageSize = getResources().getDisplayMetrics().widthPixels / 3;
        }

        void setFriends(List<Member> friends) {
            this.friends = friends;
        }

        @Override
        public int getItemCount() {
            return friends == null ? 0 : friends.size();
        }

        @NonNull
        @Override
        public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // 載入要放在recycleView的itemView
            View itemView = layoutInflater.inflate(R.layout.item_view_friends_list, parent, false);
            return new FriendsViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull FriendsViewHolder friendsViewHolder, int position) {
            Member friend = friends.get(position);
            imageTasks = new ArrayList<>();
            // 取得照片
            String url = Common.URL_SERVER + "MemberServlet";
            int memberId = friend.getId();
            ImageTask imageTask = new ImageTask(url, memberId, imageSize, friendsViewHolder.civPic);
            imageTask.execute();
            imageTasks.add(imageTask);
            friendsViewHolder.tvNickname.setText(friend.getNickName());
            // TODO 點擊好友careView 進到聊天畫面
        }
    }

    class FriendsViewHolder extends RecyclerView.ViewHolder {
        TextView tvNickname;
        CircleImageView civPic;
        public FriendsViewHolder(View v) {
            super(v);
            civPic = v.findViewById(R.id.ivUserPic);
            tvNickname = v.findViewById(R.id.tvFriendNickName);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(imageTasks != null && imageTasks.size() > 0){
            for(ImageTask imageTask : imageTasks){
                imageTask.cancel(true);
            }
            imageTasks.clear();
        }
    }
}