package com.example.tripper_android_app.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.example.tripper_android_app.MainActivity;
import com.example.tripper_android_app.location.Location_D;
import com.example.tripper_android_app.task.CommonTask;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.example.tripper_android_app.trip.Trip_LocInfo;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import static android.content.Context.MODE_PRIVATE;

public class Common {
    private static final String TAG = "TAG_Common";
    public static String URL_SERVER = "http://10.0.2.2:8080/Tripper_JAVA_Web/";
    public final static String PREF_FILE = "preference";
    public final static String DEFAULT_FILE = "";
    public static Map<String, List<Location_D>> map = new TreeMap<>();
    public static String spinnerSelect = "";
    public static String spinnerSelect2 = "";
    public static String tripId = "";
    public static int memberId;
    public static final String FRIEND_TYPE = "F"; // 朋友相關類型訊息
    public static final String TRIP_TYPE = "T"; // 行程相關類型訊息
    public static final String GROUP_TYPE = "G"; // 揪團相關類型訊息
    public static final String BLOG_TYPE = "B"; // 網誌相關類型訊息
    public static final String SEND_MESSEAGE_TYPE = "RIGHT"; //發送聊天訊息
    public static final String RECEIVE_MESSEAGE_TYPE = "LEFT"; //接收聊天訊息
    private  MainActivity activity ;

    /**
     * 檢查是否有網路連線
     */
    public static boolean networkConnected(Activity activity) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // API 23支援getActiveNetwork()
                Network network = connectivityManager.getActiveNetwork();
                // API 21支援getNetworkCapabilities()
                NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
                if (networkCapabilities != null) {
                    String msg = String.format(Locale.getDefault(),
                            "TRANSPORT_WIFI: %b%nTRANSPORT_CELLULAR: %b%nTRANSPORT_ETHERNET: %b%n",
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI),
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR),
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
                    Log.d(TAG, msg);
                    return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);
                }
            } else {
                // API 29將NetworkInfo列為deprecated
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                return networkInfo != null && networkInfo.isConnected();
            }
        }
        return false;
    }

    public static void showToast(Context context, int messageResId) {
        Toast.makeText(context, messageResId, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static String getTransId() {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssss");
        String transId = dateFormat.format(System.currentTimeMillis());
        return transId;
    }




    public static boolean netWorkConnected(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // API 23支援getActiveNetwork()
                Network network = connectivityManager.getActiveNetwork();
                // API 21支援getNetworkCapabilities()
                NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
                if (networkCapabilities != null) {
                    String msg = String.format(Locale.getDefault(),
                            "TRANSPORT_WIFI: %b%nTRANSPORT_CELLULAR: %b%nTRANSPORT_ETHERNET: %b%n",
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI),
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR),
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
                    Log.d(TAG, msg);
                    return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);
                }
            } else {
                // API 29將NetworkInfo列為deprecated
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                return networkInfo != null && networkInfo.isConnected();
            }
        }
        return false;
    }


    public static void sendTokenToServer(int memberId, String token, Context context) {
        if (Common.netWorkConnected(context)) {
            String Url = Common.URL_SERVER + "FCMServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "register");
            jsonObject.addProperty("userToken", token);
            jsonObject.addProperty("memberId", memberId);
            CommonTask registerTask = new CommonTask(Url, jsonObject.toString());
            registerTask.execute();
        } else {
            Common.showToast(context, "連線失敗");
        }
    }
//判斷是否有登入帳號
    public static boolean isLogin(Activity activity){
        SharedPreferences pref = activity.getSharedPreferences(Common.PREF_FILE,
                MODE_PRIVATE);
        boolean login = pref.getBoolean("login", false);
        if(login){
            return true;
        }
        else {
            return false;
        }
    }

    // 取得token，每次都更新user的手機token
    public static void getTokenSendServer(final Activity activity) {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                // 取得手機ID
                                String token = task.getResult().getToken();
                                String url = Common.URL_SERVER + "MemberServlet";
                                SharedPreferences pref = activity.getSharedPreferences(Common.PREF_FILE,
                                        MODE_PRIVATE);
                                int memberId = Integer.parseInt(pref.getString("memberId","0"));
                                Common.sendTokenToServer(memberId, token, activity);
                                Log.d(TAG, "token::" + token);
                            }
                        } else {
                            Log.d(TAG, "token fail");
                        }
                    }
                });
    }


}
