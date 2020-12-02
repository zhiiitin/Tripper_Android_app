package com.example.tripper_android_app.chat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tripper_android_app.MainActivity;
import com.example.tripper_android_app.R;
import com.example.tripper_android_app.fcm.AppMessage;
import com.example.tripper_android_app.friends.Friends;
import com.example.tripper_android_app.notify.Notify;
import com.example.tripper_android_app.notify.NotifyFragment;
import com.example.tripper_android_app.task.CommonTask;
import com.example.tripper_android_app.task.ImageTask;
import com.example.tripper_android_app.util.CircleImageView;
import com.example.tripper_android_app.util.Common;
import com.example.tripper_android_app.util.SendMessage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.yalantis.ucrop.UCrop;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static androidx.navigation.Navigation.findNavController;

public class ChatMainFragment extends Fragment {
    private String revieverName;
    private static final String TAG = "TAG_ChatFragment";
    private EditText messageEdit;
    private RecyclerView recyclerView;
    private ImageView sendBtn, photoBtn;
    private MainActivity activity;
    SharedPreferences pref = null;
    private List<Notify> messagess = new ArrayList<>();
    private MessageAdapter messageAdapter;
    private Gson gson = new Gson();
    private LinearLayoutManager linearLayoutManager;
    private MessageDelegate messageDelegate = MessageDelegate.getInstance();
    private int memberId = 0;
    private MessageDelegate.OnMessageReceiveListener listener;
    private FirebaseUser mUser;
    private FirebaseAuth auth;
    private Integer recieverId = 0;
    private Uri contentUri;
    private byte[] photo;
    private static final int REQ_TAKE_PICTURE = 0;
    private static final int REQ_PICK_PICTURE = 1;
    private static final int REQ_CROP_PICTURE = 2;

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

        return inflater.inflate(R.layout.fragment_chat_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mUser = auth.getCurrentUser();
        Bundle bundle = getArguments();
        if (bundle != null) {
            recieverId = bundle.getInt("recieverId");
            revieverName = bundle.getString("recirverName");
        } else {
            recieverId = Common.sendId;
            revieverName = Common.chatSenderName;
        }
//
        Log.d("### Chatfragment :: ", "recieverId : " + recieverId + "\t recieverName : " + revieverName);
        //ToolBar
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle(revieverName);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorForWhite));
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Drawable upArrow = ContextCompat.getDrawable(activity, R.drawable.abc_ic_ab_back_material);
        if (upArrow != null) {
            upArrow.setColorFilter(ContextCompat.getColor(activity, R.color.colorForWhite), PorterDuff.Mode.SRC_ATOP);
            activity.getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }

        messageEdit = view.findViewById(R.id.messageEdit);
        sendBtn = view.findViewById(R.id.sendBtn);
        photoBtn = view.findViewById(R.id.photoBtn);
        recyclerView = view.findViewById(R.id.recyclerView);
        MessageAdapter messageAdapter = new MessageAdapter(activity, messagess);
        recyclerView.setAdapter(messageAdapter);
        linearLayoutManager = new LinearLayoutManager(activity);
