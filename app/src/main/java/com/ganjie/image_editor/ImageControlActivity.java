package com.ganjie.image_editor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Transition;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ganjie.image_editor.fragment.BrushFragment;
import com.ganjie.image_editor.fragment.MosaicFragment;

import butterknife.BindView;
import butterknife.OnClick;
import cn.forward.androids.utils.ImageUtils;

public class ImageControlActivity extends BaseActivity {

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
    private FragmentManager manager;
    private FragmentTransaction transaction;
    private BrushFragment brushFragment;
    private MosaicFragment mosaicFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_image_control;
    }

    @Override
    public void initView() {
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        brushFragment = BrushFragment.newInstance();
        transaction.add(R.id.content,brushFragment,brushFragmentTag);
        transaction.commit();
        Intent intent=getIntent();
        String imgUrl=intent.getStringExtra("imgUrl");
        bitmap = ImageUtils.createBitmapFromPath(imgUrl, this);
        setBitmap(bitmap);
        Glide.with(this).load(imgUrl).into(img_show);


    }

    @OnClick({R.id.brush,R.id.prettify,R.id.text,R.id.mosaic,R.id.screenshot})
    public void onClick(View view){
        Intent intent=new Intent();
        switch (view.getId()){
            case R.id.brush:
                setSelection(toBrush);
                brush.setImageDrawable(getResources().getDrawable(R.drawable.brush));
                prettify.setImageDrawable(getResources().getDrawable(R.drawable.prettify_un));
                text.setImageDrawable(getResources().getDrawable(R.drawable.text_un));
                mosaic.setImageDrawable(getResources().getDrawable(R.drawable.mosaic_un));
                screenshot.setImageDrawable(getResources().getDrawable(R.drawable.screenshot_un));
                break;
            case R.id.prettify:
                setSelection(toEnhance);
                brush.setImageDrawable(getResources().getDrawable(R.drawable.brush_un));
                prettify.setImageDrawable(getResources().getDrawable(R.drawable.prettify));
                text.setImageDrawable(getResources().getDrawable(R.drawable.text_un));
                mosaic.setImageDrawable(getResources().getDrawable(R.drawable.mosaic_un));
                screenshot.setImageDrawable(getResources().getDrawable(R.drawable.screenshot_un));
                break;
            case R.id.text:
                setSelection(toText);
                brush.setImageDrawable(getResources().getDrawable(R.drawable.brush_un));
                prettify.setImageDrawable(getResources().getDrawable(R.drawable.prettify_un));
                text.setImageDrawable(getResources().getDrawable(R.drawable.text));
                mosaic.setImageDrawable(getResources().getDrawable(R.drawable.mosaic_un));
                screenshot.setImageDrawable(getResources().getDrawable(R.drawable.screenshot_un));
                break;
            case R.id.mosaic:
                setSelection(toMosaic);
                brush.setImageDrawable(getResources().getDrawable(R.drawable.brush_un));
                prettify.setImageDrawable(getResources().getDrawable(R.drawable.prettify_un));
                text.setImageDrawable(getResources().getDrawable(R.drawable.text_un));
                mosaic.setImageDrawable(getResources().getDrawable(R.drawable.mosaic));
                screenshot.setImageDrawable(getResources().getDrawable(R.drawable.screenshot_un));
                break;
            case R.id.screenshot:
                setSelection(toCrop);
                brush.setImageDrawable(getResources().getDrawable(R.drawable.brush_un));
                prettify.setImageDrawable(getResources().getDrawable(R.drawable.prettify_un));
                text.setImageDrawable(getResources().getDrawable(R.drawable.text_un));
                mosaic.setImageDrawable(getResources().getDrawable(R.drawable.mosaic_un));
                screenshot.setImageDrawable(getResources().getDrawable(R.drawable.screenshot));
                break;
        }
    }
    private String brushFragmentTag="brushFragmentTag";
    private String mosaicFragmentTag="mosaicFragmentTag";
    private void setSelection(int selection){
        FragmentTransaction transaction = manager.beginTransaction();
        hideFragment(transaction);
        if (1==selection){
            if (brushFragment!=null){
                transaction.show(brushFragment);
            }else {
                brushFragment = BrushFragment.newInstance();
                transaction.add(R.id.content,brushFragment,brushFragmentTag);
            }
        }else if (2==selection){
            if (mosaicFragment!=null){
                transaction.show(mosaicFragment);
            }else {
                mosaicFragment = MosaicFragment.newInstance();
                transaction.add(R.id.content,mosaicFragment,mosaicFragmentTag);
            }
        }
        transaction.commit();

    }

    private void hideFragment(FragmentTransaction transaction){
        if (brushFragment!=null){
            transaction.hide(brushFragment);
        }
        if (mosaicFragment!=null){
            transaction.hide(mosaicFragment);
        }
    }

    @Override
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public Bitmap getBitmap() {
        return bitmap;
    }
}
