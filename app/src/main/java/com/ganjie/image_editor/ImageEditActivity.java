package com.ganjie.image_editor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.muzhi.camerasdk.library.utils.PhotoEnhance;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.forward.androids.utils.ImageUtils;
import cn.forward.androids.utils.LogUtil;
import cn.forward.androids.utils.Util;
import cn.hzw.doodle.DoodleActivity;
import cn.hzw.doodle.DoodleBitmap;
import cn.hzw.doodle.DoodleColor;
import cn.hzw.doodle.DoodleOnTouchGestureListener;
import cn.hzw.doodle.DoodleParams;
import cn.hzw.doodle.DoodlePath;
import cn.hzw.doodle.DoodlePen;
import cn.hzw.doodle.DoodleShape;
import cn.hzw.doodle.DoodleText;
import cn.hzw.doodle.DoodleTouchDetector;
import cn.hzw.doodle.DoodleView;
import cn.hzw.doodle.IDoodleListener;
import cn.hzw.doodle.core.IDoodle;
import cn.hzw.doodle.core.IDoodleColor;
import cn.hzw.doodle.core.IDoodleItemListener;
import cn.hzw.doodle.core.IDoodlePen;
import cn.hzw.doodle.core.IDoodleSelectableItem;
import cn.hzw.doodle.core.IDoodleShape;
import cn.hzw.doodle.core.IDoodleTouchDetector;
import cn.hzw.doodle.dialog.DialogController;
import cn.hzw.doodle.imagepicker.ImageSelectorView;
import cn.jarlen.photoedit.crop.CropImageType;
import cn.jarlen.photoedit.crop.CropImageView;

public class ImageEditActivity extends BaseActivity implements TextDialog.OnclickListener {

    public static final String TAG = "Doodle";
    public final static int DEFAULT_MOSAIC_SIZE = 20; // 默认马赛克大小
    public final static int DEFAULT_COPY_SIZE = 20; // 默认仿制大小
    public final static int DEFAULT_TEXT_SIZE = 18; // 默认文字大小
    public final static int DEFAULT_BITMAP_SIZE = 80; // 默认贴图大小

    public static final int RESULT_ERROR = -111; // 出现错误
    public static final int INPUT = 303; // 出现错误

    public final int DEFAULT_PROGRESS = 101;
    private int mBProgress, mCProgress, mSProgress;

    @BindView(R.id.ll_color_picker)
    LinearLayout ll_color_picker;
    @BindView(R.id.menu)
    LinearLayout menu;
    @BindView(R.id.mosaic_menu)
    LinearLayout mosaic_menu;
    @BindView(R.id.pic_menu)
    LinearLayout pic_menu;
    @BindView(R.id.pic_control)
    LinearLayout pic_control;
    @BindView(R.id.crop_menu)
    LinearLayout crop_menu;
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
    @BindView(R.id.img_show)
    ImageView img_show;
    @BindView(R.id.edit_bg)
    RelativeLayout edit_bg;
    @BindView(R.id.tv_pic_text)
    TextView tv_pic_text;
    @BindView(R.id.enhance_seekbar)
    SeekBar enhance_seekbar;
    @BindView(R.id.cropmageView)
    CropImageView cropImage;

    private int color;
    private Bitmap bitmap;

    private int selectPicControl;
    private Bitmap mDoodleBitmap;

