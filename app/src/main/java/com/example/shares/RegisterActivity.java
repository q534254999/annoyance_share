package com.example.shares;


import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.shares.model.MyUser;
import com.example.shares.util.ActivityCollector;


import cn.bmob.v3.listener.SaveListener;

import static com.example.shares.MainActivity.user;
import static com.example.shares.MainActivity.dbHelper;
public class RegisterActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        dbHelper =new MyDatabaseHelper(RegisterActivity.this,"User.db",null,1);
        ActivityCollector.addActivity(this);
        final EditText re_userName=(EditText)findViewById(R.id.re_username);
        final EditText re_passWord=(EditText)findViewById(R.id.re_password);
        final EditText re_ackPassword=(EditText)findViewById(R.id.re_ackpassword); 
        Button register=(Button)findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userName=re_userName.getText().toString();
                final String password=re_passWord.getText().toString();
                final String ackPassword=re_ackPassword.getText().toString();
                if(password.equals(ackPassword)){
                    user=new MyUser();
                    user.setUsername(userName);
                    user.setPassword(password);
                    user.setNickname(userName);
                    final ProgressDialog progressDialog=new ProgressDialog(RegisterActivity.this);
                    progressDialog.setMessage("正在注册");
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    user.signUp(RegisterActivity.this, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            user.login(RegisterActivity.this, new SaveListener() {
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                                    SQLiteDatabase db=dbHelper.getWritableDatabase();
                                    ContentValues values=new ContentValues();
                                    values.put("username",userName);
                                    values.put("password",password);
                                    db.insert("User",null,values);
                                    values.clear();
                                    Intent intent =new Intent(RegisterActivity.this,MainActivity.class);
                                    startActivity(intent);
                                }

                                @Override
                                public void onFailure(int i, String s) {
                                    Toast.makeText(RegisterActivity.this, "注册失败:"+s, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            Toast.makeText(RegisterActivity.this, "注册失败:"+s , Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    Toast.makeText(RegisterActivity.this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
