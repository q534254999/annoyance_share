package com.example.shares;

import android.app.ProgressDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.shares.model.MyUser;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.listener.FindListener;

public class SearchUserActivity extends AppCompatActivity {
    List<MyUser> searchUserList=new ArrayList<>();
    RecyclerView recyclerview;
    LinearLayoutManager layoutManager;
    SearchUserAdapter adapter;
    EditText searchNickName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);
        searchNickName= (EditText)findViewById(R.id.et_find_name);
        Button search=(Button)findViewById(R.id.btn_search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog=new ProgressDialog(SearchUserActivity.this);
                progressDialog.setMessage("正在搜索");
                progressDialog.setCancelable(false);
                progressDialog.show();
                query(progressDialog);
            }
        });
        Toolbar toolbar=(Toolbar)findViewById(R.id.tb_searchuser);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        recyclerview=(RecyclerView)findViewById(R.id.search_user_recyclerview);
        layoutManager=new LinearLayoutManager(this);
        recyclerview.setLayoutManager(layoutManager);
        adapter=new SearchUserAdapter(searchUserList);
        recyclerview.setAdapter(adapter);
    }
    public void query(final ProgressDialog progressDialog){
        String name =searchNickName.getText().toString();
        if(TextUtils.isEmpty(name)){
            Toast.makeText(this, "请填写用户名", Toast.LENGTH_SHORT).show();
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            return;
        }
        MyUser.getInstance().queryUsers(name, new FindListener<MyUser>() {
            @Override
            public void onSuccess(List<MyUser> list) {
                adapter.setDatas(list);
                adapter.notifyDataSetChanged();
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
            }

            @Override
            public void onError(int i, String s) {
                adapter.setDatas(null);
                adapter.notifyDataSetChanged();
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                Toast.makeText(SearchUserActivity.this, s, Toast.LENGTH_SHORT).show();
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
}