//        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        pref = activity.getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
        memberId = Integer.parseInt(pref.getString("memberId", null));

        //取得該聊天室內容
        messagess = getAllMessagess(memberId, recieverId);
        showChat(messagess);

        if (messagess.size() != 0) {
            recyclerView.scrollToPosition(messagess.size() - 1);
        }

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int memberId = Integer.parseInt(pref.getString("memberId", "0"));
                String msg = messageEdit.getText().toString();
                if (!msg.equals("")) {
                    sendMessage(memberId);
                    recyclerView.scrollToPosition(messagess.size() - 1);
                } else {
                    Toast.makeText(activity, "請輸入訊息", Toast.LENGTH_SHORT).show();
                }
                messageEdit.setText("");
            }
        });

        photoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTypeDialog();
            }
        });
    }


    public class MessageAdapter extends RecyclerView.Adapter {
        private final int TYPE_MESSAGE_SENT = 0;
        private final int TYPE_MESSAGE_RECEIVED = 1;
        private final int TYPE_PHOTO_SENT = 2;
        private final int TYPE_PHOTO_RECEIVED = 3;

        private Context context;
        private List<Notify> messages;

        public MessageAdapter(Context context, List<Notify> messages) {
            this.messages = messages;
            this.context = context;

        }

        public void addItem() {

            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case TYPE_MESSAGE_SENT:
                    view = LayoutInflater.from(context).inflate(R.layout.item_send_message, parent, false);
                    return new SentMessageHolder(view);
                case TYPE_MESSAGE_RECEIVED:
                    view = LayoutInflater.from(context).inflate(R.layout.item_received_message, parent, false);
                    return new ReceivedMessageHolder(view);
                case TYPE_PHOTO_SENT:
                    view = LayoutInflater.from(context).inflate(R.layout.item_send_photo, parent, false);
                    return new SentPhotoHolder(view);
                case TYPE_PHOTO_RECEIVED:
                    view = LayoutInflater.from(context).inflate(R.layout.item_received_photo, parent, false);
                    return new ReceivedPhotoHolder(view);
            }

            return null;
        }

        void setMessagess(List<Notify> messages) {
            this.messages = messages;
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return messages == null ? 0 : (messages.size());
        }

        //發送訊息的ViewHolder
        private class SentMessageHolder extends RecyclerView.ViewHolder {

            TextView messageTxt, tvUptime;

            public SentMessageHolder(@NonNull View itemView) {
                super(itemView);
                messageTxt = itemView.findViewById(R.id.tvSent);
                tvUptime = itemView.findViewById(R.id.tvUptime);
            }
        }

        //發送照片的ViewHolder
        private class SentPhotoHolder extends RecyclerView.ViewHolder {
            ImageView ivSend;
            TextView tvUptime;

            public SentPhotoHolder(@NonNull View itemView) {
                super(itemView);
                ivSend = itemView.findViewById(R.id.ivSend);
                tvUptime = itemView.findViewById(R.id.tvUptime);
            }
        }

        //接收訊息的ViewHolder
        private class ReceivedMessageHolder extends RecyclerView.ViewHolder {

            TextView messageTxt, tvUptime;
            CircleImageView revievePhoto;

            public ReceivedMessageHolder(@NonNull View itemView) {
                super(itemView);
                revievePhoto = itemView.findViewById(R.id.ivPhoto);
                messageTxt = itemView.findViewById(R.id.tvReceived);
                tvUptime = itemView.findViewById(R.id.tvUptime);
            }
        }

        //接收照片的ViewHolder
        private class ReceivedPhotoHolder extends RecyclerView.ViewHolder {

            TextView tvUptime;
            CircleImageView revievePhoto;
            ImageView ivReceived;

            public ReceivedPhotoHolder(@NonNull View itemView) {
                super(itemView);
                revievePhoto = itemView.findViewById(R.id.ivPhoto);
                tvUptime = itemView.findViewById(R.id.tvUptime);
                ivReceived = itemView.findViewById(R.id.ivReceived);
            }
        }

        @Override
        public int getItemViewType(int position) {
            int memberId = Integer.parseInt(pref.getString("memberId", "0"));

            //本機端使用者的發言 顯示在右邊
            if (messages.get(position).getSendId() == memberId) {
                if (messages.get(position).getMsgType().equals("PHOTO")) {
                    return TYPE_PHOTO_SENT;
                } else {
                    return TYPE_MESSAGE_SENT;
                }
            } else {
                if (messages.get(position).getMsgType().equals("PHOTO")) {
                    return TYPE_PHOTO_RECEIVED;
                } else {
                    return TYPE_MESSAGE_RECEIVED;
                }
            }

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            int memberId = Integer.parseInt(pref.getString("memberId", "0"));

            Notify message = messages.get(position);
            try {
                if (message.getSendId() == memberId) {
                    //發送方 傳圖片
                    if (message.getMsgType().equals("PHOTO")) {
                        SentPhotoHolder photoHolder = (SentPhotoHolder) holder;
                        photoHolder.tvUptime.setText(message.getNotifyDateTime());
                        byte[] photo = Base64.decode(message.getPhoto(), Base64.DEFAULT);
                        Glide.with(activity).load(photo).into(photoHolder.ivSend);
                        //點擊圖片放大
                        photoHolder.ivSend.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                                View view = getLayoutInflater().inflate(R.layout.dialog_imageview, null);
                                alertDialog.setView(view);
                                ImageView ivPhoto = view.findViewById(R.id.ivPhoto);

                                Glide.with(activity).load(photo).into(ivPhoto);
                                //將白色部分設為透明
                                alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                alertDialog.setCancelable(true);
                                alertDialog.show();
                            }
                        });
                    } else {
                    //發送方 傳文字訊息
                        SentMessageHolder messageHolder = (SentMessageHolder) holder;
                        messageHolder.messageTxt.setText(message.getMsgBody());
                        messageHolder.tvUptime.setText(message.getNotifyDateTime());
                    }
                } else {
                    //接收方 抓圖片
                    if (message.getMsgType().equals("PHOTO")) {
                        ReceivedPhotoHolder photoHolder = (ReceivedPhotoHolder) holder;

                        int id = recieverId;
                        Bitmap bitmap = null;
                        String url = Common.URL_SERVER + "MemberServlet";
                        int imageSize = getResources().getDisplayMetrics().widthPixels / 3;
                        try {
                            bitmap = new ImageTask(url, id, imageSize).execute().get();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //若此帳號之資料庫有照片，便使用資料庫的照
                        if (bitmap != null) {
                            photoHolder.revievePhoto.setImageBitmap(bitmap);
                        } else {
                            //否則連接到第三方大頭照
                            String fbPhotoURL = mUser.getPhotoUrl().toString();
                            Glide.with(activity).load(fbPhotoURL).into(photoHolder.revievePhoto);
                        }
                        photoHolder.tvUptime.setText(message.getNotifyDateTime());
                        byte[] photo = Base64.decode(message.getPhoto(), Base64.DEFAULT);
                        Glide.with(activity).load(photo).into(photoHolder.ivReceived);
                        //點擊圖片放大
                        photoHolder.ivReceived.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                                View view = getLayoutInflater().inflate(R.layout.dialog_imageview, null);
                                alertDialog.setView(view);
                                ImageView ivPhoto = view.findViewById(R.id.ivPhoto);

                                Glide.with(activity).load(photo).into(ivPhoto);
                                //將白色部分設為透明
                                alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                alertDialog.setCancelable(true);
                                alertDialog.show();
                            }
                        });
   
                    } else {
                        //接收方 抓文字訊息
                        ReceivedMessageHolder messageHolder = (ReceivedMessageHolder) holder;
//取得大頭貼                    int id = member.getId();
                        int id = recieverId;
                        Bitmap bitmap = null;
                        String url = Common.URL_SERVER + "MemberServlet";
                        int imageSize = getResources().getDisplayMetrics().widthPixels / 3;
                        try {
                            bitmap = new ImageTask(url, id, imageSize).execute().get();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //若此帳號之資料庫有照片，便使用資料庫的照
                        if (bitmap != null) {
                            messageHolder.revievePhoto.setImageBitmap(bitmap);
                        } else {
                            //否則連接到第三方大頭照
                            String fbPhotoURL = mUser.getPhotoUrl().toString();
                            Glide.with(activity).load(fbPhotoURL).into(messageHolder.revievePhoto);
                        }
                        messageHolder.messageTxt.setText(message.getMsgBody());
                        messageHolder.tvUptime.setText(message.getNotifyDateTime());

//                        ReceivedImageHolder imageHolder = (ReceivedImageHolder) holder;
//                        imageHolder.nameTxt.setText(message.getString("name"));
//
//                        Bitmap bitmap = getBitmapFromString(message.getString("image"));
//                        imageHolder.imageView.setImageBitmap(bitmap);
                    }
                }

            } catch (
                    Exception e) {
                e.printStackTrace();
            }
        }

    }


    public void sendMessage(int memberid) {
        AppMessage message = null;

        String msgType = Common.SEND_MESSEAGE_TYPE;
        String title = "";
        String body = messageEdit.getText().toString().trim();
        int stat = 0;
        int sendId = memberid;


        //取得"台北時區"時間
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Taipei"));
        Calendar calendar = Calendar.getInstance();
        String uptime = simpleDateFormat.format(calendar.getTime());

        message = new AppMessage(msgType, memberid, title, body, stat, sendId, recieverId, uptime);
        SendMessage sendMessage = new SendMessage(activity, message);
        sendMessage.sendChatMessage();

        messageAdapter = (MessageAdapter) recyclerView.getAdapter();

        messagess = getAllMessagess(memberid, recieverId);
        showChat(messagess);


    }


    private void showChat(List<Notify> messages) {
        if (messages == null || messages.isEmpty()) {
            return;
        }
        messageAdapter = (MessageAdapter) recyclerView.getAdapter();
        if (messageAdapter == null) {
            recyclerView.setAdapter(new MessageAdapter(activity, messages));
        } else {
            messageAdapter.setMessagess(messages);
            messageAdapter.notifyDataSetChanged();
        }
    }

    private List<Notify> getAllMessagess(int memberId, int recieverId) {
        List<Notify> notifies = new ArrayList<>();
        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "FCMServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getChatMsg");
            jsonObject.addProperty("memberId", memberId);
            jsonObject.addProperty("recirverId", recieverId);
            String jsonOut = jsonObject.toString();
            System.out.println("");
            CommonTask getNotifyTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = getNotifyTask.execute().get();
                Type typelist = new TypeToken<List<Notify>>() {
                }.getType();
                notifies = gson.fromJson(jsonIn, typelist);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            Common.showToast(activity, "請確認網路連線狀態");
        }
        return notifies;
    }


    @Override
    public void onResume() {
        super.onResume();
        messageDelegate.addOnMessageReceiveListener(new MessageDelegate.OnMessageReceiveListener() {
            @Override
            public void onMessage(String message) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        messagess = getAllMessagess(memberId, recieverId);
                        showChat(messagess);
                        recyclerView.scrollToPosition(messagess.size() - 1);
                    }
                });

            }
        });
    }


    @Override
    public void onPause() {
        super.onPause();
        messageDelegate.removeOnMessageReceiveListener(listener);
    }
