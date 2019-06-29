package com.ganjie.image_editor;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;

import butterknife.BindView;
import butterknife.OnClick;
import cn.forward.androids.utils.ImageUtils;
import cn.forward.androids.utils.Util;
import cn.hzw.doodle.DoodleColor;
import cn.hzw.doodle.DoodlePath;
import cn.hzw.doodle.DoodlePen;
import cn.hzw.doodle.DoodleView;

public class ImageShowActivity extends BaseActivity {
    @BindView(R.id.img_show)
    ImageView img_show;
    @BindView(R.id.brush)
    ImageView brush;
    @BindView(R.id.prettify)
    ImageView prettify;
    @BindView(R.id.text)
    ImageView text;
    @BindView(R.id.mosaic)
    ImageView mosaic;
    @BindView(R.id.screenshot)
    ImageView screenshot;

    private Bitmap bitmap;

    private static int toBrush=1;
    private static int toMosaic=2;
    private static int toText=3;
    private static int toEnhance=4;
    private static int toCrop=5;
    public static final String KEY_IMAGE_PATH = "key_image_path";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_image_show;
    }

    @Override
    public void initView() {
        Intent intent=getIntent();
        String imgUrl=intent.getStringExtra("imgUrl");
        bitmap = ImageUtils.createBitmapFromPath(imgUrl, this);
        setBitmap(bitmap);
        Glide.with(this).load(imgUrl).into(img_show);
    }
    @OnClick({R.id.brush,R.id.prettify,R.id.text,R.id.mosaic,R.id.screenshot,R.id.save_btn})
    public void onClick(View view){
        Intent intent=new Intent();
        switch (view.getId()){
            case R.id.brush:
                intent.setClass(this,BrushActivity.class);
                startActivityForResult(intent,toBrush);
                brush.setImageDrawable(getResources().getDrawable(R.drawable.brush));
                prettify.setImageDrawable(getResources().getDrawable(R.drawable.prettify_un));
                text.setImageDrawable(getResources().getDrawable(R.drawable.text_un));
                mosaic.setImageDrawable(getResources().getDrawable(R.drawable.mosaic_un));
                screenshot.setImageDrawable(getResources().getDrawable(R.drawable.screenshot_un));
                break;
            case R.id.prettify:
                intent.setClass(this,PhotoEnhanceActivity.class);
                startActivityForResult(intent,toEnhance);
                brush.setImageDrawable(getResources().getDrawable(R.drawable.brush_un));
                prettify.setImageDrawable(getResources().getDrawable(R.drawable.prettify));
                text.setImageDrawable(getResources().getDrawable(R.drawable.text_un));
                mosaic.setImageDrawable(getResources().getDrawable(R.drawable.mosaic_un));
                screenshot.setImageDrawable(getResources().getDrawable(R.drawable.screenshot_un));
                break;
            case R.id.text:
                intent.setClass(this,AddTextActivity.class);
                startActivityForResult(intent,toText);
                brush.setImageDrawable(getResources().getDrawable(R.drawable.brush_un));
                prettify.setImageDrawable(getResources().getDrawable(R.drawable.prettify_un));
                text.setImageDrawable(getResources().getDrawable(R.drawable.text));
                mosaic.setImageDrawable(getResources().getDrawable(R.drawable.mosaic_un));
                screenshot.setImageDrawable(getResources().getDrawable(R.drawable.screenshot_un));
                break;
            case R.id.mosaic:
                intent.setClass(this,MosaicActivity.class);
                startActivityForResult(intent,toMosaic);
                brush.setImageDrawable(getResources().getDrawable(R.drawable.brush_un));
                prettify.setImageDrawable(getResources().getDrawable(R.drawable.prettify_un));
                text.setImageDrawable(getResources().getDrawable(R.drawable.text_un));
                mosaic.setImageDrawable(getResources().getDrawable(R.drawable.mosaic));
                screenshot.setImageDrawable(getResources().getDrawable(R.drawable.screenshot_un));
                break;
            case R.id.screenshot:
                intent.setClass(this,ImageCropActivity.class);
                startActivityForResult(intent,toCrop);
                brush.setImageDrawable(getResources().getDrawable(R.drawable.brush_un));
                prettify.setImageDrawable(getResources().getDrawable(R.drawable.prettify_un));
                text.setImageDrawable(getResources().getDrawable(R.drawable.text_un));
                mosaic.setImageDrawable(getResources().getDrawable(R.drawable.mosaic_un));
                screenshot.setImageDrawable(getResources().getDrawable(R.drawable.screenshot));
                break;
            case R.id.save_btn:
                save();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        bitmap=getBitmap();
        Glide.with(this).load(bitmap).into(img_show);
    }


    private void save(){
        File doodleFile = null;
        File file = null;
        String savePath = FileUtils.genEditFile().getAbsolutePath();
        boolean isDir = true;
        if (TextUtils.isEmpty(savePath)) {
            File dcimFile = new File(Environment.getExternalStorageDirectory(), "DCIM");
            doodleFile = new File(dcimFile, "Doodle");
            //　保存的路径
            file = new File(doodleFile, System.currentTimeMillis() + ".jpg");
        } else {
            if (isDir) {
                doodleFile = new File(savePath);
                //　保存的路径
                file = new File(doodleFile, System.currentTimeMillis() + ".jpg");
            } else {
                file = new File(savePath);
                doodleFile = file.getParentFile();
            }
        }
        doodleFile.mkdirs();

        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream);
            ImageUtils.addImage(getContentResolver(), file.getAbsolutePath());
            Intent intent = new Intent();
            intent.putExtra(KEY_IMAGE_PATH, file.getAbsolutePath());
            setResult(Activity.RESULT_OK, intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
//            onError(DoodleView.ERROR_SAVE, e.getMessage());
        } finally {
            Util.closeQuietly(outputStream);
        }
    }
}