    /**
     * 启动涂鸦界面
     *
     * @param activity
     * @param params      涂鸦参数
     * @param requestCode startActivityForResult的请求码
     * @see DoodleParams
     */
    public static void startActivityForResult(Activity activity, DoodleParams params, int requestCode) {
        Intent intent = new Intent(activity, ImageEditActivity.class);
        intent.putExtra(KEY_PARAMS, params);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 启动涂鸦界面
     *
     * @param activity
     * @param imagePath   　图片路径
     * @param savePath    　保存路径
     * @param isDir       　保存路径是否为目录
     * @param requestCode 　startActivityForResult的请求码
     */
    @Deprecated
    public static void startActivityForResult(Activity activity, String imagePath, String savePath, boolean isDir, int requestCode) {
        DoodleParams params = new DoodleParams();
        params.mImagePath = imagePath;
        params.mSavePath = savePath;
        params.mSavePathIsDir = isDir;
        startActivityForResult(activity, params, requestCode);
    }

    /**
     *
     * @param activity
     * @param imagePath
     * @param requestCode
     */
    @Deprecated
    public static void startActivityForResult(Activity activity, String imagePath, int requestCode) {
        DoodleParams params = new DoodleParams();
        params.mImagePath = imagePath;
        startActivityForResult(activity, params, requestCode);
    }

    public static final String KEY_PARAMS = "key_doodle_params";
    public static final String KEY_IMAGE_PATH = "key_image_path";

    private String mImagePath;
    @BindView(R.id.doodle_container)
    FrameLayout mFrameLayout;
    private IDoodle mDoodle;
    private DoodleView mDoodleView;

    private TextView mPaintSizeView;

    private View mBtnHidePanel, mSettingsPanel;
    private View mSelectedEditContainer;
    private TextView mItemScaleTextView;
    private View mBtnColor, mColorContainer;
    private View mShapeContainer, mPenContainer, mSizeContainer;
    private View mBtnUndo;
    private View mMosaicMenu;
    private View mEditBtn;

    private AlphaAnimation mViewShowAnimation, mViewHideAnimation; // view隐藏和显示时用到的渐变动画

    private DoodleParams mDoodleParams;

    // 触摸屏幕超过一定时间才判断为需要隐藏设置面板
    private Runnable mHideDelayRunnable;
    // 触摸屏幕超过一定时间才判断为需要显示设置面板
    private Runnable mShowDelayRunnable;

    private DoodleOnTouchGestureListener mTouchGestureListener;
    private Map<IDoodlePen, Float> mPenSizeMap = new HashMap<>(); //保存每个画笔对应的最新大小

    private int mMosaicLevel = -1;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_PARAMS, mDoodleParams);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
        mDoodleParams = savedInstanceState.getParcelable(KEY_PARAMS);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_image_edit;
    }

    @Override
    public void initView() {
        if (mDoodleParams == null) {
            mDoodleParams = getIntent().getExtras().getParcelable(KEY_PARAMS);
        }
        if (mDoodleParams == null) {
            LogUtil.e("TAG", "mDoodleParams is null!");
            this.finish();
            return;
        }

        mImagePath = mDoodleParams.mImagePath;
        if (mImagePath == null) {
            LogUtil.e("TAG", "mImagePath is null!");
            this.finish();
            return;
        }
        bitmap = ImageUtils.createBitmapFromPath(mImagePath, this);
        Glide.with(this).load(bitmap).into(img_show);
        if (bitmap == null) {
            LogUtil.e("TAG", "bitmap is null!");
            this.finish();
            return;
        }

         /*
        Whether or not to optimize drawing, it is suggested to open, which can optimize the drawing speed and performance.
        Note: When item is selected for editing after opening, it will be drawn at the top level, and not at the corresponding level until editing is completed.
        是否优化绘制，建议开启，可优化绘制速度和性能.
        注意：开启后item被选中编辑时时会绘制在最上面一层，直到结束编辑后才绘制在相应层级
         */
        mDoodle = mDoodleView = new DoodleView(this, bitmap, mDoodleParams.mOptimizeDrawing, new IDoodleListener() {
            @Override
            public void onSaved(IDoodle doodle, Bitmap bitmap, Runnable callback) { // 保存图片为jpg格式
                File doodleFile = null;
                File file = null;
                String savePath = mDoodleParams.mSavePath;
                boolean isDir = mDoodleParams.mSavePathIsDir;
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
                    onError(DoodleView.ERROR_SAVE, e.getMessage());
                } finally {
                    Util.closeQuietly(outputStream);
                }
            }

            public void onError(int i, String msg) {
                setResult(RESULT_ERROR);
                finish();
            }

            @Override
            public void onReady(IDoodle doodle) {
//                mEditSizeSeekBar.setMax(Math.min(mDoodleView.getWidth(), mDoodleView.getHeight()));

                float size = mDoodleParams.mPaintUnitSize > 0 ? mDoodleParams.mPaintUnitSize * mDoodle.getUnitSize() : 0;
                if (size <= 0) {
                    size = mDoodleParams.mPaintPixelSize > 0 ? mDoodleParams.mPaintPixelSize : mDoodle.getSize();
                }
                // 设置初始值
                mDoodle.setSize(size);
                // 选择画笔
                mDoodle.setPen(DoodlePen.BRUSH);
                mDoodle.setShape(DoodleShape.HAND_WRITE);
                mDoodle.setColor(new DoodleColor(mDoodleParams.mPaintColor));
                color=mDoodleParams.mPaintColor;
                if (mDoodleParams.mZoomerScale <= 0) {
                    findViewById(cn.hzw.doodle.R.id.btn_zoomer).setVisibility(View.GONE);
                }
                mDoodle.setZoomerScale(mDoodleParams.mZoomerScale);
                mTouchGestureListener.setSupportScaleItem(mDoodleParams.mSupportScaleItem);

                // 每个画笔的初始值
                mPenSizeMap.put(DoodlePen.BRUSH, mDoodle.getSize());
                mPenSizeMap.put(DoodlePen.MOSAIC, DEFAULT_MOSAIC_SIZE * mDoodle.getUnitSize());
                mPenSizeMap.put(DoodlePen.COPY, DEFAULT_COPY_SIZE * mDoodle.getUnitSize());
                mPenSizeMap.put(DoodlePen.ERASER, mDoodle.getSize());
                mPenSizeMap.put(DoodlePen.TEXT, DEFAULT_TEXT_SIZE * mDoodle.getUnitSize());
                mPenSizeMap.put(DoodlePen.BITMAP, DEFAULT_BITMAP_SIZE * mDoodle.getUnitSize());
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
                        mSelectedEditContainer.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCreateSelectableItem(IDoodle doodle, float x, float y) {
                if (mDoodle.getPen() == DoodlePen.TEXT) {
                    createDoodleText(null, x, y);
                } else if (mDoodle.getPen() == DoodlePen.BITMAP) {
                    createDoodleBitmap(null, x, y);
                }
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

        mDoodle.setIsDrawableOutside(mDoodleParams.mIsDrawableOutside);
        mFrameLayout.addView(mDoodleView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mDoodle.setDoodleMinScale(mDoodleParams.mMinScale);
        mDoodle.setDoodleMaxScale(mDoodleParams.mMaxScale);

        mBProgress = mCProgress = mSProgress = DEFAULT_PROGRESS;
        mDoodleBitmap = bitmap.copy(bitmap.getConfig(), true);
        PhotoEnhance mPhotoEnhance = new PhotoEnhance(mDoodleBitmap);
        enhance_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch (selectPicControl) {
                    case 1:
                        mPhotoEnhance.setBrightness(progress);
                        mDoodleBitmap = mPhotoEnhance.handleImage(mPhotoEnhance.Enhance_Brightness);
                        break;
                    case 2:
                        mPhotoEnhance.setContrast(progress);
                        mDoodleBitmap = mPhotoEnhance.handleImage(mPhotoEnhance.Enhance_Contrast);
                        break;
                    case 3:
                        mPhotoEnhance.setSaturation(progress);
                        mDoodleBitmap = mPhotoEnhance.handleImage(mPhotoEnhance.Enhance_Saturation);
                        break;
                    case 4:
                        mDoodleBitmap=sharpenImageAmeliorate(bitmap,progress);
                        break;
                }
                if (mDoodleBitmap != null) {
                    Glide.with(ImageEditActivity.this).load(mDoodleBitmap).into(img_show);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        Bitmap hh = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.crop_button);
        cropImage.setCropOverlayCornerBitmap(hh);
        cropImage.setImageBitmap(bitmap);
        cropImage.setGuidelines(CropImageType.CROPIMAGE_GRID_ON_TOUCH);// 触摸时显示网格

        cropImage.setFixedAspectRatio(false);// 自由剪切




    }

    private boolean canChangeColor(IDoodlePen pen) {
        return pen != DoodlePen.ERASER
                && pen != DoodlePen.BITMAP
                && pen != DoodlePen.COPY
                && pen != DoodlePen.MOSAIC;
    }

    // 添加文字
    private void createDoodleText(final DoodleText doodleText, final float x, final float y) {
        if (isFinishing()) {
            return;
        }

        DialogController.showInputTextDialog(this, doodleText == null ? null : doodleText.getText(), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = (v.getTag() + "").trim();
                if (TextUtils.isEmpty(text)) {
                    return;
                }
                if (doodleText == null) {
                    IDoodleSelectableItem item = new DoodleText(mDoodle, text, mDoodle.getSize(), mDoodle.getColor().copy(), x, y);
                    mDoodle.addItem(item);
                    mTouchGestureListener.setSelectedItem(item);
                } else {
                    doodleText.setText(text);
                }
                mDoodle.refresh();
            }
        }, null);
        if (doodleText == null) {
            mSettingsPanel.removeCallbacks(mHideDelayRunnable);
        }
    }

    // 添加贴图
    private void createDoodleBitmap(final DoodleBitmap doodleBitmap, final float x, final float y) {
        DialogController.showSelectImageDialog(this, new ImageSelectorView.ImageSelectorListener() {
            @Override
            public void onCancel() {
            }

            @Override
            public void onEnter(List<String> pathList) {
                Bitmap bitmap = ImageUtils.createBitmapFromPath(pathList.get(0), mDoodleView.getWidth() / 4, mDoodleView.getHeight() / 4);

                if (doodleBitmap == null) {
                    IDoodleSelectableItem item = new DoodleBitmap(mDoodle, bitmap, mDoodle.getSize(), x, y);
                    mDoodle.addItem(item);
                    mTouchGestureListener.setSelectedItem(item);
                } else {
                    doodleBitmap.setBitmap(bitmap);
                }
                mDoodle.refresh();
            }
        });
    }
    @OnClick({R.id.color_selector,R.id.img_white,R.id.img_black,R.id.img_red,R.id.img_yellow,R.id.img_green,R.id.img_blue,
            R.id.img_purple,R.id.img_pink,R.id.return_last,R.id.btn_arrow,R.id.btn_hand_write,R.id.btn_holl_circle,R.id.btn_holl_rect
    ,R.id.save_btn,R.id.back_btn,R.id.brush,R.id.prettify,R.id.mosaic,R.id.text,R.id.screenshot,R.id.return_last_2,R.id.mosaic_one,R.id.mosaic_two,
    R.id.pic_one,R.id.pic_two,R.id.pic_three,R.id.pic_four,R.id.pic_cancel,R.id.pic_sure,R.id.crop_o,R.id.crop_11,R.id.crop_43,R.id.pic_cancel2,R.id.pic_sure2})
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
            case R.id.return_last_2:
                mDoodle.undo();
                break;
            case R.id.btn_arrow:
                mDoodle.setShape(DoodleShape.ARROW);
                btn_hand_write.setImageDrawable(getResources().getDrawable(R.drawable.line_un));
                btn_arrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow));
                btn_holl_circle.setImageDrawable(getResources().getDrawable(R.drawable.circular_un));
                btn_holl_rect.setImageDrawable(getResources().getDrawable(R.drawable.rectangle_un));
                break;
            case R.id.btn_holl_rect:
                mDoodle.setShape(DoodleShape.HOLLOW_RECT);
                btn_hand_write.setImageDrawable(getResources().getDrawable(R.drawable.line_un));
                btn_arrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_un));
                btn_holl_circle.setImageDrawable(getResources().getDrawable(R.drawable.circular_un));
                btn_holl_rect.setImageDrawable(getResources().getDrawable(R.drawable.rectangle));
                break;
            case R.id.btn_holl_circle:
                mDoodle.setShape(DoodleShape.HOLLOW_CIRCLE);
                btn_hand_write.setImageDrawable(getResources().getDrawable(R.drawable.line_un));
                btn_arrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_un));
                btn_holl_circle.setImageDrawable(getResources().getDrawable(R.drawable.circular));
                btn_holl_rect.setImageDrawable(getResources().getDrawable(R.drawable.rectangle_un));
                break;
            case R.id.btn_hand_write:
                mDoodle.setShape(DoodleShape.HAND_WRITE);
                btn_hand_write.setImageDrawable(getResources().getDrawable(R.drawable.line));
                btn_arrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_un));
                btn_holl_circle.setImageDrawable(getResources().getDrawable(R.drawable.circular_un));
                btn_holl_rect.setImageDrawable(getResources().getDrawable(R.drawable.rectangle_un));
                break;
            case R.id.save_btn:
                mDoodle.save();
                break;
            case R.id.back_btn:
                finish();
                break;
            case R.id.brush:
                menu.setVisibility(View.VISIBLE);
                mosaic_menu.setVisibility(View.GONE);
                pic_menu.setVisibility(View.GONE);
                mDoodle.setPen(DoodlePen.BRUSH);
                mDoodle.setColor(new DoodleColor(color));
                img_show.setVisibility(View.GONE);
                cropImage.setVisibility(View.GONE);
                crop_menu.setVisibility(View.GONE);
                brush.setImageDrawable(getResources().getDrawable(R.drawable.brush));
                prettify.setImageDrawable(getResources().getDrawable(R.drawable.prettify_un));
                text.setImageDrawable(getResources().getDrawable(R.drawable.text_un));
                mosaic.setImageDrawable(getResources().getDrawable(R.drawable.mosaic_un));
                screenshot.setImageDrawable(getResources().getDrawable(R.drawable.screenshot_un));
                break;
            case R.id.prettify:
                menu.setVisibility(View.GONE);
                mosaic_menu.setVisibility(View.GONE);
                pic_menu.setVisibility(View.VISIBLE);
                img_show.setVisibility(View.GONE);
                cropImage.setVisibility(View.GONE);
                crop_menu.setVisibility(View.GONE);
                brush.setImageDrawable(getResources().getDrawable(R.drawable.brush_un));
                prettify.setImageDrawable(getResources().getDrawable(R.drawable.prettify));
                text.setImageDrawable(getResources().getDrawable(R.drawable.text_un));
                mosaic.setImageDrawable(getResources().getDrawable(R.drawable.mosaic_un));
                screenshot.setImageDrawable(getResources().getDrawable(R.drawable.screenshot_un));
                break;
            case R.id.text:
                menu.setVisibility(View.GONE);
                mosaic_menu.setVisibility(View.GONE);
                pic_menu.setVisibility(View.GONE);
                mDoodle.setPen(DoodlePen.BITMAP);
                img_show.setVisibility(View.GONE);
                cropImage.setVisibility(View.GONE);
                crop_menu.setVisibility(View.GONE);
