package com.example.tripper_android_app.setting.railway;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.tripper_android_app.MainActivity;
import com.example.tripper_android_app.R;

public class HsrTimeTableFragment extends Fragment {
    private final static String TAG = "TAG_HsrTimeTableFragment";
    private MainActivity activity;
    private WebView wvHsr;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_hsr_time_table, container, false);
        wvHsr = view.findViewById(R.id.wvHsr);
        /* WebView註冊監聽器(setOnKeyListener)，並且在裡面
           覆寫onKey()，讓用戶回到上個網頁而非上個Android頁面。 */
        wvHsr.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    wvHsr = view.findViewById(R.id.wvHsr);
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_BACK:
                            if (wvHsr.canGoBack()) {
                                wvHsr.goBack();
                                return true;
                            }
                            break;
                    }
                }
                return false;
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 設定toolbar(標題的文字和顏色)
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("高鐵時刻表");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorForWhite));
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 讓toolbar的返回箭頭變白色
        Drawable upArrow = ContextCompat.getDrawable(activity, R.drawable.abc_ic_ab_back_material);
        if(upArrow != null) {
            upArrow.setColorFilter(ContextCompat.getColor(activity, R.color.colorForWhite), PorterDuff.Mode.SRC_ATOP);
            activity.getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }

        wvHsr = view.findViewById(R.id.wvHsr);
        handleViews();
        /* 要允許不加密連線 (http) 就必須將usesCleartextTraffic設定為true */
        wvHsr.loadUrl("https://www.thsrc.com.tw/ArticleContent/a3b630bb-1066-4352-a1ef-58c7b4e8ef7c");
    }

    private void handleViews() {
        /* WebView上可運行JavaScript語法 */
        wvHsr.getSettings().setJavaScriptEnabled(true);
        wvHsr.setWebViewClient(new WebViewClient() {
            /* 如果沒有覆寫shouldOverrideUrlLoading()並回傳false，
               用戶點擊任何連結都會自動開啟手機內建的瀏覽器來呈現內容 */
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            /* shouldOverrideUrlLoading(WebView view, String url)
               在API 24時被列為deprecated，官方文件建議改覆寫下面方法 */
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Navigation.findNavController(wvHsr).popBackStack();
                return true;
            default:
                break;
        }
        return true;
    }
}
