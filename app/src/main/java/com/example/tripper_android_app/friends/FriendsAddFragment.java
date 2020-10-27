package com.example.tripper_android_app.friends;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.tripper_android_app.MainActivity;
import com.example.tripper_android_app.R;
import com.example.tripper_android_app.fcm.AppMessage;
import com.example.tripper_android_app.task.CommonTask;
import com.example.tripper_android_app.task.ImageTask;
import com.example.tripper_android_app.util.CircleImageView;
import com.example.tripper_android_app.util.Common;
import com.example.tripper_android_app.util.SendMessage;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.concurrent.ExecutionException;

import static android.content.Context.MODE_PRIVATE;

public class FriendsAddFragment extends Fragment {
    private final static String TAG = "TAG_FriendsAdd";
    private final static int NOT_FRIEND = 0;
    private final static int CHECKING = 1;
    private final static int FRIEND = 2;
    private MainActivity activity;
    private EditText etSearch;
    private TextView tvNickname;
    private CircleImageView civPic;
    private Friends friend = null;
    private CardView cardView;
    private Button btAddFriend;
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
        return inflater.inflate(R.layout.fragment_friends_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cardView = view.findViewById(R.id.cvPerson);
        cardView.setVisibility(View.INVISIBLE);
        ImageButton ibSearch = view.findViewById(R.id.ibSearch);
        etSearch = view.findViewById(R.id.etSearch);
        tvNickname = view.findViewById(R.id.tvNickname);
        civPic = view.findViewById(R.id.civPic);
        btAddFriend = view.findViewById(R.id.btAddFriend);
        //toolbar設定
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("新增好友");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorForWhite));
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        pref = activity.getSharedPreferences(Common.PREF_FILE,
                MODE_PRIVATE);


        View.OnClickListener buttonClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int memberId = Integer.parseInt(pref.getString("memberId","0"));
                if(Common.networkConnected(activity)){
                    switch (v.getId()){
                        case R.id.ibSearch:
                            String url = Common.URL_SERVER + "FriendsServlet";
                            String account = etSearch.getText().toString();

                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("action", "search");
                            jsonObject.addProperty("memberId", memberId);
                            jsonObject.addProperty("account", account.toUpperCase());
                            String jsonOut = jsonObject.toString();
                            Log.d(TAG, "friend search jsonOut::" + jsonOut);
                            CommonTask getSearchDataTask = new CommonTask(url, jsonOut);
                            try {
                                String jsonIn = getSearchDataTask.execute().get();
                                friend = new Gson().fromJson(jsonIn, Friends.class);
                                if(friend == null){
                                    Common.showToast(activity, "搜尋不到該帳號資訊");
                                    cardView.setVisibility(View.INVISIBLE);
                                    break;
                                }
                                showSearchResult();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            break;
                        case R.id.btAddFriend:
                            // 新增好友
                            int friendId = friend.getId();
                            url = Common.URL_SERVER + "FriendsServlet";
                            jsonObject = new JsonObject();
                            jsonObject.addProperty("action", "insert");
                            jsonObject.addProperty("memberId", memberId);
                            jsonObject.addProperty("friendId", friendId);
                            jsonOut = jsonObject.toString();
                            CommonTask insertTask = new CommonTask(url, jsonOut);
                            try {
                                String result = insertTask.execute().get();
                                int count = Integer.parseInt(result);
                                if( count > 0 ){
                                    btAddFriend.setText("等待確認");
                                    btAddFriend.setClickable(false);
                                    btAddFriend.setBackgroundColor(R.drawable.button_checking);
                                    sendAddFriendMsg(friend, memberId);
                                    break;
                                }
                                Common.showToast(activity, "邀請失敗!");
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            break;

                    }

                }else {
                    Common.showToast(activity, "請確認網路連線狀態");
                }
            }
        };
        ibSearch.setOnClickListener(buttonClick);
        btAddFriend.setOnClickListener(buttonClick);

    }

    private void sendAddFriendMsg(Friends friends, int memberId) {
        AppMessage message = null;
        String account = pref.getString("account","");
        // msg_type, member_id, msg_title, msg_body, msg_stat, send_id, reciver_id
        String msgType = Common.FRIEND_TYPE;
        String title =  "申請好友通知";
        String body = account + "申請加您為好友！";
        int stat = 0;
        int sendId = memberId;
        int recId = friends.getId();
        message = new AppMessage(msgType, memberId, title, body, stat, sendId, recId);
        SendMessage sendMessage = new SendMessage(activity, message);
        boolean isSendOk = sendMessage.sendMessage();
        if(isSendOk){
            Common.showToast(activity, "已發出好友邀請!");
        }
    }

    private void showSearchResult() {
        cardView.setVisibility(View.VISIBLE);
        tvNickname.setText(friend.getNickName());
        getFriendPic();
        // 處理button上的文字及是否顯示可以點擊
        Log.d("###", friend.getStatus() + "");
        switch (friend.getStatus()){
            case NOT_FRIEND:
                btAddFriend.setText("加入好友");
                btAddFriend.setClickable(true);
                btAddFriend.setBackgroundColor(R.drawable.button_setting);
                break;
            case CHECKING:
                btAddFriend.setText("等待確認");
                btAddFriend.setClickable(false);
                btAddFriend.setBackgroundColor(R.drawable.button_checking);
                break;
            case FRIEND:
                btAddFriend.setVisibility(View.INVISIBLE);
                break;
        }

    }

    private void getFriendPic() {
        if(Common.networkConnected(activity)){
            String url = Common.URL_SERVER + "MemberServlet";
            int imageSize = activity.getResources().getDisplayMetrics().widthPixels / 3;
            int friendId = friend.getId();
            ImageTask imageTask = new ImageTask(url, friendId, imageSize, civPic);
            imageTask.execute();
        }else {
            Common.showToast(activity, "請確認網路狀態");
        }

    }

    //toolbar 左上角返回按鈕
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Navigation.findNavController(getView()).popBackStack();
                return true;
            default:
                break;
        }
        Navigation.findNavController(getView()).popBackStack();
        return super.onOptionsItemSelected(item);
    }

}