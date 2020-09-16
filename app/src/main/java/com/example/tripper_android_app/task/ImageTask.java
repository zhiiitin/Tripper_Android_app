package com.example.tripper_android_app.task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.example.tripper_android_app.R;
import com.google.gson.JsonObject;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;



public class ImageTask extends AsyncTask<Object, Integer, Bitmap> {
    private final static String TAG = "TAG_ImageTask";
    private String url;
    private int id, imageSize;
    private String key; // 資料表大多數PK為String型別，所另建此欄位
    /* ImageTask的屬性strong參照到SpotListFragment內的imageView不好，
        會導致SpotListFragment進入背景時imageView被參照而無法被釋放，
        而且imageView會參照到Context，也會導致Activity無法被回收。
        改採weak參照就不會阻止imageView被回收 */
    private WeakReference<ImageView> imageViewWeakReference;

    // 取單張圖片
    public ImageTask(String url, int id, int imageSize) {
        this(url, id, imageSize, null);
    }

    public ImageTask(String url, String key, int imageSize) {
        this(url, key, imageSize, null);
    }

    // 取完圖片後使用傳入的ImageView顯示，適用於顯示多張圖片
    public ImageTask(String url, int id, int imageSize, ImageView imageView) {
        this.url = url;
        this.id = id;
        this.imageSize = imageSize;
        this.imageViewWeakReference = new WeakReference<>(imageView);
    }

    public ImageTask(String url, String key, int imageSize, ImageView imageView) {
        this.url = url;
        this.key = key;
        this.imageSize = imageSize;
        this.imageViewWeakReference = new WeakReference<>(imageView);
    }

    @Override
    protected Bitmap doInBackground(Object... params) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "getImage");
        if(key == null || key.isEmpty()){
            jsonObject.addProperty("id", id);
        }else{
            jsonObject.addProperty("id", key);
        }
        jsonObject.addProperty("imageSize", imageSize);
        return getRemoteImage(url, jsonObject.toString());
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        ImageView imageView = imageViewWeakReference.get();
        if (isCancelled() || imageView == null) {
            return;
        }
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageResource(R.drawable.no_image);
        }
    }

    private Bitmap getRemoteImage(String url, String jsonOut) {
        HttpURLConnection connection = null;
        Bitmap bitmap = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setDoInput(true); // allow inputs
            connection.setDoOutput(true); // allow outputs
            connection.setUseCaches(false); // do not use a cached copy
            connection.setRequestMethod("POST");
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            bw.write(jsonOut);
            Log.d(TAG, "output: " + jsonOut);
            bw.close();

            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                bitmap = BitmapFactory.decodeStream(
                        new BufferedInputStream(connection.getInputStream()));
            } else {
                Log.d(TAG, "response code: " + responseCode);
            }
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return bitmap;
    }
}