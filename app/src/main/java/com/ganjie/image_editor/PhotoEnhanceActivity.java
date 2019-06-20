package com.ganjie.image_editor;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jarlen.photoedit.enhance.PhotoEnhance;

public class PhotoEnhanceActivity extends BaseActivity {
    @BindView(R.id.pic_control)
    LinearLayout pic_control;
    @BindView(R.id.tv_pic_text)
    TextView tv_pic_text;
    @BindView(R.id.enhance_seekbar)
    SeekBar enhance_seekbar;
    @BindView(R.id.enhancePicture)
    ImageView enhancePicture;
    private int selectPicControl;
    public final int DEFAULT_PROGRESS = 101;
    private PhotoEnhance pe;
    private Bitmap bitmap;
    private Bitmap thisBitmap;
    private int progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_photo_enhance;
    }

    @Override
    public void initView() {
        bitmap = getBitmap();
        Glide.with(this).load(bitmap).into(enhancePicture);
        thisBitmap=bitmap.copy(bitmap.getConfig(), true);
        pe = new PhotoEnhance(thisBitmap);
        enhance_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                PhotoEnhanceActivity.this.progress=progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                pe.setSaturation(progress);
                thisBitmap = pe.handleImage(selectPicControl);
                Glide.with(PhotoEnhanceActivity.this).load(thisBitmap).into(enhancePicture);
            }
        });

    }
    @OnClick({R.id.pic_one,R.id.pic_two,R.id.pic_three,R.id.pic_four,R.id.pic_sure,R.id.pic_cancel})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.pic_one:
                pic_control.setVisibility(View.VISIBLE);
                tv_pic_text.setText("亮度");
                enhance_seekbar.setProgress(DEFAULT_PROGRESS);
                selectPicControl=1;
                break;
            case R.id.pic_two:
                pic_control.setVisibility(View.VISIBLE);
                tv_pic_text.setText("对比度");
                enhance_seekbar.setProgress(DEFAULT_PROGRESS);
                selectPicControl=2;
                break;
            case R.id.pic_three:
                pic_control.setVisibility(View.VISIBLE);
                tv_pic_text.setText("饱和度");
                enhance_seekbar.setProgress(DEFAULT_PROGRESS);
                selectPicControl=3;
                break;
            case R.id.pic_four:
                pic_control.setVisibility(View.VISIBLE);
                tv_pic_text.setText("锐化");
                enhance_seekbar.setProgress(DEFAULT_PROGRESS);
                selectPicControl=4;
                break;
            case R.id.pic_sure:
                setBitmap(thisBitmap);
                finish();
                break;
            case R.id.pic_cancel:
                finish();
                break;
        }
    }
}
