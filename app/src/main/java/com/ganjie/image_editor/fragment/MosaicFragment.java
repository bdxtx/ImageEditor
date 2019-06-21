package com.ganjie.image_editor.fragment;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ganjie.image_editor.BaseFragment;
import com.ganjie.image_editor.ImageControlActivity;
import com.ganjie.image_editor.MessageEvent;
import com.ganjie.image_editor.R;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class MosaicFragment extends BaseFragment {
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
    private Bitmap bitmap;
    private ImageControlActivity imageControlActivity;

    public static MosaicFragment newInstance(){
        MosaicFragment mosaicFragment=new MosaicFragment();
        return mosaicFragment;
    }

    public MosaicFragment() {
        // Required empty public constructor
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mosaic;
    }

    @Override
    protected void initView(View view) {
        imageControlActivity = (ImageControlActivity) getActivity();
        bitmap = imageControlActivity.getBitmap();
        mDoodle = mDoodleView = new DoodleView(imageControlActivity, bitmap, false, new IDoodleListener() {
            @Override
            public void onSaved(IDoodle doodle, Bitmap bitmap, Runnable callback) {
                imageControlActivity.setBitmap(bitmap);
            }

            public void onError(int i, String msg) {
            }

            @Override
            public void onReady(IDoodle doodle) {

                // 设置初始值
                mDoodle.setSize(50);//画笔粗细
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
        IDoodleTouchDetector detector = new DoodleTouchDetector(getActivity(), mTouchGestureListener);
        mDoodleView.setDefaultTouchDetector(detector);

        mDoodle.setIsDrawableOutside(false);
        mFrameLayout.addView(mDoodleView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @OnClick({R.id.mosaic_one,R.id.mosaic_two,R.id.return_last})
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
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden){
            mDoodle.save();
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MessageEvent messageEvent){
        if ("refresh".equals(messageEvent)){
            bitmap = imageControlActivity.getBitmap();
        }
    }

}
