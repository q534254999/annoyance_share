package com.example.shares;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.shares.model.MyUser;
import com.example.shares.util.ActivityCollector;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;

import static com.example.shares.MainActivity.avaterBitmap;
import static com.example.shares.MainActivity.dbHelper;
import static com.example.shares.MainActivity.user;

public class LoginActivity extends AppCompatActivity {
    private EditText userNameEdit;
    private EditText passwordEdit;
    private Button login;
    private Button register;
    private Button forgetPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActivityCollector.addActivity(this);
        userNameEdit=(EditText)findViewById(R.id.userName);
        passwordEdit=(EditText)findViewById(R.id.password);
        register=(Button)findViewById(R.id.register);
        forgetPassword=(Button)findViewById(R.id.forgetPassword);
        login=(Button)findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userName=userNameEdit.getText().toString();
                final String password=passwordEdit.getText().toString();
                //向服务器调取数据判断帐号与密码是否对应
                MyUser user=new MyUser();
                user.setUsername(userName);
                user.setPassword(password);
                final ProgressDialog progressDialog=new ProgressDialog(LoginActivity.this);
                progressDialog.setMessage("正在登录");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setCancelable(false);
                progressDialog.show();
                user.login(LoginActivity.this, new SaveListener() {
                    @Override
                    public void onSuccess() {
                        updateUserData();
                        SQLiteDatabase db=dbHelper.getWritableDatabase();
                        ContentValues values=new ContentValues();
                        values.put("username",userName);
                        values.put("password",password);
                        db.insert("User",null,values);
                        values.clear();
                        progressDialog.dismiss();
                        Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "登录失败:"+s, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toRegisterIntent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(toRegisterIntent);
            }
        });
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "该功能还未实现，详情请密切关注我们", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void updateUserData() {

        user = BmobUser.getCurrentUser(LoginActivity.this, MyUser.class);

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
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
