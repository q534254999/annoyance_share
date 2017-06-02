package com.example.shares;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.shares.model.Answer;
import com.example.shares.model.Topic;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;


public class TopicInfoActivity extends AppCompatActivity {
    public static List<Answer> answerList=new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;

    private TextView title;
    private TextView content;
    private ImageView img;
    private AnswerAdapter answerAdapter;
    public static ImageView home;
    private Button submit;
    Topic temp;
    TextView tv_answer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_info);
        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipe_refresh_a);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorLightBlue);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
            //    home.setImageBitmap(avaterBitmap);
                refreshAnswer(1);
            }
        });

        submit=(Button)findViewById(R.id.addanswer);
        tv_answer=(TextView)findViewById(R.id.tv_answer);
        Intent intent=getIntent();
        img=(ImageView)findViewById(R.id.tpinfoimage);
        temp=(Topic)intent.getExtras().get("topic_data");
        title=(TextView) findViewById(R.id.questiontitle);

        content=(TextView) findViewById(R.id.questioncontent);
        title.setText(temp.getContentTitle());
        content.setText(temp.getContentText());
        Glide.with(this).load(temp.getaPath()).into(img);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(TopicInfoActivity.this,AddAnswerActivity.class);
                intent.putExtra("topic",temp);
                startActivity(intent);
            }
        });
        RecyclerView osRecyclerView=(RecyclerView)findViewById(R.id.recycler_view_a);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setSmoothScrollbarEnabled(true);
        layoutManager.setAutoMeasureEnabled(true);
        osRecyclerView.setLayoutManager(layoutManager);
        refreshAnswer(1);
        answerAdapter=new AnswerAdapter(answerList);
        osRecyclerView.setAdapter(answerAdapter);
    }

    private void refreshAnswer(final int flag){
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("正在获取数据");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
        answerList.clear();
        BmobQuery<Answer> query=new BmobQuery<>();
        query.addWhereEqualTo("topic",temp);
        query.order("-createdAt");
        query.include("author");
        query.findObjects(this, new FindListener<Answer>() {
            @Override
            public void onSuccess(List<Answer> lists) {
                if(lists.size()>0) {
                    tv_answer.setVisibility(View.VISIBLE);
                    answerList.addAll(lists);

                    answerAdapter.notifyDataSetChanged();
                }


                progressDialog.dismiss();
                if(flag==1)
                    swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(int i, String s) {
                progressDialog.dismiss();
                Toast.makeText(TopicInfoActivity.this, s, Toast.LENGTH_SHORT).show();
                replaceFragment(new BlankFragment());
                if(flag==1)
                    swipeRefreshLayout.setRefreshing(false);

            }
        });
    }
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.replace(R.id.activity_topic_info,fragment);
        transaction.commit();
    }

    @Override
    protected void onRestart() {
       super.onRestart();
        refreshAnswer(1);
    }
}
