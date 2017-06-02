package com.example.shares;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

/**
 * Created by Wenpzhou on 2017/5/16 0016.
 */

public class ImageLoadingDialog extends Dialog {
    public ImageLoadingDialog(Context context) {
        super(context, R.style.ImageloadingDialogStyle);
        setOwnerActivity((Activity) context);// 设置dialog全屏显示
    }

    private ImageLoadingDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loadingdialog);
    }
}
