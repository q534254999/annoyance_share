package com.example.shares;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.Toast;

import com.example.shares.model.Annoyance;
import com.example.shares.model.MyUser;
import com.example.shares.util.ActivityCollector;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;

import static com.example.shares.MainActivity.user;

public class AddAnnoyaceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_annoyance);
        ActivityCollector.addActivity(this);
        Toolbar toolbar=(Toolbar)findViewById(R.id.add_annoyance_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        final ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){

            actionBar.setDisplayHomeAsUpEnabled(true);

        }
        final EditText addAnnoyanceTitle=(EditText)findViewById(R.id.add_annoyance_title);

        final EditText addAnnoyanceDetails=(EditText)findViewById(R.id.add_annoyance_details);
        final Checkable anoymous=(CheckBox)findViewById(R.id.anoymous_publish);
        Button publish=(Button)findViewById(R.id.publish_annoyance);
        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title=addAnnoyanceTitle.getText().toString();
                String text=addAnnoyanceDetails.getText().toString();

                if(title.equals("")){
                    Toast.makeText(AddAnnoyaceActivity.this, "标题不能为空", Toast.LENGTH_SHORT).show();
                }
                else {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                    String str = formatter.format(curDate);
                    final Annoyance annoyance = new Annoyance();
                    user = BmobUser.getCurrentUser(AddAnnoyaceActivity.this, MyUser.class);
                    annoyance.setNickName(user.getNickname() );
                    if(anoymous.isChecked())
                        annoyance.setAnonymous(Boolean.TRUE);
                    annoyance.setPublishTime(str);
                    annoyance.setAnswerNum(new Integer(0));
                    annoyance.setContentText(text);
                    annoyance.setContentTitle(title);
                    ProgressDialog progressDialog=new ProgressDialog(AddAnnoyaceActivity.this);
                    progressDialog.setMessage("请稍候");
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setCancelable(false);
                    progressDialog.show();


                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                                annoyance.setAuthor(user);
                                annoyance.setaPath(user.getaPath());
                                annoyance.save(AddAnnoyaceActivity.this, new SaveListener() {
                                    @Override
                                    public void onSuccess() {
                                        Toast.makeText(AddAnnoyaceActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(AddAnnoyaceActivity.this, MainActivity.class);
                                        intent.putExtra("grxx", 1);

                                        finish();
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onFailure(int code, String arg0) {
                                        Toast.makeText(AddAnnoyaceActivity.this, arg0, Toast.LENGTH_SHORT).show();
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
