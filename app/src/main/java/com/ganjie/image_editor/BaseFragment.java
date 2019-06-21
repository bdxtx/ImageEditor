package com.ganjie.image_editor;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 作者：陈思村 on 2019/3/21.
 * 邮箱：chensicun@51ganjie.com
 */
public abstract class BaseFragment extends Fragment {
    private Unbinder unBinder;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(this.getLayoutId(), container, false);
        unBinder = ButterKnife.bind(this, view);
        initView(view);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unBinder.unbind();
    }

    protected abstract int getLayoutId();

    /**
     * 初始化视图
     *
     * @param view
     */
    protected abstract void initView(View view);

}
