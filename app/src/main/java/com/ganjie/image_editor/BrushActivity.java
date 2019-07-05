package com.ganjie.image_editor;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;

import butterknife.BindView;
import butterknife.OnClick;
import cn.forward.androids.utils.ImageUtils;
import cn.forward.androids.utils.Util;
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

public class BrushActivity extends BaseActivity {

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
    @BindView(R.id.tv_edit)
    TextView tv_edit;
    private IDoodle mDoodle;
    private DoodleView mDoodleView;
    private DoodleOnTouchGestureListener mTouchGestureListener;
    private int color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_brush;
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
                if (selected) {
                    if (mLastPen == null) {
                        mLastPen = mDoodle.getPen();
                    }
                    if (mLastColor == null) {
                        mLastColor = mDoodle.getColor();
                    }
                    if (mSize == null) {
                        mSize = mDoodle.getSize();
                    }
                    mDoodleView.setEditMode(true);
                    mDoodle.setPen(selectableItem.getPen());
                    mDoodle.setColor(selectableItem.getColor());
                    mDoodle.setSize(selectableItem.getSize());
                    selectableItem.addItemListener(mIDoodleItemListener);
                } else {
                    selectableItem.removeItemListener(mIDoodleItemListener);

                    if (mTouchGestureListener.getSelectedItem() == null) { // nothing is selected. 当前没有选中任何一个item
                        if (mLastPen != null) {
                            mDoodle.setPen(mLastPen);
                            mLastPen = null;
                        }
                        if (mLastColor != null) {
                            mDoodle.setColor(mLastColor);
                            mLastColor = null;
                        }
                        if (mSize != null) {
                            mDoodle.setSize(mSize);
                            mSize = null;
                        }
                    }
                }
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
    @OnClick({R.id.color_selector,R.id.img_white,R.id.img_black,R.id.img_red,R.id.img_yellow,R.id.img_green,R.id.img_blue,R.id.img_purple,R.id.img_pink,R.id.return_last,R.id.pic_cancel,R.id.pic_sure,
    R.id.btn_arrow,R.id.btn_holl_rect,R.id.btn_holl_circle,R.id.btn_hand_write,R.id.tv_edit})
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
            case R.id.pic_sure:
                mDoodle.save();
                break;
            case R.id.pic_cancel:
                finish();
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
            case R.id.tv_edit:
                mDoodleView.setEditMode(!mDoodleView.isEditMode());
                if (mDoodleView.isEditMode()){
                    tv_edit.setTextColor(getResources().getColor(R.color.blue_mine));
                }else {
                    tv_edit.setTextColor(getResources().getColor(R.color.black));
                }
                break;
        }
    }
}
