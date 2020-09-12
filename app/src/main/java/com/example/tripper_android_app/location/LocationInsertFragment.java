package com.example.tripper_android_app.location;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.tripper_android_app.MainActivity;
import com.example.tripper_android_app.R;
import com.example.tripper_android_app.task.CommonTask;
import com.example.tripper_android_app.util.Common;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import com.yalantis.ucrop.UCrop;

import static android.app.Activity.RESULT_OK;

public class LocationInsertFragment extends Fragment {
    private static final String TAG = "LocationInsertFragment";
    private static final String MSG = "必填欄位";
    private MainActivity activity;
    private ImageView ivLocImage;
    private EditText etLocName, etAddress, etDesc;
    private Location location;
    private Uri contentUri;
    private byte[] image;
    private static final int REQ_TAKE_PICTURE = 0;
    private static final int REQ_PICK_PICTURE = 1;
    private static final int REQ_CROP_PICTURE = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_location_insert, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("新增景點");

        ivLocImage = view.findViewById(R.id.ivLocImage);
        etLocName = view.findViewById(R.id.etLocName);
        etAddress = view.findViewById(R.id.etAddress);
        etDesc = view.findViewById(R.id.tvDesc);
        ImageButton ibInsert = view.findViewById(R.id.ibUpdate);

        // pick pic
        ivLocImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQ_PICK_PICTURE);

            }
        });


        ibInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                     // 欄位檢核
                    String locName = etLocName.getText().toString().trim();
                    String address = etAddress.getText().toString().trim();
                    String desc = etDesc.getText().toString().trim();
                    Log.d(TAG, "locName :: " + locName);
                    if (locName.isEmpty()) {
                        etLocName.setError(MSG);
                        return;
                    }

                    if (address.isEmpty()) {
                        etAddress.setError(MSG);
                        return;
                    }

                    if (desc.isEmpty()) {
                        etDesc.setError(MSG);
                        return;
                    }


                    List<Address> addressList;
                    double latitude = -181.0;
                    double longitude = -181.0;
                    try {
                        addressList = new Geocoder(activity).getFromLocationName(locName, 1);
                        if(addressList != null && addressList.size() > 0){
                            latitude = addressList.get(0).getLatitude();
                            longitude = addressList.get(0).getLongitude();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // 檢查網路連線
                    if(Common.networkConnected(activity)){
                        String url = Common.URL_SERVER + "LocationServlet";
//                        Location(String locId, String name, String address, String locType, String city, String info,
//                        double longitude, double latitude, int createId, int useId, String createDateTime)
                        Location location = new Location(Common.getTransId(), locName, address, "type","city", desc,
                                longitude, latitude, 1, 1, new Timestamp(System.currentTimeMillis()));
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("action", "locationInsert");
                        jsonObject.addProperty("location", new Gson().toJson(location));
                        // 有圖才上傳
                        if (image != null) {
                            jsonObject.addProperty("imageBase64", Base64.encodeToString(image, Base64.DEFAULT));
                        }
                        int count = 0;
                        try {
                            String result = new CommonTask(url, jsonObject.toString()).execute().get();
                            count = Integer.parseInt(result);
                            if(count == 0){
                                Common.showToast(activity, "insert fail");
                            }else{
                                Common.showToast(activity, "insert success");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else{
                        Common.showToast(activity, "no network connection");
                    }

                Navigation.findNavController(view)
                        .popBackStack();

            }
        });



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(resultCode == RESULT_OK){
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
            image = out.toByteArray();
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
        if (bitmap != null) {
            ivLocImage.setImageBitmap(bitmap);
        } else {
            ivLocImage.setImageResource(R.drawable.no_image);
        }
    }
}