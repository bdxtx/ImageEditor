package com.ganjie.image_editor;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.hzw.doodle.DoodleBitmap;
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
import cn.jarlen.photoedit.crop.Handle;

public class AddTextActivity extends BaseActivity implements TextDialog.OnclickListener {
    @BindView(R.id.doodle_container)
    FrameLayout mFrameLayout;
    @BindView(R.id.edit_bg)
    RelativeLayout edit_bg;
    @BindView(R.id.doodle_selectable_edit_container)
    LinearLayout doodle_selectable_edit_container;


    private IDoodle mDoodle;
    private DoodleView mDoodleView;
    private DoodleOnTouchGestureListener mTouchGestureListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_add_text;
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
            IDoodlePen mLastPen = null;
            IDoodleColor mLastColor = null;
            Float mSize = null;

            IDoodleItemListener mIDoodleItemListener = new IDoodleItemListener() {
                @Override
                public void onPropertyChanged(int property) {
                    if (mTouchGestureListener.getSelectedItem() == null) {
                        return;
                    }
                    if (property == IDoodleItemListener.PROPERTY_SCALE) {
//                        mItemScaleTextView.setText(
//                                (int) (mTouchGestureListener.getSelectedItem().getScale() * 100 + 0.5f) + "%");
                    }
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
//                        mSelectedEditContainer.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCreateSelectableItem(IDoodle doodle, float x, float y) {
//                if (mDoodle.getPen() == DoodlePen.TEXT) {
//                } else if (mDoodle.getPen() == DoodlePen.BITMAP) {
//                    createDoodleBitmap(null, x, y);
//                }
            }
        }) {
            @Override
            public void setSupportScaleItem(boolean supportScaleItem) {
                super.setSupportScaleItem(supportScaleItem);
//                if (supportScaleItem) {
//                    mItemScaleTextView.setVisibility(View.VISIBLE);
//                } else {
//                    mItemScaleTextView.setVisibility(View.GONE);
//                }
            }
        };
        IDoodleTouchDetector detector = new DoodleTouchDetector(getApplicationContext(), mTouchGestureListener);
        mDoodleView.setDefaultTouchDetector(detector);

        mDoodle.setIsDrawableOutside(false);
        mFrameLayout.addView(mDoodleView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                TextDialog textDialog=new TextDialog(AddTextActivity.this,AddTextActivity.this);
                textDialog.showAtLocation(edit_bg, Gravity.BOTTOM,0,0);
            }
        },500);
    }


    @Override
    public void sure(Bitmap bitmap) {
        IDoodleSelectableItem item = new DoodleBitmap(mDoodle, bitmap, 200, 100, 100);
        mDoodle.addItem(item);
        mTouchGestureListener.setSelectedItem(item);
    }
    @OnClick({R.id.tv_add,R.id.pic_sure,R.id.pic_cancel,R.id.tv_delete})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.tv_add:
                TextDialog textDialog=new TextDialog(this,this);
                textDialog.showAtLocation(edit_bg, Gravity.BOTTOM,0,0);
                break;
            case R.id.pic_sure:
                mDoodle.save();
                break;
            case R.id.pic_cancel:
                finish();
                break;
            case R.id.tv_delete:
                mDoodle.removeItem(mTouchGestureListener.getSelectedItem());
                mTouchGestureListener.setSelectedItem(null);
                break;
        }
    }
}
