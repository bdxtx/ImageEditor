package com.ganjie.image_editor;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 作者：陈思村 on 2019/3/21.
 * 邮箱：chensicun@51ganjie.com
 */
public abstract class BaseActivity extends AppCompatActivity {
    private Unbinder unbinder;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inBaseCreate();
        requestWindowFeature(Window.FEATURE_NO_TITLE);//remove title bar  即隐藏标题栏
        if (getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }
//        getSupportActionBar().hide();// 隐藏ActionBar
        setContentView(getLayoutId());
        unbinder = ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        inBaseDestory();
    }

    /**
     * 设置布局
     *
     * @return
     */
    public abstract int getLayoutId();

    /**
     * 初始化视图
     */
    public abstract void initView();

    protected void inBaseCreate(){

    }

    protected void inBaseDestory(){

    }
}
