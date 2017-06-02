package com.example.shares;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.example.shares.model.MyUser;
import com.example.shares.model.Topic;
import com.example.shares.util.ActivityCollector;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;

import static com.example.shares.MainActivity.user;

public class AddTopicActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_topic);
        ActivityCollector.addActivity(this);
        Toolbar toolbar=(Toolbar)findViewById(R.id.add_topic_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        final ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){

            actionBar.setDisplayHomeAsUpEnabled(true);

        }
        final EditText addTopicTitle=(EditText)findViewById(R.id.add_topic_title);
        final EditText addTopicDetails=(EditText)findViewById(R.id.add_topic_details);

        Button publish=(Button)findViewById(R.id.publish_topic);
        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title=addTopicTitle.getText().toString();
                String text=addTopicDetails.getText().toString();

                if(title.equals("")){
                    Toast.makeText(AddTopicActivity.this, "标题不能为空", Toast.LENGTH_SHORT).show();
                }
                else {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                    String str = formatter.format(curDate);
                    final Topic topic = new Topic();
                    user = BmobUser.getCurrentUser(AddTopicActivity.this, MyUser.class);

                    //topic.setAnswerNum(new Integer(0));
                    topic.setContentText(text);
                    topic.setContentTitle(title);
                    ProgressDialog progressDialog=new ProgressDialog(AddTopicActivity.this);
                    progressDialog.setMessage("请稍候");
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setCancelable(false);
                    progressDialog.show();


                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                       //     topic.setAuthor(user);
                            topic.setaPath(user.getaPath());
                            topic.save(AddTopicActivity.this, new SaveListener() {
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(AddTopicActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(AddTopicActivity.this, MainActivity.class);
                                    intent.putExtra("grxx", 1);

                                    finish();
                                    startActivity(intent);
                                }

                                @Override
                                public void onFailure(int code, String arg0) {
                                    Toast.makeText(AddTopicActivity.this, arg0, Toast.LENGTH_SHORT).show();
                                }
                            });




                        }
                    }).start();
                    progressDialog.dismiss();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            default:
        }
        return true;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
