package com.ganjie.image_editor;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ganjie.image_editor.view.SelectDialog;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;
import com.yjing.imageeditlibrary.editimage.EditImageActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.hzw.doodle.DoodleActivity;
import cn.hzw.doodle.DoodleParams;
import cn.hzw.doodle.DoodleView;

public class MainActivity extends BaseActivity {
    @BindView(R.id.img_show)
    ImageView img_show;

    private String imgUrl;
    private static final int REQUEST_CODE_SELECT=1;
    public static final int ACTION_REQUEST_EDITIMAGE = 9;
    public static final int REQ_CODE_DOODLE = 101;
    public static final int IMAGE_EDIT = 200;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        initImagePick();
    }

    @OnClick({R.id.img_edit,R.id.img_photo})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.img_edit:
                if (TextUtils.isEmpty(imgUrl)){
                    Toast.makeText(this,"请先选择一张图片",Toast.LENGTH_LONG).show();
                }else {
//                    File outputFile= FileUtils.genEditFile();
//                    EditImageActivity.start(this,imgUrl,outputFile.getAbsolutePath(),ACTION_REQUEST_EDITIMAGE);
//                    // 涂鸦参数
//                    DoodleParams params = new DoodleParams();
//                    params.mIsFullScreen = true;
//                    // 图片路径
//                    params.mImagePath = imgUrl;
//                    // 初始画笔大小
//                    params.mPaintUnitSize = DoodleView.DEFAULT_SIZE;
//                    // 画笔颜色
//                    params.mPaintColor = Color.RED;
//                    // 是否支持缩放item
//                    params.mSupportScaleItem = true;
//                    // 启动涂鸦页面
//                    ImageEditActivity.startActivityForResult(MainActivity.this, params, REQ_CODE_DOODLE);
                    Intent intent=new Intent(this,ImageShowActivity.class);
                    intent.putExtra("imgUrl",imgUrl);
                    startActivityForResult(intent,IMAGE_EDIT);
                }
                break;
            case R.id.img_photo:
                getPhoto();
                break;
        }
    }

    private void initImagePick(){
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new PicassoImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(false);  //显示拍照按钮
        imagePicker.setCrop(false);        //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true); //是否按矩形区域保存
        imagePicker.setSelectLimit(9);    //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);   //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);  //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);//保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);//保存文件的高度。单位像素
    }

    private void getPhoto(){
        List<String> names = new ArrayList<>();
        names.add("拍照");
        names.add("相册");
        showDialog(new SelectDialog.SelectDialogListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: // 直接调起相机
                        //打开选择,本次允许选择的数量
                        ImagePicker.getInstance().setSelectLimit(1);
                        Intent intent = new Intent(MainActivity.this, ImageGridActivity.class);
                        intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
                        startActivityForResult(intent, REQUEST_CODE_SELECT);
                        break;
                    case 1:
                        //打开选择,本次允许选择的数量
                        ImagePicker.getInstance().setSelectLimit(1);
                        Intent intent1 = new Intent(MainActivity.this, ImageGridActivity.class);
                        /* 如果需要进入选择的时候显示已经选中的图片，
                         * 详情请查看ImagePickerActivity
                         * */
//                        intent1.putExtra(ImageGridActivity.EXTRAS_IMAGES,images);
                        startActivityForResult(intent1, REQUEST_CODE_SELECT);
                        break;
                    default:
                        break;
                }

            }
        }, names);
    }
    private SelectDialog showDialog(SelectDialog.SelectDialogListener listener, List<String> names) {
        SelectDialog dialog = new SelectDialog(this, R.style
                .transparentFrameWindowStyle,
                listener, names);
        if (!this.isFinishing()) {
            dialog.show();
        }
        return dialog;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == REQUEST_CODE_SELECT) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                imgUrl=images.get(0).path;
                Glide.with(this).load(imgUrl).into(img_show);
            } else {
                Toast.makeText(this, "没有数据", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode==ACTION_REQUEST_EDITIMAGE){
            String newFilePath = data.getStringExtra(EditImageActivity.SAVE_FILE_PATH);
            boolean isImageEdit = data.getBooleanExtra(EditImageActivity.IMAGE_IS_EDIT, false);

            if (isImageEdit) {
                Toast.makeText(this, "保存到路径: "+newFilePath, Toast.LENGTH_LONG).show();
            } else {
                newFilePath = imgUrl;
            }
            //System.out.println("newFilePath---->" + newFilePath);
            Log.d("image is edit", isImageEdit + "");
            Glide.with(this).load(newFilePath).into(img_show);
//            LoadImageTask loadTask = new LoadImageTask();
//            loadTask.execute(newFilePath);
        }
    }
}
