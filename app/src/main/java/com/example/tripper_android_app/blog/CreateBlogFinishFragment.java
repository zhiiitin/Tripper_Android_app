package com.example.tripper_android_app.blog;

import android.Manifest;
import android.app.AlertDialog;
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

import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.example.tripper_android_app.MainActivity;
import com.example.tripper_android_app.R;
import com.example.tripper_android_app.task.CommonTask;
import com.example.tripper_android_app.util.Common;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static androidx.navigation.Navigation.findNavController;

/**
 * 類別說明：
 * @author zhitin
 * @version 建立時間:Oct 11, 2020 11:35:34 PM
 *
 */

public class CreateBlogFinishFragment extends Fragment {
    private static final String TAG = "TAG_BlogFinishFragment";
    private MainActivity activity;
    private ImageView ivBGPic,ivText ;
    private ImageButton ibInsertB_PIG,ibBlogUpdate;
    private TextView tvTitle,tvInfo ;
    private TextInputEditText etTitle,etInfo ;
    private CommonTask blogFinishTask ,tripStatusTask;
    private byte[] photo;
    private static final int REQ_TAKE_PICTURE = 0;
    private static final int REQ_PICK_PICTURE = 1;
    private static final int REQ_CROP_PICTURE = 2;
    private Uri contentUri;
    private RadioGroup radioGroup;
    private int status ;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity)getActivity();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_blog_finish, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//ToolBar
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("建立網誌");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorForWhite));
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Drawable upArrow = ContextCompat.getDrawable(activity, R.drawable.abc_ic_ab_back_material);
        if (upArrow != null) {
            upArrow.setColorFilter(ContextCompat.getColor(activity, R.color.colorForWhite), PorterDuff.Mode.SRC_ATOP);
            activity.getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }
        ivBGPic = view.findViewById(R.id.ivBGPic);
        ivText = view.findViewById(R.id.ivText);
        ibInsertB_PIG = view.findViewById(R.id.ibInsertB_PIG);
        ibBlogUpdate = view.findViewById(R.id.ibBlogUpdate);
        tvTitle = view.findViewById(R.id.tvTitle);
        tvInfo = view.findViewById(R.id.tvInfo);
        etTitle = view.findViewById(R.id.etTitle);
        etInfo = view.findViewById(R.id.etInfo);
        radioGroup = view.findViewById(R.id.radioGroup);

//取得tripId
        Bundle bundle = getArguments();
        final String tripId = bundle.getString("tripId");
//取得memberId
        SharedPreferences pref = activity.getSharedPreferences(Common.PREF_FILE,
                MODE_PRIVATE);
        final String memberId = pref.getString("memberId",null);

//選擇公開或私密
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = group.findViewById(checkedId);
                if(radioButton.getText().equals("公開")){
                    status = 0 ;   //0為公開，1為私密
                }else {
                    status = 1 ;
                }
            }
        });

        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etTitle.setText("東海岸的跨年札記");
                etInfo.setText("以往跨年幾乎都是在人擠人的跨年晚會度過，但今年下定決心，逃離擁擠的都市，來到了台東，享受東岸的愜意，迎接新年的第一道曙光！");
            }
        });


//按下發佈網誌按鈕，送出標題及描述
        ibBlogUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 String title = etTitle.getText().toString().trim() ;
                 String info = etInfo.getText().toString().trim() ;

                if(title.isEmpty() || title.length() > 10){
                    etTitle.setError("標題名稱不得空白或超過10個字元");
                    return;
                }

                BlogFinish blogFinish = new BlogFinish(tripId,title,info,memberId,status);
//更改該行程BlogStatus
                String urlTripM = Common.URL_SERVER + "Trip_M_Servlet";
                JsonObject jsonObject2 = new JsonObject();
                jsonObject2.addProperty("action", "updateTrip");
                jsonObject2.addProperty("tripId",blogFinish.getTrip_Id());
                jsonObject2.addProperty("blogStatus", 1);
                tripStatusTask = new CommonTask(urlTripM,jsonObject2.toString());

                try {
                    String jsonIn2 = tripStatusTask.execute().get();
                    System.out.println(jsonIn2);
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }

                if (Common.networkConnected(activity)) {
                    String Url = Common.URL_SERVER + "BlogServlet";
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "updateBlog");
                    jsonObject.addProperty("blogFinish", new Gson().toJson(blogFinish));
                    if (photo != null) {
                        jsonObject.addProperty("imageBase64", Base64.encodeToString(photo, Base64.DEFAULT));
                    }
                    blogFinishTask = new CommonTask(Url,jsonObject.toString());
                    int count = 0;
                    try {
                        String jsonIn = blogFinishTask.execute().get();
                        count = Integer.parseInt(jsonIn);
                        if (count == 1) {
                            Log.e(TAG, "Blog insert sucessful");
                            Common.showToast(activity, "發佈成功");
                            Navigation.findNavController(view).navigate(R.id.action_createBlogFinishFragment_to_blog_HomePage);
                        } else {
                            Common.showToast(activity, "發佈失敗");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }

                }
            }
        });

//按下相機按鈕
        ibInsertB_PIG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTypeDialog();
            }
        });

    }

//--------------------------------BottonBar-----------------------------------


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Navigation.findNavController(tvTitle).popBackStack();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

//對話視窗 挑選照片
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
                .withAspectRatio(16, 9) // 設定裁減比例
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
            Log.e("TAG", e.toString());
        }
        if (bitmap != null) {
            ivBGPic.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(blogFinishTask != null){
            blogFinishTask.cancel(true);
            blogFinishTask = null ;
        }
        if(tripStatusTask!= null){
            tripStatusTask.cancel(true);
            tripStatusTask = null ;
        }

    }

}