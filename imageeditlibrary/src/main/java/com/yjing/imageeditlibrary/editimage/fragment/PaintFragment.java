package com.yjing.imageeditlibrary.editimage.fragment;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;

import com.yjing.imageeditlibrary.R;
import com.yjing.imageeditlibrary.editimage.EditImageActivity;
import com.yjing.imageeditlibrary.editimage.inter.ImageEditInte;
import com.yjing.imageeditlibrary.editimage.inter.SaveCompletedInte;
import com.yjing.imageeditlibrary.editimage.task.StickerTask;
import com.yjing.imageeditlibrary.editimage.view.ColorSeekBar;
import com.yjing.imageeditlibrary.editimage.view.CustomPaintView;
//import com.yjing.imageeditlibrary.editimage.view.PaintModeView;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * 用户自由绘制模式 操作面板
 * 可设置画笔粗细 画笔颜色
 * custom draw mode panel
 */
public class PaintFragment extends BaseFragment implements View.OnClickListener, ImageEditInte {
//    private PaintModeView mPaintModeView;
    private View popView;
    private CustomPaintView mPaintView;
    private PopupWindow setStokenWidthWindow;
    private SeekBar mStokenWidthSeekBar;
    private ImageView return_last;
    private SaveCustomPaintTask mSavePaintImageTask;
//    private ColorSeekBar colorSeekBar;
    private ImageView img_white;
    private ImageView img_red;
    private ImageView img_yellow;
    private ImageView img_green;
    private ImageView img_blue;
    private ImageView img_purple;
    private ImageView img_pink;
    private ImageView img_black;
    private ImageView color_selector;
    private LinearLayout ll_color_picker;

    public static PaintFragment newInstance(EditImageActivity activity) {
        PaintFragment fragment = new PaintFragment();
        fragment.activity = activity;
        fragment.mPaintView = activity.mPaintView;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_edit_paint, null);
//        mPaintModeView = (PaintModeView) mainView.findViewById(R.id.paint_thumb);
//        colorSeekBar = (ColorSeekBar) mainView.findViewById(R.id.colorSlider);
        return_last = (ImageView) mainView.findViewById(R.id.return_last);
        img_white = mainView.findViewById(R.id.img_white);
        img_black = mainView.findViewById(R.id.img_black);
        img_red = mainView.findViewById(R.id.img_red);
        img_yellow = mainView.findViewById(R.id.img_yellow);
        img_green = mainView.findViewById(R.id.img_green);
        img_blue = mainView.findViewById(R.id.img_blue);
        img_purple = mainView.findViewById(R.id.img_purple);
        img_pink = mainView.findViewById(R.id.img_pink);
        color_selector = mainView.findViewById(R.id.color_selector);
        ll_color_picker = mainView.findViewById(R.id.ll_color_picker);
        return mainView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        mPaintModeView.setOnClickListener(this);

        initStokeWidthPopWindow();

