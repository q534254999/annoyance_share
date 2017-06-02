package com.example.shares;

import android.os.Handler;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.shares.model.Annoyance;
import com.example.shares.model.Comment;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.shares.MainActivity.user;

public class AnnoyanceInfoActivity extends AppCompatActivity {
    private List<Comment> commentList=new ArrayList<>();
    private CommentAdapter adapter;
    private Annoyance annoyance;
    private RecyclerView recyclerView;
    private NestedScrollView scrollView;
    private static int num;
    Toolbar toolbar;
    private Handler mHandler=new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annoyance_info);
        annoyance=(Annoyance)getIntent().getSerializableExtra("annoyance_data");
        recyclerView=(RecyclerView)findViewById(R.id.annoyance_info_recyclerview);
        CircleImageView imageView=(CircleImageView)findViewById(R.id.os_info_image_view);
        TextView nickName=(TextView)findViewById(R.id.os_info_nick_name);
        TextView publishTime=(TextView)findViewById(R.id.os_info_publish_time);
        TextView contentTitle=(TextView)findViewById(R.id.os_info_content_title);
        TextView contentText=(TextView)findViewById(R.id.os_info_content_text);
        scrollView=(NestedScrollView)findViewById(R.id.annoyance_info_scrollview);
        toolbar=(Toolbar)findViewById(R.id.os_info_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        final TextView answerNum=(TextView)findViewById(R.id.os_info_answer_num);
        final EditText content=(EditText)findViewById(R.id.info_comment_content);
        Button send=(Button)findViewById(R.id.send_comment);
        if(annoyance.getAnonymous().equals(Boolean.FALSE)) {
            if(annoyance.getaPath()!=null)
                Glide.with(this).load(annoyance.getaPath()).into(imageView);
            else imageView.setImageResource(R.mipmap.default_head);
            nickName.setText(annoyance.getNickName());
        }
        else
            nickName.setText("匿名用户");
        publishTime.setText(annoyance.getPublishTime());
        contentTitle.setText(annoyance.getContentTitle());
        contentText.setText(annoyance.getContentText());
        answerNum.setText(annoyance.getAnswerNum()+"个回答");
        num=annoyance.getAnswerNum().intValue();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentContent=content.getText().toString();
                if ((commentContent!=null)&&(!commentContent.equals(""))){
                    final Comment comment=new Comment();
                    comment.setAuthor(user);
                    comment.setAnnoyance(annoyance);
                    comment.setContent(commentContent);
                    comment.save(AnnoyanceInfoActivity.this, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            annoyance.increment("answerNum");
                            annoyance.update(AnnoyanceInfoActivity.this);
                            num++;
                            answerNum.setText(num+"个回答");
                            content.getText().clear();
                            content.setFocusable(true);
                            content.setFocusableInTouchMode(true);
                            content.requestFocus();
                            InputMethodManager imm =(InputMethodManager)content.getContext().getSystemService(AnnoyanceInfoActivity
                            .INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(0,InputMethodManager.SHOW_FORCED);
                            commentList.add(comment);
                            adapter.notifyItemInserted(commentList.indexOf(comment));
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                                }
                            });



                        }

                        @Override
                        public void onFailure(int i, String s) {
                            Toast.makeText(AnnoyanceInfoActivity.this, s, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        initComments();
        recyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager layoutManager =new LinearLayoutManager(this);
        layoutManager.setSmoothScrollbarEnabled(true);
        layoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        adapter=new CommentAdapter(commentList);
        recyclerView.setAdapter(adapter);
    }
    private void initComments(){
        commentList=new ArrayList<>();
        commentList.clear();
        BmobQuery<Comment> query=new BmobQuery<>();
        query.addWhereEqualTo("annoyance",annoyance);
        query.order("createdAt");
        query.include("author");
        query.findObjects(AnnoyanceInfoActivity.this, new FindListener<Comment>() {
            @Override
            public void onSuccess(List<Comment> lists) {
                commentList.addAll(lists);
                adapter.notifyDataSetChanged();


            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(AnnoyanceInfoActivity.this, s, Toast.LENGTH_SHORT).show();


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
                break;
        }
        return true;
    }
}
