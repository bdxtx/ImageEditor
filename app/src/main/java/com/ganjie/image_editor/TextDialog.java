package com.ganjie.image_editor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

public class TextDialog extends PopupWindow implements View.OnClickListener {
    private Context context;
    private final EditText et_text;
    int color=Color.parseColor("#F1340E");
    private final View view;
    private int selected=2;
    OnclickListener listener;
    private final ImageView text_per;

    public TextDialog(Context context,OnclickListener listener){
        this.context=context;
        this.listener=listener;
        view = LayoutInflater.from(context).inflate(R.layout.text_dialog,null);
        et_text = view.findViewById(R.id.et_text);
        text_per = view.findViewById(R.id.text_per);
        view.findViewById(R.id.img_cancel).setOnClickListener(this);
        view.findViewById(R.id.img_sure).setOnClickListener(this);
        view.findViewById(R.id.text_bg).setOnClickListener(this);
        view.findViewById(R.id.text_per).setOnClickListener(this);
        view.findViewById(R.id.color_white).setOnClickListener(this);
        view.findViewById(R.id.color_black).setOnClickListener(this);
        view.findViewById(R.id.color_red).setOnClickListener(this);
        view.findViewById(R.id.color_yellow).setOnClickListener(this);
        view.findViewById(R.id.color_green).setOnClickListener(this);
        view.findViewById(R.id.color_blue).setOnClickListener(this);
        view.findViewById(R.id.color_purple).setOnClickListener(this);
        view.findViewById(R.id.color_pink).setOnClickListener(this);
        setContentView(view);
        setFocusable(true);
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setBackgroundDrawable(new ColorDrawable(Color.parseColor("#33000000")));
        setHeight(RelativeLayout.LayoutParams.MATCH_PARENT);
        setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        final InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        et_text.postDelayed(new Runnable() {
            @Override
            public void run() {
                et_text.requestFocus();
                boolean show = inputMethodManager.showSoftInput(et_text, 1);
            }
        }, 300);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_cancel:
                dismiss();
                break;
            case R.id.img_sure:
                dismiss();
                listener.sure(viewConversionBitmap(et_text));
                break;
            case R.id.text_bg:
                et_text.setBackgroundColor(color);
                selected=1;
                break;
            case R.id.text_per:
                if (selected==2){
                    text_per.setImageDrawable(view.getResources().getDrawable(R.drawable.text_bg));
                    et_text.setBackgroundColor(color);
                    selected=1;
                }else {
                    text_per.setImageDrawable(view.getResources().getDrawable(R.drawable.text_per));
                    et_text.setTextColor(color);
                    selected=2;
                }
                break;
            case R.id.color_white:
                color=Color.parseColor("#ffffff");
                update();
                break;
            case R.id.color_black:
                color=Color.parseColor("#000000");
                update();
                break;
            case R.id.color_red:
                color=Color.parseColor("#F1340E");
                update();
                break;
            case R.id.color_yellow:
                color=Color.parseColor("#FCB549");
                update();
                break;
            case R.id.color_green:
                color=Color.parseColor("#00D14D");
                update();
                break;
            case R.id.color_blue:
                color=Color.parseColor("#1879FB");
                update();
                break;
            case R.id.color_purple:
                color=Color.parseColor("#8F57FA");
                update();
                break;
            case R.id.color_pink:
                color=Color.parseColor("#F524B6");
                update();
                break;
        }
    }
    public void update(){
        if (selected==1){
            et_text.setBackgroundColor(color);
        }else {
            et_text.setTextColor(color);
        }
    }

    public Bitmap viewConversionBitmap(View v) {
        int w = v.getWidth();
        int h = v.getHeight();

        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);

        c.drawColor(Color.WHITE);
        /** 如果不设置canvas画布为白色，则生成透明 */

        v.layout(0, 0, w, h);
        v.draw(c);

        return bmp;
    }

    interface OnclickListener{
        void sure(Bitmap bitmap);
    }
}
