package com.ganjie.image_editor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jarlen.photoedit.crop.CropImageType;
import cn.jarlen.photoedit.crop.CropImageView;

public class ImageCropActivity extends BaseActivity {

    @BindView(R.id.cropmageView)
    CropImageView cropImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_image_crop;
    }

    @Override
    public void initView() {
        Bitmap bitmap=getBitmap();
        Bitmap hh = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.crop_button);
        cropImage.setCropOverlayCornerBitmap(hh);
        cropImage.setImageBitmap(bitmap);
        cropImage.setGuidelines(CropImageType.CROPIMAGE_GRID_ON_TOUCH);// 触摸时显示网格
        cropImage.setFixedAspectRatio(false);// 自由剪切
    }

    @OnClick({R.id.pic_cancel2,R.id.pic_sure2,R.id.crop_o,R.id.crop_11,R.id.crop_43})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.pic_cancel2:
                finish();
                break;
            case R.id.pic_sure2:
                Bitmap bit = cropImage.getCroppedImage();
                setBitmap(bit);
                finish();
                break;
            case R.id.crop_o:
                cropImage.setFixedAspectRatio(false);
                break;
            case R.id.crop_11:
                cropImage.setFixedAspectRatio(true);
                cropImage.setAspectRatio(10, 10);
                break;
            case R.id.crop_43:
                cropImage.setFixedAspectRatio(true);
                cropImage.setAspectRatio(40, 30);
                break;
        }
    }
}
