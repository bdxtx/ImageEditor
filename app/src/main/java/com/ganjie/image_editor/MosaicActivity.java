package com.ganjie.image_editor;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.OnClick;
import cn.hzw.doodle.DoodleColor;
import cn.hzw.doodle.DoodleOnTouchGestureListener;
import cn.hzw.doodle.DoodlePath;
import cn.hzw.doodle.DoodlePen;
import cn.hzw.doodle.DoodleShape;
import cn.hzw.doodle.DoodleTouchDetector;
import cn.hzw.doodle.DoodleView;
import cn.hzw.doodle.IDoodleListener;
import cn.hzw.doodle.core.IDoodle;
import cn.hzw.doodle.core.IDoodleColor;
import cn.hzw.doodle.core.IDoodleItemListener;
import cn.hzw.doodle.core.IDoodlePen;
import cn.hzw.doodle.core.IDoodleSelectableItem;
import cn.hzw.doodle.core.IDoodleTouchDetector;

public class MosaicActivity extends BaseActivity {
    @BindView(R.id.doodle_container)
    FrameLayout mFrameLayout;
    @BindView(R.id.mosaic_one)
    ImageView mosaic_one;
    @BindView(R.id.mosaic_two)
    ImageView mosaic_two;
    @BindView(R.id.mosaic_three)
    ImageView mosaic_three;
    @BindView(R.id.mosaic_menu)
    LinearLayout mosaic_menu;


    private IDoodle mDoodle;
    private DoodleView mDoodleView;
    private DoodleOnTouchGestureListener mTouchGestureListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_mosaic;
    }

    @Override
    public void initView() {
        Bitmap bitmap=getBitmap();
        mDoodle = mDoodleView = new DoodleView(this, bitmap, false, new IDoodleListener() {
            @Override
            public void onSaved(IDoodle doodle, Bitmap bitmap, Runnable callback) {
                setBitmap(bitmap);
                finish();
            }

            public void onError(int i, String msg) {
            }

            @Override
            public void onReady(IDoodle doodle) {

                // 设置初始值
                mDoodle.setSize(150);//画笔粗细
                // 选择画笔
                mDoodle.setPen(DoodlePen.MOSAIC);
                mDoodle.setShape(DoodleShape.HAND_WRITE);
                mDoodle.setColor(DoodlePath.getMosaicColor(mDoodle, DoodlePath.MOSAIC_LEVEL_2));
            }
        }, null);
        mTouchGestureListener = new DoodleOnTouchGestureListener(mDoodleView, new DoodleOnTouchGestureListener.ISelectionListener() {
            // save states before being selected
            IDoodlePen mLastPen = null;
            IDoodleColor mLastColor = null;
            Float mSize = null;

            IDoodleItemListener mIDoodleItemListener = new IDoodleItemListener() {
                @Override
                public void onPropertyChanged(int property) {
                }
            };

            @Override
            public void onSelectedItem(IDoodle doodle, IDoodleSelectableItem selectableItem, boolean selected) {
            }

            @Override
            public void onCreateSelectableItem(IDoodle doodle, float x, float y) {
            }
        }) {
            @Override
            public void setSupportScaleItem(boolean supportScaleItem) {
                super.setSupportScaleItem(supportScaleItem);
            }
        };
        IDoodleTouchDetector detector = new DoodleTouchDetector(getApplicationContext(), mTouchGestureListener);
        mDoodleView.setDefaultTouchDetector(detector);

        mDoodle.setIsDrawableOutside(false);
        mFrameLayout.addView(mDoodleView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }
    @OnClick({R.id.mosaic_one,R.id.mosaic_two,R.id.return_last,R.id.pic_sure,R.id.pic_cancel})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.mosaic_one:
                mosaic_one.setImageDrawable(getResources().getDrawable(R.drawable.mosaic_one));
                mosaic_two.setImageDrawable(getResources().getDrawable(R.drawable.mosaic_two_un));
                mDoodle.setColor(DoodlePath.getMosaicColor(mDoodle, DoodlePath.MOSAIC_LEVEL_2));
                break;
            case R.id.mosaic_two:
                mosaic_one.setImageDrawable(getResources().getDrawable(R.drawable.mosaic_one_un));
                mosaic_two.setImageDrawable(getResources().getDrawable(R.drawable.mosaic_two));
                mDoodle.setColor(DoodlePath.getMosaicColor(mDoodle, DoodlePath.MOSAIC_LEVEL_1));
                break;
            case R.id.return_last:
                mDoodle.undo();
                break;
            case R.id.pic_sure:
                mDoodle.save();
                break;
            case R.id.pic_cancel:
                finish();
                break;
        }
    }
}
