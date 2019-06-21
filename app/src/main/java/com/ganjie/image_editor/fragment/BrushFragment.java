package com.ganjie.image_editor.fragment;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;
import cn.hzw.doodle.DoodleColor;
import cn.hzw.doodle.DoodleOnTouchGestureListener;
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
public class BrushFragment extends BaseFragment {
    @BindView(R.id.doodle_container)
    FrameLayout mFrameLayout;
    @BindView(R.id.ll_color_picker)
    LinearLayout ll_color_picker;
    @BindView(R.id.color_selector)
    ImageView color_selector;
    @BindView(R.id.btn_arrow)
    ImageView btn_arrow;
    @BindView(R.id.btn_holl_rect)
    ImageView btn_holl_rect;
    @BindView(R.id.btn_hand_write)
    ImageView btn_hand_write;
    @BindView(R.id.btn_holl_circle)
    ImageView btn_holl_circle;
    private IDoodle mDoodle;
    private DoodleView mDoodleView;
    private DoodleOnTouchGestureListener mTouchGestureListener;
    private int color;
    private ImageControlActivity imageControlActivity;
    private Bitmap bitmap;

    public static BrushFragment newInstance(){
        BrushFragment brushFragment=new BrushFragment();
        Bundle bundle=new Bundle();
        brushFragment.setArguments(bundle);
        return brushFragment;
    }


    public BrushFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_brush;
    }

    @Override
    protected void initView(View view) {
        imageControlActivity = (ImageControlActivity) getActivity();
        bitmap = imageControlActivity.getBitmap();
        mDoodle = mDoodleView = new DoodleView(imageControlActivity, bitmap, false, new IDoodleListener() {
            @Override
            public void onSaved(IDoodle doodle, Bitmap bitmap, Runnable callback) {
                imageControlActivity.setBitmap(bitmap);
                EventBus.getDefault().post(new MessageEvent("refresh"));
            }

            public void onError(int i, String msg) {
            }

            @Override
            public void onReady(IDoodle doodle) {

                // 设置初始值
                mDoodle.setSize(10);//画笔粗细
                // 选择画笔
                mDoodle.setPen(DoodlePen.BRUSH);
                mDoodle.setShape(DoodleShape.HAND_WRITE);
                mDoodle.setColor(new DoodleColor(Color.parseColor("#F1340E")));
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


    @OnClick({R.id.color_selector,R.id.img_white,R.id.img_black,R.id.img_red,R.id.img_yellow,R.id.img_green,R.id.img_blue,R.id.img_purple,R.id.img_pink,R.id.return_last,
            R.id.btn_arrow,R.id.btn_holl_rect,R.id.btn_holl_circle,R.id.btn_hand_write})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.color_selector:
                ll_color_picker.setVisibility(View.VISIBLE);
                break;
            case R.id.img_white:
                ll_color_picker.setVisibility(View.GONE);
                color_selector.setImageDrawable(getResources().getDrawable(R.drawable.color_white));
                mDoodle.setColor(new DoodleColor(Color.WHITE));
                color=Color.WHITE;
                break;
            case R.id.img_black:
                ll_color_picker.setVisibility(View.GONE);
                color_selector.setImageDrawable(getResources().getDrawable(R.drawable.color_black));
                mDoodle.setColor(new DoodleColor(Color.BLACK));
                color=Color.BLACK;
                break;
            case R.id.img_red:
                ll_color_picker.setVisibility(View.GONE);
                color_selector.setImageDrawable(getResources().getDrawable(R.drawable.color_selector));
                mDoodle.setColor(new DoodleColor(Color.parseColor("#F1340E")));
                color=Color.parseColor("#F1340E");
                break;
            case R.id.img_yellow:
                ll_color_picker.setVisibility(View.GONE);
                color_selector.setImageDrawable(getResources().getDrawable(R.drawable.color_yellow));
                mDoodle.setColor(new DoodleColor(Color.parseColor("#FCB549")));
                color=Color.parseColor("#FCB549");
                break;
            case R.id.img_green:
                ll_color_picker.setVisibility(View.GONE);
                color_selector.setImageDrawable(getResources().getDrawable(R.drawable.color_green));
                mDoodle.setColor(new DoodleColor(Color.parseColor("#00D14D")));
                color=Color.parseColor("#00D14D");
                break;
            case R.id.img_blue:
                ll_color_picker.setVisibility(View.GONE);
                color_selector.setImageDrawable(getResources().getDrawable(R.drawable.color_blue));
                mDoodle.setColor(new DoodleColor(Color.parseColor("#1879FB")));
                color=Color.parseColor("#1879FB");
                break;
            case R.id.img_purple:
                ll_color_picker.setVisibility(View.GONE);
                color_selector.setImageDrawable(getResources().getDrawable(R.drawable.color_purple));
                mDoodle.setColor(new DoodleColor(Color.parseColor("#8F57FA")));
                color=Color.parseColor("#8F57FA");
                break;
            case R.id.img_pink:
                ll_color_picker.setVisibility(View.GONE);
                color_selector.setImageDrawable(getResources().getDrawable(R.drawable.color_pink));
                mDoodle.setColor(new DoodleColor(Color.parseColor("#F524B6")));
                color=Color.parseColor("#F524B6");
                break;
            case R.id.return_last:
                mDoodle.undo();
                break;
            case R.id.btn_arrow:
                mDoodle.setSize(20);//画笔粗细
                mDoodle.setShape(DoodleShape.ARROW);
                btn_hand_write.setImageDrawable(getResources().getDrawable(R.drawable.line_un));
                btn_arrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow));
                btn_holl_circle.setImageDrawable(getResources().getDrawable(R.drawable.circular_un));
                btn_holl_rect.setImageDrawable(getResources().getDrawable(R.drawable.rectangle_un));
                break;
            case R.id.btn_holl_rect:
                mDoodle.setSize(10);//画笔粗细
                mDoodle.setShape(DoodleShape.HOLLOW_RECT);
                btn_hand_write.setImageDrawable(getResources().getDrawable(R.drawable.line_un));
                btn_arrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_un));
                btn_holl_circle.setImageDrawable(getResources().getDrawable(R.drawable.circular_un));
                btn_holl_rect.setImageDrawable(getResources().getDrawable(R.drawable.rectangle));
                break;
            case R.id.btn_holl_circle:
                mDoodle.setSize(10);//画笔粗细
                mDoodle.setShape(DoodleShape.HOLLOW_CIRCLE);
                btn_hand_write.setImageDrawable(getResources().getDrawable(R.drawable.line_un));
                btn_arrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_un));
                btn_holl_circle.setImageDrawable(getResources().getDrawable(R.drawable.circular));
                btn_holl_rect.setImageDrawable(getResources().getDrawable(R.drawable.rectangle_un));
                break;
            case R.id.btn_hand_write:
                mDoodle.setSize(10);//画笔粗细
                mDoodle.setShape(DoodleShape.HAND_WRITE);
                btn_hand_write.setImageDrawable(getResources().getDrawable(R.drawable.line));
                btn_arrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_un));
                btn_holl_circle.setImageDrawable(getResources().getDrawable(R.drawable.circular_un));
                btn_holl_rect.setImageDrawable(getResources().getDrawable(R.drawable.rectangle_un));
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

//    public void onHide(){
//        if (mDoodle!=null){
//            mDoodle.save();
//        }
//    }

}
