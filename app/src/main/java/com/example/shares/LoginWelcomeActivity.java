package com.example.shares;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.shares.model.Annoyance;
import com.example.shares.model.Error;
import com.example.shares.model.MyUser;
import com.example.shares.util.ActivityCollector;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

import static com.example.shares.MainActivity.avaterBitmap;
import static com.example.shares.MainActivity.dbHelper;
import static com.example.shares.MainActivity.user;
import static com.example.shares.OverlappingSoundFragment.annoyanceList;

public class LoginWelcomeActivity extends AppCompatActivity {

    //private String applicationId="4762d7231bc5b0ba18af406cbfe25025";
    private static final int GOTO_MAIN_ACTIVITY=0;
    private static final int GOTO_LOGIN_ACTIVITY=1;
    public static int already_refresh_annoyance=0;
    ImageView iv_back;
    private Handler mHandler =new Handler(){
        public void handleMessage(android.os.Message msg){
            switch (msg.what){
                case GOTO_MAIN_ACTIVITY:
                    Intent mainIntent=new Intent();
                    mainIntent.setClass(LoginWelcomeActivity.this,MainActivity.class);
                    //mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(mainIntent);
                    overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                    finish();
                    break;
                default:
                    Intent loginIntent=new Intent();
                    loginIntent.setClass(LoginWelcomeActivity.this,LoginActivity.class);
                    //loginIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(loginIntent);
                    overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                    finish();
                    break;
            }
        }
    };
    private class AnimationImpl implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {
            iv_back.setBackgroundResource(R.drawable.login_back);

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            skip(); // 动画结束后跳转到别的页面
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }

    }

    private void skip() {
        final MyUser bmobUser = BmobUser.getCurrentUser(this, MyUser.class);
        if (bmobUser != null) {
            autoLogin(bmobUser);
            mHandler.sendEmptyMessageDelayed(GOTO_MAIN_ACTIVITY,1000);
        }
        else mHandler.sendEmptyMessage(GOTO_LOGIN_ACTIVITY);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login_welcome);
        iv_back=(ImageView)findViewById(R.id.iv_back) ;
        dbHelper = new MyDatabaseHelper(LoginWelcomeActivity.this, "User.db", null, 1);
        //第一：默认初始化
        //注：由于在Application中已经初始化，故无需重复初始化
        //Bmob.initialize(LoginWelcomeActivity.this, applicationId);


        AlphaAnimation anima = new AlphaAnimation(0.0f, 1.0f);
        anima.setDuration(3000);// 设置动画显示时间
        iv_back.startAnimation(anima);
        anima.setAnimationListener(new AnimationImpl());



    }



    protected void autoLogin(BmobUser bmobUser){


        SQLiteDatabase db=dbHelper.getWritableDatabase();
        Cursor cursor=db.query("User",new String[]{"username","password"},"username=?",
                new String[]{bmobUser.getUsername()},null,null,null);
        MyUser user=new MyUser();
        if (cursor!=null) {
            while (cursor.moveToNext()) {
                String userName = cursor.getString(cursor.getColumnIndex("username"));
                String password = cursor.getString(cursor.getColumnIndex("password"));

                user.setUsername(userName);
                user.setPassword(password);
                break;

            }
        }
        db.close();
        user.login(LoginWelcomeActivity.this, new SaveListener() {
            @Override
            public void onSuccess() {
                updateUserData();
                //refreshAnnoyance();
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(LoginWelcomeActivity.this, s, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginWelcomeActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
    //更新用户数据
    private void updateUserData() {

        user = BmobUser.getCurrentUser(LoginWelcomeActivity.this, MyUser.class);

        new Thread(new Runnable() {
            @Override
            public void run() {
                //Looper.prepare();
                try {
                    if ((user.getaPath() != null) && (avaterBitmap == null)) {
                        URL myUrl = new URL(user.getaPath());
                        HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
                        connection.setConnectTimeout(5000);
                        connection.setRequestMethod("GET");
                        connection.connect();

                        InputStream is = connection.getInputStream();
                        avaterBitmap = BitmapFactory.decodeStream(is);

                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //Looper.loop();
            }
        }).start();


    }



}
