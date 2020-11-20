package com.example.tripper_android_app.blog;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tripper_android_app.MainActivity;
import com.example.tripper_android_app.R;
import com.example.tripper_android_app.blog.BlogPic;
import com.example.tripper_android_app.blog.CreateBlogPicFragment;
import com.example.tripper_android_app.task.CommonTask;
import com.example.tripper_android_app.util.CircleImageView;
import com.example.tripper_android_app.util.Common;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.example.tripper_android_app.util.Common.showToast;


public class UpdateBlogPicFragment extends Fragment {
    private static final String TAG = "TAG_U_BlogPicFragment";
    private RecyclerView rvPhoto;
    private MainActivity activity;
    private ImageView ivPoint ;
    private CircleImageView ivnopic ;
    private ImageButton ibAdd;
    private TextView tvSpotName, tvnopic, tvTouch, tvUpdate;
    private ImageButton ibUpdate;
    private CommonTask imageTask;
    private static final int REQ_TAKE_PICTURE = 0;
    private static final int REQ_PICK_PICTURE = 1;
    private static final int REQ_CROP_PICTURE = 2;
    private Uri contentUri;
    private byte[] photo;
    private List<Bitmap> bitmapList = null;
    private String blogID, locId;
    private BlogPic blogPic = null ;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_blog_pic, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final NavController navController = Navigation.findNavController(view);

//ToolBar
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("新增照片");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorForWhite));
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Drawable upArrow = ContextCompat.getDrawable(activity, R.drawable.abc_ic_ab_back_material);
        if (upArrow != null) {
            upArrow.setColorFilter(ContextCompat.getColor(activity, R.color.colorForWhite), PorterDuff.Mode.SRC_ATOP);
            activity.getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }
        ivPoint = view.findViewById(R.id.ivPoint);
        tvSpotName = view.findViewById(R.id.tvSpotName);
        ibUpdate = view.findViewById(R.id.ibUpdate);
        ibAdd = view.findViewById(R.id.ibAdd);
        tvTouch = view.findViewById(R.id.tvText);
        tvUpdate = view.findViewById(R.id.tvUpdate);
        ivnopic = view.findViewById(R.id.ivNopic);
        tvnopic = view.findViewById(R.id.tvnoPic);

        ibAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTypeDialog();
                rvPhoto.setVisibility(View.VISIBLE);
                tvnopic.setVisibility(View.GONE);
                ivnopic.setVisibility(View.GONE);
            }
        });

        blogPic = new BlogPic();
//接收前頁bundle
        final Bundle bundle = getArguments();
        String spotName = (String) bundle.get("spotName");
        blogID = (String) bundle.get("blogID");
        locId = (String) bundle.get("locId");
        tvSpotName.setText(spotName);
        System.out.println(locId);
        blogPic = (BlogPic) bundle.getSerializable("blogPic"+locId);
        Log.d("$$$$$$$$$$$$$$$$$$$$$$$$$$","blogPic"+locId);
        bitmapList = new ArrayList<>() ;

