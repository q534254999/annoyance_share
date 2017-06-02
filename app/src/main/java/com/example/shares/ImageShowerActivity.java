package com.example.shares;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ImageShowerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_image_shower);
        ImageView iv_large=(ImageView)findViewById(R.id.iv_large);
        final ImageLoadingDialog dialog = new ImageLoadingDialog(this);

        dialog.show();
        Intent intent=getIntent();
        String picture=intent.getStringExtra("picture");
        Glide.with(this).load(picture).into(iv_large);
        // 两秒后关闭后dialog
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        }, 1500 );
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        finish();
        return true;

    }
}
