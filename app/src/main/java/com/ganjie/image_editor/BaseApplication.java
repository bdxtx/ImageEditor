package com.ganjie.image_editor;

import android.app.Application;
import android.graphics.Bitmap;
import android.widget.Toast;

/**
 * 作者：陈思村 on 2019/6/19.
 * 邮箱：chensicun@51ganjie.com
 */
public class BaseApplication extends Application {
    private Bitmap bitmap;
    public Bitmap getBitmap() {
        if (bitmap==null){
            Toast.makeText(this,"图片在内存中丢失，请返回首页重新选取",Toast.LENGTH_LONG).show();
        }
        return bitmap;
    }
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

}
