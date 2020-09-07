package com.example.tripper_android_app.setting.member;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tripper_android_app.R;
import com.example.tripper_android_app.task.CommonTask;
import com.example.tripper_android_app.task.ImageTask;
import com.example.tripper_android_app.util.Common;
import com.google.gson.Gson;
import com.google.gson.JsonObject;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;


public class Register_Member_Fragment extends Fragment {
    private final static String TAG = "TAG_MemberFragment";
    private FragmentActivity activity;
    private TextView tvId, tvNickName, tvLoginType ,tvUpdate;
    private CardView cvUpdate;
    private ImageButton ibLogout ;
    private ImageView ivPhoto ;
    private ImageButton ibChange ;
    private byte[] photo ;
    private static final int REQ_TAKE_PICTURE = 0;
    private static final int REQ_PICK_PICTURE = 1;
    private static final int REQ_CROP_PICTURE = 2;
    private Uri contentUri;
    private Member member ;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_register__member_, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final NavController navController = Navigation.findNavController(view);
        tvId = view.findViewById(R.id.tvId_member);
        tvNickName = view.findViewById(R.id.tvNickname_member);
        tvLoginType = view.findViewById(R.id.tvLoginType_member);
        ibLogout = view.findViewById(R.id.btLogout);
        ivPhoto = view.findViewById(R.id.ivPhoto);
        ibChange = view.findViewById(R.id.ibChange);
        tvUpdate = view.findViewById(R.id.tvUpdate);
        cvUpdate = view.findViewById(R.id.cvUpdate);

        Bundle bundle = getArguments();
        if (bundle == null || bundle.getSerializable("Member") == null) {
            Common.showToast(activity, "尚未登入會員");
            navController.popBackStack();
            return;
        } else {
            member = (Member) bundle.getSerializable("Member");

            String id = member.getId() + "";
            String nickname = member.getNickName();
            tvId.setText(id);
            tvNickName.setText(nickname);
            if(member.getLoginType() == 1){
                tvLoginType.setText("一般登入");
            }
        }

        ibLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = activity.getSharedPreferences(Common.PREF_FILE,
                        MODE_PRIVATE);
                pref.edit().putBoolean("login", false).apply();
                Navigation.findNavController(ibLogout).navigate(R.id.action_register_Member_Fragment_to_register_main_Fragment);
                Common.showToast(activity,"帳號已登出");
            }
        });

        ibChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTypeDialog();
            }
        });
 //按下編輯完成，傳送更改的資料以及照片
        cvUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.networkConnected(activity)) {
                    String Url = Common.URL_SERVER + "MemberServlet";
//                    member.setNickName();
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "memberUpdate");
                    jsonObject.addProperty("member", new Gson().toJson(member));

                    if (photo != null) {
                        jsonObject.addProperty("imageBase64", Base64.encodeToString(photo, Base64.DEFAULT));
                    }
                    int count = 0;
                    try {
                        String result = new CommonTask(Url, jsonObject.toString()).execute().get();
                        count = Integer.parseInt(result);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                    if (count == 0) {
                        Common.showToast(activity, "修改失敗");
                    } else {
                        Common.showToast(activity, "修改成功");
                    }
                } else {
                    Common.showToast(activity, "no network connection found");
                }
            }
        });

        showMember();
    }

    private void showTypeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final AlertDialog dialog = builder.create();
        final View view = View.inflate(getActivity(), R.layout.dialog_select_photo, null);
        TextView tv_select_gallery = (TextView) view.findViewById(R.id.tv_select_gallery);
        TextView tv_select_camera = (TextView) view.findViewById(R.id.tv_select_camera);
        tv_select_gallery.setOnClickListener(new View.OnClickListener() {// 在相簿中選取
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
                file = new File(file,"picture.jpg");
                contentUri = FileProvider.getUriForFile(activity,activity.getPackageName()+".provider",file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,contentUri);

                if(intent.resolveActivity(activity.getPackageManager())!= null){
                    startActivityForResult(intent,REQ_TAKE_PICTURE);
                    dialog.dismiss();
                }
                else {
                    Common.showToast(activity, "no camera app found");
                }

            }
        });

        dialog.setView(view);
        dialog.show();

    }

    private void showMember() {
        String Url = Common.URL_SERVER + "MemberServlet";
        int id = member.getId();
        int imageSize = getResources().getDisplayMetrics().widthPixels / 3;
        Bitmap bitmap = null;
        try {
            bitmap = new ImageTask(Url, id, imageSize).execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (bitmap != null) {
            ivPhoto.setImageBitmap(bitmap);
        } else {
            ivPhoto.setImageResource(R.drawable.ic_nopicture);
        }
//        etIsbn.setText(book.getIsbn());
//        etName.setText(book.getName());
//        etPrice.setText(book.getPrice() + "");
//        etAuthor.setText(book.getAuthor());
    }

    private void openAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,REQ_PICK_PICTURE);
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
            ivPhoto .setImageBitmap(bitmap);
        } else {
            ivPhoto .setImageResource(R.drawable.ic_nopicture);
        }
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode){
//            case TAKE_PHOTO:
//                if(resultCode==RESULT_OK){
//                    try {
//                        Bitmap bitmap= BitmapFactory.decodeStream(view.getContext().getContentResolver().openInputStream(imageUri));
//                        String ans=imageUri.toString();
//                        bean.image=ans;
//                        muserOperator.updateImage(bean);
//                        imageView.setImageBitmap(bitmap);
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                }
//                break;
//            case CHOOSE_PHOTO:
//                if(resultCode==RESULT_OK){
//                    if(Build.VERSION.SDK_INT>=19){
//                        handleImageOnKitKat(data);
//                    }else{
//                        handleImageBeforeKitKat(data);
//                    }
//                }
//                break;
//            default:
//                break;
//        }
//    }
}