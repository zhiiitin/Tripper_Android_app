package com.example.tripper_android_app.chat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tripper_android_app.MainActivity;
import com.example.tripper_android_app.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class ChatMainFragment extends Fragment implements TextWatcher {
    private String name;
    private String SERVER_PATH = "ws://echo.websocket.org";
    private WebSocket webSocket ;
    private EditText messageEdit;
    private RecyclerView recyclerView;
    private ImageView sendBtn;
    private MainActivity activity;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_chat_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initiateSocketConnection();
        messageEdit = view.findViewById(R.id.messageEdit);
        sendBtn = view.findViewById(R.id.sendBtn);
        recyclerView = view.findViewById(R.id.recyclerView);
        messageEdit.addTextChangedListener(this);
        MessageAdapter messageAdapter = new MessageAdapter(getLayoutInflater());
        recyclerView.setAdapter(messageAdapter);//先別管我
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObject = new JSONObject();
                try{
                    jsonObject.put("name",name);
                    jsonObject.put("message", messageEdit.getText().toString());
                    webSocket.send(jsonObject.toString());
                    jsonObject.put("isSent", true);
                    messageAdapter.addItem(jsonObject);
                    resetMessageEdit();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initializeView() {
        messageEdit = getView().findViewById(R.id.messageEdit);
        sendBtn = getView().findViewById(R.id.sendBtn);

        recyclerView = getView().findViewById(R.id.recyclerView);

        messageEdit.addTextChangedListener(this);
        MessageAdapter messageAdapter = new MessageAdapter(getLayoutInflater());//先別管我
        recyclerView.setAdapter(messageAdapter);//先別管我
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));//先別管我
    }


    private void initiateSocketConnection(){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(SERVER_PATH).build();
        webSocket = client.newWebSocket(request,new SocketListener());
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String string = s.toString().trim();
        if(string.isEmpty()){
            resetMessageEdit();
        }else{
//            sendBtn.setVisibility(View.INVISIBLE);
        }
    }

    private void resetMessageEdit() {
        messageEdit.removeTextChangedListener(this);

        messageEdit.setText("");
//       sendBtn.setVisibility(View.VISIBLE);

        messageEdit.addTextChangedListener(this);
    }

    private class SocketListener extends WebSocketListener {
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            super.onOpen(webSocket, response);

                Toast.makeText(activity, "Socket Connection Successful",
                        Toast.LENGTH_SHORT).show();
                initializeView();

        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            super.onMessage(webSocket, text);

                try {
                    JSONObject jsonObject = new JSONObject(text);
                    jsonObject.put("isSent", false);
                    MessageAdapter messageAdapter = (MessageAdapter)recyclerView.getAdapter();
                    messageAdapter.addItem(jsonObject);//先別管我
                } catch (Exception e) {
                    e.printStackTrace();
                }

        }

    }

    public class MessageAdapter extends RecyclerView.Adapter {
        private final int TYPE_MESSAGE_SENT = 0;
        private final int TYPE_MESSAGE_RECEIVED = 1;
        private final int TYPE_IMAGE_SENT = 2;
        private final int TYPE_IMAGE_RECEIVED = 3;

        private LayoutInflater inflater;
        private List<JSONObject> messages = new ArrayList<>();

        public MessageAdapter(LayoutInflater inflater){
            this.inflater = inflater;
        }

        public void addItem(JSONObject jsonObject){
            messages.add(jsonObject);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            switch(viewType) {
                case TYPE_MESSAGE_SENT:
                    view = inflater.inflate(R.layout.item_send_message, parent, false);
                    return new SentMessageHolder(view);
                case TYPE_MESSAGE_RECEIVED:
                    view = inflater.inflate(R.layout.item_received_message, parent, false);
                    return new ReceivedMessageHolder(view);
            }

            return null;
        }



        @Override
        public int getItemCount() {
            return messages.size();
        }
//發送訊息的ViewHolder
        private class SentMessageHolder extends RecyclerView.ViewHolder {

            TextView messageTxt;

            public SentMessageHolder(@NonNull View itemView) {
                super(itemView);

                messageTxt = itemView.findViewById(R.id.tvSent);
            }
        }
//接收訊息的ViewHolder
        private class ReceivedMessageHolder extends RecyclerView.ViewHolder{

            TextView nameTxt, messageTxt;

            public ReceivedMessageHolder(@NonNull View itemView) {
                super(itemView);

                nameTxt = itemView.findViewById(R.id.tvName);
                messageTxt = itemView.findViewById(R.id.tvReceived);
            }
        }

        @Override
        public int getItemViewType(int position) {

            JSONObject message = messages.get(position);

            try {
                if(message.getBoolean("isSent")){
                    if(message.has("message")){
                        return TYPE_MESSAGE_SENT;
                    }else{
                        return TYPE_IMAGE_SENT;
                    }
                }else{
                    if(message.has("message")){
                        return TYPE_MESSAGE_RECEIVED;
                    }else{
                        return TYPE_IMAGE_RECEIVED;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return -1;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            JSONObject message = messages.get(position);
            try {
                if(message.getBoolean("isSent")){
                    if(message.has("message")){
                        SentMessageHolder messageHolder = (SentMessageHolder) holder;
                        messageHolder.messageTxt.setText(message.getString("message"));
                    }else{
//                        SentImageHolder imageHolder = (SentImageHolder) holder;
//                        Bitmap bitmap = getBitmapFromString(message.getString("image"));
//
//                        imageHolder.imageView.setImageBitmap(bitmap);
                    }
                }else{
                    if(message.has("message")){
                        ReceivedMessageHolder messageHolder = (ReceivedMessageHolder) holder;
                        messageHolder.nameTxt.setText(message.getString("name"));
                        messageHolder.messageTxt.setText(message.getString("message"));
                    }else{
//                        ReceivedImageHolder imageHolder = (ReceivedImageHolder) holder;
//                        imageHolder.nameTxt.setText(message.getString("name"));
//
//                        Bitmap bitmap = getBitmapFromString(message.getString("image"));
//                        imageHolder.imageView.setImageBitmap(bitmap);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

//        private Bitmap getBitmapFromString(String image) {
//            byte[] bytes = Base64.decode(image, Base64.DEFAULT);
//            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//        }


    }


}