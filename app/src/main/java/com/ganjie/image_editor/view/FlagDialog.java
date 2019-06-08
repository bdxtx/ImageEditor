package com.ganjie.image_editor.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ganjie.image_editor.R;

/**
 * 作者：陈思村 on 2019/6/7.
 * 邮箱：chensicun@51ganjie.com
 */
public class FlagDialog extends DialogFragment {

    public static FlagDialog newInstance(){
        FlagDialog flagDialog=new FlagDialog();
        Bundle bundle=new Bundle();
        flagDialog.setArguments(bundle);
        return flagDialog;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, R.style.DialogBG);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.flag_dialog, container, false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