// toorBar

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Navigation.findNavController(messageEdit).popBackStack();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


//挑選照片

    private void showTypeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final AlertDialog dialog = builder.create();
        final View view = View.inflate(getActivity(), R.layout.dialog_select_photo, null);
        TextView tv_select_gallery = (TextView) view.findViewById(R.id.tv_select_gallery);
        TextView tv_select_camera = (TextView) view.findViewById(R.id.tv_select_camera);
        tv_select_gallery.setOnClickListener(new View.OnClickListener() {
            // 在相簿中選取
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    openAlbum();
                }
                dialog.dismiss();
            }
        });
        tv_select_camera.setOnClickListener(new View.OnClickListener() {// 呼叫照相機
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                file = new File(file, "picture.jpg");
                contentUri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".provider", file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);

                if (intent.resolveActivity(activity.getPackageManager()) != null) {
                    startActivityForResult(intent, REQ_TAKE_PICTURE);
                    dialog.dismiss();
                } else {
                    Common.showToast(activity, "no camera app found");
                }

            }
        });

        dialog.setView(view);
        dialog.show();

    }

    private void openAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQ_PICK_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQ_TAKE_PICTURE:
                    crop(contentUri);
                    break;
                case REQ_PICK_PICTURE:
                    crop(intent.getData());
                    break;
                case REQ_CROP_PICTURE:
                    handleCropResult(intent);
                    break;
            }
        }
    }

    private void crop(Uri sourceImageUri) {
        File file = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        file = new File(file, "picture_cropped.jpg");
        Uri destinationUri = Uri.fromFile(file);
        UCrop.of(sourceImageUri, destinationUri)
//                .withAspectRatio(16, 9) // 設定裁減比例
//                .withMaxResultSize(500, 500) // 設定結果尺寸不可超過指定寬高
                .start(activity, this, REQ_CROP_PICTURE);
    }

    private void handleCropResult(Intent intent) {
        Uri resultUri = UCrop.getOutput(intent);
        if (resultUri == null) {
            return;
        }
        Bitmap bitmap = null;
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                bitmap = BitmapFactory.decodeStream(
                        activity.getContentResolver().openInputStream(resultUri));
            } else {
                ImageDecoder.Source source =
                        ImageDecoder.createSource(activity.getContentResolver(), resultUri);
                bitmap = ImageDecoder.decodeBitmap(source);
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            photo = out.toByteArray();
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
        if (bitmap != null) {
            int memberId = Integer.parseInt(pref.getString("memberId", "0"));
            sendPhoto(memberId, bitmap);
            recyclerView.scrollToPosition(messagess.size() - 1);
        } else {
        }
    }

    //傳送照片
    public void sendPhoto(int memberid, Bitmap bitmap) {
        AppMessage message = null;

        String msgType = Common.SEND_PHOTO_TYPE;
        String title = "";
        String body = messageEdit.getText().toString().trim();
        int stat = 0;
        int sendId = memberid;


        //取得"台北時區"時間
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Taipei"));
        Calendar calendar = Calendar.getInstance();
        String uptime = simpleDateFormat.format(calendar.getTime());
        String photoStr = convertIconToString(bitmap);

        message = new AppMessage(msgType, memberid, title, body, stat, sendId, recieverId, uptime, photoStr);
        SendMessage sendMessage = new SendMessage(activity, message);
        sendMessage.sendChatMessage();

        messageAdapter = (MessageAdapter) recyclerView.getAdapter();

        messagess = getAllMessagess(memberid, recieverId);
        showChat(messagess);

    }

    //bitmap轉字串
    public static String convertIconToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();// outputstream
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);
        byte[] appicon = baos.toByteArray();// 轉為byte陣列
        return Base64.encodeToString(appicon, Base64.DEFAULT);

    }
}