//                Intent intent=new Intent(this,InputActivity.class);
//                startActivityForResult(intent,INPUT);
                TextDialog textDialog=new TextDialog(this,this);
                textDialog.showAtLocation(edit_bg, Gravity.BOTTOM,0,0);
                brush.setImageDrawable(getResources().getDrawable(R.drawable.brush_un));
                prettify.setImageDrawable(getResources().getDrawable(R.drawable.prettify_un));
                text.setImageDrawable(getResources().getDrawable(R.drawable.text));
                mosaic.setImageDrawable(getResources().getDrawable(R.drawable.mosaic_un));
                screenshot.setImageDrawable(getResources().getDrawable(R.drawable.screenshot_un));
                break;
            case R.id.mosaic:
                menu.setVisibility(View.GONE);
                mosaic_menu.setVisibility(View.VISIBLE);
                pic_menu.setVisibility(View.GONE);
                mDoodle.setPen(DoodlePen.MOSAIC);
                img_show.setVisibility(View.GONE);
                cropImage.setVisibility(View.GONE);
                crop_menu.setVisibility(View.GONE);
                mDoodle.setColor(DoodlePath.getMosaicColor(mDoodle, DoodlePath.MOSAIC_LEVEL_2));
                brush.setImageDrawable(getResources().getDrawable(R.drawable.brush_un));
                prettify.setImageDrawable(getResources().getDrawable(R.drawable.prettify_un));
                text.setImageDrawable(getResources().getDrawable(R.drawable.text_un));
                mosaic.setImageDrawable(getResources().getDrawable(R.drawable.mosaic));
                screenshot.setImageDrawable(getResources().getDrawable(R.drawable.screenshot_un));
                break;
            case R.id.screenshot:
                menu.setVisibility(View.GONE);
                mosaic_menu.setVisibility(View.GONE);
                pic_menu.setVisibility(View.GONE);
                img_show.setVisibility(View.GONE);
                cropImage.setVisibility(View.VISIBLE);
                crop_menu.setVisibility(View.VISIBLE);
                brush.setImageDrawable(getResources().getDrawable(R.drawable.brush_un));
                prettify.setImageDrawable(getResources().getDrawable(R.drawable.prettify_un));
                text.setImageDrawable(getResources().getDrawable(R.drawable.text_un));
                mosaic.setImageDrawable(getResources().getDrawable(R.drawable.mosaic_un));
                screenshot.setImageDrawable(getResources().getDrawable(R.drawable.screenshot));
                break;
            case R.id.mosaic_one:
                mDoodle.setColor(DoodlePath.getMosaicColor(mDoodle, DoodlePath.MOSAIC_LEVEL_2));
                break;
            case R.id.mosaic_two:
                mDoodle.setColor(DoodlePath.getMosaicColor(mDoodle, DoodlePath.MOSAIC_LEVEL_1));
                break;
            case R.id.pic_one:
                pic_control.setVisibility(View.VISIBLE);
                tv_pic_text.setText("亮度");
                enhance_seekbar.setProgress(DEFAULT_PROGRESS);
                selectPicControl=1;
                img_show.setVisibility(View.VISIBLE);
                break;
            case R.id.pic_two:
                pic_control.setVisibility(View.VISIBLE);
                tv_pic_text.setText("对比度");
                enhance_seekbar.setProgress(DEFAULT_PROGRESS);
                selectPicControl=2;
                img_show.setVisibility(View.VISIBLE);
                break;
            case R.id.pic_three:
                pic_control.setVisibility(View.VISIBLE);
                tv_pic_text.setText("饱和度");
                enhance_seekbar.setProgress(DEFAULT_PROGRESS);
                selectPicControl=3;
                img_show.setVisibility(View.VISIBLE);
                break;
            case R.id.pic_four:
                pic_control.setVisibility(View.VISIBLE);
                tv_pic_text.setText("锐化");
                enhance_seekbar.setProgress(DEFAULT_PROGRESS);
                selectPicControl=4;
                img_show.setVisibility(View.VISIBLE);
                break;
            case R.id.pic_cancel:
                pic_control.setVisibility(View.GONE);
                break;
            case R.id.pic_sure:
                pic_control.setVisibility(View.GONE);
                break;
            case R.id.pic_cancel2:
                cropImage.setVisibility(View.GONE);
                crop_menu.setVisibility(View.GONE);
                break;
            case R.id.pic_sure2:
                Bitmap bit = cropImage.getCroppedImage();
                img_show.setVisibility(View.VISIBLE);
                Glide.with(this).load(bit).into(img_show);
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

    @Override
    public void sure(Bitmap bitmap) {
        IDoodleSelectableItem item = new DoodleBitmap(mDoodle, bitmap, 200, 100, 100);
        mDoodle.addItem(item);
        mTouchGestureListener.setSelectedItem(item);
    }


    /**
     * 包裹DoodleView，监听相应的设置接口，以改变UI状态
     */
    private class DoodleViewWrapper extends DoodleView {

        public DoodleViewWrapper(Context context, Bitmap bitmap, boolean optimizeDrawing, IDoodleListener listener, IDoodleTouchDetector defaultDetector) {
            super(context, bitmap, optimizeDrawing, listener, defaultDetector);
        }

        private Map<IDoodlePen, Integer> mBtnPenIds = new HashMap<>();

        {
            mBtnPenIds.put(DoodlePen.BRUSH, cn.hzw.doodle.R.id.btn_pen_hand);
            mBtnPenIds.put(DoodlePen.MOSAIC, cn.hzw.doodle.R.id.btn_pen_mosaic);
            mBtnPenIds.put(DoodlePen.COPY, cn.hzw.doodle.R.id.btn_pen_copy);
            mBtnPenIds.put(DoodlePen.ERASER, cn.hzw.doodle.R.id.btn_pen_eraser);
            mBtnPenIds.put(DoodlePen.TEXT, cn.hzw.doodle.R.id.btn_pen_text);
            mBtnPenIds.put(DoodlePen.BITMAP, cn.hzw.doodle.R.id.btn_pen_bitmap);
        }

        @Override
        public void setPen(IDoodlePen pen) {
            IDoodlePen oldPen = getPen();
            super.setPen(pen);

            mMosaicMenu.setVisibility(GONE);
            mEditBtn.setVisibility(View.GONE); // edit btn
            if (pen == DoodlePen.BITMAP || pen == DoodlePen.TEXT) {
                mEditBtn.setVisibility(View.VISIBLE); // edit btn
                mShapeContainer.setVisibility(GONE);
                if (pen == DoodlePen.BITMAP) {
                    mColorContainer.setVisibility(GONE);
                } else {
                    mColorContainer.setVisibility(VISIBLE);
                }
            } else if (pen == DoodlePen.MOSAIC) {
                mMosaicMenu.setVisibility(VISIBLE);
                mShapeContainer.setVisibility(VISIBLE);
                mColorContainer.setVisibility(GONE);
            } else {
                mShapeContainer.setVisibility(VISIBLE);
                if (pen == DoodlePen.COPY || pen == DoodlePen.ERASER) {
                    mColorContainer.setVisibility(GONE);
                } else {
                    mColorContainer.setVisibility(VISIBLE);
                }
            }
            setSingleSelected(mBtnPenIds.values(), mBtnPenIds.get(pen));

            if (mTouchGestureListener.getSelectedItem() == null) {
                mPenSizeMap.put(oldPen, getSize()); // save
                Float size = mPenSizeMap.get(pen); // restore
                if (size != null) {
                    mDoodle.setSize(size);
                }
                if (isEditMode()) {
                    mShapeContainer.setVisibility(GONE);
                    mColorContainer.setVisibility(GONE);
                    mMosaicMenu.setVisibility(GONE);
                }
            } else {
                mShapeContainer.setVisibility(GONE);
                return;
            }

            if (pen == DoodlePen.BRUSH) {
                Drawable colorBg = mBtnColor.getBackground();
                if (colorBg instanceof ColorDrawable) {
                    mDoodle.setColor(new DoodleColor(((ColorDrawable) colorBg).getColor()));
                } else {
                    mDoodle.setColor(new DoodleColor(((BitmapDrawable) colorBg).getBitmap()));
                }
            } else if (pen == DoodlePen.MOSAIC) {
                if (mMosaicLevel <= 0) {
                    mMosaicMenu.findViewById(cn.hzw.doodle.R.id.btn_mosaic_level2).performClick();
                } else {
                    mDoodle.setColor(DoodlePath.getMosaicColor(mDoodle, mMosaicLevel));
                }
            } else if (pen == DoodlePen.COPY) {

            } else if (pen == DoodlePen.ERASER) {

            } else if (pen == DoodlePen.TEXT) {
                Drawable colorBg = mBtnColor.getBackground();
                if (colorBg instanceof ColorDrawable) {
                    mDoodle.setColor(new DoodleColor(((ColorDrawable) colorBg).getColor()));
                } else {
                    mDoodle.setColor(new DoodleColor(((BitmapDrawable) colorBg).getBitmap()));
                }
            } else if (pen == DoodlePen.BITMAP) {
                Drawable colorBg = mBtnColor.getBackground();
                if (colorBg instanceof ColorDrawable) {
                    mDoodle.setColor(new DoodleColor(((ColorDrawable) colorBg).getColor()));
                } else {
                    mDoodle.setColor(new DoodleColor(((BitmapDrawable) colorBg).getBitmap()));
                }
            }
        }

        private Map<IDoodleShape, Integer> mBtnShapeIds = new HashMap<>();
        {
            mBtnShapeIds.put(DoodleShape.HAND_WRITE, cn.hzw.doodle.R.id.btn_hand_write);
            mBtnShapeIds.put(DoodleShape.ARROW, cn.hzw.doodle.R.id.btn_arrow);
            mBtnShapeIds.put(DoodleShape.LINE, cn.hzw.doodle.R.id.btn_line);
            mBtnShapeIds.put(DoodleShape.HOLLOW_CIRCLE, cn.hzw.doodle.R.id.btn_holl_circle);
            mBtnShapeIds.put(DoodleShape.FILL_CIRCLE, cn.hzw.doodle.R.id.btn_fill_circle);
            mBtnShapeIds.put(DoodleShape.HOLLOW_RECT, cn.hzw.doodle.R.id.btn_holl_rect);
            mBtnShapeIds.put(DoodleShape.FILL_RECT, cn.hzw.doodle.R.id.btn_fill_rect);

        }

        @Override
        public void setShape(IDoodleShape shape) {
            super.setShape(shape);
            setSingleSelected(mBtnShapeIds.values(), mBtnShapeIds.get(shape));
        }

        TextView mPaintSizeView = (TextView) this.findViewById(cn.hzw.doodle.R.id.paint_size_text);

        @Override
        public void setSize(float paintSize) {
            super.setSize(paintSize);
            mPaintSizeView.setText("" + (int) paintSize);

            if (mTouchGestureListener.getSelectedItem() != null) {
                mTouchGestureListener.getSelectedItem().setSize(getSize());
            }
        }

        @Override
        public void setColor(IDoodleColor color) {
            IDoodlePen pen = getPen();
            super.setColor(color);

            DoodleColor doodleColor = null;
            if (color instanceof DoodleColor) {
                doodleColor = (DoodleColor) color;
            }
            if (doodleColor != null
                    && canChangeColor(pen)) {
                if (doodleColor.getType() == DoodleColor.Type.COLOR) {
                    mBtnColor.setBackgroundColor(doodleColor.getColor());
                } else if (doodleColor.getType() == DoodleColor.Type.BITMAP) {
                    mBtnColor.setBackgroundDrawable(new BitmapDrawable(doodleColor.getBitmap()));
                }

                if (mTouchGestureListener.getSelectedItem() != null) {
                    mTouchGestureListener.getSelectedItem().setColor(getColor().copy());
                }
            }

            if (doodleColor != null && pen == DoodlePen.MOSAIC
                    && doodleColor.getLevel() != mMosaicLevel) {
                switch (doodleColor.getLevel()) {
                    case DoodlePath.MOSAIC_LEVEL_1:
                        this.findViewById(cn.hzw.doodle.R.id.btn_mosaic_level1).performClick();
                        break;
                    case DoodlePath.MOSAIC_LEVEL_2:
                        this.findViewById(cn.hzw.doodle.R.id.btn_mosaic_level2).performClick();
                        break;
                    case DoodlePath.MOSAIC_LEVEL_3:
                        this.findViewById(cn.hzw.doodle.R.id.btn_mosaic_level3).performClick();
                        break;
                }
            }
        }

        @Override
        public void enableZoomer(boolean enable) {
            super.enableZoomer(enable);
            this.findViewById(cn.hzw.doodle.R.id.btn_zoomer).setSelected(enable);
            if (enable) {
                Toast.makeText(ImageEditActivity.this, "x" + mDoodleParams.mZoomerScale, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public boolean undo() {
            mTouchGestureListener.setSelectedItem(null);
            return super.undo();
        }

        @Override
        public void clear() {
            super.clear();
            mTouchGestureListener.setSelectedItem(null);
        }

        View mBtnEditMode = this.findViewById(cn.hzw.doodle.R.id.doodle_btn_brush_edit);
        Boolean mLastIsDrawableOutside = null;

        @Override
        public void setEditMode(boolean editMode) {
            if (editMode == isEditMode()) {
                return;
            }

            super.setEditMode(editMode);
            mBtnEditMode.setSelected(editMode);
            if (editMode) {
                Toast.makeText(ImageEditActivity.this, cn.hzw.doodle.R.string.doodle_edit_mode, Toast.LENGTH_SHORT).show();
                mLastIsDrawableOutside = mDoodle.isDrawableOutside(); // save
                mDoodle.setIsDrawableOutside(true);
                mPenContainer.setVisibility(GONE);
                mShapeContainer.setVisibility(GONE);
                mSizeContainer.setVisibility(GONE);
                mColorContainer.setVisibility(GONE);
                mBtnUndo.setVisibility(GONE);
                mMosaicMenu.setVisibility(GONE);
            } else {
                if (mLastIsDrawableOutside != null) { // restore
                    mDoodle.setIsDrawableOutside(mLastIsDrawableOutside);
                }
                mTouchGestureListener.center(); // center picture
                if (mTouchGestureListener.getSelectedItem() == null) { // restore
                    setPen(getPen());
                }
                mTouchGestureListener.setSelectedItem(null);
                mPenContainer.setVisibility(VISIBLE);
                mSizeContainer.setVisibility(VISIBLE);
                mBtnUndo.setVisibility(VISIBLE);
            }
        }

        private void setSingleSelected(Collection<Integer> ids, int selectedId) {
            for (int id : ids) {
                if (id == selectedId) {
                    this.findViewById(id).setSelected(true);
                } else {
                    this.findViewById(id).setSelected(false);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case INPUT:

                break;
        }
    }

    /**
     * 图片锐化（拉普拉斯变换）
     *
     * @return
     */
    private Bitmap sharpenImageAmeliorate(Bitmap bmp,int progress)
    {
        float rate=(progress-100)/100;
        long start = System.currentTimeMillis();
        // 拉普拉斯矩阵
        int[] laplacian = new int[]{-1, -1, -1, -1, 9, -1, -1, -1, -1};
        //        int[] laplacian = new int[]{0, -1, 0, -1, 5, -1, 0, -1, 0};
        //        int[] laplacian = new int[]{1, -2, 1, -2, 5, -2, 1, -2, 1};
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        int pixR = 0;
        int pixG = 0;
        int pixB = 0;

        int pixColor = 0;

        int newR = 0;
        int newG = 0;
        int newB = 0;

        int idx = 0;
        float alpha = 1F;
        //原图像素点数组
        int[] pixels = new int[width*height];
        //创建一个新数据保存锐化后的像素点
        int[] pixels_1 = new int[width*height];
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        for(int i = 1, length = height-1; i<length; i++)
        {
            for(int k = 1, len = width-1; k<len; k++)
            {
                idx = 0;
                for(int m = -1; m<=1; m++)
                {
                    for(int n = -1; n<=1; n++)
                    {
                        pixColor = pixels[( i+n )*width+k+m];
                        pixR = Color.red(pixColor);
                        pixG = Color.green(pixColor);
                        pixB = Color.blue(pixColor);

                        newR = newR+(int)( pixR*laplacian[idx]*alpha);
                        newG = newG+(int)( pixG*laplacian[idx]*alpha );
                        newB = newB+(int)( pixB*laplacian[idx]*alpha );
                        idx++;
                    }
                }

                newR = Math.min(255, Math.max(0, newR));
                newG = Math.min(255, Math.max(0, newG));
                newB = Math.min(255, Math.max(0, newB));

                pixels_1[i*width+k] = Color.argb(255, newR, newG, newB);
                newR = 0;
                newG = 0;
                newB = 0;
            }
        }

        bitmap.setPixels(pixels_1, 0, width, 0, 0, width, height);
        long end = System.currentTimeMillis();
        return bitmap;
    }
}