//RecyclerView
        rvPhoto = view.findViewById(R.id.rvPhoto);
        rvPhoto.setLayoutManager(new GridLayoutManager(activity, 2));
        rvPhoto.setAdapter(new ImgAdpter(activity, bitmapList));

        if(blogPic != null) {
            rvPhoto.setVisibility(View.VISIBLE);
            tvnopic.setVisibility(View.GONE);
            ivnopic.setVisibility(View.GONE);
            blogPic.setBlogId(blogID);
            blogPic.setLocId(locId);
        }

        if(blogPic.getPic1() != null){
            bitmapList.add(convertStringToIcon(blogPic.getPic1()));
        }if(blogPic.getPic2() != null){
            bitmapList.add(convertStringToIcon(blogPic.getPic2()));
        }if(blogPic.getPic3() != null){
            bitmapList.add(convertStringToIcon(blogPic.getPic3()));
        }if(blogPic.getPic4() != null){
            bitmapList.add(convertStringToIcon(blogPic.getPic4()));
        }
        if(bitmapList.size() == 4){
            ibAdd.setVisibility(View.GONE);
            tvTouch.setVisibility(View.GONE);
        }

        if(blogPic.getPic1()!= null){
            showPhotos(bitmapList);
        }

        ibUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        blogPic.setPic1(null);
                        blogPic.setPic2(null);
                        blogPic.setPic3(null);
                        blogPic.setPic4(null);
                        for(int i = 0 ; i < bitmapList.size() ; i++){
                            if(i == 0) {
                                String pic1 = convertIconToString(bitmapList.get(i));
                                blogPic.setPic1(pic1);
                            }if(i == 1) {
                                String pic2 = convertIconToString(bitmapList.get(i));
                                blogPic.setPic2(pic2);
                            }if(i == 2) {
                                String pic3 = convertIconToString(bitmapList.get(i));
                                blogPic.setPic3(pic3);
                            }if(i == 3) {
                                String pic4 = convertIconToString(bitmapList.get(i));
                                blogPic.setPic4(pic4);
                            }
                        }
                    }
                });
                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                tvUpdate.setVisibility(View.VISIBLE);

                if (Common.networkConnected(activity)) {
                    String url = Common.URL_SERVER + "BlogServlet";
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "updateImage");
                    jsonObject.addProperty("blogPic", new Gson().toJson(blogPic));
                    int count = 0;
                    try {                               //呼叫execute()執行doInBackGround
                        String jsonIn = new CommonTask(url, jsonObject.toString()).execute().get();
                        ;
                        count = Integer.parseInt(jsonIn);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                    if (count == 0) {
                        Common.showToast(activity, "新增失敗");
                    } else {
                        Common.showToast(activity, "上傳成功");
                        navController.popBackStack();
                    }
                } else {
                    showToast(activity, R.string.textNoNetwork);
                }

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Navigation.findNavController(ibUpdate).popBackStack();
                return true;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private class ImgAdpter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private LayoutInflater layoutInflater;
        //        private List<Img> imgs;
        private ImageView pickIcon;
        private int imageSize;
        private List<Bitmap> imgList;

        public List<Bitmap> getImgList() {
            return imgList;
        }

        public void setImgList(List<Bitmap> imgList) {
            this.imgList = imgList;
        }

        public LayoutInflater getLayoutInflater() {
            return layoutInflater;
        }

        public void setLayoutInflater(LayoutInflater layoutInflater) {
            this.layoutInflater = layoutInflater;
        }



        public ImageView getPickIcon() {
            return pickIcon;
        }

        public void setPickIcon(ImageView pickIcon) {
            this.pickIcon = pickIcon;
        }

        public int getImageSize() {
            return imageSize;
        }

        public void setImageSize(int imageSize) {
            this.imageSize = imageSize;
        }

        ImgAdpter(Context context, List<Bitmap> imgList) {
            layoutInflater = LayoutInflater.from(context);
            this.pickIcon = pickIcon;
            this.imgList = imgList;
            /* 螢幕寬度除以2當作將圖的尺寸 */
            imageSize = getResources().getDisplayMetrics().widthPixels / 2;
        }

        //圖片ViewHolder
        public class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView ivArticleImageInsert;
            ImageButton ibDeletePic ;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                ivArticleImageInsert = itemView.findViewById(R.id.ivPhoto);
                ibDeletePic = itemView.findViewById(R.id.ibDeletePic);

            }
        }

        //加入圖片ViewHolder > header
        public class PickViewHolder extends RecyclerView.ViewHolder {
            ImageView ivArticleImagePick;


            public PickViewHolder(@NonNull View itemView) {
                super(itemView);
                ivArticleImagePick = itemView.findViewById(R.id.ivPhoto);
            }
        }


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView;

            itemView = layoutInflater.inflate(R.layout.item_view_createblog_photo, parent, false);
            return new MyViewHolder(itemView);

        }

        //設定長度
        @Override
        public int getItemCount() {
            return imgList == null ? 1 : (imgList.size());
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//
//            if (holder instanceof PickViewHolder) {
//
//                PickViewHolder pickViewHolder = (PickViewHolder) holder; //要強轉！！
//                pickViewHolder.ivArticleImagePick.setImageResource(R.drawable.ic_imageinsert);
//                pickViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                       showTypeDialog();
//                    }
//                });


            MyViewHolder myViewHolder = (MyViewHolder) holder;
            // position -1 > 因為每增加一筆資料，onBindViewHolder的position會自動加1，(0被PickViewHolder綁住)
            // 但imgList的索引值是從0開始，對不上position的1 ， 所以 position - 1 > 跟
            Bitmap bitmapPosition = imgList.get(position);
            myViewHolder.ivArticleImageInsert.setImageBitmap(bitmapPosition);

            myViewHolder.ibDeletePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bitmapList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position,imgList.size());
                    if(bitmapList.size() < 4){
                        ibAdd.setVisibility(View.VISIBLE);
                        tvTouch.setVisibility(View.VISIBLE);
                    }
                }
            });

            myViewHolder.ivArticleImageInsert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    View view = getLayoutInflater().inflate(R.layout.dialog_imageview, null);
                    alertDialog.setView(view);
                    ImageView ivPhoto = view.findViewById(R.id.ivPhoto);

                    if (bitmapPosition != null) {

                        Glide.with(activity).load(bitmapPosition).into(ivPhoto);
                        //將白色部分設為透明
                        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        alertDialog.setCancelable(true);
                        alertDialog.show();
                    }
                }
            });
        }
    }


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
                    showToast(activity, "no camera app found");
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
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, out);
            photo = out.toByteArray();
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
        if (bitmap != null) {
            bitmapList.add(bitmap);
            showPhotos(bitmapList);

//            if (bitmapList.size() == 1) {
//                String pic1 = Base64.encodeToString(photo, Base64.DEFAULT);
//                blogPic.setPic1(pic1);
//            }
//
//            if (bitmapList.size() == 2) {
//                String pic2 = Base64.encodeToString(photo, Base64.DEFAULT);
//                blogPic.setPic2(pic2);
//            }
//
//            if (bitmapList.size() == 3) {
//                String pic3 = Base64.encodeToString(photo, Base64.DEFAULT);
//                blogPic.setPic3(pic3);
//            }
//
            if (bitmapList.size() == 4) {
                ibAdd.setVisibility(View.GONE);
                tvTouch.setVisibility(View.GONE);
            }
        }


    }

    private void showPhotos(List<Bitmap> PhotoList) {
        if (PhotoList == null || PhotoList.isEmpty()) {

        }
        ImgAdpter photoAdapter = (ImgAdpter) rvPhoto.getAdapter();
        if (photoAdapter == null) {
            rvPhoto.setAdapter(new ImgAdpter(activity, bitmapList));
        } else {
            photoAdapter.setImgList(bitmapList);
            photoAdapter.notifyDataSetChanged();
        }
    }

    public static String convertIconToString(Bitmap bitmap)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();// outputstream
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);
        byte[] appicon = baos.toByteArray();// 轉為byte陣列
        return Base64.encodeToString(appicon, Base64.DEFAULT);

    }

    public static Bitmap convertStringToIcon(String st)
    {
        // OutputStream out;
        Bitmap bitmap = null;
        try
        {
            // out = new FileOutputStream("/sdcard/aa.jpg");
            byte[] bitmapArray;
            bitmapArray = Base64.decode(st, Base64.DEFAULT);
            bitmap =
                    BitmapFactory.decodeByteArray(bitmapArray, 0,
                            bitmapArray.length);
            // bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            return bitmap;
        }
        catch (Exception e)
        {
            return null;
        }
    }
}