        return_last.setOnClickListener(this);
        img_white.setOnClickListener(this);
        img_black.setOnClickListener(this);
        img_red.setOnClickListener(this);
        img_yellow.setOnClickListener(this);
        img_green.setOnClickListener(this);
        img_blue.setOnClickListener(this);
        img_purple.setOnClickListener(this);
        img_pink.setOnClickListener(this);
        color_selector.setOnClickListener(this);


//        colorSeekBar.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {
//            @Override
//            public void onColorChangeListener(int colorBarPosition, int alphaBarPosition, int color) {
//                setPaintColor(color);
//            }
//        });
    }

    @Override
    public void onClick(View v) {
//        if (v == mPaintModeView) {//设置绘制画笔粗细
//            setStokeWidth();
//        } else
        if (v == return_last) {//撤销功能
            mPaintView.undo();
        }else if (v==img_white){
            updatePaintView(Color.WHITE);
            ll_color_picker.setVisibility(View.GONE);
            color_selector.setImageDrawable(getResources().getDrawable(R.drawable.color_white));
        }else if (v==img_black){
            updatePaintView(Color.BLACK);
            ll_color_picker.setVisibility(View.GONE);
            color_selector.setImageDrawable(getResources().getDrawable(R.drawable.color_black));
        }else if (v==img_red){
            updatePaintView(Color.parseColor("#F1340E"));
            ll_color_picker.setVisibility(View.GONE);
            color_selector.setImageDrawable(getResources().getDrawable(R.drawable.color_selector));
        }else if (v==img_yellow){
            updatePaintView(Color.parseColor("#FCB549"));
            ll_color_picker.setVisibility(View.GONE);
            color_selector.setImageDrawable(getResources().getDrawable(R.drawable.color_yellow));
        }else if (v==img_green){
            updatePaintView(Color.parseColor("#00D14D"));
            ll_color_picker.setVisibility(View.GONE);
            color_selector.setImageDrawable(getResources().getDrawable(R.drawable.color_green));
        }else if (v==img_blue){
            updatePaintView(Color.parseColor("#1879FB"));
            ll_color_picker.setVisibility(View.GONE);
            color_selector.setImageDrawable(getResources().getDrawable(R.drawable.color_blue));
        }else if (v==img_purple){
            updatePaintView(Color.parseColor("#8F57FA"));
            ll_color_picker.setVisibility(View.GONE);
            color_selector.setImageDrawable(getResources().getDrawable(R.drawable.color_purple));
        }else if (v==img_pink){
            updatePaintView(Color.parseColor("#F524B6"));
            ll_color_picker.setVisibility(View.GONE);
            color_selector.setImageDrawable(getResources().getDrawable(R.drawable.color_pink));
        }else if (v==color_selector){
            ll_color_picker.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 返回主菜单
     */
    public void backToMain() {
//        appleEdit(null);
        activity.mainImage.setVisibility(View.VISIBLE);
//        this.mPaintView.setVisibility(View.GONE);
        mPaintView.setIsOperation(false);
    }

    public void onShow() {
//        this.mPaintView.setVisibility(View.VISIBLE);
        mPaintView.setIsOperation(true);
    }

    /**
     * 设置画笔颜色
     *
     * @param paintColor
     */
    protected void setPaintColor(final int paintColor) {
//        mPaintModeView.setPaintStrokeColor(paintColor);

        updatePaintView();
    }

    /**
     * 更新画笔view
     */
    private void updatePaintView() {

//        this.mPaintView.setColor(mPaintModeView.getStokenColor());
//        this.mPaintView.setWidth(mPaintModeView.getStokenWidth());
    }

    private void updatePaintView(int color){
        this.mPaintView.setColor(color);
        this.mPaintView.setWidth(10);
    }

    /**
     * 设置画笔粗细
     * show popwidnow to set paint width
     */
//    protected void setStokeWidth() {
//        if (popView.getMeasuredHeight() == 0) {
//            popView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
//        }
//
//        mStokenWidthSeekBar.setMax(mPaintModeView.getMeasuredHeight() / 2);
//
//        mStokenWidthSeekBar.setProgress((int) mPaintModeView.getStokenWidth());
//
//        mStokenWidthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                mPaintModeView.setPaintStrokeWidth(progress);
//                updatePaintView();
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
//
//        int[] locations = new int[2];
//
//        activity.fl_main_menu.getLocationOnScreen(locations);
//        setStokenWidthWindow.showAtLocation(activity.fl_main_menu,
//                Gravity.NO_GRAVITY, 0, locations[1] - popView.getMeasuredHeight());
//    }

    /**
     * 画笔初始化以及设置画笔view的初始化
     */
    private void initStokeWidthPopWindow() {
        popView = LayoutInflater.from(activity).
                inflate(R.layout.view_set_stoke_width, null);
        setStokenWidthWindow = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.WRAP_CONTENT);

        mStokenWidthSeekBar = (SeekBar) popView.findViewById(R.id.stoke_width_seekbar);

        setStokenWidthWindow.setFocusable(true);
        setStokenWidthWindow.setOutsideTouchable(true);
        setStokenWidthWindow.setBackgroundDrawable(new BitmapDrawable());
        setStokenWidthWindow.setAnimationStyle(R.style.popwin_anim_style);

        //默认画笔颜色和宽度
//        mPaintModeView.setPaintStrokeColor(Color.RED);
//        mPaintModeView.setPaintStrokeWidth(20);

//        updatePaintView();
        this.mPaintView.setColor(Color.parseColor("#F1340E"));
        this.mPaintView.setWidth(10);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSavePaintImageTask != null && !mSavePaintImageTask.isCancelled()) {
            mSavePaintImageTask.cancel(true);
        }
    }

    /**
     * 保存涂鸦
     */
    @Override
    public void appleEdit(SaveCompletedInte inte) {
        if (mSavePaintImageTask != null && !mSavePaintImageTask.isCancelled()) {
            mSavePaintImageTask.cancel(true);
        }

        mSavePaintImageTask = new SaveCustomPaintTask(activity, inte);
        mSavePaintImageTask.execute(activity.mainBitmap);
    }

    @Override
    public void method2() {

    }

    @Override
    public void method3() {

    }

    /**
     * 文字合成任务
     * 合成最终图片
     */
    private final class SaveCustomPaintTask extends StickerTask {

        public SaveCustomPaintTask(EditImageActivity activity, SaveCompletedInte inte) {
            super(activity, inte);
        }

        @Override
        public void handleImage(Canvas canvas, Matrix m) {
            float[] f = new float[9];
            m.getValues(f);
            int dx = (int) f[Matrix.MTRANS_X];
            int dy = (int) f[Matrix.MTRANS_Y];
            float scale_x = f[Matrix.MSCALE_X];
            float scale_y = f[Matrix.MSCALE_Y];
            canvas.save();
            canvas.translate(dx, dy);
            canvas.scale(scale_x, scale_y);

            if (mPaintView.getPaintBit() != null) {
                canvas.drawBitmap(mPaintView.getPaintBit(), 0, 0, null);
            }
            canvas.restore();
        }

        @Override
        public void onPostResult(Bitmap result) {
            mPaintView.reset();
            activity.changeMainBitmap(result);
        }
    }

}
